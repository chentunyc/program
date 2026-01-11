<template>
  <div class="my-resource-container">
    <div class="page-header">
      <h1>我的资源</h1>
      <p>My Resources</p>
    </div>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索资源名称/描述"
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
        <el-col :xs="24" :sm="12" :md="6">
          <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
          <el-button type="primary" :icon="Upload" @click="handleGoUpload">
            上传新资源
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 提示信息 -->
    <el-alert
      title="温馨提示"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 20px"
    >
      <template #default>
        您只能编辑自己上传的资源。资源的发布、下架和删除操作请联系资源管理员。
      </template>
    </el-alert>

    <!-- 资源列表表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="resourceList"
        style="width: 100%"
      >
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="row.coverImage"
              :preview-src-list="[row.coverImage]"
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
              <el-icon><Picture /></el-icon>
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
            <el-tag v-if="row.status === 0" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已发布</el-tag>
            <el-tag v-else type="info" size="small">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="共享" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isShared === 1" type="success" size="small">已共享</el-tag>
            <el-tag v-else type="info" size="small">未共享</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="浏览/下载" width="100">
          <template #default="{ row }">
            <span>{{ row.viewCount || 0 }}/{{ row.downloadCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
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

    <!-- 编辑资源对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑资源"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="resourceForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="资源名称" prop="resourceName">
          <el-input
            v-model="resourceForm.resourceName"
            placeholder="请输入资源名称"
            maxlength="100"
            clearable
          />
        </el-form-item>
        <el-form-item label="资源类型">
          <el-tag :type="getTypeTagColor(resourceForm.resourceType)" size="large">
            {{ getTypeName(resourceForm.resourceType) }}
          </el-tag>
          <span class="type-hint">资源类型不可修改</span>
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input
            v-model="resourceForm.description"
            type="textarea"
            placeholder="请输入资源描述"
            :rows="3"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="封面图片">
          <div class="upload-area">
            <el-upload
              class="cover-uploader"
              :show-file-list="false"
              :http-request="handleCoverUpload"
              accept="image/*"
            >
              <el-image
                v-if="resourceForm.coverImage"
                :src="resourceForm.coverImage.startsWith('http') ? resourceForm.coverImage : '/api' + resourceForm.coverImage"
                fit="cover"
                class="cover-preview"
              />
              <div v-else class="upload-placeholder" v-loading="coverUploading">
                <el-icon><Upload /></el-icon>
                <span>点击上传封面</span>
              </div>
            </el-upload>
            <el-input
              v-model="resourceForm.coverImage"
              placeholder="或输入封面图片URL"
              clearable
              style="margin-top: 10px"
            />
          </div>
        </el-form-item>
        <el-form-item label="资源文件" prop="resourceUrl">
          <div class="file-input-area">
            <el-radio-group v-model="useUrlMode" style="margin-bottom: 10px">
              <el-radio-button :label="true">
                <el-icon><Link /></el-icon> URL地址
              </el-radio-button>
              <el-radio-button :label="false">
                <el-icon><Upload /></el-icon> 上传文件
              </el-radio-button>
            </el-radio-group>
            <template v-if="useUrlMode">
              <el-input
                v-model="resourceForm.resourceUrl"
                placeholder="请输入资源URL（如视频地址、仿真链接等）"
                clearable
              />
            </template>
            <template v-else>
              <el-upload
                class="file-uploader"
                drag
                :show-file-list="false"
                :http-request="handleFileUpload"
                :accept="getAcceptFileTypes()"
              >
                <div v-loading="fileUploading">
                  <el-icon class="el-icon--upload"><Upload /></el-icon>
                  <div class="el-upload__text">
                    将文件拖到此处，或<em>点击上传</em>
                  </div>
                  <div class="el-upload__tip">
                    支持格式：{{ getAcceptFileTypes() }}
                  </div>
                </div>
              </el-upload>
              <el-input
                v-if="resourceForm.resourceUrl"
                v-model="resourceForm.resourceUrl"
                disabled
                style="margin-top: 10px"
              >
                <template #prepend>已上传</template>
              </el-input>
            </template>
          </div>
        </el-form-item>
        <el-form-item label="标签">
          <el-input
            v-model="resourceForm.tags"
            placeholder="多个标签用逗号分隔，如：物理,化学,实验"
            clearable
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="共享设置">
              <el-switch
                v-model="resourceForm.isShared"
                :active-value="1"
                :inactive-value="0"
                active-text="共享"
                inactive-text="不共享"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前状态">
              <el-tag v-if="resourceForm.status === 0" type="warning" size="large">待审核</el-tag>
              <el-tag v-else-if="resourceForm.status === 1" type="success" size="large">已发布</el-tag>
              <el-tag v-else type="info" size="large">已下架</el-tag>
              <span class="status-hint">状态由资源管理员管理</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          保存修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Picture, Upload, Link } from '@element-plus/icons-vue'
import {
  getMyResourcePage,
  getResourceById,
  updateResource,
  uploadResourceFile,
  uploadCoverImage
} from '@/api/resource'

const router = useRouter()

// 组件状态
const loading = ref(false)
const resourceList = ref([])
const total = ref(0)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  resourceType: null,
  status: null
})

