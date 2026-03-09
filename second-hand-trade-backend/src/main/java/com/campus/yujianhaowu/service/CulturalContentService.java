package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.model.dto.CulturalContentCreateRequest;
import com.campus.yujianhaowu.model.vo.CulturalContentVO;

public interface CulturalContentService {

    Page<CulturalContentVO> getContents(String category, String keyword, Integer current, Integer size);

    CulturalContentVO getContentById(Long id);

    CulturalContentVO createContent(CulturalContentCreateRequest request, Long authorId);

    void updateContent(Long id, CulturalContentCreateRequest request);

    void deleteContent(Long id);

    void incrementViewCount(Long id);

    Page<CulturalContentVO> getRecommendContents(Integer current, Integer size);
}
