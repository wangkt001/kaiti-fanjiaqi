package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("content_comments")
public class ContentComment extends BaseEntity {

    @TableField("content_id")
    private Long contentId;

    @TableField("user_id")
    private Long userId;

    @TableField("parent_comment_id")
    private Long parentCommentId;

    @TableField("content")
    private String content;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("reply_count")
    private Integer replyCount;

    @TableField("status")
    private Integer status;
}
