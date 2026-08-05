/**
 * 用户端 token 与用户信息与 PC/管理端隔离，避免同机 H5 调试时串写 localStorage
 */
const TOKEN_KEY = 'fitai_app_token'
const USER_KEY = 'fitai_app_userInfo'
const LEGACY_TOKEN = 'token'
const LEGACY_USER = 'userInfo'

function migrateLegacyToken() {
  const legacy = uni.getStorageSync(LEGACY_TOKEN)
  if (legacy && !uni.getStorageSync(TOKEN_KEY)) {
    uni.setStorageSync(TOKEN_KEY, legacy)
    uni.removeStorageSync(LEGACY_TOKEN)
  }
}

function migrateLegacyUser() {
  const legacy = uni.getStorageSync(LEGACY_USER)
  if (legacy && !uni.getStorageSync(USER_KEY)) {
    uni.setStorageSync(USER_KEY, legacy)
    uni.removeStorageSync(LEGACY_USER)
  }
}

export function getToken() {
  migrateLegacyToken()
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function getUserInfo() {
  migrateLegacyUser()
  return uni.getStorageSync(USER_KEY) || null
}

export function setUserInfo(info) {
  uni.setStorageSync(USER_KEY, info)
}

export function removeAuth() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(USER_KEY)
  uni.removeStorageSync(LEGACY_TOKEN)
  uni.removeStorageSync(LEGACY_USER)
}

export const STORAGE_KEYS_SKIP_ON_CLEAR_CACHE = [TOKEN_KEY, USER_KEY, LEGACY_TOKEN, LEGACY_USER, 'notifyEnabled']
