<template>
  <div class="user-center-container">
    <div class="page-header">
      <h1>用户中心</h1>
      <p>User Center</p>
    </div>

    <div class="user-center-content">
      <el-container>
        <!-- 左侧导航菜单 -->
        <el-aside width="200px" class="sidebar">
          <el-menu
            :default-active="activeMenu"
            class="sidebar-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="account">
              <el-icon><Setting /></el-icon>
              <span>账号设置</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 右侧内容区域 -->
        <el-main class="main-content">
          <!-- 个人信息 -->
          <div v-show="activeMenu === 'profile'" class="content-section">
            <ProfileInfo />
          </div>

          <!-- 账号设置 -->
          <div v-show="activeMenu === 'account'" class="content-section">
            <div class="placeholder-content">
              <el-empty description="功能开发中" />
            </div>
          </div>

          <!-- 安全设置 -->
          <div v-show="activeMenu === 'security'" class="content-section">
            <div class="placeholder-content">
              <el-empty description="功能开发中" />
            </div>
          </div>
        </el-main>
      </el-container>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { User, Setting, Lock } from '@element-plus/icons-vue'
import ProfileInfo from './components/ProfileInfo.vue'

// 当前激活的菜单
const activeMenu = ref('profile')

// 菜单选择处理
const handleMenuSelect = (index) => {
  activeMenu.value = index
}
</script>

<style lang="scss" scoped>
.user-center-container {
  .page-header {
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 2px solid #e4e7ed;

    h1 {
      margin: 0;
      font-size: 28px;
      color: #303133;
      font-weight: 600;
    }

    p {
      margin: 8px 0 0;
      font-size: 14px;
      color: #909399;
      text-transform: uppercase;
      letter-spacing: 1px;
    }
  }

  .user-center-content {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .el-container {
      min-height: 600px;
    }

    .sidebar {
      background: #f5f7fa;
      border-right: 1px solid #e4e7ed;

      .sidebar-menu {
        border-right: none;
        background: transparent;

        :deep(.el-menu-item) {
          height: 50px;
          line-height: 50px;
          margin: 8px 12px;
          border-radius: 6px;

          &:hover {
            background-color: #ecf5ff;
            color: #409eff;
          }

          &.is-active {
            background-color: #409eff;
            color: #fff;

            &:hover {
              background-color: #409eff;
            }
          }
        }
      }
    }

    .main-content {
      padding: 30px;
      background: #fff;

      .content-section {
        animation: fadeIn 0.3s ease-in-out;
      }

      .placeholder-content {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 400px;
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .user-center-content {
    .el-container {
      flex-direction: column;
    }

    .sidebar {
      width: 100% !important;
      border-right: none;
      border-bottom: 1px solid #e4e7ed;

      .sidebar-menu {
        display: flex;

        :deep(.el-menu-item) {
          flex: 1;
          text-align: center;
          margin: 8px 4px;
        }
      }
    }

    .main-content {
      padding: 20px;
    }
  }
}
</style>
