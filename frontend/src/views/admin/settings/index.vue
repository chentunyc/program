<template>
  <div class="admin-settings-container">
    <div class="page-header">
      <h1>平台设置</h1>
      <p>System Settings</p>
    </div>

    <el-tabs v-model="activeTab" type="card">
      <!-- 系统信息 -->
      <el-tab-pane label="系统信息" name="info">
        <div class="settings-section">
          <h3>系统信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="系统名称">
              虚拟仿真实训教学管理及资源共享云平台
            </el-descriptions-item>
            <el-descriptions-item label="系统版本">
              v1.0.0
            </el-descriptions-item>
            <el-descriptions-item label="后端框架">
              Spring Boot 3.2.1
            </el-descriptions-item>
            <el-descriptions-item label="前端框架">
              Vue 3 + Element Plus
            </el-descriptions-item>
            <el-descriptions-item label="数据库">
              MySQL 8.0+
            </el-descriptions-item>
            <el-descriptions-item label="缓存">
              Redis
            </el-descriptions-item>
            <el-descriptions-item label="部署时间" :span="2">
              {{ new Date().toLocaleString() }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="settings-section">
          <h3>统计信息</h3>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6">
              <el-statistic title="总用户数" :value="statistics.totalUsers">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-statistic title="新闻数量" :value="statistics.totalNews">
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-statistic title="今日访问" :value="statistics.todayVisits">
                <template #prefix>
                  <el-icon><View /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-statistic title="总访问量" :value="statistics.totalVisits">
                <template #prefix>
                  <el-icon><TrendCharts /></el-icon>
                </template>
              </el-statistic>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <!-- 基本配置 -->
      <el-tab-pane label="基本配置" name="basic">
        <div class="settings-section">
          <el-form
            ref="basicFormRef"
            :model="basicConfig"
            label-width="120px"
            style="max-width: 600px"
          >
            <el-form-item label="站点名称">
              <el-input
                v-model="basicConfig.siteName"
                placeholder="请输入站点名称"
              />
            </el-form-item>
            <el-form-item label="站点Logo">
              <el-input
                v-model="basicConfig.siteLogo"
                placeholder="请输入Logo URL"
              />
            </el-form-item>
            <el-form-item label="站点描述">
              <el-input
                v-model="basicConfig.siteDescription"
                type="textarea"
                :rows="3"
                placeholder="请输入站点描述"
              />
            </el-form-item>
            <el-form-item label="ICP备案号">
              <el-input
                v-model="basicConfig.icpNumber"
                placeholder="请输入ICP备案号"
              />
            </el-form-item>
            <el-form-item label="版权信息">
              <el-input
                v-model="basicConfig.copyright"
                placeholder="请输入版权信息"
              />
            </el-form-item>
            <el-form-item label="客服邮箱">
              <el-input
                v-model="basicConfig.supportEmail"
                placeholder="请输入客服邮箱"
              />
            </el-form-item>
            <el-form-item label="客服电话">
              <el-input
                v-model="basicConfig.supportPhone"
                placeholder="请输入客服电话"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveBasic">
                保存配置
              </el-button>
              <el-button @click="handleResetBasic">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 上传配置 -->
      <el-tab-pane label="上传配置" name="upload">
        <div class="settings-section">
          <el-form
            ref="uploadFormRef"
            :model="uploadConfig"
            label-width="150px"
            style="max-width: 600px"
          >
            <el-form-item label="上传路径">
              <el-input
                v-model="uploadConfig.uploadPath"
                placeholder="文件上传保存路径"
                readonly
              />
            </el-form-item>
            <el-form-item label="允许的文件类型">
              <el-input
                v-model="uploadConfig.allowedTypes"
                type="textarea"
                :rows="2"
                placeholder="多个类型用逗号分隔"
              />
              <div class="form-tip">
                例如：image/jpeg,image/png,image/gif,application/pdf
              </div>
            </el-form-item>
            <el-form-item label="最大文件大小">
              <el-input-number
                v-model="uploadConfig.maxFileSize"
                :min="1"
                :max="100"
              />
              <span style="margin-left: 10px">MB</span>
            </el-form-item>
            <el-form-item label="图片压缩">
              <el-switch v-model="uploadConfig.imageCompression" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveUpload">
                保存配置
              </el-button>
              <el-button @click="handleResetUpload">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 安全配置 -->
      <el-tab-pane label="安全配置" name="security">
        <div class="settings-section">
          <el-form
            ref="securityFormRef"
            :model="securityConfig"
            label-width="150px"
            style="max-width: 600px"
          >
            <el-form-item label="密码最小长度">
              <el-input-number
                v-model="securityConfig.minPasswordLength"
                :min="6"
                :max="20"
              />
            </el-form-item>
            <el-form-item label="密码复杂度要求">
              <el-checkbox-group v-model="securityConfig.passwordRequirements">
                <el-checkbox label="uppercase">包含大写字母</el-checkbox>
                <el-checkbox label="lowercase">包含小写字母</el-checkbox>
                <el-checkbox label="number">包含数字</el-checkbox>
                <el-checkbox label="special">包含特殊字符</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="会话超时时间">
              <el-input-number
                v-model="securityConfig.sessionTimeout"
                :min="30"
                :max="1440"
              />
              <span style="margin-left: 10px">分钟</span>
            </el-form-item>
            <el-form-item label="登录失败锁定">
              <el-switch v-model="securityConfig.loginLockEnabled" />
            </el-form-item>
            <el-form-item
              v-if="securityConfig.loginLockEnabled"
              label="失败次数阈值"
            >
              <el-input-number
                v-model="securityConfig.maxLoginAttempts"
                :min="3"
                :max="10"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveSecurity">
                保存配置
              </el-button>
              <el-button @click="handleResetSecurity">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Document, View, TrendCharts } from '@element-plus/icons-vue'

