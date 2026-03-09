package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.interceptor.AuthInterceptor;
import com.campus.yujianhaowu.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "点赞管理", description = "点赞相关接口")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{targetType}/{targetId}")
    @Operation(summary = "点赞")
    public Result<Void> like(
            @Parameter(description = "目标类型（review/reply/product/content/comment）") @PathVariable String targetType,
            @Parameter(description = "目标 ID") @PathVariable Long targetId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        likeService.like(targetType, targetId, userId);
        return Result.success(null);
    }

    @DeleteMapping("/{targetType}/{targetId}")
    @Operation(summary = "取消点赞")
    public Result<Void> unlike(
            @Parameter(description = "目标类型") @PathVariable String targetType,
            @Parameter(description = "目标 ID") @PathVariable Long targetId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        likeService.unlike(targetType, targetId, userId);
        return Result.success(null);
    }

    @GetMapping("/{targetType}/{targetId}/status")
    @Operation(summary = "获取点赞状态")
    public Result<Map<String, Object>> getLikeStatus(
            @Parameter(description = "目标类型") @PathVariable String targetType,
            @Parameter(description = "目标 ID") @PathVariable Long targetId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        Map<String, Object> status = likeService.getLikeStatus(targetType, targetId, userId);
        return Result.success(status);
    }

    @GetMapping("/{targetType}/{targetId}/count")
    @Operation(summary = "获取点赞数量")
    public Result<Long> getLikeCount(
            @Parameter(description = "目标类型") @PathVariable String targetType,
            @Parameter(description = "目标 ID") @PathVariable Long targetId) {
        Long count = likeService.getLikeCount(targetType, targetId);
        return Result.success(count);
    }
}
