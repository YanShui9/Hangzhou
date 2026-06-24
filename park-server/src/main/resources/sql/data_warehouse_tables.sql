-- ============================================================
-- 数据仓库业务表DDL
-- 包含：enterprise_honor_record、unreported_park_record、park_tax_record
-- ============================================================

USE park_evaluation;

-- ============================================================
-- 1. 企业荣誉记录表（可能已存在，使用 IF NOT EXISTS）
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_honor_record (
    id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    enterprise_name  VARCHAR(200) DEFAULT NULL            COMMENT '企业名称',
    credit_code      VARCHAR(50)  DEFAULT NULL            COMMENT '企业代码/统一社会信用代码',
    park_id          BIGINT       DEFAULT NULL            COMMENT '所属园区ID',
    year             INT          DEFAULT NULL            COMMENT '年度',
    honor_category   VARCHAR(50)  DEFAULT NULL            COMMENT '荣誉大类：enterprise_cultivate/tech_innovation',
    honor_type       VARCHAR(80)  DEFAULT NULL            COMMENT '荣誉类型',
    honor_count      INT          DEFAULT 0               COMMENT '荣誉数量',
    source_file      VARCHAR(255) DEFAULT NULL            COMMENT '来源文件名',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_park_year (park_id, year),
    KEY idx_enterprise_name (enterprise_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业荣誉记录表';

-- ============================================================
-- 2. 未上报运营园区记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS unreported_park_record (
    id                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    park_name          VARCHAR(200) DEFAULT NULL            COMMENT '园区名称',
    park_code          VARCHAR(50)  DEFAULT NULL            COMMENT '园区代码',
    park_type          VARCHAR(50)  DEFAULT NULL            COMMENT '园区类型',
    district_name      VARCHAR(100) DEFAULT NULL            COMMENT '所属区域',
    unreported_quarter VARCHAR(50)  DEFAULT NULL            COMMENT '未上报季度',
    year               INT          DEFAULT NULL            COMMENT '年度',
    source_file        VARCHAR(255) DEFAULT NULL            COMMENT '来源文件名',
    create_time        DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_year (year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='未上报运营园区记录表';

-- ============================================================
-- 3. 园区税收记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS park_tax_record (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    park_name   VARCHAR(200)  DEFAULT NULL            COMMENT '园区名称',
    park_code   VARCHAR(50)   DEFAULT NULL            COMMENT '园区代码',
    revenue     DECIMAL(15,2) DEFAULT NULL            COMMENT '营业收入',
    tax         DECIMAL(15,2) DEFAULT NULL            COMMENT '净入库税款',
    tax_type    VARCHAR(50)   DEFAULT NULL            COMMENT '类型：park_total/leading_industry/enterprise_type',
    year        INT           DEFAULT NULL            COMMENT '年度',
    source_file VARCHAR(255)  DEFAULT NULL            COMMENT '来源文件名',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_year_type (year, tax_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区税收记录表';
