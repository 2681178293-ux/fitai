import http from "@/api";

// ========== 用户管理 ==========

/**
 * 分页查询用户
 */
export const getSysUserList = params => {
  return http.get("/admin/system/user/list", params);
};

/**
 * 获取用户详情
 */
export const getSysUserById = id => {
  return http.get(`/admin/system/user/${id}`);
};

/**
 * 新增用户
 */
export const addSysUser = data => {
  return http.post("/admin/system/user", data);
};

/**
 * 修改用户
 */
export const updateSysUser = data => {
  return http.put("/admin/system/user", data);
};

/**
 * 删除用户
 */
export const deleteSysUser = ids => {
  return http.delete(`/admin/system/user/${ids}`);
};

/**
 * 重置密码
 */
export const resetSysUserPwd = (userId, password) => {
  return http.put("/admin/system/user/resetPwd", null, { params: { userId, password } });
};

/**
 * 修改用户状态
 */
export const changeSysUserStatus = (userId, status) => {
  return http.put("/admin/system/user/status", null, { params: { userId, status } });
};

// ========== 角色管理 ==========

/**
 * 分页查询角色
 */
export const getSysRoleList = params => {
  return http.get("/admin/system/role/list", params);
};

/**
 * 获取所有角色
 */
export const getSysRoleAll = () => {
  return http.get("/admin/system/role/all");
};

/**
 * 获取角色详情
 */
export const getSysRoleById = id => {
  return http.get(`/admin/system/role/${id}`);
};

/**
 * 新增角色
 */
export const addSysRole = data => {
  return http.post("/admin/system/role", data);
};

/**
 * 修改角色
 */
export const updateSysRole = data => {
  return http.put("/admin/system/role", data);
};

/**
 * 删除角色
 */
export const deleteSysRole = ids => {
  return http.delete(`/admin/system/role/${ids}`);
};

/**
 * 修改角色状态
 */
export const changeSysRoleStatus = (roleId, status) => {
  return http.put("/admin/system/role/status", null, { params: { roleId, status } });
};

// ========== 菜单管理 ==========

/**
 * 获取菜单列表（树形）
 */
export const getSysMenuList = () => {
  return http.get("/admin/system/menu/list");
};

/**
 * 获取当前用户菜单
 */
export const getSysMenuUser = () => {
  return http.get("/admin/system/menu/user");
};

/**
 * 获取菜单下拉树
 */
export const getSysMenuSelect = () => {
  return http.get("/admin/system/menu/select");
};

/**
 * 获取菜单详情
 */
export const getSysMenuById = id => {
  return http.get(`/admin/system/menu/${id}`);
};

/**
 * 新增菜单
 */
export const addSysMenu = data => {
  return http.post("/admin/system/menu", data);
};

/**
 * 修改菜单
 */
export const updateSysMenu = data => {
  return http.put("/admin/system/menu", data);
};

/**
 * 删除菜单
 */
export const deleteSysMenu = id => {
  return http.delete(`/admin/system/menu/${id}`);
};

/**
 * 清空全部用户权限缓存（Sa-Token 权限字符串）
 */
export const invalidateSysMenuPermCache = () => {
  return http.post("/admin/system/menu/invalidate-perm-cache");
};

// ========== 部门管理 ==========

/**
 * 获取部门列表（树形）
 */
export const getSysDeptList = () => {
  return http.get("/admin/system/dept/list");
};

/**
 * 获取部门下拉树
 */
export const getSysDeptSelect = () => {
  return http.get("/admin/system/dept/select");
};

/**
 * 获取部门详情
 */
export const getSysDeptById = id => {
  return http.get(`/admin/system/dept/${id}`);
};

/**
 * 新增部门
 */
export const addSysDept = data => {
  return http.post("/admin/system/dept", data);
};

/**
 * 修改部门
 */
export const updateSysDept = data => {
  return http.put("/admin/system/dept", data);
};

/**
 * 删除部门
 */
export const deleteSysDept = id => {
  return http.delete(`/admin/system/dept/${id}`);
};

// ========== 字典管理 ==========

/**
 * 分页查询字典类型
 */
export const getSysDictTypeList = params => {
  return http.get("/admin/system/dict/type/list", params);
};

/**
 * 获取所有字典类型
 */
export const getSysDictTypeAll = () => {
  return http.get("/admin/system/dict/type/all");
};

/**
 * 获取字典类型详情
 */
export const getSysDictTypeById = id => {
  return http.get(`/admin/system/dict/type/${id}`);
};

/**
 * 新增字典类型
 */
export const addSysDictType = data => {
  return http.post("/admin/system/dict/type", data);
};

/**
 * 修改字典类型
 */
export const updateSysDictType = data => {
  return http.put("/admin/system/dict/type", data);
};

/**
 * 删除字典类型
 */
export const deleteSysDictType = ids => {
  return http.delete(`/admin/system/dict/type/${ids}`);
};

/**
 * 分页查询字典数据
 */
export const getSysDictDataList = params => {
  return http.get("/admin/system/dict/data/list", params);
};

/**
 * 根据字典类型获取字典数据
 */
export const getSysDictDataByType = dictType => {
  return http.get(`/admin/system/dict/data/type/${dictType}`);
};

/**
 * 获取字典数据详情
 */
export const getSysDictDataById = id => {
  return http.get(`/admin/system/dict/data/${id}`);
};

/**
 * 新增字典数据
 */
export const addSysDictData = data => {
  return http.post("/admin/system/dict/data", data);
};

/**
 * 修改字典数据
 */
export const updateSysDictData = data => {
  return http.put("/admin/system/dict/data", data);
};

/**
 * 删除字典数据
 */
export const deleteSysDictData = ids => {
  return http.delete(`/admin/system/dict/data/${ids}`);
};

// ========== 系统配置 ==========

/**
 * 分页查询系统配置
 */
export const getSysConfigList = params => {
  return http.get("/admin/system/config/list", params);
};

/**
 * 根据键名获取配置值
 */
export const getSysConfigByKey = configKey => {
  return http.get(`/admin/system/config/key/${configKey}`);
};

/**
 * 获取配置详情
 */
export const getSysConfigById = id => {
  return http.get(`/admin/system/config/${id}`);
};

/**
 * 新增配置
 */
export const addSysConfig = data => {
  return http.post("/admin/system/config", data);
};

/**
 * 修改配置
 */
export const updateSysConfig = data => {
  return http.put("/admin/system/config", data);
};

/**
 * 删除配置
 */
export const deleteSysConfig = ids => {
  return http.delete(`/admin/system/config/${ids}`);
};

// ========== 操作日志 ==========

/**
 * 分页查询操作日志
 */
export const getSysOperLogList = params => {
  return http.get("/admin/system/log/oper/list", params);
};

/**
 * 获取操作日志详情
 */
export const getSysOperLogById = id => {
  return http.get(`/admin/system/log/oper/${id}`);
};

/**
 * 删除操作日志
 */
export const deleteSysOperLog = ids => {
  return http.delete(`/admin/system/log/oper/${ids}`);
};

/**
 * 清空操作日志
 */
export const cleanSysOperLog = () => {
  return http.delete("/admin/system/log/oper/clean");
};

// ========== 登录日志 ==========

/**
 * 分页查询登录日志
 */
export const getSysLoginLogList = params => {
  return http.get("/admin/system/log/login/list", params);
};

/**
 * 删除登录日志
 */
export const deleteSysLoginLog = ids => {
  return http.delete(`/admin/system/log/login/${ids}`);
};

/**
 * 清空登录日志
 */
export const cleanSysLoginLog = () => {
  return http.delete("/admin/system/log/login/clean");
};
