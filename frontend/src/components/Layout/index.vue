<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="logo">

        <img src="/images/logo.png" alt="Logo" class="logo-img" />

        <span v-if="!isCollapse" class="logo-text">实训平台</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        class="sidebar-menu"
        router
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <!-- 没有子菜单的项 -->
          <el-menu-item
            v-if="!route.children || route.children.length === 0"
            :index="route.path"
            @click="handleMenuClick(route)"
          >
            <el-icon>
              <component :is="route.meta.icon" />
            </el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>

          <!-- 有子菜单的项 -->
          <el-sub-menu v-else :index="route.path">
            <template #title>
              <el-icon>
                <component :is="route.meta.icon" />
              </el-icon>
              <span>{{ route.meta.title }}</span>
            </template>

            <template v-for="child in route.children" :key="child.path">
              <!-- 二级菜单无子菜单 -->
              <el-menu-item
                v-if="!child.children || child.children.length === 0"
                :index="route.path + '/' + child.path"
              >
                <el-icon v-if="child.meta?.icon">
                  <component :is="child.meta.icon" />
                </el-icon>
                <template #title>{{ child.meta.title }}</template>
              </el-menu-item>

              <!-- 二级菜单有子菜单（三级菜单） -->
              <el-sub-menu v-else :index="route.path + '/' + child.path">
                <template #title>
                  <el-icon v-if="child.meta?.icon">
                    <component :is="child.meta.icon" />
                  </el-icon>
                  <span>{{ child.meta.title }}</span>
                </template>
                <el-menu-item
                  v-for="subChild in child.children"
                  :key="subChild.path"
                  :index="route.path + '/' + child.path + '/' + subChild.path"
                >
                  <el-icon v-if="subChild.meta?.icon">
                    <component :is="subChild.meta.icon" />
                  </el-icon>
                  <template #title>{{ subChild.meta.title }}</template>
                </el-menu-item>
              </el-sub-menu>
            </template>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleSidebar">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>

          <!-- 面包屑 -->
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta?.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 用户信息 -->
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userAvatarUrl" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userInfo?.realName || userInfo?.username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { useAppStore } from '@/store/modules/app'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 用户头像URL（处理路径前缀）
const userAvatarUrl = computed(() => {
  const avatar = userInfo.value?.avatar
  if (!avatar) return ''
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }
  return '/api' + avatar
})

// 侧边栏折叠状态
const isCollapse = computed(() => !appStore.sidebar.opened)
const sidebarWidth = computed(() => (isCollapse.value ? '64px' : '200px'))

// 当前路由
const currentRoute = computed(() => route)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 菜单路由列表
const menuRoutes = computed(() => {
  const layoutRoute = router.getRoutes().find(r => r.name === 'Layout')
  if (!layoutRoute || !layoutRoute.children) {
    return []
  }

  /**
   * 检查用户是否有权限访问路由
   */
  const hasPermission = (route) => {
    // 隐藏的路由不显示
    if (route.meta?.hidden) {
      return false
    }

    // 检查 allowedRoles
    const allowedRoles = route.meta?.allowedRoles
    if (allowedRoles && allowedRoles.length > 0) {
      const hasRole = allowedRoles.some(role => userStore.hasRole(role))
      if (!hasRole) {
        return false
      }
    }

    // 检查是否被角色排除
    const excludeRoles = route.meta?.excludeRoles
    if (excludeRoles && excludeRoles.length > 0) {
      const isExcluded = excludeRoles.some(role => userStore.hasRole(role))
      if (isExcluded) {
        return false
      }
    }

    return route.meta?.title
  }

  /**
   * 递归过滤子菜单
   */
  const filterChildren = (children) => {
    if (!children) return []
    return children
      .filter(child => hasPermission(child))
      .map(child => ({
        ...child,
        children: filterChildren(child.children)
      }))
      .filter(child => {
        // 如果有子菜单要求但过滤后没有子菜单了，也隐藏父菜单
        if (child.children && child.children.length === 0 && child.redirect) {
          return false
        }
        return true
      })
  }

  // 过滤一级菜单
  return layoutRoute.children
    .filter(route => hasPermission(route))
    .map(route => {
      const filteredChildren = filterChildren(route.children)
      return {
        ...route,
        path: '/' + route.path,
        children: filteredChildren
      }
    })
    .filter(route => {
      // 如果有子菜单要求但过滤后没有子菜单了，也隐藏父菜单
      if (route.children && route.children.length === 0 && route.redirect) {
        return false
      }
      return true
    })
})

/**
 * 切换侧边栏
 */
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

/**
 * 处理菜单点击
 */
const handleMenuClick = (route) => {
  console.log('菜单点击:', route.meta.title)
}

/**
 * 处理下拉菜单命令
 */
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user-center')
      break
    case 'logout':
      handleLogout()
      break
  }
}

/**
 * 退出登录
 */
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      userStore.logout()
    })
    .catch(() => {
      // 取消操作
    })
}
</script>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background-color: #001529;
  transition: width 0.3s;
  overflow-x: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);

  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 60px;
    padding: 0 20px;
    color: #fff;
    background-color: #002140;

    .logo-img {
      width: 32px;
      height: 32px;
      margin-right: 10px;
    }

    .logo-text {
      font-size: 18px;
      font-weight: 600;
      white-space: nowrap;
    }
  }

  .sidebar-menu {
    border-right: none;
    background-color: #001529;

    :deep(.el-menu-item) {
      color: rgba(255, 255, 255, 0.75);

      &:hover {
        color: #fff;
        background-color: rgba(255, 255, 255, 0.08);
      }

      &.is-active {
        color: #fff;
        background-color: #1890ff;
      }
    }

    :deep(.el-sub-menu) {
      .el-sub-menu__title {
        color: rgba(255, 255, 255, 0.75);

        &:hover {
          color: #fff;
          background-color: rgba(255, 255, 255, 0.08);
        }
      }

      .el-icon {
        color: inherit;
      }

      // 子菜单容器
      .el-menu {
        background-color: #000c17;
      }

      // 子菜单项样式
      .el-menu-item {
        color: rgba(255, 255, 255, 0.75);
        background-color: #000c17;

        &:hover {
          color: #fff;
          background-color: rgba(255, 255, 255, 0.08);
        }

        &.is-active {
          color: #fff;
          background-color: #1890ff;
        }
      }
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;

    .collapse-icon {
      font-size: 20px;
      cursor: pointer;
      margin-right: 20px;
      transition: color 0.3s;

      &:hover {
        color: #409eff;
      }
    }

    .breadcrumb {
      font-size: 14px;
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 5px 10px;
      border-radius: 4px;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }

      .user-avatar {
        margin-right: 10px;
      }

      .user-name {
        font-size: 14px;
        color: #303133;
      }
    }
  }
}

.main-content {
  flex: 1;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 0;
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .header {
    padding: 0 10px;
  }

  .header-left {
    .breadcrumb {
      display: none;
    }
  }
}
</style>
