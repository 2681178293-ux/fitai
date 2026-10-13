-- 将 community_post / community_comment 对齐后端实体 CommunityPost / CommunityComment
-- 旧结构：post_id/customer_id/post_type/... ；实体需要：id/user_id/type/...
-- 库：fitai_all
-- 说明：演示数据直接重建；若有业务数据请先备份。

DROP TABLE IF EXISTS `community_audit`;
DROP TABLE IF EXISTS `community_comment`;
DROP TABLE IF EXISTS `community_post`;

CREATE TABLE `community_post` (
                                  `id` bigint NOT NULL COMMENT '动态ID',
                                  `user_id` bigint NOT NULL COMMENT '发布用户ID（usr_customer.customer_id）',
                                  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文字内容',
                                  `images` json DEFAULT NULL COMMENT '图片URL列表',
                                  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'normal' COMMENT '类型：normal/checkin/challenge',
                                  `training_plan_id` bigint DEFAULT NULL COMMENT '关联训练计划ID',
                                  `like_count` int DEFAULT 0 COMMENT '点赞数',
                                  `comment_count` int DEFAULT 0 COMMENT '评论数',
                                  `status` tinyint DEFAULT 1 COMMENT '状态：0-已删除 1-正常',
                                  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `idx_community_post_user_status`(`user_id` ASC, `status` ASC) USING BTREE,
                                  INDEX `idx_community_post_status_create`(`status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区动态表' ROW_FORMAT = DYNAMIC;

INSERT INTO `community_post` VALUES
                                 (1867000001, 1, '今天完成了第一天HIIT，累但爽！体重终于开始下降了 💪', '["https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400"]', 'checkin', NULL, 28, 2, 1, '0', '2025-05-20 21:30:00'),
                                 (1867000002, 3, '卧推100kg达成！感谢教练的周期化安排。', '["https://images.unsplash.com/photo-1581009146145-b5ef050c149a?w=400"]', 'normal', NULL, 86, 1, 1, '0', '2025-05-19 18:00:00'),
                                 (1867000003, 2, '晨间瑜伽第7天，肩颈放松很多～', NULL, 'checkin', NULL, 15, 1, 1, '0', '2025-05-18 07:15:00'),
                                 (1867000004, 5, '第一次跟练产后修复课，强度刚好。', NULL, 'normal', NULL, 8, 0, 1, '0', '2025-05-22 10:00:00');

CREATE TABLE `community_comment` (
                                     `id` bigint NOT NULL COMMENT '评论ID',
                                     `post_id` bigint NOT NULL COMMENT '动态ID',
                                     `user_id` bigint NOT NULL COMMENT '评论用户ID',
                                     `parent_id` bigint DEFAULT NULL COMMENT '父评论ID',
                                     `reply_user_id` bigint DEFAULT NULL COMMENT '回复目标用户ID',
                                     `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
                                     `like_count` int DEFAULT 0 COMMENT '点赞数',
                                     `status` tinyint DEFAULT 1 COMMENT '状态：0-已删除 1-正常',
                                     `top` tinyint DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
                                     `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `idx_community_comment_post_status`(`post_id` ASC, `status` ASC) USING BTREE,
                                     INDEX `idx_community_comment_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区评论表' ROW_FORMAT = DYNAMIC;

INSERT INTO `community_comment` VALUES
                                    (2020749366733623298, 1867000001, 2, NULL, NULL, '加油！前三天最难，坚持就是胜利！', 3, 1, 1, '0', '2025-05-20 22:00:00'),
                                    (2020749366733623299, 1867000001, 3, NULL, NULL, '第一天感觉怎么样？心率大概多少？', 1, 1, 0, '0', '2025-05-20 22:15:00'),
                                    (2020749366733623300, 1867000002, 1, NULL, NULL, '太强了！向大佬学习', 0, 1, 0, '0', '2025-05-19 19:00:00'),
                                    (2020749366733623301, 1867000003, 4, NULL, NULL, '我也该试试瑜伽了', 0, 1, 0, '0', '2025-05-18 08:00:00');

-- 审核表：若已是新结构则跳过重建，仅保证有演示数据
CREATE TABLE IF NOT EXISTS `community_moderation` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核ID',
                                        `target_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对象类型：post/comment',
                                        `target_id` bigint NOT NULL COMMENT '对象ID',
                                        `content_snapshot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容快照',
                                        `audit_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审 1-通过 2-拒绝',
                                        `audit_reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拒绝原因',
                                        `auditor_id` bigint DEFAULT NULL COMMENT '审核人ID',
                                        `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        INDEX `idx_community_moderation_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
                                        INDEX `idx_community_moderation_status`(`audit_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区内容审核表' ROW_FORMAT = DYNAMIC;

INSERT INTO `community_moderation` (`id`, `target_type`, `target_id`, `content_snapshot`, `audit_status`, `audit_reason`, `auditor_id`, `audit_time`, `create_time`)
VALUES
(1, 'post', 1867000001, '今天完成了第一天HIIT，累但爽！体重终于开始下降了 💪', 1, NULL, 1, '2025-05-20 22:00:00', '2025-05-20 21:35:00'),
(2, 'comment', 2020749366733623298, '加油！前三天最难，坚持就是胜利！', 1, NULL, 1, '2025-05-20 22:05:00', '2025-05-20 22:01:00'),
(3, 'post', 1867000004, '第一次跟练产后修复课，强度刚好。', 0, NULL, NULL, NULL, '2025-05-22 10:05:00')
ON DUPLICATE KEY UPDATE
  target_type=VALUES(target_type),
  target_id=VALUES(target_id),
  content_snapshot=VALUES(content_snapshot),
  audit_status=VALUES(audit_status);
