<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增轮播图</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #image="{ row }">
        <div class="table-thumb-cell">
          <el-image :src="row.imageUrl" :preview-src-list="[row.imageUrl]" fit="contain" class="table-thumb-cover" />
        </div>
      </template>

      <template #position="{ row }">
        <el-tag v-if="row.position === 'home'" type="primary">首页</el-tag>
        <el-tag v-else-if="row.position === 'course'" type="success">课程</el-tag>
        <el-tag v-else-if="row.position === 'product'" type="warning">商城</el-tag>
        <el-tag v-else>{{ row.position }}</el-tag>
      </template>

      <template #linkType="{ row }">
        <span v-if="row.linkType === 'url'">外链</span>
        <span v-else-if="row.linkType === 'page'">页面</span>
        <span v-else-if="row.linkType === 'product'">商品</span>
        <span v-else-if="row.linkType === 'course'">课程</span>
        <span v-else>-</span>
      </template>

      <template #status="{ row }">
        <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑轮播图' : '新增轮播图'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题（可选）" />
        </el-form-item>
        <el-form-item label="图片" prop="imageUrl">
          <ImageLibrary v-model="formData.imageUrl" folder="banner" upload-action="/file/upload/banner" />
        </el-form-item>
        <el-form-item label="展示位置" prop="position">
          <el-select v-model="formData.position" placeholder="请选择" style="width: 100%">
            <el-option label="首页" value="home" />
            <el-option label="课程" value="course" />
            <el-option label="商城" value="product" />
          </el-select>
        </el-form-item>
        <el-form-item label="链接类型" prop="linkType">
          <el-select v-model="formData.linkType" placeholder="请选择（可选）" clearable style="width: 100%">
            <el-option label="外链" value="url" />
            <el-option label="跳转页面" value="page" />
            <el-option label="商品详情" value="product" />
            <el-option label="课程详情" value="course" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="formData.linkType" label="链接值" prop="linkValue">
          <el-input v-model="formData.linkValue" :placeholder="linkPlaceholder" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-date-picker
            v-model="formData.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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

<script setup name="bannerList">
import { ref, reactive, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { getBannerListApi, addBannerApi, updateBannerApi, deleteBannerApi } from "@/api/modules/marketing";
import ImageLibrary from "@/components/ImageLibrary/index.vue";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const formData = reactive({
  id: null,
  title: "",
  imageUrl: "",
  position: "home",
  linkType: "",
  linkValue: "",
  sortOrder: 0,
  timeRange: [],
  status: 1
});

const linkPlaceholder = computed(() => {
  const m = { url: "https://...", page: "/pages/xxx", product: "商品ID", course: "课程ID" };
  return m[formData.linkType] || "请输入";
});

const formRules = {
  imageUrl: [{ required: true, message: "请上传图片", trigger: "change" }],
  position: [{ required: true, message: "请选择展示位置", trigger: "change" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true, height: 72 },
  columnConfig: { resizable: true },
  toolbarConfig: {
    slots: { buttons: "toolbar_buttons" },
    refresh: true,
    zoom: false,
    export: true,
    custom: true
  },
  exportConfig: { types: ["xlsx", "csv"] },
  formConfig: {
    items: [
      {
        field: "position",
        title: "位置",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "首页", value: "home" },
            { label: "课程", value: "course" },
            { label: "商城", value: "product" }
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
    ajax: {
      query: async ({ form }) => {
        try {
          const res = await getBannerListApi(form);
          return { list: res.data || [], total: (res.data || []).length };
        } catch (error) {
          console.error("获取轮播图失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "imageUrl", title: "图片", width: 140, slots: { default: "image" } },
    { field: "title", title: "标题", minWidth: 120 },
    { field: "position", title: "位置", width: 100, slots: { default: "position" } },
    { field: "linkType", title: "链接类型", width: 100, slots: { default: "linkType" } },
    { field: "sortOrder", title: "排序", width: 80 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 160, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: null,
    title: "",
    imageUrl: "",
    position: "home",
    linkType: "",
    linkValue: "",
    sortOrder: 0,
    timeRange: [],
    status: 1
  });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    title: row.title || "",
    imageUrl: row.imageUrl,
    position: row.position || "home",
    linkType: row.linkType || "",
    linkValue: row.linkValue || "",
    sortOrder: row.sortOrder ?? 0,
    timeRange: row.startTime ? [row.startTime, row.endTime] : [],
    status: row.status ?? 1
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const [startTime, endTime] = formData.timeRange || [];
    const submitData = {
      ...formData,
      startTime: startTime || null,
      endTime: endTime || null
    };
    if (isEdit.value) {
      await updateBannerApi(formData.id, submitData);
    } else {
      await addBannerApi(submitData);
    }
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (error) {
    console.error("提交失败:", error);
    ElMessage.error("操作失败，请重试");
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定要删除该轮播图吗？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteBannerApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      ElMessage.error("删除失败，请重试");
    }
  });
};

const handleStatusChange = async row => {
  try {
    await updateBannerApi(row.id, { ...row });
    ElMessage.success(row.status === 1 ? "已启用" : "已禁用");
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1;
    ElMessage.error("操作失败");
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

</style>
