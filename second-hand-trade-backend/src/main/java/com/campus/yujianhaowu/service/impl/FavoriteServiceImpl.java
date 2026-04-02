package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.yujianhaowu.mapper.CulturalContentMapper;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.FavoriteMapper;
import com.campus.yujianhaowu.mapper.ProductMapper;
import com.campus.yujianhaowu.model.entity.CulturalContent;
import com.campus.yujianhaowu.model.dto.favorite.FavoriteRequest;
import com.campus.yujianhaowu.model.entity.Favorite;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.vo.CulturalContentVO;
import com.campus.yujianhaowu.model.vo.FavoriteProductVO;
import com.campus.yujianhaowu.model.vo.FavoriteStatusVO;
import com.campus.yujianhaowu.model.vo.ProductVO;
import com.campus.yujianhaowu.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 收藏服务实现
 */
@Slf4j
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CulturalContentMapper culturalContentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long userId, FavoriteRequest request) {
        log.info("添加收藏 - userId: {}, targetType: {}, targetId: {}", userId, request.getTargetType(),
                request.getTargetId());

        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, request.getTargetType())
                .eq(Favorite::getTargetId, request.getTargetId());

        Favorite existing = this.getOne(wrapper);
        if (existing != null) {
            log.info("收藏记录已存在，直接返回 - userId: {}, targetType: {}, targetId: {}", userId, request.getTargetType(),
                    request.getTargetId());
            return;
        }

        // 创建收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(request.getTargetType());
        favorite.setTargetId(request.getTargetId());
        try {
            this.save(favorite);
        } catch (DuplicateKeyException e) {
            log.warn("收藏记录重复插入，按已收藏处理 - userId: {}, targetType: {}, targetId: {}", userId,
                    request.getTargetType(), request.getTargetId());
            return;
        }

        // 更新收藏数量
        if ("product".equals(request.getTargetType())) {
            Product product = productMapper.selectById(request.getTargetId());
            if (product != null) {
                product.setFavoriteCount(product.getFavoriteCount() + 1);
                productMapper.updateById(product);
                log.info("商品收藏数 +1 - productId: {}, count: {}", product.getId(), product.getFavoriteCount());
            }
        } else if ("content".equals(request.getTargetType())) {
            CulturalContent content = culturalContentMapper.selectById(request.getTargetId());
            if (content != null) {
                int favoriteCount = content.getFavoriteCount() != null ? content.getFavoriteCount() : 0;
                content.setFavoriteCount(favoriteCount + 1);
                culturalContentMapper.updateById(content);
                log.info("资讯收藏数 +1 - contentId: {}, count: {}", content.getId(), content.getFavoriteCount());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long userId, String targetType, Long targetId) {
        log.info("取消收藏 - userId: {}, targetType: {}, targetId: {}", userId, targetType, targetId);

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId);

        Favorite favorite = this.getOne(wrapper);
        if (favorite != null) {
            this.remove(wrapper);

            // 更新收藏数量
            if ("product".equals(favorite.getTargetType())) {
                Product product = productMapper.selectById(targetId);
                if (product != null && product.getFavoriteCount() > 0) {
                    product.setFavoriteCount(product.getFavoriteCount() - 1);
                    productMapper.updateById(product);
                    log.info("商品收藏数 -1 - productId: {}, count: {}", product.getId(), product.getFavoriteCount());
                }
            } else if ("content".equals(favorite.getTargetType())) {
                CulturalContent content = culturalContentMapper.selectById(targetId);
                if (content != null) {
                    int favoriteCount = content.getFavoriteCount() != null ? content.getFavoriteCount() : 0;
                    if (favoriteCount > 0) {
                        content.setFavoriteCount(favoriteCount - 1);
                        culturalContentMapper.updateById(content);
                        log.info("资讯收藏数 -1 - contentId: {}, count: {}", content.getId(), content.getFavoriteCount());
                    }
                }
            }
        }
    }

    @Override
    public List<FavoriteProductVO> getUserProductFavorites(Long userId) {
        List<Favorite> favorites = baseMapper.selectUserProductFavorites(userId);
        List<FavoriteProductVO> result = new ArrayList<>();

        for (Favorite favorite : favorites) {
            Product product = productMapper.selectById(favorite.getTargetId());
            if (product != null) {
                FavoriteProductVO vo = new FavoriteProductVO();
                vo.setFavoriteId(favorite.getId());
                vo.setProductId(product.getId());
                vo.setProductName(product.getName());
                vo.setProductImage(product.getImageUrl());
                vo.setPrice(product.getPrice().doubleValue());
                vo.setSalesCount(product.getSalesCount());
                vo.setStatus(product.getStatus());
                vo.setFavoriteTime(favorite.getCreatedAt());
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public Page<ProductVO> getUserFavoriteProducts(Long userId, Long current, Long size) {
        log.info("获取用户收藏商品分页 - userId: {}, current: {}, size: {}", userId, current, size);

        // 先获取用户的收藏记录
        List<Favorite> favorites = baseMapper.selectUserProductFavorites(userId);

        // 获取商品 ID 列表
        Set<Long> productIds = favorites.stream()
                .map(Favorite::getTargetId)
                .collect(Collectors.toSet());

        // 构建分页对象
        Page<ProductVO> resultPage = new Page<>(current, size);

        if (productIds.isEmpty()) {
            resultPage.setRecords(new ArrayList<>());
            resultPage.setTotal(0);
            return resultPage;
        }

        // 查询商品列表
        List<Product> products = productMapper.selectBatchIds(productIds);

        // 转换为 VO 并按照收藏时间排序
        List<ProductVO> voList = products.stream()
                .map(this::convertToProductVO)
                .sorted((vo1, vo2) -> {
                    // 按照收藏时间倒序排列
                    Favorite f1 = favorites.stream()
                            .filter(f -> f.getTargetId().equals(vo1.getId()))
                            .findFirst()
                            .orElse(null);
                    Favorite f2 = favorites.stream()
                            .filter(f -> f.getTargetId().equals(vo2.getId()))
                            .findFirst()
                            .orElse(null);
                    if (f1 == null || f2 == null)
                        return 0;
                    return f2.getCreatedAt().compareTo(f1.getCreatedAt());
                })
                .toList();

        // 分页处理
        int total = voList.size();
        int fromIndex = (int) ((current - 1) * size);
        int toIndex = Math.min(fromIndex + (int) (long) size, total);

        List<ProductVO> pageRecords = fromIndex < total ? voList.subList(fromIndex, toIndex) : new ArrayList<>();

        resultPage.setRecords(pageRecords);
        resultPage.setTotal(total);

        log.info("返回收藏商品 - total: {}, currentPageRecords: {}", total, pageRecords.size());
        return resultPage;
    }

    @Override
    public Page<CulturalContentVO> getUserFavoriteContents(Long userId, Long current, Long size) {
        log.info("获取用户收藏资讯分页 - userId: {}, current: {}, size: {}", userId, current, size);

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, "content")
                .orderByDesc(Favorite::getCreatedAt);
        List<Favorite> favorites = this.list(wrapper);

        Page<CulturalContentVO> resultPage = new Page<>(current, size);
        if (favorites.isEmpty()) {
            resultPage.setRecords(new ArrayList<>());
            resultPage.setTotal(0);
            return resultPage;
        }

        Set<Long> contentIds = favorites.stream()
                .map(Favorite::getTargetId)
                .collect(Collectors.toSet());
        List<CulturalContent> contents = culturalContentMapper.selectBatchIds(contentIds);

        List<CulturalContentVO> voList = contents.stream()
                .map(this::convertToContentVO)
                .sorted((vo1, vo2) -> {
                    Favorite f1 = favorites.stream()
                            .filter(f -> f.getTargetId().equals(vo1.getId()))
                            .findFirst()
                            .orElse(null);
                    Favorite f2 = favorites.stream()
                            .filter(f -> f.getTargetId().equals(vo2.getId()))
                            .findFirst()
                            .orElse(null);
                    if (f1 == null || f2 == null) {
                        return 0;
                    }
                    return f2.getCreatedAt().compareTo(f1.getCreatedAt());
                })
                .toList();

        int total = voList.size();
        int fromIndex = (int) ((current - 1) * size);
        int toIndex = Math.min(fromIndex + (int) (long) size, total);
        List<CulturalContentVO> pageRecords = fromIndex < total ? voList.subList(fromIndex, toIndex)
                : new ArrayList<>();

        resultPage.setRecords(pageRecords);
        resultPage.setTotal(total);
        return resultPage;
    }

    @Override
    public boolean isFavorite(Long userId, String targetType, Long targetId) {
        if (userId == null || targetId == null) {
            return false;
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId);
        return this.count(wrapper) > 0;
    }

    @Override
    public FavoriteStatusVO getFavoriteStatus(Long userId, String targetType, Long targetId) {
        FavoriteStatusVO vo = new FavoriteStatusVO();

        // 检查是否已收藏
        boolean favorited = isFavorite(userId, targetType, targetId);
        vo.setFavorited(favorited);

        // 获取目标的收藏总数
        if ("product".equals(targetType)) {
            Product product = productMapper.selectById(targetId);
            if (product != null) {
                vo.setCount(product.getFavoriteCount() != null ? product.getFavoriteCount() : 0);
            } else {
                vo.setCount(0);
            }
        } else if ("content".equals(targetType)) {
            CulturalContent content = culturalContentMapper.selectById(targetId);
            if (content != null) {
                vo.setCount(content.getFavoriteCount() != null ? content.getFavoriteCount() : 0);
            } else {
                vo.setCount(0);
            }
        } else {
            vo.setCount(0);
        }

        return vo;
    }

    private ProductVO convertToProductVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        return vo;
    }

    private CulturalContentVO convertToContentVO(CulturalContent content) {
        CulturalContentVO vo = new CulturalContentVO();
        BeanUtils.copyProperties(content, vo);
        return vo;
    }
}
