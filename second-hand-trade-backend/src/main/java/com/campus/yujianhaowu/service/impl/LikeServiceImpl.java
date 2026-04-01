package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.ContentCommentMapper;
import com.campus.yujianhaowu.mapper.CulturalContentMapper;
import com.campus.yujianhaowu.mapper.LikeMapper;
import com.campus.yujianhaowu.model.entity.ContentComment;
import com.campus.yujianhaowu.model.entity.CulturalContent;
import com.campus.yujianhaowu.model.entity.Like;
import com.campus.yujianhaowu.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final CulturalContentMapper culturalContentMapper;
    private final ContentCommentMapper contentCommentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(String targetType, Long targetId, Long userId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, targetType)
                .eq(Like::getTargetId, targetId);

        Like existingLike = likeMapper.selectOne(wrapper);
        if (existingLike != null) {
            throw new BusinessException("已经点赞过了");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType(targetType);
        like.setTargetId(targetId);
        like.setCreatedAt(LocalDateTime.now());

        likeMapper.insert(like);
        increaseTargetLikeCount(targetType, targetId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlike(String targetType, Long targetId, Long userId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, targetType)
                .eq(Like::getTargetId, targetId);

        Like like = likeMapper.selectOne(wrapper);
        if (like != null) {
            likeMapper.delete(wrapper);
            decreaseTargetLikeCount(targetType, targetId);
        }
    }

    @Override
    public boolean isLiked(String targetType, Long targetId, Long userId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, targetType)
                .eq(Like::getTargetId, targetId);

        Like like = likeMapper.selectOne(wrapper);
        return like != null;
    }

    @Override
    public Long getLikeCount(String targetType, Long targetId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getTargetType, targetType)
                .eq(Like::getTargetId, targetId);

        return likeMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> getLikeStatus(String targetType, Long targetId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("count", getLikeCount(targetType, targetId));
        result.put("liked", userId != null && isLiked(targetType, targetId, userId));
        return result;
    }

    private void increaseTargetLikeCount(String targetType, Long targetId) {
        if ("content".equals(targetType)) {
            CulturalContent content = culturalContentMapper.selectById(targetId);
            if (content != null) {
                int likeCount = content.getLikeCount() != null ? content.getLikeCount() : 0;
                content.setLikeCount(likeCount + 1);
                culturalContentMapper.updateById(content);
            }
            return;
        }

        if ("comment".equals(targetType)) {
            ContentComment comment = contentCommentMapper.selectById(targetId);
            if (comment != null) {
                int likeCount = comment.getLikeCount() != null ? comment.getLikeCount() : 0;
                comment.setLikeCount(likeCount + 1);
                contentCommentMapper.updateById(comment);
            }
        }
    }

    private void decreaseTargetLikeCount(String targetType, Long targetId) {
        if ("content".equals(targetType)) {
            CulturalContent content = culturalContentMapper.selectById(targetId);
            if (content != null) {
                int likeCount = content.getLikeCount() != null ? content.getLikeCount() : 0;
                if (likeCount > 0) {
                    content.setLikeCount(likeCount - 1);
                    culturalContentMapper.updateById(content);
                }
            }
            return;
        }

        if ("comment".equals(targetType)) {
            ContentComment comment = contentCommentMapper.selectById(targetId);
            if (comment != null) {
                int likeCount = comment.getLikeCount() != null ? comment.getLikeCount() : 0;
                if (likeCount > 0) {
                    comment.setLikeCount(likeCount - 1);
                    contentCommentMapper.updateById(comment);
                }
            }
        }
    }
}
