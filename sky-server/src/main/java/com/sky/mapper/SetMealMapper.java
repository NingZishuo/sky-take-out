package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetMealMapper {

    /**
     * 添加套餐
     * @param setmeal
     */
    @Insert("insert into setmeal (category_id, name, price, status, description, image, create_time, update_time, create_user, update_user) " +
            "values (#{categoryId},#{name},#{price},#{status},#{description},#{image},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(operationType = OperationType.INSERT)
    void addSetMeal(Setmeal setmeal);

    @Insert("insert into setmeal_dish (setmeal_id, dish_id, name, price, copies) " +
            "VALUES (#{setmealId},#{dishId},#{name},#{price},#{copies})")
    @AutoFill(operationType = OperationType.INSERT)
    void addSetMealDish(SetmealDish setmealDish);

    /**
     * 根据category的id 查询到底某个套餐有没有dish
     * @param id
     * @return
     */
    @Select("select count(*) from setmeal_dish where setmeal_id = #{id}")
    Integer countByCategoryId(long id);

}
