<template>
  <div class="table-box w-full p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增食物</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑食物' : '新增食物'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="食物名称，与 C 端搜索一致" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="formData.categoryId" class="w-full" clearable placeholder="请选择" filterable>
            <el-option v-for="c in categoryFormOptions" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片 URL">
          <el-input v-model="formData.image" placeholder="可选" />
        </el-form-item>
        <el-form-item label="热量(kcal/100g)">
          <el-input-number v-model="formData.calories" :min="0" class="w-full" />
        </el-form-item>
        <el-form-item label="蛋白质(g)">
          <el-input-number v-model="formData.protein" :min="0" :precision="2" class="w-full" />
        </el-form-item>
        <el-form-item label="碳水(g)">
          <el-input-number v-model="formData.carbs" :min="0" :precision="2" class="w-full" />
        </el-form-item>
        <el-form-item label="脂肪(g)">
          <el-input-number v-model="formData.fat" :min="0" :precision="2" class="w-full" />
        </el-form-item>
        <el-form-item label="膳食纤维(g)">
          <el-input-number v-model="formData.fiber" :min="0" :precision="2" class="w-full" />
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="formData.unit" placeholder="如：100g" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="dietFood">
import { reactive, ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getDietFoodPageApi,
  addDietFoodApi,
  updateDietFoodApi,
  deleteDietFoodApi,
  getDietFoodCategoryOptionsApi
} from "@/api/modules/diet";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

/** 查询表单：分类下拉（含「全部」） */
const categoryFilterOptions = ref([{ label: "全部", value: null }]);
/** 编辑弹窗：仅启用分类 */
const categoryFormOptions = ref([]);

const formData = reactive({
  id: null,
  name: "",
  categoryId: null,
  image: "",
  calories: null,
  protein: null,
  carbs: null,
  fat: null,
  fiber: null,
  unit: ""
});

const formRules = {
  name: [{ required: true, message: "请输入名称", trigger: "blur" }]
};

function buildFilterOptions(list) {
  const rows = list || [];
  return [{ label: "全部", value: null }, ...rows.map(c => ({ label: c.name, value: c.id }))];
}

function buildFormOptions(list) {
  const rows = list || [];
  return rows.map(c => ({ label: c.name, value: c.id }));
}

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
        field: "keyword",
        title: "名称",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "食物名称" } }
      },
      {
        field: "categoryId",
        title: "分类",
        span: 6,
        itemRender: {
          name: "$select",
          options: [],
          props: { placeholder: "全部", clearable: true }
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
          keyword: form.keyword,
          categoryId: form.categoryId || undefined
        };
        try {
          const res = await getDietFoodPageApi(params);
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
    { field: "id", title: "ID", width: 100 },
    { field: "name", title: "名称", minWidth: 200 },
    { field: "categoryName", title: "分类", width: 100 },
    { field: "calories", title: "热量/100g", width: 100 },
    { field: "protein", title: "蛋白质", width: 90 },
    { field: "carbs", title: "碳水", width: 90 },
    { field: "fat", title: "脂肪", width: 90 },
    { field: "unit", title: "单位", width: 80 },
    { title: "操作", width: 120, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

function resetForm() {
  Object.assign(formData, {
    id: null,
    name: "",
    categoryId: null,
    image: "",
    calories: null,
    protein: null,
    carbs: null,
    fat: null,
    fiber: null,
    unit: ""
  });
}

const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    name: row.name || "",
    categoryId: row.categoryId != null ? row.categoryId : null,
    image: row.image || "",
    calories: row.calories ?? null,
    protein: row.protein != null ? Number(row.protein) : null,
    carbs: row.carbs != null ? Number(row.carbs) : null,
    fat: row.fat != null ? Number(row.fat) : null,
    fiber: row.fiber != null ? Number(row.fiber) : null,
    unit: row.unit || ""
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitLoading.value = true;
    const payload = {
      name: formData.name,
      categoryId: formData.categoryId != null ? formData.categoryId : undefined,
      image: formData.image || undefined,
      calories: formData.calories,
      protein: formData.protein,
      carbs: formData.carbs,
      fat: formData.fat,
      fiber: formData.fiber,
      unit: formData.unit || undefined
    };
    if (isEdit.value) {
      await updateDietFoodApi(formData.id, payload);
      ElMessage.success("已更新");
    } else {
      await addDietFoodApi(payload);
      ElMessage.success("已新增");
    }
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) {
      console.error(e);
      ElMessage.error(e?.response?.data?.msg || e?.message || "操作失败");
    }
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除「${row.name}」？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteDietFoodApi(row.id);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};

onMounted(async () => {
  try {
    const res = await getDietFoodCategoryOptionsApi();
    const list = res.data || [];
    categoryFilterOptions.value = buildFilterOptions(list);
    categoryFormOptions.value = buildFormOptions(list);
    const catItem = gridOptions.formConfig.items.find(i => i.field === "categoryId");
    if (catItem && catItem.itemRender) {
      catItem.itemRender.options = categoryFilterOptions.value;
    }
  } catch (e) {
    console.error(e);
  }
});
</script>

<style scoped>
.table-box {
  width: 100%;
  background: #fff;
  border-radius: 8px;
}
</style>
