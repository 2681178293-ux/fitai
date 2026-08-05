<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增优惠券</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <template #type="{ row }">
        <el-tag v-if="row.type === 1" type="success">满减</el-tag>
        <el-tag v-else-if="row.type === 2" type="warning">折扣</el-tag>
        <el-tag v-else type="info">无门槛</el-tag>
      </template>

      <template #value="{ row }">
        <span v-if="row.type === 1">¥{{ row.value }}</span>
        <span v-else-if="row.type === 2">{{ row.value }}折</span>
        <span v-else>¥{{ row.value }}</span>
      </template>

      <template #status="{ row }">
        <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
      </template>

      <template #action="{ row }">
        <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
        <el-button type="info" link :icon="User" @click="handleUsers(row)">领取记录</el-button>
        <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
      </template>
    </vxe-grid>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑优惠券' : '新增优惠券'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio :value="1">满减</el-radio>
            <el-radio :value="2">折扣</el-radio>
            <el-radio :value="3">无门槛</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优惠值" prop="value">
          <el-input-number v-model="formData.value" :min="0" :precision="formData.type === 2 ? 1 : 2" style="width: 100%" :placeholder="formData.type === 2 ? '如 8.5 表示8.5折' : '金额'" />
        </el-form-item>
        <el-form-item v-if="formData.type !== 3" label="最低消费" prop="minAmount">
          <el-input-number v-model="formData.minAmount" :min="0" :precision="2" style="width: 100%" placeholder="满多少可用" />
        </el-form-item>
        <el-form-item v-if="formData.type === 2" label="最大优惠" prop="maxDiscount">
          <el-input-number v-model="formData.maxDiscount" :min="0" :precision="2" style="width: 100%" placeholder="折扣券封顶金额" />
        </el-form-item>
        <el-form-item label="发放总量" prop="totalCount">
          <el-input-number v-model="formData.totalCount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="每人限领" prop="limitPerUser">
          <el-input-number v-model="formData.limitPerUser" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期" prop="timeRange">
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
        <el-form-item label="使用范围" prop="useScope">
          <el-select v-model="formData.useScope" placeholder="请选择" style="width: 100%">
            <el-option label="全场" value="all" />
            <el-option label="指定分类" value="category" />
            <el-option label="指定商品" value="product" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="formData.useScope !== 'all'" label="范围ID" prop="scopeIds">
          <el-input v-model="formData.scopeIds" :placeholder="formData.useScope === 'category' ? '分类ID，逗号分隔' : '商品ID，逗号分隔'" />
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

    <el-dialog v-model="usersVisible" title="领取记录" width="700px">
      <el-table :data="usersList" v-loading="usersLoading" max-height="400">
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="createTime" label="领取时间" width="180" />
      </el-table>
      <el-pagination
        v-model:current-page="usersPage"
        v-model:page-size="usersPageSize"
        :total="usersTotal"
        :page-sizes="[10, 20]"
        layout="total, sizes, prev, pager, next"
        class="mt-3"
        @current-change="fetchUsers"
        @size-change="fetchUsers"
      />
    </el-dialog>
  </div>
</template>

<script setup name="couponList">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Refresh, Edit, Delete, User } from "@element-plus/icons-vue";
import { getCouponPageApi, addCouponApi, updateCouponApi, deleteCouponApi, getCouponUsersApi } from "@/api/modules/marketing";

const xGrid = ref();
const formRef = ref();
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);

const formData = reactive({
  id: null,
  name: "",
  type: 1,
  value: null,
  minAmount: null,
  maxDiscount: null,
  totalCount: 100,
  limitPerUser: 1,
  timeRange: [],
  useScope: "all",
  scopeIds: "",
  status: 1
});

