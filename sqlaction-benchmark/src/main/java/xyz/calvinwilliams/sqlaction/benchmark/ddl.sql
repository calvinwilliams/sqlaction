CREATE TABLE `sqlaction_benchmark` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '英文名',
  `name_cn` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '中文名',
  `salary` decimal(12,2) NOT NULL COMMENT '薪水',
  `birthday` date NOT NULL COMMENT '生日',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42332 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin