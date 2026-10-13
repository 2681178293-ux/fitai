-- 修复销售属性：prd_attr_value 缺少 del_flag（实体 PrdAttrValue.delFlag）
-- 库：fitai_all；可重复执行

SET @db := DATABASE();

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db
    AND TABLE_NAME = 'prd_attr_value'
    AND COLUMN_NAME = 'del_flag'
);

SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE `prd_attr_value` ADD COLUMN `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT ''0'' COMMENT ''删除标志（0正常 2删除）'' AFTER `status`',
  'SELECT ''prd_attr_value.del_flag already exists'' AS msg'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
