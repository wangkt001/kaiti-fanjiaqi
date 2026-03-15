package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞实体
 * <p>
 * 注意：不继承 BaseEntity，因为 likes 表没有 updated_at 字段
 * </p>
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@TableName("likes")
public class Like implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("target_type")
    private String targetType;

    @TableField("target_id")
    private Long targetId;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "逻辑删除标识", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    @TableLogic
    private Integer deleted;
}
