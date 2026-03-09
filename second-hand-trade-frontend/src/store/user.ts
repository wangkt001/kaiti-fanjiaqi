import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isSeller = computed(() => userInfo.value?.role === 'seller')
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const isBuyer = computed(() => userInfo.value?.role === 'buyer')
  
  // 方法
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  const login = (newToken: string, info: UserInfo) => {
    setToken(newToken)
    setUserInfo(info)
  }
  
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
  
  // 从本地存储恢复状态
  const restoreFromStorage = () => {
    const storedToken = localStorage.getItem('token')
    const storedUserInfo = localStorage.getItem('userInfo')
    
    if (storedToken) {
      token.value = storedToken
    }
    
    if (storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo)
      } catch (e) {
        console.error('解析用户信息失败', e)
      }
    }
  }
  
  return {
    // 状态
    token,
    userInfo,
    // 计算属性
    isLoggedIn,
    isSeller,
    isAdmin,
    isBuyer,
    // 方法
    setToken,
    setUserInfo,
    login,
    logout,
    restoreFromStorage,
  }
}, {
  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['token', 'userInfo'],
  },
})
