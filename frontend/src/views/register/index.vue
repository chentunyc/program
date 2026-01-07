<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>虚拟仿真实训教学管理平台</h2>
        <p>用户注册 / User Registration</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="top"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="username" label="用户名">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名(3-50位)"
                prefix-icon="User"
                @blur="checkUsernameAvailable"
                clearable
              />
              <div v-if="usernameStatus === 'checking'" class="username-tip checking">
                <el-icon class="is-loading"><Loading /></el-icon>
                检查中...
              </div>
              <div v-else-if="usernameStatus === 'available'" class="username-tip available">
                <el-icon><CircleCheck /></el-icon>
                用户名可用
              </div>
              <div v-else-if="usernameStatus === 'unavailable'" class="username-tip unavailable">
                <el-icon><CircleClose /></el-icon>
                用户名已存在
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="realName" label="真实姓名">
              <el-input
                v-model="registerForm.realName"
                placeholder="请输入真实姓名/昵称"
                prefix-icon="UserFilled"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="password" label="密码">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码(6-20位)"
                prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="confirmPassword" label="确认密码">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="phone" label="手机号">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                maxlength="11"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="email" label="邮箱">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                prefix-icon="Message"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item prop="roleCode" label="注册身份">
          <el-radio-group v-model="registerForm.roleCode" class="role-radio-group">
            <el-radio value="STUDENT">
              <span class="role-label">
                <el-icon><User /></el-icon>
                学生
              </span>
            </el-radio>
            <el-radio value="TEACHER">
              <span class="role-label">
                <el-icon><Notebook /></el-icon>
                教师
              </span>
            </el-radio>
            <el-radio value="DATA_ADMIN">
              <span class="role-label">
                <el-icon><Folder /></el-icon>
                资料管理员
              </span>
            </el-radio>
            <el-radio value="GUEST">
              <span class="role-label">
                <el-icon><View /></el-icon>
                访客
              </span>
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-alert
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        >
          <template #default>
            手机号和邮箱至少填写一个，编号将根据身份自动生成
          </template>
        </el-alert>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            立即注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <p>
          已有账号？
          <el-link type="primary" @click="goToLogin">立即登录</el-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  UserFilled,
  Lock,
  Phone,
  Message,
  Notebook,
  Folder,
  View,
  Loading,
  CircleCheck,
  CircleClose
} from '@element-plus/icons-vue'
import { register, checkUsername } from '@/api/auth'

const router = useRouter()

// 表单引用
const registerFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 用户名检查状态
const usernameStatus = ref('') // '', 'checking', 'available', 'unavailable'

// 注册表单
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: '',
  roleCode: 'STUDENT'
})

// 确认密码验证器
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 手机号或邮箱验证器
const validatePhoneOrEmail = (rule, value, callback) => {
  if (!registerForm.phone && !registerForm.email) {
    callback(new Error('手机号和邮箱至少填写一个'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度为3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
    { validator: validatePhoneOrEmail, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
    { max: 100, message: '邮箱长度不能超过100个字符', trigger: 'blur' },
    { validator: validatePhoneOrEmail, trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请选择注册身份', trigger: 'change' }
  ]
}

/**
 * 检查用户名是否可用
 */
const checkUsernameAvailable = async () => {
  if (!registerForm.username || registerForm.username.length < 3) {
    usernameStatus.value = ''
    return
  }

  usernameStatus.value = 'checking'
  try {
    const response = await checkUsername(registerForm.username)
    usernameStatus.value = response.data ? 'available' : 'unavailable'
  } catch (error) {
    usernameStatus.value = ''
    console.error('检查用户名失败:', error)
  }
}

/**
 * 处理注册
 */
const handleRegister = async () => {
  if (!registerFormRef.value) return

  // 检查用户名是否可用
  if (usernameStatus.value === 'unavailable') {
    ElMessage.error('用户名已存在，请更换')
    return
  }

  try {
    await registerFormRef.value.validate()
  } catch (error) {
    return
  }

  loading.value = true
  try {
    await register({
      username: registerForm.username,
      password: registerForm.password,
      realName: registerForm.realName,
      phone: registerForm.phone || undefined,
      email: registerForm.email || undefined,
      roleCode: registerForm.roleCode
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 跳转到登录页
 */
const goToLogin = () => {
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.register-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 100%;
  max-width: 650px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    font-size: 24px;
    color: #303133;
    margin-bottom: 10px;
    font-weight: 600;
  }

  p {
    font-size: 14px;
    color: #909399;
  }
}

.register-form {
  .register-button {
    width: 100%;
    margin-top: 10px;
  }

  .role-radio-group {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;

    :deep(.el-radio) {
      margin-right: 0;
      padding: 10px 15px;
      border: 1px solid #dcdfe6;
      border-radius: 6px;
      transition: all 0.2s;

      &:hover {
        border-color: #409eff;
      }

      &.is-checked {
        border-color: #409eff;
        background-color: #ecf5ff;
      }
    }
  }

  .role-label {
    display: flex;
    align-items: center;
    gap: 5px;
  }

  .username-tip {
    font-size: 12px;
    margin-top: 4px;
    display: flex;
    align-items: center;
    gap: 4px;

    &.checking {
      color: #909399;
    }

    &.available {
      color: #67c23a;
    }

    &.unavailable {
      color: #f56c6c;
    }
  }
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;

  p {
    font-size: 14px;
    color: #606266;
  }
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .register-box {
    padding: 30px 20px;
  }

  .register-header h2 {
    font-size: 20px;
  }

  .el-col {
    width: 100%;
    max-width: 100%;
    flex: 0 0 100%;
  }

  .role-radio-group {
    flex-direction: column;
  }
}
</style>
