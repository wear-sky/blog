import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：附加 Authorization header
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一错误处理 + 解包 ApiResponse
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    }
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        removeToken()
        router.push('/login')
        return Promise.reject(new Error(data?.message || 'Token已过期，请重新登录'))
      }
      if (status === 403) {
        return Promise.reject(new Error(data?.message || '权限不足'))
      }
      return Promise.reject(new Error(data?.message || '请求失败'))
    }
    return Promise.reject(error)
  }
)

export default request
