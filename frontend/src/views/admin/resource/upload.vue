<template>
  <div class="resource-upload-container">
    <div class="page-header">
      <h1>资源上传</h1>
      <p>Resource Upload</p>
    </div>

    <!-- 上传说明卡片 -->
    <el-card class="info-card">
      <el-alert
        title="上传说明"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <ul class="upload-tips">
            <li>上传的资源将自动设置为<strong>待审核</strong>状态，需要资源管理员审核通过后才能发布</li>
            <li>支持的资源类型：虚拟仿真、视频、音频、文档</li>
            <li>单个文件最大支持 500MB，封面图片最大 5MB</li>
          </ul>
        </template>
      </el-alert>
    </el-card>

    <!-- 上传表单 -->
    <el-card class="form-card">
      <el-form
        ref="formRef"
        :model="resourceForm"
        :rules="formRules"
        label-width="100px"
        label-position="top"
      >
        <el-row :gutter="24">
          <el-col :xs="24" :sm="12">
            <el-form-item label="资源名称" prop="resourceName">
              <el-input
                v-model="resourceForm.resourceName"
                placeholder="请输入资源名称"
                maxlength="100"
                clearable
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="资源类型" prop="resourceType">
              <el-select
                v-model="resourceForm.resourceType"
                placeholder="请选择资源类型"
                style="width: 100%"
                @change="handleTypeChange"
              >
                <el-option label="虚拟仿真" value="SIMULATION">
                  <el-icon><Monitor /></el-icon>
                  <span style="margin-left: 8px">虚拟仿真</span>
                </el-option>
                <el-option label="视频" value="VIDEO">
                  <el-icon><VideoCamera /></el-icon>
                  <span style="margin-left: 8px">视频</span>
                </el-option>
                <el-option label="音频" value="AUDIO">
                  <el-icon><Headset /></el-icon>
                  <span style="margin-left: 8px">音频</span>
                </el-option>
                <el-option label="文档" value="DOCUMENT">
                  <el-icon><Document /></el-icon>
                  <span style="margin-left: 8px">文档</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="资源描述" prop="description">
          <el-input
            v-model="resourceForm.description"
            type="textarea"
            placeholder="请输入资源描述，便于用户了解资源内容"
            :rows="4"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-row :gutter="24">
          <el-col :xs="24" :md="8">
            <el-form-item label="封面图片">
              <div class="cover-upload-area">
                <el-upload
                  class="cover-uploader"
                  :show-file-list="false"
                  :http-request="handleCoverUpload"
                  accept="image/*"
                >
                  <el-image
                    v-if="resourceForm.coverImage"
                    :src="getCoverUrl(resourceForm.coverImage)"
                    fit="cover"
                    class="cover-preview"
                  >
                    <template #error>
                      <div class="cover-error">
                        <el-icon><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>
                  <div v-else class="upload-placeholder" v-loading="coverUploading">
                    <el-icon class="upload-icon"><Plus /></el-icon>
                    <span>点击上传封面</span>
                    <span class="upload-hint">建议尺寸: 300x200</span>
                  </div>
                </el-upload>
                <el-button
                  v-if="resourceForm.coverImage"
                  type="danger"
                  size="small"
                  text
                  @click="resourceForm.coverImage = ''"
                >
                  移除封面
                </el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="16">
            <el-form-item label="资源文件" prop="resourceUrl">
              <div class="file-upload-area">
                <el-radio-group v-model="useUrlMode" class="mode-switch">
                  <el-radio-button :label="false">
                    <el-icon><Upload /></el-icon>
                    <span>上传文件</span>
                  </el-radio-button>
                  <el-radio-button :label="true">
                    <el-icon><Link /></el-icon>
                    <span>URL地址</span>
                  </el-radio-button>
                </el-radio-group>

                <!-- 文件上传模式 -->
                <template v-if="!useUrlMode">
                  <el-upload
                    class="file-uploader"
                    drag
                    :show-file-list="false"
                    :http-request="handleFileUpload"
                    :accept="getAcceptFileTypes()"
                    :disabled="!resourceForm.resourceType"
                  >
                    <div class="upload-content" v-loading="fileUploading">
                      <template v-if="resourceForm.resourceUrl && !fileUploading">
                        <el-icon class="el-icon--success"><CircleCheckFilled /></el-icon>
                        <div class="upload-success-text">文件已上传</div>
                        <div class="uploaded-file">{{ getFileName(resourceForm.resourceUrl) }}</div>
                      </template>
                      <template v-else>
                        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                        <div class="el-upload__text">
                          将文件拖到此处，或<em>点击上传</em>
                        </div>
                        <div class="el-upload__tip" v-if="resourceForm.resourceType">
                          支持格式：{{ getAcceptFileTypes() }}
                        </div>
                        <div class="el-upload__tip warning" v-else>
                          请先选择资源类型
                        </div>
                      </template>
                    </div>
                  </el-upload>
                </template>

                <!-- URL输入模式 -->
                <template v-else>
                  <el-input
                    v-model="resourceForm.resourceUrl"
                    placeholder="请输入资源URL（如视频地址、仿真链接等）"
                    clearable
                  >
                    <template #prepend>
                      <el-icon><Link /></el-icon>
                    </template>
                  </el-input>
                  <div class="url-hint">
                    适用于在线资源，如视频网站链接、虚拟仿真平台地址等
                  </div>
                </template>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :xs="24" :sm="12">
            <el-form-item label="资源标签">
              <el-input
                v-model="resourceForm.tags"
                placeholder="多个标签用逗号分隔，如：物理,化学,实验"
                clearable
              >
                <template #prepend>
                  <el-icon><CollectionTag /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="共享设置">
              <el-switch
                v-model="resourceForm.isShared"
                :active-value="1"
                :inactive-value="0"
                active-text="公开共享"
                inactive-text="不公开"
                inline-prompt
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #909399"
              />
              <div class="share-hint">
                {{ resourceForm.isShared === 1 ? '资源审核通过后访客也可见' : '资源审核通过后访客不可见' }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 状态提示（固定为待审核） -->
        <el-form-item label="资源状态">
          <el-tag type="warning" size="large">
            <el-icon><Clock /></el-icon>
            待审核
          </el-tag>
          <span class="status-hint">上传的资源需要管理员审核后才能发布</span>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item class="submit-area">
          <el-button
            type="primary"
            size="large"
            :loading="submitting"
            :icon="Upload"
            @click="handleSubmit"
          >
            {{ submitting ? '提交中...' : '提交上传' }}
          </el-button>
          <el-button size="large" @click="handleReset">
            重置表单
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 上传成功对话框 -->
    <el-dialog
      v-model="successDialogVisible"
      title="上传成功"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="success-content">
        <el-icon class="success-icon"><CircleCheckFilled /></el-icon>
        <h3>资源上传成功！</h3>
        <p>您上传的资源已提交，等待管理员审核。</p>
        <p class="resource-name">{{ lastUploadedName }}</p>
      </div>
      <template #footer>
        <el-button @click="handleContinueUpload">继续上传</el-button>
        <el-button type="primary" @click="handleGoToResource">
          查看资源中心
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Upload,
  Plus,
  Link,
  Picture,
  Monitor,
  VideoCamera,
  Headset,
  Document,
  CollectionTag,
  Clock,
  CircleCheckFilled,
  UploadFilled
} from '@element-plus/icons-vue'
import {
  createResource,
  uploadResourceFile,
  uploadCoverImage
} from '@/api/resource'

