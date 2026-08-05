<!-- 横向布局 -->
<template>
  <el-container class="layout">
    <el-header>
      <div class="logo flx-center">
        <img class="logo-img" src="@/assets/images/logo.svg" alt="logo" />
        <span class="logo-text">{{ title }}</span>
      </div>
      <el-menu mode="horizontal" :router="false" :default-active="activeMenu">
        <!-- 不能直接使用 SubMenu 组件，无法触发 el-menu 隐藏省略功能 -->
        <template v-for="subItem in menuList" :key="subItem.path">
          <el-sub-menu v-if="subItem.children?.length" :key="subItem.path" :index="subItem.path + 'el-sub-menu'">
            <template #title>
              <el-icon>
                <component :is="subItem.meta.icon"></component>
              </el-icon>
              <span>{{ subItem.meta.title }}</span>
            </template>
            <SubMenu :menu-list="subItem.children" />
          </el-sub-menu>
          <el-menu-item v-else :key="subItem.path + 'el-menu-item'" :index="subItem.path" @click="handleClickMenu(subItem)">
            <el-icon>
              <component :is="subItem.meta.icon"></component>
            </el-icon>
            <template #title>
              <span>{{ subItem.meta.title }}</span>
            </template>
          </el-menu-item>
        </template>
      </el-menu>
      <ToolBarRight />
    </el-header>
    <Main />
  </el-container>
</template>

<script setup name="layoutTransverse">
import { computed } from "vue";
import { useAuthStore } from "@/stores/modules/auth";
import { useRoute, useRouter } from "vue-router";
import Main from "@/layouts/components/Main/index.vue";
import ToolBarRight from "@/layouts/components/Header/ToolBarRight.vue";
import SubMenu from "@/layouts/components/Menu/SubMenu.vue";

const title = import.meta.env.VITE_GLOB_APP_TITLE;

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const menuList = computed(() => authStore.showMenuListGet);
const activeMenu = computed(() => (route.meta.activeMenu ? route.meta.activeMenu : route.path));

const handleClickMenu = subItem => {
  if (subItem.meta.isLink) return window.open(subItem.meta.isLink, "_blank");
  router.push(subItem.path);
};
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
.el-container :deep(.el-header) .logo {
  width: 210px;
  margin-right: 30px;
}
.el-container :deep(.el-header) .logo .logo-img {
  width: 28px;
  object-fit: contain;
}
.el-container :deep(.el-header) .logo .logo-text {
  margin-left: 6px;
  font-size: 21.5px;
  font-weight: bold;
  color: var(--el-header-logo-text-color);
  white-space: nowrap;
}
.el-container :deep(.el-header) .el-menu {
  flex: 1;
  height: 100%;
  overflow: hidden;
  border-bottom: none;
}
.el-container :deep(.el-header) .el-menu .el-sub-menu__hide-arrow {
  width: 65px;
  height: 55px;
}
.el-container :deep(.el-header) .el-menu .el-menu-item.is-active {
  color: #ffffff !important;
}
.el-container :deep(.el-header) .el-menu .is-active {
  background-color: var(--el-color-primary) !important;
  border-bottom-color: var(--el-color-primary) !important;
}
.el-container :deep(.el-header) .el-menu .is-active::before {
  width: 0;
}
.el-container :deep(.el-header) .el-menu .is-active .el-sub-menu__title {
  color: #ffffff !important;
  background-color: var(--el-color-primary) !important;
  border-bottom-color: var(--el-color-primary) !important;
}
@media screen and (max-width: 730px) {
  .el-container :deep(.el-header) .logo {
    display: none !important;
  }
}
</style>
