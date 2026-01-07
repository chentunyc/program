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

/**
 * 用户注册
 */
export function register(data) {
  return request({
    url: '/v1/auth/register',
    method: 'post',
    data
  })
}

/**
 * 检查用户名是否可用
 */
export function checkUsername(username) {
  return request({
    url: '/v1/auth/check-username',
    method: 'get',
    params: { username }
  })
}
