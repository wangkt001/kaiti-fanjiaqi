import request from '@/utils/request';

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
  return request({
    url: '/api/favorite',
    method: 'post',
    data: {
      targetType,
      targetId,
    },
  });
}

/**
 * 取消收藏
 */
export function unfavorite(targetType: string, targetId: number) {
  return request({
    url: `/api/favorite/${targetId}`,
    method: 'delete',
  });
}

/**
 * 获取收藏列表
 */
export function getFavoriteList() {
  return request<FavoriteProduct[]>({
    url: '/api/favorite/list',
    method: 'get',
  });
}

/**
 * 获取收藏状态
 */
export function getFavoriteStatus(targetType: string, targetId: number) {
  return request<FavoriteStatus>({
    url: `/api/favorite/status`,
    method: 'get',
    params: {
      targetType,
      targetId,
    },
  });
}

/**
 * 检查是否已收藏（简化版）
 */
export function checkFavorite(targetId: number) {
  return request<boolean>({
    url: `/api/favorite/check/${targetId}`,
    method: 'get',
  });
}
