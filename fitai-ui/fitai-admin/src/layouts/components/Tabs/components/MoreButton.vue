<template>
  <el-dropdown trigger="click" :teleported="false">
    <div class="more-button">
      <el-icon :size="16"><ArrowDown /></el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="refresh">
          <el-icon><Refresh /></el-icon>{{ $t("tabs.refresh") }}
        </el-dropdown-item>
        <el-dropdown-item @click="maximize">
          <el-icon><FullScreen /></el-icon>{{ $t("tabs.maximize") }}
        </el-dropdown-item>
        <el-dropdown-item divided @click="closeCurrentTab">
          <el-icon><Remove /></el-icon>{{ $t("tabs.closeCurrent") }}
        </el-dropdown-item>
        <el-dropdown-item @click="tabStore.closeTabsOnSide(route.fullPath, 'left')">
          <el-icon><DArrowLeft /></el-icon>{{ $t("tabs.closeLeft") }}
        </el-dropdown-item>
        <el-dropdown-item @click="tabStore.closeTabsOnSide(route.fullPath, 'right')">
          <el-icon><DArrowRight /></el-icon>{{ $t("tabs.closeRight") }}
        </el-dropdown-item>
        <el-dropdown-item divided @click="tabStore.closeMultipleTab(route.fullPath)">
          <el-icon><CircleClose /></el-icon>{{ $t("tabs.closeOther") }}
        </el-dropdown-item>
        <el-dropdown-item @click="closeAllTab">
          <el-icon><FolderDelete /></el-icon>{{ $t("tabs.closeAll") }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { inject, nextTick } from "vue";
import { HOME_URL } from "@/config";
import { useTabsStore } from "@/stores/modules/tabs";
import { useGlobalStore } from "@/stores/modules/global";
import { useKeepAliveStore } from "@/stores/modules/keepAlive";
import { useRoute, useRouter } from "vue-router";
import { ArrowDown } from "@element-plus/icons-vue";

const route = useRoute();
const router = useRouter();
const tabStore = useTabsStore();
const globalStore = useGlobalStore();
const keepAliveStore = useKeepAliveStore();

// refresh current page
const refreshCurrentPage = inject("refresh");
const refresh = () => {
  setTimeout(() => {
    route.meta.isKeepAlive && keepAliveStore.removeKeepAliveName(route.fullPath);
    refreshCurrentPage(false);
    nextTick(() => {
      route.meta.isKeepAlive && keepAliveStore.addKeepAliveName(route.fullPath);
      refreshCurrentPage(true);
    });
  }, 0);
};

// maximize current page
const maximize = () => {
  globalStore.setGlobalState("maximize", true);
};

// Close Current
const closeCurrentTab = () => {
  if (route.meta.isAffix) return;
  tabStore.removeTabs(route.fullPath);
};

// Close All
const closeAllTab = () => {
  tabStore.closeMultipleTab();
  router.push(HOME_URL);
};
</script>

<style scoped>
.more-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 100%;
  cursor: pointer;
  border-left: 1px solid var(--el-border-color-light);
  transition: all 0.3s;
}
.more-button:hover {
  background-color: var(--el-color-info-light-9);
}
.more-button .el-icon {
  color: var(--el-text-color-regular);
}
</style>
