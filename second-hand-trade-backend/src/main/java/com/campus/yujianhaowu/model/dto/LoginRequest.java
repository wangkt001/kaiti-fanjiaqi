package com.campus.yujianhaowu.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户登录请求 DTO
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@Schema(description = "用户登录请求")
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度在 3 到 50 个字符")
    @Schema(description = "用户名/手机号/邮箱", example = "admin", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在 6 到 20 个字符")
    @Schema(description = "密码", example = "admin123", required = true)
    private String password;

    @Schema(description = "是否记住我", example = "true")
    private Boolean remember = true;
}
