<template>
  <view class="page">
    <view class="section">
      <view class="section-title">账号管理</view>
      <view class="menu-list">
        <view class="menu-item" @click="goTo('/pages/my/profile')">
          <text class="item-text">个人资料</text>
          <IconifyIcon icon="mdi:chevron-right" :size="20" color="#999" />
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">通用设置</view>
      <view class="menu-list">
        <view class="menu-item">
          <text class="item-text">消息通知</text>
          <switch :checked="notifyEnabled" @change="onNotifyChange" color="#3b82f6" />
        </view>
        <view class="menu-item" @click="handleClearCache">
          <text class="item-text">清除缓存</text>
          <text class="item-extra">{{ cacheSize }}</text>
          <IconifyIcon icon="mdi:chevron-right" :size="20" color="#999" />
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">其他</view>
      <view class="menu-list">
        <view class="menu-item" @click="goTo('/pages/my/address')">
          <text class="item-text">收货地址</text>
          <IconifyIcon icon="mdi:chevron-right" :size="20" color="#999" />
        </view>
        <view class="menu-item" @click="showAbout">
          <text class="item-text">关于我们</text>
          <text class="item-extra">v1.0.0</text>
          <IconifyIcon icon="mdi:chevron-right" :size="20" color="#999" />
        </view>
      </view>
    </view>

    <view class="section" v-if="isLoggedIn">
      <view class="menu-list">
        <view class="menu-item logout" @click="handleLogout">
          <text class="item-text">退出登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { STORAGE_KEYS_SKIP_ON_CLEAR_CACHE } from '@/utils/authStorage'

const userStore = useUserStore()
const keepStorageKeys = new Set(STORAGE_KEYS_SKIP_ON_CLEAR_CACHE)
const isLoggedIn = computed(() => userStore.isLoggedIn)

const notifyEnabled = ref(true)
const cacheSize = ref('0KB')

onMounted(() => {
  notifyEnabled.value = uni.getStorageSync('notifyEnabled') !== false
  calcCacheSize()
})

function calcCacheSize() {
  try {
    const info = uni.getStorageInfoSync()
    const size = info.currentSize || 0
    cacheSize.value = size < 1024 ? size + 'KB' : (size / 1024).toFixed(2) + 'MB'
  } catch (e) {
    cacheSize.value = '0KB'
  }
}

function onNotifyChange(e) {
  notifyEnabled.value = e.detail.value
  uni.setStorageSync('notifyEnabled', notifyEnabled.value)
  uni.showToast({ title: notifyEnabled.value ? '已开启' : '已关闭', icon: 'none' })
}

function handleClearCache() {
  uni.showModal({
    title: '清除缓存',
    content: '确定清除本地缓存吗？',
    success: (res) => {
      if (res.confirm) {
        try {
          const keys = uni.getStorageInfoSync().keys || []
          keys.forEach(k => {
            if (!keepStorageKeys.has(k)) {
              uni.removeStorageSync(k)
            }
          })
          calcCacheSize()
          uni.showToast({ title: '已清除' })
        } catch (e) {
          uni.showToast({ title: '清除失败', icon: 'none' })
        }
      }
    }
  })
}

function showAbout() {
  uni.showModal({
    title: '关于 FitAI',
    content: 'FitAI 智能健身平台\n\n版本：1.0.0\n\n为您提供专业的健身课程、AI 训练计划与健康管理服务。',
    showCancel: false
  })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.showToast({ title: '已退出', icon: 'success' })
        uni.reLaunch({ url: '/pages/index/index' })
      }
    }
  })
}

function goTo(url) {
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 16px;
}

.section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 14px;
  color: #999;
  padding: 12px 16px 8px;
}

.menu-list {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;

  &:last-child {
    border-bottom: none;
  }

  &.logout {
    justify-content: center;
    color: #ef4444;
  }
}

.item-text {
  flex: 1;
  font-size: 16px;
  color: #333;
}

.item-extra {
  font-size: 14px;
  color: #999;
  margin-right: 8px;
}
</style>
