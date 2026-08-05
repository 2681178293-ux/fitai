<template>
  <div class="card p-4">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 操作日志 -->
      <el-tab-pane label="操作日志" name="oper">
        <div class="search-form mb-4">
          <el-form :model="operSearch" inline>
            <el-form-item label="模块">
              <el-input v-model="operSearch.title" placeholder="请输入" clearable class="w-32" />
            </el-form-item>
            <el-form-item label="操作人">
              <el-input v-model="operSearch.operName" placeholder="请输入" clearable class="w-32" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="operSearch.businessType" placeholder="请选择" clearable style="width: 100px">
                <el-option label="其他" :value="0" />
                <el-option label="新增" :value="1" />
                <el-option label="修改" :value="2" />
                <el-option label="删除" :value="3" />
                <el-option label="查询" :value="4" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="operSearch.status" placeholder="请选择" clearable style="width: 100px">
                <el-option label="成功" :value="1" />
                <el-option label="失败" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="loadOperLog">搜索</el-button>
              <el-button :icon="Refresh" @click="handleOperReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="mb-2">
          <el-popconfirm title="确定清空所有操作日志吗?" @confirm="handleCleanOperLog">
            <template #reference>
              <el-button type="danger" size="small" :icon="Delete">清空日志</el-button>
            </template>
          </el-popconfirm>
        </div>

        <el-table :data="operLogList" border height="400" v-loading="operLoading">
          <el-table-column prop="title" label="模块" width="120" />
          <el-table-column label="类型" width="80">
            <template #default="{ row }">
              <el-tag size="small" :type="['info', 'primary', 'warning', 'danger', 'success'][row.businessType] || 'info'">
                {{ ["其他", "新增", "修改", "删除", "查询"][row.businessType] || "其他" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operName" label="操作人" width="100" />
          <el-table-column prop="operUrl" label="请求URL" min-width="200" show-overflow-tooltip />
          <el-table-column prop="operIp" label="IP" width="120" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag size="small" :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? "成功" : "失败" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="costTime" label="耗时(ms)" width="100" />
          <el-table-column prop="operTime" label="操作时间" width="160" />
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleViewOperLog(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="operPage.currentPage"
            v-model:page-size="operPage.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="operPage.total"
            background
            layout="total, sizes, prev, pager, next"
            @size-change="loadOperLog"
            @current-change="loadOperLog"
          />
        </div>
      </el-tab-pane>

      <!-- 登录日志 -->
      <el-tab-pane label="登录日志" name="login">
        <div class="search-form mb-4">
          <el-form :model="loginSearch" inline>
            <el-form-item label="用户名">
              <el-input v-model="loginSearch.username" placeholder="请输入" clearable class="w-32" />
            </el-form-item>
            <el-form-item label="IP">
              <el-input v-model="loginSearch.ip" placeholder="请输入" clearable class="w-32" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="loginSearch.status" placeholder="请选择" clearable style="width: 100px">
                <el-option label="成功" :value="1" />
                <el-option label="失败" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="客户端">
              <el-select v-model="loginSearch.clientType" placeholder="全部" clearable style="width: 120px">
                <el-option label="管理端" value="admin" />
                <el-option label="PC 用户端" value="pc" />
                <el-option label="移动端" value="mobile" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="loadLoginLog">搜索</el-button>
              <el-button :icon="Refresh" @click="handleLoginReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="mb-2">
          <el-popconfirm title="确定清空所有登录日志吗?" @confirm="handleCleanLoginLog">
            <template #reference>
              <el-button type="danger" size="small" :icon="Delete">清空日志</el-button>
            </template>
          </el-popconfirm>
        </div>

        <el-table :data="loginLogList" border height="400" v-loading="loginLoading">
          <el-table-column prop="userName" label="用户名" width="120" />
          <el-table-column prop="loginIp" label="IP地址" width="140" />
          <el-table-column prop="loginLocation" label="登录地点" width="150" />
          <el-table-column prop="browser" label="浏览器" width="120" />
          <el-table-column prop="os" label="操作系统" width="120" />
          <el-table-column label="客户端" width="100">
            <template #default="{ row }">
              <span>{{ clientTypeLabel(row.clientType) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag size="small" :type="row.status == 1 ? 'success' : 'danger'">
                {{ row.status == 1 ? "成功" : "失败" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="msg" label="消息" min-width="100" show-overflow-tooltip />
          <el-table-column prop="loginTime" label="登录时间" width="200" />
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="loginPage.currentPage"
            v-model:page-size="loginPage.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="loginPage.total"
            background
            layout="total, sizes, prev, pager, next"
            @size-change="loadLoginLog"
            @current-change="loadLoginLog"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 操作日志详情弹窗 -->
    <el-dialog v-model="operDetailVisible" title="操作日志详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="模块">{{ operDetail.title }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ ["其他", "新增", "修改", "删除", "查询"][operDetail.businessType] }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ operDetail.operName }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ operDetail.operTime }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ operDetail.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="操作状态">
          <el-tag :type="operDetail.status === 1 ? 'success' : 'danger'">{{ operDetail.status === 1 ? "成功" : "失败" }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ operDetail.operUrl }}</el-descriptions-item>
        <el-descriptions-item label="方法名" :span="2">{{ operDetail.method }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre class="text-xs max-h-40 overflow-auto">{{ operDetail.operParam }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="返回结果" :span="2">
          <pre class="text-xs max-h-40 overflow-auto">{{ operDetail.jsonResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item v-if="operDetail.errorMsg" label="错误信息" :span="2">
          <span class="text-red-500">{{ operDetail.errorMsg }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="log">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Search, Refresh, Delete } from "@element-plus/icons-vue";
import {
  getSysOperLogList,
  getSysOperLogById,
  cleanSysOperLog,
  getSysLoginLogList,
  cleanSysLoginLog
} from "@/api/modules/system";

const activeTab = ref("oper");

// 操作日志
const operSearch = ref({ title: "", operName: "", businessType: null, status: null });
const operLogList = ref([]);
const operLoading = ref(false);
const operPage = reactive({ currentPage: 1, pageSize: 10, total: 0 });
const operDetailVisible = ref(false);
const operDetail = ref({});

// 登录日志
const loginSearch = ref({ username: "", ip: "", status: null, clientType: null });

const clientTypeLabel = v => {
  if (!v) return "—";
  const map = { admin: "管理端", pc: "PC 用户端", mobile: "移动端" };
  return map[v] || v;
};
const loginLogList = ref([]);
const loginLoading = ref(false);
const loginPage = reactive({ currentPage: 1, pageSize: 10, total: 0 });

const loadOperLog = async () => {
  operLoading.value = true;
  try {
    const res = await getSysOperLogList({
      pageNum: operPage.currentPage,
      pageSize: operPage.pageSize,
      ...operSearch.value
    });
    operLogList.value = res.data?.records || [];
    operPage.total = Number(res.data?.total) || 0;
  } catch (error) {
    console.error("加载操作日志失败", error);
  } finally {
    operLoading.value = false;
  }
};

const loadLoginLog = async () => {
  loginLoading.value = true;
  try {
    const res = await getSysLoginLogList({
      pageNum: loginPage.currentPage,
      pageSize: loginPage.pageSize,
      ...loginSearch.value
    });
    loginLogList.value = res.data?.records || [];
    loginPage.total = Number(res.data?.total) || 0;
  } catch (error) {
    console.error("加载登录日志失败", error);
  } finally {
    loginLoading.value = false;
  }
};

onMounted(() => {
  loadOperLog();
});

const handleTabChange = tab => {
  if (tab === "login" && loginLogList.value.length === 0) {
    loadLoginLog();
  }
};

const handleOperReset = () => {
  operSearch.value = { title: "", operName: "", businessType: null, status: null };
  operPage.currentPage = 1;
  loadOperLog();
};

const handleLoginReset = () => {
  loginSearch.value = { username: "", ip: "", status: null, clientType: null };
  loginPage.currentPage = 1;
  loadLoginLog();
};

const handleViewOperLog = async row => {
  try {
    const res = await getSysOperLogById(row.id);
    operDetail.value = res.data || row;
    operDetailVisible.value = true;
  } catch (error) {
    operDetail.value = row;
    operDetailVisible.value = true;
  }
};

const handleCleanOperLog = async () => {
  try {
    await cleanSysOperLog();
    ElMessage.success("清空成功");
    loadOperLog();
  } catch (error) {
    ElMessage.error("清空失败");
  }
};

const handleCleanLoginLog = async () => {
  try {
    await cleanSysLoginLog();
    ElMessage.success("清空成功");
    loadLoginLog();
  } catch (error) {
    ElMessage.error("清空失败");
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
}
</style>
