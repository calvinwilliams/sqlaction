CREATE TABLE `sqlaction_benchmark` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `fname_cn` varchar(128) COLLATE utf8mb4_bin NOT NULL,
  `salary` decimal(12,2) NOT NULL,
  `birthday` time NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sqlaction_benchmark_idx1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin