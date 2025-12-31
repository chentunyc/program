import request from '@/utils/request'

/**
 * 分页查询新闻列表
 */
export function getNewsPage(params) {
  return request({
    url: '/v1/news/page',
    method: 'get',
    params
  })
}

/**
 * 获取新闻详情
 */
export function getNewsById(id) {
  return request({
    url: `/v1/news/${id}`,
    method: 'get'
  })
}

/**
 * 保存新闻(新增/更新)
 */
export function saveNews(data) {
  return request({
    url: '/v1/news/save',
    method: 'post',
    data
  })
}

/**
 * 删除新闻
 */
export function deleteNews(id) {
  return request({
    url: `/v1/news/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除新闻
 */
export function batchDeleteNews(ids) {
  return request({
    url: '/v1/news/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 发布新闻
 */
export function publishNews(id) {
  return request({
    url: `/v1/news/${id}/publish`,
    method: 'put'
  })
}

/**
 * 撤回新闻
 */
export function withdrawNews(id) {
  return request({
    url: `/v1/news/${id}/withdraw`,
    method: 'put'
  })
}

/**
 * 置顶/取消置顶
 */
export function toggleTop(id, isTop) {
  return request({
    url: `/v1/news/${id}/top`,
    method: 'put',
    params: { isTop }
  })
}

/**
 * 推荐/取消推荐
 */
export function toggleRecommend(id, isRecommend) {
  return request({
    url: `/v1/news/${id}/recommend`,
    method: 'put',
    params: { isRecommend }
  })
}
