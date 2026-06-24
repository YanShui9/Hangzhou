-- ============================================================
-- 杭州市小微园区评价数据分析平台 - 数据库初始化脚本
-- 数据库：MySQL 8.0+
-- 创建日期：2026-06-11
-- 说明：包含 6 张核心表结构及测试数据
-- 以设计文档为准
-- ============================================================

-- 创建数据库（如不存在）
CREATE DATABASE IF NOT EXISTS park_evaluation
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE park_evaluation;

-- ============================================================
-- 1. sys_user — 用户表
-- ============================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL                COMMENT '用户名',
    password    VARCHAR(255) NOT NULL                COMMENT '密码（BCrypt加密）',
    real_name   VARCHAR(50)  DEFAULT NULL            COMMENT '真实姓名',
    phone       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
    role_type   TINYINT      NOT NULL                COMMENT '角色类型：1=市级管理员, 2=区县管理员, 3=园区管理员',
    district_id BIGINT       DEFAULT NULL            COMMENT '所属区县ID',
    park_id     BIGINT       DEFAULT NULL            COMMENT '所属园区ID',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0=禁用, 1=启用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. park_info — 园区信息表
-- ============================================================
DROP TABLE IF EXISTS park_info;
CREATE TABLE park_info (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '园区ID',
    park_name       VARCHAR(100)  NOT NULL                COMMENT '园区名称',
    park_type       TINYINT       DEFAULT NULL            COMMENT '园区类型：1=制造类, 2=服务类',
    district_id     BIGINT        DEFAULT NULL            COMMENT '所属区县ID',
    district_name   VARCHAR(50)   DEFAULT NULL            COMMENT '区县名称',
    address         VARCHAR(255)  DEFAULT NULL            COMMENT '园区地址',
    build_area      DECIMAL(12,2) DEFAULT NULL            COMMENT '已建建筑面积（亩）',
    land_area       DECIMAL(12,2) DEFAULT NULL            COMMENT '实际用地数（亩）',
    contact_name    VARCHAR(50)   DEFAULT NULL            COMMENT '联系人',
    contact_phone   VARCHAR(20)   DEFAULT NULL            COMMENT '联系电话',
    introduction    TEXT          DEFAULT NULL            COMMENT '园区简介',
    star_level      TINYINT       DEFAULT NULL            COMMENT '星级：null=未评定, 3=三星, 4=四星, 5=五星',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区信息表';

-- ============================================================
-- 3. enterprise_info — 企业信息表
-- ============================================================
DROP TABLE IF EXISTS enterprise_info;
CREATE TABLE enterprise_info (
    id                 BIGINT        NOT NULL AUTO_INCREMENT COMMENT '企业ID',
    enterprise_name    VARCHAR(100)  NOT NULL                COMMENT '企业名称',
    credit_code        VARCHAR(50)   DEFAULT NULL            COMMENT '统一社会信用代码',
    park_id            BIGINT        NOT NULL                COMMENT '所属园区ID',
    industry_code      VARCHAR(20)   DEFAULT NULL            COMMENT '行业门类代码',
    industry_name      VARCHAR(100)  DEFAULT NULL            COMMENT '行业名称',
    status             VARCHAR(20)   DEFAULT '在营'           COMMENT '经营状态',
    register_date      DATE          DEFAULT NULL            COMMENT '注册日期',
    legal_person       VARCHAR(50)   DEFAULT NULL            COMMENT '法定代表人',
    registered_capital DECIMAL(15,2) DEFAULT NULL            COMMENT '注册资本（万元）',
    contact_name       VARCHAR(50)   DEFAULT NULL            COMMENT '联系人',
    contact_phone      VARCHAR(20)   DEFAULT NULL            COMMENT '联系电话',
    is_participate     TINYINT       NOT NULL DEFAULT 1      COMMENT '是否参评：0=不参评, 1=参评',
    participate_reason VARCHAR(255)  DEFAULT NULL            COMMENT '不参评原因',
    entry_start_time   DATE          DEFAULT NULL            COMMENT '入驻开始时间',
    entry_end_time     DATE          DEFAULT NULL            COMMENT '入驻截止时间',
    create_time        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_park_id (park_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信息表';

-- ============================================================
-- 4. evaluation_record — 评价记录表
-- ============================================================
DROP TABLE IF EXISTS evaluation_record;
CREATE TABLE evaluation_record (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评价记录ID',
    park_id         BIGINT        NOT NULL                COMMENT '园区ID',
    year            INT           NOT NULL                COMMENT '评价年度',
    status          TINYINT       NOT NULL DEFAULT 0      COMMENT '状态：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回',
    total_score     DECIMAL(5,2)  DEFAULT NULL            COMMENT '总分',
    grade           VARCHAR(10)   DEFAULT NULL            COMMENT '绩效分档：A/B/C/D',
    reject_category VARCHAR(255)  DEFAULT NULL            COMMENT '驳回类别',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_park_year (park_id, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价记录表';

-- ============================================================
-- 5. audit_record — 审核记录表
-- ============================================================
DROP TABLE IF EXISTS audit_record;
CREATE TABLE audit_record (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
    evaluation_id BIGINT       NOT NULL                COMMENT '评价记录ID',
    auditor_id    BIGINT       NOT NULL                COMMENT '审核人ID',
    auditor_name  VARCHAR(50)  DEFAULT NULL            COMMENT '审核人姓名',
    auditor_role  TINYINT      DEFAULT NULL            COMMENT '审核人角色',
    action        TINYINT      NOT NULL                COMMENT '操作：1=通过, 2=驳回',
    opinion       TEXT         DEFAULT NULL            COMMENT '审核意见',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    PRIMARY KEY (id),
    KEY idx_evaluation_id (evaluation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- ============================================================
-- 6. park_operation — 运营数据表
-- ============================================================
DROP TABLE IF EXISTS park_operation;
CREATE TABLE park_operation (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '运营数据ID',
    park_id          BIGINT        NOT NULL                COMMENT '园区ID',
    year             INT           NOT NULL                COMMENT '年度',
    quarter          TINYINT       NOT NULL                COMMENT '季度：1/2/3/4',
    enterprise_count INT           DEFAULT NULL            COMMENT '入驻企业数',
    employee_count   INT           DEFAULT NULL            COMMENT '员工总数',
    build_area       DECIMAL(12,2) DEFAULT NULL            COMMENT '已建面积',
    land_area        DECIMAL(12,2) DEFAULT NULL            COMMENT '用地面积',
    patent_count     INT           DEFAULT NULL            COMMENT '专利总数',
    create_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_park_year_quarter (park_id, year, quarter)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营数据表';

-- ============================================================
-- 测试数据插入
-- ============================================================

-- -----------------------------------------------------------
-- 用户数据
-- 密码均为 123456，BCrypt 加密值：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
-- role_type: 1=市级管理员, 2=区县管理员, 3=园区管理员
-- -----------------------------------------------------------
INSERT INTO sys_user (username, password, real_name, phone, role_type, district_id, park_id, status) VALUES
('admin',    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张管理', '13800000001', 1, NULL,  NULL, 1),
('district', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李区管', '13800000002', 2, 1,    NULL, 1),
('park',     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王园管', '13800000003', 3, NULL, 1,    1);

-- -----------------------------------------------------------
-- 园区数据
-- -----------------------------------------------------------
INSERT INTO park_info (park_name, park_type, district_id, district_name, address, build_area, land_area, contact_name, contact_phone, introduction, star_level) VALUES
('西湖科技园',     1, 1, '西湖区', '杭州市西湖区文一西路998号',  350.00, 500.00,  '陈主任', '0571-88000001', '西湖科技园是杭州市重点扶持的科技产业园区', 4),
('滨江高新技术园', 1, 2, '滨江区', '杭州市滨江区江南大道100号',  600.00, 800.00,  '赵主任', '0571-88000002', '滨江高新技术园聚焦集成电路和生物医药产业', 5),
('余杭未来产业园', 2, 3, '余杭区', '杭州市余杭区良渚街道200号',  900.00, 1200.00, '刘主任', '0571-88000003', '余杭未来产业园以数字经济和新材料为主导', NULL);

-- -----------------------------------------------------------
-- 企业数据
-- -----------------------------------------------------------
INSERT INTO enterprise_info (enterprise_name, credit_code, park_id, industry_code, industry_name, status, register_date, legal_person, registered_capital, contact_name, contact_phone, is_participate, participate_reason) VALUES
('杭州智云科技有限公司',     '91330100MA12345678', 1, 'I65', '软件和信息技术服务业', '在营', '2018-05-10', '张三', 500.00,  '孙经理', '13900000001', 1, NULL),
('杭州绿能环保科技有限公司', '91330100MA23456789', 1, 'N77', '生态保护和环境治理业', '在营', '2019-03-15', '李四', 200.00,  '周经理', '13900000002', 1, NULL),
('杭州量子芯片有限公司',     '91330100MA34567890', 2, 'C39', '计算机、通信和其他电子设备制造业', '在营', '2017-08-20', '王五', 2000.00, '吴经理', '13900000003', 1, NULL),
('杭州生物医药研究院有限公司', '91330100MA45678901', 2, 'C27', '医药制造业', '在营', '2020-01-08', '赵六', 800.00,  '郑经理', '13900000004', 1, NULL),
('杭州新材科技有限公司',     '91330100MA56789012', 3, 'C30', '非金属矿物制品业', '在营', '2021-06-25', '钱七', 300.00,  '冯经理', '13900000005', 0, '成立不满3年');

-- -----------------------------------------------------------
-- 评价记录（示例）
-- status: 0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回
-- -----------------------------------------------------------
INSERT INTO evaluation_record (park_id, year, status, total_score, grade) VALUES
(1, 2026, 1, 85.50, 'B'),
(2, 2026, 3, 92.00, 'A'),
(3, 2026, 0, NULL,  NULL);

-- -----------------------------------------------------------
-- 审核记录（示例）
-- action: 1=通过, 2=驳回
-- -----------------------------------------------------------
INSERT INTO audit_record (evaluation_id, auditor_id, auditor_name, auditor_role, action, opinion) VALUES
(2, 2, '李区管', 2, 1, '材料齐全，数据真实，予以通过');

-- -----------------------------------------------------------
-- 园区运营数据（示例）
-- -----------------------------------------------------------
INSERT INTO park_operation (park_id, year, quarter, enterprise_count, employee_count, build_area, land_area, patent_count) VALUES
(1, 2026, 1, 45, 3200, 350.00, 500.00, 120),
(1, 2026, 2, 46, 3350, 355.00, 500.00, 125),
(2, 2026, 1, 78, 5600, 600.00, 800.00, 280),
(2, 2026, 2, 80, 5800, 610.00, 800.00, 295),
(3, 2026, 1, 25, 1800, 900.00, 1200.00, 45);
