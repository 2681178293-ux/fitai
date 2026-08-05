<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增权益</el-button>
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
          class="benefit-icon"
        />
        <span v-else class="text-gray-400">-</span>
      </template>

      <template #minLevel="{ row }">
        <el-tag type="warning">Lv.{{ row.minLevel ?? 0 }}</el-tag>
      </template>

      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "启用" : "停用" }}</el-tag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑会员权益' : '新增会员权益'" width="660px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="权益名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：专属课程折扣" />
        </el-form-item>
        <el-form-item label="权益编码" prop="code">
          <el-input v-model="formData.code" placeholder="如：COURSE_DISCOUNT" />
        </el-form-item>
        <el-form-item label="权益图标">
          <ImageLibrary v-model="formData.icon" folder="benefit" upload-action="/file/upload/image" width="120px" height="120px" />
        </el-form-item>
        <el-form-item label="最低等级" prop="minLevel">
          <el-input-number v-model="formData.minLevel" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="权益值">
          <el-input v-model="formData.value" placeholder="如：0.8（折扣）或 100（额度）" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="权益说明" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
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

<script setup name="memberBenefit">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import ImageLibrary from "@/components/ImageLibrary/index.vue";
import {
  getBenefitPageApi,
  createBenefitApi,
  updateBenefitApi,
  deleteBenefitApi
} from "@/api/modules/member";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const defaultForm = {
  id: null,
  name: "",
  code: "",
  icon: "",
  minLevel: 0,
  value: "",
  description: "",
  sortOrder: 0,
  status: 1
};

const formData = reactive({ ...defaultForm });

const formRules = {
  name: [{ required: true, message: "请输入权益名称", trigger: "blur" }],
  code: [{ required: true, message: "请输入权益编码", trigger: "blur" }],
  minLevel: [{ required: true, message: "请输入最低等级", trigger: "blur" }]
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
  formConfig: {
    items: [
      { field: "name", title: "权益名称", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      { field: "code", title: "权益编码", span: 6, itemRender: { name: "$input", props: { placeholder: "可选" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "启用", value: 1 },
            { label: "停用", value: 0 }
          ],
          props: { placeholder: "全部" }
        }
      },
      {
        span: 6,
        align: "center",
        itemRender: {
          name: "$buttons",
          children: [
            { props: { type: "submit", content: "搜索", status: "primary" } },
            { props: { type: "reset", content: "重置" } }
          ]
        }
      }
    ]
  },
  pagerConfig: {
    enabled: true,
    pageSize: 10,
    pageSizes: [10, 20, 50]
  },
  proxyConfig: {
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        try {
          const res = await getBenefitPageApi({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            name: form.name || undefined,
            code: form.code || undefined,
            status: form.status !== "" && form.status !== undefined && form.status !== null ? form.status : undefined
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
    { field: "icon", title: "图标", width: 80, slots: { default: "icon" } },
    { field: "name", title: "权益名称", minWidth: 140 },
    { field: "code", title: "编码", minWidth: 150 },
    { field: "description", title: "描述", minWidth: 200 },
    { field: "minLevel", title: "最低等级", width: 110, slots: { default: "minLevel" } },
    { field: "value", title: "权益值", width: 110 },
    { field: "sortOrder", title: "排序", width: 80 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { ...defaultForm });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    name: row.name || "",
    code: row.code || "",
    icon: row.icon || "",
    minLevel: row.minLevel ?? 0,
    value: row.value ?? "",
    description: row.description || "",
    sortOrder: row.sortOrder ?? 0,
    status: row.status ?? 1
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      name: formData.name,
      code: formData.code,
      icon: formData.icon,
      minLevel: formData.minLevel,
      value: formData.value,
      description: formData.description,
      sortOrder: formData.sortOrder,
      status: formData.status
    };
    if (isEdit.value) {
      await updateBenefitApi(formData.id, payload);
      ElMessage.success("更新成功");
    } else {
      await createBenefitApi(payload);
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    handleRefresh();
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除权益「${row.name}」吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteBenefitApi(row.id);
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
  ElMessageBox.confirm(`确定删除选中的 ${records.length} 条权益吗？`, "提示", { type: "warning" })
    .then(async () => {
      await deleteBenefitApi(ids);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.benefit-icon {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  cursor: pointer;
}
</style>
