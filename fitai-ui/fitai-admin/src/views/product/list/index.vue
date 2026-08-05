<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增商品</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- 封面列 -->
      <template #cover="{ row }">
        <div class="table-thumb-cell">
          <el-image :src="row.coverImage" :preview-src-list="[row.coverImage]" fit="contain" class="table-thumb-cover" />
        </div>
      </template>

      <!-- 价格列 -->
      <template #price="{ row }">
        <div>
          <span class="text-red-500 font-medium">¥{{ row.minPrice?.toFixed(2) }}</span>
          <span v-if="row.minPrice !== row.maxPrice" class="text-gray-400"> ~ ¥{{ row.maxPrice?.toFixed(2) }}</span>
        </div>
      </template>

      <!-- 库存列 -->
      <template #stock="{ row }">
        <div class="stock-cell">
          <span :class="{ 'stock-warning': row.stockWarning, 'stock-danger': row.totalStock === 0 }">
            {{ row.totalStock ?? 0 }}
          </span>
          <el-tag v-if="row.totalStock === 0" type="danger" size="small" class="ml-1">缺货</el-tag>
          <el-tag v-else-if="row.stockWarning" type="warning" size="small" class="ml-1">低库存</el-tag>
        </div>
      </template>

      <!-- 状态列 -->
      <template #status="{ row }">
        <el-switch
          v-model="row.status"
          :active-value="1"
          :inactive-value="0"
          @change="handleStatusChange(row)"
        />
      </template>

      <!-- 操作列 -->
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="warning" link :icon="List" @click="handleSku(row)">SKU</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="800px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-cascader
                v-model="formData.categoryId"
                :options="categoryOptions"
                :props="{ value: 'id', label: 'name', checkStrictly: true }"
                placeholder="请选择分类"
                clearable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌">
              <el-select
                v-model="formData.brand"
                placeholder="请选择或输入品牌"
                filterable
                allow-create
                clearable
                style="width: 100%"
              >
                <el-option v-for="brand in brandOptions" :key="brand.id" :label="brand.name" :value="brand.name" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图片" prop="coverImage">
          <ImageLibrary
            v-model="formData.coverImage"
            folder="product"
            upload-action="/file/upload/product/cover"
          />
        </el-form-item>
        <el-form-item label="商品图片">
          <MultiImageLibrary
            v-model="productImages"
            folder="product"
            upload-action="/file/upload/product/image"
            :limit="9"
          />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="商品详情">
          <div v-if="dialogVisible" class="editor-container">
            <Toolbar
              class="editor-toolbar"
              :editor="editorRef"
              :defaultConfig="editorToolbarConfig"
              :mode="editorMode"
            />
            <Editor
              class="editor-content"
              v-model="valueHtml"
              :defaultConfig="editorConfig"
              :mode="editorMode"
              @onCreated="handleEditorCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- SKU管理弹窗 -->
    <el-dialog v-model="skuVisible" title="SKU管理" width="1100px" class="sku-manage-dialog">
      <div class="sku-matrix-section">
        <div class="sku-matrix-title">规格维度（笛卡尔积）</div>
        <p class="sku-matrix-hint">
          每一行一个属性：左侧为属性名（如 颜色、尺码），右侧为取值，多个值用英文或中文逗号分隔。点击「生成 SKU 行」将生成全部组合并合并到下方表格（已存在的组合会保留价格、库存与图片）。
        </p>
        <div v-for="(dim, di) in specDimensions" :key="di" class="sku-dim-row">
          <el-input v-model="dim.name" placeholder="属性名，如 颜色" class="sku-dim-name" clearable />
          <el-input v-model="dim.valuesInput" placeholder="取值，如 红,黄,蓝 或 M,L,XL" class="sku-dim-values" clearable />
          <el-button type="danger" link @click="removeSpecDimension(di)">删除</el-button>
        </div>
        <div class="sku-matrix-actions">
          <el-button :icon="Plus" @click="addSpecDimension">添加规格维度</el-button>
          <el-button type="primary" @click="generateSkuFromMatrix">生成 SKU 行（笛卡尔积）</el-button>
        </div>
      </div>
      <div class="mb-4 flex justify-between">
        <el-button type="primary" :icon="Plus" @click="handleAddSku">新增单行 SKU</el-button>
        <el-button type="success" :loading="batchSaving" @click="handleBatchSaveSku">批量保存</el-button>
      </div>
      <el-table :data="skuList" border>
        <el-table-column label="SKU图片" width="80">
          <template #default="{ row }">
            <div class="sku-image-cell table-thumb-cell" title="点击选择/更换图片" @click="openSkuImageLibrary(row)">
              <el-image
                v-if="row.image"
                :src="row.image"
                fit="cover"
                class="table-thumb-square"
                preview-disabled
              />
              <div v-else class="sku-image-placeholder">
                <el-icon><Plus /></el-icon>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="SKU名称" min-width="150">
          <template #default="{ row }">
            <el-input v-model="row.skuName" size="small" placeholder="请输入SKU名称" />
          </template>
        </el-table-column>
        <el-table-column label="规格" width="220">
          <template #default="{ row }">
            <el-input v-model="row.specsText" size="small" placeholder="如 颜色:红,尺码:M" />
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.price" :min="0" :precision="2" size="small" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.stock" :min="0" size="small" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row, $index }">
            <el-button type="primary" link @click="handleSaveSku(row)">保存</el-button>
            <el-button type="danger" link @click="handleDeleteSku(row, $index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- SKU图片选择弹窗 -->
    <el-dialog v-model="skuImageDialogVisible" title="选择SKU图片" width="1000px" destroy-on-close>
      <div class="library-container">
        <!-- 左侧分类 -->
        <div class="library-sidebar">
          <div class="sidebar-title">分类</div>
          <div class="folder-list">
            <div
              v-for="folder in skuImageFolders"
              :key="folder.key"
              class="folder-item"
              :class="{ active: skuImageCurrentFolder === folder.key }"
              @click="selectSkuImageFolder(folder.key)"
            >
              <el-icon><Folder /></el-icon>
              <span class="folder-name">{{ folder.label }}</span>
              <span class="folder-count">{{ folder.count }}</span>
            </div>
          </div>
        </div>

        <!-- 右侧内容 -->
        <div class="library-content">
          <!-- 工具栏 -->
          <div class="library-toolbar">
            <div class="toolbar-left">
              <span v-if="skuImagePickUrl" class="text-primary">已选择 1 张</span>
            </div>
            <div class="toolbar-right">
              <el-upload
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleSkuImageUploadSuccess"
                accept="image/*"
              >
                <el-button type="success" :icon="Upload">上传图片</el-button>
              </el-upload>
              <el-button :icon="Refresh" @click="loadSkuImageLibrary">刷新</el-button>
            </div>
          </div>

          <!-- 图片网格 -->
          <div v-loading="skuImageLoading" class="image-grid-container">
            <el-empty v-if="!skuImageLoading && skuImageList.length === 0" description="暂无图片" />
            <div v-else class="image-grid">
              <div
                v-for="img in skuImageList"
                :key="img.url"
                class="image-card"
                :class="{ selected: skuImagePickUrl === img.url }"
                @click="skuImagePickUrl = img.url"
              >
                <el-image :src="img.url" fit="cover" class="grid-image" />
                <div v-if="skuImagePickUrl === img.url" class="select-check">
                  <el-icon><Check /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="skuImageDialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!skuImagePickUrl" @click="confirmSkuImage">确定选择</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="productList">
