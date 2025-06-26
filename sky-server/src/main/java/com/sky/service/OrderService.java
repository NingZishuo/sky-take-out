package com.sky.service;

import com.sky.dto.*;
import com.sky.enumeration.ControllerType;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {


    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO, ControllerType controllerType);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO orderDetail(Long id);

    /**
     * 取消订单 user端
     * @param id
     */
    void orderCancel(Long id);

    /**
     * 取消订单 admin端
     * @param ordersCancelDTO
     */
    void orderCancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 统计各个订单的数量
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     */
    void orderRejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 派送订单
     * @param id
     */
    void orderDelivery(Long id);

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void orderConfirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 完成订单
     * @param id
     * @return
     */
    void orderComplete(Long id);
}
