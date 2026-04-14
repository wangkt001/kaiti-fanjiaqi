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

    OrderVO getSellerOrderDetail(Long orderId, Long sellerId);

    void shipOrder(Long orderId, String deliveryNo, String deliveryType, Long sellerId);

    /**
     * 获取订单列表（管理员）
     *
     * @param status  订单状态
     * @param current 页码
     * @param size    每页数量
     * @return 订单分页数据
     */
    Page<OrderVO> listOrders(Integer status, Integer current, Integer size);

    /**
     * 支付订单（模拟）
     *
     * @param orderNo     订单编号
     * @param paymentType 支付方式
     * @param userId      用户ID
     */
    void payOrder(String orderNo, Integer paymentType, Long userId);
}
