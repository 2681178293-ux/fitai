<template>
  <div class="card p-4">
    <!-- 搜索区域 -->
    <div class="search-form mb-4">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入" clearable class="w-40" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="searchForm.nickname" placeholder="请输入" clearable class="w-40" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入" clearable class="w-40" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="searchForm.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择"
            clearable
            check-strictly
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <vxe-grid ref="gridRef" v-bind="gridOptions">
      <!-- 工具栏按钮插槽 -->
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增账号</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
      </template>

      <!-- 状态列 -->
      <template #status_default="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? "正常" : "禁用" }}
        </el-tag>
      </template>

      <!-- 头像列 -->
      <template #avatar_default="{ row }">
        <el-avatar :size="32" :src="row.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + row.id" />
      </template>

      <!-- 性别列 -->
      <template #sex_default="{ row }">
        {{ row.sex === 1 ? "男" : row.sex === 2 ? "女" : "未知" }}
      </template>

      <!-- 操作列 -->
      <template #action_default="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="primary" link :icon="Key" @click="handleResetPwd(row)">重置密码</el-button>
        <el-popconfirm
          title="确定删除该账号吗？删除后该账号将无法登录，且可能影响其名下数据的归属，请谨慎操作。"
          @confirm="handleDelete(row)"
        >
          <template #reference>
            <el-button type="danger" link :icon="Delete" :disabled="row.id === 1">删除</el-button>
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
        <el-form-item label="用户名" required>
          <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item v-if="!formData.id" label="密码" required>
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="formData.sex">
            <el-radio :value="0">未知</el-radio>
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="formData.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择部门"
            clearable
            check-strictly
            class="w-full"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="formData.roleIds" multiple placeholder="请选择角色" class="w-full">
            <el-option v-for="role in roleList" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="pwdDialogVisible" title="重置密码" width="400px">
      <el-form :model="pwdForm" label-width="80px">
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleResetPwdSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="account">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Edit, Delete, Key, Search, Refresh } from "@element-plus/icons-vue";
import {
  getSysUserList,
  getSysUserById,
  addSysUser,
  updateSysUser,
  deleteSysUser,
  resetSysUserPwd
} from "@/api/modules/system";
import { getSysDeptSelect, getSysRoleAll } from "@/api/modules/system";

const gridRef = ref();
const dialogVisible = ref(false);
const dialogTitle = ref("新增账号");
const formData = ref({});
const submitLoading = ref(false);
const loading = ref(false);

// 重置密码
const pwdDialogVisible = ref(false);
const pwdForm = ref({ userId: null, password: "" });
const pwdLoading = ref(false);

// 部门树和角色列表
const deptTree = ref([]);
const roleList = ref([]);

// 搜索表单
const searchForm = ref({
  username: "",
  nickname: "",
  phone: "",
  deptId: null,
  status: null
});

// 分页配置
const pageConfig = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 表格数据
const tableData = ref([]);

// 加载数据（必须在 gridOptions 之前定义，因为 toolbarConfig.refresh.queryMethod 引用它）
const loadData = async () => {
  loading.value = true;
  gridOptions.loading = true;
  try {
    const res = await getSysUserList({
      pageNum: pageConfig.currentPage,
      pageSize: pageConfig.pageSize,
      ...searchForm.value
    });
    tableData.value = res.data?.records || [];
    pageConfig.total = Number(res.data?.total) || 0;
  } catch (error) {
    console.error("加载用户列表失败", error);
  } finally {
    loading.value = false;
    gridOptions.loading = false;
  }
};

const gridOptions = reactive({
  id: "accountGrid",
  border: true,
  stripe: true,
  showOverflow: true,
  height: 645,
  keepSource: true,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
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
    { field: "avatar", title: "头像", width: 80, slots: { default: "avatar_default" } },
    { field: "username", title: "用户名", width: 120 },
    { field: "nickname", title: "昵称", width: 100 },
    { field: "phone", title: "手机号", width: 130 },
    { field: "email", title: "邮箱", minWidth: 180 },
    { field: "sex", title: "性别", width: 80, slots: { default: "sex_default" } },
    { field: "status", title: "状态", width: 80, slots: { default: "status_default" } },
    { field: "createTime", title: "创建时间", width: 160 },
    { field: "action", title: "操作", width: 200, fixed: "right", slots: { default: "action_default" } }
  ],
  data: tableData
});

// 加载部门和角色
const loadDeptAndRole = async () => {
  try {
    const [deptRes, roleRes] = await Promise.all([getSysDeptSelect(), getSysRoleAll()]);
    deptTree.value = deptRes.data || [];
    roleList.value = roleRes.data || [];
  } catch (error) {
    console.error("加载部门角色失败", error);
  }
};

onMounted(() => {
  loadData();
  loadDeptAndRole();
});

const handleSearch = () => {
  pageConfig.currentPage = 1;
  loadData();
};

const handleReset = () => {
  searchForm.value = { username: "", nickname: "", phone: "", deptId: null, status: null };
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
  dialogTitle.value = "新增账号";
  formData.value = { status: 1, sex: 0, roleIds: [] };
  dialogVisible.value = true;
};

const handleEdit = async row => {
  dialogTitle.value = "编辑账号";
  try {
    const res = await getSysUserById(row.id);
    formData.value = { ...(res.data?.user || {}), roleIds: res.data?.roleIds || [] };
    dialogVisible.value = true;
  } catch (error) {
    ElMessage.error("获取用户信息失败");
  }
};

const handleDelete = async row => {
  try {
    await deleteSysUser(row.id);
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
  // 过滤掉超级管理员
  const ids = records.filter(r => r.id !== 1).map(r => r.id);
  if (ids.length === 0) {
    ElMessage.warning("超级管理员不允许删除");
    return;
  }
  try {
    await deleteSysUser(ids.join(","));
    ElMessage.success("批量删除成功");
    loadData();
  } catch (error) {
    ElMessage.error("批量删除失败");
  }
};

const handleResetPwd = row => {
  pwdForm.value = { userId: row.id, password: "" };
  pwdDialogVisible.value = true;
};

const handleResetPwdSubmit = async () => {
  if (!pwdForm.value.password) {
    ElMessage.warning("请输入新密码");
    return;
  }
  pwdLoading.value = true;
  try {
    await resetSysUserPwd(pwdForm.value.userId, pwdForm.value.password);
    ElMessage.success("密码重置成功");
    pwdDialogVisible.value = false;
  } catch (error) {
    ElMessage.error("密码重置失败");
  } finally {
    pwdLoading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formData.value.username) {
    ElMessage.warning("请输入用户名");
    return;
  }
  if (!formData.value.id && !formData.value.password) {
    ElMessage.warning("请输入密码");
    return;
  }

  submitLoading.value = true;
  try {
    if (formData.value.id) {
      await updateSysUser(formData.value);
      ElMessage.success("编辑成功");
    } else {
      await addSysUser(formData.value);
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

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 16px 0;
  margin-top: 16px;
}
</style>
