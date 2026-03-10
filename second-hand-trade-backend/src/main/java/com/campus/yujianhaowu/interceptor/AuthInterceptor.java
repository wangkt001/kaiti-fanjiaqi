package com.campus.yujianhaowu.interceptor;

import com.campus.yujianhaowu.common.ResultCode;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.util.UserContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器 - 简化版（直接从请求头获取 userId）
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 从请求头获取 userId
        String userIdStr = request.getHeader(USER_ID_HEADER);

        if (!StringUtils.hasText(userIdStr)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        try {
            Long userId = Long.parseLong(userIdStr);

            if (userId <= 0) {
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }

            // 将用户 ID 存入请求属性
            request.setAttribute("userId", userId);

            // 设置到用户上下文
            UserContextUtil.setCurrentUserId(userId);

            return true;
        } catch (NumberFormatException e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 清除用户上下文
        UserContextUtil.clear();
    }
}
