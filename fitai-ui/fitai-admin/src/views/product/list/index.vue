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

      <!-- 状态列：后端 0上架 1下架 -->
      <template #status="{ row }">
        <el-switch
          v-model="row.status"
          active-value="0"
          inactive-value="1"
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
                :props="{ value: 'categoryId', label: 'categoryName', children: 'children', checkStrictly: true, emitPath: false }"
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
                placeholder="请选择品牌"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="brand in brandOptions"
                  :key="brand.brandId"
                  :label="brand.brandName"
                  :value="brand.brandId"
                />
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

    <!-- SKU管理弹窗：规格来自分类销售属性勾选 → 笛卡尔积生成 -->
    <el-dialog v-model="skuVisible" title="SKU管理" width="1100px" class="sku-manage-dialog">
      <div class="sku-matrix-section">
        <div class="sku-matrix-title">规格维度（分类销售属性）</div>
        <p class="sku-matrix-hint">
          规格来自商品「{{ currentSpuName || "当前商品" }}」分类销售属性（接口 /sale-attr），勾选取值后点击生成笛卡尔积。已存在的规格组合会保留价格、库存与图片。
        </p>
        <div v-loading="specDimsLoading">
          <el-empty
            v-if="!specDimsLoading && categorySpecDims.length === 0"
            description="当前商品未获取到销售属性，请先在「商品分类」中关联销售属性"
            :image-size="72"
          />
          <div v-for="dim in categorySpecDims" :key="dim.attrId" class="sku-dim-row">
            <span class="sku-dim-label">{{ dim.attrName }}</span>
            <el-checkbox-group v-model="dim.selected" class="sku-dim-checks">
              <el-checkbox v-for="v in dim.values" :key="v.valueId" :value="v.attrValue">
                {{ v.attrValue }}
              </el-checkbox>
            </el-checkbox-group>
            <el-button type="success" link @click="selectAllDimValues(dim)">全选</el-button>
          </div>
          <div v-if="categorySpecDims.length" class="sku-matrix-actions">
            <el-button type="primary" @click="generateSkuFromMatrix">生成 SKU 行（笛卡尔积）</el-button>
          </div>
        </div>
      </div>
      <div class="mb-4 flex justify-end">
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
        <el-table-column label="SKU名称" min-width="160">
          <template #default="{ row }">
            <el-input v-model="row.skuName" size="small" placeholder="请输入SKU名称" />
          </template>
        </el-table-column>
        <el-table-column label="规格" min-width="200">
          <template #default="{ row }">
            <template v-if="skuSpecsEntries(row).length">
              <el-tag
                v-for="[k, v] in skuSpecsEntries(row)"
                :key="k"
                size="small"
                type="success"
                class="mr-1 mb-1"
                effect="plain"
              >
                {{ k }}: {{ v }}
              </el-tag>
            </template>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="130">
          <template #default="{ row }">
            <el-input-number v-model="row.price" :min="0" :precision="2" size="small" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="110">
          <template #default="{ row }">
            <el-input-number v-model="row.stock" :min="0" size="small" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-switch v-model="row.status" active-value="0" inactive-value="1" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
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
import {
  getProductPageApi,
  getProductDetailApi,
  addProductApi,
  updateProductApi,
  updateProductStatusApi,
  deleteProductApi,
  getSaleAttrApi,
  getProductSkuListApi,
  updateSkuApi,
  addSkuApi,
  deleteSkuApi,
  getProductCategoryTreeApi,
  getProductBrandListApi
} from "@/api/modules/product";
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
const currentCategoryId = ref(null);
const currentCategoryName = ref("");
const specDimsLoading = ref(false);
/**
 * 分类销售属性维度：
 * { attrId, attrName, attrType, values:[{valueId,attrValue}], selected: string[] }
 */
