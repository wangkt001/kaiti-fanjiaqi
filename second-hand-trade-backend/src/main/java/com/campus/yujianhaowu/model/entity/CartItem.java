package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 购物车项实体
 * <p>
 * 注意：不继承 BaseEntity，因为购物车项不需要逻辑删除（物理删除即可）
 * </p>
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Data
@TableName("cart_items")
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("product_id")
    private Long productId;

    @TableField("quantity")
    private Integer quantity;

    @TableField("selected")
    private Boolean selected;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 不包含 deleted 字段，使用物理删除
}
