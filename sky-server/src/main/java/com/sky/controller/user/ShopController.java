package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺状态管理
 */
@RestController
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //好像因为太简单 就没有去写业务层这些了
    /**
     * 设置营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    public Result<String> setStatus(@PathVariable String status) {
        log.info("设置营业状态:{}",status);
        redisTemplate.opsForValue().set("SHOP_STATUS",status);
        return Result.success();
    }


    /**
     * 获取营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer)redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("查询营业状态:{}",status);
        return Result.success(status);
    }


}
