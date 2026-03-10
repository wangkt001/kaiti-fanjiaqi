import request from '@/api'
import type { ApiResponse, PageResult } from '@/types'

export interface ContentComment {
  id: number
  contentId: number
  userId: number
  parentCommentId: number | null
  content: string
  likeCount: number
  replyCount: number
  status: number
  createdAt: string
  userInfo?: {
    id: number
    nickname: string
    avatar: string
  }
  showReply?: boolean
  replies?: ContentComment[]
}

export interface ContentCommentCreateParams {
  contentId: number
  parentCommentId?: number
  content: string
}

/**
 * 获取资讯评论列表
 */
export function getContentComments(params: {
  contentId: number
  current: number
  size: number
}) {
  return request<ApiResponse<PageResult<ContentComment>>>({
    url: `/content-comments/content/${params.contentId}`,
    method: 'get',
    params: {
      current: params.current,
      size: params.size,
    },
  })
}

/**
 * 创建评论
 */
export function createComment(data: ContentCommentCreateParams) {
  return request<ApiResponse<ContentComment>>({
    url: '/content-comments',
    method: 'post',
    data,
  })
}

/**
 * 获取评论回复列表
 */
export function getCommentReplies(commentId: number) {
  return request<ApiResponse<ContentComment[]>>({
    url: `/content-comments/${commentId}/replies`,
    method: 'get',
  })
}

/**
 * 删除评论
 */
export function deleteComment(id: number) {
  return request<ApiResponse<void>>({
    url: `/content-comments/${id}`,
    method: 'delete',
  })
}

/**
 * 点赞评论
 */
export function likeComment(id: number) {
  return request<ApiResponse<void>>({
    url: `/api/content-comments/${id}/like`,
    method: 'post',
  })
}

/**
 * 取消点赞评论
 */
export function unlikeComment(id: number) {
  return request<ApiResponse<void>>({
    url: `/api/content-comments/${id}/like`,
    method: 'delete',
  })
}
