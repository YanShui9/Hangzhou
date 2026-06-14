-- =============================================
-- 园区运营数据表 - park_operation
-- =============================================

CREATE TABLE IF NOT EXISTS `park_operation` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `park_id`          BIGINT        NOT NULL COMMENT '园区ID',
    `year`             INT           NOT NULL COMMENT '年份',
    `month`            INT           NOT NULL COMMENT '月份（1-12）',
    `employment`       INT           DEFAULT 0 COMMENT '就业人数',
    `revenue`          DECIMAL(12,2) DEFAULT 0.00 COMMENT '营收金额（万元）',
    `enterprise_count` INT           DEFAULT 0 COMMENT '入驻企业数',
    `create_by`        VARCHAR(50)   DEFAULT NULL COMMENT '创建者',
    `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
    `update_by`        VARCHAR(50)   DEFAULT NULL COMMENT '更新者',
    `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`          TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标志：0=未删除, 1=已删除',
    `remark`           VARCHAR(255)  DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_park_year_month` (`park_id`, `year`, `month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区运营数据表';

-- =============================================
-- 示例数据
-- =============================================

INSERT INTO `park_operation` (`park_id`, `year`, `month`, `employment`, `revenue`, `enterprise_count`, `create_by`, `create_time`)
VALUES
-- 2026年数据（园区1）
(1, 2026, 1,  1200, 850.50,  45, 'system', NOW()),
(1, 2026, 2,  1250, 920.30,  46, 'system', NOW()),
(1, 2026, 3,  1300, 1050.80, 48, 'system', NOW()),
(1, 2026, 4,  1350, 1120.60, 50, 'system', NOW()),
(1, 2026, 5,  1400, 1200.00, 52, 'system', NOW()),
(1, 2026, 6,  1450, 1280.50, 53, 'system', NOW()),
(1, 2026, 7,  1500, 1350.20, 55, 'system', NOW()),
(1, 2026, 8,  1480, 1300.80, 54, 'system', NOW()),
(1, 2026, 9,  1520, 1400.50, 56, 'system', NOW()),
(1, 2026, 10, 1550, 1450.30, 57, 'system', NOW()),
(1, 2026, 11, 1580, 1500.60, 58, 'system', NOW()),
(1, 2026, 12, 1600, 1550.00, 60, 'system', NOW()),

-- 2026年数据（园区2）
(2, 2026, 1,  800,  520.30,  30, 'system', NOW()),
(2, 2026, 2,  820,  550.60,  31, 'system', NOW()),
(2, 2026, 3,  850,  600.20,  32, 'system', NOW()),
(2, 2026, 4,  880,  650.80,  33, 'system', NOW()),
(2, 2026, 5,  900,  700.50,  34, 'system', NOW()),
(2, 2026, 6,  920,  720.30,  35, 'system', NOW()),
(2, 2026, 7,  950,  780.60,  36, 'system', NOW()),
(2, 2026, 8,  940,  760.20,  35, 'system', NOW()),
(2, 2026, 9,  960,  800.50,  37, 'system', NOW()),
(2, 2026, 10, 980,  830.80,  38, 'system', NOW()),
(2, 2026, 11, 1000, 860.30,  39, 'system', NOW()),
(2, 2026, 12, 1020, 900.00,  40, 'system', NOW()),

-- 2025年数据（园区1，部分月份）
(1, 2025, 1,  1000, 700.00,  40, 'system', NOW()),
(1, 2025, 2,  1020, 720.50,  40, 'system', NOW()),
(1, 2025, 3,  1050, 750.30,  41, 'system', NOW()),
(1, 2025, 4,  1080, 780.60,  42, 'system', NOW()),
(1, 2025, 5,  1100, 800.00,  43, 'system', NOW()),
(1, 2025, 6,  1120, 820.50,  43, 'system', NOW()),
(1, 2025, 7,  1150, 850.20,  44, 'system', NOW()),
(1, 2025, 8,  1140, 840.80,  44, 'system', NOW()),
(1, 2025, 9,  1160, 860.50,  45, 'system', NOW()),
(1, 2025, 10, 1180, 880.30,  45, 'system', NOW()),
(1, 2025, 11, 1200, 900.60,  46, 'system', NOW()),
(1, 2025, 12, 1220, 920.00,  47, 'system', NOW());
