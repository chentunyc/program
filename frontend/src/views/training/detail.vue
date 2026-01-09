<template>
  <div class="training-detail-container">
    <!-- 返回按钮 -->
    <div class="back-bar">
      <el-button text @click="goBack">
        <el-icon><ArrowLeft /></el-icon>返回实训中心
      </el-button>
    </div>

    <div v-loading="loading" class="content-wrapper">
      <!-- 项目信息卡片 -->
      <el-card class="project-card" shadow="never">
        <div class="project-header">
          <div class="project-cover">
            <img v-if="projectDetail.coverImage" :src="projectDetail.coverImage" alt="封面" />
            <div v-else class="cover-placeholder">
              <el-icon :size="64"><Notebook /></el-icon>
            </div>
          </div>
          <div class="project-info">
            <div class="title-row">
              <h1>{{ projectDetail.projectName }}</h1>
              <el-tag :type="getStatusType(projectDetail.status)">
                {{ getStatusText(projectDetail.status) }}
              </el-tag>
            </div>
            <p class="description">{{ projectDetail.projectDescription || '暂无描述' }}</p>
            <div class="meta-row">
              <span><el-icon><Calendar /></el-icon> {{ formatDate(projectDetail.projectStartTime) }} ~ {{ formatDate(projectDetail.projectEndTime) }}</span>
              <span><el-icon><Rank /></el-icon> {{ getDifficultyText(projectDetail.difficulty) }}</span>
              <span><el-icon><Clock /></el-icon> 报名时间: {{ formatDateTime(projectDetail.enrollTime) }}</span>
            </div>
            <div class="progress-row">
              <span class="label">完成进度:</span>
              <el-progress
                :percentage="projectDetail.progressPercent || 0"
                :status="projectDetail.progressPercent >= 100 ? 'success' : ''"
                style="flex: 1"
              />
              <span class="text">{{ projectDetail.completedTasks || 0 }}/{{ projectDetail.totalTasks || 0 }}</span>
            </div>
            <div v-if="projectDetail.status === 3" class="score-row">
              <el-icon><Trophy /></el-icon>
              <span class="score">总分: {{ projectDetail.totalScore }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 任务列表 -->
      <el-card class="tasks-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>实训任务</span>
            <el-tag type="info">共 {{ tasks.length }} 个任务</el-tag>
          </div>
        </template>

        <div v-if="tasks.length === 0" class="empty-tasks">
          <el-empty description="暂无任务" />
        </div>

        <div v-else class="task-list">
          <div
            v-for="(task, index) in tasks"
            :key="task.taskId"
            class="task-item"
            :class="{ active: currentTask?.taskId === task.taskId }"
            @click="selectTask(task)"
          >
            <div class="task-index">
              <el-icon v-if="task.status === 2" color="#67c23a"><CircleCheckFilled /></el-icon>
              <el-icon v-else-if="task.status === 1" color="#e6a23c"><Clock /></el-icon>
              <span v-else>{{ index + 1 }}</span>
            </div>
            <div class="task-content">
              <div class="task-name">{{ task.taskName }}</div>
              <div class="task-meta">
                <span>分值: {{ task.scoreWeight }}</span>
                <span v-if="task.timeLimit">限时: {{ task.timeLimit }}分钟</span>
                <el-tag v-if="task.status === 2" type="success" size="small">已批改</el-tag>
                <el-tag v-else-if="task.status === 1" type="warning" size="small">待批改</el-tag>
                <el-tag v-else type="info" size="small">未提交</el-tag>
              </div>
              <div v-if="task.status === 2" class="task-score">
                得分: <span class="score-value">{{ task.score }}</span>
              </div>
            </div>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-card>

      <!-- 任务详情/提交面板 -->
      <el-card v-if="currentTask" class="task-detail-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>{{ currentTask.taskName }}</span>
            <el-tag :type="getTaskStatusType(currentTask.status)">
              {{ getTaskStatusText(currentTask.status) }}
            </el-tag>
          </div>
        </template>

        <div class="task-detail">
          <!-- 任务说明 -->
          <div class="section">
            <h4>任务说明</h4>
            <p>{{ currentTask.taskDescription || '暂无说明' }}</p>
          </div>

          <!-- 任务要求 -->
          <div class="section">
            <h4>任务要求</h4>
            <div class="requirements">
              <span><el-icon><Star /></el-icon> 分值: {{ currentTask.scoreWeight }} 分</span>
              <span v-if="currentTask.timeLimit"><el-icon><Clock /></el-icon> 建议时间: {{ currentTask.timeLimit }} 分钟</span>
            </div>
          </div>

          <!-- 任务附件 -->
          <div v-if="currentTask.taskAttachmentUrl" class="section">
            <h4>任务附件</h4>
            <el-link :href="currentTask.taskAttachmentUrl" target="_blank" type="primary">
              <el-icon><Download /></el-icon> 下载附件
            </el-link>
          </div>

          <!-- 提交区域 -->
          <div v-if="currentTask.status !== 2" class="section submit-section">
            <h4>提交实训报告</h4>
            <el-form ref="submitFormRef" :model="submitForm" label-position="top">
              <el-form-item label="上传报告文件" required>
                <el-upload
                  ref="uploadRef"
                  class="report-upload"
                  :auto-upload="false"
                  :limit="1"
                  :on-change="handleFileChange"
                  :on-remove="handleFileRemove"
                  :on-exceed="handleExceed"
                  accept=".doc,.docx,.pdf"
                  drag
                >
                  <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                  <div class="el-upload__text">
                    将文件拖到此处，或<em>点击上传</em>
                  </div>
                  <template #tip>
                    <div class="el-upload__tip">
                      只能上传 Word (.doc, .docx) 或 PDF (.pdf) 文件
                    </div>
                  </template>
                </el-upload>
              </el-form-item>
              <el-form-item label="耗时(分钟)">
                <el-input-number v-model="submitForm.timeSpent" :min="1" :max="999" />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="submitting"
                  :disabled="!selectedFile"
                  @click="handleSubmit"
                >
                  {{ currentTask.status === 1 ? '重新提交' : '提交报告' }}
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 已批改结果 -->
          <div v-if="currentTask.status === 2" class="section result-section">
            <h4>批改结果</h4>
            <div class="result-card">
              <div class="score-display">
                <span class="label">得分</span>
                <span class="value">{{ currentTask.score }}</span>
                <span class="max">/ {{ currentTask.scoreWeight }}</span>
              </div>
              <div v-if="currentTask.feedback" class="feedback">
                <span class="label">教师评语:</span>
                <p>{{ currentTask.feedback }}</p>
              </div>
              <div class="grader-info">
                <span>评分教师: {{ currentTask.graderName || '-' }}</span>
                <span>评分时间: {{ formatDateTime(currentTask.gradedTime) }}</span>
              </div>
            </div>
          </div>

          <!-- 已提交内容 -->
          <div v-if="currentTask.status >= 1 && currentTask.attachmentUrl" class="section">
            <h4>已提交报告</h4>
            <div class="submitted-content">
              <div class="attachment">
                <el-link :href="currentTask.attachmentUrl" target="_blank" type="primary">
                  <el-icon><Document /></el-icon> 查看已提交的报告
                </el-link>
              </div>
              <div class="submit-info">
                <span>提交时间: {{ formatDateTime(currentTask.submitTime) }}</span>
                <span v-if="currentTask.timeSpent">耗时: {{ currentTask.timeSpent }} 分钟</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled, Document } from '@element-plus/icons-vue'
