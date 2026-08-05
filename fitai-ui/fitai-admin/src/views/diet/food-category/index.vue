<template>
  <div class="table-box w-full p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增分类</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? "启用" : "禁用" }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="440px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="展示名称，唯一" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="99999" class="w-full" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="dietFoodCategory">
import { reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getDietFoodCategoryPageApi,
  addDietFoodCategoryApi,
  updateDietFoodCategoryApi,
  deleteDietFoodCategoryApi
} from "@/api/modules/diet";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const formData = reactive({
  id: null,
  name: "",
  sortOrder: 0,
  status: 1
});

const formRules = {
  name: [{ required: true, message: "请输入名称", trigger: "blur" }],
  sortOrder: [{ required: true, message: "请输入排序", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "change" }]
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
        field: "keyword",
        title: "名称",
        span: 8,
        itemRender: { name: "$input", props: { placeholder: "模糊搜索" } }
      },
      {
        span: 8,
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
          const res = await getDietFoodCategoryPageApi(params);
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
    { field: "id", title: "ID", width: 170 },
    { field: "name", title: "名称", minWidth: 140 },
    { field: "sortOrder", title: "排序", width: 90 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 140, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

function resetForm() {
  Object.assign(formData, {
    id: null,
    name: "",
    sortOrder: 0,
    status: 1
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
    sortOrder: row.sortOrder ?? 0,
    status: row.status === 0 ? 0 : 1
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitLoading.value = true;
    const payload = {
      name: formData.name.trim(),
      sortOrder: formData.sortOrder,
      status: formData.status
    };
    if (isEdit.value) {
      await updateDietFoodCategoryApi(formData.id, payload);
      ElMessage.success("已更新");
    } else {
      await addDietFoodCategoryApi(payload);
      ElMessage.success("已新增");
    }
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) {
      console.error(e);
    }
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除「${row.name}」？若分类下仍有食物将无法删除。`, "提示", { type: "warning" })
    .then(async () => {
      await deleteDietFoodCategoryApi(row.id);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};

</script>

<style scoped>
.table-box {
  width: 100%;
  background: #fff;
  border-radius: 8px;
}
</style>
