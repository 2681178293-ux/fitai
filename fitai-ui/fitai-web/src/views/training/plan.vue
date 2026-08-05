<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPlanById, startPlan, completePlan, abandonPlan, addTrainingLog, getTrainingLogs, getCurrentPlan, getPlanList, modifyPlanExercise } from '@/api/training'
import request from '@/utils/request'
import { extractTrainingLogRecords, logMatchesExercise, normalizeTrainingLog } from '@/utils/trainingLog'
import { Timer, Calendar, Check, Close, TrendCharts, Sunny, Document, Warning, ArrowDown, ArrowUp, VideoPlay, MagicStick, ChatDotRound } from '@element-plus/icons-vue'
import AiReviewDialog from '@/components/training/AiReviewDialog.vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const plan = ref(null)
const loading = ref(true)
const actionLoading = ref(false)
const currentDay = ref(0)
const showLogDialog = ref(false)
const currentExercise = ref(null)
/** 当前弹窗对应的计划日索引（与 plan.startDate 相加得到 trainingDate） */
const logDayIndex = ref(null)
const trainingLogs = ref([])
const showHistoryDialog = ref(false)
const showAiReviewDialog = ref(false)
const aiReviewLogId = ref(null)
const currentActivePlan = ref(null)
const showCurrentPlanAlert = ref(false)
const expandedDays = ref(new Set()) // 展开的日期索引

// 计划列表（用于切换）
const allPlans = ref([])
const showPlanSwitcher = ref(false)

// AI 调整动作
const showModifyDialog = ref(false)
const modifyingExercise = ref(null)
const modifyDayIndex = ref(null)
const modifyExerciseIndex = ref(null)
const modifyRequest = ref('')
const modifyLoading = ref(false)

const logForm = ref({
  sets: 3,
  reps: 10,
  weight: 0,
  duration: 0,
  note: ''
})

// 加载计划详情
const loadPlan = async (forceRefresh = false) => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('计划ID不存在')
    router.push('/training')
    return
  }
  loading.value = true
  try {
    // 强制刷新：添加时间戳避免缓存
    let data
    if (forceRefresh) {
      // 使用带时间戳的请求避免缓存
      data = await request.get(`/training/plan/${id}`, {
        params: { _t: Date.now() }
      })
    } else {
      data = await getPlanById(id)
    }
    plan.value = data
    
    // 计算当前是第几天
    if (data.startDate) {
      const start = new Date(data.startDate)
      const now = new Date()
      const diff = Math.floor((now - start) / (1000 * 60 * 60 * 24))
      currentDay.value = Math.max(0, diff)
    }
    
    // 加载训练记录
    if (data.id) {
      loadTrainingLogs(data.id)
    }
    
    // 如果当前计划是待开始状态，检查是否有进行中的计划
    if (data.status === 0) {
      await checkCurrentActivePlan()
    } else if (data.status === 1) {
      // 如果计划是进行中，清除警告提示
      showCurrentPlanAlert.value = false
      currentActivePlan.value = null
    }
    
    // 默认展开当前训练日
    if (data.status === 1 && currentDay.value >= 0) {
      expandedDays.value.add(currentDay.value)
    } else {
      // 如果计划未开始，展开第一天
      expandedDays.value.add(0)
    }
  } catch (e) {
    ElMessage.error('加载失败')
    router.push('/training')
  } finally {
    loading.value = false
  }
}

// 检查当前是否有进行中的计划
const checkCurrentActivePlan = async () => {
  try {
    const currentPlan = await getCurrentPlan()
    if (!currentPlan) {
      // 没有当前计划，清除警告
      showCurrentPlanAlert.value = false
      currentActivePlan.value = null
      return null
    }
    
    // 转换为字符串比较，避免类型不一致
    const currentPlanId = String(currentPlan.id)
    const currentPagePlanId = plan.value?.id ? String(plan.value.id) : null
    
    // 只处理状态为1（进行中）的计划
    if (currentPlan.status === 1) {
      // 如果当前计划存在且不是当前页面显示的计划
      if (currentPlanId !== currentPagePlanId) {
        // 有另一个进行中的计划
        currentActivePlan.value = currentPlan
        showCurrentPlanAlert.value = true
        return currentPlan
      } else {
        // 如果是同一个计划，但页面显示状态是待开始，说明数据不同步
        // 强制重新加载当前计划数据
        if (plan.value && plan.value.status === 0) {
          console.log('检测到计划状态不一致，强制刷新...', {
            currentPlanStatus: currentPlan.status,
            pagePlanStatus: plan.value.status,
            planId: currentPlanId
          })
          await loadPlan(true) // 强制刷新
        }
      }
    } else {
      // 如果返回的计划状态不是1（进行中），说明没有进行中的计划
      // 清除警告提示
      showCurrentPlanAlert.value = false
      currentActivePlan.value = null
    }
    
    return null
  } catch (e) {
    console.error('检查当前计划失败:', e)
    // 忽略错误，可能没有进行中的计划
    showCurrentPlanAlert.value = false
    currentActivePlan.value = null
    return null
  }
}

// 加载训练记录
const loadTrainingLogs = async (planId) => {
  try {
    const page = await getTrainingLogs({ planId, pageNum: 1, pageSize: 100 })
    const records = extractTrainingLogRecords(page)
    trainingLogs.value = records.map((row) => normalizeTrainingLog(row))
  } catch (e) {
    console.error('加载训练记录失败', e)
  }
}

// 检查动作是否已完成
const isExerciseCompleted = (exercise, dayIndex) => {
  if (!plan.value || !plan.value.startDate) return false
  if (dayIndex > currentDay.value) return false
  
  const exerciseName = exercise.name || exercise.exercise
  if (!exerciseName) return false
  
  // 计算该训练日对应的日期
  const startDate = new Date(plan.value.startDate)
  const targetDate = new Date(startDate)
  targetDate.setDate(startDate.getDate() + dayIndex)
  
  // 检查是否有该动作在该日期的训练记录
  return trainingLogs.value.some((log) => {
    if (!log.trainingDate) return false
    if (!logMatchesExercise(log, exerciseName)) return false

    const logDate = new Date(log.trainingDate)
    const logDateStr = `${logDate.getFullYear()}-${String(logDate.getMonth() + 1).padStart(2, '0')}-${String(logDate.getDate()).padStart(2, '0')}`
    const targetDateStr = `${targetDate.getFullYear()}-${String(targetDate.getMonth() + 1).padStart(2, '0')}-${String(targetDate.getDate()).padStart(2, '0')}`

    return logDateStr === targetDateStr
  })
}

