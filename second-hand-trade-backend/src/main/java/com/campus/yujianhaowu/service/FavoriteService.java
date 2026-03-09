package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.yujianhaowu.model.dto.favorite.FavoriteRequest;
import com.campus.yujianhaowu.model.entity.Favorite;
import com.campus.yujianhaowu.model.vo.FavoriteProductVO;

import java.util.List;

/**
 * 收藏服务
 */
public interface FavoriteService extends IService<Favorite> {

    void addFavorite(Long userId, FavoriteRequest request);

    void removeFavorite(Long userId, Long targetId);

    List<FavoriteProductVO> getUserProductFavorites(Long userId);

    boolean isFavorite(Long userId, Long targetId);
}
