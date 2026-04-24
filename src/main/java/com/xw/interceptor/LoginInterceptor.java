package com.xw.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xw.common.Result;
import com.xw.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author XW
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        String requestURI = request.getRequestURI();

        if (StringUtils.hasText(token)) {
            try {
                // 🌟 终极修复 1：严格的身份隔离校验
                boolean isAdminApi = requestURI.startsWith("/api/admin");
                boolean isAdminToken = token.startsWith("admin_token");

                // 如果是访问管理员接口，但拿的不是管理员 Token -> 拦截！
                if (isAdminApi && !isAdminToken) {
                    log.warn("越权访问：试图用普通用户 Token 访问管理员接口！");
                    return rejectRequest(response, "权限不足，拒绝访问");
                }

                // 如果是访问普通用户接口，但拿的是管理员 Token -> 拦截！
                // (防止管理员 ID 覆盖了普通用户 ID 造成脏数据)
                if (!isAdminApi && isAdminToken) {
                    log.warn("越权访问：管理员 Token 不允许调用普通用户业务接口！");
                    return rejectRequest(response, "管理员禁止进行前台用户操作");
                }

                // 🌟 终极修复 2：解析 ID
                String[] parts = token.split("_");
                if (parts.length > 1) {
                    Long userId = Long.parseLong(parts[parts.length - 1]);
                    ThreadLocalUtil.setCurrentUserId(userId);
                    return true;
                }
            } catch (Exception e) {
                log.error("Token 解析失败: {}", e.getMessage());
            }
        }

        return rejectRequest(response, "未登录或登录已过期");
    }

    // 抽离出来的统一拦截响应方法，保持代码整洁
    private boolean rejectRequest(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        Result<String> errorResult = Result.error(msg);
        errorResult.setCode(401);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResult));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}