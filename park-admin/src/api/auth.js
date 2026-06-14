import request from '@/utils/request'

/**
 * 获取验证码
 */
export function getCaptcha() {
  return request({
    url: '/api/auth/captcha',
    method: 'get'
  })
}

/**
 * 用户登录
 * @param {Object} data
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getInfo() {
  return request({
    url: '/api/auth/info',
    method: 'get'
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}