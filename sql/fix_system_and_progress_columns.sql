-- 对齐实体字段：学习进度 / 角色 / 字典数据
-- 库：fitai_all

-- 1) crs_learn_progress.finish_flag → is_finished
SET @has_finish_flag := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crs_learn_progress' AND COLUMN_NAME = 'finish_flag'
);
SET @has_is_finished := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crs_learn_progress' AND COLUMN_NAME = 'is_finished'
);
SET @sql := IF(@has_finish_flag > 0 AND @has_is_finished = 0,
  'ALTER TABLE `crs_learn_progress` CHANGE COLUMN `finish_flag` `is_finished` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT ''0'' COMMENT ''完课（0否 1是）''',
  'SELECT ''skip crs_learn_progress''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) sys_role.order_num → role_sort
SET @has_role_order := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_role' AND COLUMN_NAME = 'order_num'
);
SET @has_role_sort := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_role' AND COLUMN_NAME = 'role_sort'
);
SET @sql := IF(@has_role_order > 0 AND @has_role_sort = 0,
  'ALTER TABLE `sys_role` CHANGE COLUMN `order_num` `role_sort` int NOT NULL DEFAULT 0 COMMENT ''显示顺序''',
  'SELECT ''skip sys_role''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) sys_dict_data.order_num → dict_sort
SET @has_dict_order := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dict_data' AND COLUMN_NAME = 'order_num'
);
SET @has_dict_sort := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dict_data' AND COLUMN_NAME = 'dict_sort'
);
SET @sql := IF(@has_dict_order > 0 AND @has_dict_sort = 0,
  'ALTER TABLE `sys_dict_data` CHANGE COLUMN `order_num` `dict_sort` int NOT NULL DEFAULT 0 COMMENT ''排序''',
  'SELECT ''skip sys_dict_data''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4) 日志菜单权限标识与后端 @SaCheckPermission 对齐
UPDATE `sys_menu` SET `perms` = 'system:login-log:list' WHERE `menu_id` = 1061;
UPDATE `sys_menu` SET `perms` = 'system:login-log:query' WHERE `menu_id` = 106101;
UPDATE `sys_menu` SET `perms` = 'system:login-log:delete' WHERE `menu_id` = 106102;
UPDATE `sys_menu` SET `perms` = 'system:oper-log:list' WHERE `menu_id` = 1062;
UPDATE `sys_menu` SET `perms` = 'system:oper-log:query' WHERE `menu_id` = 106201;
UPDATE `sys_menu` SET `perms` = 'system:oper-log:delete' WHERE `menu_id` = 106202;
