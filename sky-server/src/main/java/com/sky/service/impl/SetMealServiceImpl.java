package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private CategoryMapper  categoryMapper;

    /**
     * 添加套餐
     *
     * @param setmealDTO
     * @return
     */
    @Override
    public void addSetMeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(StatusConstant.DISABLE);
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //添加套餐
        setMealMapper.addSetMeal(setmeal);
        //添加套餐菜品对应关系表
        setMealDishMapper.addSetMealDish(setmealDTO.getSetmealDishes(),setmeal.getId());
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getSetMealById(long id) {
        SetmealVO setmealVO = setMealMapper.selectById(id);
        List<SetmealDish> setmealDishes = setMealDishMapper.selectBySetMealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        //缺少分类名称 categoryName  缺少套餐和菜品的关联关系 setmealDishes
        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);

        //获取catgoryId  注意 这里id是错序的 且id已被去重
        List<Long> categoryIds = page.stream().map(SetmealVO::getCategoryId).distinct().collect(Collectors.toList());
        if (!categoryIds.isEmpty()) {
            //根据id获取categories
            List<Category> categories = categoryMapper.selectCategoriesByIds(categoryIds);
            Map<Long, String> catgoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Category::getName));
            for (SetmealVO setmealVO : page) {
                setmealVO.setCategoryName(catgoryMap.get(setmealVO.getCategoryId()));
            }
        }

        //获取setMealIds  同样乱序
        List<Long> setMealIds = page.stream().map(SetmealVO::getId).collect(Collectors.toList());

        if (!setMealIds.isEmpty()) {
            //拿到setMealDishes
            List<SetmealDish> setMealDishes = setMealDishMapper.selectBatchBySetMealIds(setMealIds);
            Map<Long, List<SetmealDish>> setMealDishMap = setMealDishes.stream().collect(Collectors.groupingBy(SetmealDish::getSetmealId));

            for (SetmealVO setmealVO : page) {
                setmealVO.setSetmealDishes(setMealDishMap.get(setmealVO.getId()));
            }
        }

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根绝ids批量删除
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public void deleteByids(List<Long> ids) {
        int count = setMealMapper.getStatusById(ids,StatusConstant.ENABLE);
        //在售状态下，不可删除套餐
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        setMealMapper.deleteByids(ids);
        setMealDishMapper.deleteBySetMealIds(ids);
    }

    /**
     * 套餐的起售和停售
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(int status, long id) {
        //如果是有禁售变为起售 才需要检查dish有没有禁售的
        if (status == StatusConstant.ENABLE) {
            //先获取setmeal对应的list<long> dishid
            List<SetmealDish> setmealDishes = setMealDishMapper.selectBySetMealId(id);
            if (!setmealDishes.isEmpty()) {
                List<Long> dishIds = setmealDishes.stream().map(SetmealDish::getDishId).collect(Collectors.toList());
                //查看禁售dish的数量
                Integer statusCount = dishMapper.getStatusById(dishIds, StatusConstant.DISABLE);
                //大于0说明有dish是禁售的
                if (statusCount > 0) {
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        //照常update状态
        Setmeal setmeal = Setmeal.builder().
                id(id).
                status(status).
                build();
        setMealMapper.update(setmeal);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @Override
    public void updateSetMeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.update(setmeal);
        setMealDishMapper.deleteBySetMealIds(Collections.singletonList(setmealDTO.getId()));
        setMealDishMapper.addSetMealDish(setmealDTO.getSetmealDishes(),setmealDTO.getId());
    }
}
