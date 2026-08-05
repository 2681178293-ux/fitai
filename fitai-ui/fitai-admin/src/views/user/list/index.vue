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

      <!-- 性别列 -->
      <template #gender="{ row }">
        <el-tag v-if="row.gender === 1" type="primary">男</el-tag>
        <el-tag v-else-if="row.gender === 2" type="danger">女</el-tag>
        <el-tag v-else type="info">未知</el-tag>
      </template>

      <!-- VIP等级列 -->
      <template #vipLevel="{ row }">
        <el-tag v-if="row.vipLevel === 0" type="info">普通用户</el-tag>
        <el-tag v-else-if="row.vipLevel === 1" type="warning">月卡会员</el-tag>
        <el-tag v-else-if="row.vipLevel === 2" type="success">季卡会员</el-tag>
        <el-tag v-else-if="row.vipLevel === 3" type="danger">年卡会员</el-tag>
      </template>

      <!-- 健身目标列 -->
      <template #fitnessGoal="{ row }">
        <span v-if="row.fitnessGoal === 1">减脂</span>
        <span v-else-if="row.fitnessGoal === 2">增肌</span>
        <span v-else-if="row.fitnessGoal === 3">塑形</span>
        <span v-else-if="row.fitnessGoal === 4">保持健康</span>
        <span v-else>-</span>
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
        <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
      </template>
    </vxe-grid>

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <!-- 头像展示 -->
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
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ currentUser.gender === 1 ? '男' : currentUser.gender === 2 ? '女' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="身高">{{
            currentUser.height ? currentUser.height + ' cm' : '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="体重">{{
            currentUser.weight ? currentUser.weight + ' kg' : '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="体脂率">{{
            currentUser.bodyFat ? currentUser.bodyFat + '%' : '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="VIP等级">
          {{ ['普通用户', '月卡会员', '季卡会员', '年卡会员'][currentUser.vipLevel] || '普通用户' }}
        </el-descriptions-item>
        <el-descriptions-item label="VIP到期时间">{{ currentUser.vipExpireTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="userList">
import {ref, reactive} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {Refresh, View} from "@element-plus/icons-vue";
import {getUserPageApi, updateUserStatusApi} from "@/api/modules/user";

const defaultAvatar = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

const xGrid = ref();
const detailVisible = ref(false);
const currentUser = ref({});

// 表格配置
const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: {keyField: "id", isHover: true},
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
      {field: "keyword", title: "关键词", span: 8, itemRender: {name: "$input", props: {placeholder: "手机号/昵称"}}},
      {
        field: "status",
        title: "状态",
        span: 8,
        itemRender: {
          name: "$select",
          options: [
            {label: "全部", value: ""},
            {label: "正常", value: 1},
            {label: "禁用", value: 0}
          ],
          props: {placeholder: "请选择状态"}
        }
      },
      {
        span: 8,
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
          ...form
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
    {field: "phone", title: "手机号", width: 130},
    {field: "nickname", title: "昵称", minWidth: 120},
    {field: "gender", title: "性别", width: 80, slots: {default: "gender"}},
    {field: "height", title: "身高(cm)", width: 100},
    {field: "weight", title: "体重(kg)", width: 100},
    {field: "fitnessGoal", title: "健身目标", width: 100, slots: {default: "fitnessGoal"}},
    {field: "vipLevel", title: "VIP等级", width: 120, slots: {default: "vipLevel"}},
    {field: "status", title: "状态", width: 80, slots: {default: "status"}},
    {field: "createTime", title: "注册时间", width: 170},
    {title: "操作", width: 100, fixed: "right", slots: {default: "action"}}
  ]
});

// 刷新
const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

// 状态变更
const handleStatusChange = async row => {
  try {
    await updateUserStatusApi(row.id, row.status);
    ElMessage.success(row.status === 1 ? "已启用" : "已禁用");
  } catch {
    row.status = row.status === 1 ? 0 : 1;
    ElMessage.error("状态更新失败");
  }
};

// 查看详情
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
