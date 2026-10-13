-- 将 crs_course 表结构对齐后端实体 CrsCourse（is_free / video_id / calories / equipment）
-- 适用于已导入旧版 fitai_all.sql（含 free_flag / vod_*）的本地库
-- 库：fitai_all

-- 1) free_flag → is_free
SET @has_free_flag := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crs_course' AND COLUMN_NAME = 'free_flag'
);
SET @has_is_free := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crs_course' AND COLUMN_NAME = 'is_free'
);
SET @sql := IF(@has_free_flag > 0 AND @has_is_free = 0,
  'ALTER TABLE `crs_course` CHANGE COLUMN `free_flag` `is_free` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT ''0'' COMMENT ''是否免费（0否 1是）''',
  'SELECT ''skip rename free_flag''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) 去掉旧 VOD 冗余列（实体已改用 video_id）
SET @has_vod_asset := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crs_course' AND COLUMN_NAME = 'vod_asset_id'
);
SET @sql := IF(@has_vod_asset > 0,
  'ALTER TABLE `crs_course` DROP INDEX `idx_crs_course_vod`, DROP COLUMN `vod_asset_id`, DROP COLUMN `vod_video_id`, DROP COLUMN `vod_cover_url`',
  'SELECT ''skip drop vod_*''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) 补齐前端/实体字段
SET @has_video_id := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crs_course' AND COLUMN_NAME = 'video_id'
);
SET @sql := IF(@has_video_id = 0,
  'ALTER TABLE `crs_course` ADD COLUMN `video_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT ''关联视频ID(media_vod)'' AFTER `summary`',
  'SELECT ''skip add video_id''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_calories := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crs_course' AND COLUMN_NAME = 'calories'
);
SET @sql := IF(@has_calories = 0,
  'ALTER TABLE `crs_course` ADD COLUMN `calories` int UNSIGNED NULL DEFAULT 0 COMMENT ''消耗卡路里'' AFTER `video_id`',
  'SELECT ''skip add calories''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_equipment := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crs_course' AND COLUMN_NAME = 'equipment'
);
SET @sql := IF(@has_equipment = 0,
  'ALTER TABLE `crs_course` ADD COLUMN `equipment` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT ''所需器材'' AFTER `calories`',
  'SELECT ''skip add equipment''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4) 补齐演示数据的卡路里/器材（仅更新仍为空的种子行）
UPDATE `crs_course` SET `calories` = 300 WHERE `course_id` = 1 AND (`calories` IS NULL OR `calories` = 0);
UPDATE `crs_course` SET `calories` = 400, `equipment` = '哑铃' WHERE `course_id` = 2 AND (`calories` IS NULL OR `calories` = 0);
UPDATE `crs_course` SET `calories` = 150, `equipment` = '瑜伽垫' WHERE `course_id` = 3 AND (`calories` IS NULL OR `calories` = 0);
