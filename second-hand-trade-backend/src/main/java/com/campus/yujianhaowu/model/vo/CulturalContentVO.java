package com.campus.yujianhaowu.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CulturalContentVO {

    private Long id;

    private String title;

    private String summary;

    private String content;

    private String coverImage;

    private String category;

    private List<String> tags;

    private Long authorId;

    private String authorName;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer favoriteCount;

    private Boolean isTop;

    private Boolean isRecommend;

    private Boolean isPublished;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
