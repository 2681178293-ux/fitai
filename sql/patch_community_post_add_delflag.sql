-- community_post 补充逻辑删除字段（与全局 delFlag 配置一致：0正常 2删除）
ALTER TABLE `community_post`
    ADD COLUMN `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）' AFTER `status`;

UPDATE `community_post` SET `del_flag` = '0' WHERE `del_flag` IS NULL OR `del_flag` = '';
