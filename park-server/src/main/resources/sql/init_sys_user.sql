-- =============================================
-- 系统用户表 - sys_user
-- =============================================

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name`   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `role_type`   TINYINT      NOT NULL COMMENT '角色类型：1=市级管理员, 2=区县管理员, 3=园区管理员',
    `district_id` BIGINT       DEFAULT NULL COMMENT '所属区县ID（关联district_info表）',
    `park_id`     BIGINT       DEFAULT NULL COMMENT '所属园区ID',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态：0=禁用, 1=启用',
    `create_by`   VARCHAR(50)  DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(50)  DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志：0=未删除, 1=已删除',
    `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =============================================
-- 初始数据（密码均为 BCrypt 加密的 "123456"）
-- BCrypt hash: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
-- =============================================

INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role_type`, `district_id`, `park_id`, `status`, `create_by`, `create_time`)
VALUES
('admin',    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800000001', 1, NULL,     NULL, 1, 'system', NOW()),
('district01','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '西湖区管理员', '13800000002', 2, 1,        NULL, 1, 'system', NOW()),
('park01',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '公园管理员',  '13800000003', 3, 1,        1,    1, 'system', NOW());