const categorySpecDims = ref([]);

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
      {
        field: "spuName",
        title: "商品名称",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "请输入商品名称" } }
      },
      {
        field: "brandName",
        title: "品牌",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "请输入品牌" } }
      },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "上架", value: "0" },
            { label: "下架", value: "1" }
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
    seq: true,
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = {
          pageNum: page.currentPage,
          pageSize: page.pageSize,
          spuName: form.spuName || undefined,
          brandName: form.brandName || undefined,
          status: form.status === "" || form.status == null ? undefined : String(form.status)
        };
        try {
          const res = await getProductPageApi(params);
          const records = res.data?.records || [];
          const list = records.map(row => normalizeSpuRow(row));
          return { list, total: res.data?.total || 0 };
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

/** 扁平化分类树，便于按 id 取名称 */
const flattenCategories = (nodes, map = {}) => {
  (nodes || []).forEach(n => {
    if (n?.categoryId != null) map[n.categoryId] = n.categoryName;
    if (n?.children?.length) flattenCategories(n.children, map);
  });
  return map;
};

/** 后端 AdminProductVO → 页面展示字段 */
const normalizeSpuRow = row => {
  const categoryMap = flattenCategories(categoryOptions.value);
  const brand = (brandOptions.value || []).find(b => String(b.brandId) === String(row.brandId));
  return {
    ...row,
    id: row.spuId,
    name: row.spuName,
    brand: row.brandName || brand?.brandName || "",
    categoryName: row.categoryName || categoryMap[row.categoryId] || "",
    coverImage: row.coverUrl || row.coverImage || "",
    status: row.status == null ? "0" : String(row.status),
    stockWarning: row.stockWarning ?? ((row.totalStock ?? 0) > 0 && (row.totalStock ?? 0) < 20)
  };
};
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

/** 规格对象 / SkuSpecItemVO 列表 → 逗号分隔串 */
const formatSpecsFromObj = specs => {
  if (!specs) return "";
  if (Array.isArray(specs)) {
    return specs
      .filter(s => s?.attrName && s?.attrValue != null)
      .map(s => `${s.attrName}:${s.attrValue}`)
      .join(",");
  }
  if (typeof specs === "object") {
    return Object.entries(specs)
      .map(([k, v]) => `${k}:${v}`)
      .join(",");
  }
  return "";
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

/** 统一为 Map：attrName → attrValue（展示 / 笛卡尔积签名用） */
const resolveSkuSpecs = sku => {
  if (Array.isArray(sku?.specs)) {
    const obj = {};
    sku.specs.forEach(s => {
      if (s?.attrName) obj[s.attrName] = s.attrValue;
    });
    return obj;
  }
  if (sku?.specs && typeof sku.specs === "object") return sku.specs;
  return parseSpecsTextToObj(sku?.specsText || sku?.skuSpecs);
};

/** 统一为 SkuSpecItemVO 列表（提交 SkuAddDTO.specs） */
const resolveSkuSpecItems = sku => {
  if (Array.isArray(sku?.specItems) && sku.specItems.length) return sku.specItems;
  if (Array.isArray(sku?.specs) && sku.specs.length && sku.specs[0]?.attrName != null) {
    return sku.specs.map(s => ({
      attrId: s.attrId,
      attrName: s.attrName,
      valueId: s.valueId,
      attrValue: s.attrValue
    }));
  }
  // 从勾选维度回填 attrId / valueId
  const map = resolveSkuSpecs(sku);
  const items = [];
  Object.entries(map || {}).forEach(([attrName, attrValue]) => {
    const dim = categorySpecDims.value.find(d => d.attrName === attrName);
    const val = dim?.values?.find(v => String(v.attrValue) === String(attrValue));
    items.push({
      attrId: dim?.attrId ?? null,
      attrName,
      valueId: val?.valueId ?? null,
      attrValue
    });
  });
  return items;
};

const skuSpecsEntries = row => Object.entries(resolveSkuSpecs(row) || {});

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

/** 根据已有 SKU 规格，预勾选分类属性取值 */
const preselectDimsFromSkuList = (dims, list) => {
  const used = new Map();
  (list || []).forEach(sku => {
    const specs = resolveSkuSpecs(sku);
    Object.entries(specs || {}).forEach(([k, v]) => {
      if (!used.has(k)) used.set(k, new Set());
      if (v != null && String(v)) used.get(k).add(String(v));
    });
  });
  dims.forEach(dim => {
    const set = used.get(dim.attrName);
    if (!set) {
      dim.selected = [];
      return;
    }
    dim.selected = dim.values.map(v => v.attrValue).filter(val => set.has(val));
  });
};

const selectAllDimValues = dim => {
  dim.selected = (dim.values || []).map(v => v.attrValue);
};

/** 加载 SPU 销售属性维度 GET /admin/product/{spuId}/sale-attr */
const loadCategorySalesAttrDims = async spuId => {
  categorySpecDims.value = [];
  if (!spuId) return;

  specDimsLoading.value = true;
  try {
    const res = await getSaleAttrApi(spuId);
    const attrs = Array.isArray(res.data) ? res.data : [];
    categorySpecDims.value = attrs
      .filter(attr => attr && (attr.attrType == null || String(attr.attrType) === "1"))
      .map(attr => {
        const values = (attr.values || attr.value || [])
          .filter(v => v && (v.status == null || String(v.status) === "0"))
          .map(v => ({
            valueId: v.valueId ?? v.id,
            attrValue: v.attrValue
          }))
          .filter(v => v.attrValue);
        return {
          attrId: attr.attrId,
          attrName: attr.attrName || `属性${attr.attrId}`,
          attrType: String(attr.attrType ?? "1"),
          values,
          selected: []
        };
      })
      .filter(dim => dim.values.length > 0);
  } catch (e) {
    console.error("加载销售属性失败", e);
    categorySpecDims.value = [];
  } finally {
    specDimsLoading.value = false;
  }
};

const generateSkuFromMatrix = () => {
  const dims = categorySpecDims.value.filter(d => (d.selected || []).length > 0);
  if (dims.length === 0) {
    ElMessage.warning("请至少勾选一个规格取值");
    return;
  }

  const dimNames = dims.map(d => d.attrName);
  const valueArrays = dims.map(d => d.selected);
  const tuples = cartesianArrays(valueArrays);
  if (tuples.length === 0) {
    ElMessage.warning("无法生成组合，请检查勾选取值");
    return;
  }

  const existingMap = new Map();
  skuList.value.forEach(row => {
    const specs = resolveSkuSpecs(row);
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
    const specItems = [];
    dimNames.forEach((n, i) => {
      const attrValue = tuple[i];
      specs[n] = attrValue;
      const dim = dims.find(d => d.attrName === n);
      const val = dim?.values?.find(v => String(v.attrValue) === String(attrValue));
      specItems.push({
        attrId: dim?.attrId ?? null,
        attrName: n,
        valueId: val?.valueId ?? null,
        attrValue
      });
    });
    const sig = specsSignature(specs);
    const specsText = formatSpecsFromObj(specs);
    const existed = existingMap.get(sig);
    if (existed) {
      existed.specsText = specsText;
      existed.specs = specs;
      existed.specItems = specItems;
      nextList.push(existed);
    } else {
      const skuName = currentSpuName.value
        ? `${currentSpuName.value}-${tuple.join("-")}`
        : tuple.join("-");
      nextList.push({
        id: null,
        skuId: null,
        spuId: currentSpuId.value,
        skuName,
        image: "",
        specsText,
        specs,
        specItems,
        price: defaultPrice,
        stock: defaultStock,
        status: "0"
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

// 状态变更（AdminProduct 更新接口不含 status，走 spu 更新）
const handleStatusChange = async row => {
  const next = String(row.status);
  const prev = next === "0" ? "1" : "0";
  try {
    await updateProductStatusApi(row.spuId ?? row.id, { spuId: row.spuId ?? row.id, status: next });
    ElMessage.success(next === "0" ? "已上架" : "已下架");
  } catch {
    row.status = prev;
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
    detailHtml: "",
    spuSn: "",
    subtitle: "",
    unit: "件",
    status: "0"
  });
  productImages.value = [];
  valueHtml.value = "";
  dialogVisible.value = true;
};

const handleEdit = async row => {
  isEdit.value = true;
  const spuId = row.spuId ?? row.id;
  try {
    const res = await getProductDetailApi(spuId);
    const detail = res.data || row;
    Object.assign(formData, {
      ...detail,
      id: detail.spuId ?? spuId,
      name: detail.spuName || detail.name || row.name,
      brand: detail.brandId ?? row.brandId ?? row.brand,
      coverImage: detail.coverUrl || detail.coverImage || row.coverImage || "",
      description: detail.description || "",
      status: detail.status == null ? "0" : String(detail.status)
    });
    const album = detail.images || [];
    productImages.value = Array.isArray(album)
      ? album.map(img => (typeof img === "string" ? img : img?.imageUrl)).filter(Boolean)
      : [];
    valueHtml.value = detail.detailHtml || "";
  } catch (e) {
    Object.assign(formData, {
      ...row,
      id: spuId,
      name: row.spuName || row.name,
      brand: row.brandId ?? row.brand,
      coverImage: row.coverUrl || row.coverImage || "",
      status: row.status == null ? "0" : String(row.status)
    });
    productImages.value = row.images || [];
    valueHtml.value = row.detailHtml || "";
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;

    const brandId =
      typeof formData.brand === "number" || /^\d+$/.test(String(formData.brand || ""))
        ? Number(formData.brand)
        : (brandOptions.value || []).find(b => b.brandName === formData.brand)?.brandId;

    // 对齐 ProductAddDTO / ProductUpdateDTO
    const payload = {
      spuName: formData.name,
      categoryId: formData.categoryId,
      brandId: brandId || null,
      coverUrl: formData.coverImage || null,
      description: formData.description || null,
      detailHtml: valueHtml.value || formData.detailHtml || null,
      images: (productImages.value || []).filter(Boolean)
    };

    if (isEdit.value) {
      await updateProductApi(formData.id, { ...payload, spuId: formData.id });
    } else {
      await addProductApi(payload);
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
  ElMessageBox.confirm("确定要删除该商品吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await deleteProductApi(row.spuId ?? row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error("删除失败，请重试");
    }
  });
};

const handleSku = async row => {
  currentSpuId.value = row.spuId ?? row.id;
  currentSpuName.value = row.spuName || row.name || "";
  currentCategoryId.value = row.categoryId ?? null;
  currentCategoryName.value = row.categoryName || "";
  try {
    const res = await getProductSkuListApi(currentSpuId.value);
    const list = Array.isArray(res.data) ? res.data : res.data?.records || [];
    skuList.value = list.map(sku => {
      const specItems = Array.isArray(sku.specs) ? sku.specs : [];
      const specs = resolveSkuSpecs({ ...sku, specs: specItems.length ? specItems : sku.specs });
      return {
        ...sku,
        id: sku.skuId,
        name: sku.skuName,
        image: sku.coverUrl || sku.image || "",
        stock: sku.quantity ?? sku.stock ?? 0,
        specs,
        specItems,
        specsText: formatSpecsFromObj(specItems.length ? specItems : specs),
        status: sku.status == null ? "0" : String(sku.status)
      };
    });
  } catch (error) {
    console.error("获取SKU列表失败:", error);
    skuList.value = [];
  }
  await loadCategorySalesAttrDims(currentSpuId.value);
  preselectDimsFromSkuList(categorySpecDims.value, skuList.value);
  skuVisible.value = true;
};

/** 对齐 SkuAddDTO：skuName / coverUrl / skuSpecs / specs / price / quantity / status */
const toSkuPayload = (sku, isUpdate) => {
  const specsMap = resolveSkuSpecs(sku);
  const specItems = resolveSkuSpecItems(sku);
  const specsText = formatSpecsFromObj(specItems.length ? specItems : specsMap);
  sku.specs = specsMap;
  sku.specItems = specItems;
  sku.specsText = specsText;
  const payload = {
    spuId: currentSpuId.value,
    skuName: sku.skuName || sku.name || "默认SKU",
    coverUrl: sku.image || sku.coverUrl || null,
    skuSpecs: specsText || null,
    specs: specItems,
    price: sku.price ?? 0,
    quantity: sku.stock ?? sku.quantity ?? 0,
    status: sku.status != null ? String(sku.status) : "0"
  };
  if (isUpdate) payload.skuId = sku.skuId ?? sku.id;
  return payload;
};

// 批量保存SKU
const batchSaving = ref(false);
const reloadSkuList = async () => {
  if (!currentSpuId.value) return;
  try {
    const res = await getProductSkuListApi(currentSpuId.value);
    const list = Array.isArray(res.data) ? res.data : res.data?.records || [];
    skuList.value = list.map(sku => {
      const specItems = Array.isArray(sku.specs) ? sku.specs : [];
      const specs = resolveSkuSpecs({ ...sku, specs: specItems.length ? specItems : sku.specs });
      return {
        ...sku,
        id: sku.skuId,
        name: sku.skuName,
        image: sku.coverUrl || sku.image || "",
        stock: sku.quantity ?? sku.stock ?? 0,
        specs,
        specItems,
        specsText: formatSpecsFromObj(specItems.length ? specItems : specs),
        status: sku.status == null ? "0" : String(sku.status)
      };
    });
    preselectDimsFromSkuList(categorySpecDims.value, skuList.value);
  } catch (e) {
    console.error("刷新SKU列表失败:", e);
  }
};

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
        if (sku.id || sku.skuId) {
          await updateSkuApi(sku.skuId ?? sku.id, toSkuPayload(sku, true));
        } else {
          await addSkuApi(currentSpuId.value, toSkuPayload(sku, false));
        }
        successCount++;
      } catch (e) {
        console.error("SKU保存失败:", sku.skuName, e);
        failCount++;
      }
    }
    if (failCount === 0) {
      ElMessage.success(`成功保存 ${successCount} 个SKU`);
      await reloadSkuList();
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
    if (sku.id || sku.skuId) {
      await updateSkuApi(sku.skuId ?? sku.id, toSkuPayload(sku, true));
    } else {
      await addSkuApi(currentSpuId.value, toSkuPayload(sku, false));
    }
    ElMessage.success("保存成功");
    await reloadSkuList();
  } catch (error) {
    console.error("保存SKU失败:", error);
    ElMessage.error("保存失败，请重试");
  }
};

// 删除SKU
const handleDeleteSku = async (sku, index) => {
  if (sku.id || sku.skuId) {
    try {
      await ElMessageBox.confirm("确定删除该SKU吗？", "提示", { type: "warning" });
      await deleteSkuApi(sku.skuId ?? sku.id);
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

/* SKU 规格矩阵（分类销售属性勾选） */
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
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed var(--el-border-color-lighter);
}
.sku-dim-row:last-of-type {
  border-bottom: none;
}
.sku-dim-label {
  width: 72px;
  flex-shrink: 0;
  padding-top: 4px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}
.sku-dim-checks {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 4px 12px;
}
.sku-matrix-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 4px;
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
