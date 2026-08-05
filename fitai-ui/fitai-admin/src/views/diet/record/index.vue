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
      <template #meal="{ row }">
        <el-tag size="small">{{ mealLabel(row.mealType) }}</el-tag>
      </template>
    </vxe-grid>
  </div>
</template>

<script setup name="dietRecord">
import { reactive, ref, onMounted } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import { getDietRecordPageApi, getDietFoodCategoryOptionsApi } from "@/api/modules/diet";

const xGrid = ref();

onMounted(async () => {
  try {
    const res = await getDietFoodCategoryOptionsApi();
    const list = res?.data ?? res ?? [];
    const opts = [{ label: "全部", value: "" }, ...list.map(c => ({ label: c.name, value: c.id }))];
    const categoryItem = gridOptions.formConfig.items.find(i => i.field === "categoryId");
    if (categoryItem?.itemRender) {
      categoryItem.itemRender.options = opts;
    }
  } catch (e) {
    console.error(e);
  }
});

const mealLabel = type => {
  const map = {
    breakfast: "早餐",
    lunch: "午餐",
    dinner: "晚餐",
    snack: "加餐"
  };
  return map[type] || type || "-";
};

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
        field: "keyword",
        title: "食物名",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "模糊搜索" } }
      },
      {
        field: "categoryId",
        title: "分类",
        span: 6,
        itemRender: {
          name: "$select",
          options: [{ label: "全部", value: "" }]
        }
      },
      {
        field: "mealType",
        title: "餐次",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "早餐", value: "breakfast" },
            { label: "午餐", value: "lunch" },
            { label: "晚餐", value: "dinner" },
            { label: "加餐", value: "snack" }
          ]
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
        if (params.categoryId === "" || params.categoryId == null) {
          delete params.categoryId;
        }
        try {
          const res = await getDietRecordPageApi(params);
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
    { field: "id", title: "记录ID", width: 100 },
    { field: "user", title: "用户", width: 140, slots: { default: "user" } },
    { field: "recordDate", title: "日期", width: 120 },
    { field: "meal", title: "餐次", width: 90, slots: { default: "meal" } },
    { field: "foodName", title: "食物", minWidth: 160 },
    { field: "categoryName", title: "分类", width: 100 },
    { field: "portion", title: "份量(g)", width: 90 },
    { field: "calories", title: "热量", width: 80 },
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
