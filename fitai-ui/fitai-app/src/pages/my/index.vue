<template>
  <view class="my-page">
    <!-- 顶部背景 -->
    <view class="header-bg" :style="{ paddingTop: statusBarHeight + 'px' }">
      <!-- 用户信息 -->
      <view class="user-info" @click="goToProfile">
        <image :src="userInfo?.avatar || '/static/default-avatar.png'" class="avatar" mode="aspectFill" />
        <view class="user-detail" v-if="isLoggedIn">
          <text class="nickname">{{ userInfo?.nickname || '健身达人' }}</text>
          <view class="vip-tag" v-if="isVip">
            <text class="vip-text">VIP会员</text>
          </view>
        </view>
        <view class="user-detail" v-else>
          <text class="nickname">点击登录</text>
          <text class="desc">登录享受更多权益</text>
        </view>
        <text class="iconfont icon-arrow-right"></text>
      </view>

      <!-- 数据统计 -->
      <view class="stats" v-if="isLoggedIn">
        <view class="stat-item" @click="goTo('/pages/my/points')">
          <text class="stat-value">{{ points }}</text>
          <text class="stat-label">积分</text>
        </view>
        <view class="stat-item" @click="goTo('/pages/my/coupon')">
          <text class="stat-value">{{ couponCount }}</text>
          <text class="stat-label">优惠券</text>
        </view>
        <view class="stat-item" @click="goTo('/pages/my/badge')">
          <text class="stat-value">{{ badgeCount }}</text>
          <text class="stat-label">徽章</text>
        </view>
        <view class="stat-item" @click="goTo('/pages/order/list')">
          <text class="stat-value">{{ orderCount }}</text>
          <text class="stat-label">订单</text>
        </view>
      </view>
    </view>

    <!-- 会员卡片 -->
    <view class="member-card" @click="goTo('/pages/my/member')" v-if="isLoggedIn">
      <view class="member-info">
        <text class="member-title">{{ isVip ? 'VIP会员' : '开通会员' }}</text>
        <text class="member-desc">{{ isVip ? '尊享全部权益' : '专享课程9折优惠' }}</text>
      </view>
      <button class="member-btn">{{ isVip ? '续费' : '立即开通' }}</button>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-group">
        <view class="menu-item" @click="goTo('/pages/my/checkin')">
          <text class="iconfont icon-checkin menu-icon"></text>
          <text class="menu-text">每日签到</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/training/plan')">
          <text class="iconfont icon-plan menu-icon"></text>
          <text class="menu-text">我的训练</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/training/history')">
          <text class="iconfont icon-history menu-icon"></text>
          <text class="menu-text">训练记录</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/training/exercise-list')">
          <text class="iconfont icon-plan menu-icon"></text>
          <text class="menu-text">动作库</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/course/history')">
          <text class="iconfont icon-history menu-icon"></text>
          <text class="menu-text">学习记录</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/coach/index')">
          <text class="iconfont icon-plan menu-icon"></text>
          <text class="menu-text">私教教练</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/coach/my-booking')">
          <text class="iconfont icon-history menu-icon"></text>
          <text class="menu-text">我的预约</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/my/favorite')">
          <view class="menu-icon-wrap"><IconifyIcon icon="mdi:heart" :size="20" color="#3b82f6" /></view>
          <text class="menu-text">我的收藏</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/community/index')">
          <text class="iconfont icon-message menu-icon"></text>
          <text class="menu-text">社区</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/diet/index')">
          <text class="iconfont icon-setting menu-icon"></text>
          <text class="menu-text">饮食记录</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/challenge/index')">
          <text class="iconfont icon-checkin menu-icon"></text>
          <text class="menu-text">挑战赛</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/ranking/index')">
          <text class="iconfont icon-plan menu-icon"></text>
          <text class="menu-text">排行榜</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-item" @click="goTo('/pages/my/address')">
          <text class="iconfont icon-address menu-icon"></text>
          <text class="menu-text">收货地址</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/my/message')">
          <text class="iconfont icon-message menu-icon"></text>
          <text class="menu-text">消息中心</text>
          <view class="badge" v-if="unreadCount > 0">{{ unreadCount }}</view>
          <text class="iconfont icon-arrow-right"></text>
        </view>
        <view class="menu-item" @click="goTo('/pages/my/settings')">
          <text class="iconfont icon-setting menu-icon"></text>
          <text class="menu-text">设置</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <button class="logout-btn" v-if="isLoggedIn" @click="handleLogout">
      退出登录
    </button>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import http from '@/utils/request'
