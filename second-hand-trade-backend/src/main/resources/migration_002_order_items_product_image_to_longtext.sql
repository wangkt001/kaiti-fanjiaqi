USE `employment_fanjiaqi`;

ALTER TABLE `order_items` MODIFY COLUMN `product_image` LONGTEXT DEFAULT NULL COMMENT '商品图片';

SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'employment_fanjiaqi' 
AND TABLE_NAME = 'order_items'
AND COLUMN_NAME = 'product_image';
