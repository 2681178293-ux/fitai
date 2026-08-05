<template>
  <view class="iconify-icon">
    <Icon :icon="icon" :width="sizeNum" :height="sizeNum" :style="color ? { color } : {}" />
  </view>
</template>

<script setup>
/**
 * Iconify 图标 - 联网加载
 * 用法: <IconifyIcon icon="mdi:home" :size="24" />
 * 图标集: https://icon-sets.iconify.design/
 */
import { computed } from 'vue'
import { Icon } from '@iconify/vue'

const props = defineProps({
  icon: { type: String, required: true },
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '' }
})

const sizeNum = computed(() => Number(props.size) || 24)
</script>

<style scoped>
.iconify-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>
