package com.campus.yujianhaowu.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {

    private Long id;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private Boolean selected;

    private ProductInfo product;

    @Data
    public static class ProductInfo {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String mainImage;
        private Integer stock;
        private String status;
        private Long sellerId;
    }
}
