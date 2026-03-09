package com.campus.yujianhaowu.model.vo;

import com.campus.yujianhaowu.model.entity.Review;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReviewVO {

    private Long id;

    private Long productId;

    private Long orderId;

    private Long userId;

    private Long sellerId;

    private Integer rating;

    private String content;

    private String images;

    private Boolean isAnonymous;

    private Integer likeCount;

    private Integer replyCount;

    private Integer status;

    private LocalDateTime createdAt;

    private UserInfo userInfo;

    private List<ReviewReplyVO> replies;

    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }
}
