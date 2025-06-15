package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {


    /**
     * 根据category的id 查询有没有哪个dish被包含到套餐里
     * @param id
     * @return
     */
    @Select("select count(*) from setmeal_dish where dish_id = #{id}")
    Integer countByCategoryId(long id);

    /**
     * 添加菜品
     * @param dish
     */
    @AutoFill(operationType = OperationType.INSERT)
    void insertDish(Dish dish);



}
