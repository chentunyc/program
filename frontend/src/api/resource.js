import request from '@/utils/request'

/**
 * 分页查询资源列表
 */
export function getResourcePage(params) {
  return request({
    url: '/v1/resource/page',
    method: 'get',
    params
  })
}

/**
 * 获取资源详情
 */
export function getResourceById(id) {
  return request({
    url: `/v1/resource/${id}`,
    method: 'get'
  })
}

/**
 * 创建资源
 */
export function createResource(data) {
  return request({
    url: '/v1/resource',
    method: 'post',
    data
  })
}

/**
 * 更新资源
 */
export function updateResource(data) {
  return request({
    url: '/v1/resource',
    method: 'put',
    data
  })
}

/**
 * 删除资源
 */
export function deleteResource(id) {
  return request({
    url: `/v1/resource/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除资源
 */
export function batchDeleteResource(ids) {
  return request({
    url: '/v1/resource/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 发布资源
 */
export function publishResource(id) {
  return request({
    url: `/v1/resource/${id}/publish`,
    method: 'put'
  })
}

/**
 * 下架资源
 */
export function offlineResource(id) {
  return request({
    url: `/v1/resource/${id}/offline`,
    method: 'put'
  })
}

/**
 * 获取资源统计信息
 */
export function getResourceStats() {
  return request({
    url: '/v1/resource/stats',
    method: 'get'
  })
}

/**
 * 获取热门资源
 */
export function getHotResources(limit = 10) {
  return request({
    url: '/v1/resource/hot',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取最新资源
 */
export function getLatestResources(limit = 10) {
  return request({
    url: '/v1/resource/latest',
    method: 'get',
    params: { limit }
  })
}

/**
 * 记录下载
 */
export function recordDownload(id) {
  return request({
    url: `/v1/resource/${id}/download`,
    method: 'post'
  })
}

/**
 * 更新资源共享状态
 */
export function updateResourceShare(id, isShared) {
  return request({
    url: `/v1/resource/${id}/share`,
    method: 'put',
    params: { isShared }
  })
}

/**
 * 上传资源文件
 */
export function uploadResourceFile(file, resourceType) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('resourceType', resourceType)
  return request({
    url: '/v1/resource/upload/file',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传封面图片
 */
export function uploadCoverImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/v1/resource/upload/cover',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
