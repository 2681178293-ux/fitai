<template>
  <div class="card p-4">
    <!-- 搜索区域 -->
    <el-form :model="searchForm" inline class="search-form mb-4">
      <el-form-item label="菜单名称">
        <el-input v-model="searchForm.menuName" placeholder="请输入菜单名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="显示" :value="1" />
          <el-option label="隐藏" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <vxe-grid ref="gridRef" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增菜单</el-button>
        <el-button :icon="Sort" @click="toggleExpand">展开/折叠</el-button>
        <el-button type="warning" plain :icon="RefreshRight" :loading="invalidateLoading" @click="handleInvalidatePermCache">
          清空权限缓存
        </el-button>
      </template>

      <template #icon_default="{ row }">
        <el-icon v-if="sanitizeMenuIcon(row.icon)"><component :is="sanitizeMenuIcon(row.icon)" /></el-icon>
      </template>

      <template #type_default="{ row }">
        <el-tag :type="row.menuType === 'M' ? 'primary' : row.menuType === 'C' ? 'success' : 'warning'">
          {{ row.menuType === "M" ? "目录" : row.menuType === "C" ? "菜单" : "按钮" }}
        </el-tag>
      </template>

      <template #status_default="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? "显示" : "隐藏" }}
        </el-tag>
      </template>

      <template #action_default="{ row }">
        <el-button type="primary" link :icon="Plus" @click="handleAddChild(row)">新增</el-button>
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-popconfirm title="确定删除该菜单吗?" @confirm="handleDelete(row)">
          <template #reference>
            <el-button type="danger" link :icon="Delete">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </vxe-grid>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="formData.parentId"
            :data="menuOptions"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            check-strictly
            clearable
            placeholder="请选择上级菜单"
            class="w-full"
          />
        </el-form-item>
        <el-form-item label="菜单类型" required>
          <el-radio-group v-model="formData.menuType">
            <el-radio value="M">目录</el-radio>
            <el-radio value="C">菜单</el-radio>
            <el-radio value="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" required>
          <el-input v-model="formData.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item v-if="formData.menuType !== 'F'" label="菜单图标">
          <el-input v-model="formData.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item v-if="formData.menuType !== 'F'" label="路由路径">
          <el-input v-model="formData.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item v-if="formData.menuType === 'C'" label="组件路径">
          <el-input v-model="formData.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="formData.perms" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="是否显示">
          <el-switch v-model="formData.visible" :active-value="1" :inactive-value="0" />
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

<script setup name="menuManage">
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Edit, Delete, Sort, Search, Refresh, RefreshRight } from "@element-plus/icons-vue";
import {
  getSysMenuList,
  getSysMenuById,
  addSysMenu,
  updateSysMenu,
  deleteSysMenu,
  invalidateSysMenuPermCache
} from "@/api/modules/system";
import { sanitizeMenuIcon } from "@/utils/menuIcon";

const gridRef = ref();
const dialogVisible = ref(false);
const dialogTitle = ref("新增菜单");
const formData = ref({});
const submitLoading = ref(false);
const invalidateLoading = ref(false);
const isExpanded = ref(true);
const tableData = ref([]);

const searchForm = reactive({
  menuName: "",
  status: null
});

// 递归过滤树形数据
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
  if (!searchForm.menuName && searchForm.status === null) {
    return tableData.value;
  }
  return filterTree(tableData.value, node => {
    const nameMatch = !searchForm.menuName || node.menuName.includes(searchForm.menuName);
    const statusMatch = searchForm.status === null || node.status === searchForm.status;
    return nameMatch && statusMatch;
  });
});

const menuOptions = computed(() => {
  return [{ id: 0, menuName: "根目录", children: tableData.value }];
});

// loadData 必须在 gridOptions 之前定义
const loadData = async () => {
  gridOptions.loading = true;
  try {
    const res = await getSysMenuList();
    tableData.value = res.data || [];
  } catch (error) {
    console.error("加载菜单列表失败", error);
  } finally {
    gridOptions.loading = false;
  }
};

const gridOptions = reactive({
  id: "menuGrid",
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
    { field: "menuName", title: "菜单名称", minWidth: 200, treeNode: true },
    { field: "icon", title: "图标", width: 80, slots: { default: "icon_default" } },
    { field: "path", title: "路由路径", width: 200 },
    { field: "component", title: "组件路径", width: 200 },
    { field: "perms", title: "权限标识", width: 150 },
    { field: "menuType", title: "类型", width: 80, slots: { default: "type_default" } },
    { field: "sortOrder", title: "排序", width: 80 },
    { field: "status", title: "状态", width: 80, slots: { default: "status_default" } },
    { field: "action", title: "操作", width: 200, fixed: "right", slots: { default: "action_default" } }
  ],
  data: filteredData
});

onMounted(() => {
  loadData();
});

const handleSearch = () => {
  // filteredData 是 computed，自动更新
};

const handleReset = () => {
  searchForm.menuName = "";
  searchForm.status = null;
};

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value;
  gridRef.value.setAllTreeExpand(isExpanded.value);
};

const handleInvalidatePermCache = async () => {
  try {
    await ElMessageBox.confirm(
      "将清空 Redis 中所有用户的权限字符串缓存，下次访问会重新从数据库加载。批量改菜单或权限异常时可使用。是否继续？",
      "清空权限缓存",
      { type: "warning", confirmButtonText: "确定清空", cancelButtonText: "取消" }
    );
  } catch {
    return;
  }
  invalidateLoading.value = true;
  try {
    await invalidateSysMenuPermCache();
    ElMessage.success("已清空全部用户权限缓存");
  } catch (error) {
    ElMessage.error(error.message || "操作失败");
  } finally {
    invalidateLoading.value = false;
  }
};

const handleAdd = () => {
  dialogTitle.value = "新增菜单";
  formData.value = { status: 1, visible: 1, sortOrder: 0, menuType: "C", parentId: 0 };
  dialogVisible.value = true;
};

const handleAddChild = row => {
  dialogTitle.value = "新增子菜单";
  formData.value = { status: 1, visible: 1, sortOrder: 0, menuType: "C", parentId: row.id };
  dialogVisible.value = true;
};

const handleEdit = async row => {
  dialogTitle.value = "编辑菜单";
  try {
    const res = await getSysMenuById(row.id);
    formData.value = res.data || { ...row };
    dialogVisible.value = true;
  } catch (error) {
    formData.value = { ...row };
    dialogVisible.value = true;
  }
};

const handleDelete = async row => {
  try {
    await deleteSysMenu(row.id);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    ElMessage.error(error.message || "删除失败");
  }
};

const handleSubmit = async () => {
  if (!formData.value.menuName) {
    ElMessage.warning("请输入菜单名称");
    return;
  }
  if (!formData.value.menuType) {
    ElMessage.warning("请选择菜单类型");
    return;
  }

  submitLoading.value = true;
  try {
    if (formData.value.id) {
      await updateSysMenu(formData.value);
      ElMessage.success("编辑成功");
    } else {
      await addSysMenu(formData.value);
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
