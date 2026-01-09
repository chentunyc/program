import request from '@/utils/request'

/**
 * ==================== 实训项目管理 API ====================
 */

/**
 * 分页查询项目列表
 */
export function getProjectPage(params) {
  return request({
    url: '/v1/admin/training/project/page',
    method: 'get',
    params
  })
}

/**
 * 获取项目详情
 */
export function getProjectById(id) {
  return request({
    url: `/v1/admin/training/project/${id}`,
    method: 'get'
  })
}

/**
 * 创建项目
 */
export function createProject(data) {
  return request({
    url: '/v1/admin/training/project',
    method: 'post',
    data
  })
}

/**
 * 更新项目
 */
export function updateProject(data) {
  return request({
    url: '/v1/admin/training/project',
    method: 'put',
    data
  })
}

/**
 * 删除项目
 */
export function deleteProject(id) {
  return request({
    url: `/v1/admin/training/project/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除项目
 */
export function batchDeleteProject(ids) {
  return request({
    url: '/v1/admin/training/project/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 开始项目
 */
export function startProject(id) {
  return request({
    url: `/v1/admin/training/project/${id}/start`,
    method: 'put'
  })
}

/**
 * 结束项目
 */
export function endProject(id) {
  return request({
    url: `/v1/admin/training/project/${id}/end`,
    method: 'put'
  })
}

/**
 * 获取我负责的项目列表
 */
export function getMyProjects() {
  return request({
    url: '/v1/admin/training/project/my',
    method: 'get'
  })
}

/**
 * ==================== 实训任务管理 API ====================
 */

/**
 * 分页查询任务列表
 */
export function getTaskPage(params) {
  return request({
    url: '/v1/admin/training/task/page',
    method: 'get',
    params
  })
}

/**
 * 根据项目ID获取任务列表
 */
export function getTasksByProjectId(projectId) {
  return request({
    url: `/v1/admin/training/task/list/${projectId}`,
    method: 'get'
  })
}

/**
 * 获取任务详情
 */
export function getTaskById(id) {
  return request({
    url: `/v1/admin/training/task/${id}`,
    method: 'get'
  })
}

/**
 * 创建任务
 */
export function createTask(data) {
  return request({
    url: '/v1/admin/training/task',
    method: 'post',
    data
  })
}

/**
 * 更新任务
 */
export function updateTask(data) {
  return request({
    url: '/v1/admin/training/task',
    method: 'put',
    data
  })
}

/**
 * 删除任务
 */
export function deleteTask(id) {
  return request({
    url: `/v1/admin/training/task/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除任务
 */
export function batchDeleteTask(ids) {
  return request({
    url: '/v1/admin/training/task/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 发布任务
 */
export function publishTask(id) {
  return request({
    url: `/v1/admin/training/task/${id}/publish`,
    method: 'put'
  })
}

/**
 * 取消发布任务
 */
export function unpublishTask(id) {
  return request({
    url: `/v1/admin/training/task/${id}/unpublish`,
    method: 'put'
  })
}

/**
 * 调整任务排序
 */
export function updateTaskSort(id, sortOrder) {
  return request({
    url: `/v1/admin/training/task/${id}/sort`,
    method: 'put',
    params: { sortOrder }
  })
}
