package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文化资讯实体
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cultural_contents")
@Schema(description = "文化资讯")
public class CulturalContent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "资讯标题")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "资讯内容")
    private String content;

    @Schema(description = "封面图 URL")
    private String coverImage;

    @Schema(description = "封面图 Base64 编码")
    private String coverImageBase64;

    @Schema(description = "分类（intangible_heritage/exhibition/activity/news/story）", example = "intangible_heritage")
    private String category;

    @Schema(description = "标签（JSON 数组）")
    private String tags;

    @Schema(description = "作者 ID（管理员）", example = "1")
    private Long authorId;

    @Schema(description = "浏览量", example = "100")
    private Integer viewCount;

    @Schema(description = "点赞数", example = "50")
    private Integer likeCount;

    @Schema(description = "评论数", example = "20")
    private Integer commentCount;

    @Schema(description = "收藏数", example = "30")
    private Integer favoriteCount;

    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop;

    @Schema(description = "是否推荐", example = "false")
    private Boolean isRecommend;

    @Schema(description = "是否发布", example = "true")
    private Boolean isPublished;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;
}
