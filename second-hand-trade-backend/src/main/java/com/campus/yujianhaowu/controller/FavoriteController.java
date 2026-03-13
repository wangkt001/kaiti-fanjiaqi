package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.dto.favorite.FavoriteRequest;
import com.campus.yujianhaowu.model.vo.FavoriteProductVO;
import com.campus.yujianhaowu.model.vo.FavoriteStatusVO;
import com.campus.yujianhaowu.service.FavoriteService;
import com.campus.yujianhaowu.util.UserContextUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping
    public Result<Void> addFavorite(@RequestBody FavoriteRequest request) {
        Long userId = UserContextUtil.getCurrentUserId();
        favoriteService.addFavorite(userId, request);
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{targetId}")
    public Result<Void> removeFavorite(@PathVariable Long targetId) {
        Long userId = UserContextUtil.getCurrentUserId();
        favoriteService.removeFavorite(userId, targetId);
        return Result.success();
    }

    /**
     * 获取收藏列表
     */
    @GetMapping("/list")
    public Result<List<FavoriteProductVO>> getFavoriteList() {
        Long userId = UserContextUtil.getCurrentUserId();
        List<FavoriteProductVO> list = favoriteService.getUserProductFavorites(userId);
        return Result.success(list);
    }

    /**
     * 获取收藏状态
     */
    @GetMapping("/status")
    public Result<FavoriteStatusVO> getFavoriteStatus(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        Long userId = UserContextUtil.getCurrentUserId();
        boolean isFavorite = favoriteService.isFavorite(userId, targetId);

        FavoriteStatusVO vo = new FavoriteStatusVO();
        vo.setFavorited(isFavorite);
        vo.setCount(0); // 简化：暂不返回具体数量

        return Result.success(vo);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{targetId}")
    public Result<Boolean> checkFavorite(@PathVariable Long targetId) {
        Long userId = UserContextUtil.getCurrentUserId();
        boolean isFavorite = favoriteService.isFavorite(userId, targetId);
        return Result.success(isFavorite);
    }
}
