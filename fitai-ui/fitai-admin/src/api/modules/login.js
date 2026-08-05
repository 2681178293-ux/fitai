import http from "@/api";
import authMenuList from "@/assets/json/authMenuList.json";
import authButtonList from "@/assets/json/authButtonList.json";

/**
 * @name 登录模块
 */

// 是否使用动态菜单（可根据环境变量或配置切换）
const USE_DYNAMIC_MENU = true;

// 管理员登录
export const loginApi = params => {
  return http.post(`/admin/login`, params, { loading: false });
};

// 获取管理员信息
export const getAdminInfoApi = () => {
  return http.get(`/admin/info`, {}, { loading: false });
};

// 获取菜单列表
export const getAuthMenuListApi = async () => {
  if (USE_DYNAMIC_MENU) {
    // 使用动态菜单接口
    return http.get(`/admin/system/menu/routers`, {}, { loading: false });
  }
  // 使用静态菜单
  return authMenuList;
};

// 获取按钮权限
export const getAuthButtonListApi = () => {
  // TODO: 后端实现后替换为真实接口
  // return http.get(`/admin/auth/buttons`, {}, { loading: false });
  return authButtonList;
};

// 管理员退出登录
export const logoutApi = () => {
  return http.post(`/admin/logout`);
};
