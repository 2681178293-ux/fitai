<template>
  <div :class="['breadcrumb-box mask-image', !globalStore.breadcrumbIcon && 'no-icon']">
    <el-breadcrumb :separator-icon="ArrowRight">
      <transition-group name="breadcrumb">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.path">
          <div
            class="el-breadcrumb__inner is-link"
            :class="{ 'item-no-icon': !item.meta.icon }"
            @click="onBreadcrumbClick(item, index)"
          >
            <el-icon v-if="item.meta.icon && globalStore.breadcrumbIcon" class="breadcrumb-icon">
              <component :is="item.meta.icon"></component>
            </el-icon>
            <span class="breadcrumb-title">{{ item.meta.title }}</span>
          </div>
        </el-breadcrumb-item>
      </transition-group>
    </el-breadcrumb>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { HOME_URL } from "@/config";
import { useRoute, useRouter } from "vue-router";
import { ArrowRight } from "@element-plus/icons-vue";
import { useAuthStore } from "@/stores/modules/auth";
import { useGlobalStore } from "@/stores/modules/global";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const globalStore = useGlobalStore();

const breadcrumbList = computed(() => {
  let breadcrumbData = authStore.breadcrumbListGet[route.matched[route.matched.length - 1].path] ?? [];
  // 🙅‍♀️不需要首页面包屑可删除以下判断
  if (breadcrumbData[0].path !== HOME_URL) {
    breadcrumbData = [{ path: HOME_URL, meta: { icon: "HomeFilled", title: "首页" } }, ...breadcrumbData];
  }
  return breadcrumbData;
});

// Click Breadcrumb
const onBreadcrumbClick = (item, index) => {
  if (index !== breadcrumbList.value.length - 1) router.push(item.path);
};
</script>

<style scoped>
.breadcrumb-box {
  display: flex;
  align-items: center;
  overflow: hidden;
}
.breadcrumb-box .el-breadcrumb {
  white-space: nowrap;
}
.breadcrumb-box .el-breadcrumb__item {
  position: relative;
  display: inline-block;
  float: none;
}
.breadcrumb-box .el-breadcrumb__item .item-no-icon {
  transform: translateY(-3px);
}
.breadcrumb-box .el-breadcrumb__item .el-breadcrumb__inner {
  display: inline-flex;
}
.breadcrumb-box .el-breadcrumb__item .el-breadcrumb__inner.is-link {
  color: var(--el-header-text-color);
}
.breadcrumb-box .el-breadcrumb__item .el-breadcrumb__inner.is-link:hover {
  color: var(--el-color-primary);
}
.breadcrumb-box .el-breadcrumb__item .breadcrumb-icon {
  margin-top: 1px;
  margin-right: 6px;
  font-size: 16px;
}
.breadcrumb-box .el-breadcrumb__item .breadcrumb-title {
  margin-top: 2px;
}
.breadcrumb-box .el-breadcrumb__item:last-child .el-breadcrumb__inner,
.breadcrumb-box .el-breadcrumb__item:last-child .el-breadcrumb__inner:hover {
  color: var(--el-header-text-color-regular);
}
.breadcrumb-box .el-breadcrumb__item :deep(.el-breadcrumb__separator) {
  transform: translateY(-1px);
}
.no-icon .el-breadcrumb .el-breadcrumb__item {
  top: -2px;
}
.no-icon .el-breadcrumb .el-breadcrumb__item :deep(.el-breadcrumb__separator) {
  top: 4px;
}
.no-icon .el-breadcrumb .el-breadcrumb__item .item-no-icon {
  transform: translateY(0);
}
</style>
