<template>
  <div class="card p-4">
    <!-- 搜索区域 -->
    <div class="search-form mb-4">
      <el-form :model="searchForm" inline>
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入" clearable class="w-40" />
        </el-form-item>
        <el-form-item label="角色标识">
          <el-input v-model="searchForm.roleKey" placeholder="请输入" clearable class="w-40" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="正常" value="0" />
            <el-option label="禁用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <vxe-grid ref="gridRef" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
      </template>

      <template #status_default="{ row }">
        <el-tag :type="row.status === '0' ? 'success' : 'danger'">
          {{ row.status === '0' ? "正常" : "禁用" }}
        </el-tag>
      </template>

      <template #action_default="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="primary" link :icon="Setting" @click="handlePermission(row)">权限</el-button>
        <el-popconfirm title="确定删除该角色吗?" @confirm="handleDelete(row)">
          <template #reference>
            <el-button type="danger" link :icon="Delete" :disabled="row.roleId === 1">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </vxe-grid>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pageConfig.currentPage"
        v-model:page-size="pageConfig.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pageConfig.total"
        background
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="角色名称" required>
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" required>
          <el-input v-model="formData.roleKey" placeholder="请输入角色标识" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.roleSort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="数据范围">
          <el-select v-model="formData.dataScope" placeholder="请选择" class="w-full">
            <el-option label="全部数据" value="1" />
            <el-option label="本部门数据" value="2" />
            <el-option label="本部门及以下" value="3" />
            <el-option label="仅本人数据" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" active-value="0" inactive-value="1" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限配置弹窗 -->
    <el-dialog v-model="permissionVisible" title="权限配置" width="500px">
      <el-tree
        ref="treeRef"
        :data="menuTree"
        show-checkbox
        node-key="menuId"
        check-strictly
        :props="{ label: 'menuName', children: 'children' }"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permissionVisible = false">取消</el-button>
        <el-button type="primary" :loading="permLoading" @click="handleSavePermission">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="role">
import { ref, reactive, onMounted, nextTick } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Edit, Delete, Setting, Search, Refresh } from "@element-plus/icons-vue";
import {
  getSysRoleList,
  getSysRoleById,
  addSysRole,
  updateSysRole,
  deleteSysRole,
  getSysMenuList
} from "@/api/modules/system";

const gridRef = ref();
const treeRef = ref();
const dialogVisible = ref(false);
const permissionVisible = ref(false);
const dialogTitle = ref("新增角色");
const formData = ref({});
const submitLoading = ref(false);
const permLoading = ref(false);
const checkedKeys = ref([]);
const currentRole = ref(null);
const menuTree = ref([]);
const tableData = ref([]);

const searchForm = ref({
  roleName: "",
  roleKey: "",
  status: null
});

const pageConfig = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// loadData 必须在 gridOptions 之前定义
const loadData = async () => {
  gridOptions.loading = true;
  try {
    const res = await getSysRoleList({
      pageNum: pageConfig.currentPage,
      pageSize: pageConfig.pageSize,
      ...searchForm.value
    });
    tableData.value = res.data?.records || [];
    pageConfig.total = Number(res.data?.total) || 0;
  } catch (error) {
    console.error("加载角色列表失败", error);
  } finally {
    gridOptions.loading = false;
  }
};

const gridOptions = reactive({
  id: "roleGrid",
  border: true,
  stripe: true,
  showOverflow: true,
  height: 645,
  keepSource: true,
  loading: false,
  rowConfig: { keyField: "roleId", isHover: true },
  columnConfig: { resizable: true },
  checkboxConfig: { highlight: true },
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
    { type: "checkbox", width: 50 },
    { type: "seq", title: "序号", width: 60 },
    { field: "roleName", title: "角色名称", width: 150 },
    { field: "roleKey", title: "角色标识", width: 120 },
    { field: "roleSort", title: "排序", width: 80 },
    { field: "status", title: "状态", width: 80, slots: { default: "status_default" } },
    { field: "remark", title: "备注", minWidth: 200 },
    { field: "createTime", title: "创建时间", width: 160 },
    { field: "action", title: "操作", width: 200, fixed: "right", slots: { default: "action_default" } }
  ],
  data: tableData
});

const loadMenuTree = async () => {
  try {
    const res = await getSysMenuList();
    menuTree.value = res.data || [];
  } catch (error) {
    console.error("加载菜单树失败", error);
  }
};

onMounted(() => {
  loadData();
  loadMenuTree();
});

const handleSearch = () => {
  pageConfig.currentPage = 1;
  loadData();
};

const handleReset = () => {
  searchForm.value = { roleName: "", roleKey: "", status: null };
  pageConfig.currentPage = 1;
  loadData();
};

const handleSizeChange = size => {
  pageConfig.pageSize = size;
  pageConfig.currentPage = 1;
  loadData();
};

const handleCurrentChange = page => {
  pageConfig.currentPage = page;
  loadData();
};

const handleAdd = () => {
  dialogTitle.value = "新增角色";
  formData.value = { status: "0", roleSort: 0, dataScope: "1", menuIds: [] };
  dialogVisible.value = true;
};

const handleEdit = async row => {
  dialogTitle.value = "编辑角色";
  try {
    const res = await getSysRoleById(row.roleId);
    formData.value = { ...(res.data || {}), menuIds: res.data?.menuIds || [] };
    dialogVisible.value = true;
  } catch (error) {
    ElMessage.error("获取角色信息失败");
  }
};

const handleDelete = async row => {
  try {
    await deleteSysRole(row.roleId);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    ElMessage.error("删除失败");
  }
};

const handleBatchDelete = async () => {
  const records = gridRef.value.getCheckboxRecords();
  if (records.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  const ids = records.filter(r => r.roleId !== 1).map(r => r.roleId);
  if (ids.length === 0) {
    ElMessage.warning("超级管理员角色不允许删除");
    return;
  }
  try {
    await deleteSysRole(ids.join(","));
    ElMessage.success("批量删除成功");
    loadData();
  } catch (error) {
    ElMessage.error("批量删除失败");
  }
};

const handlePermission = async row => {
  currentRole.value = row;
  try {
    const res = await getSysRoleById(row.roleId);
    const menuIds = res.data?.menuIds || [];
    checkedKeys.value = menuIds;
    permissionVisible.value = true;
    await nextTick();
    if (treeRef.value) {
      treeRef.value.setCheckedKeys(menuIds);
    }
  } catch (error) {
    ElMessage.error("获取权限信息失败");
  }
};

const handleSavePermission = async () => {
  permLoading.value = true;
  try {
    const checkedNodes = treeRef.value.getCheckedKeys();
    await updateSysRole({ roleId: currentRole.value.roleId, menuIds: checkedNodes });
    ElMessage.success("权限配置保存成功");
    permissionVisible.value = false;
  } catch (error) {
    ElMessage.error("保存失败");
  } finally {
    permLoading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formData.value.roleName) {
    ElMessage.warning("请输入角色名称");
    return;
  }
  if (!formData.value.roleKey) {
    ElMessage.warning("请输入角色标识");
    return;
  }

  submitLoading.value = true;
  try {
    if (formData.value.roleId) {
      await updateSysRole(formData.value);
      ElMessage.success("编辑成功");
    } else {
      await addSysRole(formData.value);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    ElMessage.error(formData.value.roleId ? "编辑失败" : "新增失败");
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

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 16px 0;
  margin-top: 16px;
}
</style>
