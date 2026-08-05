<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { sendSmsCode, getCaptchaEnabled } from '@/api/auth'
import Verify from '@/components/verifition/Verify.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

/** 密码登录：滑块通过后得到的二次校验串，提交登录时带给后端 */
const passwordCaptchaToken = ref('')

// 登录类型
const loginType = ref('password') // password | sms

// 表单数据
const formData = reactive({
  phone: '',
  password: '',
  code: ''
})

// 表单校验规则
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

// 发送验证码（可选 AJ 行为验证）
const countdown = ref(0)
const canSendCode = computed(() => countdown.value === 0 && /^1[3-9]\d{9}$/.test(formData.phone))
const captchaRequired = ref(false)
/** 短信弹窗验证（pop） */
const verifySmsRef = ref(null)

/** 与 el-input 同宽：max-w-md 卡片内可用宽度（约 448 - 左右 p-8） */
const captchaPanelWidthPx = computed(() => {
  if (typeof window === 'undefined') return 384
  const cardMaxPx = 448
  const horizontalPadding = 64
  const effectiveCard = Math.min(cardMaxPx, window.innerWidth - 32)
  return Math.max(280, Math.floor(effectiveCard - horizontalPadding))
})

const captchaImgSize = computed(() => {
  const w = captchaPanelWidthPx.value
  const h = Math.round(w * 0.5)
  return { width: `${w}px`, height: `${h}px` }
})

const captchaBarSize = computed(() => {
  const w = captchaPanelWidthPx.value
  return { width: `${w}px`, height: '40px' }
})

const captchaBlockSize = computed(() => {
  const w = captchaPanelWidthPx.value
  const side = Math.round(50 * (w / 310))
  return { width: `${side}px`, height: `${side}px` }
})

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
  () => formData.password,
  () => {
    passwordCaptchaToken.value = ''
  }
)

function startSmsCountdown() {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value === 0) {
      clearInterval(timer)
    }
  }, 1000)
}

async function doSendSms(captchaVerification) {
  await sendSmsCode(formData.phone, captchaVerification)
  ElMessage.success('验证码已发送')
  startSmsCountdown()
}

async function handleSendCode() {
  if (!canSendCode.value) return
  try {
    if (captchaRequired.value) {
      verifySmsRef.value?.show()
      return
    }
    await doSendSms(undefined)
  } catch (e) {
    // 错误已在 request 中处理
  }
}

async function onCaptchaSuccess(payload) {
  const token = payload?.captchaVerification
  if (loginType.value === 'password') {
    passwordCaptchaToken.value = token || ''
    return
  }
  try {
    await doSendSms(token)
  } catch (e) {
    // request 已提示
  }
}

// 登录
const loading = ref(false)
const formRef = ref()

async function handleLogin() {
  await formRef.value.validate()

  if (captchaRequired.value && loginType.value === 'password') {
    if (!passwordCaptchaToken.value) {
      ElMessage.warning('请先拖动滑块完成安全验证')
      return
    }
  }

  loading.value = true
  try {
    const payload = {
      phone: formData.phone,
      password: loginType.value === 'password' ? formData.password : undefined,
      code: loginType.value === 'sms' ? formData.code : undefined,
      loginType: loginType.value
    }
    if (captchaRequired.value && loginType.value === 'password' && passwordCaptchaToken.value) {
      payload.captchaVerification = passwordCaptchaToken.value
    }
    await userStore.doLogin(payload)
    ElMessage.success('登录成功')
    passwordCaptchaToken.value = ''

    // 跳转到来源页或首页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } finally {
    loading.value = false
  }
}

// 切换登录类型
function switchLoginType(type) {
  loginType.value = type
  formData.password = ''
  formData.code = ''
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-emerald-400 to-cyan-500 p-4">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-xl p-8">
      <!-- Logo -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-emerald-500">FitAI</h1>
        <p class="text-gray-500 mt-2">智能健身，科学训练</p>
      </div>
      
      <!-- 登录类型切换 -->
      <div class="flex mb-6 bg-gray-100 rounded-lg p-1">
        <button
          @click="switchLoginType('password')"
          class="flex-1 py-2 text-sm font-medium rounded-md transition-colors"
          :class="loginType === 'password' ? 'bg-white text-emerald-600 shadow-sm' : 'text-gray-500'"
        >
          密码登录
        </button>
        <button
          @click="switchLoginType('sms')"
          class="flex-1 py-2 text-sm font-medium rounded-md transition-colors"
          :class="loginType === 'sms' ? 'bg-white text-emerald-600 shadow-sm' : 'text-gray-500'"
        >
          验证码登录
        </button>
      </div>
      
      <!-- 登录表单 -->
      <el-form ref="formRef" :model="formData" :rules="rules" label-position="top">
        <el-form-item prop="phone">
          <el-input
            v-model="formData.phone"
            placeholder="请输入手机号"
            size="large"
            maxlength="11"
          >
            <template #prefix>
              <span class="text-gray-400">+86</span>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item v-if="loginType === 'password'" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item v-if="captchaRequired && loginType === 'password'" label="安全验证" class="login-captcha-form-item">
          <p class="text-xs text-gray-500 mb-3 mt-0.5">请先完成图形验证后再点击登录</p>
          <div class="login-captcha-wrap">
            <Verify
              ref="verifyPwdRef"
              captcha-type="blockPuzzle"
              mode="fixed"
              :img-size="captchaImgSize"
              :bar-size="captchaBarSize"
              :block-size="captchaBlockSize"
              :v-space="12"
              @success="onCaptchaSuccess"
            />
          </div>
        </el-form-item>

        <el-form-item v-else-if="loginType === 'sms'" prop="code">
          <div class="flex gap-2 w-full">
            <el-input
              v-model="formData.code"
              placeholder="请输入验证码"
              size="large"
              maxlength="6"
              class="flex-1"
              @keyup.enter="handleLogin"
            />
            <el-button
              size="large"
              :disabled="!canSendCode"
              @click="handleSendCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-button
          type="primary"
          size="large"
          class="w-full mt-4"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </el-button>
      </el-form>
      
      <!-- 其他操作 -->
      <div class="mt-6 text-center text-sm text-gray-500">
        <span>还没有账号？</span>
        <router-link to="/register" class="text-emerald-500 hover:underline">立即注册</router-link>
      </div>
      
      <!-- 协议 -->
      <p class="mt-8 text-center text-xs text-gray-400">
        登录即表示同意
        <a href="#" class="text-emerald-500">《用户协议》</a>
        和
        <a href="#" class="text-emerald-500">《隐私政策》</a>
      </p>

      <Verify
        v-if="captchaRequired && loginType === 'sms'"
        ref="verifySmsRef"
        captcha-type="blockPuzzle"
        mode="pop"
        :img-size="captchaImgSize"
        :bar-size="captchaBarSize"
        :block-size="captchaBlockSize"
        :v-space="12"
        @success="onCaptchaSuccess"
      />
    </div>
  </div>
</template>

<style scoped>
.login-captcha-form-item :deep(.el-form-item__content) {
  display: block;
  width: 100%;
}

.login-captcha-wrap {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.login-captcha-wrap :deep(> div) {
  width: 100% !important;
  max-width: 100%;
  box-sizing: border-box;
}

.login-captcha-wrap :deep(.verify-img-out),
.login-captcha-wrap :deep(.verify-bar-area) {
  max-width: 100%;
}
</style>
