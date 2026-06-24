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
CREATE TABLE IF NOT EXISTS sys_user (
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
CREATE TABLE IF NOT EXISTS park_info (
    id                   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '园区ID',
    park_code            VARCHAR(50)   DEFAULT NULL            COMMENT '园区代码',
    park_name            VARCHAR(100)  NOT NULL                COMMENT '园区名称',
    park_type            TINYINT       DEFAULT NULL            COMMENT '园区类型：1=生产性制造类, 2=生产性服务类',
    district_id          BIGINT        DEFAULT NULL            COMMENT '所属区县ID',
    district_name        VARCHAR(50)   DEFAULT NULL            COMMENT '区县名称',
    address              VARCHAR(255)  DEFAULT NULL            COMMENT '园区地址',
    star_level           TINYINT       DEFAULT NULL            COMMENT '星级：null=未评定, 3=三星, 4=四星, 5=五星',
    performance          VARCHAR(10)   DEFAULT NULL            COMMENT '绩效评价：A/B/C/D/未参评',
    park_status          VARCHAR(20)   DEFAULT NULL            COMMENT '园区状态（已投运/在建/规划）',
    dev_mode             VARCHAR(50)   DEFAULT NULL            COMMENT '开发模式',
    land_source          VARCHAR(50)   DEFAULT NULL            COMMENT '土地来源',
    land_nature          VARCHAR(50)   DEFAULT NULL            COMMENT '土地性质',
    recognition          VARCHAR(255)  DEFAULT NULL            COMMENT '园区认定',
    is_upgrade           VARCHAR(10)   DEFAULT NULL            COMMENT '是否改造提升',
    upgrade_content      TEXT          DEFAULT NULL            COMMENT '改造提升内容',
    main_industry        VARCHAR(255)  DEFAULT NULL            COMMENT '主导产业',
    introduction         TEXT          DEFAULT NULL            COMMENT '园区介绍',
    land_area            DECIMAL(12,2) DEFAULT NULL            COMMENT '实际用地面积（亩）',
    build_area           DECIMAL(12,2) DEFAULT NULL            COMMENT '已建建筑面积（平方米）',
    rent_remain_area     DECIMAL(12,2) DEFAULT NULL            COMMENT '园区剩余可租面积（平方米）',
    sale_remain_area     DECIMAL(12,2) DEFAULT NULL            COMMENT '园区剩余可售面积（平方米）',
    rented_area          DECIMAL(12,2) DEFAULT NULL            COMMENT '已出租面积（平方米）',
    public_facilities    TEXT          DEFAULT NULL            COMMENT '公共配套设施',
    public_services      TEXT          DEFAULT NULL            COMMENT '公共配套服务',
    operation_org_name   VARCHAR(100)  DEFAULT NULL            COMMENT '运营机构名称',
    operation_org_code   VARCHAR(50)   DEFAULT NULL            COMMENT '运营机构统一社会信用代码',
    operation_org_nature VARCHAR(50)   DEFAULT NULL            COMMENT '运营机构性质',
    org_leader           VARCHAR(50)   DEFAULT NULL            COMMENT '机构负责人',
    org_leader_phone     VARCHAR(20)   DEFAULT NULL            COMMENT '机构负责人手机',
    org_contact          VARCHAR(50)   DEFAULT NULL            COMMENT '机构联系人',
    org_contact_phone    VARCHAR(20)   DEFAULT NULL            COMMENT '机构联系人手机',
    enterprise_count     INT           DEFAULT 0               COMMENT '入驻企业总数（家）',
    above_scale_count    INT           DEFAULT 0               COMMENT '规模以上企业（家）',
    high_tech_count      INT           DEFAULT 0               COMMENT '高新技术企业（家）',
    tech_sme_count       INT           DEFAULT 0               COMMENT '科技中小企业（家）',
    listed_count         INT           DEFAULT 0               COMMENT '上市企业（家）',
    hidden_champion_count INT          DEFAULT 0               COMMENT '隐形冠军企业（家）',
    national_srti_count  INT           DEFAULT 0               COMMENT '国家级专精特新小巨人企业（家）',
    provincial_srti_count INT          DEFAULT 0               COMMENT '省专精特新中小企业（家）',
    innovative_sme_count INT           DEFAULT 0               COMMENT '创新型中小企业（家）',
    employee_count       INT           DEFAULT 0               COMMENT '入驻企业员工人数（人）',
    national_talent      INT           DEFAULT 0               COMMENT '国千人才（人）',
    provincial_talent    INT           DEFAULT 0               COMMENT '省千人才（人）',
    master_above         INT           DEFAULT 0               COMMENT '硕士/副高以上（人）',
    senior_engineer      INT           DEFAULT 0               COMMENT '正高级工程师人数（人）',
    engineer             INT           DEFAULT 0               COMMENT '高级工程师人数（人）',
    senior_technician    INT           DEFAULT 0               COMMENT '高级技师人数（人）',
    master_degree        INT           DEFAULT 0               COMMENT '硕士以上人数（人）',
    patent_total         INT           DEFAULT 0               COMMENT '专利拥有量（件）',
    patent_invention     INT           DEFAULT 0               COMMENT '发明专利（件）',
    patent_utility       INT           DEFAULT 0               COMMENT '实用新型专利（件）',
    patent_design        INT           DEFAULT 0               COMMENT '外观设计专利（件）',
    contact_name         VARCHAR(50)   DEFAULT NULL            COMMENT '联系人',
    contact_phone        VARCHAR(20)   DEFAULT NULL            COMMENT '联系电话',
    create_time          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区信息表';

-- ============================================================
-- 3. enterprise_info — 企业信息表
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_info (
    id                 BIGINT        NOT NULL AUTO_INCREMENT COMMENT '企业ID',
    enterprise_name    VARCHAR(100)  NOT NULL                COMMENT '企业名称',
    credit_code        VARCHAR(50)   DEFAULT NULL            COMMENT '统一社会信用代码',
    district_id        BIGINT        DEFAULT NULL            COMMENT '所属区县ID',
    park_id            BIGINT        NOT NULL                COMMENT '所属园区ID',
    industry_code      VARCHAR(20)   DEFAULT NULL            COMMENT '行业门类代码',
    industry_name      VARCHAR(100)  DEFAULT NULL            COMMENT '行业名称',
    honor              VARCHAR(255)  DEFAULT NULL            COMMENT '荣誉称号',
    enterprise_status  VARCHAR(20)   DEFAULT '在营'           COMMENT '企业状态',
    register_status    VARCHAR(20)   DEFAULT '存续'           COMMENT '注册状态',
    register_date      DATE          DEFAULT NULL            COMMENT '注册日期',
    legal_person       VARCHAR(50)   DEFAULT NULL            COMMENT '法定代表人',
    contact_person     VARCHAR(50)   DEFAULT NULL            COMMENT '联系人',
    contact_phone      VARCHAR(20)   DEFAULT NULL            COMMENT '联系电话',
    registered_capital DECIMAL(15,2) DEFAULT NULL            COMMENT '注册资本（万元）',
    remark             TEXT          DEFAULT NULL            COMMENT '备注',
    create_time        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_park_id (park_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信息表';

-- ============================================================
-- 4. evaluation_record — 评价记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS evaluation_record (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评价记录ID',
    park_id         BIGINT        NOT NULL                COMMENT '园区ID',
    year            INT           NOT NULL                COMMENT '评价年度',
    status          TINYINT       NOT NULL DEFAULT 0      COMMENT '状态：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回, 5=已上报, 6=已终止',
    total_score     DECIMAL(5,2)  DEFAULT NULL            COMMENT '总分',
    grade           VARCHAR(10)   DEFAULT NULL            COMMENT '绩效分档：A/B/C/D',
    reject_category VARCHAR(255)  DEFAULT NULL            COMMENT '驳回类别',
    eval_status     TINYINT       NOT NULL DEFAULT 1      COMMENT '参评状态：0=不参评, 1=参评',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_park_year (park_id, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价记录表';

-- ============================================================
-- 5. audit_record — 审核记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS audit_record (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
    evaluation_id BIGINT       NOT NULL                COMMENT '评价记录ID',
    auditor_id    BIGINT       NOT NULL                COMMENT '审核人ID',
    auditor_name  VARCHAR(50)  DEFAULT NULL            COMMENT '审核人姓名',
    auditor_role  TINYINT      DEFAULT NULL            COMMENT '审核人角色',
    action        TINYINT      NOT NULL                COMMENT '操作：1=通过, 2=驳回',
    opinion       TEXT         DEFAULT NULL            COMMENT '审核意见',
    from_status   VARCHAR(50)  DEFAULT NULL            COMMENT '变更前状态',
    to_status     VARCHAR(50)  DEFAULT NULL            COMMENT '变更后状态',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    PRIMARY KEY (id),
    KEY idx_evaluation_id (evaluation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- ============================================================
-- 6. park_operation — 运营数据表
-- ============================================================
CREATE TABLE IF NOT EXISTS park_operation (
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
-- 测试数据插入（仅当表为空时）
-- ============================================================

-- -----------------------------------------------------------
-- 用户数据
-- 密码均为 123456，BCrypt 加密值：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
-- role_type: 1=市级管理员, 2=区县管理员, 3=园区管理员
-- -----------------------------------------------------------
INSERT INTO sys_user (username, password, real_name, phone, role_type, district_id, park_id, status) VALUES
('admin',         '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张管理', '13800000001', 1, NULL,  NULL, 1),
('district',      '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李区管', '13800000002', 2, 3,    NULL, 1),
('district_bj',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王滨管', '13800000004', 2, 4,    NULL, 1),
('district_yh',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘余管', '13800000005', 2, 6,    NULL, 1),
('park',          '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王园管', '13800000003', 3, NULL, 1,    1)
ON DUPLICATE KEY UPDATE username=username;
