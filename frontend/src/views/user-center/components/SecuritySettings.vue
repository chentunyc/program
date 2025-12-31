<template>
  <div class="security-settings">
    <div class="section-header">
      <h2>安全设置</h2>
    </div>

    <el-divider />

    <!-- 账号安全状态 -->
    <div class="security-status">
      <h3>账号安全</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="账号状态">
          <el-tag v-if="securityInfo.status === 1" type="success">
            <el-icon><CircleCheck /></el-icon>
            正常
          </el-tag>
          <el-tag v-else type="danger">
            <el-icon><CircleClose /></el-icon>
            已禁用
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="登录密码">
          <el-tag type="success">
            <el-icon><Lock /></el-icon>
            已设置
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最后登录时间">
          {{ securityInfo.lastLoginTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="最后登录IP">
          {{ securityInfo.lastLoginIp || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="账号创建时间" :span="2">
          {{ securityInfo.createTime || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <el-divider />

    <!-- 修改密码 -->
    <div class="password-section">
      <h3>修改密码</h3>
      <el-alert
        type="warning"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      >
        <template #title>
          <span>为了账号安全，建议定期修改密码。密码修改后需要重新登录。</span>
        </template>
      </el-alert>

      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="120px"
        class="password-form"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码（8-20位，包含字母和数字）"
            show-password
            clearable
          />
          <div class="password-strength">
            <span>密码强度：</span>
            <el-progress
              :percentage="passwordStrength.percentage"
              :color="passwordStrength.color"
              :stroke-width="8"
              :show-text="false"
              style="width: 120px"
            />
            <span :style="{ color: passwordStrength.color }">{{ passwordStrength.text }}</span>
          </div>
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="submitting"
            @click="handleChangePassword"
          >
            确认修改
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Lock, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { getCurrentUserProfile, changePassword } from '@/api/user'
import { useUserStore } from '@/store/modules/user'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

// 组件状态
const submitting = ref(false)
const passwordFormRef = ref(null)

// 安全信息
const securityInfo = ref({
  status: 1,
  lastLoginTime: '',
  lastLoginIp: '',
  createTime: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码强度计算
const passwordStrength = computed(() => {
  const password = passwordForm.newPassword
  if (!password) {
    return { percentage: 0, color: '#e4e7ed', text: '' }
  }

  let strength = 0
  // 长度检查
  if (password.length >= 8) strength += 25
  if (password.length >= 12) strength += 10
  // 包含小写字母
  if (/[a-z]/.test(password)) strength += 15
  // 包含大写字母
  if (/[A-Z]/.test(password)) strength += 15
  // 包含数字
  if (/\d/.test(password)) strength += 15
  // 包含特殊字符
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength += 20

  if (strength <= 30) {
    return { percentage: strength, color: '#f56c6c', text: '弱' }
  } else if (strength <= 60) {
    return { percentage: strength, color: '#e6a23c', text: '中' }
  } else {
    return { percentage: Math.min(strength, 100), color: '#67c23a', text: '强' }
  }
})

// 密码验证规则
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else if (value.length < 8 || value.length > 20) {
    callback(new Error('密码长度为8-20位'))
  } else if (!/^(?=.*[a-zA-Z])(?=.*\d).+$/.test(value)) {
    callback(new Error('密码必须包含字母和数字'))
  } else if (value === passwordForm.oldPassword) {
    callback(new Error('新密码不能与当前密码相同'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePass, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

// 加载安全信息
const loadSecurityInfo = async () => {
  try {
    const response = await getCurrentUserProfile()
    const data = response.data
    securityInfo.value = {
      status: data.status,
      lastLoginTime: data.lastLoginTime,
      lastLoginIp: data.lastLoginIp,
      createTime: data.createTime
    }
  } catch (error) {
    console.error('加载安全信息失败:', error)
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
  } catch (error) {
    return
  }

  // 确认操作
  try {
    await ElMessageBox.confirm(
      '密码修改后需要重新登录，确定要修改密码吗？',
      '确认修改',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }

  submitting.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success('密码修改成功，请重新登录')

    // 清除登录状态
    await userStore.logout()

    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '密码修改失败')
    console.error('密码修改失败:', error)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadSecurityInfo()
})
</script>

<style lang="scss" scoped>
.security-settings {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 20px;
      color: #303133;
      font-weight: 600;
    }
  }

  h3 {
    margin: 0 0 16px 0;
    font-size: 16px;
    color: #303133;
    font-weight: 500;
  }

  .security-status {
    :deep(.el-descriptions) {
      .el-descriptions__label {
        width: 120px;
        background-color: #fafafa;
        font-weight: 500;
      }

      .el-descriptions__content {
        color: #606266;
      }
    }

    :deep(.el-tag) {
      .el-icon {
        margin-right: 4px;
      }
    }
  }

  .password-section {
    .password-form {
      max-width: 500px;

      :deep(.el-form-item) {
        margin-bottom: 24px;
      }

      .password-strength {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-top: 8px;
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

@media (max-width: 768px) {
  .security-settings {
    .password-form {
      :deep(.el-form-item__label) {
        text-align: left;
        width: 100% !important;
      }

      :deep(.el-form-item__content) {
        margin-left: 0 !important;
      }
    }
  }
}
</style>
