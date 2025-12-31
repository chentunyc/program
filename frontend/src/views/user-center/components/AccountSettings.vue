<template>
  <div class="account-settings">
    <div class="section-header">
      <h2>账号设置</h2>
    </div>

    <el-divider />

    <!-- 头像设置 -->
    <div class="avatar-section">
      <h3>头像设置</h3>
      <div class="avatar-content">
        <el-avatar :size="100" :src="avatarUrl" class="user-avatar">
          <el-icon :size="40"><User /></el-icon>
        </el-avatar>
        <div class="avatar-upload">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :on-error="handleAvatarError"
            :before-upload="beforeAvatarUpload"
            accept="image/jpeg,image/png,image/gif"
          >
            <el-button type="primary" :loading="uploading">
              <el-icon><Upload /></el-icon>
              {{ uploading ? '上传中...' : '更换头像' }}
            </el-button>
          </el-upload>
          <p class="upload-tip">支持 JPG、PNG、GIF 格式，文件大小不超过 2MB</p>
        </div>
      </div>
    </div>

    <el-divider />

    <!-- 详细信息设置 -->
    <div class="profile-section">
      <div class="section-title">
        <h3>详细信息</h3>
        <el-button
          v-if="!isEditing"
          type="primary"
          :icon="Edit"
          @click="handleEdit"
        >
          编辑信息
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

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>

      <!-- 展示模式 -->
      <div v-else-if="!isEditing" class="profile-display">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="院系/部门">
            {{ userProfile.department || '-' }}
          </el-descriptions-item>
          <!-- 学生专属字段 -->
          <el-descriptions-item v-if="isStudent" label="专业">
            {{ userProfile.major || '-' }}
          </el-descriptions-item>
          <el-descriptions-item v-if="isStudent" label="班级">
            {{ userProfile.className || '-' }}
          </el-descriptions-item>
          <el-descriptions-item v-if="isStudent" label="年级">
            {{ userProfile.grade || '-' }}
          </el-descriptions-item>
          <!-- 通用字段 -->
          <el-descriptions-item label="出生日期">
            {{ userProfile.birthDate || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="身份证号">
            {{ maskIdCard(userProfile.idCard) }}
          </el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">
            {{ userProfile.address || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="2">
            {{ userProfile.introduction || '-' }}
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
              <el-form-item label="院系/部门" prop="department">
                <el-input
                  v-model="editForm.department"
                  placeholder="请输入院系/部门"
                  clearable
                />
              </el-form-item>
            </el-col>
            <!-- 学生专属：专业 -->
            <el-col v-if="isStudent" :span="12">
              <el-form-item label="专业" prop="major">
                <el-input
                  v-model="editForm.major"
                  placeholder="请输入专业"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 学生专属：班级和年级 -->
          <el-row v-if="isStudent" :gutter="20">
            <el-col :span="12">
              <el-form-item label="班级" prop="className">
                <el-input
                  v-model="editForm.className"
                  placeholder="请输入班级"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年级" prop="grade">
                <el-select
                  v-model="editForm.grade"
                  placeholder="请选择年级"
                  clearable
                  style="width: 100%"
                >
                  <el-option
                    v-for="year in gradeOptions"
                    :key="year"
                    :label="year"
                    :value="year"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="出生日期" prop="birthDate">
                <el-date-picker
                  v-model="editForm.birthDate"
                  type="date"
                  placeholder="请选择出生日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                  :disabled-date="disabledDate"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idCard">
                <el-input
                  v-model="editForm.idCard"
                  placeholder="请输入身份证号"
                  clearable
                  maxlength="18"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="地址" prop="address">
            <el-input
              v-model="editForm.address"
              placeholder="请输入地址"
              clearable
            />
          </el-form-item>

          <el-form-item label="个人简介" prop="introduction">
            <el-input
              v-model="editForm.introduction"
              type="textarea"
              placeholder="请输入个人简介"
              :rows="4"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Upload, Edit } from '@element-plus/icons-vue'
import { getUserExtendedProfile, updateUserExtendedProfile, updateAvatar } from '@/api/user'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

// 判断是否为学生角色
const isStudent = computed(() => {
  return userStore.isStudent()
})

// 组件状态
const loading = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const uploading = ref(false)
const formRef = ref(null)

// 用户详细资料
const userProfile = ref({})

// 头像相关
const avatarUrl = computed(() => {
  const avatar = userProfile.value.avatar || userStore.userInfo?.avatar
  if (!avatar) return ''
  // 如果已经是完整URL则直接返回，否则添加API前缀
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }
  // 使用API代理路径访问头像
  return '/api' + avatar
})

const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
  return baseUrl + '/v1/user/avatar'
})

