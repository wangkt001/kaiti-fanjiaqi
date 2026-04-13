package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.common.ResultCode;
import com.campus.yujianhaowu.exception.BusinessException;
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
        // 直接从请求头获取 userId
        String userIdStr = request.getHeader("X-User-Id");

        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Long userId = Long.parseLong(userIdStr);
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
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        String avatarUrl = request.get("avatarUrl");
        userService.uploadAvatar(userId, avatarUrl);
        return Result.success();
    }

    @PostMapping("/apply-seller")
    @Operation(summary = "申请成为卖家")
    public Result<Void> applySeller(
            @RequestBody Map<String, Object> sellerInfo,
            HttpServletRequest request) {
        // 直接从请求头获取 userId
        String userIdStr = request.getHeader("X-User-Id");

        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Long userId = Long.parseLong(userIdStr);
        userService.applySeller(userId, sellerInfo);
        return Result.success();
    }

    @GetMapping("/seller-status")
    @Operation(summary = "获取卖家状态")
    public Result<Map<String, String>> getSellerStatus(HttpServletRequest request) {
        // 直接从请求头获取 userId
        String userIdStr = request.getHeader("X-User-Id");

        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Long userId = Long.parseLong(userIdStr);
        return Result.success(userService.getSellerStatus(userId));
    }

    @GetMapping("/seller-apply-info")
    @Operation(summary = "获取卖家申请信息")
    public Result<Map<String, Object>> getSellerApplyInfo(HttpServletRequest request) {
        // 直接从请求头获取 userId
        String userIdStr = request.getHeader("X-User-Id");

        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Long userId = Long.parseLong(userIdStr);
        return Result.success(userService.getSellerApplyInfo(userId));
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "获取店铺公开信息")
    public Result<UserVO> getSellerPublicProfile(@PathVariable Long id) {
        return Result.success(userService.getSellerPublicProfile(id));
    }

    // ==================== 后台管理接口 ====================

    @GetMapping("/admin/list")
    @Operation(summary = "获取用户列表（管理员）")
    public Result<com.campus.yujianhaowu.common.PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserVO> page = userService.listUsers(current, size);
        com.campus.yujianhaowu.common.PageResult<UserVO> result = com.campus.yujianhaowu.common.PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    @PutMapping("/admin/status/{id}")
    @Operation(summary = "更新用户状态（管理员）")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    @GetMapping("/admin/pending-sellers")
    @Operation(summary = "获取待审核卖家列表（管理员）")
    public Result<com.campus.yujianhaowu.common.PageResult<UserVO>> listPendingSellers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserVO> page = userService
                .listPendingSellers(current, size);
        com.campus.yujianhaowu.common.PageResult<UserVO> result = com.campus.yujianhaowu.common.PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    @PostMapping("/admin/audit-seller/{id}")
    @Operation(summary = "审核卖家申请（管理员）")
    public Result<Void> auditSeller(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remark) {
        userService.auditSeller(id, status, remark);
        return Result.success();
    }

    @DeleteMapping("/admin/{id}")
    @Operation(summary = "删除用户（管理员）")
    public Result<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        userService.deleteUser(adminId, id);
        return Result.success();
    }
}
