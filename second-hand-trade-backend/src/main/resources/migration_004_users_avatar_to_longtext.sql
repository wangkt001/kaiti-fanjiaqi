USE `employment_fanjiaqi`;
-- 修改用户表 avatar 字段为 LONGTEXT 类型，用于存储 Base64 编码的头像图片
ALTER TABLE `users` 
MODIFY COLUMN `avatar` LONGTEXT DEFAULT NULL COMMENT '头像 URL（Base64 编码）';

-- 修改管理员表 avatar 字段为 LONGTEXT 类型
ALTER TABLE `admin_users` 
MODIFY COLUMN `avatar` LONGTEXT DEFAULT NULL COMMENT '头像 URL（Base64 编码）';
