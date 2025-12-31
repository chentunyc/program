import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getToken } from './auth'
import router from '@/router'

/**
 * 创建axios实例
 */
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

/**
 * 请求拦截器
 */
service.interceptors.request.use(
  (config) => {
    // 添加Token
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 */
service.interceptors.response.use(
  (response) => {
    const res = response.data

    // 二进制数据直接返回
    if (response.config.responseType === 'blob') {
      return response
    }

    // 根据自定义code判断请求是否成功
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')

      // 401: Token过期或未授权
      if (res.code === 401) {
        ElMessageBox.confirm('登录状态已过期,您可以继续留在该页面,或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const userStore = useUserStore()
          userStore.resetState()
          router.push('/login')
        })
      }

      // 403: 权限不足
      if (res.code === 403) {
        ElMessage.error('权限不足,无法访问该资源')
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  (error) => {
    console.error('响应错误:', error)

    let message = '请求失败'

    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权,请重新登录'
          const userStore = useUserStore()
          userStore.resetState()
          router.push('/login')
          break
        case 403:
          message = '权限不足'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器错误'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务不可用'
          break
        case 504:
          message = '网关超时'
          break
        default:
          message = error.response.data?.message || '请求失败'
      }
    } else if (error.message) {
      if (error.message.includes('timeout')) {
        message = '请求超时'
      } else if (error.message.includes('Network Error')) {
        message = '网络错误'
      } else {
        message = error.message
      }
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
