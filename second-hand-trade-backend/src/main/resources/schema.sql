-- 创建数据库 (注释掉，Spring Boot init mode 可能不支持 USE 等多语句)
-- CREATE DATABASE IF NOT EXISTS `employment_fanjiaqi` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE `employment_fanjiaqi`;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
  `role` VARCHAR(20) NOT NULL DEFAULT 'buyer' COMMENT '角色（buyer/seller/admin）',
  `nickname` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` LONGTEXT DEFAULT NULL COMMENT '头像 URL（Base64 编码）',
  `gender` TINYINT DEFAULT 0 COMMENT '性别（0-未知 1-男 2-女）',
  `seller_status` VARCHAR(20) DEFAULT NULL COMMENT '卖家状态（pending/approved/rejected）',
  `seller_info` JSON DEFAULT NULL COMMENT '卖家资质信息',
  `shop_name` VARCHAR(100) DEFAULT NULL COMMENT '店铺名称',
  `shop_logo` VARCHAR(255) DEFAULT NULL COMMENT '店铺 Logo URL',
  `shop_description` TEXT DEFAULT NULL COMMENT '店铺描述',
  `fans_count` INT DEFAULT 0 COMMENT '粉丝数量',
  `follow_count` INT DEFAULT 0 COMMENT '关注数量',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1-正常 0-禁用）',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录 IP',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_role` (`role`),
  KEY `idx_seller_status` (`seller_status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 初始化管理员账号（密码：123456）
INSERT IGNORE INTO `users` (`username`, `password`, `role`, `nickname`, `status`) 
VALUES ('admin', '$2a$10$nTJ3W6UNPStBDsarsI9ZbeQKKZvc8YKQwMKt3k65ndrCYL.qcV4d2', 'admin', '系统管理员', 1);

-- 商品分类表
CREATE TABLE IF NOT EXISTS `categories` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '分类 ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id` INT DEFAULT NULL COMMENT '父分类 ID',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  `icon_url` VARCHAR(255) DEFAULT NULL COMMENT '分类图标 URL',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `level` TINYINT DEFAULT 1 COMMENT '分类层级（1-一级 2-二级）',
  `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品 ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `description` TEXT NOT NULL COMMENT '商品描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价（划线价）',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `category_id` INT NOT NULL COMMENT '分类 ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家 ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态（pending/on_sale/sold_out/offline）',
  `sales_count` INT DEFAULT 0 COMMENT '销量',
  `view_count` INT DEFAULT 0 COMMENT '浏览量',
  `favorite_count` INT DEFAULT 0 COMMENT '收藏量',
  `has_sku` BOOLEAN DEFAULT FALSE COMMENT '是否有 SKU',
  `gift_packaging` BOOLEAN DEFAULT FALSE COMMENT '是否支持礼品包装',
  `customization` BOOLEAN DEFAULT FALSE COMMENT '是否支持定制',
  `packaging_fee` DECIMAL(10,2) DEFAULT 0 COMMENT '包装费用',
  `freight_template_id` BIGINT DEFAULT NULL COMMENT '运费模板 ID',
  `image_url` LONGTEXT DEFAULT NULL COMMENT '商品主图（Base64 编码）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `published_at` DATETIME DEFAULT NULL COMMENT '上架时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sales_count` (`sales_count`),
  KEY `idx_view_count` (`view_count`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 商品图片表
CREATE TABLE IF NOT EXISTS `product_images` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片 ID',
  `product_id` BIGINT NOT NULL COMMENT '商品 ID',
  `image_data` TEXT NOT NULL COMMENT '图片 Base64 编码数据',
  `type` VARCHAR(20) DEFAULT 'detail' COMMENT '类型（main-主图/detail-详情图）',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- 文化标签表
CREATE TABLE IF NOT EXISTS `cultural_tags` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签 ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '标签描述',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类（地域/工艺/IP/其他）',
  `icon_url` VARCHAR(255) DEFAULT NULL COMMENT '标签图标 URL',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `is_hot` BOOLEAN DEFAULT FALSE COMMENT '是否热门',
  `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文化标签表';

-- 商品 - 标签关联表
CREATE TABLE IF NOT EXISTS `product_tags` (
  `product_id` BIGINT NOT NULL COMMENT '商品 ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`product_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品 - 标签关联表';

-- 商品文化信息扩展表
CREATE TABLE IF NOT EXISTS `product_cultural_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录 ID',
  `product_id` BIGINT NOT NULL COMMENT '商品 ID',
  `cultural_background` TEXT DEFAULT NULL COMMENT '文化背景',
  `craft_description` TEXT DEFAULT NULL COMMENT '工艺说明',
  `designer` VARCHAR(100) DEFAULT NULL COMMENT '设计师/传承人',
  `origin_place` VARCHAR(100) DEFAULT NULL COMMENT '产地',
  `material` VARCHAR(255) DEFAULT NULL COMMENT '材质',
  `cultural_ip` VARCHAR(100) DEFAULT NULL COMMENT '文化 IP 归属',
  `story_content` TEXT DEFAULT NULL COMMENT '文化故事详情',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  KEY `idx_origin_place` (`origin_place`),
  KEY `idx_cultural_ip` (`cultural_ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品文化信息扩展表';

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin_users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` LONGTEXT DEFAULT NULL COMMENT '头像 URL（Base64 编码）',
  `role` VARCHAR(50) DEFAULT 'operator' COMMENT '角色（super_admin/admin/operator）',
  `permissions` JSON DEFAULT NULL COMMENT '权限列表',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1-正常 0-禁用）',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 初始化管理员账号（密码：admin123，BCrypt 加密）
INSERT IGNORE INTO `admin_users` (`username`, `password`, `real_name`, `role`, `status`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'super_admin', 1);

-- 初始化商品分类
INSERT IGNORE INTO `categories` (`name`, `description`, `sort_order`, `level`, `is_active`) VALUES
('传统工艺品', '包括陶瓷、刺绣、木雕等传统工艺制品', 1, 1, TRUE),
('文化创意产品', '融合现代设计的文创产品', 2, 1, TRUE),
('非遗传承', '非物质文化遗产相关制品', 3, 1, TRUE),
('博物馆文创', '各大博物馆推出的文创产品', 4, 1, TRUE),
('地方特色', '具有河南地方特色的产品', 5, 1, TRUE);

-- 初始化二级分类
INSERT IGNORE INTO `categories` (`name`, `parent_id`, `description`, `sort_order`, `level`, `is_active`) VALUES
('陶瓷', 1, '陶瓷制品', 1, 2, TRUE),
('刺绣', 1, '刺绣工艺品', 2, 2, TRUE),
('木雕', 1, '木雕工艺品', 3, 2, TRUE),
('青铜器复刻', 1, '青铜器复刻品', 4, 2, TRUE),
('文具用品', 2, '文创文具', 1, 2, TRUE),
('服饰配饰', 2, '文创服饰和配饰', 2, 2, TRUE),
('家居装饰', 2, '家居装饰用品', 3, 2, TRUE),
('数码周边', 2, '数码产品周边', 4, 2, TRUE);

-- 初始化文化标签
INSERT IGNORE INTO `cultural_tags` (`name`, `description`, `category`, `is_hot`, `sort_order`) VALUES
('豫剧', '河南豫剧文化相关', '文化', TRUE, 1),
('少林', '少林文化相关', '文化', TRUE, 2),
('牡丹', '洛阳牡丹文化相关', '文化', TRUE, 3),
('青铜器', '青铜器文化相关', '工艺', TRUE, 4),
('陶瓷', '陶瓷工艺相关', '工艺', TRUE, 5),
('刺绣', '刺绣工艺相关', '工艺', FALSE, 6),
('剪纸', '剪纸艺术相关', '非遗', TRUE, 7),
('年画', '传统年画艺术', '非遗', FALSE, 8),
('泥咕咕', '浚县泥咕咕非遗技艺', '非遗', FALSE, 9),
('洛阳', '洛阳地区特色', '地域', TRUE, 10),
('开封', '开封地区特色', '地域', TRUE, 11),
('安阳', '安阳地区特色', '地域', FALSE, 12),
('郑州', '郑州地区特色', '地域', FALSE, 13),
('河南博物院', '河南博物院 IP', 'IP', TRUE, 14),
('清明上河园', '清明上河园 IP', 'IP', FALSE, 15);

-- ==================== 互动模块表结构 ====================

-- 商品评价表
CREATE TABLE IF NOT EXISTS `reviews` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价 ID',
  `product_id` BIGINT NOT NULL COMMENT '商品 ID',
  `order_id` BIGINT NOT NULL COMMENT '订单 ID',
  `user_id` BIGINT NOT NULL COMMENT '评价用户 ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家 ID',
  `rating` TINYINT NOT NULL COMMENT '评分（1-5 星）',
  `content` TEXT DEFAULT NULL COMMENT '评价内容',
  `images` JSON DEFAULT NULL COMMENT '评价图片（JSON 数组）',
  `is_anonymous` BOOLEAN DEFAULT FALSE COMMENT '是否匿名',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT DEFAULT 0 COMMENT '回复数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1-正常 0-隐藏 -1-删除）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评价表';

-- 评价回复表
CREATE TABLE IF NOT EXISTS `review_replies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复 ID',
  `review_id` BIGINT NOT NULL COMMENT '评价 ID',
  `user_id` BIGINT NOT NULL COMMENT '回复用户 ID',
  `parent_reply_id` BIGINT DEFAULT NULL COMMENT '父回复 ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1-正常 0-隐藏 -1-删除）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_review_id` (`review_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_reply_id` (`parent_reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价回复表';

-- 点赞表
CREATE TABLE IF NOT EXISTS `likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `target_type` VARCHAR(50) NOT NULL COMMENT '目标类型（review/reply/product/content/comment）',
  `target_id` BIGINT NOT NULL COMMENT '目标 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorites` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `target_type` VARCHAR(50) NOT NULL COMMENT '目标类型（product/content）',
  `target_id` BIGINT NOT NULL COMMENT '目标 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 用户关注表
CREATE TABLE IF NOT EXISTS `user_follows` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关注 ID',
  `follower_id` BIGINT NOT NULL COMMENT '关注者 ID',
  `following_id` BIGINT NOT NULL COMMENT '被关注者 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
  KEY `idx_follower_id` (`follower_id`),
  KEY `idx_following_id` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注表';

-- 文化资讯表
CREATE TABLE IF NOT EXISTS `cultural_contents` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资讯 ID',
  `title` VARCHAR(255) NOT NULL COMMENT '资讯标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content` LONGTEXT NOT NULL COMMENT '资讯内容',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图 URL',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类（intangible_heritage/exhibition/activity/news/story）',
  `tags` JSON DEFAULT NULL COMMENT '标签（JSON 数组）',
  `author_id` BIGINT DEFAULT NULL COMMENT '作者 ID（管理员）',
  `view_count` INT DEFAULT 0 COMMENT '浏览量',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `favorite_count` INT DEFAULT 0 COMMENT '收藏数',
  `is_top` BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
  `is_recommend` BOOLEAN DEFAULT FALSE COMMENT '是否推荐',
  `is_published` BOOLEAN DEFAULT FALSE COMMENT '是否发布',
  `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_is_published` (`is_published`),
  KEY `idx_published_at` (`published_at`),
  KEY `idx_view_count` (`view_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文化资讯表';

-- 资讯评论表
CREATE TABLE IF NOT EXISTS `content_comments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论 ID',
  `content_id` BIGINT NOT NULL COMMENT '资讯 ID',
  `user_id` BIGINT NOT NULL COMMENT '评论用户 ID',
  `parent_comment_id` BIGINT DEFAULT NULL COMMENT '父评论 ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT DEFAULT 0 COMMENT '回复数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1-正常 0-隐藏 -1-删除）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_comment_id` (`parent_comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯评论表';

-- ==================== 购物车和订单模块表结构 ====================

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `product_id` BIGINT NOT NULL COMMENT '商品 ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `selected` BOOLEAN DEFAULT TRUE COMMENT '是否选中',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单 ID',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家 ID',
  `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '订单总金额',
  `payment_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '实付金额',
  `freight_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '运费',
  `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '优惠金额',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态（0-待付款 1-待发货 2-待收货 3-已完成 4-已取消 -1-已退款）',
  `payment_type` TINYINT DEFAULT NULL COMMENT '支付方式（1-微信 2-支付宝 3-银行卡）',
  `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `delivery_type` VARCHAR(50) DEFAULT NULL COMMENT '配送方式',
  `delivery_no` VARCHAR(64) DEFAULT NULL COMMENT '物流单号',
  `delivery_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `receive_time` DATETIME DEFAULT NULL COMMENT '收货时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
  `receiver_name` VARCHAR(100) DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单商品表
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单商品 ID',
  `order_id` BIGINT NOT NULL COMMENT '订单 ID',
  `product_id` BIGINT NOT NULL COMMENT '商品 ID',
  `product_name` VARCHAR(255) NOT NULL COMMENT '商品名称',
  `product_image` LONGTEXT DEFAULT NULL COMMENT '商品图片',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
  `quantity` INT NOT NULL COMMENT '商品数量',
  `total_price` DECIMAL(10,2) NOT NULL COMMENT '商品总价',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品表';

-- 订单操作日志表
CREATE TABLE IF NOT EXISTS `order_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `order_id` BIGINT NOT NULL COMMENT '订单 ID',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人 ID',
  `action` VARCHAR(50) NOT NULL COMMENT '操作动作',
  `message` VARCHAR(500) DEFAULT NULL COMMENT '操作说明',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单操作日志表';

-- 收货地址表
CREATE TABLE IF NOT EXISTS `user_addresses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `receiver_name` VARCHAR(100) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `province` VARCHAR(50) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(50) DEFAULT NULL COMMENT '市',
  `district` VARCHAR(50) DEFAULT NULL COMMENT '区/县',
  `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `is_default` BOOLEAN DEFAULT FALSE COMMENT '是否默认地址',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';

-- 商品评价表（简化版）
CREATE TABLE IF NOT EXISTS `product_reviews` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价 ID',
  `order_id` BIGINT NOT NULL COMMENT '订单 ID',
  `product_id` BIGINT NOT NULL COMMENT '商品 ID',
  `user_id` BIGINT NOT NULL COMMENT '评价用户 ID',
  `rating` TINYINT NOT NULL COMMENT '评分（1-5 星）',
  `content` TEXT DEFAULT NULL COMMENT '评价内容',
  `images` JSON DEFAULT NULL COMMENT '评价图片',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评价表';
