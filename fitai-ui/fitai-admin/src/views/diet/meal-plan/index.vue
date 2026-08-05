<template>
  <div class="table-box w-full p-5">
    <vxe-grid ref="xGrid" class="w-full" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #user="{ row }">
        <span>{{ row.userNickname || "-" }}</span>
        <span class="text-gray-400 text-xs ml-1">({{ row.userId }})</span>
      </template>
      <template #ai="{ row }">
        <el-tag :type="row.aiGenerated === 1 ? 'success' : 'info'" size="small">
          {{ row.aiGenerated === 1 ? "AI" : "手动" }}
        </el-tag>
      </template>
    </vxe-grid>
  </div>
</template>

<script setup name="dietMealPlan">
import { reactive, ref } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import { getDietMealPlanPageApi } from "@/api/modules/diet";

const xGrid = ref();

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
  pagerConfig: { pageSize: 10, pageSizes: [10, 20, 50, 100] },
  toolbarConfig: {
    slots: { buttons: "toolbar_buttons" },
    refresh: true,
    zoom: false
  },
  formConfig: {
    items: [
      {
        field: "username",
        title: "用户名",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "模糊匹配昵称" } }
      },
      {
        field: "planDate",
        title: "计划日期",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "yyyy-MM-dd", type: "date" } }
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
          const res = await getDietMealPlanPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (e) {
          console.error(e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "id", title: "方案ID", width: 100 },
    { field: "user", title: "用户", minWidth: 160, slots: { default: "user" } },
    { field: "planDate", title: "日期", width: 120 },
    { field: "targetCalories", title: "目标热量", width: 100 },
    { field: "targetProtein", title: "蛋白(g)", width: 90 },
    { field: "targetCarbs", title: "碳水(g)", width: 90 },
    { field: "targetFat", title: "脂肪(g)", width: 90 },
    { field: "ai", title: "来源", width: 80, slots: { default: "ai" } },
    { field: "createTime", title: "创建时间", width: 170 }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};
</script>

<style scoped>
.table-box {
  width: 100%;
  background: #fff;
  border-radius: 8px;
}
</style>
