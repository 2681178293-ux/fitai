<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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
import { Plus, Refresh, Delete, Bowl, Edit, CircleCheck } from '@element-plus/icons-vue'

const loading = ref(false)
const generating = ref(false)
const currentDate = ref(new Date().toISOString().split('T')[0])
const mealPlan = ref(null)
const nutritionStats = ref(null)
const showAddDialog = ref(false)
const showGenerateDialog = ref(false)
/** AI 预览结果（未落库），采纳时写入今日计划 */
const aiPreview = ref(null)
const showPlanEditDialog = ref(false)
const planEdit = ref(null)
const savingPlan = ref(false)
/** 正在采纳到记录中的餐次 breakfast/lunch/... */
const adoptingMeal = ref(null)

/** 编辑中的记录 id，为空表示新增 */
const editingRecordId = ref(null)
/** 从食物库选择的食物，用于按份量推算营养 */
const selectedFoodForRecord = ref(null)

const recordForm = ref({
  mealType: 'breakfast',
  foodName: '',
  /** 与 diet_category.id 对应，选库时由食物带出 */
  categoryId: null,
  /** 仅用于表单展示，与 categoryId 同步 */
  categoryName: '',
  portion: 100,
  calories: 0,
  protein: 0,
  carbs: 0,
  fat: 0
})

const generateForm = ref({
  goal: 'maintain',
  preferences: [],
  allergies: [],
  vegetarian: false
})

const mealTypes = [
  { value: 'breakfast', label: '早餐', icon: '🌅' },
  { value: 'lunch', label: '午餐', icon: '☀️' },
  { value: 'dinner', label: '晚餐', icon: '🌙' },
  { value: 'snack', label: '加餐', icon: '🍎' }
]

const goalOptions = [
  { value: 'lose_weight', label: '减脂' },
  { value: 'gain_muscle', label: '增肌' },
  { value: 'maintain', label: '维持' }
]

/** 后端饮食计划 JSON 加餐字段为 snacks，与餐次 snack 对齐 */
function mealPlanItems(plan, mealValue) {
  if (!plan) return []
  const key = mealValue === 'snack' ? 'snacks' : mealValue
  return plan[key] || []
}

function mealStorageKey(mealValue) {
  return mealValue === 'snack' ? 'snacks' : mealValue
}

/** 与后端 MealItem 一致，避免 undefined 导致数字框空白 */
function toMealNum(v, def = 0) {
  if (v == null || v === '') return def
  const n = Number(v)
  return Number.isFinite(n) ? n : def
}

/** 食物库每 100g 营养：接口可能为 number / 字符串 / BigDecimal 序列化 */
function toFoodPer100(v) {
  if (v == null || v === '') return 0
  if (typeof v === 'number' && Number.isFinite(v)) return v
  const n = Number(String(v).replace(/,/g, '').trim())
  return Number.isFinite(n) ? n : 0
}

function emptyMealItem() {
  return { foodName: '', portion: '', calories: 0, protein: 0, carbs: 0, fat: 0, _food: null }
}

/** 份量字符串解析为克数，与后端一致 */
function parsePortionGrams(portion) {
  if (portion == null || String(portion).trim() === '') return 100
  const m = String(portion).match(/(\d+(?:\.\d+)?)/)
  return m ? Number(m[1]) : 100
}

function normalizeMealItem(raw) {
  if (!raw || typeof raw !== 'object') return emptyMealItem()
  const base = {
    foodName: raw.foodName ?? '',
    portion: raw.portion != null ? String(raw.portion) : '',
    calories: Math.round(toMealNum(raw.calories, 0)),
    protein: Number(toMealNum(raw.protein, 0).toFixed(1)),
    carbs: Number(toMealNum(raw.carbs, 0).toFixed(1)),
    fat: Number(toMealNum(raw.fat, 0).toFixed(1)),
    _food: null
  }
  if (raw.foodId != null) {
    base.foodId = raw.foodId
  }
  return base
}

