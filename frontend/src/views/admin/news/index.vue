<template>
  <div class="admin-news-container">
    <div class="page-header">
      <h1>新闻管理</h1>
      <p>News Management</p>
    </div>

    <!-- 搜索和操作栏 -->
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
            placeholder="分类筛选"
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
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
            v-model="queryParams.status"
            placeholder="状态筛选"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-col>
      </el-row>

      <div class="action-buttons">
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          创建新闻
        </el-button>
        <el-button
          type="danger"
          :icon="Delete"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
    </div>

    <!-- 新闻列表表格 -->
    <el-table
      v-loading="loading"
      :data="newsList"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column label="封面" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.coverImage"
            :src="row.coverImage"
            fit="cover"
            style="width: 60px; height: 40px; border-radius: 4px"
            :preview-src-list="[row.coverImage]"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="100">
        <template #default="{ row }">
          <el-tag type="info" size="small">{{ row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="author" label="作者" width="100" />
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column label="置顶" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.isTop" type="danger" size="small">是</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="推荐" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.isRecommend" type="warning" size="small">是</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="info" size="small">草稿</el-tag>
          <el-tag v-else-if="row.status === 1" type="success" size="small">已发布</el-tag>
          <el-tag v-else type="danger" size="small">已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" width="180">
        <template #default="{ row }">
          {{ row.publishTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button
            v-if="row.status === 0"
            type="success"
            size="small"
            @click="handlePublish(row.id)"
          >
            发布
          </el-button>
          <el-button
            v-if="row.status === 1"
            type="warning"
            size="small"
            @click="handleWithdraw(row.id)"
          >
            撤回
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleToggleTop(row.id, row.isTop)"
          >
            {{ row.isTop ? '取消置顶' : '置顶' }}
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.current"
      v-model:page-size="queryParams.size"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadNews"
      @current-change="loadNews"
    />

    <!-- 创建/编辑新闻对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="newsForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="newsForm.title"
            placeholder="请输入新闻标题"
            clearable
          />
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input
            v-model="newsForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入新闻摘要"
          />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="newsForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入新闻内容"
          />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-input
            v-model="newsForm.coverImage"
            placeholder="请输入封面图URL"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select
            v-model="newsForm.category"
            placeholder="请选择分类"
            style="width: 100%"
          >
            <el-option label="系统公告" value="系统公告" />
            <el-option label="教学新闻" value="教学新闻" />
            <el-option label="科研动态" value="科研动态" />
            <el-option label="通知公告" value="通知公告" />
            <el-option label="行业资讯" value="行业资讯" />
          </el-select>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="newsForm.author" placeholder="请输入作者" clearable />
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="newsForm.source" placeholder="请输入来源" clearable />
        </el-form-item>
        <el-form-item label="标签">
          <el-input
            v-model="newsForm.tags"
            placeholder="多个标签用逗号分隔"
            clearable
          />
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="newsForm.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="是否推荐">
          <el-switch
            v-model="newsForm.isRecommend"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button @click="handleSaveDraft">保存草稿</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          发布
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete, Refresh } from '@element-plus/icons-vue'
import {
  getNewsPage,
  saveNews,
  deleteNews,
  batchDeleteNews,
  publishNews,
  withdrawNews,
  toggleTop
} from '@/api/news'

// 组件状态
const loading = ref(false)
const newsList = ref([])
const total = ref(0)
const selectedIds = ref([])

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  keyword: '',
  category: null,
  status: null
})

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 新闻表单
const newsForm = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  category: '',
  author: '',
  source: '',
  tags: '',
  isTop: 0,
  isRecommend: 0
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入新闻标题', trigger: 'blur' },
    { max: 200, message: '标题长度不能超过200个字符', trigger: 'blur' }
  ],
  summary: [
    { max: 500, message: '摘要长度不能超过500个字符', trigger: 'blur' }
  ],
  content: [{ required: true, message: '请输入新闻内容', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

// 加载新闻列表
const loadNews = async () => {
  loading.value = true
  try {
    const response = await getNewsPage(queryParams)
    newsList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载新闻列表失败')
    console.error('加载新闻列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.current = 1
  loadNews()
}

// 刷新
const handleRefresh = () => {
  queryParams.current = 1
  queryParams.keyword = ''
  queryParams.category = null
  queryParams.status = null
  loadNews()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 创建新闻
const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '创建新闻'
  resetForm()
  dialogVisible.value = true
}

// 编辑新闻
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑新闻'
  Object.assign(newsForm, {
    id: row.id,
    title: row.title,
    summary: row.summary,
    content: row.content,
    coverImage: row.coverImage,
    category: row.category,
    author: row.author,
    source: row.source,
    tags: row.tags,
    isTop: row.isTop,
    isRecommend: row.isRecommend
  })
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(newsForm, {
    id: null,
    title: '',
    summary: '',
    content: '',
    coverImage: '',
    category: '',
    author: '',
    source: '',
    tags: '',
    isTop: 0,
    isRecommend: 0
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 保存草稿
const handleSaveDraft = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  submitting.value = true
  try {
    await saveNews({ ...newsForm, status: 0 })
    ElMessage.success('草稿保存成功')
    dialogVisible.value = false
    loadNews()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

// 提交表单（发布）
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  submitting.value = true
  try {
    const response = await saveNews({ ...newsForm, status: 1 })
    ElMessage.success(isEdit.value ? '新闻更新成功' : '新闻发布成功')
    dialogVisible.value = false
    loadNews()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 发布新闻
const handlePublish = async (id) => {
  try {
    await publishNews(id)
    ElMessage.success('新闻发布成功')
    loadNews()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

// 撤回新闻
const handleWithdraw = async (id) => {
  try {
    await ElMessageBox.confirm('确定撤回该新闻吗？', '提示', {
      type: 'warning'
    })
    await withdrawNews(id)
    ElMessage.success('新闻撤回成功')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('撤回失败')
    }
  }
}

// 置顶/取消置顶
const handleToggleTop = async (id, currentIsTop) => {
  const newIsTop = currentIsTop === 1 ? 0 : 1
  try {
    await toggleTop(id, newIsTop)
    ElMessage.success(newIsTop === 1 ? '置顶成功' : '取消置顶成功')
    loadNews()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除新闻
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该新闻吗？此操作不可恢复！', '警告', {
      type: 'warning'
    })
    await deleteNews(id)
    ElMessage.success('新闻删除成功')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${selectedIds.value.length} 条新闻吗？此操作不可恢复！`,
      '警告',
      {
        type: 'warning'
      }
    )
    await batchDeleteNews(selectedIds.value)
    ElMessage.success('批量删除成功')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadNews()
})
</script>

<style lang="scss" scoped>
.admin-news-container {
  .page-header {
    margin-bottom: 20px;
    padding-bottom: 20px;
    padding-left: 30px;
    padding-top: 10px;
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

  .search-bar {
    margin-bottom: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .el-row {
      margin-bottom: 15px;
    }

    .action-buttons {
      display: flex;
      gap: 10px;
    }
  }

  :deep(.el-table) {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
  }

  :deep(.el-pagination) {
    justify-content: center;
    padding: 20px 0;
  }
}

@media (max-width: 768px) {
  .search-bar {
    .el-col {
      margin-bottom: 10px;
    }

    .action-buttons {
      flex-direction: column;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>
