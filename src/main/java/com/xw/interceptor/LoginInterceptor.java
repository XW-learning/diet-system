package com.xw.interceptor;

import com.xw.exception.BusinessException;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        String requestURI = request.getRequestURI();

        if (StringUtils.hasText(token)) {
            try {
                if (!JwtUtil.validateToken(token)) {
                    log.warn("Token验证失败：Token无效或已过期");
                    throw new BusinessException(401, "Token无效或已过期，请重新登录");
                }

                boolean isAdminApi = requestURI.startsWith("/api/admin");
                boolean isAdminToken = JwtUtil.isAdminToken(token);

                if (isAdminApi && !isAdminToken) {
                    log.warn("越权访问：试图用普通用户Token访问管理员接口");
                    throw new BusinessException(403, "权限不足，拒绝访问");
                }

                if (!isAdminApi && isAdminToken) {
                    log.warn("越权访问：管理员Token不允许调用普通用户业务接口");
                    throw new BusinessException(403, "管理员禁止进行前台用户操作");
                }

                Long userId = isAdminToken ?
                    JwtUtil.getAdminIdFromToken(token) :
                    JwtUtil.getUserIdFromToken(token);

                if (userId == null) {
                    log.error("Token解析失败：无法提取用户ID");
                    throw new BusinessException(401, "Token解析失败");
                }

                ThreadLocalUtil.setCurrentUserId(userId);
                return true;

            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("Token处理异常: {}", e.getMessage(), e);
                throw new BusinessException(401, "Token处理异常，请重新登录");
            }
        }

        throw new BusinessException(401, "未登录或登录已过期");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadLocalUtil.remove();
    }
}
