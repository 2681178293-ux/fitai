import { ref } from "vue";
import { defineStore } from "pinia";

export const useKeepAliveStore = defineStore("fitai-keepAlive", () => {
  // State
  const keepAliveName = ref([]);

  // Actions
  const addKeepAliveName = name => {
    if (!keepAliveName.value.includes(name)) {
      keepAliveName.value.push(name);
    }
  };

  const removeKeepAliveName = name => {
    keepAliveName.value = keepAliveName.value.filter(item => item !== name);
  };

  const setKeepAliveName = (names = []) => {
    keepAliveName.value = names;
  };

  return {
    keepAliveName,
    addKeepAliveName,
    removeKeepAliveName,
    setKeepAliveName
  };
});
