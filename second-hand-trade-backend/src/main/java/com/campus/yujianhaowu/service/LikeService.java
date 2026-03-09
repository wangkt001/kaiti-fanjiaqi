package com.campus.yujianhaowu.service;

import java.util.Map;

public interface LikeService {

    void like(String targetType, Long targetId, Long userId);

    void unlike(String targetType, Long targetId, Long userId);

    boolean isLiked(String targetType, Long targetId, Long userId);

    Long getLikeCount(String targetType, Long targetId);

    Map<String, Object> getLikeStatus(String targetType, Long targetId, Long userId);
}
