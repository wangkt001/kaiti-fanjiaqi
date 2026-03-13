-- ============================================
-- 数据库修复脚本
-- 用于修复 cultural_contents 表缺少字段的问题
-- 执行时间：2026-03-13
-- ============================================

USE `employment_fanjiaqi`;

-- 检查并添加 cover_image_base64 列
ALTER TABLE `cultural_contents` 
ADD COLUMN `cover_image_base64` LONGTEXT DEFAULT NULL COMMENT '封面图 Base64 编码' AFTER `cover_image`;

-- 验证是否添加成功
SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'employment_fanjiaqi' 
  AND TABLE_NAME = 'cultural_contents' 
  AND COLUMN_NAME IN ('cover_image', 'cover_image_base64');
