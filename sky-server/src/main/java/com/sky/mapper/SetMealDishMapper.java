package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetMealDishMapper {

    /**
     * 根据category的id 查询有没有哪个dish被包含到套餐里
     * @param id
     * @return
     */
    @Select("select count(*) from setmeal_dish where dish_id = #{id}")
    Integer countDishByCategoryId(long id);

    /**
     * 根据category的id 查询到底某个套餐有没有dish
     * @param id
     * @return
     */
    @Select("select count(*) from setmeal_dish where setmeal_id = #{id}")
    Integer countSetMealByCategoryId(long id);

    /**
     * 根据dish的ids查询有没有哪个dish被包含到套餐里
     * @param ids
     * @return
     */
    @Select("select count(*) from setmeal_dish where dish_id in (${ids}) ")
    Integer countByDishIds(String ids);
}
