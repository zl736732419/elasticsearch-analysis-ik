-- 热词表
CREATE TABLE `es_hot_word`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键标识',
  `hot_word` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT ' 热词',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- 停用词表
CREATE TABLE `es_stop_word`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键标识',
  `stop_word` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT ' 停用词',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;
