package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    SetMealMapper setMealMapper;

    @Autowired
    DishMapper dishMapper;

    /**
     * 添加套餐
     *
     * @param setmealDTO
     * @return
     */
    @Override
    public void addSetMeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.addSetMeal(setmeal);
        for (SetmealDish setmealDish : setmealDTO.getSetmealDishes()) {
            setMealMapper.addSetMealDish(setmealDish);
        }
    }

}