import { ref, reactive, computed, watch, shallowRef, onBeforeUnmount } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, List, Folder, Upload, Check } from "@element-plus/icons-vue";
import { getProductPageApi, addProductApi, updateProductApi, deleteProductApi, getProductSkuListApi, updateSkuApi, addSkuApi, deleteSkuApi, getProductCategoryTreeApi, getProductBrandListApi } from "@/api/modules/product";
import { getImageLibrary, getImageFolders } from "@/api/modules/upload";
import { useUserStore } from "@/stores/modules/user";
import "@wangeditor/editor/dist/css/style.css";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import ImageLibrary from "@/components/ImageLibrary/index.vue";
import MultiImageLibrary from "@/components/MultiImageLibrary/index.vue";

// 上传相关（用于 SKU 图片）
const userStore = useUserStore();
const uploadUrl = import.meta.env.VITE_API_URL + "/file/upload/product/image";
const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}));

// 商品图片列表
const productImages = ref([]);

// 富文本编辑器
const editorRef = shallowRef();
const valueHtml = ref(""); // 编辑器内容，独立管理
const editorMode = "default";
const editorToolbarConfig = {};
const editorConfig = {
  placeholder: "请输入商品详情内容...",
  MENU_CONF: {
    uploadImage: {
      server: import.meta.env.VITE_API_URL + "/file/upload/product/image",
      fieldName: "file",
      headers: () => ({
        Authorization: userStore.token
      }),
      customInsert(res, insertFn) {
        if (res.code === 200 && res.data) {
          insertFn(res.data, "", "");
        }
      }
    }
  }
};

