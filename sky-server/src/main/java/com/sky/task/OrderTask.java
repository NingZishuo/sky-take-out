package com.sky.task;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?") //每分钟触发一次
    public void processTimeoutOrder() {
        log.info("超时订单处理:{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,time);
        ordersList.forEach(order -> {
            order.setStatus(Orders.CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            order.setCancelReason("订单超时");
            orderMapper.update(order);
        });
    }

    @Scheduled(cron = "0 0 1 * * ?") //每天凌晨一点出发
    public void processDeliveryTimeOrder() {
        log.info("派送中订单处理:{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,time);
        ordersList.forEach(order -> {
            order.setStatus(Orders.COMPLETED);
            order.setDeliveryTime(time);
            orderMapper.update(order);
        });
    }
}
