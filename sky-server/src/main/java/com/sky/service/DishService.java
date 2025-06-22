package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {


    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    void addDishWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据categoryId查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> selectDishesByCategoryId(Integer categoryId);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO selectDishById(Long id);

    /**
     * 根据ids批量删除菜品
     * @param ids
     * @return
     */
    void deleteByIds(List<Long> ids);

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    void updateDish(DishDTO dishDTO);

    /**
     * 禁用或启用菜品
     * @param status
     * @param id
     * @return
     */
    void updateStatus(Integer status, Long id);

    /**
     * 根据categoryId查询菜品以及其风味
     * @param categoryId
     * @return
     */
    List<DishVO> selectDishVOSByCategoryId(Long categoryId);
}
