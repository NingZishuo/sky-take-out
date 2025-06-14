package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 添加分类
     * @param categoryDTO
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 分类的分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 分类统一更新
     * @param category
     * @return
     */
    void update(Category category);

    /**
     * 根据id删除分类
     * @param id
     */
    void deleteById(long id);

    /**
     * 根据type查询分类
     * @param type
     * @return
     */
    List<Category> getCategoryByType(long type);
}
