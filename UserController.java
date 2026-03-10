package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.vo.UserVO;
import com.campus.yujianhaowu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public Result<UserVO> getCurrentUser(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return Result.success(userService.getUserById(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "更新用户信息")
    public Result<Void> updateUserInfo(
            @RequestBody Map<String, Object> updates,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        userService.updateUserInfo(userId, updates);
        return Result.success(null);
    }

    @PostMapping("/apply-seller")
    @Operation(summary = "申请成为卖家")
    public Result<Void> applySeller(
            @RequestBody Map<String, Object> sellerInfo,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        userService.applySeller(userId, sellerInfo);
        return Result.success(null);
    }
}