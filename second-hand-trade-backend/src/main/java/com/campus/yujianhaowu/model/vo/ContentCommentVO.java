package com.campus.yujianhaowu.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContentCommentVO {

    private Long id;

    private Long contentId;

    private Long userId;

    private Long parentCommentId;

    private String content;

    private Integer likeCount;

    private Integer replyCount;

    private Integer status;

    private LocalDateTime createdAt;

    private UserInfo userInfo;

    private List<ContentCommentVO> replies;

    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }
}
