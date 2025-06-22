package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 * 注意 这里一定不能配置错了 WebMvcConfigurationSupport 会让application.yml中关于静态资源的配置失效
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */

    public  void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/user/shop/status");

    }


    /**
     * 扩展消息转换器
     * Spring Boot 会自动配置一些默认的 HttpMessageConverter，
     * 其中包括用于处理 JSON 的 MappingJackson2HttpMessageConverter。
     * 我们通过重写 extendMessageConverters 方法来修改默认的 ObjectMapper。
     *
     * @param converters Spring Boot 自动配置的消息转换器列表
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建一个 MappingJackson2HttpMessageConverter 实例
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        // 将你的自定义 JacksonObjectMapper 实例设置给这个消息转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        // 将这个自定义的 MappingJackson2HttpMessageConverter 添加到转换器列表的最前面
        // 这样可以确保它优先于 Spring Boot 默认的 JSON 转换器被使用
        // 如果你的项目中可能还有其他 JSON 转换器（如 FastJSON），这样做能保证你的 JacksonObjectMapper 生效
        converters.add(0, messageConverter);
    }







}