// 检查当天所有动作是否都已完成
const isDayCompleted = (dayIndex) => {
  if (!plan.value || plan.value.status !== 1) return false
  if (dayIndex > currentDay.value) return false
  
  const day = weeklyPlan.value[dayIndex]
  if (!day || day.restDay) return false
  
  const exercises = day.exercises || day.workouts || []
  if (exercises.length === 0) return false
  
  // 检查是否所有动作都已完成
  return exercises.every(exercise => isExerciseCompleted(exercise, dayIndex))
}

// 检查整个计划是否全部完成
const isPlanCompleted = computed(() => {
  if (!plan.value || plan.value.status !== 1) return false
  
  // 检查所有已过去的训练日是否都完成了
  for (let i = 0; i <= currentDay.value && i < weeklyPlan.value.length; i++) {
    const day = weeklyPlan.value[i]
    if (!day.restDay && !isDayCompleted(i)) {
      return false
    }
  }
  
  return true
})

// 计划状态映射：后端返回数字，前端使用字符串
const statusConfig = {
  0: { label: '待开始', color: 'info', key: 'pending' },
  1: { label: '进行中', color: 'success', key: 'active' },
  2: { label: '已完成', color: 'primary', key: 'completed' },
  3: { label: '已放弃', color: 'danger', key: 'abandoned' }
}

// 获取计划状态配置
const planStatus = computed(() => {
  if (!plan.value) return null
  return statusConfig[plan.value.status] || statusConfig[0]
})

// 获取状态文本（用于计划切换器）
const getStatusText = (status) => {
  const statusMap = {
    0: '草稿',
    1: '进行中',
    2: '已完成',
    3: '已放弃'
  }
  return statusMap[status] || `未知(${status})`
}

// 获取状态颜色（用于计划切换器）
const getStatusColor = (status) => {
  const colorMap = {
    0: 'info',
    1: 'success',
    2: 'primary',
    3: 'danger'
  }
  return colorMap[status] || 'info'
}

// 格式化日期（用于计划切换器）
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

// 解析训练计划数据
const weeklyPlan = computed(() => {
  if (!plan.value?.planData) return []
  
  try {
    const data = typeof plan.value.planData === 'string' 
      ? JSON.parse(plan.value.planData) 
      : plan.value.planData
    
    // 适配不同的数据结构
    if (Array.isArray(data)) return data
    if (data.weeklyPlan) return data.weeklyPlan
    if (data.days) return data.days
    
    return []
  } catch {
    return []
  }
})

// 开始训练计划
const handleStart = async () => {
  // 防止重复点击
  if (actionLoading.value) {
    return
  }
  
  actionLoading.value = true
  try {
    // 先检查当前计划状态
    const currentPlanData = await getPlanById(plan.value.id)
    if (currentPlanData.status === 1) {
      ElMessage.info('计划已经是进行中状态')
      await loadPlan(true) // 强制刷新
      actionLoading.value = false
      return
    }
    
    // 调用开始计划接口
    await startPlan(plan.value.id)
    ElMessage.success('✅ 计划已开始！现在可以记录训练了')
    
    // 强制刷新计划数据
    await loadPlan(true)
    
    // 清除警告提示
    showCurrentPlanAlert.value = false
    currentActivePlan.value = null
  } catch (error) {
    console.error('开始计划失败:', error)
    
    // 如果是"已有进行中的计划"错误，获取当前计划并显示友好提示
    if (error.message && error.message.includes('已有进行中的计划')) {
      try {
        // 重新获取当前进行中的计划
        const currentPlan = await getCurrentPlan()
        
        // 只处理状态为1（进行中）的计划
        if (currentPlan && currentPlan.status === 1) {
          currentActivePlan.value = currentPlan
          showCurrentPlanAlert.value = true
          
          // 显示确认对话框，提供跳转选项
          ElMessageBox.confirm(
            `<div style="padding: 10px 0;">
              <div style="font-size: 16px; font-weight: 600; margin-bottom: 12px; color: #e6a23c;">
                ⚠️ 无法开始新计划
              </div>
              <div style="margin-bottom: 12px; line-height: 1.6;">
                您当前有一个进行中的计划：<br/>
                <span style="font-weight: 600; color: #409eff;">"${currentPlan.name || 'AI训练计划'}"</span>
              </div>
              <div style="background: #fdf6ec; padding: 10px; border-radius: 4px; margin-bottom: 12px; border-left: 3px solid #e6a23c;">
                <div style="font-weight: 600; margin-bottom: 4px;">系统限制：</div>
                <div>同时只能有一个进行中的训练计划</div>
              </div>
              <div style="background: #f0f9ff; padding: 10px; border-radius: 4px; border-left: 3px solid #409eff;">
                <div style="font-weight: 600; margin-bottom: 4px; color: #409eff;">解决方案：</div>
                <ol style="margin: 4px 0; padding-left: 20px; line-height: 1.8;">
                  <li>点击"前往当前计划"按钮</li>
                  <li>完成或放弃当前进行中的计划</li>
                  <li>然后才能开始这个新计划</li>
                </ol>
              </div>
            </div>`,
            '提示',
            {
              confirmButtonText: '前往当前计划',
              cancelButtonText: '取消',
              type: 'warning',
              distinguishCancelAndClose: true,
              dangerouslyUseHTMLString: true,
              customClass: 'plan-conflict-dialog'
            }
          ).then(async () => {
            // 跳转到当前计划
            if (currentPlan.id) {
              const planId = String(currentPlan.id)
              const currentPagePlanId = String(route.params.id)
              
              // 检查是否是当前页面
              if (currentPagePlanId === planId) {
                // 如果是同一个计划，说明这就是进行中的计划
                // 但当前计划状态是"待开始"，说明数据不同步，强制刷新
                ElMessage.info('正在刷新计划数据...')
                
                // 强制重新加载计划数据（带时间戳避免缓存）
                await loadPlan(true)
                
                // 等待数据加载完成
                await new Promise(resolve => setTimeout(resolve, 500))
                
                // 再次检查状态
                if (plan.value) {
                  if (plan.value.status === 1) {
                    ElMessage.success('✅ 计划状态已更新为"进行中"，现在可以记录训练了！')
                    // 清除警告提示
                    showCurrentPlanAlert.value = false
                    currentActivePlan.value = null
                  } else if (plan.value.status === 0) {
                    // 如果刷新后仍然是待开始，说明后端数据确实不是进行中
                    // 可能是 getCurrentPlan 返回了错误的计划，或者数据不一致
                    ElMessage.warning({
                      message: '⚠️ 数据不一致：后端显示这是进行中的计划，但状态为"待开始"。请刷新页面或直接点击"开始训练"按钮。',
                      duration: 6000
                    })
                    // 清除警告提示，允许用户直接开始
                    showCurrentPlanAlert.value = false
                    currentActivePlan.value = null
                  }
                }
                return
              }
              
              // 使用 replace 跳转，避免历史记录堆积
              router.replace(`/training/plan/${planId}`).then(() => {
                // 跳转成功，页面会自动重新加载
              }).catch(err => {
                // 忽略 NavigationDuplicated 错误
                if (err.name !== 'NavigationDuplicated') {
                  ElMessage.error('跳转失败，请稍后重试')
                  console.error('对话框路由跳转错误:', err)
                }
              })
            } else {
              ElMessage.error('计划ID不存在')
            }
          }).catch(() => {
            // 用户取消或关闭对话框，不做任何操作
          })
        } else {
          ElMessage.error(error.message || '操作失败')
        }
      } catch (e) {
        ElMessage.error(error.message || '操作失败')
      }
    } else {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    actionLoading.value = false
  }
}

// 完成训练计划
const handleComplete = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要完成这个训练计划吗？完成后将无法继续记录训练。',
      '完成计划',
      {
        confirmButtonText: '确定完成',
        cancelButtonText: '取消',
        type: 'success',
        distinguishCancelAndClose: true
      }
    )
    
    actionLoading.value = true
    await completePlan(plan.value.id)
    ElMessage.success('🎉 恭喜完成训练计划！')
    loadPlan()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    actionLoading.value = false
  }
}

