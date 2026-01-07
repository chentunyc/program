<template>
  <div class="profile-info">
    <div class="section-header">
      <h2>个人信息</h2>
      <el-button
        v-if="!isEditing"
        type="primary"
        :icon="Edit"
        @click="handleEdit"
      >
        编辑资料
      </el-button>
      <div v-else>
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          :loading="saving"
          @click="handleSave"
        >
          保存
        </el-button>
      </div>
    </div>

    <el-divider />

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 展示模式 -->
    <div v-else-if="!isEditing" class="profile-display">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名" :span="1">
          <el-tag type="info">{{ userProfile.username }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="真实姓名" :span="1">
          {{ userProfile.realName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item :label="getIdLabel()" :span="1">
          {{ userProfile.employeeNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="性别" :span="1">
          <el-tag v-if="userProfile.gender === 1" type="primary">男</el-tag>
          <el-tag v-else-if="userProfile.gender === 2" type="danger">女</el-tag>
          <span v-else>未知</span>
        </el-descriptions-item>
        <el-descriptions-item label="手机号" :span="1">
          {{ userProfile.phone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱" :span="1">
          {{ userProfile.email || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="账号状态" :span="1">
          <el-tag v-if="userProfile.status === 1" type="success">正常</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="角色" :span="1">
          <el-tag
            v-for="role in userProfile.roles"
            :key="role"
            type="warning"
            style="margin-right: 8px"
          >
            {{ getRoleName(role) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最后登录时间" :span="1">
          {{ userProfile.lastLoginTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="最后登录IP" :span="1">
          {{ userProfile.lastLoginIp || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">
          {{ userProfile.createTime || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 编辑模式 -->
    <div v-else class="profile-edit">
      <el-form
        ref="formRef"
        :model="editForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="editForm.realName"
                placeholder="请输入真实姓名"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="editForm.gender">
                <el-radio :label="0">未知</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="editForm.phone"
                placeholder="请输入手机号"
                clearable
                maxlength="11"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="editForm.email"
                placeholder="请输入邮箱"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-alert
          title="提示"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <ul style="margin: 0; padding-left: 20px;">
              <li>用户名、{{ getIdLabel() }}等信息不可修改</li>
              <li>修改信息后请点击"保存"按钮</li>
            </ul>
          </template>
        </el-alert>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { getCurrentUserProfile, updateCurrentUserProfile } from '@/api/user'

// 组件状态
const loading = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const formRef = ref(null)

// 用户资料数据
const userProfile = ref({})

// 编辑表单数据
const editForm = reactive({
  realName: '',
  gender: 0,
  phone: '',
  email: ''
})

// 表单验证规则
const formRules = {
  realName: [
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
    { max: 100, message: '邮箱长度不能超过100个字符', trigger: 'blur' }
  ]
}

// 角色名称映射
const getRoleName = (roleCode) => {
  const roleMap = {
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'DATA_ADMIN': '资料管理员',
    'STUDENT': '学生',
    'GUEST': '访客'
  }
  return roleMap[roleCode] || roleCode
}

// 根据角色获取编号标签
const getIdLabel = () => {
  const roles = userProfile.value?.roles || []
  if (roles.includes('STUDENT')) return '学号'
  if (roles.includes('ADMIN') || roles.includes('TEACHER') || roles.includes('DATA_ADMIN')) return '工号'
  if (roles.includes('GUEST')) return '访客ID'
  return '编号'
}

// 加载用户资料
const loadUserProfile = async () => {
  loading.value = true
  try {
    const response = await getCurrentUserProfile()
    userProfile.value = response.data
  } catch (error) {
    ElMessage.error('加载用户资料失败')
    console.error('加载用户资料失败:', error)
  } finally {
    loading.value = false
  }
}

// 进入编辑模式
const handleEdit = () => {
  // 将当前数据填充到编辑表单
  editForm.realName = userProfile.value.realName || ''
  editForm.gender = userProfile.value.gender ?? 0
  editForm.phone = userProfile.value.phone || ''
  editForm.email = userProfile.value.email || ''
  isEditing.value = true
}

// 取消编辑
const handleCancel = () => {
  isEditing.value = false
  // 重置表单验证
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 保存修改
const handleSave = async () => {
  // 表单验证
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  // 准备更新数据 - 仅发送有值的字段
  const updateData = {}
  if (editForm.realName) updateData.realName = editForm.realName
  if (editForm.gender !== null && editForm.gender !== undefined) {
    updateData.gender = editForm.gender
  }
  if (editForm.phone) updateData.phone = editForm.phone
  if (editForm.email) updateData.email = editForm.email

  // 检查是否有修改
  if (Object.keys(updateData).length === 0) {
    ElMessage.warning('没有需要保存的修改')
    return
  }

  saving.value = true
  try {
    const response = await updateCurrentUserProfile(updateData)
    userProfile.value = response.data
    isEditing.value = false
    ElMessage.success('资料更新成功')
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
    console.error('更新用户资料失败:', error)
  } finally {
    saving.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserProfile()
})
</script>

<style lang="scss" scoped>
.profile-info {
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

  .loading-container {
    padding: 20px 0;
  }

  .profile-display {
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
  }

  .profile-edit {
    max-width: 800px;

    :deep(.el-form-item) {
      margin-bottom: 24px;
    }

    :deep(.el-alert) {
      margin-top: 20px;
    }
  }
}

@media (max-width: 768px) {
  .profile-edit {
    .el-col {
      width: 100%;
    }
  }
}
</style>