const formRules = {
  name: [{ required: true, message: "请输入优惠券名称", trigger: "blur" }],
  type: [{ required: true, message: "请选择类型", trigger: "change" }],
  value: [{ required: true, message: "请输入优惠值", trigger: "blur" }],
  totalCount: [{ required: true, message: "请输入发放总量", trigger: "blur" }]
};

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
  columnConfig: { resizable: true },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  formConfig: {
    items: [
      { field: "keyword", title: "关键词", span: 6, itemRender: { name: "$input", props: { placeholder: "优惠券名称" } } },
      { field: "status", title: "状态", span: 6, itemRender: { name: "$select", options: [{ label: "全部", value: null }, { label: "启用", value: 1 }, { label: "禁用", value: 0 }], props: { placeholder: "请选择" } } },
      { span: 6, itemRender: { name: "$buttons", children: [{ props: { type: "submit", content: "搜索", status: "primary" } }, { props: { type: "reset", content: "重置" } }] } }
    ]
  },
  proxyConfig: {
    form: true,
    ajax: {
      query: async ({ page, form }) => {
        try {
          const res = await getCouponPageApi({
            ...form,
            pageNum: page?.currentPage || 1,
            pageSize: page?.pageSize || 20
          });
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          console.error("获取优惠券失败:", e);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "name", title: "名称", minWidth: 140 },
    { field: "type", title: "类型", width: 90, slots: { default: "type" } },
    { field: "value", title: "面值", width: 90, slots: { default: "value" } },
    { field: "minAmount", title: "最低消费", width: 100 },
    { field: "totalCount", title: "总量", width: 80 },
    { field: "remainCount", title: "剩余", width: 80 },
    { field: "limitPerUser", title: "限领", width: 70 },
    { field: "startTime", title: "开始时间", width: 170 },
    { field: "endTime", title: "结束时间", width: 170 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { title: "操作", width: 200, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, { id: null, name: "", type: 1, value: null, minAmount: null, maxDiscount: null, totalCount: 100, limitPerUser: 1, timeRange: [], useScope: "all", scopeIds: "", status: 1 });
  dialogVisible.value = true;
};

const handleEdit = row => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    type: row.type ?? 1,
    value: row.value,
    minAmount: row.minAmount,
    maxDiscount: row.maxDiscount,
    totalCount: row.totalCount ?? 100,
    limitPerUser: row.limitPerUser ?? 1,
    timeRange: row.startTime ? [row.startTime, row.endTime] : [],
    useScope: row.useScope || "all",
    scopeIds: row.scopeIds || "",
    status: row.status ?? 1
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    const [startTime, endTime] = formData.timeRange || [];
    const submitData = { ...formData, startTime: startTime || null, endTime: endTime || null };
    if (isEdit.value) {
      await updateCouponApi(formData.id, submitData);
    } else {
      await addCouponApi(submitData);
    }
    ElMessage.success(isEdit.value ? "编辑成功" : "新增成功");
    dialogVisible.value = false;
    handleRefresh();
  } catch (e) {
    if (e !== false) ElMessage.error("操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = row => {
  ElMessageBox.confirm("确定要删除该优惠券吗？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteCouponApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

const handleStatusChange = async row => {
  try {
    await updateCouponApi(row.id, { ...row });
    ElMessage.success(row.status === 1 ? "已启用" : "已禁用");
  } catch (e) {
    row.status = row.status === 1 ? 0 : 1;
    ElMessage.error("操作失败");
  }
};

const usersVisible = ref(false);
const usersList = ref([]);
const usersLoading = ref(false);
const usersPage = ref(1);
const usersPageSize = ref(10);
const usersTotal = ref(0);
let currentCouponId = null;

const handleUsers = row => {
  currentCouponId = row.id;
  usersPage.value = 1;
  usersVisible.value = true;
  fetchUsers();
};

const fetchUsers = async () => {
  if (!currentCouponId) return;
  usersLoading.value = true;
  try {
    const res = await getCouponUsersApi(currentCouponId, { pageNum: usersPage.value, pageSize: usersPageSize.value });
    const data = res.data || {};
    usersList.value = data.records || [];
    usersTotal.value = Number(data.total) || 0;
  } catch (e) {
    usersList.value = [];
  } finally {
    usersLoading.value = false;
  }
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
