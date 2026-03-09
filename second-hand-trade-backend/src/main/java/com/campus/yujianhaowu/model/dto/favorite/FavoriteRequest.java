package com.campus.yujianhaowu.model.dto.favorite;

import lombok.Data;

/**
 * 收藏请求 DTO
 */
@Data
public class FavoriteRequest {

    private String targetType;

    private Long targetId;
}
