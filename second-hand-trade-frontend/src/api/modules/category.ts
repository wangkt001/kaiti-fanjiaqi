import { http } from '@/api'
import type { Category, ApiResponse } from '@/types'

/**
 * 获取分类列表
 */
export const getCategoryList = () => {
  return http.get<ApiResponse<Category[]>>('/categories')
}

/**
 * 获取分类树
 */
export const getCategoryTree = () => {
  return http.get<ApiResponse<Category[]>>('/categories/tree')
}

/**
 * 获取分类详情
 */
export const getCategoryDetail = (id: number) => {
  return http.get<ApiResponse<Category>>(`/categories/${id}`)
}

/**
 * 获取分类下的商品
 */
export const getCategoryGoods = (id: number, params?: any) => {
  return http.get<ApiResponse<any>>(`/categories/${id}/products`, { params })
}
