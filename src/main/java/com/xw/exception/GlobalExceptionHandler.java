package com.xw.exception;

import com.xw.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 拦截 Controller 层抛出的异常，将其转化为统一的 Result 格式返回给前端
 * @author XW
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. 拦截自定义的业务异常 (BusinessException)
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        Result<String> result = Result.error(e.getMessage());
        if (e.getCode() != null) {
            result.setCode(e.getCode());
        }
        return result;
    }

    /**
     * 2. 拦截参数校验异常 (如 @NotBlank, @NotNull 等 javax.validation 注解未通过时抛出的异常)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 将所有参数校验错误信息拼接起来
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数校验异常: {}", message);
        return Result.error("参数错误: " + message);
    }

    /**
     * 3. 拦截表单绑定异常 (针对没有加 @RequestBody 的参数绑定异常)
     */
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数绑定异常: {}", message);
        return Result.error("参数错误: " + message);
    }

    /**
     * 4. 拦截所有的运行时异常 (RuntimeException) 和 未知异常 (兜底)
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 打印完整的异常堆栈信息到日志中，方便后端开发排查问题
        log.error("系统内部异常: ", e);

        // 返回给前端的提示，屏蔽掉底层代码细节，只返回 Exception 的简短 message 或通用提示
        return Result.error("服务器开小差了，请稍后再试: " + e.getMessage());
    }
}