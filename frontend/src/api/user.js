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

/**
 * 修改密码
 */
export function changePassword(data) {
  return request({
    url: '/v1/user/password',
    method: 'put',
    data
  })
}

/**
 * 获取当前用户扩展信息
 */
export function getUserExtendedProfile() {
  return request({
    url: '/v1/user/extended-profile',
    method: 'get'
  })
}

/**
 * 更新当前用户扩展信息
 */
export function updateUserExtendedProfile(data) {
  return request({
    url: '/v1/user/extended-profile',
    method: 'put',
    data
  })
}

/**
 * 上传头像
 */
export function updateAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/v1/user/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