// 放弃训练计划
const handleAbandon = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要放弃这个训练计划吗？放弃后将无法恢复，需要重新生成计划才能开始训练。',
      '放弃计划',
      {
        confirmButtonText: '确定放弃',
        cancelButtonText: '取消',
        type: 'warning',
        distinguishCancelAndClose: true
      }
    )
    
    actionLoading.value = true
    await abandonPlan(plan.value.id)
    ElMessage.success('计划已放弃')
    loadPlan()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    actionLoading.value = false
  }
}

// 打开记录弹窗
const openLogDialog = (exercise, dayIndex) => {
  currentExercise.value = exercise
  logDayIndex.value = dayIndex
  const repsRaw = exercise.reps
  const repsNum = typeof repsRaw === 'number' ? repsRaw : parseInt(String(repsRaw ?? '').split(/[-~]/)[0], 10)
  logForm.value = {
    sets: exercise.sets || 3,
    reps: Number.isFinite(repsNum) && repsNum > 0 ? repsNum : 10,
    weight: 0,
    duration: exercise.duration || 0,
    note: ''
  }
  showLogDialog.value = true
}

// 保存训练记录
const handleSaveLog = async () => {
  try {
    if (logDayIndex.value == null || !plan.value?.startDate) {
      ElMessage.error('无法确定训练日，请关闭后重试')
      return
    }
    const start = new Date(plan.value.startDate)
    const trainingDay = new Date(start.getFullYear(), start.getMonth(), start.getDate())
    trainingDay.setDate(trainingDay.getDate() + logDayIndex.value)
    const trainingDateStr = `${trainingDay.getFullYear()}-${String(trainingDay.getMonth() + 1).padStart(2, '0')}-${String(trainingDay.getDate()).padStart(2, '0')}`

    const newLogId = await addTrainingLog({
      planId: plan.value.id,
      exerciseName: (currentExercise.value.name || currentExercise.value.exercise || '').trim(),
      trainingDate: trainingDateStr,
      sets: logForm.value.sets,
      reps: logForm.value.reps,
      weight: logForm.value.weight,
      duration: logForm.value.duration ?? 0,
      note: logForm.value.note
    })

    // 重新加载训练记录
    await loadTrainingLogs(plan.value.id)

    // 检查是否完成当天所有训练
    const dayIndex = weeklyPlan.value.findIndex((day, idx) => {
      const exercises = day.exercises || day.workouts || []
      return exercises.some(ex =>
        (ex.name || ex.exercise) === (currentExercise.value.name || currentExercise.value.exercise)
      )
    })

    if (dayIndex >= 0 && isDayCompleted(dayIndex)) {
      ElMessage.success('🎉 恭喜！您已完成今天的全部训练！')
    } else {
      ElMessage.success('✅ 记录成功！继续加油！')
    }

    showLogDialog.value = false
    logDayIndex.value = null

    if (newLogId) {
      try {
        await ElMessageBox.confirm('记录已保存，是否查看本次训练的 AI 点评？', 'AI 训练点评', {
          confirmButtonText: '查看点评',
          cancelButtonText: '暂不',
          type: 'info'
        })
        aiReviewLogId.value = newLogId
        showAiReviewDialog.value = true
      } catch {
        /* 用户取消 */
      }
    }
  } catch (error) {
    ElMessage.error(error.message || '记录失败')
  }
}

