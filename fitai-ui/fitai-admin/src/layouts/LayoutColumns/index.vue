<!-- 分栏布局 -->
<template>
  <el-container class="layout">
    <div class="aside-split">
      <div class="logo flx-center">
        <img class="logo-img" src="@/assets/images/logo.svg" alt="logo" />
      </div>
      <el-scrollbar>
        <div class="split-list">
          <div
            v-for="item in menuList"
            :key="item.path"
            class="split-item"
            :class="{ 'split-active': splitActive === item.path || `/${splitActive.split('/')[1]}` === item.path }"
            @click="changeSubMenu(item)"
          >
            <el-icon>
              <component :is="item.meta.icon"></component>
            </el-icon>
            <span class="title">{{ item.meta.title }}</span>
          </div>
        </div>
      </el-scrollbar>
    </div>
    <el-aside :class="{ 'not-aside': !subMenuList.length }" :style="{ width: isCollapse ? '65px' : '210px' }">
      <div class="logo flx-center">
        <span v-show="subMenuList.length" class="logo-text">{{ isCollapse ? "G" : title }}</span>
      </div>
      <el-scrollbar>
        <el-menu
          :router="false"
          :default-active="activeMenu"
          :collapse="isCollapse"
          :unique-opened="accordion"
          :collapse-transition="false"
        >
          <SubMenu :menu-list="subMenuList" />
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <el-container>
      <el-header>
        <ToolBarLeft />
        <ToolBarRight />
      </el-header>
      <Main />
    </el-container>
  </el-container>
</template>

<script setup name="layoutColumns">
import { ref, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/modules/auth";
import { useGlobalStore } from "@/stores/modules/global";
import Main from "@/layouts/components/Main/index.vue";
import ToolBarLeft from "@/layouts/components/Header/ToolBarLeft.vue";
import ToolBarRight from "@/layouts/components/Header/ToolBarRight.vue";
import SubMenu from "@/layouts/components/Menu/SubMenu.vue";

const title = import.meta.env.VITE_GLOB_APP_TITLE;

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const globalStore = useGlobalStore();
const accordion = computed(() => globalStore.accordion);
const isCollapse = computed(() => globalStore.isCollapse);
const menuList = computed(() => authStore.showMenuListGet);
const activeMenu = computed(() => (route.meta.activeMenu ? route.meta.activeMenu : route.path));

const subMenuList = ref([]);
const splitActive = ref("");
watch(
  () => [menuList, route],
  () => {
    // 当前菜单没有数据直接 return
    if (!menuList.value.length) return;
    splitActive.value = route.path;
    const menuItem = menuList.value.filter(item => {
      return route.path === item.path || `/${route.path.split("/")[1]}` === item.path;
    });
    if (menuItem[0].children?.length) return (subMenuList.value = menuItem[0].children);
    subMenuList.value = [];
  },
  {
    deep: true,
    immediate: true
  }
);

// change SubMenu
const changeSubMenu = item => {
  splitActive.value = item.path;
  if (item.children?.length) return (subMenuList.value = item.children);
  subMenuList.value = [];
  router.push(item.path);
};
</script>

<style scoped>
.el-container {
  width: 100%;
  height: 100%;
}
.aside-split {
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  width: 70px;
  height: 100%;
  background-color: var(--el-menu-bg-color);
  border-right: 1px solid var(--el-aside-border-color);
}
.aside-split .logo {
  box-sizing: border-box;
  height: 55px;
}
.aside-split .logo .logo-img {
  width: 32px;
  object-fit: contain;
}
.aside-split .el-scrollbar {
  height: calc(100% - 55px);
}
.aside-split .split-list {
  flex: 1;
}
.aside-split .split-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 70px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.aside-split .split-item .el-icon {
  font-size: 20px;
  color: var(--el-menu-text-color);
}
.aside-split .split-item .title {
  margin-top: 6px;
  font-size: 12px;
  color: var(--el-menu-text-color);
}
.aside-split .split-active {
  background-color: var(--el-color-primary) !important;
}
.aside-split .split-active .el-icon,
.aside-split .split-active .title {
  color: #ffffff !important;
}
.not-aside {
  width: 0 !important;
  border-right: none !important;
}
.el-aside {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  background-color: var(--el-menu-bg-color);
  border-right: 1px solid var(--el-aside-border-color);
  transition: width 0.3s ease;
}
.el-aside .el-scrollbar {
  height: calc(100% - 55px);
}
.el-aside .el-scrollbar .el-menu {
  width: 100%;
  overflow-x: hidden;
  border-right: none;
}
.el-aside .logo {
  box-sizing: border-box;
  height: 55px;
}
.el-aside .logo .logo-text {
  font-size: 24px;
  font-weight: bold;
  color: var(--el-aside-logo-text-color);
  white-space: nowrap;
}
.el-header {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 55px;
  padding: 0 15px;
  background-color: var(--el-header-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);
}
</style>