import {
  getStudentProjectDetail,
  getStudentProjectTasks,
  submitTask,
  startStudentProject,
  uploadTrainingReport
} from '@/api/training'

const route = useRoute()
const router = useRouter()

const projectId = ref(Number(route.params.id))
const loading = ref(false)
const submitting = ref(false)

const projectDetail = ref({})
const tasks = ref([])
const currentTask = ref(null)

const submitFormRef = ref()
const uploadRef = ref()
const selectedFile = ref(null)

const submitForm = reactive({
  taskId: null,
  projectId: null,
  attachmentUrl: '',
  timeSpent: null
})

// 加载项目详情
const loadProjectDetail = async () => {
  loading.value = true
  try {
    const res = await getStudentProjectDetail(projectId.value)
    if (res.code === 200) {
      projectDetail.value = res.data
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载项目详情失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 加载任务列表
const loadTasks = async () => {
  try {
    const res = await getStudentProjectTasks(projectId.value)
    if (res.code === 200) {
      tasks.value = res.data || []
      // 默认选中第一个任务
      if (tasks.value.length > 0 && !currentTask.value) {
        selectTask(tasks.value[0])
      }
    }
  } catch (error) {
    console.error('加载任务列表失败:', error)
  }
}

// 选中任务
const selectTask = (task) => {
  currentTask.value = task
  // 重置提交表单
  submitForm.taskId = task.taskId
  submitForm.projectId = projectId.value
  submitForm.attachmentUrl = ''
  submitForm.timeSpent = task.timeSpent || null
  // 清空上传组件
  selectedFile.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 文件选择变化
const handleFileChange = (file) => {
  // 验证文件类型
  const allowedTypes = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ]
  const allowedExtensions = ['.pdf', '.doc', '.docx']

  const fileName = file.name.toLowerCase()
  const hasValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext))

  if (!hasValidExtension && !allowedTypes.includes(file.raw.type)) {
    ElMessage.error('只能上传 Word 或 PDF 文件')
    uploadRef.value.clearFiles()
    selectedFile.value = null
    return
  }

  selectedFile.value = file.raw
}

// 文件移除
const handleFileRemove = () => {
  selectedFile.value = null
}

// 文件超出限制
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先删除已选文件')
}

