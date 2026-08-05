<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增徽章</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
        <el-button :icon="Tickets" @click="openGrantRecord">发放记录</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #icon="{ row }">
        <el-image
          v-if="row.icon"
          :src="row.icon"
          :preview-src-list="[row.icon]"
          :preview-teleported="true"
          fit="cover"
          class="badge-icon"
        />
        <span v-else class="text-gray-400">-</span>
      </template>

      <template #condition="{ row }">
        <span>{{ row.conditionType || "-" }}</span>
        <span v-if="row.conditionValue != null" class="ml-1 text-gray-500">/ {{ row.conditionValue }}</span>
      </template>

      <template #grantType="{ row }">
        <el-tag v-if="row.grantType === 2" type="warning">手动发放</el-tag>
        <el-tag v-else type="success">自动发放</el-tag>
      </template>

      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "启用" : "停用" }}</el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="success" link :icon="Promotion" @click="openGrant(row)">手动发放</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增 / 编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑徽章' : '新增徽章'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="徽章名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：坚持不懈" />
        </el-form-item>
        <el-form-item label="徽章编码" prop="code">
          <el-input v-model="formData.code" placeholder="如：CHECKIN_7" />
        </el-form-item>
        <el-form-item label="徽章图标">
          <ImageLibrary v-model="formData.icon" folder="badge" upload-action="/file/upload/image" width="120px" height="120px" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="formData.category" placeholder="如：签到 / 训练 / 社交" />
        </el-form-item>
        <el-form-item label="条件类型">
          <el-input v-model="formData.conditionType" placeholder="如：checkin_days / course_count" />
        </el-form-item>
        <el-form-item label="条件值">
          <el-input-number v-model="formData.conditionValue" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="发放方式" prop="grantType">
          <el-radio-group v-model="formData.grantType">
            <el-radio :value="1">自动发放</el-radio>
            <el-radio :value="2">手动发放</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="赠送积分">
          <el-input-number v-model="formData.points" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="徽章说明" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 发放记录 -->
    <el-dialog v-model="grantRecordVisible" title="徽章发放记录" width="900px">
      <vxe-grid ref="grantGrid" v-bind="grantGridOptions">
        <template #isDisplayed="{ row }">
          <el-tag :type="row.isDisplayed === 1 ? 'success' : 'info'">
            {{ row.isDisplayed === 1 ? "已佩戴" : "未佩戴" }}
          </el-tag>
        </template>
      </vxe-grid>
    </el-dialog>

    <!-- 手动发放 -->
    <el-dialog v-model="grantVisible" title="手动发放徽章" width="520px" destroy-on-close>
      <el-form ref="grantFormRef" :model="grantForm" :rules="grantRules" label-width="100px">
        <el-form-item label="徽章">
          <span class="font-medium">{{ grantForm.badgeName || "-" }}</span>
        </el-form-item>
        <el-form-item label="用户ID" prop="userIdsText">
          <el-input
            v-model="grantForm.userIdsText"
            type="textarea"
            :rows="4"
            placeholder="多个用户ID用英文逗号、空格或换行分隔"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="grantVisible = false">取消</el-button>
        <el-button type="primary" :loading="grantLoading" @click="handleGrantSubmit">确定发放</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="memberBadge">
import { ref, reactive, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, Tickets, Promotion } from "@element-plus/icons-vue";
import ImageLibrary from "@/components/ImageLibrary/index.vue";
import {
  getBadgePageApi,
  createBadgeApi,
  updateBadgeApi,
  deleteBadgeApi,
  getBadgeGrantPageApi,
  grantBadgeApi
} from "@/api/modules/member";

const xGrid = ref();
const grantGrid = ref();
const formRef = ref();
const grantFormRef = ref();
const dialogVisible = ref(false);
const grantRecordVisible = ref(false);
const grantVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const grantLoading = ref(false);

const defaultForm = {
  id: null,
  name: "",
  code: "",
  icon: "",
  category: "",
  conditionType: "",
  conditionValue: 0,
  grantType: 1,
  points: 0,
  description: "",
  sortOrder: 0,
  status: 1
};

const formData = reactive({ ...defaultForm });

const formRules = {
  name: [{ required: true, message: "请输入徽章名称", trigger: "blur" }],
  code: [{ required: true, message: "请输入徽章编码", trigger: "blur" }],
  category: [{ required: true, message: "请输入徽章分类", trigger: "blur" }],
  grantType: [{ required: true, message: "请选择发放方式", trigger: "change" }]
};

const grantForm = reactive({
  badgeId: null,
  badgeName: "",
  userIdsText: ""
});

