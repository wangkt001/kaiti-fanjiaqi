package com.campus.yujianhaowu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.PageResult;
import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.dto.CulturalContentCreateRequest;
import com.campus.yujianhaowu.model.vo.CulturalContentVO;
import com.campus.yujianhaowu.service.CulturalContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/cultural-contents")
@RequiredArgsConstructor
@Tag(name = "文化资讯管理", description = "文化资讯相关接口")
public class CulturalContentController {

    private final CulturalContentService culturalContentService;

    @GetMapping
    @Operation(summary = "获取文化资讯列表")
    public Result<PageResult<CulturalContentVO>> getContents(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        Page<CulturalContentVO> page = culturalContentService.getContents(category, keyword, current, size);
        PageResult<CulturalContentVO> result = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    @GetMapping("/recommend")
    @Operation(summary = "获取推荐资讯")
    public Result<PageResult<CulturalContentVO>> getRecommendContents(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        Page<CulturalContentVO> page = culturalContentService.getRecommendContents(current, size);
        PageResult<CulturalContentVO> result = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取资讯详情")
    public Result<CulturalContentVO> getContentDetail(
            @Parameter(description = "资讯 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        // 增加浏览量
        culturalContentService.incrementViewCount(id);
        CulturalContentVO content = culturalContentService.getContentById(id);
        return Result.success(content);
    }

    @PostMapping
    @Operation(summary = "创建资讯")
    public Result<CulturalContentVO> createContent(
            @RequestBody @Validated CulturalContentCreateRequest request,
            HttpServletRequest httpRequest) {
        Long authorId = (Long) httpRequest.getAttribute("userId");
        CulturalContentVO content = culturalContentService.createContent(request, authorId);
        return Result.success(content);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新资讯")
    public Result<Void> updateContent(
            @Parameter(description = "资讯 ID") @PathVariable Long id,
            @RequestBody @Validated CulturalContentCreateRequest request) {
        culturalContentService.updateContent(id, request);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除资讯")
    public Result<Void> deleteContent(
            @Parameter(description = "资讯 ID") @PathVariable Long id) {
        culturalContentService.deleteContent(id);
        return Result.success(null);
    }
}
