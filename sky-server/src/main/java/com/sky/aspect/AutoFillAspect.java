package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面,实现公共字段自动填充处理逻辑
 */
@Component
@Aspect
@Slf4j
public class AutoFillAspect {
    /**
     * 指定切入点
     */
    //public int spring.CalculatorLogImpl.add(int,int)注意看完整示例
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
        //注意里面啥都不用写
    }

    @Before(value = "autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始公共字段填充");
        //固定写法 signature就相当于 之前的clazz
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //signature拿到的方法 一定是具体的某个  和直接clazz不一样 之前的都是抽象的
        Method method = signature.getMethod();

        //有了方法 抽象参数和注解不在话下
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.operationType();
        //想要具象参数 还得joinPoint
        Object object = joinPoint.getArgs()[0];
        //拿到具体需要赋值的类
        Class<?> parameterType = method.getParameterTypes()[0];

        //TODO 注意 我们这里默认非INSERT情况全是UPDATE
        try {
            if (operationType == OperationType.INSERT) {
                Method setCreateUser = parameterType.getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setCreateTime = parameterType.getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                setCreateUser.invoke(object, BaseContext.getCurrentId());
                setCreateTime.invoke(object, LocalDateTime.now());
            }
            Method setUpdateUser = parameterType.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            Method setUpdateTime = parameterType.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            setUpdateUser.invoke(object, BaseContext.getCurrentId());
            setUpdateTime.invoke(object, LocalDateTime.now());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }

}
