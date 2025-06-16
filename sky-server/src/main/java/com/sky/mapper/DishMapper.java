package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {



    /**
     * 根据ids的status  查询到底有几个在售卖中
     * @param ids
     * @return
     */
    @Select("select count(*) from dish where id in (${ids}) and status = 1 ")
    Integer getStatusById(String ids);
    /**
     * 添加菜品
     * @param dish
     */
    @AutoFill(operationType = OperationType.INSERT)
    void insertDish(Dish dish);


    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectDishesByCategoryId(Integer categoryId);

    DishVO selectDishById(Integer id);

    void deleteByIds(String ids);

    @AutoFill(operationType = OperationType.UPDATE)
    void update(Dish dish);



}
