package com.campus.yujianhaowu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.PageResult;
import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.dto.ReviewCreateRequest;
import com.campus.yujianhaowu.model.dto.ReviewReplyRequest;
import com.campus.yujianhaowu.model.vo.ReviewPermissionVO;
import com.campus.yujianhaowu.model.vo.ReviewVO;
import com.campus.yujianhaowu.model.vo.ReviewReplyVO;
import com.campus.yujianhaowu.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "评价管理", description = "商品评价相关接口")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "获取商品评价列表")
    public Result<PageResult<ReviewVO>> getProductReviews(
            @Parameter(description = "商品 ID") @PathVariable Long productId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "评分筛选") @RequestParam(required = false) Integer rating) {
        Page<ReviewVO> page = reviewService.getProductReviews(productId, current, size, rating);
        PageResult<ReviewVO> result = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取评价详情")
    public Result<ReviewVO> getReviewDetail(
            @Parameter(description = "评价 ID") @PathVariable Long id) {
        ReviewVO review = reviewService.getReviewById(id);
        return Result.success(review);
    }

    @GetMapping("/user")
    @Operation(summary = "获取当前用户评价列表")
    public Result<PageResult<ReviewVO>> getUserReviews(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Page<ReviewVO> page = reviewService.getUserReviews(userId, current, size);
        PageResult<ReviewVO> result = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    @GetMapping("/product/{productId}/permission")
    @Operation(summary = "获取商品评价权限")
    public Result<ReviewPermissionVO> getReviewPermission(
            @PathVariable Long productId,
            @RequestParam(required = false) Long orderId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ReviewPermissionVO result = reviewService.getReviewPermission(productId, userId, orderId);
        return Result.success(result);
    }

    @GetMapping("/{id}/replies")
    @Operation(summary = "获取评价回复列表")
    public Result<List<ReviewReplyVO>> getReviewReplies(
            @Parameter(description = "评价 ID") @PathVariable Long id) {
        List<ReviewReplyVO> replies = reviewService.getReviewReplies(id);
        return Result.success(replies);
    }

    @PostMapping
    @Operation(summary = "创建评价")
    public Result<ReviewVO> createReview(
            @RequestBody @Validated ReviewCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ReviewVO review = reviewService.createReview(request, userId);
        return Result.success(review);
    }

    @PostMapping("/reply")
    @Operation(summary = "回复评价")
    public Result<ReviewReplyVO> replyReview(
            @RequestBody @Validated ReviewReplyRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ReviewReplyVO reply = reviewService.replyReview(request, userId);
        return Result.success(reply);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评价")
    public Result<Void> deleteReview(
            @Parameter(description = "评价 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        reviewService.deleteReview(id, userId);
        return Result.success(null);
    }

    @DeleteMapping("/reply/{id}")
    @Operation(summary = "删除回复")
    public Result<Void> deleteReply(
            @Parameter(description = "回复 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        reviewService.deleteReply(id, userId);
        return Result.success(null);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞评价")
    public Result<Void> likeReview(
            @Parameter(description = "评价 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        reviewService.likeReview(id, userId);
        return Result.success(null);
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞评价")
    public Result<Void> unlikeReview(
            @Parameter(description = "评价 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        reviewService.unlikeReview(id, userId);
        return Result.success(null);
    }

    @GetMapping("/{id}/liked")
    @Operation(summary = "检查是否已点赞")
    public Result<Boolean> isUserLikedReview(
            @Parameter(description = "评价 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        boolean liked = reviewService.isUserLikedReview(id, userId);
        return Result.success(liked);
    }
}
