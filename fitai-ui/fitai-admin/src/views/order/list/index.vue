<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- 订单状态列 -->
      <template #status="{ row }">
        <el-tag :type="statusMap[row.status]?.type || 'info'">
          {{ statusMap[row.status]?.label || row.status }}
        </el-tag>
      </template>

      <!-- 金额列 -->
      <template #amount="{ row }">
        <span class="text-red-500 font-medium">¥{{ row.payAmount?.toFixed(2) }}</span>
      </template>

      <!-- 操作列 -->
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
        <el-button v-if="row.status === 'shipped'" type="primary" link @click="handleLogistics(row)">查看物流</el-button>
        <el-button v-if="row.status === 'paid'" type="success" link :icon="Van" @click="handleShipWithConfirm(row)">发货</el-button>
        <el-button v-if="row.status === 'unpaid'" type="danger" link :icon="Close" @click="handleCancel(row)">取消</el-button>
      </template>
    </vxe-grid>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="statusMap[currentOrder.status]?.type">
            {{ statusMap[currentOrder.status]?.label }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户手机">{{ currentOrder.userPhone }}</el-descriptions-item>
        <el-descriptions-item label="商品总额">¥{{ currentOrder.totalAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">¥{{ currentOrder.discountAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="运费">¥{{ currentOrder.freightAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">
          <span class="text-red-500 font-bold">¥{{ currentOrder.payAmount?.toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ currentOrder.payType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">
          {{ formatAddress(currentOrder.addressSnapshot) }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="mt-4">
        <h4 class="mb-3 font-medium">商品明细</h4>
        <el-table :data="currentOrder.items" border>
          <el-table-column label="商品图片" width="80">
            <template #default="{ row }">
              <div class="table-thumb-cell">
                <el-image :src="row.skuImage" :preview-src-list="[row.skuImage]" class="table-thumb-square" />
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="skuName" label="商品名称" min-width="200" />
          <el-table-column prop="specs" label="规格" width="150">
            <template #default="{ row }">
              {{ formatSpecs(row.specs) }}
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="100">
            <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="totalAmount" label="小计" width="100">
            <template #default="{ row }">¥{{ row.totalAmount?.toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog v-model="shipVisible" title="订单发货" width="500px">
      <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="100px">
        <el-form-item label="物流公司" prop="expressCompany">
          <el-select v-model="shipForm.expressCompany" placeholder="请选择物流公司" style="width: 100%">
            <el-option label="顺丰速运" value="SF" />
            <el-option label="中通快递" value="ZTO" />
            <el-option label="圆通速递" value="YTO" />
            <el-option label="韵达快递" value="YD" />
            <el-option label="京东物流" value="JD" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" prop="expressNo">
          <el-input v-model="shipForm.expressNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipLoading" @click="handleShipSubmit">确定发货</el-button>
      </template>
    </el-dialog>

    <!-- 物流轨迹弹窗 -->
    <el-dialog v-model="logisticsVisible" title="物流轨迹" width="560px">
      <div v-loading="logisticsLoading" class="min-h-[200px]">
        <template v-if="logisticsData">
          <div class="mb-4 flex gap-4 text-sm">
            <span>{{ logisticsData.expressCompanyName }} {{ logisticsData.expressNo }}</span>
            <el-tag size="small">{{ logisticsData.status }}</el-tag>
          </div>
          <el-timeline v-if="logisticsData.traces?.length">
            <el-timeline-item
              v-for="(item, idx) in logisticsData.traces"
              :key="idx"
              :timestamp="item.time"
              placement="top"
            >
              <div>{{ item.context }}</div>
              <div v-if="item.location" class="text-gray-500 text-xs mt-1">{{ item.location }}</div>
            </el-timeline-item>
          </el-timeline>
          <div v-else class="text-gray-500">暂无轨迹信息</div>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="orderList">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, View, Van, Close } from "@element-plus/icons-vue";
import { getOrderPageApi, getOrderDetailApi, shipOrderApi, cancelOrderApi, getOrderLogisticsApi } from "@/api/modules/order";

const xGrid = ref();
const shipFormRef = ref();
const detailVisible = ref(false);
const shipVisible = ref(false);
const shipLoading = ref(false);
const logisticsVisible = ref(false);
const logisticsLoading = ref(false);
const logisticsData = ref(null);
const currentOrder = ref({});

// 订单状态映射
const statusMap = {
  unpaid: { label: "待支付", type: "warning" },
  paid: { label: "已支付", type: "success" },
  shipped: { label: "已发货", type: "primary" },
  received: { label: "已收货", type: "success" },
  completed: { label: "已完成", type: "success" },
  cancelled: { label: "已取消", type: "info" },
  refunding: { label: "退款中", type: "danger" },
  refunded: { label: "已退款", type: "info" }
};

// 发货表单
const shipForm = reactive({
  orderId: null,
  expressCompany: "",
  expressNo: ""
});

const shipRules = {
  expressCompany: [{ required: true, message: "请选择物流公司", trigger: "change" }],
  expressNo: [{ required: true, message: "请输入物流单号", trigger: "blur" }]
};

// 表格配置
const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
  pagerConfig: {
    pageSize: 10,
    pageSizes: [10, 20, 50, 100]
  },
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
      { field: "orderNo", title: "订单号", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入订单号" } } },
      { field: "userPhone", title: "用户手机", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入手机号" } } },
      {
        field: "status",
        title: "订单状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "待支付", value: "unpaid" },
            { label: "已支付", value: "paid" },
            { label: "已发货", value: "shipped" },
            { label: "已收货", value: "received" },
            { label: "已完成", value: "completed" },
            { label: "已取消", value: "cancelled" }
          ],
          props: { placeholder: "请选择状态" }
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
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = {
          pageNum: page.currentPage,
          pageSize: page.pageSize,
          ...form
        };
        try {
          const res = await getOrderPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (error) {
          console.error("获取订单列表失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "orderNo", title: "订单号", width: 200 },
    { field: "userPhone", title: "用户手机", width: 130 },
    { field: "productNames", title: "商品", minWidth: 200, showOverflow: true },
    { field: "totalAmount", title: "商品总额", width: 100, formatter: ({ cellValue }) => `¥${cellValue?.toFixed(2)}` },
    { field: "payAmount", title: "实付金额", width: 100, slots: { default: "amount" } },
    { field: "status", title: "订单状态", width: 100, slots: { default: "status" } },
    { field: "payType", title: "支付方式", width: 100 },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 180, fixed: "right", slots: { default: "action" } }
  ]
});

// 格式化地址
const formatAddress = address => {
  if (!address) return "-";
  return `${address.name} ${address.phone} ${address.province}${address.city}${address.district}${address.detail}`;
};

// 格式化规格
const formatSpecs = specs => {
  if (!specs) return "-";
  return Object.entries(specs).map(([k, v]) => `${k}:${v}`).join(" ");
};

// 刷新
const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

// 查看详情
const handleView = row => {
  currentOrder.value = row;
  detailVisible.value = true;
};

// 查看物流
const handleLogistics = async row => {
  logisticsVisible.value = true;
  logisticsData.value = null;
  logisticsLoading.value = true;
  try {
    const res = await getOrderLogisticsApi(row.id);
    logisticsData.value = res.data;
  } catch (error) {
    console.error("查询物流失败:", error);
    ElMessage.error(error?.response?.data?.msg || "查询物流失败");
  } finally {
    logisticsLoading.value = false;
  }
};

// 发货（带风险确认）
const handleShipWithConfirm = row => {
  ElMessageBox.confirm(
    `确定对订单「${row.orderNo}」执行发货操作？发货后将无法在系统中直接撤销，请确认已核对用户收货信息与支付状态。`,
    "发货确认",
    {
      type: "warning"
    }
  ).then(() => {
    shipForm.orderId = row.id;
    shipForm.expressCompany = "";
    shipForm.expressNo = "";
    shipVisible.value = true;
  });
};

// 提交发货
const handleShipSubmit = async () => {
  try {
    await shipFormRef.value.validate();
    shipLoading.value = true;
    
    await shipOrderApi(shipForm.orderId, shipForm);
    
    ElMessage.success("发货成功");
    shipVisible.value = false;
    handleRefresh();
  } catch (error) {
    console.error("发货失败:", error);
    ElMessage.error("发货失败，请重试");
  } finally {
    shipLoading.value = false;
  }
};

// 取消订单（高危操作）
const handleCancel = row => {
  ElMessageBox.confirm(
    `确定要取消订单「${row.orderNo}」吗？取消后订单状态将变为【已取消】，用户将无法继续支付，如已支付需走退款流程。`,
    "取消订单确认",
    {
      type: "warning"
    }
  ).then(async () => {
    try {
      await cancelOrderApi(row.id);
      ElMessage.success("订单已取消");
      handleRefresh();
    } catch (error) {
      console.error("取消订单失败:", error);
      ElMessage.error("取消订单失败，请重试");
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
