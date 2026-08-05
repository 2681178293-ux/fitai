/**
 * 请求工具类
 */

import { getToken, removeAuth } from '@/utils/authStorage'

const BASE_URL = import.meta.env.VITE_API_BASE || '/api/mobile'

// 序列化 query 参数
function serializeParams(params) {
  if (!params || typeof params !== 'object') return ''
  const parts = Object.keys(params)
    .filter(k => params[k] !== undefined && params[k] !== null && params[k] !== '')
    .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(params[k]))
  return parts.length ? '?' + parts.join('&') : ''
}

// 请求拦截
function request(options) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    let url = BASE_URL + options.url
    const method = (options.method || 'GET').toUpperCase()
    const params = options.params || {}
    const data = options.data

    // GET 或 有 params 时：data 合并到 params 并拼到 URL
    const queryParams = method === 'GET' ? { ...params, ...(data || {}) } : params
    url += serializeParams(queryParams)

    // POST 请求需显式 JSON 序列化，确保后端能正确解析 loginType 等字段
    const body = method === 'GET' ? {} : (data || {})
    const config = {
      url,
      method,
      data: method === 'GET' ? {} : (typeof body === 'object' && !ArrayBuffer.isView(body) ? JSON.stringify(body) : body),
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      timeout: 30000
    }

    if (token) {
      config.header['Authorization'] = token
    }

    // 发起请求
    uni.request({
      ...config,
      success: (res) => {
        const { statusCode, data } = res

        if (statusCode === 200) {
          if (data.code === 200) {
            resolve(data)
          } else if (data.code === 1101 || data.code === 1102 || data.code === 1103) {
            removeAuth()
            uni.navigateTo({ url: '/pages/login/index' })
            reject(data)
          } else if (data.code === 1104) {
            uni.showToast({
              title: data.msg || '没有权限',
              icon: 'none'
            })
            reject(data)
          } else {
            uni.showToast({
              title: data.msg || '请求失败',
              icon: 'none'
            })
            reject(data)
          }
        } else {
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络连接失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// 封装请求方法
export const http = {
  get: (url, data) => request({ url, method: 'GET', data }),
  post: (url, data, opt) => request({ url, method: 'POST', data, ...opt }),
  put: (url, data, opt) => request({ url, method: 'PUT', data, ...opt }),
  delete: (url, opt) => request({ url, method: 'DELETE', ...opt })
}

export default http
