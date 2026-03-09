package com.campus.yujianhaowu.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ContentCommentCreateRequest {

    @NotNull(message = "资讯 ID 不能为空")
    private Long contentId;

    private Long parentCommentId;

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
