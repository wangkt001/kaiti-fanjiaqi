package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类实体
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("categories")
@Schema(description = "商品分类信息")
public class Category extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父分类 ID")
    private Long parentId;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "分类图标 URL")
    private String iconUrl;

    @Schema(description = "排序", example = "0")
    private Integer sortOrder;

    @Schema(description = "分类层级（1-一级 2-二级）", example = "1")
    private Integer level;

    @Schema(description = "是否启用", example = "true")
    private Boolean isActive;
}
