package com.campus.yujianhaowu.model.dto;

import lombok.Data;

@Data
public class ReviewReplyRequest {

    private Long reviewId;

    private Long parentReplyId;

    private String content;
}
