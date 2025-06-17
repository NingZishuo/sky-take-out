package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
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
}
