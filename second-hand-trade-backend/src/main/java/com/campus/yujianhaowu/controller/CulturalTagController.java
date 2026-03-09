package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.entity.CulturalTag;
import com.campus.yujianhaowu.model.vo.CulturalTagVO;
import com.campus.yujianhaowu.service.CulturalTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文化标签控制器
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Tag(name = "文化标签管理", description = "文化标签 CRUD、查询等接口")
public class CulturalTagController {

    private final CulturalTagService culturalTagService;

    @GetMapping
    @Operation(summary = "获取标签列表")
    public Result<List<CulturalTagVO>> listTags(
            @RequestParam(required = false) String category) {

        if (category != null) {
            return Result.success(culturalTagService.listTagsByCategory(category));
        }
        return Result.success(culturalTagService.listTags());
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门标签")
    public Result<List<CulturalTagVO>> listHotTags(
            @RequestParam(required = false) Integer limit) {
        return Result.success(culturalTagService.listHotTags(limit));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取标签详情")
    public Result<CulturalTagVO> getTagDetail(@PathVariable Long id) {
        return Result.success(culturalTagService.getTagById(id));
    }

    @PostMapping
    @Operation(summary = "创建标签")
    public Result<Long> createTag(@RequestBody CulturalTag tag) {
        return Result.success(culturalTagService.createTag(tag));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新标签")
    public Result<Void> updateTag(
            @PathVariable Long id,
            @RequestBody CulturalTag tag) {
        culturalTagService.updateTag(id, tag);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签")
    public Result<Void> deleteTag(@PathVariable Long id) {
        culturalTagService.deleteTag(id);
        return Result.success();
    }

    @PutMapping("/{id}/hot")
    @Operation(summary = "设置热门标签")
    public Result<Void> setHotTag(
            @PathVariable Long id,
            @RequestParam Boolean isHot) {
        culturalTagService.setHotTag(id, isHot);
        return Result.success();
    }
}
