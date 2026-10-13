-- 修复商城商品相关表字段与实体不一致
-- 库：fitai_all；可重复执行
-- PrdSpu/PrdSku.sort ← order_num
-- PrdBrand.brandLogo ← logo_url
-- PrdSpuImage.isMain ← main_flag
-- 商品列表菜单 component ← /product/list/index

SET @db := DATABASE();

-- ========== prd_spu: order_num -> sort ==========
SET @has_order := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='prd_spu' AND COLUMN_NAME='order_num'
);
SET @has_sort := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='prd_spu' AND COLUMN_NAME='sort'
);
SET @sql := IF(
  @has_order > 0 AND @has_sort = 0,
  'ALTER TABLE `prd_spu` CHANGE COLUMN `order_num` `sort` int NOT NULL DEFAULT 0 COMMENT ''排序(越大越靠前)''',
  'SELECT ''prd_spu.sort ok'' AS msg'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== prd_sku: order_num -> sort ==========
SET @has_order := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='prd_sku' AND COLUMN_NAME='order_num'
);
SET @has_sort := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='prd_sku' AND COLUMN_NAME='sort'
);
SET @sql := IF(
  @has_order > 0 AND @has_sort = 0,
  'ALTER TABLE `prd_sku` CHANGE COLUMN `order_num` `sort` int NOT NULL DEFAULT 0 COMMENT ''排序''',
  'SELECT ''prd_sku.sort ok'' AS msg'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== prd_brand: logo_url -> brand_logo ==========
SET @has_logo_url := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='prd_brand' AND COLUMN_NAME='logo_url'
);
SET @has_brand_logo := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='prd_brand' AND COLUMN_NAME='brand_logo'
);
SET @sql := IF(
  @has_logo_url > 0 AND @has_brand_logo = 0,
  'ALTER TABLE `prd_brand` CHANGE COLUMN `logo_url` `brand_logo` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT ''Logo URL''',
  'SELECT ''prd_brand.brand_logo ok'' AS msg'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== prd_spu_image: main_flag -> is_main ==========
SET @has_main_flag := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='prd_spu_image' AND COLUMN_NAME='main_flag'
);
SET @has_is_main := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='prd_spu_image' AND COLUMN_NAME='is_main'
);
SET @sql := IF(
  @has_main_flag > 0 AND @has_is_main = 0,
  'ALTER TABLE `prd_spu_image` CHANGE COLUMN `main_flag` `is_main` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT ''0'' COMMENT ''主图（0否 1是）''',
  'SELECT ''prd_spu_image.is_main ok'' AS msg'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== 菜单：商品列表 component ==========
UPDATE `sys_menu`
SET path = '/product/list',
    component = '/product/list/index',
    route_name = 'productList'
WHERE menu_id = 73;
