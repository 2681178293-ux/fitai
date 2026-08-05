<template>
  <view class="el-icon-wrap" :style="{ width: sizeNum + 'px', height: sizeNum + 'px', fontSize: sizeNum + 'px' }">
    <component :is="iconComp" v-if="iconComp" />
  </view>
</template>

<script setup>
/**
 * Element Plus 图标组件
 * 用法: <ElIcon name="House" :size="24" />
 * 图标: House, Shop, ShoppingCart, User, UserFilled, ChatDotRound, Reading 等
 * 完整列表: https://element-plus.org/zh-CN/component/icon.html
 */
import { computed } from 'vue'
import * as EpIcons from '@element-plus/icons-vue'

const iconMap = EpIcons

const props = defineProps({
  name: { type: String, required: true },
  size: { type: [Number, String], default: 24 }
})

const sizeNum = computed(() => Number(props.size) || 24)
const iconComp = computed(() => iconMap[props.name] || null)
</script>

<style scoped>
.el-icon-wrap {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: inherit;
}
.el-icon-wrap :deep(svg) {
  width: 100%;
  height: 100%;
}
</style>
