USE `employment_fanjiaqi`;
-- 修改用户表 shop_logo 字段为 LONGTEXT 类型，用于存储 Base64 编码的店铺 Logo 图片
ALTER TABLE `users` 
MODIFY COLUMN `shop_logo` LONGTEXT DEFAULT NULL COMMENT '店铺 Logo（Base64 编码）';
