import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 状态 - 只保存 userInfo
  const userInfo = ref<UserInfo | null>(null)
  
  // 计算属性
  const isLoggedIn = computed(() => !!userInfo.value)
  const userId = computed(() => userInfo.value?.id)
  const username = computed(() => userInfo.value?.username)
  const nickname = computed(() => userInfo.value?.nickname)
  const role = computed(() => userInfo.value?.role)
  const avatar = computed(() => userInfo.value?.avatar)
  const isSeller = computed(() => userInfo.value?.role === 'seller')
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const isBuyer = computed(() => userInfo.value?.role === 'buyer')
  
  // 方法
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  const login = (info: UserInfo) => {
    setUserInfo(info)
  }
  
  const logout = () => {
    userInfo.value = null
    localStorage.removeItem('userInfo')
  }
  
  // 从本地存储恢复用户信息
  const restoreUserInfo = () => {
    try {
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        userInfo.value = JSON.parse(userInfoStr)
      }
    } catch (e) {
      console.error('恢复用户信息失败', e)
      userInfo.value = null
    }
  }
  
  return {
    // 状态
    userInfo,
    // 计算属性
    isLoggedIn,
    userId,
    username,
    nickname,
    role,
    avatar,
    isSeller,
    isAdmin,
    isBuyer,
    // 方法
    setUserInfo,
    login,
    logout,
    restoreUserInfo,
  }
}, {
  persist: {
    key: 'user',
    storage: localStorage,
    paths: ['userInfo'],
  },
})
