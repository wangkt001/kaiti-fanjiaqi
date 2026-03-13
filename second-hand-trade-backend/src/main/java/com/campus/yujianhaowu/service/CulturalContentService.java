package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.model.entity.CulturalContent;
import com.campus.yujianhaowu.model.vo.CulturalContentVO;

/**
 * 文化资讯 Service 接口
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
public interface CulturalContentService {

    /**
     * 分页查询文化资讯
     *
     * @param current  当前页
     * @param size     每页大小
     * @param category 分类（可选）
     * @param keyword  搜索关键词（可选）
     * @return 分页数据
     */
    Page<CulturalContentVO> page(Long current, Long size, String category, String keyword);

    /**
     * 获取推荐的文化资讯
     *
     * @param limit 数量限制
     * @return 推荐资讯列表
     */
    Page<CulturalContentVO> getRecommendContents(Long current, Long size);

    /**
     * 根据 ID 获取文化资讯
     *
     * @param id 资讯 ID
     * @return 资讯详情
     */
    CulturalContentVO getById(Long id);

    /**
     * 增加浏览量
     *
     * @param id 资讯 ID
     */
    void increaseViewCount(Long id);

    /**
     * 初始化文化资讯数据
     */
    void initializeData();
}
