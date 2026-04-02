package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.model.dto.ReviewCreateRequest;
import com.campus.yujianhaowu.model.dto.ReviewReplyRequest;
import com.campus.yujianhaowu.model.vo.ReviewPermissionVO;
import com.campus.yujianhaowu.model.vo.ReviewVO;
import com.campus.yujianhaowu.model.vo.ReviewReplyVO;

import java.util.List;

public interface ReviewService {

    Page<ReviewVO> getProductReviews(Long productId, Integer current, Integer size, Integer rating);

    ReviewVO getReviewById(Long id);

    ReviewVO createReview(ReviewCreateRequest request, Long userId);

    ReviewReplyVO replyReview(ReviewReplyRequest request, Long userId);

    List<ReviewReplyVO> getReviewReplies(Long reviewId);

    Page<ReviewVO> getUserReviews(Long userId, Integer current, Integer size);

    void deleteReview(Long id, Long userId);

    void deleteReply(Long id, Long userId);

    void likeReview(Long reviewId, Long userId);

    void unlikeReview(Long reviewId, Long userId);

    boolean isUserLikedReview(Long reviewId, Long userId);

    ReviewPermissionVO getReviewPermission(Long productId, Long userId, Long orderId);
}
