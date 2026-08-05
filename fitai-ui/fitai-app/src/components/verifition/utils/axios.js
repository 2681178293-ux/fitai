/**
 * AJ-Captcha：直连 /captcha/*，返回 ResponseModel（repCode），不经业务 code 封装
 */
const BASE_URL = import.meta.env.VITE_API_BASE || '/api/mobile'

export default function request(config) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + config.url,
      method: (config.method || 'POST').toUpperCase(),
      data: config.data,
      header: {
        'Content-Type': 'application/json; charset=UTF-8',
        'X-Requested-With': 'XMLHttpRequest',
        ...(config.header || {})
      },
      timeout: 40000,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          reject(res)
        }
      },
      fail: reject
    })
  })
}
