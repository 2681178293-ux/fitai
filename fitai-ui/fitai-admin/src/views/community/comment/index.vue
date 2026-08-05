<template>
  <div class="table-box p-5">
    <vxe-grid ref="xGrid" v-bind="gridOptions">
      <template #toolbar_buttons>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
      </template>

      <template #content="{ row }">
        <div class="max-w-xs truncate" :title="row.content">{{ row.content || "-" }}</div>
      </template>

      <template #top="{ row }">
        <el-tag v-if="row.isTop === 1" type="warning" size="small">置顶</el-tag>
        <span v-else class="text-gray-400">-</span>
      </template>

      <template #status="{ row }">
        <el-tag v-if="row.status === 1" type="success" size="small">正常</el-tag>
        <el-tag v-else type="info" size="small">已删除</el-tag>
      </template>

      <template #action="{ row }">
        <el-button
          v-if="row.isTop === 1"
          type="info"
          link
          :icon="Bottom"
          @click="handleTop(row, 0)"
        >
          取消置顶
        </el-button>
        <el-button v-else type="warning" link :icon="Top" @click="handleTop(row, 1)">置顶</el-button>
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
  </div>
</template>

<script setup name="communityComment">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, Delete, Top, Bottom } from "@element-plus/icons-vue";
import {
  getCommentPageApi,
  deleteCommentApi,
  batchDeleteCommentApi,
  setCommentTopApi
} from "@/api/modules/biz-ext";

const xGrid = ref();

const gridOptions = reactive({
  border: true,
  showOverflow: true,
  height: 600,
  loading: false,
  rowConfig: { keyField: "id" },
  checkboxConfig: { reserve: true },
  toolbarConfig: { slots: { buttons: "toolbar_buttons" }, refresh: true, zoom: false },
  proxyConfig: {
    form: true,
    response: { result: "list", total: "total" },
    ajax: {
      query: async ({ page, form }) => {
        const params = { pageNum: page.currentPage, pageSize: page.pageSize, ...form };
        try {
          const res = await getCommentPageApi(params);
          const data = res.data || {};
          return { list: data.records || [], total: data.total || 0 };
        } catch (e) {
          return { list: [], total: 0 };
        }
      }
    }
  },
  formConfig: {
    items: [
      { field: "keyword", title: "关键词", span: 5, itemRender: { name: "$input", props: { placeholder: "评论内容" } } },
      { field: "postId", title: "动态ID", span: 5, itemRender: { name: "$input", props: { placeholder: "动态ID" } } },
      { field: "userId", title: "用户ID", span: 5, itemRender: { name: "$input", props: { placeholder: "用户ID" } } },
      {
        field: "status",
        title: "状态",
        span: 5,
        itemRender: {
          name: "$select",
          options: [
            { label: "全部", value: "" },
            { label: "正常", value: 1 },
            { label: "已删除", value: 0 }
          ],
          props: { placeholder: "请选择" }
        }
      },
      {
        span: 4,
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
  pagerConfig: { enabled: true },
  columns: [
    { type: "checkbox", width: 50 },
    { type: "seq", width: 60 },
    { field: "id", title: "ID", width: 90 },
    { field: "userNickname", title: "评论人", width: 120 },
    { field: "postExcerpt", title: "所属动态", minWidth: 160 },
    { field: "content", title: "评论内容", minWidth: 200, slots: { default: "content" } },
    { field: "likeCount", title: "点赞", width: 80 },
    { field: "isTop", title: "置顶", width: 80, slots: { default: "top" } },
    { field: "status", title: "状态", width: 90, slots: { default: "status" } },
    { field: "createTime", title: "评论时间", width: 170 },
    { title: "操作", width: 180, fixed: "right", slots: { default: "action" } }
  ]
});

const handleRefresh = () => xGrid.value?.commitProxy("query");

const handleTop = (row, top) => {
  setCommentTopApi(row.id, top)
    .then(() => {
      ElMessage.success(top === 1 ? "已置顶" : "已取消置顶");
      handleRefresh();
    })
    .catch(() => ElMessage.error("操作失败"));
};

const handleDelete = row => {
  ElMessageBox.confirm("确定删除该评论？", "提示", { type: "warning" }).then(async () => {
    try {
      await deleteCommentApi(row.id);
      ElMessage.success("删除成功");
      handleRefresh();
    } catch (e) {
      ElMessage.error("删除失败");
    }
  });
};

const handleBatchDelete = () => {
  const rows = xGrid.value?.getCheckboxRecords() || [];
  if (!rows.length) {
    ElMessage.warning("请先勾选要删除的评论");
    return;
  }
  const ids = rows.map(r => r.id);
  ElMessageBox.confirm(`确定删除选中的 ${ids.length} 条评论？`, "提示", { type: "warning" }).then(
    async () => {
      try {
        await batchDeleteCommentApi(ids);
        ElMessage.success("批量删除成功");
        xGrid.value?.clearCheckboxRow();
        handleRefresh();
      } catch (e) {
        ElMessage.error("批量删除失败");
      }
    }
  );
};
</script>

<style scoped>
.table-box {
  background: #fff;
  border-radius: 8px;
}
</style>
