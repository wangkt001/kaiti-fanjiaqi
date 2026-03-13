package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_replies")
public class ReviewReply extends BaseEntity {

    @TableField("review_id")
    private Long reviewId;

    @TableField("user_id")
    private Long userId;

    @TableField("parent_reply_id")
    private Long parentReplyId;

    @TableField("content")
    private String content;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("status")
    private Integer status;
}
