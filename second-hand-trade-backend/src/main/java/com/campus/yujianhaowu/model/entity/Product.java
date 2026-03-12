package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("products")
@Schema(description = "商品信息")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "原价（划线价）")
    private BigDecimal originalPrice;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "卖家 ID")
    private Long sellerId;

    @Schema(description = "状态（pending/on_sale/sold_out/offline）", example = "on_sale")
    private String status;

    @Schema(description = "销量", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    private Integer salesCount;

    @Schema(description = "浏览量", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    private Integer viewCount;

    @Schema(description = "收藏量", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    private Integer favoriteCount;

    @Schema(description = "是否有 SKU", example = "false")
    private Boolean hasSku;

    @Schema(description = "是否支持礼品包装", example = "false")
    private Boolean giftPackaging;

    @Schema(description = "是否支持定制", example = "false")
    private Boolean customization;

    @Schema(description = "包装费用")
    private BigDecimal packagingFee;

    @Schema(description = "运费模板 ID")
    private Long freightTemplateId;

    @Schema(description = "商品主图（Base64 编码或图片 ID）")
    private String imageUrl;

    @Schema(description = "上架时间", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    private LocalDateTime publishedAt;
}
