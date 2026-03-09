package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文化标签实体
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cultural_tags")
@Schema(description = "文化标签信息")
public class CulturalTag extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "标签名称")
    private String name;

    @Schema(description = "标签描述")
    private String description;

    @Schema(description = "分类（地域/工艺/IP/其他）", example = "文化")
    private String category;

    @Schema(description = "标签图标 URL")
    private String iconUrl;

    @Schema(description = "排序", example = "0")
    private Integer sortOrder;

    @Schema(description = "是否热门", example = "false")
    private Boolean isHot;

    @Schema(description = "是否启用", example = "true")
    private Boolean isActive;
}
