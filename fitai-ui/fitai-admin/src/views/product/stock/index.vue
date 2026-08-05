<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-checkbox v-model="onlyWarning" class="ml-3" @change="handleRefresh">仅看库存预警（≤{{ WARNING_LINE }}）</el-checkbox>
      </template>
      <template #stock="{ row }">
        <el-tag :type="row.stock <= WARNING_LINE ? 'danger' : 'success'">{{ row.stock }}</el-tag>
      </template>
      <template #price="{ row }">¥{{ Number(row.price || 0).toFixed(2) }}</template>
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleAdjust(row)">调整库存</el-button>
        <el-button type="info" link :icon="Document" @click="handleLogs(row)">调整记录</el-button>
      </template>
    </vxe-grid>

    <!-- 调整库存弹窗 -->
    <el-dialog v-model="adjustVisible" title="调整库存" width="460px" destroy-on-close>
      <el-form ref="adjustFormRef" :model="adjustForm" :rules="adjustRules" label-width="100px">
        <el-form-item label="SKU">
          <span>{{ adjustForm.skuName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span class="font-medium">{{ adjustForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="调整后库存" prop="quantity">
          <el-input-number v-model="adjustForm.quantity" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input v-model="adjustForm.reason" type="textarea" :rows="3" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAdjustSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 调整记录弹窗 -->
    <el-dialog v-model="logVisible" title="库存调整记录" width="720px">
      <el-table v-loading="logLoading" :data="logList" border max-height="420">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="changeAmount" label="变更量" width="100">
          <template #default="{ row }">
            <span :class="row.changeAmount >= 0 ? 'text-green-500' : 'text-red-500'">
              {{ row.changeAmount >= 0 ? '+' : '' }}{{ row.changeAmount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="beforeStock" label="变更前" width="90" />
        <el-table-column prop="afterStock" label="变更后" width="90" />
        <el-table-column prop="reason" label="原因" min-width="160" />
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="createTime" label="时间" width="170" />
      </el-table>
      <div v-if="!logLoading && !logList.length" class="text-gray-400 text-center py-4">暂无调整记录</div>
    </el-dialog>
  </div>
</template>

<script setup name="productStock">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, Edit, Document } from "@element-plus/icons-vue";
import { getStockPageApi, getStockWarningApi, adjustStockApi, getStockLogApi } from "@/api/modules/mall-ext";

const WARNING_LINE = 10;

const xGrid = ref();
const adjustFormRef = ref();
const onlyWarning = ref(false);
const adjustVisible = ref(false);
const submitLoading = ref(false);
const logVisible = ref(false);
const logLoading = ref(false);
const logList = ref([]);

const adjustForm = reactive({
  skuId: null,
  skuName: "",
  currentStock: 0,
  quantity: 0,
  reason: ""
});

const adjustRules = {
  quantity: [{ required: true, message: "请输入调整后库存", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "skuId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  pagerConfig: { enabled: true, pageSize: 10, pageSizes: [10, 20, 50, 100] },
  formConfig: {
    items: [
      { field: "keyword", title: "关键词", span: 8, itemRender: { name: "$input", props: { placeholder: "SPU/SKU 名称" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "启用", value: 1 },
            { label: "禁用", value: 0 }
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
          const res = onlyWarning.value ? await getStockWarningApi(params) : await getStockPageApi(params);
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
    { field: "skuId", title: "SKU ID", width: 90 },
    { field: "spuName", title: "商品名称", minWidth: 180 },
    { field: "skuName", title: "SKU名称", minWidth: 160 },
    { field: "price", title: "价格", width: 100, slots: { default: "price" } },
    { field: "stock", title: "库存", width: 100, slots: { default: "stock" } },
    { field: "salesCount", title: "销量", width: 90 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 190, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleAdjust = row => {
  Object.assign(adjustForm, {
    skuId: row.skuId,
    skuName: row.skuName || row.spuName || `SKU#${row.skuId}`,
    currentStock: row.stock,
    quantity: row.stock,
    reason: ""
  });
  adjustVisible.value = true;
};

const handleAdjustSubmit = async () => {
  try {
    await adjustFormRef.value.validate();
    submitLoading.value = true;
    await adjustStockApi({ skuId: adjustForm.skuId, quantity: adjustForm.quantity, reason: adjustForm.reason });
    ElMessage.success("库存调整成功");
    adjustVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) {
      /* 校验失败或拦截器已提示 */
    }
  } finally {
    submitLoading.value = false;
  }
};

const handleLogs = async row => {
  logVisible.value = true;
  logList.value = [];
  logLoading.value = true;
  try {
    const res = await getStockLogApi(row.skuId);
    logList.value = res.data || [];
  } catch (e) {
    logList.value = [];
  } finally {
    logLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
