import request from '@/api'
import type { ApiResponse, PageResult } from '@/types'

export interface Review {
  id: number
  productId: number
  orderId: number
  userId: number
  sellerId: number
  rating: number
  content: string
  images: string[]
  isAnonymous: boolean
  likeCount: number
  replyCount: number
  status: number
  createdAt: string
  userInfo?: {
    id: number
    nickname: string
    avatar: string
  }
  replies?: ReviewReply[]
}

export interface ReviewReply {
  id: number
  reviewId: number
  userId: number
  parentReplyId: number | null
  content: string
  likeCount: number
  status: number
  createdAt: string
  userInfo?: {
    id: number
    nickname: string
    avatar: string
  }
}

export interface ReviewCreateParams {
  productId: number
  orderId: number
  rating: number
  content?: string
  images?: string[]
  isAnonymous?: boolean
}

export interface ReviewReplyParams {
  reviewId: number
  parentReplyId?: number
  content: string
}

/**
 * 获取商品评价列表
 */
export function getProductReviews(params: {
  productId: number
  current: number
  size: number
  rating?: number
}) {
  const { productId, ...rest } = params
  return request<ApiResponse<PageResult<Review>>>({
    url: `/api/reviews/product/${productId}`,
    method: 'get',
    params: rest,
  })
}

/**
 * 获取评价详情
 */
export function getReviewDetail(id: number) {
  return request<ApiResponse<Review>>({
    url: `/api/reviews/${id}`,
    method: 'get',
  })
}

/**
 * 获取评价回复列表
 */
export function getReviewReplies(id: number) {
  return request<ApiResponse<ReviewReply[]>>({
    url: `/api/reviews/${id}/replies`,
    method: 'get',
  })
}

/**
 * 创建评价
 */
export function createReview(data: ReviewCreateParams) {
  return request<ApiResponse<Review>>({
    url: '/api/reviews',
    method: 'post',
    data,
  })
}

/**
 * 回复评价
 */
export function replyReview(data: ReviewReplyParams) {
  return request<ApiResponse<ReviewReply>>({
    url: '/api/reviews/reply',
    method: 'post',
    data,
  })
}

/**
 * 删除评价
 */
export function deleteReview(id: number) {
  return request<ApiResponse<void>>({
    url: `/api/reviews/${id}`,
    method: 'delete',
  })
}

/**
 * 删除回复
 */
export function deleteReply(id: number) {
  return request<ApiResponse<void>>({
    url: `/api/reviews/reply/${id}`,
    method: 'delete',
  })
}

/**
 * 点赞评价
 */
export function likeReview(id: number) {
  return request<ApiResponse<void>>({
    url: `/api/reviews/${id}/like`,
    method: 'post',
  })
}

/**
 * 取消点赞评价
 */
export function unlikeReview(id: number) {
  return request<ApiResponse<void>>({
    url: `/api/reviews/${id}/like`,
    method: 'delete',
  })
}

/**
 * 检查是否已点赞
 */
export function checkReviewLiked(id: number) {
  return request<ApiResponse<boolean>>({
    url: `/api/reviews/${id}/liked`,
    method: 'get',
  })
}
