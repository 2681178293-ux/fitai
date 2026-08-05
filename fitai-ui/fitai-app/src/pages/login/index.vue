<template>
  <view class="login-page">
    <view class="login-header">
      <image src="/static/logo.png" class="logo" mode="aspectFit" />
      <text class="title">FitAI 智能健身</text>
      <text class="subtitle">您的私人健身教练</text>
    </view>

    <view class="login-form">
      <!-- 登录类型切换 -->
      <view class="login-type-tabs">
        <view 
          class="tab-item" 
          :class="{ active: loginType === 'password' }"
          @click="loginType = 'password'"
        >
          密码登录
        </view>
        <view 
          class="tab-item" 
          :class="{ active: loginType === 'sms' }"
          @click="loginType = 'sms'"
        >
          验证码登录
        </view>
      </view>

      <!-- 手机号 -->
      <view class="form-item">
        <text class="label">手机号</text>
        <input 
          v-model="form.phone" 
          type="number" 
          maxlength="11"
          placeholder="请输入手机号" 
          class="input"
        />
      </view>

      <view class="form-item" v-if="loginType === 'sms'">
        <text class="label">验证码</text>
        <view class="input-group">
          <input 
            v-model="form.smsCode" 
            type="number" 
            maxlength="6"
            placeholder="请输入验证码" 
            class="input flex-1"
          />
          <button 
            class="sms-btn" 
            :disabled="countdown > 0"
            @click="sendCode"
          >
            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
          </button>
        </view>
      </view>

      <view class="form-item" v-if="loginType === 'password'">
        <text class="label">密码</text>
        <input 
          v-model="form.password" 
          type="password" 
          placeholder="请输入密码" 
          class="input"
        />
      </view>

      <!-- #ifdef H5 -->
      <view v-if="captchaRequired && loginType === 'password'" class="form-item captcha-block">
        <text class="label">安全验证</text>
        <text class="captcha-hint">请完成拼图后再点击登录</text>
        <Verify
          ref="verifyPwdRef"
          captcha-type="blockPuzzle"
          mode="fixed"
          @success="onCaptchaSuccess"
        />
      </view>
      <!-- #endif -->

      <button class="login-btn" :loading="loading" @click="handleLogin">
        登录
      </button>

      <!-- 微信一键登录 -->
      <!-- #ifdef MP-WEIXIN -->
      <button class="wx-login-btn" open-type="getPhoneNumber" @getphonenumber="wxLogin">
        <text class="iconfont icon-wechat"></text>
        微信一键登录
      </button>
      <!-- #endif -->

      <view class="register-link">
        还没有账号？<text class="link" @click="goRegister">立即注册</text>
      </view>
    </view>

    <view class="agreement">
      <checkbox :checked="agreed" @click="agreed = !agreed" />
      <text class="agreement-text">
        我已阅读并同意
        <text class="link" @click="showAgreement('user')">《用户协议》</text>
        和
        <text class="link" @click="showAgreement('privacy')">《隐私政策》</text>
      </text>
    </view>

    <!-- #ifdef H5 -->
    <Verify
      v-if="captchaRequired && loginType === 'sms'"
      ref="verifySmsRef"
      captcha-type="blockPuzzle"
      mode="pop"
      @success="onCaptchaSuccess"
    />
    <!-- #endif -->
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useUserStore } from '@/store/user'
import { login, sendSmsCode, getCaptchaEnabled } from '@/api/auth'
// #ifdef H5
import Verify from '@/components/verifition/Verify.vue'
// #endif

const userStore = useUserStore()

const loginType = ref('password') // password | sms
const loading = ref(false)
const countdown = ref(0)
const agreed = ref(false)

const form = ref({
  phone: '',
  smsCode: '',
  password: ''
})

const captchaRequired = ref(false)
/** 密码登录：滑块通过后传给后端的 captchaVerification */
const passwordCaptchaToken = ref('')
// #ifdef H5
const verifyPwdRef = ref(null)
const verifySmsRef = ref(null)
// #endif

onMounted(async () => {
  try {
    captchaRequired.value = Boolean(await getCaptchaEnabled())
  } catch {
    captchaRequired.value = false
  }
})

watch(loginType, () => {
  passwordCaptchaToken.value = ''
})

watch(
  () => form.value.password,
  () => {
    passwordCaptchaToken.value = ''
  }
)

