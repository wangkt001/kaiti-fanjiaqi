package com.campus.yujianhaowu.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品文化信息 VO
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@Schema(description = "商品文化信息")
public class ProductCulturalInfoVO {

    @Schema(description = "记录 ID", example = "1")
    private Long id;

    @Schema(description = "商品 ID", example = "1")
    private Long productId;

    @Schema(description = "文化背景")
    private String culturalBackground;

    @Schema(description = "工艺说明")
    private String craftDescription;

    @Schema(description = "设计师/传承人")
    private String designer;

    @Schema(description = "产地", example = "河南洛阳")
    private String originPlace;

    @Schema(description = "材质")
    private String material;

    @Schema(description = "文化 IP", example = "河南博物院")
    private String culturalIp;

    @Schema(description = "文化故事详情")
    private String storyContent;
}
