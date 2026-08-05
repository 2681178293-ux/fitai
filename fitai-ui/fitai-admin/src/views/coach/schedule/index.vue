<template>
  <div class="table-box p-5">
    <el-row :gutter="16">
      <!-- 左侧：教练选择 -->
      <el-col :span="6">
        <el-card shadow="never" class="schedule-card">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-bold">选择教练</span>
              <el-button link :icon="Refresh" @click="loadCoachOptions">刷新</el-button>
            </div>
          </template>
          <el-select
            v-model="currentCoachId"
            placeholder="请选择教练"
            filterable
            clearable
            style="width: 100%"
            @change="handleCoachChange"
          >
            <el-option v-for="item in coachOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <div class="mt-4 text-gray-400 text-xs leading-6">
            <div>1. 出勤星期：教练每周固定可预约的日期。</div>
            <div>2. 开放时段：留空表示使用系统默认时段。</div>
            <div>3. 请假日期：该日期不可被用户预约。</div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：排期编辑 -->
      <el-col :span="18">
        <el-card shadow="never" class="schedule-card">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-bold">排期设置{{ currentCoachName ? "：" + currentCoachName : "" }}</span>
              <el-button type="primary" :icon="Check" :loading="submitLoading" :disabled="!currentCoachId" @click="handleSave">
                保存排期
              </el-button>
            </div>
          </template>

          <el-empty v-if="!currentCoachId" description="请先在左侧选择教练" />

          <el-form v-else v-loading="loading" label-width="100px">
            <el-form-item label="出勤星期">
              <el-checkbox-group v-model="workDays">
                <el-checkbox v-for="item in weekOptions" :key="item.value" :value="item.value" border class="mr-2 mb-2">
                  {{ item.label }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="开放时段">
              <div class="w-full">
                <div v-for="(slot, index) in workSlots" :key="index" class="flex items-center mb-2">
                  <el-time-select
                    v-model="slot.start"
                    start="06:00"
                    step="00:30"
                    end="23:00"
                    placeholder="开始时间"
                    style="width: 140px"
                  />
                  <span class="ml-2 mr-2 text-gray-400">至</span>
                  <el-time-select
                    v-model="slot.end"
                    start="06:00"
                    step="00:30"
                    end="23:30"
                    :min-time="slot.start"
                    placeholder="结束时间"
                    style="width: 140px"
                  />
                  <el-button class="ml-2" type="danger" link :icon="Delete" @click="handleRemoveSlot(index)">删除</el-button>
                </div>
                <el-button type="primary" link :icon="Plus" @click="handleAddSlot">添加时段</el-button>
                <span v-if="workSlots.length === 0" class="ml-2 text-gray-400 text-xs">未配置时段将使用系统默认时段</span>
              </div>
            </el-form-item>

            <el-form-item label="请假日期">
              <el-date-picker
                v-model="leaveDates"
                type="dates"
                value-format="YYYY-MM-DD"
                placeholder="选择请假日期（可多选）"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="排期预览">
              <div class="text-gray-400 text-xs leading-6">
                <div>出勤星期：{{ previewWorkDays || "未设置" }}</div>
                <div>开放时段：{{ previewWorkSlots || "使用默认时段" }}</div>
                <div>请假日期：{{ previewLeaveDates || "无" }}</div>
              </div>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="coachSchedule">
import { ref, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, Plus, Delete, Check } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import { getCoachListApi, getCoachScheduleApi, updateCoachScheduleApi } from "@/api/modules/coach";

const loading = ref(false);
const submitLoading = ref(false);
const coachOptions = ref([]);
const currentCoachId = ref(null);
const workDays = ref([]);
const workSlots = ref([]);
const leaveDates = ref([]);

const weekOptions = [
  { label: "周一", value: 1 },
  { label: "周二", value: 2 },
  { label: "周三", value: 3 },
  { label: "周四", value: 4 },
  { label: "周五", value: 5 },
  { label: "周六", value: 6 },
  { label: "周日", value: 7 }
];

const currentCoachName = computed(() => {
  const item = coachOptions.value.find(c => c.id === currentCoachId.value);
  return item ? item.name : "";
});

const previewWorkDays = computed(() =>
  [...workDays.value]
    .sort((a, b) => a - b)
    .map(d => weekOptions.find(w => w.value === d)?.label)
    .filter(Boolean)
    .join("、")
);

const previewWorkSlots = computed(() => buildSlotList().join("、"));

const previewLeaveDates = computed(() => (leaveDates.value || []).join("、"));

// 拼装时段字符串数组
function buildSlotList() {
  return (workSlots.value || [])
    .filter(s => s.start && s.end)
    .map(s => s.start + "-" + s.end);
}

// 逗号字符串 -> 数组
function splitCsv(text) {
  if (!text) return [];
  return String(text)
    .split(",")
    .map(s => s.trim())
    .filter(Boolean);
}

// 加载教练下拉
const loadCoachOptions = async () => {
  try {
    const res = await getCoachListApi();
    coachOptions.value = res.data || [];
  } catch (error) {
    console.error("获取教练列表失败:", error);
    coachOptions.value = [];
  }
};

// 重置表单
const resetSchedule = () => {
  workDays.value = [];
  workSlots.value = [];
  leaveDates.value = [];
};

// 加载排期
const loadSchedule = async coachId => {
  if (!coachId) {
    resetSchedule();
    return;
  }
  loading.value = true;
  try {
    const res = await getCoachScheduleApi(coachId);
    const data = res.data || {};
    workDays.value = splitCsv(data.workDays).map(d => Number(d));
    workSlots.value = splitCsv(data.workSlots).map(item => {
      const [start, end] = item.split("-");
      return { start: start || "", end: end || "" };
    });
    leaveDates.value = splitCsv(data.leaveDates).map(d => dayjs(d).format("YYYY-MM-DD"));
  } catch (error) {
    console.error("获取教练排期失败:", error);
    resetSchedule();
  } finally {
    loading.value = false;
  }
};

const handleCoachChange = val => {
  loadSchedule(val);
};

const handleAddSlot = () => {
  workSlots.value.push({ start: "", end: "" });
};

const handleRemoveSlot = index => {
  workSlots.value.splice(index, 1);
};

// 保存
const handleSave = async () => {
  if (!currentCoachId.value) {
    ElMessage.warning("请先选择教练");
    return;
  }
  if (workDays.value.length === 0) {
    ElMessage.warning("请至少选择一个出勤星期");
    return;
  }
  const invalid = (workSlots.value || []).some(s => !s.start || !s.end);
  if (invalid) {
    ElMessage.warning("请补全所有开放时段的起止时间");
    return;
  }
  submitLoading.value = true;
  try {
    const payload = {
      workDays: [...workDays.value].sort((a, b) => a - b).join(","),
      workSlots: buildSlotList().join(","),
      leaveDates: (leaveDates.value || []).map(d => dayjs(d).format("YYYY-MM-DD")).join(",")
    };
    await updateCoachScheduleApi(currentCoachId.value, payload);
    ElMessage.success("排期保存成功");
    loadSchedule(currentCoachId.value);
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  loadCoachOptions();
});
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.schedule-card {
  min-height: 560px;
}
</style>
