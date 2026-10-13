<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增属性</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>
      <template #attrType="{ row }">
        <el-tag :type="String(row.attrType) === '1' ? 'success' : 'info'">
          {{ attrTypeMap[row.attrType] || row.attrType }}
        </el-tag>
      </template>
      <template #selectType="{ row }">
        <span>{{ selectTypeMap[row.selectType] || '-' }}</span>
      </template>
      <template #values="{ row }">
        <template v-if="row.values?.length">
          <el-tag v-for="v in row.values" :key="v.valueId" size="small" class="mr-1 mb-1">{{ v.attrValue }}</el-tag>
        </template>
        <span v-else class="text-gray-400">-</span>
      </template>
      <template #status="{ row }">
        <el-tag :type="String(row.status) === '0' ? 'success' : 'info'">
          {{ String(row.status) === '0' ? '正常' : '停用' }}
        </el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑属性' : '新增属性'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="属性名称" prop="attrName">
          <el-input v-model="formData.attrName" placeholder="如 颜色、尺码" />
        </el-form-item>
        <el-form-item label="属性类型">
          <el-select v-model="formData.attrType" style="width: 100%">
            <el-option label="销售属性" value="1" />
            <el-option label="基本属性" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="录入方式">
          <el-select v-model="formData.selectType" style="width: 100%">
            <el-option label="单选" value="1" />
            <el-option label="多选" value="2" />
            <el-option label="手工输入" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.orderNum" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" active-value="0" inactive-value="1" />
        </el-form-item>
        <el-form-item label="属性值">
          <div class="w-full">
            <div v-for="(v, idx) in formData.values" :key="idx" class="value-row">
              <el-input v-model="v.attrValue" placeholder="属性值" style="width: 200px" />
              <el-input-number v-model="v.orderNum" :min="0" placeholder="排序" style="width: 120px" />
              <el-button type="danger" link :icon="Delete" @click="removeValue(idx)">删除</el-button>
            </div>
            <el-button type="primary" plain :icon="Plus" size="small" @click="addValue">添加属性值</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="productAttr">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getAttrPageApi,
  getAttrDetailApi,
  addAttrApi,
  updateAttrApi,
  deleteAttrApi
} from "@/api/modules/mall-ext";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const attrTypeMap = { 1: "销售属性", 2: "基本属性" };
const selectTypeMap = { 1: "单选", 2: "多选", 3: "手工输入" };

const formData = reactive({
  id: null,
  attrName: "",
  attrType: "1",
  selectType: "1",
  orderNum: 0,
  status: "0",
  values: []
});

const formRules = {
  attrName: [{ required: true, message: "请输入属性名称", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "attrId" },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  pagerConfig: { enabled: true, pageSize: 10, pageSizes: [10, 20, 50, 100] },
  formConfig: {
    items: [
      { field: "attrName", title: "属性名称", span: 6, itemRender: { name: "$input", props: { placeholder: "属性名称" } } },
      {
        field: "attrType",
        title: "属性类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "销售属性", value: "1" },
            { label: "基本属性", value: "2" }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "正常", value: "0" },
            { label: "停用", value: "1" }
          ],
          props: { placeholder: "请选择" }
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
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getAttrPageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "attrName", title: "属性名称", minWidth: 140 },
    { field: "attrType", title: "属性类型", width: 110, slots: { default: "attrType" } },
    { field: "selectType", title: "录入方式", width: 100, slots: { default: "selectType" } },
    { field: "values", title: "属性值", minWidth: 240, slots: { default: "values" } },
    { field: "orderNum", title: "排序", width: 80 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const addValue = () => formData.values.push({ valueId: null, attrValue: "", orderNum: 0, status: "0" });
const removeValue = idx => formData.values.splice(idx, 1);

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: null,
    attrName: "",
    attrType: "1",
    selectType: "1",
    orderNum: 0,
    status: "0",
    values: []
  });
  dialogVisible.value = true;
};

const handleEdit = async row => {
  isEdit.value = true;
  let full = row;
  try {
    const res = await getAttrDetailApi(row.attrId ?? row.id);
    full = res.data || row;
  } catch (e) {
    /* 用行数据兜底 */
  }
  Object.assign(formData, {
    id: full.attrId ?? full.id,
    attrName: full.attrName,
    attrType: String(full.attrType ?? "1"),
    selectType: String(full.selectType ?? "1"),
    orderNum: full.orderNum ?? 0,
    status: String(full.status ?? "0"),
    values: (full.values || []).map(v => ({
      valueId: v.valueId ?? null,
      attrValue: v.attrValue,
      orderNum: v.orderNum ?? 0,
      status: String(v.status ?? "0")
    }))
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      attrId: formData.id,
      attrName: formData.attrName,
      attrType: formData.attrType,
      selectType: formData.selectType,
      orderNum: formData.orderNum,
      status: formData.status,
      values: formData.values.filter(v => v.attrValue?.trim())
    };
    if (isEdit.value) {
      await updateAttrApi(payload);
    } else {
      await addAttrApi(payload);
    }
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) {
      /* 校验失败或拦截器已提示 */
    }
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm(`确定删除属性「${row.attrName}」？`, "提示", { type: "warning" }).then(async () => {
    try {
      await deleteAttrApi(row.attrId ?? row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      /* 拦截器已提示 */
    }
  });
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
.value-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
</style>
