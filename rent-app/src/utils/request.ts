// #ifdef H5
// H5 环境走 Vite 代理，用相对路径即可
const BASE_URL = '/api'
// #endif
// #ifndef H5
// 小程序 / App 等环境的 wx.request 必须使用带协议和域名的完整 URL
// 开发环境指向本地网关；生产环境请替换为线上域名，如 'https://your-domain.com/api'
const BASE_URL = 'http://localhost:8080/api'
// #endif

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
}

interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

export function request<T = any>(options: RequestOptions): Promise<ApiResponse<T>> {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    const header: Record<string, string> = {
      'Content-Type': 'application/json',
      ...options.header,
    }
    if (token) {
      // 后端 Sa-Token 读取裸 token（token-name=Authorization），不认 Bearer 前缀
      header['Authorization'] = token
    }

    const finalUrl = BASE_URL + options.url
    console.log('[request] method=', options.method || 'GET', 'url=', finalUrl)
    uni.request({
      url: finalUrl,
      method: options.method || 'GET',
      data: options.data,
      header,
      success: (res: any) => {
        const data = res.data as ApiResponse<T>
        if (data.code === 200) {
          resolve(data)
        } else if (data.code === 1001) {
          uni.removeStorageSync('token')
          uni.navigateTo({ url: '/pages/login/login' })
          reject(new Error('请先登录'))
        } else {
          uni.showToast({ title: data.msg || '请求失败', icon: 'none' })
          reject(new Error(data.msg))
        }
      },
      fail: (err: any) => {
        uni.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      },
    })
  })
}

export const get = <T>(url: string, data?: any) => request<T>({ url, method: 'GET', data })
export const post = <T>(url: string, data?: any) => request<T>({ url, method: 'POST', data })
export const put = <T>(url: string, data?: any) => request<T>({ url, method: 'PUT', data })
export const del = <T>(url: string, data?: any) => request<T>({ url, method: 'DELETE', data })
