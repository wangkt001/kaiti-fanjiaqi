-- 数据库迁移脚本：修改 products 表的 image_url 列为 LONGTEXT 类型
-- 执行此脚本前，请确保已备份数据库

USE `employment_fanjiaqi`;

-- 修改 image_url 列为 LONGTEXT 类型以支持更长的 Base64 图片数据
ALTER TABLE `products` MODIFY COLUMN `image_url` LONGTEXT DEFAULT NULL COMMENT '商品主图（Base64 编码）';

-- 同时也修改 product_images 表的 image_data 列以保持一致性
ALTER TABLE `product_images` MODIFY COLUMN `image_data` LONGTEXT NOT NULL COMMENT '图片 Base64 编码数据';

-- 验证修改结果
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'employment_fanjiaqi' 
AND TABLE_NAME IN ('products', 'product_images') 
AND COLUMN_NAME IN ('image_url', 'image_data');
