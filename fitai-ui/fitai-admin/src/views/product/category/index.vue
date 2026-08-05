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
          :active-value="1"
          :inactive-value="0"
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
            :props="{ value: 'id', label: 'name', checkStrictly: true, emitPath: false }"
            placeholder="无（顶级分类）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类图标">
          <el-input v-model="formData.icon" placeholder="Element Plus 图标名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="productCategory">
import { ref, reactive, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { getProductCategoryTreeApi, addProductCategoryApi, updateProductCategoryApi, deleteProductCategoryApi } from "@/api/modules/product";
import { sanitizeMenuIcon } from "@/utils/menuIcon";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const categoryTree = ref([]);

// 表单数据
const formData = reactive({
  id: null,
  parentId: 0,
  name: "",
  icon: "",
  sortOrder: 0
});

const formRules = {
  name: [{ required: true, message: "请输入分类名称", trigger: "blur" }]
};

const dialogTitle = computed(() => {
  if (isEdit.value) return "编辑分类";
  return formData.parentId ? "新增子分类" : "新增分类";
});

// 表格配置
const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 645,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
  treeConfig: {
    rowField: "id",
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
          const res = await getProductCategoryTreeApi();
          categoryTree.value = res.data || [];
          return res.data || [];
        } catch (error) {
          console.error("获取商品分类失败:", error);
          categoryTree.value = [];
          return [];
        }
      }
    }
  },
  columns: [
    { field: "name", title: "分类名称", minWidth: 200, treeNode: true },
    { field: "icon", title: "图标", width: 100, slots: { default: "icon" } },
    { field: "sortOrder", title: "排序", width: 100 },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { title: "操作", width: 280, fixed: "right", slots: { default: "action" } }
  ]
});

// 刷新
const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

// 状态变更
const handleStatusChange = async row => {
  try {
    await updateProductCategoryApi(row.id, { status: row.status });
    ElMessage.success(row.status === 1 ? "已启用" : "已禁用");
  } catch {
    row.status = row.status === 1 ? 0 : 1;
  }
};

// 新增
const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: null,
    parentId: 0,
    name: "",
    icon: "",
    sortOrder: 0
  });
  dialogVisible.value = true;
};

// 新增子级
const handleAddChild = row => {
  isEdit.value = false;
  Object.assign(formData, {
    id: null,
    parentId: row.id,
    name: "",
    icon: "",
    sortOrder: 0
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    parentId: row.parentId,
    name: row.name,
    icon: row.icon,
    sortOrder: row.sortOrder
  });
  dialogVisible.value = true;
};

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    
    if (isEdit.value) {
      await updateProductCategoryApi(formData.id, formData);
    } else {
      await addProductCategoryApi(formData);
    }
    
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (error) {
    console.error("提交失败:", error);
    ElMessage.error("操作失败，请重试");
  } finally {
    submitLoading.value = false;
  }
};

// 删除
const handleDelete = row => {
  if (row.children?.length > 0) {
    ElMessage.warning("请先删除子分类");
    return;
  }
  ElMessageBox.confirm("确定要删除该分类吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteProductCategoryApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error("删除失败，请重试");
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
