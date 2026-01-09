<template>
  <div class="training-container">
    <div class="page-header">
      <h1>实训中心</h1>
      <p>参与实训项目，提升实践技能</p>
    </div>

    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab" class="training-tabs" @tab-change="handleTabChange">
      <!-- 可报名项目 -->
      <el-tab-pane label="可报名项目" name="available">
        <div class="filter-bar">
          <el-input
            v-model="availableQuery.projectName"
            placeholder="搜索项目名称"
            clearable
            style="width: 200px"
            @clear="loadAvailableProjects"
            @keyup.enter="loadAvailableProjects"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="availableQuery.difficulty" placeholder="难度等级" clearable style="width: 120px" @change="loadAvailableProjects">
            <el-option label="初级" :value="1" />
            <el-option label="中级" :value="2" />
            <el-option label="高级" :value="3" />
          </el-select>
          <el-button type="primary" @click="loadAvailableProjects">
            <el-icon><Search /></el-icon>搜索
          </el-button>
        </div>

        <div v-loading="availableLoading" class="project-grid">
          <el-empty v-if="availableProjects.length === 0" description="暂无可报名的项目" />
          <el-card v-for="project in availableProjects" :key="project.id" class="project-card" shadow="hover">
            <div class="project-cover">
              <img v-if="project.coverImage" :src="project.coverImage" alt="项目封面" />
              <div v-else class="cover-placeholder">
                <el-icon :size="48"><Notebook /></el-icon>
              </div>
              <el-tag :type="getDifficultyType(project.difficulty)" class="difficulty-tag">
                {{ getDifficultyText(project.difficulty) }}
              </el-tag>
            </div>
            <div class="project-info">
              <h3>{{ project.projectName }}</h3>
              <p class="description">{{ project.description || '暂无描述' }}</p>
              <div class="meta">
                <span><el-icon><Calendar /></el-icon> {{ formatDate(project.startTime) }} ~ {{ formatDate(project.endTime) }}</span>
                <span><el-icon><User /></el-icon> {{ project.memberCount || 0 }}/{{ project.maxMembers || '不限' }}</span>
              </div>
              <el-button type="primary" class="enroll-btn" @click="handleEnroll(project)">
                立即报名
              </el-button>
            </div>
          </el-card>
        </div>

        <el-pagination
          v-if="availableTotal > 0"
          v-model:current-page="availableQuery.pageNum"
          v-model:page-size="availableQuery.pageSize"
          :total="availableTotal"
          :page-sizes="[8, 16, 24]"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="loadAvailableProjects"
          @current-change="loadAvailableProjects"
        />
      </el-tab-pane>

      <!-- 我的实训 -->
      <el-tab-pane label="我的实训" name="myProjects">
        <div class="filter-bar">
          <el-input
            v-model="myQuery.projectName"
            placeholder="搜索项目名称"
            clearable
            style="width: 200px"
            @clear="loadMyProjects"
            @keyup.enter="loadMyProjects"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="myQuery.status" placeholder="参与状态" clearable style="width: 120px" @change="loadMyProjects">
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
          <el-button type="primary" @click="loadMyProjects">
            <el-icon><Search /></el-icon>搜索
          </el-button>
        </div>

        <div v-loading="myLoading" class="project-grid">
          <el-empty v-if="myProjects.length === 0" description="您还没有参与任何实训项目" />
          <el-card v-for="project in myProjects" :key="project.id" class="project-card my-project-card" shadow="hover">
            <div class="project-cover">
              <img v-if="project.coverImage" :src="project.coverImage" alt="项目封面" />
              <div v-else class="cover-placeholder">
                <el-icon :size="48"><Notebook /></el-icon>
              </div>
              <el-tag :type="getStatusType(project.status)" class="status-tag">
                {{ getStatusText(project.status) }}
              </el-tag>
            </div>
            <div class="project-info">
              <h3>{{ project.projectName }}</h3>
              <div class="progress-section">
                <span class="progress-label">完成进度</span>
                <el-progress
                  :percentage="project.progressPercent || 0"
                  :status="project.progressPercent >= 100 ? 'success' : ''"
                />
                <span class="progress-text">{{ project.completedTasks || 0 }}/{{ project.totalTasks || 0 }} 任务</span>
              </div>
              <div class="meta">
                <span v-if="project.totalScore !== null && project.status === 3">
                  <el-icon><Trophy /></el-icon> 得分: {{ project.totalScore }}
                </span>
                <span v-else>
                  <el-icon><Clock /></el-icon> 报名: {{ formatDate(project.enrollTime) }}
                </span>
              </div>
              <el-button type="primary" class="enter-btn" @click="enterProject(project)">
                {{ project.status === 0 ? '开始实训' : '继续实训' }}
              </el-button>
            </div>
          </el-card>
        </div>

        <el-pagination
          v-if="myTotal > 0"
          v-model:current-page="myQuery.pageNum"
          v-model:page-size="myQuery.pageSize"
          :total="myTotal"
          :page-sizes="[8, 16, 24]"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="loadMyProjects"
          @current-change="loadMyProjects"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAvailableProjects, getStudentProjects, enrollProject } from '@/api/training'

