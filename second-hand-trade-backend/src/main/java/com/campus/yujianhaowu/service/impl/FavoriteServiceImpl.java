package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.FavoriteMapper;
import com.campus.yujianhaowu.mapper.ProductMapper;
import com.campus.yujianhaowu.model.dto.favorite.FavoriteRequest;
import com.campus.yujianhaowu.model.entity.Favorite;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.vo.FavoriteProductVO;
import com.campus.yujianhaowu.service.FavoriteService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏服务实现
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Resource
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long userId, FavoriteRequest request) {
        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, request.getTargetType())
                .eq(Favorite::getTargetId, request.getTargetId());

        Favorite existing = this.getOne(wrapper);
        if (existing != null) {
            throw new BusinessException("已经收藏过了");
        }

        // 创建收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(request.getTargetType());
        favorite.setTargetId(request.getTargetId());
        this.save(favorite);

        // 更新商品收藏数量
        if ("product".equals(request.getTargetType())) {
            Product product = productMapper.selectById(request.getTargetId());
            if (product != null) {
                product.setFavoriteCount(product.getFavoriteCount() + 1);
                productMapper.updateById(product);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long userId, Long targetId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetId, targetId);

        Favorite favorite = this.getOne(wrapper);
        if (favorite != null) {
            this.remove(wrapper);

            // 更新商品收藏数量
            if ("product".equals(favorite.getTargetType())) {
                Product product = productMapper.selectById(targetId);
                if (product != null && product.getFavoriteCount() > 0) {
                    product.setFavoriteCount(product.getFavoriteCount() - 1);
                    productMapper.updateById(product);
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
                vo.setProductImage(null); // 简化：暂不处理商品图片
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
    public boolean isFavorite(Long userId, Long targetId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetId, targetId);
        return this.count(wrapper) > 0;
    }
}
