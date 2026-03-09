package com.campus.yujianhaowu.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewReplyVO {

    private Long id;

    private Long reviewId;

    private Long userId;

    private Long parentReplyId;

    private String content;

    private Integer likeCount;

    private Integer status;

    private LocalDateTime createdAt;

    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }
}