function startCountdown() {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

async function doSendSms(captchaVerification) {
  await sendSmsCode(form.value.phone, captchaVerification)
  uni.showToast({ title: '验证码已发送', icon: 'success' })
  startCountdown()
}

// #ifdef H5
async function onCaptchaSuccess(payload) {
  const token = payload?.captchaVerification
  if (loginType.value === 'password') {
    passwordCaptchaToken.value = token || ''
    return
  }
  try {
    await doSendSms(token)
  } catch (e) {
    console.error('发送验证码失败', e)
  }
}
// #endif

async function sendCode() {
  if (!form.value.phone || !/^1[3-9]\d{9}$/.test(form.value.phone)) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }

  if (captchaRequired.value) {
    // #ifdef H5
    verifySmsRef.value?.show()
    return
    // #endif
    // #ifndef H5
    uni.showToast({
      title: '行为验证码需在 H5 环境完成，请在管理端关闭 sys.captcha.enable 或使用浏览器访问',
      icon: 'none',
      duration: 3500
    })
    return
    // #endif
  }

  try {
    await doSendSms(undefined)
  } catch (e) {
    console.error('发送验证码失败', e)
  }
}

async function handleLogin() {
  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议', icon: 'none' })
    return
  }

  if (!form.value.phone || !/^1[3-9]\d{9}$/.test(form.value.phone)) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }

  if (loginType.value === 'sms' && !form.value.smsCode) {
    uni.showToast({ title: '请输入验证码', icon: 'none' })
    return
  }

  if (loginType.value === 'password' && !form.value.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }

  if (captchaRequired.value && loginType.value === 'password') {
    // #ifdef H5
    if (!passwordCaptchaToken.value) {
      uni.showToast({ title: '请先完成安全验证', icon: 'none' })
      return
    }
    // #endif
    // #ifndef H5
    uni.showToast({
      title: '行为验证码需在 H5 环境完成，请在管理端关闭 sys.captcha.enable 或使用浏览器访问',
      icon: 'none',
      duration: 3500
    })
    return
    // #endif
  }

  loading.value = true
  try {
    const data = {
      phone: form.value.phone,
      loginType: loginType.value, // password | sms，后端必填
      ...(loginType.value === 'sms' ? { code: form.value.smsCode } : { password: form.value.password })
    }
    // #ifdef H5
    if (captchaRequired.value && loginType.value === 'password' && passwordCaptchaToken.value) {
      data.captchaVerification = passwordCaptchaToken.value
    }
    // #endif

    const res = await login(data)

    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.userInfo ?? null)

    passwordCaptchaToken.value = ''
    uni.showToast({ title: '登录成功', icon: 'success', duration: 1200 })

    // 统一 reLaunch 到首页 tab，避免 navigateBack 回到注册页等中间页导致「像没跳转」
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 400)
  } catch (e) {
    console.error('登录失败', e)
  } finally {
    loading.value = false
  }
}

function wxLogin(e) {
  if (e.detail.errMsg === 'getPhoneNumber:ok') {
    uni.showToast({ title: '后端未配置微信登录，请用手机号登录', icon: 'none', duration: 2500 })
    return
  }
  uni.showToast({ title: '需要授权手机号', icon: 'none' })
}

function goRegister() {
  uni.navigateTo({ url: '/pages/login/register' })
}

function showAgreement(type) {
  // 显示协议
  console.log('显示协议', type)
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  padding: 60px 24px 24px;
  background: linear-gradient(180deg, #f0f9ff 0%, #ffffff 100%);
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 48px;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
}

.login-form {
  background-color: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.login-type-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.tab-item {
  flex: 1;
  height: 44px;
  line-height: 44px;
  text-align: center;
  font-size: 15px;
  color: #6b7280;
  background-color: #f3f4f6;
  border-radius: 8px;
  transition: all 0.2s;
}

.tab-item.active {
  color: #fff;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  font-weight: 500;
}

.form-item {
  margin-bottom: 20px;
}

.captcha-block {
  overflow: hidden;
}

.captcha-hint {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 8px;
}

.label {
  display: block;
  font-size: 14px;
  color: #374151;
  margin-bottom: 8px;
}

.input {
  width: 100%;
  height: 48px;
  padding: 0 16px;
  background-color: #f3f4f6;
  border-radius: 8px;
  font-size: 16px;
}

.input-group {
  display: flex;
  gap: 12px;
}

.flex-1 {
  flex: 1;
}

.sms-btn {
  min-width: 100px;
  height: 48px;
  padding: 0 16px;
  background-color: #3b82f6;
  color: #fff;
  font-size: 14px;
  border-radius: 8px;
  border: none;
}

.sms-btn[disabled] {
  background-color: #9ca3af;
}

.login-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-radius: 8px;
  border: none;
}

.wx-login-btn {
  width: 100%;
  height: 48px;
  margin-top: 16px;
  background-color: #07c160;
  color: #fff;
  font-size: 16px;
  border-radius: 8px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #6b7280;
}

.link {
  color: #3b82f6;
}

.agreement {
  display: flex;
  align-items: flex-start;
  margin-top: 24px;
  padding: 0 12px;
}

.agreement-text {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.5;
  margin-left: 8px;
}
</style>
