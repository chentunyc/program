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

/**
 * ==================== 学生实训中心 API ====================
 */

/**
 * 获取可报名的项目列表
 */
export function getAvailableProjects(params) {
  return request({
    url: '/v1/training/available',
    method: 'get',
    params
  })
}

/**
 * 获取我参与的项目列表
 */
export function getStudentProjects(params) {
  return request({
    url: '/v1/training/my-projects',
    method: 'get',
    params
  })
}

/**
 * 报名参加项目
 */
export function enrollProject(projectId) {
  return request({
    url: `/v1/training/enroll/${projectId}`,
    method: 'post'
  })
}

/**
 * 获取学生项目详情
 */
export function getStudentProjectDetail(projectId) {
  return request({
    url: `/v1/training/project/${projectId}`,
    method: 'get'
  })
}

/**
 * 获取项目任务列表(学生视角)
 */
export function getStudentProjectTasks(projectId) {
  return request({
    url: `/v1/training/project/${projectId}/tasks`,
    method: 'get'
  })
}

/**
 * 获取任务详情(学生视角)
 */
export function getStudentTaskDetail(taskId) {
  return request({
    url: `/v1/training/task/${taskId}`,
    method: 'get'
  })
}

/**
 * 提交任务
 */
export function submitTask(data) {
  return request({
    url: '/v1/training/task/submit',
    method: 'post',
    data
  })
}

/**
 * 开始项目
 */
export function startStudentProject(projectId) {
  return request({
    url: `/v1/training/project/${projectId}/start`,
    method: 'post'
  })
}

/**
 * 上传实训报告文件
 */
export function uploadTrainingReport(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/v1/training/upload/report',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * ==================== 教师批改评分 API ====================
 */

/**
 * 获取教师负责的项目列表(批改用)
 */
export function getGradingProjects() {
  return request({
    url: '/v1/admin/training/grading/my-projects',
    method: 'get'
  })
}

/**
 * 获取项目的学生列表
 */
export function getProjectStudents(projectId) {
  return request({
    url: `/v1/admin/training/grading/project/${projectId}/students`,
    method: 'get'
  })
}

/**
 * 分页查询提交记录
 */
export function getSubmissions(params) {
  return request({
    url: '/v1/admin/training/grading/submissions',
    method: 'get',
    params
  })
}

/**
 * 获取提交详情
 */
export function getSubmissionDetail(submissionId) {
  return request({
    url: `/v1/admin/training/grading/submission/${submissionId}`,
    method: 'get'
  })
}

/**
 * 批改评分
 */
export function gradeSubmission(data) {
  return request({
    url: '/v1/admin/training/grading/grade',
    method: 'post',
    data
  })
}

/**
 * 批量评分
 */
export function batchGradeSubmissions(data) {
  return request({
    url: '/v1/admin/training/grading/grade/batch',
    method: 'post',
    data
  })
}

/**
 * 获取学生在项目中的所有提交记录
 */
export function getStudentSubmissions(projectId, studentId) {
  return request({
    url: `/v1/admin/training/grading/project/${projectId}/student/${studentId}/submissions`,
    method: 'get'
  })
}

/**
 * 完成学生评分
 */
export function completeStudentGrading(projectId, studentId) {
  return request({
    url: `/v1/admin/training/grading/project/${projectId}/student/${studentId}/complete`,
    method: 'post'
  })
}

/**
 * 获取项目详细成绩(用于导出)
 */
export function getDetailedScores(projectId) {
  return request({
    url: `/v1/admin/training/grading/project/${projectId}/detailed-scores`,
    method: 'get'
  })
}
