<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增品牌</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- Logo列 -->
      <template #logo="{ row }">
        <div v-if="row.logo" class="table-thumb-cell">
          <el-image :src="row.logo" :preview-src-list="[row.logo]" class="table-thumb-square" />
        </div>
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
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑品牌' : '新增品牌'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="品牌名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入品牌名称" />
        </el-form-item>
        <el-form-item label="品牌Logo">
          <ImageLibrary
            v-model="formData.logo"
            folder="product"
            upload-action="/file/upload/product/brand"
            width="100px"
            height="100px"
          />
        </el-form-item>
        <el-form-item label="品牌描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入品牌描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="productBrand">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { getProductBrandPageApi, addProductBrandApi, updateProductBrandApi, deleteProductBrandApi } from "@/api/modules/product";
import ImageLibrary from "@/components/ImageLibrary/index.vue";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

// 表单数据
const formData = reactive({
  id: null,
  name: "",
  logo: "",
  description: "",
  sortOrder: 0,
  status: 1
});

const formRules = {
  name: [{ required: true, message: "请输入品牌名称", trigger: "blur" }]
};

// 表格配置
const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
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
  pagerConfig: {
    enabled: true,
    pageSize: 10,
    pageSizes: [10, 20, 50, 100]
  },
  proxyConfig: {
    seq: true,
    response: {
      result: "records",
      total: "total"
    },
    ajax: {
      query: async ({ page }) => {
        try {
          const res = await getProductBrandPageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize
          });
          return res.data;
        } catch (error) {
          console.error("获取品牌列表失败:", error);
          return { records: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", title: "序号", width: 60 },
    { field: "logo", title: "Logo", width: 80, slots: { default: "logo" } },
    { field: "name", title: "品牌名称", minWidth: 150 },
    { field: "description", title: "品牌描述", minWidth: 200 },
    { field: "sortOrder", title: "排序", width: 80 },
    { field: "status", title: "状态", width: 80, slots: { default: "status" } },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

// 刷新
const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

// 状态变更
const handleStatusChange = async row => {
  try {
    await updateProductBrandApi(row.id, { status: row.status });
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
    name: "",
    logo: "",
    description: "",
    sortOrder: 0,
    status: 1
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    logo: row.logo || "",
    description: row.description || "",
    sortOrder: row.sortOrder || 0,
    status: row.status
  });
  dialogVisible.value = true;
};

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    
    if (isEdit.value) {
      await updateProductBrandApi(formData.id, formData);
    } else {
      await addProductBrandApi(formData);
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

// 删除
const handleDelete = row => {
  ElMessageBox.confirm("确定要删除该品牌吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteProductBrandApi(row.id);
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