// 编辑器创建完成
const handleEditorCreated = editor => {
  editorRef.value = editor;
};

// 组件销毁时销毁编辑器
onBeforeUnmount(() => {
  if (editorRef.value) {
    editorRef.value.destroy();
  }
});

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const skuVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const categoryOptions = ref([]);
const brandOptions = ref([]);
const skuList = ref([]);
const currentSpuId = ref(null); // 当前管理SKU的商品ID
const currentSpuName = ref("");
/** 规格矩阵：每行 { name: 属性名, valuesInput: 逗号分隔的取值 } */
const specDimensions = ref([{ name: "", valuesInput: "" }]);

// 表单数据
const formData = reactive({
  id: null,
  name: "",
  categoryId: null,
  brand: "",
  coverImage: "",
  description: "",
  detailHtml: ""
});

const formRules = {
  name: [{ required: true, message: "请输入商品名称", trigger: "blur" }],
  categoryId: [{ required: true, message: "请选择商品分类", trigger: "change" }]
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
      { field: "name", title: "商品名称", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入名称" } } },
      { field: "brand", title: "品牌", span: 6, itemRender: { name: "$input", props: { placeholder: "请输入品牌" } } },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "上架", value: 1 },
            { label: "下架", value: 0 }
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
          const res = await getProductPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (error) {
          console.error("获取商品列表失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "coverImage", title: "封面", width: 140, slots: { default: "cover" } },
    { field: "name", title: "商品名称", minWidth: 200 },
    { field: "categoryName", title: "分类", width: 120 },
    { field: "brand", title: "品牌", width: 100 },
    { field: "minPrice", title: "价格", width: 150, slots: { default: "price" } },
    { field: "totalStock", title: "库存", width: 120, slots: { default: "stock" } },
    { field: "salesCount", title: "销量", width: 80 },
    { field: "status", title: "状态", width: 80, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 180, fixed: "right", slots: { default: "action" } }
  ]
});

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getProductCategoryTreeApi();
    categoryOptions.value = res.data || [];
  } catch (error) {
    console.error("获取商品分类失败:", error);
    categoryOptions.value = [];
  }
};

// 加载品牌列表
const loadBrands = async () => {
  try {
    const res = await getProductBrandListApi();
    brandOptions.value = res.data || [];
  } catch (error) {
    console.error("获取品牌列表失败:", error);
    brandOptions.value = [];
  }
};

loadCategories();
loadBrands();

/** 规格对象 → 与批量保存解析一致的逗号分隔串（键值中勿含英文逗号） */
const formatSpecsFromObj = specs => {
  if (!specs || typeof specs !== "object") return "";
  return Object.entries(specs)
    .map(([k, v]) => `${k}:${v}`)
    .join(",");
};

/** 解析规格文本为对象（支持逗号分隔或空格分隔的旧数据） */
const parseSpecsTextToObj = text => {
  if (!text || !String(text).trim()) return {};
  const specsObj = {};
  const t = String(text).trim();
  const parts = t.includes(",") ? t.split(",") : t.split(/\s+/);
  parts.forEach(item => {
    const idx = item.indexOf(":");
    if (idx > -1) {
      const key = item.slice(0, idx).trim();
      const value = item.slice(idx + 1).trim();
      if (key) specsObj[key] = value;
    }
  });
  return specsObj;
};

const splitSpecValues = input => {
  if (!input) return [];
  return String(input)
    .split(/[,，]/)
    .map(s => s.trim())
    .filter(Boolean);
};

