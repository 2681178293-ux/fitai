-- 修复 ord_order 优惠券字段与实体 OrdOrder.couponUserId 不一致
-- 库：fitai_all；可重复执行
-- coupon_receive_id -> coupon_user_id

SET @db := DATABASE();

SET @has_old := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ord_order' AND COLUMN_NAME='coupon_receive_id'
);
SET @has_new := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ord_order' AND COLUMN_NAME='coupon_user_id'
);
SET @sql := IF(
  @has_old > 0 AND @has_new = 0,
  'ALTER TABLE `ord_order` CHANGE COLUMN `coupon_receive_id` `coupon_user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT ''使用的用户优惠券ID(取消/退款时退券)''',
  'SELECT ''ord_order.coupon_user_id ok'' AS msg'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
