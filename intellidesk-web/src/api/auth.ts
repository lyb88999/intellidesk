import request from './request'
import type { LoginRequest, LoginResponse, RegisterRequest, UserInfo } from '@/types/auth'

/**
 * 用户登录
 */
export const login = (data: LoginRequest) => {
  return request({
    url: '/user/auth/login',
    method: 'post',
    data
  }).then((res: any) => res.data as LoginResponse)
}

/**
 * 用户登出
 */
export const logout = () => {
  return request({
    url: '/user/auth/logout',
    method: 'post'
  })
}

/**
 * 用户注册
 */
export const register = (data: RegisterRequest) => {
  return request({
    url: '/user/auth/register',
    method: 'post',
    data
  }).then((res: any) => res.data as number)
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  return request({
    url: '/user/auth/info',
    method: 'get'
  }).then((res: any) => res.data as UserInfo)
}

/**
 * 修改密码
 */
export const changePassword = (data: { oldPassword: string; newPassword: string }) => {
  return request({
    url: '/user/auth/change-password',
    method: 'post',
    data
  })
}
