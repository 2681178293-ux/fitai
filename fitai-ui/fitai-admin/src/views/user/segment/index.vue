<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增分群</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #userCount="{ row }">
        <el-tag type="primary">{{ row.userCount ?? 0 }}</el-tag>
      </template>

      <template #status="{ row }">
        <el-tag v-if="row.status == 0" type="success">启用</el-tag>
        <el-tag v-else type="info">停用</el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="User" @click="handleViewUser(row)">查看用户</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑分群 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分群' : '新增分群'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="分群名称" prop="segmentName">
          <el-input v-model="formData.segmentName" placeholder="如：高活跃减脂用户" />
        </el-form-item>
        <el-form-item label="分群编码" prop="segmentCode">
          <el-input v-model="formData.segmentCode" placeholder="如：high_active_fat_loss" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="规则JSON">
          <el-input
            v-model="formData.ruleJson"
            type="textarea"
            :rows="6"
            placeholder='JSON 格式，如：{"vipLevel":[1,2],"minDays":7}'
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio value="0">启用</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分群用户抽屉 -->
    <el-drawer v-model="userDrawerVisible" :title="`分群用户 - ${currentSegment.segmentName || ''}`" size="900px" destroy-on-close>
      <div class="mb-4 flex flex-wrap items-center gap-3">
        <el-input v-model="joinUserIds" placeholder="输入用户ID，多个用英文逗号分隔" style="width: 320px" clearable />
        <el-button type="primary" :icon="Plus" :loading="joinLoading" @click="handleJoinUser">批量加入</el-button>
        <el-button type="danger" :icon="Delete" @click="handleRemoveUser">批量移出</el-button>
        <el-button :icon="Refresh" @click="loadSegmentUser">刷新</el-button>
      </div>
      <el-table
        ref="userTableRef"
        v-loading="userLoading"
        :data="userList"
        border
        height="560"
        row-key="segmentRelId"
      >
        <el-table-column type="selection" width="50" reserve-selection />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.avatar || defaultAvatar" />
          </template>
        </el-table-column>
        <el-table-column prop="customerId" label="用户ID" width="110" />
        <el-table-column prop="nickname" label="昵称" min-width="140" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="加入时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <div class="mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="userPage.pageNum"
          v-model:page-size="userPage.pageSize"
          :total="userPage.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadSegmentUser"
          @current-change="loadSegmentUser"
        />
      </div>
    </el-drawer>
  </div>
</template>

<script setup name="userSegment">
import { ref, reactive } from "vue";
import dayjs from "dayjs";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, User } from "@element-plus/icons-vue";
import {
  getUserSegmentPageApi,
  createUserSegmentApi,
  updateUserSegmentApi,
  deleteUserSegmentApi,
  getUserSegmentUserPageApi,
  addUserSegmentUserApi,
  removeUserSegmentUserApi
} from "@/api/modules/user";

const defaultAvatar = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

const xGrid = ref();
const formRef = ref();
const userTableRef = ref();
const dialogVisible = ref(false);
const userDrawerVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const joinLoading = ref(false);
const userLoading = ref(false);
const joinUserIds = ref("");
const userList = ref([]);
const currentSegment = ref({});

const userPage = reactive({ pageNum: 1, pageSize: 10, total: 0 });

const formData = reactive({
  segmentId: null,
  segmentName: "",
  segmentCode: "",
  ruleJson: "",
  status: "0"
});

const formRules = {
  segmentName: [{ required: true, message: "请输入分群名称", trigger: "blur" }],
  segmentCode: [
    { required: true, message: "请输入分群编码", trigger: "blur" },
    { pattern: /^[A-Za-z0-9_-]+$/, message: "编码仅支持字母、数字、下划线和中划线", trigger: "blur" }
  ]
};

