package com.xw.exception;

/**
 * 自定义业务异常
 *
 * @author XW
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误状态码
     */
    private Integer code;

    /**
     * 构造方法，默认状态码为500
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造方法，自定义状态码
     *
     * @param code    错误状态码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误状态码
     *
     * @return 状态码
     */
    public Integer getCode() {
        return code;
    }
}