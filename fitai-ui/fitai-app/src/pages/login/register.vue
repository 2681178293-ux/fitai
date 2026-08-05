<template>
  <view class="page">
    <view class="form">
      <input v-model="form.phone" class="input" type="number" placeholder="手机号" maxlength="11" />
      <input v-model="form.code" class="input" type="number" placeholder="验证码" maxlength="6" />
      <input v-model="form.password" class="input" type="password" placeholder="密码" />
      <view class="btn" @click="handleRegister">注册</view>
      <view class="link" @click="goLogin">已有账号？去登录</view>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { register, sendSmsCode } from '@/api/auth'

const form = reactive({ phone: '', code: '', password: '' })

async function handleRegister() {
  if (!form.phone || !form.password) {
    uni.showToast({ title: '请填写完整', icon: 'none' })
    return
  }
  try {
    await register(form)
    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } catch (e) {
    uni.showToast({ title: e.msg || '注册失败', icon: 'none' })
  }
}

function goLogin() {
  uni.navigateBack()
}
</script>

<style scoped>
.page { padding: 40px 24px; }
.form { display: flex; flex-direction: column; gap: 16px; }
.input { height: 48px; padding: 0 16px; background: #f5f5f5; border-radius: 8px; }
.btn { height: 48px; line-height: 48px; background: #3b82f6; color: #fff; text-align: center; border-radius: 8px; }
.link { text-align: center; font-size: 14px; color: #3b82f6; margin-top: 16px; }
</style>
