package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.entity.User;
import com.campus.yujianhaowu.model.vo.UserVO;
import com.campus.yujianhaowu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户信息控制器
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息管理相关接口")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息")
    public Result<UserVO> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getCurrentUser(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户信息")
    public Result<Void> updateProfile(@Valid @RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, user);
        return Result.success();
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @PostMapping("/avatar")
    @Operation(summary = "上传头像")
    public Result<Void> uploadAvatar(
            @RequestParam String avatarUrl,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.uploadAvatar(userId, avatarUrl);
        return Result.success();
    }

    @PostMapping("/apply-seller")
    @Operation(summary = "申请成为卖家")
    public Result<Void> applySeller(
            @RequestBody Map<String, Object> sellerInfo,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.applySeller(userId, sellerInfo);
        return Result.success();
    }

    @GetMapping("/seller-status")
    @Operation(summary = "获取卖家状态")
    public Result<Map<String, String>> getSellerStatus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getSellerStatus(userId));
    }
}
