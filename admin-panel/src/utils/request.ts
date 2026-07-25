import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token) {
      // 后端 Sa-Token 的 token-name 为 Authorization，直接读取裸 token，不兼容 "Bearer " 前缀
      config.headers.Authorization = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    if (res.code === 1001) {
      localStorage.removeItem('token')
      router.push('/login')
      ElMessage.error('请先登录')
      return Promise.reject(new Error('请先登录'))
    }
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg))
  },
  (error) => {
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default service
