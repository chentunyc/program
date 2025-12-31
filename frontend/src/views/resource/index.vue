<template>
  <div class="resource-container">
    <!-- 页面头部 - 统计信息 -->
    <div class="resource-header">
      <div class="stats-cards">
        <div class="stat-card simulation" @click="filterByType('SIMULATION')">
          <div class="stat-icon">
            <el-icon><Monitor /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.simulationCount || 0 }}</span>
            <span class="stat-label">虚拟仿真</span>
          </div>
        </div>
        <div class="stat-card video" @click="filterByType('VIDEO')">
          <div class="stat-icon">
            <el-icon><VideoCamera /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.videoCount || 0 }}</span>
            <span class="stat-label">视频资源</span>
          </div>
        </div>
        <div class="stat-card audio" @click="filterByType('AUDIO')">
          <div class="stat-icon">
            <el-icon><Headset /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.audioCount || 0 }}</span>
            <span class="stat-label">音频资源</span>
          </div>
        </div>
        <div class="stat-card document" @click="filterByType('DOCUMENT')">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.documentCount || 0 }}</span>
            <span class="stat-label">文档资源</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card class="filter-card">
      <div class="filter-area">
        <div class="filter-left">
          <el-radio-group v-model="queryParams.resourceType" @change="handleSearch">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="SIMULATION">虚拟仿真</el-radio-button>
            <el-radio-button label="VIDEO">视频</el-radio-button>
            <el-radio-button label="AUDIO">音频</el-radio-button>
            <el-radio-button label="DOCUMENT">文档</el-radio-button>
          </el-radio-group>
        </div>
        <div class="filter-right">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索资源名称、描述、标签..."
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 资源列表 -->
    <div class="resource-list" v-loading="loading">
      <template v-if="resourceList.length > 0">
        <div class="resource-grid">
          <div
            v-for="item in resourceList"
            :key="item.id"
            class="resource-card"
            @click="goToDetail(item.id)"
          >
            <!-- 封面 -->
            <div class="card-cover">
              <el-image
                v-if="item.coverImage"
                :src="getImageUrl(item.coverImage)"
                fit="cover"
              >
                <template #error>
                  <div class="cover-placeholder">
                    <el-icon :size="40">
                      <component :is="getTypeIcon(item.resourceType)" />
                    </el-icon>
                  </div>
                </template>
              </el-image>
              <div v-else class="cover-placeholder">
                <el-icon :size="40">
                  <component :is="getTypeIcon(item.resourceType)" />
                </el-icon>
              </div>
              <el-tag class="type-tag" :type="getTypeTagColor(item.resourceType)" size="small">
                {{ item.resourceTypeName }}
              </el-tag>
            </div>
            <!-- 内容 -->
            <div class="card-content">
              <h3 class="card-title" :title="item.resourceName">{{ item.resourceName }}</h3>
              <p class="card-desc">{{ item.description || '暂无描述' }}</p>
              <div class="card-tags" v-if="item.tags">
                <el-tag
                  v-for="tag in item.tags.split(',').slice(0, 3)"
                  :key="tag"
                  size="small"
                  type="info"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>
            <!-- 底部信息 -->
            <div class="card-footer">
              <div class="footer-left">
                <span class="uploader">{{ item.uploaderName || '管理员' }}</span>
              </div>
              <div class="footer-right">
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ item.viewCount || 0 }}
                </span>
                <span class="stat-item">
                  <el-icon><Download /></el-icon>
                  {{ item.downloadCount || 0 }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <el-empty v-else-if="!loading" description="暂无资源数据">
        <el-button type="primary" @click="resetQuery">刷新页面</el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div class="pagination-area" v-if="total > 0">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getResourcePage, getResourceStats } from '@/api/resource'

const router = useRouter()

// 数据
const loading = ref(false)
const resourceList = ref([])
const total = ref(0)
const stats = ref({
  simulationCount: 0,
  videoCount: 0,
  audioCount: 0,
  documentCount: 0,
  totalCount: 0
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  resourceType: '',
  keyword: '',
  status: 1 // 只查询已发布的
})

