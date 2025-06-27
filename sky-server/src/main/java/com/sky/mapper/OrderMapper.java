package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 综合插入
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 综合查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> getByConditons(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询orders
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);


    /**
     * 查询超过15分钟还没有支付的订单
     * @param pendingPayment
     * @param time
     * @return
     */
    @Select("select * from orders where status = #{pendingPayment} and order_time <= #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer pendingPayment, LocalDateTime time);
}
