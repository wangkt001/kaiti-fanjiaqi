package com.campus.yujianhaowu.util;

import com.campus.yujianhaowu.exception.BusinessException;

/**
 * 用户上下文工具类
 */
public class UserContextUtil {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static Long getCurrentUserId() {
        Long userId = CURRENT_USER_ID.get();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        return userId;
    }

    public static void clear() {
        CURRENT_USER_ID.remove();
    }
}