const formatTime = val => (val ? dayjs(val).format("YYYY-MM-DD HH:mm:ss") : "-");

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "segmentId", isHover: true },
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
      { field: "segmentName", title: "分群名称", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      { field: "segmentCode", title: "分群编码", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "启用", value: "0" },
            { label: "停用", value: "1" }
          ],
          props: { placeholder: "全部" }
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
            segmentName: form.segmentName || undefined,
            segmentCode: form.segmentCode || undefined,
            status: form.status !== "" && form.status !== undefined && form.status !== null ? form.status : undefined
          };
          const res = await getUserSegmentPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (e) {
          console.error("获取分群列表失败:", e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "checkbox", width: 50 },
    { type: "seq", title: "序号", width: 60 },
    { field: "segmentName", title: "分群名称", minWidth: 160 },
    { field: "segmentCode", title: "分群编码", minWidth: 160 },
    { field: "ruleJson", title: "规则JSON", minWidth: 200 },
    { field: "userCount", title: "用户数", width: 100, slots: { default: "userCount" } },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 240, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const resetForm = () => {
  Object.assign(formData, {
    segmentId: null,
    segmentName: "",
    segmentCode: "",
    ruleJson: "",
    status: "0"
  });
};

const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    segmentId: row.segmentId,
    segmentName: row.segmentName || "",
    segmentCode: row.segmentCode || "",
    ruleJson: row.ruleJson || "",
    status: row.status != null ? String(row.status) : "0"
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  if (formData.ruleJson && String(formData.ruleJson).trim()) {
    try {
      JSON.parse(formData.ruleJson);
    } catch {
      ElMessage.error("规则JSON 格式不正确，请检查");
      return;
    }
  }
  try {
    submitLoading.value = true;
    const payload = {
      segmentName: formData.segmentName,
      segmentCode: formData.segmentCode,
      ruleJson: formData.ruleJson,
      status: formData.status
    };
    if (isEdit.value) {
      await updateUserSegmentApi({ ...payload, segmentId: formData.segmentId });
      ElMessage.success("更新成功");
    } else {
      await createUserSegmentApi(payload);
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    handleRefresh();
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除分群「${row.segmentName}」吗？删除后关联用户关系一并移除。`, "提示", { type: "warning" })
    .then(async () => {
      await deleteUserSegmentApi(row.segmentId);
      ElMessage.success("删除成功");
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
  ElMessageBox.confirm(`确定删除选中的 ${records.length} 个分群吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteUserSegmentApi(records.map(r => r.segmentId).join(","));
      ElMessage.success("批量删除成功");
      handleRefresh();
    })
    .catch(() => {});
};

const loadSegmentUser = async () => {
  if (!currentSegment.value.segmentId) return;
  try {
    userLoading.value = true;
    const res = await getUserSegmentUserPageApi(currentSegment.value.segmentId, {
      pageNum: userPage.pageNum,
      pageSize: userPage.pageSize
    });
    userList.value = res.data?.records || [];
    userPage.total = res.data?.total || 0;
  } catch (e) {
    console.error("获取分群用户失败:", e);
    userList.value = [];
    userPage.total = 0;
  } finally {
    userLoading.value = false;
  }
};

const handleViewUser = row => {
  currentSegment.value = row;
  joinUserIds.value = "";
  userPage.pageNum = 1;
  userPage.total = 0;
  userList.value = [];
  userDrawerVisible.value = true;
  loadSegmentUser();
};

const handleJoinUser = async () => {
  const ids = String(joinUserIds.value || "")
    .split(/[,，\s]+/)
    .map(s => s.trim())
    .filter(Boolean);
  if (ids.length === 0) {
    ElMessage.warning("请输入要加入的用户ID");
    return;
  }
  if (ids.some(id => !/^\d+$/.test(id))) {
    ElMessage.warning("用户ID 只能为数字，多个请用英文逗号分隔");
    return;
  }
  try {
    joinLoading.value = true;
    await addUserSegmentUserApi(currentSegment.value.segmentId, ids.map(Number));
    ElMessage.success(`已加入 ${ids.length} 个用户`);
    joinUserIds.value = "";
    userPage.pageNum = 1;
    loadSegmentUser();
    handleRefresh();
  } finally {
    joinLoading.value = false;
  }
};

const handleRemoveUser = () => {
  const rows = userTableRef.value?.getSelectionRows() || [];
  if (rows.length === 0) {
    ElMessage.warning("请选择要移出的用户");
    return;
  }
  ElMessageBox.confirm(`确定将选中的 ${rows.length} 个用户移出该分群吗？`, "提示", { type: "warning" })
    .then(async () => {
      await removeUserSegmentUserApi(currentSegment.value.segmentId, rows.map(r => r.segmentRelId).join(","));
      ElMessage.success("移出成功");
      userTableRef.value?.clearSelection();
      loadSegmentUser();
      handleRefresh();
    })
    .catch(() => {});
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
