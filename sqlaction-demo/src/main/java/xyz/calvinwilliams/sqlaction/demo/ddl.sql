CREATE TABLE `sqlaction_demo` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '名字',
  `address` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '地址',
  PRIMARY KEY (`id`),
  KEY `sqlaction_demo` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
