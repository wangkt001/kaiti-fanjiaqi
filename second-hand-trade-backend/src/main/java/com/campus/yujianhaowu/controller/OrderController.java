package com.campus.yujianhaowu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.PageResult;
import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.interceptor.AuthInterceptor;
import com.campus.yujianhaowu.model.dto.OrderCreateRequest;
import com.campus.yujianhaowu.model.vo.OrderVO;
import com.campus.yujianhaowu.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单")
    public Result<String> createOrder(
            @RequestBody @Validated OrderCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        String orderNo = orderService.createOrder(userId, request);
        return Result.success(orderNo);
    }

    @GetMapping
    @Operation(summary = "获取用户订单列表")
    public Result<PageResult<OrderVO>> getUserOrders(
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        Page<OrderVO> page = orderService.getUserOrders(userId, status, current, size);
        PageResult<OrderVO> result = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public Result<OrderVO> getOrderDetail(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        OrderVO order = orderService.getOrderDetail(id, userId);
        return Result.success(order);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        orderService.cancelOrder(id, userId);
        return Result.success(null);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认收货")
    public Result<Void> confirmOrder(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        orderService.confirmOrder(id, userId);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单")
    public Result<Void> deleteOrder(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        orderService.deleteOrder(id, userId);
        return Result.success(null);
    }

    @GetMapping("/seller/list")
    @Operation(summary = "卖家订单列表")
    public Result<PageResult<OrderVO>> getSellerOrders(
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        Long sellerId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        Page<OrderVO> page = orderService.getSellerOrders(sellerId, status, current, size);
        PageResult<OrderVO> result = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
        return Result.success(result);
    }

    @PutMapping("/seller/{id}/ship")
    @Operation(summary = "卖家发货")
    public Result<Void> shipOrder(
            @Parameter(description = "订单 ID") @PathVariable Long id,
            @Parameter(description = "物流单号") @RequestParam String deliveryNo,
            @Parameter(description = "配送方式") @RequestParam String deliveryType,
            HttpServletRequest httpRequest) {
        Long sellerId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTR);
        orderService.shipOrder(id, deliveryNo, deliveryType, sellerId);
        return Result.success(null);
    }
}
