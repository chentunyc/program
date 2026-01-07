import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getUserInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router from '@/router'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(getToken())
  const userInfo = ref(null)
  const roles = ref([])
  const permissions = ref([])

  /**
   * 登录
   */
  const loginAction = async (loginForm) => {
    try {
      const res = await login(loginForm)
      token.value = res.data.token
      setToken(res.data.token)
      return res
    } catch (error) {
      return Promise.reject(error)
    }
  }

  /**
   * 获取用户信息
   */
  const getUserInfoAction = async () => {
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
      roles.value = res.data.roles || []
      permissions.value = res.data.permissions || []
      return res
    } catch (error) {
      return Promise.reject(error)
    }
  }

  /**
   * 登出
   */
  const logoutAction = async () => {
    try {
      await logout()
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 清除本地状态
      token.value = null
      userInfo.value = null
      roles.value = []
      permissions.value = []
      removeToken()
      router.push('/login')
    }
  }

  /**
   * 重置状态
   */
  const resetState = () => {
    token.value = null
    userInfo.value = null
    roles.value = []
    permissions.value = []
    removeToken()
  }

  /**
   * 判断是否有指定角色
   */
  const hasRole = (role) => {
    return roles.value.includes(role)
  }

  /**
   * 判断是否有指定权限
   */
  const hasPermission = (permission) => {
    return permissions.value.includes(permission)
  }

  /**
   * 判断是否为管理员
   */
  const isAdmin = () => {
    return hasRole('ADMIN')
  }

  /**
   * 判断是否为教师
   */
  const isTeacher = () => {
    return hasRole('TEACHER')
  }

  /**
   * 判断是否为学生
   */
  const isStudent = () => {
    return hasRole('STUDENT')
  }

  /**
   * 判断是否为访客
   */
  const isGuest = () => {
    return hasRole('GUEST')
  }

  /**
   * 判断是否为资料管理员
   */
  const isDataAdmin = () => {
    return hasRole('DATA_ADMIN')
  }

  return {
    // 状态
    token,
    userInfo,
    roles,
    permissions,
    // 方法
    login: loginAction,
    getUserInfo: getUserInfoAction,
    logout: logoutAction,
    resetState,
    hasRole,
    hasPermission,
    isAdmin,
    isTeacher,
    isStudent,
    isGuest,
    isDataAdmin
  }
})
