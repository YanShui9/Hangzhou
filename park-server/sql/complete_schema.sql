-- ============================================================
-- 杭州市小微园区评价数据分析平台 - 完整数据库建表脚本
-- 生成日期: 2026-06-25
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 引擎: InnoDB
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `park_evaluation` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `park_evaluation`;

-- ============================================================
-- 1. 区县信息表 (district_info)
-- ============================================================
DROP TABLE IF EXISTS `district_info`;
CREATE TABLE `district_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `district_code` varchar(20) NOT NULL COMMENT '区县编码（如 330102）',
  `district_name` varchar(100) NOT NULL COMMENT '区县名称',
  `city` varchar(100) DEFAULT '杭州市' COMMENT '所属地市',
  `province` varchar(100) DEFAULT '浙江省' COMMENT '所属省份',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_district_code` (`district_code`),
  UNIQUE KEY `uk_district_name` (`district_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='区县信息表';

-- ============================================================
-- 2. 系统用户表 (sys_user)
-- ============================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `enterprise_name` varchar(200) DEFAULT NULL COMMENT '企业名称（园区账号使用）',
  `credit_code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码（园区账号使用）',
  `role_type` tinyint NOT NULL COMMENT '角色类型：1=市级管理员, 2=区县管理员, 3=园区管理员',
  `district_id` bigint DEFAULT NULL COMMENT '所属区县ID',
  `park_id` bigint DEFAULT NULL COMMENT '所属园区ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0=禁用, 1=启用',
  `department` varchar(100) DEFAULT NULL COMMENT '所属部门',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_type` (`role_type`),
  KEY `idx_district_id` (`district_id`),
  KEY `idx_park_id` (`park_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

-- ============================================================
-- 3. 园区基础信息表 (park_info)
-- ============================================================
DROP TABLE IF EXISTS `park_info`;
CREATE TABLE `park_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `year` int DEFAULT NULL COMMENT '年度（默认当前年）',
  `park_code` varchar(50) DEFAULT NULL COMMENT '园区代码',
  `park_name` varchar(200) NOT NULL COMMENT '园区名称',
  `park_type` varchar(50) DEFAULT NULL COMMENT '园区类型（生产性制造类/生产性服务类）',
  `district_id` bigint DEFAULT NULL COMMENT '所属区县ID',
  `district_name` varchar(100) DEFAULT NULL COMMENT '区县名称',
  `address` varchar(500) DEFAULT NULL COMMENT '园区地址',
  `star_level` int DEFAULT '0' COMMENT '园区星级',
  `performance` varchar(20) DEFAULT NULL COMMENT '绩效评价（A/B/C/D/未参评）',
  `park_status` varchar(20) DEFAULT NULL COMMENT '园区状态（已投运/在建/规划）',
  `dev_mode` varchar(50) DEFAULT NULL COMMENT '开发模式',
  `land_source` varchar(50) DEFAULT NULL COMMENT '土地来源',
  `land_nature` varchar(50) DEFAULT NULL COMMENT '土地性质',
  `recognition` varchar(100) DEFAULT NULL COMMENT '园区认定',
  `is_upgrade` varchar(10) DEFAULT NULL COMMENT '是否升级改造',
  `upgrade_content` text COMMENT '改造提升内容',
  `main_industry` varchar(500) DEFAULT NULL COMMENT '主导产业',
  `introduction` text COMMENT '园区介绍',
  `land_area` decimal(12,2) DEFAULT NULL COMMENT '实际用地面积（亩）',
  `build_area` decimal(12,2) DEFAULT NULL COMMENT '已建建筑面积（平方米）',
  `rented_area` decimal(12,2) DEFAULT NULL COMMENT '园区已租面积（平方米）',
  `rent_remain_area` decimal(12,2) DEFAULT NULL COMMENT '园区剩余可租面积（平方米）',
  `sale_remain_area` decimal(12,2) DEFAULT NULL COMMENT '园区剩余可售面积（平方米）',
  `public_facilities` text COMMENT '公共配套设施',
  `public_services` text COMMENT '公共配套服务',
  `operation_org_name` varchar(200) DEFAULT NULL COMMENT '运营单位',
  `operation_org_code` varchar(50) DEFAULT NULL COMMENT '运营机构统一社会信用代码',
  `operation_org_nature` varchar(50) DEFAULT NULL COMMENT '运营性质',
  `org_leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `org_leader_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
  `org_contact` varchar(50) DEFAULT NULL COMMENT '联系人',
  `org_contact_phone` varchar(20) DEFAULT NULL COMMENT '联系人电话',
  `enterprise_count` int DEFAULT '0' COMMENT '入驻企业总数（家）',
  `above_scale_count` int DEFAULT '0' COMMENT '规模以上企业（家）',
  `high_tech_count` int DEFAULT '0' COMMENT '高新技术企业（家）',
  `tech_sme_count` int DEFAULT '0' COMMENT '科技型中小企业（家）',
  `listed_count` int DEFAULT '0' COMMENT '上市企业（家）',
  `hidden_champion_count` int DEFAULT '0' COMMENT '隐形冠军及培育企业（家）',
  `national_srti_count` int DEFAULT '0' COMMENT '国家级专精特新"小巨人"企业（家）',
  `provincial_srti_count` int DEFAULT '0' COMMENT '省专精特新中小企业（家）',
  `innovative_sme_count` int DEFAULT '0' COMMENT '创新型中小企业（家）',
  `employee_count` int DEFAULT '0' COMMENT '入驻企业员工总数（人）',
  `national_talent` int DEFAULT '0' COMMENT '"国千"人才人数（人）',
  `provincial_talent` int DEFAULT '0' COMMENT '"省千"人才人数（人）',
  `senior_engineer` int DEFAULT '0' COMMENT '正高级工程师人数（人）',
  `engineer` int DEFAULT '0' COMMENT '高级工程师人数（人）',
  `senior_technician` int DEFAULT '0' COMMENT '高级技师人数（人）',
  `master_above` int DEFAULT '0' COMMENT '硕士及副高以上人数（人）',
  `master_degree` int DEFAULT '0' COMMENT '硕士以上人数（人）',
  `patent_total` int DEFAULT '0' COMMENT '专利拥有量（件）',
  `patent_invention` int DEFAULT '0' COMMENT '发明专利（件）',
  `patent_utility` int DEFAULT '0' COMMENT '实用新型专利（件）',
  `patent_design` int DEFAULT '0' COMMENT '外观设计专利（件）',
  `park_image` text COMMENT '园区图片（JSON数组）',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0=未删除, 1=已删除',
  PRIMARY KEY (`id`),
  KEY `idx_district_id` (`district_id`),
  KEY `idx_park_code` (`park_code`),
  KEY `idx_park_name` (`park_name`),
  KEY `idx_star_level` (`star_level`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='园区基础信息表';

-- ============================================================
-- 4. 入驻企业信息表 (enterprise_info)
-- ============================================================
DROP TABLE IF EXISTS `enterprise_info`;
CREATE TABLE `enterprise_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `enterprise_name` varchar(200) NOT NULL COMMENT '企业名称',
  `credit_code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
  `park_id` bigint NOT NULL COMMENT '所属园区ID',
  `industry_code` varchar(20) DEFAULT NULL COMMENT '行业门类代码',
  `industry_name` varchar(100) DEFAULT NULL COMMENT '行业名称',
  `status` varchar(20) DEFAULT '在营' COMMENT '经营状态',
  `enterprise_address` varchar(500) DEFAULT NULL COMMENT '企业地址',
  `settled_time` date DEFAULT NULL COMMENT '入驻时间',
  `business_scope` varchar(1000) DEFAULT NULL COMMENT '经营范围',
  `register_date` date DEFAULT NULL COMMENT '注册日期',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '法定代表人',
  `registered_capital` decimal(15,2) DEFAULT NULL COMMENT '注册资本（万元）',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `is_participate` tinyint NOT NULL DEFAULT '1' COMMENT '是否参评：0=不参评, 1=参评',
  `participate_reason` varchar(255) DEFAULT NULL COMMENT '不参评原因',
  `legal_person_phone` varchar(20) DEFAULT NULL COMMENT '法人电话',
  `employee_count` int DEFAULT NULL COMMENT '员工人数',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_park_id` (`park_id`),
  KEY `idx_credit_code` (`credit_code`),
  KEY `idx_enterprise_name` (`enterprise_name`),
  KEY `idx_is_participate` (`is_participate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='入驻企业信息表';

-- ============================================================
-- 5. 企业荣誉记录表 (enterprise_honor_record)
-- ============================================================
DROP TABLE IF EXISTS `enterprise_honor_record`;
CREATE TABLE `enterprise_honor_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enterprise_name` varchar(200) DEFAULT NULL COMMENT '企业名称',
  `credit_code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
  `park_id` bigint DEFAULT NULL COMMENT '所属园区ID',
  `year` int NOT NULL COMMENT '年度',
  `honor_category` varchar(30) DEFAULT NULL COMMENT '荣誉大类',
  `honor_type` varchar(80) DEFAULT NULL COMMENT '荣誉类型',
  `honor_count` int DEFAULT '1' COMMENT '数量',
  `source_file` varchar(500) DEFAULT NULL COMMENT '来源文件',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_park_year` (`park_id`,`year`),
  KEY `idx_park_year_category` (`park_id`,`year`,`honor_category`),
  KEY `idx_enterprise` (`enterprise_name`,`credit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='企业荣誉记录表';

-- ============================================================
-- 6. 评价记录表 (evaluation_record)
-- ============================================================
DROP TABLE IF EXISTS `evaluation_record`;
CREATE TABLE `evaluation_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价记录ID',
  `park_id` bigint NOT NULL COMMENT '园区ID',
  `year` int NOT NULL COMMENT '评价年度',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回',
  `evaluation_status` tinyint DEFAULT NULL COMMENT '参评状态：1=参评, 0=不参评',
  `total_score` decimal(10,2) DEFAULT NULL COMMENT '总分',
  `grade` varchar(10) DEFAULT NULL COMMENT '绩效分档：A/B/C/D',
  `score_detail` json DEFAULT NULL COMMENT '审核打分详情JSON',
  `reject_categories` varchar(500) DEFAULT NULL COMMENT '驳回指标类别列表',
  `district_file` varchar(500) DEFAULT NULL COMMENT '区县行文文件路径',
  `park_extra_data` text COMMENT '园区端附加数据JSON（含上传文件元数据）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_park_year` (`park_id`,`year`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价记录表';

-- ============================================================
-- 7. 园区评价评分表 (park_evaluation_score)
-- ============================================================
DROP TABLE IF EXISTS `park_evaluation_score`;
CREATE TABLE `park_evaluation_score` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `park_id` bigint NOT NULL COMMENT '园区ID',
  `year` int NOT NULL COMMENT '年度',
  `revenue_per_mu` decimal(12,2) DEFAULT NULL COMMENT '亩均营收（万元/亩）',
  `tax_per_mu` decimal(12,2) DEFAULT NULL COMMENT '亩均税收（万元/亩）',
  `leading_industry` varchar(500) DEFAULT NULL COMMENT '主导产业',
  `enterprise_total` int DEFAULT NULL COMMENT '参评企业总数',
  `industry_dev_score` decimal(10,2) DEFAULT NULL COMMENT '产业发展得分',
  `enterprise_cultivate_score` decimal(10,2) DEFAULT NULL COMMENT '企业培育得分',
  `tech_innovation_score` decimal(10,2) DEFAULT NULL COMMENT '科技创新得分',
  `service_capability_score` decimal(10,2) DEFAULT NULL COMMENT '服务能力得分',
  `benefit_output_score` decimal(10,2) DEFAULT NULL COMMENT '效益产出得分',
  `safety_production_score` decimal(10,2) DEFAULT NULL COMMENT '安全生产得分',
  `other_score` decimal(10,2) DEFAULT NULL COMMENT '其他得分',
  `scores_json` json DEFAULT NULL COMMENT '各维度分数JSON',
  `total_score` decimal(10,2) DEFAULT NULL COMMENT '总得分',
  `grade` varchar(2) DEFAULT NULL COMMENT '绩效分档：A/B/C/D',
  `data_source` varchar(50) DEFAULT NULL COMMENT '数据来源',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_park_year` (`park_id`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='园区评价评分表';

-- ============================================================
-- 8. 评价关联企业表 (evaluation_enterprise)
-- ============================================================
DROP TABLE IF EXISTS `evaluation_enterprise`;
CREATE TABLE `evaluation_enterprise` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `evaluation_id` bigint DEFAULT NULL COMMENT '评价记录ID',
  `park_id` bigint DEFAULT NULL COMMENT '园区ID',
  `park_name` varchar(200) DEFAULT NULL COMMENT '园区名称',
  `enterprise_name` varchar(200) DEFAULT NULL COMMENT '入驻企业名称',
  `credit_code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
  `settled_start_time` varchar(50) DEFAULT NULL COMMENT '入驻开始时间',
  `settled_end_time` varchar(50) DEFAULT NULL COMMENT '入驻截止时间',
  `settled_date` varchar(100) DEFAULT NULL COMMENT '入驻起止时间（合并展示）',
  `registered_address` varchar(500) DEFAULT NULL COMMENT '企业注册地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_park_id` (`park_id`),
  KEY `idx_credit_code` (`credit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价关联企业表';

-- ============================================================
-- 9. 评价附件表 (evaluation_file)
-- ============================================================
DROP TABLE IF EXISTS `evaluation_file`;
CREATE TABLE `evaluation_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) DEFAULT NULL COMMENT '原始文件名',
  `stored_name` varchar(255) DEFAULT NULL COMMENT '存储文件名（UUID）',
  `path` varchar(500) DEFAULT NULL COMMENT '文件存储路径',
  `url` varchar(500) DEFAULT NULL COMMENT '文件访问URL',
  `size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(20) DEFAULT NULL COMMENT '文件类型：image/pdf/word/excel/other',
  `biz_type` varchar(50) DEFAULT NULL COMMENT '业务模块：tech_innovation/tech_project/enterprise/cultivation/industry',
  `biz_id` bigint DEFAULT NULL COMMENT '关联业务ID',
  `upload_user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_biz` (`biz_type`,`biz_id`),
  KEY `idx_stored_name` (`stored_name`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价附件表';

-- ============================================================
-- 10. 科技创新记录表 (tech_innovation)
-- ============================================================
DROP TABLE IF EXISTS `tech_innovation`;
CREATE TABLE `tech_innovation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `evaluation_id` bigint DEFAULT NULL COMMENT '评价记录ID',
  `project_name` varchar(255) DEFAULT NULL COMMENT '所属项目名称',
  `file_id` bigint DEFAULT NULL COMMENT '附件文件ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '附件文件名',
  `file_url` varchar(500) DEFAULT NULL COMMENT '附件文件URL',
  `category` varchar(20) DEFAULT NULL COMMENT '人才类别：A/B/C/D类',
  `name` varchar(100) DEFAULT NULL COMMENT '评定人才姓名',
  `date` date DEFAULT NULL COMMENT '评定日期',
  `company` varchar(255) DEFAULT NULL COMMENT '所属企业',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='科技创新记录表';

-- ============================================================
-- 11. 院所合作项目表 (tech_project)
-- ============================================================
DROP TABLE IF EXISTS `tech_project`;
CREATE TABLE `tech_project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `evaluation_id` bigint DEFAULT NULL COMMENT '评价记录ID',
  `name` varchar(255) DEFAULT NULL COMMENT '项目名称',
  `file_id` bigint DEFAULT NULL COMMENT '附件文件ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '附件文件名',
  `file_url` varchar(500) DEFAULT NULL COMMENT '附件文件URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='院所合作项目表';

-- ============================================================
-- 12. 企业培育记录表 (cultivation_record)
-- ============================================================
DROP TABLE IF EXISTS `cultivation_record`;
CREATE TABLE `cultivation_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `evaluation_id` bigint DEFAULT NULL COMMENT '评价记录ID',
  `project_name` varchar(255) DEFAULT NULL COMMENT '所属项目名称',
  `file_id` bigint DEFAULT NULL COMMENT '附件文件ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '附件文件名',
  `file_url` varchar(500) DEFAULT NULL COMMENT '附件文件URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业培育记录表';

-- ============================================================
-- 13. 审核记录表 (audit_record)
-- ============================================================
DROP TABLE IF EXISTS `audit_record`;
CREATE TABLE `audit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
  `evaluation_id` bigint NOT NULL COMMENT '评价记录ID',
  `auditor_id` bigint NOT NULL COMMENT '审核人ID',
  `auditor_name` varchar(50) DEFAULT NULL COMMENT '审核人姓名',
  `auditor_role` tinyint DEFAULT NULL COMMENT '审核人角色：1=市级, 2=区县, 3=园区',
  `action` tinyint NOT NULL COMMENT '操作：1=通过, 2=驳回',
  `opinion` text COMMENT '审核意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_auditor_role` (`auditor_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审核记录表';

-- ============================================================
-- 14. 园区运营数据表 (park_operation)
-- ============================================================
DROP TABLE IF EXISTS `park_operation`;
CREATE TABLE `park_operation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '运营数据ID',
  `park_id` bigint NOT NULL COMMENT '园区ID',
  `year` int NOT NULL COMMENT '年度',
  `quarter` tinyint NOT NULL COMMENT '季度：1/2/3/4',
  `enterprise_count` int DEFAULT NULL COMMENT '入驻企业数',
  `employee_count` int DEFAULT NULL COMMENT '员工总数',
  `build_area` decimal(12,2) DEFAULT NULL COMMENT '已建面积',
  `land_area` decimal(12,2) DEFAULT NULL COMMENT '用地面积',
  `patent_count` int DEFAULT NULL COMMENT '专利总数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_park_year_quarter` (`park_id`,`year`,`quarter`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='园区运营数据表';

-- ============================================================
-- 15. 数据仓库表 (data_warehouse)
-- ============================================================
DROP TABLE IF EXISTS `data_warehouse`;
CREATE TABLE `data_warehouse` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) NOT NULL COMMENT '数据名称',
  `file_type` varchar(50) NOT NULL COMMENT '文件类型',
  `year` int NOT NULL COMMENT '归属年度',
  `file_name` varchar(500) DEFAULT NULL COMMENT '附件文件名',
  `file_path` varchar(500) DEFAULT NULL COMMENT '附件存储路径',
  `file_size` varchar(20) DEFAULT NULL COMMENT '文件大小',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0=未删除, 1=已删除',
  PRIMARY KEY (`id`),
  KEY `idx_file_type` (`file_type`),
  KEY `idx_year` (`year`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据仓库表';

-- ============================================================
-- 16. 园区税收记录表 (park_tax_record)
-- ============================================================
DROP TABLE IF EXISTS `park_tax_record`;
CREATE TABLE `park_tax_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `park_id` bigint DEFAULT NULL COMMENT '园区ID',
  `park_code` varchar(50) DEFAULT NULL COMMENT '园区代码',
  `park_name` varchar(200) NOT NULL COMMENT '园区名称',
  `revenue` decimal(18,2) DEFAULT '0.00' COMMENT '营业收入（万元）',
  `tax` decimal(18,2) DEFAULT '0.00' COMMENT '净入库税款（万元）',
  `tax_type` varchar(30) NOT NULL COMMENT '类型：park_total/leading_industry/enterprise_type',
  `industry_name` varchar(200) DEFAULT NULL COMMENT '产业名称（主导产业或企业类型）',
  `year` int NOT NULL COMMENT '年度',
  `source_file` varchar(500) DEFAULT NULL COMMENT '来源文件',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_park_year` (`park_id`,`year`),
  KEY `idx_tax_type_year` (`tax_type`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='园区税收记录表';

-- ============================================================
-- 17. 未上报运营园区记录表 (unreported_park_record)
-- ============================================================
DROP TABLE IF EXISTS `unreported_park_record`;
CREATE TABLE `unreported_park_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `park_id` bigint DEFAULT NULL COMMENT '园区ID（匹配 park_info）',
  `park_name` varchar(200) NOT NULL COMMENT '园区名称',
  `park_code` varchar(50) DEFAULT NULL COMMENT '园区代码',
  `park_type` varchar(50) DEFAULT NULL COMMENT '园区类型',
  `district_name` varchar(50) DEFAULT NULL COMMENT '所属区域',
  `unreported_quarter` varchar(20) DEFAULT NULL COMMENT '未上报季度，如 2026Q4',
  `year` int NOT NULL COMMENT '年度',
  `source_file` varchar(500) DEFAULT NULL COMMENT '来源文件',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_park_year` (`park_id`,`year`),
  KEY `idx_year_quarter` (`year`,`unreported_quarter`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='未上报运营园区记录表';

-- ============================================================
-- 18. 园区行文文件表 (park_document)
-- ============================================================
DROP TABLE IF EXISTS `park_document`;
CREATE TABLE `park_document` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `park_id` bigint DEFAULT NULL COMMENT '园区ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名',
  `file_url` varchar(500) DEFAULT NULL COMMENT '文件存储路径',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_park_id` (`park_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='园区行文文件表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 初始化区县数据
INSERT INTO `district_info` (`district_code`, `district_name`, `city`, `province`, `sort_order`) VALUES
('330102', '上城区', '杭州市', '浙江省', 1),
('330105', '拱墅区', '杭州市', '浙江省', 2),
('330106', '西湖区', '杭州市', '浙江省', 3),
('330108', '滨江区', '杭州市', '浙江省', 4),
('330109', '萧山区', '杭州市', '浙江省', 5),
('330110', '余杭区', '杭州市', '浙江省', 6),
('330111', '富阳区', '杭州市', '浙江省', 7),
('330112', '临安区', '杭州市', '浙江省', 8),
('330113', '临平区', '杭州市', '浙江省', 9),
('330114', '钱塘区', '杭州市', '浙江省', 10),
('330122', '桐庐县', '杭州市', '浙江省', 11),
('330127', '淳安县', '杭州市', '浙江省', 12),
('330182', '建德市', '杭州市', '浙江省', 13);

-- 初始化管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role_type`, `status`, `department`) VALUES
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', '13800000000', 1, 1, '市经信局');

-- ============================================================
-- 数据库建表完成
-- ============================================================
