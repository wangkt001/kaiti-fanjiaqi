USE `employment_fanjiaqi`;

ALTER TABLE `users`
  MODIFY COLUMN `seller_status` VARCHAR(20) DEFAULT NULL COMMENT '卖家状态（pending/approved/rejected）';

UPDATE `users`
SET `seller_status` = NULL
WHERE `role` = 'buyer'
  AND `seller_info` IS NULL
  AND `seller_status` = 'pending';

SELECT `id`, `username`, `role`, `seller_status`
FROM `users`
WHERE `seller_status` = 'pending';
