-- community_comment：is_top 改名为 top，并补充逻辑删除字段 del_flag
ALTER TABLE `community_comment`
    CHANGE COLUMN `is_top` `top` tinyint DEFAULT 0 COMMENT '是否置顶：0-否 1-是';

ALTER TABLE `community_comment`
    ADD COLUMN `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）' AFTER `top`;

UPDATE `community_comment` SET `del_flag` = '0' WHERE `del_flag` IS NULL OR `del_flag` = '';
