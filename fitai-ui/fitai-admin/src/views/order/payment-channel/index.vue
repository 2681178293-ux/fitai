<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增渠道</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #icon="{ row }">
        <div v-if="row.icon" class="table-thumb-cell">
          <el-image :src="row.icon" :preview-src-list="[row.icon]" class="table-thumb-square" />
        </div>
        <span v-else>-</span>
      </template>
      <template #feeRate="{ row }">
        {{ row.feeRate != null ? (Number(row.feeRate) * 100).toFixed(2) + '%' : '-' }}
      </template>
      <template #status="{ row }">
        <el-switch
          v-model="row.status"
          :active-value="1"
          :inactive-value="0"
          @change="handleToggle(row)"
        />
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">配置</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '配置支付渠道' : '新增支付渠道'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="渠道名称" prop="channelName">
          <el-input v-model="formData.channelName" placeholder="如 支付宝、微信支付" />
        </el-form-item>
        <el-form-item label="渠道编码" prop="channelCode">
          <el-input v-model="formData.channelCode" :disabled="isEdit" placeholder="如 alipay、wxpay（唯一）" />
        </el-form-item>
        <el-form-item label="商户号">
          <el-input v-model="formData.merchantId" placeholder="请输入商户号" />
        </el-form-item>
        <el-form-item label="费率">
          <el-input-number v-model="formData.feeRate" :min="0" :max="1" :step="0.001" :precision="4" style="width: 100%" />
          <span class="text-gray-400 text-xs ml-2">小数，如 0.006 表示 0.6%</span>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="formData.icon" placeholder="图标 URL（选填）" />
        </el-form-item>
        <el-form-item label="配置JSON">
          <el-input v-model="formData.configJson" type="textarea" :rows="4" placeholder='如 {"appId":"..."}' />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="paymentChannel">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getChannelPageApi,
  addChannelApi,
  updateChannelApi,
  toggleChannelStatusApi,
  deleteChannelApi
} from "@/api/modules/mall-ext";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const formData = reactive({
  id: null,
  channelName: "",
  channelCode: "",
  merchantId: "",
  configJson: "",
  feeRate: 0,
  icon: "",
  sortOrder: 0,
  status: 1
});

const formRules = {
  channelName: [{ required: true, message: "请输入渠道名称", trigger: "blur" }],
  channelCode: [{ required: true, message: "请输入渠道编码", trigger: "blur" }]
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
      { field: "channelName", title: "渠道名称", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入渠道名称" } } },
      { field: "channelCode", title: "渠道编码", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入渠道编码" } } },
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
          const res = await getChannelPageApi(params);
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
    { field: "icon", title: "图标", width: 80, slots: { default: "icon" } },
    { field: "channelName", title: "渠道名称", minWidth: 140 },
    { field: "channelCode", title: "渠道编码", width: 120 },
    { field: "merchantId", title: "商户号", minWidth: 140 },
    { field: "feeRate", title: "费率", width: 100, slots: { default: "feeRate" } },
    { field: "sortOrder", title: "排序", width: 80 },
    { field: "status", title: "状态", width: 80, slots: { default: "status" } },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleToggle = async row => {
  try {
    await toggleChannelStatusApi(row.id);
    ElMessage.success(row.status === 1 ? "已启用" : "已停用");
  } catch (e) {
    row.status = row.status === 1 ? 0 : 1;
  }
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: null,
    channelName: "",
    channelCode: "",
    merchantId: "",
    configJson: "",
    feeRate: 0,
    icon: "",
    sortOrder: 0,
    status: 1
  });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    channelName: row.channelName,
    channelCode: row.channelCode,
    merchantId: row.merchantId || "",
    configJson: row.configJson || "",
    feeRate: row.feeRate != null ? Number(row.feeRate) : 0,
    icon: row.icon || "",
    sortOrder: row.sortOrder || 0,
    status: row.status
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    if (isEdit.value) {
      await updateChannelApi(formData.id, formData);
    } else {
      await addChannelApi(formData);
    }
    ElMessage.success(isEdit.value ? "配置成功" : "新增成功");
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

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除渠道「${row.channelName}」？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteChannelApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      /* 拦截器已提示 */
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
