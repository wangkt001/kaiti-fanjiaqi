package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.yujianhaowu.mapper.CulturalTagMapper;
import com.campus.yujianhaowu.model.entity.CulturalTag;
import com.campus.yujianhaowu.model.vo.CulturalTagVO;
import com.campus.yujianhaowu.service.CulturalTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文化标签服务实现类
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CulturalTagServiceImpl implements CulturalTagService {

    private final CulturalTagMapper culturalTagMapper;

    @Override
    public List<CulturalTagVO> listTags() {
        LambdaQueryWrapper<CulturalTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CulturalTag::getIsActive, true)
                .orderByAsc(CulturalTag::getSortOrder);

        List<CulturalTag> tags = culturalTagMapper.selectList(wrapper);
        return tags.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<CulturalTagVO> listHotTags(Integer limit) {
        LambdaQueryWrapper<CulturalTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CulturalTag::getIsActive, true)
                .eq(CulturalTag::getIsHot, true)
                .orderByAsc(CulturalTag::getSortOrder)
                .last("LIMIT " + (limit != null ? limit : 10));

        List<CulturalTag> tags = culturalTagMapper.selectList(wrapper);
        return tags.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<CulturalTagVO> listTagsByCategory(String category) {
        LambdaQueryWrapper<CulturalTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CulturalTag::getIsActive, true)
                .eq(CulturalTag::getCategory, category)
                .orderByAsc(CulturalTag::getSortOrder);

        List<CulturalTag> tags = culturalTagMapper.selectList(wrapper);
        return tags.stream().map(this::convertToVO).toList();
    }

    @Override
    public CulturalTagVO getTagById(Long id) {
        CulturalTag tag = culturalTagMapper.selectById(id);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }
        return convertToVO(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTag(CulturalTag tag) {
        culturalTagMapper.insert(tag);
        log.info("标签创建成功，tagId: {}", tag.getId());
        return tag.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTag(Long id, CulturalTag tag) {
        CulturalTag existTag = culturalTagMapper.selectById(id);
        if (existTag == null) {
            throw new RuntimeException("标签不存在");
        }

        if (tag.getName() != null) {
            existTag.setName(tag.getName());
        }
        if (tag.getDescription() != null) {
            existTag.setDescription(tag.getDescription());
        }
        if (tag.getCategory() != null) {
            existTag.setCategory(tag.getCategory());
        }
        if (tag.getIconUrl() != null) {
            existTag.setIconUrl(tag.getIconUrl());
        }
        if (tag.getSortOrder() != null) {
            existTag.setSortOrder(tag.getSortOrder());
        }
        if (tag.getIsHot() != null) {
            existTag.setIsHot(tag.getIsHot());
        }
        if (tag.getIsActive() != null) {
            existTag.setIsActive(tag.getIsActive());
        }

        culturalTagMapper.updateById(existTag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        culturalTagMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setHotTag(Long id, Boolean isHot) {
        CulturalTag tag = culturalTagMapper.selectById(id);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }

        tag.setIsHot(isHot);
        culturalTagMapper.updateById(tag);
    }

    /**
     * 转换为 VO
     */
    private CulturalTagVO convertToVO(CulturalTag tag) {
        CulturalTagVO vo = new CulturalTagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setDescription(tag.getDescription());
        vo.setCategory(tag.getCategory());
        vo.setIconUrl(tag.getIconUrl());
        vo.setSortOrder(tag.getSortOrder());
        vo.setIsHot(tag.getIsHot());
        vo.setIsActive(tag.getIsActive());
        return vo;
    }
}
