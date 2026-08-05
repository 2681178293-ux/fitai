<template>
  <el-dropdown trigger="click" @command="setAssemblySize">
    <el-icon class="toolBar-icon" :size="18"><Grid /></el-icon>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in assemblySizeList"
          :key="item.value"
          :command="item.value"
          :disabled="assemblySize === item.value"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from "vue";
import { useGlobalStore } from "@/stores/modules/global";
import { Grid } from "@element-plus/icons-vue";

const globalStore = useGlobalStore();
const assemblySize = computed(() => globalStore.assemblySize);

const assemblySizeList = [
  { label: "默认", value: "default" },
  { label: "大型", value: "large" },
  { label: "小型", value: "small" }
];

const setAssemblySize = item => {
  if (assemblySize.value === item) return;
  globalStore.setGlobalState("assemblySize", item);
};
</script>
