-- 食物库补齐 image / fiber 字段（与实体、前端表单对齐）
ALTER TABLE `diet_food`
  ADD COLUMN IF NOT EXISTS `image` varchar(512) NULL COMMENT '图片' AFTER `food_code`,
  ADD COLUMN IF NOT EXISTS `fiber` decimal(6,2) NULL COMMENT '膳食纤维g' AFTER `carbs_g`;
