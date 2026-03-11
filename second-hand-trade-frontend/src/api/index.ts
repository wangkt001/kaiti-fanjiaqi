import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import type { ApiResponse } from '@/types'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL + '/api' || 'http://localhost:8080/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 使用 userStore 获取 userId
    const userStore = useUserStore()
    const uid = userStore.userId
    
    console.log('🔍 [请求拦截器]')
    console.log('  URL:', config.url)
    console.log('  userId from store:', uid)
    console.log('  userInfo:', userStore.userInfo)
    
    // 添加 userId 到请求头
    if (uid) {
      config.headers['X-User-Id'] = String(uid)
      console.log('  ✅ 已添加 X-User-Id:', uid)
    } else {
      console.log('  ⚠️ 未登录或 userId 为空')
    }
    
    console.log('  请求头:', config.headers)
    console.log('------------------')
    
    // 添加请求时间戳（防止缓存）
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now(),
      }
    }
    
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data
    
    // 如果返回的业务状态码不是 200，说明接口有错误
    if (res.code !== 200) {
      // 401: 未授权
      if (res.code === 401) {
        ElMessageBox.confirm('登录状态已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          const userStore = useUserStore()
          userStore.logout()
          location.reload()
        })
        return Promise.reject(new Error(res.message || 'Error'))
      }
      
      // 其他错误
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    return res
  },
  (error) => {
    console.error('响应错误:', error)
    
    let message = '网络错误，请稍后重试'
    
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请先登录'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务不可用'
          break
        case 504:
          message = '网关超时'
          break
        default:
          message = `连接错误${error.response.status}`
      }
    } else if (error.request) {
      message = '无法连接到服务器'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 导出请求方法
export const http = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, config).then((res) => res.data as T)
  },

  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config).then((res) => res.data as T)
  },

  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config).then((res) => res.data as T)
  },

  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, config).then((res) => res.data as T)
  },

  upload<T = any>(url: string, data: FormData, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, {
      ...config,
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }).then((res) => res.data as T)
  },

  download(url: string, config?: AxiosRequestConfig): Promise<Blob> {
    return service.get(url, {
      ...config,
      responseType: 'blob',
    }).then((res) => res.data as Blob)
  },
}

export default service
