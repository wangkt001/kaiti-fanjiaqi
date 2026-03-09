import request from '@/api'
import type { ApiResponse, PageResult } from '@/types'

export interface Order {
  id: number
  orderNo: string
  userId: number
  sellerId: number
  totalAmount: number
  paymentAmount: number
  freightAmount: number
  discountAmount: number
  status: number
  paymentType: number | null
  paymentTime: string | null
  deliveryType: string | null
  deliveryNo: string | null
  deliveryTime: string | null
  receiveTime: string | null
  remark: string | null
  receiverName: string | null
  receiverPhone: string | null
  receiverAddress: string | null
  createdAt: string
  items?: OrderItem[]
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  productImage: string | null
  price: number
  quantity: number
  totalPrice: number
}

export interface OrderCreateParams {
  cartItemIds: number[]
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  remark?: string
  paymentType?: number
}

export interface OrderCheckoutInfo {
  items: OrderItem[]
  totalAmount: number
  freightAmount: number
  discountAmount: number
  paymentAmount: number
}

/**
 * 创建订单
 */
export function createOrder(data: OrderCreateParams) {
  return request<ApiResponse<string>>({
    url: '/api/orders',
    method: 'post',
    data,
  })
}

/**
 * 获取订单列表
 */
export function getOrderList(params: {
  status?: number
  current: number
  size: number
}) {
  return request<ApiResponse<PageResult<Order>>>({
    url: '/api/orders',
    method: 'get',
    params,
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id: number) {
  return request<ApiResponse<Order>>({
    url: `/api/orders/${id}`,
    method: 'get',
  })
}

/**
 * 取消订单
 */
export function cancelOrder(id: number) {
  return request<ApiResponse<void>>({
    url: `/api/orders/${id}/cancel`,
    method: 'put',
  })
}

/**
 * 确认收货
 */
export function confirmOrder(id: number) {
  return request<ApiResponse<void>>({
    url: `/api/orders/${id}/confirm`,
    method: 'put',
  })
}

/**
 * 删除订单
 */
export function deleteOrder(id: number) {
  return request<ApiResponse<void>>({
    url: `/api/orders/${id}`,
    method: 'delete',
  })
}

/**
 * 获取结算信息
 */
export function getCheckoutInfo(cartItemIds: number[]) {
  return request<ApiResponse<OrderCheckoutInfo>>({
    url: '/api/cart/checkout',
    method: 'post',
    params: {
      cartItemIds,
    },
  })
}

/**
 * 模拟支付
 */
export function payOrder(orderNo: string, paymentType: number) {
  return request<ApiResponse<void>>({
    url: '/api/orders/pay',
    method: 'post',
    data: {
      orderNo,
      paymentType,
    },
  })
}
