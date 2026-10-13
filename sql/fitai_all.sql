-- ==============================================
-- FitAI 全量建库 SQL（单体库 fitai_all）
-- 仅用于新建库：DROP + CREATE + 种子。不含存量 ALTER / 字典补插 / 菜单补丁。
-- 秒杀只用 seckill_*；付费会员用 vip_plan / vip_order；成长/积分用 mem_*
-- 列名准绳见 docs/NAMING.md「库表列名」
-- 更新时间: 2026-09-04
-- ==============================================
CREATE DATABASE IF NOT EXISTS `fitai_all` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `fitai_all`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============ 模块: fitai_ai ============
-- ----------------------------
-- Table structure for ai_agent
-- ----------------------------
DROP TABLE IF EXISTS `ai_agent`;
CREATE TABLE `ai_agent`  (
                             `agent_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'AgentID',
                             `agent_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
                             `agent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编码',
                             `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                             `model_id` bigint UNSIGNED NOT NULL COMMENT '主模型ID',
                             `prompt_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '提示词ID',
                             `flow_json` json NOT NULL COMMENT '编排流程JSON(nodes/edges)',
                             `tool_ids_json` json NULL COMMENT '绑定工具ID列表',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                             `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                             `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                             `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`agent_id`) USING BTREE,
                             UNIQUE INDEX `uk_ai_agent_code`(`agent_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Agent编排' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_agent
-- ----------------------------
INSERT INTO `ai_agent` VALUES (1, 'FitAI教练Agent', 'fit_coach_agent', '综合健身问答与方案生成', 1, 1, '{\"graph\": {\"edges\": [{\"to\": \"tool\", \"from\": \"analyze\", \"when\": \"branch:true\", \"keyword\": \"天气\"}, {\"to\": \"reply\", \"from\": \"analyze\", \"when\": \"branch:false\"}, {\"to\": \"reply\", \"from\": \"tool\"}, {\"to\": \"end\", \"from\": \"reply\"}], \"entry\": \"analyze\", \"nodes\": {\"end\": {\"id\": \"end\", \"type\": \"end\"}, \"tool\": {\"id\": \"tool\", \"type\": \"mcp\", \"toolCode\": \"mcp_weather\"}, \"reply\": {\"id\": \"reply\", \"type\": \"llm\"}, \"analyze\": {\"id\": \"analyze\", \"type\": \"llm\"}}}}', '[1, 2, 3]', '0', '0', '超级管理员', '2026-05-26 16:38:26', 'admin', '2026-07-07 00:03:14');

-- ----------------------------
-- Table structure for ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_message`;
CREATE TABLE `ai_chat_message`  (
                                    `message_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
                                    `session_id` bigint UNSIGNED NOT NULL COMMENT '会话ID',
                                    `conversation_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'conversationId(冗余)',
                                    `role` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色 user/assistant/system/tool',
                                    `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
                                    `prompt_tokens` int UNSIGNED NULL DEFAULT NULL COMMENT 'Prompt Token',
                                    `completion_tokens` int UNSIGNED NULL DEFAULT NULL COMMENT 'Completion Token',
                                    `total_tokens` int UNSIGNED NULL DEFAULT NULL COMMENT '总Token',
                                    `finish_reason` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '结束原因 stop/length/tool_calls',
                                    `tool_calls_json` json NULL COMMENT '工具调用',
                                    `rag_context_json` json NULL COMMENT 'RAG检索上下文',
                                    `model_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模型编码(冗余)',
                                    `latency_ms` int UNSIGNED NULL DEFAULT NULL COMMENT '耗时毫秒',
                                    `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '消息状态：0 成功 1 失败',
                                    `error_msg` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '失败详情（超时/限流/异常堆栈摘要）',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                    PRIMARY KEY (`message_id`) USING BTREE,
                                    INDEX `idx_ai_chat_message_session`(`session_id` ASC) USING BTREE,
                                    INDEX `idx_ai_chat_message_conversation`(`conversation_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI对话消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_chat_message
-- ----------------------------
INSERT INTO `ai_chat_message` VALUES (1, 1, 'a3f2c8e1-4b5d-4e6f-9a0b-1c2d3e4f5a6b', 'user', '我想减脂，身高175体重72.5，每周能练4次，怎么安排？', 68, 0, 68, NULL, NULL, NULL, NULL, NULL, '0', NULL, '2025-05-20 20:00:00');
INSERT INTO `ai_chat_message` VALUES (2, 1, 'a3f2c8e1-4b5d-4e6f-9a0b-1c2d3e4f5a6b', 'assistant', '根据您的体测数据（BMI 23.7），建议采用「3次力量 + 1次有氧」的Weekly Split：\n- 周一/三/五：全身力量（深蹲、卧推、划船各4×12）\n- 周六：30分钟HIIT或慢跑\n- 饮食控制在1800kcal左右，蛋白质≥120g/天\n如有膝关节不适请降低深蹲幅度。', 0, 186, 892, 'stop', NULL, NULL, 'qwen-plus', 1380, '0', NULL, '2026-07-07 10:00:00');
INSERT INTO `ai_chat_message` VALUES (3, 1, 'a3f2c8e1-4b5d-4e6f-9a0b-1c2d3e4f5a6b', 'user', '那饮食具体怎么吃？', 24, 0, 24, NULL, NULL, NULL, NULL, NULL, '0', NULL, '2025-05-20 20:00:00');
INSERT INTO `ai_chat_message` VALUES (4, 1, 'a3f2c8e1-4b5d-4e6f-9a0b-1c2d3e4f5a6b', 'assistant', '推荐一日三餐示例：\n早餐：2个水煮蛋 + 燕麦50g + 蓝莓\n午餐：鸡胸肉150g + 糙米饭150g + 西兰花\n晚餐：清蒸鱼120g + 蔬菜沙拉\n加餐（训练后）：希腊酸奶1杯\n可在App「饮食记录」中一键记录。', 0, 152, 940, 'stop', NULL, NULL, 'qwen-plus', 1120, '0', NULL, '2026-07-06 08:00:00');
INSERT INTO `ai_chat_message` VALUES (5, 2, 'b4e3d9f2-5c6e-4f7a-8b1c-2d3e4f5a6b7c', 'user', '增肌期用PPL分化合适吗？', 32, 0, 32, NULL, NULL, NULL, NULL, NULL, '0', NULL, '2025-05-18 19:00:00');
INSERT INTO `ai_chat_message` VALUES (6, 2, 'b4e3d9f2-5c6e-4f7a-8b1c-2d3e4f5a6b7c', 'assistant', 'PPL（Push/Pull/Legs）非常适合有一定训练基础的增肌期，建议每周6练1休或3天循环2周。注意每肌群每周14-20组有效容量即可。', 0, 98, 888, 'stop', NULL, NULL, 'qwen-plus', 980, '0', NULL, '2026-07-05 10:00:00');
INSERT INTO `ai_chat_message` VALUES (7, 13, 'c7ce0154-a02e-4366-a01e-d4960a1dd887', 'user', '你是', NULL, NULL, NULL, NULL, NULL, NULL, 'qwen-plus', NULL, '0', NULL, '2026-07-06 15:21:40');
INSERT INTO `ai_chat_message` VALUES (8, 13, 'c7ce0154-a02e-4366-a01e-d4960a1dd887', 'assistant', '我是FitAI，你的专业健身教练。我专注于提供科学、安全的健身与营养建议，包括训练动作推荐、饮食热量与营养成分查询等。如果你有任何关于增肌、减脂、塑形、运动康复或健康饮食的问题，欢迎随时告诉我！💪  \n（温馨提示：如涉及疾病、伤痛或特殊健康状况，请务必先咨询医生或专业医疗人员。）\n\n我不是医生，以下建议仅供参考，如有疾病请咨询专业医师。', 362, 83, 445, 'stop', NULL, NULL, 'qwen-plus', 3093, '0', NULL, '2026-07-06 15:21:40');
INSERT INTO `ai_chat_message` VALUES (9, 14, '6538239e-a744-40a3-8729-8b92f531b5d7', 'user', '你是', NULL, NULL, NULL, NULL, NULL, NULL, 'qwen-plus', NULL, '0', NULL, '2026-07-06 15:22:13');
INSERT INTO `ai_chat_message` VALUES (10, 14, '6538239e-a744-40a3-8729-8b92f531b5d7', 'assistant', '我是FitAI，你的专业健身教练。我可以帮你制定训练计划、推荐饮食方案、解答健身与营养相关问题。所有建议均基于科学依据，并严格遵守安全原则——如涉及疾病或健康风险，我会提醒你及时就医。\n\n请告诉我你的目标（如增肌、减脂、提升体能）、当前状况（如训练经验、时间安排、饮食偏好），或直接提问！💪\n\n我不是医生，以下建议仅供参考，如有疾病请咨询专业医师。', 362, 85, 447, 'stop', NULL, NULL, 'qwen-plus', 3325, '0', NULL, '2026-07-06 15:22:13');
INSERT INTO `ai_chat_message` VALUES (11, 15, 'e9532f31-1325-4a5a-8398-788ada15b032', 'user', '你好', NULL, NULL, NULL, NULL, NULL, NULL, 'qwen-plus', NULL, '0', NULL, '2026-07-06 17:26:37');
INSERT INTO `ai_chat_message` VALUES (12, 15, 'e9532f31-1325-4a5a-8398-788ada15b032', 'assistant', '你好！我是 FitAI 智能健身助手，可以帮你：\n- 查询针对特定肌群的训练动作（如胸、背、腿等）\n- 查询常见食物的热量及营养成分（如蛋白质、脂肪、碳水）\n\n请问你想了解哪方面的健身或营养信息？😊\n\n我不是医生，以下建议仅供参考，如有疾病请咨询专业医师。', 363, 62, 425, 'stop', NULL, NULL, 'qwen-plus', 4821, '0', NULL, '2026-07-06 17:26:37');

-- ----------------------------
-- Table structure for ai_chat_session
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_session`;
CREATE TABLE `ai_chat_session`  (
                                    `session_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '会话ID',
                                    `conversation_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Spring AI conversationId(UUID)',
                                    `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                    `model_id` bigint UNSIGNED NOT NULL COMMENT '模型ID',
                                    `model_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模型编码(冗余)',
                                    `session_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会话标题',
                                    `scene` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'chat' COMMENT '场景',
                                    `message_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '消息数(冗余)',
                                    `total_tokens` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '总Token(冗余)',
                                    `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
                                    `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0进行中 1已结束 2已归档）',
                                    `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                    `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`session_id`) USING BTREE,
                                    UNIQUE INDEX `uk_ai_chat_conversation`(`conversation_id` ASC) USING BTREE,
                                    INDEX `idx_ai_chat_session_customer`(`customer_id` ASC) USING BTREE,
                                    INDEX `idx_ai_chat_session_customer_status`(`customer_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI对话会话' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_chat_session
-- ----------------------------
INSERT INTO `ai_chat_session` VALUES (1, 'a3f2c8e1-4b5d-4e6f-9a0b-1c2d3e4f5a6b', 1, 1, 'qwen-plus', '如何科学减脂', 'chat', 4, 1856, '2025-05-20 20:08:00', '0', '0', '2025-05-20 20:00:00', '2025-05-20 20:08:00');
INSERT INTO `ai_chat_session` VALUES (2, 'b4e3d9f2-5c6e-4f7a-8b1c-2d3e4f5a6b7c', 3, 1, 'qwen-plus', '增肌训练分化', 'chat', 2, 920, '2025-05-18 19:10:00', '1', '0', '2025-05-18 19:00:00', '2025-05-18 19:10:00');
INSERT INTO `ai_chat_session` VALUES (3, 'd523c788-8e12-4114-8093-28c2ede091cb', 1, 1, 'qwen-plus', '练臀', 'train_plan', 0, 0, NULL, '0', '0', '2026-07-06 10:30:59', NULL);
INSERT INTO `ai_chat_session` VALUES (4, '4a0caa21-05c5-4e85-ab0a-601294e1f227', 1, 1, 'qwen-plus', '怎么吃', 'diet_plan', 0, 0, NULL, '0', '0', '2026-07-06 10:31:10', NULL);
INSERT INTO `ai_chat_session` VALUES (5, '4db97071-ef7e-4425-9a90-b6470ef5fe8a', 1, 1, 'qwen-plus', '你是', 'chat', 0, 0, NULL, '0', '0', '2026-07-06 13:33:11', NULL);
INSERT INTO `ai_chat_session` VALUES (6, '2b2be209-7abc-4da9-b24a-fd7ba261c476', 1, 1, 'qwen-plus', '你是', 'chat', 0, 0, NULL, '0', '0', '2026-07-06 13:42:04', NULL);
INSERT INTO `ai_chat_session` VALUES (7, '76984faa-4873-47fa-8aec-9f4b95548d1d', 1, 1, 'qwen-plus', '你是', 'chat', 0, 0, NULL, '0', '0', '2026-07-06 13:43:12', NULL);
INSERT INTO `ai_chat_session` VALUES (8, '8c2f2e35-b6e3-40e8-a1dc-d412c28dceb5', 1, 1, 'qwen-plus', '你是', 'chat', 0, 0, NULL, '0', '0', '2026-07-06 13:51:44', NULL);
INSERT INTO `ai_chat_session` VALUES (9, '8bd898bb-f056-4781-9dbb-afb67ea6a35b', 1, 1, 'qwen-plus', '111', 'chat', 0, 0, NULL, '0', '0', '2026-07-06 14:02:59', NULL);
INSERT INTO `ai_chat_session` VALUES (10, 'd9331aa7-2739-45a6-ac1e-8be9531a6422', 1, 1, 'qwen-plus', 'hello', 'chat', 0, 0, NULL, '0', '0', '2026-07-06 14:13:05', NULL);
INSERT INTO `ai_chat_session` VALUES (11, 'c29a56c5-b59e-41c3-a77a-6c5c1e1e3ae8', 1, 1, 'qwen-plus', '22', 'chat', 0, 0, NULL, '0', '0', '2026-07-06 14:20:22', NULL);
INSERT INTO `ai_chat_session` VALUES (12, 'f87816c4-f8fe-4275-bd1e-de3f92a45dc3', 1, 1, 'qwen-plus', '你好', 'chat', 0, 0, NULL, '0', '0', '2026-07-06 14:22:00', NULL);
INSERT INTO `ai_chat_session` VALUES (13, 'c7ce0154-a02e-4366-a01e-d4960a1dd887', 1, 1, 'qwen-plus', '你是', 'chat', 2, 445, '2026-07-06 15:21:40', '0', '0', '2026-07-06 15:21:37', '2026-07-06 15:21:40');
INSERT INTO `ai_chat_session` VALUES (14, '6538239e-a744-40a3-8729-8b92f531b5d7', 1, 1, 'qwen-plus', '你是', 'chat', 2, 447, '2026-07-06 15:22:13', '0', '0', '2026-07-06 15:22:10', '2026-07-06 15:22:13');
INSERT INTO `ai_chat_session` VALUES (15, 'e9532f31-1325-4a5a-8398-788ada15b032', 1, 1, 'qwen-plus', '你好', 'chat', 2, 425, '2026-07-06 17:26:37', '0', '0', '2026-07-06 17:26:32', '2026-07-06 17:26:37');
INSERT INTO `ai_chat_session` VALUES (16, '5aa2a872-a978-4294-beb0-d3c5f9a9e418', 1, 1, 'qwen-plus', 'chillo', 'chat', 0, 0, NULL, '0', '0', '2026-07-06 19:08:29', NULL);

-- ----------------------------
-- Table structure for ai_cost_log
-- ----------------------------
DROP TABLE IF EXISTS `ai_cost_log`;
CREATE TABLE `ai_cost_log`  (
                                `cost_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '成本ID',
                                `customer_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
                                `session_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '会话ID',
                                `model_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型',
                                `prompt_tokens` int UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Prompt Token',
                                `completion_tokens` int UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Completion Token',
                                `total_tokens` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '总Token',
                                `cost_amount` decimal(10, 6) NOT NULL DEFAULT 0.000000 COMMENT '费用元',
                                `biz_scene` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务场景',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                PRIMARY KEY (`cost_id`) USING BTREE,
                                INDEX `idx_ai_cost_log_time`(`create_time` ASC) USING BTREE,
                                INDEX `idx_ai_cost_log_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI成本流水' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_cost_log
-- ----------------------------
INSERT INTO `ai_cost_log` VALUES (1, 1, 1, 'qwen-plus', 45, 811, 856, 0.008560, 'chat', '2026-07-07 10:00:00');
INSERT INTO `ai_cost_log` VALUES (2, 1, 13, 'qwen-plus', 362, 83, 445, 0.000456, 'chat', '2026-07-06 12:00:00');
INSERT INTO `ai_cost_log` VALUES (3, 1, 14, 'qwen-plus', 362, 85, 447, 0.000460, 'chat', '2026-07-05 12:00:00');
INSERT INTO `ai_cost_log` VALUES (4, 1, 15, 'qwen-plus', 363, 62, 425, 0.000414, 'chat', '2026-07-04 12:00:00');
INSERT INTO `ai_cost_log` VALUES (5, 1, 1, 'qwen-plus', 45, 811, 856, 0.008560, 'chat', '2026-07-03 12:00:00');
INSERT INTO `ai_cost_log` VALUES (6, 2, 3, 'deepseek-chat', 300, 600, 900, 0.009000, 'chat', '2026-07-02 12:00:00');
INSERT INTO `ai_cost_log` VALUES (7, 1, 1, 'qwen-plus', 150, 300, 450, 0.013500, 'chat', '2026-07-01 12:00:00');

-- ----------------------------
-- Table structure for ai_dataset
-- ----------------------------
DROP TABLE IF EXISTS `ai_dataset`;
CREATE TABLE `ai_dataset`  (
                               `dataset_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '数据集ID',
                               `dataset_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
                               `dataset_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编码',
                               `dataset_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'eval' COMMENT '类型 eval=模型评测 rag=RAG质量评估',
                               `row_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '行数(冗余)',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                               `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                               `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`dataset_id`) USING BTREE,
                               UNIQUE INDEX `uk_ai_dataset_code`(`dataset_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI数据集' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_dataset
-- ----------------------------
INSERT INTO `ai_dataset` VALUES (1, '健身问答检索集', 'fitness_qa_rag', 'rag', 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `ai_dataset` VALUES (2, '健身问答评测集', 'fitness_qa_eval', 'eval', 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for ai_dataset_row
-- ----------------------------
DROP TABLE IF EXISTS `ai_dataset_row`;
CREATE TABLE `ai_dataset_row`  (
                                   `row_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '行ID',
                                   `dataset_id` bigint UNSIGNED NOT NULL COMMENT '数据集ID',
                                   `input_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '输入',
                                   `output_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '期望输出',
                                   `metadata_json` json NULL COMMENT '元数据',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   PRIMARY KEY (`row_id`) USING BTREE,
                                   INDEX `idx_ai_dataset_row_ds`(`dataset_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据集行' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_dataset_row
-- ----------------------------
INSERT INTO `ai_dataset_row` VALUES (1, 1, '每周练几次合适？', '建议每周3-4次训练。', '{\"scene\": \"chat\", \"difficulty\": \"easy\"}', '2025-05-20 10:00:00');
INSERT INTO `ai_dataset_row` VALUES (2, 1, '减脂期怎么吃？', '控制热量缺口，保证蛋白质摄入。', '{\"scene\": \"diet\", \"difficulty\": \"medium\"}', '2025-05-20 10:00:00');
INSERT INTO `ai_dataset_row` VALUES (3, 2, '每周练几次合适？', '建议每周3-4次训练。', '{\"scene\": \"chat\", \"difficulty\": \"easy\"}', '2025-05-20 10:00:00');
INSERT INTO `ai_dataset_row` VALUES (4, 2, '减脂期怎么吃？', '控制热量缺口，保证蛋白质摄入。', '{\"scene\": \"diet\", \"difficulty\": \"medium\"}', '2025-05-20 10:00:00');

-- ----------------------------
-- Table structure for ai_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `ai_evaluation`;
CREATE TABLE `ai_evaluation`  (
                                  `evaluation_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评测ID',
                                  `eval_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评测名称',
                                  `model_id` bigint UNSIGNED NOT NULL COMMENT '模型ID',
                                  `dataset_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '数据集ID',
                                  `metrics_json` json NULL COMMENT '指标结果JSON',
                                  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0进行中 1完成 2失败）',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`evaluation_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '模型评测' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_evaluation
-- ----------------------------
INSERT INTO `ai_evaluation` VALUES (2, 'qwen-plus', 1, 2, '{\"accuracy\": 0.875, \"rowCount\": 2, \"passCount\": 2, \"totalCost\": 0.0055, \"durationSec\": 38, \"totalTokens\": 4046, \"avgLatencyMs\": 17704, \"promptTokens\": 2138, \"completionTokens\": 1908}', '1', 'admin', '2026-07-06 17:37:27', 'admin', '2026-07-06 17:37:40');

-- ----------------------------
-- Table structure for ai_guardrail
-- ----------------------------
DROP TABLE IF EXISTS `ai_guardrail`;
CREATE TABLE `ai_guardrail`  (
                                 `guardrail_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '护栏ID',
                                 `rule_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则名称',
                                 `rule_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型 keyword/medical/disclaimer/model',
                                 `rule_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则内容/关键词',
                                 `action` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'block' COMMENT '动作 block/warn/replace',
                                 `priority` int NOT NULL DEFAULT 0 COMMENT '优先级',
                                 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                 `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`guardrail_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '内容护栏' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_guardrail
-- ----------------------------
INSERT INTO `ai_guardrail` VALUES (1, '医疗免责声明', 'medical', '我不是医生，以下建议仅供参考，如有疾病请咨询专业医师。', 'warn', 100, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `ai_guardrail` VALUES (2, '敏感词过滤', 'keyword', '赌博,代练,色情,毒品', 'block', 90, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-07-07 00:41:55');
INSERT INTO `ai_guardrail` VALUES (3, '输入辱骂拦截', 'keyword', '傻逼,废物,去死', 'block', 88, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `ai_guardrail` VALUES (4, '手机号正则脱敏', 'regex', '1[3-9]\\d{9}', 'replace', 82, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `ai_guardrail` VALUES (5, '仇恨言论语义', 'semantic', 'hate_speech>=0.80', 'block', 78, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `ai_guardrail` VALUES (6, '身份证 PII 脱敏', 'pii', 'id_card', 'replace', 92, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `ai_guardrail` VALUES (7, '邮箱 PII 脱敏', 'pii', 'email', 'replace', 86, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `ai_guardrail` VALUES (8, '极限词替换', 'keyword', '保证治愈,100%有效,绝对安全', 'replace', 75, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for ai_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge`;
CREATE TABLE `ai_knowledge`  (
                                 `knowledge_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '知识库ID',
                                 `kb_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识库名称',
                                 `kb_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编码',
                                 `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                 `embedding_model_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '嵌入模型ID',
                                 `embedding_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'text-embedding-v3' COMMENT 'DashScope嵌入模型',
                                 `vector_dims` smallint UNSIGNED NOT NULL DEFAULT 1024 COMMENT '向量维度',
                                 `vector_engine` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'milvus' COMMENT '向量引擎 milvus/redis',
                                 `milvus_collection` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Milvus collection 名',
                                 `redis_index_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Redis索引名 idx:fitai:kb:{code}',
                                 `distance_metric` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'COSINE' COMMENT '距离度量 COSINE/L2/IP',
                                 `doc_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '文档数(冗余)',
                                 `chunk_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '分片数(冗余)',
                                 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                 `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`knowledge_id`) USING BTREE,
                                 UNIQUE INDEX `uk_ai_knowledge_code`(`kb_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_knowledge
-- ----------------------------
INSERT INTO `ai_knowledge` VALUES (1, '健身知识库', 'fitness_kb', '训练动作、营养、康复知识', 2, 'text-embedding-v3', 1024, 'milvus', 'fitai_kb_fitness_kb', 'idx:fitai:kb:fitness_kb', 'COSINE', 1, 2, '0', '2', '超级管理员', '2026-05-26 16:38:26', 'admin', '2026-07-06 16:23:51');
INSERT INTO `ai_knowledge` VALUES (2, '减服知识库', '减服知识库', '', NULL, 'qwen-embedding-v3', 1024, 'milvus', NULL, 'idx:kb_2', 'COSINE', 3, 6, '0', '0', 'admin', '2026-07-06 13:37:50', 'admin', '2026-07-06 16:57:16');
INSERT INTO `ai_knowledge` VALUES (3, '增肌知识库', '增肌知识库', '增肌知识库', NULL, 'qwen-embedding-v3', 1024, 'milvus', NULL, 'idx:增肌知识库', 'COSINE', 0, 0, '0', '0', 'admin', '2026-07-06 17:29:19', NULL, '2026-07-06 17:30:46');

-- ----------------------------
-- Table structure for ai_knowledge_chunk
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge_chunk`;
CREATE TABLE `ai_knowledge_chunk`  (
                                       `chunk_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分片ID',
                                       `knowledge_id` bigint UNSIGNED NOT NULL COMMENT '知识库ID',
                                       `doc_id` bigint UNSIGNED NOT NULL COMMENT '文档ID',
                                       `chunk_index` int UNSIGNED NOT NULL COMMENT '分片序号',
                                       `chunk_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分片文本',
                                       `content_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容SHA256',
                                       `token_count` int UNSIGNED NULL DEFAULT NULL COMMENT 'Token数',
                                       `redis_doc_key` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Redis文档键 fitai:vec:{kb}:{doc}:{idx}',
                                       `redis_index_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Redis索引名(冗余)',
                                       `vector_dims` smallint UNSIGNED NOT NULL DEFAULT 1024 COMMENT '向量维度',
                                       `distance_metric` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'COSINE' COMMENT '距离度量',
                                       `embedding_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'text-embedding-v3' COMMENT '嵌入模型',
                                       `vector_engine` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'milvus' COMMENT '向量引擎',
                                       `milvus_vector_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Milvus 向量主键',
                                       `metadata_json` json NULL COMMENT 'Redis TAG/Numeric过滤元数据',
                                       `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '索引（0待写入 1已写入 2失败）',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                       `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`chunk_id`) USING BTREE,
                                       UNIQUE INDEX `uk_ai_chunk_redis_key`(`redis_doc_key` ASC) USING BTREE,
                                       INDEX `idx_ai_chunk_doc`(`doc_id` ASC) USING BTREE,
                                       INDEX `idx_ai_chunk_kb`(`knowledge_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识库分片(Redis向量元数据)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_knowledge_chunk
-- ----------------------------
INSERT INTO `ai_knowledge_chunk` VALUES (16, 2, 6, 1, '每天制造 300–500 大卡温和热量缺口，不极端节食 每餐固定配比：1 掌蛋白 + 2 拳蔬菜 + 1 拳主食 主食一半换成粗粮：玉米、红薯、燕麦、糙米、杂粮饭 晚餐主食减量至半拳，尽量不吃精制米面 保证每日蛋白质摄入，每餐必有肉 / 蛋 / 豆制品 烹饪用喷油壶，少油少盐，拒绝红烧、油炸、勾芡 戒掉奶茶、果汁、可乐等所有含糖饮料 每日饮水量 1.', '4389d7bab04a0a4bd9a49f70b31ec1bf1dc111dd7d55f1cd951a7c678c2ce3b2', NULL, 'fitai:vec:减服知识库:6:1', 'idx:kb_2', 1024, 'COSINE', 'qwen-embedding-v3', 'milvus', NULL, NULL, '1', '2026-07-06 16:45:33', '2026-07-06 16:45:33');
INSERT INTO `ai_knowledge_chunk` VALUES (17, 2, 6, 2, '5–2L，饭前半小时喝一杯温水 吃饭顺序：蔬菜→蛋白质→主食，平稳血糖更抗饿 细嚼慢咽，每口咀嚼 20 次，一餐用时不少于 15 分钟 吃到七分饱就停，不撑肚子，不光盘硬塞剩饭 杜绝宵夜，睡前 3 小时不再进食任何东西 零食替换：薯片饼干换成黄瓜、小番茄、无糖酸奶 外食优先清蒸、白灼、清炒，避开干锅、火锅重油汤底 水果放在白天吃，每日总量一个拳头大小，不吃榴莲荔枝等高糖果 不喝蜂蜜水、红糖水，避开隐形添加糖 腌制咸菜、腊肉少吃，高盐容易水肿显胖 自己带饭做饭，可控油盐，减少外卖频次 少吃面条、馄饨、包子这类高碳水快餐 每周可安排 1 次轻欺骗餐，避免长期压抑暴饮暴食', '5c81ac78a3087bee5ffb3703efd08629fb3056b807b4b4881247c258759684de', NULL, 'fitai:vec:减服知识库:6:2', 'idx:kb_2', 1024, 'COSINE', 'qwen-embedding-v3', 'milvus', NULL, NULL, '1', '2026-07-06 16:45:33', '2026-07-06 16:45:33');
INSERT INTO `ai_knowledge_chunk` VALUES (18, 2, 7, 1, '每周安排 4–6 次有氧运动，单次时长 30–45 分钟新手从快走起步，不伤膝盖，容易长期坚持可选有氧：慢跑、跳绳、骑行、爬楼梯、椭圆机、帕梅拉燃脂操每周安排 2–3 次力量训练，保住肌肉、提高基础代谢居家徒手力量：深蹲、俯卧撑、平板支撑、臀桥、箭步蹲力量训练先练大肌群（腿、臀、背），燃脂效率更高碎片化动起来：每坐一小时起身走动 3–5 分钟上下班短途选择步行或骑车，代替坐车能走楼梯尽量不坐电梯，累积日常消耗运动前后各拉伸 5–10 分钟，防止肌肉结块、酸痛不要空腹高强度运动，避免低血糖头晕乏力运动后半小时补充少量蛋白质，不要暴饮暴食制定固定运动时段，形成习惯，避免三天打鱼两天晒网循序渐进加运动量，不要一开始猛练导致受伤放弃搭配间歇训练（快慢交替），减脂效率高于匀速慢跑日常多站立，减少久坐瘫沙', '68d47b4aee5cc988908a950f9429153114657b7037d287b2dae200121b352480', NULL, 'fitai:vec:减服知识库:7:1', 'idx:kb_2', 1024, 'COSINE', 'qwen-embedding-v3', 'milvus', NULL, NULL, '1', '2026-07-06 16:46:02', '2026-07-06 16:46:02');
INSERT INTO `ai_knowledge_chunk` VALUES (19, 2, 7, 2, '发玩手机睡前简单拉伸，避免水肿、改善体态记录每次运动时长，直观看到进步，提升动力多人结伴运动，互相监督，更容易坚持下去经期降低运动强度，以慢走、轻柔拉伸为主，不剧烈蹦跳', 'e85d8a34444bd1930116d9db3823a7ee1754fb8ec87a294197295ccaf6a087d2', NULL, 'fitai:vec:减服知识库:7:2', 'idx:kb_2', 1024, 'COSINE', 'qwen-embedding-v3', 'milvus', NULL, NULL, '1', '2026-07-06 16:46:02', '2026-07-06 16:46:03');
INSERT INTO `ai_knowledge_chunk` VALUES (22, 2, 8, 1, '每天保证 7–8 小时睡眠，熬夜会升高发胖激素 固定作息，尽量 23 点前入睡，不睡懒觉 熬夜后不要靠暴饮暴食弥补情绪 晨起喝一杯温水，促进肠道代谢排便 养成晨起排便习惯，减少腹部堆积胀气 减少长期焦虑压力，压力大容易囤积腹部脂肪 每天称重固定时间：晨起空腹上完厕所称，数据更准确 不要一天频繁多次称重，容易心态焦虑内耗 每周拍一次体态对比照，比体重数字更能体现减脂效果 少喝酒，酒精热量高且阻碍脂肪分解 戒烟，吸烟影响代谢、容易腹部肥胖 减少长时间刷短视频，避免无意识吃东西 三餐定时定点，不要饥一顿饱一顿乱吃饭 睡前一小时远离手机，提升睡眠质量 避免长期跷二郎腿，防止腿型浮肿、骨盆歪斜 坐姿端正，不弯腰塌肚，改善小腹突出问题 每天简单泡脚 15 分钟，促进循环消下肢水肿 情绪想吃东西时，先�', '994c31826e4f7b8c698f5ac23d79b4963f876f0ceb2d75b87b2665704a673fa3', NULL, 'fitai:vec:减服知识库:8:1', 'idx:kb_2', 1024, 'COSINE', 'qwen-embedding-v3', 'milvus', NULL, NULL, '1', '2026-07-06 16:57:36', '2026-07-06 16:57:36');
INSERT INTO `ai_knowledge_chunk` VALUES (23, 2, 8, 2, '�水等 10 分钟，区分真饿还是嘴馋 每月复盘减脂进度，适时调整饮食运动方案 接纳缓慢减脂节奏，每月减重 2–4 斤最健康不易反弹', 'bc9e05f6c4bc97bbd1404aefb1b6a749e2f91c3e116028e09f8f2eb255474574', NULL, 'fitai:vec:减服知识库:8:2', 'idx:kb_2', 1024, 'COSINE', 'qwen-embedding-v3', 'milvus', NULL, NULL, '1', '2026-07-06 16:57:36', '2026-07-06 16:57:36');

-- ----------------------------
-- Table structure for ai_knowledge_doc
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge_doc`;
CREATE TABLE `ai_knowledge_doc`  (
                                     `doc_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文档ID',
                                     `knowledge_id` bigint UNSIGNED NOT NULL COMMENT '知识库ID',
                                     `doc_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档标题',
                                     `doc_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'text' COMMENT '类型 text/pdf/md/url',
                                     `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类 exercise/nutrition/injury/equipment',
                                     `tags` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签(逗号分隔)',
                                     `source` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源',
                                     `source_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源URL',
                                     `content_text` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '原文',
                                     `content_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '全文SHA256',
                                     `chunk_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '分片数(冗余)',
                                     `index_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '索引（0待处理 1已完成 2失败）',
                                     `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                     `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                     `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                     `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                     `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`doc_id`) USING BTREE,
                                     INDEX `idx_ai_knowledge_doc_kb`(`knowledge_id` ASC) USING BTREE,
                                     INDEX `idx_ai_knowledge_doc_category`(`category` ASC) USING BTREE,
                                     INDEX `idx_ai_knowledge_doc_kb_title`(`knowledge_id` ASC, `doc_title` ASC) USING BTREE,
                                     INDEX `idx_ai_knowledge_doc_kb_hash`(`knowledge_id` ASC, `content_hash` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识库文档' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_knowledge_doc
-- ----------------------------
INSERT INTO `ai_knowledge_doc` VALUES (1, 1, '深蹲动作指南', 'text', 'exercise', '深蹲,膝关节', 'FitAI内测讲义', NULL, '深蹲是复合动作，主要锻炼股四头肌和臀大肌。注意膝盖与脚尖方向一致。', '9d8588735c145d5296f288704e4bd125d7d1bdce666c1599f85a1f1854fc93ed', 1, '1', '0', '2', '超级管理员', '2025-05-01 09:00:00', '', '2026-07-06 16:53:21');
INSERT INTO `ai_knowledge_doc` VALUES (2, 2, '减肥 - 作息', 'md', NULL, NULL, 'upload', NULL, '每天保证 7–8 小时睡眠，熬夜会升高发胖激素 固定作息，尽量 23 点前入睡，不睡懒觉 熬夜后不要靠暴饮暴食弥补情绪 晨起喝一杯温水，促进肠道代谢排便 养成晨起排便习惯，减少腹部堆积胀气 减少长期焦虑压力，压力大容易囤积腹部脂肪 每天称重固定时间：晨起空腹上完厕所称，数据更准确 不要一天频繁多次称重，容易心态焦虑内耗 每周拍一次体态对比照，比体重数字更能体现减脂效果 少喝酒，酒精热量高且阻碍脂肪分解 戒烟，吸烟影响代谢、容易腹部肥胖 减少长时间刷短视频，避免无意识吃东西 三餐定时定点，不要饥一顿饱一顿乱吃饭 睡前一小时远离手机，提升睡眠质量 避免长期跷二郎腿，防止腿型浮肿、骨盆歪斜 坐姿端正，不弯腰塌肚，改善小腹突出问题 每天简单泡脚 15 分钟，促进循环消下肢水肿 情绪想吃东西时，先喝水等 10 分钟，区分真饿还是嘴馋 每月复盘减脂进度，适时调整饮食运动方案 接纳缓慢减脂节奏，每月减重 2–4 斤最健康不易反弹', '6bb290ba021f4b614515c555acfa94cb26669db9db8af0dfb921f9bae4e8eea2', 2, '1', '0', '2', 'admin', '2026-07-06 13:38:46', 'admin', '2026-07-06 16:53:21');
INSERT INTO `ai_knowledge_doc` VALUES (3, 2, '减肥-饮食', 'md', NULL, NULL, 'upload', NULL, '每天制造 300–500 大卡温和热量缺口，不极端节食 每餐固定配比：1 掌蛋白 + 2 拳蔬菜 + 1 拳主食 主食一半换成粗粮：玉米、红薯、燕麦、糙米、杂粮饭 晚餐主食减量至半拳，尽量不吃精制米面 保证每日蛋白质摄入，每餐必有肉 / 蛋 / 豆制品 烹饪用喷油壶，少油少盐，拒绝红烧、油炸、勾芡 戒掉奶茶、果汁、可乐等所有含糖饮料 每日饮水量 1.5–2L，饭前半小时喝一杯温水 吃饭顺序：蔬菜→蛋白质→主食，平稳血糖更抗饿 细嚼慢咽，每口咀嚼 20 次，一餐用时不少于 15 分钟 吃到七分饱就停，不撑肚子，不光盘硬塞剩饭 杜绝宵夜，睡前 3 小时不再进食任何东西 零食替换：薯片饼干换成黄瓜、小番茄、无糖酸奶 外食优先清蒸、白灼、清炒，避开干锅、火锅重油汤底 水果放在白天吃，每日总量一个拳头大小，不吃榴莲荔枝等高糖果 不喝蜂蜜水、红糖水，避开隐形添加糖 腌制咸菜、腊肉少吃，高盐容易水肿显胖 自己带饭做饭，可控油盐，减少外卖频次 少吃面条、馄饨、包子这类高碳水快餐 每周可安排 1 次轻欺骗餐，避免长期压抑暴饮暴食', 'a8618e78895e9cfa1004b4890d42385aecff4d4180fb849b2005fad5aada35ea', 2, '1', '0', '2', 'admin', '2026-07-06 13:40:05', 'admin', '2026-07-06 16:53:21');
INSERT INTO `ai_knowledge_doc` VALUES (4, 2, '减肥-运动', 'md', NULL, NULL, 'upload', NULL, '每周安排 4–6 次有氧运动，单次时长 30–45 分钟新手从快走起步，不伤膝盖，容易长期坚持可选有氧：慢跑、跳绳、骑行、爬楼梯、椭圆机、帕梅拉燃脂操每周安排 2–3 次力量训练，保住肌肉、提高基础代谢居家徒手力量：深蹲、俯卧撑、平板支撑、臀桥、箭步蹲力量训练先练大肌群（腿、臀、背），燃脂效率更高碎片化动起来：每坐一小时起身走动 3–5 分钟上下班短途选择步行或骑车，代替坐车能走楼梯尽量不坐电梯，累积日常消耗运动前后各拉伸 5–10 分钟，防止肌肉结块、酸痛不要空腹高强度运动，避免低血糖头晕乏力运动后半小时补充少量蛋白质，不要暴饮暴食制定固定运动时段，形成习惯，避免三天打鱼两天晒网循序渐进加运动量，不要一开始猛练导致受伤放弃搭配间歇训练（快慢交替），减脂效率高于匀速慢跑日常多站立，减少久坐瘫沙发玩手机睡前简单拉伸，避免水肿、改善体态记录每次运动时长，直观看到进步，提升动力多人结伴运动，互相监督，更容易坚持下去经期降低运动强度，以慢走、轻柔拉伸为主，不剧烈蹦跳', '19920c4ecf175153a0071be865e9fd942f1227e94981b56d620832e89d10b32e', 2, '1', '0', '2', 'admin', '2026-07-06 13:41:01', 'admin', '2026-07-06 16:53:21');
INSERT INTO `ai_knowledge_doc` VALUES (5, 2, '减肥 - 作息', 'md', NULL, NULL, 'upload', NULL, '每天保证 7–8 小时睡眠，熬夜会升高发胖激素 固定作息，尽量 23 点前入睡，不睡懒觉 熬夜后不要靠暴饮暴食弥补情绪 晨起喝一杯温水，促进肠道代谢排便 养成晨起排便习惯，减少腹部堆积胀气 减少长期焦虑压力，压力大容易囤积腹部脂肪 每天称重固定时间：晨起空腹上完厕所称，数据更准确 不要一天频繁多次称重，容易心态焦虑内耗 每周拍一次体态对比照，比体重数字更能体现减脂效果 少喝酒，酒精热量高且阻碍脂肪分解 戒烟，吸烟影响代谢、容易腹部肥胖 减少长时间刷短视频，避免无意识吃东西 三餐定时定点，不要饥一顿饱一顿乱吃饭 睡前一小时远离手机，提升睡眠质量 避免长期跷二郎腿，防止腿型浮肿、骨盆歪斜 坐姿端正，不弯腰塌肚，改善小腹突出问题 每天简单泡脚 15 分钟，促进循环消下肢水肿 情绪想吃东西时，先喝水等 10 分钟，区分真饿还是嘴馋 每月复盘减脂进度，适时调整饮食运动方案 接纳缓慢减脂节奏，每月减重 2–4 斤最健康不易反弹', '6bb290ba021f4b614515c555acfa94cb26669db9db8af0dfb921f9bae4e8eea2', 2, '1', '0', '2', 'admin', '2026-07-06 16:35:00', 'admin', '2026-07-06 16:57:16');
INSERT INTO `ai_knowledge_doc` VALUES (6, 2, '减肥-饮食', 'md', NULL, NULL, 'upload', NULL, '每天制造 300–500 大卡温和热量缺口，不极端节食 每餐固定配比：1 掌蛋白 + 2 拳蔬菜 + 1 拳主食 主食一半换成粗粮：玉米、红薯、燕麦、糙米、杂粮饭 晚餐主食减量至半拳，尽量不吃精制米面 保证每日蛋白质摄入，每餐必有肉 / 蛋 / 豆制品 烹饪用喷油壶，少油少盐，拒绝红烧、油炸、勾芡 戒掉奶茶、果汁、可乐等所有含糖饮料 每日饮水量 1.5–2L，饭前半小时喝一杯温水 吃饭顺序：蔬菜→蛋白质→主食，平稳血糖更抗饿 细嚼慢咽，每口咀嚼 20 次，一餐用时不少于 15 分钟 吃到七分饱就停，不撑肚子，不光盘硬塞剩饭 杜绝宵夜，睡前 3 小时不再进食任何东西 零食替换：薯片饼干换成黄瓜、小番茄、无糖酸奶 外食优先清蒸、白灼、清炒，避开干锅、火锅重油汤底 水果放在白天吃，每日总量一个拳头大小，不吃榴莲荔枝等高糖果 不喝蜂蜜水、红糖水，避开隐形添加糖 腌制咸菜、腊肉少吃，高盐容易水肿显胖 自己带饭做饭，可控油盐，减少外卖频次 少吃面条、馄饨、包子这类高碳水快餐 每周可安排 1 次轻欺骗餐，避免长期压抑暴饮暴食', 'a8618e78895e9cfa1004b4890d42385aecff4d4180fb849b2005fad5aada35ea', 2, '1', '0', '0', 'admin', '2026-07-06 16:45:31', 'admin', '2026-07-06 16:53:21');
INSERT INTO `ai_knowledge_doc` VALUES (7, 2, '减肥-运动', 'md', NULL, NULL, 'upload', NULL, '每周安排 4–6 次有氧运动，单次时长 30–45 分钟新手从快走起步，不伤膝盖，容易长期坚持可选有氧：慢跑、跳绳、骑行、爬楼梯、椭圆机、帕梅拉燃脂操每周安排 2–3 次力量训练，保住肌肉、提高基础代谢居家徒手力量：深蹲、俯卧撑、平板支撑、臀桥、箭步蹲力量训练先练大肌群（腿、臀、背），燃脂效率更高碎片化动起来：每坐一小时起身走动 3–5 分钟上下班短途选择步行或骑车，代替坐车能走楼梯尽量不坐电梯，累积日常消耗运动前后各拉伸 5–10 分钟，防止肌肉结块、酸痛不要空腹高强度运动，避免低血糖头晕乏力运动后半小时补充少量蛋白质，不要暴饮暴食制定固定运动时段，形成习惯，避免三天打鱼两天晒网循序渐进加运动量，不要一开始猛练导致受伤放弃搭配间歇训练（快慢交替），减脂效率高于匀速慢跑日常多站立，减少久坐瘫沙发玩手机睡前简单拉伸，避免水肿、改善体态记录每次运动时长，直观看到进步，提升动力多人结伴运动，互相监督，更容易坚持下去经期降低运动强度，以慢走、轻柔拉伸为主，不剧烈蹦跳', '19920c4ecf175153a0071be865e9fd942f1227e94981b56d620832e89d10b32e', 2, '1', '0', '0', 'admin', '2026-07-06 16:46:02', 'admin', '2026-07-06 16:53:21');
INSERT INTO `ai_knowledge_doc` VALUES (8, 2, '减肥 - 作息', 'md', NULL, NULL, 'upload', NULL, '每天保证 7–8 小时睡眠，熬夜会升高发胖激素 固定作息，尽量 23 点前入睡，不睡懒觉 熬夜后不要靠暴饮暴食弥补情绪 晨起喝一杯温水，促进肠道代谢排便 养成晨起排便习惯，减少腹部堆积胀气 减少长期焦虑压力，压力大容易囤积腹部脂肪 每天称重固定时间：晨起空腹上完厕所称，数据更准确 不要一天频繁多次称重，容易心态焦虑内耗 每周拍一次体态对比照，比体重数字更能体现减脂效果 少喝酒，酒精热量高且阻碍脂肪分解 戒烟，吸烟影响代谢、容易腹部肥胖 减少长时间刷短视频，避免无意识吃东西 三餐定时定点，不要饥一顿饱一顿乱吃饭 睡前一小时远离手机，提升睡眠质量 避免长期跷二郎腿，防止腿型浮肿、骨盆歪斜 坐姿端正，不弯腰塌肚，改善小腹突出问题 每天简单泡脚 15 分钟，促进循环消下肢水肿 情绪想吃东西时，先喝水等 10 分钟，区分真饿还是嘴馋 每月复盘减脂进度，适时调整饮食运动方案 接纳缓慢减脂节奏，每月减重 2–4 斤最健康不易反弹', '6bb290ba021f4b614515c555acfa94cb26669db9db8af0dfb921f9bae4e8eea2', 2, '1', '0', '0', 'admin', '2026-07-06 16:46:42', 'admin', '2026-07-06 16:53:21');
INSERT INTO `ai_knowledge_doc` VALUES (9, 3, '减肥 - 作息', 'md', NULL, NULL, 'upload', NULL, '每天保证 7–8 小时睡眠，熬夜会升高发胖激素 固定作息，尽量 23 点前入睡，不睡懒觉 熬夜后不要靠暴饮暴食弥补情绪 晨起喝一杯温水，促进肠道代谢排便 养成晨起排便习惯，减少腹部堆积胀气 减少长期焦虑压力，压力大容易囤积腹部脂肪 每天称重固定时间：晨起空腹上完厕所称，数据更准确 不要一天频繁多次称重，容易心态焦虑内耗 每周拍一次体态对比照，比体重数字更能体现减脂效果 少喝酒，酒精热量高且阻碍脂肪分解 戒烟，吸烟影响代谢、容易腹部肥胖 减少长时间刷短视频，避免无意识吃东西 三餐定时定点，不要饥一顿饱一顿乱吃饭 睡前一小时远离手机，提升睡眠质量 避免长期跷二郎腿，防止腿型浮肿、骨盆歪斜 坐姿端正，不弯腰塌肚，改善小腹突出问题 每天简单泡脚 15 分钟，促进循环消下肢水肿 情绪想吃东西时，先喝水等 10 分钟，区分真饿还是嘴馋 每月复盘减脂进度，适时调整饮食运动方案 接纳缓慢减脂节奏，每月减重 2–4 斤最健康不易反弹', '6bb290ba021f4b614515c555acfa94cb26669db9db8af0dfb921f9bae4e8eea2', 2, '1', '0', '2', 'admin', '2026-07-06 17:29:46', 'admin', '2026-07-06 17:30:46');
INSERT INTO `ai_knowledge_doc` VALUES (10, 3, '减肥 - 作息2', 'md', NULL, NULL, 'upload', NULL, '每天制造 300–500 大卡温和热量缺口，不极端节食 每餐固定配比：1 掌蛋白 + 2 拳蔬菜 + 1 拳主食 主食一半换成粗粮：玉米、红薯、燕麦、糙米、杂粮饭 晚餐主食减量至半拳，尽量不吃精制米面 保证每日蛋白质摄入，每餐必有肉 / 蛋 / 豆制品 烹饪用喷油壶，少油少盐，拒绝红烧、油炸、勾芡 戒掉奶茶、果汁、可乐等所有含糖饮料 每日饮水量 1.5–2L，饭前半小时喝一杯温水 吃饭顺序：蔬菜→蛋白质→主食，平稳血糖更抗饿 细嚼慢咽，每口咀嚼 20 次，一餐用时不少于 15 分钟 吃到七分饱就停，不撑肚子，不光盘硬塞剩饭 杜绝宵夜，睡前 3 小时不再进食任何东西 零食替换：薯片饼干换成黄瓜、小番茄、无糖酸奶 外食优先清蒸、白灼、清炒，避开干锅、火锅重油汤底 水果放在白天吃，每日总量一个拳头大小，不吃榴莲荔枝等高糖果 不喝蜂蜜水、红糖水，避开隐形添加糖 腌制咸菜、腊肉少吃，高盐容易水肿显胖 自己带饭做饭，可控油盐，减少外卖频次 少吃面条、馄饨、包子这类高碳水快餐 每周可安排 1 次轻欺骗餐，避免长期压抑暴饮暴食', 'a8618e78895e9cfa1004b4890d42385aecff4d4180fb849b2005fad5aada35ea', 2, '1', '0', '2', 'admin', '2026-07-06 17:30:16', 'admin', '2026-07-06 17:30:35');

-- ----------------------------
-- Table structure for ai_model
-- ----------------------------
DROP TABLE IF EXISTS `ai_model`;
CREATE TABLE `ai_model`  (
                             `model_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '模型ID',
                             `model_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '展示名称',
                             `model_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型编码',
                             `provider` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'dashscope' COMMENT '提供商 dashscope/openai',
                             `api_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API模型名 qwen-plus/qwen-turbo',
                             `model_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'chat' COMMENT '类型 chat/embedding/image',
                             `temperature` decimal(3, 2) NOT NULL DEFAULT 0.70 COMMENT '温度',
                             `top_p` decimal(3, 2) NOT NULL DEFAULT 0.90 COMMENT 'TopP',
                             `max_tokens` int UNSIGNED NOT NULL DEFAULT 2048 COMMENT '最大Token',
                             `input_price` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '输入单价(元/百万Token)',
                             `output_price` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '输出单价(元/百万Token)',
                             `base_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API Base URL',
                             `api_key_ref` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密钥配置键(非明文)',
                             `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                             `default_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '默认（0否 1是）',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                             `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                             `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                             `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`model_id`) USING BTREE,
                             UNIQUE INDEX `uk_ai_model_code`(`model_code` ASC) USING BTREE,
                             INDEX `idx_ai_model_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI模型配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_model
-- ----------------------------
INSERT INTO `ai_model` VALUES (1, '通义千问Plus', 'qwen-plus', 'dashscope', 'qwen-plus', 'chat', 0.70, 0.90, 4096, 0.8000, 2.0000, NULL, NULL, 10, '1', '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2026-07-06 17:36:35');
INSERT INTO `ai_model` VALUES (2, '通义Embedding V3', 'text-embedding-v3', 'dashscope', 'text-embedding-v3', 'embedding', 0.00, 0.90, 0, 0.0000, 0.0000, NULL, '', 20, '1', '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2026-07-06 15:21:05');
INSERT INTO `ai_model` VALUES (3, '通义千问Turbo', 'qwen-turbo', 'dashscope', 'qwen-turbo', 'chat', 0.60, 0.90, 2048, 0.3000, 0.6000, NULL, '', 30, '0', '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2026-07-06 15:21:07');
INSERT INTO `ai_model` VALUES (4, 'Ollama Qwen2.5', 'ollama-qwen2.5', 'ollama', 'qwen2.5:7b', 'chat', 0.70, 0.90, 4096, 0.0000, 0.0000, 'http://127.0.0.1:11434', NULL, 40, '0', '0', '0', '超级管理员', '2026-05-28 10:20:49', '', '2026-05-28 10:20:49');

-- ----------------------------
-- Table structure for ai_plan_review
-- ----------------------------
DROP TABLE IF EXISTS `ai_plan_review`;
CREATE TABLE `ai_plan_review`  (
                                   `review_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '审核ID',
                                   `biz_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型 train/diet',
                                   `plan_id` bigint UNSIGNED NOT NULL COMMENT '方案ID',
                                   `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                   `session_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'AI会话ID',
                                   `content_snapshot` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '方案快照JSON',
                                   `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '审核状态（0待审 1通过 2拒绝）',
                                   `reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拒绝原因',
                                   `auditor_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '审核人',
                                   `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   PRIMARY KEY (`review_id`) USING BTREE,
                                   INDEX `idx_ai_plan_review_plan`(`biz_type` ASC, `plan_id` ASC) USING BTREE,
                                   INDEX `idx_ai_plan_review_audit`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI方案审核' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_plan_review
-- ----------------------------
INSERT INTO `ai_plan_review` VALUES (1, 'train', 1, 1, 1, NULL, '1', NULL, 1, '2025-05-18 14:00:00', '2025-05-18 14:00:00');
INSERT INTO `ai_plan_review` VALUES (2, 'diet', 1, 1, 1, NULL, '1', NULL, 1, '2025-05-18 15:00:00', '2025-05-18 15:00:00');

-- ----------------------------
-- Table structure for ai_prompt
-- ----------------------------
DROP TABLE IF EXISTS `ai_prompt`;
CREATE TABLE `ai_prompt`  (
                              `prompt_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '提示词ID',
                              `prompt_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
                              `prompt_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编码',
                              `scene` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景 train_plan/diet_plan/chat/coach',
                              `system_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'System提示词',
                              `user_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'User模板',
                              `variables_json` json NULL COMMENT '变量定义',
                              `model_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '默认模型ID',
                              `version` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '版本',
                              `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`prompt_id`) USING BTREE,
                              UNIQUE INDEX `uk_ai_prompt_code`(`prompt_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '提示词管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_prompt
-- ----------------------------
INSERT INTO `ai_prompt` VALUES (1, 'AI健身教练', 'fitness_coach', '客服话术', '你是FitAI专业健身教练，回答需安全、科学，涉及疾病请建议就医。', '用户问题：{question}', '[\"question\"]', 1, 1, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-07-06 17:02:42');
INSERT INTO `ai_prompt` VALUES (2, '训练计划生成', 'train_plan_gen', '训练咨询', '你是 FitAI 训练计划教练。根据用户描述生成科学训练计划，并在末尾输出唯一 JSON（可用 ```json 代码块），字段：planName, goal, weeks, daysPerWeek, fitnessLevel, items:[{weekNo,dayNo,actionName,sets,reps,restSec}]。', '身高{height}cm 体重{weight}kg 目标{goal}', '[\"height\", \"weight\", \"goal\"]', 1, 1, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-07-06 17:02:42');
INSERT INTO `ai_prompt` VALUES (3, '饮食方案生成', 'diet_plan_gen', '饮食建议', '你是 FitAI 营养师。根据用户需求生成饮食方案，并在末尾输出唯一 JSON（可用 ```json 代码块），字段：planName, targetCalories, macros:{protein_g,carbs_g,fat_g}, items:[{mealType,foodName,amount}]。', '目标：{goal} 每日热量约 {calories}kcal', '[\"goal\", \"calories\"]', 1, 1, 3, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-07-06 17:02:42');

-- ----------------------------
-- Table structure for ai_quota_gift
-- ----------------------------
DROP TABLE IF EXISTS `ai_quota_gift`;
CREATE TABLE `ai_quota_gift`  (
                                  `gift_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '赠送ID',
                                  `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                  `quota_month` char(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '额度月份 yyyy-MM',
                                  `gift_tokens` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '赠送 Token',
                                  `benefit_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联权益ID',
                                  `reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原因',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  PRIMARY KEY (`gift_id`) USING BTREE,
                                  INDEX `idx_ai_quota_gift_customer_month`(`customer_id` ASC, `quota_month` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI配额赠送流水' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_quota_gift
-- ----------------------------

-- ----------------------------
-- Table structure for ai_rag_eval
-- ----------------------------
DROP TABLE IF EXISTS `ai_rag_eval`;
CREATE TABLE `ai_rag_eval`  (
                                `eval_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评估ID',
                                `knowledge_id` bigint UNSIGNED NOT NULL COMMENT '知识库ID',
                                `eval_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评估名称',
                                `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '测试问题',
                                `expected_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '期望答案',
                                `actual_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '实际答案',
                                `recall_score` decimal(5, 4) NULL DEFAULT NULL COMMENT '上下文召回（期望答案能否由检索上下文推出）',
                                `faithfulness` decimal(5, 4) NULL DEFAULT NULL COMMENT '忠实度',
                                `relevancy_score` decimal(5, 4) NULL DEFAULT NULL COMMENT '回答相关性',
                                `correctness` decimal(5, 4) NULL DEFAULT NULL COMMENT '答案正确性（对比期望答案）',
                                `latency_ms` int UNSIGNED NULL DEFAULT NULL COMMENT '耗时',
                                `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                PRIMARY KEY (`eval_id`) USING BTREE,
                                INDEX `idx_ai_rag_eval_kb`(`knowledge_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'RAG质量评估' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_rag_eval
-- ----------------------------
INSERT INTO `ai_rag_eval` VALUES (1, 1, '深蹲知识召回测试', '深蹲主要练哪些肌肉？', '股四头肌和臀大肌', '深蹲主要锻炼股四头肌和臀大肌，需注意膝盖方向。', 0.9200, 0.9500, 0.9300, 0.9600, 850, '超级管理员', '2025-05-20 10:00:00');
INSERT INTO `ai_rag_eval` VALUES (2, 1, '动作安全测试', '深蹲时膝盖内扣怎么办？', '保持膝盖与脚尖同向', '应调整站位，使膝盖与脚尖方向一致，避免内扣。', 0.8800, 0.9100, 0.9000, 0.9200, 920, '超级管理员', '2025-05-20 10:00:00');
INSERT INTO `ai_rag_eval` VALUES (3, 2, '减肥有氧怎么做', '减肥有氧怎么做', '不做有氧', '根据你提供的上下文，减肥有氧运动的科学做法如下（简洁实用、可直接执行）：\n\n✅ **频率与时长**：  \n- 每周做 **4–6 次** 有氧运动；  \n- 每次 **30–45 分钟**，新手从 **快走起步**（保护膝盖、易坚持）。\n\n✅ **优选有氧方式（任选1–2种长期坚持）**：  \n- ✅ 低冲击友好：快走、椭圆机、骑行、爬楼梯（能走楼梯尽量不坐电梯）  \n- ✅ 高效燃脂（进阶可选）：慢跑、跳绳、帕梅拉燃脂操  \n- ✅ 提升效率技巧：加入 **间歇训练**（如快走2分钟+慢走1分钟交替），比匀速运动减脂效果更好。\n\n✅ **关键注意事项**：  \n- ❌ 不空腹做高强度有氧（防低血糖、头晕乏力）；  \n- ✅ 运动前后各拉伸 **5–10 分钟**（防肌肉僵硬、酸痛、结块）；  \n- ✅ 运动后 **30分钟内补充少量蛋白质**（如1个鸡蛋/1杯无糖豆浆/一小把坚果），不暴饮暴食；  \n- ✅ 循序渐进加量，避免初期猛练→受伤→放弃；  \n- ✅ 经期降低强度，以**慢走、轻柔拉伸**为主，不蹦跳。\n\n✅ **融入生活的小习惯（放大燃脂效果）**：  \n- 每坐1小时，起身走动 **3–5分钟**；  \n- 上下班短途步行或骑车；  \n- 日常多站立、少瘫坐，减少久坐；  \n- 记录每次运动时长，可视化进步，增强动力；  \n- 结伴运动，互相监督，提升坚持率。\n\n💡小贴士：有氧是“制造热量缺口”的主力，但**必须搭配每周2–3次力量训练**（深蹲、俯卧撑、臀桥等），才能保住肌肉、提高基础代谢——这才是不易反弹的核心。\n\n需要我帮你制定一份「新手第1周有氧计划表」或「居家间歇快走跟练方案」，可以随时告诉我 👍', 0.0000, 1.0000, 1.0000, 0.0000, 8736, 'admin', '2026-07-06 17:31:58');
INSERT INTO `ai_rag_eval` VALUES (4, 2, '减肥有氧怎么做', '减肥有氧怎么做', '一周3回', '根据你提供的上下文，减肥有氧运动的科学做法如下（简洁、实操、不伤身）：\n\n✅ **频率与时长**：  \n- 每周安排 **4–6 次** 有氧运动；  \n- 单次 **30–45 分钟**（新手可从 20 分钟起步，逐步加量）。\n\n✅ **新手友好首选**：  \n- **快走**（平地或坡度适中）：不伤膝盖、易坚持、适合长期执行；  \n- 其他可选方式（按兴趣和条件搭配）：  \n  ▪ 慢跑（关节无不适者）  \n  ▪ 跳绳（注意缓冲，初学者从“原地踏步跳”开始）  \n  ▪ 骑行（户外/动感单车/健身房固定自行车）  \n  ▪ 爬楼梯（代替电梯，注意保护膝关节，下楼尽量走扶梯减少冲击）  \n  ▪ 椭圆机（低冲击，对膝盖友好）  \n  ▪ 帕梅拉等居家燃脂操（跟练时注意动作标准，避免代偿）\n\n✅ **效率升级技巧**：  \n- ✨ **加入间歇训练（HIIT 或 MIIT）**：如快走 3 分钟 + 快跑/高抬腿 1 分钟，循环进行——比匀速慢跑燃脂效率更高，且有“后燃效应”；  \n- ⏱️ **运动前后各拉伸 5–10 分钟**：预防肌肉僵硬、酸痛、结块，提升柔韧与体态；  \n- 🚫 **避免空腹高强度有氧**（尤其晨起未进食时），以防低血糖、头晕乏力；  \n- 🥛 **运动后 30 分钟内补充少量优质蛋白**（如1个鸡蛋、1杯无糖豆浆、1小把坚果），助修复、防肌肉流失，不暴饮暴食。\n\n✅ **融入日常的小习惯（积少成多）**：  \n- 每坐 1 小时 → 起身走动 3–5 分钟；  \n- 上下班短途 → 步行或骑行；  \n- 能走楼梯绝不坐电梯（量力而行，保护膝盖）；  \n- 睡前/晨起简单拉伸 + 泡脚 15 分钟 → 改善循环、缓解水肿、优化体态。\n\n✅ **关键提醒（避坑指南）**：  \n- ❌ 不要一上来就猛练（如每天跑5公里+跳绳1000个），易受伤、难坚持、易放弃；  \n- ❌ 不要只做有氧、忽略力量训练（每周仍需 2–3 次力量，保住肌肉、提高基础代谢）；  \n- ❌ 不要频繁称重（建议**每周固定时间（晨起空腹+如厕后）称1次**），关注体态照、围度变化更真实；  \n- ✅ 接纳节奏：健康减脂是 **每月减 2–4 斤**，稳扎稳打不易反弹。\n\n需要我帮你制定一份「新手第1周有氧计划表」或「居家免器械有氧+力量组合方案」，也可以随时告诉我 👍', 0.0000, 1.0000, 1.0000, 0.3000, 17184, 'admin', '2026-07-06 17:33:16');
INSERT INTO `ai_rag_eval` VALUES (5, 2, '减肥有氧', '减肥有氧', '每周安排 4–6 次有氧运动，单次时长 30–45 分钟\n\n新手从快走起步，不伤膝盖，容易长期坚持', '根据你提供的详细上下文，以下是关于「减肥有氧」的**科学、可执行、兼顾安全与可持续性**的核心要点总结，已去重、归类、逻辑梳理，便于你日常参考和实践：\n\n---\n\n### ✅ 一、有氧运动安排（减脂主力）\n- **频率**：每周 4–6 次  \n- **时长**：每次 30–45 分钟（新手可从 20 分钟起步，渐进增加）  \n- **强度建议**：中等强度（心率维持在最大心率的 60%–75%，说话微喘但能连贯成句）  \n- **优选方式（低伤膝+易坚持）**：  \n  ▪️ 快走（新手首选，保护膝盖，适合长期养成习惯）  \n  ▪️ 慢跑、骑行、椭圆机、爬楼梯（心肺+下肢燃脂兼顾）  \n  ▪️ 跳绳（高效但需一定基础，注意缓冲/地面/膝盖状态）  \n  ▪️ 帕梅拉燃脂操（趣味性强，居家友好，注意动作规范防代偿）\n\n- **进阶技巧**：  \n  ▪️ 加入**间歇训练（HIIT 或中低强度间歇）**：如快走3分钟+慢走1分钟，循环进行。燃脂效率＞匀速有氧，且有“后燃效应”。  \n  ▪️ 避免空腹高强度有氧（尤其晨起未进食），以防低血糖、头晕、肌肉分解。\n\n---\n\n### ✅ 二、搭配策略（提升整体减脂效果）\n- **力量训练不可少**：每周 2–3 次，重点练大肌群（腿、臀、背），保住肌肉量 → 维持/提高基础代谢 → 防止平台期 & 反弹。  \n- **碎片化消耗**：每坐1小时起身走动3–5分钟；短途通勤步行/骑行；能走楼梯不坐电梯。  \n- **运动前后必做**：各拉伸 5–10 分钟（动态热身+静态放松），防酸痛、防结块、保柔韧。  \n- **运动后营养**：30分钟内补充少量优质蛋白（如1个鸡蛋、1杯无糖豆浆、1小把原味坚果），不暴饮暴食。\n\n---\n\n### ✅ 三、关键提醒（避坑指南）\n- ❌ 不要一上来就猛练（如每天跳绳1小时+跑步5km）→ 易受伤、难坚持、易放弃。  \n- ❌ 不要只盯着体重秤：每周固定时间（晨起空腹+排便后）称重即可；更推荐**每周拍体态对比照 + 测围度（腰/臀/大腿）**。  \n- ❌ 不要忽略恢复：睡眠＜7小时、长期熬夜 → 皮质醇↑、瘦素↓、饥饿感↑ → 腹部脂肪更易堆积。务必23点前睡，保证7–8小时优质睡眠。  \n- ❌ 不要用情绪化饮食“补偿”运动或压力：想吃先停10分钟，喝温水+区分“真饿 vs 嘴馋”。\n\n---\n\n### ✅ 四、小贴士（增强可持续性）\n- 🌟 设定固定运动时段（如每天19:00–19:45），形成生物钟式习惯。  \n- 🌟 记录每次运动时长/类型/感受（简单打卡），积累正向反馈，提升动力。  \n- 🌟 结伴运动 or 加入社群：互相监督鼓励，显著提高坚持率。  \n- 🌟 经期适当降低强度：以快走、猫牛式、轻柔拉伸为主，避免跳跃、负重、倒立类动作。\n\n---\n\n如需，我可为你：\n- 定制一份「新手4周渐进有氧计划表」（含快走→慢跑→间歇过渡）  \n- 推荐「居家无器械燃脂操合集」（帕梅拉/跟练资源+注意事项）  \n- 提供「办公室/碎片时间微运动清单」（坐姿+站立版，每组≤2分钟）  \n\n欢迎随时告诉我你的当前状态（如：体重/身高/运动经验/膝盖情况/作息），帮你个性化优化 👇', 1.0000, 1.0000, 1.0000, 0.9500, 22300, 'admin', '2026-07-06 17:34:03');

-- ----------------------------
-- Table structure for ai_token_usage
-- ----------------------------
DROP TABLE IF EXISTS `ai_token_usage`;
CREATE TABLE `ai_token_usage`  (
                                   `usage_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流水ID',
                                   `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                   `scene` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景 plan/plan_modify/diet/review/knowledge',
                                   `model_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模型编码',
                                   `prompt_tokens` int UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Prompt Token',
                                   `completion_tokens` int UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Completion Token',
                                   `total_tokens` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '总Token',
                                   `biz_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联业务ID',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '记录时间',
                                   PRIMARY KEY (`usage_id`) USING BTREE,
                                   INDEX `idx_ai_token_usage_scene_time`(`scene` ASC, `create_time` ASC) USING BTREE,
                                   INDEX `idx_ai_token_usage_customer_time`(`customer_id` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI非会话Token消耗(审计/统计)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_token_usage
-- ----------------------------
INSERT INTO `ai_token_usage` VALUES (1, 1, 'plan', 'qwen-plus', 320, 641, 961, 1, '2026-07-07 10:00:00');
INSERT INTO `ai_token_usage` VALUES (2, 1, 'plan_modify', 'qwen-plus', 180, 308, 488, 1, '2026-07-06 10:00:00');
INSERT INTO `ai_token_usage` VALUES (3, 1, 'diet', 'qwen-plus', 420, 869, 1289, 1, '2026-07-05 10:00:00');
INSERT INTO `ai_token_usage` VALUES (4, 1, 'chat', 'qwen-plus', 362, 83, 445, 13, '2026-07-06 15:21:40');
INSERT INTO `ai_token_usage` VALUES (5, 1, 'chat', 'qwen-plus', 362, 85, 447, 14, '2026-07-06 15:22:13');
INSERT INTO `ai_token_usage` VALUES (6, 1, 'chat', 'qwen-plus', 363, 62, 425, 15, '2026-07-06 17:26:36');

-- ----------------------------
-- Table structure for ai_tool
-- ----------------------------
DROP TABLE IF EXISTS `ai_tool`;
CREATE TABLE `ai_tool`  (
                            `tool_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '工具ID',
                            `tool_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工具名称',
                            `tool_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编码',
                            `tool_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'function' COMMENT '类型 function/mcp/http',
                            `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                            `schema_json` json NOT NULL COMMENT '参数Schema(JSON Schema)',
                            `endpoint` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'HTTP/MCP端点',
                            `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                            `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                            `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`tool_id`) USING BTREE,
                            UNIQUE INDEX `uk_ai_tool_code`(`tool_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI工具管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_tool
-- ----------------------------
INSERT INTO `ai_tool` VALUES (1, '查询食物热量', 'query_food_calorie', 'function', '从食物库查询热量', '{\"type\": \"object\", \"required\": [\"food_name\"], \"properties\": {\"food_name\": {\"type\": \"string\", \"description\": \"食物名称，如鸡胸肉、米饭\"}}}', NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-07-07 00:00:04');
INSERT INTO `ai_tool` VALUES (2, '查询动作库', 'query_train_action', 'function', '查询训练动作', '{\"type\": \"object\", \"required\": [\"muscle_group\"], \"properties\": {\"muscle_group\": {\"type\": \"string\", \"description\": \"肌群或动作关键词，如胸、背、深蹲\"}}}', NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-07-07 00:00:04');
INSERT INTO `ai_tool` VALUES (3, 'MCP 天气查询', 'mcp_weather', 'mcp', '查询指定城市的当前天气', '{\"parameters\": {\"type\": \"object\", \"required\": [\"city\"], \"properties\": {\"city\": {\"type\": \"string\", \"description\": \"城市名，如北京、上海\"}}}, \"description\": \"查询指定城市的当前天气\", \"mcpToolName\": \"get_weather\"}', 'http://127.0.0.1:8081/mcp', '0', '0', 'admin', '2026-05-26 16:38:26', 'admin', '2026-07-07 00:00:04');
INSERT INTO `ai_tool` VALUES (4, 'HTTP 查食物热量', 'http_search_food', 'http', '通过网关 REST 按关键词搜索食物热量', '{\"path\": \"/diet/food/search\", \"method\": \"GET\", \"headers\": {\"Content-Type\": \"application/json\"}, \"timeoutMs\": 10000, \"parameters\": {\"type\": \"object\", \"required\": [\"keyword\"], \"properties\": {\"keyword\": {\"type\": \"string\", \"description\": \"食物名称，如鸡胸肉、米饭\"}}}, \"description\": \"按关键词搜索食物及热量信息\", \"queryMapping\": {\"keyword\": \"keyword\"}}', 'http://127.0.0.1:20000', '1', '0', 'admin', '2026-07-07 00:00:04', '', '2026-07-07 00:00:04');
INSERT INTO `ai_tool` VALUES (5, '查近7天训练次数', 'sql_train_week_count', 'sql_query', '统计用户最近7天训练记录条数（只读）', '{\"maxRows\": 100, \"readOnly\": true, \"datasource\": \"fitai_train\", \"parameters\": {\"type\": \"object\", \"required\": [\"customer_id\"], \"properties\": {\"customer_id\": {\"type\": \"integer\", \"description\": \"用户ID\"}}}, \"description\": \"统计指定用户最近7天的训练记录条数\", \"sqlTemplate\": \"SELECT COUNT(*) AS train_count FROM train_record WHERE customer_id = :customer_id AND train_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)\", \"allowedTables\": [\"train_record\"]}', 'fitai_train', '1', '0', 'admin', '2026-07-07 00:00:04', '', '2026-07-07 00:00:04');

-- ----------------------------
-- Table structure for ai_trace
-- ----------------------------
DROP TABLE IF EXISTS `ai_trace`;
CREATE TABLE `ai_trace`  (
                             `trace_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '追踪ID',
                             `trace_uuid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链路UUID',
                             `session_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '会话ID',
                             `conversation_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'conversationId',
                             `customer_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
                             `model_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模型',
                             `biz_scene` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务场景 chat/train_plan 等',
                             `agent_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Agent 键 coach/diet/rehab/cs',
                             `prompt_tokens` int UNSIGNED NULL DEFAULT NULL COMMENT '输入 Token',
                             `completion_tokens` int UNSIGNED NULL DEFAULT NULL COMMENT '输出 Token',
                             `tool_call_count` int UNSIGNED NULL DEFAULT 0 COMMENT '工具调用次数',
                             `steps_json` json NOT NULL COMMENT '链路步骤 retrieve/prompt/llm/output',
                             `total_latency_ms` int UNSIGNED NULL DEFAULT NULL COMMENT '总耗时',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0成功 1失败）',
                             `error_msg` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             PRIMARY KEY (`trace_id`) USING BTREE,
                             UNIQUE INDEX `uk_ai_trace_uuid`(`trace_uuid` ASC) USING BTREE,
                             INDEX `idx_ai_trace_session`(`session_id` ASC) USING BTREE,
                             INDEX `idx_ai_trace_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI链路追踪' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_trace
-- ----------------------------
INSERT INTO `ai_trace` VALUES (1, 'trace-uuid-001', 1, 'conv-uuid-001', 1, 'qwen-plus', 'chat', 'coach', 320, 641, 0, '[{\"kind\": \"RETRIEVE\", \"name\": \"retrieve\", \"spanId\": 1, \"status\": \"OK\", \"durationMs\": 120}, {\"kind\": \"CONFIG\", \"name\": \"prompt\", \"spanId\": 2, \"status\": \"OK\", \"durationMs\": 30}, {\"kind\": \"LLM\", \"name\": \"llm\", \"spanId\": 3, \"status\": \"OK\", \"durationMs\": 1100}, {\"kind\": \"OUTPUT\", \"name\": \"output\", \"spanId\": 4, \"status\": \"OK\", \"durationMs\": 10}]', 1260, '0', NULL, '2026-07-05 10:00:00');

-- ----------------------------
-- Table structure for ai_customer_quota
-- ----------------------------
DROP TABLE IF EXISTS `ai_user_quota`;
DROP TABLE IF EXISTS `ai_customer_quota`;
CREATE TABLE `ai_customer_quota`  (
                                      `quota_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '额度ID',
                                      `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                      `quota_month` char(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '额度月份 yyyy-MM',
                                      `quota_total` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '月总额度(Token)',
                                      `quota_used` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '已用 Token',
                                      `quota_remain` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '剩余 Token(冗余)',
                                      `source_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'base' COMMENT '月基础来源 base/member（赠送见 ai_quota_gift）',
                                      `source_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '来源ID',
                                      `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`quota_id`) USING BTREE,
                                      UNIQUE INDEX `uk_ai_customer_quota`(`customer_id` ASC, `quota_month` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI用户月额度' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_customer_quota
-- ----------------------------
INSERT INTO `ai_customer_quota` VALUES (1, 1, '2025-05', 30000, 3600, 26400, 'member', 1, '2025-05-20 10:00:00', '2025-05-20 10:00:00');
INSERT INTO `ai_customer_quota` VALUES (2, 3, '2025-05', 150000, 4500, 145500, 'member', 2, '2025-05-20 10:00:00', '2025-05-20 10:00:00');
INSERT INTO `ai_customer_quota` VALUES (3, 1, '2026-07', 30000, 1317, 28683, 'base', NULL, '2026-07-06 09:28:50', '2026-07-06 15:21:40');

-- ----------------------------
-- Table structure for ai_vector_index
-- ----------------------------
DROP TABLE IF EXISTS `ai_vector_index`;
CREATE TABLE `ai_vector_index`  (
                                    `index_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '索引ID',
                                    `index_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Redis索引名',
                                    `vector_engine` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'milvus' COMMENT '向量引擎',
                                    `index_prefix` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '键前缀 fitai:vec:',
                                    `milvus_collection` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Milvus collection',
                                    `knowledge_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联知识库',
                                    `vector_dims` smallint UNSIGNED NOT NULL DEFAULT 1024 COMMENT '向量维度',
                                    `distance_metric` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'COSINE' COMMENT 'COSINE/L2/IP',
                                    `algorithm` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'HNSW' COMMENT '索引算法 HNSW/FLAT',
                                    `embedding_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'text-embedding-v3' COMMENT '嵌入模型',
                                    `endpoint` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '连接地址 milvus://host:19530',
                                    `doc_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '文档数(冗余)',
                                    `last_rebuild_time` datetime NULL DEFAULT NULL COMMENT '最后重建时间',
                                    `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1重建中 2异常）',
                                    `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                    `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                    `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                    `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`index_id`) USING BTREE,
                                    UNIQUE INDEX `uk_ai_vector_index_name`(`index_name` ASC) USING BTREE,
                                    UNIQUE INDEX `uk_ai_vector_index_kb`(`knowledge_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Redis向量索引注册' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_vector_index
-- ----------------------------
INSERT INTO `ai_vector_index` VALUES (4, 'fit_lose', 'milvus', 'idx:fit_lose', 'fit_lose', 2, 1024, 'COSINE', 'HNSW', 'text-embedding-v3', '192.168.49.110', 0, NULL, '0', '0', 'admin', '2026-07-06 16:19:22', NULL, '2026-07-06 16:21:08');
INSERT INTO `ai_vector_index` VALUES (5, 'fitai_muscle', 'milvus', 'idx:fitai_muscle', 'fitai_muscle', 3, 1024, 'COSINE', 'HNSW', 'text-embedding-v3', '192.168.49.110', 0, NULL, '0', '0', 'admin', '2026-07-06 17:28:36', NULL, '2026-07-06 17:29:19');




-- ==============================================
-- 模块: fitai_crs (原 fitai_crs 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for crs_category
-- ----------------------------
DROP TABLE IF EXISTS `crs_category`;
CREATE TABLE `crs_category`  (
                                 `category_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                 `parent_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID',
                                 `ancestors` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '祖级列表',
                                 `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
                                 `icon_oss_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '图标OSS图片ID',
                                 `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标（Element 图标类名）',
                                 `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                 `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`category_id`) USING BTREE,
                                 INDEX `idx_crs_category_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crs_category
-- ----------------------------
INSERT INTO `crs_category` VALUES (1, 0, '0', '燃脂训练', NULL, 'Bicycle', 1, '0', '0', '超级管理员', '2026-05-26 18:07:30', '超级管理员', '2026-05-26 18:07:30');
INSERT INTO `crs_category` VALUES (2, 0, '0', '力量训练', NULL, 'Medal', 2, '0', '0', '超级管理员', '2026-05-26 18:08:28', '超级管理员', '2026-05-26 18:08:28');
INSERT INTO `crs_category` VALUES (3, 1, '0,1', 'HIIT', NULL, 'Trophy', 1, '0', '0', '超级管理员', '2026-05-26 18:07:38', '超级管理员', '2026-05-26 18:07:38');
INSERT INTO `crs_category` VALUES (4, 0, '0', '瑜伽普拉提', NULL, 'Stopwatch', 3, '0', '0', '超级管理员', '2026-05-26 18:08:39', '超级管理员', '2026-05-26 18:08:39');
INSERT INTO `crs_category` VALUES (5, 4, '0,4', '入门瑜伽', NULL, NULL, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for crs_course
-- ----------------------------
DROP TABLE IF EXISTS `crs_course`;
CREATE TABLE `crs_course`  (
                               `course_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '课程ID',
                               `category_id` bigint UNSIGNED NOT NULL COMMENT '分类ID',
                               `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类名(冗余)',
                               `course_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程标题',
                               `cover_oss_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '封面OSS图片ID',
                               `cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面URL(冗余,来自media_oss)',
                               `coach_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '教练ID',
                               `coach_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '教练名(冗余)',
                               `difficulty` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '难度（1初级 2中级 3高级）',
                               `duration_min` int UNSIGNED NULL DEFAULT 0 COMMENT '总时长分钟(冗余)',
                               `lesson_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '课时数(冗余)',
                               `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '价格',
                               `is_free` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '是否免费（0否 1是）',
                               `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '简介',
                               `video_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联视频ID(media_vod)',
                               `calories` int UNSIGNED NULL DEFAULT 0 COMMENT '消耗卡路里',
                               `equipment` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所需器材',
                               `view_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '播放量(冗余)',
                               `like_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数(冗余)',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0上架 1下架 2草稿）',
                               `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作人',
                               `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`course_id`) USING BTREE,
                               INDEX `idx_crs_course_category`(`category_id` ASC) USING BTREE,
                               INDEX `idx_crs_course_coach`(`coach_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crs_course
-- ----------------------------
INSERT INTO `crs_course` VALUES (1, 3, 'HIIT', '21天燃脂挑战', 1, 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/course/hiit21_cover.jpg', 1, '王强', '1', 45, 2, 99.00, '0', '适合初学者的HIIT课程，每天15-25分钟，配合饮食建议效果更佳。', NULL, 300, NULL, 3280, 328, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `crs_course` VALUES (2, 2, '力量训练', '新手增肌入门', 2, 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/course/muscle_cover.jpg', 1, '王强', '1', 60, 3, 129.00, '0', '从深蹲、硬拉、卧推三大项开始，系统学习力量训练基础。', NULL, 400, '哑铃', 1560, 156, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `crs_course` VALUES (3, 5, '入门瑜伽', '7天晨间瑜伽', 3, 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/course/yoga_cover.jpg', 2, '李婉清', '1', 35, 7, 0.00, '1', '每天10分钟唤醒身体，改善久坐体态。', NULL, 150, '瑜伽垫', 8920, 892, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for crs_course_like
-- ----------------------------
DROP TABLE IF EXISTS `crs_course_like`;
CREATE TABLE `crs_course_like`  (
                                    `like_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '点赞记录ID',
                                    `course_id` bigint UNSIGNED NOT NULL COMMENT '课程ID',
                                    `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '点赞时间',
                                    PRIMARY KEY (`like_id`) USING BTREE,
                                    UNIQUE INDEX `uk_crs_course_like`(`course_id` ASC, `customer_id` ASC) USING BTREE,
                                    INDEX `idx_crs_course_like_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程点赞' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crs_course_like
-- ----------------------------

-- ----------------------------
-- Table structure for crs_course_favorite
-- ----------------------------
DROP TABLE IF EXISTS `crs_course_favorite`;
CREATE TABLE `crs_course_favorite`  (
                                        `favorite_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏记录ID',
                                        `course_id` bigint UNSIGNED NOT NULL COMMENT '课程ID',
                                        `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                        `create_time` datetime NULL DEFAULT NULL COMMENT '收藏时间',
                                        PRIMARY KEY (`favorite_id`) USING BTREE,
                                        UNIQUE INDEX `uk_crs_course_favorite`(`course_id` ASC, `customer_id` ASC) USING BTREE,
                                        INDEX `idx_crs_course_favorite_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程收藏' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crs_course_favorite
-- ----------------------------

-- ----------------------------
-- Table structure for crs_learn_progress
-- ----------------------------
DROP TABLE IF EXISTS `crs_learn_progress`;
CREATE TABLE `crs_learn_progress`  (
                                       `progress_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '进度ID',
                                       `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                       `course_id` bigint UNSIGNED NOT NULL COMMENT '课程ID',
                                       `course_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程名(冗余)',
                                       `video_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '当前课时ID',
                                       `video_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课时名(冗余)',
                                       `progress_pct` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '进度%',
                                       `learned_sec` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '已学秒数',
                                       `total_sec` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '总秒数(冗余)',
                                       `is_finished` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '完课（0否 1是）',
                                       `last_learn_time` datetime NULL DEFAULT NULL COMMENT '最后学习时间',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                       `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`progress_id`) USING BTREE,
                                       UNIQUE INDEX `uk_crs_learn_progress`(`customer_id` ASC, `course_id` ASC) USING BTREE,
                                       INDEX `idx_crs_learn_progress_course`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程学习进度' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crs_learn_progress
-- ----------------------------
INSERT INTO `crs_learn_progress` VALUES (1, 1, 1, '21天燃脂挑战', NULL, NULL, 70, 1415, 2020, '0', '2025-05-20 21:00:00', '2025-05-20 21:00:00', '2025-05-20 21:00:00');
INSERT INTO `crs_learn_progress` VALUES (2, 2, 3, '7天晨间瑜伽', NULL, NULL, 100, 600, 600, '1', '2025-05-18 07:20:00', '2025-05-18 07:20:00', '2025-05-18 07:20:00');

-- ==============================================
-- 模块: fitai_diet (原 fitai_diet 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for diet_category
-- ----------------------------
DROP TABLE IF EXISTS `diet_category`;
CREATE TABLE `diet_category`  (
                                  `category_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                  `parent_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID',
                                  `ancestors` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '祖级列表',
                                  `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
                                  `level` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '层级(1起)',
                                  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`category_id`) USING BTREE,
                                  INDEX `idx_diet_category_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '饮食分类(树)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of diet_category
-- ----------------------------
INSERT INTO `diet_category` VALUES (1, 0, '0', '主食', 1, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (2, 0, '0', '蛋白质', 1, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (3, 0, '0', '蔬菜', 1, 3, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (4, 0, '0', '水果', 1, 4, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (5, 0, '0', '乳制品', 1, 5, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (16, 0, '0', '坚果油脂', 1, 6, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (6, 1, '0,1', '米饭面食', 2, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (7, 1, '0,1', '杂粮薯类', 2, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (8, 2, '0,2', '禽畜肉', 2, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (9, 2, '0,2', '蛋类', 2, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (10, 2, '0,2', '水产', 2, 3, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (11, 2, '0,2', '豆制品', 2, 4, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (12, 3, '0,3', '叶菜', 2, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (13, 3, '0,3', '花果菜', 2, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (14, 4, '0,4', '常见水果', 2, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (15, 5, '0,5', '奶与酸奶', 2, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_category` VALUES (17, 16, '0,16', '坚果', 2, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for diet_food
-- ----------------------------
DROP TABLE IF EXISTS `diet_food`;
CREATE TABLE `diet_food`  (
                              `food_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '食物ID',
                              `category_id` bigint UNSIGNED NOT NULL COMMENT '分类ID',
                              `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类名(冗余)',
                              `food_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '食物名称',
                              `food_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '编码',
                              `unit` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '100g' COMMENT '计量单位',
                              `calories` decimal(8, 2) NOT NULL COMMENT '热量kcal',
                              `protein_g` decimal(8, 2) NULL DEFAULT NULL COMMENT '蛋白质g',
                              `fat_g` decimal(8, 2) NULL DEFAULT NULL COMMENT '脂肪g',
                              `carbs_g` decimal(8, 2) NULL DEFAULT NULL COMMENT '碳水g',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`food_id`) USING BTREE,
                              INDEX `idx_diet_food_category`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '食物库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of diet_food
-- ----------------------------
INSERT INTO `diet_food` VALUES (1, 6, '米饭面食', '糙米饭', NULL, '100g', 116.00, 2.60, 0.90, 25.60, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (2, 8, '禽畜肉', '鸡胸肉', NULL, '100g', 133.00, 19.40, 5.00, 2.50, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (3, 9, '蛋类', '水煮蛋', NULL, '100g', 144.00, 12.60, 9.60, 1.20, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (4, 7, '杂粮薯类', '燕麦片', NULL, '100g', 367.00, 13.00, 6.50, 62.00, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (5, 6, '米饭面食', '全麦面包', NULL, '100g', 247.00, 8.50, 3.50, 43.00, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (6, 10, '水产', '三文鱼', NULL, '100g', 208.00, 20.00, 13.00, 0.00, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (7, 11, '豆制品', '豆腐', NULL, '100g', 76.00, 8.10, 4.80, 1.90, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (8, 13, '花果菜', '西兰花', NULL, '100g', 34.00, 2.80, 0.40, 7.00, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (9, 12, '叶菜', '生菜', NULL, '100g', 15.00, 1.40, 0.20, 2.90, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (10, 14, '常见水果', '香蕉', NULL, '100g', 89.00, 1.10, 0.30, 22.80, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (11, 14, '常见水果', '苹果', NULL, '100g', 52.00, 0.30, 0.20, 13.80, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (12, 15, '奶与酸奶', '无糖酸奶', NULL, '100g', 61.00, 3.50, 3.30, 4.70, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (13, 7, '杂粮薯类', '红薯', NULL, '100g', 86.00, 1.60, 0.10, 20.10, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (14, 10, '水产', '虾仁', NULL, '100g', 93.00, 18.60, 0.80, 2.00, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (15, 15, '奶与酸奶', '纯牛奶', NULL, '100g', 54.00, 3.00, 3.20, 3.40, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (16, 17, '坚果', '核桃', NULL, '100g', 654.00, 15.20, 65.20, 13.70, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (17, 12, '叶菜', '菠菜', NULL, '100g', 23.00, 2.90, 0.40, 3.60, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `diet_food` VALUES (18, 6, '米饭面食', '荞麦面', NULL, '100g', 348.00, 9.70, 2.70, 71.50, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for diet_plan
-- ----------------------------
DROP TABLE IF EXISTS `diet_plan`;
CREATE TABLE `diet_plan`  (
                              `plan_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '方案ID',
                              `plan_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方案名称',
                              `customer_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
                              `plan_date` date NULL DEFAULT NULL COMMENT '计划日期（C端按日唯一）',
                              `plan_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '方案类型（1系统 2个人 3AI）',
                              `target_calories` decimal(8, 2) NOT NULL COMMENT '目标热量kcal',
                              `macros_json` json NULL COMMENT '宏量目标JSON：protein_g/carbs_g/fat_g',
                              `plan_json` json NULL COMMENT '四餐JSON：breakfast/lunch/dinner/snacks',
                              `ai_session_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'AI会话ID',
                              `audit_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核状态（0待审 1通过 2拒绝）',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`plan_id`) USING BTREE,
                              INDEX `idx_diet_plan_customer`(`customer_id` ASC) USING BTREE,
                              UNIQUE INDEX `uk_diet_plan_customer_date`(`customer_id` ASC, `plan_date` ASC)
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '饮食方案' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of diet_plan
-- ----------------------------
INSERT INTO `diet_plan` VALUES (1, 'AI减脂饮食-1800kcal', 1, '2025-05-18', '3', 1800.00, '{\"fat_g\": 60, \"carbs_g\": 180, \"protein_g\": 120}', '{\"breakfast\":[{\"foodId\":3,\"foodName\":\"水煮蛋\",\"portion\":\"100g\",\"calories\":144,\"protein\":12.6,\"carbs\":1.2,\"fat\":9.6}],\"lunch\":[{\"foodId\":2,\"foodName\":\"鸡胸肉\",\"portion\":\"150g\",\"calories\":200,\"protein\":29.1,\"carbs\":3.8,\"fat\":7.5},{\"foodId\":1,\"foodName\":\"糙米饭\",\"portion\":\"150g\",\"calories\":174,\"protein\":3.9,\"carbs\":38.4,\"fat\":1.4}],\"dinner\":[],\"snacks\":[]}', NULL, '1', '0', '0', '超级管理员', '2025-05-18 15:00:00', '', '2025-05-18 15:00:00');

-- ----------------------------
-- Table structure for diet_plan_item
-- ----------------------------
DROP TABLE IF EXISTS `diet_plan_item`;
CREATE TABLE `diet_plan_item`  (
                                   `item_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
                                   `plan_id` bigint UNSIGNED NOT NULL COMMENT '方案ID',
                                   `meal_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '餐次（1早 2午 3晚 4加餐）',
                                   `food_id` bigint UNSIGNED NOT NULL COMMENT '食物ID',
                                   `food_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '食物名(冗余)',
                                   `portion_g` decimal(8, 2) NOT NULL COMMENT '份量g',
                                   `calories` decimal(8, 2) NOT NULL COMMENT '热量kcal(冗余)',
                                   PRIMARY KEY (`item_id`) USING BTREE,
                                   INDEX `idx_diet_plan_item_plan`(`plan_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '饮食方案明细' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of diet_plan_item
-- ----------------------------
INSERT INTO `diet_plan_item` VALUES (1, 1, '1', 3, '水煮蛋', 2.00, 144.00);
INSERT INTO `diet_plan_item` VALUES (2, 1, '2', 2, '鸡胸肉', 150.00, 199.50);
INSERT INTO `diet_plan_item` VALUES (3, 1, '2', 1, '糙米饭', 150.00, 174.00);

-- ----------------------------
-- Table structure for diet_record
-- ----------------------------
DROP TABLE IF EXISTS `diet_record`;
CREATE TABLE `diet_record`  (
                                `record_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                                `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                `record_date` date NOT NULL COMMENT '记录日期',
                                `meal_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '餐次（1早 2午 3晚 4加餐）',
                                `food_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '食物ID（手填可空）',
                                `food_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '食物名(冗余)',
                                `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '分类ID',
                                `portion_g` decimal(8, 2) NOT NULL COMMENT '份量g',
                                `calories` decimal(8, 2) NOT NULL COMMENT '热量kcal',
                                `protein_g` decimal(8, 2) NULL DEFAULT NULL COMMENT '蛋白质g',
                                `carbs_g` decimal(8, 2) NULL DEFAULT NULL COMMENT '碳水g',
                                `fat_g` decimal(8, 2) NULL DEFAULT NULL COMMENT '脂肪g',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                PRIMARY KEY (`record_id`) USING BTREE,
                                INDEX `idx_diet_record_customer_date`(`customer_id` ASC, `record_date` ASC) USING BTREE,
                                INDEX `idx_diet_record_date`(`record_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '饮食记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of diet_record
-- ----------------------------
INSERT INTO `diet_record` VALUES (1, 1, '2025-05-20', '1', 3, '水煮蛋', 9, 100.00, 144.00, 12.60, 1.20, 9.60, '2025-05-20 10:00:00');
INSERT INTO `diet_record` VALUES (2, 1, '2025-05-20', '2', 2, '鸡胸肉', 8, 120.00, 160.00, 23.30, 3.00, 6.00, '2025-05-20 10:00:00');




-- ==============================================
-- 模块: fitai_media (原 fitai_media 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for media_oss
-- ----------------------------
DROP TABLE IF EXISTS `media_oss`;
CREATE TABLE `media_oss`  (
                              `oss_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'OSS图片ID',
                              `oss_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片名称',
                              `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'OSS分类ID',
                              `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类名(冗余)',
                              `biz_module` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务模块 course/product/marketing/action',
                              `biz_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '业务ID',
                              `oss_bucket` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OSS Bucket',
                              `oss_object_key` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OSS ObjectKey',
                              `oss_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OSS访问URL',
                              `oss_file_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原始文件名',
                              `oss_file_size` bigint UNSIGNED NULL DEFAULT NULL COMMENT '文件大小(字节)',
                              `oss_etag` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OSS ETag',
                              `mime_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MIME类型',
                              `width` int UNSIGNED NULL DEFAULT NULL COMMENT '宽px',
                              `height` int UNSIGNED NULL DEFAULT NULL COMMENT '高px',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1失败）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`oss_id`) USING BTREE,
                              INDEX `idx_media_oss_category`(`category_id` ASC) USING BTREE,
                              INDEX `idx_media_oss_biz`(`biz_module` ASC, `biz_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 133 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OSS图片' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of media_oss
-- ----------------------------
INSERT INTO `media_oss` VALUES (85, '1', 6, '人物头像', 'library', NULL, 'fitai-media', 'media/2026/05/26/207a60ec5deb422d9bf3866a14ea2b40.jpeg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/207a60ec5deb422d9bf3866a14ea2b40.jpeg', '1.jpeg', 16548, NULL, 'image/jpeg', 610, 610, '0', '0', '超级管理员', '2026-05-26 15:10:46', '超级管理员', '2026-05-26 15:10:46');
INSERT INTO `media_oss` VALUES (86, '2', 6, '人物头像', 'library', NULL, 'fitai-media', 'media/2026/05/26/07c41b7499154850b569ad4261245568.jpeg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/07c41b7499154850b569ad4261245568.jpeg', '2.jpeg', 32362, NULL, 'image/jpeg', 1100, 1100, '0', '0', '超级管理员', '2026-05-26 15:10:46', '超级管理员', '2026-05-26 15:10:46');
INSERT INTO `media_oss` VALUES (87, '9', 6, '人物头像', 'library', NULL, 'fitai-media', 'media/2026/05/26/509296e2d4324d7a8b1a0dc4344e2d14.jpeg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/509296e2d4324d7a8b1a0dc4344e2d14.jpeg', '9.jpeg', 257518, NULL, 'image/jpeg', 3000, 3000, '0', '0', '超级管理员', '2026-05-26 15:10:47', '超级管理员', '2026-05-26 15:10:47');
INSERT INTO `media_oss` VALUES (88, '10', 6, '人物头像', 'library', NULL, 'fitai-media', 'media/2026/05/26/232a8ddd58bd4e0eac6b526925e4be9d.jpeg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/232a8ddd58bd4e0eac6b526925e4be9d.jpeg', '10.jpeg', 36263, NULL, 'image/jpeg', 260, 260, '0', '0', '超级管理员', '2026-05-26 15:10:47', '超级管理员', '2026-05-26 15:10:47');
INSERT INTO `media_oss` VALUES (89, '11', 6, '人物头像', 'library', NULL, 'fitai-media', 'media/2026/05/26/a3886845409c43cab5dfe9589218b6f0.jpeg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a3886845409c43cab5dfe9589218b6f0.jpeg', '11.jpeg', 38679, NULL, 'image/jpeg', 285, 285, '0', '0', '超级管理员', '2026-05-26 15:10:47', '超级管理员', '2026-05-26 15:10:47');
INSERT INTO `media_oss` VALUES (90, 'banner1', 7, '轮播图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/668ace8c78924cee8dbdf0d17ee16e71.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/668ace8c78924cee8dbdf0d17ee16e71.png', 'banner1.png', 4334892, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:00', '超级管理员', '2026-05-26 15:11:00');
INSERT INTO `media_oss` VALUES (91, 'banner2', 7, '轮播图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/63002352eded4770b6357506fba81242.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/63002352eded4770b6357506fba81242.png', 'banner2.png', 3360773, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:02', '超级管理员', '2026-05-26 15:11:02');
INSERT INTO `media_oss` VALUES (92, 'banner3', 7, '轮播图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/2c31c7e0d9504e38b910b68827389ee4.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/2c31c7e0d9504e38b910b68827389ee4.png', 'banner3.png', 4319694, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:03', '超级管理员', '2026-05-26 15:11:03');
INSERT INTO `media_oss` VALUES (93, 'banner4', 7, '轮播图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/1fbeb2ae81644f45acf1f8b850029a90.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1fbeb2ae81644f45acf1f8b850029a90.png', 'banner4.png', 4328942, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:05', '超级管理员', '2026-05-26 15:11:05');
INSERT INTO `media_oss` VALUES (94, 'banner5', 7, '轮播图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/13bce42e820746878d4b75c8f583cdbc.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/13bce42e820746878d4b75c8f583cdbc.png', 'banner5.png', 3883272, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:07', '超级管理员', '2026-05-26 15:11:07');
INSERT INTO `media_oss` VALUES (95, 'banner6', 7, '轮播图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/1a0243fdc7154ef89af67e0b0681f9a1.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1a0243fdc7154ef89af67e0b0681f9a1.png', 'banner6.png', 4201801, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:08', '超级管理员', '2026-05-26 15:11:08');
INSERT INTO `media_oss` VALUES (96, '腹肌训练', 1, '课程图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/db7917cb637545cb93a70b6809c9828f.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/db7917cb637545cb93a70b6809c9828f.png', '腹肌训练.png', 3156872, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:25', '超级管理员', '2026-05-26 15:11:25');
INSERT INTO `media_oss` VALUES (97, '跑步', 1, '课程图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/6cde87304c0848419e8495987f00e647.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/6cde87304c0848419e8495987f00e647.png', '跑步.png', 3947285, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:26', '超级管理员', '2026-05-26 15:11:26');
INSERT INTO `media_oss` VALUES (98, '全身塑形', 1, '课程图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/1c9aaf13ffad4f12b7755bbd0df02e20.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1c9aaf13ffad4f12b7755bbd0df02e20.png', '全身塑形.png', 2380557, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:27', '超级管理员', '2026-05-26 15:11:27');
INSERT INTO `media_oss` VALUES (99, '燃脂', 1, '课程图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/00e3a488ce6d4860aedb03f0b250bad8.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/00e3a488ce6d4860aedb03f0b250bad8.png', '燃脂.png', 3979633, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:29', '超级管理员', '2026-05-26 15:11:29');
INSERT INTO `media_oss` VALUES (100, '生成课程封面', 1, '课程图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/12002791dee24f90bb8dd39bea80d2fb.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/12002791dee24f90bb8dd39bea80d2fb.png', '生成课程封面.png', 2540232, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:30', '超级管理员', '2026-05-26 15:11:30');
INSERT INTO `media_oss` VALUES (101, '瑜伽', 1, '课程图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/f956542ce04841d099475eef006a91e1.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/f956542ce04841d099475eef006a91e1.png', '瑜伽.png', 3147219, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:11:31', '超级管理员', '2026-05-26 15:11:31');
INSERT INTO `media_oss` VALUES (102, '波比跳', 4, '动作图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/db733cf8114b41a5b84f353bb166c87b.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/db733cf8114b41a5b84f353bb166c87b.png', '波比跳.png', 1519989, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:12:41', '超级管理员', '2026-05-26 15:12:41');
INSERT INTO `media_oss` VALUES (103, '俯卧撑', 4, '动作图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/a92739cecbdf4634beeb76a963c56d2a.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a92739cecbdf4634beeb76a963c56d2a.png', '俯卧撑.png', 3413862, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:12:42', '超级管理员', '2026-05-26 15:12:42');
INSERT INTO `media_oss` VALUES (104, '卷腹', 4, '动作图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/74a32161d2ac404fb53d4f3e6a5deb8e.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/74a32161d2ac404fb53d4f3e6a5deb8e.png', '卷腹.png', 1754248, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:12:43', '超级管理员', '2026-05-26 15:12:43');
INSERT INTO `media_oss` VALUES (105, '开合跳', 4, '动作图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/5ec9dffebb4644f38235a994c79c7360.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/5ec9dffebb4644f38235a994c79c7360.png', '开合跳.png', 1575997, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:12:44', '超级管理员', '2026-05-26 15:12:44');
INSERT INTO `media_oss` VALUES (106, '平板支撑', 4, '动作图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/32a8768eddf147c496727e3e78a08bec.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/32a8768eddf147c496727e3e78a08bec.png', '平板支撑.png', 3149792, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:12:45', '超级管理员', '2026-05-26 15:12:45');
INSERT INTO `media_oss` VALUES (107, '深蹲', 4, '动作图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/6509f62e78b64ed5a4b0dfa4513bc80d.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/6509f62e78b64ed5a4b0dfa4513bc80d.png', '深蹲.png', 2455017, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:12:46', '超级管理员', '2026-05-26 15:12:46');
INSERT INTO `media_oss` VALUES (108, '可调节哑铃套装', 8, '商品封面', 'library', NULL, 'fitai-media', 'media/2026/05/26/d323add7ca4f478f885a1b1c4f979491.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/d323add7ca4f478f885a1b1c4f979491.png', '可调节哑铃套装.png', 2136774, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:13:51', '超级管理员', '2026-05-26 15:13:51');
INSERT INTO `media_oss` VALUES (109, '乳清蛋白粉 ', 8, '商品封面', 'library', NULL, 'fitai-media', 'media/2026/05/26/aa56d4fd1ba04e5a9cf860403b4da0e8.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/aa56d4fd1ba04e5a9cf860403b4da0e8.png', '乳清蛋白粉 .png', 1820755, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:13:51', '超级管理员', '2026-05-26 15:13:51');
INSERT INTO `media_oss` VALUES (110, '速干运动T恤', 8, '商品封面', 'library', NULL, 'fitai-media', 'media/2026/05/26/9b838462b0bb497a89587ad1cb9afdc4.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/9b838462b0bb497a89587ad1cb9afdc4.png', '速干运动T恤.png', 2719612, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:13:52', '超级管理员', '2026-05-26 15:13:52');
INSERT INTO `media_oss` VALUES (111, '瑜伽垫', 8, '商品封面', 'library', NULL, 'fitai-media', 'media/2026/05/26/3bd55a6a8e8542878354527d5bf244f2.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/3bd55a6a8e8542878354527d5bf244f2.png', '瑜伽垫.png', 1950246, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:13:53', '超级管理员', '2026-05-26 15:13:53');
INSERT INTO `media_oss` VALUES (112, '专业健身手套', 8, '商品封面', 'library', NULL, 'fitai-media', 'media/2026/05/26/342cdae52b444ebfb9525f660587bfc3.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/342cdae52b444ebfb9525f660587bfc3.png', '专业健身手套.png', 2443818, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:13:54', '超级管理员', '2026-05-26 15:13:54');
INSERT INTO `media_oss` VALUES (113, '专业跑步鞋', 8, '商品封面', 'library', NULL, 'fitai-media', 'media/2026/05/26/4ca3b25a8d854be99b2c1b2344e0992c.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/4ca3b25a8d854be99b2c1b2344e0992c.png', '专业跑步鞋.png', 1981380, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:13:54', '超级管理员', '2026-05-26 15:13:54');
INSERT INTO `media_oss` VALUES (114, 'miao', 9, '秒杀商品', 'library', NULL, 'fitai-media', 'media/2026/05/26/17835e37898b46fd99d87f8bd5173302.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/17835e37898b46fd99d87f8bd5173302.png', 'miao.png', 4361684, NULL, 'image/png', 2730, 1535, '0', '0', '超级管理员', '2026-05-26 15:14:10', '超级管理员', '2026-05-26 15:14:10');
INSERT INTO `media_oss` VALUES (115, '361', 10, '品牌图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/8cf98d393aaa4df8a9b077c540f18c5c.jpg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/8cf98d393aaa4df8a9b077c540f18c5c.jpg', '361.jpg', 11524, NULL, 'image/jpeg', 324, 242, '0', '0', '超级管理员', '2026-05-26 15:14:15', '超级管理员', '2026-05-26 15:14:15');
INSERT INTO `media_oss` VALUES (116, 'anta', 10, '品牌图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/712a5e4e5f43468d96761dab61c9b376.jpg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/712a5e4e5f43468d96761dab61c9b376.jpg', 'anta.jpg', 12923, NULL, 'image/jpeg', 324, 242, '0', '0', '超级管理员', '2026-05-26 15:14:16', '超级管理员', '2026-05-26 15:14:16');
INSERT INTO `media_oss` VALUES (117, 'erke', 10, '品牌图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/fafb38c3686b4f658fc4f9387d473468.jpg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/fafb38c3686b4f658fc4f9387d473468.jpg', 'erke.jpg', 13732, NULL, 'image/jpeg', 325, 243, '0', '0', '超级管理员', '2026-05-26 15:14:16', '超级管理员', '2026-05-26 15:14:16');
INSERT INTO `media_oss` VALUES (118, 'lining', 10, '品牌图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/f7694767941b4b22a97bedb576434b8c.jpg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/f7694767941b4b22a97bedb576434b8c.jpg', 'lining.jpg', 13843, NULL, 'image/jpeg', 325, 243, '0', '0', '超级管理员', '2026-05-26 15:14:16', '超级管理员', '2026-05-26 15:14:16');
INSERT INTO `media_oss` VALUES (119, 'tebu', 10, '品牌图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/8a20d169c82542c58ddb0e5aa186a4dc.jpg', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/8a20d169c82542c58ddb0e5aa186a4dc.jpg', 'tebu.jpg', 19031, NULL, 'image/jpeg', 325, 243, '0', '0', '超级管理员', '2026-05-26 15:14:16', '超级管理员', '2026-05-26 15:14:16');
INSERT INTO `media_oss` VALUES (120, '可调节哑铃套装2', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/201a471ad59349af99a886917bcb4f05.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/201a471ad59349af99a886917bcb4f05.png', '可调节哑铃套装2.png', 2311946, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:22', '超级管理员', '2026-05-26 15:14:22');
INSERT INTO `media_oss` VALUES (121, '可调节哑铃套装3', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/10b30c74c9b04077a7d37892fe2dc0a2.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/10b30c74c9b04077a7d37892fe2dc0a2.png', '可调节哑铃套装3.png', 2171658, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:23', '超级管理员', '2026-05-26 15:14:23');
INSERT INTO `media_oss` VALUES (122, '乳清蛋白粉2', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/110599f0b57147449586fee01e04c3cd.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/110599f0b57147449586fee01e04c3cd.png', '乳清蛋白粉2.png', 3250532, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:24', '超级管理员', '2026-05-26 15:14:24');
INSERT INTO `media_oss` VALUES (123, '乳清蛋白粉3 ', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/ae5d13288fc4403f9a024c792facfcef.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/ae5d13288fc4403f9a024c792facfcef.png', '乳清蛋白粉3 .png', 1900582, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:25', '超级管理员', '2026-05-26 15:14:25');
INSERT INTO `media_oss` VALUES (124, '速干运动T恤2', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/ab43f3447e664c30ad7f9f864651da07.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/ab43f3447e664c30ad7f9f864651da07.png', '速干运动T恤2.png', 2291933, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:26', '超级管理员', '2026-05-26 15:14:26');
INSERT INTO `media_oss` VALUES (125, '速干运动T恤3', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/d5e1a5e7fdbc4384b8192cb464105434.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/d5e1a5e7fdbc4384b8192cb464105434.png', '速干运动T恤3.png', 2452421, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:26', '超级管理员', '2026-05-26 15:14:26');
INSERT INTO `media_oss` VALUES (126, '速干运动T恤4', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/7b7cd0bcc6bb47d38ecbb181b0b8d84e.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/7b7cd0bcc6bb47d38ecbb181b0b8d84e.png', '速干运动T恤4.png', 2815662, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:27', '超级管理员', '2026-05-26 15:14:27');
INSERT INTO `media_oss` VALUES (127, '瑜伽垫2', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/50fa22ec30124d53be4b72aff7b53ef3.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/50fa22ec30124d53be4b72aff7b53ef3.png', '瑜伽垫2.png', 2475486, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:28', '超级管理员', '2026-05-26 15:14:28');
INSERT INTO `media_oss` VALUES (128, '瑜伽垫3', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/65032a253eed4adfa44b6afec69b702e.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/65032a253eed4adfa44b6afec69b702e.png', '瑜伽垫3.png', 2493262, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:29', '超级管理员', '2026-05-26 15:14:29');
INSERT INTO `media_oss` VALUES (129, '专业健身手套2', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/9da7d5148e0040babd448d2f325e45cb.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/9da7d5148e0040babd448d2f325e45cb.png', '专业健身手套2.png', 2416085, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:29', '超级管理员', '2026-05-26 15:14:29');
INSERT INTO `media_oss` VALUES (130, '专业健身手套3', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/c1cd5f0d906846e09ae21390bc0a5ce8.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/c1cd5f0d906846e09ae21390bc0a5ce8.png', '专业健身手套3.png', 2077232, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:30', '超级管理员', '2026-05-26 15:14:30');
INSERT INTO `media_oss` VALUES (131, '专业跑步鞋2', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/d01104be512a455ca8935df0132c74cb.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/d01104be512a455ca8935df0132c74cb.png', '专业跑步鞋2.png', 2105756, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:31', '超级管理员', '2026-05-26 15:14:31');
INSERT INTO `media_oss` VALUES (132, '专业跑步鞋3', 11, '商品图片', 'library', NULL, 'fitai-media', 'media/2026/05/26/3ecb33380d5e43fabc1ce1ae60d0ab43.png', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/3ecb33380d5e43fabc1ce1ae60d0ab43.png', '专业跑步鞋3.png', 1985528, NULL, 'image/png', 2048, 2048, '0', '0', '超级管理员', '2026-05-26 15:14:31', '超级管理员', '2026-05-26 15:14:31');

-- ----------------------------
-- Table structure for media_oss_category
-- ----------------------------
DROP TABLE IF EXISTS `media_oss_category`;
CREATE TABLE `media_oss_category`  (
                                       `category_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                       `parent_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID',
                                       `ancestors` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '祖级列表',
                                       `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
                                       `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                       `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                       `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                       `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                       `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                       `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`category_id`) USING BTREE,
                                       INDEX `idx_media_oss_category_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OSS图片分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of media_oss_category
-- ----------------------------
INSERT INTO `media_oss_category` VALUES (1, 0, '0', '课程图片', 2, '0', '0', '超级管理员', '2026-05-26 14:42:01', '超级管理员', '2026-05-26 14:42:01');
INSERT INTO `media_oss_category` VALUES (2, 1, '0,1', '课程封面', 2, '0', '2', '超级管理员', '2026-05-26 14:55:59', '超级管理员', '2026-05-26 14:55:59');
INSERT INTO `media_oss_category` VALUES (3, 1, '0,1', '课时封面', 4, '0', '2', '超级管理员', '2026-05-26 15:02:42', '超级管理员', '2026-05-26 15:02:42');
INSERT INTO `media_oss_category` VALUES (4, 0, '0', '动作图片', 3, '0', '0', '超级管理员', '2026-05-26 14:25:07', '超级管理员', '2026-05-26 14:25:07');
INSERT INTO `media_oss_category` VALUES (5, 0, '0', '商品相关', 1, '0', '0', '超级管理员', '2026-05-26 14:25:08', '超级管理员', '2026-05-26 14:25:08');
INSERT INTO `media_oss_category` VALUES (6, 0, '0', '人物头像', 4, '0', '0', '超级管理员', '2026-05-26 14:25:02', '超级管理员', '2026-05-26 14:25:04');
INSERT INTO `media_oss_category` VALUES (7, 0, '0', '轮播图片', 5, '0', '0', '超级管理员', '2026-05-26 14:59:12', '超级管理员', '2026-05-26 15:02:48');
INSERT INTO `media_oss_category` VALUES (8, 5, '0,5', '商品封面', 1, '0', '0', '超级管理员', '2026-05-26 15:13:03', '超级管理员', '2026-05-26 15:13:03');
INSERT INTO `media_oss_category` VALUES (9, 5, '0,5', '秒杀商品', 2, '0', '0', '超级管理员', '2026-05-26 15:13:11', '超级管理员', '2026-05-26 15:13:11');
INSERT INTO `media_oss_category` VALUES (10, 5, '0,5', '品牌图片', 3, '0', '0', '超级管理员', '2026-05-26 15:13:21', '超级管理员', '2026-05-26 15:13:21');
INSERT INTO `media_oss_category` VALUES (11, 5, '0,5', '商品图片', 4, '0', '0', '超级管理员', '2026-05-26 15:13:35', '超级管理员', '2026-05-26 15:13:35');

-- ----------------------------
-- Table structure for media_vod
-- ----------------------------
DROP TABLE IF EXISTS `media_vod`;
CREATE TABLE `media_vod`  (
                              `vod_asset_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'VOD视频ID',
                              `vod_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '视频名称',
                              `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'VOD分类ID',
                              `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类名(冗余)',
                              `biz_module` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务模块 course/action',
                              `biz_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '业务ID',
                              `vod_video_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '阿里云VOD VideoId',
                              `vod_cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面URL(自有OSS永久地址,由VOD截图转存)',
                              `vod_duration` decimal(10, 2) NULL DEFAULT NULL COMMENT '时长(秒)',
                              `vod_size` bigint UNSIGNED NULL DEFAULT NULL COMMENT '文件大小(字节)',
                              `vod_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'VOD状态 Normal/Uploading/Transcoding/TranscodeFail',
                              `vod_cate_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '阿里云VOD分类ID(冗余)',
                              `vod_storage_location` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存储地址如out-in-xx',
                              `vod_snapshot_urls` json NULL COMMENT '截图URL列表',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1处理中 2失败）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`vod_asset_id`) USING BTREE,
                              UNIQUE INDEX `uk_media_vod_video_id`(`vod_video_id` ASC) USING BTREE,
                              INDEX `idx_media_vod_category`(`category_id` ASC) USING BTREE,
                              INDEX `idx_media_vod_biz`(`biz_module` ASC, `biz_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'VOD视频' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of media_vod
-- ----------------------------
INSERT INTO `media_vod` VALUES (17, '腹肌', 2, '试看片段', 'library', NULL, 'a01de73f57ee71f1811a4531858c0102', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/vod/cover/a01de73f57ee71f1811a4531858c0102.jpg', 5.04, 887628, 'Normal', 1000718118, 'outin-db7c6e788ec211f09e4300163e32a995.oss-cn-shanghai.aliyuncs.com', NULL, '0', '2', '超级管理员', '2026-05-25 12:00:31', '超级管理员', '2026-05-26 15:21:56');
INSERT INTO `media_vod` VALUES (18, '腹肌', 4, '动作库', 'library', NULL, '206434aa58d371f1af6a5107e0c90102', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/vod/cover/206434aa58d371f1af6a5107e0c90102.jpg', 5.04, 887628, 'Normal', 1000718118, 'outin-db7c6e788ec211f09e4300163e32a995.oss-cn-shanghai.aliyuncs.com', NULL, '0', '2', '超级管理员', '2026-05-26 15:22:35', '超级管理员', '2026-05-26 15:22:56');
INSERT INTO `media_vod` VALUES (19, '腹肌', 4, '动作库', 'library', NULL, '10827ebb58d371f1bf834531959d0102', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/vod/cover/10827ebb58d371f1bf834531959d0102.jpg', 5.04, 887628, 'Normal', 1000718118, 'outin-db7c6e788ec211f09e4300163e32a995.oss-cn-shanghai.aliyuncs.com', NULL, '0', '0', '超级管理员', '2026-05-26 15:23:03', '超级管理员', '2026-05-26 15:23:04');
INSERT INTO `media_vod` VALUES (20, '力量', 4, '动作库', 'library', NULL, '60f0cac958d371f1be7b5017f1e80102', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/vod/cover/60f0cac958d371f1be7b5017f1e80102.jpg', 5.04, 1134539, 'Normal', 1000718118, 'outin-db7c6e788ec211f09e4300163e32a995.oss-cn-shanghai.aliyuncs.com', NULL, '0', '0', '超级管理员', '2026-05-26 15:23:27', '超级管理员', '2026-05-26 15:23:28');
INSERT INTO `media_vod` VALUES (21, '燃脂', 4, '动作库', 'library', NULL, '5037d05058d471f1ba604531959c0102', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/vod/cover/5037d05058d471f1ba604531959c0102.jpg', 5.04, 936853, 'Normal', 1000718118, 'outin-db7c6e788ec211f09e4300163e32a995.oss-cn-shanghai.aliyuncs.com', NULL, '0', '0', '超级管理员', '2026-05-26 15:27:14', '超级管理员', '2026-05-26 15:27:15');
INSERT INTO `media_vod` VALUES (22, '塑型', 4, '动作库', 'library', NULL, '702e585c58d471f180185017f0e80102', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/vod/cover/702e585c58d471f180185017f0e80102.jpg', 5.04, 1704841, 'Normal', 1000718118, 'outin-db7c6e788ec211f09e4300163e32a995.oss-cn-shanghai.aliyuncs.com', NULL, '0', '0', '超级管理员', '2026-05-26 15:27:33', '超级管理员', '2026-05-26 15:27:35');
INSERT INTO `media_vod` VALUES (23, '瑜伽', 4, '动作库', 'library', NULL, 'b0b0035d58d471f1bff16723a78f0102', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/vod/cover/b0b0035d58d471f1bff16723a78f0102.jpg', 5.04, 624986, 'Normal', 1000718118, 'outin-db7c6e788ec211f09e4300163e32a995.oss-cn-shanghai.aliyuncs.com', NULL, '0', '0', '超级管理员', '2026-05-26 15:27:34', '超级管理员', '2026-05-26 15:27:34');

-- ----------------------------
-- Table structure for media_vod_category
-- ----------------------------
DROP TABLE IF EXISTS `media_vod_category`;
CREATE TABLE `media_vod_category`  (
                                       `category_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                       `parent_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID',
                                       `ancestors` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '祖级列表',
                                       `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
                                       `vod_cate_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '阿里云VOD分类ID(同步)',
                                       `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                       `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                       `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                       `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                       `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                       `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`category_id`) USING BTREE,
                                       INDEX `idx_media_vod_category_parent`(`parent_id` ASC) USING BTREE,
                                       INDEX `idx_media_vod_category_vod`(`vod_cate_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'VOD视频分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of media_vod_category
-- ----------------------------
INSERT INTO `media_vod_category` VALUES (1, 0, '0', '课程正片', 100001, 1, '0', '0', '超级管理员', '2026-05-26 15:22:20', '超级管理员', '2026-05-26 15:22:20');
INSERT INTO `media_vod_category` VALUES (2, 0, '0', '试看片段', 100003, 2, '0', '0', '超级管理员', '2026-05-26 15:22:18', '超级管理员', '2026-05-26 15:22:18');
INSERT INTO `media_vod_category` VALUES (3, 0, '0', '动作示范', 100002, 3, '0', '0', '超级管理员', '2026-05-26 15:22:19', '超级管理员', '2026-05-26 15:22:19');
INSERT INTO `media_vod_category` VALUES (4, 0, '0', '动作库', NULL, 1, '0', '0', '超级管理员', '2026-05-26 15:22:25', '超级管理员', '2026-05-26 15:22:25');




-- ==============================================
-- 模块: fitai_mem (原 fitai_mem 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for mem_badge
-- ----------------------------
DROP TABLE IF EXISTS `mem_badge`;
CREATE TABLE `mem_badge`  (
                              `badge_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '徽章ID',
                              `badge_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '徽章编码',
                              `badge_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '徽章名称',
                              `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                              `icon_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标URL',
                              `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'achievement' COMMENT '分类 training/social/achievement',
                              `condition_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '条件类型 train_days/checkin/points',
                              `condition_value` int UNSIGNED NULL DEFAULT NULL COMMENT '条件值',
                              `points_reward` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '奖励积分',
                              `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`badge_id`) USING BTREE,
                              UNIQUE INDEX `uk_mem_badge_code`(`badge_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '徽章定义' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mem_badge
-- ----------------------------
INSERT INTO `mem_badge` VALUES (1, 'first_train', '首次训练', '完成第一次训练记录', NULL, 'training', 'train_count', 1, 50, 1, '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2025-05-01 09:00:00');
INSERT INTO `mem_badge` VALUES (2, 'checkin_7', '连续签到7天', '连续签到满7天', NULL, 'achievement', 'checkin_days', 7, 100, 2, '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2025-05-01 09:00:00');
INSERT INTO `mem_badge` VALUES (3, 'fat_loss_5kg', '减脂5kg', '累计减重5kg', NULL, 'achievement', 'weight_loss_kg', 5, 200, 3, '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2025-05-01 09:00:00');

-- 历史死表（权益已迁 vip_benefit）
DROP TABLE IF EXISTS `mem_benefit`;

-- ----------------------------
-- Table structure for mem_checkin
-- ----------------------------
DROP TABLE IF EXISTS `mem_checkin`;
CREATE TABLE `mem_checkin`  (
                                `checkin_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '签到ID',
                                `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                `checkin_date` date NOT NULL COMMENT '签到日期',
                                `continuous_days` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '连续签到天数',
                                `points_earned` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '获得积分',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
                                PRIMARY KEY (`checkin_id`) USING BTREE,
                                UNIQUE INDEX `uk_mem_checkin_customer_date`(`customer_id` ASC, `checkin_date` ASC) USING BTREE,
                                INDEX `idx_mem_checkin_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '每日签到' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mem_checkin
-- ----------------------------
INSERT INTO `mem_checkin` VALUES (1, 1, '2025-05-20', 3, 10, '2025-05-20 08:00:00');
INSERT INTO `mem_checkin` VALUES (2, 1, '2025-05-21', 4, 10, '2025-05-21 07:55:00');
INSERT INTO `mem_checkin` VALUES (3, 3, '2025-05-22', 1, 10, '2025-05-22 07:10:00');
INSERT INTO `mem_checkin` VALUES (4, 1, '2026-07-06', 1, 10, '2026-07-06 11:42:15');

-- ----------------------------
-- Table structure for mem_customer_badge
-- ----------------------------
DROP TABLE IF EXISTS `mem_customer_badge`;
CREATE TABLE `mem_customer_badge`  (
                                       `customer_badge_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                       `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                       `badge_id` bigint UNSIGNED NOT NULL COMMENT '徽章ID',
                                       `unlock_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '解锁时间',
                                       `display_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '展示（0否 1是）',
                                       PRIMARY KEY (`customer_badge_id`) USING BTREE,
                                       UNIQUE INDEX `uk_mem_customer_badge`(`customer_id` ASC, `badge_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户徽章' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mem_customer_badge
-- ----------------------------
INSERT INTO `mem_customer_badge` VALUES (1, 1, 1, '2025-05-20 21:00:00', '1');

-- ----------------------------
-- Table structure for mem_growth_log
-- ----------------------------
DROP TABLE IF EXISTS `mem_growth_log`;
CREATE TABLE `mem_growth_log`  (
                                   `log_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流水ID',
                                   `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                   `change_value` int NOT NULL COMMENT '变动成长值',
                                   `balance_after` int UNSIGNED NOT NULL COMMENT '变动后成长值',
                                   `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型 sign/train/order',
                                   `biz_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '业务ID',
                                   `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   PRIMARY KEY (`log_id`) USING BTREE,
                                   INDEX `idx_mem_growth_log_customer`(`customer_id` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '成长值流水' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mem_growth_log
-- ----------------------------
INSERT INTO `mem_growth_log` VALUES (1, 1, 50, 1580, 'train', NULL, '完成HIIT训练', '2025-05-20 21:00:00');
INSERT INTO `mem_growth_log` VALUES (2, 3, 100, 6280, 'order', NULL, '续费年度会员', '2025-05-01 10:00:00');
INSERT INTO `mem_growth_log` VALUES (3, 4, 30, 2100, 'train', NULL, '完成半马备赛跑', '2025-05-18 07:00:00');
INSERT INTO `mem_growth_log` VALUES (4, 1, 5, 1585, 'sign', 4, '每日签到成长', '2026-07-06 11:42:15');

-- ----------------------------
-- Table structure for mem_level
-- ----------------------------
DROP TABLE IF EXISTS `mem_level`;
CREATE TABLE `mem_level`  (
                              `level_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '等级ID',
                              `level_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '等级名称',
                              `level_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '等级编码',
                              `order_num` int NOT NULL DEFAULT 0 COMMENT '排序(越大越高)',
                              `growth_min` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '成长值下限',
                              `growth_max` int UNSIGNED NULL DEFAULT NULL COMMENT '成长值上限',
                              `icon_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标URL',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`level_id`) USING BTREE,
                              UNIQUE INDEX `uk_mem_level_code`(`level_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会员等级' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mem_level
-- ----------------------------
INSERT INTO `mem_level` VALUES (1, '普通会员', 'normal', 1, 0, 999, NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `mem_level` VALUES (2, '黄金会员', 'gold', 2, 1000, 4999, NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `mem_level` VALUES (3, '钻石会员', 'diamond', 3, 5000, NULL, NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- 历史死表（关联已迁 vip_level_benefit）
DROP TABLE IF EXISTS `mem_level_benefit`;

-- ----------------------------
-- Table structure for mem_points_log
-- ----------------------------
DROP TABLE IF EXISTS `mem_points_log`;
CREATE TABLE `mem_points_log`  (
                                   `log_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流水ID',
                                   `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                   `rule_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '规则ID',
                                   `change_points` int NOT NULL COMMENT '变动积分',
                                   `balance_after` int UNSIGNED NOT NULL COMMENT '变动后余额',
                                   `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型',
                                   `biz_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '业务ID',
                                   `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   PRIMARY KEY (`log_id`) USING BTREE,
                                   INDEX `idx_mem_points_log_customer`(`customer_id` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分流水' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mem_points_log
-- ----------------------------
INSERT INTO `mem_points_log` VALUES (1, 1, 1, 10, 320, 'sign', NULL, '每日签到', '2025-05-22 08:00:00');
INSERT INTO `mem_points_log` VALUES (2, 3, 2, 20, 960, 'train', NULL, '完成胸背训练', '2025-05-21 20:30:00');
INSERT INTO `mem_points_log` VALUES (3, 4, 2, 20, 450, 'train', NULL, '完成10公里慢跑', '2025-05-20 06:45:00');
INSERT INTO `mem_points_log` VALUES (4, 1, NULL, -50, 270, 'order', NULL, '积分抵扣商品订单', '2025-05-19 15:00:00');
INSERT INTO `mem_points_log` VALUES (5, 1, NULL, 10, 330, 'checkin', 4, '每日签到（连续 1 天）', '2026-07-06 11:42:15');

-- ----------------------------
-- Table structure for mem_points_rule
-- ----------------------------
DROP TABLE IF EXISTS `mem_points_rule`;
CREATE TABLE `mem_points_rule`  (
                                    `rule_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '规则ID',
                                    `rule_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则名称',
                                    `rule_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则编码',
                                    `event_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件 sign/train/order/review',
                                    `points` int NOT NULL COMMENT '积分值(可负)',
                                    `daily_limit` int UNSIGNED NULL DEFAULT NULL COMMENT '日上限',
                                    `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                    `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                    `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                    `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                    `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`rule_id`) USING BTREE,
                                    UNIQUE INDEX `uk_mem_points_rule_code`(`rule_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分规则' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mem_points_rule
-- ----------------------------
INSERT INTO `mem_points_rule` VALUES (1, '每日签到', 'daily_sign', 'sign', 10, 10, '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2025-05-01 09:00:00');
INSERT INTO `mem_points_rule` VALUES (2, '完成训练', 'train_done', 'train', 20, 60, '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2025-05-01 09:00:00');

-- ==============================================
-- 模块: fitai_mkt (原 fitai_mkt 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for mkt_banner
-- ----------------------------
DROP TABLE IF EXISTS `mkt_banner`;
CREATE TABLE `mkt_banner`  (
                               `banner_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '轮播ID',
                               `banner_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
                               `image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
                               `link_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '链接类型：url/page/product/course',
                               `link_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '链接值',
                               `position` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'home' COMMENT '位置：home/course/product',
                               `order_num` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
                               `start_time` datetime NULL DEFAULT NULL COMMENT '有效期开始',
                               `end_time` datetime NULL DEFAULT NULL COMMENT '有效期结束',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                               `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                               `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`banner_id`) USING BTREE,
                               INDEX `idx_mkt_banner_position_status`(`position` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '轮播图' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mkt_banner
-- ----------------------------
INSERT INTO `mkt_banner` VALUES (1, '21天燃脂挑战', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/banner/home_01.jpg', 'course', '1', 'home', 1, NULL, NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `mkt_banner` VALUES (2, '年度会员限时5折', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/banner/member_sale.jpg', 'url', 'https://fitai.example.com/member', 'home', 2, NULL, NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `mkt_banner` VALUES (3, '乳清蛋白新品上市', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/banner/whey_new.jpg', 'product', '2', 'product', 3, NULL, NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');


-- Table structure for mkt_challenge
-- ----------------------------
DROP TABLE IF EXISTS `mkt_challenge`;
CREATE TABLE `mkt_challenge`  (
                                  `challenge_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '挑战ID',
                                  `challenge_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '挑战名称',
                                  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                  `cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面URL',
                                  `challenge_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型：plank/pushup/running/checkin',
                                  `target_days` int NULL DEFAULT 0 COMMENT '目标天数',
                                  `target_value` int NULL DEFAULT 0 COMMENT '目标值',
                                  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
                                  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
                                  `participant_count` int NULL DEFAULT 0 COMMENT '参与人数',
                                  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0上线 1下线）',
                                  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`challenge_id`) USING BTREE,
                                  INDEX `idx_mkt_challenge_status`(`status` ASC, `start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '挑战赛' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mkt_challenge
-- ----------------------------
INSERT INTO `mkt_challenge` VALUES (1, '30天打卡挑战', '连续打卡30天，养成运动习惯', NULL, 'checkin', 30, 30, '2025-05-01 00:00:00', '2026-12-31 23:59:59', 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `mkt_challenge` VALUES (2, '7日平板支撑', '每天累计平板支撑达标', NULL, 'plank', 7, 300, '2025-06-01 00:00:00', '2026-12-31 23:59:59', 0, '1', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for mkt_challenge_record
-- ----------------------------
DROP TABLE IF EXISTS `mkt_challenge_record`;
CREATE TABLE `mkt_challenge_record`  (
                                         `challenge_record_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                                         `challenge_id` bigint UNSIGNED NOT NULL COMMENT '挑战ID',
                                         `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID（usr_customer.customer_id）',
                                         `current_days` int NULL DEFAULT 0 COMMENT '当前完成天数',
                                         `current_value` int NULL DEFAULT 0 COMMENT '当前完成值',
                                         `finish_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '完成（0否 1是）',
                                         `cheat_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '作弊标记（0正常 1作弊）',
                                         `join_time` datetime NULL DEFAULT NULL COMMENT '参与时间',
                                         `last_checkin_time` datetime NULL DEFAULT NULL COMMENT '最后打卡时间',
                                         PRIMARY KEY (`challenge_record_id`) USING BTREE,
                                         INDEX `idx_mkt_challenge_record_challenge`(`challenge_id` ASC, `customer_id` ASC) USING BTREE,
                                         INDEX `idx_mkt_challenge_record_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '挑战赛参与记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mkt_challenge_record
-- ----------------------------
INSERT INTO `mkt_challenge_record` VALUES (1, 1, 1, 12, 12, 0, 0, '2025-05-01 10:00:00', '2025-05-20 10:00:00');
INSERT INTO `mkt_challenge_record` VALUES (2, 1, 3, 30, 30, 1, 0, '2025-05-01 11:00:00', '2025-05-30 09:00:00');


-- Table structure for mkt_coupon
-- ----------------------------
DROP TABLE IF EXISTS `mkt_coupon`;
CREATE TABLE `mkt_coupon`  (
                               `coupon_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
                               `coupon_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '优惠券名称',
                               `coupon_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型：1满减 2折扣 3无门槛',
                               `coupon_value` decimal(10, 2) NOT NULL COMMENT '优惠值（金额或折扣）',
                               `min_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最低消费',
                               `max_discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '最大优惠（折扣券封顶）',
                               `total_count` int UNSIGNED NOT NULL COMMENT '发放总量',
                               `remain_count` int UNSIGNED NOT NULL COMMENT '剩余数量',
                               `limit_per_customer` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '每人限领',
                               `start_time` datetime NOT NULL COMMENT '生效时间',
                               `end_time` datetime NOT NULL COMMENT '失效时间',
                               `use_scope` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'all' COMMENT '使用范围：all/category/product',
                               `scope_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '范围ID列表',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                               `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                               `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`coupon_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '优惠券' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mkt_coupon
-- ----------------------------
INSERT INTO `mkt_coupon` VALUES (1, '满100减20', '1', 20.00, 100.00, NULL, 1000, 998, 1, '2025-05-01 00:00:00', '2026-12-31 23:59:59', 'all', NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `mkt_coupon` VALUES (2, '全场8.5折', '2', 8.50, 0.00, 50.00, 500, 500, 1, '2025-05-01 00:00:00', '2026-12-31 23:59:59', 'all', NULL, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for mkt_coupon_receive
-- ----------------------------
DROP TABLE IF EXISTS `mkt_coupon_user`;
DROP TABLE IF EXISTS `mkt_coupon_receive`;
CREATE TABLE `mkt_coupon_receive`  (
                                       `coupon_receive_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '领取ID',
                                       `coupon_id` bigint UNSIGNED NOT NULL COMMENT '优惠券ID',
                                       `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID（usr_customer.customer_id）',
                                       `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0未使用 1已使用 2已过期/作废）',
                                       `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '使用订单号',
                                       `use_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
                                       `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
                                       `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
                                       PRIMARY KEY (`coupon_receive_id`) USING BTREE,
                                       INDEX `idx_mkt_coupon_receive_customer`(`customer_id` ASC, `status` ASC) USING BTREE,
                                       INDEX `idx_mkt_coupon_receive_coupon`(`coupon_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '优惠券领取记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mkt_coupon_receive
-- ----------------------------
INSERT INTO `mkt_coupon_receive` VALUES (1, 1, 1, '0', NULL, NULL, '2026-12-31 23:59:59', '2026-05-25 09:37:56');
INSERT INTO `mkt_coupon_receive` VALUES (2, 1, 3, '1', 'ORD202505150001', '2025-05-15 12:00:00', '2026-12-31 23:59:59', '2026-05-25 09:37:56');


-- ----------------------------
-- Table structure for mkt_mq_consume_log
-- ----------------------------
DROP TABLE IF EXISTS `mkt_mq_consume_log`;
CREATE TABLE `mkt_mq_consume_log`  (
                                       `message_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息ID(幂等键)',
                                       `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
                                       `consume_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消费时间',
                                       PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'MQ消费幂等日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mkt_mq_consume_log
-- ----------------------------




-- ==============================================
-- 模块: fitai_msg（C 端站内消息收件箱）
-- ==============================================

-- ----------------------------
-- Table structure for msg_notice
-- ----------------------------
DROP TABLE IF EXISTS `msg_notice`;
CREATE TABLE `msg_notice`  (
                               `notice_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
                               `customer_id` bigint UNSIGNED NOT NULL COMMENT 'C端用户ID（每用户一条，禁止NULL广播行）',
                               `notice_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
                               `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
                               `notice_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system' COMMENT '类型 system/order/activity/ai',
                               `biz_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联业务ID',
                               `read_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '已读（0未读 1已读）',
                               `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
                               `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`notice_id`) USING BTREE,
                               INDEX `idx_msg_customer_read`(`customer_id` ASC, `read_flag` ASC) USING BTREE,
                               INDEX `idx_msg_customer_type`(`customer_id` ASC, `notice_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'C端站内消息收件箱' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of msg_notice
-- ----------------------------
INSERT INTO `msg_notice` VALUES (1, 1, '欢迎加入 FitAI', '完善个人资料，获取专属训练计划推荐。', 'system', NULL, '0', NULL, '0', '2025-05-20 10:00:00', '2025-05-20 10:00:00');
INSERT INTO `msg_notice` VALUES (2, 1, '订单支付成功', '您的订单已支付成功，商品将尽快发货。', 'order', 1, '0', NULL, '0', '2025-05-21 11:30:00', '2025-05-21 11:30:00');
INSERT INTO `msg_notice` VALUES (3, 1, '春季挑战赛开启', '参与 30 天打卡赢积分，完成训练还可抽年卡。', 'activity', NULL, '1', '2025-05-22 09:00:00', '0', '2025-05-21 09:00:00', '2025-05-22 09:00:00');
INSERT INTO `msg_notice` VALUES (4, 1, 'AI 计划已生成', '根据您的目标生成了本周训练计划，快去看看吧。', 'ai', NULL, '0', NULL, '0', '2025-05-22 15:00:00', '2025-05-22 15:00:00');
INSERT INTO `msg_notice` VALUES (5, 2, '欢迎加入 FitAI', '完善个人资料，获取专属训练计划推荐。', 'system', NULL, '0', NULL, '0', '2025-05-20 10:05:00', '2025-05-20 10:05:00');

-- ==============================================
-- 模块: fitai_ord (原 fitai_ord 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for ord_local_message
-- ----------------------------
DROP TABLE IF EXISTS `ord_local_message`;
CREATE TABLE `ord_local_message`  (
                                      `message_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息ID(UUID，兼做消费幂等键)',
                                      `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型(如 SECKILL_STOCK_RESTORE)',
                                      `biz_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务键(订单号等)',
                                      `exchange_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标交换机',
                                      `routing_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路由键',
                                      `payload` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'JSON 消息体',
                                      `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态(0待投递确认 1已确认 2重试耗尽)',
                                      `retry_count` int NOT NULL DEFAULT 0 COMMENT '已重试次数',
                                      `next_retry_time` datetime NULL DEFAULT NULL COMMENT '下次补偿重发时间',
                                      `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`message_id`) USING BTREE,
                                      INDEX `idx_local_msg_retry`(`status` ASC, `next_retry_time` ASC) USING BTREE,
                                      INDEX `idx_local_msg_biz`(`biz_type` ASC, `biz_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '本地消息表(可靠消息最终一致性)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ord_local_message
-- ----------------------------
INSERT INTO `ord_local_message` VALUES ('afa4f8c8c1d640af978572e188888a33', 'STOCK_RELEASE', 'ORD202505220001', 'fitai.ord.exchange', 'ord.stock.event', '{\"orderNo\":\"ORD202505220001\",\"action\":\"RELEASE\",\"lines\":[{\"quantity\":1,\"skuId\":2}]}', '1', 0, '2026-07-06 09:29:29', '2026-07-06 09:28:29', '2026-07-06 09:28:29');

-- ----------------------------
-- Table structure for ord_logistics
-- ----------------------------
DROP TABLE IF EXISTS `ord_logistics`;
CREATE TABLE `ord_logistics`  (
                                  `logistics_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '物流ID',
                                  `order_id` bigint UNSIGNED NOT NULL COMMENT '订单ID',
                                  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号(冗余)',
                                  `express_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '快递公司编码',
                                  `express_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '快递公司(冗余)',
                                  `tracking_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '运单号',
                                  `ship_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
                                  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0待发货 1已发货 2运输中 3已签收）',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`logistics_id`) USING BTREE,
                                  INDEX `idx_ord_logistics_order`(`order_id` ASC) USING BTREE,
                                  INDEX `idx_ord_logistics_tracking`(`tracking_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '物流' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ord_logistics
-- ----------------------------
INSERT INTO `ord_logistics` VALUES (1, 1, 'ORD202505200001', 'SF', '顺丰速运', 'SF1324567890123', '2025-05-21 09:00:00', '2', '2025-05-21 09:00:00', '2025-05-21 09:00:00');
INSERT INTO `ord_logistics` VALUES (2, 2, 'ORD202505210001', 'YTO', '圆通速递', 'YT9876543210987', '2025-05-21 14:00:00', '3', '2025-05-21 14:00:00', '2025-05-21 14:00:00');

-- ----------------------------
-- Table structure for ord_logistics_trace
-- ----------------------------
DROP TABLE IF EXISTS `ord_logistics_trace`;
CREATE TABLE `ord_logistics_trace`  (
                                        `trace_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '轨迹ID',
                                        `logistics_id` bigint UNSIGNED NOT NULL COMMENT '物流ID',
                                        `trace_time` datetime NOT NULL COMMENT '轨迹时间',
                                        `trace_desc` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '轨迹描述',
                                        `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                        PRIMARY KEY (`trace_id`) USING BTREE,
                                        INDEX `idx_ord_logistics_trace_lid`(`logistics_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '物流轨迹' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ord_logistics_trace
-- ----------------------------
INSERT INTO `ord_logistics_trace` VALUES (1, 1, '2025-05-21 09:00:00', '【上海市】快件已由顺丰揽收', '2025-05-21 09:00:00');
INSERT INTO `ord_logistics_trace` VALUES (2, 1, '2025-05-21 18:30:00', '【上海市】快件到达上海浦东转运中心', '2025-05-21 18:30:00');
INSERT INTO `ord_logistics_trace` VALUES (3, 1, '2025-05-22 08:15:00', '【上海市】快件正在派送中，派送员：张师傅 13812345678', '2025-05-22 08:15:00');
INSERT INTO `ord_logistics_trace` VALUES (4, 2, '2025-05-21 14:00:00', '【北京市】快件已揽收', '2025-05-21 14:00:00');
INSERT INTO `ord_logistics_trace` VALUES (5, 2, '2025-05-22 10:00:00', '【北京市】快件已签收，签收人：本人', '2025-05-22 10:00:00');

-- ----------------------------
-- Table structure for ord_order
-- ----------------------------
DROP TABLE IF EXISTS `ord_order`;
CREATE TABLE `ord_order`  (
                              `order_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                              `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
                              `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                              `customer_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称(冗余)',
                              `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总额',
                              `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
                              `discount_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
                              `coupon_user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '使用的用户优惠券ID(取消/退款时退券)',
                              `activity_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '秒杀活动ID(仅SK订单,一人一单按活动判重)',
                              `pay_channel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付渠道',
                              `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                              `receiver_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货人',
                              `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货电话',
                              `receiver_addr` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货地址',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0待支付 1已支付 2已发货 3已完成 4已取消 5退款中 6已退款）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`order_id`) USING BTREE,
                              UNIQUE INDEX `uk_ord_order_no`(`order_no` ASC) USING BTREE,
                              INDEX `idx_ord_order_customer`(`customer_id` ASC) USING BTREE,
                              INDEX `idx_ord_order_status`(`status` ASC) USING BTREE,
                              INDEX `idx_ord_order_activity`(`customer_id` ASC, `activity_id` ASC) USING BTREE,
                              INDEX `idx_ord_order_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ord_order
-- ----------------------------
INSERT INTO `ord_order` VALUES (1, 'ORD202505200001', 1, '健身小白', 59.90, 59.90, 0.00, NULL, NULL, 'wechat', '2025-05-20 15:30:00', '张三', '13800000001', '上海市浦东新区张杨路500号12楼1203室', '2', '0', '2026-05-20 15:28:00', '2026-05-26 08:31:05');
INSERT INTO `ord_order` VALUES (2, 'ORD202505210001', 3, 'IronMike', 337.90, 337.90, 0.00, NULL, NULL, 'alipay', '2025-05-21 11:20:00', 'Mike', '13800000003', '北京市朝阳区建国路88号SOHO 3号楼', '3', '0', '2026-05-21 11:18:00', '2026-05-26 08:31:09');
INSERT INTO `ord_order` VALUES (3, 'ORD202505220001', 2, '瑜伽达人Lily', 299.00, 299.00, 0.00, NULL, NULL, NULL, NULL, '李莉', '13800000002', '杭州市西湖区文三路478号', '4', '0', '2026-05-22 10:05:00', '2026-07-06 09:28:29');

-- ----------------------------
-- Table structure for ord_order_item
-- ----------------------------
DROP TABLE IF EXISTS `ord_order_item`;
CREATE TABLE `ord_order_item`  (
                                   `item_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
                                   `order_id` bigint UNSIGNED NOT NULL COMMENT '订单ID',
                                   `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单号(冗余)',
                                   `spu_id` bigint UNSIGNED NOT NULL COMMENT 'SPU ID',
                                   `sku_id` bigint UNSIGNED NOT NULL COMMENT 'SKU ID',
                                   `sku_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SKU名称(冗余)',
                                   `sku_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SKU货号(冗余)',
                                   `sku_specs` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规格(冗余)',
                                   `cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面URL(冗余)',
                                   `price` decimal(10, 2) NOT NULL COMMENT '单价(下单快照)',
                                   `quantity` int UNSIGNED NOT NULL COMMENT '数量',
                                   `subtotal` decimal(10, 2) NOT NULL COMMENT '小计',
                                   PRIMARY KEY (`item_id`) USING BTREE,
                                   INDEX `idx_ord_order_item_order`(`order_id` ASC) USING BTREE,
                                   INDEX `idx_ord_order_item_sku`(`sku_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单明细(SKU快照)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ord_order_item
-- ----------------------------
INSERT INTO `ord_order_item` VALUES (1, 1, 'ORD202505200001', 1, 1, '默认款', 'FP-JR-001', '规格:默认款', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/jump_rope.jpg', 59.90, 1, 59.90);
INSERT INTO `ord_order_item` VALUES (2, 2, 'ORD202505210001', 2, 2, '巧克力味 2lb', 'MT-WP-2LB-CHOC', '口味:巧克力', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/whey.jpg', 299.00, 1, 299.00);
INSERT INTO `ord_order_item` VALUES (3, 2, 'ORD202505210001', 4, 6, '400g', 'MT-BCAA-400', '规格:400g', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/bcaa.jpg', 168.00, 1, 168.00);
INSERT INTO `ord_order_item` VALUES (4, 3, 'ORD202505220001', 2, 2, '巧克力味 2lb', 'MT-WP-2LB-CHOC', '口味:巧克力', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/whey.jpg', 299.00, 1, 299.00);

-- ----------------------------
-- Table structure for ord_payment_channel
-- ----------------------------
DROP TABLE IF EXISTS `ord_payment_channel`;
CREATE TABLE `ord_payment_channel`  (
                                        `channel_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '渠道ID',
                                        `channel_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道名称',
                                        `channel_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编码 wechat/alipay/apple',
                                        `merchant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户号',
                                        `config_json` json NULL COMMENT '配置(密钥引用)',
                                        `fee_rate` decimal(5, 4) NULL DEFAULT 0.0000 COMMENT '费率',
                                        `icon_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标URL',
                                        `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                        `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                        `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                        `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                        `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                        `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                        `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        PRIMARY KEY (`channel_id`) USING BTREE,
                                        UNIQUE INDEX `uk_ord_payment_channel_code`(`channel_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付渠道' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ord_payment_channel
-- ----------------------------
INSERT INTO `ord_payment_channel` VALUES (1, '微信支付', 'wechat', '1900000001', NULL, 0.0060, NULL, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `ord_payment_channel` VALUES (2, '支付宝', 'alipay', '2088000001', NULL, 0.0060, NULL, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for ord_payment_record
-- ----------------------------
DROP TABLE IF EXISTS `ord_payment_record`;
CREATE TABLE `ord_payment_record`  (
                                       `payment_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '支付ID',
                                       `payment_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付流水号',
                                       `merge_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '合并支付批次号',
                                       `order_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单类型（1商品 2会员）',
                                       `order_id` bigint UNSIGNED NOT NULL COMMENT '业务订单ID',
                                       `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务订单号(冗余)',
                                       `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                       `channel_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付渠道 wechat/alipay',
                                       `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
                                       `transaction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方交易号',
                                       `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0待支付 1成功 2失败 3已退款）',
                                       `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                                       `callback_raw` json NULL COMMENT '回调原文',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                       `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`payment_id`) USING BTREE,
                                       UNIQUE INDEX `uk_ord_payment_no`(`payment_no` ASC) USING BTREE,
                                       INDEX `idx_ord_payment_order`(`order_type` ASC, `order_id` ASC) USING BTREE,
                                       INDEX `idx_ord_payment_transaction`(`transaction_id` ASC) USING BTREE,
                                       INDEX `idx_ord_payment_merge`(`merge_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付流水' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ord_payment_record
-- ----------------------------
INSERT INTO `ord_payment_record` VALUES (1, 'PAY202505200001', NULL, '1', 1, 'ORD202505200001', 1, 'wechat', 59.90, '4200000123456789012345678901234', '1', '2025-05-20 15:30:00', NULL, '2025-05-20 15:30:00', '2025-05-20 15:30:00');
INSERT INTO `ord_payment_record` VALUES (2, 'PAY202505010001', NULL, '2', 1, 'MEM202505010001', 1, 'wechat', 29.90, '4200000987654321098765432109876', '1', '2025-05-01 10:00:00', NULL, '2025-05-01 10:00:00', '2025-05-01 10:00:00');
INSERT INTO `ord_payment_record` VALUES (3, 'PAY202505210001', NULL, '1', 2, 'ORD202505210001', 3, 'alipay', 337.90, '2025052122000123456789012345678', '1', '2025-05-21 11:20:00', NULL, '2025-05-21 11:20:00', '2025-05-21 11:20:00');

-- ----------------------------
-- Table structure for ord_refund
-- ----------------------------
DROP TABLE IF EXISTS `ord_refund`;
CREATE TABLE `ord_refund`  (
                               `refund_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '退款ID',
                               `refund_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '退款单号',
                               `order_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单类型（1商品 2会员）',
                               `order_id` bigint UNSIGNED NOT NULL COMMENT '原订单ID',
                               `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原订单号(冗余)',
                               `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                               `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
                               `reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原因',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0待审核 1已同意 2已拒绝 3已退款）',
                               `order_prev_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申请时订单原状态(拒绝时恢复)',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`refund_id`) USING BTREE,
                               UNIQUE INDEX `uk_ord_refund_no`(`refund_no` ASC) USING BTREE,
                               INDEX `idx_ord_refund_status`(`status` ASC) USING BTREE,
                               INDEX `idx_ord_refund_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '售后退款' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ord_refund
-- ----------------------------
INSERT INTO `ord_refund` VALUES (1, 'REF202505220001', '1', 1, 'ORD202505200001', 1, 59.90, '商品不合适，申请退货退款', '0', NULL, '2025-05-20 10:00:00', '2025-05-20 10:00:00');




-- ==============================================
-- 模块: fitai_prd (原 fitai_prd 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for prd_attr
-- ----------------------------
DROP TABLE IF EXISTS `prd_attr`;
CREATE TABLE `prd_attr`  (
                             `attr_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '属性ID',
                             `attr_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '属性名(如口味/尺码)',
                             `attr_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '类型（1销售属性 2基本属性）',
                             `select_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '录入方式（1单选 2多选 3手工输入）',
                             `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                             `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                             `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                             `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`attr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品属性' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_attr
-- ----------------------------
INSERT INTO `prd_attr` VALUES (1, '口味', '1', '1', 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_attr` VALUES (2, '尺码', '1', '1', 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_attr` VALUES (3, '规格', '1', '1', 3, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for prd_attr_value
-- ----------------------------
DROP TABLE IF EXISTS `prd_attr_value`;
CREATE TABLE `prd_attr_value`  (
                                   `value_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '属性值ID',
                                   `attr_id` bigint UNSIGNED NOT NULL COMMENT '属性ID',
                                   `attr_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '属性值',
                                   `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                   `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                   `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                   PRIMARY KEY (`value_id`) USING BTREE,
                                   INDEX `idx_prd_attr_value_attr`(`attr_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品属性值' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_attr_value
-- ----------------------------
INSERT INTO `prd_attr_value` VALUES (1, 1, '巧克力', 1, '0', '0');
INSERT INTO `prd_attr_value` VALUES (2, 1, '香草', 2, '0', '0');
INSERT INTO `prd_attr_value` VALUES (3, 2, 'M', 1, '0', '0');
INSERT INTO `prd_attr_value` VALUES (4, 2, 'L', 2, '0', '0');
INSERT INTO `prd_attr_value` VALUES (5, 3, '默认款', 1, '0', '0');
INSERT INTO `prd_attr_value` VALUES (6, 3, '2lb', 2, '0', '0');
INSERT INTO `prd_attr_value` VALUES (7, 3, '400g', 3, '0', '0');

-- ----------------------------
-- Table structure for prd_brand
-- ----------------------------
DROP TABLE IF EXISTS `prd_brand`;
CREATE TABLE `prd_brand`  (
                              `brand_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
                              `brand_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品牌名称',
                              `brand_logo` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Logo URL',
                              `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '品牌描述',
                              `first_letter` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '首字母(检索)',
                              `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`brand_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品品牌' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_brand
-- ----------------------------
INSERT INTO `prd_brand` VALUES (1, 'FitPro', NULL, '专业健身装备品牌', 'F', 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_brand` VALUES (2, 'MuscleTech', NULL, '运动营养补剂品牌', 'M', 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for prd_cart
-- ----------------------------
DROP TABLE IF EXISTS `prd_cart`;
CREATE TABLE `prd_cart`  (
                             `cart_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
                             `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                             `spu_id` bigint UNSIGNED NOT NULL COMMENT 'SPU ID(冗余)',
                             `sku_id` bigint UNSIGNED NOT NULL COMMENT 'SKU ID',
                             `sku_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SKU名称(冗余)',
                             `sku_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SKU货号(冗余)',
                             `cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面URL(冗余)',
                             `price` decimal(10, 2) NOT NULL COMMENT '单价(冗余,加购时快照)',
                             `quantity` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '数量',
                             `selected` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '选中（0否 1是）',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`cart_id`) USING BTREE,
                             UNIQUE INDEX `uk_prd_cart_customer_sku`(`customer_id` ASC, `sku_id` ASC) USING BTREE,
                             INDEX `idx_prd_cart_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户购物车(SKU)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_cart
-- ----------------------------
INSERT INTO `prd_cart` VALUES (1, 2, 2, 2, '巧克力味 2lb', 'MT-WP-2LB-CHOC', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/whey.jpg', 299.00, 1, '1', '2025-05-22 09:00:00', '2025-05-22 09:00:00');
INSERT INTO `prd_cart` VALUES (2, 1, 3, 4, 'M码', 'FP-GL-M', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/gloves.jpg', 39.90, 2, '0', '2025-05-21 14:00:00', '2025-05-21 14:00:00');

-- ----------------------------
-- Table structure for prd_category
-- ----------------------------
DROP TABLE IF EXISTS `prd_category`;
CREATE TABLE `prd_category`  (
                                 `category_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                 `parent_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID',
                                 `ancestors` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '祖级列表',
                                 `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
                                 `level` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '层级(1起)',
                                 `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标（Element 图标类名）',
                                 `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                 `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`category_id`) USING BTREE,
                                 INDEX `idx_prd_category_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品分类(树)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_category
-- ----------------------------
INSERT INTO `prd_category` VALUES (1, 0, '0', '运动装备', 1, NULL, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_category` VALUES (2, 0, '0', '营养补剂', 1, NULL, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_category` VALUES (3, 1, '0,1', '有氧器械', 2, NULL, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_category` VALUES (4, 1, '0,1', '护具配件', 2, NULL, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_category` VALUES (5, 2, '0,2', '蛋白粉', 2, NULL, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_category` VALUES (6, 2, '0,2', '氨基酸', 2, NULL, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for prd_category_attr
-- ----------------------------
DROP TABLE IF EXISTS `prd_category_attr`;
CREATE TABLE `prd_category_attr`  (
                                      `category_id` bigint UNSIGNED NOT NULL COMMENT '分类ID',
                                      `attr_id` bigint UNSIGNED NOT NULL COMMENT '属性ID',
                                      PRIMARY KEY (`category_id`, `attr_id`) USING BTREE,
                                      INDEX `idx_prd_category_attr_attr`(`attr_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '分类关联属性（复合主键）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_category_attr
-- ----------------------------
INSERT INTO `prd_category_attr` VALUES (5, 1);
INSERT INTO `prd_category_attr` VALUES (4, 2);
INSERT INTO `prd_category_attr` VALUES (3, 3);
INSERT INTO `prd_category_attr` VALUES (6, 3);

-- ----------------------------
-- Table structure for prd_local_message
-- ----------------------------
DROP TABLE IF EXISTS `prd_local_message`;
CREATE TABLE `prd_local_message`  (
                                      `message_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息ID(UUID，兼做消费幂等键)',
                                      `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型(如 PRD_ES_SYNC)',
                                      `biz_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务键(spuId 等)',
                                      `exchange_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标交换机',
                                      `routing_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路由键',
                                      `payload` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'JSON 消息体',
                                      `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态(0待投递确认 1已确认 2重试耗尽)',
                                      `retry_count` int NOT NULL DEFAULT 0 COMMENT '已重试次数',
                                      `next_retry_time` datetime NULL DEFAULT NULL COMMENT '下次补偿重发时间',
                                      `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`message_id`) USING BTREE,
                                      INDEX `idx_local_msg_retry`(`status` ASC, `next_retry_time` ASC) USING BTREE,
                                      INDEX `idx_local_msg_biz`(`biz_type` ASC, `biz_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '本地消息表(商品ES同步可靠消息)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_local_message
-- ----------------------------

-- ----------------------------
-- Table structure for prd_mq_consume_log
-- ----------------------------
DROP TABLE IF EXISTS `prd_mq_consume_log`;
CREATE TABLE `prd_mq_consume_log`  (
                                       `message_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息ID(幂等键)',
                                       `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型(STOCK_RELEASE/STOCK_DEDUCT/STOCK_RESTORE/PRD_ES_SYNC)',
                                       `consume_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消费时间',
                                       PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'MQ消费幂等日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_mq_consume_log
-- ----------------------------
INSERT INTO `prd_mq_consume_log` VALUES ('afa4f8c8c1d640af978572e188888a33', 'STOCK_RELEASE', '2026-07-06 09:28:29');

-- ----------------------------
-- Table structure for prd_sku
-- ----------------------------
DROP TABLE IF EXISTS `prd_sku`;
CREATE TABLE `prd_sku`  (
                            `sku_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
                            `spu_id` bigint UNSIGNED NOT NULL COMMENT 'SPU ID',
                            `sku_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SKU名称',
                            `sku_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SKU货号',
                            `sku_specs` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规格描述(冗余,如口味:巧克力)',
                            `price` decimal(10, 2) NOT NULL COMMENT '售价',
                            `market_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '市场价',
                            `cost_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '成本价',
                            `weight` int UNSIGNED NULL DEFAULT NULL COMMENT '重量(克)',
                            `sales_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '销量(冗余)',
                            `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
                            `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0上架 1下架）',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                            `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                            `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`sku_id`) USING BTREE,
                            UNIQUE INDEX `uk_prd_sku_sn`(`sku_sn` ASC) USING BTREE,
                            INDEX `idx_prd_sku_spu`(`spu_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品SKU' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_sku
-- ----------------------------
INSERT INTO `prd_sku` VALUES (1, 1, '默认款', 'FP-JR-001', '规格:默认款', 59.90, 79.90, 35.00, 350, 156, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_sku` VALUES (2, 2, '巧克力味 2lb', 'MT-WP-2LB-CHOC', '口味:巧克力', 299.00, 359.00, 180.00, 907, 67, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_sku` VALUES (3, 2, '香草味 2lb', 'MT-WP-2LB-VAN', '口味:香草', 299.00, 359.00, 180.00, 907, 12, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_sku` VALUES (4, 3, 'M码', 'FP-GL-M', '尺码:M', 39.90, 49.90, 18.00, 120, 52, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_sku` VALUES (5, 3, 'L码', 'FP-GL-L', '尺码:L', 39.90, 49.90, 18.00, 130, 37, 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_sku` VALUES (6, 4, '400g', 'MT-BCAA-400', '规格:400g', 168.00, 198.00, 90.00, 400, 34, 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_sku` VALUES (7, 5, '2lb', 'SKU-2lb', '规格:2lb', 55.00, 100.00, NULL, NULL, 0, 1, '0', '0', '超级管理员', '2026-05-27 09:09:04', '超级管理员', '2026-05-27 09:09:04');
INSERT INTO `prd_sku` VALUES (8, 5, '400g', 'SKU-400g', '规格:400g', 66.00, 120.00, NULL, NULL, 0, 2, '0', '0', '超级管理员', '2026-05-27 09:09:04', '超级管理员', '2026-05-27 09:09:04');

-- ----------------------------
-- Table structure for prd_sku_attr
-- ----------------------------
DROP TABLE IF EXISTS `prd_sku_attr`;
CREATE TABLE `prd_sku_attr`  (
                                 `sku_id` bigint UNSIGNED NOT NULL COMMENT 'SKU ID',
                                 `attr_id` bigint UNSIGNED NOT NULL COMMENT '属性ID',
                                 `value_id` bigint UNSIGNED NOT NULL COMMENT '属性值ID',
                                 PRIMARY KEY (`sku_id`, `attr_id`) USING BTREE,
                                 INDEX `idx_prd_sku_attr_attr`(`attr_id` ASC) USING BTREE,
                                 INDEX `idx_prd_sku_attr_value`(`value_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'SKU规格关联（复合主键）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_sku_attr
-- ----------------------------
INSERT INTO `prd_sku_attr` VALUES (2, 1, 1);
INSERT INTO `prd_sku_attr` VALUES (3, 1, 2);
INSERT INTO `prd_sku_attr` VALUES (4, 2, 3);
INSERT INTO `prd_sku_attr` VALUES (5, 2, 4);
INSERT INTO `prd_sku_attr` VALUES (1, 3, 5);
INSERT INTO `prd_sku_attr` VALUES (7, 3, 6);
INSERT INTO `prd_sku_attr` VALUES (6, 3, 7);
INSERT INTO `prd_sku_attr` VALUES (8, 3, 7);

-- ----------------------------
-- Table structure for prd_spu
-- ----------------------------
DROP TABLE IF EXISTS `prd_spu`;
CREATE TABLE `prd_spu`  (
                            `spu_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'SPU ID',
                            `category_id` bigint UNSIGNED NOT NULL COMMENT '分类ID',
                            `brand_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '品牌ID',
                            `spu_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SPU名称',
                            `spu_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SPU货号',
                            `subtitle` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '副标题/卖点',
                            `unit` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '件' COMMENT '计量单位',
                            `min_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最低售价(冗余,来自SKU)',
                            `max_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最高售价(冗余,来自SKU)',
                            `total_stock` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '总库存(冗余,来自SKU)',
                            `sales_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '总销量(冗余)',
                            `sort` int NOT NULL DEFAULT 0 COMMENT '排序(越大越靠前)',
                            `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0上架 1下架）',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                            `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                            `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`spu_id`) USING BTREE,
                            UNIQUE INDEX `uk_prd_spu_sn`(`spu_sn` ASC) USING BTREE,
                            INDEX `idx_prd_spu_category`(`category_id` ASC) USING BTREE,
                            INDEX `idx_prd_spu_brand`(`brand_id` ASC) USING BTREE,
                            INDEX `idx_prd_spu_status_sort`(`status` ASC, `sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品SPU' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_spu
-- ----------------------------
INSERT INTO `prd_spu` VALUES (1, 3, 1, '专业竞速跳绳', 'FP-JR', '360°轴承，竞速训练必备', '件', 59.90, 59.90, 186, 156, 100, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_spu` VALUES (2, 5, 2, '乳清蛋白粉 2lb', 'MT-WP-2LB', '每份24g蛋白质，低糖配方', '罐', 299.00, 299.00, 123, 79, 90, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_spu` VALUES (3, 4, 1, '健身手套', 'FP-GL', '透气防滑，硬拉卧推必备', '双', 39.90, 39.90, 320, 89, 80, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_spu` VALUES (4, 6, 2, '支链氨基酸 BCAA', 'MT-BCAA', '2:1:1比例，训练日饮用', '瓶', 168.00, 168.00, 120, 34, 70, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `prd_spu` VALUES (5, 3, 1, '登山杖', 'ds-z', '登山杖', '件', 55.00, 66.00, 300, 0, 5, '0', '0', '超级管理员', '2026-05-27 09:09:04', '', '2026-05-27 09:09:04');

-- ----------------------------
-- Table structure for prd_spu_detail
-- ----------------------------
DROP TABLE IF EXISTS `prd_spu_detail`;
CREATE TABLE `prd_spu_detail`  (
                                   `detail_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '详情ID',
                                   `spu_id` bigint UNSIGNED NOT NULL COMMENT 'SPU ID',
                                   `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '简介',
                                   `detail_html` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '详情富文本',
                                   PRIMARY KEY (`detail_id`) USING BTREE,
                                   UNIQUE INDEX `uk_prd_spu_detail_spu`(`spu_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'SPU详情' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_spu_detail
-- ----------------------------
INSERT INTO `prd_spu_detail` VALUES (1, 1, '钢丝内芯，360°轴承，适合HIIT与拳击热身。', '2025-05-20 10:00:00');
INSERT INTO `prd_spu_detail` VALUES (2, 2, '每份24g蛋白质，低糖配方，训练后30分钟内补充最佳。', '2025-05-20 10:00:00');
INSERT INTO `prd_spu_detail` VALUES (3, 3, '透气防滑，硬拉卧推必备。', '2025-05-20 10:00:00');
INSERT INTO `prd_spu_detail` VALUES (4, 4, '2:1:1比例，训练日饮用。', '2025-05-20 10:00:00');
INSERT INTO `prd_spu_detail` VALUES (5, 5, '2025-05-20 10:00:00', '2025-05-20 10:00:00');

-- ----------------------------
-- Table structure for prd_spu_image
-- ----------------------------
DROP TABLE IF EXISTS `prd_spu_image`;
CREATE TABLE `prd_spu_image`  (
                                  `image_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '图片ID',
                                  `spu_id` bigint UNSIGNED NOT NULL COMMENT 'SPU ID',
                                  `oss_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'OSS图片ID(media_oss)',
                                  `image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL(冗余,来自media_oss)',
                                  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                  `is_main` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '主图（0否 1是）',
                                  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  PRIMARY KEY (`image_id`) USING BTREE,
                                  INDEX `idx_prd_spu_image_spu`(`spu_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'SPU图片' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_spu_image
-- ----------------------------
INSERT INTO `prd_spu_image` VALUES (1, 1, NULL, 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/jump_rope.jpg', 1, '1', '0', '2025-05-20 10:00:00');
INSERT INTO `prd_spu_image` VALUES (2, 1, NULL, 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/jump_rope_2.jpg', 2, '0', '0', '2025-05-20 10:00:00');
INSERT INTO `prd_spu_image` VALUES (3, 2, NULL, 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/whey.jpg', 1, '1', '0', '2025-05-20 10:00:00');
INSERT INTO `prd_spu_image` VALUES (4, 3, NULL, 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/gloves.jpg', 1, '1', '0', '2025-05-20 10:00:00');
INSERT INTO `prd_spu_image` VALUES (5, 4, NULL, 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/product/bcaa.jpg', 1, '1', '0', '2025-05-20 10:00:00');

-- ----------------------------
-- Table structure for prd_stock
-- ----------------------------
DROP TABLE IF EXISTS `prd_stock`;
CREATE TABLE `prd_stock`  (
                              `stock_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '库存ID',
                              `sku_id` bigint UNSIGNED NOT NULL COMMENT 'SKU ID',
                              `warehouse_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '仓库编码',
                              `quantity` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '可用库存',
                              `locked_qty` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '锁定库存',
                              `low_stock` int UNSIGNED NOT NULL DEFAULT 10 COMMENT '预警库存',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`stock_id`) USING BTREE,
                              UNIQUE INDEX `uk_prd_stock_sku_wh`(`sku_id` ASC, `warehouse_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'SKU库存' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_stock
-- ----------------------------
INSERT INTO `prd_stock` VALUES (1, 1, 'DEFAULT', 186, 2, 20, '2025-05-20 10:00:00');
INSERT INTO `prd_stock` VALUES (2, 2, 'DEFAULT', 78, 0, 10, '2026-07-06 09:28:29');
INSERT INTO `prd_stock` VALUES (3, 3, 'DEFAULT', 45, 0, 10, '2025-05-20 10:00:00');
INSERT INTO `prd_stock` VALUES (4, 4, 'DEFAULT', 180, 0, 15, '2025-05-20 10:00:00');
INSERT INTO `prd_stock` VALUES (5, 5, 'DEFAULT', 140, 0, 15, '2025-05-20 10:00:00');
INSERT INTO `prd_stock` VALUES (6, 6, 'DEFAULT', 120, 0, 10, '2025-05-20 10:00:00');
INSERT INTO `prd_stock` VALUES (7, 7, 'DEFAULT', 100, 0, 10, '2026-05-27 09:09:04');
INSERT INTO `prd_stock` VALUES (8, 8, 'DEFAULT', 200, 0, 10, '2026-05-27 09:09:04');

-- ----------------------------
-- Table structure for prd_favorite
-- ----------------------------
DROP TABLE IF EXISTS `prd_user_favorite`;
DROP TABLE IF EXISTS `prd_favorite`;
CREATE TABLE `prd_favorite`  (
                                 `favorite_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
                                 `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                 `spu_id` bigint UNSIGNED NOT NULL COMMENT 'SPU ID',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '收藏时间',
                                 PRIMARY KEY (`favorite_id`) USING BTREE,
                                 UNIQUE INDEX `uk_prd_favorite_customer_spu`(`customer_id` ASC, `spu_id` ASC) USING BTREE,
                                 INDEX `idx_prd_favorite_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品收藏' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prd_favorite
-- ----------------------------
INSERT INTO `prd_favorite` VALUES (1, 1, 1, '2025-05-20 10:00:00');
INSERT INTO `prd_favorite` VALUES (2, 2, 2, '2025-05-20 10:00:00');
INSERT INTO `prd_favorite` VALUES (3, 3, 4, '2025-05-20 10:00:00');



-- ----------------------------
-- Table structure for seckill_activity
-- ----------------------------
DROP TABLE IF EXISTS `seckill_order`;
DROP TABLE IF EXISTS `seckill_sku`;
DROP TABLE IF EXISTS `seckill_activity`;
CREATE TABLE `seckill_activity`  (
                                     `activity_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '活动ID',
                                     `activity_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动名称',
                                     `cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面URL',
                                     `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                     `start_time` datetime NOT NULL COMMENT '开始时间',
                                     `end_time` datetime NOT NULL COMMENT '结束时间',
                                     `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0未开始 1进行中 2已结束）',
                                     `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                     `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                     `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                     `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`activity_id`) USING BTREE,
                                     INDEX `idx_seckill_activity_status_time`(`status` ASC, `start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '秒杀活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of seckill_activity
-- ----------------------------
INSERT INTO `seckill_activity` (`activity_id`, `activity_name`, `cover_url`, `description`, `start_time`, `end_time`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (1, '新年活动', 'http://192.168.56.10:9000/fitai/seckill/covers/2026/02/08/db40277113744be6917dae34703dcb9d.png', '新年活动', '2026-02-08 00:00:00', '2026-03-11 00:00:00', '0', '0', '超级管理员', '2026-02-08 14:10:37', '', '2026-02-08 14:10:37');

-- ----------------------------
-- Table structure for seckill_order
-- ----------------------------
CREATE TABLE `seckill_order`  (
                                  `seckill_order_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '秒杀订单ID',
                                  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
                                  `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID（usr_customer.customer_id）',
                                  `activity_id` bigint UNSIGNED NOT NULL COMMENT '活动ID',
                                  `sku_id` bigint UNSIGNED NOT NULL COMMENT 'SKU ID',
                                  `seckill_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '秒杀价',
                                  `quantity` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '购买数量',
                                  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0待支付 1已支付 2已取消）',
                                  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                                  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
                                  PRIMARY KEY (`seckill_order_id`) USING BTREE,
                                  UNIQUE INDEX `uk_seckill_order_no`(`order_no` ASC) USING BTREE,
                                  INDEX `idx_seckill_order_customer_activity`(`customer_id` ASC, `activity_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '秒杀订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of seckill_order
-- ----------------------------
INSERT INTO `seckill_order` (`seckill_order_id`, `order_no`, `customer_id`, `activity_id`, `sku_id`, `seckill_price`, `quantity`, `status`, `create_time`, `pay_time`, `cancel_time`) VALUES (1, 'SK2020391372779114496', 1, 1, 7, 1.00, 1, '1', '2026-02-08 14:56:55', '2026-02-08 14:56:57', NULL);
INSERT INTO `seckill_order` (`seckill_order_id`, `order_no`, `customer_id`, `activity_id`, `sku_id`, `seckill_price`, `quantity`, `status`, `create_time`, `pay_time`, `cancel_time`) VALUES (2, 'SK2020392272864169984', 1, 1, 7, 1.00, 1, '2', '2026-02-08 15:00:30', NULL, '2026-02-08 15:15:30');
INSERT INTO `seckill_order` (`seckill_order_id`, `order_no`, `customer_id`, `activity_id`, `sku_id`, `seckill_price`, `quantity`, `status`, `create_time`, `pay_time`, `cancel_time`) VALUES (3, 'SK2020680841033818112', 1, 1, 7, 1.00, 1, '1', '2026-02-09 10:07:10', '2026-02-09 10:08:47', NULL);
INSERT INTO `seckill_order` (`seckill_order_id`, `order_no`, `customer_id`, `activity_id`, `sku_id`, `seckill_price`, `quantity`, `status`, `create_time`, `pay_time`, `cancel_time`) VALUES (4, 'SK2020683608775335936', 1, 1, 7, 1.00, 1, '1', '2026-02-09 10:18:09', '2026-02-09 10:18:54', NULL);

-- ----------------------------
-- Table structure for seckill_sku
-- ----------------------------
CREATE TABLE `seckill_sku`  (
                                `seckill_sku_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '秒杀SKU ID',
                                `activity_id` bigint UNSIGNED NOT NULL COMMENT '活动ID',
                                `sku_id` bigint UNSIGNED NOT NULL COMMENT 'SKU ID',
                                `seckill_price` decimal(10, 2) NOT NULL COMMENT '秒杀价',
                                `total_stock` int UNSIGNED NOT NULL COMMENT '秒杀总库存',
                                `available_stock` int UNSIGNED NOT NULL COMMENT '剩余可售库存',
                                `limit_per_customer` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '每人限购',
                                `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                PRIMARY KEY (`seckill_sku_id`) USING BTREE,
                                INDEX `idx_seckill_sku_activity`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '秒杀商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of seckill_sku
-- ----------------------------
INSERT INTO `seckill_sku` (`seckill_sku_id`, `activity_id`, `sku_id`, `seckill_price`, `total_stock`, `available_stock`, `limit_per_customer`, `order_num`, `status`, `create_time`) VALUES (1, 1, 7, 1.00, 100, 97, 1, 0, '0', '2026-02-08 14:10:37');
INSERT INTO `seckill_sku` (`seckill_sku_id`, `activity_id`, `sku_id`, `seckill_price`, `total_stock`, `available_stock`, `limit_per_customer`, `order_num`, `status`, `create_time`) VALUES (2, 1, 7, 1.00, 100, 98, 5, 0, '0', '2026-02-08 14:10:37');


-- ==============================================
-- 模块: fitai_sys (原 fitai_sys 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
                               `config_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '参数主键',
                               `config_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数名称',
                               `config_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数键名',
                               `config_value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数键值',
                               `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                               `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                               `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                               `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`config_id`) USING BTREE,
                               UNIQUE INDEX `uk_sys_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'AI默认模型', 'ai.default.model', 'qwen-plus', 'Y', '0', '0', '默认大模型', '超级管理员', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (2, 'VOD区域', 'aliyun.vod.region', 'cn-shanghai', 'Y', '0', '0', '阿里云点播区域', '超级管理员', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (3, '账号初始密码', 'sys.user.initPassword', '123456', 'Y', '0', '0', '新增用户初始密码', '超级管理员', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (4, '验证码开关', 'sys.captcha.enable', 'false', 'Y', '0', '0', '是否开启验证码', '超级管理员', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (5, 'Redis向量前缀', 'redis.vector.prefix', 'fitai:vec:', 'Y', '0', '0', 'Redis 向量键前缀', '超级管理员', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (6, 'AI功能开关', 'ai.enabled', 'true', 'Y', '0', '0', '用户端 AI 总开关；配置为 false 时关闭全站 AI 能力', '超级管理员', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (7, '非会员每月AI Token基础额度', 'ai.quota.monthly.base.tokens', '30000', 'N', '0', '0', '非会员月度基础额度（tokens），每月自动重置', '超级管理员', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (8, 'AI Green 内容安全', 'ai.green.enabled', 'false', 'Y', '0', '0', 'true 时启用阿里云 Green LLM 审核', 'admin', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (9, 'AI Langfuse 可观测', 'ai.langfuse.enabled', 'false', 'Y', '0', '0', 'true 时上报 Langfuse trace', 'admin', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (10, 'C端每日AI Token上限', 'ai.quota.daily.tokens', '10000', 'N', '0', '0', '非会员每日 Token 上限；会员读套餐 ai_quota_daily_tokens', '超级管理员', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');
INSERT INTO `sys_config` VALUES (11, 'C端每日AI对话次数上限', 'ai.quota.daily.chat.limit', '20', 'N', '0', '0', '非会员每日 LLM 调用次数；会员读套餐 ai_daily_chat_limit', '超级管理员', '2026-05-26 16:45:48', '', '2026-05-26 16:45:48');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
                             `dept_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '部门ID',
                             `parent_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '父部门ID',
                             `ancestors` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '祖级列表',
                             `dept_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
                             `order_num` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
                             `leader` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
                             `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
                             `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                             `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                             `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                             `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`dept_id`) USING BTREE,
                             INDEX `idx_sys_dept_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', 'FitAI科技', 0, '陈总', '021-58888888', 'contact@fitai.com', '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '运营中心', 1, '张运营', '13800138001', 'ops@fitai.com', '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '技术部', 2, '李技术', '13800138002', 'tech@fitai.com', '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `sys_dept` VALUES (103, 100, '0,100', 'AI产品部', 3, '赵产品', '13800138003', 'ai@fitai.com', '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
                                  `dict_code` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '字典编码',
                                  `dict_sort` int NOT NULL DEFAULT 0 COMMENT '排序',
                                  `dict_label` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典标签',
                                  `dict_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典键值',
                                  `dict_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
                                  `css_class` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '样式',
                                  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`dict_code`) USING BTREE,
                                  INDEX `idx_sys_dict_data_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 132 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (4, 1, '待支付', '0', 'ord_order_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (5, 2, '已支付', '1', 'ord_order_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (6, 3, '已发货', '2', 'ord_order_status', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (7, 4, '已完成', '3', 'ord_order_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (8, 5, '已取消', '4', 'ord_order_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (9, 1, '普通会员', 'normal', 'mem_level_code', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (10, 2, '黄金会员', 'gold', 'mem_level_code', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (11, 3, '钻石会员', 'diamond', 'mem_level_code', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (12, 1, '初级', '1', 'crs_difficulty', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (13, 2, '中级', '2', 'crs_difficulty', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (14, 3, '高级', '3', 'crs_difficulty', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (15, 1, '微信支付', 'wechat', 'ord_pay_channel', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (16, 2, '支付宝', 'alipay', 'ord_pay_channel', NULL, '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (17, 1, '已上架', '0', 'crs_course_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (18, 2, '已下架', '1', 'crs_course_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (19, 3, '草稿', '2', 'crs_course_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (20, 1, '普通', 'normal', 'community_post_type', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (21, 2, '打卡', 'checkin', 'community_post_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (22, 3, '挑战', 'challenge', 'community_post_type', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (23, 1, '正常', '0', 'sys_normal_disable', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (24, 2, '停用', '1', 'sys_normal_disable', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (25, 1, '否', '0', 'sys_yes_no', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (26, 2, '是', '1', 'sys_yes_no', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (27, 1, '其他', '0', 'sys_oper_type', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (28, 2, '新增', '1', 'sys_oper_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (29, 3, '修改', '2', 'sys_oper_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (30, 4, '删除', '3', 'sys_oper_type', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (31, 5, '查询', '4', 'sys_oper_type', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (32, 1, '成功', '0', 'sys_oper_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (33, 2, '失败', '1', 'sys_oper_status', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (34, 1, '失败', '0', 'sys_login_status', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (35, 2, '成功', '1', 'sys_login_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (36, 1, '管理端', 'admin', 'sys_client_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (37, 2, 'PC用户端', 'pc', 'sys_client_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (38, 3, '移动端', 'mobile', 'sys_client_type', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (39, 1, '公告', '1', 'sys_notice_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (40, 2, '推送', '2', 'sys_notice_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (41, 1, '草稿', '0', 'sys_notice_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (42, 2, '已发布', '1', 'sys_notice_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (43, 3, '已撤回', '2', 'sys_notice_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (44, 1, '未开始', '0', 'seckill_activity_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (45, 2, '进行中', '1', 'seckill_activity_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (46, 3, '已结束', '2', 'seckill_activity_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (47, 1, '待支付', '0', 'seckill_order_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (48, 2, '已支付', '1', 'seckill_order_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (49, 3, '已取消', '2', 'seckill_order_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (50, 1, '待支付', '0', 'mem_order_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (51, 2, '已支付', '1', 'mem_order_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (52, 3, '已取消', '2', 'mem_order_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (53, 4, '已退款', '3', 'mem_order_status', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (54, 1, '生效中', '1', 'mem_subscription_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (55, 2, '已过期', '2', 'mem_subscription_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (56, 3, '已取消', '3', 'mem_subscription_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (57, 1, '未使用', '0', 'mkt_coupon_receive_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (58, 2, '已使用', '1', 'mkt_coupon_receive_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (59, 3, '已过期', '2', 'mkt_coupon_receive_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (60, 1, '平板支撑', 'plank', 'mkt_challenge_type', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (61, 2, '俯卧撑', 'pushup', 'mkt_challenge_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (62, 3, '跑步', 'running', 'mkt_challenge_type', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (63, 4, '打卡', 'checkin', 'mkt_challenge_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (64, 1, '首页', 'home', 'mkt_banner_position', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (65, 2, '课程', 'course', 'mkt_banner_position', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (66, 3, '商城', 'product', 'mkt_banner_position', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (67, 1, '跟练', '1', 'usr_coach_bind_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (68, 2, '私教', '2', 'usr_coach_bind_type', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (69, 1, '有效', '0', 'usr_coach_bind_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (70, 2, '已解绑', '1', 'usr_coach_bind_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (71, 1, '待确认', '0', 'usr_coach_booking_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (72, 2, '已确认', '1', 'usr_coach_booking_status', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (73, 3, '已完成', '2', 'usr_coach_booking_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (74, 4, '已取消', '3', 'usr_coach_booking_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (75, 1, '蓝牙', 'bluetooth', 'train_connect_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (76, 2, 'WiFi', 'wifi', 'train_connect_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (77, 3, 'ANT+', 'ant', 'train_connect_type', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (78, 1, '草稿', '0', 'train_plan_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (79, 2, '进行中', '1', 'train_plan_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (80, 3, '已完成', '2', 'train_plan_status', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (81, 4, '已放弃', '3', 'train_plan_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (82, 1, '早餐', 'breakfast', 'diet_meal_type', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (83, 2, '午餐', 'lunch', 'diet_meal_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (84, 3, '晚餐', 'dinner', 'diet_meal_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (85, 4, '加餐', 'snack', 'diet_meal_type', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (86, 1, '动态', 'post', 'community_target_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (87, 2, '评论', 'comment', 'community_target_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (88, 1, '待审', '0', 'community_audit_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (89, 2, '通过', '1', 'community_audit_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (90, 3, '拒绝', '2', 'community_audit_status', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (91, 1, '学习中', '0', 'crs_learn_finish', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (92, 2, '已完成', '1', 'crs_learn_finish', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (93, 1, '待审核', '0', 'pay_refund_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (94, 2, '已同意', '1', 'pay_refund_status', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (95, 3, '已拒绝', '2', 'pay_refund_status', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (96, 4, '已退款', '3', 'pay_refund_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (97, 1, '获得', 'earn', 'mem_points_change_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (98, 2, '消耗', 'spend', 'mem_points_change_type', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (99, 3, '过期', 'expire', 'mem_points_change_type', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (100, 1, '签到', 'checkin', 'mem_points_biz_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (101, 2, '徽章', 'badge', 'mem_points_biz_type', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (102, 3, '打卡', 'sign', 'mem_points_biz_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (103, 4, '训练', 'train', 'mem_points_biz_type', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (104, 5, '订单', 'order', 'mem_points_biz_type', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (105, 6, '课程', 'course', 'mem_points_biz_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (106, 1, '全局', 'global', 'ai_prompt_scene', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (107, 2, '训练计划', 'training', 'ai_prompt_scene', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (108, 3, '饮食方案', 'diet', 'ai_prompt_scene', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (109, 4, '闲聊', 'chat', 'ai_prompt_scene', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (110, 5, '训练点评', 'review', 'ai_prompt_scene', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (111, 6, '知识库问答', 'knowledge', 'ai_prompt_scene', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (112, 1, '关键词', 'keyword', 'ai_guardrail_rule_type', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (113, 2, '正则', 'regex', 'ai_guardrail_rule_type', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (114, 3, '长度限制', 'length', 'ai_guardrail_rule_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (115, 4, '医疗提示', 'medical', 'ai_guardrail_rule_type', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (116, 5, '免责声明', 'disclaimer', 'ai_guardrail_rule_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (117, 1, '模型评测', 'eval', 'ai_dataset_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (118, 2, 'RAG评估', 'rag', 'ai_dataset_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (119, 1, '正常', '0', 'ai_vector_index_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (120, 2, '重建中', '1', 'ai_vector_index_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (121, 3, '异常', '2', 'ai_vector_index_status', 'danger', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (122, 1, '进行中', '0', 'ai_session_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (123, 2, '已结束', '1', 'ai_session_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (124, 3, '已归档', '2', 'ai_session_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (125, 1, '对话', 'chat', 'ai_session_scene', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (126, 2, '训练计划', 'train_plan', 'ai_session_scene', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (127, 3, '饮食方案', 'diet_plan', 'ai_session_scene', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (128, 4, '训练点评', 'review', 'ai_session_scene', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (129, 1, 'function', 'function', 'ai_tool_type', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (130, 2, 'mcp', 'mcp', 'ai_tool_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (131, 3, 'http', 'http', 'ai_tool_type', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (132, 1, '待发货', '0', 'ord_logistics_status', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (133, 2, '已发货', '1', 'ord_logistics_status', 'warning', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (134, 3, '运输中', '2', 'ord_logistics_status', 'primary', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (135, 4, '已签收', '3', 'ord_logistics_status', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (136, 1, '销售属性', '1', 'prd_attr_type', 'success', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');
INSERT INTO `sys_dict_data` VALUES (137, 2, '基本属性', '2', 'prd_attr_type', 'info', '0', '0', NULL, '超级管理员', '2026-05-26 16:47:10', '', '2026-05-26 16:47:10');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
                                  `dict_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '字典主键',
                                  `dict_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
                                  `dict_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
                                  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`dict_id`) USING BTREE,
                                  UNIQUE INDEX `uk_sys_dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', '0', 'C端用户与后台账号通用', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (2, '订单状态', 'ord_order_status', '0', '0', '商品订单状态', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (3, '会员等级', 'mem_level_code', '0', '0', '会员等级编码', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (4, '课程难度', 'crs_difficulty', '0', '0', '1初级 2中级 3高级', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (5, '支付渠道', 'ord_pay_channel', '0', '0', 'wechat/alipay/apple', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (6, '课程状态', 'crs_course_status', '0', '0', '0上架 1下架 2草稿', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (7, '社区动态类型', 'community_post_type', '0', '0', 'normal普通 checkin打卡 challenge挑战', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (8, '系统启停', 'sys_normal_disable', '0', '0', '通用 0正常 1停用', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (9, '是否', 'sys_yes_no', '0', '0', '通用 0否 1是', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (10, '操作类型', 'sys_oper_type', '0', '0', '0其他 1新增 2修改 3删除 4查询', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (11, '操作结果', 'sys_oper_status', '0', '0', '0成功 1失败', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (12, '登录结果', 'sys_login_status', '0', '0', '0失败 1成功', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (13, '客户端类型', 'sys_client_type', '0', '0', 'admin/pc/mobile', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (14, '通知类型', 'sys_notice_type', '0', '0', '1公告 2推送', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (15, '通知状态', 'sys_notice_status', '0', '0', '0草稿 1已发布 2已撤回', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (16, '秒杀活动状态', 'seckill_activity_status', '0', '0', '0未开始 1进行中 2已结束', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (17, '秒杀订单状态', 'seckill_order_status', '0', '0', '0待支付 1已支付 2已取消', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (18, '会员订单状态', 'mem_order_status', '0', '0', '0待支付 1已支付 2已取消 3已退款', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (19, '会员订阅状态', 'mem_subscription_status', '0', '0', '1生效中 2已过期 3已取消', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (20, '优惠券领取状态', 'mkt_coupon_receive_status', '0', '0', '0未使用 1已使用 2已过期', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (21, '挑战赛类型', 'mkt_challenge_type', '0', '0', 'plank/pushup/running/checkin', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (22, '轮播位置', 'mkt_banner_position', '0', '0', 'home/course/product', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (23, '教练绑定类型', 'usr_coach_bind_type', '0', '0', '1跟练 2私教', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (24, '教练绑定状态', 'usr_coach_bind_status', '0', '0', '0有效 1已解绑', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (25, '教练预约状态', 'usr_coach_booking_status', '0', '0', '0待确认 1已确认 2已完成 3已取消', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (26, '设备连接方式', 'train_connect_type', '0', '0', 'bluetooth/wifi/ant', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (27, '训练计划状态', 'train_plan_status', '0', '0', '0草稿 1进行中 2已完成 3已放弃', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (28, '餐次', 'diet_meal_type', '0', '0', 'breakfast/lunch/dinner/snack', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (29, '社区审核对象', 'community_target_type', '0', '0', 'post动态 comment评论', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (30, '社区审核状态', 'community_audit_status', '0', '0', '0待审 1通过 2拒绝', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (31, '学习完成状态', 'crs_learn_finish', '0', '0', '0学习中 1已完成', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (32, '退款审核状态', 'pay_refund_status', '0', '0', '0待审核 1已同意 2已拒绝 3已退款', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (33, '积分变动类型', 'mem_points_change_type', '0', '0', 'earn获得 spend消耗 expire过期', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (34, '积分业务来源', 'mem_points_biz_type', '0', '0', 'checkin/badge/sign/train/order/course', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (35, '提示词场景', 'ai_prompt_scene', '0', '0', 'global/training/diet/chat/review/knowledge', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (36, '护栏规则类型', 'ai_guardrail_rule_type', '0', '0', 'keyword/regex/length/medical/disclaimer', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (37, '数据集类型', 'ai_dataset_type', '0', '0', 'eval/rag', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (38, '向量索引状态', 'ai_vector_index_status', '0', '0', '0正常 1重建中 2异常', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (39, 'AI会话状态', 'ai_session_status', '0', '0', '0进行中 1已结束 2已归档', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (40, 'AI会话场景', 'ai_session_scene', '0', '0', 'chat/train_plan/diet_plan/review', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (41, 'AI工具类型', 'ai_tool_type', '0', '0', 'function/mcp/http', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (42, '物流状态', 'ord_logistics_status', '0', '0', '0待发货 1已发货 2运输中 3已签收', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');
INSERT INTO `sys_dict_type` VALUES (43, '商品属性类型', 'prd_attr_type', '0', '0', '1销售 2基本', '超级管理员', '2026-05-26 16:46:56', '', '2026-05-26 16:46:56');

-- ----------------------------
-- Table structure for sys_entry_daily
-- ----------------------------
DROP TABLE IF EXISTS `sys_entry_daily`;
CREATE TABLE `sys_entry_daily`  (
                                    `stat_date` date NOT NULL COMMENT '统计日期',
                                    `channel` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道 pc/mobile',
                                    `visit_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '访问次数',
                                    PRIMARY KEY (`stat_date`, `channel`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'C端入口日统计' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_entry_daily
-- ----------------------------
INSERT INTO `sys_entry_daily` VALUES ('2026-05-19', 'mobile', 256);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-19', 'pc', 128);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-20', 'mobile', 301);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-20', 'pc', 142);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-21', 'mobile', 288);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-21', 'pc', 156);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-22', 'mobile', 275);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-22', 'pc', 134);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-23', 'mobile', 312);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-23', 'pc', 168);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-24', 'mobile', 329);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-24', 'pc', 175);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-25', 'mobile', 196);
INSERT INTO `sys_entry_daily` VALUES ('2026-05-25', 'pc', 89);

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
                            `job_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务ID',
                            `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
                            `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
                            `invoke_target` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
                            `cron_expression` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'cron表达式',
                            `misfire_policy` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '3' COMMENT '计划策略（1立即 2执行一次 3放弃）',
                            `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '并发（0允许 1禁止）',
                            `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                            `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                            `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                            `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`job_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (1, '同步VOD转码状态', 'DEFAULT', 'vodTask.syncTranscodeStatus()', '0 0/5 * * * ?', '3', '1', '0', '0', '每5分钟拉取阿里云VOD转码进度', '超级管理员', '2026-05-26 16:46:37', '', '2026-05-26 16:46:37');
INSERT INTO `sys_job` VALUES (2, '会员到期提醒', 'DEFAULT', 'memTask.notifyExpireSoon()', '0 0 9 * * ?', '3', '1', '0', '0', '到期前3天推送续费提醒', '超级管理员', '2026-05-26 16:46:37', '', '2026-05-26 16:46:37');
INSERT INTO `sys_job` VALUES (3, 'AI成本日汇总', 'DEFAULT', 'aiTask.aggregateDailyCost()', '0 30 1 * * ?', '3', '1', '0', '0', '凌晨汇总前日Token消耗', '超级管理员', '2026-05-26 16:46:37', '', '2026-05-26 16:46:37');
INSERT INTO `sys_job` VALUES (4, 'AI月额度归档', 'DEFAULT', 'aiTask.resetMonthlyQuota()', '0 10 0 1 * ?', '3', '1', '0', '0', '每月清理上月 ai_customer_quota 缓存行', '超级管理员', '2026-05-26 16:46:37', '', '2026-05-26 16:46:37');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
                                `job_log_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
                                `job_id` bigint UNSIGNED NOT NULL COMMENT '任务ID',
                                `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
                                `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
                                `invoke_target` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标',
                                `job_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志信息',
                                `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1失败）',
                                `exception_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '异常信息',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                PRIMARY KEY (`job_log_id`) USING BTREE,
                                INDEX `idx_sys_job_log_job`(`job_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
INSERT INTO `sys_job_log` VALUES (1, 1, '同步VOD转码状态', 'DEFAULT', 'vodTask.syncTranscodeStatus()', '执行成功，同步 3 条媒资', '0', NULL, '2025-05-22 08:00:00');

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
                                  `log_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
                                  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录账号(管理端user_name/C端mobile)',
                                  `user_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'admin' COMMENT '用户类型 admin/customer',
                                  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录IP',
                                  `login_location` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录地点',
                                  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '浏览器',
                                  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作系统',
                                  `client_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端 admin/app/mobile/pc',
                                  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '状态（0失败 1成功）',
                                  `msg` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提示消息',
                                  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
                                  PRIMARY KEY (`log_id`) USING BTREE,
                                  INDEX `idx_sys_login_log_time`(`login_time` ASC) USING BTREE,
                                  INDEX `idx_sys_login_log_user`(`user_name` ASC, `login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '登录日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
INSERT INTO `sys_login_log` VALUES (1, 'admin', 'admin', '127.0.0.1', '局域网', 'Chrome', 'Windows 10', 'admin', '1', '登录成功', '2025-05-20 09:00:00');
INSERT INTO `sys_login_log` VALUES (2, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10', 'admin', '0', '用户或密码不正确', '2025-05-20 09:01:12');
INSERT INTO `sys_login_log` VALUES (3, '13800000001', 'customer', '127.0.0.1', '局域网', 'Safari', 'iPhone', 'app', '1', '登录成功', '2025-05-22 08:20:00');
INSERT INTO `sys_login_log` VALUES (4, 'operator', 'admin', '192.168.1.105', '内网', 'Chrome', 'Windows 10', 'admin', '1', '登录成功', '2025-05-21 14:00:00');
INSERT INTO `sys_login_log` VALUES (5, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '0', '用户或密码不正确', '2026-05-25 15:12:02');
INSERT INTO `sys_login_log` VALUES (6, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 15:12:11');
INSERT INTO `sys_login_log` VALUES (7, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 15:55:58');
INSERT INTO `sys_login_log` VALUES (8, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 15:56:19');
INSERT INTO `sys_login_log` VALUES (9, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 16:02:35');
INSERT INTO `sys_login_log` VALUES (10, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 16:02:50');
INSERT INTO `sys_login_log` VALUES (11, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 16:23:21');
INSERT INTO `sys_login_log` VALUES (12, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 16:35:59');
INSERT INTO `sys_login_log` VALUES (13, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 16:36:03');
INSERT INTO `sys_login_log` VALUES (14, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 16:36:07');
INSERT INTO `sys_login_log` VALUES (15, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 16:36:12');
INSERT INTO `sys_login_log` VALUES (16, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 16:36:17');
INSERT INTO `sys_login_log` VALUES (17, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Unknown', 'admin', '1', '登录成功', '2026-05-25 16:36:24');
INSERT INTO `sys_login_log` VALUES (18, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Unknown', 'admin', '1', '登录成功', '2026-05-25 16:36:31');
INSERT INTO `sys_login_log` VALUES (19, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 17:58:22');
INSERT INTO `sys_login_log` VALUES (20, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 19:10:51');
INSERT INTO `sys_login_log` VALUES (21, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 19:10:55');
INSERT INTO `sys_login_log` VALUES (22, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-25 19:10:59');
INSERT INTO `sys_login_log` VALUES (23, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Unknown', 'admin', '1', '登录成功', '2026-05-25 19:11:08');
INSERT INTO `sys_login_log` VALUES (24, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Unknown', 'admin', '1', '登录成功', '2026-05-25 19:11:15');
INSERT INTO `sys_login_log` VALUES (25, 'admin', 'admin', '127.0.0.1', '局域网', 'Unknown', 'Unknown', 'admin', '1', '登录成功', '2026-05-25 19:12:23');
INSERT INTO `sys_login_log` VALUES (26, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-26 08:23:33');
INSERT INTO `sys_login_log` VALUES (27, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-26 14:10:05');
INSERT INTO `sys_login_log` VALUES (28, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-26 14:39:36');
INSERT INTO `sys_login_log` VALUES (29, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-26 14:40:20');
INSERT INTO `sys_login_log` VALUES (30, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-26 14:41:22');
INSERT INTO `sys_login_log` VALUES (31, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-26 15:35:16');
INSERT INTO `sys_login_log` VALUES (32, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Android', 'admin', '1', '登录成功', '2026-05-27 09:40:15');
INSERT INTO `sys_login_log` VALUES (33, 'operator', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Android', 'admin', '1', '登录成功', '2026-05-27 09:44:52');
INSERT INTO `sys_login_log` VALUES (34, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Android', 'admin', '0', '用户或密码不正确', '2026-05-27 09:45:09');
INSERT INTO `sys_login_log` VALUES (35, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Android', 'admin', '1', '登录成功', '2026-05-27 09:45:12');
INSERT INTO `sys_login_log` VALUES (36, 'operator', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-27 10:26:29');
INSERT INTO `sys_login_log` VALUES (37, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-05-27 10:30:48');
INSERT INTO `sys_login_log` VALUES (38, 'admin', 'admin', '127.0.0.1', '局域网', 'MSEdge', 'Windows 10 or Windows Server 2016', 'admin', '1', '登录成功', '2026-06-02 17:59:17');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
                             `menu_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                             `menu_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
                             `parent_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '父菜单ID',
                             `ancestors` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '祖级列表',
                             `order_num` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
                             `path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由地址',
                             `component` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
                             `route_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由名称',
                             `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型（M目录 C菜单 F按钮）',
                             `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '显示（0显示 1隐藏）',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                             `perms` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
                             `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标（Element 图标类名）',
                             `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                             `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                             `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`menu_id`) USING BTREE,
                             INDEX `idx_sys_menu_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106205 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '首页', 0, '0', 0, '/home/index', '/home/index', 'home', 'C', '0', '0', 'system:dashboard:view', 'House', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10, '用户与会员', 0, '0', 1, '/user', NULL, 'User', 'M', '0', '0', NULL, 'UserFilled', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11, 'C端用户', 10, '0,10', 1, '/user', '/user/index', 'user', 'C', '0', '0', 'usr:customer:list', 'User', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (12, '体测数据', 10, '0,10', 2, '/user/body', '/user/body/index', 'userBody', 'C', '0', '0', 'usr:body:list', 'DataAnalysis', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (13, '教练管理', 10, '0,10', 3, '/coach', '/coach/index', 'coach', 'C', '0', '0', 'usr:coach:list', 'Avatar', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (14, '用户分群', 10, '0,10', 4, '/user/segment', '/user/segment/index', 'userSegment', 'C', '0', '0', 'usr:segment:list', 'CollectionTag', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (15, '会员套餐', 10, '0,10', 5, '/member/plan', '/member/plan/index', 'memberPlan', 'C', '0', '0', 'vip:plan:list', 'Medal', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (16, '会员等级', 10, '0,10', 6, '/member/level', '/member/level/index', 'memberLevel', 'C', '0', '0', 'mem:level:list', 'Rank', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (17, '会员权益', 10, '0,10', 7, '/member/benefit', '/member/benefit/index', 'memberBenefit', 'C', '0', '0', 'vip:benefit:list', 'Star', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (18, '积分体系', 10, '0,10', 8, '/member/points-log', '/member/points-log/index', 'memberPointsLog', 'C', '0', '0', 'mem:points-log:list', 'Trophy', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (19, '约课审核', 10, '0,10', 9, '/coach/booking', '/coach/booking/index', 'coachBooking', 'C', '0', '0', 'usr:booking:list', 'Calendar', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (115, '会员订阅', 10, '0,10', 10, '/member/subscription', '/member/subscription/index', 'memberSubscription', 'C', '0', '0', 'vip:subscription:list', 'Ticket', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (116, '徽章管理', 10, '0,10', 11, '/member/badge', '/member/badge/index', 'memberBadge', 'C', '0', '0', 'mem:badge:list', 'Medal', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (117, '签到记录', 10, '0,10', 12, '/member/checkin', '/member/checkin/index', 'memberCheckin', 'C', '0', '0', 'mem:checkin:list', 'Calendar', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (118, '积分规则', 10, '0,10', 13, '/member/points-rule', '/member/points-rule/index', 'memberPointsRule', 'C', '0', '0', 'mem:points-rule:list', 'SetUp', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (119, '教练绑定', 10, '0,10', 14, '/coach/bind', '/coach/bind/index', 'coachBind', 'C', '0', '0', 'usr:coach:list', 'Link', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (120, '教练排班', 10, '0,10', 15, '/coach/schedule', '/coach/schedule/index', 'coachSchedule', 'C', '0', '0', 'usr:coach:query', 'Clock', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (123, '用户地址', 10, '0,10', 16, '/user/address', '/user/address/index', 'userAddress', 'C', '0', '0', 'usr:address:list', 'Location', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (124, '用户设置', 10, '0,10', 17, '/user/setting', '/user/setting/index', 'userSetting', 'C', '0', '0', 'usr:setting:list', 'Setting', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (20, '内容与课程', 0, '0', 2, '/course', NULL, 'Course', 'M', '0', '0', NULL, 'VideoPlay', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (24, '图片中心', 20, '0,20', 1, '/media/library', '/media/library/index', 'MediaLibrary', 'C', '0', '0', 'media:asset:list', 'Picture', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (23, '视频中心', 20, '0,20', 2, '/course/video', '/course/video/index', 'courseVideo', 'C', '0', '0', 'crs:video:list', 'Film', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (21, '课程分类', 20, '0,20', 3, '/course/category', '/course/category/index', 'courseCategory', 'C', '0', '0', 'crs:category:list', 'Files', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (22, '课程列表', 20, '0,20', 4, '/course/list', '/course/list/index', 'CourseList', 'C', '0', '0', 'crs:course:list', 'Tickets', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (27, '学习进度', 20, '0,20', 5, '/course/progress', '/course/progress/index', 'courseProgress', 'C', '0', '0', 'crs:progress:list', 'DataLine', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (25, '社区动态', 20, '0,20', 6, '/community/post', '/community/post/index', 'communityPost', 'C', '0', '0', 'community:post:list', 'ChatLineRound', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (26, '内容审核', 20, '0,20', 7, '/community/moderation', '/community/moderation/index', 'CommunityModeration', 'C', '0', '0', 'community:audit:list', 'Warning', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (28, '社区评论', 20, '0,20', 8, '/community/comment', '/community/comment/index', 'communityComment', 'C', '0', '0', 'community:comment:list', 'ChatDotRound', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (30, '训练管理', 0, '0', 3, '/training', NULL, 'Training', 'M', '0', '0', NULL, 'Position', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (31, '动作库', 30, '0,30', 1, '/training/exercise', '/training/exercise/index', 'trainingExercise', 'C', '0', '0', 'train:exercise:list', 'Position', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (32, '训练计划', 30, '0,30', 2, '/training/plan', '/training/plan/index', 'trainingPlan', 'C', '0', '0', 'train:plan:list', 'Calendar', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (33, '训练记录', 30, '0,30', 3, '/training/record', '/training/record/index', 'trainingRecord', 'C', '0', '0', 'train:record:list', 'Tickets', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (34, '穿戴设备', 30, '0,30', 4, '/training/device', '/training/device/index', 'trainingDevice', 'C', '0', '0', 'train:device:list', 'Watch', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (40, '饮食管理', 0, '0', 4, '/diet', NULL, 'Diet', 'M', '0', '0', NULL, 'Food', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (41, '饮食分类', 40, '0,40', 1, '/diet/food-category', '/diet/food-category/index', 'dietFoodCategory', 'C', '0', '0', 'diet:food-category:list', 'FolderOpened', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (42, '食物库', 40, '0,40', 2, '/diet/food', '/diet/food/index', 'dietFood', 'C', '0', '0', 'diet:food:list', 'ShoppingBag', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (43, '饮食方案', 40, '0,40', 3, '/diet/meal-plan', '/diet/meal-plan/index', 'dietMealPlan', 'C', '0', '0', 'diet:meal-plan:list', 'Calendar', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (44, '饮食记录', 40, '0,40', 4, '/diet/record', '/diet/record/index', 'dietRecord', 'C', '0', '0', 'diet:record:list', 'Tickets', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (50, 'AI 管理', 0, '0', 5, '/ai', NULL, 'Ai', 'M', '0', '0', NULL, 'Cpu', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (51, 'AI 看板', 5004, '0,50,5004', 1, '/ai/stats', '/ai/stats/index', 'aiStats', 'C', '0', '0', 'ai:stats:list', 'Histogram', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (52, '知识库管理', 5002, '0,50,5002', 2, '/ai/knowledge', '/ai/knowledge/index', 'aiKnowledge', 'C', '0', '0', 'ai:knowledge:list', 'Document', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-07-06 15:40:07');
INSERT INTO `sys_menu` VALUES (53, '对话记录', 5001, '0,50,5001', 1, '/ai/conversation', '/ai/conversation/index', 'aiConversation', 'C', '1', '0', 'ai:conversation:list', 'ChatDotRound', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (54, '模型配置', 5003, '0,50,5003', 1, '/ai/model', '/ai/model/index', 'aiModel', 'C', '0', '0', 'ai:model:list', 'Setting', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (55, '提示词管理', 5001, '0,50,5001', 3, '/ai/prompt', '/ai/prompt/index', 'aiPrompt', 'C', '0', '0', 'ai:prompt:list', 'EditPen', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (56, 'RAG 知识库评估', 5002, '0,50,5002', 3, '/ai/rag-eval', '/ai/rag-eval/index', 'aiRagEval', 'C', '0', '0', 'ai:rag-eval:list', 'MagicStick', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (57, '方案审核', 5001, '0,50,5001', 4, '/ai/plan-review', '/ai/plan-review/index', 'aiPlanReview', 'C', '0', '0', 'ai:plan-review:list', 'View', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (58, '会话管理', 5001, '0,50,5001', 2, '/ai/session', '/ai/session/index', 'aiSession', 'C', '0', '0', 'ai:session:list', 'ChatDotRound', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (61, 'Agent 编排', 5003, '0,50,5003', 3, '/ai/agent', '/ai/agent/index', 'aiAgent', 'C', '0', '0', 'ai:agent:list', 'Connection', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (62, '工具管理', 5003, '0,50,5003', 4, '/ai/tool', '/ai/tool/index', 'aiTool', 'C', '0', '0', 'ai:tool:list', 'Tools', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (63, '数据集', 5002, '0,50,5002', 4, '/ai/dataset', '/ai/dataset/index', 'aiDataset', 'C', '0', '0', 'ai:dataset:list', 'Folder', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (64, '模型评测（问答集）', 5003, '0,50,5003', 2, '/ai/evaluation', '/ai/evaluation/index', 'aiEvaluation', 'C', '0', '0', 'ai:evaluation:list', 'TrendCharts', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (65, '成本管控', 50, '0,50', 5, '/ai/cost-group', NULL, 'AiCostGroup', 'M', '0', '0', NULL, 'Coin', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (66, '内容护栏', 5004, '0,50,5004', 4, '/ai/guardrail', '/ai/guardrail/index', 'aiGuardrail', 'C', '0', '0', 'ai:guardrail:list', 'Lock', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (67, '向量库管理', 5002, '0,50,5002', 1, '/ai/vector-index', '/ai/vector-index/index', 'aiVectorIndex', 'C', '0', '0', 'ai:vector-index:list', 'Share', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-07-06 15:40:30');
INSERT INTO `sys_menu` VALUES (68, '链路追踪', 5004, '0,50,5004', 3, '/ai/trace', '/ai/trace/index', 'aiTrace', 'C', '0', '0', 'ai:trace:list', 'Monitor', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (70, '商城管理', 0, '0', 7, '/product', NULL, 'Product', 'M', '0', '0', NULL, 'ShoppingCart', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (71, '商品分类', 70, '0,70', 1, '/product/category', '/product/category/index', 'productCategory', 'C', '0', '0', 'prd:category:list', 'Files', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (72, '品牌管理', 70, '0,70', 2, '/product/brand', '/product/brand/index', 'productBrand', 'C', '0', '0', 'prd:brand:list', 'PriceTag', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (73, '商品列表', 70, '0,70', 4, '/product/list', '/product/list/index', 'productList', 'C', '0', '0', 'prd:product:list', 'Goods', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (74, '库存管理', 70, '0,70', 5, '/product/stock', '/product/stock/index', 'productStock', 'C', '0', '0', 'prd:stock:list', 'Box', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (75, '销售属性', 70, '0,70', 3, '/product/attr', '/product/attr/index', 'productAttr', 'C', '0', '0', 'prd:attr:list', 'Operation', '0', '超级管理员', '2025-05-20 10:00:00', '', '2025-05-20 10:00:00');
INSERT INTO `sys_menu` VALUES (76, '搜索索引(ES)', 70, '0,70', 6, '/product/es', '/product/es/index', 'productEs', 'C', '0', '0', 'prd:es:list', 'Search', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (80, '订单中心', 0, '0', 8, '/order', NULL, 'Order', 'M', '0', '0', NULL, 'List', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (81, '商品订单', 80, '0,80', 1, '/order', '/order/index', 'order', 'C', '0', '0', 'ord:order:list', 'Tickets', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (82, '会员订单', 80, '0,80', 2, '/member/order', '/member/order/index', 'memberOrder', 'C', '0', '0', 'vip:order:list', 'Document', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (83, '售后退款', 80, '0,80', 3, '/pay/refund', '/pay/refund/index', 'payRefund', 'C', '0', '0', 'pay:refund:list', 'Ticket', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (84, '支付渠道', 80, '0,80', 4, '/pay/channel', '/pay/channel/index', 'payChannel', 'C', '0', '0', 'pay:channel:list', 'Wallet', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (85, '物流管理', 80, '0,80', 5, '/order/logistics', '/order/logistics/index', 'orderLogistics', 'C', '0', '0', 'ord:logistics:list', 'Van', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (90, '营销管理', 0, '0', 9, '/marketing', NULL, 'Marketing', 'M', '0', '0', NULL, 'Picture', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (91, '轮播图管理', 90, '0,90', 1, '/marketing/banner', '/marketing/banner/index', 'marketingBanner', 'C', '0', '0', 'mkt:banner:list', 'Picture', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (92, '优惠券管理', 90, '0,90', 2, '/marketing/coupon', '/marketing/coupon/index', 'marketingCoupon', 'C', '0', '0', 'mkt:coupon:list', 'Ticket', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (93, '秒杀活动', 90, '0,90', 3, '/seckill/activity', '/seckill/activity/index', 'seckillActivity', 'C', '0', '0', 'seckill:activity:list', 'Odometer', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (94, '挑战打卡', 90, '0,90', 4, '/marketing/challenge', '/marketing/challenge/index', 'marketingChallenge', 'C', '0', '0', 'mkt:challenge:list', 'Trophy', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (95, '领券记录', 90, '0,90', 5, '/marketing/coupon-receive', '/marketing/coupon-receive/index', 'marketingCouponReceive', 'C', '0', '0', 'mkt:coupon-receive:list', 'Tickets', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (96, '秒杀订单', 90, '0,90', 6, '/seckill/order', '/seckill/order/index', 'seckillOrder', 'C', '0', '0', 'seckill:order:list', 'ShoppingCart', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (100, '系统管理', 0, '0', 10, '/system', NULL, 'System', 'M', '0', '0', NULL, 'Setting', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (101, '账号管理', 100, '0,100', 1, '/system/account', '/system/account/index', 'SystemAccount', 'C', '0', '0', 'system:user:list', 'User', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (102, '角色管理', 100, '0,100', 2, '/system/role', '/system/role/index', 'SystemRole', 'C', '0', '0', 'system:role:list', 'UserFilled', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (103, '菜单管理', 100, '0,100', 3, '/system/menu', '/system/menu/index', 'SystemMenu', 'C', '0', '0', 'system:menu:list', 'Grid', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (104, '部门管理', 100, '0,100', 4, '/system/department', '/system/department/index', 'SystemDepartment', 'C', '0', '0', 'system:dept:list', 'OfficeBuilding', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 100, '0,100', 5, '/system/dict', '/system/dict/index', 'SystemDict', 'C', '0', '0', 'system:dict:list', 'Notebook', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (106, '日志管理', 100, '0,100', 6, '/system/logs', NULL, 'SystemLogs', 'M', '0', '0', NULL, 'Document', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (107, '任务调度(XXL-Job)', 100, '0,100', 7, '/system/task', '/system/task/index', 'SystemTask', 'C', '0', '0', 'system:job:list', 'Clock', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (108, '系统配置', 100, '0,100', 8, '/system/config', '/system/config/index', 'SystemConfig', 'C', '0', '0', 'system:config:list', 'Setting', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (109, '消息管理', 100, '0,100', 9, '/system/notice', '/system/notice/index', 'SystemNotice', 'C', '0', '0', 'system:notice:list', 'Bell', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (659, '成本总览', 65, '0,50,65', 1, '/ai/cost', '/ai/cost/index', 'aiCost', 'C', '0', '0', 'ai:cost:list', 'Coin', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (691, '用户额度', 65, '0,50,65', 2, '/ai/quota', '/ai/quota/index', 'aiQuota', 'C', '0', '0', 'ai:quota:list', 'User', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (692, 'Token 流水', 5004, '0,50,5004', 2, '/ai/token-usage', '/ai/token-usage/index', 'aiTokenUsage', 'C', '0', '0', 'ai:token-usage:list', 'Tickets', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1061, '登录日志', 106, '0,100,106', 1, '/system/login-log', '/system/log/index', 'SystemLoginLog', 'C', '0', '0', 'system:login-log:list', 'User', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1062, '操作日志', 106, '0,100,106', 2, '/system/oper-log', '/system/log/index', 'SystemOperLog', 'C', '0', '0', 'system:oper-log:list', 'Tickets', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1101, '查询', 11, '0,10,11', 1, NULL, NULL, NULL, 'F', '1', '0', 'usr:customer:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1102, '新增', 11, '0,10,11', 2, NULL, NULL, NULL, 'F', '1', '0', 'usr:customer:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1103, '修改', 11, '0,10,11', 3, NULL, NULL, NULL, 'F', '1', '0', 'usr:customer:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1104, '删除', 11, '0,10,11', 4, NULL, NULL, NULL, 'F', '1', '0', 'usr:customer:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1105, '导出', 11, '0,10,11', 5, NULL, NULL, NULL, 'F', '1', '0', 'usr:customer:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1201, '查询', 12, '0,10,12', 1, NULL, NULL, NULL, 'F', '1', '0', 'usr:body:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1202, '导出', 12, '0,10,12', 2, NULL, NULL, NULL, 'F', '1', '0', 'usr:body:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1301, '查询', 13, '0,10,13', 1, NULL, NULL, NULL, 'F', '1', '0', 'usr:coach:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1302, '新增', 13, '0,10,13', 2, NULL, NULL, NULL, 'F', '1', '0', 'usr:coach:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1303, '修改', 13, '0,10,13', 3, NULL, NULL, NULL, 'F', '1', '0', 'usr:coach:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1304, '删除', 13, '0,10,13', 4, NULL, NULL, NULL, 'F', '1', '0', 'usr:coach:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1305, '导出', 13, '0,10,13', 5, NULL, NULL, NULL, 'F', '1', '0', 'usr:coach:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1401, '查询', 14, '0,10,14', 1, NULL, NULL, NULL, 'F', '1', '0', 'usr:segment:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1402, '新增', 14, '0,10,14', 2, NULL, NULL, NULL, 'F', '1', '0', 'usr:segment:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1403, '修改', 14, '0,10,14', 3, NULL, NULL, NULL, 'F', '1', '0', 'usr:segment:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1404, '删除', 14, '0,10,14', 4, NULL, NULL, NULL, 'F', '1', '0', 'usr:segment:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1405, '导出', 14, '0,10,14', 5, NULL, NULL, NULL, 'F', '1', '0', 'usr:segment:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1501, '查询', 15, '0,10,15', 1, NULL, NULL, NULL, 'F', '1', '0', 'vip:plan:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1502, '新增', 15, '0,10,15', 2, NULL, NULL, NULL, 'F', '1', '0', 'vip:plan:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1503, '修改', 15, '0,10,15', 3, NULL, NULL, NULL, 'F', '1', '0', 'vip:plan:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1504, '删除', 15, '0,10,15', 4, NULL, NULL, NULL, 'F', '1', '0', 'vip:plan:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1505, '导出', 15, '0,10,15', 5, NULL, NULL, NULL, 'F', '1', '0', 'vip:plan:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1601, '查询', 16, '0,10,16', 1, NULL, NULL, NULL, 'F', '1', '0', 'mem:level:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1602, '新增', 16, '0,10,16', 2, NULL, NULL, NULL, 'F', '1', '0', 'mem:level:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1603, '修改', 16, '0,10,16', 3, NULL, NULL, NULL, 'F', '1', '0', 'mem:level:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1604, '删除', 16, '0,10,16', 4, NULL, NULL, NULL, 'F', '1', '0', 'mem:level:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1605, '导出', 16, '0,10,16', 5, NULL, NULL, NULL, 'F', '1', '0', 'mem:level:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1701, '查询', 17, '0,10,17', 1, NULL, NULL, NULL, 'F', '1', '0', 'vip:benefit:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1702, '新增', 17, '0,10,17', 2, NULL, NULL, NULL, 'F', '1', '0', 'vip:benefit:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1703, '修改', 17, '0,10,17', 3, NULL, NULL, NULL, 'F', '1', '0', 'vip:benefit:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1704, '删除', 17, '0,10,17', 4, NULL, NULL, NULL, 'F', '1', '0', 'vip:benefit:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1705, '导出', 17, '0,10,17', 5, NULL, NULL, NULL, 'F', '1', '0', 'vip:benefit:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1801, '查询', 18, '0,10,18', 1, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-log:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1802, '新增', 18, '0,10,18', 2, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-log:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1803, '修改', 18, '0,10,18', 3, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-log:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1804, '删除', 18, '0,10,18', 4, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-log:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1805, '导出', 18, '0,10,18', 5, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-log:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1806, '积分调整', 18, '0,10,18', 6, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-log:adjust', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (1901, '审核操作', 19, '0,10,19', 1, NULL, NULL, NULL, 'F', '1', '0', 'usr:booking:audit', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11501, '查询', 115, '0,10,115', 1, NULL, NULL, NULL, 'F', '1', '0', 'vip:subscription:list', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11502, '修改', 115, '0,10,115', 2, NULL, NULL, NULL, 'F', '1', '0', 'vip:subscription:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11601, '查询', 116, '0,10,116', 1, NULL, NULL, NULL, 'F', '1', '0', 'mem:badge:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11602, '新增', 116, '0,10,116', 2, NULL, NULL, NULL, 'F', '1', '0', 'mem:badge:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11603, '修改', 116, '0,10,116', 3, NULL, NULL, NULL, 'F', '1', '0', 'mem:badge:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11604, '删除', 116, '0,10,116', 4, NULL, NULL, NULL, 'F', '1', '0', 'mem:badge:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11605, '发放徽章', 116, '0,10,116', 5, NULL, NULL, NULL, 'F', '1', '0', 'mem:badge:grant', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11701, '查询', 117, '0,10,117', 1, NULL, NULL, NULL, 'F', '1', '0', 'mem:checkin:list', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11702, '删除', 117, '0,10,117', 2, NULL, NULL, NULL, 'F', '1', '0', 'mem:checkin:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11801, '查询', 118, '0,10,118', 1, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-rule:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11802, '新增', 118, '0,10,118', 2, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-rule:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11803, '修改', 118, '0,10,118', 3, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-rule:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11804, '删除', 118, '0,10,118', 4, NULL, NULL, NULL, 'F', '1', '0', 'mem:points-rule:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11901, '查询', 119, '0,10,119', 1, NULL, NULL, NULL, 'F', '1', '0', 'usr:coach:list', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (11902, '修改', 119, '0,10,119', 2, NULL, NULL, NULL, 'F', '1', '0', 'usr:coach:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (12001, '查询', 120, '0,10,120', 1, NULL, NULL, NULL, 'F', '1', '0', 'usr:coach:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (12002, '修改', 120, '0,10,120', 2, NULL, NULL, NULL, 'F', '1', '0', 'usr:coach:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (12301, '查询', 123, '0,10,123', 1, NULL, NULL, NULL, 'F', '1', '0', 'usr:address:list', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (12302, '删除', 123, '0,10,123', 2, NULL, NULL, NULL, 'F', '1', '0', 'usr:address:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (12401, '查询', 124, '0,10,124', 1, NULL, NULL, NULL, 'F', '1', '0', 'usr:setting:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (12402, '修改', 124, '0,10,124', 2, NULL, NULL, NULL, 'F', '1', '0', 'usr:setting:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2101, '查询', 21, '0,20,21', 1, NULL, NULL, NULL, 'F', '1', '0', 'crs:category:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2102, '新增', 21, '0,20,21', 2, NULL, NULL, NULL, 'F', '1', '0', 'crs:category:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2103, '修改', 21, '0,20,21', 3, NULL, NULL, NULL, 'F', '1', '0', 'crs:category:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2104, '删除', 21, '0,20,21', 4, NULL, NULL, NULL, 'F', '1', '0', 'crs:category:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2105, '导出', 21, '0,20,21', 5, NULL, NULL, NULL, 'F', '1', '0', 'crs:category:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2201, '查询', 22, '0,20,22', 1, NULL, NULL, NULL, 'F', '1', '0', 'crs:course:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2202, '新增', 22, '0,20,22', 2, NULL, NULL, NULL, 'F', '1', '0', 'crs:course:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2203, '修改', 22, '0,20,22', 3, NULL, NULL, NULL, 'F', '1', '0', 'crs:course:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2204, '删除', 22, '0,20,22', 4, NULL, NULL, NULL, 'F', '1', '0', 'crs:course:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2205, '导出', 22, '0,20,22', 5, NULL, NULL, NULL, 'F', '1', '0', 'crs:course:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2301, '查询', 23, '0,20,23', 1, NULL, NULL, NULL, 'F', '1', '0', 'crs:video:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2302, '新增', 23, '0,20,23', 2, NULL, NULL, NULL, 'F', '1', '0', 'crs:video:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2303, '修改', 23, '0,20,23', 3, NULL, NULL, NULL, 'F', '1', '0', 'crs:video:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2304, '删除', 23, '0,20,23', 4, NULL, NULL, NULL, 'F', '1', '0', 'crs:video:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2305, '导出', 23, '0,20,23', 5, NULL, NULL, NULL, 'F', '1', '0', 'crs:video:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2401, '查询', 24, '0,20,24', 1, NULL, NULL, NULL, 'F', '1', '0', 'media:asset:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2402, '上传媒资', 24, '0,20,24', 2, NULL, NULL, NULL, 'F', '1', '0', 'media:asset:upload', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2403, '修改', 24, '0,20,24', 3, NULL, NULL, NULL, 'F', '1', '0', 'media:asset:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2404, '删除', 24, '0,20,24', 4, NULL, NULL, NULL, 'F', '1', '0', 'media:asset:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2405, '导出', 24, '0,20,24', 5, NULL, NULL, NULL, 'F', '1', '0', 'media:asset:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2501, '查询', 25, '0,20,25', 1, NULL, NULL, NULL, 'F', '1', '0', 'community:post:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2502, '新增', 25, '0,20,25', 2, NULL, NULL, NULL, 'F', '1', '0', 'community:post:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2503, '修改', 25, '0,20,25', 3, NULL, NULL, NULL, 'F', '1', '0', 'community:post:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2504, '删除', 25, '0,20,25', 4, NULL, NULL, NULL, 'F', '1', '0', 'community:post:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2505, '导出', 25, '0,20,25', 5, NULL, NULL, NULL, 'F', '1', '0', 'community:post:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2601, '查询', 26, '0,20,26', 1, NULL, NULL, NULL, 'F', '1', '0', 'community:audit:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2602, '审核通过', 26, '0,20,26', 2, NULL, NULL, NULL, 'F', '1', '0', 'community:audit:pass', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2603, '审核拒绝', 26, '0,20,26', 3, NULL, NULL, NULL, 'F', '1', '0', 'community:audit:reject', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2604, '导出', 26, '0,20,26', 4, NULL, NULL, NULL, 'F', '1', '0', 'community:audit:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2701, '查询', 27, '0,20,27', 1, NULL, NULL, NULL, 'F', '1', '0', 'crs:progress:list', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2702, '重置进度', 27, '0,20,27', 2, NULL, NULL, NULL, 'F', '1', '0', 'crs:progress:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2801, '查询', 28, '0,20,28', 1, NULL, NULL, NULL, 'F', '1', '0', 'community:comment:list', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2802, '删除', 28, '0,20,28', 2, NULL, NULL, NULL, 'F', '1', '0', 'community:comment:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (2803, '修改', 28, '0,20,28', 3, NULL, NULL, NULL, 'F', '1', '0', 'community:comment:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3101, '查询', 31, '0,30,31', 1, NULL, NULL, NULL, 'F', '1', '0', 'train:exercise:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3102, '新增', 31, '0,30,31', 2, NULL, NULL, NULL, 'F', '1', '0', 'train:exercise:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3103, '修改', 31, '0,30,31', 3, NULL, NULL, NULL, 'F', '1', '0', 'train:exercise:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3104, '删除', 31, '0,30,31', 4, NULL, NULL, NULL, 'F', '1', '0', 'train:exercise:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3105, '导出', 31, '0,30,31', 5, NULL, NULL, NULL, 'F', '1', '0', 'train:exercise:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3201, '查询', 32, '0,30,32', 1, NULL, NULL, NULL, 'F', '1', '0', 'train:plan:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3202, '新增', 32, '0,30,32', 2, NULL, NULL, NULL, 'F', '1', '0', 'train:plan:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3203, '修改', 32, '0,30,32', 3, NULL, NULL, NULL, 'F', '1', '0', 'train:plan:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3204, '删除', 32, '0,30,32', 4, NULL, NULL, NULL, 'F', '1', '0', 'train:plan:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3205, '导出', 32, '0,30,32', 5, NULL, NULL, NULL, 'F', '1', '0', 'train:plan:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3301, '查询', 33, '0,30,33', 1, NULL, NULL, NULL, 'F', '1', '0', 'train:record:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3302, '导出', 33, '0,30,33', 2, NULL, NULL, NULL, 'F', '1', '0', 'train:record:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3401, '查询', 34, '0,30,34', 1, NULL, NULL, NULL, 'F', '1', '0', 'train:device:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3402, '新增', 34, '0,30,34', 2, NULL, NULL, NULL, 'F', '1', '0', 'train:device:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3403, '修改', 34, '0,30,34', 3, NULL, NULL, NULL, 'F', '1', '0', 'train:device:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3404, '删除', 34, '0,30,34', 4, NULL, NULL, NULL, 'F', '1', '0', 'train:device:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (3405, '导出', 34, '0,30,34', 5, NULL, NULL, NULL, 'F', '1', '0', 'train:device:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4101, '查询', 41, '0,40,41', 1, NULL, NULL, NULL, 'F', '1', '0', 'diet:food-category:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4102, '新增', 41, '0,40,41', 2, NULL, NULL, NULL, 'F', '1', '0', 'diet:food-category:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4103, '修改', 41, '0,40,41', 3, NULL, NULL, NULL, 'F', '1', '0', 'diet:food-category:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4104, '删除', 41, '0,40,41', 4, NULL, NULL, NULL, 'F', '1', '0', 'diet:food-category:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4105, '导出', 41, '0,40,41', 5, NULL, NULL, NULL, 'F', '1', '0', 'diet:food-category:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4201, '查询', 42, '0,40,42', 1, NULL, NULL, NULL, 'F', '1', '0', 'diet:food:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4202, '新增', 42, '0,40,42', 2, NULL, NULL, NULL, 'F', '1', '0', 'diet:food:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4203, '修改', 42, '0,40,42', 3, NULL, NULL, NULL, 'F', '1', '0', 'diet:food:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4204, '删除', 42, '0,40,42', 4, NULL, NULL, NULL, 'F', '1', '0', 'diet:food:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4205, '导出', 42, '0,40,42', 5, NULL, NULL, NULL, 'F', '1', '0', 'diet:food:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4301, '查询', 43, '0,40,43', 1, NULL, NULL, NULL, 'F', '1', '0', 'diet:meal-plan:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4302, '新增', 43, '0,40,43', 2, NULL, NULL, NULL, 'F', '1', '0', 'diet:meal-plan:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4303, '修改', 43, '0,40,43', 3, NULL, NULL, NULL, 'F', '1', '0', 'diet:meal-plan:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4304, '删除', 43, '0,40,43', 4, NULL, NULL, NULL, 'F', '1', '0', 'diet:meal-plan:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4305, '导出', 43, '0,40,43', 5, NULL, NULL, NULL, 'F', '1', '0', 'diet:meal-plan:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4401, '查询', 44, '0,40,44', 1, NULL, NULL, NULL, 'F', '1', '0', 'diet:record:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4402, '导出', 44, '0,40,44', 2, NULL, NULL, NULL, 'F', '1', '0', 'diet:record:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (4403, '新增', 44, '', 3, NULL, NULL, NULL, 'F', '1', '0', 'diet:record:add', NULL, '0', '超级管理员', '2026-05-27 13:50:33', '', '2026-05-27 13:50:33');
INSERT INTO `sys_menu` VALUES (4404, '修改', 44, '', 4, NULL, NULL, NULL, 'F', '1', '0', 'diet:record:update', NULL, '0', '超级管理员', '2026-05-27 13:50:33', '', '2026-05-27 13:50:33');
INSERT INTO `sys_menu` VALUES (4405, '删除', 44, '', 5, NULL, NULL, NULL, 'F', '1', '0', 'diet:record:delete', NULL, '0', '超级管理员', '2026-05-27 13:50:33', '', '2026-05-27 13:50:33');
INSERT INTO `sys_menu` VALUES (5001, '对话服务', 50, '0,50', 1, '/ai/assistant-group', NULL, 'AiAssistantGroup', 'M', '0', '0', NULL, 'ChatDotRound', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5002, '知识与检索', 50, '0,50', 2, '/ai/knowledge-group', NULL, 'AiKnowledgeGroup', 'M', '0', '0', NULL, 'Document', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5003, '模型与编排', 50, '0,50', 3, '/ai/model-group', NULL, 'AiModelGroup', 'M', '0', '0', NULL, 'Operation', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5004, '运营监控', 50, '0,50', 4, '/ai/ops-group', NULL, 'AiOpsGroup', 'M', '0', '0', NULL, 'Monitor', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5101, '查询', 51, '0,50,5004,51', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:stats:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5102, '导出', 51, '0,50,5004,51', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:stats:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5201, '查询', 52, '0,50,5002,52', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:knowledge:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5202, '新增', 52, '0,50,5002,52', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:knowledge:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5203, '修改', 52, '0,50,5002,52', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:knowledge:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5204, '删除', 52, '0,50,5002,52', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:knowledge:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5205, '导出', 52, '0,50,5002,52', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:knowledge:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5301, '查询', 53, '0,50,5001,53', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:conversation:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5302, '导出', 53, '0,50,5001,53', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:conversation:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5303, '修改', 53, '0,50,5001,53', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:conversation:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5304, '删除', 53, '0,50,5001,53', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:conversation:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5801, '查询', 58, '0,50,5001,58', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:session:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5802, '修改', 58, '0,50,5001,58', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:session:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5803, '删除', 58, '0,50,5001,58', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:session:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5804, '导出', 58, '0,50,5001,58', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:session:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6911, '查询', 691, '0,50,65,691', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:quota:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6912, '赠送', 691, '0,50,65,691', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:quota:gift', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6913, '删除', 691, '0,50,65,691', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:quota:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5401, '查询', 54, '0,50,5003,54', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:model:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5402, '新增', 54, '0,50,5003,54', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:model:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5403, '修改', 54, '0,50,5003,54', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:model:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5404, '删除', 54, '0,50,5003,54', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:model:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5405, '导出', 54, '0,50,5003,54', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:model:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5501, '查询', 55, '0,50,5001,55', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:prompt:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5502, '新增', 55, '0,50,5001,55', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:prompt:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5503, '修改', 55, '0,50,5001,55', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:prompt:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5504, '删除', 55, '0,50,5001,55', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:prompt:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5505, '导出', 55, '0,50,5001,55', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:prompt:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5601, '查询', 56, '0,50,5002,56', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:rag-eval:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5602, '新增', 56, '0,50,5002,56', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:rag-eval:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5603, '修改', 56, '0,50,5002,56', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:rag-eval:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5604, '删除', 56, '0,50,5002,56', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:rag-eval:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5605, '导出', 56, '0,50,5002,56', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:rag-eval:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5701, '查询', 57, '0,50,5001,57', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:plan-review:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5702, '审核通过', 57, '0,50,5001,57', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:plan-review:audit', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5703, '审核拒绝', 57, '0,50,5001,57', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:plan-review:reject', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (5704, '导出', 57, '0,50,5001,57', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:plan-review:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6101, '查询', 61, '0,50,5003,61', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:agent:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6102, '新增', 61, '0,50,5003,61', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:agent:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6103, '修改', 61, '0,50,5003,61', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:agent:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6104, '删除', 61, '0,50,5003,61', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:agent:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6105, '导出', 61, '0,50,5003,61', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:agent:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6201, '查询', 62, '0,50,5003,62', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:tool:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6202, '新增', 62, '0,50,5003,62', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:tool:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6203, '修改', 62, '0,50,5003,62', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:tool:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6204, '删除', 62, '0,50,5003,62', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:tool:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6205, '导出', 62, '0,50,5003,62', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:tool:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6301, '查询', 63, '0,50,5002,63', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:dataset:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6302, '新增', 63, '0,50,5002,63', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:dataset:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6303, '修改', 63, '0,50,5002,63', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:dataset:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6304, '删除', 63, '0,50,5002,63', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:dataset:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6305, '导出', 63, '0,50,5002,63', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:dataset:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6401, '查询', 64, '0,50,5003,64', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:evaluation:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6402, '新增', 64, '0,50,5003,64', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:evaluation:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6403, '修改', 64, '0,50,5003,64', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:evaluation:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6404, '删除', 64, '0,50,5003,64', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:evaluation:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6405, '导出', 64, '0,50,5003,64', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:evaluation:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6501, '查询', 65, '0,50,65', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:cost:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6502, '导出', 65, '0,50,65', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:cost:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6601, '查询', 66, '0,50,5004,66', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:guardrail:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6602, '新增', 66, '0,50,5004,66', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:guardrail:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6603, '修改', 66, '0,50,5004,66', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:guardrail:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6604, '删除', 66, '0,50,5004,66', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:guardrail:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6605, '导出', 66, '0,50,5004,66', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:guardrail:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6701, '查询', 67, '0,50,5002,67', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:vector-index:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6702, '新增', 67, '0,50,5002,67', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:vector-index:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6703, '修改', 67, '0,50,5002,67', 3, NULL, NULL, NULL, 'F', '1', '0', 'ai:vector-index:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6704, '删除', 67, '0,50,5002,67', 4, NULL, NULL, NULL, 'F', '1', '0', 'ai:vector-index:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6705, '导出', 67, '0,50,5002,67', 5, NULL, NULL, NULL, 'F', '1', '0', 'ai:vector-index:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6801, '查询', 68, '0,50,5004,68', 1, NULL, NULL, NULL, 'F', '1', '0', 'ai:trace:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (6802, '导出', 68, '0,50,5004,68', 2, NULL, NULL, NULL, 'F', '1', '0', 'ai:trace:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7101, '查询', 71, '0,70,71', 1, NULL, NULL, NULL, 'F', '1', '0', 'prd:category:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7102, '新增', 71, '0,70,71', 2, NULL, NULL, NULL, 'F', '1', '0', 'prd:category:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7103, '修改', 71, '0,70,71', 3, NULL, NULL, NULL, 'F', '1', '0', 'prd:category:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7104, '删除', 71, '0,70,71', 4, NULL, NULL, NULL, 'F', '1', '0', 'prd:category:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7105, '导出', 71, '0,70,71', 5, NULL, NULL, NULL, 'F', '1', '0', 'prd:category:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7201, '查询', 72, '0,70,72', 1, NULL, NULL, NULL, 'F', '1', '0', 'prd:brand:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7202, '新增', 72, '0,70,72', 2, NULL, NULL, NULL, 'F', '1', '0', 'prd:brand:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7203, '修改', 72, '0,70,72', 3, NULL, NULL, NULL, 'F', '1', '0', 'prd:brand:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7204, '删除', 72, '0,70,72', 4, NULL, NULL, NULL, 'F', '1', '0', 'prd:brand:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7205, '导出', 72, '0,70,72', 5, NULL, NULL, NULL, 'F', '1', '0', 'prd:brand:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7301, '查询', 73, '0,70,73', 1, NULL, NULL, NULL, 'F', '1', '0', 'prd:product:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7302, '新增', 73, '0,70,73', 2, NULL, NULL, NULL, 'F', '1', '0', 'prd:product:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7303, '修改', 73, '0,70,73', 3, NULL, NULL, NULL, 'F', '1', '0', 'prd:product:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7304, '删除', 73, '0,70,73', 4, NULL, NULL, NULL, 'F', '1', '0', 'prd:product:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7305, '导出', 73, '0,70,73', 5, NULL, NULL, NULL, 'F', '1', '0', 'prd:product:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7401, '查询', 74, '0,70,74', 1, NULL, NULL, NULL, 'F', '1', '0', 'prd:stock:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7402, '修改', 74, '0,70,74', 2, NULL, NULL, NULL, 'F', '1', '0', 'prd:stock:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7403, '导出', 74, '0,70,74', 3, NULL, NULL, NULL, 'F', '1', '0', 'prd:stock:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7404, '库存调整', 74, '0,70,74', 4, NULL, NULL, NULL, 'F', '1', '0', 'prd:stock:adjust', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7501, '查询', 75, '', 1, NULL, NULL, NULL, 'F', '1', '0', 'prd:attr:query', NULL, '0', '超级管理员', '2025-05-20 10:00:00', '', '2025-05-20 10:00:00');
INSERT INTO `sys_menu` VALUES (7502, '新增', 75, '', 2, NULL, NULL, NULL, 'F', '1', '0', 'prd:attr:add', NULL, '0', '超级管理员', '2025-05-20 10:00:00', '', '2025-05-20 10:00:00');
INSERT INTO `sys_menu` VALUES (7503, '修改', 75, '', 3, NULL, NULL, NULL, 'F', '1', '0', 'prd:attr:update', NULL, '0', '超级管理员', '2025-05-20 10:00:00', '', '2025-05-20 10:00:00');
INSERT INTO `sys_menu` VALUES (7504, '删除', 75, '', 4, NULL, NULL, NULL, 'F', '1', '0', 'prd:attr:delete', NULL, '0', '超级管理员', '2025-05-20 10:00:00', '', '2025-05-20 10:00:00');
INSERT INTO `sys_menu` VALUES (7601, '查询', 76, '0,70,76', 1, NULL, NULL, NULL, 'F', '1', '0', 'prd:es:list', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (7602, '重建/维护', 76, '0,70,76', 2, NULL, NULL, NULL, 'F', '1', '0', 'prd:es:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8101, '查询', 81, '0,80,81', 1, NULL, NULL, NULL, 'F', '1', '0', 'ord:order:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8102, '导出', 81, '0,80,81', 2, NULL, NULL, NULL, 'F', '1', '0', 'ord:order:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8103, '发货', 81, '0,80,81', 3, NULL, NULL, NULL, 'F', '1', '0', 'ord:order:ship', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8104, '取消订单', 81, '0,80,81', 4, NULL, NULL, NULL, 'F', '1', '0', 'ord:order:cancel', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8105, '订单详情', 81, '0,80,81', 5, NULL, NULL, NULL, 'F', '1', '0', 'ord:order:detail', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8201, '查询', 82, '0,80,82', 1, NULL, NULL, NULL, 'F', '1', '0', 'vip:order:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8202, '新增', 82, '0,80,82', 2, NULL, NULL, NULL, 'F', '1', '0', 'vip:order:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8203, '修改', 82, '0,80,82', 3, NULL, NULL, NULL, 'F', '1', '0', 'vip:order:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8204, '删除', 82, '0,80,82', 4, NULL, NULL, NULL, 'F', '1', '0', 'vip:order:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8205, '导出', 82, '0,80,82', 5, NULL, NULL, NULL, 'F', '1', '0', 'vip:order:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8301, '查询', 83, '0,80,83', 1, NULL, NULL, NULL, 'F', '1', '0', 'pay:refund:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8302, '审核通过', 83, '0,80,83', 2, NULL, NULL, NULL, 'F', '1', '0', 'pay:refund:audit', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8303, '审核拒绝', 83, '0,80,83', 3, NULL, NULL, NULL, 'F', '1', '0', 'pay:refund:reject', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8304, '导出', 83, '0,80,83', 4, NULL, NULL, NULL, 'F', '1', '0', 'pay:refund:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8401, '查询', 84, '0,80,84', 1, NULL, NULL, NULL, 'F', '1', '0', 'pay:channel:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8402, '新增', 84, '0,80,84', 2, NULL, NULL, NULL, 'F', '1', '0', 'pay:channel:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8403, '修改', 84, '0,80,84', 3, NULL, NULL, NULL, 'F', '1', '0', 'pay:channel:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8404, '删除', 84, '0,80,84', 4, NULL, NULL, NULL, 'F', '1', '0', 'pay:channel:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8405, '导出', 84, '0,80,84', 5, NULL, NULL, NULL, 'F', '1', '0', 'pay:channel:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8501, '查询', 85, '0,80,85', 1, NULL, NULL, NULL, 'F', '1', '0', 'ord:logistics:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8502, '新增', 85, '0,80,85', 2, NULL, NULL, NULL, 'F', '1', '0', 'ord:logistics:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8503, '修改', 85, '0,80,85', 3, NULL, NULL, NULL, 'F', '1', '0', 'ord:logistics:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8504, '删除', 85, '0,80,85', 4, NULL, NULL, NULL, 'F', '1', '0', 'ord:logistics:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (8505, '导出', 85, '0,80,85', 5, NULL, NULL, NULL, 'F', '1', '0', 'ord:logistics:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9101, '查询', 91, '0,90,91', 1, NULL, NULL, NULL, 'F', '1', '0', 'mkt:banner:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9102, '新增', 91, '0,90,91', 2, NULL, NULL, NULL, 'F', '1', '0', 'mkt:banner:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9103, '修改', 91, '0,90,91', 3, NULL, NULL, NULL, 'F', '1', '0', 'mkt:banner:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9104, '删除', 91, '0,90,91', 4, NULL, NULL, NULL, 'F', '1', '0', 'mkt:banner:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9105, '导出', 91, '0,90,91', 5, NULL, NULL, NULL, 'F', '1', '0', 'mkt:banner:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9201, '查询', 92, '0,90,92', 1, NULL, NULL, NULL, 'F', '1', '0', 'mkt:coupon:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9202, '新增', 92, '0,90,92', 2, NULL, NULL, NULL, 'F', '1', '0', 'mkt:coupon:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9203, '修改', 92, '0,90,92', 3, NULL, NULL, NULL, 'F', '1', '0', 'mkt:coupon:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9204, '删除', 92, '0,90,92', 4, NULL, NULL, NULL, 'F', '1', '0', 'mkt:coupon:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9205, '导出', 92, '0,90,92', 5, NULL, NULL, NULL, 'F', '1', '0', 'mkt:coupon:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9301, '查询', 93, '0,90,93', 1, NULL, NULL, NULL, 'F', '1', '0', 'seckill:activity:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9302, '新增', 93, '0,90,93', 2, NULL, NULL, NULL, 'F', '1', '0', 'seckill:activity:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9303, '修改', 93, '0,90,93', 3, NULL, NULL, NULL, 'F', '1', '0', 'seckill:activity:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9304, '删除', 93, '0,90,93', 4, NULL, NULL, NULL, 'F', '1', '0', 'seckill:activity:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9305, '导出', 93, '0,90,93', 5, NULL, NULL, NULL, 'F', '1', '0', 'seckill:activity:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9401, '查询', 94, '0,90,94', 1, NULL, NULL, NULL, 'F', '1', '0', 'mkt:challenge:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9402, '新增', 94, '0,90,94', 2, NULL, NULL, NULL, 'F', '1', '0', 'mkt:challenge:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9403, '修改', 94, '0,90,94', 3, NULL, NULL, NULL, 'F', '1', '0', 'mkt:challenge:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9404, '删除', 94, '0,90,94', 4, NULL, NULL, NULL, 'F', '1', '0', 'mkt:challenge:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9405, '导出', 94, '0,90,94', 5, NULL, NULL, NULL, 'F', '1', '0', 'mkt:challenge:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9501, '查询', 95, '0,90,95', 1, NULL, NULL, NULL, 'F', '1', '0', 'mkt:coupon-receive:list', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9502, '修改', 95, '0,90,95', 2, NULL, NULL, NULL, 'F', '1', '0', 'mkt:coupon-receive:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (9601, '查询', 96, '0,90,96', 1, NULL, NULL, NULL, 'F', '1', '0', 'seckill:activity:list', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10101, '查询', 101, '0,100,101', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:user:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10102, '新增', 101, '0,100,101', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:user:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10103, '修改', 101, '0,100,101', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:user:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10104, '删除', 101, '0,100,101', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:user:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10105, '导出', 101, '0,100,101', 5, NULL, NULL, NULL, 'F', '1', '0', 'system:user:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10201, '查询', 102, '0,100,102', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:role:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10202, '新增', 102, '0,100,102', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:role:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10203, '修改', 102, '0,100,102', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:role:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10204, '删除', 102, '0,100,102', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:role:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10205, '导出', 102, '0,100,102', 5, NULL, NULL, NULL, 'F', '1', '0', 'system:role:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10206, '分配权限', 102, '0,100,102', 6, NULL, NULL, NULL, 'F', '1', '0', 'system:role:assign', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10301, '查询', 103, '0,100,103', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:menu:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10302, '新增', 103, '0,100,103', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:menu:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10303, '修改', 103, '0,100,103', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:menu:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10304, '删除', 103, '0,100,103', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:menu:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10305, '导出', 103, '0,100,103', 5, NULL, NULL, NULL, 'F', '1', '0', 'system:menu:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10401, '查询', 104, '0,100,104', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:dept:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10402, '新增', 104, '0,100,104', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:dept:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10403, '修改', 104, '0,100,104', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:dept:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10404, '删除', 104, '0,100,104', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:dept:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10405, '导出', 104, '0,100,104', 5, NULL, NULL, NULL, 'F', '1', '0', 'system:dept:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10501, '查询', 105, '0,100,105', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:dict:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10502, '新增', 105, '0,100,105', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:dict:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10503, '修改', 105, '0,100,105', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:dict:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10504, '删除', 105, '0,100,105', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:dict:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10505, '导出', 105, '0,100,105', 5, NULL, NULL, NULL, 'F', '1', '0', 'system:dict:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10701, '查询', 107, '0,100,107', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:job:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:26');
INSERT INTO `sys_menu` VALUES (10702, '新增', 107, '0,100,107', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:job:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10703, '修改', 107, '0,100,107', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:job:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10704, '删除', 107, '0,100,107', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:job:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10705, '导出', 107, '0,100,107', 5, NULL, NULL, NULL, 'F', '1', '0', 'system:job:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10706, '立即执行', 107, '0,100,107', 6, NULL, NULL, NULL, 'F', '1', '0', 'system:job:run', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10707, '暂停任务', 107, '0,100,107', 7, NULL, NULL, NULL, 'F', '1', '0', 'system:job:pause', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10801, '查询', 108, '0,100,108', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:config:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10802, '新增', 108, '0,100,108', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:config:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10803, '修改', 108, '0,100,108', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:config:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10804, '删除', 108, '0,100,108', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:config:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10805, '导出', 108, '0,100,108', 5, NULL, NULL, NULL, 'F', '1', '0', 'system:config:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10901, '查询', 109, '0,100,109', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:notice:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10902, '新增', 109, '0,100,109', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:notice:add', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10903, '修改', 109, '0,100,109', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:notice:update', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10904, '删除', 109, '0,100,109', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:notice:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10905, '导出', 109, '0,100,109', 5, NULL, NULL, NULL, 'F', '1', '0', 'system:notice:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (10906, '发布推送', 109, '0,100,109', 6, NULL, NULL, NULL, 'F', '1', '0', 'system:notice:publish', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (106101, '查询', 1061, '0,100,106,1061', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:login-log:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (106102, '删除', 1061, '0,100,106,1061', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:login-log:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (106103, '导出', 1061, '0,100,106,1061', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:login-log:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (106104, '清空', 1061, '0,100,106,1061', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:login-log:clear', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (106201, '查询', 1062, '0,100,106,1062', 1, NULL, NULL, NULL, 'F', '1', '0', 'system:oper-log:query', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (106202, '删除', 1062, '0,100,106,1062', 2, NULL, NULL, NULL, 'F', '1', '0', 'system:oper-log:delete', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (106203, '导出', 1062, '0,100,106,1062', 3, NULL, NULL, NULL, 'F', '1', '0', 'system:oper-log:export', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');
INSERT INTO `sys_menu` VALUES (106204, '清空', 1062, '0,100,106,1062', 4, NULL, NULL, NULL, 'F', '1', '0', 'system:oper-log:clear', NULL, '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-28 11:48:27');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
                               `notice_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '公告ID',
                               `notice_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
                               `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型（1公告 2推送）',
                               `notice_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
                               `target_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '目标（0全部 1指定用户 2用户分群）',
                               `target_ids` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标ID列表(JSON)',
                               `push_channel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'app' COMMENT '推送渠道 app/sms/email',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0草稿 1已发布 2撤回）',
                               `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                               `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                               `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '春季健身挑战赛开启', '2', '参与30天打卡赢500积分，完成训练还可抽取年卡会员。', '0', NULL, 'app', '1', '0', '2025-05-01 09:00:00', '超级管理员', '2026-05-26 16:46:00', '', '2026-05-26 16:46:00');
INSERT INTO `sys_notice` VALUES (2, '系统维护公告', '1', '5月25日 02:00-04:00 进行数据库升级，期间部分功能不可用。', '0', NULL, NULL, '1', '0', '2025-05-20 18:00:00', '超级管理员', '2026-05-26 16:46:00', '', '2026-05-26 16:46:00');
INSERT INTO `sys_notice` VALUES (3, '新用户专享券到账', '2', '您有一张满199减30优惠券，有效期至5月31日。', '1', NULL, 'app', '1', '0', '2025-05-18 10:00:00', '超级管理员', '2026-05-26 16:46:00', '', '2026-05-26 16:46:00');

-- ----------------------------
-- Table structure for sys_notice_read
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_read`;
CREATE TABLE `sys_notice_read`  (
                                    `read_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '已读ID',
                                    `notice_id` bigint UNSIGNED NOT NULL COMMENT '公告ID',
                                    `customer_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'C端用户ID',
                                    `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '管理端用户ID',
                                    `read_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
                                    PRIMARY KEY (`read_id`) USING BTREE,
                                    UNIQUE INDEX `uk_notice_customer`(`notice_id` ASC, `customer_id` ASC) USING BTREE,
                                    UNIQUE INDEX `uk_notice_user`(`notice_id` ASC, `user_id` ASC) USING BTREE,
                                    INDEX `idx_sys_notice_read_customer`(`customer_id` ASC) USING BTREE,
                                    INDEX `idx_sys_notice_read_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息已读记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice_read
-- ----------------------------
INSERT INTO `sys_notice_read` VALUES (1, 1, 1, NULL, '2025-05-02 08:30:00');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
                                 `oper_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志主键',
                                 `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模块标题',
                                 `business_type` tinyint NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
                                 `method` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法名称',
                                 `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求方式',
                                 `oper_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人员',
                                 `oper_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求URL',
                                 `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主机地址',
                                 `oper_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
                                 `json_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '返回参数',
                                 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1异常）',
                                 `error_msg` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误消息',
                                 `oper_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                 `cost_time` bigint UNSIGNED NULL DEFAULT 0 COMMENT '消耗时间(毫秒)',
                                 PRIMARY KEY (`oper_id`) USING BTREE,
                                 INDEX `idx_sys_oper_log_time`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 220 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (1, 'C端用户', 1, 'com.fitai.web.controller.usr.CustomerController.add()', 'POST', 'admin', '/api/usr/customer', '127.0.0.1', NULL, NULL, '0', NULL, '2025-05-20 10:00:00', 85);
INSERT INTO `sys_oper_log` VALUES (2, '课程管理', 2, 'com.fitai.web.controller.crs.CourseController.edit()', 'PUT', 'admin', '/api/crs/course/1', '127.0.0.1', NULL, NULL, '0', NULL, '2025-05-20 11:30:00', 120);
INSERT INTO `sys_oper_log` VALUES (3, '优惠券', 1, 'com.fitai.web.controller.mkt.CouponController.add()', 'POST', 'operator', '/api/mkt/coupon', '192.168.1.105', NULL, NULL, '0', NULL, '2025-05-21 14:20:00', 96);
INSERT INTO `sys_oper_log` VALUES (4, '内容审核', 2, 'net.wanho.fitai.modules.community.controller.admin.AdminAuditController.pass()', 'PUT', 'operator', '/admin/community/audit/1/pass', '192.168.1.105', NULL, NULL, '0', NULL, '2025-05-21 15:05:00', 45);
INSERT INTO `sys_oper_log` VALUES (5, 'AI模型配置', 2, 'com.fitai.web.controller.ai.ModelController.edit()', 'PUT', 'admin', '/api/ai/model/1', '127.0.0.1', NULL, NULL, '0', NULL, '2025-05-22 09:30:00', 210);
INSERT INTO `sys_oper_log` VALUES (6, '新建 OSS 分类', 1, 'net.wanho.media.admin.MediaOssController.createCategory()', 'POST', 'admin', '/admin/media/oss/category', '127.0.0.1', '{\"categoryName\":\"人物头像\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:02', 85);
INSERT INTO `sys_oper_log` VALUES (7, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:04', 48);
INSERT INTO `sys_oper_log` VALUES (8, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:07', 49);
INSERT INTO `sys_oper_log` VALUES (9, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:08', 41);
INSERT INTO `sys_oper_log` VALUES (10, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:09', 49);
INSERT INTO `sys_oper_log` VALUES (11, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:10', 39);
INSERT INTO `sys_oper_log` VALUES (12, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:11', 42);
INSERT INTO `sys_oper_log` VALUES (13, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:11', 45);
INSERT INTO `sys_oper_log` VALUES (14, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:15', 43);
INSERT INTO `sys_oper_log` VALUES (15, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":5,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:16', 45);
INSERT INTO `sys_oper_log` VALUES (16, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:17', 36);
INSERT INTO `sys_oper_log` VALUES (17, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:18', 45);
INSERT INTO `sys_oper_log` VALUES (18, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":3,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:22', 39);
INSERT INTO `sys_oper_log` VALUES (19, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":2,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:25', 37);
INSERT INTO `sys_oper_log` VALUES (20, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":3,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:25:26', 44);
INSERT INTO `sys_oper_log` VALUES (21, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":3,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:38:24', 131);
INSERT INTO `sys_oper_log` VALUES (22, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":3,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:38:26', 50);
INSERT INTO `sys_oper_log` VALUES (23, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:01', 78);
INSERT INTO `sys_oper_log` VALUES (24, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:02', 80);
INSERT INTO `sys_oper_log` VALUES (25, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:04', 55);
INSERT INTO `sys_oper_log` VALUES (26, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":2,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:05', 58);
INSERT INTO `sys_oper_log` VALUES (27, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":4,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:07', 49);
INSERT INTO `sys_oper_log` VALUES (28, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":4,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:08', 50);
INSERT INTO `sys_oper_log` VALUES (29, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":5,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:09', 60);
INSERT INTO `sys_oper_log` VALUES (30, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:14', 51);
INSERT INTO `sys_oper_log` VALUES (31, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:16', 44);
INSERT INTO `sys_oper_log` VALUES (32, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:42:16', 46);
INSERT INTO `sys_oper_log` VALUES (33, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:45:58', 45);
INSERT INTO `sys_oper_log` VALUES (34, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:46:23', 51);
INSERT INTO `sys_oper_log` VALUES (35, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:49:11', 52);
INSERT INTO `sys_oper_log` VALUES (36, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":5,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:49:13', 42);
INSERT INTO `sys_oper_log` VALUES (37, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:52:09', 54);
INSERT INTO `sys_oper_log` VALUES (38, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:52:10', 42);
INSERT INTO `sys_oper_log` VALUES (39, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:52:11', 42);
INSERT INTO `sys_oper_log` VALUES (40, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:52:13', 49);
INSERT INTO `sys_oper_log` VALUES (41, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:52:17', 35);
INSERT INTO `sys_oper_log` VALUES (42, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:52:17', 38);
INSERT INTO `sys_oper_log` VALUES (43, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:52:20', 42);
INSERT INTO `sys_oper_log` VALUES (44, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:52:22', 45);
INSERT INTO `sys_oper_log` VALUES (45, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:52:45', 34);
INSERT INTO `sys_oper_log` VALUES (46, '删除 OSS 分类', 3, 'net.wanho.media.admin.MediaOssController.deleteCategories()', 'DELETE', 'admin', '/admin/media/oss/category', '127.0.0.1', '[2]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:55:59', 36);
INSERT INTO `sys_oper_log` VALUES (47, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:57:41', 35);
INSERT INTO `sys_oper_log` VALUES (48, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:57:42', 40);
INSERT INTO `sys_oper_log` VALUES (49, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:57:43', 36);
INSERT INTO `sys_oper_log` VALUES (50, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"targetCategoryId\":1,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:58:14', 151);
INSERT INTO `sys_oper_log` VALUES (51, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"targetCategoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:58:16', 80);
INSERT INTO `sys_oper_log` VALUES (52, '批量删除 OSS 图片', 3, 'net.wanho.media.admin.MediaOssController.deleteImages()', 'DELETE', 'admin', '/admin/media/oss', '127.0.0.1', '{\"ossIds\":[7]}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:58:36', 80);
INSERT INTO `sys_oper_log` VALUES (53, '新建 OSS 分类', 1, 'net.wanho.media.admin.MediaOssController.createCategory()', 'POST', 'admin', '/admin/media/oss/category', '127.0.0.1', '{\"categoryName\":\"轮播图片\",\"parentId\":0}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:59:12', 41);
INSERT INTO `sys_oper_log` VALUES (54, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":8,\"ossName\":\"banner1\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/4454ececbe874f8bb29161fd2304802e.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778765634}}', '0', NULL, '2026-05-26 14:59:26', 789);
INSERT INTO `sys_oper_log` VALUES (55, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":9,\"ossName\":\"banner1\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1eda5a8eed6b4dc1b8c89b3777b2aad7.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778766184}}', '0', NULL, '2026-05-26 14:59:26', 1335);
INSERT INTO `sys_oper_log` VALUES (56, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":10,\"ossName\":\"banner2\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/4125f308ccd64deaa20a5ce3f1ecfacf.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778766213}}', '0', NULL, '2026-05-26 14:59:26', 492);
INSERT INTO `sys_oper_log` VALUES (57, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":11,\"ossName\":\"banner1\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a0bc55b5395d4ea39062b98f591fc43c.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778766296}}', '0', NULL, '2026-05-26 14:59:26', 1448);
INSERT INTO `sys_oper_log` VALUES (58, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":12,\"ossName\":\"banner1\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/475598f6223a42f49195d581bb27dc6a.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778766301}}', '0', NULL, '2026-05-26 14:59:26', 1452);
INSERT INTO `sys_oper_log` VALUES (59, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":13,\"ossName\":\"banner1\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/450ca933a5404bd4bfe374b224eb0317.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778766516}}', '0', NULL, '2026-05-26 14:59:27', 1666);
INSERT INTO `sys_oper_log` VALUES (60, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":14,\"ossName\":\"banner1\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/83dc95afb3ab4d19a829b8785f3f60ab.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778766667}}', '0', NULL, '2026-05-26 14:59:27', 1814);
INSERT INTO `sys_oper_log` VALUES (61, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":15,\"ossName\":\"banner2\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/94833110b096494194317a649cacc23d.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778766854}}', '0', NULL, '2026-05-26 14:59:27', 472);
INSERT INTO `sys_oper_log` VALUES (62, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":16,\"ossName\":\"banner2\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/31047a172c294acb84ca810aa9486f89.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778767050}}', '0', NULL, '2026-05-26 14:59:27', 463);
INSERT INTO `sys_oper_log` VALUES (63, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":17,\"ossName\":\"banner2\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/d9b41ccb104f4558a7bb8405f90e0305.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778767232}}', '0', NULL, '2026-05-26 14:59:27', 493);
INSERT INTO `sys_oper_log` VALUES (64, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":18,\"ossName\":\"banner3\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/be49ca4118af4970b44e973fafc2b5d0.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778767999}}', '0', NULL, '2026-05-26 14:59:28', 1059);
INSERT INTO `sys_oper_log` VALUES (65, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":19,\"ossName\":\"banner2\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a5f394cfca1341e8b20992d280ee4fa2.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778768134}}', '0', NULL, '2026-05-26 14:59:28', 1743);
INSERT INTO `sys_oper_log` VALUES (66, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":20,\"ossName\":\"banner2\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/f536a2d9d72f46609254d04625011728.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778769088}}', '0', NULL, '2026-05-26 14:59:29', 2783);
INSERT INTO `sys_oper_log` VALUES (67, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":21,\"ossName\":\"banner3\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/0d267c83139c4de79f2ca14a2ef1bb6b.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778769186}}', '0', NULL, '2026-05-26 14:59:29', 2842);
INSERT INTO `sys_oper_log` VALUES (68, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":22,\"ossName\":\"banner3\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/b85a721f2cae4ff6aaa0db81a41c9127.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778769246}}', '0', NULL, '2026-05-26 14:59:29', 2117);
INSERT INTO `sys_oper_log` VALUES (69, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":23,\"ossName\":\"banner3\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/065aa883e3a34188aeecebdb8a0a088c.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778769251}}', '0', NULL, '2026-05-26 14:59:29', 1039);
INSERT INTO `sys_oper_log` VALUES (70, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":24,\"ossName\":\"banner3\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/fc0681f13f29434dbeee54830adc33ba.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778769257}}', '0', NULL, '2026-05-26 14:59:29', 1942);
INSERT INTO `sys_oper_log` VALUES (71, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":25,\"ossName\":\"banner4\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/0f292c01b8c04d04b6ab3ac7028fc599.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778769857}}', '0', NULL, '2026-05-26 14:59:30', 590);
INSERT INTO `sys_oper_log` VALUES (72, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":26,\"ossName\":\"banner4\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1a110e0323514c6e95fab3f3157a2e16.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778770089}}', '0', NULL, '2026-05-26 14:59:30', 2002);
INSERT INTO `sys_oper_log` VALUES (73, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":27,\"ossName\":\"banner3\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/46c0721c09a74de1b1daa0ccd221ac4f.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778770587}}', '0', NULL, '2026-05-26 14:59:31', 1425);
INSERT INTO `sys_oper_log` VALUES (74, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":28,\"ossName\":\"banner5\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a6cf78db60d74a45a7c5ee379c30c66c.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778770786}}', '0', NULL, '2026-05-26 14:59:31', 619);
INSERT INTO `sys_oper_log` VALUES (75, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":29,\"ossName\":\"banner1\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a6978b7f1d92488994d54ab902fe405e.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778771391}}', '0', NULL, '2026-05-26 14:59:31', 728);
INSERT INTO `sys_oper_log` VALUES (76, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":30,\"ossName\":\"banner4\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/e7ea2848ac994c4cba7b9ae9e684a6af.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778771799}}', '0', NULL, '2026-05-26 14:59:32', 2447);
INSERT INTO `sys_oper_log` VALUES (77, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":31,\"ossName\":\"banner4\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/e2a515dd131b4b9a97295d8b5e2aa0f1.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778771866}}', '0', NULL, '2026-05-26 14:59:32', 2524);
INSERT INTO `sys_oper_log` VALUES (78, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":32,\"ossName\":\"banner4\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/826dd7a9ec41454489de012659508517.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778772182}}', '0', NULL, '2026-05-26 14:59:32', 2821);
INSERT INTO `sys_oper_log` VALUES (79, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":33,\"ossName\":\"banner4\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/91ebc095051a45a1b2d581c61603ef90.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778772191}}', '0', NULL, '2026-05-26 14:59:32', 1320);
INSERT INTO `sys_oper_log` VALUES (80, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":34,\"ossName\":\"banner5\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/ff3ac854c6cc42e2a58b0b29bfe9e7c0.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778772197}}', '0', NULL, '2026-05-26 14:59:32', 2253);
INSERT INTO `sys_oper_log` VALUES (81, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":35,\"ossName\":\"banner6\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/55f37eb01c0c42a59518328fdfc1dbf9.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778772797}}', '0', NULL, '2026-05-26 14:59:33', 1329);
INSERT INTO `sys_oper_log` VALUES (82, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":36,\"ossName\":\"banner5\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/e949e73d2fa845629d9f13c03e1ba523.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778773051}}', '0', NULL, '2026-05-26 14:59:33', 759);
INSERT INTO `sys_oper_log` VALUES (83, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":37,\"ossName\":\"banner2\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/dd33a495d1a24e4facec0ee0bffb2673.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778773372}}', '0', NULL, '2026-05-26 14:59:33', 1506);
INSERT INTO `sys_oper_log` VALUES (84, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":38,\"ossName\":\"banner5\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/e889b665aa004513b7e7aff45f62f87b.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778773593}}', '0', NULL, '2026-05-26 14:59:34', 1327);
INSERT INTO `sys_oper_log` VALUES (85, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":39,\"ossName\":\"banner6\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/4621e0027bec4454b7c5f391c378d5cd.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778774096}}', '0', NULL, '2026-05-26 14:59:34', 1215);
INSERT INTO `sys_oper_log` VALUES (86, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":40,\"ossName\":\"banner5\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/0429133f95da4858979bcf3b7ebb8ede.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778774240}}', '0', NULL, '2026-05-26 14:59:34', 1965);
INSERT INTO `sys_oper_log` VALUES (87, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":41,\"ossName\":\"banner5\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/fbf14405c3f3406ab5a051628aa14749.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778774812}}', '0', NULL, '2026-05-26 14:59:35', 2881);
INSERT INTO `sys_oper_log` VALUES (88, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":42,\"ossName\":\"banner6\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/f5e2aacc1e464de38155b470d8c1ea82.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778775098}}', '0', NULL, '2026-05-26 14:59:35', 1943);
INSERT INTO `sys_oper_log` VALUES (89, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":43,\"ossName\":\"banner3\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/d18b45ff913e442190fe25538707e8a3.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778775105}}', '0', NULL, '2026-05-26 14:59:35', 1607);
INSERT INTO `sys_oper_log` VALUES (90, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":44,\"ossName\":\"banner6\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/29a7e0ac760d42fdb54f4373504e83fe.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778775184}}', '0', NULL, '2026-05-26 14:59:35', 1528);
INSERT INTO `sys_oper_log` VALUES (91, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":45,\"ossName\":\"banner6\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/46dd55efdfd7477197605f09d228ca49.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778775920}}', '0', NULL, '2026-05-26 14:59:36', 1592);
INSERT INTO `sys_oper_log` VALUES (92, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":46,\"ossName\":\"banner6\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/c629926519924f35acfe330a4677f89c.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778776289}}', '0', NULL, '2026-05-26 14:59:36', 1376);
INSERT INTO `sys_oper_log` VALUES (93, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":47,\"ossName\":\"banner4\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/7685b8d8a5e14973b2a8dcb6b56c0011.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778776582}}', '0', NULL, '2026-05-26 14:59:37', 1316);
INSERT INTO `sys_oper_log` VALUES (94, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":48,\"ossName\":\"banner5\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/4d32a417ed1242f7a5e9c9af5de0ca1d.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778778102}}', '0', NULL, '2026-05-26 14:59:38', 1200);
INSERT INTO `sys_oper_log` VALUES (95, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":49,\"ossName\":\"banner6\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/952d43c0449e4b229a65bdbd467fd090.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778779458}}', '0', NULL, '2026-05-26 14:59:39', 1278);
INSERT INTO `sys_oper_log` VALUES (96, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":50,\"ossName\":\"banner1\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/2a1a66e83f93461fab2b2926c8b417f2.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778780846}}', '0', NULL, '2026-05-26 14:59:41', 1304);
INSERT INTO `sys_oper_log` VALUES (97, '批量删除 OSS 图片', 3, 'net.wanho.media.admin.MediaOssController.deleteImages()', 'DELETE', 'admin', '/admin/media/oss', '127.0.0.1', '{\"ossIds\":[50,49,48,47,46,45,44,43,42,41,39,38,40,37,36,35,33,32,34,31,30,29,27,28,26,25,23,22,24,21,20,19,18,17,16,15,14,13,8,12,11,10,9]}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 14:59:47', 984);
INSERT INTO `sys_oper_log` VALUES (98, '删除 OSS 分类', 3, 'net.wanho.media.admin.MediaOssController.deleteCategories()', 'DELETE', 'admin', '/admin/media/oss/category', '127.0.0.1', '[3]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:02:42', 32);
INSERT INTO `sys_oper_log` VALUES (99, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"targetCategoryId\":1,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:02:48', 67);
INSERT INTO `sys_oper_log` VALUES (100, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"targetCategoryId\":1,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:02:49', 63);
INSERT INTO `sys_oper_log` VALUES (101, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"targetCategoryId\":5,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:02:50', 62);
INSERT INTO `sys_oper_log` VALUES (102, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":6,\"targetCategoryId\":4,\"direction\":\"down\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:02:51', 86);
INSERT INTO `sys_oper_log` VALUES (103, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":51,\"ossName\":\"banner1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/0ed51813e9e74a75a2917f576324cdfb.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778982682}}', '0', NULL, '2026-05-26 15:03:03', 1544);
INSERT INTO `sys_oper_log` VALUES (104, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":52,\"ossName\":\"banner1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/4d8b5df7f79b4068b94df2df1bc1ce31.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778982932}}', '0', NULL, '2026-05-26 15:03:03', 1794);
INSERT INTO `sys_oper_log` VALUES (105, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":53,\"ossName\":\"banner2\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/f58cadfd8b12419bb6ff027c6500c18d.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778983922}}', '0', NULL, '2026-05-26 15:03:04', 1173);
INSERT INTO `sys_oper_log` VALUES (106, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":54,\"ossName\":\"banner2\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/78635c06b3dd4f558e5d79bd1c5531aa.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778984162}}', '0', NULL, '2026-05-26 15:03:04', 1156);
INSERT INTO `sys_oper_log` VALUES (107, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":55,\"ossName\":\"banner3\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/95284ee8eeab4058b089fa0fef89d186.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778985293}}', '0', NULL, '2026-05-26 15:03:05', 1284);
INSERT INTO `sys_oper_log` VALUES (108, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":56,\"ossName\":\"banner3\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/86364d528d484c56990e396214f9cb2c.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778985737}}', '0', NULL, '2026-05-26 15:03:06', 1507);
INSERT INTO `sys_oper_log` VALUES (109, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":57,\"ossName\":\"banner4\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/2b0cffcf9aed4e15bc44990d5841af6e.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778986847}}', '0', NULL, '2026-05-26 15:03:07', 1474);
INSERT INTO `sys_oper_log` VALUES (110, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":58,\"ossName\":\"banner4\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/de852413b58f4ce38447f9369f8f859b.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778987184}}', '0', NULL, '2026-05-26 15:03:07', 1331);
INSERT INTO `sys_oper_log` VALUES (111, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":59,\"ossName\":\"banner5\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/7d93c99639b34902b46cd37eddd3f95c.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778988160}}', '0', NULL, '2026-05-26 15:03:08', 1244);
INSERT INTO `sys_oper_log` VALUES (112, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":60,\"ossName\":\"banner5\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/49b02028b2c34b46b3bf649d1b5e45d4.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778988592}}', '0', NULL, '2026-05-26 15:03:09', 1327);
INSERT INTO `sys_oper_log` VALUES (113, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":61,\"ossName\":\"banner6\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/2e4af770e50e4fd09168cf5d4283b24c.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778989722}}', '0', NULL, '2026-05-26 15:03:10', 1493);
INSERT INTO `sys_oper_log` VALUES (114, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":62,\"ossName\":\"banner6\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/792f011398c04dff8c95b9d2750e65b5.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778990000}}', '0', NULL, '2026-05-26 15:03:10', 1323);
INSERT INTO `sys_oper_log` VALUES (115, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":63,\"ossName\":\"banner1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a4e9524ea729497b9e59478f653f99c0.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778991186}}', '0', NULL, '2026-05-26 15:03:11', 1366);
INSERT INTO `sys_oper_log` VALUES (116, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":64,\"ossName\":\"1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/273426202b6547faa003a7eb8c401f97.jpeg\",\"ossFileName\":\"1.jpeg\",\"ossFileSize\":16548,\"mimeType\":\"image/jpeg\",\"width\":610,\"height\":610,\"createTime\":1779778991304}}', '0', NULL, '2026-05-26 15:03:11', 89);
INSERT INTO `sys_oper_log` VALUES (117, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":65,\"ossName\":\"2\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/2421b86de468499abbfdde0320dedc99.jpeg\",\"ossFileName\":\"2.jpeg\",\"ossFileSize\":32362,\"mimeType\":\"image/jpeg\",\"width\":1100,\"height\":1100,\"createTime\":1779778991429}}', '0', NULL, '2026-05-26 15:03:11', 98);
INSERT INTO `sys_oper_log` VALUES (118, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":66,\"ossName\":\"banner1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/e2423c63a2594d2f8f30d546b2d583f8.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779778991535}}', '0', NULL, '2026-05-26 15:03:12', 1457);
INSERT INTO `sys_oper_log` VALUES (119, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":67,\"ossName\":\"1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/5cc8c51f38ca4f4fbc7051ce607d3442.jpeg\",\"ossFileName\":\"1.jpeg\",\"ossFileSize\":16548,\"mimeType\":\"image/jpeg\",\"width\":610,\"height\":610,\"createTime\":1779778991691}}', '0', NULL, '2026-05-26 15:03:12', 117);
INSERT INTO `sys_oper_log` VALUES (120, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":68,\"ossName\":\"2\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1d5d860f4dff421a9f19ffc5ef991eff.jpeg\",\"ossFileName\":\"2.jpeg\",\"ossFileSize\":32362,\"mimeType\":\"image/jpeg\",\"width\":1100,\"height\":1100,\"createTime\":1779778991827}}', '0', NULL, '2026-05-26 15:03:12', 103);
INSERT INTO `sys_oper_log` VALUES (121, '批量删除 OSS 图片', 3, 'net.wanho.media.admin.MediaOssController.deleteImages()', 'DELETE', 'admin', '/admin/media/oss', '127.0.0.1', '{\"ossIds\":[66,67,68,63,64,65,61,62,60,59,57,58,56,55,53,54,51,52]}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:03:46', 518);
INSERT INTO `sys_oper_log` VALUES (122, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":69,\"ossName\":\"banner1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/4b9867b9d4dc40cab9c46c7fad00d54e.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779053623}}', '0', NULL, '2026-05-26 15:04:14', 1946);
INSERT INTO `sys_oper_log` VALUES (123, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":70,\"ossName\":\"banner2\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/362471283dc84a8d8c511b57e618448d.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779054874}}', '0', NULL, '2026-05-26 15:04:15', 1183);
INSERT INTO `sys_oper_log` VALUES (124, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":71,\"ossName\":\"banner3\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/153bc39d8c714523ba56f1e8105c964e.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779056473}}', '0', NULL, '2026-05-26 15:04:16', 1523);
INSERT INTO `sys_oper_log` VALUES (125, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":72,\"ossName\":\"banner4\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/17f08a374b374841a35a5b2615354a64.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779059123}}', '0', NULL, '2026-05-26 15:04:19', 2556);
INSERT INTO `sys_oper_log` VALUES (126, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":73,\"ossName\":\"banner5\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/ad43507eb51247a58103c0d7335d13bd.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779060511}}', '0', NULL, '2026-05-26 15:04:21', 1305);
INSERT INTO `sys_oper_log` VALUES (127, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":74,\"ossName\":\"banner6\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/d3740b856d1f46e98319677fa3ea6233.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779062019}}', '0', NULL, '2026-05-26 15:04:22', 1447);
INSERT INTO `sys_oper_log` VALUES (128, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":75,\"ossName\":\"banner1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/6dabb39d308f4192a858ac68aa32f5bf.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779063648}}', '0', NULL, '2026-05-26 15:04:24', 1540);
INSERT INTO `sys_oper_log` VALUES (129, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":76,\"ossName\":\"1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/6afa946e0a2b4997b0ee223007f5c700.jpeg\",\"ossFileName\":\"1.jpeg\",\"ossFileSize\":16548,\"mimeType\":\"image/jpeg\",\"width\":610,\"height\":610,\"createTime\":1779779063790}}', '0', NULL, '2026-05-26 15:04:24', 125);
INSERT INTO `sys_oper_log` VALUES (130, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":77,\"ossName\":\"2\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/9530645d30714b968bc68167c2704d2c.jpeg\",\"ossFileName\":\"2.jpeg\",\"ossFileSize\":32362,\"mimeType\":\"image/jpeg\",\"width\":1100,\"height\":1100,\"createTime\":1779779063933}}', '0', NULL, '2026-05-26 15:04:24', 108);
INSERT INTO `sys_oper_log` VALUES (131, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":78,\"ossName\":\"12\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/6ddd1e3abb6547469c75ddd5fabd5b08.jpeg\",\"ossFileName\":\"12.jpeg\",\"ossFileSize\":51205,\"mimeType\":\"image/jpeg\",\"width\":658,\"height\":658,\"createTime\":1779779064087}}', '0', NULL, '2026-05-26 15:04:24', 129);
INSERT INTO `sys_oper_log` VALUES (132, '批量删除 OSS 图片', 3, 'net.wanho.media.admin.MediaOssController.deleteImages()', 'DELETE', 'admin', '/admin/media/oss', '127.0.0.1', '{\"ossIds\":[75,76,77,78,74,73,72,71,70,69]}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:04:54', 269);
INSERT INTO `sys_oper_log` VALUES (133, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":79,\"ossName\":\"12\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/efe0ef205a0c4ce3b2542f8da9afc338.jpeg\",\"ossFileName\":\"12.jpeg\",\"ossFileSize\":51205,\"mimeType\":\"image/jpeg\",\"width\":658,\"height\":658,\"createTime\":1779779273281}}', '0', NULL, '2026-05-26 15:07:53', 160);
INSERT INTO `sys_oper_log` VALUES (134, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":80,\"ossName\":\"1\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/5334b477856044bfa33803de05d75ce0.jpeg\",\"ossFileName\":\"1.jpeg\",\"ossFileSize\":16548,\"mimeType\":\"image/jpeg\",\"width\":610,\"height\":610,\"createTime\":1779779312193}}', '0', NULL, '2026-05-26 15:08:32', 101);
INSERT INTO `sys_oper_log` VALUES (135, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":81,\"ossName\":\"2\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1d5777d9b97f4cc9a1361642dbd1b43c.jpeg\",\"ossFileName\":\"2.jpeg\",\"ossFileSize\":32362,\"mimeType\":\"image/jpeg\",\"width\":1100,\"height\":1100,\"createTime\":1779779337755}}', '0', NULL, '2026-05-26 15:08:58', 80);
INSERT INTO `sys_oper_log` VALUES (136, '批量删除 OSS 图片', 3, 'net.wanho.media.admin.MediaOssController.deleteImages()', 'DELETE', 'admin', '/admin/media/oss', '127.0.0.1', '{\"ossIds\":[79]}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:09:03', 60);
INSERT INTO `sys_oper_log` VALUES (137, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":82,\"ossName\":\"9\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/7c0793e6a0494f62bd9a3bf4a742c571.jpeg\",\"ossFileName\":\"9.jpeg\",\"ossFileSize\":257518,\"mimeType\":\"image/jpeg\",\"width\":3000,\"height\":3000,\"createTime\":1779779423867}}', '0', NULL, '2026-05-26 15:10:24', 248);
INSERT INTO `sys_oper_log` VALUES (138, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":83,\"ossName\":\"10\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/cab70967eb234a5fa5df1e7aa20863e3.jpeg\",\"ossFileName\":\"10.jpeg\",\"ossFileSize\":36263,\"mimeType\":\"image/jpeg\",\"width\":260,\"height\":260,\"createTime\":1779779423962}}', '0', NULL, '2026-05-26 15:10:24', 64);
INSERT INTO `sys_oper_log` VALUES (139, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":84,\"ossName\":\"11\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/700eb569b86b4973afd793ad2f1373eb.jpeg\",\"ossFileName\":\"11.jpeg\",\"ossFileSize\":38679,\"mimeType\":\"image/jpeg\",\"width\":285,\"height\":285,\"createTime\":1779779424053}}', '0', NULL, '2026-05-26 15:10:24', 62);
INSERT INTO `sys_oper_log` VALUES (140, '批量删除 OSS 图片', 3, 'net.wanho.media.admin.MediaOssController.deleteImages()', 'DELETE', 'admin', '/admin/media/oss', '127.0.0.1', '{\"ossIds\":[82,83,84,81,80]}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:10:39', 177);
INSERT INTO `sys_oper_log` VALUES (141, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":85,\"ossName\":\"1\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/207a60ec5deb422d9bf3866a14ea2b40.jpeg\",\"ossFileName\":\"1.jpeg\",\"ossFileSize\":16548,\"mimeType\":\"image/jpeg\",\"width\":610,\"height\":610,\"createTime\":1779779446332}}', '0', NULL, '2026-05-26 15:10:46', 81);
INSERT INTO `sys_oper_log` VALUES (142, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":86,\"ossName\":\"2\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/07c41b7499154850b569ad4261245568.jpeg\",\"ossFileName\":\"2.jpeg\",\"ossFileSize\":32362,\"mimeType\":\"image/jpeg\",\"width\":1100,\"height\":1100,\"createTime\":1779779446438}}', '0', NULL, '2026-05-26 15:10:46', 78);
INSERT INTO `sys_oper_log` VALUES (143, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":87,\"ossName\":\"9\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/509296e2d4324d7a8b1a0dc4344e2d14.jpeg\",\"ossFileName\":\"9.jpeg\",\"ossFileSize\":257518,\"mimeType\":\"image/jpeg\",\"width\":3000,\"height\":3000,\"createTime\":1779779446676}}', '0', NULL, '2026-05-26 15:10:47', 205);
INSERT INTO `sys_oper_log` VALUES (144, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":88,\"ossName\":\"10\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/232a8ddd58bd4e0eac6b526925e4be9d.jpeg\",\"ossFileName\":\"10.jpeg\",\"ossFileSize\":36263,\"mimeType\":\"image/jpeg\",\"width\":260,\"height\":260,\"createTime\":1779779446777}}', '0', NULL, '2026-05-26 15:10:47', 72);
INSERT INTO `sys_oper_log` VALUES (145, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":89,\"ossName\":\"11\",\"categoryId\":6,\"categoryName\":\"人物头像\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a3886845409c43cab5dfe9589218b6f0.jpeg\",\"ossFileName\":\"11.jpeg\",\"ossFileSize\":38679,\"mimeType\":\"image/jpeg\",\"width\":285,\"height\":285,\"createTime\":1779779446881}}', '0', NULL, '2026-05-26 15:10:47', 75);
INSERT INTO `sys_oper_log` VALUES (146, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":90,\"ossName\":\"banner1\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/668ace8c78924cee8dbdf0d17ee16e71.png\",\"ossFileName\":\"banner1.png\",\"ossFileSize\":4334892,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779459890}}', '0', NULL, '2026-05-26 15:11:00', 1540);
INSERT INTO `sys_oper_log` VALUES (147, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":91,\"ossName\":\"banner2\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/63002352eded4770b6357506fba81242.png\",\"ossFileName\":\"banner2.png\",\"ossFileSize\":3360773,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779461583}}', '0', NULL, '2026-05-26 15:11:02', 1623);
INSERT INTO `sys_oper_log` VALUES (148, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":92,\"ossName\":\"banner3\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/2c31c7e0d9504e38b910b68827389ee4.png\",\"ossFileName\":\"banner3.png\",\"ossFileSize\":4319694,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779463190}}', '0', NULL, '2026-05-26 15:11:03', 1512);
INSERT INTO `sys_oper_log` VALUES (149, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":93,\"ossName\":\"banner4\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1fbeb2ae81644f45acf1f8b850029a90.png\",\"ossFileName\":\"banner4.png\",\"ossFileSize\":4328942,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779465197}}', '0', NULL, '2026-05-26 15:11:05', 1941);
INSERT INTO `sys_oper_log` VALUES (150, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":94,\"ossName\":\"banner5\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/13bce42e820746878d4b75c8f583cdbc.png\",\"ossFileName\":\"banner5.png\",\"ossFileSize\":3883272,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779466663}}', '0', NULL, '2026-05-26 15:11:07', 1390);
INSERT INTO `sys_oper_log` VALUES (151, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":95,\"ossName\":\"banner6\",\"categoryId\":7,\"categoryName\":\"轮播图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1a0243fdc7154ef89af67e0b0681f9a1.png\",\"ossFileName\":\"banner6.png\",\"ossFileSize\":4201801,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779468164}}', '0', NULL, '2026-05-26 15:11:08', 1426);
INSERT INTO `sys_oper_log` VALUES (152, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":96,\"ossName\":\"腹肌训练\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/db7917cb637545cb93a70b6809c9828f.png\",\"ossFileName\":\"腹肌训练.png\",\"ossFileSize\":3156872,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779484696}}', '0', NULL, '2026-05-26 15:11:25', 1120);
INSERT INTO `sys_oper_log` VALUES (153, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":97,\"ossName\":\"跑步\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/6cde87304c0848419e8495987f00e647.png\",\"ossFileName\":\"跑步.png\",\"ossFileSize\":3947285,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779486129}}', '0', NULL, '2026-05-26 15:11:26', 1356);
INSERT INTO `sys_oper_log` VALUES (154, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":98,\"ossName\":\"全身塑形\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/1c9aaf13ffad4f12b7755bbd0df02e20.png\",\"ossFileName\":\"全身塑形.png\",\"ossFileSize\":2380557,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779487239}}', '0', NULL, '2026-05-26 15:11:27', 1048);
INSERT INTO `sys_oper_log` VALUES (155, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":99,\"ossName\":\"燃脂\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/00e3a488ce6d4860aedb03f0b250bad8.png\",\"ossFileName\":\"燃脂.png\",\"ossFileSize\":3979633,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779488953}}', '0', NULL, '2026-05-26 15:11:29', 1639);
INSERT INTO `sys_oper_log` VALUES (156, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":100,\"ossName\":\"生成课程封面\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/12002791dee24f90bb8dd39bea80d2fb.png\",\"ossFileName\":\"生成课程封面.png\",\"ossFileSize\":2540232,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779490139}}', '0', NULL, '2026-05-26 15:11:30', 1125);
INSERT INTO `sys_oper_log` VALUES (157, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":101,\"ossName\":\"瑜伽\",\"categoryId\":1,\"categoryName\":\"课程图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/f956542ce04841d099475eef006a91e1.png\",\"ossFileName\":\"瑜伽.png\",\"ossFileSize\":3147219,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779491335}}', '0', NULL, '2026-05-26 15:11:31', 1132);
INSERT INTO `sys_oper_log` VALUES (158, '修改 OSS 分类', 2, 'net.wanho.media.admin.MediaOssController.updateCategory()', 'PUT', 'admin', '/admin/media/oss/category', '127.0.0.1', '{\"categoryId\":4,\"categoryName\":\"动作图片\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:12:29', 33);
INSERT INTO `sys_oper_log` VALUES (159, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":102,\"ossName\":\"波比跳\",\"categoryId\":4,\"categoryName\":\"动作图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/db733cf8114b41a5b84f353bb166c87b.png\",\"ossFileName\":\"波比跳.png\",\"ossFileSize\":1519989,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779561097}}', '0', NULL, '2026-05-26 15:12:41', 766);
INSERT INTO `sys_oper_log` VALUES (160, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":103,\"ossName\":\"俯卧撑\",\"categoryId\":4,\"categoryName\":\"动作图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a92739cecbdf4634beeb76a963c56d2a.png\",\"ossFileName\":\"俯卧撑.png\",\"ossFileSize\":3413862,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779562288}}', '0', NULL, '2026-05-26 15:12:42', 1118);
INSERT INTO `sys_oper_log` VALUES (161, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":104,\"ossName\":\"卷腹\",\"categoryId\":4,\"categoryName\":\"动作图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/74a32161d2ac404fb53d4f3e6a5deb8e.png\",\"ossFileName\":\"卷腹.png\",\"ossFileSize\":1754248,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779563006}}', '0', NULL, '2026-05-26 15:12:43', 670);
INSERT INTO `sys_oper_log` VALUES (162, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":105,\"ossName\":\"开合跳\",\"categoryId\":4,\"categoryName\":\"动作图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/5ec9dffebb4644f38235a994c79c7360.png\",\"ossFileName\":\"开合跳.png\",\"ossFileSize\":1575997,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779563649}}', '0', NULL, '2026-05-26 15:12:44', 602);
INSERT INTO `sys_oper_log` VALUES (163, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":106,\"ossName\":\"平板支撑\",\"categoryId\":4,\"categoryName\":\"动作图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/32a8768eddf147c496727e3e78a08bec.png\",\"ossFileName\":\"平板支撑.png\",\"ossFileSize\":3149792,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779564760}}', '0', NULL, '2026-05-26 15:12:45', 1042);
INSERT INTO `sys_oper_log` VALUES (164, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":107,\"ossName\":\"深蹲\",\"categoryId\":4,\"categoryName\":\"动作图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/6509f62e78b64ed5a4b0dfa4513bc80d.png\",\"ossFileName\":\"深蹲.png\",\"ossFileSize\":2455017,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779565672}}', '0', NULL, '2026-05-26 15:12:46', 858);
INSERT INTO `sys_oper_log` VALUES (165, 'OSS 分类排序', 2, 'net.wanho.media.admin.MediaOssController.moveCategory()', 'PUT', 'admin', '/admin/media/oss/category/move', '127.0.0.1', '{\"categoryId\":5,\"targetCategoryId\":1,\"direction\":\"up\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:12:53', 71);
INSERT INTO `sys_oper_log` VALUES (166, '新建 OSS 分类', 1, 'net.wanho.media.admin.MediaOssController.createCategory()', 'POST', 'admin', '/admin/media/oss/category', '127.0.0.1', '{\"categoryName\":\"商品封面\",\"parentId\":5}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:13:03', 29);
INSERT INTO `sys_oper_log` VALUES (167, '新建 OSS 分类', 1, 'net.wanho.media.admin.MediaOssController.createCategory()', 'POST', 'admin', '/admin/media/oss/category', '127.0.0.1', '{\"categoryName\":\"秒杀商品\",\"parentId\":5}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:13:11', 43);
INSERT INTO `sys_oper_log` VALUES (168, '新建 OSS 分类', 1, 'net.wanho.media.admin.MediaOssController.createCategory()', 'POST', 'admin', '/admin/media/oss/category', '127.0.0.1', '{\"categoryName\":\"品牌图片\",\"parentId\":5}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:13:21', 30);
INSERT INTO `sys_oper_log` VALUES (169, '新建 OSS 分类', 1, 'net.wanho.media.admin.MediaOssController.createCategory()', 'POST', 'admin', '/admin/media/oss/category', '127.0.0.1', '{\"categoryName\":\"商品图片\",\"parentId\":5}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:13:35', 38);
INSERT INTO `sys_oper_log` VALUES (170, '修改 OSS 分类', 2, 'net.wanho.media.admin.MediaOssController.updateCategory()', 'PUT', 'admin', '/admin/media/oss/category', '127.0.0.1', '{\"categoryId\":5,\"categoryName\":\"商品相关\"}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:13:40', 26);
INSERT INTO `sys_oper_log` VALUES (171, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":108,\"ossName\":\"可调节哑铃套装\",\"categoryId\":8,\"categoryName\":\"商品封面\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/d323add7ca4f478f885a1b1c4f979491.png\",\"ossFileName\":\"可调节哑铃套装.png\",\"ossFileSize\":2136774,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779630842}}', '0', NULL, '2026-05-26 15:13:51', 808);
INSERT INTO `sys_oper_log` VALUES (172, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":109,\"ossName\":\"乳清蛋白粉 \",\"categoryId\":8,\"categoryName\":\"商品封面\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/aa56d4fd1ba04e5a9cf860403b4da0e8.png\",\"ossFileName\":\"乳清蛋白粉 .png\",\"ossFileSize\":1820755,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779631400}}', '0', NULL, '2026-05-26 15:13:51', 509);
INSERT INTO `sys_oper_log` VALUES (173, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":110,\"ossName\":\"速干运动T恤\",\"categoryId\":8,\"categoryName\":\"商品封面\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/9b838462b0bb497a89587ad1cb9afdc4.png\",\"ossFileName\":\"速干运动T恤.png\",\"ossFileSize\":2719612,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779632220}}', '0', NULL, '2026-05-26 15:13:52', 753);
INSERT INTO `sys_oper_log` VALUES (174, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":111,\"ossName\":\"瑜伽垫\",\"categoryId\":8,\"categoryName\":\"商品封面\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/3bd55a6a8e8542878354527d5bf244f2.png\",\"ossFileName\":\"瑜伽垫.png\",\"ossFileSize\":1950246,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779632805}}', '0', NULL, '2026-05-26 15:13:53', 535);
INSERT INTO `sys_oper_log` VALUES (175, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":112,\"ossName\":\"专业健身手套\",\"categoryId\":8,\"categoryName\":\"商品封面\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/342cdae52b444ebfb9525f660587bfc3.png\",\"ossFileName\":\"专业健身手套.png\",\"ossFileSize\":2443818,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779633538}}', '0', NULL, '2026-05-26 15:13:54', 678);
INSERT INTO `sys_oper_log` VALUES (176, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":113,\"ossName\":\"专业跑步鞋\",\"categoryId\":8,\"categoryName\":\"商品封面\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/4ca3b25a8d854be99b2c1b2344e0992c.png\",\"ossFileName\":\"专业跑步鞋.png\",\"ossFileSize\":1981380,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779634203}}', '0', NULL, '2026-05-26 15:13:54', 594);
INSERT INTO `sys_oper_log` VALUES (177, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":114,\"ossName\":\"miao\",\"categoryId\":9,\"categoryName\":\"秒杀商品\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/17835e37898b46fd99d87f8bd5173302.png\",\"ossFileName\":\"miao.png\",\"ossFileSize\":4361684,\"mimeType\":\"image/png\",\"width\":2730,\"height\":1535,\"createTime\":1779779649825}}', '0', NULL, '2026-05-26 15:14:10', 1434);
INSERT INTO `sys_oper_log` VALUES (178, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":115,\"ossName\":\"361\",\"categoryId\":10,\"categoryName\":\"品牌图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/8cf98d393aaa4df8a9b077c540f18c5c.jpg\",\"ossFileName\":\"361.jpg\",\"ossFileSize\":11524,\"mimeType\":\"image/jpeg\",\"width\":324,\"height\":242,\"createTime\":1779779655442}}', '0', NULL, '2026-05-26 15:14:15', 96);
INSERT INTO `sys_oper_log` VALUES (179, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":116,\"ossName\":\"anta\",\"categoryId\":10,\"categoryName\":\"品牌图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/712a5e4e5f43468d96761dab61c9b376.jpg\",\"ossFileName\":\"anta.jpg\",\"ossFileSize\":12923,\"mimeType\":\"image/jpeg\",\"width\":324,\"height\":242,\"createTime\":1779779655532}}', '0', NULL, '2026-05-26 15:14:16', 60);
INSERT INTO `sys_oper_log` VALUES (180, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":117,\"ossName\":\"erke\",\"categoryId\":10,\"categoryName\":\"品牌图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/fafb38c3686b4f658fc4f9387d473468.jpg\",\"ossFileName\":\"erke.jpg\",\"ossFileSize\":13732,\"mimeType\":\"image/jpeg\",\"width\":325,\"height\":243,\"createTime\":1779779655623}}', '0', NULL, '2026-05-26 15:14:16', 60);
INSERT INTO `sys_oper_log` VALUES (181, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":118,\"ossName\":\"lining\",\"categoryId\":10,\"categoryName\":\"品牌图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/f7694767941b4b22a97bedb576434b8c.jpg\",\"ossFileName\":\"lining.jpg\",\"ossFileSize\":13843,\"mimeType\":\"image/jpeg\",\"width\":325,\"height\":243,\"createTime\":1779779655720}}', '0', NULL, '2026-05-26 15:14:16', 65);
INSERT INTO `sys_oper_log` VALUES (182, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":119,\"ossName\":\"tebu\",\"categoryId\":10,\"categoryName\":\"品牌图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/8a20d169c82542c58ddb0e5aa186a4dc.jpg\",\"ossFileName\":\"tebu.jpg\",\"ossFileSize\":19031,\"mimeType\":\"image/jpeg\",\"width\":325,\"height\":243,\"createTime\":1779779655814}}', '0', NULL, '2026-05-26 15:14:16', 64);
INSERT INTO `sys_oper_log` VALUES (183, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":120,\"ossName\":\"可调节哑铃套装2\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/201a471ad59349af99a886917bcb4f05.png\",\"ossFileName\":\"可调节哑铃套装2.png\",\"ossFileSize\":2311946,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779661997}}', '0', NULL, '2026-05-26 15:14:22', 718);
INSERT INTO `sys_oper_log` VALUES (184, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":121,\"ossName\":\"可调节哑铃套装3\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/10b30c74c9b04077a7d37892fe2dc0a2.png\",\"ossFileName\":\"可调节哑铃套装3.png\",\"ossFileSize\":2171658,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779662934}}', '0', NULL, '2026-05-26 15:14:23', 886);
INSERT INTO `sys_oper_log` VALUES (185, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":122,\"ossName\":\"乳清蛋白粉2\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/110599f0b57147449586fee01e04c3cd.png\",\"ossFileName\":\"乳清蛋白粉2.png\",\"ossFileSize\":3250532,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779664097}}', '0', NULL, '2026-05-26 15:14:24', 1092);
INSERT INTO `sys_oper_log` VALUES (186, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":123,\"ossName\":\"乳清蛋白粉3 \",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/ae5d13288fc4403f9a024c792facfcef.png\",\"ossFileName\":\"乳清蛋白粉3 .png\",\"ossFileSize\":1900582,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779664753}}', '0', NULL, '2026-05-26 15:14:25', 587);
INSERT INTO `sys_oper_log` VALUES (187, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":124,\"ossName\":\"速干运动T恤2\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/ab43f3447e664c30ad7f9f864651da07.png\",\"ossFileName\":\"速干运动T恤2.png\",\"ossFileSize\":2291933,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779665525}}', '0', NULL, '2026-05-26 15:14:26', 715);
INSERT INTO `sys_oper_log` VALUES (188, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":125,\"ossName\":\"速干运动T恤3\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/d5e1a5e7fdbc4384b8192cb464105434.png\",\"ossFileName\":\"速干运动T恤3.png\",\"ossFileSize\":2452421,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779666311}}', '0', NULL, '2026-05-26 15:14:26', 725);
INSERT INTO `sys_oper_log` VALUES (189, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":126,\"ossName\":\"速干运动T恤4\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/7b7cd0bcc6bb47d38ecbb181b0b8d84e.png\",\"ossFileName\":\"速干运动T恤4.png\",\"ossFileSize\":2815662,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779667154}}', '0', NULL, '2026-05-26 15:14:27', 774);
INSERT INTO `sys_oper_log` VALUES (190, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":127,\"ossName\":\"瑜伽垫2\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/50fa22ec30124d53be4b72aff7b53ef3.png\",\"ossFileName\":\"瑜伽垫2.png\",\"ossFileSize\":2475486,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779667930}}', '0', NULL, '2026-05-26 15:14:28', 726);
INSERT INTO `sys_oper_log` VALUES (191, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":128,\"ossName\":\"瑜伽垫3\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/65032a253eed4adfa44b6afec69b702e.png\",\"ossFileName\":\"瑜伽垫3.png\",\"ossFileSize\":2493262,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779668805}}', '0', NULL, '2026-05-26 15:14:29', 813);
INSERT INTO `sys_oper_log` VALUES (192, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":129,\"ossName\":\"专业健身手套2\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/9da7d5148e0040babd448d2f325e45cb.png\",\"ossFileName\":\"专业健身手套2.png\",\"ossFileSize\":2416085,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779669491}}', '0', NULL, '2026-05-26 15:14:29', 626);
INSERT INTO `sys_oper_log` VALUES (193, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":130,\"ossName\":\"专业健身手套3\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/c1cd5f0d906846e09ae21390bc0a5ce8.png\",\"ossFileName\":\"专业健身手套3.png\",\"ossFileSize\":2077232,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779670110}}', '0', NULL, '2026-05-26 15:14:30', 571);
INSERT INTO `sys_oper_log` VALUES (194, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":131,\"ossName\":\"专业跑步鞋2\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/d01104be512a455ca8935df0132c74cb.png\",\"ossFileName\":\"专业跑步鞋2.png\",\"ossFileSize\":2105756,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779670753}}', '0', NULL, '2026-05-26 15:14:31', 590);
INSERT INTO `sys_oper_log` VALUES (195, '上传 OSS 图片', 1, 'net.wanho.media.admin.MediaOssController.upload()', 'POST', 'admin', '/admin/media/oss/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"ossId\":132,\"ossName\":\"专业跑步鞋3\",\"categoryId\":11,\"categoryName\":\"商品图片\",\"ossUrl\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/3ecb33380d5e43fabc1ce1ae60d0ab43.png\",\"ossFileName\":\"专业跑步鞋3.png\",\"ossFileSize\":1985528,\"mimeType\":\"image/png\",\"width\":2048,\"height\":2048,\"createTime\":1779779671367}}', '0', NULL, '2026-05-26 15:14:31', 563);
INSERT INTO `sys_oper_log` VALUES (196, '批量删除 VOD 视频', 3, 'net.wanho.media.admin.MediaVodController.deleteVideos()', 'DELETE', 'admin', '/admin/media/vod', '127.0.0.1', '{\"vodAssetIds\":[17]}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:21:56', 864);
INSERT INTO `sys_oper_log` VALUES (197, '删除 VOD 分类', 3, 'net.wanho.media.admin.MediaVodController.deleteCategories()', 'DELETE', 'admin', '/admin/media/vod/category', '127.0.0.1', '[2]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:22:18', 54);
INSERT INTO `sys_oper_log` VALUES (198, '删除 VOD 分类', 3, 'net.wanho.media.admin.MediaVodController.deleteCategories()', 'DELETE', 'admin', '/admin/media/vod/category', '127.0.0.1', '[3]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:22:19', 64);
INSERT INTO `sys_oper_log` VALUES (199, '删除 VOD 分类', 3, 'net.wanho.media.admin.MediaVodController.deleteCategories()', 'DELETE', 'admin', '/admin/media/vod/category', '127.0.0.1', '[1]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:22:20', 61);
INSERT INTO `sys_oper_log` VALUES (200, '新建 VOD 分类', 1, 'net.wanho.media.admin.MediaVodController.createCategory()', 'POST', 'admin', '/admin/media/vod/category', '127.0.0.1', '{\"categoryName\":\"动作库\",\"parentId\":0}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:22:25', 63);
INSERT INTO `sys_oper_log` VALUES (201, '上传 VOD 视频', 1, 'net.wanho.media.admin.MediaVodController.upload()', 'POST', 'admin', '/admin/media/vod/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"vodAssetId\":18,\"vodName\":\"腹肌\",\"categoryId\":4,\"categoryName\":\"动作库\",\"vodVideoId\":\"206434aa58d371f1af6a5107e0c90102\",\"vodSize\":887628,\"vodStatus\":\"Uploading\",\"createTime\":1779780154540}}', '0', NULL, '2026-05-26 15:22:35', 1200);
INSERT INTO `sys_oper_log` VALUES (202, '批量删除 VOD 视频', 3, 'net.wanho.media.admin.MediaVodController.deleteVideos()', 'DELETE', 'admin', '/admin/media/vod', '127.0.0.1', '{\"vodAssetIds\":[18]}', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 15:22:56', 195);
INSERT INTO `sys_oper_log` VALUES (203, '上传 VOD 视频', 1, 'net.wanho.media.admin.MediaVodController.upload()', 'POST', 'admin', '/admin/media/vod/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"vodAssetId\":19,\"vodName\":\"腹肌\",\"categoryId\":4,\"categoryName\":\"动作库\",\"vodVideoId\":\"10827ebb58d371f1bf834531959d0102\",\"vodSize\":887628,\"vodStatus\":\"Uploading\",\"createTime\":1779780183352}}', '0', NULL, '2026-05-26 15:23:03', 838);
INSERT INTO `sys_oper_log` VALUES (204, '上传 VOD 视频', 1, 'net.wanho.media.admin.MediaVodController.upload()', 'POST', 'admin', '/admin/media/vod/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"vodAssetId\":20,\"vodName\":\"力量\",\"categoryId\":4,\"categoryName\":\"动作库\",\"vodVideoId\":\"60f0cac958d371f1be7b5017f1e80102\",\"vodSize\":1134539,\"vodStatus\":\"Uploading\",\"createTime\":1779780207409}}', '0', NULL, '2026-05-26 15:23:27', 917);
INSERT INTO `sys_oper_log` VALUES (205, '上传 VOD 视频', 1, 'net.wanho.media.admin.MediaVodController.upload()', 'POST', 'admin', '/admin/media/vod/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"vodAssetId\":21,\"vodName\":\"燃脂\",\"categoryId\":4,\"categoryName\":\"动作库\",\"vodVideoId\":\"5037d05058d471f1ba604531959c0102\",\"vodSize\":936853,\"vodStatus\":\"Uploading\",\"createTime\":1779780433985}}', '0', NULL, '2026-05-26 15:27:14', 1739);
INSERT INTO `sys_oper_log` VALUES (206, '上传 VOD 视频', 1, 'net.wanho.media.admin.MediaVodController.upload()', 'POST', 'admin', '/admin/media/vod/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"vodAssetId\":22,\"vodName\":\"塑型\",\"categoryId\":4,\"categoryName\":\"动作库\",\"vodVideoId\":\"702e585c58d471f180185017f0e80102\",\"vodSize\":1704841,\"vodStatus\":\"Uploading\",\"createTime\":1779780453440}}', '0', NULL, '2026-05-26 15:27:33', 1083);
INSERT INTO `sys_oper_log` VALUES (207, '上传 VOD 视频', 1, 'net.wanho.media.admin.MediaVodController.upload()', 'POST', 'admin', '/admin/media/vod/upload', '127.0.0.1', '{}', '{\"msg\":\"success\",\"code\":200,\"data\":{\"vodAssetId\":23,\"vodName\":\"瑜伽\",\"categoryId\":4,\"categoryName\":\"动作库\",\"vodVideoId\":\"b0b0035d58d471f1bff16723a78f0102\",\"vodSize\":624986,\"vodStatus\":\"Uploading\",\"createTime\":1779780454195}}', '0', NULL, '2026-05-26 15:27:34', 724);
INSERT INTO `sys_oper_log` VALUES (208, '修改账号', 2, 'net.wanho.sys.admin.SysUserController.updateUser()', 'PUT', 'admin', '/admin/system/user', '127.0.0.1', '[{\"userId\":2,\"deptId\":101,\"nickName\":\"运营专员\",\"email\":\"operator@fitai.com\",\"phonenumber\":\"13900001111\",\"sex\":\"0\",\"avatar\":\"https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a3886845409c43cab5dfe9589218b6f0.jpeg\",\"status\":\"0\",\"roleIds\":[2]}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 17:59:45', 127);
INSERT INTO `sys_oper_log` VALUES (209, '修改课程分类', 2, 'net.wanho.crs.admin.CrsCategoryController.updateCategory()', 'PUT', 'admin', '/admin/course/category', '127.0.0.1', '[{\"categoryId\":1,\"parentId\":0,\"categoryName\":\"燃脂训练\",\"icon\":\"Bicycle\",\"orderNum\":1,\"status\":\"0\"}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 18:07:30', 47);
INSERT INTO `sys_oper_log` VALUES (210, '修改课程分类', 2, 'net.wanho.crs.admin.CrsCategoryController.updateCategory()', 'PUT', 'admin', '/admin/course/category', '127.0.0.1', '[{\"categoryId\":3,\"parentId\":1,\"categoryName\":\"HIIT\",\"icon\":\"Trophy\",\"orderNum\":1,\"status\":\"0\"}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 18:07:38', 51);
INSERT INTO `sys_oper_log` VALUES (211, '修改课程分类', 2, 'net.wanho.crs.admin.CrsCategoryController.updateCategory()', 'PUT', 'admin', '/admin/course/category', '127.0.0.1', '[{\"categoryId\":3,\"parentId\":1,\"categoryName\":\"HIIT2\",\"icon\":\"Trophy\",\"orderNum\":1,\"status\":\"0\"}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 18:07:52', 30);
INSERT INTO `sys_oper_log` VALUES (212, '修改课程分类', 2, 'net.wanho.crs.admin.CrsCategoryController.updateCategory()', 'PUT', 'admin', '/admin/course/category', '127.0.0.1', '[{\"categoryId\":3,\"parentId\":1,\"categoryName\":\"HIIT\",\"icon\":\"Trophy\",\"orderNum\":1,\"status\":\"0\"}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 18:07:56', 33);
INSERT INTO `sys_oper_log` VALUES (213, '修改课程分类', 2, 'net.wanho.crs.admin.CrsCategoryController.updateCategory()', 'PUT', 'admin', '/admin/course/category', '127.0.0.1', '[{\"categoryId\":2,\"parentId\":0,\"categoryName\":\"力量训练\",\"icon\":\"Medal\",\"orderNum\":2,\"status\":\"0\"}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 18:08:28', 40);
INSERT INTO `sys_oper_log` VALUES (214, '修改课程分类', 2, 'net.wanho.crs.admin.CrsCategoryController.updateCategory()', 'PUT', 'admin', '/admin/course/category', '127.0.0.1', '[{\"categoryId\":4,\"parentId\":0,\"categoryName\":\"瑜伽普拉提\",\"icon\":\"Stopwatch\",\"orderNum\":3,\"status\":\"0\"}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-26 18:08:39', 30);
INSERT INTO `sys_oper_log` VALUES (215, '新增 SPU', 1, 'net.wanho.prd.admin.PrdSpuController.createSpu()', 'POST', 'admin', '/admin/product/spu', '127.0.0.1', '[{\"categoryId\":3,\"brandId\":1,\"spuName\":\"登山杖\",\"spuSn\":\"ds-z\",\"subtitle\":\"登山杖\",\"unit\":\"件\",\"sort\":5,\"status\":\"0\",\"skus\":[{\"skuName\":\"2lb\",\"skuSn\":\"SKU-2lb\",\"price\":55,\"marketPrice\":100,\"status\":\"0\",\"quantity\":100,\"specItems\":[{\"attrId\":3,\"valueId\":6}]},{\"skuName\":\"400g\",\"skuSn\":\"SKU-400g\",\"price\":66,\"marketPrice\":120,\"status\":\"0\",\"quantity\":200,\"specItems\":[{\"attrId\":3,\"valueId\":7}]}]}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-27 09:09:04', 188);
INSERT INTO `sys_oper_log` VALUES (216, '修改角色', 2, 'net.wanho.sys.admin.SysRoleController.updateRole()', 'PUT', 'admin', '/admin/system/role', '127.0.0.1', '[{\"roleId\":2,\"roleName\":\"运营人员\",\"roleSort\":2,\"status\":\"0\",\"remark\":\"内容运营与活动配置\",\"menuIds\":[1,10,11,1101,1102,1103,1104,1105,12,1201,1202,13,1301,1302,1303,1304,1305,14,1401,1402,1403,1404,1405,15,1501,1502,1503,1504,1505,16,1601,1602,1603,1604,1605,17,1701,1702,1703,1704,1705,18,1801,1802,1803,1804,1805,1806,20,21,2101,2102,2103,2104,2105,22,2201,2202,2203,2204,2205,23,2301,2302,2303,2304,2305,24,2401,2402,2403,2404,2405,25,2501,2502,2503,2504,2505,26,2601,2602,2603,2604,90,91,9101,9102,9103,9104,9105,92,9201,9202,9203,9204,9205,93,9301,9302,9303,9304,9305,94,9401,9402,9403,9404,9405,100,101,10101,10102,10103,10104,10105,102,10201,10202,10203,10204,10205,10206,103,10301,10302,10303,10304,10305,104,10401,10402,10403,10404,10405,105,10501,10502,10503,10504,10505,106,1061,106101,106102,106103,106104,1062,106201,106202,106203,106204,107,10701,10702,10703,10704,10705,10706,10707,108,10801,10802,10803,10804,10805,109,10901,10902,10903,10904,10905,10906]}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-05-27 10:26:02', 218);
INSERT INTO `sys_oper_log` VALUES (217, '修改系统配置', 2, 'net.wanho.sys.admin.SysConfigController.updateConfig()', 'PUT', 'admin', '/admin/system/config', '127.0.0.1', '[{\"configId\":2,\"configName\":\"VOD区域\",\"configValue\":\"cn-shanghai\",\"configType\":\"Y\",\"status\":\"0\",\"remark\":\"阿里云点播区域\"}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-06-01 15:46:17', 126);
INSERT INTO `sys_oper_log` VALUES (218, '新增系统配置', 1, 'net.wanho.sys.admin.SysConfigController.createConfig()', 'POST', 'admin', '/admin/system/config', '127.0.0.1', '[{\"configName\":\"1\",\"configKey\":\"1\",\"configValue\":\"1\",\"configType\":\"N\",\"status\":\"0\",\"remark\":\"1\"}]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-06-01 15:47:01', 53);
INSERT INTO `sys_oper_log` VALUES (219, '删除系统配置', 3, 'net.wanho.sys.admin.SysConfigController.deleteConfigs()', 'DELETE', 'admin', '/admin/system/config', '127.0.0.1', '[[8]]', '{\"msg\":\"success\",\"code\":200}', '0', NULL, '2026-06-01 15:47:15', 65);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `role_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                             `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
                             `role_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串',
                             `role_sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
                             `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                             `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                             `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                             `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                             `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`role_id`) USING BTREE,
                             UNIQUE INDEX `uk_sys_role_key`(`role_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', '0', '拥有全部菜单与数据权限', '0', '超级管理员', '2026-05-26 16:19:20', '', '2026-05-26 16:19:22');
INSERT INTO `sys_role` VALUES (2, '运营人员', 'operator', 2, '1', '0', '内容运营与活动配置', '0', '超级管理员', '2026-05-26 16:19:23', '超级管理员', '2026-05-27 10:26:45');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
                                  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
                                  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
                                  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE,
                                  INDEX `idx_sys_role_dept_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色部门关联（自定义数据权限，复合主键）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
                                  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
                                  `menu_id` bigint UNSIGNED NOT NULL COMMENT '菜单ID',
                                  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE,
                                  INDEX `idx_sys_role_menu_menu`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联（复合主键）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 10);
INSERT INTO `sys_role_menu` VALUES (2, 11);
INSERT INTO `sys_role_menu` VALUES (2, 12);
INSERT INTO `sys_role_menu` VALUES (2, 13);
INSERT INTO `sys_role_menu` VALUES (2, 14);
INSERT INTO `sys_role_menu` VALUES (2, 15);
INSERT INTO `sys_role_menu` VALUES (2, 16);
INSERT INTO `sys_role_menu` VALUES (2, 17);
INSERT INTO `sys_role_menu` VALUES (2, 18);
INSERT INTO `sys_role_menu` VALUES (2, 19);
INSERT INTO `sys_role_menu` VALUES (2, 115);
INSERT INTO `sys_role_menu` VALUES (2, 116);
INSERT INTO `sys_role_menu` VALUES (2, 117);
INSERT INTO `sys_role_menu` VALUES (2, 118);
INSERT INTO `sys_role_menu` VALUES (2, 119);
INSERT INTO `sys_role_menu` VALUES (2, 120);
INSERT INTO `sys_role_menu` VALUES (2, 123);
INSERT INTO `sys_role_menu` VALUES (2, 124);
INSERT INTO `sys_role_menu` VALUES (2, 20);
INSERT INTO `sys_role_menu` VALUES (2, 21);
INSERT INTO `sys_role_menu` VALUES (2, 22);
INSERT INTO `sys_role_menu` VALUES (2, 23);
INSERT INTO `sys_role_menu` VALUES (2, 24);
INSERT INTO `sys_role_menu` VALUES (2, 25);
INSERT INTO `sys_role_menu` VALUES (2, 26);
INSERT INTO `sys_role_menu` VALUES (2, 27);
INSERT INTO `sys_role_menu` VALUES (2, 28);
INSERT INTO `sys_role_menu` VALUES (2, 30);
INSERT INTO `sys_role_menu` VALUES (2, 31);
INSERT INTO `sys_role_menu` VALUES (2, 32);
INSERT INTO `sys_role_menu` VALUES (2, 33);
INSERT INTO `sys_role_menu` VALUES (2, 34);
INSERT INTO `sys_role_menu` VALUES (2, 40);
INSERT INTO `sys_role_menu` VALUES (2, 41);
INSERT INTO `sys_role_menu` VALUES (2, 42);
INSERT INTO `sys_role_menu` VALUES (2, 43);
INSERT INTO `sys_role_menu` VALUES (2, 44);
INSERT INTO `sys_role_menu` VALUES (2, 50);
INSERT INTO `sys_role_menu` VALUES (2, 51);
INSERT INTO `sys_role_menu` VALUES (2, 52);
INSERT INTO `sys_role_menu` VALUES (2, 53);
INSERT INTO `sys_role_menu` VALUES (2, 54);
INSERT INTO `sys_role_menu` VALUES (2, 55);
INSERT INTO `sys_role_menu` VALUES (2, 56);
INSERT INTO `sys_role_menu` VALUES (2, 57);
INSERT INTO `sys_role_menu` VALUES (2, 58);
INSERT INTO `sys_role_menu` VALUES (2, 61);
INSERT INTO `sys_role_menu` VALUES (2, 62);
INSERT INTO `sys_role_menu` VALUES (2, 63);
INSERT INTO `sys_role_menu` VALUES (2, 64);
INSERT INTO `sys_role_menu` VALUES (2, 65);
INSERT INTO `sys_role_menu` VALUES (2, 66);
INSERT INTO `sys_role_menu` VALUES (2, 67);
INSERT INTO `sys_role_menu` VALUES (2, 68);
INSERT INTO `sys_role_menu` VALUES (2, 70);
INSERT INTO `sys_role_menu` VALUES (2, 71);
INSERT INTO `sys_role_menu` VALUES (2, 72);
INSERT INTO `sys_role_menu` VALUES (2, 73);
INSERT INTO `sys_role_menu` VALUES (2, 74);
INSERT INTO `sys_role_menu` VALUES (2, 75);
INSERT INTO `sys_role_menu` VALUES (2, 76);
INSERT INTO `sys_role_menu` VALUES (2, 80);
INSERT INTO `sys_role_menu` VALUES (2, 81);
INSERT INTO `sys_role_menu` VALUES (2, 82);
INSERT INTO `sys_role_menu` VALUES (2, 83);
INSERT INTO `sys_role_menu` VALUES (2, 84);
INSERT INTO `sys_role_menu` VALUES (2, 85);
INSERT INTO `sys_role_menu` VALUES (2, 90);
INSERT INTO `sys_role_menu` VALUES (2, 91);
INSERT INTO `sys_role_menu` VALUES (2, 92);
INSERT INTO `sys_role_menu` VALUES (2, 93);
INSERT INTO `sys_role_menu` VALUES (2, 94);
INSERT INTO `sys_role_menu` VALUES (2, 95);
INSERT INTO `sys_role_menu` VALUES (2, 96);
INSERT INTO `sys_role_menu` VALUES (2, 100);
INSERT INTO `sys_role_menu` VALUES (2, 101);
INSERT INTO `sys_role_menu` VALUES (2, 102);
INSERT INTO `sys_role_menu` VALUES (2, 103);
INSERT INTO `sys_role_menu` VALUES (2, 104);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 106);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 109);
INSERT INTO `sys_role_menu` VALUES (2, 659);
INSERT INTO `sys_role_menu` VALUES (2, 691);
INSERT INTO `sys_role_menu` VALUES (2, 692);
INSERT INTO `sys_role_menu` VALUES (2, 1061);
INSERT INTO `sys_role_menu` VALUES (2, 1062);
INSERT INTO `sys_role_menu` VALUES (2, 1101);
INSERT INTO `sys_role_menu` VALUES (2, 1102);
INSERT INTO `sys_role_menu` VALUES (2, 1103);
INSERT INTO `sys_role_menu` VALUES (2, 1104);
INSERT INTO `sys_role_menu` VALUES (2, 1105);
INSERT INTO `sys_role_menu` VALUES (2, 1201);
INSERT INTO `sys_role_menu` VALUES (2, 1202);
INSERT INTO `sys_role_menu` VALUES (2, 1301);
INSERT INTO `sys_role_menu` VALUES (2, 1302);
INSERT INTO `sys_role_menu` VALUES (2, 1303);
INSERT INTO `sys_role_menu` VALUES (2, 1304);
INSERT INTO `sys_role_menu` VALUES (2, 1305);
INSERT INTO `sys_role_menu` VALUES (2, 1401);
INSERT INTO `sys_role_menu` VALUES (2, 1402);
INSERT INTO `sys_role_menu` VALUES (2, 1403);
INSERT INTO `sys_role_menu` VALUES (2, 1404);
INSERT INTO `sys_role_menu` VALUES (2, 1405);
INSERT INTO `sys_role_menu` VALUES (2, 1501);
INSERT INTO `sys_role_menu` VALUES (2, 1502);
INSERT INTO `sys_role_menu` VALUES (2, 1503);
INSERT INTO `sys_role_menu` VALUES (2, 1504);
INSERT INTO `sys_role_menu` VALUES (2, 1505);
INSERT INTO `sys_role_menu` VALUES (2, 1601);
INSERT INTO `sys_role_menu` VALUES (2, 1602);
INSERT INTO `sys_role_menu` VALUES (2, 1603);
INSERT INTO `sys_role_menu` VALUES (2, 1604);
INSERT INTO `sys_role_menu` VALUES (2, 1605);
INSERT INTO `sys_role_menu` VALUES (2, 1701);
INSERT INTO `sys_role_menu` VALUES (2, 1702);
INSERT INTO `sys_role_menu` VALUES (2, 1703);
INSERT INTO `sys_role_menu` VALUES (2, 1704);
INSERT INTO `sys_role_menu` VALUES (2, 1705);
INSERT INTO `sys_role_menu` VALUES (2, 1801);
INSERT INTO `sys_role_menu` VALUES (2, 1802);
INSERT INTO `sys_role_menu` VALUES (2, 1803);
INSERT INTO `sys_role_menu` VALUES (2, 1804);
INSERT INTO `sys_role_menu` VALUES (2, 1805);
INSERT INTO `sys_role_menu` VALUES (2, 1806);
INSERT INTO `sys_role_menu` VALUES (2, 1901);
INSERT INTO `sys_role_menu` VALUES (2, 2101);
INSERT INTO `sys_role_menu` VALUES (2, 2102);
INSERT INTO `sys_role_menu` VALUES (2, 2103);
INSERT INTO `sys_role_menu` VALUES (2, 2104);
INSERT INTO `sys_role_menu` VALUES (2, 2105);
INSERT INTO `sys_role_menu` VALUES (2, 2201);
INSERT INTO `sys_role_menu` VALUES (2, 2202);
INSERT INTO `sys_role_menu` VALUES (2, 2203);
INSERT INTO `sys_role_menu` VALUES (2, 2204);
INSERT INTO `sys_role_menu` VALUES (2, 2205);
INSERT INTO `sys_role_menu` VALUES (2, 2301);
INSERT INTO `sys_role_menu` VALUES (2, 2302);
INSERT INTO `sys_role_menu` VALUES (2, 2303);
INSERT INTO `sys_role_menu` VALUES (2, 2304);
INSERT INTO `sys_role_menu` VALUES (2, 2305);
INSERT INTO `sys_role_menu` VALUES (2, 2401);
INSERT INTO `sys_role_menu` VALUES (2, 2402);
INSERT INTO `sys_role_menu` VALUES (2, 2403);
INSERT INTO `sys_role_menu` VALUES (2, 2404);
INSERT INTO `sys_role_menu` VALUES (2, 2405);
INSERT INTO `sys_role_menu` VALUES (2, 2501);
INSERT INTO `sys_role_menu` VALUES (2, 2502);
INSERT INTO `sys_role_menu` VALUES (2, 2503);
INSERT INTO `sys_role_menu` VALUES (2, 2504);
INSERT INTO `sys_role_menu` VALUES (2, 2505);
INSERT INTO `sys_role_menu` VALUES (2, 2601);
INSERT INTO `sys_role_menu` VALUES (2, 2602);
INSERT INTO `sys_role_menu` VALUES (2, 2603);
INSERT INTO `sys_role_menu` VALUES (2, 2604);
INSERT INTO `sys_role_menu` VALUES (2, 5001);
INSERT INTO `sys_role_menu` VALUES (2, 5002);
INSERT INTO `sys_role_menu` VALUES (2, 5003);
INSERT INTO `sys_role_menu` VALUES (2, 5004);
INSERT INTO `sys_role_menu` VALUES (2, 9101);
INSERT INTO `sys_role_menu` VALUES (2, 9102);
INSERT INTO `sys_role_menu` VALUES (2, 9103);
INSERT INTO `sys_role_menu` VALUES (2, 9104);
INSERT INTO `sys_role_menu` VALUES (2, 9105);
INSERT INTO `sys_role_menu` VALUES (2, 9201);
INSERT INTO `sys_role_menu` VALUES (2, 9202);
INSERT INTO `sys_role_menu` VALUES (2, 9203);
INSERT INTO `sys_role_menu` VALUES (2, 9204);
INSERT INTO `sys_role_menu` VALUES (2, 9205);
INSERT INTO `sys_role_menu` VALUES (2, 9301);
INSERT INTO `sys_role_menu` VALUES (2, 9302);
INSERT INTO `sys_role_menu` VALUES (2, 9303);
INSERT INTO `sys_role_menu` VALUES (2, 9304);
INSERT INTO `sys_role_menu` VALUES (2, 9305);
INSERT INTO `sys_role_menu` VALUES (2, 9401);
INSERT INTO `sys_role_menu` VALUES (2, 9402);
INSERT INTO `sys_role_menu` VALUES (2, 9403);
INSERT INTO `sys_role_menu` VALUES (2, 9404);
INSERT INTO `sys_role_menu` VALUES (2, 9405);
INSERT INTO `sys_role_menu` VALUES (2, 10101);
INSERT INTO `sys_role_menu` VALUES (2, 10102);
INSERT INTO `sys_role_menu` VALUES (2, 10103);
INSERT INTO `sys_role_menu` VALUES (2, 10104);
INSERT INTO `sys_role_menu` VALUES (2, 10105);
INSERT INTO `sys_role_menu` VALUES (2, 10201);
INSERT INTO `sys_role_menu` VALUES (2, 10202);
INSERT INTO `sys_role_menu` VALUES (2, 10203);
INSERT INTO `sys_role_menu` VALUES (2, 10204);
INSERT INTO `sys_role_menu` VALUES (2, 10205);
INSERT INTO `sys_role_menu` VALUES (2, 10206);
INSERT INTO `sys_role_menu` VALUES (2, 10301);
INSERT INTO `sys_role_menu` VALUES (2, 10302);
INSERT INTO `sys_role_menu` VALUES (2, 10303);
INSERT INTO `sys_role_menu` VALUES (2, 10304);
INSERT INTO `sys_role_menu` VALUES (2, 10305);
INSERT INTO `sys_role_menu` VALUES (2, 10401);
INSERT INTO `sys_role_menu` VALUES (2, 10402);
INSERT INTO `sys_role_menu` VALUES (2, 10403);
INSERT INTO `sys_role_menu` VALUES (2, 10404);
INSERT INTO `sys_role_menu` VALUES (2, 10405);
INSERT INTO `sys_role_menu` VALUES (2, 10501);
INSERT INTO `sys_role_menu` VALUES (2, 10502);
INSERT INTO `sys_role_menu` VALUES (2, 10503);
INSERT INTO `sys_role_menu` VALUES (2, 10504);
INSERT INTO `sys_role_menu` VALUES (2, 10505);
INSERT INTO `sys_role_menu` VALUES (2, 10701);
INSERT INTO `sys_role_menu` VALUES (2, 10702);
INSERT INTO `sys_role_menu` VALUES (2, 10703);
INSERT INTO `sys_role_menu` VALUES (2, 10704);
INSERT INTO `sys_role_menu` VALUES (2, 10705);
INSERT INTO `sys_role_menu` VALUES (2, 10706);
INSERT INTO `sys_role_menu` VALUES (2, 10707);
INSERT INTO `sys_role_menu` VALUES (2, 10801);
INSERT INTO `sys_role_menu` VALUES (2, 10802);
INSERT INTO `sys_role_menu` VALUES (2, 10803);
INSERT INTO `sys_role_menu` VALUES (2, 10804);
INSERT INTO `sys_role_menu` VALUES (2, 10805);
INSERT INTO `sys_role_menu` VALUES (2, 10901);
INSERT INTO `sys_role_menu` VALUES (2, 10902);
INSERT INTO `sys_role_menu` VALUES (2, 10903);
INSERT INTO `sys_role_menu` VALUES (2, 10904);
INSERT INTO `sys_role_menu` VALUES (2, 10905);
INSERT INTO `sys_role_menu` VALUES (2, 10906);
INSERT INTO `sys_role_menu` VALUES (2, 106101);
INSERT INTO `sys_role_menu` VALUES (2, 106102);
INSERT INTO `sys_role_menu` VALUES (2, 106103);
INSERT INTO `sys_role_menu` VALUES (2, 106104);
INSERT INTO `sys_role_menu` VALUES (2, 106201);
INSERT INTO `sys_role_menu` VALUES (2, 106202);
INSERT INTO `sys_role_menu` VALUES (2, 106203);
INSERT INTO `sys_role_menu` VALUES (2, 106204);
INSERT INTO `sys_role_menu` VALUES (2, 11501);
INSERT INTO `sys_role_menu` VALUES (2, 11502);
INSERT INTO `sys_role_menu` VALUES (2, 11601);
INSERT INTO `sys_role_menu` VALUES (2, 11602);
INSERT INTO `sys_role_menu` VALUES (2, 11603);
INSERT INTO `sys_role_menu` VALUES (2, 11604);
INSERT INTO `sys_role_menu` VALUES (2, 11605);
INSERT INTO `sys_role_menu` VALUES (2, 11701);
INSERT INTO `sys_role_menu` VALUES (2, 11702);
INSERT INTO `sys_role_menu` VALUES (2, 11801);
INSERT INTO `sys_role_menu` VALUES (2, 11802);
INSERT INTO `sys_role_menu` VALUES (2, 11803);
INSERT INTO `sys_role_menu` VALUES (2, 11804);
INSERT INTO `sys_role_menu` VALUES (2, 11901);
INSERT INTO `sys_role_menu` VALUES (2, 11902);
INSERT INTO `sys_role_menu` VALUES (2, 12001);
INSERT INTO `sys_role_menu` VALUES (2, 12002);
INSERT INTO `sys_role_menu` VALUES (2, 12301);
INSERT INTO `sys_role_menu` VALUES (2, 12302);
INSERT INTO `sys_role_menu` VALUES (2, 12401);
INSERT INTO `sys_role_menu` VALUES (2, 12402);
INSERT INTO `sys_role_menu` VALUES (2, 2701);
INSERT INTO `sys_role_menu` VALUES (2, 2702);
INSERT INTO `sys_role_menu` VALUES (2, 2801);
INSERT INTO `sys_role_menu` VALUES (2, 2802);
INSERT INTO `sys_role_menu` VALUES (2, 2803);
INSERT INTO `sys_role_menu` VALUES (2, 7601);
INSERT INTO `sys_role_menu` VALUES (2, 7602);
INSERT INTO `sys_role_menu` VALUES (2, 9501);
INSERT INTO `sys_role_menu` VALUES (2, 9502);
INSERT INTO `sys_role_menu` VALUES (2, 9601);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `user_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                             `dept_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '部门ID',
                             `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录账号',
                             `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
                             `user_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '用户类型（0系统用户）',
                             `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `phonenumber` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
                             `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '性别（0男 1女 2未知）',
                             `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
                             `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                             `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                             `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后登录IP',
                             `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
                             `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                             `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`user_id`) USING BTREE,
                             UNIQUE INDEX `uk_sys_user_name`(`user_name` ASC) USING BTREE,
                             INDEX `idx_sys_user_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '后台账号表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 100, 'admin', '超级管理员', '0', 'admin@fitai.com', '15888888888', '0', NULL, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '192.168.49.251', '2026-07-06 19:28:42', '超级管理员', '2026-05-26 16:11:32', 'admin', '2026-07-06 19:28:42');
INSERT INTO `sys_user` VALUES (2, 101, 'operator', '运营专员', '0', 'operator@fitai.com', '13900001111', '0', 'https://fitai-media.oss-cn-shanghai.aliyuncs.com/media/2026/05/26/a3886845409c43cab5dfe9589218b6f0.jpeg', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-05-27 10:26:29', '超级管理员', '2026-05-26 16:11:36', '运营专员', '2026-05-27 10:26:29');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
                                  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
                                  INDEX `idx_sys_user_role_role`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联（复合主键）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);




-- ==============================================
-- 模块: fitai_train (原 fitai_train 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for train_action
-- ----------------------------
DROP TABLE IF EXISTS `train_action`;
CREATE TABLE `train_action`  (
                                 `action_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '动作ID',
                                 `action_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '动作名称',
                                 `action_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '动作编码',
                                 `muscle_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '肌群',
                                 `equipment` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '器械',
                                 `difficulty` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '难度（1入门 2进阶 3高级）',
                                 `calories_per_min` decimal(5, 2) NULL DEFAULT NULL COMMENT '每分钟消耗kcal',
                                 `tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '动作要点',
                                 `common_mistakes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '常见错误',
                                 `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                 `vod_asset_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '示范视频ID(media_vod)',
                                 `vod_video_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'VOD VideoId(冗余)',
                                 `vod_cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面URL(冗余)',
                                 `vod_duration` decimal(10, 2) NULL DEFAULT NULL COMMENT '时长(冗余)',
                                 `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                 `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`action_id`) USING BTREE,
                                 UNIQUE INDEX `uk_train_action_code`(`action_code` ASC) USING BTREE,
                                 INDEX `idx_train_action_muscle`(`muscle_group` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '动作库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of train_action
-- ----------------------------
INSERT INTO `train_action` VALUES (1, '标准深蹲', 'squat', '腿部', '自重', 1, 8.50, '保持膝盖与脚尖同向，下蹲至大腿平行', '避免膝内扣、塌腰', 1, 3, 'v_demo_squat_001', 'https://demo.cover.aliyun.com/squat.jpg', 45.00, '双脚与肩同宽，下蹲至大腿平行', '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2025-05-01 09:00:00');
INSERT INTO `train_action` VALUES (2, '俯卧撑', 'pushup', '胸部', '自重', 1, 7.00, '身体成直线，肩胛稳定', '塌腰、耸肩', 2, NULL, NULL, NULL, NULL, '标准俯卧撑动作', '0', '0', '超级管理员', '2025-05-01 09:00:00', '', '2025-05-01 09:00:00');

-- ----------------------------
-- Table structure for train_device
-- ----------------------------
DROP TABLE IF EXISTS `train_device`;
CREATE TABLE `train_device`  (
                                 `device_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '设备ID',
                                 `device_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称',
                                 `device_brand` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品牌 apple/huawei/xiaomi',
                                 `device_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '型号',
                                 `connect_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ble' COMMENT '连接方式 ble/api',
                                 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                 `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`device_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '穿戴设备' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of train_device
-- ----------------------------
INSERT INTO `train_device` VALUES (1, 'Apple Watch', 'apple', 'Series 9', 'api', '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `train_device` VALUES (2, '华为手环', 'huawei', 'Band 8', 'ble', '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for train_device_bind
-- ----------------------------
DROP TABLE IF EXISTS `train_device_bind`;
CREATE TABLE `train_device_bind`  (
                                      `bind_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
                                      `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                      `device_id` bigint UNSIGNED NOT NULL COMMENT '设备ID',
                                      `device_sn` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备序列号',
                                      `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0已绑定 1已解绑）',
                                      `last_sync_time` datetime NULL DEFAULT NULL COMMENT '最后同步',
                                      `create_time` datetime NULL DEFAULT NULL COMMENT '绑定时间',
                                      `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`bind_id`) USING BTREE,
                                      INDEX `idx_train_device_bind_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备绑定' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of train_device_bind
-- ----------------------------
INSERT INTO `train_device_bind` VALUES (1, 1, 1, 'AW-SN-001', '0', '2025-05-20 08:00:00', '2025-05-20 08:00:00', '2025-05-20 08:00:00');

-- ----------------------------
-- Table structure for train_plan
-- ----------------------------
DROP TABLE IF EXISTS `train_plan`;
CREATE TABLE `train_plan`  (
                               `plan_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '计划ID',
                               `plan_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划名称',
                               `customer_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户ID(个人计划)',
                               `coach_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '教练ID',
                               `plan_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '方案类型（1系统 2个人 3AI生成）',
                               `goal` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标 fat_loss/muscle/keep',
                               `weeks` tinyint UNSIGNED NOT NULL DEFAULT 4 COMMENT '周期周数',
                               `days_per_week` tinyint UNSIGNED NOT NULL DEFAULT 3 COMMENT '每周训练天',
                               `difficulty` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '难度（1入门 2进阶 3高级）',
                               `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
                               `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
                               `plan_json` json NULL COMMENT 'AI计划JSON(可选冗余)',
                               `ai_session_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'AI会话ID',
                               `audit_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核状态（0待审 1通过 2拒绝）',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                               `lifecycle_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT 'C端生命周期（0草稿 1进行中 2已完成 3已放弃）',
                               `gen_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT 'AI生成态（0生成中 1完成 2失败）',
                               `gen_error_msg` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'AI生成失败原因',
                               `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                               `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`plan_id`) USING BTREE,
                               INDEX `idx_train_plan_customer`(`customer_id` ASC) USING BTREE,
                               INDEX `idx_train_plan_customer_status`(`customer_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of train_plan
-- ----------------------------
INSERT INTO `train_plan` VALUES (1, 'AI减脂计划-张三', 1, NULL, '3', 'fat_loss', 4, 4, 1, '2025-05-01', '2025-05-28', NULL, NULL, '1', '0', '0', '1', NULL, '0', '超级管理员', '2025-05-18 14:00:00', '', '2025-05-18 14:00:00');

-- ----------------------------
-- Table structure for train_plan_item
-- ----------------------------
DROP TABLE IF EXISTS `train_plan_item`;
CREATE TABLE `train_plan_item`  (
                                    `item_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
                                    `plan_id` bigint UNSIGNED NOT NULL COMMENT '计划ID',
                                    `week_no` tinyint UNSIGNED NOT NULL COMMENT '第几周',
                                    `day_no` tinyint UNSIGNED NOT NULL COMMENT '第几天',
                                    `action_id` bigint UNSIGNED NOT NULL COMMENT '动作ID',
                                    `action_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '动作名(冗余)',
                                    `sets` tinyint UNSIGNED NOT NULL DEFAULT 3 COMMENT '组数',
                                    `reps` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '12' COMMENT '每组次数/时长 如12、10-12、30秒',
                                    `rest_sec` smallint UNSIGNED NULL DEFAULT 60 COMMENT '休息秒',
                                    `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                    PRIMARY KEY (`item_id`) USING BTREE,
                                    INDEX `idx_train_plan_item_plan`(`plan_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划明细' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of train_plan_item
-- ----------------------------
INSERT INTO `train_plan_item` VALUES (1, 1, 1, 1, 1, '标准深蹲', 4, '12-15', 60, 1);
INSERT INTO `train_plan_item` VALUES (2, 1, 1, 1, 2, '俯卧撑', 3, '12', 60, 2);

-- ----------------------------
-- Table structure for train_record
-- ----------------------------
DROP TABLE IF EXISTS `train_record`;
CREATE TABLE `train_record`  (
                                 `record_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                                 `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                 `plan_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '计划ID',
                                 `train_date` date NOT NULL COMMENT '训练日期',
                                 `duration_min` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '时长分钟',
                                 `calories` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '消耗kcal',
                                 `completion_pct` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '完成度%',
                                 `feeling` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '训练感受 1-5',
                                 `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                 `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'app' COMMENT '来源 app/device',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 PRIMARY KEY (`record_id`) USING BTREE,
                                 INDEX `idx_train_record_customer_date`(`customer_id` ASC, `train_date` ASC) USING BTREE,
                                 INDEX `idx_train_record_plan`(`plan_id` ASC) USING BTREE,
                                 INDEX `idx_train_record_date`(`train_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of train_record
-- ----------------------------
INSERT INTO `train_record` VALUES (1, 1, 1, '2025-05-20', 45, 320.50, 100, 4, '完成全部计划动作', 'app', '2025-05-20 21:00:00');
INSERT INTO `train_record` VALUES (2, 3, NULL, '2025-05-21', 60, 450.00, 85, 5, '增肌日训练', 'app', '2025-05-21 20:30:00');
INSERT INTO `train_record` VALUES (3, 1, NULL, '2026-07-06', 30, 210.00, 100, 4, '首页快捷打卡', 'app', '2026-07-06 11:42:11');

-- ----------------------------
-- Table structure for train_record_item
-- ----------------------------
DROP TABLE IF EXISTS `train_record_item`;
CREATE TABLE `train_record_item`  (
                                      `item_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
                                      `record_id` bigint UNSIGNED NOT NULL COMMENT '训练记录ID',
                                      `action_id` bigint UNSIGNED NOT NULL COMMENT '动作ID',
                                      `action_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '动作名(冗余)',
                                      `sets_done` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '完成组数',
                                      `reps_done` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '每组次数',
                                      `load_kg` decimal(6, 2) NULL DEFAULT NULL COMMENT '负重kg',
                                      `sets_json` json NULL COMMENT '组数明细JSON',
                                      `duration_sec` int UNSIGNED NULL DEFAULT NULL COMMENT '耗时秒',
                                      `calories` decimal(8, 2) NULL DEFAULT NULL COMMENT '消耗kcal',
                                      `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                                      PRIMARY KEY (`item_id`) USING BTREE,
                                      INDEX `idx_train_record_item_record`(`record_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练记录明细' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of train_record_item
-- ----------------------------
INSERT INTO `train_record_item` VALUES (1, 1, 1, '标准深蹲', 4, 15, 20.00, '[{\"reps\": 15, \"weight\": 20, \"completed\": true}]', 480, 180.00, 1);
INSERT INTO `train_record_item` VALUES (2, 1, 2, '俯卧撑', 3, 12, NULL, '[{\"reps\": 12, \"completed\": true}]', 300, 140.50, 2);




-- ==============================================
-- 模块: fitai_user (原 fitai_user 库，已合并至 fitai_all)
-- ==============================================

-- ----------------------------
-- Table structure for usr_address
-- ----------------------------
DROP TABLE IF EXISTS `usr_address`;
CREATE TABLE `usr_address`  (
                                `address_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID',
                                `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                `receiver_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人',
                                `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
                                `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省',
                                `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '市',
                                `district` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区',
                                `detail_addr` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
                                `postal_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮编',
                                `default_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '默认（0否 1是）',
                                `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`address_id`) USING BTREE,
                                INDEX `idx_usr_address_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收货地址' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_address
-- ----------------------------
INSERT INTO `usr_address` VALUES (1, 1, '张三', '13800000001', '上海市', '上海市', '浦东新区', 'XX路1号101室', NULL, '1', '0', '0', '2025-05-20 10:00:00', '2025-05-20 10:00:00');

-- ----------------------------
-- Table structure for usr_body_metric
-- ----------------------------
DROP TABLE IF EXISTS `usr_body_metric`;
CREATE TABLE `usr_body_metric`  (
                                    `metric_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '体测ID',
                                    `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                    `record_date` date NOT NULL COMMENT '记录日期',
                                    `height_cm` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高cm',
                                    `weight_kg` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重kg',
                                    `bmi` decimal(4, 2) NULL DEFAULT NULL COMMENT 'BMI',
                                    `body_fat_pct` decimal(4, 2) NULL DEFAULT NULL COMMENT '体脂率%',
                                    `muscle_kg` decimal(5, 2) NULL DEFAULT NULL COMMENT '肌肉量kg',
                                    `waist_cm` decimal(5, 2) NULL DEFAULT NULL COMMENT '腰围cm',
                                    `chest_cm` decimal(5, 2) NULL DEFAULT NULL COMMENT '胸围cm',
                                    `hip_cm` decimal(5, 2) NULL DEFAULT NULL COMMENT '臀围cm',
                                    `arm_cm` decimal(5, 2) NULL DEFAULT NULL COMMENT '臂围cm',
                                    `thigh_cm` decimal(5, 2) NULL DEFAULT NULL COMMENT '大腿围cm',
                                    `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'manual' COMMENT '来源 manual/device/ai',
                                    `device_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '设备ID',
                                    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                    PRIMARY KEY (`metric_id`) USING BTREE,
                                    INDEX `idx_usr_body_customer_date`(`customer_id` ASC, `record_date` ASC) USING BTREE,
                                    INDEX `idx_usr_body_date`(`record_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '体测数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_body_metric
-- ----------------------------
INSERT INTO `usr_body_metric` VALUES (1, 1, '2025-04-01', 175.00, 74.20, 24.24, 24.10, 32.50, 86.00, 96.00, 98.00, 32.00, 58.00, 'manual', NULL, '初始体测', '2025-04-01 10:00:00');
INSERT INTO `usr_body_metric` VALUES (2, 1, '2025-05-01', 175.00, 73.00, 23.84, 22.50, 33.10, 84.50, 95.00, 97.00, 32.50, 57.50, 'manual', NULL, '月度复测', '2025-05-01 10:00:00');
INSERT INTO `usr_body_metric` VALUES (3, 1, '2025-05-15', 175.00, 72.50, 23.67, 21.80, 33.80, 83.00, 94.50, 96.50, 33.00, 57.00, 'device', 1, 'Apple Watch同步', '2025-05-15 08:00:00');
INSERT INTO `usr_body_metric` VALUES (4, 3, '2025-05-10', 180.00, 85.00, 26.23, 18.20, 42.30, 88.00, 110.00, 102.00, 38.00, 62.00, 'manual', NULL, '增肌期记录', '2025-05-10 09:00:00');
INSERT INTO `usr_body_metric` VALUES (5, 4, '2025-05-18', 172.00, 68.00, 22.99, 16.50, 30.20, 78.00, 92.00, 94.00, 30.00, 54.00, 'device', 2, '华为手环同步', '2025-05-18 07:00:00');
INSERT INTO `usr_body_metric` VALUES (6, 5, '2025-05-12', 160.00, 58.50, 22.85, 28.30, 24.10, 72.00, 88.00, 92.00, 26.00, 50.00, 'manual', NULL, '产后恢复起点', '2025-05-12 14:00:00');

-- ----------------------------
-- Table structure for usr_coach
-- ----------------------------
DROP TABLE IF EXISTS `usr_coach`;
CREATE TABLE `usr_coach`  (
                              `coach_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '教练ID',
                              `coach_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
                              `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头衔',
                              `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
                              `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
                              `specialty` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专长标签',
                              `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '简介',
                              `cert_json` json NULL COMMENT '资质证书',
                              `rating` decimal(3, 2) NOT NULL DEFAULT 5.00 COMMENT '评分(冗余)',
                              `student_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '学员数(冗余)',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`coach_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_coach
-- ----------------------------
INSERT INTO `usr_coach` VALUES (1, '王强', '高级私教', NULL, '13700001001', '增肌,力量训练,体态矫正', '原国家队体能教练助理，10年一线私教经验，擅长新手入门与周期化训练。', '[{\"name\": \"ACE-CPT\", \"year\": 2018}, {\"name\": \"NSCA-CSCS\", \"year\": 2020}]', 4.92, 286, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `usr_coach` VALUES (2, '李婉清', '瑜伽导师', NULL, '13700001002', '哈他瑜伽,普拉提,产后修复', '印度瑞诗凯诗瑜伽学院200小时认证，专注女性体态与柔韧提升。', '[{\"name\": \"RYT-200\", \"year\": 2019}]', 4.88, 203, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `usr_coach` VALUES (3, '陈浩', '跑步教练', NULL, '13700001003', '马拉松,有氧耐力,减脂', '全马PB 2:58，帮助200+学员完成首个半马。', '[{\"name\": \"中国田协马拉松教练\", \"year\": 2021}]', 4.85, 156, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for usr_coach_bind
-- ----------------------------
DROP TABLE IF EXISTS `usr_coach_bind`;
CREATE TABLE `usr_coach_bind`  (
                                   `bind_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
                                   `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                   `coach_id` bigint UNSIGNED NOT NULL COMMENT '教练ID',
                                   `coach_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '教练名(冗余)',
                                   `bind_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '类型（1跟练 2私教）',
                                   `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0有效 1已解绑）',
                                   `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
                                   `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   PRIMARY KEY (`bind_id`) USING BTREE,
                                   INDEX `idx_usr_coach_bind_customer`(`customer_id` ASC) USING BTREE,
                                   INDEX `idx_usr_coach_bind_coach`(`coach_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户教练绑定' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_coach_bind
-- ----------------------------
INSERT INTO `usr_coach_bind` VALUES (1, 1, 1, '王强', '2', '0', '2025-04-01 10:00:00', NULL, '2025-04-01 10:00:00', '2025-04-01 10:00:00');
INSERT INTO `usr_coach_bind` VALUES (2, 4, 3, '陈浩', '1', '0', '2025-05-01 09:00:00', NULL, '2025-05-01 09:00:00', '2025-05-01 09:00:00');

-- ----------------------------
-- Table structure for usr_coach_booking
-- ----------------------------
DROP TABLE IF EXISTS `usr_coach_booking`;
CREATE TABLE `usr_coach_booking`  (
                                      `booking_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '预约ID',
                                      `booking_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约单号',
                                      `coach_id` bigint UNSIGNED NOT NULL COMMENT '教练ID',
                                      `coach_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '教练名(冗余)',
                                      `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                      `customer_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称(冗余)',
                                      `booking_date` date NOT NULL COMMENT '预约日期',
                                      `time_slot` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '时段(如 09:00-10:00)',
                                      `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注(训练目标等)',
                                      `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0待确认 1已确认 2已完成 3已取消）',
                                      `cancel_reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
                                      `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`booking_id`) USING BTREE,
                                      UNIQUE INDEX `uk_usr_booking_no`(`booking_no` ASC) USING BTREE,
                                      INDEX `idx_usr_booking_slot`(`coach_id` ASC, `booking_date` ASC, `time_slot` ASC) USING BTREE,
                                      INDEX `idx_usr_booking_customer`(`customer_id` ASC, `booking_date` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练约课预约单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_coach_booking
-- ----------------------------
INSERT INTO `usr_coach_booking` VALUES (1, 'BK202505200001', 1, '王强', 1, '健身小白', '2025-05-22', '10:00-11:00', '想学习深蹲和硬拉的标准动作', '2', NULL, '2025-05-20 09:00:00', '2025-05-22 11:00:00');
INSERT INTO `usr_coach_booking` VALUES (2, 'BK202505210001', 2, '李婉清', 2, '瑜伽达人Lily', '2025-05-23', '15:00-16:00', '产后修复课程咨询', '1', NULL, '2025-05-21 14:00:00', '2025-05-21 15:00:00');

-- ----------------------------
-- Table structure for usr_coach_schedule
-- ----------------------------
DROP TABLE IF EXISTS `usr_coach_schedule`;
CREATE TABLE `usr_coach_schedule`  (
                                       `schedule_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '排课ID',
                                       `coach_id` bigint UNSIGNED NOT NULL COMMENT '教练ID',
                                       `work_days` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1,2,3,4,5,6,7' COMMENT '出勤星期(1周一~7周日,逗号分隔)',
                                       `work_slots` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开放时段(逗号分隔,空=全部默认时段)',
                                       `leave_dates` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请假日期(yyyy-MM-dd,逗号分隔)',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                       `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`schedule_id`) USING BTREE,
                                       UNIQUE INDEX `uk_usr_schedule_coach`(`coach_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练排课表(周模板+请假)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_coach_schedule
-- ----------------------------
INSERT INTO `usr_coach_schedule` VALUES (1, 1, '1,2,3,4,5', '09:00-10:00,10:00-11:00,15:00-16:00,16:00-17:00,17:00-18:00,18:00-19:00', NULL, '2025-05-01 10:00:00', '2025-05-01 10:00:00');
INSERT INTO `usr_coach_schedule` VALUES (2, 2, '2,4,6,7', '10:00-11:00,11:00-12:00,14:00-15:00,15:00-16:00', NULL, '2025-05-01 10:00:00', '2025-05-01 10:00:00');

-- ----------------------------
-- Table structure for usr_customer
-- ----------------------------
DROP TABLE IF EXISTS `usr_customer`;
CREATE TABLE `usr_customer`  (
                                 `customer_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'C端用户ID',
                                 `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
                                 `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
                                 `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                                 `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
                                 `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
                                 `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '2' COMMENT '性别（0男 1女 2未知）',
                                 `birthday` date NULL DEFAULT NULL COMMENT '生日',
                                 `height_cm` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高cm(冗余最新体测)',
                                 `weight_kg` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重kg(冗余最新体测)',
                                 `body_fat_pct` decimal(4, 2) NULL DEFAULT NULL COMMENT '体脂率%(冗余)',
                                 `fitness_goal` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '健身目标 1减脂 2增肌 3塑形 4健康',
                                 `fitness_level` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '训练水平 1新手 2进阶 3高级',
                                 `member_level_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '会员等级ID(冗余)',
                                 `member_level_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员等级名(冗余)',
                                 `growth_value` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '成长值(会员等级)',
                                 `points_balance` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '积分余额(冗余)',
                                 `register_source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'app' COMMENT '注册来源',
                                 `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录',
                                 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1冻结）',
                                 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`customer_id`) USING BTREE,
                                 UNIQUE INDEX `uk_usr_customer_mobile`(`mobile` ASC) USING BTREE,
                                 INDEX `idx_usr_customer_level`(`member_level_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'C端用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_customer
-- ----------------------------
INSERT INTO `usr_customer` VALUES (1, '健身小白', '13800000001', 'zhangsan@example.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', NULL, '0', '1996-03-15', 175.00, 72.50, 21.80, '1', 1, 2, '黄金会员', 1585, 330, 'app', '2026-07-06 19:07:47', '0', '0', '2025-04-01 10:00:00', '2026-07-06 19:07:47');
INSERT INTO `usr_customer` VALUES (2, '瑜伽达人Lily', '13800000002', 'lily@example.com', NULL, NULL, '1', '1992-08-22', 165.00, 55.00, NULL, '3', 1, 1, '普通会员', 420, 85, 'wechat', '2025-05-21 19:45:00', '0', '0', '2025-04-05 14:00:00', '2025-05-21 19:45:00');
INSERT INTO `usr_customer` VALUES (3, 'IronMike', '13800000003', 'mike@example.com', NULL, NULL, '0', '1990-11-08', 180.00, 85.00, 18.20, '2', 3, 3, '钻石会员', 6280, 960, 'app', '2025-05-22 07:10:00', '0', '0', '2025-03-15 09:00:00', '2025-05-22 07:10:00');
INSERT INTO `usr_customer` VALUES (4, '跑步爱好者', '13800000004', 'runner@example.com', NULL, NULL, '0', '1988-05-30', 172.00, 68.00, 16.50, '1', 2, 2, '黄金会员', 2100, 450, 'app', '2025-05-20 06:30:00', '0', '0', '2025-04-10 08:00:00', '2025-05-20 06:30:00');
INSERT INTO `usr_customer` VALUES (5, '产后恢复中', '13800000005', 'mom@example.com', NULL, NULL, '1', '1994-01-12', 160.00, 58.00, 28.30, '3', 1, 1, '普通会员', 180, 30, 'douyin', '2025-05-19 21:00:00', '0', '0', '2025-05-10 16:00:00', '2025-05-19 21:00:00');

-- ----------------------------
-- Table structure for usr_segment
-- ----------------------------
DROP TABLE IF EXISTS `usr_segment`;
CREATE TABLE `usr_segment`  (
                                `segment_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分群ID',
                                `segment_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分群名称',
                                `segment_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分群编码',
                                `rule_json` json NULL COMMENT '规则JSON',
                                `user_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户数(冗余)',
                                `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                                `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                                `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`segment_id`) USING BTREE,
                                UNIQUE INDEX `uk_usr_segment_code`(`segment_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户分群' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_segment
-- ----------------------------
INSERT INTO `usr_segment` VALUES (1, '高活跃会员', 'high_active', '{\"member_level\": [\"gold\", \"diamond\"], \"login_days_30\": \">=15\"}', 2, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `usr_segment` VALUES (2, '沉睡用户', 'dormant', '{\"last_login_days\": \">=30\", \"order_count_90d\": 0}', 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');
INSERT INTO `usr_segment` VALUES (3, '新注册用户', 'new_user', '{\"register_days\": \"<=7\"}', 1, '0', '0', '超级管理员', '2026-05-26 16:38:26', '', '2026-05-26 16:38:26');

-- ----------------------------
-- Table structure for usr_segment_rel
-- ----------------------------
DROP TABLE IF EXISTS `usr_segment_rel`;
CREATE TABLE `usr_segment_rel`  (
                                    `segment_rel_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID',
                                    `segment_id` bigint UNSIGNED NOT NULL COMMENT '分群ID',
                                    `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
                                    PRIMARY KEY (`segment_rel_id`) USING BTREE,
                                    UNIQUE INDEX `uk_usr_segment_rel`(`segment_id` ASC, `customer_id` ASC) USING BTREE,
                                    INDEX `idx_usr_segment_rel_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户分群关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_segment_rel
-- ----------------------------
INSERT INTO `usr_segment_rel` VALUES (1, 1, 3, '2025-05-20 10:00:00');
INSERT INTO `usr_segment_rel` VALUES (2, 1, 4, '2025-05-20 10:00:00');
INSERT INTO `usr_segment_rel` VALUES (3, 2, 5, '2025-05-20 10:00:00');
INSERT INTO `usr_segment_rel` VALUES (4, 3, 5, '2025-05-20 10:00:00');

-- ----------------------------
-- Table structure for usr_setting
-- ----------------------------
DROP TABLE IF EXISTS `usr_setting`;
CREATE TABLE `usr_setting`  (
                                `setting_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '设置ID',
                                `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                `available_equipment_json` json NULL COMMENT '可用器材列表',
                                `weekly_days` tinyint UNSIGNED NOT NULL DEFAULT 3 COMMENT '每周训练天数',
                                `session_duration_min` int UNSIGNED NOT NULL DEFAULT 45 COMMENT '单次训练时长(分钟)',
                                `diet_preference` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '口味偏好',
                                `diet_restrictions` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '忌口',
                                `vegetarian_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '素食（0否 1是）',
                                `reminder_time` time NULL DEFAULT NULL COMMENT '训练提醒时间',
                                `push_enabled` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '推送开关 0关 1开',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`setting_id`) USING BTREE,
                                UNIQUE INDEX `uk_usr_setting_customer`(`customer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户训练/饮食偏好设置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of usr_setting
-- ----------------------------
INSERT INTO `usr_setting` VALUES (1, 1, '[\"哑铃\", \"弹力带\"]', 4, 45, '清淡', '无', '0', '07:00:00', '1', '2025-04-01 10:00:00', '2025-05-01 10:00:00');
INSERT INTO `usr_setting` VALUES (2, 3, '[\"杠铃\", \"器械\"]', 5, 60, NULL, NULL, '0', '18:30:00', '1', '2025-03-15 09:00:00', '2025-03-15 09:00:00');



-- ==============================================
-- 模块: community（管理端社区：动态 / 评论 / 审核）
-- 表字段与实体 CommunityPost / CommunityComment / CommunityModeration 对齐
-- ==============================================

DROP TABLE IF EXISTS `community_audit`;
DROP TABLE IF EXISTS `community_moderation`;
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
                                 (1867000001, 1, '今天完成了第一天HIIT，累但爽！体重终于开始下降了 💪', '[\"https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400\"]', 'checkin', NULL, 28, 2, 1, '0', '2025-05-20 21:30:00'),
                                 (1867000002, 3, '卧推100kg达成！感谢教练的周期化安排。', '[\"https://images.unsplash.com/photo-1581009146145-b5ef050c149a?w=400\"]', 'normal', NULL, 86, 1, 1, '0', '2025-05-19 18:00:00'),
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

CREATE TABLE `community_moderation` (
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

INSERT INTO `community_moderation` VALUES
                                       (1, 'post', 1867000001, '今天完成了第一天HIIT，累但爽！体重终于开始下降了 💪', 1, NULL, 1, '2025-05-20 22:00:00', '2025-05-20 21:35:00'),
                                       (2, 'comment', 2020749366733623298, '加油！前三天最难，坚持就是胜利！', 1, NULL, 1, '2025-05-20 22:05:00', '2025-05-20 22:01:00'),
                                       (3, 'post', 1867000004, '第一次跟练产后修复课，强度刚好。', 0, NULL, NULL, NULL, '2025-05-22 10:05:00');


-- ==============================================
-- 模块: vip（付费会员：套餐 / 订单 / 权益；订阅由 vip_order 派生）
-- ==============================================

DROP TABLE IF EXISTS `member_level_benefit`;
DROP TABLE IF EXISTS `member_benefit`;
DROP TABLE IF EXISTS `member_order`;
DROP TABLE IF EXISTS `member_plan`;
DROP TABLE IF EXISTS `vip_level_benefit`;
DROP TABLE IF EXISTS `vip_benefit`;
DROP TABLE IF EXISTS `vip_order`;
DROP TABLE IF EXISTS `vip_plan`;

CREATE TABLE `vip_plan` (
                            `plan_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
                            `plan_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '套餐名称',
                            `package_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '套餐类型（1月卡 2季卡 3年卡）',
                            `duration_days` int UNSIGNED NOT NULL COMMENT '有效天数',
                            `price` decimal(10, 2) NOT NULL COMMENT '价格',
                            `original_price` decimal(10, 2) DEFAULT NULL COMMENT '原价',
                            `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '套餐描述',
                            `benefits` json DEFAULT NULL COMMENT '权益列表',
                            `ai_quota_monthly` int DEFAULT NULL COMMENT '每月加赠AI Token',
                            `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                            `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0上架 1下架）',
                            `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                            PRIMARY KEY (`plan_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'VIP套餐' ROW_FORMAT = DYNAMIC;

INSERT INTO `vip_plan` VALUES
                           (1, '月度会员', '1', 30, 29.90, 39.90, '适合短期体验，轻量体验 AI 能力', '[\"商城课程 9 折\",\"每月加赠 AI 约 2 万 tokens\",\"AI 训练计划生成\"]', 20000, 1, '0', '超级管理员', '2025-05-01 10:00:00', '', '2025-05-01 10:00:00', '0'),
                           (2, '季度会员', '2', 90, 69.90, 99.90, '性价比之选，AI 用量更充足', '[\"商城课程 9 折\",\"每月加赠 AI 约 8 万 tokens\",\"AI 训练计划生成\",\"优先客服响应\"]', 80000, 2, '0', '超级管理员', '2025-05-01 10:00:00', '', '2025-05-01 10:00:00', '0'),
                           (3, '年度会员', '3', 365, 199.00, 399.00, '全年畅享，重度 AI 用户', '[\"商城课程 9 折\",\"每月加赠 AI 约 30 万 tokens\",\"AI 训练计划生成\",\"优先客服\",\"生日礼包\"]', 300000, 3, '0', '超级管理员', '2025-05-01 10:00:00', '', '2025-05-01 10:00:00', '0'),
                           (4, '周卡体验', '1', 7, 9.90, 19.90, '7 天轻量体验，适合新用户试水', '[\"商城课程 9.5 折\",\"每日加赠 AI 约 5 千 tokens（按周折算展示）\"]', 5000, 4, '0', '超级管理员', '2025-05-01 10:00:00', '', '2025-05-01 10:00:00', '0'),
                           (5, '【测试】下架套餐', '1', 30, 0.01, 99.00, '仅用于管理端下架态展示，用户端不应出现', '[\"不应展示\"]', 0, 99, '1', '超级管理员', '2025-05-01 10:00:00', '', '2025-05-01 10:00:00', '0');

CREATE TABLE `vip_order` (
                             `vip_order_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                             `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
                             `customer_id` bigint UNSIGNED NOT NULL COMMENT '用户ID（usr_customer.customer_id）',
                             `plan_id` bigint UNSIGNED NOT NULL COMMENT '套餐ID',
                             `plan_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '套餐名称快照',
                             `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
                             `pay_channel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付渠道',
                             `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
                             `start_time` datetime DEFAULT NULL COMMENT '会员开始时间',
                             `end_time` datetime DEFAULT NULL COMMENT '会员结束时间',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0待支付 1已支付 2已取消 3已退款）',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`vip_order_id`) USING BTREE,
                             UNIQUE INDEX `uk_vip_order_no`(`order_no` ASC) USING BTREE,
                             INDEX `idx_vip_order_customer_status`(`customer_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'VIP订单' ROW_FORMAT = DYNAMIC;

INSERT INTO `vip_order` VALUES
                            (10001, 'M202505010001', 1, 1, '月度会员', 29.90, 'alipay', '2026-07-01 10:00:00', '2026-07-01 10:00:00', '2027-07-01 10:00:00', '1', '2026-07-01 09:55:00', '2026-07-01 10:00:00'),
                            (10002, 'M202505010002', 2, 2, '季度会员', 69.90, 'wechat', '2026-06-15 10:00:00', '2026-06-15 10:00:00', '2026-09-13 10:00:00', '1', '2026-06-15 09:50:00', '2026-06-15 10:00:00'),
                            (10003, 'M202505010003', 1, 4, '周卡体验', 9.90, NULL, NULL, NULL, NULL, '0', '2026-07-22 11:00:00', '2026-07-22 11:00:00'),
                            (10004, 'M202505010004', 3, 3, '年度会员', 199.00, NULL, NULL, NULL, NULL, '2', '2026-07-21 14:00:00', '2026-07-21 15:00:00'),
                            (10005, 'M202505010005', 2, 1, '月度会员', 29.90, 'alipay', '2025-02-01 10:00:00', '2025-02-01 10:00:00', '2025-03-03 10:00:00', '3', '2025-02-01 09:55:00', '2025-03-04 10:00:00');

CREATE TABLE `vip_benefit` (
                               `benefit_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '权益ID',
                               `benefit_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权益编码',
                               `benefit_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权益名称',
                               `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
                               `icon_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标URL',
                               `min_level` tinyint DEFAULT 1 COMMENT '最低会员等级',
                               `benefit_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权益值',
                               `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新人',
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
                               PRIMARY KEY (`benefit_id`) USING BTREE,
                               UNIQUE INDEX `uk_vip_benefit_code`(`benefit_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'VIP权益' ROW_FORMAT = DYNAMIC;

INSERT INTO `vip_benefit` VALUES
                              (1, 'course_90off', '课程9折', '商城课程享受9折优惠', NULL, 1, '0.90', 1, '0', '超级管理员', '2025-05-01 10:00:00', '', '2025-05-01 10:00:00', '0'),
                              (2, 'ai_quota_20k', 'AI加赠2万Token', '每月加赠约2万 AI tokens', NULL, 1, '20000', 2, '0', '超级管理员', '2025-05-01 10:00:00', '', '2025-05-01 10:00:00', '0'),
                              (3, 'priority_service', '优先客服', '会员专属优先客服通道', NULL, 2, '1', 3, '0', '超级管理员', '2025-05-01 10:00:00', '', '2025-05-01 10:00:00', '0');

CREATE TABLE `vip_level_benefit` (
                                     `level_benefit_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID',
                                     `level_id` bigint UNSIGNED NOT NULL COMMENT '等级ID',
                                     `benefit_id` bigint UNSIGNED NOT NULL COMMENT '权益ID',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (`level_benefit_id`) USING BTREE,
                                     UNIQUE INDEX `uk_vip_level_benefit`(`level_id` ASC, `benefit_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '等级与VIP权益关联' ROW_FORMAT = DYNAMIC;

INSERT INTO `vip_level_benefit` VALUES
                                    (1, 2, 1, '2025-05-01 10:00:00'),
                                    (2, 3, 1, '2025-05-01 10:00:00'),
                                    (3, 3, 3, '2025-05-01 10:00:00');

SET FOREIGN_KEY_CHECKS = 1;
