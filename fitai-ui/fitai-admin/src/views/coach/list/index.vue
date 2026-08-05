<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增教练</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- 头像列 -->
      <template #avatar="{ row }">
        <div class="table-thumb-cell">
          <el-image
            :src="row.avatar || defaultAvatar"
            :preview-src-list="[row.avatar || defaultAvatar]"
            :preview-teleported="true"
            fit="cover"
            class="table-thumb-avatar"
          >
            <template #error>
              <el-avatar :size="40" :src="defaultAvatar" />
            </template>
          </el-image>
        </div>
      </template>

      <!-- 性别列 -->
      <template #gender="{ row }">
        <el-tag v-if="row.gender === 1" type="primary">男</el-tag>
        <el-tag v-else-if="row.gender === 2" type="danger">女</el-tag>
        <el-tag v-else type="info">未知</el-tag>
      </template>

      <!-- 专长列 -->
      <template #specialty="{ row }">
        <template v-if="row.specialty">
          <el-tag v-for="tag in splitTags(row.specialty)" :key="tag" class="mr-1" type="success" size="small">
            {{ tag }}
          </el-tag>
        </template>
        <span v-else class="text-gray-400">-</span>
      </template>

      <!-- 课时费列 -->
      <template #price="{ row }">
        <span>{{ row.price != null ? "¥" + Number(row.price).toFixed(2) : "-" }}</span>
      </template>

      <!-- 评分列 -->
      <template #rating="{ row }">
        <span>{{ row.rating != null ? Number(row.rating).toFixed(2) : "-" }}</span>
      </template>

      <!-- 状态列 -->
      <template #status="{ row }">
        <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
      </template>

      <!-- 操作列 -->
      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑教练' : '新增教练'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="教练姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入教练姓名" />
        </el-form-item>
        <el-form-item label="头衔">
          <el-input v-model="formData.title" placeholder="如：高级私教" />
        </el-form-item>
        <el-form-item label="头像">
          <ImageLibrary
            v-model="formData.avatar"
            folder="coach"
            upload-action="/file/upload/image"
            width="100px"
            height="100px"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="formData.gender">
            <el-radio :value="0">未知</el-radio>
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="专长标签">
          <el-input v-model="formData.specialty" placeholder="多个标签用英文逗号分隔，如：力量训练,减脂" />
        </el-form-item>
        <el-form-item label="课时费">
          <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="教练简介">
          <el-input v-model="formData.intro" type="textarea" :rows="3" placeholder="请输入教练简介" />
        </el-form-item>
        <el-form-item label="资质证书">
          <el-input
            v-model="certificatesText"
            type="textarea"
            :rows="4"
            placeholder="每行一条证书，将保存为列表展示在用户端"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
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

<script setup name="coachList">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import ImageLibrary from "@/components/ImageLibrary/index.vue";
import {
  getCoachPageApi,
  createCoachApi,
  updateCoachApi,
  deleteCoachApi,
  updateCoachStatusApi
} from "@/api/modules/coach";

const defaultAvatar = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const certificatesText = ref("");

const formData = reactive({
  id: null,
  name: "",
  title: "",
  avatar: "",
  phone: "",
  gender: 0,
  specialty: "",
  intro: "",
  price: null,
  sortOrder: 0,
  status: 1
});

const formRules = {
  name: [{ required: true, message: "请输入教练姓名", trigger: "blur" }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }]
};

function splitTags(text) {
  if (!text) return [];
  return String(text)
    .split(",")
    .map(s => s.trim())
    .filter(Boolean);
}

function parseCertificates(text) {
  if (!text || !String(text).trim()) return [];
  return String(text)
    .split(/\r?\n/)
    .map(s => s.trim())
    .filter(Boolean);
}

