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
          v-if="row.iconUrl"
          :src="row.iconUrl"
          :preview-src-list="[row.iconUrl]"
          :preview-teleported="true"
          fit="cover"
          class="level-icon"
        />
        <span v-else class="text-gray-400">-</span>
      </template>

      <template #exp="{ row }">
        <span>{{ row.growthMin ?? 0 }} ~ {{ row.growthMax != null ? row.growthMax : "不限" }}</span>
      </template>

      <template #benefitNames="{ row }">
        <template v-if="row.benefitNames && row.benefitNames.length">
          <el-tag v-for="(n, i) in row.benefitNames" :key="i" class="mr-1" size="small" type="success">{{ n }}</el-tag>
        </template>
        <span v-else class="text-gray-400">-</span>
      </template>

      <template #status="{ row }">
        <el-tag :type="row.status == 0 ? 'success' : 'info'">{{ row.status == 0 ? "启用" : "停用" }}</el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑会员等级' : '新增会员等级'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="等级编码" prop="levelCode">
          <el-input v-model="formData.levelCode" placeholder="如：gold" />
        </el-form-item>
        <el-form-item label="等级名称" prop="levelName">
          <el-input v-model="formData.levelName" placeholder="如：黄金会员" />
        </el-form-item>
        <el-form-item label="排序" prop="levelSort">
          <el-input-number v-model="formData.levelSort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="成长值下限" prop="growthMin">
          <el-input-number v-model="formData.growthMin" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="成长值上限" prop="growthMax">
          <el-input-number v-model="formData.growthMax" :min="0" style="width: 100%" placeholder="留空表示不限" />
        </el-form-item>
        <el-form-item label="等级图标">
          <ImageLibrary v-model="formData.iconUrl" folder="level" upload-action="/file/upload/image" width="120px" height="120px" />
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
            <el-option
              v-for="item in benefitOptions"
              :key="item.benefitId"
              :label="item.benefitName"
              :value="item.benefitId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio value="0">启用</el-radio>
            <el-radio value="1">停用</el-radio>
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
  levelId: null,
  levelCode: "",
  levelName: "",
  levelSort: 0,
  growthMin: 0,
  growthMax: null,
  iconUrl: "",
  benefitIds: [],
  status: "0"
};

const formData = reactive({ ...defaultForm });

const formRules = {
  levelCode: [{ required: true, message: "请输入等级编码", trigger: "blur" }],
  levelName: [{ required: true, message: "请输入等级名称", trigger: "blur" }],
  growthMin: [{ required: true, message: "请输入成长值下限", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "levelId", isHover: true },
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
    { field: "levelCode", title: "等级编码", width: 120 },
    { field: "levelName", title: "等级名称", minWidth: 140 },
    { field: "growthMin", title: "经验区间", width: 160, slots: { default: "exp" } },
    { field: "iconUrl", title: "图标", width: 80, slots: { default: "icon" } },
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

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    levelId: row.levelId,
    levelCode: row.levelCode || "",
    levelName: row.levelName || "",
    levelSort: row.levelSort ?? 0,
    growthMin: row.growthMin ?? 0,
    growthMax: row.growthMax ?? null,
    iconUrl: row.iconUrl || "",
    benefitIds: Array.isArray(row.benefitIds) ? [...row.benefitIds] : [],
    status: row.status != null ? String(row.status) : "0"
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    // 后端 Add/Update DTO 字段名为 icon、levelSort
    const payload = {
      levelName: formData.levelName,
      levelCode: formData.levelCode,
      levelSort: formData.levelSort,
      growthMin: formData.growthMin,
      growthMax: formData.growthMax,
      icon: formData.iconUrl,
      status: formData.status
    };
    if (isEdit.value) {
      await updateLevelApi({ ...payload, levelId: formData.levelId });
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
  ElMessageBox.confirm(`确定删除等级「${row.levelName}」吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteLevelApi(row.levelId);
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
  const ids = records.map(r => r.levelId).join(",");
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
