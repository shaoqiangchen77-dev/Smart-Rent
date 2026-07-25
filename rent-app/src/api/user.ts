import { post, get } from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  phone: string
  role: number
}

export interface UserInfo {
  id: number
  username: string
  phone: string
  email: string | null
  avatar: string | null
  nickname: string | null
  role: number
  status: number
}

export const login = (data: LoginParams) => post<{ token: string }>('/user/login', data)
export const register = (data: RegisterParams) => post('/user/register', data)
export const getUserInfo = () => get<UserInfo>('/user/info')
export const updateUserInfo = (data: Partial<UserInfo>) => post('/user/update', data)