// 当前激活的标签页
const activeTab = ref('info')

// 保存状态
const saving = ref(false)

// 统计信息（模拟数据）
const statistics = reactive({
  totalUsers: 156,
  totalNews: 48,
  todayVisits: 1234,
  totalVisits: 45678
})

// 基本配置
const basicConfig = reactive({
  siteName: '虚拟仿真实训教学管理及资源共享云平台',
  siteLogo: '/logo.png',
  siteDescription: '专业的虚拟仿真实训教学管理平台，提供资源共享、实训管理等功能',
  icpNumber: '粤ICP备XXXXXXXX号',
  copyright: '© 2024 虚拟仿真实训平台 版权所有',
  supportEmail: 'support@training.com',
  supportPhone: '400-123-4567'
})

const basicConfigBackup = { ...basicConfig }

// 上传配置
const uploadConfig = reactive({
  uploadPath: './uploads',
  allowedTypes: 'image/jpeg,image/png,image/gif,application/pdf',
  maxFileSize: 10,
  imageCompression: true
})

const uploadConfigBackup = { ...uploadConfig }

// 安全配置
const securityConfig = reactive({
  minPasswordLength: 6,
  passwordRequirements: ['lowercase', 'number'],
  sessionTimeout: 120,
  loginLockEnabled: true,
  maxLoginAttempts: 5
})

const securityConfigBackup = { ...securityConfig }

// 保存基本配置
const handleSaveBasic = async () => {
  saving.value = true
  try {
    // 模拟保存
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('基本配置保存成功')
    Object.assign(basicConfigBackup, basicConfig)
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 重置基本配置
const handleResetBasic = () => {
  Object.assign(basicConfig, basicConfigBackup)
  ElMessage.info('已重置为上次保存的配置')
}

// 保存上传配置
const handleSaveUpload = async () => {
  saving.value = true
  try {
    // 模拟保存
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('上传配置保存成功')
    Object.assign(uploadConfigBackup, uploadConfig)
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 重置上传配置
const handleResetUpload = () => {
  Object.assign(uploadConfig, uploadConfigBackup)
  ElMessage.info('已重置为上次保存的配置')
}

// 保存安全配置
const handleSaveSecurity = async () => {
  saving.value = true
  try {
    // 模拟保存
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('安全配置保存成功')
    Object.assign(securityConfigBackup, securityConfig)
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 重置安全配置
const handleResetSecurity = () => {
  Object.assign(securityConfig, securityConfigBackup)
  ElMessage.info('已重置为上次保存的配置')
}
</script>

<style lang="scss" scoped>
.admin-settings-container {
  .page-header {
    margin-bottom: 20px;
    padding-bottom: 20px;
    padding-left: 30px;
    padding-top: 10px;
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

  :deep(.el-tabs) {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }

  .settings-section {
    margin-bottom: 30px;

    h3 {
      margin: 0 0 20px;
      font-size: 18px;
      color: #303133;
      font-weight: 600;
    }

    :deep(.el-descriptions) {
      margin-bottom: 20px;
    }

    :deep(.el-statistic) {
      text-align: center;
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;
      margin-bottom: 10px;

      .el-statistic__content {
        font-size: 28px;
        font-weight: 600;
      }
    }

    .form-tip {
      margin-top: 5px;
      font-size: 12px;
      color: #909399;
    }
  }
}

@media (max-width: 768px) {
  .settings-section {
    :deep(.el-form) {
      .el-form-item__label {
        width: 100% !important;
        text-align: left;
      }

      .el-form-item__content {
        margin-left: 0 !important;
      }
    }
  }
}
</style>
