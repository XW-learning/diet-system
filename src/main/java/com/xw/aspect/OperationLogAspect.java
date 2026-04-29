package com.xw.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.xw.annotation.LogOperation;

import java.util.Arrays;

/**
 * 操作日志切面
 * 拦截带有@LogOperation注解的方法并记录日志
 *
 * @author XW
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    /**
     * 环绕通知，拦截所有加了@LogOperation注解的方法
     *
     * @param joinPoint 切点
     * @return 方法执行结果
     */
    @Around("@annotation(com.xw.annotation.LogOperation)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long time = System.currentTimeMillis() - beginTime;
        saveLog(joinPoint, time);

        return result;
    }

    /**
     * 保存日志信息
     *
     * @param joinPoint 切点
     * @param time      执行耗时
     */
    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogOperation annotation = signature.getMethod().getAnnotation(LogOperation.class);

        String operation = annotation != null ? annotation.value() : "未知操作";
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        String params = Arrays.toString(joinPoint.getArgs());

        log.info("================ 操作日志 ================");
        log.info("业务行为: {}", operation);
        log.info("请求方法: {}.{}()", className, methodName);
        log.info("请求参数: {}", params);
        log.info("执行耗时: {} ms", time);
        log.info("==========================================");
    }
}