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

        System.out.println("=== [AuthInterceptor] ===");
        System.out.println("请求 URL: " + request.getRequestURI());
        System.out.println("请求头 X-User-Id: " + userIdStr);

        if (!StringUtils.hasText(userIdStr)) {
            System.out.println("❌ 未找到 X-User-Id 请求头");
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        System.out.println("✅ 找到 X-User-Id: " + userIdStr);

        try {
            Long userId = Long.parseLong(userIdStr);

            System.out.println("解析后的 userId: " + userId);

            if (userId <= 0) {
                System.out.println("❌ userId <= 0");
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }

            // 将用户 ID 存入请求属性
            request.setAttribute("userId", userId);

            System.out.println("✅ 已设置 request.userId = " + userId);
            System.out.println("验证 request.getAttribute('userId'): " + request.getAttribute("userId"));
            System.out.println("=========================");

            // 设置到用户上下文
            UserContextUtil.setCurrentUserId(userId);

            return true;
        } catch (NumberFormatException e) {
            System.out.println("❌ NumberFormatException: " + e.getMessage());
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("❌ Exception: " + e.getMessage());
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
