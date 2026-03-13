package com.campus.yujianhaowu.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文化资讯 VO
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "文化资讯")
public class CulturalContentVO {

    @Schema(description = "资讯 ID", example = "1")
    private Long id;

    @Schema(description = "资讯标题", example = "豫剧：中原文化的瑰宝")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "资讯内容")
    private String content;

    @Schema(description = "封面图 URL")
    private String coverImage;

    @Schema(description = "分类", example = "intangible_heritage")
    private String category;

    @Schema(description = "标签（JSON 数组）")
    private String tags;

    @Schema(description = "作者 ID", example = "1")
    private Long authorId;

    @Schema(description = "作者名称", example = "管理员")
    private String authorName;

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

    @Schema(description = "是否推荐", example = "true")
    private Boolean isRecommend;

    @Schema(description = "是否发布", example = "true")
    private Boolean isPublished;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
