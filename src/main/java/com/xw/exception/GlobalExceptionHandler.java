package com.xw.exception;

import com.xw.common.Result;
import com.xw.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 拦截Controller层抛出的异常，转化为统一的Result格式返回
 *
 * @author XW
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private HttpServletRequest request;

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<String>> handleBusinessException(BusinessException e) {
        int code = e.getCode() != null ? e.getCode() : 400;
        log.error("业务异常 [{}]: {}", getContext(), e.getMessage());
        return ResponseEntity.status(code).body(Result.error(code, e.getMessage()));
    }

    /**
     * 处理参数校验异常（@Valid/@Validated on @RequestBody / form bindings）
     * BindException 是 MethodArgumentNotValidException 的父类，一个方法覆盖两种场景
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<String>> handleBindException(BindException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常 [{}]: {}", getContext(), message);
        return ResponseEntity.status(400).body(Result.error(400, "参数错误: " + message));
    }

    /**
     * 处理类级别 @Validated 校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<String>> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常 [{}]: {}", getContext(), message);
        return ResponseEntity.status(400).body(Result.error(400, "参数错误: " + message));
    }

    /**
     * 处理请求体 JSON 格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败 [{}]", getContext(), e);
        return ResponseEntity.status(400).body(Result.error(400, "请求参数格式错误"));
    }

    /**
     * 处理 URL 参数类型转换失败
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型转换失败 [{}]: {}", getContext(), e.getMessage());
        return ResponseEntity.status(400).body(Result.error(400, "参数类型错误: " + e.getName()));
    }

    /**
     * 处理缺少必填请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("缺少必填参数 [{}]: {}", getContext(), e.getMessage());
        return ResponseEntity.status(400).body(Result.error(400, "缺少必填参数: " + e.getParameterName()));
    }

    /**
     * 处理请求方法不支持（如 POST 接口用 GET 访问）
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持 [{}]: {}", getContext(), e.getMessage());
        return ResponseEntity.status(405).body(Result.error(405, e.getMessage()));
    }

    /**
     * 处理文件上传超出大小限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("文件上传超限 [{}]", getContext(), e);
        return ResponseEntity.status(413).body(Result.error(413, "上传文件大小超出限制"));
    }

    /**
     * 处理 404 资源不存在
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<String>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("请求资源不存在 [{}]: {}", getContext(), e.getMessage());
        return ResponseEntity.status(404).body(Result.error(404, "请求的资源不存在"));
    }

    /**
     * 处理数据库完整性异常（唯一索引冲突、非空限制等）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<String>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message;
        Throwable root = e.getMostSpecificCause();
        if (root instanceof DuplicateKeyException || (root.getMessage() != null && root.getMessage().contains("Duplicate entry"))) {
            message = "数据记录冲突，请检查是否重复提交";
        } else {
            message = "数据记录冲突或格式不合规";
        }
        log.error("数据库异常 [{}]: {}", getContext(), root.getMessage(), e);
        return ResponseEntity.status(400).body(Result.error(400, message));
    }

    /**
     * 处理所有未被其他 Handler 捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<String>> handleException(Exception e) {
        log.error("系统内部异常 [{}]: ", getContext(), e);
        return ResponseEntity.status(500).body(Result.error(500, "服务器开小差了，请稍后再试"));
    }

    private String getContext() {
        try {
            Long userId = ThreadLocalUtil.getCurrentUserId();
            String uri = request != null ? request.getRequestURI() : "unknown";
            if (userId != null) {
                return "用户ID:" + userId + ", 请求:" + uri;
            }
            return "请求:" + uri;
        } catch (Exception ignored) {
            return "unknown";
        }
    }
}
