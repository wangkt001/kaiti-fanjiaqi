package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.FavoriteMapper;
import com.campus.yujianhaowu.mapper.ProductMapper;
import com.campus.yujianhaowu.model.entity.Favorite;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.vo.ProductVO;
import com.campus.yujianhaowu.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void favorite(String targetType, Long targetId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId);

        Favorite existingFavorite = favoriteMapper.selectOne(wrapper);
        if (existingFavorite != null) {
            throw new BusinessException("已经收藏过了");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(targetType);
        favorite.setTargetId(targetId);
        favorite.setCreatedAt(LocalDateTime.now());

        favoriteMapper.insert(favorite);

        if ("product".equals(targetType)) {
            Product product = productMapper.selectById(targetId);
            if (product != null) {
                product.setFavoriteCount(product.getFavoriteCount() + 1);
                productMapper.updateById(product);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfavorite(String targetType, Long targetId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId);

        Favorite favorite = favoriteMapper.selectOne(wrapper);
        if (favorite != null) {
            favoriteMapper.delete(wrapper);

            if ("product".equals(targetId)) {
                Product product = productMapper.selectById(targetId);
                if (product != null && product.getFavoriteCount() > 0) {
                    product.setFavoriteCount(product.getFavoriteCount() - 1);
                    productMapper.updateById(product);
                }
            }
        }
    }

    @Override
    public boolean isFavorite(String targetType, Long targetId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId);

        Favorite favorite = favoriteMapper.selectOne(wrapper);
        return favorite != null;
    }

    @Override
    public Long getFavoriteCount(String targetType, Long targetId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId);

        return favoriteMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> getFavoriteStatus(String targetType, Long targetId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("count", getFavoriteCount(targetType, targetId));
        result.put("favorited", userId != null && isFavorite(targetType, targetId, userId));
        return result;
    }

    @Override
    public Page<ProductVO> getUserFavorites(Long userId, Integer current, Integer size) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, "product")
                .orderByDesc(Favorite::getCreatedAt);

        Page<Favorite> favoritePage = favoriteMapper.selectPage(new Page<>(current, size), wrapper);

        List<Long> productIds = favoritePage.getRecords().stream()
                .map(Favorite::getTargetId)
                .collect(Collectors.toList());

        Page<ProductVO> voPage = new Page<>(current, size, favoritePage.getTotal());

        if (productIds.isEmpty()) {
            voPage.setRecords(List.of());
            return voPage;
        }

        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.in(Product::getId, productIds)
                .eq(Product::getStatus, "on_sale");

        List<Product> products = productMapper.selectList(productWrapper);
        List<ProductVO> voList = products.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setStock(product.getStock());
        vo.setCategoryId(product.getCategoryId());
        vo.setSellerId(product.getSellerId());
        vo.setStatus(product.getStatus());
        vo.setSalesCount(product.getSalesCount());
        vo.setViewCount(product.getViewCount());
        vo.setFavoriteCount(product.getFavoriteCount());
        vo.setCreatedAt(product.getCreatedAt());
        vo.setPublishedAt(product.getPublishedAt());
        return vo;
    }
}
