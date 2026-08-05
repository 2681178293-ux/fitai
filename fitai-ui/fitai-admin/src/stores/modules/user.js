import { ref } from "vue";
import { defineStore } from "pinia";
import piniaPersistConfig from "@/stores/helper/persist";

export const useUserStore = defineStore(
  "fitai-user",
  () => {
    // State
    const token = ref("");
    const userInfo = ref({ name: "Wanli" });

    // Actions
    const setToken = newToken => {
      token.value = newToken;
    };

    const setUserInfo = info => {
      userInfo.value = info;
    };

    return {
      token,
      userInfo,
      setToken,
      setUserInfo
    };
  },
  {
    persist: piniaPersistConfig("fitai-user")
  }
);
