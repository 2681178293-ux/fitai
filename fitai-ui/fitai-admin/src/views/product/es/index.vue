<template>
  <div class="table-box p-5">
    <el-card shadow="never" class="mb-4">
      <template #header>
        <div class="flex items-center justify-between">
          <span class="font-medium">ES 索引状态</span>
          <el-button :icon="Refresh" :loading="statusLoading" @click="loadStatus">刷新状态</el-button>
        </div>
      </template>

      <div v-loading="statusLoading">
        <div class="mb-3 flex items-center gap-3">
          <span>启用状态：</span>
          <el-tag :type="status.enabled ? 'success' : 'info'">
            {{ status.enabled ? '已启用' : '未启用' }}
          </el-tag>
        </div>
        <el-alert
          v-if="status.message"
          :title="status.message"
          :type="status.enabled ? 'info' : 'warning'"
          :closable="false"
          class="mb-3"
        />
        <el-descriptions v-if="status.enabled && otherFields.length" :column="2" border>
          <el-descriptions-item v-for="item in otherFields" :key="item.key" :label="item.key">
            {{ item.value }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <span class="font-medium">索引操作</span>
      </template>
      <div class="flex flex-col gap-4">
        <div class="op-row">
          <div>
            <div class="font-medium">全量重建索引</div>
            <div class="text-gray-400 text-sm">删除现有索引并重建，重新灌入全部上架商品，耗时取决于数据量。</div>
          </div>
          <el-button type="primary" :loading="rebuildLoading" :disabled="!status.enabled" @click="handleRebuild">
            重建索引
          </el-button>
        </div>
        <el-divider class="my-0" />
        <div class="op-row">
          <div>
            <div class="font-medium text-red-500">删除索引</div>
            <div class="text-gray-400 text-sm">删除整个 ES 索引，删除后商品搜索将不可用，需重建恢复。</div>
          </div>
          <el-button type="danger" :loading="deleteLoading" :disabled="!status.enabled" @click="handleDelete">
            删除索引
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup name="productEs">
import { ref, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh } from "@element-plus/icons-vue";
import { getEsStatusApi, rebuildEsApi, deleteEsIndexApi } from "@/api/modules/mall-ext";

const status = ref({ enabled: false });
const statusLoading = ref(false);
const rebuildLoading = ref(false);
const deleteLoading = ref(false);

const otherFields = computed(() =>
  Object.entries(status.value || {})
    .filter(([k]) => k !== "enabled" && k !== "message")
    .map(([key, value]) => ({ key, value: typeof value === "object" ? JSON.stringify(value) : value }))
);

const loadStatus = async () => {
  statusLoading.value = true;
  try {
    const res = await getEsStatusApi();
    status.value = res.data || { enabled: false };
  } catch (e) {
    status.value = { enabled: false };
  } finally {
    statusLoading.value = false;
  }
};

const handleRebuild = () => {
  ElMessageBox.confirm("确定全量重建 ES 索引？将删除现有索引并重新灌入全部上架商品。", "重建确认", {
    type: "warning"
  }).then(async () => {
    rebuildLoading.value = true;
    try {
      await rebuildEsApi();
      ElMessage.success("重建完成");
      loadStatus();
    } catch (e) {
      /* 拦截器已提示 */
    } finally {
      rebuildLoading.value = false;
    }
  });
};

const handleDelete = () => {
  ElMessageBox.confirm("确定删除 ES 索引？删除后商品搜索将不可用，需重建恢复。", "删除确认", {
    type: "warning"
  }).then(async () => {
    deleteLoading.value = true;
    try {
      await deleteEsIndexApi();
      ElMessage.success("索引已删除");
      loadStatus();
    } catch (e) {
      /* 拦截器已提示 */
    } finally {
      deleteLoading.value = false;
    }
  });
};

onMounted(loadStatus);
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.op-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