const router = useRouter()

// 表单引用
const formRef = ref(null)

// 上传状态
const coverUploading = ref(false)
const fileUploading = ref(false)
const submitting = ref(false)
const useUrlMode = ref(false) // false: 上传模式, true: URL模式

// 成功对话框
const successDialogVisible = ref(false)
const lastUploadedName = ref('')

// 资源表单
const resourceForm = reactive({
  resourceName: '',
  resourceType: '',
  description: '',
  coverImage: '',
  resourceUrl: '',
  tags: '',
  isShared: 0,
  status: 0 // 固定为待审核状态
})

// 表单验证规则
const formRules = {
  resourceName: [
    { required: true, message: '请输入资源名称', trigger: 'blur' },
    { max: 100, message: '资源名称不能超过100个字符', trigger: 'blur' }
  ],
  resourceType: [
    { required: true, message: '请选择资源类型', trigger: 'change' }
  ],
  resourceUrl: [
    { required: true, message: '请上传资源文件或输入URL', trigger: 'blur' }
  ]
}

/**
 * 获取封面URL
 */
const getCoverUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return '/api' + path
}

/**
 * 获取文件名
 */
const getFileName = (url) => {
  if (!url) return ''
  return url.split('/').pop()
}

/**
 * 获取允许的文件类型
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
 * 资源类型变化时清空已上传的文件
 */
const handleTypeChange = () => {
  if (!useUrlMode.value) {
    resourceForm.resourceUrl = ''
  }
}

/**
 * 处理封面图片上传
 */
const handleCoverUpload = async (options) => {
  const file = options.file
  // 验证文件类型
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('请上传 JPG/PNG/GIF/WEBP 格式的图片')
    return
  }
  // 验证文件大小 (5MB)
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
  if (!resourceForm.resourceType) {
    ElMessage.error('请先选择资源类型')
    return
  }

  // 验证文件大小 (500MB)
  if (file.size > 500 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 500MB')
    return
  }

  fileUploading.value = true
  try {
    const { data } = await uploadResourceFile(file, resourceForm.resourceType)
    resourceForm.resourceUrl = data
    ElMessage.success('文件上传成功')
  } catch (error) {
    ElMessage.error(error.message || '文件上传失败')
  } finally {
    fileUploading.value = false
  }
}

