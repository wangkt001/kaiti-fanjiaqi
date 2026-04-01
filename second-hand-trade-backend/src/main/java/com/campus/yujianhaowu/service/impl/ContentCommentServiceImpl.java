package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.ContentCommentMapper;
import com.campus.yujianhaowu.mapper.CulturalContentMapper;
import com.campus.yujianhaowu.mapper.UserMapper;
import com.campus.yujianhaowu.model.dto.ContentCommentCreateRequest;
import com.campus.yujianhaowu.model.entity.ContentComment;
import com.campus.yujianhaowu.model.entity.CulturalContent;
import com.campus.yujianhaowu.model.entity.User;
import com.campus.yujianhaowu.model.vo.ContentCommentVO;
import com.campus.yujianhaowu.service.ContentCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentCommentServiceImpl implements ContentCommentService {

    private final ContentCommentMapper contentCommentMapper;
    private final CulturalContentMapper culturalContentMapper;
    private final UserMapper userMapper;

    @Override
    public Page<ContentCommentVO> getContentComments(Long contentId, Integer current, Integer size) {
        Page<ContentComment> page = new Page<>(current, size);
        LambdaQueryWrapper<ContentComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContentComment::getContentId, contentId)
                .eq(ContentComment::getStatus, 1)
                .isNull(ContentComment::getParentCommentId)
                .orderByDesc(ContentComment::getCreatedAt);

        Page<ContentComment> commentPage = contentCommentMapper.selectPage(page, wrapper);

        Page<ContentCommentVO> voPage = new Page<>(current, size, commentPage.getTotal());
        List<ContentCommentVO> voList = commentPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voList.forEach(comment -> comment.setReplies(getCommentReplies(comment.getId())));
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentCommentVO createComment(ContentCommentCreateRequest request, Long userId) {
        CulturalContent culturalContent = culturalContentMapper.selectById(request.getContentId());
        if (culturalContent == null) {
            throw new BusinessException("资讯不存在");
        }

        ContentComment comment = new ContentComment();
        comment.setContentId(request.getContentId());
        comment.setUserId(userId);
        comment.setParentCommentId(request.getParentCommentId());
        comment.setContent(request.getContent());
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setStatus(1);

        contentCommentMapper.insert(comment);

        if (request.getParentCommentId() != null) {
            ContentComment parentComment = contentCommentMapper.selectById(request.getParentCommentId());
            if (parentComment != null) {
                parentComment.setReplyCount(parentComment.getReplyCount() + 1);
                contentCommentMapper.updateById(parentComment);
            }
        }

        int commentCount = culturalContent.getCommentCount() != null ? culturalContent.getCommentCount() : 0;
        culturalContent.setCommentCount(commentCount + 1);
        culturalContentMapper.updateById(culturalContent);

        return convertToVO(comment);
    }

    @Override
    public List<ContentCommentVO> getCommentReplies(Long commentId) {
        LambdaQueryWrapper<ContentComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContentComment::getParentCommentId, commentId)
                .eq(ContentComment::getStatus, 1)
                .orderByAsc(ContentComment::getCreatedAt);

        List<ContentComment> replies = contentCommentMapper.selectList(wrapper);
        return replies.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long id, Long userId) {
        ContentComment comment = contentCommentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的评论");
        }

        if (comment.getStatus() != null && comment.getStatus() == -1) {
            return;
        }

        comment.setStatus(-1);
        contentCommentMapper.updateById(comment);

        CulturalContent culturalContent = culturalContentMapper.selectById(comment.getContentId());
        if (culturalContent != null) {
            int commentCount = culturalContent.getCommentCount() != null ? culturalContent.getCommentCount() : 0;
            if (commentCount > 0) {
                culturalContent.setCommentCount(commentCount - 1);
                culturalContentMapper.updateById(culturalContent);
            }
        }

        if (comment.getParentCommentId() != null) {
            ContentComment parentComment = contentCommentMapper.selectById(comment.getParentCommentId());
            if (parentComment != null) {
                int replyCount = parentComment.getReplyCount() != null ? parentComment.getReplyCount() : 0;
                if (replyCount > 0) {
                    parentComment.setReplyCount(replyCount - 1);
                    contentCommentMapper.updateById(parentComment);
                }
            }
        }
    }

    @Override
    public void likeComment(Long commentId, Long userId) {
        ContentComment comment = contentCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        comment.setLikeCount(comment.getLikeCount() + 1);
        contentCommentMapper.updateById(comment);
    }

    @Override
    public void unlikeComment(Long commentId, Long userId) {
        ContentComment comment = contentCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            contentCommentMapper.updateById(comment);
        }
    }

    @Override
    public boolean isUserLikedComment(Long commentId, Long userId) {
        return false;
    }

    private ContentCommentVO convertToVO(ContentComment comment) {
        ContentCommentVO vo = new ContentCommentVO();
        vo.setId(comment.getId());
        vo.setContentId(comment.getContentId());
        vo.setUserId(comment.getUserId());
        vo.setParentCommentId(comment.getParentCommentId());
        vo.setContent(comment.getContent());
        vo.setLikeCount(comment.getLikeCount());
        vo.setReplyCount(comment.getReplyCount());
        vo.setStatus(comment.getStatus());
        vo.setCreatedAt(comment.getCreatedAt());

        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            ContentCommentVO.UserInfo userInfo = new ContentCommentVO.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatar(user.getAvatar());
            vo.setUserInfo(userInfo);
        }

        return vo;
    }
}
