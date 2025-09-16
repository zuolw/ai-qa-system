-- 创建一个名为 'ai_qa_system' 的数据库，如果它不存在的话
CREATE DATABASE IF NOT EXISTS `ai_qa_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换到该数据库
USE `ai_qa_system`;

-- ----------------------------
-- 用户表 (user)
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(255) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 问答历史表 (qa_history) (可选，用于功能扩展)
-- ----------------------------
DROP TABLE IF EXISTS `qa_history`;
CREATE TABLE `qa_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `question` TEXT NOT NULL COMMENT '用户提出的问题',
  `answer` LONGTEXT COMMENT 'AI返回的回答',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问答历史表';

-- 插入一些测试数据 (可选)
INSERT INTO `user` (`username`, `password`) VALUES ('testuser', '$2a$10$abcdefghijklmnopqrstuv'); -- 密码是加密的，请通过注册接口创建用户