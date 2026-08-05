import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const API_BASE = import.meta.env.VITE_API_BASE || '/api/pc'

// 创建 axios 实例
const request = axios.create({
  baseURL: API_BASE,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 错误提示去重：2 秒内只展示一次
const ERROR_DEBOUNCE_MS = 2000
let lastErrorTime = 0

function showErrorOnce(message, type = 'error') {
  const now = Date.now()
  if (now - lastErrorTime < ERROR_DEBOUNCE_MS) return
  lastErrorTime = now
  if (type === 'warning') {
    ElMessage.warning(message)
  } else {
    ElMessage.error(message)
  }
}

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = userStore.token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { code, msg, data } = response.data
    
    // 成功
    if (code === 200) {
      return data
    }
    
    // 未登录或 Token 过期
    if (code === 1101 || code === 1102 || code === 1103) {
      const userStore = useUserStore()
      userStore.clearUserInfo()
      ElMessage.warning('请先登录')
      router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } })
      return Promise.reject(new Error(msg))
    }

    if (code === 1104) {
      showErrorOnce(msg || '没有权限', 'warning')
      return Promise.reject(new Error(msg))
    }

    // 其他业务错误（去重）
    showErrorOnce(msg || '请求失败')
    return Promise.reject(new Error(msg))
  },
  (error) => {
    // 网络错误（去重）
    if (error.response) {
      const { status } = error.response
      switch (status) {
        case 401:
          showErrorOnce('请先登录', 'warning')
          break
        case 403:
          showErrorOnce('没有权限')
          break
        case 404:
          showErrorOnce('接口不存在')
          break
        case 500:
          showErrorOnce('服务器错误')
          break
        default:
          showErrorOnce('请求失败')
      }
    } else if (error.code === 'ECONNABORTED') {
      showErrorOnce('请求超时')
    } else {
      showErrorOnce('网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