/** 多规格笛卡尔积：[[a,b],[1,2]] → [[a,1],[a,2],[b,1],[b,2]] */
const cartesianArrays = arrays => {
  if (!arrays.length) return [];
  if (arrays.some(a => !a.length)) return [];
  return arrays.reduce(
    (acc, arr) => acc.flatMap(prefix => arr.map(item => [...prefix, item])),
    [[]]
  );
};

const specsSignature = specs =>
  Object.keys(specs)
    .sort()
    .map(k => `${k}=${specs[k]}`)
    .join("&");

/** 从当前 SKU 列表反推规格维度，便于再次编辑矩阵 */
const inferDimensionsFromSkuList = list => {
  const keyToSet = new Map();
  (list || []).forEach(sku => {
    const specs =
      sku.specs && typeof sku.specs === "object" && !Array.isArray(sku.specs)
        ? sku.specs
        : parseSpecsTextToObj(sku.specsText);
    if (!specs || !Object.keys(specs).length) return;
    Object.entries(specs).forEach(([k, v]) => {
      if (!keyToSet.has(k)) keyToSet.set(k, new Set());
      if (v != null && String(v)) keyToSet.get(k).add(String(v));
    });
  });
  if (keyToSet.size === 0) {
    return [{ name: "", valuesInput: "" }];
  }
  return Array.from(keyToSet.entries()).map(([name, set]) => ({
    name,
    valuesInput: Array.from(set).join(",")
  }));
};

const addSpecDimension = () => {
  specDimensions.value.push({ name: "", valuesInput: "" });
};

const removeSpecDimension = di => {
  if (specDimensions.value.length <= 1) {
    specDimensions.value = [{ name: "", valuesInput: "" }];
    return;
  }
  specDimensions.value.splice(di, 1);
};

const generateSkuFromMatrix = () => {
  const dims = specDimensions.value
    .map(d => ({
      name: (d.name || "").trim(),
      values: splitSpecValues(d.valuesInput)
    }))
    .filter(d => d.name && d.values.length > 0);

  if (dims.length === 0) {
    ElMessage.warning("请至少填写一行规格：属性名 + 取值（多个用逗号分隔）");
    return;
  }
  const names = new Set();
  for (const d of dims) {
    if (names.has(d.name)) {
      ElMessage.warning(`属性名「${d.name}」重复，请合并为一行`);
      return;
    }
    names.add(d.name);
  }

  const dimNames = dims.map(d => d.name);
  const valueArrays = dims.map(d => d.values);
  const tuples = cartesianArrays(valueArrays);
  if (tuples.length === 0) {
    ElMessage.warning("无法生成组合，请检查各属性取值是否为空");
    return;
  }

  const existingMap = new Map();
  skuList.value.forEach(row => {
    const specs =
      row.specs && typeof row.specs === "object" && !Array.isArray(row.specs)
        ? row.specs
        : parseSpecsTextToObj(row.specsText);
    if (specs && Object.keys(specs).length) {
      existingMap.set(specsSignature(specs), row);
    }
  });

  const last = skuList.value[skuList.value.length - 1];
  const defaultPrice = last && last.price != null ? last.price : 0;
  const defaultStock = last && last.stock != null ? last.stock : 0;

  const nextList = [];
  tuples.forEach(tuple => {
    const specs = {};
    dimNames.forEach((n, i) => {
      specs[n] = tuple[i];
    });
    const sig = specsSignature(specs);
    const specsText = formatSpecsFromObj(specs);
    const existed = existingMap.get(sig);
    if (existed) {
      existed.specsText = specsText;
      existed.specs = specs;
      nextList.push(existed);
    } else {
      const skuName = currentSpuName.value
        ? `${currentSpuName.value}-${tuple.join("-")}`
        : tuple.join("-");
      nextList.push({
        id: null,
        spuId: currentSpuId.value,
        skuName,
        image: "",
        specsText,
        specs,
        price: defaultPrice,
        stock: defaultStock,
        status: 1
      });
    }
  });

  skuList.value = nextList;
  ElMessage.success(`已按笛卡尔积生成 ${tuples.length} 行 SKU，请核对价格库存后批量保存`);
};

// 刷新
const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

// 状态变更
const handleStatusChange = async row => {
  try {
    await updateProductApi(row.id, { status: row.status });
    ElMessage.success(row.status === 1 ? "已上架" : "已下架");
  } catch {
    row.status = row.status === 1 ? 0 : 1;
  }
};