// 打开历史记录对话框
const openHistoryDialog = () => {
  showHistoryDialog.value = true
}

function openAiReviewForLog(logId) {
  if (!logId) return
  aiReviewLogId.value = logId
  showAiReviewDialog.value = true
}

// 打开 AI 调整弹窗
const openModifyDialog = (exercise, dayIndex, exIndex) => {
  modifyingExercise.value = exercise
  modifyDayIndex.value = dayIndex
  modifyExerciseIndex.value = exIndex
  modifyRequest.value = ''
  showModifyDialog.value = true
}

// 提交 AI 调整请求
const handleModifyExercise = async () => {
  if (!modifyRequest.value.trim()) {
    ElMessage.warning('请输入修改需求')
    return
  }
  modifyLoading.value = true
  const startTime = Date.now()
  try {
    await modifyPlanExercise({
      planId: plan.value.id,
      dayIndex: modifyDayIndex.value,
      exerciseIndex: modifyExerciseIndex.value,
      userRequest: modifyRequest.value.trim()
    })
    const elapsed = Date.now() - startTime
    ElMessage.success(`AI 动作调整成功！用时 ${elapsed} ms`)
    showModifyDialog.value = false
    // 刷新计划数据
    await loadPlan(true)
  } catch (error) {
    const errorMessage = error.response?.data?.msg || error.response?.data?.message || error.message || '调整失败，请稍后再试'
    ElMessage.error({ message: errorMessage, duration: 5000, showClose: true })
  } finally {
    modifyLoading.value = false
  }
}

// 切换日期展开/折叠
const toggleDay = (dayIndex) => {
  if (expandedDays.value.has(dayIndex)) {
    expandedDays.value.delete(dayIndex)
  } else {
    expandedDays.value.add(dayIndex)
  }
}

// 检查日期是否展开
const isDayExpanded = (dayIndex) => {
  return expandedDays.value.has(dayIndex)
}

// 开始当天的训练
const startTodayTraining = (dayIndex) => {
  // 展开当天的训练内容
  expandedDays.value.add(dayIndex)
  // 滚动到当天训练内容
  setTimeout(() => {
    const element = document.querySelector(`[data-day-index="${dayIndex}"]`)
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  }, 100)
}

// 跳转到当前进行中的计划
const goToCurrentPlan = async () => {
  // 防止重复点击
  if (actionLoading.value) {
    return
  }
  
  actionLoading.value = true
  try {
    let planId = null
    
    // 优先使用已有的计划ID
    if (currentActivePlan.value?.id) {
      planId = String(currentActivePlan.value.id)
    } else {
      // 如果数据不存在，尝试重新获取
      ElMessage.warning('正在获取计划信息...')
      const plan = await checkCurrentActivePlan()
      if (plan?.id) {
        planId = String(plan.id)
        currentActivePlan.value = plan
      }
    }
    
    if (planId) {
      // 检查是否是当前页面
      const currentPlanId = route.params.id
      if (currentPlanId === planId) {
        ElMessage.info('这就是当前计划页面')
        actionLoading.value = false
        return
      }
      
      // 使用 replace 而不是 push，避免浏览器历史记录堆积
      router.replace(`/training/plan/${planId}`).then(() => {
        // 跳转成功后重新加载页面数据
        loadPlan()
      }).catch(err => {
        // 忽略 NavigationDuplicated 错误（Vue Router 的正常行为）
        if (err.name !== 'NavigationDuplicated') {
          ElMessage.error('跳转失败，请稍后重试')
          console.error('路由跳转错误:', err)
        } else {
          // 即使是重复导航，也重新加载数据
          loadPlan()
        }
      })
    } else {
      ElMessage.error('无法获取当前计划ID，请刷新页面重试')
      actionLoading.value = false
    }
  } catch (error) {
    console.error('跳转失败:', error)
    ElMessage.error(`跳转失败: ${error.message || '请稍后重试'}`)
    actionLoading.value = false
  }
}

// 加载所有计划列表（用于切换）
const loadAllPlans = async () => {
  try {
    const result = await getPlanList({ pageNum: 1, pageSize: 100 })
    if (result && result.records) {
      allPlans.value = result.records || []
    } else if (Array.isArray(result)) {
      allPlans.value = result
    } else if (result && result.data) {
      allPlans.value = Array.isArray(result.data) ? result.data : (result.data.records || [])
    } else {
      allPlans.value = []
    }
    
    // 按创建时间倒序排列
    allPlans.value.sort((a, b) => {
      const timeA = new Date(a.createTime || 0).getTime()
      const timeB = new Date(b.createTime || 0).getTime()
      return timeB - timeA
    })
  } catch (e) {
    console.error('加载计划列表失败', e)
    allPlans.value = []
  }
}

// 切换到其他计划
const switchToOtherPlan = (targetPlanId) => {
  if (!targetPlanId || String(targetPlanId) === String(route.params.id)) {
    return
  }
  
  // 使用 replace 避免历史记录堆积
  router.replace(`/training/plan/${targetPlanId}`).then(() => {
    loadPlan(true) // 强制刷新
  }).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      ElMessage.error('切换失败，请稍后重试')
      console.error('切换计划失败:', err)
    } else {
      loadPlan(true)
    }
  })
}

onMounted(() => {
  loadPlan()
  loadAllPlans() // 加载计划列表用于切换
})
</script>

