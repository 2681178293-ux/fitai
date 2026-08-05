<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增挑战赛</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #cover="{ row }">
        <div v-if="row.coverImage" class="table-thumb-cell">
          <el-image :src="row.coverImage" :preview-src-list="[row.coverImage]" class="table-thumb-square" />
        </div>
        <span v-else>-</span>
      </template>
      <template #type="{ row }">
        <el-tag size="small">{{ typeMap[row.type] || row.type || '-' }}</el-tag>
      </template>
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上线' : '下线' }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button v-if="row.status !== 1" type="success" link @click="handleStatus(row, 1)">上线</el-button>
        <el-button v-else type="warning" link @click="handleStatus(row, 0)">下线</el-button>
        <el-button type="info" link :icon="Tickets" @click="handleRecords(row)">参与记录</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑挑战赛' : '新增挑战赛'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="挑战名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入挑战名称" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="formData.coverImage" placeholder="封面图 URL（选填）" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="formData.type" placeholder="请选择" style="width: 100%">
            <el-option v-for="(label, val) in typeMap" :key="val" :label="label" :value="val" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标天数">
          <el-input-number v-model="formData.targetDays" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="目标值">
          <el-input-number v-model="formData.targetValue" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" active-text="上线" inactive-text="下线" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 参与记录弹窗 -->
    <el-dialog v-model="recordVisible" :title="`参与记录 - ${recordChallenge.name || ''}`" width="900px">
      <el-table v-loading="recordLoading" :data="recordList" border max-height="460">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="currentDays" label="完成天数" width="100" />
        <el-table-column prop="currentValue" label="完成值" width="100" />
        <el-table-column label="是否完成" width="100">
          <template #default="{ row }">
            <el-tag :type="row.completed === 1 ? 'success' : 'info'" size="small">
              {{ row.completed === 1 ? '已完成' : '进行中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="作弊标记" width="100">
          <template #default="{ row }">
            <el-tag :type="row.cheatFlag === 1 ? 'danger' : 'success'" size="small">
              {{ row.cheatFlag === 1 ? '作弊' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="参与时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAdjustScore(row)">成绩调整</el-button>
            <el-button v-if="row.cheatFlag !== 1" type="danger" link @click="handleMarkCheat(row, 1)">标记作弊</el-button>
            <el-button v-else type="success" link @click="handleMarkCheat(row, 0)">取消作弊</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="flex justify-end mt-3">
        <el-pagination
          v-model:current-page="recordPage.pageNum"
          v-model:page-size="recordPage.pageSize"
          :total="recordPage.total"
          layout="total, prev, pager, next"
          @current-change="loadRecords"
        />
      </div>
    </el-dialog>

    <!-- 成绩调整弹窗 -->
    <el-dialog v-model="scoreVisible" title="成绩调整" width="420px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="完成天数">
          <el-input-number v-model="scoreForm.currentDays" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="完成值">
          <el-input-number v-model="scoreForm.currentValue" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scoreVisible = false">取消</el-button>
        <el-button type="primary" :loading="scoreLoading" @click="handleScoreSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="marketingChallenge">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, Tickets } from "@element-plus/icons-vue";
import {
  getChallengePageApi,
  getChallengeDetailApi,
  addChallengeApi,
  updateChallengeApi,
  deleteChallengeApi,
  changeChallengeStatusApi,
  getChallengeRecordPageApi,
  markChallengeCheatApi,
  adjustChallengeScoreApi
} from "@/api/modules/mall-ext";

const typeMap = {
  plank: "平板支撑",
  pushup: "俯卧撑",
  running: "跑步",
  checkin: "打卡"
};

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const formData = reactive({
  id: null,
  name: "",
  description: "",
  coverImage: "",
  type: "",
  targetDays: 0,
  targetValue: 0,
  startTime: "",
  endTime: "",
  status: 0
});

const formRules = {
  name: [{ required: true, message: "请输入挑战名称", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  pagerConfig: { enabled: true, pageSize: 10, pageSizes: [10, 20, 50, 100] },
  formConfig: {
    items: [
      { field: "name", title: "挑战名称", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入名称" } } },
      {
        field: "type",
        title: "类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "平板支撑", value: "plank" },
            { label: "俯卧撑", value: "pushup" },
            { label: "跑步", value: "running" },
            { label: "打卡", value: "checkin" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "上线", value: 1 },
            { label: "下线", value: 0 }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        span: 6,
        align: "center",
        itemRender: {
          name: "$buttons",
          children: [
            { props: { type: "submit", content: "搜索", status: "primary" } },
            { props: { type: "reset", content: "重置" } }
          ]
        }
      }
    ]
  },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getChallengePageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "coverImage", title: "封面", width: 80, slots: { default: "cover" } },
    { field: "name", title: "挑战名称", minWidth: 160 },
    { field: "type", title: "类型", width: 100, slots: { default: "type" } },
    { field: "targetDays", title: "目标天数", width: 90 },
    { field: "targetValue", title: "目标值", width: 90 },
    { field: "participantCount", title: "参与人数", width: 90 },
    { field: "startTime", title: "开始时间", width: 170 },
    { field: "endTime", title: "结束时间", width: 170 },
    { field: "status", title: "状态", width: 80, slots: { default: "status" } },
    { title: "操作", width: 300, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: null,
    name: "",
    description: "",
    coverImage: "",
    type: "",
    targetDays: 0,
    targetValue: 0,
    startTime: "",
    endTime: "",
    status: 0
  });
  dialogVisible.value = true;
};

const handleEdit = async row => {
  isEdit.value = true;
  let full = row;
  try {
    const res = await getChallengeDetailApi(row.id);
    full = res.data || row;
  } catch (e) {
    /* 用行数据兜底 */
  }
  Object.assign(formData, {
    id: full.id,
    name: full.name,
    description: full.description || "",
    coverImage: full.coverImage || "",
    type: full.type || "",
    targetDays: full.targetDays ?? 0,
    targetValue: full.targetValue ?? 0,
    startTime: full.startTime || "",
    endTime: full.endTime || "",
    status: full.status ?? 0
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = { ...formData };
    if (isEdit.value) {
      await updateChallengeApi(formData.id, payload);
    } else {
      delete payload.id;
      await addChallengeApi(payload);
    }
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) {
      /* 校验失败或拦截器已提示 */
    }
  } finally {
    submitLoading.value = false;
  }
};

const handleStatus = (row, status) => {
  ElMessageBox.confirm(`确定将挑战赛「${row.name}」${status === 1 ? '上线' : '下线'}？`, "提示", { type: "warning" }).then(
    async () => {
      try {
        await changeChallengeStatusApi(row.id, status);
        ElMessage.success(status === 1 ? "已上线" : "已下线");
        handleRefresh();
      } catch (e) {
        /* 拦截器已提示 */
      }
    }
  );
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除挑战赛「${row.name}」？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteChallengeApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      /* 拦截器已提示 */
    }
  });
};

/* ============ 参与记录 ============ */
const recordVisible = ref(false);
const recordLoading = ref(false);
const recordList = ref([]);
const recordChallenge = ref({});
const recordPage = reactive({ pageNum: 1, pageSize: 10, total: 0 });

const loadRecords = async () => {
  recordLoading.value = true;
  try {
    const res = await getChallengeRecordPageApi({
      challengeId: recordChallenge.value.id,
      pageNum: recordPage.pageNum,
      pageSize: recordPage.pageSize
    });
    const data = res.data || {};
    recordList.value = data.records || [];
    recordPage.total = data.total || 0;
  } catch (e) {
    recordList.value = [];
    recordPage.total = 0;
  } finally {
    recordLoading.value = false;
  }
};

const handleRecords = row => {
  recordChallenge.value = row;
  recordPage.pageNum = 1;
  recordVisible.value = true;
  loadRecords();
};

const handleMarkCheat = (row, cheatFlag) => {
  ElMessageBox.confirm(`确定${cheatFlag === 1 ? '标记作弊' : '取消作弊'}？`, "提示", { type: "warning" }).then(async () => {
    try {
      await markChallengeCheatApi(row.id, cheatFlag);
      ElMessage.success("操作成功");
      loadRecords();
    } catch (e) {
      /* 拦截器已提示 */
    }
  });
};

/* ============ 成绩调整 ============ */
const scoreVisible = ref(false);
const scoreLoading = ref(false);
const scoreForm = reactive({ recordId: null, currentDays: 0, currentValue: 0 });

const handleAdjustScore = row => {
  Object.assign(scoreForm, {
    recordId: row.id,
    currentDays: row.currentDays ?? 0,
    currentValue: row.currentValue ?? 0
  });
  scoreVisible.value = true;
};

const handleScoreSubmit = async () => {
  scoreLoading.value = true;
  try {
    await adjustChallengeScoreApi({
      recordId: scoreForm.recordId,
      currentDays: scoreForm.currentDays,
      currentValue: scoreForm.currentValue
    });
    ElMessage.success("成绩已调整");
    scoreVisible.value = false;
    loadRecords();
  } catch (e) {
    /* 拦截器已提示 */
  } finally {
    scoreLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
