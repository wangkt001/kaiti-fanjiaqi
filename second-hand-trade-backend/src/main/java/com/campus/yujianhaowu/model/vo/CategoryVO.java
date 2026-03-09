package com.campus.yujianhaowu.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品分类 VO
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "商品分类")
public class CategoryVO {

    @Schema(description = "分类 ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "传统工艺品")
    private String name;

    @Schema(description = "父分类 ID", example = "0")
    private Long parentId;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "分类图标 URL")
    private String iconUrl;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "分类层级", example = "1")
    private Integer level;

    @Schema(description = "是否启用", example = "true")
    private Boolean isActive;

    @Schema(description = "子分类列表")
    private List<CategoryVO> children;
}
