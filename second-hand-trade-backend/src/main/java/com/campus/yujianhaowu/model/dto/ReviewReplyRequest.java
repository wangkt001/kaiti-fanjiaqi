package com.campus.yujianhaowu.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ReviewReplyRequest {

    @NotNull(message = "评价 ID 不能为空")
    private Long reviewId;

    private Long parentReplyId;

    @NotBlank(message = "回复内容不能为空")
    private String content;
}
