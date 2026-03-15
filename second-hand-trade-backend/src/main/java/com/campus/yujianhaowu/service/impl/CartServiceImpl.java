package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.CartItemMapper;
import com.campus.yujianhaowu.mapper.ProductMapper;
import com.campus.yujianhaowu.model.entity.CartItem;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.vo.CartItemVO;
import com.campus.yujianhaowu.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CartItemVO> getCartItems(Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getCreatedAt);

        List<CartItem> cartItems = cartItemMapper.selectList(wrapper);
        List<CartItemVO> voList = new ArrayList<>();

        for (CartItem item : cartItems) {
            CartItemVO vo = convertToVO(item);
            voList.add(vo);
        }

        return voList;
    }

    @Override
    public CartItemVO addToCart(Long userId, Long productId, Integer quantity) {
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查商品状态
        if ("offline".equals(product.getStatus())) {
            throw new BusinessException("商品已下架");
        }

        // 检查购物车是否已有该商品
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId);

        CartItem existingItem = cartItemMapper.selectOne(wrapper);
        if (existingItem != null) {
            // 更新数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setSelected(true);
            cartItemMapper.updateById(existingItem);
            return convertToVO(existingItem);
        } else {
            // 新增购物车项
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(quantity);
            item.setSelected(true);
            cartItemMapper.insert(item);
            return convertToVO(item);
        }
    }

    @Override
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException("数量必须大于 0");
        }

        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId);

        CartItem item = cartItemMapper.selectOne(wrapper);
        if (item == null) {
            throw new BusinessException("购物车中无此商品");
        }

        item.setQuantity(quantity);
        cartItemMapper.updateById(item);
    }

    @Override
    public void selectItem(Long userId, Long productId, Boolean selected) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId);

        CartItem item = cartItemMapper.selectOne(wrapper);
        if (item == null) {
            throw new BusinessException("购物车中无此商品");
        }

        item.setSelected(selected);
        cartItemMapper.updateById(item);
    }

    @Override
    public void selectAll(Long userId, Boolean selected) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);

        List<CartItem> items = cartItemMapper.selectList(wrapper);
        for (CartItem item : items) {
            item.setSelected(selected);
            cartItemMapper.updateById(item);
        }
    }

    @Override
    public void deleteItem(Long userId, Long productId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId);

        cartItemMapper.delete(wrapper);
    }

    @Override
    public void clearCart(Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);

        cartItemMapper.delete(wrapper);
    }

    @Override
    public Integer getCartCount(Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);
        Long count = cartItemMapper.selectCount(wrapper);
        return count.intValue();
    }

    @Override
    public Map<String, Object> getCheckoutInfo(Long userId, List<Long> cartItemIds) {
        List<CartItemVO> checkoutItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Object cartItemIdObj : cartItemIds) {
            Long cartItemId = ((Number) cartItemIdObj).longValue();
            CartItem cartItem = cartItemMapper.selectById(cartItemId);
            if (cartItem != null && cartItem.getUserId().equals(userId)) {
                CartItemVO vo = convertToVO(cartItem);
                checkoutItems.add(vo);
                totalAmount = totalAmount.add(
                    vo.getProduct().getPrice().multiply(BigDecimal.valueOf(vo.getQuantity()))
                );
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", checkoutItems);
        result.put("totalAmount", totalAmount);
        result.put("freightAmount", BigDecimal.ZERO);
        result.put("discountAmount", BigDecimal.ZERO);
        result.put("paymentAmount", totalAmount);

        return result;
    }

    private CartItemVO convertToVO(CartItem item) {
        CartItemVO vo = new CartItemVO();
        vo.setId(item.getId());
        vo.setUserId(item.getUserId());
        vo.setProductId(item.getProductId());
        vo.setQuantity(item.getQuantity());
        vo.setSelected(item.getSelected());

        // 加载商品信息
        Product product = productMapper.selectById(item.getProductId());
        if (product != null) {
            CartItemVO.ProductInfo productInfo = new CartItemVO.ProductInfo();
            productInfo.setId(product.getId());
            productInfo.setName(product.getName());
            productInfo.setDescription(product.getDescription());
            productInfo.setPrice(product.getPrice());
            productInfo.setMainImage(null); // 简化：暂不处理商品图片
            productInfo.setStock(product.getStock());
            productInfo.setStatus(product.getStatus());
            vo.setProduct(productInfo);
        }

        return vo;
    }
}
