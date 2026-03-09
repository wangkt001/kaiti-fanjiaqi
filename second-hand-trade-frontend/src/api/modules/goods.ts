import { http } from '@/api'
import type { Goods, PageParams, PageResult, ApiResponse } from '@/types'

/**
 * 获取商品列表
 */
export const getGoodsList = (params: PageParams & { [key: string]: any }) => {
  return http.get<ApiResponse<PageResult<Goods>>>('/products', { params })
}

/**
 * 获取商品详情
 */
export const getGoodsDetail = (id: number) => {
  return http.get<ApiResponse<Goods>>(`/products/${id}`)
}

/**
 * 发布商品
 */
export const createGoods = (data: any) => {
  return http.post<ApiResponse<Goods>>('/products', data)
}

/**
 * 更新商品
 */
export const updateGoods = (id: number, data: any) => {
  return http.put<ApiResponse>(`/products/${id}`, data)
}

/**
 * 删除商品
 */
export const deleteGoods = (id: number) => {
  return http.delete<ApiResponse>(`/products/${id}`)
}

/**
 * 上下架商品
 */
export const toggleGoodsStatus = (id: number, status: string) => {
  return http.put<ApiResponse>(`/products/${id}/status`, { status })
}

/**
 * 获取卖家商品列表
 */
export const getSellerGoodsList = (params: PageParams & { [key: string]: any }) => {
  return http.get<ApiResponse<PageResult<Goods>>>('/products/seller', { params })
}

/**
 * 搜索商品
 */
export const searchGoods = (keyword: string, params?: PageParams) => {
  return http.get<ApiResponse<PageResult<Goods>>>('/products/search', {
    params: { keyword, ...params },
  })
}

/**
 * 获取推荐商品
 */
export const getRecommendGoods = (limit?: number) => {
  return http.get<ApiResponse<Goods[]>>('/products/recommend', { params: { limit } })
}

/**
 * 获取热门商品
 */
export const getHotGoods = (limit?: number) => {
  return http.get<ApiResponse<Goods[]>>('/products/hot', { params: { limit } })
}

/**
 * 获取最新发布商品
 */
export const getNewGoods = (limit?: number) => {
  return http.get<ApiResponse<Goods[]>>('/products/new', { params: { limit } })
}
