<template>
  <div 
    class="fixed -top-[25px] -right-[25px] z-[999] w-[55px] h-[55px] cursor-pointer bg-[var(--el-color-info)] rounded-full opacity-90 hover:bg-[var(--el-color-info-dark-2)] transition-colors"
    @click="exitMaximize"
  >
    <el-icon class="relative top-[46%] left-[19%] text-sm text-white">
      <CloseBold />
    </el-icon>
  </div>
</template>

<script setup>
import { useGlobalStore } from "@/stores/modules/global";
import { CloseBold } from "@element-plus/icons-vue";

const globalStore = useGlobalStore();
const exitMaximize = () => {
  globalStore.setGlobalState("maximize", false);
};
</script>
