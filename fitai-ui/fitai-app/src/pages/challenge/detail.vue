<template>
  <view class="page">
    <view v-if="detail">
      <text class="title">{{ detail.name }}</text>
      <text class="desc">{{ detail.description }}</text>
      <view class="btn" @click="handleJoin">参与挑战</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getChallengeDetail, joinChallenge } from '@/api/challenge'
import { useUserStore } from '@/store/user'

const detail = ref(null)
const userStore = useUserStore()

onMounted(async () => {
  const pages = getCurrentPages()
  const opts = pages[pages.length - 1]?.options || {}
  const id = opts.id ? String(opts.id).trim() : ''
  if (!id) return
  try {
    const res = await getChallengeDetail(id)
    detail.value = res.data
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  }
})

async function handleJoin() {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  try {
    await joinChallenge(detail.value.id)
    uni.showToast({ title: '参与成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: e.msg || '参与失败', icon: 'none' })
  }
}
</script>

<style scoped>
.page { padding: 16px; }
.title { font-size: 18px; font-weight: bold; }
.desc { display: block; margin-top: 8px; color: #666; }
.btn { margin-top: 24px; padding: 12px; background: #3b82f6; color: #fff; text-align: center; border-radius: 8px; }
</style>
