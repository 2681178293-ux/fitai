<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Present" @click="handleGift()">赠送额度</el-button>
        <el-button :icon="Tickets" @click="openGiftRecords">赠送记录</el-button>
        <el-button :icon="Setting" @click="openConfig">配额配置</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #vip="{ row }">
        <el-tag v-if="row.vipActive" type="success">{{ vipLevelLabel(row.vipLevel) }}</el-tag>
        <el-tag v-else type="info">普通</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleDetail(row)">详情</el-button>
        <el-button type="success" link :icon="Present" @click="handleGift(row)">赠送</el-button>
      </template>
    </vxe-grid>

    <!-- 赠送额度弹窗 -->
    <el-dialog v-model="giftVisible" title="赠送 Token 额度" width="480px" destroy-on-close>
      <el-form ref="giftFormRef" :model="giftForm" :rules="giftRules" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input-number v-model="giftForm.userId" :min="1" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="赠送 Token" prop="giftTokens">
          <el-input-number v-model="giftForm.giftTokens" :min="1" :step="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="giftForm.reason" type="textarea" :rows="2" placeholder="活动/客服补偿等（选填）" />
        </el-form-item>
        <el-form-item label="关联权益ID">
          <el-input-number v-model="giftForm.benefitId" :min="1" :controls="false" style="width: 100%" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="giftVisible = false">取消</el-button>
        <el-button type="primary" :loading="giftLoading" @click="submitGift">确定赠送</el-button>
      </template>
    </el-dialog>

    <!-- 用户配额详情 -->
    <el-dialog v-model="detailVisible" title="用户配额详情" width="560px" destroy-on-close>
      <el-descriptions v-if="detail" :column="2" border size="small">
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detail.nickname || "-" }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.phone || "-" }}</el-descriptions-item>
        <el-descriptions-item label="会员">
          <el-tag v-if="detail.vipActive" type="success">{{ vipLevelLabel(detail.vipLevel) }}</el-tag>
          <el-tag v-else type="info">普通</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="基础额度">{{ formatNumber(detail.baseLimit) }}</el-descriptions-item>
        <el-descriptions-item label="会员加赠">{{ formatNumber(detail.vipBonus) }}</el-descriptions-item>
        <el-descriptions-item label="赠送额度">{{ formatNumber(detail.giftTokens) }}</el-descriptions-item>
        <el-descriptions-item label="本月总额度">{{ formatNumber(detail.totalLimit) }}</el-descriptions-item>
        <el-descriptions-item label="本月已用">{{ formatNumber(detail.used) }}</el-descriptions-item>
        <el-descriptions-item label="本月剩余">{{ formatNumber(detail.remaining) }}</el-descriptions-item>
        <el-descriptions-item label="周期" :span="2">{{ detail.periodLabel || "-" }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 赠送记录 -->
    <el-dialog v-model="recordsVisible" title="赠送记录" width="820px" destroy-on-close>
      <el-form :inline="true" class="mb-2">
        <el-form-item label="用户ID">
          <el-input v-model="recordQuery.userId" placeholder="用户ID" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="月份">
          <el-input v-model="recordQuery.quotaMonth" placeholder="yyyy-MM" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRecords(1)">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="records" border size="small" v-loading="recordsLoading">
        <el-table-column prop="giftId" label="ID" width="80" />
        <el-table-column prop="customerId" label="用户ID" width="90" />
        <el-table-column prop="nickname" label="昵称" min-width="100" />
        <el-table-column prop="quotaMonth" label="月份" width="90" />
        <el-table-column prop="giftTokens" label="赠送Token" width="100" />
        <el-table-column prop="reason" label="原因" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link @click="revokeGift(row)">撤销</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="mt-2"
        layout="total, prev, pager, next"
        :total="recordTotal"
        :page-size="recordQuery.pageSize"
        :current-page="recordQuery.pageNum"
        @current-change="loadRecords"
      />
    </el-dialog>

    <!-- 配额配置 -->
    <el-dialog v-model="configVisible" title="配额全局配置" width="620px" destroy-on-close>
      <el-descriptions :column="1" border size="small" class="mb-3">
        <el-descriptions-item label="非会员每月基础额度">{{ formatNumber(config.monthlyBaseTokens) }} Token</el-descriptions-item>
        <el-descriptions-item label="配置键">{{ config.configKey || "-" }}</el-descriptions-item>
      </el-descriptions>
      <div class="config-subtitle">会员档位每月加赠</div>
      <el-table :data="config.planBonuses || []" border size="small">
        <el-table-column prop="planId" label="套餐ID" width="90" />
        <el-table-column prop="name" label="套餐名称" min-width="140" />
        <el-table-column label="档位" width="90">
          <template #default="{ row }">{{ planTypeLabel(row.type) }}</template>
        </el-table-column>
        <el-table-column prop="aiTokenQuotaMonthly" label="每月加赠(Token)" min-width="140" />
      </el-table>
      <template #footer>
        <el-button @click="configVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="aiQuota">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, View, Present, Tickets, Setting } from "@element-plus/icons-vue";
import {
  getQuotaPageApi,
  getUserQuotaApi,
  grantQuotaGiftApi,
  getQuotaGiftPageApi,
  deleteQuotaGiftsApi,
  getQuotaConfigApi
} from "@/api/modules/ai-obs";

const xGrid = ref();

function formatNumber(n) {
  if (n == null) return "0";
  return Number(n).toLocaleString();
}
function vipLevelLabel(level) {
  const map = { 0: "普通", 1: "月卡会员", 2: "季卡会员", 3: "年卡会员" };
  return map[level] || "会员";
}
function planTypeLabel(type) {
  const map = { 1: "月", 2: "季", 3: "年" };
  return map[type] || "-";
}

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "userId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getQuotaPageApi(params);
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
      { field: "userId", title: "用户ID", span: 6, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "昵称/手机号" } } },
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
  columns: [
    { type: "seq", width: 60 },
    { field: "userId", title: "用户ID", width: 90 },
    { field: "nickname", title: "昵称", minWidth: 120 },
    { field: "phone", title: "手机号", width: 130 },
    { field: "vipActive", title: "会员", width: 100, slots: { default: "vip" } },
    { field: "totalLimit", title: "本月总额度", width: 120 },
    { field: "used", title: "已用", width: 110 },
    { field: "remaining", title: "剩余", width: 110 },
    { field: "giftTokens", title: "赠送", width: 100 },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

// ------ 赠送 ------
const giftVisible = ref(false);
const giftLoading = ref(false);
const giftFormRef = ref();
const giftForm = reactive({ userId: null, giftTokens: 1000, reason: "", benefitId: null });
const giftRules = {
  userId: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
  giftTokens: [{ required: true, message: "请输入赠送 Token 数", trigger: "blur" }]
};

const handleGift = (row = null) => {
  Object.assign(giftForm, { userId: row?.userId ?? null, giftTokens: 1000, reason: "", benefitId: null });
  giftVisible.value = true;
};

const submitGift = async () => {
  try {
    await giftFormRef.value.validate();
    giftLoading.value = true;
    await grantQuotaGiftApi({
      userId: giftForm.userId,
      giftTokens: giftForm.giftTokens,
      reason: giftForm.reason || null,
      benefitId: giftForm.benefitId || null
    });
    ElMessage.success("赠送成功");
    giftVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error(e?.msg || "赠送失败");
  } finally {
    giftLoading.value = false;
  }
};

// ------ 详情 ------
const detailVisible = ref(false);
const detail = ref(null);
const handleDetail = async row => {
  try {
    const res = await getUserQuotaApi(row.userId);
    detail.value = res.data || null;
    detailVisible.value = true;
  } catch (e) {
    ElMessage.error("获取详情失败");
  }
};

// ------ 赠送记录 ------
const recordsVisible = ref(false);
const recordsLoading = ref(false);
const records = ref([]);
const recordTotal = ref(0);
const recordQuery = reactive({ userId: "", quotaMonth: "", pageNum: 1, pageSize: 10 });

const openGiftRecords = () => {
  recordsVisible.value = true;
  loadRecords(1);
};
const loadRecords = async (page = 1) => {
  recordQuery.pageNum = page;
  recordsLoading.value = true;
  try {
    const res = await getQuotaGiftPageApi({
      userId: recordQuery.userId || null,
      quotaMonth: recordQuery.quotaMonth || null,
      pageNum: recordQuery.pageNum,
      pageSize: recordQuery.pageSize
    });
    const data = res.data || {};
    records.value = data.records || [];
    recordTotal.value = data.total || 0;
  } catch (e) {
    records.value = [];
    recordTotal.value = 0;
  } finally {
    recordsLoading.value = false;
  }
};
const revokeGift = row => {
  ElMessageBox.confirm("撤销后该笔赠送额度即时回落，确定撤销？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteQuotaGiftsApi(row.giftId);
      ElMessage.success("撤销成功");
      loadRecords(recordQuery.pageNum);
      handleRefresh();
    } catch (e) {
      ElMessage.error("撤销失败");
    }
  });
};

// ------ 配置 ------
const configVisible = ref(false);
const config = ref({});
const openConfig = async () => {
  try {
    const res = await getQuotaConfigApi();
    config.value = res.data || {};
    configVisible.value = true;
  } catch (e) {
    ElMessage.error("加载配置失败");
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.config-subtitle {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #303133;
}
</style>
