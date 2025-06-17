package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
    Integer countByDishIds(List<Long> ids);
    /**
     * 添加套餐菜品关系表
     *
     * @param setmealDishes,id
     * @return
     */
    void addSetMealDish(List<SetmealDish> setmealDishes, Long id);

    /**
     * 根据setMealId查询套餐菜品关系表
     * @param setMealId
     * @return
     */
    List<SetmealDish> selectBySetMealId(long setMealId);

    /**
     * 根据setMealId查询套餐菜品关系表
     * @param dishId
     * @return
     */
    List<SetmealDish> selectBySetDishId(long dishId);

    /**
     * 根据setMealIds 批量查询套餐附属菜品
     * @param setMealIds
     * @return
     */
    List<SetmealDish> selectBatchBySetMealIds(List<Long> setMealIds);


    /**
     * 根据setMealIds 批量删除套餐附属菜品
     * @param setMealIds
     * @return
     */
    void deleteBySetMealIds(List<Long> setMealIds);



}
