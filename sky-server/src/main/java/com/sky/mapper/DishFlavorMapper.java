package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    /**
     * 添加风味
     *
     * @param flavors
     * @param dishId
     */
    void insertFlavors(List<DishFlavor> flavors, Long dishId);


    List<DishFlavor> selectFlavorsByDishId(List<Long> dishIds);

    void deleteByDishId(List<Long> ids);


}
