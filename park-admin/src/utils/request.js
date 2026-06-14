import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import router from '@/router'

const service = axios.create({
  baseURL: '/', // 基础URL为'/'，因为后端接口统一以 /api/ 开头
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = store.state.user.token
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code && res.code !== 200) {
      Message({
        message: res.message || '请求失败',
        type: 'error',
        duration: 3000
      })
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    console.error('响应错误:', error)

    // 处理 401 未授权（Token 过期或无效）
    if (error.response && error.response.status === 401) {
      Message({
        message: '登录已过期，请重新登录',
        type: 'warning',
        duration: 2000
      })
      // 清除用户信息并跳转到登录页
      store.dispatch('user/logout').then(() => {
        // 检查当前是否已经在登录页面，避免重复导航
        if (router.currentRoute.path !== '/login') {
          router.push('/login')
        }
      })
      return Promise.reject(error)
    }

    const message = error.response
      ? error.response.data.message || error.response.statusText
      : '网络异常，请稍后重试'
    Message({
      message,
      type: 'error',
      duration: 3000
    })
    return Promise.reject(error)
  }
)

export default service
