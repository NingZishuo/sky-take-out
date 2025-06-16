package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增菜品和其风味
     *
     * @param dishDTO
     * @return
     */
    @Transactional
    @Override
    public void addDishWithFlavor(DishDTO dishDTO) {
        //显然 菜品和风味要分开插入
        //添加菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insertDish(dish);
        Long dishId = dish.getId();
        //添加风味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            dishFlavorMapper.insertFlavors(flavors, dishId);
        }
    }

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Transactional
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据categoryId查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> selectDishesByCategoryId(Integer categoryId) {
        List<Dish> dishes = dishMapper.selectDishesByCategoryId(categoryId);
        return dishes;
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO selectDishById(Integer id) {
        DishVO dishVO = dishMapper.selectDishById(id);
        return dishVO;
    }

    /**
     * 根据ids批量删除菜品
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public void deleteByIds(String ids) {
        //通过DishIds 查询是否有套餐和dish关联
        Integer count = setMealDishMapper.countByDishIds(ids);
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //查询dish是否起售
        count = dishMapper.getStatusById(ids);
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        //删除dishes
        dishMapper.deleteByIds(ids);
        //删除dishes关联的风味
        dishFlavorMapper.deleteByDishId(ids);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @Transactional
    @Override
    public void updateDish(@RequestBody DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //先删除口味
        dishFlavorMapper.deleteByDishId(String.valueOf(dishDTO.getId()));
        //再添加新的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            dishFlavorMapper.insertFlavors(flavors, dishDTO.getId());
        }
    }

    /**
     * 禁用或启用菜品
     * @param status
     * @param id
     * @return
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        Dish dish = Dish.builder().
                id(id).
                status(status).
                build();
        dishMapper.update(dish);
    }
}
