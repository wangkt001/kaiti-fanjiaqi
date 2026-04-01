package com.campus.yujianhaowu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.PageResult;
import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.dto.favorite.FavoriteRequest;
import com.campus.yujianhaowu.model.vo.FavoriteProductVO;
import com.campus.yujianhaowu.model.vo.FavoriteStatusVO;
import com.campus.yujianhaowu.model.vo.ProductVO;
import com.campus.yujianhaowu.service.FavoriteService;
import com.campus.yujianhaowu.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/favorite")
@Tag(name = "收藏管理", description = "收藏相关接口")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping
    @Operation(summary = "添加收藏")
    public Result<Void> addFavorite(@RequestBody FavoriteRequest request, HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            return Result.error(401, "未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        favoriteService.addFavorite(userId, request);
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{targetType}/{targetId}")
    @Operation(summary = "取消收藏")
    public Result<Void> removeFavorite(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            return Result.error(401, "未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        favoriteService.removeFavorite(userId, targetType, targetId);
        return Result.success();
    }

    /**
     * 获取收藏列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取收藏列表")
    public Result<List<FavoriteProductVO>> getFavoriteList(HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            return Result.error(401, "未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        List<FavoriteProductVO> list = favoriteService.getUserProductFavorites(userId);
        return Result.success(list);
    }

    /**
     * 分页获取收藏商品
     */
    @GetMapping("/products")
    @Operation(summary = "分页获取收藏商品")
    public Result<PageResult<ProductVO>> getUserFavoriteProducts(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            return Result.error(401, "未登录");
        }
        Long userId = Long.parseLong(userIdStr);
        Page<ProductVO> page = favoriteService.getUserFavoriteProducts(userId, current, size);
        PageResult<ProductVO> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    /**
     * 获取收藏状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取收藏状态")
    public Result<FavoriteStatusVO> getFavoriteStatus(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            FavoriteStatusVO vo = new FavoriteStatusVO();
            vo.setFavorited(false);
            vo.setCount(0);
            return Result.success(vo);
        }
        Long userId = Long.parseLong(userIdStr);
        FavoriteStatusVO vo = favoriteService.getFavoriteStatus(userId, targetType, targetId);
        return Result.success(vo);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{targetType}/{targetId}")
    @Operation(summary = "检查是否已收藏")
    public Result<Boolean> checkFavorite(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            HttpServletRequest httpRequest) {
        String userIdStr = httpRequest.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            return Result.success(false);
        }
        Long userId = Long.parseLong(userIdStr);
        boolean isFavorite = favoriteService.isFavorite(userId, targetType, targetId);
        return Result.success(isFavorite);
    }
}
