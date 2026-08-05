import router from "@/routers/index";
import { LOGIN_URL } from "@/config";
import { ElNotification } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
import { useAuthStore } from "@/stores/modules/auth";

// 引入 views 文件夹下所有 vue 文件
const modules = import.meta.glob("@/views/**/*.vue");

// 创建一个映射，支持多种路径格式
const normalizedModules = {};
Object.keys(modules).forEach(key => {
  // 提取相对于 views 的路径
  const match = key.match(/views(.+)\.vue$/);
  if (match) {
    normalizedModules[match[1]] = modules[key];
  }
});

// 调试：打印 modules 的 key
console.log("Dynamic Router - Original modules:", Object.keys(modules));
console.log("Dynamic Router - Normalized modules:", Object.keys(normalizedModules));

/**
 * @description 初始化动态路由
 */
export const initDynamicRouter = async () => {
  const userStore = useUserStore();
  const authStore = useAuthStore();

  try {
    // 1.获取菜单列表 && 按钮权限列表
    await authStore.getAuthMenuList();
    await authStore.getAuthButtonList();

    console.log("Dynamic Router - authMenuListGet:", authStore.authMenuListGet);
    console.log("Dynamic Router - flatMenuListGet:", authStore.flatMenuListGet);

    // 2.判断当前用户有没有菜单权限
    if (!authStore.authMenuListGet.length) {
      ElNotification({
        title: "无权限访问",
        message: "当前账号无任何菜单权限，请联系系统管理员！",
        type: "warning",
        duration: 3000
      });
      userStore.setToken("");
      router.replace(LOGIN_URL);
      return Promise.reject("No permission");
    }

    // 3.添加动态路由（仅注册能解析到组件的菜单页；目录节点 path 可能为占位，扁平化后不再注册）
    authStore.flatMenuListGet.forEach(item => {
      item.children && delete item.children;
      if (!item.path) {
        console.warn("Dynamic Router - Skip route without path:", item.name);
        return;
      }
      if (item.component && typeof item.component == "string") {
        const component = normalizedModules[item.component];
        console.log("Dynamic Router - Loading component:", item.component, "exists:", !!component);
        if (component) {
          item.component = component;
        } else {
          console.warn("Dynamic Router - Component not found:", item.component);
          return;
        }
      }
      // 目录节点无 component，扁平化后无需 addRoute（侧栏用树形 authMenuList）
      if (typeof item.component !== "function") {
        return;
      }
      if (item.meta?.isFull) {
        router.addRoute(item);
      } else {
        router.addRoute("layout", item);
      }
    });
  } catch (error) {
    console.error("Dynamic Router - Error:", error);
    // 当按钮 || 菜单请求出错时，重定向到登陆页
    userStore.setToken("");
    router.replace(LOGIN_URL);
    return Promise.reject(error);
  }
};
