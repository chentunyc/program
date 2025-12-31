<template>
  <div class="favorites-container">
    <!-- 统计卡片 -->
    <div class="stats-section">
      <div class="stats-cards">
        <div class="stat-card total" :class="{ active: !activeType }" @click="filterByType('')">
          <div class="stat-icon">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalCount || 0 }}</span>
            <span class="stat-label">全部收藏</span>
          </div>
        </div>
        <div class="stat-card resource" :class="{ active: activeType === 'RESOURCE' }" @click="filterByType('RESOURCE')">
          <div class="stat-icon">
            <el-icon><Folder /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.resourceCount || 0 }}</span>
            <span class="stat-label">资源</span>
          </div>
        </div>
        <div class="stat-card news" :class="{ active: activeType === 'NEWS' }" @click="filterByType('NEWS')">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.newsCount || 0 }}</span>
            <span class="stat-label">新闻</span>
          </div>
        </div>
        <div class="stat-card course" :class="{ active: activeType === 'COURSE' }" @click="filterByType('COURSE')">
          <div class="stat-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.courseCount || 0 }}</span>
            <span class="stat-label">课程</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 收藏列表 -->
    <div class="favorites-list" v-loading="loading">
      <template v-if="favoriteList.length > 0">
        <div class="favorite-grid">
          <div
            v-for="item in favoriteList"
            :key="item.id"
            class="favorite-card"
          >
            <!-- 封面 -->
            <div class="card-cover" @click="goToDetail(item)">
              <el-image
                v-if="item.coverImage"
                :src="getImageUrl(item.coverImage)"
                fit="cover"
              >
                <template #error>
                  <div class="cover-placeholder">
                    <el-icon :size="40"><Star /></el-icon>
                  </div>
                </template>
              </el-image>
              <div v-else class="cover-placeholder">
                <el-icon :size="40"><Star /></el-icon>
              </div>
              <el-tag class="type-tag" :type="getTypeTagColor(item.resourceType)" size="small">
                {{ item.resourceTypeName || getTypeName(item.resourceType) }}
              </el-tag>
            </div>
            <!-- 内容 -->
            <div class="card-content" @click="goToDetail(item)">
              <h3 class="card-title" :title="item.resourceName">{{ item.resourceName || '未知资源' }}</h3>
              <p class="card-desc">{{ item.description || '暂无描述' }}</p>
            </div>
            <!-- 底部信息 -->
            <div class="card-footer">
              <div class="footer-left">
                <span class="collect-time">{{ formatDate(item.createTime) }} 收藏</span>
              </div>
              <div class="footer-right">
                <el-button type="danger" size="small" link @click.stop="handleRemove(item)">
                  <el-icon><Delete /></el-icon>
                  取消收藏
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <el-empty v-else-if="!loading" description="暂无收藏内容">
        <el-button type="primary" @click="goToResource">去浏览资源</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFavoriteList, getFavoriteStats, removeFavorite } from '@/api/favorite'
import dayjs from 'dayjs'

const router = useRouter()

// 数据
const loading = ref(false)
const favoriteList = ref([])
const total = ref(0)
const activeType = ref('')
const stats = ref({
  totalCount: 0,
  resourceCount: 0,
  newsCount: 0,
  noticeCount: 0,
  courseCount: 0,
  labCount: 0
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  resourceType: ''
})

/**
 * 获取收藏统计
 */
const fetchStats = async () => {
  try {
    const { data } = await getFavoriteStats()
    stats.value = data
  } catch (error) {
    console.error('获取统计失败:', error)
  }
}

/**
 * 获取收藏列表
 */
const fetchFavoriteList = async () => {
  loading.value = true
  try {
    const { data } = await getFavoriteList(queryParams)
    favoriteList.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error(error.message || '获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 按类型筛选
 */
const filterByType = (type) => {
  activeType.value = type
  queryParams.resourceType = type
  queryParams.pageNum = 1
  fetchFavoriteList()
}

/**
 * 取消收藏
 */
const handleRemove = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该资源吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await removeFavorite(item.resourceType, item.resourceId)
    ElMessage.success('取消收藏成功')

    // 刷新列表和统计
    fetchFavoriteList()
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error(error.message || '取消收藏失败')
    }
  }
}

