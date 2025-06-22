package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 根据categoryId查询菜品以及其风味
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(@RequestParam Long categoryId) {
        log.info("根据categoryId查询菜品:{}",categoryId);
        List<DishVO> dishVOS = null;
        //构造redis中的key 规则:dish_categoryId
        String key = "dish_" + categoryId;
        //查询是否有这个key 有的话说明有缓存菜品
        if(redisTemplate.hasKey(key)) {
            dishVOS = (List<DishVO>) redisTemplate.opsForValue().get(key);
        }else{
            //没有的话查询数据库
            dishVOS = dishService.selectDishVOSByCategoryId(categoryId);
            //且把这个数据放入redis
            redisTemplate.opsForValue().set(key,dishVOS);
        }

        return Result.success(dishVOS);
    }


}
