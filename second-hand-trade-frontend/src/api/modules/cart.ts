import request from '@/api'

export interface CartItem {
  id: number
  userId: number
  productId: number
  quantity: number
  selected: boolean
  product: {
    id: number
    name: string
    description: string
    price: number
    mainImage: string | null
    stock: number
    status: string
  }
}

export interface CartCheckoutInfo {
  totalAmount: number
  freightAmount: number
  paymentAmount: number
}

/**
 * 获取购物车列表
 */
export function getCartItems() {
  return request<CartItem[]>({
    url: '/cart',
    method: 'get',
  })
}

/**
 * 获取购物车商品数量
 */
export function getCartCount() {
  return request<number>({
    url: '/cart/count',
    method: 'get',
  })
}

/**
 * 添加到购物车
 */
export function addToCart(productId: number, quantity: number) {
  return request<CartItem>({
    url: '/cart/add',
    method: 'post',
    params: {
      productId,
      quantity,
    },
  })
}

/**
 * 更新购物车商品数量
 */
export function updateQuantity(productId: number, quantity: number) {
  return request<void>({
    url: '/cart/update',
    method: 'put',
    params: {
      productId,
      quantity,
    },
  })
}

/**
 * 选中/取消选中购物车商品
 */
export function selectItem(productId: number, selected: boolean) {
  return request<void>({
    url: '/cart/select',
    method: 'put',
    params: {
      productId,
      selected,
    },
  })
}

/**
 * 全选/取消全选
 */
export function selectAll(selected: boolean) {
  return request<void>({
    url: '/cart/select-all',
    method: 'put',
    params: {
      selected,
    },
  })
}

/**
 * 删除购物车商品
 */
export function deleteItem(productId: number) {
  return request<void>({
    url: '/cart/delete',
    method: 'delete',
    params: {
      productId,
    },
  })
}

/**
 * 清空购物车
 */
export function clearCart() {
  return request<void>({
    url: '/cart/clear',
    method: 'delete',
  })
}

/**
 * 获取结算信息
 */
export function checkout(cartItemIds: number[]) {
  return request<CartCheckoutInfo>({
    url: '/cart/checkout',
    method: 'post',
    params: {
      cartItemIds,
    },
  })
}