// 提交作业
const handleSubmit = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择要上传的报告文件')
    return
  }

  if (!submitForm.taskId) {
    ElMessage.error('任务信息异常，请刷新页面重试')
    return
  }

  submitting.value = true
  try {
    // 如果是第一次提交，先开始项目
    if (projectDetail.value.status === 0) {
      await startStudentProject(projectId.value)
    }

    // 先上传文件
    const uploadRes = await uploadTrainingReport(selectedFile.value)
    if (uploadRes.code !== 200) {
      ElMessage.error(uploadRes.msg || '文件上传失败')
      return
    }

    // 提交任务
    const submitData = {
      taskId: submitForm.taskId,
      projectId: submitForm.projectId,
      attachmentUrl: uploadRes.data,
      timeSpent: submitForm.timeSpent
    }

    const res = await submitTask(submitData)
    if (res.code === 200) {
      ElMessage.success('提交成功')
      // 刷新数据
      await loadProjectDetail()
      await loadTasks()
      // 更新当前任务
      const updatedTask = tasks.value.find(t => t.taskId === submitForm.taskId)
      if (updatedTask) {
        selectTask(updatedTask)
      }
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  router.push('/training')
}

// 格式化
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.substring(0, 10)
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.substring(0, 16).replace('T', ' ')
}

const getDifficultyText = (difficulty) => {
  const texts = { 1: '初级', 2: '中级', 3: '高级' }
  return texts[difficulty] || '未知'
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'primary', 2: 'success', 3: 'warning' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '已报名', 1: '进行中', 2: '已完成', 3: '已评分' }
  return texts[status] || '未知'
}

const getTaskStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success' }
  return types[status] || 'info'
}

const getTaskStatusText = (status) => {
  const texts = { 0: '未提交', 1: '待批改', 2: '已批改' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadProjectDetail()
  loadTasks()
})
</script>

<style lang="scss" scoped>
.training-detail-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.back-bar {
  margin-bottom: 16px;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 350px;
  grid-template-rows: auto 1fr;
  gap: 20px;

  @media (max-width: 1200px) {
    grid-template-columns: 1fr;
  }
}

