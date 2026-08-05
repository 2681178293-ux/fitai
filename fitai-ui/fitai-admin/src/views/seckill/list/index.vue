<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增活动</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- 封面列 -->
      <template #cover="{ row }">
        <div v-if="row.coverImage" class="table-thumb-cell">
          <el-image :src="row.coverImage" :preview-src-list="[row.coverImage]" fit="contain" class="table-thumb-cover" />
        </div>
        <span v-else class="text-gray-400 text-sm">暂无封面</span>
      </template>

      <!-- 活动状态列 -->
      <template #status="{ row }">
        <el-tag v-if="row.status === 0" type="warning">未开始</el-tag>
        <el-tag v-else-if="row.status === 1" type="success">进行中</el-tag>
        <el-tag v-else type="info">已结束</el-tag>
      </template>

      <!-- 操作列 -->
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="warning" link :icon="Goods" @click="handleSku(row)">秒杀商品</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑活动' : '新增活动'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="封面图片" prop="coverImage">
          <ImageLibrary v-model="formData.coverImage" folder="seckill" upload-action="/file/upload/seckill/cover" width="200px" height="100px" />
        </el-form-item>
        <el-form-item label="活动时间" prop="timeRange">
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
        <el-form-item label="活动描述">
          <el-input v-model="formData.description" type="textarea" :rows="4" placeholder="请输入活动描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 秒杀商品管理弹窗 -->
    <el-dialog v-model="skuVisible" title="秒杀商品管理" width="1000px">
      <div class="mb-4">
        <el-button type="primary" :icon="Plus" @click="handleAddSku">添加商品</el-button>
      </div>
      <el-table :data="skuList" border>
        <el-table-column label="商品图片" width="80">
          <template #default="{ row }">
            <div class="table-thumb-cell">
              <el-image :src="row.skuImage" :preview-src-list="[row.skuImage]" class="table-thumb-square" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="skuName" label="商品名称" min-width="200" />
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="{ row }">
            <span class="text-gray-400 line-through">¥{{ row.originalPrice?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="seckillPrice" label="秒杀价" width="100">
          <template #default="{ row }">
            <span class="text-red-500 font-bold">¥{{ row.seckillPrice?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalStock" label="总库存" width="80" />
        <el-table-column prop="availableStock" label="剩余库存" width="100" />
        <el-table-column prop="limitPerUser" label="限购" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="success" size="small" @click="handleToggleSkuStatus(row)">启用</el-button>
            <el-button v-else type="info" size="small" @click="handleToggleSkuStatus(row)">禁用</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDeleteSku(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加秒杀商品弹窗 -->
    <el-dialog v-model="addSkuVisible" title="添加秒杀商品" width="500px">
      <el-form ref="skuFormRef" :model="skuFormData" :rules="skuFormRules" label-width="100px">
        <el-form-item label="选择商品" prop="skuId">
          <el-select v-model="skuFormData.skuId" placeholder="请选择商品" style="width: 100%">
            <el-option v-for="item in productOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="秒杀价格" prop="seckillPrice">
          <el-input-number v-model="skuFormData.seckillPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="秒杀库存" prop="totalStock">
          <el-input-number v-model="skuFormData.totalStock" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="每人限购" prop="limitPerUser">
          <el-input-number v-model="skuFormData.limitPerUser" :min="1" :max="10" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addSkuVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSkuSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="seckillList">
import { ref, reactive, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, Goods } from "@element-plus/icons-vue";
import { getSeckillActivityPageApi, addSeckillActivityApi, updateSeckillActivityApi, deleteSeckillActivityApi, getSeckillSkuListApi, addSeckillSkuApi, deleteSeckillSkuApi, toggleSeckillSkuStatusApi } from "@/api/modules/seckill";
import { getSkuOptionsApi } from "@/api/modules/product";
import ImageLibrary from "@/components/ImageLibrary/index.vue";

const xGrid = ref();
const formRef = ref();
const skuFormRef = ref();
const dialogVisible = ref(false);
const skuVisible = ref(false);
const addSkuVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const currentActivity = ref(null);
const skuList = ref([]);
const productOptions = ref([]);

// 表单数据
const formData = reactive({
  id: null,
  name: "",
  coverImage: "",
  timeRange: [],
  description: ""
});

const formRules = {
  name: [{ required: true, message: "请输入活动名称", trigger: "blur" }],
  timeRange: [{ required: true, message: "请选择活动时间", trigger: "change" }]
};

// 秒杀商品表单
const skuFormData = reactive({
  skuId: null,
  seckillPrice: 0,
  totalStock: 100,
  limitPerUser: 1
});

const skuFormRules = {
  skuId: [{ required: true, message: "请选择商品", trigger: "change" }],
  seckillPrice: [{ required: true, message: "请输入秒杀价格", trigger: "blur" }],
  totalStock: [{ required: true, message: "请输入秒杀库存", trigger: "blur" }]
};

// 表格配置
const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true, height: 72 },
  columnConfig: { resizable: true },
  pagerConfig: {
    pageSize: 10,
    pageSizes: [10, 20, 50, 100]
  },
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
      { field: "name", title: "活动名称", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入名称" } } },
      {
        field: "status",
        title: "活动状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "未开始", value: 0 },
            { label: "进行中", value: 1 },
            { label: "已结束", value: 2 }
          ],
          props: { placeholder: "请选择状态" }
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
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = {
          pageNum: page.currentPage,
          pageSize: page.pageSize,
          ...form
        };
        try {
          const res = await getSeckillActivityPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (error) {
          console.error("获取秒杀活动列表失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "coverImage", title: "封面", width: 140, slots: { default: "cover" } },
    { field: "name", title: "活动名称", minWidth: 200 },
    { field: "startTime", title: "开始时间", width: 170 },
    { field: "endTime", title: "结束时间", width: 170 },
    { field: "skuCount", title: "商品数量", width: 100 },
    { field: "status", title: "状态", width: 100, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 220, fixed: "right", slots: { default: "action" } }
  ]
});

// 加载可选商品（从商品SKU列表获取）
const loadProducts = async () => {
  try {
    const res = await getSkuOptionsApi();
    productOptions.value = res.data || [];
  } catch (error) {
    console.error("获取商品选项失败:", error);
    productOptions.value = [];
  }
};

loadProducts();

// 刷新
const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

// 新增
const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: null,
    name: "",
    coverImage: "",
    timeRange: [],
    description: ""
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    coverImage: row.coverImage,
    timeRange: [row.startTime, row.endTime],
    description: row.description
  });
  dialogVisible.value = true;
};

// 日期格式归一化：API 返回 ISO（含 T），后端期望 yyyy-MM-dd HH:mm:ss
const normalizeDatetime = val => (val && typeof val === "string" ? val.replace("T", " ") : val);

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    
    const submitData = {
      ...formData,
      startTime: normalizeDatetime(formData.timeRange?.[0]),
      endTime: normalizeDatetime(formData.timeRange?.[1])
    };
    
    if (isEdit.value) {
      await updateSeckillActivityApi(formData.id, submitData);
    } else {
      await addSeckillActivityApi(submitData);
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

// 删除
const handleDelete = row => {
  ElMessageBox.confirm("确定要删除该活动吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteSeckillActivityApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error("删除失败，请重试");
    }
  });
};

// 秒杀商品管理
const handleSku = async row => {
  currentActivity.value = row;
  
  try {
    const res = await getSeckillSkuListApi(row.id);
    skuList.value = res.data || [];
  } catch (error) {
    console.error("获取秒杀商品列表失败:", error);
    skuList.value = [];
  }
  skuVisible.value = true;
};

// 添加秒杀商品
const handleAddSku = () => {
  Object.assign(skuFormData, {
    skuId: null,
    seckillPrice: 0,
    totalStock: 100,
    limitPerUser: 1
  });
  addSkuVisible.value = true;
};

// 提交添加秒杀商品
const handleAddSkuSubmit = async () => {
  try {
    await skuFormRef.value.validate();
    
    await addSeckillSkuApi(currentActivity.value.id, skuFormData);
    
    ElMessage.success("添加成功");
    addSkuVisible.value = false;
    handleSku(currentActivity.value);
  } catch (error) {
    console.error("添加秒杀商品失败:", error);
  }
};

// 删除秒杀商品
const handleDeleteSku = row => {
  ElMessageBox.confirm("确定要删除该秒杀商品吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteSeckillSkuApi(row.id);
      ElMessage.success("删除成功");
      handleSku(currentActivity.value);
    } catch (error) {
      console.error("删除秒杀商品失败:", error);
      ElMessage.error("删除失败，请重试");
    }
  });
};

// 切换秒杀商品状态
const handleToggleSkuStatus = async row => {
  try {
    await toggleSeckillSkuStatusApi(row.id);
    ElMessage.success(row.status === 1 ? "已禁用" : "已启用");
    handleSku(currentActivity.value);
  } catch (error) {
    console.error("切换状态失败:", error);
    ElMessage.error("操作失败，请重试");
  }
};

</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
