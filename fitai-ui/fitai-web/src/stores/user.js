import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref('')
  const userInfo = ref(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isVip = computed(() => userInfo.value?.vipLevel > 0)
  const avatar = computed(() => userInfo.value?.avatar || '/default-avatar.png')
  const nickname = computed(() => userInfo.value?.nickname || '用户')

  // 登录
  async function doLogin(data) {
    const res = await login(data)
    token.value = res.token
    userInfo.value = res.userInfo
    return res
  }

  // 登出
  async function doLogout() {
    try {
      await logout()
    } catch (e) {
      // 忽略错误
    }
    clearUserInfo()
  }

  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getUserInfo()
      userInfo.value = res
    } catch (e) {
      clearUserInfo()
    }
  }

  // 清除用户信息
  function clearUserInfo() {
    token.value = ''
    userInfo.value = null
  }

  // 检查权限
  function hasPermission(permission) {
    // 简化处理，实际应从用户权限列表判断
    return isLoggedIn.value
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isVip,
    avatar,
    nickname,
    doLogin,
    doLogout,
    fetchUserInfo,
    clearUserInfo,
    hasPermission
  }
}, {
  persist: {
    key: 'fitai-user',
    paths: ['token', 'userInfo']
  }
})
