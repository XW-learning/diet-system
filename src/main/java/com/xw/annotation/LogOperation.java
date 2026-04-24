package com.xw.annotation;

import java.lang.annotation.*;

/**
 * 自定义操作日志注解
 */
@Target(ElementType.METHOD) // 作用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
@Documented
public @interface LogOperation {
    String value() default ""; // 用于记录操作名称，如 "修改密码"
}