import request from '@/utils/request'

/**
 * ==================== 管理员用户管理 API ====================
 */

/**
 * 分页查询用户列表（管理员）
 */
export function getUserPage(params) {
  return request({
    url: '/v1/user/admin/page',
    method: 'get',
    params
  })
}

/**
 * 创建用户（管理员）
 */
export function createUser(data) {
  return request({
    url: '/v1/user/admin',
    method: 'post',
    data
  })
}

/**
 * 更新用户信息（管理员）
 */
export function updateUser(userId, data) {
  return request({
    url: `/v1/user/admin/${userId}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户（管理员）
 */
export function deleteUser(userId) {
  return request({
    url: `/v1/user/admin/${userId}`,
    method: 'delete'
  })
}

/**
 * 批量删除用户（管理员）
 */
export function batchDeleteUsers(userIds) {
  return request({
    url: '/v1/user/admin/batch',
    method: 'delete',
    data: userIds
  })
}

/**
 * 重置用户密码（管理员）
 */
export function resetPassword(userId, newPassword) {
  return request({
    url: `/v1/user/admin/${userId}/reset-password`,
    method: 'put',
    params: { newPassword }
  })
}

/**
 * 获取所有角色列表
 */
export function getRoleList() {
  return request({
    url: '/v1/role/list',
    method: 'get'
  })
}

/**
 * 检查员工编号是否可用（管理员）
 * @param {string} employeeNo - 员工编号
 * @param {number} excludeUserId - 排除的用户ID（编辑时传入当前用户ID）
 */
export function checkEmployeeNo(employeeNo, excludeUserId) {
  return request({
    url: '/v1/user/admin/check-employee-no',
    method: 'get',
    params: { employeeNo, excludeUserId }
  })
}
