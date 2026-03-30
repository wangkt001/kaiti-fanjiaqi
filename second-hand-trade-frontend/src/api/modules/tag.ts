import { http } from '@/api'
import type { CulturalTag } from '@/types'

/**
 * 获取标签列表
 */
export const getTagList = (params?: any) => {
  return http.get<CulturalTag[]>('/tags', { params })
}

/**
 * 获取热门标签
 */
export const getHotTags = (limit?: number) => {
  return http.get<CulturalTag[]>('/tags/hot', { params: { limit } })
}

/**
 * 获取标签详情
 */
export const getTagDetail = (id: number) => {
  return http.get<CulturalTag>(`/tags/${id}`)
}

/**
 * 获取标签下的商品
 */
export const getTagGoods = (id: number, params?: any) => {
  return http.get<any>(`/tags/${id}/products`, { params })
}
