package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cultural_contents")
public class CulturalContent extends BaseEntity {

    @TableField("title")
    private String title;

    @TableField("summary")
    private String summary;

    @TableField("content")
    private String content;

    @TableField("cover_image")
    private String coverImage;

    @TableField("category")
    private String category;

    @TableField("tags")
    private String tags;

    @TableField("author_id")
    private Long authorId;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("comment_count")
    private Integer commentCount;

    @TableField("favorite_count")
    private Integer favoriteCount;

    @TableField("is_top")
    private Boolean isTop;

    @TableField("is_recommend")
    private Boolean isRecommend;

    @TableField("is_published")
    private Boolean isPublished;

    @TableField("published_at")
    private LocalDateTime publishedAt;
}
