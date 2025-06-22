package com.sky.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 配置类 用来创建RedisTemplate对象
 */
@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    //<String,Object>这个不写 默认是两个Object
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        log.info("开始创建RedisTemplate");
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        //设置连接工厂对象 注意依赖引入的时候redisConnectionFactory会被生成到ioc容器中
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化器 把 Java 层面的 key 和 value，按照这些序列化器转成 byte[]传给redis”
        //这行会让所有key直接转化成redis的string类型的key传过去 (假如你传对象 会因为类型无法转换报错)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //在序列化 Java 对象时，会将其转换为 JSON 字符串。在反序列化时，它默认会尝试将其直接反序列化为 Object 类型(或者说你填写的泛型类型)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 关键：注册 Java 8 日期时间模块
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return redisTemplate;

    }
}
