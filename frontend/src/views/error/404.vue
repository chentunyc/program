<template>
  <div class="error-container">
    <div class="error-content">
      <h1>404</h1>
      <p class="error-text">抱歉,您访问的页面不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/home')
}
</script>

<style lang="scss" scoped>
.error-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.error-content {
  text-align: center;

  h1 {
    font-size: 120px;
    color: #409eff;
    margin-bottom: 20px;
  }

  .error-text {
    font-size: 20px;
    color: #606266;
    margin-bottom: 30px;
  }
}
</style>
