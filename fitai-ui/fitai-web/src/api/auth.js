import request from '@/utils/request'

/**
 * 登录
 */
export function login(data) {
  return request.post('/auth/login', data)
}

/**
 * 注册
 */
export function register(data) {
  return request.post('/auth/register', data)
}

/**
 * 登出
 */
export function logout() {
  return request.post('/auth/logout')
}

/**
 * 是否启用 AJ 行为验证码（sys.captcha.enable）
 */
export function getCaptchaEnabled() {
  return request.get('/auth/captcha/enabled')
}

/**
 * 发送短信验证码（开启行为验证时须传 captchaVerification）
 */
export function sendSmsCode(phone, captchaVerification) {
  const params = { phone }
  if (captchaVerification) {
    params.captchaVerification = captchaVerification
  }
  return request.post('/auth/sms/send', null, { params })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request.get('/user/info')
}

/**
 * 修改用户信息
 */
export function updateUserInfo(data) {
  return request.put('/user', data)
}
