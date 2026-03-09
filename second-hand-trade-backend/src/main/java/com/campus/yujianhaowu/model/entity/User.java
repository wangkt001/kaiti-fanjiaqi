package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
@Schema(description = "用户信息")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码（加密存储）", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "角色（buyer/seller/admin）", example = "buyer")
    private String role;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像 URL")
    private String avatar;

    @Schema(description = "性别（0-未知 1-男 2-女）", example = "0")
    private Integer gender;

    @Schema(description = "卖家状态（pending/approved/rejected）", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    private String sellerStatus;

    @Schema(description = "卖家资质信息（JSON）")
    private String sellerInfo;

    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "店铺 Logo URL")
    private String shopLogo;

    @Schema(description = "店铺描述")
    private String shopDescription;

    @Schema(description = "粉丝数量", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    private Integer fansCount;

    @Schema(description = "关注数量", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    private Integer followCount;

    @Schema(description = "状态（1-正常 0-禁用）", example = "1")
    private Integer status;

    @Schema(description = "最后登录时间", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    private LocalDateTime lastLoginAt;

    @Schema(description = "最后登录 IP", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    private String lastLoginIp;
}
