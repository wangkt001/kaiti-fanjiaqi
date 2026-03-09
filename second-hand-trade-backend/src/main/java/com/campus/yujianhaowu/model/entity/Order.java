package com.campus.yujianhaowu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("orders")
public class Order extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    @TableField("seller_id")
    private Long sellerId;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    @TableField("freight_amount")
    private BigDecimal freightAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("status")
    private Integer status;

    @TableField("payment_type")
    private Integer paymentType;

    @TableField("payment_time")
    private LocalDateTime paymentTime;

    @TableField("delivery_type")
    private String deliveryType;

    @TableField("delivery_no")
    private String deliveryNo;

    @TableField("delivery_time")
    private LocalDateTime deliveryTime;

    @TableField("receive_time")
    private LocalDateTime receiveTime;

    @TableField("remark")
    private String remark;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("receiver_phone")
    private String receiverPhone;

    @TableField("receiver_address")
    private String receiverAddress;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