.project-card {
  grid-column: 1 / -1;

  .project-header {
    display: flex;
    gap: 24px;

    @media (max-width: 768px) {
      flex-direction: column;
    }
  }

  .project-cover {
    width: 240px;
    height: 160px;
    border-radius: 8px;
    overflow: hidden;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .cover-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: rgba(255, 255, 255, 0.8);
    }
  }

  .project-info {
    flex: 1;

    .title-row {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 8px;

      h1 {
        font-size: 22px;
        margin: 0;
        color: #303133;
      }
    }

    .description {
      color: #606266;
      font-size: 14px;
      margin: 0 0 12px;
      line-height: 1.6;
    }

    .meta-row {
      display: flex;
      gap: 20px;
      color: #909399;
      font-size: 13px;
      margin-bottom: 12px;
      flex-wrap: wrap;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }

    .progress-row {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 8px;

      .label {
        color: #606266;
        font-size: 14px;
      }

      .text {
        color: #909399;
        font-size: 13px;
      }
    }

    .score-row {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #e6a23c;
      font-size: 16px;
      font-weight: 600;

      .score {
        color: #f56c6c;
      }
    }
  }
}

.tasks-card {
  grid-row: 2;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .task-list {
    max-height: 500px;
    overflow-y: auto;
  }

  .task-item {
    display: flex;
    align-items: center;
    padding: 12px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    border: 1px solid transparent;

    &:hover {
      background: #f5f7fa;
    }

    &.active {
      background: #ecf5ff;
      border-color: #409eff;
    }

    .task-index {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: #f0f2f5;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      color: #606266;
      margin-right: 12px;
      flex-shrink: 0;
    }

    .task-content {
      flex: 1;
      min-width: 0;

      .task-name {
        font-size: 14px;
        color: #303133;
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .task-meta {
        font-size: 12px;
        color: #909399;
        display: flex;
        gap: 8px;
        align-items: center;
        flex-wrap: wrap;
      }

      .task-score {
        font-size: 12px;
        color: #67c23a;
        margin-top: 4px;

        .score-value {
          font-weight: 600;
        }
      }
    }

    .arrow {
      color: #c0c4cc;
    }
  }
}

.task-detail-card {
  grid-row: 2;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .task-detail {
    .section {
      margin-bottom: 24px;

      h4 {
        font-size: 15px;
        color: #303133;
        margin: 0 0 12px;
        padding-bottom: 8px;
        border-bottom: 1px solid #ebeef5;
      }

      p {
        color: #606266;
        font-size: 14px;
        line-height: 1.6;
        margin: 0;
      }

      .requirements {
        display: flex;
        gap: 24px;
        color: #606266;
        font-size: 14px;

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }

    .submit-section {
      .report-upload {
        width: 100%;

        :deep(.el-upload-dragger) {
          width: 100%;
        }
      }
    }

    .result-section {
      .result-card {
        background: #f0f9eb;
        border-radius: 8px;
        padding: 16px;

        .score-display {
          display: flex;
          align-items: baseline;
          gap: 4px;
          margin-bottom: 12px;

          .label {
            font-size: 14px;
            color: #606266;
          }

          .value {
            font-size: 32px;
            font-weight: 600;
            color: #67c23a;
          }

          .max {
            font-size: 16px;
            color: #909399;
          }
        }

        .feedback {
          margin-bottom: 12px;

          .label {
            font-size: 13px;
            color: #909399;
          }

          p {
            margin: 8px 0 0;
            color: #606266;
            font-size: 14px;
          }
        }

        .grader-info {
          font-size: 12px;
          color: #909399;
          display: flex;
          gap: 16px;
        }
      }
    }

    .submitted-content {
      background: #f5f7fa;
      border-radius: 8px;
      padding: 16px;

      .attachment {
        margin-bottom: 12px;
      }

      .submit-info {
        font-size: 12px;
        color: #909399;
        display: flex;
        gap: 16px;
      }
    }
  }
}

.empty-tasks {
  padding: 40px 0;
}
</style>
