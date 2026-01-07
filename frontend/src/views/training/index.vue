<template>
  <div class="training-container">
    <div class="page-header">
      <h1>实训中心</h1>
      <p>Training Center</p>
    </div>

    <div class="coming-soon-card">
      <el-icon :size="80" color="#909399">
        <Notebook />
      </el-icon>
      <h2>功能开发中</h2>
      <p>实训中心模块正在紧张开发中，敬请期待...</p>
      <el-button type="primary" @click="goBack">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.push('/home')
}
</script>

<style lang="scss" scoped>
.training-container {
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

.coming-soon-card {
  background: #fff;
  border-radius: 8px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  max-width: 500px;
  margin: 0 auto;

  h2 {
    font-size: 24px;
    color: #303133;
    margin: 20px 0 10px;
  }

  p {
    font-size: 14px;
    color: #909399;
    margin-bottom: 30px;
  }
}
</style>
