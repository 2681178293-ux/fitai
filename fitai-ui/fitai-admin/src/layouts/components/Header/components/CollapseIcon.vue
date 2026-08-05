<template>
  <el-icon class="mr-5 text-[22px] text-[var(--el-header-text-color)] cursor-pointer" @click="changeCollapse">
    <component :is="globalStore.isCollapse ? 'expand' : 'fold'"></component>
  </el-icon>
</template>

<script setup>
import { useGlobalStore } from "@/stores/modules/global";

const globalStore = useGlobalStore();
const changeCollapse = () => globalStore.setGlobalState("isCollapse", !globalStore.isCollapse);
</script>
