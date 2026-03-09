import request from '@/api'
import type { ApiResponse, PageResult } from '@/types'
import type { Product } from '@/types'

export interface FavoriteStatus {
  count: number
  favorited: boolean
}

/**
 * 收藏
 * @param targetType 目标类型（product/content）
 * @param targetId 目标 ID
 */
export function favorite(targetType: string, targetId: number) {
  return request<ApiResponse<void>>({
    url: `/api/favorites/${targetType}/${targetId}`,
    method: 'post',
  })
}

/**
 * 取消收藏
 */
export function unfavorite(targetType: string, targetId: number) {
  return request<ApiResponse<void>>({
    url: `/api/favorites/${targetType}/${targetId}`,
    method: 'delete',
  })
}

/**
 * 获取收藏状态
 */
export function getFavoriteStatus(targetType: string, targetId: number) {
  return request<ApiResponse<FavoriteStatus>>({
    url: `/api/favorites/${targetType}/${targetId}/status`,
    method: 'get',
  })
}

/**
 * 获取收藏数量
 */
export function getFavoriteCount(targetType: string, targetId: number) {
  return request<ApiResponse<number>>({
    url: `/api/favorites/${targetType}/${targetId}/count`,
    method: 'get',
  })
}

/**
 * 获取用户收藏的商品列表
 */
export function getUserFavoriteProducts(params: {
  current: number
  size: number
}) {
  return request<ApiResponse<PageResult<Product>>>({
    url: '/api/favorites/products',
    method: 'get',
    params,
  })
}
