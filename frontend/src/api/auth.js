import request from '@/utils/request'

/**
 * 登录
 */
export function login(data) {
  return request({
    url: '/v1/auth/login',
    method: 'post',
    data
  })
}

/**
 * 登出
 */
export function logout() {
  return request({
    url: '/v1/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/v1/auth/user-info',
    method: 'get'
  })
}

/**
 * 刷新Token
 */
export function refreshToken() {
  return request({
    url: '/v1/auth/refresh-token',
    method: 'post'
  })
}
