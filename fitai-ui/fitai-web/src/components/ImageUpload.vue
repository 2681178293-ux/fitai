<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { uploadImage } from '@/api/file'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  modelValue: {
    type: [String, Array],
    default: ''
  },
  // 单图或多图模式
  multiple: {
    type: Boolean,
    default: false
  },
  // 最大文件数量（多图模式）
  limit: {
    type: Number,
    default: 9
  },
  // 最大文件大小 (MB)
  maxSize: {
    type: Number,
    default: 5
  },
  // 预览尺寸
  width: {
    type: String,
    default: '100px'
  },
  height: {
    type: String,
    default: '100px'
  },
  // 是否圆形（头像模式）
  round: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const uploading = ref(false)

// 图片列表
const imageList = computed(() => {
  if (props.multiple) {
    return Array.isArray(props.modelValue) ? props.modelValue : []
  }
  return props.modelValue ? [props.modelValue] : []
})

// 是否可以继续上传
const canUpload = computed(() => {
  if (!props.multiple) {
    return !props.modelValue
  }
  return imageList.value.length < props.limit
})

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLtSize = file.size / 1024 / 1024 < props.maxSize

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLtSize) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB`)
    return false
  }
  return true
}

// 处理上传
const handleUpload = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  if (!beforeUpload(file)) {
    event.target.value = ''
    return
  }

  uploading.value = true
  try {
    const url = await uploadImage(file)
    
    if (props.multiple) {
      const newList = [...imageList.value, url]
      emit('update:modelValue', newList)
    } else {
      emit('update:modelValue', url)
    }
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error(error.message || '上传失败')
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}

// 删除图片
const handleDelete = (index) => {
  if (props.multiple) {
    const newList = [...imageList.value]
    newList.splice(index, 1)
    emit('update:modelValue', newList)
  } else {
    emit('update:modelValue', '')
  }
}
</script>

<template>
  <div class="image-upload" :class="{ 'is-multiple': multiple }">
    <!-- 已上传图片预览 -->
    <div 
      v-for="(img, index) in imageList" 
      :key="index"
      class="image-item"
      :style="{ width, height }"
      :class="{ 'is-round': round }"
    >
      <img :src="img" alt="" />
      <div class="image-actions">
        <el-icon class="delete-icon" @click="handleDelete(index)"><Delete /></el-icon>
      </div>
    </div>

    <!-- 上传按钮 -->
    <label 
      v-if="canUpload"
      class="upload-trigger"
      :style="{ width, height }"
      :class="{ 'is-round': round, 'is-loading': uploading }"
    >
      <input 
        type="file" 
        accept="image/*" 
        @change="handleUpload"
        :disabled="uploading"
      />
      <el-icon v-if="!uploading" class="plus-icon"><Plus /></el-icon>
      <span v-else class="loading-text">上传中...</span>
    </label>
  </div>
</template>

<style scoped>
.image-upload {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.image-item {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.image-item.is-round {
  border-radius: 50%;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s;
}

.image-item:hover .image-actions {
  opacity: 1;
}

.delete-icon {
  color: white;
  font-size: 20px;
  cursor: pointer;
}

.upload-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.3s;
  background: #fafafa;
}

.upload-trigger.is-round {
  border-radius: 50%;
}

.upload-trigger:hover {
  border-color: var(--el-color-primary);
}

.upload-trigger.is-loading {
  cursor: not-allowed;
  opacity: 0.6;
}

.upload-trigger input {
  display: none;
}

.plus-icon {
  font-size: 24px;
  color: #8c939d;
}

.loading-text {
  font-size: 12px;
  color: #8c939d;
}
</style>
