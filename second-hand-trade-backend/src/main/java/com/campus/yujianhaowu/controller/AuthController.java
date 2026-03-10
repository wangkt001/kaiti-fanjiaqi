package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.dto.LoginRequest;
import com.campus.yujianhaowu.model.dto.RegisterRequest;
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
 * 用户控制器
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "用户认证", description = "用户登录、注册、登出等接口")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Long> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(HttpServletRequest request) {
        // 简化认证，无需特殊处理
        return Result.success();
    }
}