/**
 * 重置表单
 */
const handleReset = () => {
  Object.assign(resourceForm, {
    resourceName: '',
    resourceType: '',
    description: '',
    coverImage: '',
    resourceUrl: '',
    tags: '',
    isShared: 0,
    status: 0
  })
  useUrlMode.value = false
  if (formRef.value) {
    formRef.value.clearValidate()
  }
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

  // 额外验证：确保资源URL已设置
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
    const submitData = {
      ...resourceForm,
      status: 0 // 强制设为待审核状态
    }

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

    await createResource(submitData)
    lastUploadedName.value = resourceForm.resourceName
    successDialogVisible.value = true
  } catch (error) {
    ElMessage.error(error.message || '上传失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

/**
 * 继续上传
 */
const handleContinueUpload = () => {
  successDialogVisible.value = false
  handleReset()
}

/**
 * 前往资源中心
 */
const handleGoToResource = () => {
  successDialogVisible.value = false
  router.push('/resource')
}
</script>

<style lang="scss" scoped>
.resource-upload-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;

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

  .info-card {
    margin-bottom: 20px;

    .upload-tips {
      margin: 10px 0 0;
      padding-left: 20px;

      li {
        margin-bottom: 5px;
        color: #606266;

        strong {
          color: #e6a23c;
        }
      }
    }
  }

  .form-card {
    :deep(.el-form-item__label) {
      font-weight: 500;
    }
  }

  // 封面上传区域
  .cover-upload-area {
    .cover-uploader {
      :deep(.el-upload) {
        border: 2px dashed #d9d9d9;
        border-radius: 8px;
        cursor: pointer;
        overflow: hidden;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
        }
      }
    }

    .cover-preview {
      width: 200px;
      height: 133px;
      display: block;
    }

    .cover-error {
      width: 200px;
      height: 133px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f7fa;
      color: #909399;
      font-size: 32px;
    }

    .upload-placeholder {
      width: 200px;
      height: 133px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #8c939d;
      background: #fafafa;

      .upload-icon {
        font-size: 32px;
        margin-bottom: 8px;
        color: #c0c4cc;
      }

      span {
        font-size: 14px;
      }

      .upload-hint {
        font-size: 12px;
        color: #c0c4cc;
        margin-top: 4px;
      }
    }
  }

  // 文件上传区域
  .file-upload-area {
    .mode-switch {
      margin-bottom: 15px;

      .el-icon {
        margin-right: 4px;
      }
    }

    .file-uploader {
      width: 100%;

      :deep(.el-upload) {
        width: 100%;
      }

      :deep(.el-upload-dragger) {
        width: 100%;
        padding: 40px 20px;
        border-radius: 8px;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
        }
      }

      .upload-content {
        text-align: center;
      }

      .el-icon--upload {
        font-size: 48px;
        color: #c0c4cc;
        margin-bottom: 10px;
      }

      .el-icon--success {
        font-size: 48px;
        color: #67c23a;
        margin-bottom: 10px;
      }

      .upload-success-text {
        font-size: 16px;
        color: #67c23a;
        margin-bottom: 5px;
      }

      .uploaded-file {
        font-size: 12px;
        color: #909399;
        word-break: break-all;
      }

      .el-upload__tip {
        font-size: 12px;
        color: #909399;
        margin-top: 10px;

        &.warning {
          color: #e6a23c;
        }
      }
    }

    .url-hint {
      font-size: 12px;
      color: #909399;
      margin-top: 8px;
    }
  }

  // 共享设置提示
  .share-hint {
    font-size: 12px;
    color: #909399;
    margin-top: 8px;
  }

  // 状态提示
  .status-hint {
    font-size: 12px;
    color: #909399;
    margin-left: 10px;
  }

  // 提交区域
  .submit-area {
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;

    :deep(.el-form-item__content) {
      justify-content: center;
    }
  }

  // 成功对话框
  .success-content {
    text-align: center;
    padding: 20px 0;

    .success-icon {
      font-size: 64px;
      color: #67c23a;
    }

    h3 {
      margin: 15px 0 10px;
      color: #303133;
    }

    p {
      color: #606266;
      margin: 5px 0;
    }

    .resource-name {
      color: #409eff;
      font-weight: 500;
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .resource-upload-container {
    padding: 10px;

    .cover-upload-area {
      .cover-preview,
      .cover-error,
      .upload-placeholder {
        width: 150px;
        height: 100px;
      }
    }

    .submit-area {
      :deep(.el-button) {
        width: 100%;
        margin-bottom: 10px;
        margin-left: 0 !important;
      }
    }
  }
}
</style>
