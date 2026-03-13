package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reviews")
public class Review extends BaseEntity {

    @TableField("product_id")
    private Long productId;

    @TableField("order_id")
    private Long orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("seller_id")
    private Long sellerId;

    @TableField("rating")
    private Integer rating;

    @TableField("content")
    private String content;

    @TableField("images")
    private String images;

    @TableField("is_anonymous")
    private Boolean isAnonymous;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("reply_count")
    private Integer replyCount;

    @TableField("status")
    private Integer status;
}
