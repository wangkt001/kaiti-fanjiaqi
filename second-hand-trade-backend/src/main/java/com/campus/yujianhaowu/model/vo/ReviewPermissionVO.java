package com.campus.yujianhaowu.model.vo;

import lombok.Data;

@Data
public class ReviewPermissionVO {

    private Boolean canReview;

    private Boolean hasPurchased;

    private Boolean hasReviewed;

    private Long orderId;

    private String reason;
}
