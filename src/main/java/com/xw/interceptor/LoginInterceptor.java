package com.xw.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xw.common.Result;
import com.xw.utils.JwtUtil;
import com.xw.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 * 验证用户Token并进行权限校验
 *
 * @author XW
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 请求前置处理
     * 验证Token有效性并进行权限校验
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @return true-放行，false-拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        String requestURI = request.getRequestURI();

        if (StringUtils.hasText(token)) {
            try {
                // 1. 验证Token是否有效（签名正确且未过期）
                if (!JwtUtil.validateToken(token)) {
                    log.warn("Token验证失败：Token无效或已过期");
                    return rejectRequest(response, "Token无效或已过期，请重新登录");
                }

                // 2. 判断是否为管理员接口
                boolean isAdminApi = requestURI.startsWith("/api/admin");
                boolean isAdminToken = JwtUtil.isAdminToken(token);

                // 3. 权限校验：普通用户不能访问管理员接口
                if (isAdminApi && !isAdminToken) {
                    log.warn("越权访问：试图用普通用户Token访问管理员接口");
                    return rejectRequest(response, "权限不足，拒绝访问");
                }

                // 4. 权限校验：管理员不能访问普通用户接口
                if (!isAdminApi && isAdminToken) {
                    log.warn("越权访问：管理员Token不允许调用普通用户业务接口");
                    return rejectRequest(response, "管理员禁止进行前台用户操作");
                }

                // 5. 从Token中提取用户ID
                Long userId = isAdminToken ? 
                    JwtUtil.getAdminIdFromToken(token) : 
                    JwtUtil.getUserIdFromToken(token);

                if (userId == null) {
                    log.error("Token解析失败：无法提取用户ID");
                    return rejectRequest(response, "Token解析失败");
                }

                // 6. 将用户ID存入ThreadLocal，供后续业务使用
                ThreadLocalUtil.setCurrentUserId(userId);
                return true;

            } catch (Exception e) {
                log.error("Token处理异常: {}", e.getMessage(), e);
                return rejectRequest(response, "Token处理异常，请重新登录");
            }
        }

        return rejectRequest(response, "未登录或登录已过期");
    }

    /**
     * 统一拦截响应方法
     *
     * @param response HTTP响应
     * @param msg      错误消息
     * @return false
     */
    private boolean rejectRequest(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        Result<String> errorResult = Result.error(msg);
        errorResult.setCode(401);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResult));
        return false;
    }

    /**
     * 请求完成后清理ThreadLocal
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @param ex       异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}