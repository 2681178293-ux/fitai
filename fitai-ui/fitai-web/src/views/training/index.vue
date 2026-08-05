<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCurrentPlan, generatePlan, getExerciseList, getTrainingStats, diagnosePlans, getPlanList, restorePlan, deletePlan, startPlan } from '@/api/training'
import { Cpu, Trophy, Calendar, TrendCharts, Timer, Aim, Document, RefreshRight, Delete, VideoPlay } from '@element-plus/icons-vue'

const router = useRouter()

// 当前计划（显示用，不一定是进行中的）
const currentPlan = ref(null)
// 用户手动选择的当前计划ID（存储在 localStorage）
const selectedPlanId = ref(null)
const loading = ref(false)
const diagnosing = ref(false)

// 所有计划列表
const allPlans = ref([])
const plansLoading = ref(false)
const showAllPlans = ref(false)

// 训练统计
const stats = ref({
  totalCount: 0,
  monthCount: 0,
  streakDays: 0
})

// 动作列表
const exercises = ref([])

// 生成计划表单
const showGenerateDialog = ref(false)
const generateForm = ref({
  goal: 1,
  experience: 1,
  equipment: [],
  weeklyDays: 3,
  sessionDuration: 45,
  customPrompt: ''
})

const goalOptions = [
  { value: 1, label: '减脂', icon: '🔥', color: '#ef4444' },
  { value: 2, label: '增肌', icon: '💪', color: '#3b82f6' },
  { value: 3, label: '塑形', icon: '✨', color: '#8b5cf6' },
  { value: 4, label: '保持健康', icon: '❤️', color: '#10b981' }
]

const experienceOptions = [
  { value: 1, label: '新手', desc: '刚开始健身' },
  { value: 2, label: '进阶', desc: '健身3-12个月' },
  { value: 3, label: '高级', desc: '健身1年以上' }
]

const equipmentOptions = [
  { value: 'none', label: '徒手', icon: '🤸' },
  { value: 'dumbbell', label: '哑铃', icon: '🏋️' },
  { value: 'barbell', label: '杠铃', icon: '🏋️‍♂️' },
  { value: 'machine', label: '器械', icon: '🎰' },
  { value: 'band', label: '弹力带', icon: '🎗️' }
]

// 加载所有计划列表
const loadAllPlans = async () => {
  plansLoading.value = true
  try {
    const result = await getPlanList({ pageNum: 1, pageSize: 100 })
    console.log('计划列表数据:', result)
    
    // 处理分页数据格式
    if (result && result.records) {
      // MyBatis-Plus 分页格式
      allPlans.value = result.records || []
    } else if (result && result.list) {
      // 其他分页格式
      allPlans.value = result.list || []
    } else if (Array.isArray(result)) {
      // 直接数组格式
      allPlans.value = result
    } else if (result && result.data) {
      // 嵌套数据格式
      allPlans.value = Array.isArray(result.data) ? result.data : (result.data.records || [])
    } else {
      allPlans.value = []
    }
    
    // 按创建时间倒序排列（最新的在前）
    allPlans.value.sort((a, b) => {
      const timeA = new Date(a.createTime || 0).getTime()
      const timeB = new Date(b.createTime || 0).getTime()
      return timeB - timeA
    })
    
    console.log('处理后的计划列表:', allPlans.value.length, '个计划')
  } catch (e) {
    console.error('加载计划列表失败', e)
    ElMessage.error('加载计划列表失败: ' + (e.message || '未知错误'))
    allPlans.value = []
  } finally {
    plansLoading.value = false
  }
}

