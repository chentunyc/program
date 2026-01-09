<template>
  <div class="data-management-container">
    <div class="page-header">
      <h1>数据管理</h1>
      <p>Training Data Management</p>
    </div>

    <!-- 项目选择和导出操作 -->
    <el-card class="filter-card" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="12" :md="8">
          <div class="selector-wrapper">
            <span class="selector-label">选择项目：</span>
            <el-select
              v-model="selectedProjectId"
              placeholder="请选择要查看的项目"
              filterable
              style="flex: 1"
              @change="handleProjectChange"
            >
              <el-option
                v-for="project in projects"
                :key="project.id"
                :label="project.projectName"
                :value="project.id"
              >
                <div class="project-option">
                  <span>{{ project.projectName }}</span>
                  <el-tag :type="getStatusType(project.status)" size="small">
                    {{ getStatusText(project.status) }}
                  </el-tag>
                </div>
              </el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="16">
          <div class="action-bar">
            <el-dropdown
              split-button
              type="success"
              :disabled="!selectedProjectId || students.length === 0"
              @click="exportSummary"
              @command="handleExportCommand"
            >
              <el-icon><Download /></el-icon> 导出总表
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="summary">
                    <el-icon><Document /></el-icon> 导出总表 (学生总分)
                  </el-dropdown-item>
                  <el-dropdown-item command="detailed">
                    <el-icon><List /></el-icon> 导出分表 (每个任务成绩)
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 项目统计概览 -->
    <el-row v-if="selectedProjectId && projectInfo" :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon primary"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ students.length }}</div>
              <div class="stat-label">参与学生</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon success"><CircleCheck /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ completedCount }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon warning"><Clock /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ inProgressCount }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon danger"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ averageScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 未选择项目提示 -->
    <el-empty v-if="!selectedProjectId" description="请先选择一个项目">
      <template #image>
        <el-icon style="font-size: 80px; color: #909399"><DataAnalysis /></el-icon>
      </template>
    </el-empty>

    <!-- 成绩数据表格 -->
    <el-card v-if="selectedProjectId" class="data-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>学生成绩列表</span>
          <el-tag type="info">共 {{ students.length }} 人</el-tag>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="students"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="studentName" label="姓名" width="120">
          <template #default="{ row }">
            {{ row.studentName || row.studentUsername || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="studentUsername" label="学号" width="120" />
        <el-table-column label="进度" width="180">
          <template #default="{ row }">
            <el-progress
              :percentage="row.progressPercent || 0"
              :status="row.progressPercent >= 100 ? 'success' : ''"
              :stroke-width="8"
            />
            <span class="progress-text">{{ row.completedTasks || 0 }}/{{ row.totalTasks || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStudentStatusType(row.status)" size="small">
              {{ getStudentStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="总分" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.totalScore !== null" class="score-value">{{ row.totalScore }}</span>
            <span v-else class="no-score">-</span>
          </template>
        </el-table-column>
        <el-table-column label="报名时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.enrollTime) }}
          </template>
        </el-table-column>
        <el-table-column label="完成时间" width="160">
          <template #default="{ row }">
            {{ row.status === 2 ? formatDateTime(row.completeTime) : '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Refresh, User, CircleCheck, Clock, TrendCharts, DataAnalysis, Document, List } from '@element-plus/icons-vue'
import { getGradingProjects, getProjectStudents, getDetailedScores } from '@/api/training'

// 项目列表
const projects = ref([])
const selectedProjectId = ref(null)
const projectInfo = ref(null)
const loading = ref(false)

// 学生数据
const students = ref([])

// 统计数据
const completedCount = computed(() => {
  return students.value.filter(s => s.status === 2).length
})

const inProgressCount = computed(() => {
  return students.value.filter(s => s.status === 1).length
})

const averageScore = computed(() => {
  const scored = students.value.filter(s => s.totalScore !== null && s.totalScore !== undefined)
  if (scored.length === 0) return '-'
  const total = scored.reduce((sum, s) => sum + Number(s.totalScore), 0)
  return (total / scored.length).toFixed(1)
})

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

// 加载学生数据
const loadStudents = async () => {
  if (!selectedProjectId.value) {
    students.value = []
    return
  }

  loading.value = true
  try {
    const res = await getProjectStudents(selectedProjectId.value)
    if (res.code === 200) {
      students.value = res.data || []
    }
  } catch (error) {
    console.error('加载学生数据失败:', error)
    ElMessage.error('加载学生数据失败')
  } finally {
    loading.value = false
  }
}

// 项目变更
const handleProjectChange = () => {
  projectInfo.value = projects.value.find(p => p.id === selectedProjectId.value)
  loadStudents()
}

// 刷新
const handleRefresh = () => {
  loadProjects()
  if (selectedProjectId.value) {
    loadStudents()
  }
}

// 处理导出命令
const handleExportCommand = (command) => {
  if (command === 'summary') {
    exportSummary()
  } else if (command === 'detailed') {
    exportDetailed()
  }
}

// 导出总表 (学生总分)
const exportSummary = () => {
  if (students.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }

  const project = projects.value.find(p => p.id === selectedProjectId.value)
  const projectName = project?.projectName || '实训项目'

  // 构建CSV内容
  const headers = ['序号', '姓名', '学号', '完成任务', '总任务', '进度(%)', '状态', '总分', '报名时间', '完成时间']
  const rows = students.value.map((s, index) => [
    index + 1,
    s.studentName || s.studentUsername || '-',
    s.studentUsername || '-',
    s.completedTasks || 0,
    s.totalTasks || 0,
    s.progressPercent || 0,
    getStudentStatusText(s.status),
    s.totalScore !== null && s.totalScore !== undefined ? s.totalScore : '-',
    formatDateTime(s.enrollTime),
    s.status === 2 ? formatDateTime(s.completeTime) : '-'
  ])

  downloadCSV(headers, rows, `${projectName}_成绩总表_${formatDate(new Date())}.csv`)
  ElMessage.success('导出成功，文件可用Excel打开')
}

// 导出分表 (每个任务成绩)
const exportDetailed = async () => {
  if (!selectedProjectId.value) {
    ElMessage.warning('请先选择项目')
    return
  }

  loading.value = true
  try {
    const res = await getDetailedScores(selectedProjectId.value)
    if (res.code === 200) {
      const detailedData = res.data || []

      if (detailedData.length === 0) {
        ElMessage.warning('没有详细成绩数据')
        return
      }

      const project = projects.value.find(p => p.id === selectedProjectId.value)
      const projectName = project?.projectName || '实训项目'

      // 构建CSV内容
      const headers = ['序号', '姓名', '学号', '任务名称', '任务分值', '得分', '提交状态', '提交时间']
      const rows = detailedData.map((item, index) => [
        index + 1,
        item.studentName || item.studentUsername || '-',
        item.studentUsername || '-',
        item.taskName || '-',
        item.scoreWeight || 0,
        item.score !== null && item.score !== undefined ? item.score : '-',
        getTaskStatusText(item.status),
        formatDateTime(item.submitTime)
      ])

      downloadCSV(headers, rows, `${projectName}_成绩分表_${formatDate(new Date())}.csv`)
      ElMessage.success('导出成功，文件可用Excel打开')
    } else {
      ElMessage.error(res.msg || '获取详细成绩失败')
    }
  } catch (error) {
    console.error('导出详细成绩失败:', error)
    ElMessage.error('导出失败')
  } finally {
    loading.value = false
  }
}

// 下载CSV文件
const downloadCSV = (headers, rows, filename) => {
  // 添加BOM以支持中文
  const BOM = '\uFEFF'
  const csvContent = BOM + [headers.join(','), ...rows.map(r => r.join(','))].join('\n')

  // 创建Blob并下载
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', filename)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 任务状态文本
const getTaskStatusText = (status) => {
  const texts = { 0: '未提交', 1: '待批改', 2: '已批改' }
  return texts[status] || '未提交'
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.substring(0, 16).replace('T', ' ')
}

// 格式化日期
const formatDate = (date) => {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}${month}${day}`
}

// 项目状态
const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'primary', 2: 'success' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return texts[status] || '未知'
}

// 学生状态
const getStudentStatusType = (status) => {
  const types = { 0: 'info', 1: 'primary', 2: 'success' }
  return types[status] || 'info'
}

const getStudentStatusText = (status) => {
  const texts = { 0: '已报名', 1: '进行中', 2: '已完成' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadProjects()
})
</script>

<style lang="scss" scoped>
.data-management-container {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;
    padding-bottom: 20px;
    padding-left: 10px;
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

  .filter-card {
    margin-bottom: 20px;

    .selector-wrapper {
      display: flex;
      align-items: center;
      gap: 12px;

      .selector-label {
        white-space: nowrap;
        color: #606266;
        font-weight: 500;
      }
    }

    .action-bar {
      display: flex;
      justify-content: flex-end;
      gap: 10px;

      @media (max-width: 768px) {
        margin-top: 12px;
        justify-content: flex-start;
      }
    }

    .project-option {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
    }
  }

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          font-size: 40px;
          padding: 12px;
          border-radius: 12px;
          background: #f5f7fa;

          &.primary { color: #409eff; background: #ecf5ff; }
          &.success { color: #67c23a; background: #f0f9eb; }
          &.warning { color: #e6a23c; background: #fdf6ec; }
          &.danger { color: #f56c6c; background: #fef0f0; }
        }

        .stat-info {
          .stat-value {
            font-size: 28px;
            font-weight: 600;
            color: #303133;
          }

          .stat-label {
            font-size: 13px;
            color: #909399;
            margin-top: 4px;
          }
        }
      }
    }
  }

  .data-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .progress-text {
      font-size: 12px;
      color: #909399;
      margin-left: 8px;
    }

    .score-value {
      font-size: 16px;
      font-weight: 600;
      color: #67c23a;
    }

    .no-score {
      color: #c0c4cc;
    }
  }
}

@media (max-width: 768px) {
  .stats-row {
    .el-col {
      margin-bottom: 12px;
    }
  }
}
</style>
