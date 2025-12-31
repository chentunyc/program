<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="logo">
        <img src="@/assets/logo.svg" alt="Logo" class="logo-img" />
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
          <el-menu-item
            v-if="!route.meta?.hidden"
            :index="route.path"
            @click="handleMenuClick(route)"
          >
            <el-icon>
              <component :is="route.meta.icon" />
            </el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>
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
              <el-avatar :size="32" :src="userInfo?.avatar" class="user-avatar">
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

// 侧边栏折叠状态
const isCollapse = computed(() => !appStore.sidebar.opened)
const sidebarWidth = computed(() => (isCollapse.value ? '64px' : '200px'))

// 当前路由
const currentRoute = computed(() => route)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 菜单路由列表
const menuRoutes = computed(() => {
  const routes = router.getRoutes().filter((route) => {
    return route.path.startsWith('/') && route.meta?.title && !route.meta?.hidden
  })

  // 调试：打印菜单路由
  console.log('菜单路由数量:', routes.length)
  console.log('菜单路由列表:', routes.map(r => ({ path: r.path, title: r.meta?.title, hidden: r.meta?.hidden })))

  return routes
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

    :deep(.el-icon) {
      color: inherit;
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
