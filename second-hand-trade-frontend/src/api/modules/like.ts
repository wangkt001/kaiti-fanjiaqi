import request from '@/api'
import type { ApiResponse } from '@/types'

export interface LikeStatus {
  count: number
  liked: boolean
}

/**
 * 点赞
 * @param targetType 目标类型（review/reply/product/content/comment）
 * @param targetId 目标 ID
 */
export function like(targetType: string, targetId: number) {
  return request<ApiResponse<void>>({
    url: `/api/likes/${targetType}/${targetId}`,
    method: 'post',
  })
}

/**
 * 取消点赞
 */
export function unlike(targetType: string, targetId: number) {
  return request<ApiResponse<void>>({
    url: `/api/likes/${targetType}/${targetId}`,
    method: 'delete',
  })
}

/**
 * 获取点赞状态
 */
export function getLikeStatus(targetType: string, targetId: number) {
  return request<ApiResponse<LikeStatus>>({
    url: `/api/likes/${targetType}/${targetId}/status`,
    method: 'get',
  })
}

/**
 * 获取点赞数量
 */
export function getLikeCount(targetType: string, targetId: number) {
  return request<ApiResponse<number>>({
    url: `/api/likes/${targetType}/${targetId}/count`,
    method: 'get',
  })
}
