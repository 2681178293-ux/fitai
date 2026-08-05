<template>
  <view class="page-root">
  <scroll-view scroll-y class="page">
    <view class="toolbar">
      <picker mode="date" :value="currentDate" @change="onDateChange">
        <view class="date-pill">{{ currentDate }} ▾</view>
      </picker>
      <view class="btn-ghost" @click="loadData">刷新</view>
    </view>

    <view class="card">
      <text class="card-title">今日营养</text>
      <view v-if="nutritionStats" class="stat-rows">
        <view class="stat-row">
          <text>热量</text>
          <text>{{ nutritionStats.actualCalories || 0 }} / {{ nutritionStats.targetCalories ?? '-' }} kcal</text>
        </view>
        <view class="stat-row">
          <text>蛋白质</text>
          <text>{{ num1(nutritionStats.actualProtein) }} / {{ nutritionStats.targetProtein ?? '-' }} g</text>
        </view>
        <view class="stat-row">
          <text>碳水</text>
          <text>{{ num1(nutritionStats.actualCarbs) }} / {{ nutritionStats.targetCarbs ?? '-' }} g</text>
        </view>
        <view class="stat-row">
          <text>脂肪</text>
          <text>{{ num1(nutritionStats.actualFat) }} / {{ nutritionStats.targetFat ?? '-' }} g</text>
        </view>
      </view>
      <view v-else class="empty-hint">暂无目标，可先生成饮食计划</view>
    </view>

    <view class="actions">
      <view class="btn-primary" @click="openAdd">记录饮食</view>
      <view class="btn-outline" @click="openGenerate">AI 生成计划</view>
    </view>

    <view v-if="mealPlan" class="card">
      <view class="card-head">
        <text class="card-title">饮食计划</text>
        <text v-if="mealPlan.aiGenerated" class="tag-ai">AI</text>
      </view>
      <view v-for="meal in mealTypes" :key="meal.value" class="meal-block">
        <view class="meal-block-head">
          <text class="meal-label">{{ meal.icon }} {{ meal.label }}</text>
          <view
            v-if="mealPlanItems(mealPlan, meal.value)?.length"
            class="btn-adopt-mini"
            :class="{ disabled: adoptingMeal && adoptingMeal !== meal.value }"
            @click="adoptPlanMealToRecords(meal)"
          >
            {{ adoptingMeal === meal.value ? '写入中…' : '采纳' }}
          </view>
        </view>
        <view v-if="mealPlanItems(mealPlan, meal.value)?.length" class="meal-items">
          <view v-for="(item, idx) in mealPlanItems(mealPlan, meal.value)" :key="idx" class="meal-item">
            <text class="food-name">{{ item.foodName }}</text>
            <text class="food-meta">{{ item.portion }} · {{ item.calories }}kcal</text>
          </view>
        </view>
        <text v-else class="muted">暂无安排</text>
      </view>
    </view>

    <view class="card">
      <text class="card-title">饮食记录</text>
      <template v-if="nutritionStats?.recordsByMeal">
        <view v-for="meal in mealTypes" :key="'r' + meal.value" class="meal-block">
          <text class="meal-label">{{ meal.icon }} {{ meal.label }}</text>
          <view v-if="nutritionStats.recordsByMeal[meal.value]?.length" class="record-list">
            <view v-for="record in nutritionStats.recordsByMeal[meal.value]" :key="record.id" class="record-row">
              <view class="record-main">
                <text class="food-name">{{ record.foodName }}</text>
                <text class="food-meta">
                  <text v-if="record.categoryName" class="cat-tag">{{ record.categoryName }} · </text>{{ record.portion }}g · {{ record.calories }}kcal
                </text>
              </view>
              <view class="record-ops">
                <text class="link" @click="openEdit(record)">编辑</text>
                <text class="link danger" @click="confirmDelete(record)">删除</text>
              </view>
            </view>
          </view>
          <text v-else class="muted">暂无记录</text>
        </view>
      </template>
      <view v-else class="empty-hint">今日还没有饮食记录</view>
    </view>

    <view class="bottom-space" />
  </scroll-view>

  <!-- 记录弹层（与 scroll-view 同级，保证 fixed 遮罩全屏） -->
  <view v-if="showRecordSheet" class="mask" @click="closeRecordSheet">
    <view class="sheet" @click.stop>
      <view class="sheet-title">{{ editingRecordId ? '编辑饮食记录' : '记录饮食' }}</view>
      <view class="field">
        <text class="label">餐次</text>
        <picker :range="mealLabels" :value="mealPickerIndex" @change="onMealPick">
          <view class="picker-val">{{ mealTypes[mealPickerIndex].icon }} {{ mealTypes[mealPickerIndex].label }}</view>
        </picker>
      </view>
      <view class="field">
        <text class="label">食物名称</text>
        <input v-model="recordForm.foodName" class="input" placeholder="手填或下方选食物库" @input="onFoodNameInput" />
      </view>
      <view v-if="foodSearchList.length" class="food-suggest">
        <view v-for="f in foodSearchList" :key="f.id" class="food-suggest-item" @click="pickFood(f)">
          <text>{{ f.name }}</text>
          <text class="muted">{{ f.calories }}kcal/100g</text>
        </view>
      </view>
      <view class="field">
        <text class="label">份量(g)</text>
        <input v-model="portionStr" class="input" type="digit" @blur="onPortionBlur" />
      </view>
      <view class="field">
        <text class="label">热量(kcal)</text>
        <input v-model.number="recordForm.calories" class="input" type="number" />
      </view>
      <view class="row3">
        <view class="field sm">
          <text class="label">蛋白</text>
          <input v-model.number="recordForm.protein" class="input" type="digit" />
        </view>
        <view class="field sm">
          <text class="label">碳水</text>
          <input v-model.number="recordForm.carbs" class="input" type="digit" />
        </view>
        <view class="field sm">
          <text class="label">脂肪</text>
          <input v-model.number="recordForm.fat" class="input" type="digit" />
        </view>
      </view>
      <view class="sheet-actions">
        <view class="btn-cancel" @click="closeRecordSheet">取消</view>
        <view class="btn-ok" @click="saveRecord">保存</view>
      </view>
    </view>
  </view>

  <view v-if="showGenerateSheet" class="mask" @click="showGenerateSheet = false">
    <view class="sheet sheet-tall" @click.stop>
      <view class="sheet-title">AI 生成饮食计划</view>
      <view class="field">
        <text class="label">目标</text>
        <picker :range="goalLabels" :value="goalIndex" @change="onGoalPick">
          <view class="picker-val">{{ goalLabels[goalIndex] }}</view>
        </picker>
      </view>
      <view class="check-row">
        <text>素食</text>
        <switch :checked="generateForm.vegetarian" @change="onVegetarianChange" />
      </view>
      <scroll-view v-if="aiPreview" scroll-y class="preview-box">
        <text class="preview-meta">
          预览（未保存）· 约 {{ aiPreview.targetCalories }}kcal
        </text>
        <text class="preview-tip">采纳后写入今日计划；修改餐次与份量请在 PC 端「饮食」页使用「编辑今日饮食计划」。</text>
        <view v-for="meal in mealTypes" :key="'pv' + meal.value" class="preview-meal">
          <text class="preview-meal-t">{{ meal.icon }} {{ meal.label }}</text>
          <view v-if="mealPlanItems(aiPreview, meal.value)?.length">
            <text v-for="(it, i) in mealPlanItems(aiPreview, meal.value)" :key="i" class="preview-line">
              {{ it.foodName }} · {{ it.portion }} · {{ it.calories }}kcal
            </text>
          </view>
          <text v-else class="muted">无</text>
        </view>
      </scroll-view>
      <view class="sheet-actions col">
        <view class="btn-ok" :class="{ disabled: generating }" @click="runGenerate">{{ generating ? '生成中…' : '生成预览' }}</view>
        <view
          class="btn-adopt"
          :class="{ disabled: !aiPreview || savingPlan }"
          @click="adoptPreview"
        >
          {{ savingPlan ? '保存中…' : '采纳为今日饮食' }}
        </view>
        <view class="btn-cancel" @click="showGenerateSheet = false">关闭</view>
      </view>
    </view>
  </view>
  </view>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import {
  getMealPlan,
  getDailyNutritionStats,
  previewMealPlan,
  saveMealPlan,
  addDietRecord,
  updateDietRecord,
  deleteDietRecord,
  searchFood,
  getFoodById
} from '@/api/diet'

