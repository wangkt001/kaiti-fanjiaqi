package com.campus.yujianhaowu.model.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class ReviewCreateRequest {

    @NotNull(message = "商品 ID 不能为空")
    private Long productId;

    @NotNull(message = "订单 ID 不能为空")
    private Long orderId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为 1 星")
    @Max(value = 5, message = "评分最高为 5 星")
    private Integer rating;

    private String content;

    private String images;

    private Boolean isAnonymous;
}
