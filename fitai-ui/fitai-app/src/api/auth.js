import http from '@/utils/request'

/**
 * 登录
 */
export function login(data) {
  return http.post('/auth/login', data)
}

/**
 * 注册
 */
export function register(data) {
  return http.post('/auth/register', data)
}

/**
 * 是否启用行为验证码
 */
export function getCaptchaEnabled() {
  return http.get('/auth/captcha/enabled')
}

/**
 * 发送验证码（开启行为验证时传 captchaVerification）
 */
export function sendSmsCode(phone, captchaVerification) {
  const params = { phone }
  if (captchaVerification) {
    params.captchaVerification = captchaVerification
  }
  return http.post('/auth/sms/send', {}, { params })
}

/**
 * 登出
 */
export function logout() {
  return http.post('/auth/logout')
}

/**
 * 微信登录
 */
export function wxLogin(code) {
  return http.post('/auth/wx/login', { code })
}
