/**
 * AJ-Captcha 专用：直连后端 /captcha/*，返回 ResponseModel（repCode），不走业务 AjaxResult 封装
 */
import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_BASE || '/api/pc'

const captchaHttp = axios.create({
  baseURL: API_BASE,
  timeout: 40000,
  headers: {
    'X-Requested-With': 'XMLHttpRequest',
    'Content-Type': 'application/json; charset=UTF-8'
  }
})

captchaHttp.interceptors.response.use(
  (response) => response.data,
  (error) => Promise.reject(error)
)

export default function request(config) {
  return captchaHttp(config)
}
