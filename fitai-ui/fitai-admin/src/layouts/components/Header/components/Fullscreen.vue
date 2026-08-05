<template>
  <div class="fullscreen">
    <el-icon class="toolBar-icon" :size="20" @click="handleFullScreen">
      <FullScreen v-if="!isFullscreen" />
      <Aim v-else />
    </el-icon>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import screenfull from "screenfull";
import { FullScreen, Aim } from "@element-plus/icons-vue";

const isFullscreen = ref(screenfull.isFullscreen);

onMounted(() => {
  screenfull.on("change", () => {
    if (screenfull.isFullscreen) isFullscreen.value = true;
    else isFullscreen.value = false;
  });
});

const handleFullScreen = () => {
  if (!screenfull.isEnabled) ElMessage.warning("当前您的浏览器不支持全屏 ❌");
  screenfull.toggle();
};
</script>
