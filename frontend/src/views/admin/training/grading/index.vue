<template>
  <div class="grading-container">
    <div class="page-header">
      <h2>实训批改</h2>
      <p>批改学生提交的实训作业</p>
    </div>

    <div class="content-layout">
      <!-- 左侧: 项目和学生筛选 -->
      <el-card class="filter-card" shadow="never">
        <template #header>
          <span>筛选条件</span>
        </template>

        <el-form label-position="top">
          <el-form-item label="选择项目">
            <el-select
              v-model="selectedProjectId"
              placeholder="请选择项目"
              clearable
              style="width: 100%"
              @change="handleProjectChange"
            >
              <el-option
                v-for="project in projects"
                :key="project.id"
                :label="project.projectName"
                :value="project.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item v-if="selectedProjectId" label="选择任务">
            <el-select
              v-model="selectedTaskId"
              placeholder="全部任务"
              clearable
              style="width: 100%"
              @change="handleTaskChange"
            >
              <el-option
                v-for="task in projectTasks"
                :key="task.id"
                :label="task.taskName"
                :value="task.id"
              >
                <span>{{ task.taskName }}</span>
                <el-tag size="small" style="margin-left: 8px">{{ task.scoreWeight }}分</el-tag>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="学生姓名">
            <el-input
              v-model="queryParams.studentName"
              placeholder="搜索学生"
              clearable
              @keyup.enter="loadSubmissions"
            />
          </el-form-item>

          <el-form-item label="批改状态">
            <el-select
              v-model="queryParams.status"
              placeholder="全部状态"
              clearable
              style="width: 100%"
            >
              <el-option label="待批改" :value="1" />
              <el-option label="已批改" :value="2" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" style="width: 100%" @click="loadSubmissions">
              <el-icon><Search /></el-icon> 搜索
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 学生列表 -->
        <div v-if="selectedProjectId" class="student-section">
          <div class="section-title">
            <span>参与学生</span>
            <el-tag type="info" size="small">{{ students.length }}人</el-tag>
          </div>
          <div v-loading="studentsLoading" class="student-list">
            <div
              v-for="student in students"
              :key="student.id"
              class="student-item"
              :class="{ active: selectedStudentId === student.studentId }"
              @click="selectStudent(student)"
            >
              <el-avatar :size="36" icon="User" />
              <div class="student-info">
                <div class="name">{{ student.studentName || student.studentUsername }}</div>
                <div class="progress">
                  <el-progress
                    :percentage="student.progressPercent || 0"
                    :stroke-width="4"
                    :show-text="false"
                  />
                  <span>{{ student.completedTasks }}/{{ student.totalTasks }}</span>
                </div>
              </div>
              <el-tag
                :type="getStudentStatusType(student.status)"
                size="small"
              >
                {{ getStudentStatusText(student.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 右侧: 提交列表和批改 -->
      <div class="main-content">
        <!-- 提交列表 -->
        <el-card class="submissions-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>提交记录</span>
              <el-tag type="info">共 {{ pagination.total }} 条</el-tag>
            </div>
          </template>

          <el-table
            v-loading="submissionsLoading"
            :data="submissions"
            stripe
            @row-click="handleRowClick"
          >
            <el-table-column label="学生" width="120">
              <template #default="{ row }">
                <div class="student-cell">
                  <span>{{ row.studentName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="任务" prop="taskName" min-width="150" show-overflow-tooltip />
            <el-table-column label="项目" prop="projectName" width="150" show-overflow-tooltip />
            <el-table-column label="分值" prop="scoreWeight" width="70" align="center" />
            <el-table-column label="提交时间" width="160">
              <template #default="{ row }">
                {{ formatDateTime(row.submitTime) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 2 ? 'success' : 'warning'" size="small">
                  {{ row.status === 2 ? '已批改' : '待批改' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="得分" width="80" align="center">
              <template #default="{ row }">
                <span v-if="row.status === 2" class="score">{{ row.score }}</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  size="small"
                  link
                  @click.stop="openGradeDialog(row)"
                >
                  {{ row.status === 2 ? '查看' : '批改' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            background
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="loadSubmissions"
            @current-change="loadSubmissions"
          />
        </el-card>
      </div>
    </div>

    <!-- 批改对话框 -->
    <el-dialog
      v-model="gradeDialogVisible"
      :title="currentSubmission?.status === 2 ? '查看批改结果' : '批改作业'"
      width="700px"
      destroy-on-close
    >
      <div v-if="currentSubmission" class="grade-dialog-content">
        <!-- 任务信息 -->
        <div class="info-section">
          <h4>任务信息</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="任务名称">{{ currentSubmission.taskName }}</el-descriptions-item>
            <el-descriptions-item label="分值">{{ currentSubmission.scoreWeight }}</el-descriptions-item>
            <el-descriptions-item label="学生">{{ currentSubmission.studentName }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatDateTime(currentSubmission.submitTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 任务描述 -->
        <div class="info-section">
          <h4>任务描述</h4>
          <p class="description">{{ currentSubmission.taskDescription || '暂无描述' }}</p>
        </div>

        <!-- 学生提交内容 -->
        <div class="info-section">
          <h4>学生提交内容</h4>
          <div class="submitted-content">
            <p>{{ currentSubmission.content || '无文字内容' }}</p>
            <el-link
              v-if="currentSubmission.attachmentUrl"
              :href="currentSubmission.attachmentUrl"
              target="_blank"
              type="primary"
            >
              <el-icon><Link /></el-icon> 查看附件
            </el-link>
          </div>
        </div>

        <!-- 评分表单 -->
        <div class="info-section">
          <h4>{{ currentSubmission.status === 2 ? '批改结果' : '评分' }}</h4>
          <el-form
            ref="gradeFormRef"
            :model="gradeForm"
            :rules="gradeRules"
            label-position="top"
            :disabled="currentSubmission.status === 2"
          >
            <el-form-item label="得分(0-100)" prop="score">
              <el-input-number
                v-model="gradeForm.score"
                :min="0"
                :max="100"
                :precision="1"
                style="width: 200px"
              />
              <span class="score-hint">
                实际得分 = 输入分数 × {{ currentSubmission.scoreWeight }} / 100
              </span>
            </el-form-item>
            <el-form-item label="评语" prop="feedback">
              <el-input
                v-model="gradeForm.feedback"
                type="textarea"
                :rows="4"
                placeholder="请输入评语(可选)"
              />
            </el-form-item>
          </el-form>

          <!-- 已批改信息 -->
          <div v-if="currentSubmission.status === 2" class="graded-info">
            <span>评分教师: {{ currentSubmission.graderName || '-' }}</span>
            <span>评分时间: {{ formatDateTime(currentSubmission.gradedTime) }}</span>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="gradeDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentSubmission?.status !== 2"
          type="primary"
          :loading="grading"
          @click="handleGrade"
        >
          提交评分
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getGradingProjects,
  getProjectStudents,
  getSubmissions,
  getSubmissionDetail,
  gradeSubmission,
  getTasksByProjectId
} from '@/api/training'

// 项目列表
const projects = ref([])
const selectedProjectId = ref(null)

// 任务列表
const projectTasks = ref([])
const selectedTaskId = ref(null)

// 学生列表
const students = ref([])
const studentsLoading = ref(false)
const selectedStudentId = ref(null)

// 提交列表
const submissions = ref([])
const submissionsLoading = ref(false)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  projectId: null,
  taskId: null,
  studentName: '',
  status: null
})
const pagination = reactive({
  total: 0
})

// 批改对话框
const gradeDialogVisible = ref(false)
const currentSubmission = ref(null)
const gradeFormRef = ref()
const grading = ref(false)
const gradeForm = reactive({
  submissionId: null,
  score: null,
  feedback: ''
})
const gradeRules = {
  score: [{ required: true, message: '请输入得分', trigger: 'blur' }]
}

// 加载项目列表
const loadProjects = async () => {
  try {
    const res = await getGradingProjects()
    if (res.code === 200) {
      projects.value = res.data || []
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
  }
}

// 加载项目任务列表
const loadProjectTasks = async () => {
  if (!selectedProjectId.value) {
    projectTasks.value = []
    return
  }
  try {
    const res = await getTasksByProjectId(selectedProjectId.value)
    if (res.code === 200) {
      projectTasks.value = res.data || []
    }
  } catch (error) {
    console.error('加载任务列表失败:', error)
  }
}

// 加载学生列表
const loadStudents = async () => {
  if (!selectedProjectId.value) {
    students.value = []
    return
  }
  studentsLoading.value = true
  try {
    const res = await getProjectStudents(selectedProjectId.value)
    if (res.code === 200) {
      students.value = res.data || []
    }
  } catch (error) {
    console.error('加载学生列表失败:', error)
  } finally {
    studentsLoading.value = false
  }
}

// 加载提交列表
const loadSubmissions = async () => {
  submissionsLoading.value = true
  try {
    const params = {
      ...queryParams,
      projectId: selectedProjectId.value,
      taskId: selectedTaskId.value
    }
    const res = await getSubmissions(params)
    if (res.code === 200) {
      submissions.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('加载提交列表失败:', error)
  } finally {
    submissionsLoading.value = false
  }
}

// 项目变更
const handleProjectChange = () => {
  queryParams.projectId = selectedProjectId.value
  selectedTaskId.value = null
  queryParams.taskId = null
  selectedStudentId.value = null
  loadProjectTasks()
  loadStudents()
  loadSubmissions()
}

// 任务变更
const handleTaskChange = () => {
  queryParams.taskId = selectedTaskId.value
  loadSubmissions()
}

// 选择学生
const selectStudent = (student) => {
  selectedStudentId.value = student.studentId
  queryParams.studentName = student.studentName || student.studentUsername
  loadSubmissions()
}

// 点击行
const handleRowClick = (row) => {
  openGradeDialog(row)
}

// 打开批改对话框
const openGradeDialog = async (row) => {
  try {
    const res = await getSubmissionDetail(row.id)
    if (res.code === 200) {
      currentSubmission.value = res.data
      gradeForm.submissionId = res.data.id
      gradeForm.score = res.data.score ? (res.data.score * 100 / res.data.scoreWeight).toFixed(1) : null
      gradeForm.feedback = res.data.feedback || ''
      gradeDialogVisible.value = true
    }
  } catch (error) {
    console.error('加载提交详情失败:', error)
    ElMessage.error('加载详情失败')
  }
}

// 提交评分
const handleGrade = async () => {
  await gradeFormRef.value.validate()
  grading.value = true
  try {
    const res = await gradeSubmission(gradeForm)
    if (res.code === 200) {
      ElMessage.success('评分成功')
      gradeDialogVisible.value = false
      loadSubmissions()
      if (selectedProjectId.value) {
        loadStudents()
      }
    } else {
      ElMessage.error(res.msg || '评分失败')
    }
  } catch (error) {
    console.error('评分失败:', error)
    ElMessage.error('评分失败')
  } finally {
    grading.value = false
  }
}

// 格式化
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.substring(0, 16).replace('T', ' ')
}

const getStudentStatusType = (status) => {
  const types = { 0: 'info', 1: 'primary', 2: 'success', 3: 'warning' }
  return types[status] || 'info'
}

const getStudentStatusText = (status) => {
  const texts = { 0: '已报名', 1: '进行中', 2: '已完成', 3: '已评分' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadProjects()
  loadSubmissions()
})
</script>

<style lang="scss" scoped>
.grading-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;

  h2 {
    font-size: 22px;
    color: #303133;
    margin: 0 0 8px;
  }

  p {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

.content-layout {
  display: flex;
  gap: 20px;

  @media (max-width: 1200px) {
    flex-direction: column;
  }
}

.filter-card {
  width: 300px;
  flex-shrink: 0;
  height: fit-content;

  @media (max-width: 1200px) {
    width: 100%;
  }

  .student-section {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;

    .section-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      font-size: 14px;
      color: #606266;
    }

    .student-list {
      max-height: 400px;
      overflow-y: auto;
    }

    .student-item {
      display: flex;
      align-items: center;
      padding: 10px;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s;
      margin-bottom: 4px;

      &:hover {
        background: #f5f7fa;
      }

      &.active {
        background: #ecf5ff;
      }

      .student-info {
        flex: 1;
        margin-left: 10px;
        min-width: 0;

        .name {
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
        }

        .progress {
          display: flex;
          align-items: center;
          gap: 8px;

          .el-progress {
            flex: 1;
          }

          span {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }
}

.main-content {
  flex: 1;
  min-width: 0;
}

.submissions-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .student-cell {
    font-size: 13px;
  }

  .score {
    color: #67c23a;
    font-weight: 600;
  }
}

.grade-dialog-content {
  .info-section {
    margin-bottom: 20px;

    h4 {
      font-size: 14px;
      color: #303133;
      margin: 0 0 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid #ebeef5;
    }

    .description {
      color: #606266;
      font-size: 14px;
      line-height: 1.6;
      margin: 0;
    }

    .submitted-content {
      background: #f5f7fa;
      border-radius: 6px;
      padding: 12px;

      p {
        margin: 0 0 8px;
        color: #606266;
        font-size: 14px;
        white-space: pre-wrap;
        word-break: break-all;
      }
    }

    .score-hint {
      margin-left: 12px;
      font-size: 12px;
      color: #909399;
    }

    .graded-info {
      margin-top: 12px;
      font-size: 12px;
      color: #909399;
      display: flex;
      gap: 20px;
    }
  }
}
</style>
