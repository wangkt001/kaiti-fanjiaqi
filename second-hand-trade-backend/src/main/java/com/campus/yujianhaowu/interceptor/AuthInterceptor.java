package com.campus.yujianhaowu.interceptor;

import com.campus.yujianhaowu.common.ResultCode;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.util.JwtUtil;
import com.campus.yujianhaowu.util.UserContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 认证拦截器
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USER_ID_ATTR = "userId";

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_TOKEN_PREFIX = "token:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 获取 Token
        String token = getTokenFromRequest(request);

        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        try {
            // 验证 Token
            Long userId = jwtUtil.validateToken(token);

            if (userId == null) {
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }

            // 检查 Token 是否在 Redis 中存在（用于登出功能）
            String redisKey = REDIS_TOKEN_PREFIX + userId;
            String redisToken = redisTemplate.opsForValue().get(redisKey);

            if (!StringUtils.hasText(redisToken) || !redisToken.equals(token)) {
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }

            // 刷新 Token 过期时间
            redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);

            // 将用户 ID 存入请求属性
            request.setAttribute("userId", userId);

            // 设置到用户上下文
            UserContextUtil.setCurrentUserId(userId);

            return true;
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

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从 Header 中获取
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 从 Parameter 中获取
        return request.getParameter("token");
    }
}