// 新增
const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: null,
    name: "",
    categoryId: null,
    brand: "",
    coverImage: "",
    description: "",
    detailHtml: ""
  });
  // 清空图片列表
  productImages.value = [];
  // 初始化编辑器内容
  valueHtml.value = "";
  dialogVisible.value = true;
};

// 编辑
const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, { ...row });
  // 初始化图片列表
  productImages.value = row.images || [];
  // 初始化编辑器内容
  valueHtml.value = row.detailHtml || "";
  dialogVisible.value = true;
};

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    
    const submitData = {
      ...formData,
      detailHtml: valueHtml.value, // 使用编辑器的内容
      images: productImages.value
    };
    
    if (isEdit.value) {
      await updateProductApi(formData.id, submitData);
    } else {
      await addProductApi(submitData);
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
  ElMessageBox.confirm("确定要删除该商品吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteProductApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error("删除失败，请重试");
    }
  });
};

// SKU管理
const handleSku = async row => {
  currentSpuId.value = row.id;
  currentSpuName.value = row.name || "";
  try {
    const res = await getProductSkuListApi(row.id);
    skuList.value = (res.data || []).map(sku => ({
      ...sku,
      specsText: formatSpecsFromObj(sku.specs || parseSpecsTextToObj(sku.specsText))
    }));
  } catch (error) {
    console.error("获取SKU列表失败:", error);
    skuList.value = [];
  }
  specDimensions.value = inferDimensionsFromSkuList(skuList.value);
  skuVisible.value = true;
};

// 新增SKU
const handleAddSku = () => {
  skuList.value.push({
    id: null,
    spuId: currentSpuId.value,
    skuName: "",
    image: "",
    specsText: "",
    specs: null,
    price: 0,
    stock: 0,
    status: 1
  });
};

// 批量保存SKU
const batchSaving = ref(false);
const handleBatchSaveSku = async () => {
  if (skuList.value.length === 0) {
    ElMessage.warning("暂无SKU可保存");
    return;
  }
  batchSaving.value = true;
  let successCount = 0;
  let failCount = 0;
  try {
    for (const sku of skuList.value) {
      try {
        if (sku.specsText && String(sku.specsText).trim()) {
          sku.specs = parseSpecsTextToObj(sku.specsText);
        } else {
          sku.specs = null;
        }
        if (sku.id) {
          await updateSkuApi(sku.id, sku);
        } else {
          const res = await addSkuApi(currentSpuId.value, sku);
          sku.id = res.data;
        }
        successCount++;
      } catch (e) {
        console.error("SKU保存失败:", sku.skuName, e);
        failCount++;
      }
    }
    if (failCount === 0) {
      ElMessage.success(`成功保存 ${successCount} 个SKU`);
    } else {
      ElMessage.warning(`保存完成：成功 ${successCount} 个，失败 ${failCount} 个`);
    }
  } finally {
    batchSaving.value = false;
  }
};

// 保存SKU
const handleSaveSku = async sku => {
  try {
    if (sku.specsText && String(sku.specsText).trim()) {
      sku.specs = parseSpecsTextToObj(sku.specsText);
    } else {
      sku.specs = null;
    }

    if (sku.id) {
      await updateSkuApi(sku.id, sku);
    } else {
      const res = await addSkuApi(currentSpuId.value, sku);
      sku.id = res.data;
    }
    ElMessage.success("保存成功");
  } catch (error) {
    console.error("保存SKU失败:", error);
    ElMessage.error("保存失败，请重试");
  }
};

// 删除SKU
const handleDeleteSku = async (sku, index) => {
  if (sku.id) {
    try {
      await ElMessageBox.confirm("确定删除该SKU吗？", "提示", { type: "warning" });
      await deleteSkuApi(sku.id);
      ElMessage.success("删除成功");
    } catch (error) {
      if (error !== "cancel") {
        console.error("删除SKU失败:", error);
        ElMessage.error("删除失败，请重试");
      }
      return;
    }
  }
  skuList.value.splice(index, 1);
};

