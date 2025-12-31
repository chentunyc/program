<template>
  <div class="news-container">
    <div class="page-header">
      <h1>新闻公告</h1>
      <p>News & Announcements</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索新闻标题/内容"
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
            v-model="queryParams.category"
            placeholder="新闻分类"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option label="系统公告" value="系统公告" />
            <el-option label="教学新闻" value="教学新闻" />
            <el-option label="科研动态" value="科研动态" />
            <el-option label="通知公告" value="通知公告" />
            <el-option label="行业资讯" value="行业资讯" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="24" :md="8">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 新闻列表 -->
    <div class="news-list">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="news in newsList" :key="news.id">
          <div class="news-card" @click="goToDetail(news.id)">
            <!-- 封面图 -->
            <div class="news-cover">
              <el-image
                :src="news.coverImage || '/default-news-cover.jpg'"
                fit="cover"
                lazy
              >
                <template #error>
                  <div class="image-error">
                    <el-icon :size="40"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <!-- 置顶标签 -->
              <div v-if="news.isTop === 1" class="top-tag">
                <el-tag type="danger" size="small">置顶</el-tag>
              </div>
              <!-- 类型标签 -->
              <div class="type-tag">
                <el-tag :type="getCategoryTag(news.category)" size="small">
                  {{ news.category || '未分类' }}
                </el-tag>
              </div>
            </div>

            <!-- 新闻内容 -->
            <div class="news-content">
              <h3 class="news-title">{{ news.title }}</h3>
              <p class="news-summary">{{ news.summary || '暂无摘要' }}</p>
              <div class="news-meta">
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ news.viewCount || 0 }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(news.publishTime) }}
                </span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="!newsList.length && !loading" description="暂无新闻" />

      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :page-sizes="[10, 20, 30, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getNewsPage } from '@/api/news'
import dayjs from 'dayjs'

const router = useRouter()

// 查询参数
const queryParams = ref({
  keyword: '',
  category: null,
  status: 1, // 只查询已发布的(0-草稿,1-已发布,2-已下架)
  current: 1,
  size: 12
})

// 数据
const newsList = ref([])
const total = ref(0)
const loading = ref(false)

/**
 * 获取分类标签颜色
 */
const getCategoryTag = (category) => {
  const tagMap = {
    '系统公告': 'danger',
    '教学新闻': 'success',
    '科研动态': 'warning',
    '通知公告': 'danger',
    '行业资讯': 'info'
  }
  return tagMap[category] || ''
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}

/**
 * 获取新闻列表
 */
const getNewsList = async () => {
  loading.value = true
  try {
    const { data } = await getNewsPage(queryParams.value)
    newsList.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取新闻列表失败:', error)
    ElMessage.error(error.message || '获取新闻列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  queryParams.value.current = 1
  getNewsList()
}

/**
 * 重置
 */
const handleReset = () => {
  queryParams.value = {
    keyword: '',
    category: null,
    status: 1,
    current: 1,
    size: 12
  }
  getNewsList()
}

/**
 * 跳转详情
 */
const goToDetail = (id) => {
  router.push(`/news/${id}`)
}

onMounted(() => {
  getNewsList()
})
</script>

<style lang="scss" scoped>
.news-container {
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

.search-bar {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.news-list {
  min-height: 400px;

  .news-card {
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
    }

    .news-cover {
      position: relative;
      width: 100%;
      height: 200px;
      overflow: hidden;

      :deep(.el-image) {
        width: 100%;
        height: 100%;
      }

      .image-error {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        background: #f5f7fa;
        color: #909399;
      }

      .top-tag {
        position: absolute;
        top: 10px;
        left: 10px;
      }

      .type-tag {
        position: absolute;
        top: 10px;
        right: 10px;
      }
    }

    .news-content {
      padding: 15px;
      flex: 1;
      display: flex;
      flex-direction: column;

      .news-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 10px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .news-summary {
        font-size: 14px;
        color: #606266;
        margin-bottom: 15px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        flex: 1;
      }

      .news-meta {
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        color: #909399;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

@media screen and (max-width: 768px) {
  .page-header h1 {
    font-size: 22px;
  }

  .search-bar {
    padding: 15px;

    .el-col {
      margin-bottom: 10px;
    }
  }

  :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
