package com.campus.yujianhaowu.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息 VO
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户 ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像 URL")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "性别（0-未知 1-男 2-女）", example = "1")
    private Integer gender;

    @Schema(description = "角色（buyer/seller/admin）", example = "buyer")
    private String role;

    @Schema(description = "卖家状态（pending/approved/rejected）")
    private String sellerStatus;

    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "店铺 Logo URL")
    private String shopLogo;

    @Schema(description = "粉丝数量", example = "100")
    private Integer fansCount;

    @Schema(description = "关注数量", example = "50")
    private Integer followCount;

    @Schema(description = "用户状态（1-正常 0-禁用）", example = "1")
    private Integer status;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;
}
