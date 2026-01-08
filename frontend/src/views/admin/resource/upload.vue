<template>
  <div class="resource-upload-container">
    <div class="page-header">
      <h1>资源上传</h1>
      <p>Resource Upload</p>
    </div>

    <el-empty description="资源上传功能开发中...">
      <el-button type="primary">刷新页面</el-button>
    </el-empty>
  </div>
</template>

<script setup>
// 资源上传页面 - 占位
</script>

<style lang="scss" scoped>
.resource-upload-container {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;
    padding-bottom: 20px;
    padding-left: 10px;
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
}
</style>
