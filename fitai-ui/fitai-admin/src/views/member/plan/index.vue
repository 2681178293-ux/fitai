<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增套餐</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #type="{ row }">
        <el-tag v-if="String(row.type) === '1'" type="warning">月卡</el-tag>
        <el-tag v-else-if="String(row.type) === '2'" type="success">季卡</el-tag>
        <el-tag v-else-if="String(row.type) === '3'" type="danger">年卡</el-tag>
        <el-tag v-else type="info">{{ row.type ?? "-" }}</el-tag>
      </template>

      <template #price="{ row }">
        <span>¥{{ row.price != null ? Number(row.price).toFixed(2) : "-" }}</span>
        <span v-if="row.originalPrice != null" class="ml-2 text-gray-400 line-through text-xs">
          ¥{{ Number(row.originalPrice).toFixed(2) }}
        </span>
      </template>

      <!-- 库表：0上架 1下架 -->
      <template #status="{ row }">
        <el-switch
          v-model="row.status"
          active-value="0"
          inactive-value="1"
          @change="handleStatusChange(row)"
        />
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑会员套餐' : '新增会员套餐'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：月度会员" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio value="1">月卡</el-radio>
            <el-radio value="2">季卡</el-radio>
            <el-radio value="3">年卡</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="有效天数" prop="durationDays">
          <el-input-number v-model="formData.durationDays" :min="1" :max="3650" style="width: 100%" />
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="划线价">
          <el-input-number v-model="formData.originalPrice" :min="0" :precision="2" style="width: 100%" placeholder="可选" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="套餐说明" />
        </el-form-item>
        <el-form-item label="权益列表">
          <el-input
            v-model="benefitsText"
            type="textarea"
            :rows="5"
            placeholder="每行一条权益，将保存为列表展示在用户端"
          />
        </el-form-item>
        <el-form-item label="每月加赠 AI tokens">
          <el-input-number v-model="formData.aiTokenQuotaMonthly" :min="0" :step="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio value="0">上架</el-radio>
            <el-radio value="1">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="memberPlan">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getMemberPlansApi,
  createMemberPlanApi,
  updateMemberPlanApi,
  deleteMemberPlanApi
} from "@/api/modules/member";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const benefitsText = ref("");

const formData = reactive({
  id: null,
  name: "",
  type: "1",
  durationDays: 30,
  price: null,
  originalPrice: null,
  description: "",
  aiTokenQuotaMonthly: 0,
  sortOrder: 0,
  status: "0"
});

const formRules = {
  name: [{ required: true, message: "请输入套餐名称", trigger: "blur" }],
  type: [{ required: true, message: "请选择类型", trigger: "change" }],
  durationDays: [{ required: true, message: "请输入有效天数", trigger: "blur" }],
  price: [{ required: true, message: "请输入售价", trigger: "blur" }]
};

function parseBenefits(text) {
  if (!text || !String(text).trim()) return [];
  return String(text)
    .split(/\r?\n/)
    .map(s => s.trim())
    .filter(Boolean);
}

function benefitsToText(val) {
  if (!val) return "";
  if (Array.isArray(val)) return val.join("\n");
  if (typeof val === "string") {
    try {
      const parsed = JSON.parse(val);
      if (Array.isArray(parsed)) return parsed.join("\n");
    } catch {
      // 非 JSON 时按原文展示
    }
    return val;
  }
  return "";
}

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
  pagerConfig: { enabled: false },
  proxyConfig: {
    seq: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async () => {
        try {
          const res = await getMemberPlansApi();
          const list = Array.isArray(res?.data) ? res.data : [];
          return { list, total: list.length };
        } catch (e) {
          console.error(e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", title: "序号", width: 60 },
    { field: "name", title: "套餐名称", minWidth: 140 },
    { field: "type", title: "类型", width: 90, slots: { default: "type" } },
    { field: "durationDays", title: "天数", width: 70 },
    { field: "price", title: "价格", width: 160, slots: { default: "price" } },
    { field: "aiTokenQuotaMonthly", title: "月赠 tokens", width: 110 },
    { field: "sortOrder", title: "排序", width: 70 },
    { field: "status", title: "上架", width: 90, slots: { default: "status" } },
    { title: "操作", width: 140, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const handleStatusChange = async row => {
  const prev = row.status === "0" ? "1" : "0";
  try {
    await updateMemberPlanApi({ id: row.id, status: String(row.status) });
    ElMessage.success(row.status === "0" ? "已上架" : "已下架");
  } catch {
    row.status = prev;
  }
};

const handleAdd = () => {
  isEdit.value = false;
  benefitsText.value = "";
  Object.assign(formData, {
    id: null,
    name: "",
    type: "1",
    durationDays: 30,
    price: null,
    originalPrice: null,
    description: "",
    aiTokenQuotaMonthly: 0,
    sortOrder: 0,
    status: "0"
  });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  benefitsText.value = benefitsToText(row.benefits);
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    type: row.type != null ? String(row.type) : "1",
    durationDays: row.durationDays ?? 30,
    price: row.price != null ? Number(row.price) : null,
    originalPrice: row.originalPrice != null ? Number(row.originalPrice) : null,
    description: row.description || "",
    aiTokenQuotaMonthly: row.aiTokenQuotaMonthly ?? 0,
    sortOrder: row.sortOrder ?? 0,
    status: row.status != null ? String(row.status) : "0"
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      name: formData.name,
      type: String(formData.type),
      durationDays: formData.durationDays,
      price: formData.price,
      originalPrice: formData.originalPrice,
      description: formData.description,
      benefits: JSON.stringify(parseBenefits(benefitsText.value)),
      aiTokenQuotaMonthly: formData.aiTokenQuotaMonthly,
      sortOrder: formData.sortOrder,
      status: String(formData.status)
    };
    if (isEdit.value) {
      await updateMemberPlanApi({ ...payload, id: formData.id });
      ElMessage.success("更新成功");
    } else {
      await createMemberPlanApi(payload);
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    handleRefresh();
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除套餐「${row.name}」吗？`, "提示", {
    type: "warning"
  })
    .then(async () => {
      await deleteMemberPlanApi(row.id);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};
</script>
