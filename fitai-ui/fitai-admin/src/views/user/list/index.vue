<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button type="primary" :icon="Refresh" @click="handleRefresh">刷新</el-button>
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
              <el-avatar :size="40" :src="defaultAvatar"/>
            </template>
          </el-image>
        </div>
      </template>

      <!-- 性别列：0男 1女 2未知 -->
      <template #gender="{ row }">
        <el-tag v-if="String(row.gender) === '0'" type="primary">男</el-tag>
        <el-tag v-else-if="String(row.gender) === '1'" type="danger">女</el-tag>
        <el-tag v-else type="info">未知</el-tag>
      </template>

      <!-- 健身目标列 -->
      <template #fitnessGoal="{ row }">
        <span v-if="String(row.fitnessGoal) === '1'">减脂</span>
        <span v-else-if="String(row.fitnessGoal) === '2'">增肌</span>
        <span v-else-if="String(row.fitnessGoal) === '3'">塑形</span>
        <span v-else-if="String(row.fitnessGoal) === '4'">保持健康</span>
        <span v-else>-</span>
      </template>

      <!-- 状态列：0正常 1冻结 -->
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
        <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
      </template>
    </vxe-grid>

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <div class="flex justify-center mb-6">
        <el-image
            :src="currentUser.avatar || defaultAvatar"
            :preview-src-list="[currentUser.avatar || defaultAvatar]"
            :preview-teleported="true"
            fit="cover"
            class="detail-avatar"
        >
          <template #error>
            <el-avatar :size="100" :src="defaultAvatar"/>
          </template>
        </el-image>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.customerId }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.mobile }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickName }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ String(currentUser.gender) === '0' ? '男' : String(currentUser.gender) === '1' ? '女' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="身高">
          {{ currentUser.heightCm != null ? currentUser.heightCm + ' cm' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="体重">
          {{ currentUser.weightKg != null ? currentUser.weightKg + ' kg' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="体脂率">
          {{ currentUser.bodyFatPct != null ? currentUser.bodyFatPct + '%' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="会员等级">
          {{ currentUser.memberLevelName || '普通用户' }}
        </el-descriptions-item>
        <el-descriptions-item label="积分余额">{{ currentUser.pointsBalance ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="最后登录">{{ currentUser.lastLoginTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="userList">
import {ref, reactive} from "vue";
import {ElMessage} from "element-plus";
import {Refresh, View} from "@element-plus/icons-vue";
import {getUserPageApi, updateUserStatusApi} from "@/api/modules/user";

const defaultAvatar = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

const xGrid = ref();
const detailVisible = ref(false);
const currentUser = ref({});

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: {keyField: "customerId", isHover: true},
  columnConfig: {resizable: true},
  pagerConfig: {
    pageSize: 10,
    pageSizes: [10, 20, 50, 100]
  },
  toolbarConfig: {
    slots: {buttons: "toolbar_buttons"},
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
        field: "nickName",
        title: "昵称",
        span: 6,
        itemRender: {name: "$input", props: {placeholder: "昵称", clearable: true}}
      },
      {
        field: "mobile",
        title: "手机号",
        span: 6,
        itemRender: {name: "$input", props: {placeholder: "手机号", clearable: true}}
      },
      {
        field: "status",
        title: "状态",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            {label: "全部", value: ""},
            {label: "正常", value: "0"},
            {label: "冻结", value: "1"}
          ],
          props: {placeholder: "请选择状态", clearable: true}
        }
      },
      {
        span: 6,
        align: "center",
        itemRender: {
          name: "$buttons",
          children: [
            {props: {type: "submit", content: "搜索", status: "primary"}},
            {props: {type: "reset", content: "重置"}}
          ]
        }
      }
    ]
  },
  proxyConfig: {
    seq: true,
    form: true,
    response: {result: "list", total: "total"},
    ajax: {
      query: async ({page, form}) => {
        const params = {
          pageNum: page.currentPage,
          pageSize: page.pageSize,
          nickName: form.nickName || undefined,
          mobile: form.mobile || undefined,
          status: form.status === "" || form.status == null ? undefined : String(form.status)
        };
        try {
          const res = await getUserPageApi(params);
          return {list: res.data?.records || [], total: res.data?.total || 0};
        } catch (error) {
          console.error("获取用户列表失败:", error);
          return {list: [], total: 0};
        }
      }
    }
  },
  columns: [
    {type: "seq", width: 60, title: "序号"},
    {field: "avatar", title: "头像", width: 80, slots: {default: "avatar"}},
    {field: "mobile", title: "手机号", width: 130},
    {field: "nickName", title: "昵称", minWidth: 120},
    {field: "gender", title: "性别", width: 80, slots: {default: "gender"}},
    {field: "heightCm", title: "身高(cm)", width: 100},
    {field: "weightKg", title: "体重(kg)", width: 100},
    {field: "fitnessGoal", title: "健身目标", width: 100, slots: {default: "fitnessGoal"}},
    {field: "memberLevelName", title: "会员等级", width: 120},
    {field: "status", title: "状态", width: 90, slots: {default: "status"}},
    {field: "lastLoginTime", title: "最后登录", width: 170},
    {title: "操作", width: 100, fixed: "right", slots: {default: "action"}}
  ]
});

const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

const handleStatusChange = async row => {
  const prev = row.status === "0" ? "1" : "0";
  try {
    await updateUserStatusApi(row.customerId, row.status);
    ElMessage.success(row.status === "0" ? "已启用" : "已冻结");
  } catch {
    row.status = prev;
    ElMessage.error("状态更新失败");
  }
};

const handleView = row => {
  currentUser.value = row;
  detailVisible.value = true;
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}

.detail-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>