const router = useRouter()

// 标签页
const activeTab = ref('available')

// 可报名项目
const availableLoading = ref(false)
const availableProjects = ref([])
const availableTotal = ref(0)
const availableQuery = ref({
  pageNum: 1,
  pageSize: 8,
  projectName: '',
  difficulty: null
})

// 我的实训
const myLoading = ref(false)
const myProjects = ref([])
const myTotal = ref(0)
const myQuery = ref({
  pageNum: 1,
  pageSize: 8,
  projectName: '',
  status: null
})

// 加载可报名项目
const loadAvailableProjects = async () => {
  availableLoading.value = true
  try {
    const res = await getAvailableProjects(availableQuery.value)
    if (res.code === 200) {
      availableProjects.value = res.data.records || []
      availableTotal.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载可报名项目失败:', error)
  } finally {
    availableLoading.value = false
  }
}

// 加载我的实训
const loadMyProjects = async () => {
  myLoading.value = true
  try {
    const res = await getStudentProjects(myQuery.value)
    if (res.code === 200) {
      myProjects.value = res.data.records || []
      myTotal.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载我的实训失败:', error)
  } finally {
    myLoading.value = false
  }
}

// 报名项目
const handleEnroll = async (project) => {
  try {
    await ElMessageBox.confirm(
      `确定要报名参加"${project.projectName}"吗？`,
      '报名确认',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }
    )
    const res = await enrollProject(project.id)
    if (res.code === 200) {
      ElMessage.success('报名成功')
      loadAvailableProjects()
      // 切换到我的实训标签
      activeTab.value = 'myProjects'
      loadMyProjects()
    } else {
      ElMessage.error(res.msg || '报名失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('报名失败')
    }
  }
}

// 进入项目
const enterProject = (project) => {
  router.push(`/training/${project.projectId}`)
}

// 标签页切换
const handleTabChange = (tab) => {
  if (tab === 'available') {
    loadAvailableProjects()
  } else if (tab === 'myProjects') {
    loadMyProjects()
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.substring(0, 10)
}

// 难度等级
const getDifficultyType = (difficulty) => {
  const types = { 1: 'success', 2: 'warning', 3: 'danger' }
  return types[difficulty] || 'info'
}

const getDifficultyText = (difficulty) => {
  const texts = { 1: '初级', 2: '中级', 3: '高级' }
  return texts[difficulty] || '未知'
}

// 状态
const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'primary', 2: 'success', 3: 'warning' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '已报名', 1: '进行中', 2: '已完成', 3: '已评分' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadAvailableProjects()
})
</script>

<style lang="scss" scoped>
.training-container {
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;

  h1 {
    font-size: 28px;
    color: #303133;
    margin-bottom: 10px;
  }

  p {
    font-size: 14px;
    color: #909399;
  }
}

.training-tabs {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.project-card {
  border-radius: 8px;
  overflow: hidden;

  :deep(.el-card__body) {
    padding: 0;
  }

  .project-cover {
    position: relative;
    height: 160px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

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

    .difficulty-tag,
    .status-tag {
      position: absolute;
      top: 10px;
      right: 10px;
    }
  }

  .project-info {
    padding: 16px;

    h3 {
      font-size: 16px;
      color: #303133;
      margin: 0 0 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .description {
      font-size: 13px;
      color: #909399;
      margin: 0 0 12px;
      line-height: 1.5;
      height: 40px;
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .meta {
      font-size: 12px;
      color: #909399;
      margin-bottom: 12px;
      display: flex;
      flex-direction: column;
      gap: 4px;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }

    .progress-section {
      margin-bottom: 12px;

      .progress-label {
        font-size: 12px;
        color: #909399;
        display: block;
        margin-bottom: 4px;
      }

      .progress-text {
        font-size: 12px;
        color: #606266;
        margin-top: 4px;
        display: block;
      }
    }

    .enroll-btn,
    .enter-btn {
      width: 100%;
    }
  }
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>
