<template>
  <div class="admin-project-container">
    <div class="page-header">
      <h1>项目管理</h1>
      <p>Training Project Management</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="search-bar">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索项目名称/编码"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
            v-model="queryParams.category"
            placeholder="项目分类"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option label="软件开发" value="软件开发" />
            <el-option label="网络工程" value="网络工程" />
            <el-option label="数据分析" value="数据分析" />
            <el-option label="人工智能" value="人工智能" />
            <el-option label="物联网" value="物联网" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
            v-model="queryParams.difficulty"
            placeholder="难度等级"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option label="初级" :value="1" />
            <el-option label="中级" :value="2" />
            <el-option label="高级" :value="3" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
            v-model="queryParams.status"
            placeholder="项目状态"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-col>
      </el-row>

      <div class="action-buttons">
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          创建项目
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

    <!-- 项目列表表格 -->
    <el-table
      v-loading="loading"
      :data="projectList"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="projectCode" label="项目编码" width="150" />
      <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column label="难度" width="80">
        <template #default="{ row }">
          <el-tag :type="getDifficultyType(row.difficulty)" size="small">
            {{ row.difficultyName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="权重" width="100">
        <template #default="{ row }">
          <el-progress
            :percentage="row.totalWeight || 0"
            :color="getWeightColor(row.totalWeight)"
            :stroke-width="10"
          />
        </template>
      </el-table-column>
      <el-table-column prop="totalTasks" label="任务数" width="80" />
      <el-table-column prop="memberCount" label="参与人数" width="90" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)" size="small">
            {{ row.statusName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="managerName" label="负责人" width="100" />
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">
          {{ row.createTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="info" size="small" @click="handleManageTasks(row)">
            任务
          </el-button>
          <el-button
            v-if="row.status === 0"
            type="success"
            size="small"
            @click="handleStart(row)"
          >
            开始
          </el-button>
          <el-button
            v-if="row.status === 1"
            type="warning"
            size="small"
            @click="handleEnd(row)"
          >
            结束
          </el-button>
          <el-button
            type="danger"
            size="small"
            :disabled="row.status !== 0"
            @click="handleDelete(row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.pageNum"
      v-model:page-size="queryParams.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadProjects"
      @current-change="loadProjects"
    />

    <!-- 创建/编辑项目对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="projectForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model="projectForm.projectName"
            placeholder="请输入项目名称"
            maxlength="100"
            show-word-limit
            clearable
          />
        </el-form-item>
        <el-form-item label="项目分类" prop="category">
          <el-select v-model="projectForm.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="软件开发" value="软件开发" />
            <el-option label="网络工程" value="网络工程" />
            <el-option label="数据分析" value="数据分析" />
            <el-option label="人工智能" value="人工智能" />
            <el-option label="物联网" value="物联网" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级" prop="difficulty">
          <el-radio-group v-model="projectForm.difficulty">
            <el-radio :label="1">初级</el-radio>
            <el-radio :label="2">中级</el-radio>
            <el-radio :label="3">高级</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number
            v-model="projectForm.maxMembers"
            :min="1"
            :max="1000"
            placeholder="最大参与人数"
          />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input
            v-model="projectForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入项目描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-input
            v-model="projectForm.coverImage"
            placeholder="请输入封面图片URL"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete, Refresh } from '@element-plus/icons-vue'
import {
  getProjectPage,
  createProject,
  updateProject,
  deleteProject,
  batchDeleteProject,
  startProject,
  endProject
} from '@/api/training'

const router = useRouter()

// 组件状态
const loading = ref(false)
const projectList = ref([])
const total = ref(0)
const selectedIds = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  category: null,
  difficulty: null,
  status: null
})

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 项目表单
const projectForm = reactive({
  id: null,
  projectName: '',
  category: '',
  difficulty: 1,
  maxMembers: 50,
  description: '',
  coverImage: ''
})

// 表单验证规则
const formRules = {
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { max: 100, message: '项目名称不能超过100个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择项目分类', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ]
}

// 难度类型映射
const getDifficultyType = (difficulty) => {
  const map = { 1: 'success', 2: 'warning', 3: 'danger' }
  return map[difficulty] || 'info'
}

// 状态类型映射
const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'primary', 2: 'success' }
  return map[status] || 'info'
}

// 权重颜色
const getWeightColor = (weight) => {
  if (weight === 100) return '#67c23a'
  if (weight >= 80) return '#e6a23c'
  return '#f56c6c'
}

// 加载项目列表
const loadProjects = async () => {
  loading.value = true
  try {
    const response = await getProjectPage(queryParams)
    projectList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载项目列表失败')
    console.error('加载项目列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadProjects()
}

// 刷新
const handleRefresh = () => {
  queryParams.pageNum = 1
  queryParams.keyword = ''
  queryParams.category = null
  queryParams.difficulty = null
  queryParams.status = null
  loadProjects()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 创建项目
const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '创建项目'
  resetForm()
  dialogVisible.value = true
}

// 编辑项目
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑项目'
  Object.assign(projectForm, {
    id: row.id,
    projectName: row.projectName,
    category: row.category,
    difficulty: row.difficulty,
    maxMembers: row.maxMembers || 50,
    description: row.description || '',
    coverImage: row.coverImage || ''
  })
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(projectForm, {
    id: null,
    projectName: '',
    category: '',
    difficulty: 1,
    maxMembers: 50,
    description: '',
    coverImage: ''
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
      await updateProject(projectForm)
      ElMessage.success('项目更新成功')
    } else {
      await createProject(projectForm)
      ElMessage.success('项目创建成功')
    }
    dialogVisible.value = false
    loadProjects()
  } catch (error) {
    ElMessage.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
  } finally {
    submitting.value = false
  }
}

// 删除项目
const handleDelete = async (projectId) => {
  try {
    await ElMessageBox.confirm('确定删除该项目吗？此操作不可恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await deleteProject(projectId)
    ElMessage.success('项目删除成功')
    loadProjects()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${selectedIds.value.length} 个项目吗？此操作不可恢复！`,
      '警告',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )
    await batchDeleteProject(selectedIds.value)
    ElMessage.success('批量删除成功')
    loadProjects()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 开始项目
const handleStart = async (row) => {
  if (row.totalWeight !== 100) {
    ElMessage.warning(`任务权重总和必须为100分，当前为${row.totalWeight || 0}分`)
    return
  }
  try {
    await ElMessageBox.confirm('确定开始该项目吗？开始后将无法添加或删除任务。', '提示', {
      type: 'info',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await startProject(row.id)
    ElMessage.success('项目已开始')
    loadProjects()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

// 结束项目
const handleEnd = async (row) => {
  try {
    await ElMessageBox.confirm('确定结束该项目吗？结束后将无法进行任何修改。', '警告', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await endProject(row.id)
    ElMessage.success('项目已结束')
    loadProjects()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

// 管理任务
const handleManageTasks = (row) => {
  router.push(`/admin/training/task?projectId=${row.id}&projectName=${encodeURIComponent(row.projectName)}`)
}

// 组件挂载时加载数据
onMounted(() => {
  loadProjects()
})
</script>

<style lang="scss" scoped>
.admin-project-container {
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
