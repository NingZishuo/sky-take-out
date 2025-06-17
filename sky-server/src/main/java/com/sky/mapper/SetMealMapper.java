package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {

    /**
     * 添加套餐
     * @param setmeal
     */
    @AutoFill(operationType = OperationType.INSERT)
    void addSetMeal(Setmeal setmeal);


    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    SetmealVO selectById(long id);

    /**
     * 分页查询基础数据
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据ids 查询是否有起售的菜品
     * @param ids,status
     * @return
     */
    int getStatusById(List<Long> ids,int status);

    /**
     * 根绝ids批量删除
     * @param ids
     * @return
     */
    void deleteByids(List<Long> ids);

    /**
     * 综合更新套餐
     * @param setmeal
     */
    @AutoFill(operationType = OperationType.UPDATE)
    void update(Setmeal setmeal);
}