<template>
  <div class="plan-detail-page p-4 max-w-4xl mx-auto pb-24">
    <div v-if="loading" class="text-center py-16 text-gray-500">
      <el-icon class="is-loading text-3xl"><Timer /></el-icon>
      <p class="mt-2">加载中...</p>
    </div>
    
    <template v-else-if="plan">
      <!-- 当前计划提示 -->
      <el-alert
        v-if="showCurrentPlanAlert && currentActivePlan"
        type="warning"
        :closable="true"
        show-icon
        class="mb-4"
        @close="showCurrentPlanAlert = false"
      >
        <template #title>
          <div class="flex items-center gap-2">
            <el-icon><Warning /></el-icon>
            <span class="font-medium">已有进行中的计划</span>
          </div>
        </template>
        <template #default>
          <div class="mt-2">
            <p class="mb-3 text-gray-700">
              您当前有一个进行中的计划：<span class="font-semibold text-orange-600">{{ currentActivePlan.name || 'AI训练计划' }}</span>
            </p>
            <p class="mb-3 text-sm text-gray-600">
              请先完成或放弃当前计划，才能开始新的计划。
            </p>
            <el-button 
              type="primary" 
              @click="goToCurrentPlan"
              :loading="actionLoading"
              :disabled="!currentActivePlan?.id"
            >
              <el-icon><TrendCharts /></el-icon>
              前往当前计划
            </el-button>
          </div>
        </template>
      </el-alert>

      <!-- 计划头部 -->
      <div class="plan-header bg-gradient-to-r from-emerald-500 to-cyan-500 rounded-2xl p-6 text-white mb-6">
        <div class="flex justify-between items-start">
          <div class="flex-1">
            <div class="flex items-center justify-between mb-2">
              <el-tag :type="planStatus?.color" effect="dark">
                {{ planStatus?.label }}
              </el-tag>
              <!-- 计划切换器 -->
              <el-dropdown 
                v-if="allPlans.length > 1"
                trigger="click"
                @command="switchToOtherPlan"
                class="plan-switcher"
              >
                <el-button 
                  type="primary" 
                  size="small" 
                  :icon="Document"
                  circle
                  style="background: rgba(255, 255, 255, 0.2); border: 1px solid rgba(255, 255, 255, 0.3);"
                >
                  <el-icon><Document /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu class="plan-switcher-menu">
                    <div class="plan-switcher-header">切换计划</div>
                    <el-dropdown-item 
                      v-for="p in allPlans" 
                      :key="p.id"
                      :command="p.id"
                      :class="{ 'is-current': String(p.id) === String(plan.id) }"
                    >
                      <div class="plan-switcher-item">
                        <div class="plan-switcher-name">
                          {{ p.name }}
                          <el-tag 
                            v-if="String(p.id) === String(plan.id)" 
                            size="small" 
                            type="success"
                            effect="plain"
                            style="margin-left: 8px;"
                          >
                            当前
                          </el-tag>
                        </div>
                        <div class="plan-switcher-meta">
                          <el-tag 
                            size="small" 
                            :type="getStatusColor(p.status)"
                            effect="dark"
                          >
                            {{ getStatusText(p.status) }}
                          </el-tag>
                          <span class="plan-switcher-date">{{ formatDate(p.createTime) }}</span>
                        </div>
                      </div>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <h1 class="text-2xl font-bold">{{ plan.name || 'AI 训练计划' }}</h1>
            <p class="mt-2 opacity-90 flex items-center gap-2">
              <el-icon><Calendar /></el-icon>
              {{ plan.startDate }} - {{ plan.endDate }}
            </p>
          </div>
          <div class="text-right ml-4">
            <div class="text-3xl font-bold">{{ plan.totalDays || weeklyPlan.length }}</div>
            <div class="text-sm opacity-90">天训练</div>
          </div>
        </div>
        
        <!-- 进度条 -->
        <div v-if="plan.status === 1" class="mt-4">
          <div class="flex justify-between text-sm mb-1">
            <span>训练进度</span>
            <span>第 {{ currentDay + 1 }} 天</span>
          </div>
          <el-progress 
            :percentage="Math.min(100, ((currentDay + 1) / (plan.totalDays || weeklyPlan.length)) * 100)"
            :show-text="false"
            :stroke-width="8"
            color="#ffffff"
          />
        </div>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-section mb-6">
        <!-- 操作说明 -->
        <div class="action-tips mb-4 p-4 bg-blue-50 rounded-lg border border-blue-200">
          <div class="flex items-start gap-3">
            <el-icon class="text-blue-500 mt-0.5 text-xl"><Sunny /></el-icon>
            <div class="flex-1 text-sm text-gray-700">
              <!-- 待开始状态 -->
              <template v-if="plan.status === 0">
                <div class="font-semibold mb-2 text-base text-gray-800">为什么是"待开始"状态？</div>
                <div v-if="showCurrentPlanAlert && currentActivePlan" class="mb-3 p-2 bg-yellow-50 rounded border border-yellow-200">
                  <div class="font-medium text-yellow-800 mb-1">⚠️ 无法开始的原因：</div>
                  <div class="text-yellow-700">您当前有一个进行中的计划："<span class="font-semibold">{{ currentActivePlan.name }}</span>"</div>
                  <div class="text-yellow-700 mt-1">系统限制：同时只能有一个进行中的训练计划。</div>
                  <div class="mt-2 text-yellow-800">
                    <span class="font-medium">解决方案：</span>
                    <ol class="list-decimal list-inside mt-1 space-y-1">
                      <li>点击上方"前往当前计划"按钮</li>
                      <li>完成或放弃当前进行中的计划</li>
                      <li>然后才能开始这个新计划</li>
                    </ol>
                  </div>
                </div>
                <div v-else>
                  <div class="font-medium mb-1">如何开始训练？</div>
                  <div>点击下方"开始训练"按钮，计划将正式开始。开始后可以每天记录训练完成情况。</div>
                </div>
              </template>
              
              <!-- 进行中状态 -->
              <template v-if="plan.status === 1">
                <div class="font-semibold mb-2 text-base text-gray-800">如何记录和确认训练？</div>
                <div class="space-y-2">
                  <div class="flex items-start gap-2">
                    <span class="font-medium text-green-600">1️⃣ 记录单个动作：</span>
                    <span>完成某个动作后，点击动作右侧的<span class="font-semibold text-blue-600">"记录完成"</span>按钮，填写实际完成的组数、次数等信息。</span>
                  </div>
                  <div class="flex items-start gap-2">
                    <span class="font-medium text-green-600">2️⃣ 查看完成状态：</span>
                    <span>已完成的动作会显示<span class="px-2 py-0.5 bg-green-100 text-green-700 rounded text-xs font-medium">✓ 已完成</span>标签和绿色背景。</span>
                  </div>
                  <div class="flex items-start gap-2">
                    <span class="font-medium text-green-600">3️⃣ 完成当天训练：</span>
                    <span>完成当天所有动作后，训练日会显示<span class="px-2 py-0.5 bg-green-100 text-green-700 rounded text-xs font-medium">✓ 全部完成</span>标签。</span>
                  </div>
                  <div class="flex items-start gap-2">
                    <span class="font-medium text-green-600">4️⃣ 完成整个计划：</span>
                    <span>完成所有训练日后，<span class="font-semibold text-green-600">"完成计划"</span>按钮将可用，点击即可结束整个训练计划。</span>
                  </div>
                  <div v-if="isPlanCompleted" class="mt-3 p-3 bg-gradient-to-r from-green-50 to-emerald-50 rounded border-2 border-green-300">
                    <div class="flex items-center gap-2 text-green-800 font-semibold">
                      <el-icon class="text-xl"><Check /></el-icon>
                      <span>🎉 恭喜！您已完成所有训练，可以点击"完成计划"结束训练了！</span>
                    </div>
                  </div>
                </div>
              </template>
              
              <!-- 已完成状态 -->
              <template v-if="plan.status === 2">
                <div class="font-medium mb-1">计划已完成</div>
                <div>恭喜！您已完成这个训练计划。可以查看训练记录或生成新的计划。</div>
              </template>
            </div>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="action-buttons flex gap-3 flex-wrap">
          <el-button 
            v-if="plan.status === 0"
            type="primary" 
            :loading="actionLoading"
            @click="handleStart"
            size="large"
            class="flex-1 min-w-[140px]"
          >
            <el-icon><VideoPlay /></el-icon>
            开始训练
          </el-button>
          
        <el-button 
          v-if="plan.status === 1"
          type="success"
          :loading="actionLoading"
          @click="handleComplete"
          size="large"
          class="flex-1 min-w-[140px]"
          :disabled="!isPlanCompleted"
        >
          <el-icon><Check /></el-icon>
          {{ isPlanCompleted ? '完成计划' : '完成计划（需完成所有训练）' }}
        </el-button>
          
          <el-button 
            v-if="plan.status === 1"
            type="danger"
            :loading="actionLoading"
            @click="handleAbandon"
            size="large"
            class="flex-1 min-w-[140px]"
          >
            <el-icon><Close /></el-icon>
            放弃计划
          </el-button>
          
          <el-button 
            v-if="plan.status === 1 || plan.status === 2"
            type="info"
            @click="openHistoryDialog"
            size="large"
          >
            <el-icon><Document /></el-icon>
            查看记录
          </el-button>
        </div>
      </div>

      <!-- 每日训练计划 -->
      <div class="weekly-plan">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-bold flex items-center gap-2">
            <el-icon class="text-orange-500"><TrendCharts /></el-icon>
            训练安排
          </h2>
          <div v-if="plan.status === 1" class="text-sm text-gray-500 flex items-center gap-2">
            <el-icon class="text-blue-500"><Sunny /></el-icon>
            <span>点击动作右侧的<span class="font-semibold text-blue-600">"记录完成"</span>按钮确认训练</span>
          </div>
        </div>
        
        <el-empty v-if="weeklyPlan.length === 0" description="暂无训练安排" />
        
        <!-- 提示：如何记录训练 -->
        <div v-if="plan.status === 1 && weeklyPlan.length > 0" class="mb-4 p-3 bg-blue-50 rounded-lg border border-blue-200">
          <div class="flex items-start gap-2 text-sm text-blue-800">
            <el-icon class="text-blue-500 mt-0.5"><Sunny /></el-icon>
            <div>
              <div class="font-medium mb-1">如何确认训练完成？</div>
              <div>1. 展开训练日（点击训练日头部）</div>
              <div>2. 完成某个动作后，点击动作右侧的<span class="font-semibold text-blue-600">蓝色"记录完成"</span>按钮</div>
              <div>3. 填写实际完成的组数、次数等信息</div>
              <div>4. 点击"确认完成"保存记录</div>
            </div>
          </div>
        </div>
        
        <div 
          v-for="(day, dayIndex) in weeklyPlan" 
          :key="dayIndex" 
          :data-day-index="dayIndex"
          class="day-card bg-white rounded-xl p-4 mb-4 shadow-sm hover:shadow-md transition-shadow"
        >
          <div 
            class="day-header flex flex-wrap items-center justify-between gap-x-3 gap-y-2 mb-3 cursor-pointer"
            @click="toggleDay(dayIndex)"
          >
            <div class="flex min-w-0 flex-1 flex-wrap items-center gap-2">
              <el-icon 
                class="shrink-0 text-gray-400 transition-transform"
                :class="{ 'rotate-90': isDayExpanded(dayIndex) }"
              >
                <ArrowDown />
              </el-icon>
              <span 
                :class="['day-badge shrink-0', { 'active': dayIndex === currentDay, 'completed': dayIndex < currentDay }]"
              >
                {{ day.day || `第${dayIndex + 1}天` }}
              </span>
              <span class="min-w-0 flex-1 truncate text-gray-500">
                {{ day.focus || day.target || day.title || '' }}
              </span>
              <div class="flex shrink-0 items-center gap-1.5">
                <el-tag v-if="dayIndex === currentDay && plan.status === 1" type="warning" size="small" class="day-header-tag">
                  今日
                </el-tag>
                <el-tag v-if="isDayCompleted(dayIndex)" type="success" size="small" class="day-header-tag day-header-tag--done">
                  <el-icon class="day-header-tag__icon"><Check /></el-icon>
                  全部完成
                </el-tag>
              </div>
            </div>
            <div class="flex shrink-0 items-center gap-2">
              <el-button 
                v-if="plan.status === 1 && dayIndex === currentDay && !isDayCompleted(dayIndex)"
                type="primary" 
                size="small"
                @click.stop="startTodayTraining(dayIndex)"
              >
                <el-icon><VideoPlay /></el-icon>
                开始训练
              </el-button>
              <el-tag v-if="day.restDay" type="info">休息日</el-tag>
            </div>
          </div>

          <!-- 训练动作列表（可折叠） -->
          <el-collapse-transition>
            <div v-show="isDayExpanded(dayIndex) || dayIndex === currentDay">
              <div v-if="!day.restDay && (day.exercises || day.workouts)" class="exercises-list space-y-3 mt-3">
                <div 
                  v-for="(exercise, exIndex) in (day.exercises || day.workouts)" 
                  :key="exIndex"
                  :class="['exercise-item flex justify-between items-center p-4 rounded-lg transition-all', 
                    isExerciseCompleted(exercise, dayIndex) ? 'bg-green-50 border-2 border-green-300' : 'bg-gray-50 border border-gray-200 hover:border-blue-300']"
                >
                  <div class="flex-1">
                    <div class="flex items-center gap-2 mb-1">
                      <div class="font-semibold text-gray-800">{{ exercise.name || exercise.exercise }}</div>
                      <el-icon v-if="isExerciseCompleted(exercise, dayIndex)" class="text-green-500 text-lg">
                        <Check />
                      </el-icon>
                    </div>
                    <div class="text-sm text-gray-600">
                      <span v-if="exercise.sets" class="font-medium">{{ exercise.sets }}组</span>
                      <span v-if="exercise.reps"> x <span class="font-medium">{{ exercise.reps }}</span>次</span>
                      <span v-if="exercise.duration">，<span class="font-medium">{{ exercise.duration }}</span>分钟</span>
                      <span v-if="exercise.weight">，<span class="font-medium">{{ exercise.weight }}kg</span></span>
                    </div>
                  </div>
                  <div class="flex items-center gap-2 ml-4">
                    <!-- 已完成状态（不用 el-tag：图标+中文易被默认行高裁切） -->
                    <span
                      v-if="isExerciseCompleted(exercise, dayIndex)"
                      class="exercise-completed-badge"
                    >
                      <el-icon class="exercise-completed-badge__icon"><Check /></el-icon>
                      已完成
                    </span>

                    <!-- 记录完成按钮 - 计划进行中且未完成 -->
                    <el-button
                      v-else-if="plan.status === 1 && dayIndex <= currentDay"
                      type="primary"
                      size="default"
                      @click.stop="openLogDialog(exercise, dayIndex)"
                      class="font-medium"
                    >
                      <el-icon><Check /></el-icon>
                      记录完成
                    </el-button>

                    <!-- 计划未开始或未来日期的提示 -->
                    <el-tag
                      v-else-if="plan.status === 0"
                      type="info"
                      size="small"
                    >
                      请先开始计划
                    </el-tag>
                    <el-tag
                      v-else-if="dayIndex > currentDay"
                      type="info"
                      size="small"
                    >
                      未到训练日
                    </el-tag>

                    <!-- 仅待开始状态可 AI 调整；开始执行后不再允许改动作，避免与记录进度混淆 -->
                    <el-button
                      v-if="plan.status === 0"
                      size="small"
                      @click.stop="openModifyDialog(exercise, dayIndex, exIndex)"
                      title="AI 调整此动作"
                    >
                      <el-icon><MagicStick /></el-icon>
                      AI 调整
                    </el-button>
                  </div>
                </div>
              </div>
              
              <!-- 休息日 -->
              <div v-else-if="day.restDay" class="text-center py-4 text-gray-400">
                <el-icon class="text-2xl"><Timer /></el-icon>
                <p class="mt-1">休息恢复</p>
              </div>
            </div>
          </el-collapse-transition>
        </div>
      </div>

      <!-- 训练建议 -->
      <div v-if="plan.tips || plan.notes" class="tips-card bg-amber-50 rounded-xl p-4 mt-6">
        <h3 class="font-medium text-amber-800 mb-2">训练建议</h3>
        <p class="text-amber-700 text-sm">{{ plan.tips || plan.notes }}</p>
      </div>
    </template>

    <!-- 训练记录历史弹窗 -->
    <el-dialog v-model="showHistoryDialog" title="训练记录" width="600px">
      <div v-if="trainingLogs.length === 0" class="text-center py-8 text-gray-400">
        <el-icon :size="48"><Document /></el-icon>
        <p class="mt-2">暂无训练记录</p>
      </div>
      <div v-else class="space-y-3">
        <div
          v-for="log in trainingLogs"
          :key="log.id"
          class="log-item bg-gray-50 rounded-lg p-3"
        >
          <div class="flex justify-between items-start gap-2">
            <div class="min-w-0 flex-1">
              <div class="font-medium">{{ log.exerciseName }}</div>
              <div class="text-sm text-gray-500 mt-1">
                <span v-if="log.sets">{{ log.sets }}组 x {{ log.reps }}次</span>
                <span v-if="log.weight" class="ml-2">{{ log.weight }}kg</span>
                <span v-if="log.duration" class="ml-2">{{ log.duration }}分钟</span>
              </div>
              <div v-if="log.note" class="text-sm text-gray-400 mt-1">{{ log.note }}</div>
            </div>
            <div class="flex flex-col items-end gap-2 shrink-0">
              <div class="text-sm text-gray-400">
                {{ new Date(log.trainingDate).toLocaleDateString() }}
              </div>
              <el-button
                v-if="log.id"
                type="primary"
                plain
                size="small"
                @click="openAiReviewForLog(log.id)"
              >
                <el-icon class="mr-1"><ChatDotRound /></el-icon>
                AI 点评
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 记录训练弹窗 -->
    <el-dialog v-model="showLogDialog" title="记录训练完成" width="450px" @closed="logDayIndex = null">
      <div v-if="currentExercise" class="mb-4 p-4 bg-gradient-to-r from-blue-50 to-green-50 rounded-lg border border-blue-200">
        <div class="flex items-center gap-2 mb-2">
          <el-icon class="text-blue-500"><Check /></el-icon>
          <div class="font-semibold text-lg">{{ currentExercise.name || currentExercise.exercise }}</div>
        </div>
        <div class="text-sm text-gray-600">
          <span v-if="currentExercise.sets">计划：{{ currentExercise.sets }}组</span>
          <span v-if="currentExercise.reps"> x {{ currentExercise.reps }}次</span>
          <span v-if="currentExercise.duration">，{{ currentExercise.duration }}分钟</span>
        </div>
      </div>
      
      <div class="mb-3 text-sm text-gray-600">
        <el-icon class="text-orange-500"><Sunny /></el-icon>
        请填写您实际完成的训练数据：
      </div>
      
      <el-form :model="logForm" label-width="90px">
        <el-form-item label="组数" required>
          <el-input-number 
            v-model="logForm.sets" 
            :min="1" 
            :max="20" 
            :precision="0"
            style="width: 100%"
          />
          <div class="text-xs text-gray-400 mt-1">完成的组数</div>
        </el-form-item>
        <el-form-item label="次数/时长">
          <el-input-number 
            v-model="logForm.reps" 
            :min="1" 
            :max="100" 
            :precision="0"
            style="width: 100%"
            placeholder="次数"
          />
          <div class="text-xs text-gray-400 mt-1">每组完成的次数（如深蹲、俯卧撑）</div>
        </el-form-item>
        <el-form-item label="重量(kg)">
          <el-input-number 
            v-model="logForm.weight" 
            :min="0" 
            :precision="1"
            style="width: 100%"
            placeholder="0"
          />
          <div class="text-xs text-gray-400 mt-1">使用的重量（如有）</div>
        </el-form-item>
        <el-form-item label="时长(分钟)">
          <el-input-number 
            v-model="logForm.duration" 
            :min="0" 
            :precision="0"
            style="width: 100%"
            placeholder="0"
          />
          <div class="text-xs text-gray-400 mt-1">总训练时长（如平板支撑、有氧运动）</div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="logForm.note" 
            type="textarea" 
            :rows="3" 
            placeholder="记录训练感受、注意事项等（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="showLogDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSaveLog" :loading="actionLoading">
            <el-icon><Check /></el-icon>
            确认完成
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- AI 调整动作弹窗 -->
    <el-dialog v-model="showModifyDialog" title="AI 调整动作" width="500px">
      <div v-if="modifyingExercise" class="mb-4 p-4 bg-gradient-to-r from-purple-50 to-blue-50 rounded-lg border border-purple-200">
        <div class="flex items-center gap-2 mb-1">
          <el-icon class="text-purple-500"><MagicStick /></el-icon>
          <span class="font-semibold text-lg">{{ modifyingExercise.name || modifyingExercise.exercise }}</span>
        </div>
        <div class="text-sm text-gray-600">
          <span v-if="modifyingExercise.sets">{{ modifyingExercise.sets }}组</span>
          <span v-if="modifyingExercise.reps"> x {{ modifyingExercise.reps }}次</span>
          <span v-if="modifyingExercise.duration">，{{ modifyingExercise.duration }}分钟</span>
        </div>
      </div>
      <div class="mb-2 text-sm text-gray-600">请描述您希望如何调整这个动作：</div>
      <el-input
        v-model="modifyRequest"
        type="textarea"
        :rows="3"
        maxlength="200"
        show-word-limit
        placeholder="例如：换成不需要杠铃的、降低强度、换一个类似的动作..."
      />
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="showModifyDialog = false">取消</el-button>
          <el-button type="primary" @click="handleModifyExercise" :loading="modifyLoading">
            <el-icon><MagicStick /></el-icon>
            AI 调整
          </el-button>
        </div>
      </template>
    </el-dialog>

    <AiReviewDialog v-model="showAiReviewDialog" :training-log-id="aiReviewLogId" />
  </div>
