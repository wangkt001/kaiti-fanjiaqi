import { http } from '@/api'
import type { PageParams, PageResult, ApiResponse } from '@/types'

export interface Review {
  id: number
  productId: number
  orderId: number
  userId: number
  userInfo?: {
    nickname: string
    avatar: string
  }
  sellerId: number
  rating: number
  content: string
  images?: string[]
  isAnonymous: boolean
  likeCount: number
  replyCount: number
  status: number
  createdAt: string
  replies?: ReviewReply[]
}

export interface ReviewReply {
  id: number
  reviewId: number
  userId: number
  userInfo?: {
    nickname: string
    avatar: string
  }
  parentReplyId?: number
  content: string
  likeCount: number
  createdAt: string
}

export interface ReviewCreateParams {
  orderId: number;
  productId: number;
  rating: number;
  content: string;
  images?: string[];
  isAnonymous: boolean;
}

/**
 * 获取商品评价列表
 */
export const getProductReviews = (params: PageParams & { productId: number; rating?: number }) => {
  const { productId, ...queryParams } = params
  return http.get<ApiResponse<PageResult<Review>>>(`/reviews/product/${productId}`, { params: queryParams })
}

/**
 * 点赞评价
 */
export const likeReview = (id: number) => {
  return http.post<ApiResponse>(`/reviews/${id}/like`)
}

/**
 * 取消点赞评价
 */
export const unlikeReview = (id: number) => {
  return http.delete<ApiResponse>(`/reviews/${id}/like`)
}

/**
 * 回复评价
 */
export const replyReview = (data: { reviewId: number; content: string; parentReplyId?: number }) => {
  return http.post<ApiResponse>('/reviews/reply', data)
}

/**
 * 发布评价
 */
export const createReview = (data: ReviewCreateParams) => {
  return http.post<ApiResponse>('/reviews', data)
}
