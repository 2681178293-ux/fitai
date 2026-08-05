import { ref } from "vue";
import { defineStore } from "pinia";
import router from "@/routers";
import { getUrlWithParams } from "@/utils";
import { useKeepAliveStore } from "./keepAlive";
import piniaPersistConfig from "@/stores/helper/persist";

export const useTabsStore = defineStore(
  "fitai-tabs",
  () => {
    // State
    const tabsMenuList = ref([]);

    // Actions
    const addTabs = async tabItem => {
      const keepAliveStore = useKeepAliveStore();
      if (tabsMenuList.value.every(item => item.path !== tabItem.path)) {
        tabsMenuList.value.push(tabItem);
      }
      // add keepalive
      if (!keepAliveStore.keepAliveName.includes(tabItem.name) && tabItem.isKeepAlive) {
        keepAliveStore.addKeepAliveName(tabItem.path);
      }
    };

    const removeTabs = async (tabPath, isCurrent = true) => {
      const keepAliveStore = useKeepAliveStore();
      if (isCurrent) {
        tabsMenuList.value.forEach((item, index) => {
          if (item.path !== tabPath) return;
          const nextTab = tabsMenuList.value[index + 1] || tabsMenuList.value[index - 1];
          if (!nextTab) return;
          router.push(nextTab.path);
        });
      }
      // remove keepalive
      const tabItem = tabsMenuList.value.find(item => item.path === tabPath);
      tabItem?.isKeepAlive && keepAliveStore.removeKeepAliveName(tabItem.path);
      // set tabs
      tabsMenuList.value = tabsMenuList.value.filter(item => item.path !== tabPath);
    };

    const closeTabsOnSide = async (path, type) => {
      const keepAliveStore = useKeepAliveStore();
      const currentIndex = tabsMenuList.value.findIndex(item => item.path === path);
      if (currentIndex !== -1) {
        const range = type === "left" ? [0, currentIndex] : [currentIndex + 1, tabsMenuList.value.length];
        tabsMenuList.value = tabsMenuList.value.filter((item, index) => {
          return index < range[0] || index >= range[1] || !item.close;
        });
      }
      // set keepalive
      const KeepAliveList = tabsMenuList.value.filter(item => item.isKeepAlive);
      keepAliveStore.setKeepAliveName(KeepAliveList.map(item => item.path));
    };

    const closeMultipleTab = async tabsMenuValue => {
      const keepAliveStore = useKeepAliveStore();
      tabsMenuList.value = tabsMenuList.value.filter(item => {
        return item.path === tabsMenuValue || !item.close;
      });
      // set keepalive
      const KeepAliveList = tabsMenuList.value.filter(item => item.isKeepAlive);
      keepAliveStore.setKeepAliveName(KeepAliveList.map(item => item.path));
    };

    const setTabs = async list => {
      tabsMenuList.value = list;
    };

    const setTabsTitle = async title => {
      tabsMenuList.value.forEach(item => {
        if (item.path == getUrlWithParams()) item.title = title;
      });
    };

    return {
      tabsMenuList,
      addTabs,
      removeTabs,
      closeTabsOnSide,
      closeMultipleTab,
      setTabs,
      setTabsTitle
    };
  },
  {
    persist: piniaPersistConfig("fitai-tabs")
  }
);
