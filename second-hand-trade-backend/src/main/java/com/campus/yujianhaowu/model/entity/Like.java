package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("likes")
public class Like extends BaseEntity {

    @TableField("user_id")
    private Long userId;

    @TableField("target_type")
    private String targetType;

    @TableField("target_id")
    private Long targetId;
}
