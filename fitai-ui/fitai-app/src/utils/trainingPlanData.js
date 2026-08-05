/**
 * 将后端 / AI 返回的 planData（对象或 JSON 字符串）规范为前端可渲染结构
 */
export function normalizePlanData(raw) {
  if (raw == null || raw === '') return null

  let obj = raw
  if (typeof raw === 'string') {
    try {
      obj = JSON.parse(raw)
    } catch {
      return { _fallbackText: raw }
    }
  }

  if (typeof obj !== 'object' || obj === null) {
    return { _fallbackText: String(raw) }
  }

  if (obj._fallbackText) return obj

  const weekly =
    Array.isArray(obj.weeklyPlan) ? obj.weeklyPlan
    : Array.isArray(obj.days) ? obj.days
    : Array.isArray(obj.week) ? obj.week
    : null

  const tipsArr = asArrayField(obj.tips)
  const warnArr = asArrayField(obj.warnings)
  const hasNutrition = obj.nutrition != null && String(obj.nutrition).trim() !== ''
  const hasWeekly = weekly && weekly.length > 0

  if (hasWeekly || tipsArr.length > 0 || warnArr.length > 0 || hasNutrition) {
    return { ...obj, weeklyPlan: weekly || [] }
  }

  try {
    const s = JSON.stringify(obj, null, 2)
    return { _fallbackText: s.length > 4000 ? s.slice(0, 4000) + '\n…' : s }
  } catch {
    return { _fallbackText: String(obj) }
  }
}

export function asArrayField(v) {
  if (v == null) return []
  if (Array.isArray(v)) return v
  if (typeof v === 'string') return v.trim() ? [v] : []
  return []
}
