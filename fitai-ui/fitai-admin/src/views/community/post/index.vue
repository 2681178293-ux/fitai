<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <!-- 工具栏按钮 -->
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </template>

      <!-- 类型列 -->
      <template #type="{ row }">
        <el-tag :type="typeMap[row.type]?.type || 'info'" size="small">
          {{ typeMap[row.type]?.label || row.type }}
        </el-tag>
      </template>

      <!-- 状态列 -->
      <template #status="{ row }">
        <el-tag :type="statusMap[row.status]?.type || 'info'" size="small">
          {{ statusMap[row.status]?.label || row.status }}
        </el-tag>
      </template>

      <!-- 用户列 -->
      <template #user="{ row }">
        <div class="flex items-center gap-2">
          <el-avatar :size="28" :src="row.userAvatar">
            {{ row.userNickname?.charAt(0) || '-' }}
          </el-avatar>
          <span>{{ row.userNickname || '-' }}</span>
        </div>
      </template>

      <!-- 内容列 -->
      <template #content="{ row }">
        <div class="max-w-xs truncate" :title="row.content">{{ row.content || '-' }}</div>
      </template>

      <!-- 图片列 -->
      <template #images="{ row }">
        <div v-if="row.images?.length" class="flex gap-1 flex-wrap">
          <el-image
            v-for="(img, idx) in (row.images || []).slice(0, 3)"
            :key="idx"
            :src="img"
            :preview-src-list="row.images"
            class="w-10 h-10 rounded object-cover"
            fit="cover"
          />
          <span v-if="row.images?.length > 3" class="text-gray-400 text-xs">+{{ row.images.length - 3 }}</span>
        </div>
        <span v-else class="text-gray-400">-</span>
      </template>

      <!-- 操作列 -->
      <template #action="{ row }">
        <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
        <el-button
          v-if="row.status === 1"
          type="danger"
          link
          :icon="Delete"
          @click="handleDelete(row)"
        >
          删除
        </el-button>
      </template>
    </vxe-grid>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="动态详情" width="600px">
      <el-descriptions v-if="currentPost.id" :column="1" border>
        <el-descriptions-item label="动态ID">{{ currentPost.id }}</el-descriptions-item>
        <el-descriptions-item label="发布者">
          <div class="flex items-center gap-2">
            <el-avatar :size="32" :src="currentPost.userAvatar">
              {{ currentPost.userNickname?.charAt(0) || '-' }}
            </el-avatar>
            {{ currentPost.userNickname }} (ID: {{ currentPost.userId }})
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="typeMap[currentPost.type]?.type || 'info'" size="small">
            {{ typeMap[currentPost.type]?.label || currentPost.type }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusMap[currentPost.status]?.type || 'info'" size="small">
            {{ statusMap[currentPost.status]?.label || currentPost.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ currentPost.likeCount ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="评论数">{{ currentPost.commentCount ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ currentPost.createTime }}</el-descriptions-item>
        <el-descriptions-item label="内容">
          <div class="whitespace-pre-wrap">{{ currentPost.content || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="图片">
          <div v-if="currentPost.images?.length" class="flex gap-2 flex-wrap">
            <el-image
              v-for="(img, idx) in currentPost.images"
              :key="idx"
              :src="img"
              :preview-src-list="currentPost.images"
              class="w-20 h-20 rounded object-cover cursor-pointer"
              fit="cover"
            />
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="communityPostList">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, View, Delete } from "@element-plus/icons-vue";
import {
  getCommunityPostPageApi,
  getCommunityPostDetailApi,
  deleteCommunityPostApi
} from "@/api/modules/community";

const xGrid = ref();
const detailVisible = ref(false);
const currentPost = ref({});

// 类型映射
const typeMap = {
  normal: { label: "普通", type: "info" },
  checkin: { label: "打卡", type: "success" },
  challenge: { label: "挑战", type: "warning" }
};

// 状态映射
const statusMap = {
  0: { label: "已删除", type: "info" },
  1: { label: "正常", type: "success" }
};

// 表格配置
const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id", isHover: true },
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
        field: "keyword",
        title: "内容关键词",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "搜索动态内容" } }
      },
      {
        field: "userId",
        title: "用户ID",
        span: 6,
        itemRender: { name: "$input", props: { placeholder: "用户ID" } }
      },
      {
        field: "type",
        title: "类型",
        span: 6,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "普通", value: "normal" },
            { label: "打卡", value: "checkin" },
            { label: "挑战", value: "challenge" }
          ],
          props: { placeholder: "请选择类型" }
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
            { label: "正常", value: 1 },
            { label: "已删除", value: 0 }
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
          const res = await getCommunityPostPageApi(params);
          return { list: res.data?.records || [], total: res.data?.total || 0 };
        } catch (error) {
          console.error("获取社区动态列表失败:", error);
          return { list: [], total: 0 };
        }
      }
    }
  },
  columns: [
    { type: "seq", width: 60, title: "序号" },
    { field: "id", title: "ID", width: 100 },
    { field: "user", title: "发布者", width: 140, slots: { default: "user" } },
    { field: "content", title: "内容", minWidth: 200, slots: { default: "content" } },
    { field: "images", title: "图片", width: 140, slots: { default: "images" } },
    { field: "type", title: "类型", width: 90, slots: { default: "type" } },
    { field: "likeCount", title: "点赞", width: 80 },
    { field: "commentCount", title: "评论", width: 80 },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { field: "createTime", title: "发布时间", width: 170 },
    { title: "操作", width: 150, fixed: "right", slots: { default: "action" } }
  ]
});

// 刷新
const handleRefresh = () => {
  xGrid.value.commitProxy("query");
};

// 查看详情
const handleView = row => {
  currentPost.value = { ...row };
  detailVisible.value = true;
};

// 删除
const handleDelete = row => {
  ElMessageBox.confirm(
    `确定要删除该动态吗？删除后用户将无法查看此内容。`,
    "删除确认",
    { type: "warning" }
  ).then(async () => {
    try {
      await deleteCommunityPostApi(row.id);
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
