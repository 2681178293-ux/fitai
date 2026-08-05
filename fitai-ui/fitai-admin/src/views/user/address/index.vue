<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #region="{ row }">
        <span>{{ formatRegion(row) }}</span>
      </template>

      <template #isDefault="{ row }">
        <el-tag v-if="row.isDefault" type="success">默认</el-tag>
        <el-tag v-else type="info">否</el-tag>
      </template>

      <template #createTime="{ row }">
        <span>{{ formatTime(row.createTime) }}</span>
      </template>

      <template #action="{ row }">
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>
  </div>
</template>

<script setup name="userAddress">
import { ref, reactive } from "vue";
import dayjs from "dayjs";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, Delete } from "@element-plus/icons-vue";
import { getUserAddressPageApi, deleteUserAddressApi } from "@/api/modules/user";

const xGrid = ref();

const formatTime = val => (val ? dayjs(val).format("YYYY-MM-DD HH:mm:ss") : "-");

const formatRegion = row => {
  const text = [row.province, row.city, row.district].filter(Boolean).join(" / ");
  return text || "-";
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
      { field: "userId", title: "用户ID", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      { field: "phone", title: "手机号", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      {
        field: "keyword",
        title: "关键词",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "收货人/地址" } }
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
            userId: form.userId ? Number(form.userId) : undefined,
            phone: form.phone || undefined,
            keyword: form.keyword || undefined
          };
          const res = await getUserAddressPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (e) {
          console.error("获取收货地址失败:", e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "checkbox", width: 50 },
    { type: "seq", title: "序号", width: 60 },
    { field: "userId", title: "用户ID", width: 100 },
    { field: "userNickname", title: "用户昵称", minWidth: 120 },
    { field: "name", title: "收货人", width: 110 },
    { field: "phone", title: "手机号", width: 130 },
    { field: "province", title: "省市区", minWidth: 180, slots: { default: "region" } },
    { field: "detail", title: "详细地址", minWidth: 220 },
    { field: "isDefault", title: "是否默认", width: 100, slots: { default: "isDefault" } },
    { field: "createTime", title: "创建时间", width: 170, slots: { default: "createTime" } },
    { title: "操作", width: 90, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除「${row.name || ""}」的收货地址吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteUserAddressApi(row.id);
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
  ElMessageBox.confirm(`确定删除选中的 ${records.length} 条收货地址吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteUserAddressApi(records.map(r => r.id).join(","));
      ElMessage.success("批量删除成功");
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
