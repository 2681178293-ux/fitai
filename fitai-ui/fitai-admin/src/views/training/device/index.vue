<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增设备</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button :icon="Link" @click="openBind">绑定记录</el-button>
      </template>

      <template #connect="{ row }">
        <el-tag size="small">{{ connectMap[row.connectType] || row.connectType || "-" }}</el-tag>
      </template>

      <template #status="{ row }">
        <el-tag v-if="row.status === 1" type="success">上架</el-tag>
        <el-tag v-else type="info">下架</el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑设备' : '新增设备'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="formData.deviceName" placeholder="设备名称" />
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="formData.deviceBrand" placeholder="设备品牌" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="formData.deviceModel" placeholder="设备型号" />
        </el-form-item>
        <el-form-item label="连接方式">
          <el-select v-model="formData.connectType" placeholder="请选择" clearable style="width: 100%">
            <el-option label="蓝牙" value="bluetooth" />
            <el-option label="WiFi" value="wifi" />
            <el-option label="ANT+" value="ant" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" placeholder="请选择" style="width: 100%">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定记录 -->
    <el-dialog v-model="bindVisible" title="设备绑定记录" width="880px">
      <vxe-grid ref="bindGrid" v-bind="bindGridOptions">
        <template #bind_status="{ row }">
          <el-tag v-if="row.bindStatus === 0" type="success" size="small">已绑定</el-tag>
          <el-tag v-else type="info" size="small">已解绑</el-tag>
        </template>
        <template #bind_action="{ row }">
          <el-button
            v-if="row.bindStatus === 0"
            type="warning"
            link
            :icon="RemoveFilled"
            @click="handleUnbind(row)"
          >
            解绑
          </el-button>
          <span v-else class="text-gray-400">-</span>
        </template>
      </vxe-grid>
    </el-dialog>
  </div>
</template>

<script setup name="trainingDevice">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, Link, RemoveFilled } from "@element-plus/icons-vue";
import {
  getTrainDevicePageApi,
  addTrainDeviceApi,
  updateTrainDeviceApi,
  deleteTrainDeviceApi,
  getTrainDeviceBindPageApi,
  unbindTrainDeviceApi
} from "@/api/modules/biz-ext";

const xGrid = ref();
const bindGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const bindVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const connectMap = { bluetooth: "蓝牙", wifi: "WiFi", ant: "ANT+" };

const formData = reactive({
  id: null,
  deviceName: "",
  deviceBrand: "",
  deviceModel: "",
  connectType: "",
  status: 1
});

const formRules = {
  deviceName: [{ required: true, message: "请输入设备名称", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getTrainDevicePageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  formConfig: {
    items: [
      { field: "deviceName", title: "名称", span: 5, itemRender: { name: "$input", props: { placeholder: "设备名称" } } },
      { field: "deviceBrand", title: "品牌", span: 5, itemRender: { name: "$input", props: { placeholder: "品牌" } } },
      {
        field: "connectType",
        title: "连接方式",
        span: 5,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "蓝牙", value: "bluetooth" },
            { label: "WiFi", value: "wifi" },
            { label: "ANT+", value: "ant" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        field: "status",
        title: "状态",
        span: 5,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "上架", value: 1 },
            { label: "下架", value: 0 }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        span: 4,
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
  pagerConfig: { enabled: true },
  columns: [
    { type: "seq", width: 60 },
    { field: "id", title: "ID", width: 90 },
    { field: "deviceName", title: "设备名称", minWidth: 160 },
    { field: "deviceBrand", title: "品牌", width: 120 },
    { field: "deviceModel", title: "型号", width: 140 },
    { field: "connectType", title: "连接方式", width: 110, slots: { default: "connect" } },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const bindGridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 480,
  rowConfig: { keyField: "id" },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getTrainDeviceBindPageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  pagerConfig: { enabled: true, pageSize: 10 },
  columns: [
    { type: "seq", width: 60 },
    { field: "id", title: "ID", width: 90 },
    { field: "userNickname", title: "用户", minWidth: 120 },
    { field: "userId", title: "用户ID", width: 100 },
    { field: "deviceName", title: "设备", minWidth: 140 },
    { field: "deviceSn", title: "序列号", width: 160 },
    { field: "bindStatus", title: "绑定状态", width: 100, slots: { default: "bind_status" } },
    { field: "lastSyncTime", title: "最后同步", width: 170 },
    { field: "createTime", title: "绑定时间", width: 170 },
    { title: "操作", width: 100, fixed: "right", slots: { default: "bind_action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { id: null, deviceName: "", deviceBrand: "", deviceModel: "", connectType: "", status: 1 });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    deviceName: row.deviceName,
    deviceBrand: row.deviceBrand || "",
    deviceModel: row.deviceModel || "",
    connectType: row.connectType || "",
    status: row.status ?? 1
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      deviceName: formData.deviceName,
      deviceBrand: formData.deviceBrand || null,
      deviceModel: formData.deviceModel || null,
      connectType: formData.connectType || null,
      status: formData.status
    };
    if (isEdit.value) await updateTrainDeviceApi(formData.id, payload);
    else await addTrainDeviceApi(payload);
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定删除该设备？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteTrainDeviceApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

const openBind = () => {
  bindVisible.value = true;
  setTimeout(() => bindGrid.value?.commitProxy("query"), 0);
};

const handleUnbind = row => {
  ElMessageBox.confirm("确定解绑该设备？", "提示", { type: "warning" }).then(async () => {
    try {
      await unbindTrainDeviceApi(row.id);
      ElMessage.success("解绑成功");
      bindGrid.value?.commitProxy("query");
    } catch (e) {
      ElMessage.error("解绑失败");
    }
  });
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
