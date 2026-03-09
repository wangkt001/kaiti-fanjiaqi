package com.campus.yujianhaowu.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.CulturalContentMapper;
import com.campus.yujianhaowu.mapper.UserMapper;
import com.campus.yujianhaowu.model.dto.CulturalContentCreateRequest;
import com.campus.yujianhaowu.model.entity.CulturalContent;
import com.campus.yujianhaowu.model.entity.User;
import com.campus.yujianhaowu.model.vo.CulturalContentVO;
import com.campus.yujianhaowu.service.CulturalContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CulturalContentServiceImpl implements CulturalContentService {

    private final CulturalContentMapper culturalContentMapper;
    private final UserMapper userMapper;

    @Override
    public Page<CulturalContentVO> getContents(String category, String keyword, Integer current, Integer size) {
        Page<CulturalContent> page = new Page<>(current, size);
        LambdaQueryWrapper<CulturalContent> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(category)) {
            wrapper.eq(CulturalContent::getCategory, category);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(CulturalContent::getTitle, keyword)
                    .or()
                    .like(CulturalContent::getSummary, keyword));
        }

        wrapper.eq(CulturalContent::getIsPublished, true)
                .orderByDesc(CulturalContent::getIsTop)
                .orderByDesc(CulturalContent::getPublishedAt);

        Page<CulturalContent> contentPage = culturalContentMapper.selectPage(page, wrapper);

        Page<CulturalContentVO> voPage = new Page<>(current, size, contentPage.getTotal());
        List<CulturalContentVO> voList = contentPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public CulturalContentVO getContentById(Long id) {
        CulturalContent content = culturalContentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException("资讯不存在");
        }
        return convertToVO(content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CulturalContentVO createContent(CulturalContentCreateRequest request, Long authorId) {
        CulturalContent content = new CulturalContent();
        content.setTitle(request.getTitle());
        content.setSummary(request.getSummary());
        content.setContent(request.getContent());
        content.setCoverImage(request.getCoverImage());
        content.setCategory(request.getCategory());
        content.setTags(request.getTags());
        content.setAuthorId(authorId);
        content.setViewCount(0);
        content.setLikeCount(0);
        content.setCommentCount(0);
        content.setFavoriteCount(0);
        content.setIsTop(false);
        content.setIsRecommend(false);
        content.setIsPublished(request.getIsPublished() != null ? request.getIsPublished() : false);

        if (request.getIsPublished()) {
            content.setPublishedAt(LocalDateTime.now());
        }

        culturalContentMapper.insert(content);
        return convertToVO(content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContent(Long id, CulturalContentCreateRequest request) {
        CulturalContent content = culturalContentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException("资讯不存在");
        }

        content.setTitle(request.getTitle());
        content.setSummary(request.getSummary());
        content.setContent(request.getContent());
        content.setCoverImage(request.getCoverImage());
        content.setCategory(request.getCategory());
        content.setTags(request.getTags());

        if (request.getIsPublished() != null && !request.getIsPublished() && content.getIsPublished()) {
            content.setIsPublished(false);
        } else if (request.getIsPublished() != null && request.getIsPublished() && !content.getIsPublished()) {
            content.setIsPublished(true);
            content.setPublishedAt(LocalDateTime.now());
        }

        culturalContentMapper.updateById(content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContent(Long id) {
        CulturalContent content = culturalContentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException("资讯不存在");
        }
        culturalContentMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        CulturalContent content = culturalContentMapper.selectById(id);
        if (content != null) {
            content.setViewCount(content.getViewCount() + 1);
            culturalContentMapper.updateById(content);
        }
    }

    @Override
    public Page<CulturalContentVO> getRecommendContents(Integer current, Integer size) {
        Page<CulturalContent> page = new Page<>(current, size);
        LambdaQueryWrapper<CulturalContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CulturalContent::getIsPublished, true)
                .orderByDesc(CulturalContent::getIsRecommend)
                .orderByDesc(CulturalContent::getViewCount);

        Page<CulturalContent> contentPage = culturalContentMapper.selectPage(page, wrapper);
        Page<CulturalContentVO> voPage = new Page<>(current, size, contentPage.getTotal());
        List<CulturalContentVO> voList = contentPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    private CulturalContentVO convertToVO(CulturalContent content) {
        CulturalContentVO vo = new CulturalContentVO();
        vo.setId(content.getId());
        vo.setTitle(content.getTitle());
        vo.setSummary(content.getSummary());
        vo.setContent(content.getContent());
        vo.setCoverImage(content.getCoverImage());
        vo.setCategory(content.getCategory());

        if (content.getTags() != null) {
            vo.setTags(JSONUtil.toList(content.getTags(), String.class));
        }

        vo.setAuthorId(content.getAuthorId());
        vo.setViewCount(content.getViewCount());
        vo.setLikeCount(content.getLikeCount());
        vo.setCommentCount(content.getCommentCount());
        vo.setFavoriteCount(content.getFavoriteCount());
        vo.setIsTop(content.getIsTop());
        vo.setIsRecommend(content.getIsRecommend());
        vo.setIsPublished(content.getIsPublished());
        vo.setPublishedAt(content.getPublishedAt());
        vo.setCreatedAt(content.getCreatedAt());
        vo.setUpdatedAt(content.getUpdatedAt());

        if (content.getAuthorId() != null) {
            User author = userMapper.selectById(content.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getNickname());
            }
        }

        return vo;
    }
}