/** 食物库 unit（如 100g）回显到计划行份量文本 */
function standardPortionLabel(food) {
  if (!food) return '100g'
  const u = food.unit != null ? String(food.unit).trim() : ''
  return u !== '' ? u : '100g'
}

/** 记录饮食：份量为克数，从 unit 中取首个数字，无则 100 */
function standardPortionGrams(food) {
  if (!food) return 100
  const m = String(food.unit ?? '').match(/(\d+(?:\.\d+)?)/)
  return m ? Number(m[1]) : 100
}

function applyStandardPortionFromFood(row, food) {
  if (!row || !food) return
  row.portion = standardPortionLabel(food)
}

function recalcPlanRowFromFood(row, food) {
  if (!row || !food) return
  const g = parsePortionGrams(row.portion)
  const ratio = g / 100
  const kcal = toFoodPer100(food.calories)
  const p = toFoodPer100(food.protein)
  const c = toFoodPer100(food.carbs)
  const f = toFoodPer100(food.fat)
  row.calories = Math.round(kcal * ratio)
  row.protein = Number((p * ratio).toFixed(1))
  row.carbs = Number((c * ratio).toFixed(1))
  row.fat = Number((f * ratio).toFixed(1))
}

/** 选库后按 id 再拉一次库表，避免下拉项与数据库不一致 */
async function onPlanRowFoodSelect(row, sel) {
  if (!row || !sel || !sel.name) return
  row.foodName = sel.name
  if (sel.id == null) {
    delete row.foodId
    row._food = sel
    applyStandardPortionFromFood(row, sel)
    recalcPlanRowFromFood(row, sel)
    return
  }
  row.foodId = sel.id
  try {
    const food = await getFoodById(sel.id)
    row._food = food
    applyStandardPortionFromFood(row, food)
    recalcPlanRowFromFood(row, food)
  } catch {
    row._food = sel
    applyStandardPortionFromFood(row, sel)
    recalcPlanRowFromFood(row, sel)
  }
}

function clearPlanRowFood(row) {
  if (!row) return
  row._food = null
  delete row.foodId
}

async function onPlanPortionInput(row) {
  if (!row) return
  if (!row._food && row.foodId) {
    try {
      row._food = await getFoodById(row.foodId)
    } catch {
      return
    }
  }
  if (row._food) {
    recalcPlanRowFromFood(row, row._food)
  }
}

function stripMealRowsForSave(rows) {
  if (!Array.isArray(rows)) return []
  return rows.map(({ _food, ...rest }) => {
    const out = {
      foodName: rest.foodName ?? '',
      portion: rest.portion != null ? String(rest.portion) : '',
      calories: Math.round(toMealNum(rest.calories, 0)),
      protein: Number(toMealNum(rest.protein, 0).toFixed(1)),
      carbs: Number(toMealNum(rest.carbs, 0).toFixed(1)),
      fat: Number(toMealNum(rest.fat, 0).toFixed(1))
    }
    if (rest.foodId != null) {
      out.foodId = rest.foodId
    }
    return out
  })
}

async function queryFoodSearch(queryString, cb) {
  const q = (queryString || '').trim()
  try {
    // axios 拦截器已解包 AjaxResult，成功时返回体即为 data（此处为数组）
    const res = await searchFood(q)
    const rows = Array.isArray(res) ? res : []
    const list = rows.map((f) => ({
      value: f.name,
      id: f.id,
      name: f.name,
      calories: f.calories,
      protein: f.protein,
      carbs: f.carbs,
      fat: f.fat,
      categoryId: f.categoryId,
      categoryName: f.categoryName,
      unit: f.unit
    }))
    cb(list)
  } catch {
    cb([])
  }
}

function clearFoodSelect() {
  selectedFoodForRecord.value = null
  recordForm.value.categoryId = null
  recordForm.value.categoryName = ''
}

