-- =============================================
-- 系统用户表 - sys_user
-- 与外部 park_evaluation.sql 保持一致
-- =============================================

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `role_type` tinyint NOT NULL COMMENT '角色类型：1=市级管理员, 2=区县管理员, 3=园区管理员',
    `district_id` bigint DEFAULT NULL COMMENT '所属区县ID',
    `park_id` bigint DEFAULT NULL COMMENT '所属园区ID',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=禁用, 1=启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 初始测试数据（密码均为 BCrypt 加密的 "123456"）
-- =============================================

INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role_type`, `district_id`, `park_id`, `status`, `create_time`, `update_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张管理', '13800000001', 1, NULL, NULL, 1, NOW(), NOW()),
('district', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李区管', '13800000002', 2, 3, NULL, 1, NOW(), NOW()),
('park', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '园区管理员', '13800000003', 3, 3, 1, 1, NOW(), NOW());