const currentDate = ref(new Date().toISOString().split('T')[0])
const mealPlan = ref(null)
const nutritionStats = ref(null)
const loading = ref(false)

const showRecordSheet = ref(false)
const showGenerateSheet = ref(false)
const editingRecordId = ref(null)
const selectedFoodForRecord = ref(null)
const foodSearchList = ref([])
let foodSearchTimer = null

const mealTypes = [
  { value: 'breakfast', label: '早餐', icon: '🌅' },
  { value: 'lunch', label: '午餐', icon: '☀️' },
  { value: 'dinner', label: '晚餐', icon: '🌙' },
  { value: 'snack', label: '加餐', icon: '🍎' }
]
const mealLabels = mealTypes.map((m) => m.label + ' ' + m.icon)

const goalOptions = [
  { value: 'lose_weight', label: '减脂' },
  { value: 'gain_muscle', label: '增肌' },
  { value: 'maintain', label: '维持' }
]
const goalLabels = goalOptions.map((g) => g.label)
const goalIndex = ref(2)

const generateForm = ref({
  goal: 'maintain',
  preferences: [],
  allergies: [],
  vegetarian: false
})

const recordForm = ref({
  mealType: 'breakfast',
  foodName: '',
  categoryId: null,
  categoryName: '',
  portion: 100,
  calories: 0,
  protein: 0,
  carbs: 0,
  fat: 0
})

