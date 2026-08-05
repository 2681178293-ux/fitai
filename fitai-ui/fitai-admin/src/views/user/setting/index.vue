<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #availableEquipment="{ row }">
        <template v-if="(row.availableEquipment || []).length">
          <el-tag v-for="item in row.availableEquipment" :key="item" class="mr-1 mb-1" type="info">{{ item }}</el-tag>
        </template>
        <span v-else>-</span>
      </template>

      <template #weeklyDays="{ row }">
        <span>{{ row.weeklyDays != null ? row.weeklyDays + " 天" : "-" }}</span>
      </template>

      <template #sessionDuration="{ row }">
        <span>{{ row.sessionDuration != null ? row.sessionDuration + " 分钟" : "-" }}</span>
      </template>

      <template #reminderTime="{ row }">
        <span>{{ row.reminderTime || "-" }}</span>
      </template>

      <template #pushEnabled="{ row }">
        <el-tag v-if="row.pushEnabled" type="success">已开启</el-tag>
        <el-tag v-else type="info">已关闭</el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
      </template>
    </vxe-grid>

    <!-- 编辑用户设置 -->
    <el-dialog v-model="dialogVisible" title="编辑用户设置" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="130px">
        <el-form-item label="用户">
          <span>{{ formData.userNickname || "-" }}（ID：{{ formData.userId }}）</span>
        </el-form-item>
        <el-form-item label="可用器材">
          <el-select
            v-model="formData.availableEquipment"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入可用器材"
            style="width: 100%"
          >
            <el-option v-for="item in equipmentOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="每周训练天数" prop="weeklyDays">
          <el-input-number v-model="formData.weeklyDays" :min="0" :max="7" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单次训练时长" prop="sessionDuration">
          <el-input-number v-model="formData.sessionDuration" :min="0" :max="300" :step="5" style="width: 100%" />
          <div class="mt-1 text-xs text-gray-400">单位：分钟</div>
        </el-form-item>
        <el-form-item label="提醒时间">
          <el-time-picker
            v-model="formData.reminderTime"
            format="HH:mm"
            value-format="HH:mm"
            placeholder="选择提醒时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="是否推送">
          <el-switch v-model="formData.pushEnabled" active-text="开启" inactive-text="关闭" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="userSetting">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, Edit } from "@element-plus/icons-vue";
import { getUserSettingPageApi, updateUserSettingApi } from "@/api/modules/user";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const submitLoading = ref(false);

const equipmentOptions = ["哑铃", "杠铃", "壶铃", "弹力带", "瑜伽垫", "跳绳", "单杠", "健身球", "跑步机", "划船机", "无器械"];

const formData = reactive({
  id: null,
  userId: null,
  userNickname: "",
  availableEquipment: [],
  weeklyDays: 3,
  sessionDuration: 60,
  reminderTime: "",
  pushEnabled: true
});

const formRules = {
  weeklyDays: [{ required: true, message: "请输入每周训练天数", trigger: "blur" }],
  sessionDuration: [{ required: true, message: "请输入单次训练时长", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
  toolbarConfig: {
    slots: { buttons: "toolbar_buttons" },
    refresh: true,
    zoom: false,
    export: true,
    custom: true
  },
  exportConfig: {
    types: ["xlsx", "csv", "pdf", "html", "xml", "txt"]
  },
  formConfig: {
    items: [
      { field: "userId", title: "用户ID", span: 8, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      {
        span: 8,
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
  pagerConfig: {
    enabled: true,
    pageSize: 10,
    pageSizes: [10, 20, 50, 100]
  },
  proxyConfig: {
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        try {
          const params = {
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            userId: form.userId ? Number(form.userId) : undefined
          };
          const res = await getUserSettingPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (e) {
          console.error("获取用户设置失败:", e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", title: "序号", width: 60 },
    { field: "userId", title: "用户ID", width: 100 },
    { field: "userNickname", title: "用户昵称", minWidth: 130 },
    { field: "availableEquipment", title: "可用器材", minWidth: 240, slots: { default: "availableEquipment" } },
    { field: "weeklyDays", title: "每周训练天数", width: 120, slots: { default: "weeklyDays" } },
    { field: "sessionDuration", title: "单次时长", width: 110, slots: { default: "sessionDuration" } },
    { field: "reminderTime", title: "提醒时间", width: 110, slots: { default: "reminderTime" } },
    { field: "pushEnabled", title: "是否推送", width: 100, slots: { default: "pushEnabled" } },
    { title: "操作", width: 100, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const handleEdit = row => {
  Object.assign(formData, {
    id: row.id ?? null,
    userId: row.userId ?? null,
    userNickname: row.userNickname || "",
    availableEquipment: Array.isArray(row.availableEquipment) ? [...row.availableEquipment] : [],
    weeklyDays: row.weeklyDays ?? 3,
    sessionDuration: row.sessionDuration ?? 60,
    reminderTime: row.reminderTime || "",
    pushEnabled: !!row.pushEnabled
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  try {
    submitLoading.value = true;
    const payload = {
      availableEquipment: formData.availableEquipment,
      weeklyDays: formData.weeklyDays,
      sessionDuration: formData.sessionDuration,
      reminderTime: formData.reminderTime || null,
      pushEnabled: formData.pushEnabled
    };
    await updateUserSettingApi(formData.userId, payload);
    ElMessage.success("保存成功");
    dialogVisible.value = false;
    handleRefresh();
  } finally {
    submitLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
