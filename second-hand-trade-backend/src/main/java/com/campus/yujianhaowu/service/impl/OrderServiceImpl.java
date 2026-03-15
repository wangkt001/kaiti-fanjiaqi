package com.campus.yujianhaowu.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.CartItemMapper;
import com.campus.yujianhaowu.mapper.OrderItemMapper;
import com.campus.yujianhaowu.mapper.OrderMapper;
import com.campus.yujianhaowu.mapper.ProductMapper;
import com.campus.yujianhaowu.model.dto.OrderCreateRequest;
import com.campus.yujianhaowu.model.entity.CartItem;
import com.campus.yujianhaowu.model.entity.Order;
import com.campus.yujianhaowu.model.entity.OrderItem;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.vo.CartItemVO;
import com.campus.yujianhaowu.model.vo.OrderVO;
import com.campus.yujianhaowu.service.CartService;
import com.campus.yujianhaowu.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final CartService cartService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Long userId, OrderCreateRequest request) {
        // 1. 获取购物车选中的商品
        List<CartItemVO> cartItems = cartService.getCartItems(userId);
        List<CartItemVO> selectedItems = cartItems.stream()
                .filter(CartItemVO::getSelected)
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new BusinessException("请选择要购买的商品");
        }

        // 2. 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItemVO item : selectedItems) {
            totalAmount = totalAmount.add(
                    item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 3. 创建订单
        // 注意：这里我们假设购物车中选中的商品都属于同一个卖家
        // 在实际业务中，如果包含多个卖家的商品，应该拆分为多个子订单
        // 这里为了简化，我们取第一个商品的卖家 ID 作为订单的卖家 ID
        // 如果需要支持多卖家结算，前端应该限制只能结算同一个卖家的商品，或者后端进行拆单
        Long sellerId = selectedItems.get(0).getProduct().getSellerId();

        String orderNo = IdUtil.fastSimpleUUID();
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setSellerId(sellerId);
        order.setTotalAmount(totalAmount);
        order.setPaymentAmount(totalAmount); // 简化：无运费和优惠
        order.setFreightAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setStatus(0); // 待付款
        order.setPaymentType(request.getPaymentType());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setRemark(request.getRemark());

        orderMapper.insert(order);

        // 4. 创建订单商品
        for (CartItemVO cartItem : selectedItems) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(cartItem.getProductId());
            item.setProductName(cartItem.getProduct().getName());
            item.setProductImage(cartItem.getProduct().getMainImage());
            item.setPrice(cartItem.getProduct().getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setTotalPrice(
                    cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItemMapper.insert(item);

            // 5. 扣减库存
            Product product = productMapper.selectById(cartItem.getProductId());
            if (product != null && product.getStock() >= cartItem.getQuantity()) {
                product.setStock(product.getStock() - cartItem.getQuantity());
                productMapper.updateById(product);
            }
        }

        // 6. 删除已购买的购物车项
        for (CartItemVO cartItem : selectedItems) {
            cartService.deleteItem(userId, cartItem.getProductId());
        }

        return orderNo;
    }

    @Override
    public Page<OrderVO> getUserOrders(Long userId, Integer status, Integer current, Integer size) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);

        Page<OrderVO> voPage = new Page<>(current, size, orderPage.getTotal());
        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public OrderVO getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该订单");
        }
        return convertToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消待付款订单");
        }

        order.setStatus(4); // 已取消
        orderMapper.updateById(order);

        // 恢复库存
        List<OrderItem> items = getOrderItems(orderId);
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("只能确认待收货订单");
        }

        order.setStatus(3); // 已完成
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 3 && order.getStatus() != 4) {
            throw new BusinessException("只能删除已完成或已取消的订单");
        }
        order.setDeleted(1);
        orderMapper.updateById(order);
    }

    @Override
    public Page<OrderVO> getSellerOrders(Long sellerId, Integer status, Integer current, Integer size) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getSellerId, sellerId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        Page<OrderVO> voPage = new Page<>(current, size, orderPage.getTotal());
        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long orderId, String deliveryNo, String deliveryType, Long sellerId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("只能对待发货订单进行发货操作");
        }

        order.setStatus(2); // 待收货
        order.setDeliveryType(deliveryType);
        order.setDeliveryNo(deliveryNo);
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public Page<OrderVO> listOrders(Integer status, Integer current, Integer size) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        Page<OrderVO> voPage = new Page<>(current, size, orderPage.getTotal());
        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(String orderNo, Integer paymentType, Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);

        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，无法支付");
        }

        order.setStatus(1);
        order.setPaymentType(paymentType);
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    private List<OrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return orderItemMapper.selectList(wrapper);
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setSellerId(order.getSellerId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPaymentAmount(order.getPaymentAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setStatus(order.getStatus());
        vo.setPaymentType(order.getPaymentType());
        vo.setPaymentTime(order.getPaymentTime());
        vo.setDeliveryType(order.getDeliveryType());
        vo.setDeliveryNo(order.getDeliveryNo());
        vo.setDeliveryTime(order.getDeliveryTime());
        vo.setReceiveTime(order.getReceiveTime());
        vo.setRemark(order.getRemark());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setCreatedAt(order.getCreatedAt());

        // 加载订单商品
        List<OrderItem> items = getOrderItems(order.getId());
        List<OrderVO.OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItem item : items) {
            OrderVO.OrderItemVO itemVO = new OrderVO.OrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(item.getProductName());
            itemVO.setProductImage(item.getProductImage());
            itemVO.setPrice(item.getPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setTotalPrice(item.getTotalPrice());
            itemVOs.add(itemVO);
        }
        vo.setItems(itemVOs);

        return vo;
    }
}
