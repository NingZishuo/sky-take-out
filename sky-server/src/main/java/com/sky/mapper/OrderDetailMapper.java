package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {


    /**
     * 批量插入
     * @param shoppingCarts
     */
    void insert(List<ShoppingCart> shoppingCarts,Long orderId);

    /**
     * 根据orderId批量查询orderDetail
     * @param orderIdList
     * @return
     */
    List<OrderDetail> selectByOrderIdList(List<Long> orderIdList);
}
