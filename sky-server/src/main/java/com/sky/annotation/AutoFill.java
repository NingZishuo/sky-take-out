package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：用于标识某个方法需要进行公共字段填充处理
 */
@Retention(RetentionPolicy.RUNTIME) //runtime 运行时用 不然注解不保留
@Target({ElementType.METHOD}) //只给methond用
public @interface AutoFill {
   //数据库操作类型
    OperationType operationType();

}
