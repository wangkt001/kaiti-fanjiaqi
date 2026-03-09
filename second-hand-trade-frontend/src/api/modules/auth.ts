import { http } from '@/api';

/**
 * 认证 API
 */

export interface LoginParams {
  username: string;
  password: string;
}

export interface RegisterParams {
  username: string;
  password: string;
  nickname: string;
  phone?: string;
  role?: string;
}

/**
 * 登录
 */
export function login(data: LoginParams) {
  return http.post('/api/auth/login', data);
}

/**
 * 注册
 */
export function register(data: RegisterParams) {
  return http.post('/api/auth/register', data);
}

/**
 * 登出
 */
export function logout() {
  return http.post('/api/auth/logout');
}
