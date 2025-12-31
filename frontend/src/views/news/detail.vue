<template>
  <div class="news-detail-container">
    <el-card v-loading="loading">
      <!-- 返回按钮 -->
      <div class="back-btn">
        <el-button @click="goBack" link>
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>

      <div v-if="newsDetail" class="detail-content">
        <!-- 标题 -->
        <h1 class="news-title">{{ newsDetail.title }}</h1>

        <!-- 元信息 -->
        <div class="news-meta">
          <el-tag :type="getCategoryTag(newsDetail.category)" size="small">
            {{ newsDetail.category || '未分类' }}
          </el-tag>
          <span class="meta-item">
            <el-icon><User /></el-icon>
            {{ newsDetail.author || '管理员' }}
          </span>
          <span class="meta-item">
            <el-icon><Clock /></el-icon>
            {{ formatDateTime(newsDetail.publishTime) }}
          </span>
          <span class="meta-item">
            <el-icon><View /></el-icon>
            {{ newsDetail.viewCount || 0 }} 次浏览
          </span>
        </div>

        <!-- 摘要 -->
        <div v-if="newsDetail.summary" class="news-summary">
          <el-alert :title="newsDetail.summary" type="info" :closable="false" />
        </div>

        <!-- 封面图 -->
        <div v-if="newsDetail.coverImage" class="news-cover">
          <el-image :src="newsDetail.coverImage" fit="contain">
            <template #error>
              <div class="image-error">
                <el-icon :size="40"><Picture /></el-icon>
                <p>图片加载失败</p>
              </div>
            </template>
          </el-image>
        </div>

        <!-- 正文内容 -->
        <div class="news-content" v-html="formatContent(newsDetail.content)"></div>

        <!-- 底部信息 -->
        <div class="news-footer">
          <el-divider />
          <div class="footer-info">
            <span v-if="newsDetail.source">来源:{{ newsDetail.source }}</span>
            <span>发布时间:{{ formatDateTime(newsDetail.publishTime) }}</span>
          </div>
        </div>

        <!-- 推荐阅读 -->
        <div v-if="recommendList.length > 0" class="recommend-section">
          <el-divider content-position="left">
            <h3>推荐阅读</h3>
          </el-divider>
          <div class="recommend-list">
            <div
              v-for="item in recommendList"
              :key="item.id"
              class="recommend-item"
              @click="goToNews(item.id)"
            >
              <h4>{{ item.title }}</h4>
              <p>{{ formatDate(item.publishTime) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else-if="!loading" description="新闻不存在或已删除" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getNewsById, getNewsPage } from '@/api/news'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

// 数据
const newsDetail = ref(null)
const recommendList = ref([])
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
 * 格式化日期时间
 */
const formatDateTime = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}

/**
 * 格式化内容 - 处理换行
 */
const formatContent = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br/>')
}

/**
 * 获取新闻详情
 */
const getDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('新闻ID不存在')
    goBack()
    return
  }

  loading.value = true
  try {
    const { data } = await getNewsById(id)
    newsDetail.value = data
    // 获取推荐阅读
    getRecommendNews()
  } catch (error) {
    console.error('获取新闻详情失败:', error)
    ElMessage.error(error.message || '获取新闻详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取推荐阅读列表
 */
const getRecommendNews = async () => {
  try {
    const { data } = await getNewsPage({
      isRecommend: 1,
      status: 1,
      current: 1,
      size: 5
    })
    // 过滤掉当前新闻
    recommendList.value = (data.records || []).filter(
      (item) => item.id !== newsDetail.value?.id
    )
  } catch (error) {
    console.error('获取推荐新闻失败:', error)
  }
}

/**
 * 跳转到其他新闻
 */
const goToNews = (id) => {
  router.push(`/news/${id}`)
  // 刷新页面数据
  newsDetail.value = null
  getDetail()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

/**
 * 返回列表
 */
const goBack = () => {
  router.push('/news')
}

onMounted(() => {
  getDetail()
})
</script>

<style lang="scss" scoped>
.news-detail-container {
  padding: 20px;

  .el-card {
    max-width: 1000px;
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
  .news-title {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
    line-height: 1.4;
    margin-bottom: 20px;
    text-align: center;
  }

  .news-meta {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
    flex-wrap: wrap;
    padding: 15px 0;
    margin-bottom: 20px;
    font-size: 14px;
    color: #909399;
    border-bottom: 1px solid #e4e7ed;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }

  .news-summary {
    margin-bottom: 20px;

    :deep(.el-alert__title) {
      font-size: 15px;
      line-height: 1.6;
    }
  }

  .news-cover {
    margin-bottom: 30px;
    text-align: center;

    :deep(.el-image) {
      max-width: 100%;
      border-radius: 8px;
    }

    .image-error {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px;
      background: #f5f7fa;
      border-radius: 8px;
      color: #909399;

      p {
        margin-top: 10px;
      }
    }
  }

  .news-content {
    font-size: 16px;
    line-height: 1.8;
    color: #606266;
    text-align: justify;
    margin-bottom: 30px;

    :deep(img) {
      max-width: 100%;
      height: auto;
      margin: 20px 0;
      border-radius: 4px;
    }

    :deep(p) {
      margin-bottom: 15px;
    }

    :deep(h2),
    :deep(h3),
    :deep(h4) {
      margin: 20px 0 15px;
      font-weight: 600;
      color: #303133;
    }
  }

  .news-footer {
    .footer-info {
      display: flex;
      justify-content: space-between;
      font-size: 14px;
      color: #909399;
      flex-wrap: wrap;
      gap: 10px;
    }
  }

  .recommend-section {
    margin-top: 40px;

    h3 {
      font-size: 18px;
      color: #303133;
    }

    .recommend-list {
      .recommend-item {
        padding: 15px;
        margin-bottom: 10px;
        background: #f5f7fa;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #e4e7ed;
          transform: translateX(5px);
        }

        h4 {
          font-size: 15px;
          color: #303133;
          margin-bottom: 8px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        p {
          font-size: 13px;
          color: #909399;
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .news-detail-container {
    padding: 10px;

    .el-card :deep(.el-card__body) {
      padding: 20px 15px;
    }
  }

  .detail-content {
    .news-title {
      font-size: 22px;
    }

    .news-meta {
      gap: 10px;
      font-size: 13px;
    }

    .news-content {
      font-size: 15px;
    }
  }
}
</style>
