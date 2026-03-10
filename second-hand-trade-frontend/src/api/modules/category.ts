import { http } from '@/api'
import type { Category } from '@/types'

/**
 * 获取分类列表
 */
export const getCategoryList = () => {
  return http.get<Category[]>('/categories')
}

/**
 * 获取分类树
 */
export const getCategoryTree = () => {
  return http.get<Category[]>('/categories/tree')
}

/**
 * 获取分类详情
 */
export const getCategoryDetail = (id: number) => {
  return http.get<Category>(`/categories/${id}`)
}

/**
 * 获取分类下的商品
 */
export const getCategoryGoods = (id: number, params?: any) => {
  return http.get<any>(`/categories/${id}/products`, { params })
}
