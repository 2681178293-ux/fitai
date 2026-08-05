<template>
  <div class="card p-4">
    <el-alert
      title="定时任务管理"
      type="info"
      description="本系统使用 XXL-Job 分布式任务调度平台管理定时任务，请点击下方按钮访问 XXL-Job 管理控制台。"
      show-icon
      :closable="false"
      class="mb-4"
    />

    <div class="flex flex-col items-center justify-center py-20">
      <el-icon :size="80" color="#409EFF" class="mb-4">
        <Timer />
      </el-icon>
      <h2 class="text-xl font-bold mb-2">XXL-Job 任务调度平台</h2>
      <p class="text-gray-500 mb-6">分布式任务调度框架，轻量级、易扩展</p>
      
      <div class="flex gap-4">
        <el-button type="primary" size="large" @click="openXxlJob">
          <el-icon class="mr-2"><Link /></el-icon>
          打开 XXL-Job 控制台
        </el-button>
        <el-button size="large" @click="openXxlJobDocs">
          <el-icon class="mr-2"><Document /></el-icon>
          查看文档
        </el-button>
      </div>

      <el-divider />

      <div class="text-center">
        <h3 class="font-bold mb-4">快速说明</h3>
        <el-descriptions :column="1" border class="max-w-xl">
          <el-descriptions-item label="控制台地址">
            <el-link type="primary" :href="xxlJobUrl" target="_blank">{{ xxlJobUrl }}</el-link>
          </el-descriptions-item>
          <el-descriptions-item label="默认账号">admin</el-descriptions-item>
          <el-descriptions-item label="默认密码">123456</el-descriptions-item>
          <el-descriptions-item label="执行器 AppName">fitai-executor</el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
  </div>
</template>

<script setup name="task">
import { Timer, Link, Document } from "@element-plus/icons-vue";

// XXL-Job 控制台地址（根据实际部署修改）
const xxlJobUrl = "http://192.168.49.110:8080/xxl-job-admin";

const openXxlJob = () => {
  window.open(xxlJobUrl, "_blank");
};

const openXxlJobDocs = () => {
  window.open("https://www.xuxueli.com/xxl-job/", "_blank");
};
</script>

<style scoped>
.card {
  background: var(--el-bg-color);
  border-radius: 4px;
}
</style>