// SKU 图片选择相关
const skuImageDialogVisible = ref(false);
const skuImageLoading = ref(false);
const skuImageFolders = ref([]);
const skuImageList = ref([]);
const skuImageCurrentFolder = ref("product");
const skuImagePickUrl = ref("");
const currentEditingSku = ref(null);

// 打开 SKU 图片选择
const openSkuImageLibrary = (row) => {
  currentEditingSku.value = row;
  skuImagePickUrl.value = row.image || "";
  skuImageDialogVisible.value = true;
  loadSkuImageFolders();
  loadSkuImageLibrary();
};

// 加载分类
const loadSkuImageFolders = async () => {
  try {
    const res = await getImageFolders();
    skuImageFolders.value = res.data || [];
  } catch (error) {
    skuImageFolders.value = [{ key: "", label: "全部", count: 0 }];
  }
};

// 加载图片列表
const loadSkuImageLibrary = async () => {
  skuImageLoading.value = true;
  try {
    const res = await getImageLibrary(skuImageCurrentFolder.value, 200);
    skuImageList.value = res.data || [];
  } catch (error) {
    skuImageList.value = [];
  } finally {
    skuImageLoading.value = false;
  }
};

// 选择分类
const selectSkuImageFolder = (key) => {
  skuImageCurrentFolder.value = key;
  loadSkuImageLibrary();
};

// 上传成功
const handleSkuImageUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success("上传成功");
    loadSkuImageLibrary();
    loadSkuImageFolders();
  } else {
    ElMessage.error(response.msg || "上传失败");
  }
};

// 确认选择
const confirmSkuImage = () => {
  if (currentEditingSku.value && skuImagePickUrl.value) {
    currentEditingSku.value.image = skuImagePickUrl.value;
    ElMessage.success("已选择图片");
  }
  skuImageDialogVisible.value = false;
};

// 监听弹窗关闭时清理资源
watch(dialogVisible, val => {
  if (!val) {
    // 弹窗关闭时清空图片列表
    productImages.value = [];
    // 清空编辑器内容
    valueHtml.value = "";
    // 销毁编辑器
    if (editorRef.value) {
      editorRef.value.destroy();
      editorRef.value = null;
    }
  }
});
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

/* 富文本编辑器 */
.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  width: 100%;
}

.editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
}

.editor-content {
  height: 300px;
  overflow-y: auto;
}

/* SKU 规格矩阵 */
.sku-matrix-section {
  margin-bottom: 16px;
  padding: 12px 14px;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-lighter);
}
.sku-matrix-title {
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
}
.sku-matrix-hint {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  margin: 0 0 12px;
}
.sku-dim-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.sku-dim-name {
  width: 140px;
  flex-shrink: 0;
}
.sku-dim-values {
  flex: 1;
  min-width: 0;
}
.sku-matrix-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
}

/* SKU 图片单元格 */
.sku-image-cell {
  cursor: pointer;
}

.sku-image-placeholder {
  width: 56px;
  height: 56px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #8c939d;
}

.sku-image-placeholder:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}

/* 图片库弹窗 */
.library-container {
  display: flex;
  height: 500px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  overflow: hidden;
}

.library-sidebar {
  width: 160px;
  border-right: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-lighter);
  flex-shrink: 0;
}

.sidebar-title {
  padding: 12px 16px;
  font-weight: 600;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.folder-list {
  padding: 8px 0;
  max-height: calc(100% - 45px);
  overflow-y: auto;
}

.folder-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.folder-item:hover {
  background: var(--el-fill-color);
}

.folder-item.active {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.folder-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.folder-count {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.library-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.library-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.image-grid-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}

.image-card {
  position: relative;
  aspect-ratio: 1;
  border-radius: 6px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
}

.image-card:hover {
  border-color: var(--el-color-primary-light-3);
}

.image-card.selected {
  border-color: var(--el-color-primary);
}

.grid-image {
  width: 100%;
  height: 100%;
}

.select-check {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--el-color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.text-primary {
  color: var(--el-color-primary);
}

/* 库存样式 */
.stock-cell {
  display: flex;
  align-items: center;
}

.stock-warning {
  color: var(--el-color-warning);
  font-weight: 600;
}

.stock-danger {
  color: var(--el-color-danger);
  font-weight: 600;
}

.ml-1 {
  margin-left: 4px;
}
</style>
