import { http } from '@/api';

/**
 * 收藏 API
 */

export interface FavoriteProduct {
  favoriteId: number
  productId: number
  productName: string
  productImage: string | null
  price: number
  salesCount: number
  status: string
  favoriteTime: string
}

export interface FavoriteStatus {
  favorited: boolean
  count: number
}

/**
 * 添加收藏
 */
export function addFavorite(targetType: string, targetId: number) {
  return http.post('/api/favorite', {
    targetType,
    targetId,
  });
}

/**
 * 取消收藏
 */
export function unfavorite(targetType: string, targetId: number) {
  return http.delete(`/api/favorite/${targetType}/${targetId}`);
}

/**
 * 获取收藏列表
 */
export function getFavoriteList() {
  return http.get<FavoriteProduct[]>('/api/favorite/list');
}

/**
 * 获取收藏状态
 */
export function getFavoriteStatus(targetType: string, targetId: number) {
  return http.get<FavoriteStatus>('/api/favorite/status', {
    params: {
      targetType,
      targetId,
    },
  });
}

/**
 * 检查是否已收藏（简化版）
 */
export function checkFavorite(targetType: string, targetId: number) {
  return http.get<boolean>(`/api/favorite/check/${targetType}/${targetId}`);
}

/**
 * 获取用户收藏的商品列表（分页）
 */
export function getUserFavoriteProducts(params: {
  current: number
  size: number
}) {
  return http.get<any>('/api/favorite/products', {
    params,
  });
}
