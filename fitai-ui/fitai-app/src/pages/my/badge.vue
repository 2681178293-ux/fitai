<template>
  <view class="page">
    <view v-if="loading" class="hint">加载中...</view>
    <view v-else class="grid">
      <view
        v-for="b in badges"
        :key="String(b.id)"
        class="cell"
        :class="{ locked: !b.unlocked }"
      >
        <image :src="b.icon || '/static/default-avatar.png'" class="icon" mode="aspectFill" />
        <text class="name">{{ b.name }}</text>
        <text class="desc">{{ b.description }}</text>
        <view v-if="b.unlocked" class="unlock-row">
          <text class="unlock-time">{{ b.unlockTime ? String(b.unlockTime).slice(0, 10) : '已解锁' }}</text>
          <switch
            v-if="b.unlocked"
            :checked="!!b.isDisplayed"
            @change="(e) => onDisplayChange(b, e.detail.value)"
          />
        </view>
        <text v-else class="lock-tip">未解锁</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyBadges, setBadgeDisplay } from '@/api/badge'
import { getToken } from '@/utils/authStorage'

const loading = ref(true)
const badges = ref([])

async function load() {
  loading.value = true
  try {
    const res = await getMyBadges()
    badges.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    badges.value = []
  } finally {
    loading.value = false
  }
}

async function onDisplayChange(b, display) {
  if (!b.unlocked) return
  try {
    await setBadgeDisplay(b.id, display)
    b.isDisplayed = display
    uni.showToast({ title: display ? '已展示' : '已隐藏', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
    await load()
  }
}

onMounted(async () => {
  if (!getToken()) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  await load()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: 12px;
  background: #f5f5f5;
}
.hint {
  text-align: center;
  padding: 48px;
  color: #999;
}
.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.cell {
  width: calc(50% - 5px);
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  box-sizing: border-box;
}
.cell.locked {
  opacity: 0.55;
}
.icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: block;
  margin: 0 auto 8px;
}
.name {
  display: block;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #111;
}
.desc {
  display: block;
  text-align: center;
  font-size: 11px;
  color: #6b7280;
  margin-top: 4px;
  line-height: 1.4;
  min-height: 32px;
}
.unlock-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}
.unlock-time {
  font-size: 11px;
  color: #9ca3af;
}
.lock-tip {
  display: block;
  text-align: center;
  margin-top: 8px;
  font-size: 12px;
  color: #9ca3af;
}
</style>
