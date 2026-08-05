<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, sendSmsCode, getCaptchaEnabled } from '@/api/auth'
import { User, Lock, Phone, Message } from '@element-plus/icons-vue'
import Verify from '@/components/verifition/Verify.vue'

const router = useRouter()
const loading = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)

const form = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  smsCode: '',
  nickname: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  smsCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度2-20位', trigger: 'blur' }
  ]
}

const formRef = ref(null)
const captchaRequired = ref(false)
const verifyRef = ref(null)

onMounted(async () => {
  try {
    captchaRequired.value = Boolean(await getCaptchaEnabled())
  } catch {
    captchaRequired.value = false
  }
})

function startSmsCountdown() {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

async function doSendSms(captchaVerification) {
  await sendSmsCode(form.phone, captchaVerification)
  ElMessage.success('验证码已发送')
  startSmsCountdown()
}

// 发送验证码
const handleSendCode = async () => {
  if (!form.phone || !/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }

  sendingCode.value = true
  try {
    if (captchaRequired.value) {
      verifyRef.value?.show()
      return
    }
    await doSendSms(undefined)
  } catch (error) {
    // 错误已在 request 拦截器中提示，这里不重复
  } finally {
    sendingCode.value = false
  }
}

async function onCaptchaSuccess(payload) {
  sendingCode.value = true
  try {
    await doSendSms(payload?.captchaVerification)
  } catch {
    // 已提示
  } finally {
    sendingCode.value = false
  }
}

// 注册
const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await register({
      phone: form.phone,
      password: form.password,
      smsCode: form.smsCode,
      nickname: form.nickname
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    // 错误已在 request 拦截器中提示，这里不重复
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-500 to-purple-600">
    <div class="register-card bg-white rounded-2xl shadow-2xl p-8 w-full max-w-md mx-4">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800">创建账号</h1>
        <p class="text-gray-500 mt-2">加入 FitAI，开始智能健身之旅</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="昵称"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="手机号"
            size="large"
            :prefix-icon="Phone"
          />
        </el-form-item>

        <el-form-item prop="smsCode">
          <div class="flex gap-2 w-full">
            <el-input
              v-model="form.smsCode"
              placeholder="验证码"
              size="large"
              :prefix-icon="Message"
              class="flex-1"
            />
            <el-button
              size="large"
              :disabled="countdown > 0"
              :loading="sendingCode"
              @click="handleSendCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            size="large"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-button
          type="primary"
          size="large"
          class="w-full mt-4"
          :loading="loading"
          @click="handleRegister"
        >
          注册
        </el-button>
      </el-form>

      <div class="text-center mt-6">
        <span class="text-gray-500">已有账号？</span>
        <router-link to="/login" class="text-blue-500 hover:underline ml-1">
          立即登录
        </router-link>
      </div>

      <Verify
        v-if="captchaRequired"
        ref="verifyRef"
        captcha-type="blockPuzzle"
        mode="pop"
        @success="onCaptchaSuccess"
      />
    </div>
  </div>
</template>

<style scoped>
.register-page {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 320'%3E%3Cpath fill='%23ffffff' fill-opacity='0.1' d='M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,112C672,96,768,96,864,112C960,128,1056,160,1152,160C1248,160,1344,128,1392,112L1440,96L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z'%3E%3C/path%3E%3C/svg%3E");
  background-position: bottom;
  background-repeat: no-repeat;
}
</style>