const portionStr = ref('100')

const mealPickerIndex = computed(() => {
  const i = mealTypes.findIndex((m) => m.value === recordForm.value.mealType)
  return i >= 0 ? i : 0
})

const generating = ref(false)
const aiPreview = ref(null)
const savingPlan = ref(false)
const adoptingMeal = ref(null)

function num1(v) {
  if (v == null || v === '') return '0.0'
  return Number(v).toFixed(1)
}

function mealPlanItems(plan, mealValue) {
  if (!plan) return []
  const key = mealValue === 'snack' ? 'snacks' : mealValue
  return plan[key] || []
}

function parsePortionGrams(portion) {
  if (portion == null || String(portion).trim() === '') return 100
  const m = String(portion).match(/(\d+(?:\.\d+)?)/)
  return m ? Number(m[1]) : 100
}

async function adoptPlanMealToRecords(meal) {
  const plan = mealPlan.value
  if (!plan) return
  const items = mealPlanItems(plan, meal.value)
  if (!items?.length) {
    uni.showToast({ title: '本餐暂无计划项', icon: 'none' })
    return
  }
  const ok = await new Promise((resolve) => {
    uni.showModal({
      title: '采纳到记录',
      content: `将「${meal.label}」${items.length} 项追加写入今日饮食记录？`,
      success: (res) => resolve(res.confirm),
      fail: () => resolve(false)
    })
  })
  if (!ok) return
  adoptingMeal.value = meal.value
  try {
    for (const item of items) {
      let categoryId
      if (item.foodId != null) {
        try {
          const res = await getFoodById(item.foodId)
          const row = res.data != null ? res.data : res
          categoryId = row?.categoryId
        } catch {
          categoryId = undefined
        }
      }
      await addDietRecord({
        recordDate: currentDate.value,
        mealType: meal.value,
        foodName: item.foodName,
        categoryId: categoryId != null ? categoryId : undefined,
        portion: parsePortionGrams(item.portion),
        calories: item.calories ?? 0,
        protein: item.protein ?? 0,
        carbs: item.carbs ?? 0,
        fat: item.fat ?? 0
      })
    }
    uni.showToast({ title: `已写入${items.length}条`, icon: 'success' })
    loadData()
  } catch (e) {
    uni.showToast({ title: e.msg || '写入失败', icon: 'none' })
  } finally {
    adoptingMeal.value = null
  }
}

