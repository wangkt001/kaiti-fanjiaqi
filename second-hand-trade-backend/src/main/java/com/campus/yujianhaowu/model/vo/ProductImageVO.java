package com.campus.yujianhaowu.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品图片 VO
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@Schema(description = "商品图片")
public class ProductImageVO {

    @Schema(description = "图片 ID", example = "1")
    private Long id;

    @Schema(description = "商品 ID", example = "1")
    private Long productId;

    @Schema(description = "图片 URL", example = "http://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "类型（main-主图/detail-详情图）", example = "main")
    private String type;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}
