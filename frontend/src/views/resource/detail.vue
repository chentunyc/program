<template>
  <div class="resource-detail-container">
    <el-card v-loading="loading">
      <!-- 返回按钮 -->
      <div class="back-btn">
        <el-button @click="goBack" link>
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>

      <div v-if="resourceDetail" class="detail-content">
        <!-- 头部信息 -->
        <div class="detail-header">
          <div class="header-left">
            <!-- 封面 -->
            <div class="cover-box">
              <el-image
                v-if="resourceDetail.coverImage"
                :src="getImageUrl(resourceDetail.coverImage)"
                fit="cover"
              >
                <template #error>
                  <div class="cover-placeholder">
                    <el-icon :size="60">
                      <component :is="getTypeIcon(resourceDetail.resourceType)" />
                    </el-icon>
                  </div>
                </template>
              </el-image>
              <div v-else class="cover-placeholder">
                <el-icon :size="60">
                  <component :is="getTypeIcon(resourceDetail.resourceType)" />
                </el-icon>
              </div>
            </div>
          </div>
          <div class="header-right">
            <h1 class="resource-title">{{ resourceDetail.resourceName }}</h1>
            <div class="resource-meta">
              <el-tag :type="getTypeTagColor(resourceDetail.resourceType)">
                {{ resourceDetail.resourceTypeName }}
              </el-tag>
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ resourceDetail.uploaderName || '管理员' }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ formatDateTime(resourceDetail.createTime) }}
              </span>
            </div>
            <div class="resource-stats">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ resourceDetail.viewCount || 0 }} 次浏览
              </span>
              <span class="stat-item">
                <el-icon><Download /></el-icon>
                {{ resourceDetail.downloadCount || 0 }} 次下载
              </span>
              <span class="stat-item" v-if="resourceDetail.fileSizeFormat">
                <el-icon><Files /></el-icon>
                {{ resourceDetail.fileSizeFormat }}
              </span>
            </div>
            <div class="resource-tags" v-if="resourceDetail.tags">
              <el-tag
                v-for="tag in resourceDetail.tags.split(',')"
                :key="tag"
                type="info"
                size="small"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="handleAccess">
                <el-icon><VideoPlay /></el-icon>
                {{ getAccessButtonText(resourceDetail.resourceType) }}
              </el-button>
              <el-button size="large" @click="handleDownload" v-if="hasDownloadUrl">
                <el-icon><Download /></el-icon>
                下载资源
              </el-button>
            </div>
          </div>
        </div>

        <!-- 资源描述 -->
        <el-divider content-position="left">
          <el-icon><InfoFilled /></el-icon>
          资源描述
        </el-divider>
        <div class="resource-description">
          {{ resourceDetail.description || '暂无描述' }}
        </div>

        <!-- 资源详情 - 根据类型显示不同内容 -->
        <el-divider content-position="left">
          <el-icon><Document /></el-icon>
          资源详情
        </el-divider>
        <div class="resource-info">
          <!-- 虚拟仿真资源 -->
          <template v-if="resourceDetail.resourceType === 'SIMULATION'">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="技术栈">
                {{ resourceDetail.technology || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="支持平台">
                {{ resourceDetail.supportPlatform || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="最低配置" :span="2">
                {{ resourceDetail.minRequirement || '无特殊要求' }}
              </el-descriptions-item>
              <el-descriptions-item label="启动地址" :span="2">
                <el-link v-if="resourceDetail.simulationUrl" :href="resourceDetail.simulationUrl" target="_blank" type="primary">
                  {{ resourceDetail.simulationUrl }}
                </el-link>
                <span v-else>暂无</span>
              </el-descriptions-item>
            </el-descriptions>
          </template>

          <!-- 视频资源 -->
          <template v-else-if="resourceDetail.resourceType === 'VIDEO'">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="视频时长">
                {{ resourceDetail.durationFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="分辨率">
                {{ resourceDetail.resolution || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="视频格式">
                {{ resourceDetail.videoFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="字幕">
                {{ resourceDetail.subtitleUrl ? '有字幕' : '无字幕' }}
              </el-descriptions-item>
            </el-descriptions>
            <!-- 视频播放器 -->
            <div class="video-player" v-if="resourceDetail.videoUrl">
              <video
                controls
                :src="getMediaUrl(resourceDetail.videoUrl)"
                width="100%"
                poster=""
              >
                您的浏览器不支持视频播放
              </video>
            </div>
          </template>

          <!-- 音频资源 -->
          <template v-else-if="resourceDetail.resourceType === 'AUDIO'">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="音频时长">
                {{ resourceDetail.audioDurationFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="音频格式">
                {{ resourceDetail.audioFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="比特率">
                {{ resourceDetail.bitrate || '未知' }}
              </el-descriptions-item>
            </el-descriptions>
            <!-- 音频播放器 -->
            <div class="audio-player" v-if="resourceDetail.audioUrl">
              <audio controls :src="getMediaUrl(resourceDetail.audioUrl)" style="width: 100%">
                您的浏览器不支持音频播放
              </audio>
            </div>
          </template>

          <!-- 文档资源 -->
          <template v-else-if="resourceDetail.resourceType === 'DOCUMENT'">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="文档格式">
                {{ resourceDetail.documentFormat || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="页数">
                {{ resourceDetail.pageCount || '未知' }} 页
              </el-descriptions-item>
              <el-descriptions-item label="预览链接" :span="2" v-if="resourceDetail.previewUrl">
                <el-link :href="resourceDetail.previewUrl" target="_blank" type="primary">
                  在线预览
                </el-link>
              </el-descriptions-item>
            </el-descriptions>
            <!-- 文档预览iframe -->
            <div class="document-preview" v-if="resourceDetail.previewUrl">
              <iframe :src="resourceDetail.previewUrl" width="100%" height="600px" frameborder="0"></iframe>
            </div>
          </template>
        </div>

        <!-- 相关资源推荐 -->
        <div v-if="relatedResources.length > 0" class="related-section">
          <el-divider content-position="left">
            <el-icon><Collection /></el-icon>
            相关推荐
          </el-divider>
          <div class="related-list">
            <div
              v-for="item in relatedResources"
              :key="item.id"
              class="related-item"
              @click="goToResource(item.id)"
            >
              <div class="item-cover">
                <el-icon :size="24">
                  <component :is="getTypeIcon(item.resourceType)" />
                </el-icon>
              </div>
              <div class="item-info">
                <h4>{{ item.resourceName }}</h4>
                <p>
                  <el-tag size="small" :type="getTypeTagColor(item.resourceType)">
                    {{ item.resourceTypeName }}
                  </el-tag>
                  <span class="view-count">{{ item.viewCount || 0 }} 次浏览</span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else-if="!loading" description="资源不存在或已删除" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getResourceById, getResourcePage, recordDownload } from '@/api/resource'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

// 数据
const resourceDetail = ref(null)
const relatedResources = ref([])
const loading = ref(false)

/**
 * 是否有下载链接
 */
const hasDownloadUrl = computed(() => {
  if (!resourceDetail.value) return false
  const detail = resourceDetail.value
  return detail.videoUrl || detail.audioUrl || detail.documentUrl || detail.simulationUrl
})

/**
 * 格式化日期时间
 */
const formatDateTime = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 获取类型图标
 */
const getTypeIcon = (type) => {
  const iconMap = {
    SIMULATION: 'Monitor',
    VIDEO: 'VideoCamera',
    AUDIO: 'Headset',
    DOCUMENT: 'Document'
  }
  return iconMap[type] || 'Folder'
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
 * 获取访问按钮文本
 */
const getAccessButtonText = (type) => {
  const textMap = {
    SIMULATION: '启动仿真',
    VIDEO: '播放视频',
    AUDIO: '播放音频',
    DOCUMENT: '在线阅读'
  }
  return textMap[type] || '查看资源'
}

/**
 * 获取图片URL
 */
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 静态资源直接访问public目录，不走/api代理
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
  // 静态资源直接访问public目录，不走/api代理
  return url
}

/**
 * 获取资源详情
 */
const getDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('资源ID不存在')
    goBack()
    return
  }

  loading.value = true
  try {
    const { data } = await getResourceById(id)
    resourceDetail.value = data
    // 获取相关资源
    getRelatedResources()
  } catch (error) {
    console.error('获取资源详情失败:', error)
    ElMessage.error(error.message || '获取资源详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取相关资源
 */
const getRelatedResources = async () => {
  if (!resourceDetail.value) return
  try {
    const { data } = await getResourcePage({
      resourceType: resourceDetail.value.resourceType,
      status: 1,
      pageNum: 1,
      pageSize: 5
    })
    // 过滤掉当前资源
    relatedResources.value = (data.records || []).filter(
      (item) => item.id !== resourceDetail.value?.id
    )
  } catch (error) {
    console.error('获取相关资源失败:', error)
  }
}

/**
 * 处理访问/启动
 */
const handleAccess = () => {
  if (!resourceDetail.value) return
  const detail = resourceDetail.value

  let url = ''
  switch (detail.resourceType) {
    case 'SIMULATION':
      url = detail.simulationUrl
      break
    case 'VIDEO':
      // 视频直接在页面播放，滚动到播放器位置
      const videoPlayer = document.querySelector('.video-player')
      if (videoPlayer) {
        videoPlayer.scrollIntoView({ behavior: 'smooth' })
      }
      return
    case 'AUDIO':
      // 音频直接在页面播放，滚动到播放器位置
      const audioPlayer = document.querySelector('.audio-player')
      if (audioPlayer) {
        audioPlayer.scrollIntoView({ behavior: 'smooth' })
      }
      return
    case 'DOCUMENT':
      url = detail.previewUrl || detail.documentUrl
      break
  }

  if (url) {
    window.open(url.startsWith('http') ? url : getMediaUrl(url), '_blank')
  } else {
    ElMessage.warning('暂无可访问的资源链接')
  }
}

/**
 * 处理下载
 */
const handleDownload = async () => {
  if (!resourceDetail.value) return
  const detail = resourceDetail.value

  let url = detail.videoUrl || detail.audioUrl || detail.documentUrl
  if (!url) {
    ElMessage.warning('暂无可下载的资源')
    return
  }

  // 记录下载
  try {
    await recordDownload(detail.id)
  } catch (error) {
    console.error('记录下载失败:', error)
  }

  // 下载文件
  const downloadUrl = url.startsWith('http') ? url : getMediaUrl(url)
  const link = document.createElement('a')
  link.href = downloadUrl
  link.download = detail.resourceName
  link.target = '_blank'
  link.click()
}

/**
 * 跳转到其他资源
 */
const goToResource = (id) => {
  router.push(`/resource/${id}`)
  // 刷新页面数据
  resourceDetail.value = null
  getDetail()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

/**
 * 返回列表
 */
const goBack = () => {
  router.push('/resource')
}

onMounted(() => {
  getDetail()
})
</script>

<style lang="scss" scoped>
.resource-detail-container {
  padding: 20px;

  .el-card {
    max-width: 1200px;
    margin: 0 auto;

    :deep(.el-card__body) {
      padding: 30px;
    }
  }
}

.back-btn {
  margin-bottom: 20px;
}

.detail-content {
  .detail-header {
    display: flex;
    gap: 30px;
    margin-bottom: 30px;

    .header-left {
      .cover-box {
        width: 300px;
        height: 200px;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

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
    }

    .header-right {
      flex: 1;
      display: flex;
      flex-direction: column;

      .resource-title {
        font-size: 24px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 15px;
        line-height: 1.4;
      }

      .resource-meta {
        display: flex;
        align-items: center;
        gap: 20px;
        margin-bottom: 15px;
        color: #909399;
        font-size: 14px;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 5px;
        }
      }

      .resource-stats {
        display: flex;
        gap: 25px;
        margin-bottom: 15px;
        color: #606266;
        font-size: 14px;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 5px;
        }
      }

      .resource-tags {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        margin-bottom: 20px;
      }

      .action-buttons {
        margin-top: auto;
        display: flex;
        gap: 15px;
      }
    }
  }

  .resource-description {
    font-size: 15px;
    line-height: 1.8;
    color: #606266;
    padding: 15px 20px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 20px;
  }

  .resource-info {
    margin-bottom: 30px;

    .video-player,
    .audio-player,
    .document-preview {
      margin-top: 20px;
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;
    }

    .video-player video {
      border-radius: 8px;
      max-height: 500px;
    }
  }

  .related-section {
    margin-top: 30px;

    .related-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 15px;

      .related-item {
        display: flex;
        align-items: center;
        padding: 15px;
        background: #f5f7fa;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #e4e7ed;
          transform: translateX(5px);
        }

        .item-cover {
          width: 50px;
          height: 50px;
          border-radius: 8px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          margin-right: 15px;
          flex-shrink: 0;
        }

        .item-info {
          flex: 1;
          overflow: hidden;

          h4 {
            font-size: 14px;
            color: #303133;
            margin-bottom: 8px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          p {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .resource-detail-container {
    padding: 10px;

    .el-card :deep(.el-card__body) {
      padding: 20px 15px;
    }
  }

  .detail-content .detail-header {
    flex-direction: column;

    .header-left .cover-box {
      width: 100%;
      height: 180px;
    }

    .header-right {
      .resource-title {
        font-size: 20px;
      }

      .resource-meta,
      .resource-stats {
        flex-wrap: wrap;
        gap: 10px;
      }

      .action-buttons {
        flex-direction: column;
      }
    }
  }
}
</style>
