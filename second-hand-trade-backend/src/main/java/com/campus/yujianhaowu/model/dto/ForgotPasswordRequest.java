package com.campus.yujianhaowu.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "忘记密码请求")
public class ForgotPasswordRequest {

    @NotBlank(message = "登录账号不能为空")
    @Schema(description = "登录账号", example = "zhangsan", required = true)
    private String account;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在 6 到 20 个字符")
    @Schema(description = "新密码", example = "123456", required = true)
    private String newPassword;

    @NotBlank(message = "旧密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在 6 到 20 个字符")
    @Schema(description = "旧密码", example = "123456", required = true)
    private String oldPassword;
}
