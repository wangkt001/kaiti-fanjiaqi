package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.ReviewMapper;
import com.campus.yujianhaowu.mapper.ReviewReplyMapper;
import com.campus.yujianhaowu.mapper.UserMapper;
import com.campus.yujianhaowu.model.dto.ReviewCreateRequest;
import com.campus.yujianhaowu.model.dto.ReviewReplyRequest;
import com.campus.yujianhaowu.model.entity.Review;
import com.campus.yujianhaowu.model.entity.ReviewReply;
import com.campus.yujianhaowu.model.entity.User;
import com.campus.yujianhaowu.model.vo.ReviewVO;
import com.campus.yujianhaowu.model.vo.ReviewReplyVO;
import com.campus.yujianhaowu.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ReviewReplyMapper reviewReplyMapper;
    private final UserMapper userMapper;

    @Override
    public Page<ReviewVO> getProductReviews(Long productId, Integer current, Integer size, Integer rating) {
        Page<Review> page = new Page<>(current, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId)
                .eq(Review::getStatus, 1)
                .orderByDesc(Review::getCreatedAt);

        if (rating != null) {
            wrapper.eq(Review::getRating, rating);
        }

        Page<Review> reviewPage = reviewMapper.selectPage(page, wrapper);

        Page<ReviewVO> voPage = new Page<>(current, size, reviewPage.getTotal());
        List<ReviewVO> voList = reviewPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public ReviewVO getReviewById(Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        return convertToVO(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewVO createReview(ReviewCreateRequest request, Long userId) {
        Review review = new Review();
        review.setProductId(request.getProductId());
        review.setOrderId(request.getOrderId());
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
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
        ReviewReply reply = new ReviewReply();
        reply.setReviewId(request.getReviewId());
        reply.setUserId(userId);
        reply.setParentReplyId(request.getParentReplyId());
        reply.setContent(request.getContent());
        reply.setLikeCount(0);
        reply.setStatus(1);

        reviewReplyMapper.insert(reply);

        if (request.getParentReplyId() == null) {
            Review review = reviewMapper.selectById(request.getReviewId());
            if (review != null) {
                review.setReplyCount(review.getReplyCount() + 1);
                reviewMapper.updateById(review);
            }
        }

        return convertToVO(reply);
    }

    @Override
    public List<ReviewReplyVO> getReviewReplies(Long reviewId) {
        LambdaQueryWrapper<ReviewReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewReply::getReviewId, reviewId)
                .eq(ReviewReply::getStatus, 1)
                .orderByAsc(ReviewReply::getCreatedAt);

        List<ReviewReply> replies = reviewReplyMapper.selectList(wrapper);
        return replies.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long id, Long userId) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
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
        if (reply == null) {
            throw new BusinessException("回复不存在");
        }
        if (!reply.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的回复");
        }
        reply.setStatus(-1);
        reviewReplyMapper.updateById(reply);
    }

    @Override
    public void likeReview(Long reviewId, Long userId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        review.setLikeCount(review.getLikeCount() + 1);
        reviewMapper.updateById(review);
    }

    @Override
    public void unlikeReview(Long reviewId, Long userId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        if (review.getLikeCount() > 0) {
            review.setLikeCount(review.getLikeCount() - 1);
            reviewMapper.updateById(review);
        }
    }

    @Override
    public boolean isUserLikedReview(Long reviewId, Long userId) {
        return false;
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
        vo.setImages(review.getImages());
        vo.setIsAnonymous(review.getIsAnonymous());
        vo.setLikeCount(review.getLikeCount());
        vo.setReplyCount(review.getReplyCount());
        vo.setStatus(review.getStatus());
        vo.setCreatedAt(review.getCreatedAt());

        if (!review.getIsAnonymous()) {
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
}
