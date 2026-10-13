<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增品牌</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- 后端字段 brandLogo -->
      <template #brandLogo="{ row }">
        <div v-if="row.brandLogo" class="table-thumb-cell">
          <el-image :src="row.brandLogo" :preview-src-list="[row.brandLogo]" class="table-thumb-square" />
        </div>
        <span v-else>-</span>
      </template>

      <!-- 后端 status：0正常 1停用 -->
      <template #status="{ row }">
        <el-switch
          v-model="row.status"
          active-value="0"
          inactive-value="1"
          @change="handleStatusChange(row)"
        />
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑品牌' : '新增品牌'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="formData.brandName" placeholder="请输入品牌名称" />
        </el-form-item>
        <el-form-item label="品牌Logo">
          <ImageLibrary
            v-model="formData.brandLogo"
            folder="product"
            upload-action="/file/upload/product/brand"
            width="100px"
            height="100px"
          />
        </el-form-item>
        <el-form-item label="品牌描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入品牌描述" />
        </el-form-item>
        <el-form-item label="首字母">
          <el-input v-model="formData.firstLetter" maxlength="1" placeholder="检索首字母" style="width: 120px" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.orderNum" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" active-value="0" inactive-value="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="productBrand">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import {
  getProductBrandPageApi,
  addProductBrandApi,
  updateProductBrandApi,
  deleteProductBrandApi
} from "@/api/modules/product";
import ImageLibrary from "@/components/ImageLibrary/index.vue";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

/** 与后端 PrdBrandAddDTO / UpdateDTO / VO 字段一致 */
const formData = reactive({
  brandId: null,
  brandName: "",
  brandLogo: "",
  description: "",
  firstLetter: "",
  orderNum: 0,
  status: "0"
});

const formRules = {
  brandName: [{ required: true, message: "请输入品牌名称", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "brandId", isHover: true },
  columnConfig: { resizable: true },
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
    pageSizes: [10, 20, 50, 100]
  },
  formConfig: {
    items: [
      {
        field: "brandName",
        title: "品牌名称",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "品牌名称", clearable: true } }
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
          props: { placeholder: "请选择", clearable: true }
        }
      },
      {
        span: 6,
        align: "left",
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
    seq: true,
    response: {
      result: "list",
      total: "total"
    },
    ajax: {
      query: async ({ page, form }) => {
        try {
          const params = {
            pageNum: page.currentPage,
            pageSize: page.pageSize
          };
          if (form?.brandName) params.brandName = form.brandName;
          if (form?.status !== undefined && form?.status !== null && form?.status !== "") {
            params.status = String(form.status);
          }
          const res = await getProductBrandPageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (error) {
          console.error("获取品牌列表失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", title: "序号", width: 60 },
    { field: "brandLogo", title: "Logo", width: 80, slots: { default: "brandLogo" } },
    { field: "brandName", title: "品牌名称", minWidth: 150 },
    { field: "description", title: "品牌描述", minWidth: 200 },
    { field: "orderNum", title: "排序", width: 80 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

const handleStatusChange = async row => {
  const prev = row.status === "0" ? "1" : "0";
  try {
    await updateProductBrandApi({
      brandId: row.brandId,
      status: String(row.status)
    });
    ElMessage.success(row.status === "0" ? "已启用" : "已停用");
  } catch {
    row.status = prev;
  }
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    brandId: null,
    brandName: "",
    brandLogo: "",
    description: "",
    firstLetter: "",
    orderNum: 0,
    status: "0"
  });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    brandId: row.brandId,
    brandName: row.brandName || "",
    brandLogo: row.brandLogo || "",
    description: row.description || "",
    firstLetter: row.firstLetter || "",
    orderNum: row.orderNum ?? 0,
    status: row.status != null ? String(row.status) : "0"
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;

    const payload = {
      brandName: formData.brandName,
      brandLogo: formData.brandLogo || undefined,
      description: formData.description || undefined,
      firstLetter: formData.firstLetter || undefined,
      orderNum: formData.orderNum,
      status: formData.status != null ? String(formData.status) : "0"
    };

    if (isEdit.value) {
      await updateProductBrandApi({
        ...payload,
        brandId: formData.brandId
      });
    } else {
      await addProductBrandApi(payload);
    }

    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定要删除该品牌吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteProductBrandApi(row.brandId);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error("删除失败，请重试");
    }
  });
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
