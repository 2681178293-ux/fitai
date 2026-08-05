import { ref } from "vue";
import { defineStore } from "pinia";
import { DEFAULT_PRIMARY } from "@/config";
import piniaPersistConfig from "@/stores/helper/persist";

export const useGlobalStore = defineStore(
  "fitai-global",
  () => {
    // State - 修改默认值之后，需清除 localStorage 数据
    // 布局模式 (纵向：vertical | 经典：classic | 横向：transverse | 分栏：columns)
    const layout = ref("vertical");
    // element 组件大小
    const assemblySize = ref("default");
    // 当前系统语言
    const language = ref(null);
    // 当前页面是否全屏
    const maximize = ref(false);
    // 主题颜色
    const primary = ref(DEFAULT_PRIMARY);
    // 深色模式
    const isDark = ref(false);
    // 灰色模式
    const isGrey = ref(false);
    // 色弱模式
    const isWeak = ref(false);
    // 侧边栏反转
    const asideInverted = ref(false);
    // 头部反转
    const headerInverted = ref(false);
    // 折叠菜单
    const isCollapse = ref(false);
    // 菜单手风琴
    const accordion = ref(true);
    // 页面水印
    const watermark = ref(false);
    // 面包屑导航
    const breadcrumb = ref(true);
    // 面包屑导航图标
    const breadcrumbIcon = ref(true);
    // 标签页
    const tabs = ref(true);
    // 标签页图标
    const tabsIcon = ref(true);
    // 页脚
    const footer = ref(true);

    // Actions
    const setGlobalState = (key, value) => {
      const stateMap = {
        layout,
        assemblySize,
        language,
        maximize,
        primary,
        isDark,
        isGrey,
        isWeak,
        asideInverted,
        headerInverted,
        isCollapse,
        accordion,
        watermark,
        breadcrumb,
        breadcrumbIcon,
        tabs,
        tabsIcon,
        footer
      };
      if (stateMap[key]) {
        stateMap[key].value = value;
      }
    };

    return {
      layout,
      assemblySize,
      language,
      maximize,
      primary,
      isDark,
      isGrey,
      isWeak,
      asideInverted,
      headerInverted,
      isCollapse,
      accordion,
      watermark,
      breadcrumb,
      breadcrumbIcon,
      tabs,
      tabsIcon,
      footer,
      setGlobalState
    };
  },
  {
    persist: piniaPersistConfig("fitai-global")
  }
);