function onDateChange(e) {
  currentDate.value = e.detail.value
}

watch(currentDate, () => {
  loadData()
})

async function loadData() {
  loading.value = true
  uni.showLoading({ title: '加载中' })
  try {
    const [planRes, statsRes] = await Promise.all([
      getMealPlan(currentDate.value),
      getDailyNutritionStats(currentDate.value)
    ])
    mealPlan.value = planRes.data
    nutritionStats.value = statsRes.data
  } catch (e) {
    mealPlan.value = null
    nutritionStats.value = null
  } finally {
    loading.value = false
    uni.hideLoading()
  }
}

function onMealPick(e) {
  const i = Number(e.detail.value)
  recordForm.value.mealType = mealTypes[i].value
}

function onGoalPick(e) {
  goalIndex.value = Number(e.detail.value)
  generateForm.value.goal = goalOptions[goalIndex.value].value
}

function onVegetarianChange(e) {
  generateForm.value.vegetarian = !!e.detail.value
}

function openAdd() {
  editingRecordId.value = null
  selectedFoodForRecord.value = null
  foodSearchList.value = []
  recordForm.value = {
    mealType: 'breakfast',
    foodName: '',
    categoryId: null,
    categoryName: '',
    portion: 100,
    calories: 0,
    protein: 0,
    carbs: 0,
    fat: 0
  }
  portionStr.value = '100'
  showRecordSheet.value = true
}

function openEdit(record) {
  editingRecordId.value = record.id
  selectedFoodForRecord.value = null
  foodSearchList.value = []
  recordForm.value = {
    mealType: record.mealType || 'breakfast',
    foodName: record.foodName || '',
    categoryId: record.categoryId ?? null,
    categoryName: record.categoryName || '',
    portion: Number(record.portion) || 100,
    calories: record.calories ?? 0,
    protein: Number(record.protein) || 0,
    carbs: Number(record.carbs) || 0,
    fat: Number(record.fat) || 0
  }
  portionStr.value = String(recordForm.value.portion)
  showRecordSheet.value = true
}

function closeRecordSheet() {
  showRecordSheet.value = false
  editingRecordId.value = null
  selectedFoodForRecord.value = null
}

function onFoodNameInput() {
  selectedFoodForRecord.value = null
  recordForm.value.categoryId = null
  recordForm.value.categoryName = ''
  const kw = recordForm.value.foodName || ''
  clearTimeout(foodSearchTimer)
  foodSearchTimer = setTimeout(async () => {
    const q = kw.trim()
    if (!q) {
      foodSearchList.value = []
      return
    }
    try {
      const res = await searchFood(q)
      foodSearchList.value = res.data || []
    } catch {
      foodSearchList.value = []
    }
  }, 280)
}

function pickFood(f) {
  selectedFoodForRecord.value = f
  recordForm.value.foodName = f.name
  recordForm.value.categoryId = f.categoryId ?? null
  recordForm.value.categoryName = f.categoryName || ''
  recordForm.value.portion = 100
  portionStr.value = '100'
  foodSearchList.value = []
  recalcFromFood()
}

function onPortionBlur() {
  const n = Number(portionStr.value)
  recordForm.value.portion = Number.isFinite(n) && n >= 0 ? n : 0
  portionStr.value = String(recordForm.value.portion)
  if (selectedFoodForRecord.value) {
    recalcFromFood()
  }
}

function recalcFromFood() {
  const food = selectedFoodForRecord.value
  if (!food) return
  const g = Number(recordForm.value.portion) || 0
  const r = g / 100
  recordForm.value.calories = Math.round((food.calories || 0) * r)
  recordForm.value.protein = Number(((food.protein || 0) * r).toFixed(1))
  recordForm.value.carbs = Number(((food.carbs || 0) * r).toFixed(1))
  recordForm.value.fat = Number(((food.fat || 0) * r).toFixed(1))
}

