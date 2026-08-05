<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增等级</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #icon="{ row }">
        <el-image
          v-if="row.icon"
          :src="row.icon"
          :preview-src-list="[row.icon]"
          :preview-teleported="true"
          fit="cover"
          class="level-icon"
        />
        <span v-else class="text-gray-400">-</span>
      </template>

      <template #exp="{ row }">
        <span>{{ row.minExp ?? 0 }} ~ {{ row.maxExp != null ? row.maxExp : "不限" }}</span>
      </template>

      <template #benefitNames="{ row }">
        <template v-if="row.benefitNames && row.benefitNames.length">
          <el-tag v-for="(n, i) in row.benefitNames" :key="i" class="mr-1" size="small" type="success">{{ n }}</el-tag>
        </template>
        <span v-else class="text-gray-400">-</span>
      </template>

      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "启用" : "停用" }}</el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑会员等级' : '新增会员等级'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="等级值" prop="level">
          <el-input-number v-model="formData.level" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="等级名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：青铜会员" />
        </el-form-item>
        <el-form-item label="最小经验值" prop="minExp">
          <el-input-number v-model="formData.minExp" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大经验值" prop="maxExp">
          <el-input-number v-model="formData.maxExp" :min="0" style="width: 100%" placeholder="留空表示不限" />
        </el-form-item>
        <el-form-item label="等级图标">
          <ImageLibrary v-model="formData.icon" folder="level" upload-action="/file/upload/image" width="120px" height="120px" />
        </el-form-item>
        <el-form-item label="关联权益">
          <el-select
            v-model="formData.benefitIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            placeholder="请选择该等级享有的权益"
            style="width: 100%"
          >
            <el-option v-for="item in benefitOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="权益说明">
          <el-input v-model="formData.benefits" type="textarea" :rows="3" placeholder="等级权益文字说明" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="memberLevel">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import ImageLibrary from "@/components/ImageLibrary/index.vue";
import {
  getLevelPageApi,
  getLevelDetailApi,
  createLevelApi,
  updateLevelApi,
  deleteLevelApi,
  getBenefitListApi
} from "@/api/modules/member";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const benefitOptions = ref([]);

const defaultForm = {
  id: null,
  level: 1,
  name: "",
  minExp: 0,
  maxExp: null,
  icon: "",
  benefits: "",
  benefitIds: [],
  status: 1
};

const formData = reactive({ ...defaultForm });

const formRules = {
  level: [{ required: true, message: "请输入等级值", trigger: "blur" }],
  name: [{ required: true, message: "请输入等级名称", trigger: "blur" }],
  minExp: [{ required: true, message: "请输入最小经验值", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
  checkboxConfig: { highlight: true },
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
    pageSizes: [10, 20, 50]
  },
  proxyConfig: {
    seq: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page }) => {
        try {
          const res = await getLevelPageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize
          });
          const pageData = res.data || {};
          return { list: pageData.records || [], total: pageData.total || 0 };
        } catch (e) {
          console.error(e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "checkbox", width: 50 },
    { type: "seq", title: "序号", width: 60 },
    { field: "level", title: "等级值", width: 90 },
    { field: "name", title: "等级名称", minWidth: 140 },
    { field: "minExp", title: "经验区间", width: 160, slots: { default: "exp" } },
    { field: "icon", title: "图标", width: 80, slots: { default: "icon" } },
    { field: "benefitNames", title: "关联权益", minWidth: 200, slots: { default: "benefitNames" } },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const loadBenefitOptions = async () => {
  try {
    const res = await getBenefitListApi();
    benefitOptions.value = Array.isArray(res?.data) ? res.data : [];
  } catch (e) {
    console.error(e);
    benefitOptions.value = [];
  }
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { ...defaultForm, benefitIds: [] });
  dialogVisible.value = true;
};

const handleEdit = async row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    level: row.level ?? 1,
    name: row.name || "",
    minExp: row.minExp ?? 0,
    maxExp: row.maxExp ?? null,
    icon: row.icon || "",
    benefits: row.benefits || "",
    benefitIds: Array.isArray(row.benefitIds) ? [...row.benefitIds] : [],
    status: row.status ?? 1
  });
  dialogVisible.value = true;
  try {
    const res = await getLevelDetailApi(row.id);
    const detail = res?.data;
    if (detail) {
      formData.benefitIds = Array.isArray(detail.benefitIds) ? [...detail.benefitIds] : [];
      formData.benefits = detail.benefits || formData.benefits;
    }
  } catch (e) {
    console.error(e);
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      level: formData.level,
      name: formData.name,
      minExp: formData.minExp,
      maxExp: formData.maxExp,
      icon: formData.icon,
      benefits: formData.benefits,
      benefitIds: formData.benefitIds,
      status: formData.status
    };
    if (isEdit.value) {
      await updateLevelApi(formData.id, payload);
      ElMessage.success("更新成功");
    } else {
      await createLevelApi(payload);
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    handleRefresh();
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除等级「${row.name}」吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteLevelApi(row.id);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};

const handleBatchDelete = () => {
  const records = xGrid.value?.getCheckboxRecords() || [];
  if (records.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  const ids = records.map(r => r.id).join(",");
  ElMessageBox.confirm(`确定删除选中的 ${records.length} 个等级吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteLevelApi(ids);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};

onMounted(() => {
  loadBenefitOptions();
});
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.level-icon {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  cursor: pointer;
}
</style>