/**
 * 获取资源统计
 */
const fetchStats = async () => {
  try {
    const { data } = await getResourceStats()
    stats.value = data
  } catch (error) {
    console.error('获取统计失败:', error)
  }
}

/**
 * 获取资源列表
 */
const fetchResourceList = async () => {
  loading.value = true
  try {
    const { data } = await getResourcePage(queryParams)
    resourceList.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取资源列表失败:', error)
    ElMessage.error(error.message || '获取资源列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 按类型筛选
 */
const filterByType = (type) => {
  queryParams.resourceType = type
  queryParams.pageNum = 1
  fetchResourceList()
}

/**
 * 搜索
 */
const handleSearch = () => {
  queryParams.pageNum = 1
  fetchResourceList()
}

/**
 * 重置查询
 */
const resetQuery = () => {
  queryParams.pageNum = 1
  queryParams.resourceType = ''
  queryParams.keyword = ''
  fetchResourceList()
}

/**
 * 分页大小改变
 */
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  fetchResourceList()
}

/**
 * 页码改变
 */
const handleCurrentChange = (page) => {
  queryParams.pageNum = page
  fetchResourceList()
}

/**
 * 跳转到详情页
 */
const goToDetail = (id) => {
  router.push(`/resource/${id}`)
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

onMounted(() => {
  fetchStats()
  fetchResourceList()
})
</script>

<style lang="scss" scoped>
.resource-container {
  padding: 20px;
}

// 统计卡片
.resource-header {
  margin-bottom: 20px;

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    .stat-card {
      display: flex;
      align-items: center;
      padding: 20px;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
      }

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;

        .el-icon {
          font-size: 28px;
          color: #fff;
        }
      }

      .stat-info {
        display: flex;
        flex-direction: column;

        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #303133;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }

      &.simulation .stat-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.video .stat-icon {
        background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      }

      &.audio .stat-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.document .stat-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
    }
  }
}

// 筛选区域
.filter-card {
  margin-bottom: 20px;

  .filter-area {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 15px;

    .filter-right {
      display: flex;
      gap: 10px;
    }
  }
}

// 资源列表
.resource-list {
  min-height: 400px;

  .resource-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }

  .resource-card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    }

    .card-cover {
      position: relative;
      height: 160px;
      background: #f5f7fa;

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

      .type-tag {
        position: absolute;
        top: 10px;
        right: 10px;
      }
    }

    .card-content {
      padding: 15px;

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .card-desc {
        font-size: 13px;
        color: #909399;
        line-height: 1.5;
        height: 40px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .card-tags {
        margin-top: 10px;
        display: flex;
        gap: 5px;
        flex-wrap: wrap;

        .el-tag {
          font-size: 12px;
        }
      }
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 15px;
      border-top: 1px solid #ebeef5;
      font-size: 13px;
      color: #909399;

      .footer-right {
        display: flex;
        gap: 15px;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

// 分页
.pagination-area {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px 0;
}

// 响应式
@media screen and (max-width: 1400px) {
  .resource-list .resource-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 1024px) {
  .resource-header .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .resource-list .resource-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-card .filter-area {
    flex-direction: column;
    align-items: flex-start;

    .filter-right {
      width: 100%;
      flex-wrap: wrap;

      .el-input {
        width: 100% !important;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .resource-container {
    padding: 10px;
  }

  .resource-header .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;

    .stat-card {
      padding: 15px;

      .stat-icon {
        width: 45px;
        height: 45px;
        margin-right: 12px;

        .el-icon {
          font-size: 22px;
        }
      }

      .stat-info {
        .stat-value {
          font-size: 22px;
        }

        .stat-label {
          font-size: 12px;
        }
      }
    }
  }

  .resource-list .resource-grid {
    grid-template-columns: 1fr;
  }
}
</style>
