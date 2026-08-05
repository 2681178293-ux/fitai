<template>
  <div class="theme-setting">
    <el-icon class="toolBar-icon" :size="20" @click="openDrawer"><Brush /></el-icon>
  </div>
</template>

<script setup>
import mittBus from "@/utils/mittBus";
import { Brush } from "@element-plus/icons-vue";

const openDrawer = () => {
  mittBus.emit("openThemeDrawer");
};
</script>
