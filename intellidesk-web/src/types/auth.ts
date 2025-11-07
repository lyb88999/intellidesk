/**
 * 登录请求
 */
export interface LoginRequest {
  username: string
  password: string
}

/**
 * 登录响应
 */
export interface LoginResponse {
  accessToken: string
  refreshToken: string
  userInfo: UserInfo
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar?: string
  userType: number
  status: number
  roles?: string[]
  permissions?: string[]
}

/**
 * 注册请求
 */
export interface RegisterRequest {
  username: string
  password: string
  nickname: string
  email: string
  phone: string
}
