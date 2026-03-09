package com.campus.yujianhaowu.service;

import com.campus.yujianhaowu.model.entity.CulturalTag;
import com.campus.yujianhaowu.model.vo.CulturalTagVO;

import java.util.List;

/**
 * 文化标签服务接口
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
public interface CulturalTagService {

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    List<CulturalTagVO> listTags();

    /**
     * 获取热门标签
     *
     * @param limit 数量限制
     * @return 标签列表
     */
    List<CulturalTagVO> listHotTags(Integer limit);

    /**
     * 根据分类获取标签
     *
     * @param category 分类
     * @return 标签列表
     */
    List<CulturalTagVO> listTagsByCategory(String category);

    /**
     * 根据 ID 获取标签
     *
     * @param id 标签 ID
     * @return 标签
     */
    CulturalTagVO getTagById(Long id);

    /**
     * 创建标签
     *
     * @param tag 标签信息
     * @return 标签 ID
     */
    Long createTag(CulturalTag tag);

    /**
     * 更新标签
     *
     * @param id  标签 ID
     * @param tag 标签信息
     */
    void updateTag(Long id, CulturalTag tag);

    /**
     * 删除标签
     *
     * @param id 标签 ID
     */
    void deleteTag(Long id);

    /**
     * 设置热门标签
     *
     * @param id    标签 ID
     * @param isHot 是否热门
     */
    void setHotTag(Long id, Boolean isHot);
}
