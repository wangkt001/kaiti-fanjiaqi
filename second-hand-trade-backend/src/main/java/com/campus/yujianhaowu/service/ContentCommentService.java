package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.model.dto.ContentCommentCreateRequest;
import com.campus.yujianhaowu.model.vo.ContentCommentVO;

import java.util.List;

public interface ContentCommentService {

    Page<ContentCommentVO> getContentComments(Long contentId, Integer current, Integer size);

    ContentCommentVO createComment(ContentCommentCreateRequest request, Long userId);

    List<ContentCommentVO> getCommentReplies(Long commentId);

    void deleteComment(Long id, Long userId);

    void likeComment(Long commentId, Long userId);

    void unlikeComment(Long commentId, Long userId);

    boolean isUserLikedComment(Long commentId, Long userId);
}
