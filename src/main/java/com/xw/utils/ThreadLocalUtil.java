package com.xw.utils;

/**
 * ThreadLocal工具类
 * 用于在当前线程中保存和获取用户ID
 *
 * @author XW
 */
public class ThreadLocalUtil {
    
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 获取当前线程中的用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        return THREAD_LOCAL.get();
    }

    /**
     * 设置当前线程中的用户ID
     *
     * @param userId 用户ID
     */
    public static void setCurrentUserId(Long userId) {
        THREAD_LOCAL.set(userId);
    }

    /**
     * 清除ThreadLocal，防止内存泄漏
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}