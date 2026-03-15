package com.campus.yujianhaowu.service;

import com.campus.yujianhaowu.model.entity.CartItem;
import com.campus.yujianhaowu.model.vo.CartItemVO;

import java.util.List;
import java.util.Map;

public interface CartService {

    List<CartItemVO> getCartItems(Long userId);

    CartItemVO addToCart(Long userId, Long productId, Integer quantity);

    void updateQuantity(Long userId, Long productId, Integer quantity);

    void selectItem(Long userId, Long productId, Boolean selected);

    void selectAll(Long userId, Boolean selected);

    void deleteItem(Long userId, Long productId);

    void clearCart(Long userId);

    Integer getCartCount(Long userId);

    Map<String, Object> getCheckoutInfo(Long userId, List<Long> cartItemIds);
}
