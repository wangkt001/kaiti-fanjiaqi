package com.campus.yujianhaowu.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private Long sellerId;

    private String userName;

    private BigDecimal totalAmount;

    private BigDecimal paymentAmount;

    private BigDecimal freightAmount;

    private BigDecimal discountAmount;

    private Integer status;

    private Integer paymentType;

    private LocalDateTime paymentTime;

    private String deliveryType;

    private String deliveryNo;

    private LocalDateTime deliveryTime;

    private LocalDateTime receiveTime;

    private String remark;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private LocalDateTime createdAt;

    private List<OrderItemVO> items;

    @Data
    public static class OrderItemVO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalPrice;
    }
}