async function onFoodSelect(item) {
  if (!item || !item.name) return
  recordForm.value.foodName = item.name
  if (item.id == null) {
    selectedFoodForRecord.value = item
    recordForm.value.categoryId = item.categoryId ?? null
    recordForm.value.categoryName = item.categoryName || ''
    recordForm.value.portion = standardPortionGrams(item)
    recalcNutritionFromSelectedFood()
    return
  }
  try {
    const food = await getFoodById(item.id)
    selectedFoodForRecord.value = food
    recordForm.value.categoryId = food?.categoryId ?? null
    recordForm.value.categoryName = food?.categoryName || ''
    recordForm.value.portion = standardPortionGrams(food)
    recalcNutritionFromSelectedFood()
  } catch {
    selectedFoodForRecord.value = item
    recordForm.value.categoryId = item.categoryId ?? null
    recordForm.value.categoryName = item.categoryName || ''
    recordForm.value.portion = standardPortionGrams(item)
    recalcNutritionFromSelectedFood()
  }
}

function recalcNutritionFromSelectedFood() {
  const food = selectedFoodForRecord.value
  if (!food) return
  const g = Number(recordForm.value.portion) || 0
  const ratio = g / 100
  const kcal = toFoodPer100(food.calories)
  const p = toFoodPer100(food.protein)
  const c = toFoodPer100(food.carbs)
  const f = toFoodPer100(food.fat)
  recordForm.value.calories = Math.round(kcal * ratio)
  recordForm.value.protein = Number((p * ratio).toFixed(1))
  recordForm.value.carbs = Number((c * ratio).toFixed(1))
  recordForm.value.fat = Number((f * ratio).toFixed(1))
}

watch(
  () => recordForm.value.portion,
  () => {
    if (selectedFoodForRecord.value) {
      recalcNutritionFromSelectedFood()
    }
  }
)

