/**
 * 支付宝同步跳转落地页基址（后端拼接 ?orderNo=）
 */
export function getMobilePayReturnBaseUrl() {
  const fromEnv = import.meta.env.VITE_PAY_RETURN_URL
  if (fromEnv && String(fromEnv).trim()) {
    return String(fromEnv).replace(/\/$/, '')
  }
  if (typeof window !== 'undefined' && window.location?.origin) {
    return `${window.location.origin}/#/pages/order/result`
  }
  return ''
}
