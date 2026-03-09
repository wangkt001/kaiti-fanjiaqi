import { http } from '@/api'
import type { CulturalTag, ApiResponse } from '@/types'

/**
 * 获取标签列表
 */
export const getTagList = (params?: any) => {
  return http.get<ApiResponse<CulturalTag[]>>('/tags', { params })
}

/**
 * 获取热门标签
 */
export const getHotTags = (limit?: number) => {
  return http.get<ApiResponse<CulturalTag[]>>('/tags/hot', { params: { limit } })
}

/**
 * 获取标签详情
 */
export const getTagDetail = (id: number) => {
  return http.get<ApiResponse<CulturalTag>>(`/tags/${id}`)
}

/**
 * 获取标签下的商品
 */
export const getTagGoods = (id: number, params?: any) => {
  return http.get<ApiResponse<any>>(`/tags/${id}/products`, { params })
}