watch(
  () => recordForm.value.portion,
  (v) => {
    portionStr.value = String(v ?? '')
    if (selectedFoodForRecord.value) {
      recalcFromFood()
    }
  }
)

async function saveRecord() {
  if (!recordForm.value.foodName?.trim()) {
    uni.showToast({ title: '请输入食物名称', icon: 'none' })
    return
  }
  const payload = {
    recordDate: currentDate.value,
    mealType: recordForm.value.mealType,
    foodName: recordForm.value.foodName.trim(),
    categoryId: recordForm.value.categoryId != null ? recordForm.value.categoryId : undefined,
    portion: recordForm.value.portion,
    calories: recordForm.value.calories,
    protein: recordForm.value.protein,
    carbs: recordForm.value.carbs,
    fat: recordForm.value.fat
  }
  try {
    uni.showLoading({ title: '保存中' })
    if (editingRecordId.value) {
      await updateDietRecord(editingRecordId.value, payload)
    } else {
      await addDietRecord(payload)
    }
    uni.showToast({ title: '已保存', icon: 'success' })
    closeRecordSheet()
    loadData()
  } catch (e) {
    uni.showToast({ title: e.msg || '保存失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

function confirmDelete(record) {
  uni.showModal({
    title: '提示',
    content: '确定删除这条记录？',
    success: async (res) => {
      if (!res.confirm) return
      try {
        await deleteDietRecord(record.id)
        uni.showToast({ title: '已删除', icon: 'success' })
        loadData()
      } catch (e) {
        uni.showToast({ title: e.msg || '删除失败', icon: 'none' })
      }
    }
  })
}

function openGenerate() {
  aiPreview.value = null
  showGenerateSheet.value = true
}

async function runGenerate() {
  if (generating.value) return
  generating.value = true
  try {
    uni.showLoading({ title: '生成中' })
    const res = await previewMealPlan({
      planDate: currentDate.value,
      ...generateForm.value
    })
    aiPreview.value = res.data
    uni.showToast({ title: '请查看预览后采纳', icon: 'none' })
  } catch (e) {
    uni.showToast({ title: e.msg || '生成失败', icon: 'none' })
  } finally {
    generating.value = false
    uni.hideLoading()
  }
}

async function adoptPreview() {
  const p = aiPreview.value
  if (!p || savingPlan.value) return
  savingPlan.value = true
  try {
    uni.showLoading({ title: '保存中' })
    await saveMealPlan({
      planDate: currentDate.value,
      targetCalories: p.targetCalories,
      targetProtein: p.targetProtein,
      targetCarbs: p.targetCarbs,
      targetFat: p.targetFat,
      breakfast: p.breakfast || [],
      lunch: p.lunch || [],
      dinner: p.dinner || [],
      snacks: p.snacks || [],
      aiGenerated: 1
    })
    uni.showToast({ title: '已采纳', icon: 'success' })
    showGenerateSheet.value = false
    aiPreview.value = null
    loadData()
  } catch (e) {
    uni.showToast({ title: e.msg || '保存失败', icon: 'none' })
  } finally {
    savingPlan.value = false
    uni.hideLoading()
  }
}

loadData()
</script>

<style scoped lang="scss">
.page-root {
  min-height: 100vh;
  position: relative;
}

.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 12px;
  box-sizing: border-box;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.date-pill {
  background: #fff;
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 14px;
}

.btn-ghost {
  font-size: 14px;
  color: #3b82f6;
  padding: 8px 12px;
}

.card {
  background: #fff;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 12px;
}

.card-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.tag-ai {
  font-size: 11px;
  color: #16a34a;
  background: #dcfce7;
  padding: 2px 8px;
  border-radius: 4px;
}

.stat-rows {
  margin-top: 8px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0f0;
  color: #555;
}

.empty-hint {
  color: #999;
  font-size: 13px;
  padding: 8px 0;
}

.actions {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.btn-primary {
  flex: 1;
  text-align: center;
  background: #22c55e;
  color: #fff;
  padding: 12px;
  border-radius: 8px;
  font-size: 15px;
}

.btn-outline {
  flex: 1;
  text-align: center;
  border: 1px solid #22c55e;
  color: #16a34a;
  padding: 12px;
  border-radius: 8px;
  font-size: 15px;
}

.meal-block {
  margin-bottom: 12px;
}

.meal-block-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
}

.meal-block-head .meal-label {
  margin-bottom: 0;
  flex: 1;
}

.btn-adopt-mini {
  font-size: 12px;
  color: #16a34a;
  padding: 4px 10px;
  border: 1px solid #22c55e;
  border-radius: 6px;
  flex-shrink: 0;
}

.btn-adopt-mini.disabled {
  opacity: 0.5;
  pointer-events: none;
}

.meal-label {
  font-size: 13px;
  color: #666;
  display: block;
  margin-bottom: 6px;
}

.meal-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.meal-item {
  background: #f9fafb;
  padding: 8px 10px;
  border-radius: 6px;
}

.food-name {
  font-size: 14px;
  color: #111;
  display: block;
}

.food-meta {
  font-size: 12px;
  color: #888;
}

.muted {
  color: #999;
  font-size: 12px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f9fafb;
  padding: 10px;
  border-radius: 8px;
}

.record-main {
  flex: 1;
  min-width: 0;
}

.record-ops {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.link {
  font-size: 13px;
  color: #3b82f6;
}

.link.danger {
  color: #ef4444;
}

.bottom-space {
  height: 24px;
}

.mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 999;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.sheet {
  width: 100%;
  max-height: 85vh;
  overflow-y: auto;
  background: #fff;
  border-radius: 12px 12px 0 0;
  padding: 16px;
  box-sizing: border-box;
}

.sheet-tall {
  max-height: 90vh;
}

.preview-box {
  height: 200px;
  margin-top: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
  box-sizing: border-box;
}

.preview-meta {
  font-size: 12px;
  color: #666;
  display: block;
  margin-bottom: 8px;
}

.preview-tip {
  font-size: 11px;
  color: #9ca3af;
  display: block;
  margin-bottom: 8px;
  line-height: 1.4;
}

.preview-meal {
  margin-bottom: 8px;
}

.preview-meal-t {
  font-size: 13px;
  font-weight: 600;
  display: block;
  margin-bottom: 4px;
}

.preview-line {
  display: block;
  font-size: 12px;
  color: #374151;
  margin-left: 8px;
}

.sheet-title {
  font-size: 17px;
  font-weight: bold;
  margin-bottom: 14px;
}

.field {
  margin-bottom: 12px;
}

.field.sm {
  flex: 1;
}

.label {
  font-size: 12px;
  color: #666;
  display: block;
  margin-bottom: 4px;
}

.input {
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px;
  font-size: 14px;
  box-sizing: border-box;
}

.picker-val {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px;
  font-size: 14px;
}

.row3 {
  display: flex;
  gap: 8px;
}

.food-suggest {
  max-height: 160px;
  overflow-y: auto;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 12px;
}

.food-suggest-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #f3f4f6;
  font-size: 14px;
}

.sheet-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.sheet-actions.col {
  flex-direction: column;
}

.btn-cancel {
  flex: 1;
  text-align: center;
  padding: 12px;
  background: #f3f4f6;
  border-radius: 8px;
}

.btn-ok {
  flex: 1;
  text-align: center;
  padding: 12px;
  background: #22c55e;
  color: #fff;
  border-radius: 8px;
}

.btn-ok.disabled {
  opacity: 0.5;
}

.btn-adopt {
  flex: 1;
  text-align: center;
  padding: 12px;
  background: #0ea5e9;
  color: #fff;
  border-radius: 8px;
}

.btn-adopt.disabled {
  opacity: 0.5;
}

.check-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 14px;
}
</style>
