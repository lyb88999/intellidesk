import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getUserInfo } from '@/api/auth'
import type { LoginRequest, LoginResponse, UserInfo } from '@/types/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const refreshToken = ref<string>(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<UserInfo | null>(null)

  // 登录
  const loginAction = async (loginData: LoginRequest): Promise<void> => {
    const response = await login(loginData)
    token.value = response.accessToken
    refreshToken.value = response.refreshToken
    userInfo.value = response.userInfo

    localStorage.setItem('token', response.accessToken)
    localStorage.setItem('refreshToken', response.refreshToken)
  }

  // 登出
  const logoutAction = async (): Promise<void> => {
    try {
      await logout()
    } finally {
      token.value = ''
      refreshToken.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
    }
  }

  // 获取用户信息
  const getUserInfoAction = async (): Promise<void> => {
    const info = await getUserInfo()
    userInfo.value = info
  }

  return {
    token,
    refreshToken,
    userInfo,
    loginAction,
    logoutAction,
    getUserInfoAction
  }
})
