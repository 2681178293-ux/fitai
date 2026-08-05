<template>
  <div class="tool-bar-ri">
    <div class="header-icon">
      <AssemblySize id="assemblySize" />
      <Language id="language" />
      <SearchMenu id="searchMenu" />
      <ThemeSetting id="themeSetting" />
      <Message id="message" />
      <Fullscreen id="fullscreen" />
    </div>
    <Avatar />
  </div>
</template>

<script setup>
import AssemblySize from "./components/AssemblySize.vue";
import Language from "./components/Language.vue";
import SearchMenu from "./components/SearchMenu.vue";
import ThemeSetting from "./components/ThemeSetting.vue";
import Message from "./components/Message.vue";
import Fullscreen from "./components/Fullscreen.vue";
import Avatar from "./components/Avatar.vue";
</script>

<style scoped>
.tool-bar-ri {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-right: 25px;
  gap: 20px;
}
.header-icon {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-right: 8px;
}
.header-icon > * {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6px;
  color: var(--el-header-text-color);
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s;
}
.header-icon > *:hover {
  background-color: rgba(0, 0, 0, 0.04);
}
/* 确保 el-badge 内部对齐 */
.header-icon :deep(.el-badge) {
  display: flex;
  align-items: center;
}
</style>
