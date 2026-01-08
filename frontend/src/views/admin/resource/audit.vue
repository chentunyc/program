<template>
  <div class="resource-audit-container">
    <div class="page-header">
      <h1>资源审核</h1>
      <p>Resource Audit</p>
    </div>

    <!-- 待审核统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card pending">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ pendingCount }}</span>
              <span class="stat-label">待审核</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card simulation">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ typeStats.SIMULATION || 0 }}</span>
              <span class="stat-label">虚拟仿真</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card video">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><VideoCamera /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ typeStats.VIDEO || 0 }}</span>
              <span class="stat-label">视频</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card document">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ (typeStats.AUDIO || 0) + (typeStats.DOCUMENT || 0) }}</span>
              <span class="stat-label">音频/文档</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索资源名称"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="5">
          <el-select
            v-model="queryParams.resourceType"
            placeholder="资源类型"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option label="虚拟仿真" value="SIMULATION" />
            <el-option label="视频" value="VIDEO" />
            <el-option label="音频" value="AUDIO" />
            <el-option label="文档" value="DOCUMENT" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="5">
          <el-select
            v-model="queryParams.status"
            placeholder="状态筛选"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option label="待审核" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="warning" @click="filterPending">
            <el-icon><Clock /></el-icon>
            仅看待审核
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 资源列表 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="resourceList"
        style="width: 100%"
        :row-class-name="getRowClassName"
      >
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="getImageUrl(row.coverImage)"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px"
            >
              <template #error>
                <div class="cover-placeholder-small">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="cover-placeholder-small">
              <el-icon>
                <component :is="getTypeIcon(row.resourceType)" />
              </el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="resourceName" label="资源名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagColor(row.resourceType)" size="small">
              {{ row.resourceTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning" size="small" effect="dark">
              <el-icon class="is-loading" style="margin-right: 4px"><Loading /></el-icon>
              待审核
            </el-tag>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已发布</el-tag>
            <el-tag v-else type="info" size="small">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="共享" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isShared === 1" type="success" size="small">是</el-tag>
            <el-tag v-else type="info" size="small">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="uploaderName" label="上传者" width="100" />
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handlePreview(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <template v-if="row.status === 0">
              <el-button type="success" size="small" @click="handleApprove(row)">
                <el-icon><Check /></el-icon>
                通过
              </el-button>
              <el-button type="danger" size="small" @click="handleReject(row)">
                <el-icon><Close /></el-icon>
                拒绝
              </el-button>
            </template>
            <template v-else>
              <el-button
                v-if="row.status === 2"
                type="success"
                size="small"
                @click="handleApprove(row)"
              >
                重新发布
              </el-button>
              <el-button
                v-if="row.status === 1"
                type="warning"
                size="small"
                @click="handleReject(row)"
              >
                下架
              </el-button>
            </template>
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
        @size-change="loadResources"
        @current-change="loadResources"
      />
    </el-card>

    <!-- 资源预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="资源预览"
      width="900px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-if="previewData" class="preview-content">
        <!-- 基本信息 -->
        <div class="preview-header">
          <div class="preview-cover">
            <el-image
              v-if="previewData.coverImage"
              :src="getImageUrl(previewData.coverImage)"
              fit="cover"
            >
              <template #error>
                <div class="cover-placeholder">
                  <el-icon :size="40">
                    <component :is="getTypeIcon(previewData.resourceType)" />
                  </el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="cover-placeholder">
              <el-icon :size="40">
                <component :is="getTypeIcon(previewData.resourceType)" />
              </el-icon>
            </div>
          </div>
          <div class="preview-info">
            <h2>{{ previewData.resourceName }}</h2>
            <div class="info-tags">
              <el-tag :type="getTypeTagColor(previewData.resourceType)">
                {{ previewData.resourceTypeName }}
              </el-tag>
              <el-tag v-if="previewData.status === 0" type="warning">待审核</el-tag>
              <el-tag v-else-if="previewData.status === 1" type="success">已发布</el-tag>
              <el-tag v-else type="info">已下架</el-tag>
              <el-tag v-if="previewData.isShared === 1" type="success">共享资源</el-tag>
            </div>
            <div class="info-meta">
              <span><el-icon><User /></el-icon> {{ previewData.uploaderName || '管理员' }}</span>
              <span><el-icon><Clock /></el-icon> {{ formatDateTime(previewData.createTime) }}</span>
              <span v-if="previewData.fileSizeFormat">
                <el-icon><Files /></el-icon> {{ previewData.fileSizeFormat }}
              </span>
            </div>
            <div class="info-stats">
              <span><el-icon><View /></el-icon> {{ previewData.viewCount || 0 }} 次浏览</span>
              <span><el-icon><Download /></el-icon> {{ previewData.downloadCount || 0 }} 次下载</span>
            </div>
          </div>
        </div>

        <!-- 描述 -->
        <el-divider content-position="left">资源描述</el-divider>
        <div class="preview-description">
          {{ previewData.description || '暂无描述' }}
        </div>

        <!-- 标签 -->
        <div v-if="previewData.tags" class="preview-tags">
          <el-tag
            v-for="tag in previewData.tags.split(',')"
            :key="tag"
            type="info"
            size="small"
          >
            {{ tag }}
          </el-tag>
        </div>

        <!-- 资源详情 -->
        <el-divider content-position="left">资源详情</el-divider>
        <div class="preview-details">
          <!-- 虚拟仿真 -->
          <template v-if="previewData.resourceType === 'SIMULATION'">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="技术栈">
                {{ previewData.technology || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="支持平台">
                {{ previewData.supportPlatform || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="最低配置" :span="2">
                {{ previewData.minRequirement || '无特殊要求' }}
              </el-descriptions-item>
              <el-descriptions-item label="启动地址" :span="2">
                <el-link
                  v-if="previewData.simulationUrl"
                  :href="previewData.simulationUrl"
                  target="_blank"
                  type="primary"
                >
                  {{ previewData.simulationUrl }}
                </el-link>
                <span v-else class="text-muted">暂无</span>
              </el-descriptions-item>
            </el-descriptions>
          </template>

          <!-- 视频 -->
          <template v-else-if="previewData.resourceType === 'VIDEO'">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="视频时长">
                {{ previewData.durationFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="分辨率">
                {{ previewData.resolution || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="视频格式">
                {{ previewData.videoFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="字幕">
                {{ previewData.subtitleUrl ? '有字幕' : '无字幕' }}
              </el-descriptions-item>
            </el-descriptions>
            <div v-if="previewData.videoUrl" class="media-player">
              <video
                controls
                :src="getMediaUrl(previewData.videoUrl)"
                width="100%"
              >
                您的浏览器不支持视频播放
              </video>
            </div>
          </template>

          <!-- 音频 -->
          <template v-else-if="previewData.resourceType === 'AUDIO'">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="音频时长">
                {{ previewData.audioDurationFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="音频格式">
                {{ previewData.audioFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="比特率">
                {{ previewData.bitrate || '未知' }}
              </el-descriptions-item>
            </el-descriptions>
            <div v-if="previewData.audioUrl" class="media-player">
              <audio controls :src="getMediaUrl(previewData.audioUrl)" style="width: 100%">
                您的浏览器不支持音频播放
              </audio>
            </div>
          </template>

          <!-- 文档 -->
          <template v-else-if="previewData.resourceType === 'DOCUMENT'">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="文档格式">
                {{ previewData.documentFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="页数">
                {{ previewData.pageCount || '未知' }} 页
              </el-descriptions-item>
              <el-descriptions-item label="文档地址" :span="2">
                <el-link
                  v-if="previewData.documentUrl"
                  :href="getMediaUrl(previewData.documentUrl)"
                  target="_blank"
                  type="primary"
                >
                  点击查看/下载
                </el-link>
                <span v-else class="text-muted">暂无</span>
              </el-descriptions-item>
            </el-descriptions>
            <div v-if="previewData.previewUrl" class="document-preview">
              <iframe :src="previewData.previewUrl" width="100%" height="400px" frameborder="0"></iframe>
            </div>
          </template>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="previewVisible = false">关闭</el-button>
          <template v-if="previewData && previewData.status === 0">
            <el-button type="danger" @click="handleRejectFromPreview">
              <el-icon><Close /></el-icon>
              拒绝
            </el-button>
            <el-button type="success" @click="handleApproveFromPreview">
              <el-icon><Check /></el-icon>
              通过审核
            </el-button>
          </template>
        </div>
      </template>
    </el-dialog>

    <!-- 拒绝原因对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝原因"
      width="500px"
    >
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejectLoading">
          确认拒绝
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Refresh, Clock, Monitor, VideoCamera, Document, Picture, View,
  Download, Check, Close, User, Files, Loading, Headset
} from '@element-plus/icons-vue'
import {
  getResourcePage,
  getResourceById,
  publishResource,
  offlineResource
} from '@/api/resource'
import dayjs from 'dayjs'

// 数据状态
const loading = ref(false)
const resourceList = ref([])
const total = ref(0)
const pendingCount = ref(0)
const typeStats = ref({})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  resourceType: null,
  status: 0 // 默认显示待审核
})

// 预览状态
const previewVisible = ref(false)
const previewData = ref(null)

// 拒绝对话框
const rejectDialogVisible = ref(false)
const rejectForm = reactive({
  resourceId: null,
  reason: ''
})
const rejectLoading = ref(false)

/**
 * 格式化日期时间
 */
const formatDateTime = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 获取图片URL
 */
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 静态资源直接访问，不走/api代理
  return url
}

/**
 * 获取媒体URL
 */
const getMediaUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 静态资源直接访问，不走/api代理
  return url
}

/**
 * 获取类型图标
 */
const getTypeIcon = (type) => {
  const iconMap = {
    SIMULATION: Monitor,
    VIDEO: VideoCamera,
    AUDIO: Headset,
    DOCUMENT: Document
  }
  return iconMap[type] || Document
}

/**
 * 获取类型标签颜色
 */
const getTypeTagColor = (type) => {
  const colorMap = {
    SIMULATION: 'primary',
    VIDEO: 'success',
    AUDIO: 'warning',
    DOCUMENT: 'info'
  }
  return colorMap[type] || ''
}

/**
 * 获取行样式类名
 */
const getRowClassName = ({ row }) => {
  if (row.status === 0) {
    return 'pending-row'
  }
  return ''
}

/**
 * 加载资源列表
 */
const loadResources = async () => {
  loading.value = true
  try {
    const response = await getResourcePage(queryParams)
    resourceList.value = response.data.records || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载资源列表失败')
    console.error('加载资源列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载待审核统计
 */
const loadPendingStats = async () => {
  try {
    // 获取待审核总数
    const pendingRes = await getResourcePage({ status: 0, pageNum: 1, pageSize: 1 })
    pendingCount.value = pendingRes.data.total || 0

    // 按类型统计待审核数量
    const types = ['SIMULATION', 'VIDEO', 'AUDIO', 'DOCUMENT']
    const stats = {}
    for (const type of types) {
      const res = await getResourcePage({ status: 0, resourceType: type, pageNum: 1, pageSize: 1 })
      stats[type] = res.data.total || 0
    }
    typeStats.value = stats
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  queryParams.pageNum = 1
  loadResources()
}

/**
 * 重置
 */
const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.keyword = ''
  queryParams.resourceType = null
  queryParams.status = null
  loadResources()
}

/**
 * 仅看待审核
 */
const filterPending = () => {
  queryParams.status = 0
  queryParams.pageNum = 1
  loadResources()
}

/**
 * 预览资源
 */
const handlePreview = async (row) => {
  try {
    const { data } = await getResourceById(row.id)
    previewData.value = data
    previewVisible.value = true
  } catch (error) {
    ElMessage.error('获取资源详情失败')
    console.error('获取资源详情失败:', error)
  }
}

/**
 * 审核通过
 */
const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定通过资源「${row.resourceName}」的审核并发布吗？`,
      '审核确认',
      {
        type: 'success',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )
    await publishResource(row.id)
    ElMessage.success('审核通过，资源已发布')
    loadResources()
    loadPendingStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
      console.error('审核通过失败:', error)
    }
  }
}

/**
 * 从预览对话框中通过审核
 */
const handleApproveFromPreview = async () => {
  if (!previewData.value) return
  await handleApprove(previewData.value)
  previewVisible.value = false
}

/**
 * 拒绝资源
 */
const handleReject = (row) => {
  rejectForm.resourceId = row.id
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

/**
 * 从预览对话框中拒绝
 */
const handleRejectFromPreview = () => {
  if (!previewData.value) return
  handleReject(previewData.value)
}

/**
 * 确认拒绝
 */
const confirmReject = async () => {
  rejectLoading.value = true
  try {
    await offlineResource(rejectForm.resourceId)
    ElMessage.success('已拒绝该资源')
    rejectDialogVisible.value = false
    previewVisible.value = false
    loadResources()
    loadPendingStats()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('拒绝失败:', error)
  } finally {
    rejectLoading.value = false
  }
}

// 初始化
onMounted(() => {
  loadResources()
  loadPendingStats()
})
</script>

<style lang="scss" scoped>
.resource-audit-container {
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

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-3px);
      }

      .stat-content {
        display: flex;
        align-items: center;
        gap: 15px;

        .stat-icon {
          width: 50px;
          height: 50px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          color: #fff;
        }

        .stat-info {
          .stat-value {
            display: block;
            font-size: 28px;
            font-weight: 700;
            color: #303133;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }

      &.pending .stat-icon {
        background: linear-gradient(135deg, #e6a23c 0%, #f56c6c 100%);
      }

      &.simulation .stat-icon {
        background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
      }

      &.video .stat-icon {
        background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
      }

      &.document .stat-icon {
        background: linear-gradient(135deg, #909399 0%, #c0c4cc 100%);
      }
    }
  }

  .filter-card {
    margin-bottom: 20px;

    .el-row {
      align-items: center;
    }

    .el-col {
      margin-bottom: 10px;

      @media (min-width: 768px) {
        margin-bottom: 0;
      }
    }
  }

  .table-card {
    .cover-placeholder-small {
      width: 60px;
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 4px;
      color: #fff;
    }

    :deep(.pending-row) {
      background-color: #fdf6ec;
    }

    :deep(.el-pagination) {
      margin-top: 20px;
      justify-content: center;
    }
  }
}

// 预览对话框样式
.preview-content {
  .preview-header {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;

    .preview-cover {
      width: 200px;
      height: 130px;
      border-radius: 8px;
      overflow: hidden;
      flex-shrink: 0;

      .el-image {
        width: 100%;
        height: 100%;
      }

      .cover-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: rgba(255, 255, 255, 0.8);
      }
    }

    .preview-info {
      flex: 1;

      h2 {
        margin: 0 0 10px;
        font-size: 20px;
        color: #303133;
      }

      .info-tags {
        display: flex;
        gap: 8px;
        margin-bottom: 10px;
        flex-wrap: wrap;
      }

      .info-meta {
        display: flex;
        gap: 20px;
        color: #909399;
        font-size: 14px;
        margin-bottom: 8px;
        flex-wrap: wrap;

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }

      .info-stats {
        display: flex;
        gap: 20px;
        color: #606266;
        font-size: 14px;

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }

  .preview-description {
    padding: 15px;
    background: #f5f7fa;
    border-radius: 8px;
    color: #606266;
    line-height: 1.8;
    margin-bottom: 15px;
  }

  .preview-tags {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    margin-bottom: 20px;
  }

  .preview-details {
    .media-player {
      margin-top: 20px;
      padding: 15px;
      background: #f5f7fa;
      border-radius: 8px;

      video {
        border-radius: 8px;
        max-height: 400px;
      }
    }

    .document-preview {
      margin-top: 20px;
      padding: 15px;
      background: #f5f7fa;
      border-radius: 8px;
    }

    .text-muted {
      color: #909399;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .resource-audit-container {
    padding: 10px;

    .stats-row .el-col {
      margin-bottom: 10px;
    }
  }

  .preview-content .preview-header {
    flex-direction: column;

    .preview-cover {
      width: 100%;
      height: 150px;
    }
  }
}
</style>
