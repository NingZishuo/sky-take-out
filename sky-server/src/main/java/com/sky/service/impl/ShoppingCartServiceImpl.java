package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Transactional
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断该商品是否已经存在了 因为前端操作数量的按钮也是这个请求
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //如果存在 数量＋1
        shoppingCart.setNumber(1);
        Integer updateNum = shoppingCartMapper.update(shoppingCart);
        if (updateNum == 0) {
            //如果不存在 插入新数据
            //但是你要查额外的数据啊 比如name Image  这些到底是从dish还是setmeal 我们是不是要判断
            if (shoppingCartDTO.getDishId() != null) {
                //添加菜品进入购物车
                DishVO dishVO = dishMapper.selectDishById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dishVO.getName());
                shoppingCart.setAmount(dishVO.getPrice());
                shoppingCart.setImage(dishVO.getImage());
            } else {
                //添加套餐进入购物车
                SetmealVO setmealVO = setMealMapper.selectById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setImage(setmealVO.getImage());
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        List<ShoppingCart> list = shoppingCartMapper.selectByConditions(shoppingCart);
        return list;
    }

    /**
     * 削减某个商品的数量
     *
     * @param shoppingCartDTO
     * @return
     */
    @Transactional
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //数量-1
        shoppingCart.setNumber(-1);
        shoppingCartMapper.update(shoppingCart);

        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByConditions(shoppingCart);
        if (shoppingCarts.get(0).getNumber() == 0) {
            shoppingCartMapper.deleteByConditions(shoppingCart);
        }
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @Override
    public void clearShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                                                .userId(BaseContext.getCurrentId())
                                                .build();
        shoppingCartMapper.deleteByConditions(shoppingCart);
    }
}
