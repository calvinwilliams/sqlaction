CREATE TABLE `user_base` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(45) COLLATE utf8mb4_bin NOT NULL COMMENT '名字',
  `gender` varchar(3) COLLATE utf8mb4_bin NOT NULL COMMENT '性别',
  `age` smallint(6) NOT NULL COMMENT '年龄',
  `address` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '地址',
  `lvl` int(11) NOT NULL COMMENT '级别',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_base_idx1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

CREATE TABLE `user_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `item_name` varchar(45) COLLATE utf8mb4_bin NOT NULL COMMENT '商品名称',
  `amount` int(11) NOT NULL COMMENT '数量',
  `total_price` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_idx1` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
