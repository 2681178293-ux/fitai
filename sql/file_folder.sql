-- ----------------------------
-- 图片库分类表（管理端 ImageLibrary 使用）
-- 执行前建议: mysql --default-character-set=utf8mb4 ...
-- ----------------------------
CREATE TABLE IF NOT EXISTS `file_folder` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `folder_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OSS目录前缀',
  `label` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '侧栏显示名称',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_file_folder_key` (`folder_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '图片库分类' ROW_FORMAT = DYNAMIC;

INSERT INTO `file_folder` (`folder_key`, `label`, `sort_order`, `del_flag`, `create_by`, `create_time`)
SELECT * FROM (
  SELECT 'banner' AS folder_key, '轮播图片' AS label, 1 AS sort_order, '0' AS del_flag, 'admin' AS create_by, NOW() AS create_time
  UNION ALL SELECT 'images', '默认图片', 2, '0', 'admin', NOW()
  UNION ALL SELECT 'course', '课程图片', 3, '0', 'admin', NOW()
  UNION ALL SELECT 'product', '商品图片', 4, '0', 'admin', NOW()
  UNION ALL SELECT 'seckill', '秒杀图片', 5, '0', 'admin', NOW()
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM `file_folder` LIMIT 1);
