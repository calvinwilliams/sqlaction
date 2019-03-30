CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '���',
  `name` varchar(45) COLLATE utf8mb4_bin NOT NULL COMMENT '����',
  `gender` varchar(3) COLLATE utf8mb4_bin NOT NULL COMMENT '�Ա�',
  `age` smallint(6) NOT NULL COMMENT '����',
  `address` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '��ַ',
  `level` int(11) NOT NULL COMMENT '����',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_idx1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

CREATE TABLE `user_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '���',
  `user_id` int(11) NOT NULL COMMENT '�û����',
  `item_name` varchar(45) COLLATE utf8mb4_bin NOT NULL COMMENT '��Ʒ����',
  `amount` int(11) NOT NULL COMMENT '����',
  `total_price` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_idx1` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
