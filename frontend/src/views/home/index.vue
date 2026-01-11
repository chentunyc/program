<template>
  <div class="home-container">
    <div class="page-header">
      <h1>欢迎使用虚拟仿真实训教学管理平台</h1>
      <p>Virtual Training Platform</p>
    </div>

    <div class="welcome-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #409eff">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-content">
              <p class="stat-label">用户信息</p>
              <p class="stat-value">{{ userInfo?.realName || userInfo?.username }}</p>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #67c23a">
              <el-icon :size="32"><Medal /></el-icon>
            </div>
            <div class="stat-content">
              <p class="stat-label">角色</p>
              <p class="stat-value">{{ getRoleName() }}</p>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #e6a23c">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <p class="stat-label">登录时间</p>
              <p class="stat-value">{{ currentTime }}</p>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #f56c6c">
              <el-icon :size="32"><Cpu /></el-icon>
            </div>
            <div class="stat-content">
              <p class="stat-label">系统状态</p>
              <p class="stat-value">正常</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="quick-entry">
      <h2>快捷入口</h2>
      <el-row :gutter="20" class="entry-cards">
        <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="entry in quickEntries" :key="entry.path">
          <div class="entry-card" @click="goToPage(entry.path)">
            <el-icon :size="40" :color="entry.color">
              <component :is="entry.icon" />
            </el-icon>
            <p>{{ entry.title }}</p>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 当前时间
const currentTime = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
let timer = null

// 快捷入口（根据登录状态和角色动态过滤）
const quickEntries = computed(() => {
  const allEntries = [
    { title: '新闻公告', icon: 'Bell', path: '/news', color: '#409eff', requireAuth: false },
    { title: '用户中心', icon: 'User', path: '/user-center', color: '#67c23a', requireAuth: true },
    { title: '资源中心', icon: 'Folder', path: '/resource', color: '#e6a23c', requireAuth: true },
    { title: '实训中心', icon: 'Notebook', path: '/training', color: '#f56c6c', requireAuth: true, excludeRoles: ['GUEST', 'ADMIN', 'TEACHER', 'DATA_ADMIN'] }
  ]

  const isLoggedIn = !!userStore.token
  const roles = userInfo.value?.roles || []

  return allEntries.filter(entry => {
    // 未登录时，只显示不需要认证的入口
    if (!isLoggedIn && entry.requireAuth) {
      return false
    }
    // 如果没有角色限制，显示
    if (!entry.excludeRoles || entry.excludeRoles.length === 0) {
      return true
    }
    // 如果用户拥有被排除的角色，不显示
    return !entry.excludeRoles.some(role => roles.includes(role))
  })
})

/**
 * 获取角色名称
 */
const getRoleName = () => {
  const roles = userInfo.value?.roles || []
  if (roles.includes('ADMIN')) return '管理员'
  if (roles.includes('TEACHER')) return '教师'
  if (roles.includes('DATA_ADMIN')) return '资料管理员'
  if (roles.includes('STUDENT')) return '学生'
  if (roles.includes('GUEST')) return '访客'
  return '未知'
}

/**
 * 跳转页面
 */
const goToPage = (path) => {
  router.push(path)
}

/**
 * 更新时间
 */
const updateTime = () => {
  currentTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style lang="scss" scoped>
.home-container {
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;

  h1 {
    font-size: 28px;
    color: #303133;
    margin-bottom: 10px;
  }

  p {
    font-size: 14px;
    color: #909399;
  }
}

.welcome-card {
  margin-bottom: 30px;

  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      margin-right: 15px;
    }

    .stat-content {
      flex: 1;

      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 5px;
      }

      .stat-value {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
    }
  }
}

.quick-entry {
  h2 {
    font-size: 20px;
    color: #303133;
    margin-bottom: 20px;
  }

  .entry-cards {
    .entry-card {
      background: #fff;
      border-radius: 8px;
      padding: 30px 20px;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      margin-bottom: 20px;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
      }

      p {
        margin-top: 15px;
        font-size: 15px;
        color: #303133;
        font-weight: 500;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .page-header h1 {
    font-size: 22px;
  }

  .stat-card {
    padding: 15px !important;
  }

  .entry-card {
    padding: 20px 15px !important;
  }
}
</style>
