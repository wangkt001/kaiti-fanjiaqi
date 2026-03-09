package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.model.dto.OrderCreateRequest;
import com.campus.yujianhaowu.model.vo.OrderVO;

import java.util.List;

public interface OrderService {

    String createOrder(Long userId, OrderCreateRequest request);

    Page<OrderVO> getUserOrders(Long userId, Integer status, Integer current, Integer size);

    OrderVO getOrderDetail(Long orderId, Long userId);

    void cancelOrder(Long orderId, Long userId);

    void confirmOrder(Long orderId, Long userId);

    void deleteOrder(Long orderId, Long userId);

    Page<OrderVO> getSellerOrders(Long sellerId, Integer status, Integer current, Integer size);

    void shipOrder(Long orderId, String deliveryNo, String deliveryType, Long sellerId);
}
