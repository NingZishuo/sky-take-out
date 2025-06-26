package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 插入新的购物车数据
     * @param shoppingCart
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 综合更新
     * @param shoppingCart
     */
    Integer update(ShoppingCart shoppingCart);


    /**
     * 根据某个条件进行查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> selectByConditions(ShoppingCart shoppingCart);

    /**
     * 综合进行删除
     * @param shoppingCart
     */
    void deleteByConditions(ShoppingCart shoppingCart);

    /**
     * 批量插入
     * @param shoppingCartList
     */
    void Batchinsert(List<ShoppingCart> shoppingCartList);
}