onMounted(async () => {
  loading.value = true
  
  // 从 localStorage 读取用户选择的计划ID
  const savedPlanId = localStorage.getItem('selectedPlanId')
  if (savedPlanId) {
    selectedPlanId.value = savedPlanId
  }
  
  // 并行加载数据
  try {
    const [planData, exerciseData, statsData] = await Promise.allSettled([
      getCurrentPlan(),
      getExerciseList({ status: 1 }),
      getTrainingStats()
    ])
    
    if (planData.status === 'fulfilled') {
      const plan = planData.value
      // 确保计划有有效的ID
      if (plan && plan.id) {
        // 如果用户之前选择了计划，优先使用选择的计划
        if (selectedPlanId.value) {
          // 从计划列表中查找选择的计划
          await loadAllPlans()
          const selectedPlan = allPlans.value.find(p => String(p.id) === selectedPlanId.value)
          if (selectedPlan) {
            currentPlan.value = selectedPlan
          } else {
            currentPlan.value = plan
            selectedPlanId.value = String(plan.id)
            localStorage.setItem('selectedPlanId', String(plan.id))
          }
        } else {
          currentPlan.value = plan
          selectedPlanId.value = String(plan.id)
          localStorage.setItem('selectedPlanId', String(plan.id))
        }
      } else {
        console.warn('获取到的计划没有ID:', plan)
        currentPlan.value = null
      }
    }
    if (exerciseData.status === 'fulfilled') {
      exercises.value = exerciseData.value || []
    }
    if (statsData.status === 'fulfilled') {
      stats.value = statsData.value || stats.value
    }
    
    // 加载所有计划列表
    await loadAllPlans()
    
    // 如果用户选择了计划，确保显示正确
    if (selectedPlanId.value && allPlans.value.length > 0) {
      const selectedPlan = allPlans.value.find(p => String(p.id) === selectedPlanId.value)
      if (selectedPlan) {
        currentPlan.value = selectedPlan
      }
    }
  } catch (e) {
    console.error('加载数据失败', e)
  }
  
  loading.value = false
})

// 生成 AI 计划
const generating = ref(false)
const generateStep = ref(1)

function openGenerateDialog() {
  generateStep.value = 1
  showGenerateDialog.value = true
}

function nextStep() {
  if (generateStep.value < 3) {
    generateStep.value++
  } else {
    handleGenerate()
  }
}

function prevStep() {
  if (generateStep.value > 1) {
    generateStep.value--
  }
}

async function handleGenerate() {
  // 防止重复提交
  if (generating.value) {
    return
  }
  
  generating.value = true
  const startTime = Date.now()
  try {
    console.log('开始生成计划，参数:', generateForm.value)

    const plan = await generatePlan(generateForm.value)
    const elapsed = Date.now() - startTime

    console.log('计划生成成功:', plan)

    if (!plan || !plan.id) {
      throw new Error('生成失败：返回的计划数据无效')
    }

    currentPlan.value = plan
    showGenerateDialog.value = false
    ElMessage.success(`AI 训练计划生成成功！用时 ${elapsed} ms`)
    
    // 重新加载计划列表
    await loadAllPlans()
    
    // 跳转到计划详情页
    router.push(`/training/plan/${plan.id}`)
  } catch (error) {
    console.error('生成计划失败:', error)
    
    // 显示详细的错误信息
    let errorMessage = '生成失败'
    
    if (error.response) {
      // 后端返回的错误
      const errorData = error.response.data || error.response
      errorMessage = errorData.msg || errorData.message || error.message || '生成失败，请稍后再试'
    } else if (error.message) {
      // 前端错误
      errorMessage = error.message
    }
    
    ElMessage.error({
      message: errorMessage,
      duration: 5000,
      showClose: true
    })
    
    // 如果是网络错误，给出提示
    if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      ElMessage.warning('请求超时，请检查网络连接后重试')
    } else if (error.message?.includes('Network Error')) {
      ElMessage.warning('网络错误，请检查网络连接')
    }
  } finally {
    generating.value = false
  }
}

function goToPlan(id) {
  if (!id) {
    console.warn('计划ID不存在，currentPlan:', currentPlan.value)
    ElMessage.warning('计划ID不存在，请刷新页面或重新生成计划')
    return
  }
  router.push(`/training/plan/${id}`)
}

function goToExerciseList() {
  router.push('/training/exercise')
}

function goToExerciseDetail(id) {
  if (!id) return
  router.push(`/training/exercise/${id}`)
}

// 切换当前计划（仅切换显示，不执行计划，不跳转详情页）
function switchToPlan(plan) {
  if (!plan || !plan.id) {
    ElMessage.warning('计划信息无效')
    return
  }
  
  // 如果已经是当前计划，不做任何操作
  if (selectedPlanId.value === String(plan.id)) {
    return
  }
  
  // 保存选择的计划ID到 localStorage
  selectedPlanId.value = String(plan.id)
  localStorage.setItem('selectedPlanId', String(plan.id))
  
  // 更新当前计划显示
  currentPlan.value = plan
  
  ElMessage.success(`✅ 已切换为当前计划：${plan.name}`)
  
  // 不跳转，停留在当前页面
}