import { getOrderList } from '@/api/order'
import { getMySeckillOrders } from '@/api/seckill'

/** 未登录可访问（与 PC meta.noAuth 对齐的常见浏览页） */
function isGuestAllowed(url) {
  if (!url) return false
  const exact = new Set([
    '/pages/my/settings',
    '/pages/login/index',
    '/pages/community/index',
    '/pages/challenge/index',
    '/pages/ranking/index',
    '/pages/course/index',
    '/pages/shop/index',
    '/pages/seckill/index',
    '/pages/training/exercise-list',
    '/pages/coach/index'
  ])
  if (exact.has(url)) return true
  const prefixes = [
    '/pages/course/detail',
    '/pages/training/exercise-detail',
    '/pages/shop/detail',
    '/pages/seckill/detail',
    '/pages/challenge/detail',
    '/pages/coach/detail'
  ]
  return prefixes.some((p) => url.startsWith(p))
}

const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const isVip = computed(() => userStore.isVip)
const userInfo = computed(() => userStore.userInfo)

const statusBarHeight = ref(0)
const points = ref(0)
const couponCount = ref(0)
const badgeCount = ref(0)
const orderCount = ref(0)
const unreadCount = ref(0)

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight

  if (isLoggedIn.value) {
    loadStats()
  }
})

async function loadStats() {
  try {
    const [pointsRes, couponRes, badgeRes, msgRes, orderRes, seckillRes] = await Promise.all([
      http.get('/points/balance').catch(() => ({ data: { balance: 0 } })),
      http.get('/coupon/my', { status: 0 }).catch(() => ({ data: [] })),
      http.get('/badge/my').catch(() => ({ data: [] })),
      http.get('/message/unread-count').catch(() => ({ data: { count: 0 } })),
      getOrderList({ pageNum: 1, pageSize: 1 }).catch(() => ({ data: { total: 0 } })),
      getMySeckillOrders().catch(() => ({ data: [] }))
    ])

    points.value = pointsRes.data?.balance || 0
    couponCount.value = Array.isArray(couponRes.data) ? couponRes.data.length : 0
    badgeCount.value = Array.isArray(badgeRes.data) 
      ? badgeRes.data.filter(b => b.unlocked).length : 0
    unreadCount.value = msgRes.data?.count || 0
    const productTotal = orderRes.data?.total ?? 0
    const seckillLen = Array.isArray(seckillRes.data) ? seckillRes.data.length : 0
    orderCount.value = Number(productTotal) + seckillLen
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

function goTo(url) {
  if (!isLoggedIn.value && !isGuestAllowed(url)) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  uni.navigateTo({ url })
}

function goToProfile() {
  if (isLoggedIn.value) {
    uni.navigateTo({ url: '/pages/my/profile' })
  } else {
    uni.navigateTo({ url: '/pages/login/index' })
  }
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.my-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header-bg {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  padding-bottom: 60px;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 20px 16px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.user-detail {
  flex: 1;
  margin-left: 16px;
}

.nickname {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 4px;
}

.vip-tag {
  display: inline-flex;
  align-items: center;
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  padding: 2px 8px;
  border-radius: 10px;
  margin-top: 4px;
}

.vip-text {
  font-size: 10px;
  color: #fff;
}

.icon-arrow-right {
  color: rgba(255, 255, 255, 0.6);
}

.stats {
  display: flex;
  justify-content: space-around;
  padding: 16px;
  margin: 0 16px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 4px;
}

.member-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -40px 16px 16px;
  padding: 16px;
  background: linear-gradient(135deg, #1f2937 0%, #374151 100%);
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.member-title {
  font-size: 16px;
  font-weight: bold;
  color: #fbbf24;
}

.member-desc {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
}

.member-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: #1f2937;
  font-size: 12px;
  font-weight: bold;
  border-radius: 16px;
  border: none;
}

.menu-section {
  padding: 0 16px;
}

.menu-group {
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
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
}

.menu-icon {
  font-size: 20px;
  color: #3b82f6;
  margin-right: 12px;
}
.menu-icon-wrap {
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.menu-text {
  flex: 1;
  font-size: 15px;
  color: #333;
}

.badge {
  min-width: 18px;
  height: 18px;
  padding: 0 6px;
  background-color: #ef4444;
  color: #fff;
  font-size: 10px;
  border-radius: 9px;
  text-align: center;
  line-height: 18px;
  margin-right: 8px;
}

.logout-btn {
  margin: 24px 16px;
  height: 48px;
  background-color: #fff;
  color: #ef4444;
  font-size: 16px;
  border-radius: 8px;
  border: none;
}
</style>
