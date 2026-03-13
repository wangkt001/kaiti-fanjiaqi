-- 为 cultural_contents 表添加 cover_image_base64 列
ALTER TABLE `cultural_contents` 
ADD COLUMN `cover_image_base64` LONGTEXT DEFAULT NULL COMMENT '封面图 Base64 编码' AFTER `cover_image`;
