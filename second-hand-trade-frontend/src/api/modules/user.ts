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
export const uploadAvatar = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return http.upload<ApiResponse<{ url: string }>>('/users/avatar', formData)
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
