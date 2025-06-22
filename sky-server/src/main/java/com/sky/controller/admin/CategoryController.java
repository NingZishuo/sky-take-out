package com.sky.controller.admin;

import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类管理
 */
@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @return
     */
    @PostMapping("")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分类的分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类的分页查询:{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 分类的启用和禁用
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> editStatus(@PathVariable Integer status,@RequestParam long id) {
        log.info("种类的启用和禁用:{}", status,id);
        Category category = Category.builder().
                status(status).
                id(id).
                build();
        categoryService.update(category);
        return Result.success();
    }

    /**
     * 分类的编辑
     * @param categoryDTO
     * @return
     */
    @PutMapping("")
    public Result<String> editCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("种类编辑:{}",categoryDTO);
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryService.update(category);
        return Result.success();
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping()
    public Result<String> deleteCategory(@RequestParam long id) {
        log.info("删除分类:{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据种类获得分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> getCategoryByType(@RequestParam Integer type) {
        log.info("根据type获得分类:{}",type);
        List<Category> categories = categoryService.getCategoryByType(type);
        return Result.success(categories);
    }

}
