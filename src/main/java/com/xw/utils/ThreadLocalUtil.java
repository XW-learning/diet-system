package com.xw.utils;

/**
 * ThreadLocal 工具类，用于在当前线程中保存和获取用户 ID
 * @author XW
 */
public class ThreadLocalUtil {
    // 提供 ThreadLocal 对象
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    // 根据键获取值
    public static Long getCurrentUserId() {
        return THREAD_LOCAL.get();
    }

    // 存储键值对
    public static void setCurrentUserId(Long userId) {
        THREAD_LOCAL.set(userId);
    }

    // 清除 ThreadLocal 防止内存泄漏
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}