// 开始执行计划（真正开始训练）
async function startPlanExecution(plan) {
  if (!plan || !plan.id) {
    ElMessage.warning('计划信息无效')
    return
  }
  
  // 如果计划已经是进行中状态
  if (plan.status === 1) {
    ElMessage.info('计划已经是进行中状态')
    goToPlan(plan.id)
    return
  }
  
  // 如果计划不是草稿状态
  if (plan.status !== 0) {
    ElMessage.warning('只能开始草稿状态的计划')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要开始执行计划"${plan.name}"吗？开始后将无法同时执行其他计划。`,
      '开始训练计划',
      {
        confirmButtonText: '确定开始',
        cancelButtonText: '取消',
        type: 'success',
        distinguishCancelAndClose: true
      }
    )
    
    // 调用开始计划接口
    await startPlan(plan.id)
    ElMessage.success('✅ 计划已开始！现在可以记录训练了')
    
    // 重新加载数据
    await loadAllPlans()
    const currentPlanData = await getCurrentPlan()
    if (currentPlanData && currentPlanData.id) {
      currentPlan.value = currentPlanData
      selectedPlanId.value = currentPlanData.id
      localStorage.setItem('selectedPlanId', String(currentPlanData.id))
    }
    
    // 跳转到计划详情页
    router.push(`/training/plan/${plan.id}`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('开始计划失败:', error)
      
      // 如果是"已有进行中的计划"错误，提供更友好的提示
      if (error.message && error.message.includes('已有进行中的计划')) {
        ElMessageBox.confirm(
          '当前已有进行中的计划，需要先完成或放弃当前计划才能开始新计划。是否前往当前计划页面？',
          '提示',
          {
            confirmButtonText: '前往当前计划',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          if (currentPlan.value?.id) {
            goToPlan(currentPlan.value.id)
          }
        }).catch(() => {})
      } else {
        ElMessage.error('开始失败: ' + (error.message || '未知错误'))
      }
    }
  }
}

// 诊断计划状态
async function handleDiagnose() {
  diagnosing.value = true
  try {
    const result = await diagnosePlans()
    const data = result
    
    // 构建诊断报告
    let report = `📊 计划状态诊断报告\n\n`
    report += `📈 统计信息:\n`
    report += `  总计划数: ${data.totalPlans}\n`
    report += `  草稿计划: ${data.draftCount}\n`
    report += `  进行中计划: ${data.activeCount}\n`
    report += `  已完成计划: ${data.completedCount}\n`
    report += `  已放弃计划: ${data.abandonedCount}\n\n`
    report += `${data.diagnosis}\n\n`
    
    if (data.activePlans && data.activePlans.length > 0) {
      report += `🔄 进行中的计划:\n`
      data.activePlans.forEach((plan, index) => {
        report += `  ${index + 1}. ${plan.name} (ID: ${plan.id})\n`
        report += `     状态: ${getStatusText(plan.status)}\n`
        report += `     开始日期: ${plan.startDate || '未设置'}\n\n`
      })
    }
    
    if (data.latestDraft) {
      report += `📝 最新草稿计划: ${data.latestDraft.name} (ID: ${data.latestDraft.id})\n\n`
    }
    
    if (data.hasMultipleActivePlans) {
      report += `⚠️ 警告: 发现多个进行中的计划！\n`
      report += `建议操作:\n`
      report += `1. 前往计划详情页\n`
      report += `2. 完成或放弃多余的计划\n`
      report += `3. 只保留一个进行中的计划\n`
    }
    
    // 显示诊断结果
    ElMessageBox.alert(report, '诊断结果', {
      confirmButtonText: '确定',
      type: data.hasMultipleActivePlans ? 'warning' : 'info',
      customClass: 'diagnose-dialog'
    })
    
    console.log('诊断结果:', data)
  } catch (error) {
    console.error('诊断失败:', error)
    ElMessage.error('诊断失败: ' + (error.message || '未知错误'))
  } finally {
    diagnosing.value = false
  }
}

function getStatusText(status) {
  const statusMap = {
    0: '草稿',
    1: '进行中',
    2: '已完成',
    3: '已放弃'
  }
  return statusMap[status] || `未知(${status})`
}

function getStatusColor(status) {
  const colorMap = {
    0: 'info',
    1: 'success',
    2: 'primary',
    3: 'danger'
  }
  return colorMap[status] || 'info'
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

// 恢复已放弃的计划
async function handleRestorePlan(plan) {
  try {
    await ElMessageBox.confirm(
      `确定要恢复计划"${plan.name}"吗？恢复后计划将变为草稿状态，可以重新开始。`,
      '恢复计划',
      {
        confirmButtonText: '确定恢复',
        cancelButtonText: '取消',
        type: 'success',
        distinguishCancelAndClose: true
      }
    )
    
    // 调用恢复接口（将状态从3改为0）
    await restorePlan(plan.id)
    ElMessage.success('✅ 计划已恢复为草稿状态')
    
    // 重新加载计划列表和当前计划
    await loadAllPlans()
    const currentPlanData = await getCurrentPlan()
    if (currentPlanData && currentPlanData.id) {
      currentPlan.value = currentPlanData
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复计划失败:', error)
      ElMessage.error('恢复失败: ' + (error.message || '未知错误'))
    }
  }
}

// 删除计划（所有状态都可以删除）
async function handleDeletePlan(plan) {
  if (!plan || !plan.id) {
    ElMessage.warning('计划信息无效')
    return
  }
  
  try {
    const statusTextMap = {
      0: '草稿',
      1: '进行中',
      2: '已完成',
      3: '已放弃'
    }
    const statusText = statusTextMap[plan.status] || '计划'
    
    await ElMessageBox.confirm(
      `确定要删除${statusText}计划"${plan.name}"吗？删除后将无法恢复。`,
      '删除计划',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error',
        distinguishCancelAndClose: true
      }
    )
    
    // 调用删除接口
    await deletePlan(plan.id)
    ElMessage.success('✅ 计划已删除')
    
    // 重新加载计划列表
    await loadAllPlans()
    
    // 如果删除的是当前计划，清空当前计划并重新获取
    if (currentPlan.value?.id === plan.id) {
      currentPlan.value = null
      selectedPlanId.value = null
      localStorage.removeItem('selectedPlanId')
      
      // 尝试获取新的当前计划
      try {
        const currentPlanData = await getCurrentPlan()
        if (currentPlanData && currentPlanData.id) {
          currentPlan.value = currentPlanData
          selectedPlanId.value = String(currentPlanData.id)
          localStorage.setItem('selectedPlanId', String(currentPlanData.id))
        }
      } catch (e) {
        // 忽略错误，可能没有当前计划
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除计划失败:', error)
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

function getGoalLabel(goal) {
  return goalOptions.find(o => o.value === goal)?.label || '综合健身'
}

function getExpLabel(exp) {
  return experienceOptions.find(o => o.value === exp)?.label || '普通'
}

function toggleEquipment(value) {
  const index = generateForm.value.equipment.indexOf(value)
  if (index > -1) {
    generateForm.value.equipment.splice(index, 1)
  } else {
    generateForm.value.equipment.push(value)
  }
}
</script>

<template>
  <div class="training-page">
    <!-- 顶部横幅 -->
    <div class="hero-banner">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            <el-icon class="ai-icon"><Cpu /></el-icon>
            AI 智能训练
          </h1>
          <p class="hero-desc">让 AI 为你量身定制专属训练计划，科学健身，高效达成目标</p>
          <el-button type="primary" size="large" class="hero-btn" @click="openGenerateDialog">
            <el-icon><Cpu /></el-icon>
            生成专属计划
          </el-button>
        </div>
        <div class="hero-stats">
          <div class="stat-card">
            <el-icon class="stat-icon"><Trophy /></el-icon>
            <div class="stat-value">{{ stats.totalCount }}</div>
            <div class="stat-label">累计训练</div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-icon"><Calendar /></el-icon>
            <div class="stat-value">{{ stats.monthCount }}</div>
            <div class="stat-label">本月训练</div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-icon"><TrendCharts /></el-icon>
            <div class="stat-value">{{ stats.streakDays }}</div>
            <div class="stat-label">连续打卡</div>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <!-- 当前计划 -->
      <section class="section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Aim /></el-icon>
            当前计划
          </h2>
          <el-button 
            type="info" 
            size="small" 
            @click="handleDiagnose"
            :loading="diagnosing"
            style="margin-left: auto;">
            <el-icon><TrendCharts /></el-icon>
            诊断计划状态
          </el-button>
        </div>
        
        <div v-if="loading" class="loading-state">
          <el-icon class="loading-icon is-loading"><Cpu /></el-icon>
          <span>加载中...</span>
        </div>
        
        <div v-else-if="currentPlan && currentPlan.id" class="current-plan-card" @click="goToPlan(currentPlan.id)">
          <div class="plan-header">
            <div class="plan-info">
              <h3 class="plan-name">{{ currentPlan.name }}</h3>
              <div class="plan-meta">
                <el-tag effect="dark" size="small" :color="goalOptions.find(o => o.value === currentPlan.goal)?.color">
                  {{ getGoalLabel(currentPlan.goal) }}
                </el-tag>
                <el-tag size="small" type="info">{{ getExpLabel(currentPlan.level) }}</el-tag>
              </div>
            </div>
            <span v-if="currentPlan.isAiGenerated" class="ai-badge">
              <el-icon><Cpu /></el-icon>
              <span>AI 生成</span>
            </span>
          </div>
          <div class="plan-actions">
            <el-button type="primary" size="large" class="start-btn" @click.stop="goToPlan(currentPlan.id)">
              <el-icon><Timer /></el-icon>
              开始今日训练
            </el-button>
            <el-button size="large" @click.stop="goToPlan(currentPlan.id)">查看详情</el-button>
          </div>
        </div>
        
        <div v-else class="empty-plan">
          <div class="empty-icon">
            <el-icon :size="48"><Cpu /></el-icon>
          </div>
          <h3>还没有训练计划</h3>
          <p>让 AI 根据你的目标和身体状况，智能生成个性化训练方案</p>
          <el-button type="primary" size="large" @click="openGenerateDialog">
            <el-icon><Cpu /></el-icon>
            AI 生成计划
          </el-button>
        </div>
      </section>
      
      <!-- 所有计划列表 -->
      <section class="section" v-if="allPlans.length > 0">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Document /></el-icon>
            所有计划
            <el-tag size="small" type="info" style="margin-left: 8px;">{{ allPlans.length }}</el-tag>
          </h2>
          <el-button 
            text 
            type="primary"
            @click="showAllPlans = !showAllPlans"
          >
            {{ showAllPlans ? '收起' : '展开全部' }}
          </el-button>
        </div>
        
        <div v-if="plansLoading" class="loading-state">
          <el-icon class="loading-icon is-loading"><Cpu /></el-icon>
          <span>加载中...</span>
        </div>
        
        <div v-else class="plans-list">
          <div 
            v-for="plan in (showAllPlans ? allPlans : allPlans.slice(0, 3))" 
            :key="plan.id"
            class="plan-item-card"
            :class="{ 'is-current': selectedPlanId === String(plan.id) }"
          >
            <div class="plan-item-header">
              <div class="plan-item-info">
                <h3 class="plan-item-name">
                  {{ plan.name }}
                  <span 
                    v-if="selectedPlanId === String(plan.id)" 
                    class="current-badge"
                  >
                    <el-icon><Aim /></el-icon>
                    <span>当前</span>
                  </span>
                </h3>
                <div class="plan-item-meta">
                  <el-tag 
                    size="small" 
                    :type="getStatusColor(plan.status)"
                    effect="dark"
                  >
                    {{ getStatusText(plan.status) }}
                  </el-tag>
                  <el-tag 
                    v-if="plan.goal"
                    size="small" 
                    :color="goalOptions.find(o => o.value === plan.goal)?.color"
                    effect="dark"
                  >
                    {{ getGoalLabel(plan.goal) }}
                  </el-tag>
                  <el-tag 
                    v-if="plan.level"
                    size="small" 
                    type="info"
                  >
                    {{ getExpLabel(plan.level) }}
                  </el-tag>
                  <span 
                    v-if="plan.isAiGenerated"
                    class="ai-badge-tag"
                  >
                    <el-icon><Cpu /></el-icon>
                    <span>AI生成</span>
                  </span>
                </div>
              </div>
              <div class="plan-item-date">
                <div class="date-label">创建时间</div>
                <div class="date-value">{{ formatDate(plan.createTime) }}</div>
                <div v-if="plan.startDate" class="date-label" style="margin-top: 4px;">开始日期</div>
                <div v-if="plan.startDate" class="date-value">{{ formatDate(plan.startDate) }}</div>
              </div>
            </div>
            <div class="plan-item-actions">
              <el-button 
                v-if="selectedPlanId !== String(plan.id)"
                type="success" 
                size="small"
                @click.stop="switchToPlan(plan)"
              >
                <el-icon><Aim /></el-icon>
                设为当前
              </el-button>
              <el-button 
                v-if="plan.status === 0"
                type="warning" 
                size="small"
                @click.stop="startPlanExecution(plan)"
              >
                <el-icon><VideoPlay /></el-icon>
                开始计划
              </el-button>
              <el-button 
                v-if="plan.status === 3"
                type="success" 
                size="small"
                @click.stop="handleRestorePlan(plan)"
              >
                <el-icon><RefreshRight /></el-icon>
                恢复
              </el-button>
              <el-button 
                type="danger" 
                size="small"
                @click.stop="handleDeletePlan(plan)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
              <el-button 
                type="primary" 
                size="small"
                @click.stop="goToPlan(plan.id)"
              >
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </section>
      
      <!-- 动作库 -->
      <section class="section">
        <div class="section-header">
          <h2 class="section-title">动作库</h2>
          <el-button text type="primary" @click="goToExerciseList">查看全部</el-button>
        </div>
        
        <div class="exercise-grid">
          <div
            v-for="exercise in exercises.slice(0, 8)"
            :key="exercise.id"
            class="exercise-card cursor-pointer"
            @click="goToExerciseDetail(exercise.id)"
          >
            <div class="exercise-cover">
              <img
                :src="exercise.coverImage || 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=300'"
                :alt="exercise.name"
              />
              <div class="exercise-overlay">
                <el-button type="primary" size="small" circle>
                  <el-icon><VideoPlay /></el-icon>
                </el-button>
              </div>
            </div>
            <div class="exercise-info">
              <h3>{{ exercise.name }}</h3>
              <div class="exercise-tags">
                <el-tag size="small" type="info">{{ exercise.muscleGroupName }}</el-tag>
                <el-tag size="small">{{ exercise.difficultyName }}</el-tag>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
    
    <!-- AI 生成计划对话框 -->
    <el-dialog 
      v-model="showGenerateDialog" 
      :title="null"
      width="560px"
      class="generate-dialog"
      :show-close="false"
    >
      <div class="dialog-header">
        <div class="dialog-icon">
          <el-icon :size="32"><Cpu /></el-icon>
        </div>
        <h2>AI 智能生成训练计划</h2>
        <p>回答几个简单问题，AI 将为你定制专属方案</p>
      </div>

      <!-- 步骤指示器 -->
      <div class="step-indicator">
        <div 
          v-for="i in 3" 
          :key="i" 
          class="step-dot"
          :class="{ active: generateStep >= i, current: generateStep === i }"
        ></div>
      </div>

      <!-- 步骤1: 目标选择 -->
      <div v-show="generateStep === 1" class="step-content">
        <h3 class="step-title">你的健身目标是什么？</h3>
        <div class="goal-options">
          <div 
            v-for="opt in goalOptions" 
            :key="opt.value"
            class="goal-option"
            :class="{ selected: generateForm.goal === opt.value }"
            :style="{ '--goal-color': opt.color }"
            @click="generateForm.goal = opt.value"
          >
            <span class="goal-icon">{{ opt.icon }}</span>
            <span class="goal-label">{{ opt.label }}</span>
          </div>
        </div>
      </div>

      <!-- 步骤2: 经验和器材 -->
      <div v-show="generateStep === 2" class="step-content">
        <h3 class="step-title">你的训练经验？</h3>
        <div class="exp-options">
          <div 
            v-for="opt in experienceOptions" 
            :key="opt.value"
            class="exp-option"
            :class="{ selected: generateForm.experience === opt.value }"
            @click="generateForm.experience = opt.value"
          >
            <span class="exp-label">{{ opt.label }}</span>
            <span class="exp-desc">{{ opt.desc }}</span>
          </div>
        </div>

        <h3 class="step-title" style="margin-top: 24px;">可用器材</h3>
        <div class="equipment-options">
          <div 
            v-for="opt in equipmentOptions" 
            :key="opt.value"
            class="equipment-option"
            :class="{ selected: generateForm.equipment.includes(opt.value) }"
            @click="toggleEquipment(opt.value)"
          >
            <span class="equipment-icon">{{ opt.icon }}</span>
            <span class="equipment-label">{{ opt.label }}</span>
          </div>
        </div>
      </div>

      <!-- 步骤3: 时间安排 -->
      <div v-show="generateStep === 3" class="step-content">
        <h3 class="step-title">每周训练几天？</h3>
        <div class="days-selector">
          <div 
            v-for="i in 7" 
            :key="i"
            class="day-option"
            :class="{ selected: generateForm.weeklyDays === i }"
            @click="generateForm.weeklyDays = i"
          >
            {{ i }}
          </div>
        </div>
        <p class="days-hint">建议初学者每周 3-4 天，进阶者 4-5 天</p>

        <h3 class="step-title" style="margin-top: 24px;">每次训练时长</h3>
        <div class="duration-options">
          <div 
            v-for="d in [30, 45, 60, 90]" 
            :key="d"
            class="duration-option"
            :class="{ selected: generateForm.sessionDuration === d }"
            @click="generateForm.sessionDuration = d"
          >
            {{ d }} 分钟
          </div>
        </div>

        <h3 class="step-title" style="margin-top: 24px;">个性化需求（可选）</h3>
        <el-input
          v-model="generateForm.customPrompt"
          type="textarea"
          :rows="3"
          maxlength="200"
          show-word-limit
          placeholder="例如：今天不想深蹲、膝盖有伤避免跑跳、想多练背部..."
          style="margin-top: 8px;"
        />
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="generateStep > 1" @click="prevStep">上一步</el-button>
          <el-button @click="showGenerateDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            :loading="generating" 
            @click="nextStep"
          >
            {{ generateStep < 3 ? '下一步' : (generating ? 'AI 生成中...' : '生成计划') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.training-page {
  min-height: 100vh;
  background: #f8fafc;
  padding-bottom: 80px;
}

/* 顶部横幅 */
.hero-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 24px;
  color: white;
}

.hero-content {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 40px;
}

.hero-title {
  font-size: 32px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.ai-icon {
  background: rgba(255, 255, 255, 0.2);
  padding: 8px;
  border-radius: 12px;
}

.hero-desc {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 24px;
}

.hero-btn {
  font-size: 16px;
  padding: 12px 32px;
  border-radius: 12px;
}

.hero-stats {
  display: flex;
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  padding: 20px 24px;
  border-radius: 16px;
  text-align: center;
  min-width: 100px;
}

.stat-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
  margin-top: 4px;
}

/* 主内容区 */
.main-content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1f2937;
}

/* 加载状态 */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px;
  color: #6b7280;
}

.loading-icon {
  font-size: 24px;
}

/* 当前计划卡片 */
.current-plan-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s;
}

.current-plan-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 计划列表 */
.plans-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.plan-item-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.plan-item-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #e5e7eb;
}

.plan-item-card.is-current {
  border-color: #10b981;
  background: linear-gradient(135deg, #ffffff 0%, #f0fdf4 100%);
  position: relative;
}

.plan-item-card.is-current::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #10b981 0%, #34d399 100%);
  border-radius: 12px 12px 0 0;
}

.plan-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.plan-item-info {
  flex: 1;
}

.plan-item-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.plan-item-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.plan-item-date {
  text-align: right;
  min-width: 100px;
}

.date-label {
  font-size: 11px;
  color: #6b7280;
  margin-bottom: 2px;
}

.date-value {
  font-size: 12px;
  color: #374151;
  font-weight: 500;
}

.plan-item-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
  flex-wrap: wrap;
}

.plan-item-actions .el-button {
  margin: 0;
}

/* 当前标签样式优化 */
.current-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  margin-left: 8px;
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  color: white;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
  transition: all 0.3s;
}

.current-badge:hover {
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
  transform: translateY(-1px);
}

.current-badge .el-icon {
  font-size: 14px;
}

/* AI生成标签样式优化 */
.ai-badge-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%);
  color: white;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
  transition: all 0.3s;
}

.ai-badge-tag:hover {
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
  transform: translateY(-1px);
}

.ai-badge-tag .el-icon {
  font-size: 14px;
}

/* 当前计划卡片中的AI生成标签 */
.current-plan-card .ai-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%);
  color: white;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.25);
  transition: all 0.3s;
}

.current-plan-card .ai-badge:hover {
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.35);
  transform: translateY(-1px);
}

.current-plan-card .ai-badge .el-icon {
  font-size: 16px;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.plan-name {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.plan-meta {
  display: flex;
  gap: 8px;
}

.ai-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.25);
  transition: all 0.3s;
}

.ai-badge:hover {
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.35);
  transform: translateY(-1px);
}

.ai-badge .el-icon {
  font-size: 16px;
}

.plan-actions {
  display: flex;
  gap: 12px;
}

.start-btn {
  flex: 1;
}

/* 空状态 */
.empty-plan {
  background: white;
  border-radius: 20px;
  padding: 48px 24px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.empty-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
}

.empty-plan h3 {
  font-size: 20px;
  color: #1f2937;
  margin-bottom: 8px;
}

.empty-plan p {
  color: #6b7280;
  margin-bottom: 24px;
  max-width: 300px;
  margin-left: auto;
  margin-right: auto;
}

/* 动作网格 */
.exercise-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

@media (max-width: 768px) {
  .exercise-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .hero-content {
    flex-direction: column;
    text-align: center;
  }
  
  .hero-stats {
    width: 100%;
    justify-content: center;
  }
}

.exercise-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.exercise-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}

.exercise-cover {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
}

.exercise-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.exercise-card:hover .exercise-cover img {
  transform: scale(1.1);
}

.exercise-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.exercise-card:hover .exercise-overlay {
  opacity: 1;
}

.exercise-info {
  padding: 12px;
}

.exercise-info h3 {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.exercise-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

/* 对话框样式 */
.generate-dialog :deep(.el-dialog__header) {
  display: none;
}

.generate-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.dialog-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px 24px;
  text-align: center;
}

.dialog-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.dialog-header h2 {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 8px;
}

.dialog-header p {
  opacity: 0.9;
  font-size: 14px;
}

/* 步骤指示器 */
.step-indicator {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 20px;
}

.step-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #e5e7eb;
  transition: all 0.3s;
}

.step-dot.active {
  background: #667eea;
}

.step-dot.current {
  transform: scale(1.3);
}

/* 步骤内容 */
.step-content {
  padding: 0 24px 24px;
}

.step-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
  text-align: center;
}

/* 目标选项 */
.goal-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.goal-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 2px solid #e5e7eb;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.goal-option:hover {
  border-color: var(--goal-color, #667eea);
}

.goal-option.selected {
  border-color: var(--goal-color, #667eea);
  background: color-mix(in srgb, var(--goal-color, #667eea) 10%, white);
}

.goal-icon {
  font-size: 32px;
}

.goal-label {
  font-weight: 600;
  color: #374151;
}

/* 经验选项 */
.exp-options {
  display: flex;
  gap: 12px;
}

.exp-option {
  flex: 1;
  padding: 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.exp-option:hover,
.exp-option.selected {
  border-color: #667eea;
}

.exp-option.selected {
  background: #f0f0ff;
}

.exp-label {
  display: block;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.exp-desc {
  font-size: 12px;
  color: #6b7280;
}

/* 器材选项 */
.equipment-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.equipment-option {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.equipment-option:hover,
.equipment-option.selected {
  border-color: #667eea;
}

.equipment-option.selected {
  background: #667eea;
  color: white;
}

.equipment-icon {
  font-size: 18px;
}

/* 天数选择器 */
.days-selector {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.day-option {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.day-option:hover,
.day-option.selected {
  border-color: #667eea;
}

.day-option.selected {
  background: #667eea;
  color: white;
}

.days-hint {
  text-align: center;
  font-size: 12px;
  color: #9ca3af;
  margin-top: 12px;
}

/* 时长选项 */
.duration-options {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.duration-option {
  padding: 12px 24px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.duration-option:hover,
.duration-option.selected {
  border-color: #667eea;
}

.duration-option.selected {
  background: #667eea;
  color: white;
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
}
</style>
