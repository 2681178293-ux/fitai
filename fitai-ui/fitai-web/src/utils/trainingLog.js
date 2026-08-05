/**
 * 从训练记录分页接口取出列表（后端返回 MyBatis-Plus Page，字段为 records）
 */
export function extractTrainingLogRecords(page) {
  if (!page) return []
  return page.records ?? page.list ?? []
}

/**
 * 将 exercisesData 中的动作名展开，用于与计划内动作匹配
 */
export function logMatchesExercise(log, exerciseName) {
  if (!log || !exerciseName) return false
  const target = String(exerciseName).trim()
  const names = []
  if (log.exerciseName) {
    names.push(String(log.exerciseName).trim())
  }
  const data = log.exercisesData
  if (Array.isArray(data)) {
    data.forEach((item) => {
      if (item?.exerciseName) {
        names.push(String(item.exerciseName).trim())
      }
    })
  }
  return names.some((n) => n === target)
}

/**
 * 列表/历史页展示用：补全 exerciseName、组次数、备注等
 */
export function normalizeTrainingLog(log) {
  if (!log) return log
  const data = log.exercisesData
  let exerciseName = log.exerciseName
  let sets = log.sets
  let reps = log.reps
  let weight = log.weight
  const note = log.note ?? log.remark
  if (Array.isArray(data) && data.length > 0) {
    const first = data[0]
    if (!exerciseName && first.exerciseName) {
      exerciseName = String(first.exerciseName).trim()
    }
    if (Array.isArray(first.sets) && first.sets.length > 0) {
      if (sets == null) sets = first.sets.length
      const firstSet = first.sets[0]
      if (reps == null && firstSet?.reps != null) reps = firstSet.reps
      if (weight == null && firstSet?.weight != null) {
        weight = Number(firstSet.weight)
      }
    }
  }
  return { ...log, exerciseName, sets, reps, weight, note }
}
