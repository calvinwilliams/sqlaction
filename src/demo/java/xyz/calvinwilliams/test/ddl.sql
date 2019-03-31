CREATE TABLE `sqlaction_demo` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '±àºÅ',
  `name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT 'Ãû×Ö',
  `address` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'µØÖ·',
  PRIMARY KEY (`id`),
  KEY `sqlaction_demo` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
