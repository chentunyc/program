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
            <el-col :xs="24" :sm="12" :md="12">
              <el-statistic title="总用户数" :value="statistics.totalUsers">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <el-col :xs="24" :sm="12" :md="12">
              <el-statistic title="新闻数量" :value="statistics.totalNews">
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-statistic>
            </el-col>
            <!-- 访问统计功能待实现，暂时隐藏 -->
            <!-- <el-col :xs="24" :sm="12" :md="6">
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
            </el-col> -->
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
            <!-- 上传路径：当前未被实际使用，使用 application.yml 中的配置 -->
            <!-- <el-form-item label="上传路径">
              <el-input
                v-model="uploadConfig.uploadPath"
                placeholder="文件上传保存路径"
                readonly
              />
            </el-form-item> -->
            <!-- 允许的文件类型：当前未被实际使用，各模块硬编码了各自的文件类型限制 -->
            <!-- <el-form-item label="允许的文件类型">
              <el-input
                v-model="uploadConfig.allowedTypes"
                type="textarea"
                :rows="2"
                placeholder="多个类型用逗号分隔"
              />
              <div class="form-tip">
                例如：image/jpeg,image/png,image/gif,application/pdf
              </div>
            </el-form-item> -->
            <el-form-item label="最大文件大小">
              <el-input-number
                v-model="uploadConfig.maxFileSize"
                :min="1"
                :max="500"
              />
              <span style="margin-left: 10px">MB</span>
              <div class="form-tip">
                此配置应用于资源管理模块的文件上传
              </div>
            </el-form-item>
            <!-- 图片压缩：当前未被实际使用 -->
            <!-- <el-form-item label="图片压缩">
              <el-switch v-model="uploadConfig.imageCompression" />
            </el-form-item> -->
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Document, View, TrendCharts } from '@element-plus/icons-vue'
import { getAllConfig, saveConfigByGroup, getStatistics } from '@/api/admin'

// 当前激活的标签页
const activeTab = ref('info')

// 加载状态
const loading = ref(false)

// 保存状态
const saving = ref(false)

// 统计信息
const statistics = reactive({
  totalUsers: 0,
  totalNews: 0,
  todayVisits: 0,
  totalVisits: 0
})

// 基本配置
const basicConfig = reactive({
  siteName: '',
  siteLogo: '',
  siteDescription: '',
  icpNumber: '',
  copyright: '',
  supportEmail: '',
  supportPhone: ''
})

// 用于重置的备份
let basicConfigBackup = {}

// 上传配置
const uploadConfig = reactive({
  uploadPath: '',
  allowedTypes: '',
  maxFileSize: 10,
  imageCompression: true
})

let uploadConfigBackup = {}

// 安全配置
const securityConfig = reactive({
  minPasswordLength: 6,
  passwordRequirements: [],
  sessionTimeout: 120,
  loginLockEnabled: true,
  maxLoginAttempts: 5
})

let securityConfigBackup = {}

// 加载所有配置
const loadAllConfig = async () => {
  loading.value = true
  try {
    const res = await getAllConfig()
    if (res.code === 200) {
      const data = res.data

      // 加载基本配置
      if (data.basic) {
        Object.assign(basicConfig, data.basic)
        basicConfigBackup = { ...data.basic }
      }

      // 加载上传配置
      if (data.upload) {
        Object.assign(uploadConfig, data.upload)
        uploadConfigBackup = { ...data.upload }
      }

      // 加载安全配置
      if (data.security) {
        Object.assign(securityConfig, data.security)
        securityConfigBackup = { ...data.security }
      }
    }
  } catch (error) {
    console.error('加载配置失败:', error)
    ElMessage.error('加载配置失败')
  } finally {
    loading.value = false
  }
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

// 保存基本配置
const handleSaveBasic = async () => {
  saving.value = true
  try {
    const res = await saveConfigByGroup('basic', basicConfig)
    if (res.code === 200) {
      ElMessage.success('基本配置保存成功')
      basicConfigBackup = { ...basicConfig }
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
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
    const res = await saveConfigByGroup('upload', uploadConfig)
    if (res.code === 200) {
      ElMessage.success('上传配置保存成功')
      uploadConfigBackup = { ...uploadConfig }
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
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
    const res = await saveConfigByGroup('security', securityConfig)
    if (res.code === 200) {
      ElMessage.success('安全配置保存成功')
      securityConfigBackup = { ...securityConfig }
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
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

// 页面加载时获取数据
onMounted(() => {
  loadAllConfig()
  loadStatistics()
})
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
