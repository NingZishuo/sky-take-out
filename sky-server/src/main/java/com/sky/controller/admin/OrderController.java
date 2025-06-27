package com.sky.controller.admin;

import com.sky.dto.*;
import com.sky.enumeration.ControllerType;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理
 */
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 统计各个订单的数量
     * @return
     */
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        log.info("统计各个订单的数量");
        OrderStatisticsVO orderStatisticsVO =  orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 分页查询订单(有条件)
     * @return
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("分页查询订单(有条件):{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO, ControllerType.ADMIN_CLIENT);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    public Result<OrderVO> orderDetail(@PathVariable Long id) {
        log.info("订单详情查询{}", id);
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }


    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    public Result<String> orderCancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单{}", ordersCancelDTO);
        orderService.orderCancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    public Result<String> orderRejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单{}", ordersRejectionDTO);
        orderService.orderRejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    public Result<String> orderConfirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单:{}", ordersConfirmDTO);
        orderService.orderConfirm(ordersConfirmDTO);
        return Result.success();
    }



    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result<String> orderDelivery(@PathVariable Long id) {
        log.info("派送:{}", id);
        orderService.orderDelivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result<String> orderComplete(@PathVariable Long id) {
        log.info("完成:{}", id);
        orderService.orderComplete(id);
        return Result.success();
    }

}
