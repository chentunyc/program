import request from '@/utils/request'

/**
 * 添加收藏
 * @param {Object} data - { resourceType, resourceId }
 */
export function addFavorite(data) {
  return request({
    url: '/v1/user/favorites',
    method: 'post',
    data
  })
}

/**
 * 取消收藏
 * @param {string} resourceType - 资源类型
 * @param {number} resourceId - 资源ID
 */
export function removeFavorite(resourceType, resourceId) {
  return request({
    url: `/v1/user/favorites/${resourceType}/${resourceId}`,
    method: 'delete'
  })
}

/**
 * 检查是否已收藏
 * @param {string} resourceType - 资源类型
 * @param {number} resourceId - 资源ID
 */
export function checkFavorite(resourceType, resourceId) {
  return request({
    url: `/v1/user/favorites/check/${resourceType}/${resourceId}`,
    method: 'get'
  })
}

/**
 * 获取收藏列表（分页）
 * @param {Object} params - { pageNum, pageSize, resourceType }
 */
export function getFavoriteList(params) {
  return request({
    url: '/v1/user/favorites',
    method: 'get',
    params
  })
}

/**
 * 获取收藏统计
 */
export function getFavoriteStats() {
  return request({
    url: '/v1/user/favorites/stats',
    method: 'get'
  })
}
