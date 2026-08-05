<template>
  <div class="card p-4">
    <div class="search-form mb-4">
      <el-form :model="query" inline>
        <el-form-item label="名称">
          <el-input v-model="query.configName" placeholder="配置名称" clearable style="width: 160px" @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item label="键名">
          <el-input v-model="query.configKey" placeholder="配置键名" clearable style="width: 160px" @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="fetchData">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="mb-3 flex items-center justify-between">
      <h2 class="text-base font-semibold text-slate-700">系统配置</h2>
      <div class="space-x-2">
        <el-button type="primary" @click="openAdd">新增配置</el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="configId" label="主键" width="70" />
      <el-table-column prop="configName" label="名称" min-width="140" />
      <el-table-column prop="configKey" label="键名" min-width="180" />
      <el-table-column prop="configValue" label="值" min-width="220">
        <template #default="{ row }">
          <template v-if="isBooleanConfigRow(row)">
            <el-switch
              :model-value="isTrueString(row.configValue)"
              :loading="Boolean(row._toggleLoading)"
              inline-prompt
              active-text="开"
              inactive-text="关"
              @change="val => handleConfigToggle(row, val)"
            />
          </template>
          <span v-else class="font-mono text-sm">{{ row.configValue }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="configType" label="内置" width="80">
        <template #default="{ row }">
          <el-tag :type="row.configType === 'Y' ? 'info' : 'success'">
            {{ row.configType === 'Y' ? '系统' : '自定义' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-button
            v-if="['ai.enabled', 'sys.captcha.enable', 'sms.enabled', 'pay.alipay.enabled', 'pay.wxpay.enabled'].includes(row.configKey)"
            type="danger"
            link
            @click="handleDangerousConfig(row)"
          >
            风险修改
          </el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      class="mt-4 justify-end"
      @size-change="fetchData"
      @current-change="fetchData"
    />

    <el-dialog v-model="formVisible" :title="form.configId ? '编辑配置' : '新增配置'" width="520" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="名称" prop="configName">
          <el-input v-model="form.configName" placeholder="配置名称" />
        </el-form-item>
        <el-form-item label="键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="如 ai.enabled" />
        </el-form-item>
        <el-form-item v-if="isBooleanForm" label="值" prop="configValue">
          <div class="flex items-center gap-3">
            <el-switch
              v-model="form.configValue"
              active-value="true"
              inactive-value="false"
              inline-prompt
              active-text="开"
              inactive-text="关"
            />
            <span class="text-xs text-slate-400">存库仍为字符串 true / false</span>
          </div>
        </el-form-item>
        <el-form-item v-else label="值" prop="configValue">
          <el-input v-model="form.configValue" placeholder="配置值" />
        </el-form-item>
        <el-form-item label="内置">
          <el-radio-group v-model="form.configType">
            <el-radio value="Y">系统</el-radio>
            <el-radio value="N">自定义</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="3" placeholder="备注说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-card class="mt-6" shadow="hover">
      <template #header><span>核心开关说明</span></template>
      <el-descriptions :column="1" size="small" border>
        <el-descriptions-item label="ai.enabled">
          是否允许普通用户调用 AI（对话、训练计划、知识库问答等）。关闭后相关接口直接返回友好错误。
        </el-descriptions-item>
        <el-descriptions-item label="sms.enabled">
          是否实际发送短信验证码。关闭时只在日志中打印，不调用真实短信网关。
        </el-descriptions-item>
        <el-descriptions-item label="sys.captcha.enable">
          是否要求 AJ 行为验证码（滑块）。开启后发送短信前须先通过 /captcha/get、/captcha/check；PC 与移动端 H5 已集成；小程序等非 H5 端需关闭或改用 H5 访问。
        </el-descriptions-item>
        <el-descriptions-item label="pay.alipay.enabled">
          是否开启支付宝支付通道。关闭时前端发起支付会直接提示“支付功能暂时关闭”。
        </el-descriptions-item>
        <el-descriptions-item label="pay.wxpay.enabled">
          为后续接入微信支付预留的开关，当前 demo 版本可保持关闭。
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Refresh } from "@element-plus/icons-vue";
import { getSysConfigList, addSysConfig, updateSysConfig, deleteSysConfig } from "@/api/modules/system";

/** 约定为布尔语义的键（库中仍存 varchar: true/false） */
const BOOLEAN_CONFIG_KEYS = new Set([
  "ai.enabled",
  "sys.captcha.enable",
  "sms.enabled",
  "pay.alipay.enabled",
  "pay.wxpay.enabled"
]);

function isTrueString(val) {
  return String(val ?? "")
    .trim()
    .toLowerCase() === "true";
}

function isBooleanStringValue(val) {
  const v = String(val ?? "")
    .trim()
    .toLowerCase();
  return v === "true" || v === "false";
}

function isBooleanConfigKey(key) {
  return BOOLEAN_CONFIG_KEYS.has(key);
}

/** 表格行：已知布尔键，或值为 true/false 字符串 */
function isBooleanConfigRow(row) {
  return isBooleanConfigKey(row.configKey) || isBooleanStringValue(row.configValue);
}

const loading = ref(false);
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const query = reactive({
  configName: "",
  configKey: ""
});

const formVisible = ref(false);
const formRef = ref();
const submitLoading = ref(false);
const form = reactive({
  configId: null,
  configName: "",
  configKey: "",
  configValue: "",
  configType: "N",
  remark: ""
});

/** 弹窗：布尔键，或编辑时已是 true/false */
const isBooleanForm = computed(() => {
  if (isBooleanConfigKey(form.configKey)) return true;
  if (form.configId && isBooleanStringValue(form.configValue)) return true;
  return false;
});

watch(
  () => [formVisible.value, form.configKey],
  () => {
    if (!formVisible.value || !isBooleanConfigKey(form.configKey)) return;
    if (form.configValue === "" || form.configValue == null) {
      form.configValue = "false";
    }
  }
);

const formRules = {
  configName: [{ required: true, message: "请输入名称", trigger: "blur" }],
  configKey: [{ required: true, message: "请输入键名", trigger: "blur" }],
  configValue: [{ required: true, message: "请输入值", trigger: "blur" }]
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getSysConfigList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      configName: query.configName || undefined,
      configKey: query.configKey || undefined
    });
    const data = res.data || {};
    tableData.value = data.records || [];
    total.value = Number(data.total) || 0;
  } catch (e) {
    tableData.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const resetQuery = () => {
  query.configName = "";
  query.configKey = "";
  pageNum.value = 1;
  fetchData();
};

const openAdd = () => {
  Object.assign(form, {
    configId: null,
    configName: "",
    configKey: "",
    configValue: "",
    configType: "N",
    remark: ""
  });
  formVisible.value = true;
};

const openEdit = row => {
  Object.assign(form, {
    configId: row.configId,
    configName: row.configName,
    configKey: row.configKey,
    configValue: row.configValue,
    configType: row.configType || "N",
    remark: row.remark || ""
  });
  formVisible.value = true;
};

const submitForm = async () => {
  await formRef.value?.validate();
  submitLoading.value = true;
  try {
    if (form.configId) {
      await updateSysConfig(form);
      ElMessage.success("修改成功");
    } else {
      await addSysConfig(form);
      ElMessage.success("新增成功");
    }
    formVisible.value = false;
    fetchData();
  } catch (e) {
    ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除配置「${row.configName}」？`, "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteSysConfig(row.configId);
      ElMessage.success("删除成功");
      fetchData();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

// 针对核心开关的高危修改确认提示
/** 表格内开关：仍写回字符串 true/false */
const handleConfigToggle = async (row, val) => {
  const next = val ? "true" : "false";
  if (next === String(row.configValue ?? "").trim().toLowerCase()) return;
  row._toggleLoading = true;
  try {
    await updateSysConfig({
      configId: row.configId,
      configName: row.configName,
      configKey: row.configKey,
      configValue: next,
      configType: row.configType || "N",
      remark: row.remark || ""
    });
    row.configValue = next;
    ElMessage.success("已更新");
  } catch {
    ElMessage.error("更新失败");
  } finally {
    row._toggleLoading = false;
  }
};

const handleDangerousConfig = row => {
  ElMessageBox.confirm(
    `即将修改核心开关「${row.configKey}」，可能影响线上功能可用性（如 AI 对话、短信发送、支付能力）。\n\n建议在修改前确认：\n1）已在 README 或运维文档中说明变更原因\n2）已在业务低峰期操作\n3）相关业务方已知晓本次变更\n\n仍然确认继续编辑吗？`,
    "高危配置修改确认",
    {
      type: "warning",
      confirmButtonText: "确认，继续编辑",
      cancelButtonText: "取消"
    }
  ).then(() => {
    openEdit(row);
  });
};

onMounted(fetchData);
</script>

<style scoped>
.search-form {
  padding: 16px 20px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
}
.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 20px;
}
</style>