</template>

<style scoped>
.day-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 60px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  background: #f3f4f6;
  color: #6b7280;
}

.day-badge.active {
  background: linear-gradient(135deg, #10b981, #06b6d4);
  color: white;
}

.day-badge.completed {
  background: #d1fae5;
  color: #059669;
}

.day-header {
  user-select: none;
}

.day-header:hover {
  background: rgba(0, 0, 0, 0.02);
  border-radius: 8px;
  padding: 4px 8px;
  margin: -4px -8px;
}

/* 日头部标签：与 flex 同排时不被压扁、图标与文字垂直居中 */
:deep(.day-header-tag.el-tag) {
  display: inline-flex;
  align-items: center;
  height: auto;
  min-height: 22px;
  line-height: 1.45;
  padding: 4px 10px;
}

:deep(.day-header-tag .el-tag__content) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  line-height: 1.45;
}

.day-header-tag__icon {
  font-size: 14px;
  flex-shrink: 0;
}

.exercise-item:hover {
  transform: translateX(4px);
}

/* 与「记录完成」按钮视觉同级，避免文字底部被裁切 */
.exercise-completed-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
  color: #fff;
  background: linear-gradient(180deg, #22c55e 0%, #16a34a 100%);
  box-shadow: 0 1px 2px rgb(0 0 0 / 8%);
}

.exercise-completed-badge__icon {
  font-size: 16px;
  flex-shrink: 0;
}

/* 计划冲突对话框样式 */
:deep(.plan-conflict-dialog) {
  min-width: 500px;
}

:deep(.plan-conflict-dialog .el-message-box__message) {
  text-align: left;
}
/* 计划切换器样式 */
.plan-switcher {
  margin-left: auto;
}

.plan-switcher-menu {
  min-width: 280px;
  max-height: 400px;
  overflow-y: auto;
}

.plan-switcher-header {
  padding: 12px 16px;
  font-weight: 600;
  color: #1f2937;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
  font-size: 14px;
}

.plan-switcher-item {
  padding: 8px 0;
}

.plan-switcher-name {
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  font-size: 14px;
}

.plan-switcher-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #6b7280;
}

.plan-switcher-date {
  margin-left: auto;
}

.el-dropdown-menu__item.is-current {
  background: #f0fdf4;
  color: #10b981;
}

.el-dropdown-menu__item.is-current:hover {
  background: #dcfce7;
}
</style>
