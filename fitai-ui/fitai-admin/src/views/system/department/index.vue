<template>
  <div class="card p-4">
    <!-- 搜索区域 -->
    <el-form :model="searchForm" inline class="search-form mb-4">
      <el-form-item label="部门名称">
        <el-input v-model="searchForm.deptName" placeholder="请输入部门名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <vxe-grid ref="gridRef" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增部门</el-button>
        <el-button :icon="Sort" @click="toggleExpand">展开/折叠</el-button>
      </template>

      <template #status_default="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? "正常" : "禁用" }}
        </el-tag>
      </template>

      <template #action_default="{ row }">
        <el-button type="primary" link :icon="Plus" @click="handleAddChild(row)">新增</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-popconfirm title="确定删除该部门吗?" @confirm="handleDelete(row)">
          <template #reference>
            <el-button type="danger" link :icon="Delete">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </vxe-grid>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="formData.parentId"
            :data="deptOptions"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            check-strictly
            clearable
            placeholder="请选择上级部门"
            class="w-full"
          />
        </el-form-item>
        <el-form-item label="部门名称" required>
          <el-input v-model="formData.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="formData.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
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

<script setup name="department">
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Edit, Delete, Sort, Search, Refresh } from "@element-plus/icons-vue";
import { getSysDeptList, getSysDeptById, addSysDept, updateSysDept, deleteSysDept } from "@/api/modules/system";

const gridRef = ref();
const dialogVisible = ref(false);
const dialogTitle = ref("新增部门");
const formData = ref({});
const submitLoading = ref(false);
const isExpanded = ref(true);
const tableData = ref([]);

const searchForm = reactive({
  deptName: "",
  status: null
});

const filterTree = (data, predicate) => {
  return data.reduce((acc, node) => {
    const children = node.children ? filterTree(node.children, predicate) : [];
    if (predicate(node) || children.length > 0) {
      acc.push({ ...node, children: children.length > 0 ? children : node.children ? [] : undefined });
    }
    return acc;
  }, []);
};

const filteredData = computed(() => {
  if (!searchForm.deptName && searchForm.status === null) {
    return tableData.value;
  }
  return filterTree(tableData.value, node => {
    const nameMatch = !searchForm.deptName || node.deptName.includes(searchForm.deptName);
    const statusMatch = searchForm.status === null || node.status === searchForm.status;
    return nameMatch && statusMatch;
  });
});

const deptOptions = computed(() => {
  return [{ id: 0, deptName: "根部门", children: tableData.value }];
});

// loadData 必须在 gridOptions 之前定义
const loadData = async () => {
  gridOptions.loading = true;
  try {
    const res = await getSysDeptList();
    tableData.value = res.data || [];
  } catch (error) {
    console.error("加载部门列表失败", error);
  } finally {
    gridOptions.loading = false;
  }
};

const gridOptions = reactive({
  id: "deptGrid",
  border: true,
  showOverflow: true,
  height: 645,
  keepSource: true,
  loading: false,
  animat: false,
  rowConfig: { keyField: "id", isCurrent: true, isHover: true },
  columnConfig: { resizable: true },
  treeConfig: {
    childrenField: "children",
    expandAll: true,
    reserve: true
  },
  toolbarConfig: {
    slots: { buttons: "toolbar_buttons" },
    refresh: true,
    refreshOptions: { queryMethod: loadData },
    zoom: false,
    export: true,
    custom: true
  },
  exportConfig: { types: ["xlsx", "csv", "pdf"] },
  columns: [
    { field: "deptName", title: "部门名称", minWidth: 200, treeNode: true },
    { field: "leader", title: "负责人", width: 120 },
    { field: "phone", title: "联系电话", width: 150 },
    { field: "email", title: "邮箱", width: 180 },
    { field: "sortOrder", title: "排序", width: 80 },
    { field: "status", title: "状态", width: 80, slots: { default: "status_default" } },
    { field: "createTime", title: "创建时间", width: 160 },
    { field: "action", title: "操作", width: 200, fixed: "right", slots: { default: "action_default" } }
  ],
  data: filteredData
});

onMounted(() => {
  loadData();
});

const handleSearch = () => {};

const handleReset = () => {
  searchForm.deptName = "";
  searchForm.status = null;
};

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value;
  gridRef.value.setAllTreeExpand(isExpanded.value);
};

const handleAdd = () => {
  dialogTitle.value = "新增部门";
  formData.value = { status: 1, sortOrder: 0, parentId: 0 };
  dialogVisible.value = true;
};

const handleAddChild = row => {
  dialogTitle.value = "新增子部门";
  formData.value = { status: 1, sortOrder: 0, parentId: row.id };
  dialogVisible.value = true;
};

const handleEdit = async row => {
  dialogTitle.value = "编辑部门";
  try {
    const res = await getSysDeptById(row.id);
    formData.value = res.data || { ...row };
    dialogVisible.value = true;
  } catch (error) {
    formData.value = { ...row };
    dialogVisible.value = true;
  }
};

const handleDelete = async row => {
  try {
    await deleteSysDept(row.id);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    ElMessage.error(error.message || "删除失败");
  }
};

const handleSubmit = async () => {
  if (!formData.value.deptName) {
    ElMessage.warning("请输入部门名称");
    return;
  }

  submitLoading.value = true;
  try {
    if (formData.value.id) {
      await updateSysDept(formData.value);
      ElMessage.success("编辑成功");
    } else {
      await addSysDept(formData.value);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    ElMessage.error(formData.value.id ? "编辑失败" : "新增失败");
  } finally {
    submitLoading.value = false;
  }
};
</script>

<style scoped>
.card {
  background: var(--el-bg-color);
  border-radius: 4px;
}

.search-form {
  padding: 16px;
  background: var(--el-fill-color-light);
  border-radius: 4px;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 16px;
}
</style>
