-- =============================================
-- 园区运营数据季度填报表 - park_operation_quarter
-- =============================================

CREATE TABLE IF NOT EXISTS `park_operation_quarter` (
    `id`                     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `park_id`                BIGINT        NOT NULL COMMENT '园区ID',
    `year`                   INT           NOT NULL COMMENT '年份',
    `quarter`                INT           NOT NULL COMMENT '季度（1-4）',
    `current_enterprises`    INT           DEFAULT 0 COMMENT '当前入驻企业（家）',
    `total_employees`        INT           DEFAULT 0 COMMENT '入驻企业总人数（人）',
    `enterprise_list_file_id` BIGINT       DEFAULT NULL COMMENT '入驻企业名单附件ID',
    `enterprise_list_file_name` VARCHAR(255) DEFAULT NULL COMMENT '入驻企业名单附件名',
    `enterprise_list_file_url` VARCHAR(500) DEFAULT NULL COMMENT '入驻企业名单附件URL',
    `rented_area`            DECIMAL(12,2) DEFAULT 0.00 COMMENT '园区已租面积（平方米）',
    `available_rent_area`    DECIMAL(12,2) DEFAULT 0.00 COMMENT '园区剩余可租面积（平方米）',
    `available_sale_area`    DECIMAL(12,2) DEFAULT 0.00 COMMENT '园区剩余可售面积（平方米）',
    `employee_count`         INT           DEFAULT 0 COMMENT '入驻企业员工总数（人）',
    `national_talent_count`  INT           DEFAULT 0 COMMENT '"国千"人才人数（人）',
    `provincial_talent_count` INT          DEFAULT 0 COMMENT '"省千"人才人数（人）',
    `senior_engineer_count`  INT           DEFAULT 0 COMMENT '正高级工程师人数（人）',
    `engineer_count`         INT           DEFAULT 0 COMMENT '高级工程师人数（人）',
    `technician_count`       INT           DEFAULT 0 COMMENT '高级技师人数（人）',
    `master_and_senior_count` INT          DEFAULT 0 COMMENT '硕士及副高以上人数（人）',
    `master_count`           INT           DEFAULT 0 COMMENT '硕士以上人数（人）',
    `patent_count`           INT           DEFAULT 0 COMMENT '专利拥有量（件）',
    `invention_patent_count` INT           DEFAULT 0 COMMENT '发明专利（件）',
    `utility_patent_count`   INT           DEFAULT 0 COMMENT '实用新型专利（件）',
    `design_patent_count`    INT           DEFAULT 0 COMMENT '外观设计专利（件）',
    `create_by`              VARCHAR(50)   DEFAULT NULL COMMENT '创建者',
    `create_time`            DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`              VARCHAR(50)   DEFAULT NULL COMMENT '更新者',
    `update_time`            DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标志：0=未删除, 1=已删除',
    `remark`                 VARCHAR(255)  DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_park_year_quarter` (`park_id`, `year`, `quarter`),
    KEY `idx_park_id` (`park_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区运营数据季度填报表';
