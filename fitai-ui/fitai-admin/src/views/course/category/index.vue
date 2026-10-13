<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增分类</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- 图标列 -->
      <template #icon="{ row }">
        <el-icon v-if="sanitizeMenuIcon(row.icon)" :size="20"><component :is="sanitizeMenuIcon(row.icon)" /></el-icon>
        <span v-else>-</span>
      </template>

      <!-- 状态列 -->
      <template #status="{ row }">
        <el-switch
          v-model="row.status"
          active-value="0"
          inactive-value="1"
          @change="handleStatusChange(row)"
        />
      </template>

      <!-- 操作列 -->
      <template #action="{ row }">
        <el-button type="primary" link :icon="Plus" @click="handleAddChild(row)">新增子级</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="父级分类">
          <el-cascader
            v-model="formData.parentId"
            :options="categoryTree"
            :props="{ value: 'categoryId', label: 'categoryName', children: 'children', checkStrictly: true, emitPath: false }"
            placeholder="无（顶级分类）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类图标">
          <el-input v-model="formData.icon" placeholder="Element Plus 图标名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.orderNum" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="courseCategory">
import { ref, reactive, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { getCourseCategoryTreeApi, addCourseCategoryApi, updateCourseCategoryApi, deleteCourseCategoryApi } from "@/api/modules/course";
import { sanitizeMenuIcon } from "@/utils/menuIcon";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const categoryTree = ref([]);

// 表单字段与后端 CrsCategoryAddDTO / UpdateDTO 保持一致
const formData = reactive({
  categoryId: null,
  parentId: 0,
  categoryName: "",
  icon: "",
  orderNum: 0,
  status: "0"
});

const formRules = {
  categoryName: [{ required: true, message: "请输入分类名称", trigger: "blur" }]
};

const dialogTitle = computed(() => {
  if (isEdit.value) return "编辑分类";
  return formData.parentId ? "新增子分类" : "新增分类";
});

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 645,
  loading: false,
  rowConfig: { keyField: "categoryId", isHover: true },
  columnConfig: { resizable: true },
  treeConfig: {
    rowField: "categoryId",
    parentField: "parentId",
    childrenField: "children",
    expandAll: true
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
  proxyConfig: {
    ajax: {
      query: async () => {
        try {
          const res = await getCourseCategoryTreeApi();
          categoryTree.value = res.data || [];
          return res.data || [];
        } catch (error) {
          console.error("获取课程分类失败:", error);
          categoryTree.value = [];
          return [];
        }
      }
    }
  },
  columns: [
    { field: "categoryName", title: "分类名称", minWidth: 200, treeNode: true },
    { field: "icon", title: "图标", width: 100, slots: { default: "icon" } },
    { field: "orderNum", title: "排序", width: 100 },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { title: "操作", width: 280, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

const handleStatusChange = async row => {
  const prev = row.status === "0" ? "1" : "0";
  try {
    await updateCourseCategoryApi({
      categoryId: row.categoryId,
      status: row.status
    });
    ElMessage.success(row.status === "0" ? "已启用" : "已停用");
  } catch {
    row.status = prev;
  }
};

const resetForm = () => {
  Object.assign(formData, {
    categoryId: null,
    parentId: 0,
    categoryName: "",
    icon: "",
    orderNum: 0,
    status: "0"
  });
};

const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleAddChild = row => {
  isEdit.value = false;
  Object.assign(formData, {
    categoryId: null,
    parentId: row.categoryId,
    categoryName: "",
    icon: "",
    orderNum: 0,
    status: "0"
  });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    categoryId: row.categoryId,
    parentId: row.parentId ?? 0,
    categoryName: row.categoryName || "",
    icon: row.icon || "",
    orderNum: row.orderNum ?? 0,
    status: row.status ?? "0"
  });
  dialogVisible.value = true;
};

const buildPayload = () => {
  const parentId = formData.parentId == null ? 0 : formData.parentId;
  return {
    categoryId: formData.categoryId,
    parentId,
    categoryName: formData.categoryName.trim(),
    icon: formData.icon || undefined,
    orderNum: formData.orderNum ?? 0,
    status: formData.status || "0"
  };
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = buildPayload();

    if (isEdit.value) {
      await updateCourseCategoryApi(payload);
    } else {
      const { categoryId, ...addPayload } = payload;
      await addCourseCategoryApi(addPayload);
    }

    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除分类「${row.categoryName}」吗？`, "提示", {
    type: "warning",
    confirmButtonText: "确定",
    cancelButtonText: "取消"
  })
    .then(async () => {
      try {
        // 是否有子分类由后端校验并返回业务错误，前端不再本地拦截
        await deleteCourseCategoryApi(row.categoryId);
        ElMessage.success("删除成功");
        handleRefresh();
      } catch (error) {
        // 全局拦截器已弹出后端错误信息（如存在子分类）
        console.error("删除失败:", error);
      }
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
