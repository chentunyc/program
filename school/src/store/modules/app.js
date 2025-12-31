import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 应用状态管理
 */
export const useAppStore = defineStore('app', () => {
  // 状态
  const sidebar = ref({
    opened: true,
    withoutAnimation: false
  })
  const device = ref('desktop')
  const size = ref('default')

  /**
   * 切换侧边栏
   */
  const toggleSidebar = () => {
    sidebar.value.opened = !sidebar.value.opened
    sidebar.value.withoutAnimation = false
  }

  /**
   * 关闭侧边栏
   */
  const closeSidebar = (withoutAnimation) => {
    sidebar.value.opened = false
    sidebar.value.withoutAnimation = withoutAnimation
  }

  /**
   * 设置设备类型
   */
  const setDevice = (deviceType) => {
    device.value = deviceType
  }

  /**
   * 设置组件大小
   */
  const setSize = (sizeValue) => {
    size.value = sizeValue
  }

  /**
   * 初始化应用
   */
  const initApp = () => {
    // 检测设备类型
    const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
      navigator.userAgent
    )
    setDevice(isMobile ? 'mobile' : 'desktop')

    // 移动设备默认关闭侧边栏
    if (isMobile) {
      closeSidebar(true)
    }
  }

  return {
    // 状态
    sidebar,
    device,
    size,
    // 方法
    toggleSidebar,
    closeSidebar,
    setDevice,
    setSize,
    initApp
  }
})
