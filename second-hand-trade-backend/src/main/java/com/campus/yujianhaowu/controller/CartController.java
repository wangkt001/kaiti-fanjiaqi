package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.vo.CartItemVO;
import com.campus.yujianhaowu.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "购物车管理", description = "购物车相关接口")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "获取购物车列表")
    public Result<List<CartItemVO>> getCartItems(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        List<CartItemVO> items = cartService.getCartItems(userId);
        return Result.success(items);
    }

    @GetMapping("/count")
    @Operation(summary = "获取购物车商品数量")
    public Result<Integer> getCartCount(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer count = cartService.getCartCount(userId);
        return Result.success(count);
    }

    @PostMapping("/add")
    @Operation(summary = "添加到购物车")
    public Result<CartItemVO> addToCart(
            @Parameter(description = "商品 ID") @RequestParam Long productId,
            @Parameter(description = "数量") @RequestParam(defaultValue = "1") Integer quantity,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        CartItemVO item = cartService.addToCart(userId, productId, quantity);
        return Result.success(item);
    }

    @PutMapping("/update")
    @Operation(summary = "更新购物车商品数量")
    public Result<Void> updateQuantity(
            @Parameter(description = "商品 ID") @RequestParam Long productId,
            @Parameter(description = "数量") @RequestParam Integer quantity,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        cartService.updateQuantity(userId, productId, quantity);
        return Result.success(null);
    }

    @PutMapping("/select")
    @Operation(summary = "选中/取消选中购物车商品")
    public Result<Void> selectItem(
            @Parameter(description = "商品 ID") @RequestParam Long productId,
            @Parameter(description = "是否选中") @RequestParam Boolean selected,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        cartService.selectItem(userId, productId, selected);
        return Result.success(null);
    }

    @PutMapping("/select-all")
    @Operation(summary = "全选/取消全选")
    public Result<Void> selectAll(
            @Parameter(description = "是否全选") @RequestParam Boolean selected,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        cartService.selectAll(userId, selected);
        return Result.success(null);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除购物车商品")
    public Result<Void> deleteItem(
            @Parameter(description = "商品 ID") @RequestParam Long productId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        cartService.deleteItem(userId, productId);
        return Result.success(null);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "清空购物车")
    public Result<Void> clearCart(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        cartService.clearCart(userId);
        return Result.success(null);
    }

    @PostMapping("/checkout")
    @Operation(summary = "获取结算信息")
    public Result<Map<String, Object>> checkout(
            @Parameter(description = "购物车项 ID 列表") @RequestParam List<Long> cartItemIds,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        // TODO: 实现结算逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", 0);
        result.put("freightAmount", 0);
        result.put("paymentAmount", 0);
        return Result.success(result);
    }
}
