CREATE TABLE `user`  (
     `id` bigint NOT NULL AUTO_INCREMENT,
     `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
     `access_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问凭证',
     `access_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐值',
     `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `user` VALUES (1, 'hello', '2a76b9719d911017c592', '7d793037a0760186574b0282f2f435e7', 'admin');
INSERT INTO `user` VALUES (2, 'zhangsan', '0e7bd9443513f22ab9af', 'e10adc3949ba59abbe56e057f20f883e', 'auth-user');