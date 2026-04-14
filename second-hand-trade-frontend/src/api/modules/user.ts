import { http } from '@/api'
import type { UserInfo, LoginParams, RegisterParams, ApiResponse } from '@/types'

/**
 * 用户登录
 */
export const login = (data: LoginParams) => {
  return http.post<ApiResponse<{ token: string; userInfo: UserInfo }>>('/auth/login', data)
}

/**
 * 用户注册
 */
export const register = (data: RegisterParams) => {
  return http.post<ApiResponse>('/auth/register', data)
}

/**
 * 忘记密码
 */
export const forgotPassword = (data: {
  phone: string
  nickname: string
  newPassword: string
}) => {
  return http.post<ApiResponse>('/auth/forgot-password', data)
}

/**
 * 用户登出
 */
export const logout = () => {
  return http.post<ApiResponse>('/auth/logout')
}

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => {
  return http.get<ApiResponse<UserInfo>>('/users/profile')
}

/**
 * 更新用户信息
 */
export const updateProfile = (data: Partial<UserInfo>) => {
  return http.put<ApiResponse>('/users/profile', data)
}

/**
 * 上传头像
 */
export const uploadAvatar = (avatarUrl: string) => {
  return http.post<ApiResponse>('/users/avatar', { avatarUrl })
}

export const uploadShopLogo = (shopLogoUrl: string) => {
  return http.post<ApiResponse>('/users/shop-logo', { shopLogoUrl })
}

export const updateShopInfo = (shopName: string, shopDescription: string) => {
  return http.put<ApiResponse>('/users/shop-info', { shopName, shopDescription })
}

export const uploadImageFile = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return http.upload<{ url: string; filename: string }>('/files/upload', formData)
}

/**
 * 修改密码
 */
export const changePassword = (data: { oldPassword: string; newPassword: string }) => {
  return http.put<ApiResponse>('/users/password', data)
}

/**
 * 申请成为卖家
 */
export const applySeller = (sellerInfo: any) => {
  return http.post<ApiResponse>('/users/apply-seller', sellerInfo)
}

/**
 * 获取卖家状态
 */
export const getSellerStatus = () => {
  return http.get<ApiResponse<{ status: string; remark?: string }>>('/users/seller-status')
}

/**
 * 获取卖家申请信息
 */
export const getSellerApplyInfo = () => {
  return http.get<ApiResponse<any>>('/users/seller-apply-info')
}

/**
 * 获取店铺公开信息
 */
export const getSellerPublicProfile = (id: number) => {
  return http.get<ApiResponse<UserInfo>>(`/users/public/${id}`)
}

/**
 * 后台管理：获取用户列表
 */
export const listUsers = (current: number, size: number) => {
  return http.get<ApiResponse<PageResult<UserInfo>>>('/users/admin/list', {
    params: { current, size }
  })
}

/**
 * 后台管理：更新用户状态
 */
export const updateUserStatus = (id: number, status: number) => {
  return http.put<ApiResponse>(`/users/admin/status/${id}`, null, {
    params: { status }
  })
}

/**
 * 后台管理：删除用户
 */
export const deleteUser = (id: number) => {
  return http.delete<ApiResponse>(`/users/admin/${id}`)
}

/**
 * 获取购物车数量
 */
export const getCartCount = () => {
  return http.get<number>('/cart/count')
}

/**
 * 获取待审核卖家列表（管理员）
 */
export const listPendingSellers = (current: number, size: number) => {
  return http.get<ApiResponse<PageResult<UserInfo>>>('/users/admin/pending-sellers', {
    params: { current, size }
  })
}

/**
 * 审核卖家申请（管理员）
 */
export const auditSeller = (id: number, status: string, remark?: string) => {
  return http.post<ApiResponse>(`/users/admin/audit-seller/${id}`, null, {
    params: { status, remark }
  })
}
