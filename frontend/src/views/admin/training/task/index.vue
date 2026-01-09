<template>
  <div class="admin-task-container">
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1>任务管理</h1>
          <p>Training Task Management</p>
        </div>
        <el-button v-if="currentProjectId" type="text" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回项目列表
        </el-button>
      </div>
    </div>

    <!-- 项目选择卡片 - 当没有选择项目时显示引导 -->
    <div class="project-selector-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="16" :md="12">
          <div class="selector-wrapper">
            <span class="selector-label">
              <el-icon><FolderOpened /></el-icon>
              当前项目：
            </span>
            <el-select
              v-model="selectedProjectId"
              placeholder="请选择要管理的项目"
              filterable
              style="flex: 1; min-width: 200px"
              @change="handleProjectChange"
            >
              <el-option
                v-for="project in projectOptions"
                :key="project.id"
                :label="project.projectName"
                :value="project.id"
              >
                <div class="project-option">
                  <span>{{ project.projectName }}</span>
                  <el-tag
                    :type="getProjectStatusType(project.status)"
                    size="small"
                    style="margin-left: 8px"
                  >
                    {{ getProjectStatusName(project.status) }}
                  </el-tag>
                </div>
              </el-option>
            </el-select>
          </div>
        </el-col>
        <el-col v-if="currentProjectId" :xs="24" :sm="8" :md="12">
          <div class="project-meta">
            <el-tag :type="getProjectStatusType(projectStatus)" size="large">
              {{ getProjectStatusName(projectStatus) }}
            </el-tag>
            <span class="weight-info">
              权重总计：
              <span :class="{ 'weight-ok': totalWeight === 100, 'weight-error': totalWeight !== 100 }">
                {{ totalWeight }}
              </span>
              / 100 分
              <el-tooltip v-if="totalWeight !== 100" content="所有任务权重之和必须等于100分才能开始项目" placement="top">
                <el-icon class="warning-icon"><Warning /></el-icon>
              </el-tooltip>
              <el-icon v-else class="success-icon"><CircleCheck /></el-icon>
            </span>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 未选择项目时的引导提示 -->
    <div v-if="!currentProjectId" class="empty-guide">
      <el-empty description="请先选择一个项目">
        <template #image>
          <el-icon style="font-size: 80px; color: #409eff"><FolderOpened /></el-icon>
        </template>
        <div class="guide-text">
          <p>任务需要归属于一个实训项目</p>
          <p>请在上方下拉框中选择项目，或前往项目管理创建新项目</p>
        </div>
        <el-button type="primary" @click="router.push('/admin/training/project')">
          <el-icon><Plus /></el-icon>
          前往项目管理
        </el-button>
      </el-empty>
    </div>

    <!-- 已选择项目时显示任务管理 -->
    <template v-else>
      <!-- 搜索和操作栏 -->
      <div class="search-bar">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8">
            <el-input
              v-model="queryParams.keyword"
              placeholder="搜索任务名称"
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
              v-model="queryParams.status"
              placeholder="任务状态"
              clearable
              style="width: 100%"
              @change="handleSearch"
            >
              <el-option label="未发布" :value="0" />
              <el-option label="已发布" :value="1" />
            </el-select>
          </el-col>
        </el-row>

        <div class="action-buttons">
          <el-tooltip
            :content="getCreateButtonTooltip()"
            :disabled="projectStatus === 0"
            placement="top"
          >
            <span>
              <el-button
                type="primary"
                :icon="Plus"
                :disabled="projectStatus !== 0"
                @click="handleCreate"
              >
                创建任务
              </el-button>
            </span>
          </el-tooltip>
          <el-button
            type="danger"
            :icon="Delete"
            :disabled="selectedIds.length === 0 || projectStatus !== 0"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
          <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        </div>
      </div>

      <!-- 提示信息 -->
      <el-alert
        v-if="projectStatus === 1"
        title="项目进行中"
        description="项目已开始，只能修改任务内容，不能添加或删除任务"
        type="warning"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      />
      <el-alert
        v-if="projectStatus === 2"
        title="项目已结束"
        description="项目已结束，无法进行任何修改操作"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      />

      <!-- 任务列表表格 -->
      <el-table
        v-loading="loading"
        :data="taskList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" :selectable="canSelect" />
        <el-table-column prop="sortOrder" label="序号" width="70" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="权重" width="100">
          <template #default="{ row }">
            <el-tag type="warning">{{ row.scoreWeight }} 分</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时限" width="100">
          <template #default="{ row }">
            {{ row.timeLimit ? row.timeLimit + ' 分钟' : '不限' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ row.createTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :disabled="projectStatus === 2"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              :disabled="projectStatus === 2"
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              size="small"
              :disabled="projectStatus === 2"
              @click="handleUnpublish(row)"
            >
              取消发布
            </el-button>
            <el-button
              type="info"
              size="small"
              :disabled="projectStatus !== 0"
              @click="handleSortUp(row)"
            >
              上移
            </el-button>
            <el-button
              type="danger"
              size="small"
              :disabled="projectStatus !== 0"
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态提示 -->
      <el-empty v-if="!loading && taskList.length === 0" description="暂无任务">
        <el-button v-if="projectStatus === 0" type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          创建第一个任务
        </el-button>
      </el-empty>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadTasks"
        @current-change="loadTasks"
      />
    </template>

    <!-- 创建/编辑任务对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="taskForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="任务名称" prop="taskName">
          <el-input
            v-model="taskForm.taskName"
            placeholder="请输入任务名称"
            maxlength="100"
            show-word-limit
            clearable
          />
        </el-form-item>
        <el-form-item label="分数权重" prop="scoreWeight">
          <el-input-number
            v-model="taskForm.scoreWeight"
            :min="1"
            :max="100"
            :precision="1"
            placeholder="分数权重"
          />
          <span class="form-tip">（所有任务权重之和需为100分）</span>
        </el-form-item>
        <el-form-item label="时间限制">
          <el-input-number
            v-model="taskForm.timeLimit"
            :min="0"
            :max="9999"
            placeholder="时间限制（分钟）"
          />
          <span class="form-tip">（0表示不限时）</span>
        </el-form-item>
        <el-form-item label="排序序号">
          <el-input-number
            v-model="taskForm.sortOrder"
            :min="1"
            :max="999"
            placeholder="排序序号"
          />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入任务描述和要求"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="附件地址">
          <el-input
            v-model="taskForm.attachmentUrl"
            placeholder="请输入附件资源URL"
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Plus, Delete, Refresh, ArrowLeft,
  FolderOpened, Warning, CircleCheck
} from '@element-plus/icons-vue'
import {
  getTaskPage,
  createTask,
  updateTask,
  deleteTask,
  batchDeleteTask,
  publishTask,
  unpublishTask,
  updateTaskSort,
  getProjectPage,
  getProjectById
} from '@/api/training'

const route = useRoute()
const router = useRouter()

// 项目相关状态
const selectedProjectId = ref(null)
const currentProjectId = ref(null)
const projectName = ref('')
const projectStatus = ref(0)
const projectOptions = ref([])

// 组件状态
const loading = ref(false)
const taskList = ref([])
const total = ref(0)
const selectedIds = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: null
})

