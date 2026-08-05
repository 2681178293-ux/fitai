import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, getUserInfo, setUserInfo, removeAuth } from '@/utils/authStorage'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(getToken() || '')
  const userInfo = ref(getUserInfo() || null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isVip = computed(() => {
    if (!userInfo.value) return false
    return userInfo.value.vipLevel > 0 && new Date(userInfo.value.vipExpireTime) > new Date()
  })

  // 方法
  function setTokenAction(newToken) {
    token.value = newToken
    setToken(newToken)
  }

  function setUserInfoAction(info) {
    userInfo.value = info
    setUserInfo(info)
  }

  function checkLogin() {
    const storedToken = getToken()
    if (storedToken) {
      token.value = storedToken
      userInfo.value = getUserInfo()
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    removeAuth()
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isVip,
    setToken: setTokenAction,
    setUserInfo: setUserInfoAction,
    checkLogin,
    logout
  }
})
