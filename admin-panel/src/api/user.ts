import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
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

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export const login = (data: LoginParams) =>
  request.post('/user/login', data)

export const getUserInfo = () =>
  request.get('/user/info')

export const getUserList = (params: { page: number; size: number; role?: number; status?: number }) =>
  request.get('/user/list', { params })

export const updateUserStatus = (id: number, status: number) =>
  request.post(`/user/${id}/status`, { status })
