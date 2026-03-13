package com.campus.yujianhaowu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.vo.CulturalContentVO;
import com.campus.yujianhaowu.service.CulturalContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文化资讯 Controller
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@RestController
@RequestMapping("/cultural-contents")
@RequiredArgsConstructor
@Tag(name = "文化资讯管理", description = "文化资讯相关接口")
public class CulturalContentController {

    private final CulturalContentService culturalContentService;

    @GetMapping
    @Operation(summary = "分页查询文化资讯")
    public Result<Page<CulturalContentVO>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String category) {
        Page<CulturalContentVO> page = culturalContentService.page(current, size, category);
        return Result.success(page);
    }

    @GetMapping("/recommend")
    @Operation(summary = "获取推荐的文化资讯")
    public Result<Page<CulturalContentVO>> getRecommendContents(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "4") Long size) {
        Page<CulturalContentVO> page = culturalContentService.getRecommendContents(current, size);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文化资讯详情")
    public Result<CulturalContentVO> getById(@PathVariable Long id) {
        CulturalContentVO vo = culturalContentService.getById(id);
        // 增加浏览量
        culturalContentService.increaseViewCount(id);
        return Result.success(vo);
    }
}
