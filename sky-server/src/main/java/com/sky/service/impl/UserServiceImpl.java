package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.json.JacksonObjectMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    //微信服务接口地址
    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties weChatProperties;


    /**
     * 从wechat拿取用户信息
     * @param userLoginDTO
     * @return
     */
    private Map<String,String> getResultFromWeChat(UserLoginDTO userLoginDTO){
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", userLoginDTO.getCode());
        paramMap.put("grant_type", "authorization_code");

        String jsonResult = HttpClientUtil.doGet(WX_LOGIN, paramMap);
        JSONObject jsonObject = JSON.parseObject(jsonResult);

        Map<String, String> resultMap = new HashMap<>();
        //权限有限 只能拿到openid
        resultMap.put("openid", jsonObject.getString("openid"));
        resultMap.put("errmsg", jsonObject.getString("errmsg"));

        return resultMap;
    }


    /**
     * 登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {

        Map<String, String> resultMap = getResultFromWeChat(userLoginDTO);
        //权限有限 只能拿到openid
        String openid = resultMap.get("openid");

        //注意 你调用的是别人的接口 是有可能失败的
        if(openid == null){
            throw new LoginFailedException(resultMap.get("errmsg"));
        }

        User user = userMapper.getByOpenId(openid);

        //TODO 这里我有个想法  假如resultMap 已经有user所需的各种数据  是不是能用反射挨个赋值进去user呢？
        //没有user  是新用户需要自动注册
        if (user == null) {
            user = User.builder().
                    openid(openid).
                    createTime(LocalDateTime.now()).
                    build();
            userMapper.insert(user);
        }
        return user;
    }


}
