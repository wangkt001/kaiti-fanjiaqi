package com.campus.yujianhaowu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.PageResult;
import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.dto.ContentCommentCreateRequest;
import com.campus.yujianhaowu.model.vo.ContentCommentVO;
import com.campus.yujianhaowu.service.ContentCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/content-comments")
@RequiredArgsConstructor
@Tag(name = "资讯评论管理", description = "文化资讯评论相关接口")
public class ContentCommentController {

    private final ContentCommentService contentCommentService;

    @GetMapping("/content/{contentId}")
    @Operation(summary = "获取资讯评论列表")
    public Result<PageResult<ContentCommentVO>> getContentComments(
            @Parameter(description = "资讯 ID") @PathVariable Long contentId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        Page<ContentCommentVO> page = contentCommentService.getContentComments(contentId, current, size);
        PageResult<ContentCommentVO> result = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    @PostMapping
    @Operation(summary = "创建评论")
    public Result<ContentCommentVO> createComment(
            @RequestBody @Validated ContentCommentCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ContentCommentVO comment = contentCommentService.createComment(request, userId);
        return Result.success(comment);
    }

    @GetMapping("/{id}/replies")
    @Operation(summary = "获取评论回复列表")
    public Result<List<ContentCommentVO>> getCommentReplies(
            @Parameter(description = "评论 ID") @PathVariable Long id) {
        List<ContentCommentVO> replies = contentCommentService.getCommentReplies(id);
        return Result.success(replies);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteComment(
            @Parameter(description = "评论 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        contentCommentService.deleteComment(id, userId);
        return Result.success(null);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞评论")
    public Result<Void> likeComment(
            @Parameter(description = "评论 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        contentCommentService.likeComment(id, userId);
        return Result.success(null);
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞评论")
    public Result<Void> unlikeComment(
            @Parameter(description = "评论 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        contentCommentService.unlikeComment(id, userId);
        return Result.success(null);
    }
}
