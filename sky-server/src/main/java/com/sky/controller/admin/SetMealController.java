package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    /**
     * 添加套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping("")
    public Result<String> addSetMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("添加套餐：{}", setmealDTO);
        setMealService.addSetMeal(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id查询setMeal
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> getSetMealById(@PathVariable int id) {
        log.info("根据id查询菜品:{}", id);
        SetmealVO setmealVO = setMealService.getSetMealById(id);
        return Result.success(setmealVO);
    }

    /**
     * 套餐分页查询
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询:{}",setmealPageQueryDTO);
        PageResult pageResult = setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根绝ids批量删除
     * @param ids
     * @return
     */
    @DeleteMapping("")
    public Result<String> deleteSetMeal(@RequestParam List<Long> ids) {
        log.info("根绝ids批量删除:{}", ids);
        setMealService.deleteByids(ids);
        return Result.success();
    }
    /**
     * 套餐的起售和停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable int status,@RequestParam long id) {
        log.info("套餐的起售和停售:status:{},id:{}", status,id);
        setMealService.updateStatus(status,id);
        return Result.success();
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping("")
    public Result<String> updateSetMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐:{}", setmealDTO);
        setMealService.updateSetMeal(setmealDTO);
        return Result.success();
    }

    
}
