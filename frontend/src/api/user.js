import request from '@/utils/request'

/**
 * 获取当前用户资料
 */
export function getCurrentUserProfile() {
  return request({
    url: '/v1/user/profile',
    method: 'get'
  })
}

/**
 * 更新当前用户资料
 */
export function updateCurrentUserProfile(data) {
  return request({
    url: '/v1/user/profile',
    method: 'put',
    data
  })
}

/**
 * 根据ID获取用户资料
 */
export function getUserProfileById(userId) {
  return request({
    url: `/v1/user/profile/${userId}`,
    method: 'get'
  })
}
