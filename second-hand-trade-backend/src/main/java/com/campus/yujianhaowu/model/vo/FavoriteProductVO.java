package com.campus.yujianhaowu.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏商品 VO
 */
@Data
public class FavoriteProductVO {

    private Long favoriteId;

    private Long productId;

    private String productName;

    private String productImage;

    private Double price;

    private Integer salesCount;

    private String status;

    private LocalDateTime favoriteTime;
}