// 对话框状态
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 上传状态
const coverUploading = ref(false)
const fileUploading = ref(false)
const useUrlMode = ref(true)

// 资源表单
const resourceForm = reactive({
  id: null,
  resourceName: '',
  resourceType: '',
  description: '',
  coverImage: '',
  resourceUrl: '',
  filePath: '',
  tags: '',
  isShared: 0,
  status: 0
})

// 表单验证规则
const formRules = {
  resourceName: [
    { required: true, message: '请输入资源名称', trigger: 'blur' },
    { max: 100, message: '资源名称不能超过100个字符', trigger: 'blur' }
  ],
  resourceUrl: [
    { required: true, message: '请输入资源URL', trigger: 'blur' }
  ]
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
 * 获取类型名称
 */
const getTypeName = (type) => {
  const nameMap = {
    SIMULATION: '虚拟仿真',
    VIDEO: '视频',
    AUDIO: '音频',
    DOCUMENT: '文档'
  }
  return nameMap[type] || '未知'
}

/**
 * 加载资源列表
 */
const loadResources = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    const response = await getMyResourcePage(params)
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
 * 搜索
 */
const handleSearch = () => {
  queryParams.pageNum = 1
  loadResources()
}

/**
 * 刷新
 */
const handleRefresh = () => {
  queryParams.pageNum = 1
  queryParams.keyword = ''
  queryParams.resourceType = null
  queryParams.status = null
  loadResources()
}

/**
 * 跳转到上传页面
 */
const handleGoUpload = () => {
  router.push('/admin/resource/upload')
}

/**
 * 编辑资源
 */
const handleEdit = async (row) => {
  useUrlMode.value = true

  try {
    // 从详情接口获取完整数据（包含类型特定的URL）
    const { data: detail } = await getResourceById(row.id)

    // 根据资源类型提取对应的URL
    let resourceUrl = ''
    switch (detail.resourceType) {
      case 'VIDEO':
        resourceUrl = detail.videoUrl || ''
        break
      case 'AUDIO':
        resourceUrl = detail.audioUrl || ''
        break
      case 'DOCUMENT':
        resourceUrl = detail.documentUrl || ''
        break
      case 'SIMULATION':
        resourceUrl = detail.simulationUrl || ''
        break
    }

    Object.assign(resourceForm, {
      id: detail.id,
      resourceName: detail.resourceName,
      resourceType: detail.resourceType,
      description: detail.description || '',
      coverImage: detail.coverImage || '',
      resourceUrl: resourceUrl,
      filePath: resourceUrl,
      tags: detail.tags || '',
      isShared: detail.isShared || 0,
      status: detail.status || 0
    })
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取资源详情失败')
    console.error('获取资源详情失败:', error)
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  Object.assign(resourceForm, {
    id: null,
    resourceName: '',
    resourceType: '',
    description: '',
    coverImage: '',
    resourceUrl: '',
    filePath: '',
    tags: '',
    isShared: 0,
    status: 0
  })
  coverUploading.value = false
  fileUploading.value = false
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

/**
 * 处理封面图片上传
 */
const handleCoverUpload = async (options) => {
  const file = options.file
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('请上传 JPG/PNG/GIF/WEBP 格式的图片')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }

  coverUploading.value = true
  try {
    const { data } = await uploadCoverImage(file)
    resourceForm.coverImage = data
    ElMessage.success('封面上传成功')
  } catch (error) {
    ElMessage.error(error.message || '封面上传失败')
  } finally {
    coverUploading.value = false
  }
}

/**
 * 处理资源文件上传
 */
const handleFileUpload = async (options) => {
  const file = options.file
  if (file.size > 500 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 500MB')
    return
  }

  fileUploading.value = true
  try {
    const { data } = await uploadResourceFile(file, resourceForm.resourceType)
    resourceForm.resourceUrl = data
    resourceForm.filePath = data
    ElMessage.success('文件上传成功')
  } catch (error) {
    ElMessage.error(error.message || '文件上传失败')
  } finally {
    fileUploading.value = false
  }
}

/**
 * 获取允许的文件类型提示
 */
const getAcceptFileTypes = () => {
  const typeMap = {
    VIDEO: '.mp4,.avi,.mov,.wmv,.flv,.mkv,.webm',
    AUDIO: '.mp3,.wav,.flac,.aac,.ogg,.wma',
    DOCUMENT: '.pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.txt',
    SIMULATION: '.zip,.rar,.7z,.html,.unity3d'
  }
  return typeMap[resourceForm.resourceType] || '*'
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  if (!resourceForm.resourceUrl || resourceForm.resourceUrl.trim() === '') {
    if (useUrlMode.value) {
      ElMessage.error('请输入资源URL')
    } else {
      ElMessage.error('请先上传资源文件')
    }
    return
  }

  submitting.value = true
  try {
    // 根据资源类型设置对应的URL字段
    const submitData = { ...resourceForm }
    switch (resourceForm.resourceType) {
      case 'VIDEO':
        submitData.videoUrl = resourceForm.resourceUrl
        break
      case 'AUDIO':
        submitData.audioUrl = resourceForm.resourceUrl
        break
      case 'DOCUMENT':
        submitData.documentUrl = resourceForm.resourceUrl
        break
      case 'SIMULATION':
        submitData.simulationUrl = resourceForm.resourceUrl
        break
    }

    await updateResource(submitData)
    ElMessage.success('资源更新成功')
    dialogVisible.value = false
    loadResources()
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    submitting.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadResources()
})
</script>

<style lang="scss" scoped>
.my-resource-container {
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

  .search-card {
    margin-bottom: 20px;

    .el-row {
      align-items: center;
    }
  }

  .table-card {
    .cover-placeholder-small {
      width: 60px;
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f7fa;
      border-radius: 4px;
      color: #909399;
    }

    :deep(.el-pagination) {
      margin-top: 20px;
      justify-content: center;
    }
  }

  .type-hint,
  .status-hint {
    font-size: 12px;
    color: #909399;
    margin-left: 10px;
  }

  // 上传区域样式
  .upload-area {
    width: 100%;

    .cover-uploader {
      :deep(.el-upload) {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        overflow: hidden;
        transition: border-color 0.3s;

        &:hover {
          border-color: #409eff;
        }
      }
    }

    .cover-preview {
      width: 150px;
      height: 100px;
      display: block;
    }

    .upload-placeholder {
      width: 150px;
      height: 100px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #8c939d;
      background: #fafafa;

      .el-icon {
        font-size: 28px;
        margin-bottom: 8px;
      }

      span {
        font-size: 12px;
      }
    }
  }

  .file-input-area {
    width: 100%;

    .file-uploader {
      width: 100%;

      :deep(.el-upload) {
        width: 100%;
      }

      :deep(.el-upload-dragger) {
        width: 100%;
        padding: 30px 20px;
      }

      .el-icon--upload {
        font-size: 48px;
        color: #c0c4cc;
        margin-bottom: 10px;
      }

      .el-upload__tip {
        font-size: 12px;
        color: #909399;
        margin-top: 10px;
      }
    }
  }
}

@media (max-width: 768px) {
  .my-resource-container {
    padding: 10px;

    .search-card {
      .el-col {
        margin-bottom: 10px;
      }
    }
  }
}
</style>
