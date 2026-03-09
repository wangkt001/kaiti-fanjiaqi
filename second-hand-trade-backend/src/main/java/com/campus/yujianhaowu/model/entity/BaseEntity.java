package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@Schema(description = "基础实体")
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "创建时间", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @Schema(description = "逻辑删除标识", accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    @TableLogic
    private Integer deleted;
}
