package com.campus.yujianhaowu.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.LikeMapper;
import com.campus.yujianhaowu.mapper.OrderItemMapper;
import com.campus.yujianhaowu.mapper.OrderMapper;
import com.campus.yujianhaowu.mapper.ProductMapper;
import com.campus.yujianhaowu.mapper.ReviewMapper;
import com.campus.yujianhaowu.mapper.ReviewReplyMapper;
import com.campus.yujianhaowu.mapper.UserMapper;
import com.campus.yujianhaowu.model.dto.ReviewCreateRequest;
import com.campus.yujianhaowu.model.dto.ReviewReplyRequest;
import com.campus.yujianhaowu.model.entity.Like;
import com.campus.yujianhaowu.model.entity.Order;
import com.campus.yujianhaowu.model.entity.OrderItem;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.entity.Review;
import com.campus.yujianhaowu.model.entity.ReviewReply;
import com.campus.yujianhaowu.model.entity.User;
import com.campus.yujianhaowu.model.vo.ReviewPermissionVO;
import com.campus.yujianhaowu.model.vo.ReviewReplyVO;
import com.campus.yujianhaowu.model.vo.ReviewVO;
import com.campus.yujianhaowu.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ReviewReplyMapper reviewReplyMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final LikeMapper likeMapper;

    @Override
    public Page<ReviewVO> getProductReviews(Long productId, Integer current, Integer size, Integer rating) {
        Page<Review> page = new Page<>(current, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId)
                .eq(Review::getStatus, 1)
                .orderByDesc(Review::getCreatedAt);

        if (rating != null) {
            if (rating >= 4) {
                wrapper.ge(Review::getRating, 4);
            } else if (rating <= 2) {
                wrapper.le(Review::getRating, 2);
            } else {
                wrapper.eq(Review::getRating, rating);
            }
        }

        Page<Review> reviewPage = reviewMapper.selectPage(page, wrapper);
        Page<ReviewVO> voPage = new Page<>(current, size, reviewPage.getTotal());
        voPage.setRecords(reviewPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public ReviewVO getReviewById(Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null || review.getStatus() == null || review.getStatus() != 1) {
            throw new BusinessException("评价不存在");
        }
        return convertToVO(review);
    }

    @Override
    public Page<ReviewVO> getUserReviews(Long userId, Integer current, Integer size) {
        Page<Review> page = new Page<>(current, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId)
                .eq(Review::getStatus, 1)
                .orderByDesc(Review::getCreatedAt);

        Page<Review> reviewPage = reviewMapper.selectPage(page, wrapper);
        Page<ReviewVO> voPage = new Page<>(current, size, reviewPage.getTotal());
        voPage.setRecords(reviewPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewVO createReview(ReviewCreateRequest request, Long userId) {
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null || order.getDeleted() != null && order.getDeleted() == 1) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("只能评价自己的订单");
        }
        if (order.getStatus() == null || order.getStatus() != 3) {
            throw new BusinessException("仅已完成订单可评价");
        }

        OrderItem orderItem = findOrderItem(request.getOrderId(), request.getProductId());
        if (orderItem == null) {
            throw new BusinessException("订单中不存在该商品");
        }

        LambdaQueryWrapper<Review> repeatWrapper = new LambdaQueryWrapper<>();
        repeatWrapper.eq(Review::getOrderId, request.getOrderId())
                .eq(Review::getProductId, request.getProductId())
                .eq(Review::getUserId, userId)
                .eq(Review::getStatus, 1);
        if (reviewMapper.selectCount(repeatWrapper) > 0) {
            throw new BusinessException("该商品已评价，请勿重复提交");
        }

        Review review = new Review();
        review.setProductId(request.getProductId());
        review.setOrderId(request.getOrderId());
        review.setUserId(userId);
        review.setSellerId(order.getSellerId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(toJsonString(request.getImages()));
        review.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false);
        review.setLikeCount(0);
        review.setReplyCount(0);
        review.setStatus(1);

        reviewMapper.insert(review);
        return convertToVO(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewReplyVO replyReview(ReviewReplyRequest request, Long userId) {
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        Review review = reviewMapper.selectById(request.getReviewId());
        if (review == null || review.getStatus() == null || review.getStatus() != 1) {
            throw new BusinessException("评价不存在");
        }

        ReviewReply reply = new ReviewReply();
        reply.setReviewId(request.getReviewId());
        reply.setUserId(userId);
        reply.setParentReplyId(request.getParentReplyId());
        reply.setContent(request.getContent().trim());
        reply.setLikeCount(0);
        reply.setStatus(1);

        reviewReplyMapper.insert(reply);

        Integer replyCount = review.getReplyCount() != null ? review.getReplyCount() : 0;
        review.setReplyCount(replyCount + 1);
        reviewMapper.updateById(review);

        return convertToVO(reply);
    }

    @Override
    public List<ReviewReplyVO> getReviewReplies(Long reviewId) {
        LambdaQueryWrapper<ReviewReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewReply::getReviewId, reviewId)
                .eq(ReviewReply::getStatus, 1)
                .orderByAsc(ReviewReply::getCreatedAt);

        return reviewReplyMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long id, Long userId) {
        Review review = reviewMapper.selectById(id);
        if (review == null || review.getStatus() == null || review.getStatus() != 1) {
            throw new BusinessException("评价不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的评价");
        }
        review.setStatus(-1);
        reviewMapper.updateById(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReply(Long id, Long userId) {
        ReviewReply reply = reviewReplyMapper.selectById(id);
        if (reply == null || reply.getStatus() == null || reply.getStatus() != 1) {
            throw new BusinessException("回复不存在");
        }
        if (!reply.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的回复");
        }
        reply.setStatus(-1);
        reviewReplyMapper.updateById(reply);

        Review review = reviewMapper.selectById(reply.getReviewId());
        if (review != null) {
            int replyCount = review.getReplyCount() != null ? review.getReplyCount() : 0;
            review.setReplyCount(Math.max(0, replyCount - 1));
            reviewMapper.updateById(review);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeReview(Long reviewId, Long userId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null || review.getStatus() == null || review.getStatus() != 1) {
            throw new BusinessException("评价不存在");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType("review");
        like.setTargetId(reviewId);
        like.setCreatedAt(LocalDateTime.now());
        try {
            likeMapper.insert(like);
        } catch (DuplicateKeyException e) {
            return;
        }

        int likeCount = review.getLikeCount() != null ? review.getLikeCount() : 0;
        review.setLikeCount(likeCount + 1);
        reviewMapper.updateById(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeReview(Long reviewId, Long userId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null || review.getStatus() == null || review.getStatus() != 1) {
            throw new BusinessException("评价不存在");
        }

        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, "review")
                .eq(Like::getTargetId, reviewId);
        if (likeMapper.delete(wrapper) > 0) {
            int likeCount = review.getLikeCount() != null ? review.getLikeCount() : 0;
            review.setLikeCount(Math.max(0, likeCount - 1));
            reviewMapper.updateById(review);
        }
    }

    @Override
    public boolean isUserLikedReview(Long reviewId, Long userId) {
        if (userId == null) {
            return false;
        }

        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, "review")
                .eq(Like::getTargetId, reviewId);
        return likeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public ReviewPermissionVO getReviewPermission(Long productId, Long userId, Long orderId) {
        ReviewPermissionVO result = new ReviewPermissionVO();
        result.setCanReview(false);
        result.setHasPurchased(false);
        result.setHasReviewed(false);

        if (userId == null) {
            result.setReason("请先登录");
            return result;
        }

        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getUserId, userId)
                .eq(Order::getStatus, 3)
                .orderByDesc(Order::getCreatedAt);
        List<Order> orders = orderMapper.selectList(orderWrapper);
        if (orders.isEmpty()) {
            result.setReason("完成订单后可评价");
            return result;
        }

        if (orderId != null) {
            Order targetOrder = orders.stream()
                    .filter(order -> order.getId().equals(orderId))
                    .findFirst()
                    .orElse(null);
            if (targetOrder == null) {
                result.setReason("该订单不可评价");
                return result;
            }

            OrderItem item = findOrderItem(targetOrder.getId(), productId);
            if (item == null) {
                result.setReason("该订单中不存在此商品");
                return result;
            }

            result.setHasPurchased(true);
            LambdaQueryWrapper<Review> reviewWrapper = new LambdaQueryWrapper<>();
            reviewWrapper.eq(Review::getOrderId, targetOrder.getId())
                    .eq(Review::getProductId, productId)
                    .eq(Review::getUserId, userId)
                    .eq(Review::getStatus, 1);
            if (reviewMapper.selectCount(reviewWrapper) > 0) {
                result.setHasReviewed(true);
                result.setOrderId(targetOrder.getId());
                result.setReason("该订单商品已评价");
                return result;
            }

            result.setCanReview(true);
            result.setOrderId(targetOrder.getId());
            result.setReason("可发表评价");
            return result;
        }

        for (Order order : orders) {
            OrderItem item = findOrderItem(order.getId(), productId);
            if (item == null) {
                continue;
            }

            result.setHasPurchased(true);
            LambdaQueryWrapper<Review> reviewWrapper = new LambdaQueryWrapper<>();
            reviewWrapper.eq(Review::getOrderId, order.getId())
                    .eq(Review::getProductId, productId)
                    .eq(Review::getUserId, userId)
                    .eq(Review::getStatus, 1);
            if (reviewMapper.selectCount(reviewWrapper) > 0) {
                result.setHasReviewed(true);
                result.setReason("您已评价过该商品");
                return result;
            }

            result.setCanReview(true);
            result.setOrderId(order.getId());
            result.setReason("可发表评价");
            return result;
        }

        result.setReason("完成订单后可评价");
        return result;
    }

    private ReviewVO convertToVO(Review review) {
        ReviewVO vo = new ReviewVO();
        vo.setId(review.getId());
        vo.setProductId(review.getProductId());
        vo.setOrderId(review.getOrderId());
        vo.setUserId(review.getUserId());
        vo.setSellerId(review.getSellerId());
        vo.setRating(review.getRating());
        vo.setContent(review.getContent());
        vo.setImages(parseImages(review.getImages()));
        vo.setIsAnonymous(review.getIsAnonymous());
        vo.setLikeCount(review.getLikeCount());
        vo.setReplyCount(review.getReplyCount());
        vo.setStatus(review.getStatus());
        vo.setCreatedAt(review.getCreatedAt());
        vo.setReplies(getReviewReplies(review.getId()));

        Product product = productMapper.selectById(review.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
            vo.setProductImage(product.getImageUrl());
        }

        if (!Boolean.TRUE.equals(review.getIsAnonymous())) {
            User user = userMapper.selectById(review.getUserId());
            if (user != null) {
                ReviewVO.UserInfo userInfo = new ReviewVO.UserInfo();
                userInfo.setId(user.getId());
                userInfo.setNickname(user.getNickname());
                userInfo.setAvatar(user.getAvatar());
                vo.setUserInfo(userInfo);
            }
        }

        return vo;
    }

    private ReviewReplyVO convertToVO(ReviewReply reply) {
        ReviewReplyVO vo = new ReviewReplyVO();
        vo.setId(reply.getId());
        vo.setReviewId(reply.getReviewId());
        vo.setUserId(reply.getUserId());
        vo.setParentReplyId(reply.getParentReplyId());
        vo.setContent(reply.getContent());
        vo.setLikeCount(reply.getLikeCount());
        vo.setStatus(reply.getStatus());
        vo.setCreatedAt(reply.getCreatedAt());

        User user = userMapper.selectById(reply.getUserId());
        if (user != null) {
            ReviewReplyVO.UserInfo userInfo = new ReviewReplyVO.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatar(user.getAvatar());
            vo.setUserInfo(userInfo);
        }

        return vo;
    }

    private OrderItem findOrderItem(Long orderId, Long productId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId)
                .eq(OrderItem::getProductId, productId);
        return orderItemMapper.selectOne(wrapper);
    }

    private String toJsonString(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return JSONUtil.toJsonStr(images);
    }

    private List<String> parseImages(String images) {
        if (images == null || images.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return JSONUtil.toList(images, String.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