const grantRules = {
  userIdsText: [{ required: true, message: "请输入用户ID", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
  checkboxConfig: { highlight: true },
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
      { field: "name", title: "徽章名称", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      { field: "code", title: "徽章编码", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      { field: "category", title: "分类", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "启用", value: 1 },
            { label: "停用", value: 0 }
          ],
          props: { placeholder: "全部" }
        }
      },
      {
        span: 24,
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
    pageSizes: [10, 20, 50]
  },
  proxyConfig: {
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        try {
          const res = await getBadgePageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            name: form.name || undefined,
            code: form.code || undefined,
            category: form.category || undefined,
            status: form.status !== "" && form.status !== undefined && form.status !== null ? form.status : undefined
          });
          const pageData = res.data || {};
          return { list: pageData.records || [], total: pageData.total || 0 };
        } catch (e) {
          console.error(e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "checkbox", width: 50 },
    { type: "seq", title: "序号", width: 60 },
    { field: "icon", title: "图标", width: 80, slots: { default: "icon" } },
    { field: "name", title: "徽章名称", minWidth: 140 },
    { field: "code", title: "编码", minWidth: 140 },
    { field: "category", title: "分类", width: 110 },
    { field: "conditionType", title: "解锁条件", minWidth: 160, slots: { default: "condition" } },
    { field: "points", title: "赠送积分", width: 100 },
    { field: "grantType", title: "发放方式", width: 110, slots: { default: "grantType" } },
    { field: "sortOrder", title: "排序", width: 70 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 220, fixed: "right", slots: { default: "action" } }
  ]
});

const grantGridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 460,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
  formConfig: {
    items: [
      { field: "userId", title: "用户ID", span: 8, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      { field: "badgeId", title: "徽章ID", span: 8, itemRender: { name: "$input", props: { placeholder: "可选" } } },
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
    pageSizes: [10, 20, 50]
  },
  proxyConfig: {
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        try {
          const res = await getBadgeGrantPageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            userId: form.userId ? Number(form.userId) : undefined,
            badgeId: form.badgeId ? Number(form.badgeId) : undefined
          });
          const pageData = res.data || {};
          return { list: pageData.records || [], total: pageData.total || 0 };
        } catch (e) {
          console.error(e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", title: "序号", width: 60 },
    { field: "userId", title: "用户ID", width: 100 },
    { field: "userNickname", title: "昵称", minWidth: 120 },
    { field: "badgeName", title: "徽章名称", minWidth: 140 },
    { field: "unlockTime", title: "获得时间", minWidth: 160 },
    { field: "grantSource", title: "获得来源", width: 120 },
    { field: "grantBy", title: "操作人", width: 100 },
    { field: "isDisplayed", title: "是否佩戴", width: 100, slots: { default: "isDisplayed" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { ...defaultForm });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    name: row.name || "",
    code: row.code || "",
    icon: row.icon || "",
    category: row.category || "",
    conditionType: row.conditionType || "",
    conditionValue: row.conditionValue ?? 0,
    grantType: row.grantType ?? 1,
    points: row.points ?? 0,
    description: row.description || "",
    sortOrder: row.sortOrder ?? 0,
    status: row.status ?? 1
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      name: formData.name,
      code: formData.code,
      icon: formData.icon,
      category: formData.category,
      conditionType: formData.conditionType,
      conditionValue: formData.conditionValue,
      grantType: formData.grantType,
      points: formData.points,
      description: formData.description,
      sortOrder: formData.sortOrder,
      status: formData.status
    };
    if (isEdit.value) {
      await updateBadgeApi(formData.id, payload);
      ElMessage.success("更新成功");
    } else {
      await createBadgeApi(payload);
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    handleRefresh();
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除徽章「${row.name}」吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteBadgeApi(row.id);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};

const handleBatchDelete = () => {
  const records = xGrid.value?.getCheckboxRecords() || [];
  if (records.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  const ids = records.map(r => r.id).join(",");
  ElMessageBox.confirm(`确定删除选中的 ${records.length} 个徽章吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteBadgeApi(ids);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};

const openGrantRecord = () => {
  grantRecordVisible.value = true;
  nextTick(() => {
    grantGrid.value?.commitProxy("query");
  });
};

const openGrant = row => {
  grantForm.badgeId = row.id;
  grantForm.badgeName = row.name;
  grantForm.userIdsText = "";
  grantVisible.value = true;
};

const parseUserIds = text => {
  if (!text) return [];
  return String(text)
    .split(/[\s,，;；]+/)
    .map(s => s.trim())
    .filter(Boolean)
    .map(Number)
    .filter(n => !Number.isNaN(n));
};

const handleGrantSubmit = async () => {
  try {
    await grantFormRef.value.validate();
    const userIds = parseUserIds(grantForm.userIdsText);
    if (userIds.length === 0) {
      ElMessage.warning("请输入有效的用户ID");
      return;
    }
    grantLoading.value = true;
    await grantBadgeApi({ badgeId: grantForm.badgeId, userIds });
    ElMessage.success(`已向 ${userIds.length} 位用户发放徽章`);
    grantVisible.value = false;
  } finally {
    grantLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.badge-icon {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  cursor: pointer;
}
</style>
