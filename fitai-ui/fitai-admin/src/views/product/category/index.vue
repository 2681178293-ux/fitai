<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增分类</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #icon="{ row }">
        <el-icon v-if="sanitizeMenuIcon(row.icon)" :size="20">
          <component :is="sanitizeMenuIcon(row.icon)" />
        </el-icon>
        <span v-else>-</span>
      </template>

      <template #attrNames="{ row }">
        <template v-if="row.attrNames?.length">
          <el-tag
            v-for="name in row.attrNames"
            :key="name"
            size="small"
            class="mr-1 mb-1"
            type="info"
          >
            {{ name }}
          </el-tag>
        </template>
        <span v-else class="text-gray-400">-</span>
      </template>

      <!-- 后端 status：0正常 1停用 -->
      <template #status="{ row }">
        <el-switch
          v-model="row.status"
          active-value="0"
          inactive-value="1"
          @change="handleStatusChange(row)"
        />
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Plus" @click="handleAddChild(row)">新增子级</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="父级分类">
          <el-cascader
            v-model="formData.parentId"
            :options="categoryTree"
            :props="{
              value: 'categoryId',
              label: 'categoryName',
              children: 'children',
              checkStrictly: true,
              emitPath: false
            }"
            placeholder="无（顶级分类）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类图标">
          <div class="icon-field">
            <el-input v-model="formData.icon" placeholder="Element Plus 图标名称，如 Burger">
              <template #prefix>
                <el-icon v-if="sanitizeMenuIcon(formData.icon)">
                  <component :is="sanitizeMenuIcon(formData.icon)" />
                </el-icon>
              </template>
            </el-input>
          </div>
        </el-form-item>
        <el-form-item label="关联属性">
          <div v-if="attrOptions.length" class="attr-check-wrap">
            <el-checkbox-group v-model="formData.attrIds" class="attr-checkbox-group">
              <el-checkbox
                v-for="item in attrOptions"
                :key="item.attrId"
                :value="item.attrId"
                border
              >
                {{ item.attrName }}（{{ attrTypeLabel(item.attrType) }}）
              </el-checkbox>
            </el-checkbox-group>
            <p class="attr-tip">销售属性会作为该分类商品 SKU 的可选规格，请先在属性库维护取值。</p>
          </div>
          <el-empty v-else description="暂无属性，请先在属性库维护" :image-size="64" />
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

<script setup name="productCategory">
import { ref, reactive, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getProductCategoryTreeApi,
  getProductCategoryDetailApi,
  addProductCategoryApi,
  updateProductCategoryApi,
  updateProductCategoryStatusApi,
  deleteProductCategoryApi
} from "@/api/modules/product";
import { getAttrPageApi } from "@/api/modules/mall-ext";
import { sanitizeMenuIcon } from "@/utils/menuIcon";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const categoryTree = ref([]);
const attrOptions = ref([]);

/** 与后端 PrdCategoryAddDTO / UpdateDTO 字段一致 */
const formData = reactive({
  categoryId: null,
  parentId: 0,
  categoryName: "",
  icon: "",
  orderNum: 0,
  status: "0",
  attrIds: []
});

const formRules = {
  categoryName: [{ required: true, message: "请输入分类名称", trigger: "blur" }]
};

const dialogTitle = computed(() => {
  if (isEdit.value) return "编辑分类";
  return formData.parentId ? "新增子分类" : "新增分类";
});

const attrTypeLabel = type => (String(type) === "1" ? "销售" : "基本");

const loadAttrOptions = async () => {
  try {
    const res = await getAttrPageApi({ pageNum: 1, pageSize: 1000, status: "0" });
    attrOptions.value = res.data?.records || res.data?.list || [];
  } catch (e) {
    console.error(e);
    attrOptions.value = [];
  }
};

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
          // tree 接口已返回 attrNames（PrdCategoryServiceImpl.tree）
          const treeRes = await getProductCategoryTreeApi();
          const tree = treeRes.data || [];
          categoryTree.value = tree;
          return tree;
        } catch (error) {
          console.error("获取商品分类失败:", error);
          categoryTree.value = [];
          return [];
        }
      }
    }
  },
  columns: [
    { field: "categoryName", title: "分类名称", minWidth: 200, treeNode: true },
    { field: "icon", title: "图标", width: 100, slots: { default: "icon" } },
    { field: "attrNames", title: "关联属性", minWidth: 180, slots: { default: "attrNames" } },
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
    await updateProductCategoryStatusApi(row.categoryId, { status: String(row.status) });
    ElMessage.success(row.status === "0" ? "已启用" : "已停用");
  } catch {
    row.status = prev;
  }
};

const resetForm = (extra = {}) => {
  Object.assign(formData, {
    categoryId: null,
    parentId: 0,
    categoryName: "",
    icon: "",
    orderNum: 0,
    status: "0",
    attrIds: [],
    ...extra
  });
};

const handleAdd = async () => {
  isEdit.value = false;
  resetForm();
  await loadAttrOptions();
  dialogVisible.value = true;
};

const handleAddChild = async row => {
  isEdit.value = false;
  resetForm({ parentId: row.categoryId });
  await loadAttrOptions();
  dialogVisible.value = true;
};

const fillEditForm = detail => {
  Object.assign(formData, {
    categoryId: detail.categoryId,
    parentId: detail.parentId ?? 0,
    categoryName: detail.categoryName || "",
    icon: detail.icon || "",
    orderNum: detail.orderNum ?? 0,
    status: detail.status != null ? String(detail.status) : "0",
    attrIds: Array.isArray(detail.attrIds) ? [...detail.attrIds] : []
  });
};

const handleEdit = async row => {
  isEdit.value = true;
  const categoryId = row.categoryId ?? row.id;
  await loadAttrOptions();
  try {
    const res = await getProductCategoryDetailApi(categoryId);
    fillEditForm(res.data || row);
  } catch (e) {
    console.error("获取分类详情失败，回退列表行数据:", e);
    fillEditForm(row);
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;

    const parentId = formData.parentId || 0;
    const payload = {
      parentId,
      categoryName: formData.categoryName,
      icon: formData.icon || undefined,
      orderNum: formData.orderNum,
      status: formData.status || "0",
      attrIds: formData.attrIds || []
    };

    if (isEdit.value) {
      await updateProductCategoryApi({
        ...payload,
        categoryId: formData.categoryId
      });
    } else {
      await addProductCategoryApi(payload);
    }

    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (error) {
    console.error("提交失败:", error);
    if (error !== false) ElMessage.error(error?.msg || "操作失败，请重试");
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定要删除该分类吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteProductCategoryApi(row.categoryId);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      console.error("删除失败:", error);
    }
  });
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.icon-field {
  width: 100%;
}

.attr-check-wrap {
  width: 100%;
}

.attr-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.attr-checkbox-group :deep(.el-checkbox) {
  margin-right: 0;
  height: auto;
  padding: 8px 12px;
  border-radius: 6px;
}

.attr-tip {
  margin: 10px 0 0;
  font-size: 12px;
  line-height: 1.5;
  color: #909399;
}
</style>
