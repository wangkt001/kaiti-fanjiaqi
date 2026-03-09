package com.campus.yujianhaowu.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文化标签 VO
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@Schema(description = "文化标签")
public class CulturalTagVO {

    @Schema(description = "标签 ID", example = "1")
    private Long id;

    @Schema(description = "标签名称", example = "豫剧")
    private String name;

    @Schema(description = "标签描述")
    private String description;

    @Schema(description = "分类", example = "文化")
    private String category;

    @Schema(description = "图标 URL")
    private String iconUrl;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否热门", example = "true")
    private Boolean isHot;

    @Schema(description = "是否启用", example = "true")
    private Boolean isActive;
}
