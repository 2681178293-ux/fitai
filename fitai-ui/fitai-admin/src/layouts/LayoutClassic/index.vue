<!-- 经典布局 -->
<template>
  <el-container class="layout">
    <el-header>
      <div class="header-lf mask-image">
        <div class="logo flx-center">
          <img class="logo-img" src="@/assets/images/logo.svg" alt="logo" />
          <span class="logo-text">{{ title }}</span>
        </div>
        <ToolBarLeft />
      </div>
      <div class="header-ri">
        <ToolBarRight />
      </div>
    </el-header>
    <el-container class="classic-content">
      <el-aside>
        <div class="aside-box" :style="{ width: isCollapse ? '65px' : '210px' }">
          <el-scrollbar>
            <el-menu
              :router="false"
              :default-active="activeMenu"
              :collapse="isCollapse"
              :unique-opened="accordion"
              :collapse-transition="false"
            >
              <SubMenu :menu-list="menuList" />
            </el-menu>
          </el-scrollbar>
        </div>
      </el-aside>
      <el-container class="classic-main">
        <Main />
      </el-container>
    </el-container>
  </el-container>
</template>

<script setup name="layoutClassic">
import { computed } from "vue";
import { useRoute } from "vue-router";
import { useAuthStore } from "@/stores/modules/auth";
import { useGlobalStore } from "@/stores/modules/global";
import Main from "@/layouts/components/Main/index.vue";
import SubMenu from "@/layouts/components/Menu/SubMenu.vue";
import ToolBarLeft from "@/layouts/components/Header/ToolBarLeft.vue";
import ToolBarRight from "@/layouts/components/Header/ToolBarRight.vue";

const title = import.meta.env.VITE_GLOB_APP_TITLE;

const route = useRoute();
const authStore = useAuthStore();
const globalStore = useGlobalStore();
const accordion = computed(() => globalStore.accordion);
const isCollapse = computed(() => globalStore.isCollapse);
const menuList = computed(() => authStore.showMenuListGet);
const activeMenu = computed(() => (route.meta.activeMenu ? route.meta.activeMenu : route.path));
</script>

<style scoped>
.el-container {
  width: 100%;
  height: 100%;
}
.el-container :deep(.el-header) {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 55px;
  padding: 0 15px 0 0;
  background-color: var(--el-header-bg-color);
  border-bottom: 1px solid var(--el-header-border-color);
}
.el-container :deep(.el-header) .header-lf {
  display: flex;
  align-items: center;
  overflow: hidden;
  white-space: nowrap;
}
.el-container :deep(.el-header) .header-lf .logo {
  flex-shrink: 0;
  width: 210px;
  margin-right: 16px;
}
.el-container :deep(.el-header) .header-lf .logo .logo-img {
  width: 28px;
  object-fit: contain;
}
.el-container :deep(.el-header) .header-lf .logo .logo-text {
  margin-left: 6px;
  font-size: 21.5px;
  font-weight: bold;
  color: var(--el-header-logo-text-color);
  white-space: nowrap;
}
.classic-content {
  display: flex;
  height: calc(100% - 55px);
}
.classic-content :deep(.el-aside) {
  width: auto;
  background-color: var(--el-menu-bg-color);
  border-right: 1px solid var(--el-aside-border-color);
}
.classic-content :deep(.el-aside) .aside-box {
  display: flex;
  flex-direction: column;
  height: 100%;
  transition: width 0.3s ease;
}
.classic-content :deep(.el-aside) .aside-box .el-menu {
  width: 100%;
  overflow-x: hidden;
  border-right: none;
}
.classic-main {
  display: flex;
  flex-direction: column;
}
</style>
