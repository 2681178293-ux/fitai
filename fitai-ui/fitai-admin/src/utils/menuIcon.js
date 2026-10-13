import * as ElementIcons from "@element-plus/icons-vue";

/**
 * 库表 component 与 views 实际路径不一致时的兼容映射
 */
const COMPONENT_PATH_ALIASES = {
  "/user/index": "/user/list/index",
  "/order/index": "/order/list/index",
  "/coach/index": "/coach/list/index"
};

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

function componentToRoutePath(component) {
  if (!component || typeof component !== "string") return null;
  const aliased = COMPONENT_PATH_ALIASES[component] || component;
  if (aliased.endsWith("/index")) return aliased.slice(0, -"/index".length);
  return aliased;
}

/**
 * 修正库表父子 path 相同导致的 Vue Router 自重定向死循环（Maximum call stack size exceeded）。
 * 例如：目录 /user redirect→ 子菜单 /user。
 */
function normalizeMenuRoutes(routes, parentPath = null) {
  if (!Array.isArray(routes)) return routes;
  return routes.map(item => {
    const next = { ...item };

    if (next.component && COMPONENT_PATH_ALIASES[next.component]) {
      next.component = COMPONENT_PATH_ALIASES[next.component];
    }

    // 子菜单 path 与父级相同 → 按 component 推导唯一 path
    if (parentPath && next.path === parentPath) {
      const derived = componentToRoutePath(next.component);
      next.path = derived && derived !== parentPath ? derived : `${parentPath}/list`;
    }

    if (next.children?.length) {
      next.children = normalizeMenuRoutes(next.children, next.path);
      // 自重定向或无效 redirect：改指向第一个可见子路由
      if (!next.redirect || next.redirect === next.path) {
        const firstChild = next.children.find(c => c.path && !c.meta?.isHide);
        if (firstChild?.path) next.redirect = firstChild.path;
        else delete next.redirect;
      }
    } else if (next.redirect === next.path) {
      delete next.redirect;
    }

    return next;
  });
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
 * 登录后拉取的动态路由树：统一净化 meta.icon，并修复 path/redirect 冲突。
 */
export function sanitizeMenuRouterList(routes) {
  return normalizeMenuRoutes(walkRoutes(routes ?? []));
}
