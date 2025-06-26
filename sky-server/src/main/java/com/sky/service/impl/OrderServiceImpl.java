package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.enumeration.ControllerType;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常
        //地址簿异常(这里前端好像有问题 即使地址全部清空 点击结算还是会有地址)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //购物车数据为空
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByConditions(ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build());
        if (shoppingCarts == null || shoppingCarts.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //1.订单表插入一条
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        //订单号不用UUID 而是时间戳+userId
        orders.setNumber(LocalDateTime.now().toString() + "_" + BaseContext.getCurrentId());
        //未支付
        orders.setPayStatus(Orders.UN_PAID);
        //等待付款
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(BaseContext.getCurrentId());
        //设置下单时间
        orders.setOrderTime(LocalDateTime.now());
        //通过地址簿填写一些个人信息
        //orders.setUserName(); 注意 这个是没法写的 这个要的是user表中 真正的真实姓名
        orders.setConsignee(addressBook.getConsignee());//收货人姓名 用户可以随便写
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());
        orderMapper.insert(orders);
        //2.订单明细表插入n条数据
        orderDetailMapper.insert(shoppingCarts, orders.getId());
        //3.下单后清除购物车
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        shoppingCartMapper.deleteByConditions(shoppingCart);
        //4.构建返回数据
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime()).build();

        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
       /* JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );*/
        JSONObject jsonObject = new JSONObject();
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 历史订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO, ControllerType controllerType) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        //如果是user端调用 则需要userId
        if (controllerType == ControllerType.USER_CLIENT) {
            ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        }
        //如果是admin端调用 则不需要

        //初步获取所有订单  但是List<OrderDetail> orderDetailList 仍需填充
        Page<OrderVO> orderVOList = orderMapper.getByConditons(ordersPageQueryDTO);
        //空的话不需要去获取OrderDetail
        if (orderVOList != null && !orderVOList.isEmpty()) {
            //拿到orderId的集合
            List<Long> orderIDList = orderVOList.stream().map(OrderVO::getId).collect(Collectors.toList());
            //批量查询出来orderDetailList
            List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderIdList(orderIDList);
            Map<Long, List<OrderDetail>> OrderDetailMap = orderDetailList.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));
            orderVOList.forEach(orderVO -> orderVO.setOrderDetailList(OrderDetailMap.get(orderVO.getId())));
        }

        return new PageResult(orderVOList.getTotal(), orderVOList.getResult());
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO orderDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderIdList(Collections.singletonList(id));
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 取消订单
     *
     * @param id
     */
    @Transactional
    @Override
    public void orderCancel(Long id) {
        Orders order = orderMapper.getById(id);

        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (order.getStatus() >= Orders.CONFIRMED) {
            //接单后 无法取消订单 自己联系商家吧  其实可以不写的 因为接单后 不会暴露api接口 user端根本无法发送请求
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }


        //如果是待接单 则需要退款
        if (Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED)) {
            //微信小程序调用退款

            //我们这里直接设置成退款
            order.setPayStatus(Orders.REFUND);
        }

        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason("用户取消");
        order.setStatus(Orders.CANCELLED);

        orderMapper.update(order);
    }

    /**
     * 取消订单 admin端
     * @param ordersCancelDTO
     */
    @Override
    public void orderCancel(OrdersCancelDTO ordersCancelDTO) {
        Orders order = orderMapper.getById(ordersCancelDTO.getId());

        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }


        //如果是待接单及以上状态 则需要退款
        if (order.getStatus()>= Orders.TO_BE_CONFIRMED) {
            //微信小程序调用退款

            //我们这里直接设置成退款
            order.setPayStatus(Orders.REFUND);
        }

        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(ordersCancelDTO.getCancelReason());
        order.setStatus(Orders.CANCELLED);

        orderMapper.update(order);
    }

    /**
     * 再来一单
     *
     * @param id
     */
    @Transactional
    @Override
    public void repetition(Long id) {
        //清空购物车
        shoppingCartMapper.deleteByConditions(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());

        List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderIdList(Collections.singletonList(id));
        List<ShoppingCart> shoppingCartList = new ArrayList<>();

        Long userId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();
        orderDetails.forEach(orderDetail -> {
            ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).createTime(now).build();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCartList.add(shoppingCart);
        });
        shoppingCartMapper.Batchinsert(shoppingCartList);
    }

    /**
     * 统计各个订单的数量
     *
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        //获取所有Orders对象
        Page<OrderVO> orderVOList = orderMapper.getByConditons(new OrdersPageQueryDTO());
        OrderStatisticsVO orderStatisticsVO = null;
        if (orderVOList != null && !orderVOList.isEmpty()) {
            Map<Integer, List<OrderVO>> map = orderVOList.stream().collect(Collectors.groupingBy(OrderVO::getStatus));
            orderStatisticsVO = OrderStatisticsVO.builder()
                    .toBeConfirmed(map.get(Orders.TO_BE_CONFIRMED) == null ? 0 : map.get(Orders.TO_BE_CONFIRMED).size())
                    .confirmed(map.get(Orders.CONFIRMED) == null ? 0 : map.get(Orders.CONFIRMED).size())
                    .deliveryInProgress(map.get(Orders.DELIVERY_IN_PROGRESS) == null ? 0 : map.get(Orders.DELIVERY_IN_PROGRESS).size())
                    .build();
        }


        return orderStatisticsVO;
    }

    /**
     * 拒单 admin
     * @param ordersRejectionDTO
     */
    @Override
    public void orderRejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = orderMapper.getById(ordersRejectionDTO.getId());

        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //如果是待接单及以上状态 则需要退款  其实可以不写的 因为调用这个api的必定是TO_BE_CONFIRMED待接单状态
        if (order.getStatus()>= Orders.TO_BE_CONFIRMED) {
            //微信小程序调用退款

            //我们这里直接设置成退款
            order.setPayStatus(Orders.REFUND);
        }

        order.setCancelTime(LocalDateTime.now());
        order.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        order.setStatus(Orders.CANCELLED);

        orderMapper.update(order);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void orderConfirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders order = orderMapper.getById(ordersConfirmDTO.getId());
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        order.setStatus(Orders.CONFIRMED);

    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void orderDelivery(Long id) {

        Orders order = orderMapper.getById(id);

        if (order == null || !Objects.equals(order.getStatus(), Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        order.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(order);

    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void orderComplete(Long id) {
        Orders order = orderMapper.getById(id);

        if (order == null || !Objects.equals(order.getStatus(), Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        order.setStatus(Orders.COMPLETED);
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(order);

    }
}
