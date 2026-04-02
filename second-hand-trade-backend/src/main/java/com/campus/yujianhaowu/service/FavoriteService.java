package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.yujianhaowu.model.dto.favorite.FavoriteRequest;
import com.campus.yujianhaowu.model.vo.CulturalContentVO;
import com.campus.yujianhaowu.model.entity.Favorite;
import com.campus.yujianhaowu.model.vo.FavoriteProductVO;
import com.campus.yujianhaowu.model.vo.FavoriteStatusVO;
import com.campus.yujianhaowu.model.vo.ProductVO;

import java.util.List;

/**
 * 收藏服务
 */
public interface FavoriteService extends IService<Favorite> {

    void addFavorite(Long userId, FavoriteRequest request);

    void removeFavorite(Long userId, String targetType, Long targetId);

    List<FavoriteProductVO> getUserProductFavorites(Long userId);

    Page<ProductVO> getUserFavoriteProducts(Long userId, Long current, Long size);

    Page<CulturalContentVO> getUserFavoriteContents(Long userId, Long current, Long size);

    boolean isFavorite(Long userId, String targetType, Long targetId);

    FavoriteStatusVO getFavoriteStatus(Long userId, String targetType, Long targetId);
}
