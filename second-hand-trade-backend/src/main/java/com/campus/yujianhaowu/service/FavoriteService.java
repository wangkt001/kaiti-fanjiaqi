package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.vo.ProductVO;

import java.util.Map;

public interface FavoriteService {

    void favorite(String targetType, Long targetId, Long userId);

    void unfavorite(String targetType, Long targetId, Long userId);

    boolean isFavorite(String targetType, Long targetId, Long userId);

    Long getFavoriteCount(String targetType, Long targetId);

    Map<String, Object> getFavoriteStatus(String targetType, Long targetId, Long userId);

    Page<ProductVO> getUserFavorites(Long userId, Integer current, Integer size);
}
