<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>虚拟仿真实训教学管理平台</h2>
        <p>Virtual Training Platform</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p class="register-link">
          还没有账号？
          <el-link type="primary" @click="goToRegister">立即注册</el-link>
        </p>
        <p class="home-link">
          <el-link type="info" @click="goToHome">
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </el-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import { HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 表单引用
const loginFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

/**
 * 处理登录
 */
const handleLogin = async () => {
  // 验证表单
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        ElMessage.success('登录成功')

        // 跳转到目标页面或首页
        const redirect = route.query.redirect || '/home'
        router.push(redirect)
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

/**
 * 跳转到注册页
 */
const goToRegister = () => {
  router.push('/register')
}

/**
 * 返回首页
 */
const goToHome = () => {
  router.push('/home')
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

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

.login-form {
  .login-button {
    width: 100%;
    margin-top: 10px;
  }
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;

  p {
    font-size: 13px;
    color: #909399;
  }

  .register-link {
    margin-top: 10px;
    color: #606266;
  }

  .home-link {
    margin-top: 15px;

    .el-icon {
      margin-right: 4px;
      vertical-align: middle;
    }
  }
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .login-box {
    padding: 30px 20px;
  }

  .login-header h2 {
    font-size: 20px;
  }
}
</style>
