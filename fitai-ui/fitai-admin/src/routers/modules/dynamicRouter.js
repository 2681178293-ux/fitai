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

/**
 * 库表 sys_menu.component 与 views 实际路径不一致时的兼容映射
 *（后端有 component 值时不会走 PATH_COMPONENT_MAP，需在前端兜底）
 */
const COMPONENT_ALIASES = {
  "/media/index": "/media/library/index",
  "/course/index": "/course/list/index",
  "/community/audit": "/community/moderation/index",
  "/community/audit/index": "/community/moderation/index",
  "/product/index": "/product/list/index",
  "/user/index": "/user/list/index",
  "/order/index": "/order/list/index",
  "/coach/index": "/coach/list/index",
  "/system/user": "/system/account/index",
  "/system/user/index": "/system/account/index",
  "/system/dept": "/system/department/index",
  "/system/dept/index": "/system/department/index",
  "/system/job": "/system/task/index",
  "/system/job/index": "/system/task/index",
  "/system/login-log": "/system/log/index",
  "/system/login-log/index": "/system/log/index",
  "/system/oper-log": "/system/log/index",
  "/system/oper-log/index": "/system/log/index"
};

const resolveViewComponent = rawPath => {
  if (!rawPath || typeof rawPath !== "string") return null;
  let componentPath = COMPONENT_ALIASES[rawPath] || rawPath;
  let component = normalizedModules[componentPath];
  if (!component && !componentPath.endsWith("/index")) {
    const withIndex = `${componentPath}/index`;
    componentPath = COMPONENT_ALIASES[withIndex] || withIndex;
    component = normalizedModules[componentPath];
  }
  return component ? { component, componentPath } : null;
};

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

    // 3.添加动态路由
    authStore.flatMenuListGet.forEach(item => {
      item.children && delete item.children;
      if (item.component && typeof item.component == "string") {
        const resolved = resolveViewComponent(item.component);
        if (resolved) {
          item.component = resolved.component;
        } else {
          console.warn("Dynamic Router - Component not found:", item.component);
        }
      }
      if (item.meta.isFull) {
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
