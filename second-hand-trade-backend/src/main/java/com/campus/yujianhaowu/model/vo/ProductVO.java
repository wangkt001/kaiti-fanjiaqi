package com.campus.yujianhaowu.model.vo;

import com.campus.yujianhaowu.model.entity.CulturalTag;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品信息 VO
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "商品信息")
public class ProductVO {

    @Schema(description = "商品 ID", example = "1")
    private Long id;

    @Schema(description = "商品名称", example = "河南博物院文创产品")
    private String name;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品价格", example = "99.00")
    private BigDecimal price;

    @Schema(description = "原价", example = "129.00")
    private BigDecimal originalPrice;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Schema(description = "分类 ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "博物馆文创")
    private String categoryName;

    @Schema(description = "卖家 ID", example = "1")
    private Long sellerId;

    @Schema(description = "卖家昵称", example = "河南博物院旗舰店")
    private String sellerName;

    @Schema(description = "店铺名称", example = "河南博物院旗舰店")
    private String shopName;

    @Schema(description = "店铺 Logo URL")
    private String shopLogo;

    @Schema(description = "状态（pending/on_sale/sold_out/offline）", example = "on_sale")
    private String status;

    @Schema(description = "销量", example = "500")
    private Integer salesCount;

    @Schema(description = "浏览量", example = "1000")
    private Integer viewCount;

    @Schema(description = "收藏量", example = "200")
    private Integer favoriteCount;

    @Schema(description = "是否有 SKU", example = "false")
    private Boolean hasSku;

    @Schema(description = "是否支持礼品包装", example = "true")
    private Boolean giftPackaging;

    @Schema(description = "是否支持定制", example = "false")
    private Boolean customization;

    @Schema(description = "商品图片列表")
    private List<ProductImageVO> images;

    @Schema(description = "文化标签列表")
    private List<CulturalTagVO> tags;

    @Schema(description = "文化信息")
    private ProductCulturalInfoVO culturalInfo;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "上架时间")
    private LocalDateTime publishedAt;

    @Schema(description = "是否已收藏", example = "false")
    private Boolean isFavorite;
}
