package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 新增分类
     * @param category
     */
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) VALUES " +
            "(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(operationType = OperationType.INSERT)
    void insert(Category category);

    /**
     * 分类的分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 分类统一更新
     * @param category
     */
    @AutoFill(operationType = OperationType.UPDATE)
    void update(Category category);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(long id);

    /**
     * 根据type查询分类
     * @param type
     * @return
     */
    List<Category> getCategoryByType(@Param("type") long type);

    /**
     * 根据id 批量获取categoryName
     * @param id
     * @return
     */
    @Select("select * from category where id = #{id}")
    String selectNameId(long id);
}