// 计算权重总和
const totalWeight = computed(() => {
  return taskList.value.reduce((sum, task) => sum + (parseFloat(task.scoreWeight) || 0), 0)
})

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 任务表单
const taskForm = reactive({
  id: null,
  projectId: null,
  taskName: '',
  description: '',
  scoreWeight: 10,
  timeLimit: 0,
  sortOrder: 1,
  attachmentUrl: ''
})

// 表单验证规则
const formRules = {
  taskName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' },
    { max: 100, message: '任务名称不能超过100个字符', trigger: 'blur' }
  ],
  scoreWeight: [
    { required: true, message: '请输入分数权重', trigger: 'blur' }
  ]
}

// 获取项目状态类型
const getProjectStatusType = (status) => {
  const map = { 0: 'info', 1: 'primary', 2: 'success' }
  return map[status] || 'info'
}

// 获取项目状态名称
const getProjectStatusName = (status) => {
  const map = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return map[status] || '未知'
}

// 获取创建按钮提示
const getCreateButtonTooltip = () => {
  if (projectStatus.value === 1) return '项目进行中，无法添加新任务'
  if (projectStatus.value === 2) return '项目已结束，无法添加新任务'
  return ''
}

// 是否可以选中
const canSelect = () => {
  return projectStatus.value === 0
}