const loadData = async () => {
  loading.value = true
  try {
    const [planRes, statsRes] = await Promise.all([
      getMealPlan(currentDate.value),
      getDailyNutritionStats(currentDate.value)
    ])
    mealPlan.value = planRes
    nutritionStats.value = statsRes
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

/** 将本餐计划项追加写入今日饮食记录（不覆盖已有记录） */
async function adoptPlanMealToRecords(meal) {
  const plan = mealPlan.value
  if (!plan) return
  const items = mealPlanItems(plan, meal.value)
  if (!items?.length) {
    ElMessage.warning('本餐暂无计划项')
    return
  }
  try {
    await ElMessageBox.confirm(
      `将「${meal.label}」中 ${items.length} 项追加写入「今日饮食记录」（当前日期），是否继续？`,
      '采纳到记录'
    )
  } catch {
    return
  }
  adoptingMeal.value = meal.value
  try {
    for (const item of items) {
      let categoryId
      if (item.foodId != null) {
        try {
          const foodRow = await getFoodById(item.foodId)
          categoryId = foodRow?.categoryId
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
    ElMessage.success(`已写入 ${items.length} 条饮食记录`)
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '写入失败')
  } finally {
    adoptingMeal.value = null
  }
}

/** 仅 AI 预览（占次数，不落库） */
const runPreview = async () => {
  generating.value = true
  try {
    const data = await previewMealPlan({
      planDate: currentDate.value,
      ...generateForm.value
    })
    aiPreview.value = data
    ElMessage.success('已生成预览，请确认后采纳')
  } catch (error) {
    ElMessage.error(error.message || '生成失败')
  } finally {
    generating.value = false
  }
}

/** 将预览写入当日饮食计划 */
const handleAdoptPreview = async () => {
  const p = aiPreview.value
  if (!p) return
  savingPlan.value = true
  try {
    const saved = await saveMealPlan({
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
    mealPlan.value = saved
    showGenerateDialog.value = false
    aiPreview.value = null
    ElMessage.success('已采纳为今日饮食计划')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    savingPlan.value = false
  }
}

function openPlanEdit() {
  const p = mealPlan.value
  if (!p) return
  const mapRows = (arr) => (arr || []).map((x) => normalizeMealItem(x))
  planEdit.value = {
    planDate: currentDate.value,
    targetCalories: p.targetCalories ?? 0,
    targetProtein: p.targetProtein ?? 0,
    targetCarbs: p.targetCarbs ?? 0,
    targetFat: p.targetFat ?? 0,
    breakfast: mapRows(p.breakfast),
    lunch: mapRows(p.lunch),
    dinner: mapRows(p.dinner),
    snacks: mapRows(p.snacks),
    _aiTag: p.aiGenerated === 1
  }
  showPlanEditDialog.value = true
}

function planEditList(mealValue) {
  const k = mealStorageKey(mealValue)
  if (!planEdit.value) return []
  if (!planEdit.value[k]) {
    planEdit.value[k] = []
  }
  return planEdit.value[k]
}

function addMealRow(mealValue) {
  planEditList(mealValue).push(emptyMealItem())
}

function removeMealRow(mealValue, idx) {
  planEditList(mealValue).splice(idx, 1)
}

const savePlanEdit = async () => {
  const e = planEdit.value
  if (!e) return
  savingPlan.value = true
  try {
    const saved = await saveMealPlan({
      planDate: e.planDate,
      targetCalories: e.targetCalories,
      targetProtein: e.targetProtein,
      targetCarbs: e.targetCarbs,
      targetFat: e.targetFat,
      breakfast: stripMealRowsForSave(e.breakfast),
      lunch: stripMealRowsForSave(e.lunch),
      dinner: stripMealRowsForSave(e.dinner),
      snacks: stripMealRowsForSave(e.snacks),
      aiGenerated: e._aiTag ? 1 : 0
    })
    mealPlan.value = saved
    showPlanEditDialog.value = false
    ElMessage.success('计划已保存')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    savingPlan.value = false
  }
}

function onGenerateDialogClosed() {
  aiPreview.value = null
}

function clearAiPreview() {
  aiPreview.value = null
}

function openAddDialog() {
  editingRecordId.value = null
  selectedFoodForRecord.value = null
  resetRecordForm()
  showAddDialog.value = true
}

function openEditRecord(record) {
  editingRecordId.value = record.id
  selectedFoodForRecord.value = null
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
  showAddDialog.value = true
}

const handleSaveRecord = async () => {
  if (!recordForm.value.foodName) {
    ElMessage.warning('请输入食物名称')
    return
  }
  const payload = {
    recordDate: currentDate.value,
    mealType: recordForm.value.mealType,
    foodName: recordForm.value.foodName,
    categoryId: recordForm.value.categoryId != null ? recordForm.value.categoryId : undefined,
    portion: recordForm.value.portion,
    calories: recordForm.value.calories,
    protein: recordForm.value.protein,
    carbs: recordForm.value.carbs,
    fat: recordForm.value.fat
  }
  try {
    if (editingRecordId.value) {
      await updateDietRecord(editingRecordId.value, payload)
      ElMessage.success('更新成功')
    } else {
      await addDietRecord(payload)
      ElMessage.success('记录成功')
    }
    showAddDialog.value = false
    editingRecordId.value = null
    selectedFoodForRecord.value = null
    resetRecordForm()
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}

const handleDeleteRecord = async (record) => {
  try {
    await ElMessageBox.confirm('确定删除这条记录吗？', '提示')
    await deleteDietRecord(record.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const resetRecordForm = () => {
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
}

const getNutritionStatus = (percent) => {
  if (percent < 80) return 'warning'
  if (percent <= 120) return 'success'
  return 'exception'
}

watch(currentDate, () => {
  loadData()
})

onMounted(() => {
  loadData()
})

function onAddDialogClosed() {
  editingRecordId.value = null
  selectedFoodForRecord.value = null
}
</script>

<template>
  <div class="diet-page">
    <div class="page-header flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold flex items-center gap-2">
          <el-icon class="text-green-500"><Bowl /></el-icon>
          饮食管理
        </h1>
        <p class="text-gray-500 mt-1">记录饮食，科学营养</p>
      </div>
      <div class="flex gap-2">
        <el-date-picker
          v-model="currentDate"
          type="date"
          value-format="YYYY-MM-DD"
          :clearable="false"
        />
        <el-button @click="loadData" :icon="Refresh">刷新</el-button>
      </div>
    </div>

    <div
      v-loading="loading"
      class="mt-6 grid grid-cols-1 gap-6 lg:grid-cols-[minmax(0,240px)_minmax(0,1fr)]"
    >
      <div class="nutrition-card bg-white rounded-lg p-4 shadow-sm min-w-0 w-full">
        <h2 class="font-bold mb-4">今日营养摄入</h2>

        <template v-if="nutritionStats">
          <div class="mb-4">
            <div class="flex justify-between text-sm mb-1">
              <span>热量</span>
              <span>{{ nutritionStats.actualCalories || 0 }} / {{ nutritionStats.targetCalories || '-' }} kcal</span>
            </div>
            <el-progress
              :percentage="nutritionStats.caloriesPercent || 0"
              :status="getNutritionStatus(nutritionStats.caloriesPercent)"
            />
          </div>

          <div class="mb-4">
            <div class="flex justify-between text-sm mb-1">
              <span>蛋白质</span>
              <span>{{ (nutritionStats.actualProtein || 0).toFixed(1) }} / {{ nutritionStats.targetProtein || '-' }} g</span>
            </div>
            <el-progress
              :percentage="nutritionStats.proteinPercent || 0"
              :status="getNutritionStatus(nutritionStats.proteinPercent)"
              color="#f59e0b"
            />
          </div>

          <div class="mb-4">
            <div class="flex justify-between text-sm mb-1">
              <span>碳水化合物</span>
              <span>{{ (nutritionStats.actualCarbs || 0).toFixed(1) }} / {{ nutritionStats.targetCarbs || '-' }} g</span>
            </div>
            <el-progress
              :percentage="nutritionStats.carbsPercent || 0"
              :status="getNutritionStatus(nutritionStats.carbsPercent)"
              color="#3b82f6"
            />
          </div>

          <div class="mb-4">
            <div class="flex justify-between text-sm mb-1">
              <span>脂肪</span>
              <span>{{ (nutritionStats.actualFat || 0).toFixed(1) }} / {{ nutritionStats.targetFat || '-' }} g</span>
            </div>
            <el-progress
              :percentage="nutritionStats.fatPercent || 0"
              :status="getNutritionStatus(nutritionStats.fatPercent)"
              color="#ec4899"
            />
          </div>
        </template>

        <el-empty v-else description="暂无目标数据" :image-size="80">
          <el-button type="primary" @click="showGenerateDialog = true"> AI 生成饮食计划 </el-button>
        </el-empty>
      </div>

      <div class="min-w-0">
        <div class="flex gap-2 mb-4">
          <el-button type="primary" @click="openAddDialog" :icon="Plus"> 记录饮食 </el-button>
          <el-button @click="showGenerateDialog = true"> AI 生成计划 </el-button>
        </div>

        <div v-if="mealPlan" class="meal-plan bg-white rounded-lg p-4 shadow-sm mb-4">
          <div class="flex flex-wrap items-center justify-between gap-2 mb-4">
            <h2 class="font-bold flex items-center gap-2">
              <span>今日饮食计划</span>
              <el-tag v-if="mealPlan.aiGenerated" size="small" type="success">AI生成</el-tag>
            </h2>
            <el-button type="primary" plain size="small" :icon="Edit" @click="openPlanEdit"> 编辑计划 </el-button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div v-for="meal in mealTypes" :key="meal.value" class="meal-section">
              <div class="flex flex-wrap items-center justify-between gap-2 mb-2">
                <h3 class="font-medium text-gray-700 m-0">{{ meal.icon }} {{ meal.label }}</h3>
                <el-button
                  v-if="mealPlanItems(mealPlan, meal.value)?.length"
                  size="small"
                  type="success"
                  plain
                  :icon="CircleCheck"
                  :loading="adoptingMeal === meal.value"
                  :disabled="adoptingMeal !== null && adoptingMeal !== meal.value"
                  @click="adoptPlanMealToRecords(meal)"
                >
                  采纳到记录
                </el-button>
              </div>
              <div v-if="mealPlanItems(mealPlan, meal.value)?.length" class="space-y-2">
                <div
                  v-for="(item, idx) in mealPlanItems(mealPlan, meal.value)"
                  :key="idx"
                  class="text-sm p-2 bg-gray-50 rounded"
                >
                  <div class="font-medium">{{ item.foodName }}</div>
                  <div class="text-gray-500 text-xs">{{ item.portion }} · {{ item.calories }}kcal</div>
                </div>
              </div>
              <p v-else class="text-gray-400 text-sm">暂无安排</p>
            </div>
          </div>
        </div>

        <div class="diet-records bg-white rounded-lg p-4 shadow-sm">
          <h2 class="font-bold mb-4">今日饮食记录</h2>

          <template v-if="nutritionStats?.recordsByMeal">
            <div v-for="meal in mealTypes" :key="meal.value" class="mb-4">
              <h3 class="text-sm font-medium text-gray-500 mb-2">{{ meal.icon }} {{ meal.label }}</h3>
              <div v-if="nutritionStats.recordsByMeal[meal.value]?.length" class="space-y-2">
                <div
                  v-for="record in nutritionStats.recordsByMeal[meal.value]"
                  :key="record.id"
                  class="flex justify-between items-center p-3 bg-gray-50 rounded-lg gap-2"
                >
                  <div class="min-w-0 flex-1">
                    <div class="font-medium">{{ record.foodName }}</div>
                    <div class="text-sm text-gray-500">
                      <span v-if="record.categoryName" class="mr-2">{{ record.categoryName }}</span>
                      {{ record.portion }}g · {{ record.calories }}kcal
                    </div>
                  </div>
                  <div class="flex shrink-0">
                    <el-button type="primary" text :icon="Edit" @click="openEditRecord(record)" />
                    <el-button type="danger" text :icon="Delete" @click="handleDeleteRecord(record)" />
                  </div>
                </div>
              </div>
              <p v-else class="text-gray-400 text-sm">暂无记录</p>
            </div>
          </template>
          <el-empty v-else description="今日还没有饮食记录" :image-size="80" />
        </div>
      </div>
    </div>

    <el-dialog
      v-model="showAddDialog"
      :title="editingRecordId ? '编辑饮食记录' : '记录饮食'"
      width="440px"
      @closed="onAddDialogClosed"
    >
      <el-form :model="recordForm" label-position="top">
        <el-form-item label="餐次">
          <el-select v-model="recordForm.mealType" class="w-full">
            <el-option
              v-for="meal in mealTypes"
              :key="meal.value"
              :label="meal.icon + ' ' + meal.label"
              :value="meal.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类（可选）">
          <el-input v-model="recordForm.categoryName" placeholder="从食物库选择时自动带入" disabled />
        </el-form-item>
        <el-form-item label="食物名称（可搜索食物库）">
          <el-autocomplete
            v-model="recordForm.foodName"
            class="w-full"
            :fetch-suggestions="queryFoodSearch"
            placeholder="点击或输入：下拉展示食物库；无数据请先在管理端维护"
            clearable
            value-key="value"
            :trigger-on-focus="true"
            :debounce="300"
            fit-input-width
            @select="onFoodSelect"
            @clear="clearFoodSelect"
          />
        </el-form-item>
        <el-form-item label="份量（克）">
          <el-input-number v-model="recordForm.portion" :min="0" class="w-full" />
        </el-form-item>
        <el-form-item label="热量（kcal）">
          <el-input-number v-model="recordForm.calories" :min="0" class="w-full" />
        </el-form-item>
        <div class="grid grid-cols-3 gap-2">
          <el-form-item label="蛋白质(g)">
            <el-input-number v-model="recordForm.protein" :min="0" :precision="1" size="small" />
          </el-form-item>
          <el-form-item label="碳水(g)">
            <el-input-number v-model="recordForm.carbs" :min="0" :precision="1" size="small" />
          </el-form-item>
          <el-form-item label="脂肪(g)">
            <el-input-number v-model="recordForm.fat" :min="0" :precision="1" size="small" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRecord">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showGenerateDialog"
      title="AI 生成饮食计划"
      width="560px"
      destroy-on-close
      @closed="onGenerateDialogClosed"
    >
      <el-form :model="generateForm" label-position="top">
        <el-form-item label="健身目标">
          <el-radio-group v-model="generateForm.goal">
            <el-radio-button v-for="opt in goalOptions" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="口味偏好">
          <el-select v-model="generateForm.preferences" multiple placeholder="选择口味偏好" class="w-full">
            <el-option label="清淡" value="清淡" />
            <el-option label="重口味" value="重口味" />
            <el-option label="辣" value="辣" />
            <el-option label="甜" value="甜" />
          </el-select>
        </el-form-item>
        <el-form-item label="忌口">
          <el-select v-model="generateForm.allergies" multiple placeholder="选择忌口食材" class="w-full">
            <el-option label="海鲜" value="海鲜" />
            <el-option label="牛奶" value="牛奶" />
            <el-option label="鸡蛋" value="鸡蛋" />
            <el-option label="花生" value="花生" />
            <el-option label="大豆" value="大豆" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="generateForm.vegetarian">素食</el-checkbox>
        </el-form-item>
      </el-form>

      <div v-if="aiPreview" class="mt-4 border border-gray-200 rounded-lg p-3 max-h-64 overflow-y-auto bg-gray-50">
        <p class="text-sm text-gray-600 mb-2">
          预览（未写入）· 目标约 {{ aiPreview.targetCalories }}kcal / 蛋白 {{ aiPreview.targetProtein }}g / 碳水
          {{ aiPreview.targetCarbs }}g / 脂肪 {{ aiPreview.targetFat }}g
        </p>
        <div class="grid grid-cols-1 gap-2 text-sm">
          <div v-for="meal in mealTypes" :key="'pv' + meal.value">
            <span class="font-medium">{{ meal.icon }} {{ meal.label }}</span>
            <div v-if="mealPlanItems(aiPreview, meal.value)?.length" class="ml-2 text-gray-600">
              <div v-for="(it, i) in mealPlanItems(aiPreview, meal.value)" :key="i">
                {{ it.foodName }} · {{ it.portion }} · {{ it.calories }}kcal
              </div>
            </div>
            <span v-else class="text-gray-400 ml-2">无</span>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showGenerateDialog = false">关闭</el-button>
        <el-button v-if="aiPreview" @click="clearAiPreview">清除预览</el-button>
        <el-button type="primary" plain :loading="generating" @click="runPreview"> 生成预览 </el-button>
        <el-button
          type="success"
          :disabled="!aiPreview"
          :loading="savingPlan"
          @click="handleAdoptPreview"
        >
          采纳为今日饮食
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showPlanEditDialog"
      title="编辑今日饮食计划"
      width="min(96vw, 920px)"
      destroy-on-close
      class="diet-plan-edit-dialog"
    >
      <el-alert type="info" :closable="false" show-icon class="diet-plan-edit-tip">
        <template #title>
          <span class="text-xs leading-relaxed text-slate-600">
            目标：参考饮食计划，可自行调整。
          </span>
        </template>
      </el-alert>
      <el-form v-if="planEdit" :model="planEdit" label-position="top">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-3 mb-4">
          <el-form-item label="目标热量(kcal)">
            <el-input-number v-model="planEdit.targetCalories" :min="0" class="w-full" />
          </el-form-item>
          <el-form-item label="蛋白质(g)">
            <el-input-number v-model="planEdit.targetProtein" :min="0" class="w-full" />
          </el-form-item>
          <el-form-item label="碳水(g)">
            <el-input-number v-model="planEdit.targetCarbs" :min="0" class="w-full" />
          </el-form-item>
          <el-form-item label="脂肪(g)">
            <el-input-number v-model="planEdit.targetFat" :min="0" class="w-full" />
          </el-form-item>
        </div>
        <div v-for="meal in mealTypes" :key="'ed' + meal.value" class="mb-6 last:mb-0">
          <div class="flex items-center justify-between mb-2">
            <span class="font-medium">{{ meal.icon }} {{ meal.label }}</span>
            <el-button size="small" type="primary" link @click="addMealRow(meal.value)">添加食物</el-button>
          </div>
          <el-table
            :data="planEditList(meal.value)"
            border
            stripe
            size="small"
            fit
            class="plan-edit-table"
            empty-text="暂无食物，点击「添加食物」"
          >
            <el-table-column label="食物" min-width="120">
              <template #default="{ row }">
                <el-autocomplete
                  v-model="row.foodName"
                  class="plan-food-name"
                  :fetch-suggestions="queryFoodSearch"
                  placeholder="搜库"
                  clearable
                  value-key="value"
                  :trigger-on-focus="true"
                  :debounce="300"
                  fit-input-width
                  @select="(s) => onPlanRowFoodSelect(row, s)"
                  @clear="clearPlanRowFood(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="份量" min-width="88">
              <template #default="{ row }">
                <el-input
                  v-model="row.portion"
                  placeholder="选库带入 unit"
                  @input="onPlanPortionInput(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="热量" min-width="96">
              <template #default="{ row }">
                <el-input-number v-model="row.calories" :min="0" controls-position="right" class="w-full" />
              </template>
            </el-table-column>
            <el-table-column label="蛋白" min-width="88">
              <template #default="{ row }">
                <el-input-number v-model="row.protein" :min="0" :precision="1" controls-position="right" class="w-full" />
              </template>
            </el-table-column>
            <el-table-column label="碳水" min-width="88">
              <template #default="{ row }">
                <el-input-number v-model="row.carbs" :min="0" :precision="1" controls-position="right" class="w-full" />
              </template>
            </el-table-column>
            <el-table-column label="脂肪" min-width="88">
              <template #default="{ row }">
                <el-input-number v-model="row.fat" :min="0" :precision="1" controls-position="right" class="w-full" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="64" align="center">
              <template #default="{ $index }">
                <el-button type="danger" link size="small" @click="removeMealRow(meal.value, $index)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="showPlanEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingPlan" @click="savePlanEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.diet-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.plan-edit-table :deep(.el-input-number) {
  width: 100%;
}

.plan-edit-table :deep(.el-input-number .el-input__inner) {
  text-align: left;
}

.plan-edit-table {
  width: 100%;
}

.plan-edit-table :deep(.el-table__inner-wrapper) {
  width: 100%;
}

.plan-edit-table :deep(.el-table__header-wrapper table),
.plan-edit-table :deep(.el-table__body-wrapper table) {
  width: 100% !important;
}

.plan-food-name {
  width: 100%;
  max-width: 100%;
}

.plan-food-name :deep(.el-input__wrapper) {
  padding-left: 8px;
  padding-right: 8px;
}
</style>

<style>
.diet-plan-edit-dialog.el-dialog {
  max-width: min(96vw, 920px);
}

.diet-plan-edit-tip {
  margin-bottom: 12px;
}

.diet-plan-edit-tip.el-alert--info {
  --el-alert-bg-color: #f0f9ff;
}
</style>
