<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #status="{ row }">
        <el-tag :type="statusMap[row.status]?.type || 'info'">
          {{ statusMap[row.status]?.label ?? row.status }}
        </el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
      </template>
    </vxe-grid>

    <!-- 物流详情 + 轨迹 -->
    <el-dialog v-model="detailVisible" title="物流单详情" width="620px">
      <div v-loading="detailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusMap[detail.status]?.type || 'info'">
              {{ statusMap[detail.status]?.label ?? detail.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="快递公司">{{ detail.expressName || detail.expressCompany || '-' }}</el-descriptions-item>
          <el-descriptions-item label="快递单号">{{ detail.expressNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ detail.shipTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detail.createTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="mt-4 mb-3 flex items-center justify-between">
          <h4 class="font-medium">物流轨迹</h4>
          <el-button type="primary" size="small" :icon="Refresh" :loading="refreshLoading" @click="handleRefreshTrace">刷新轨迹</el-button>
        </div>
        <el-timeline v-if="detail.traces?.length">
          <el-timeline-item
            v-for="(item, idx) in detail.traces"
            :key="idx"
            :timestamp="item.traceTime"
            placement="top"
          >
            <div>{{ item.traceDesc }}</div>
            <div v-if="item.location" class="text-gray-500 text-xs mt-1">{{ item.location }}</div>
          </el-timeline-item>
        </el-timeline>
        <div v-else class="text-gray-500">暂无轨迹信息，可点击「刷新轨迹」获取</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="orderLogistics">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, View } from "@element-plus/icons-vue";
import { getLogisticsPageApi, getLogisticsDetailApi, refreshLogisticsApi } from "@/api/modules/mall-ext";

const xGrid = ref();
const detailVisible = ref(false);
const detailLoading = ref(false);
const refreshLoading = ref(false);
const detail = ref({});
const currentId = ref(null);

const statusMap = {
  0: { label: "待发货", type: "info" },
  1: { label: "已发货", type: "warning" },
  2: { label: "运输中", type: "primary" },
  3: { label: "已签收", type: "success" }
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
      { field: "orderNo", title: "订单号", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入订单号" } } },
      { field: "expressNo", title: "快递单号", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入快递单号" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "待发货", value: 0 },
            { label: "已发货", value: 1 },
            { label: "运输中", value: 2 },
            { label: "已签收", value: 3 }
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
          const res = await getLogisticsPageApi(params);
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
    { field: "orderNo", title: "订单号", minWidth: 180 },
    { field: "expressName", title: "快递公司", minWidth: 120 },
    { field: "expressCompany", title: "公司编码", width: 110 },
    { field: "expressNo", title: "快递单号", minWidth: 160 },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { field: "shipTime", title: "发货时间", width: 170 },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 100, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const loadDetail = async id => {
  detailLoading.value = true;
  try {
    const res = await getLogisticsDetailApi(id);
    detail.value = res.data || {};
  } catch (e) {
    detail.value = {};
  } finally {
    detailLoading.value = false;
  }
};

const handleView = row => {
  currentId.value = row.id;
  detail.value = {};
  detailVisible.value = true;
  loadDetail(row.id);
};

const handleRefreshTrace = async () => {
  if (!currentId.value) return;
  refreshLoading.value = true;
  try {
    const res = await refreshLogisticsApi(currentId.value);
    detail.value = res.data || detail.value;
    ElMessage.success("轨迹已刷新");
    handleRefresh();
  } catch (e) {
    /* 拦截器已提示 */
  } finally {
    refreshLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
