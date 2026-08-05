import * as ElementIcons from "@element-plus/icons-vue";

/**
 * 菜单 / 表格中的 icon 须为已全局注册的 Element Plus 图标组件名（如 HomeFilled）。
 * 若为纯数字等非法 DOM 标签名，Vue 会执行 createElement('11') 并抛出 InvalidCharacterError。
 * @param {*} icon 后端或表单中的图标字段
 * @returns {string|undefined} 合法图标名，无效时返回 undefined
 */
export function sanitizeMenuIcon(icon) {
  if (icon == null || icon === "") return undefined;
  const name = typeof icon === "string" ? icon.trim() : String(icon).trim();
  if (!name) return undefined;
  if (!/^[A-Za-z_]/.test(name)) return undefined;
  return Object.prototype.hasOwnProperty.call(ElementIcons, name) ? name : undefined;
}

function walkRoutes(routes) {
  if (!Array.isArray(routes)) return routes;
  return routes.map(item => {
    const next = { ...item };
    if (next.meta && next.meta.icon !== undefined && next.meta.icon !== null) {
      next.meta = { ...next.meta, icon: sanitizeMenuIcon(next.meta.icon) };
    }
    if (next.children?.length) {
      next.children = walkRoutes(next.children);
    }
    return next;
  });
}

/**
 * 登录后拉取的动态路由树：统一净化 meta.icon，避免非法 :is 渲染。
 */
export function sanitizeMenuRouterList(routes) {
  return walkRoutes(routes ?? []);
}