function certificatesToText(list) {
  if (!list || !Array.isArray(list)) return "";
  return list.join("\n");
}

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
  checkboxConfig: { highlight: true },
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
      { field: "name", title: "教练姓名", span: 8, itemRender: { name: "$input", props: { placeholder: "请输入教练姓名" } } },
      {
        field: "specialty",
        title: "专长",
        span: 8,
        itemRender: { name: "$input", props: { placeholder: "请输入专长标签" } }
      },
      {
        field: "status",
        title: "状态",
        span: 8,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "正常", value: 1 },
            { label: "停用", value: 0 }
          ],
          props: { placeholder: "请选择状态" }
        }
      },
      {
        span: 24,
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
          const res = await getCoachPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (error) {
          console.error("获取教练列表失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "checkbox", width: 50 },
    { type: "seq", title: "序号", width: 60 },
    { field: "avatar", title: "头像", width: 80, slots: { default: "avatar" } },
    { field: "name", title: "教练姓名", width: 110 },
    { field: "title", title: "头衔", width: 120 },
    { field: "phone", title: "手机号", width: 130 },
    { field: "gender", title: "性别", width: 80, slots: { default: "gender" } },
    { field: "specialty", title: "专长", minWidth: 180, slots: { default: "specialty" } },
    { field: "price", title: "课时费", width: 100, slots: { default: "price" } },
    { field: "rating", title: "评分", width: 80, slots: { default: "rating" } },
    { field: "studentCount", title: "学员数", width: 80 },
    { field: "sortOrder", title: "排序", width: 70 },
    { field: "status", title: "状态", width: 80, slots: { default: "status" } },
    { field: "createTime", title: "创建时间", width: 170 },
    { title: "操作", width: 140, fixed: "right", slots: { default: "action" } }
  ]
});

// 刷新
const handleRefresh = () => {
  xGrid.value?.commitProxy("query");
};

// 启用停用
const handleStatusChange = async row => {
  try {
    await updateCoachStatusApi(row.id, row.status);
    ElMessage.success(row.status === 1 ? "已启用" : "已停用");
  } catch {
    row.status = row.status === 1 ? 0 : 1;
    ElMessage.error("状态更新失败");
  }
};

// 新增
const handleAdd = () => {
  isEdit.value = false;
  certificatesText.value = "";
  Object.assign(formData, {
    id: null,
    name: "",
    title: "",
    avatar: "",
    phone: "",
    gender: 0,
    specialty: "",
    intro: "",
    price: null,
    sortOrder: 0,
    status: 1
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = row => {
  isEdit.value = true;
  certificatesText.value = certificatesToText(row.certificates);
  Object.assign(formData, {
    id: row.id,
    name: row.name || "",
    title: row.title || "",
    avatar: row.avatar || "",
    phone: row.phone || "",
    gender: row.gender ?? 0,
    specialty: row.specialty || "",
    intro: row.intro || "",
    price: row.price != null ? Number(row.price) : null,
    sortOrder: row.sortOrder ?? 0,
    status: row.status ?? 1
  });
  dialogVisible.value = true;
};

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const payload = {
      name: formData.name,
      title: formData.title,
      avatar: formData.avatar,
      phone: formData.phone,
      gender: formData.gender,
      specialty: formData.specialty,
      intro: formData.intro,
      certificates: parseCertificates(certificatesText.value),
      price: formData.price,
      sortOrder: formData.sortOrder,
      status: formData.status
    };
    if (isEdit.value) {
      await updateCoachApi(formData.id, payload);
      ElMessage.success("更新成功");
    } else {
      await createCoachApi(payload);
      ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    handleRefresh();
  } finally {
    submitLoading.value = false;
  }
};

// 删除
const handleDelete = row => {
  ElMessageBox.confirm(`确定删除教练「${row.name}」吗？`, "提示", {
    type: "warning"
  })
    .then(async () => {
      await deleteCoachApi(row.id);
      ElMessage.success("已删除");
      handleRefresh();
    })
    .catch(() => {});
};

// 批量删除
const handleBatchDelete = () => {
  const records = xGrid.value?.getCheckboxRecords() || [];
  if (records.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  const ids = records.map(r => r.id).join(",");
  ElMessageBox.confirm(`确定删除选中的 ${records.length} 位教练吗？`, "提示", {
    type: "warning"
  })
    .then(async () => {
      await deleteCoachApi(ids);
      ElMessage.success("批量删除成功");
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
</style>