// 返回项目列表
const goBack = () => {
  router.push('/admin/training/project')
}

// 加载项目列表
const loadProjectOptions = async () => {
  try {
    const response = await getProjectPage({ pageNum: 1, pageSize: 1000 })
    projectOptions.value = response.data.records
  } catch (error) {
    console.error('加载项目列表失败:', error)
  }
}

// 加载项目详情
const loadProjectDetail = async (id) => {
  try {
    const response = await getProjectById(id)
    projectStatus.value = response.data.status
    projectName.value = response.data.projectName
  } catch (error) {
    console.error('加载项目详情失败:', error)
  }
}

// 处理项目切换
const handleProjectChange = async (projectId) => {
  if (projectId) {
    currentProjectId.value = projectId
    await loadProjectDetail(projectId)
    // 更新URL但不刷新页面
    const project = projectOptions.value.find(p => p.id === projectId)
    if (project) {
      router.replace({
        path: '/admin/training/task',
        query: { projectId, projectName: project.projectName }
      })
    }
    loadTasks()
  } else {
    currentProjectId.value = null
    projectName.value = ''
    projectStatus.value = 0
    taskList.value = []
    total.value = 0
    router.replace({ path: '/admin/training/task' })
  }
}

// 加载任务列表
const loadTasks = async () => {
  if (!currentProjectId.value) return

  loading.value = true
  try {
    const params = {
      ...queryParams,
      projectId: currentProjectId.value
    }
    const response = await getTaskPage(params)
    taskList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载任务列表失败')
    console.error('加载任务列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadTasks()
}

// 刷新
const handleRefresh = () => {
  queryParams.pageNum = 1
  queryParams.keyword = ''
  queryParams.status = null
  loadTasks()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 创建任务
const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '创建任务'
  resetForm()
  const maxSort = Math.max(...taskList.value.map(t => t.sortOrder || 0), 0)
  taskForm.sortOrder = maxSort + 1
  dialogVisible.value = true
}

// 编辑任务
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑任务'
  Object.assign(taskForm, {
    id: row.id,
    projectId: row.projectId,
    taskName: row.taskName,
    description: row.description || '',
    scoreWeight: row.scoreWeight,
    timeLimit: row.timeLimit || 0,
    sortOrder: row.sortOrder || 1,
    attachmentUrl: row.attachmentUrl || ''
  })
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(taskForm, {
    id: null,
    projectId: currentProjectId.value,
    taskName: '',
    description: '',
    scoreWeight: 10,
    timeLimit: 0,
    sortOrder: 1,
    attachmentUrl: ''
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
    const data = { ...taskForm }
    if (!isEdit.value) {
      data.projectId = currentProjectId.value
    }
    if (isEdit.value) {
      await updateTask(data)
      ElMessage.success('任务更新成功')
    } else {
      await createTask(data)
      ElMessage.success('任务创建成功')
    }
    dialogVisible.value = false
    loadTasks()
  } catch (error) {
    ElMessage.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
  } finally {
    submitting.value = false
  }
}

// 删除任务
const handleDelete = async (taskId) => {
  try {
    await ElMessageBox.confirm('确定删除该任务吗？此操作不可恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await deleteTask(taskId)
    ElMessage.success('任务删除成功')
    loadTasks()
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
      `确定删除选中的 ${selectedIds.value.length} 个任务吗？此操作不可恢复！`,
      '警告',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )
    await batchDeleteTask(selectedIds.value)
    ElMessage.success('批量删除成功')
    loadTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 发布任务
const handlePublish = async (row) => {
  try {
    await publishTask(row.id)
    ElMessage.success('任务已发布')
    loadTasks()
  } catch (error) {
    ElMessage.error(error.message || '发布失败')
  }
}

// 取消发布
const handleUnpublish = async (row) => {
  try {
    await unpublishTask(row.id)
    ElMessage.success('已取消发布')
    loadTasks()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 上移排序
const handleSortUp = async (row) => {
  const currentIndex = taskList.value.findIndex(t => t.id === row.id)
  if (currentIndex <= 0) {
    ElMessage.info('已经是第一个了')
    return
  }
  const prevTask = taskList.value[currentIndex - 1]
  try {
    await updateTaskSort(row.id, prevTask.sortOrder)
    await updateTaskSort(prevTask.id, row.sortOrder)
    ElMessage.success('排序调整成功')
    loadTasks()
  } catch (error) {
    ElMessage.error('调整排序失败')
  }
}

// 监听路由参数变化
watch(
  () => route.query,
  async (query) => {
    // 确保项目列表已加载
    if (projectOptions.value.length === 0) {
      await loadProjectOptions()
    }

    if (query.projectId) {
      const pid = parseInt(query.projectId)
      selectedProjectId.value = pid
      currentProjectId.value = pid
      projectName.value = query.projectName || ''
      await loadProjectDetail(pid)
      loadTasks()
    } else {
      selectedProjectId.value = null
      currentProjectId.value = null
      projectName.value = ''
      projectStatus.value = 0
      taskList.value = []
    }
  },
  { immediate: true }
)

// 组件挂载时加载项目列表
onMounted(() => {
  loadProjectOptions()
})
</script>

<style lang="scss" scoped>
.admin-task-container {
  .page-header {
    margin-bottom: 20px;
    padding-bottom: 20px;
    padding-left: 30px;
    padding-top: 10px;
    border-bottom: 2px solid #e4e7ed;

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
    }

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

  .project-selector-card {
    margin-bottom: 20px;
    padding: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);

    .selector-wrapper {
      display: flex;
      align-items: center;
      gap: 12px;

      .selector-label {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #fff;
        font-size: 16px;
        font-weight: 500;
        white-space: nowrap;
      }
    }

    .project-meta {
      display: flex;
      align-items: center;
      gap: 20px;
      justify-content: flex-end;

      .weight-info {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 14px;
        color: #fff;

        .weight-ok {
          color: #95f204;
          font-weight: bold;
          font-size: 18px;
        }

        .weight-error {
          color: #ffd93d;
          font-weight: bold;
          font-size: 18px;
        }

        .warning-icon {
          color: #ffd93d;
          font-size: 18px;
          margin-left: 4px;
        }

        .success-icon {
          color: #95f204;
          font-size: 18px;
          margin-left: 4px;
        }
      }
    }

    .project-option {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
    }
  }

  .empty-guide {
    padding: 60px 20px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    text-align: center;

    .guide-text {
      margin: 20px 0;
      color: #909399;
      line-height: 1.8;

      p {
        margin: 0;
      }
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

  :deep(.el-empty) {
    padding: 40px 0;
  }

  .form-tip {
    margin-left: 10px;
    color: #909399;
    font-size: 12px;
  }
}

@media (max-width: 768px) {
  .project-selector-card {
    .project-meta {
      margin-top: 15px;
      justify-content: flex-start;
    }
  }

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
