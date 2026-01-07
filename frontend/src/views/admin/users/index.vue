<template>
  <div class="admin-users-container">
    <div class="page-header">
      <h1>用户管理</h1>
      <p>User Management</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="search-bar">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索用户名/姓名/编号"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
            v-model="queryParams.roleCode"
            placeholder="角色筛选"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="资料管理员" value="DATA_ADMIN" />
            <el-option label="学生" value="STUDENT" />
            <el-option label="访客" value="GUEST" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
            v-model="queryParams.status"
            placeholder="状态筛选"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-col>
      </el-row>

      <div class="action-buttons">
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          创建用户
        </el-button>
        <el-button
          type="danger"
          :icon="Delete"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
    </div>

    <!-- 用户列表表格 -->
    <el-table
      v-loading="loading"
      :data="userList"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="realName" label="真实姓名" min-width="100" />
      <el-table-column label="编号" min-width="120">
        <template #default="{ row }">
          {{ row.employeeNo || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="性别" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.gender === 1" type="primary" size="small">男</el-tag>
          <el-tag v-else-if="row.gender === 2" type="danger" size="small">女</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column label="角色" min-width="150">
        <template #default="{ row }">
          <el-tag
            v-for="role in row.roles"
            :key="role"
            type="warning"
            size="small"
            style="margin-right: 5px"
          >
            {{ getRoleName(role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">
          {{ row.createTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="warning" size="small" @click="handleResetPassword(row)">
            重置密码
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.current"
      v-model:page-size="queryParams.size"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadUsers"
      @current-change="loadUsers"
    />

    <!-- 创建/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="userForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="isEdit"
            clearable
          />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码（6-20位）"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="userForm.realName"
            placeholder="请输入真实姓名"
            clearable
          />
        </el-form-item>
        <el-form-item label="编号">
          <el-input
            v-model="userForm.employeeNo"
            placeholder="学号/工号/访客ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="userForm.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="userForm.phone"
            placeholder="请输入手机号"
            maxlength="11"
            clearable
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="userForm.email"
            placeholder="请输入邮箱"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select
            v-model="userForm.roleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="resetPasswordVisible"
      title="重置密码"
      width="400px"
    >
      <el-form :model="resetPasswordForm" label-width="100px">
        <el-form-item label="新密码">
          <el-input
            v-model="resetPasswordForm.newPassword"
            type="password"
            placeholder="请输入新密码（6-20位）"
            show-password
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPasswordVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="resettingPassword"
          @click="handleConfirmResetPassword"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete, Refresh } from '@element-plus/icons-vue'
import {
  getUserPage,
  createUser,
  updateUser,
  deleteUser,
  batchDeleteUsers,
  resetPassword,
  getRoleList
} from '@/api/admin'

// 组件状态
const loading = ref(false)
const userList = ref([])
const total = ref(0)
const selectedIds = ref([])
const roleList = ref([])

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  keyword: '',
  roleCode: null,
  status: null
})

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 重置密码对话框
const resetPasswordVisible = ref(false)
const resettingPassword = ref(false)
const currentResetUserId = ref(null)
const resetPasswordForm = reactive({
  newPassword: ''
})

// 用户表单
const userForm = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  employeeNo: '',
  gender: 0,
  phone: '',
  email: '',
  status: 1,
  roleIds: []
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  roleIds: [
    { required: true, message: '请选择至少一个角色', trigger: 'change' }
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

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const response = await getUserPage(queryParams)
    userList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载用户列表失败')
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载角色列表
const loadRoles = async () => {
  try {
    const response = await getRoleList()
    roleList.value = response.data
  } catch (error) {
    ElMessage.error('加载角色列表失败')
    console.error('加载角色列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  queryParams.current = 1
  loadUsers()
}

// 刷新
const handleRefresh = () => {
  queryParams.current = 1
  queryParams.keyword = ''
  queryParams.roleCode = null
  queryParams.status = null
  loadUsers()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 创建用户
const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '创建用户'
  resetForm()
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    employeeNo: row.employeeNo,
    gender: row.gender ?? 0,
    phone: row.phone,
    email: row.email,
    status: row.status,
    roleIds: []  // 需要从后端获取
  })
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(userForm, {
    id: null,
    username: '',
    password: '',
    realName: '',
    employeeNo: '',
    gender: 0,
    phone: '',
    email: '',
    status: 1,
    roleIds: []
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      // 编辑用户
      await updateUser(userForm.id, {
        realName: userForm.realName,
        employeeNo: userForm.employeeNo,
        gender: userForm.gender,
        phone: userForm.phone,
        email: userForm.email,
        status: userForm.status,
        roleIds: userForm.roleIds
      })
      ElMessage.success('用户更新成功')
    } else {
      // 创建用户
      await createUser({
        username: userForm.username,
        password: userForm.password,
        realName: userForm.realName,
        employeeNo: userForm.employeeNo,
        roleIds: userForm.roleIds
      })
      ElMessage.success('用户创建成功')
    }
    dialogVisible.value = false
    loadUsers()
  } catch (error) {
    ElMessage.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
  } finally {
    submitting.value = false
  }
}

// 删除用户
const handleDelete = async (userId) => {
  try {
    await ElMessageBox.confirm('确定删除该用户吗？此操作不可恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await deleteUser(userId)
    ElMessage.success('用户删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${selectedIds.value.length} 个用户吗？此操作不可恢复！`,
      '警告',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )
    await batchDeleteUsers(selectedIds.value)
    ElMessage.success('批量删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 重置密码
const handleResetPassword = (row) => {
  currentResetUserId.value = row.id
  resetPasswordForm.newPassword = ''
  resetPasswordVisible.value = true
}

// 确认重置密码
const handleConfirmResetPassword = async () => {
  if (!resetPasswordForm.newPassword || resetPasswordForm.newPassword.length < 6) {
    ElMessage.warning('请输入6-20位新密码')
    return
  }

  resettingPassword.value = true
  try {
    await resetPassword(currentResetUserId.value, resetPasswordForm.newPassword)
    ElMessage.success('密码重置成功')
    resetPasswordVisible.value = false
  } catch (error) {
    ElMessage.error('重置密码失败')
  } finally {
    resettingPassword.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUsers()
  loadRoles()
})
</script>

<style lang="scss" scoped>
.admin-users-container {
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

  .search-bar {
    margin-bottom: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .el-row {
      margin-bottom: 15px;
    }

    .action-buttons {
      display: flex;
      gap: 10px;
    }
  }

  :deep(.el-table) {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
  }

  :deep(.el-pagination) {
    justify-content: center;
    padding: 20px 0;
  }
}

@media (max-width: 768px) {
  .search-bar {
    .el-col {
      margin-bottom: 10px;
    }

    .action-buttons {
      flex-direction: column;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>
