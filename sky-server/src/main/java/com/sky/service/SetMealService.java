package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    /**
     * 添加套餐
     * @param setmealDTO
     * @return
     */
    void addSetMeal(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getSetMealById(long id);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根绝ids批量删除
     * @param ids
     * @return
     */
    void deleteByids(List<Long> ids);

    /**
     * 套餐的起售和停售
     * @param status
     * @param id
     */
    void updateStatus(int status, long id);

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    void updateSetMeal(SetmealDTO setmealDTO);

    /**
     * 根据条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> selectSetMealByCondition(Setmeal setmeal);

    /**
     * 根据套餐id查询包含的菜品列表
     * @param setMealid
     * @return
     */
    List<DishItemVO> selectSetMealDishesBySetMealId(Long setMealid);
}