const uploadHeaders = computed(() => {
  return {
    Authorization: `Bearer ${getToken()}`
  }
})

// 年级选项
const gradeOptions = computed(() => {
  const currentYear = new Date().getFullYear()
  const years = []
  for (let i = currentYear; i >= currentYear - 10; i--) {
    years.push(`${i}级`)
  }
  return years
})

// 编辑表单
const editForm = reactive({
  department: '',
  major: '',
  className: '',
  grade: '',
  birthDate: '',
  idCard: '',
  address: '',
  introduction: ''
})

// 表单验证规则
const formRules = {
  department: [
    { max: 100, message: '院系/部门长度不能超过100个字符', trigger: 'blur' }
  ],
  major: [
    { max: 100, message: '专业长度不能超过100个字符', trigger: 'blur' }
  ],
  className: [
    { max: 50, message: '班级长度不能超过50个字符', trigger: 'blur' }
  ],
  idCard: [
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  address: [
    { max: 200, message: '地址长度不能超过200个字符', trigger: 'blur' }
  ],
  introduction: [
    { max: 500, message: '个人简介长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 身份证号脱敏
const maskIdCard = (idCard) => {
  if (!idCard) return '-'
  if (idCard.length <= 6) return idCard
  return idCard.substring(0, 3) + '***********' + idCard.substring(idCard.length - 4)
}

// 禁用未来日期
const disabledDate = (date) => {
  return date.getTime() > Date.now()
}

// 加载用户详细资料
const loadUserProfile = async () => {
  loading.value = true
  try {
    const response = await getUserExtendedProfile()
    userProfile.value = response.data || {}
  } catch (error) {
    console.error('加载用户详细资料失败:', error)
    // 如果接口不存在，使用空对象
    userProfile.value = {}
  } finally {
    loading.value = false
  }
}

// 进入编辑模式
const handleEdit = () => {
  editForm.department = userProfile.value.department || ''
  editForm.major = userProfile.value.major || ''
  editForm.className = userProfile.value.className || ''
  editForm.grade = userProfile.value.grade || ''
  editForm.birthDate = userProfile.value.birthDate || ''
  editForm.idCard = userProfile.value.idCard || ''
  editForm.address = userProfile.value.address || ''
  editForm.introduction = userProfile.value.introduction || ''
  isEditing.value = true
}

// 取消编辑
const handleCancel = () => {
  isEditing.value = false
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 保存修改
const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  saving.value = true
  try {
    const response = await updateUserExtendedProfile(editForm)
    userProfile.value = response.data || editForm
    isEditing.value = false
    ElMessage.success('信息更新成功')
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
    console.error('更新用户详细资料失败:', error)
  } finally {
    saving.value = false
  }
}

// 头像上传前检查
const beforeAvatarUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('头像图片只能是 JPG/PNG/GIF 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }

  uploading.value = true
  return true
}

// 头像上传成功
const handleAvatarSuccess = (response) => {
  uploading.value = false
  if (response.code === 200) {
    userProfile.value.avatar = response.data
    // 更新store中的用户信息
    userStore.userInfo.avatar = response.data
    ElMessage.success('头像更新成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 头像上传失败
const handleAvatarError = () => {
  uploading.value = false
  ElMessage.error('头像上传失败，请重试')
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserProfile()
})
</script>

<style lang="scss" scoped>
.account-settings {
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

  .avatar-section {
    .avatar-content {
      display: flex;
      align-items: center;
      gap: 24px;

      .user-avatar {
        border: 2px solid #e4e7ed;
        flex-shrink: 0;
      }

      .avatar-upload {
        .upload-tip {
          margin: 8px 0 0;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  .profile-section {
    .section-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }
  }

  .loading-container {
    padding: 20px 0;
  }

  .profile-display {
    :deep(.el-descriptions) {
      .el-descriptions__label {
        width: 100px;
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
  }
}

@media (max-width: 768px) {
  .account-settings {
    .avatar-section .avatar-content {
      flex-direction: column;
      align-items: flex-start;
    }

    .profile-edit .el-col {
      width: 100%;
    }
  }
}
</style>