/**
 * 跳转到详情页
 */
const goToDetail = (item) => {
  // 根据资源类型跳转到不同的详情页
  switch (item.resourceType) {
    case 'RESOURCE':
      router.push(`/resource/${item.resourceId}`)
      break
    case 'NEWS':
      router.push(`/news/${item.resourceId}`)
      break
    case 'COURSE':
      router.push(`/course/${item.resourceId}`)
      break
    default:
      ElMessage.info('暂不支持查看该类型资源')
  }
}

/**
 * 跳转到资源中心
 */
const goToResource = () => {
  router.push('/resource')
}

/**
 * 分页大小改变
 */
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  fetchFavoriteList()
}

/**
 * 页码改变
 */
const handleCurrentChange = (page) => {
  queryParams.pageNum = page
  fetchFavoriteList()
}

/**
 * 获取类型名称
 */
const getTypeName = (type) => {
  const nameMap = {
    RESOURCE: '资源',
    NEWS: '新闻',
    NOTICE: '公告',
    COURSE: '课程',
    LAB: '实验室'
  }
  return nameMap[type] || '其他'
}

/**
 * 获取类型标签颜色
 */
const getTypeTagColor = (type) => {
  const colorMap = {
    RESOURCE: 'primary',
    NEWS: 'success',
    NOTICE: 'info',
    COURSE: 'warning',
    LAB: 'danger'
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
  return url
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}

onMounted(() => {
  fetchStats()
  fetchFavoriteList()
})
</script>

<style lang="scss" scoped>
.favorites-container {
  padding: 0;
}

// 统计卡片
.stats-section {
  margin-bottom: 20px;

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 15px;

    .stat-card {
      display: flex;
      align-items: center;
      padding: 15px;
      background: #f5f7fa;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      border: 2px solid transparent;

      &:hover {
        background: #e9ecf0;
      }

      &.active {
        border-color: var(--el-color-primary);
        background: var(--el-color-primary-light-9);
      }

      .stat-icon {
        width: 45px;
        height: 45px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;

        .el-icon {
          font-size: 22px;
          color: #fff;
        }
      }

      .stat-info {
        display: flex;
        flex-direction: column;

        .stat-value {
          font-size: 22px;
          font-weight: 700;
          color: #303133;
        }

        .stat-label {
          font-size: 13px;
          color: #909399;
        }
      }

      &.total .stat-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.resource .stat-icon {
        background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      }

      &.news .stat-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.course .stat-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
    }
  }
}

// 收藏列表
.favorites-list {
  min-height: 300px;

  .favorite-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }

  .favorite-card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;
    border: 1px solid #ebeef5;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
    }

    .card-cover {
      position: relative;
      height: 140px;
      background: #f5f7fa;
      cursor: pointer;

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
      cursor: pointer;

      .card-title {
        font-size: 15px;
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
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 15px;
      border-top: 1px solid #ebeef5;
      font-size: 12px;
      color: #909399;

      .collect-time {
        color: #c0c4cc;
      }
    }
  }
}

// 分页
.pagination-area {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 10px 0;
}

// 响应式
@media screen and (max-width: 1024px) {
  .stats-section .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .favorites-list .favorite-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .stats-section .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;

    .stat-card {
      padding: 12px;

      .stat-icon {
        width: 38px;
        height: 38px;
        margin-right: 10px;

        .el-icon {
          font-size: 18px;
        }
      }

      .stat-info {
        .stat-value {
          font-size: 18px;
        }

        .stat-label {
          font-size: 12px;
        }
      }
    }
  }

  .favorites-list .favorite-grid {
    grid-template-columns: 1fr;
  }
}
</style>
