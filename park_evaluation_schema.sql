
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `audit_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
  `evaluation_id` bigint NOT NULL COMMENT '评价记录ID',
  `auditor_id` bigint NOT NULL COMMENT '审核人ID',
  `auditor_name` varchar(50) DEFAULT NULL COMMENT '审核人姓名',
  `auditor_role` tinyint DEFAULT NULL COMMENT '审核人角色',
  `action` tinyint NOT NULL COMMENT '操作：1=通过, 2=驳回',
  `opinion` text COMMENT '审核意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审核记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cultivation_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cultivation_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `evaluation_id` bigint DEFAULT NULL COMMENT '评价记录ID',
  `project_name` varchar(255) DEFAULT NULL COMMENT '所属项目名称',
  `file_id` bigint DEFAULT NULL COMMENT '附件文件ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '附件文件名',
  `file_url` varchar(500) DEFAULT NULL COMMENT '附件文件URL',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业培育记录';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `data_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `data_warehouse` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL COMMENT '数据名称',
  `file_type` varchar(50) NOT NULL COMMENT '文件类型',
  `year` int NOT NULL COMMENT '归属年度',
  `file_name` varchar(500) DEFAULT NULL COMMENT '附件文件名',
  `file_path` varchar(500) DEFAULT NULL COMMENT '附件存储路径',
  `file_size` varchar(20) DEFAULT NULL COMMENT '文件大小',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_file_type` (`file_type`),
  KEY `idx_year` (`year`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据仓库表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `district_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `district_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `district_code` varchar(20) NOT NULL,
  `district_name` varchar(100) NOT NULL,
  `city` varchar(100) DEFAULT (_gbk'������'),
  `province` varchar(100) DEFAULT (_gbk'�㽭ʡ'),
  `sort_order` int DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_district_code` (`district_code`),
  UNIQUE KEY `uk_district_name` (`district_name`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='区县信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `enterprise_honor_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enterprise_honor_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enterprise_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业名称',
  `credit_code` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '统一信用代码',
  `park_id` bigint DEFAULT NULL COMMENT '所属园区ID',
  `year` int NOT NULL COMMENT '年度',
  `honor_category` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '荣誉大类',
  `honor_type` varchar(80) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '荣誉类型(25种标准值)',
  `honor_count` int DEFAULT '1' COMMENT '数量',
  `source_file` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源文件',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_park_year` (`park_id`,`year`),
  KEY `idx_park_year_category` (`park_id`,`year`,`honor_category`),
  KEY `idx_enterprise` (`enterprise_name`,`credit_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4096 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='企业荣誉记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `enterprise_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enterprise_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `enterprise_name` varchar(100) NOT NULL COMMENT '企业名称',
  `credit_code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
  `park_id` bigint NOT NULL COMMENT '所属园区ID',
  `industry_code` varchar(20) DEFAULT NULL COMMENT '行业门类代码',
  `industry_name` varchar(100) DEFAULT NULL COMMENT '行业名称',
  `status` varchar(20) DEFAULT '在营' COMMENT '经营状态',
  `register_date` date DEFAULT NULL COMMENT '注册日期',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '法定代表人',
  `registered_capital` decimal(15,2) DEFAULT NULL COMMENT '注册资本（万元）',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `is_participate` tinyint NOT NULL DEFAULT '1' COMMENT '是否参评：0=不参评, 1=参评',
  `participate_reason` varchar(255) DEFAULT NULL COMMENT '不参评原因',
  `enterprise_address` varchar(500) DEFAULT NULL COMMENT '企业地址',
  `legal_person_phone` varchar(20) DEFAULT NULL COMMENT '法人电话',
  `employee_count` int DEFAULT NULL COMMENT '员工人数',
  `settled_time` date DEFAULT NULL COMMENT '入驻时间',
  `business_scope` varchar(1000) DEFAULT NULL COMMENT '经营范围',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_park_id` (`park_id`)
) ENGINE=InnoDB AUTO_INCREMENT=239 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `evaluation_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '原始文件名',
  `stored_name` varchar(255) DEFAULT NULL COMMENT '存储文件名（UUID）',
  `path` varchar(500) DEFAULT NULL COMMENT '文件存储路径',
  `url` varchar(500) DEFAULT NULL COMMENT '文件访问URL',
  `size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(20) DEFAULT NULL COMMENT '文件类型：image/pdf/word/excel/other',
  `biz_type` varchar(50) DEFAULT NULL COMMENT '业务模块',
  `biz_id` bigint DEFAULT NULL COMMENT '关联业务ID',
  `upload_user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_biz` (`biz_type`,`biz_id`),
  KEY `idx_stored_name` (`stored_name`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价附件';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `evaluation_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价记录ID',
  `park_id` bigint NOT NULL COMMENT '园区ID',
  `year` int NOT NULL COMMENT '评价年度',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回',
  `total_score` decimal(5,2) DEFAULT NULL COMMENT '总分',
  `grade` varchar(10) DEFAULT NULL COMMENT '绩效分档：A/B/C/D',
  `reject_category` varchar(255) DEFAULT NULL COMMENT '驳回类别',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `score_detail` json DEFAULT NULL COMMENT '审核打分详情JSON',
  `district_file` varchar(500) DEFAULT NULL COMMENT '区县行文文件路径',
  `reject_categories` varchar(500) DEFAULT NULL COMMENT '驳回指标类别列表(多选,逗号分隔)',
  PRIMARY KEY (`id`),
  KEY `idx_park_year` (`park_id`,`year`)
) ENGINE=InnoDB AUTO_INCREMENT=823 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `park_evaluation_score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `park_evaluation_score` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `park_id` bigint NOT NULL COMMENT '园区ID',
  `year` int NOT NULL COMMENT '年度',
  `revenue_per_mu` decimal(12,2) DEFAULT NULL COMMENT '亩均营收(万元/亩)',
  `tax_per_mu` decimal(12,2) DEFAULT NULL COMMENT '亩均税收(万元/亩)',
  `leading_industry` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主导产业Top3',
  `enterprise_total` int DEFAULT NULL COMMENT '参评企业数',
  `industry_dev_score` decimal(10,2) DEFAULT NULL COMMENT '产业发展总分',
  `enterprise_cultivate_score` decimal(10,2) DEFAULT NULL COMMENT '企业培育总分',
  `tech_innovation_score` decimal(10,2) DEFAULT NULL COMMENT '科技创新总分',
  `service_capability_score` decimal(10,2) DEFAULT NULL COMMENT '服务能力总分',
  `benefit_output_score` decimal(10,2) DEFAULT NULL COMMENT '效益产出总分',
  `safety_production_score` decimal(10,2) DEFAULT NULL COMMENT '安全生产总分',
  `other_score` decimal(10,2) DEFAULT NULL COMMENT '其他总分',
  `scores_json` json DEFAULT NULL COMMENT '全部子指标明细JSON',
  `total_score` decimal(10,2) DEFAULT NULL COMMENT '总分',
  `grade` varchar(2) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '绩效分档A/B/C/D',
  `data_source` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '数据来源',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_park_year` (`park_id`,`year`)
) ENGINE=InnoDB AUTO_INCREMENT=356 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='园区评价评分表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `park_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `park_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `park_code` varchar(50) DEFAULT NULL,
  `year` int DEFAULT NULL COMMENT '年度',
  `park_name` varchar(200) NOT NULL,
  `park_type` varchar(50) DEFAULT NULL,
  `district_id` bigint DEFAULT NULL,
  `district_name` varchar(100) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `star_level` int DEFAULT '0',
  `performance` varchar(20) DEFAULT NULL,
  `park_status` varchar(20) DEFAULT NULL,
  `dev_mode` varchar(50) DEFAULT NULL,
  `land_source` varchar(50) DEFAULT NULL,
  `land_nature` varchar(50) DEFAULT NULL,
  `recognition` varchar(100) DEFAULT NULL,
  `is_upgrade` varchar(10) DEFAULT NULL,
  `upgrade_content` text,
  `main_industry` varchar(500) DEFAULT NULL,
  `introduction` text,
  `land_area` decimal(12,2) DEFAULT NULL,
  `build_area` decimal(12,2) DEFAULT NULL,
  `rent_remain_area` decimal(12,2) DEFAULT NULL,
  `sale_remain_area` decimal(12,2) DEFAULT NULL,
  `rented_area` decimal(12,2) DEFAULT NULL,
  `public_facilities` text,
  `public_services` text,
  `operation_org_name` varchar(200) DEFAULT NULL,
  `operation_org_code` varchar(50) DEFAULT NULL,
  `operation_org_nature` varchar(50) DEFAULT NULL,
  `org_leader` varchar(50) DEFAULT NULL,
  `org_leader_phone` varchar(20) DEFAULT NULL,
  `org_contact` varchar(50) DEFAULT NULL,
  `org_contact_phone` varchar(20) DEFAULT NULL,
  `enterprise_count` int DEFAULT '0',
  `above_scale_count` int DEFAULT '0',
  `high_tech_count` int DEFAULT '0',
  `tech_sme_count` int DEFAULT '0',
  `listed_count` int DEFAULT '0',
  `hidden_champion_count` int DEFAULT '0',
  `national_srti_count` int DEFAULT '0',
  `provincial_srti_count` int DEFAULT '0',
  `innovative_sme_count` int DEFAULT '0',
  `employee_count` int DEFAULT '0',
  `national_talent` int DEFAULT '0',
  `provincial_talent` int DEFAULT '0',
  `master_above` int DEFAULT '0',
  `senior_engineer` int DEFAULT '0',
  `engineer` int DEFAULT '0',
  `senior_technician` int DEFAULT '0',
  `master_degree` int DEFAULT '0',
  `patent_total` int DEFAULT '0',
  `patent_invention` int DEFAULT '0',
  `patent_utility` int DEFAULT '0',
  `patent_design` int DEFAULT '0',
  `park_image` text COMMENT '园区图片（Base64或URL）',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_district_id` (`district_id`),
  KEY `idx_park_code` (`park_code`),
  KEY `idx_park_name` (`park_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1371 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='园区基础信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `park_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='运营数据表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `park_tax_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `park_tax_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `park_id` bigint DEFAULT NULL COMMENT '园区ID',
  `park_code` varchar(50) DEFAULT NULL COMMENT '园区代码',
  `park_name` varchar(200) NOT NULL COMMENT '园区名称',
  `tax_type` varchar(30) NOT NULL COMMENT '税收类型：total(总营税收)/leading_industry(主导产业)/enterprise_type(企业类型)',
  `industry_name` varchar(200) DEFAULT NULL COMMENT '产业名称（主导产业或企业类型）',
  `revenue` decimal(18,2) DEFAULT '0.00' COMMENT '营业收入(万元)',
  `tax` decimal(18,2) DEFAULT '0.00' COMMENT '净入库税款(万元)',
  `year` int NOT NULL COMMENT '年度',
  `source_file` varchar(500) DEFAULT NULL COMMENT '来源文件',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_park_year` (`park_id`,`year`),
  KEY `idx_tax_type_year` (`tax_type`,`year`)
) ENGINE=InnoDB AUTO_INCREMENT=1296 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='园区营税收表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=267 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tech_innovation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tech_innovation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `evaluation_id` bigint DEFAULT NULL COMMENT '评价记录ID',
  `project_name` varchar(255) DEFAULT NULL COMMENT '所属项目名称',
  `file_id` bigint DEFAULT NULL COMMENT '附件文件ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '附件文件名',
  `file_url` varchar(500) DEFAULT NULL COMMENT '附件文件URL',
  `category` varchar(20) DEFAULT NULL COMMENT '人才类别：A/B/C/D类',
  `name` varchar(100) DEFAULT NULL COMMENT '评定人才姓名',
  `date` date DEFAULT NULL COMMENT '评定日期',
  `company` varchar(255) DEFAULT NULL COMMENT '所属企业',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='科技创新记录';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tech_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tech_project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `evaluation_id` bigint DEFAULT NULL COMMENT '评价记录ID',
  `name` varchar(255) DEFAULT NULL COMMENT '项目名称',
  `file_id` bigint DEFAULT NULL COMMENT '附件文件ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '附件文件名',
  `file_url` varchar(500) DEFAULT NULL COMMENT '附件文件URL',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='院所合作项目';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `unreported_park_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unreported_park_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `park_id` bigint DEFAULT NULL COMMENT '园区ID（匹配 park_info）',
  `park_code` varchar(50) DEFAULT NULL COMMENT '园区代码',
  `park_name` varchar(200) NOT NULL COMMENT '园区名称',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='未上报运营园区名单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

