<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { reviewTraining } from '@/api/ai'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  trainingLogId: { type: [Number, String], default: null }
})

const emit = defineEmits(['update:modelValue'])

const loading = ref(false)
const reviewPayload = ref(null)

function asList(value) {
  if (Array.isArray(value)) {
    return value.map((x) => (typeof x === 'string' ? x : JSON.stringify(x)))
  }
  if (value == null || value === '') {
    return []
  }
  return [String(value)]
}

function closeDialog() {
  emit('update:modelValue', false)
}

watch(
  () => [props.modelValue, props.trainingLogId],
  async ([open, logId]) => {
    if (!open || logId == null || logId === '') {
      if (!open) {
        reviewPayload.value = null
      }
      return
    }
    loading.value = true
    reviewPayload.value = null
    try {
      // 训练记录 id 为 Snowflake Long，后端 Jackson 已序列化为字符串；切勿 Number()，否则超过 MAX_SAFE_INTEGER 会失真，导致接口报「训练记录不存在」
      const data = await reviewTraining({
        trainingLogId: logId,
        includeHistory: true
      })
      reviewPayload.value = data && typeof data === 'object' ? data : { summary: String(data ?? '') }
    } catch (err) {
      ElMessage.error(err?.message || 'AI 点评失败')
      closeDialog()
    } finally {
      loading.value = false
    }
  },
  { flush: 'post' }
)
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    title="AI 训练点评"
    width="560px"
    destroy-on-close
    @update:model-value="emit('update:modelValue', $event)"
  >
    <div v-loading="loading" class="min-h-[120px]">
      <template v-if="reviewPayload">
        <div v-if="reviewPayload.overallScore != null" class="mb-3 flex items-center gap-2">
          <span class="text-gray-600">综合评分</span>
          <el-tag type="warning" size="large">{{ reviewPayload.overallScore }}</el-tag>
        </div>
        <p v-if="reviewPayload.summary" class="text-gray-800 mb-4 leading-relaxed">{{ reviewPayload.summary }}</p>
        <div v-if="asList(reviewPayload.highlights).length" class="mb-3">
          <div class="text-sm font-medium text-green-700 mb-1">亮点</div>
          <ul class="list-disc pl-5 text-sm text-gray-700 space-y-1">
            <li v-for="(line, i) in asList(reviewPayload.highlights)" :key="'h' + i">{{ line }}</li>
          </ul>
        </div>
        <div v-if="asList(reviewPayload.improvements).length" class="mb-3">
          <div class="text-sm font-medium text-amber-700 mb-1">可改进</div>
          <ul class="list-disc pl-5 text-sm text-gray-700 space-y-1">
            <li v-for="(line, i) in asList(reviewPayload.improvements)" :key="'i' + i">{{ line }}</li>
          </ul>
        </div>
        <div v-if="asList(reviewPayload.nextSuggestions).length" class="mb-3">
          <div class="text-sm font-medium text-blue-700 mb-1">下次建议</div>
          <ul class="list-disc pl-5 text-sm text-gray-700 space-y-1">
            <li v-for="(line, i) in asList(reviewPayload.nextSuggestions)" :key="'n' + i">{{ line }}</li>
          </ul>
        </div>
        <p
          v-if="reviewPayload.encouragement"
          class="text-sm text-violet-700 bg-violet-50 rounded-lg p-3 mt-2"
        >
          {{ reviewPayload.encouragement }}
        </p>
      </template>
      <el-empty v-else-if="!loading" description="暂无点评内容" />
    </div>
    <template #footer>
      <el-button type="primary" @click="closeDialog">关闭</el-button>
    </template>
  </el-dialog>
</template>
