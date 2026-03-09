package com.campus.yujianhaowu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.PageResult;
import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.interceptor.AuthInterceptor;
import com.campus.yujianhaowu.model.vo.ProductVO;
import com.campus.yujianhaowu.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "收藏管理", description = "收藏相关接口")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{targetType}/{targetId}")
    @Operation(summary = "收藏")
    public Result<Void> favorite(
            @Parameter(description = "目标类型（product/content）") @PathVariable String targetType,
            @Parameter(description = "目标 ID") @PathVariable Long targetId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        favoriteService.favorite(targetType, targetId, userId);
        return Result.success(null);
    }

    @DeleteMapping("/{targetType}/{targetId}")
    @Operation(summary = "取消收藏")
    public Result<Void> unfavorite(
            @Parameter(description = "目标类型") @PathVariable String targetType,
            @Parameter(description = "目标 ID") @PathVariable Long targetId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        favoriteService.unfavorite(targetType, targetId, userId);
        return Result.success(null);
    }

    @GetMapping("/{targetType}/{targetId}/status")
    @Operation(summary = "获取收藏状态")
    public Result<Map<String, Object>> getFavoriteStatus(
            @Parameter(description = "目标类型") @PathVariable String targetType,
            @Parameter(description = "目标 ID") @PathVariable Long targetId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        Map<String, Object> status = favoriteService.getFavoriteStatus(targetType, targetId, userId);
        return Result.success(status);
    }

    @GetMapping("/{targetType}/{targetId}/count")
    @Operation(summary = "获取收藏数量")
    public Result<Long> getFavoriteCount(
            @Parameter(description = "目标类型") @PathVariable String targetType,
            @Parameter(description = "目标 ID") @PathVariable Long targetId) {
        Long count = favoriteService.getFavoriteCount(targetType, targetId);
        return Result.success(count);
    }

    @GetMapping("/products")
    @Operation(summary = "获取用户收藏的商品列表")
    public Result<PageResult<ProductVO>> getUserFavoriteProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        Page<ProductVO> page = favoriteService.getUserFavorites(userId, current, size);
        PageResult<ProductVO> result = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }
}
