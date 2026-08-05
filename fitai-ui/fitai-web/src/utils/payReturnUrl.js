/**
 * 支付宝同步跳转落地页基址（后端会拼接 ?orderNo=）
 * 可通过 VITE_PAY_RETURN_URL 覆盖（生产域名与路径以实际部署为准）
 */
export function getPcPayReturnBaseUrl() {
  const fromEnv = import.meta.env.VITE_PAY_RETURN_URL
  if (fromEnv && String(fromEnv).trim()) {
    return String(fromEnv).replace(/\/$/, '')
  }
  if (typeof window !== 'undefined' && window.location?.origin) {
    return `${window.location.origin}/mall/order-result`
  }
  return 'http://localhost:3000/mall/order-result'
}
