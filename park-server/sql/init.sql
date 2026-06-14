/*
 * 杭州小微园区评价系统数据库初始化脚本
 * 兼容 MySQL 5.7+ 和 MySQL 8.0+
 */

-- 创建数据库
DROP DATABASE IF EXISTS park_evaluation;
CREATE DATABASE park_evaluation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE park_evaluation;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- Table structure for audit_record
-- ============================================================
DROP TABLE IF EXISTS `audit_record`;
CREATE TABLE `audit_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
  `evaluation_id` bigint NOT NULL COMMENT '评价记录ID',
  `auditor_id` bigint NOT NULL COMMENT '审核人ID',
  `auditor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `auditor_role` tinyint NULL DEFAULT NULL COMMENT '审核人角色',
  `action` tinyint NOT NULL COMMENT '操作：1=通过, 2=驳回',
  `opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '审核意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_evaluation_id`(`evaluation_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审核记录表' ROW_FORMAT = Dynamic;

-- Records of audit_record
INSERT INTO `audit_record` VALUES (1, 2, 2, '李区管', 2, 1, '材料齐全，数据真实，予以通过', '2026-06-11 19:00:25');

-- ============================================================
-- Table structure for district_info
-- ============================================================
DROP TABLE IF EXISTS `district_info`;
CREATE TABLE `district_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '区县ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '区县名称',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区县编码',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父级ID（预留）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=禁用, 1=启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '区县信息表' ROW_FORMAT = Dynamic;

-- Records of district_info
INSERT INTO `district_info` VALUES (1, '上城区', '330102', 0, 1, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (2, '拱墅区', '330105', 0, 2, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (3, '西湖区', '330106', 0, 3, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (4, '滨江区', '330108', 0, 4, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (5, '萧山区', '330109', 0, 5, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (6, '余杭区', '330110', 0, 6, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (7, '富阳区', '330111', 0, 7, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (8, '临安区', '330112', 0, 8, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (9, '临平区', '330113', 0, 9, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (10, '钱塘区', '330114', 0, 10, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (11, '桐庐县', '330122', 0, 11, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (12, '淳安县', '330127', 0, 12, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');
INSERT INTO `district_info` VALUES (13, '建德市', '330182', 0, 13, 1, '2026-06-11 20:56:54', '2026-06-11 20:56:54');

-- ============================================================
-- Table structure for enterprise_info
-- ============================================================
DROP TABLE IF EXISTS `enterprise_info`;
CREATE TABLE `enterprise_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `enterprise_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '企业名称',
  `credit_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '统一社会信用代码',
  `park_id` bigint NOT NULL COMMENT '所属园区ID',
  `industry_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行业门类代码',
  `industry_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行业名称',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '在营' COMMENT '经营状态',
  `register_date` date NULL DEFAULT NULL COMMENT '注册日期',
  `legal_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法定代表人',
  `registered_capital` decimal(15, 2) NULL DEFAULT NULL COMMENT '注册资本（万元）',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `is_participate` tinyint NOT NULL DEFAULT 1 COMMENT '是否参评：0=不参评, 1=参评',
  `participate_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '不参评原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_park_id`(`park_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业信息表' ROW_FORMAT = Dynamic;

-- Records of enterprise_info
INSERT INTO `enterprise_info` VALUES (1, '杭州智云科技有限公司', '91330100MA12345678', 1, 'I65', '软件和信息技术服务业', '在营', '2018-05-10', '张三', 500.00, '孙经理', '13900000001', 1, NULL, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `enterprise_info` VALUES (2, '杭州绿能环保科技有限公司', '91330100MA23456789', 1, 'N77', '生态保护和环境治理业', '在营', '2019-03-15', '李四', 200.00, '周经理', '13900000002', 1, NULL, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `enterprise_info` VALUES (3, '杭州量子芯片有限公司', '91330100MA34567890', 2, 'C39', '计算机、通信和其他电子设备制造业', '在营', '2017-08-20', '王五', 2000.00, '吴经理', '13900000003', 1, NULL, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `enterprise_info` VALUES (4, '杭州生物医药研究院有限公司', '91330100MA45678901', 2, 'C27', '医药制造业', '在营', '2020-01-08', '赵六', 800.00, '郑经理', '13900000004', 1, NULL, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `enterprise_info` VALUES (5, '杭州新材科技有限公司', '91330100MA56789012', 3, 'C30', '非金属矿物制品业', '在营', '2021-06-25', '钱七', 300.00, '冯经理', '13900000005', 0, '成立不满3年', '2026-06-11 19:00:25', '2026-06-11 19:00:25');

-- ============================================================
-- Table structure for evaluation_record
-- ============================================================
DROP TABLE IF EXISTS `evaluation_record`;
CREATE TABLE `evaluation_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价记录ID',
  `park_id` bigint NOT NULL COMMENT '园区ID',
  `year` int NOT NULL COMMENT '评价年度',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回',
  `total_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总分',
  `grade` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绩效分档：A/B/C/D',
  `reject_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '驳回类别',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_park_year`(`park_id` ASC, `year` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '评价记录表' ROW_FORMAT = Dynamic;

-- Records of evaluation_record
INSERT INTO `evaluation_record` VALUES (1, 1, 2026, 1, 85.50, 'B', NULL, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `evaluation_record` VALUES (2, 2, 2026, 3, 92.00, 'A', NULL, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `evaluation_record` VALUES (3, 3, 2026, 0, NULL, NULL, NULL, '2026-06-11 19:00:25', '2026-06-11 19:00:25');

-- ============================================================
-- Table structure for park_info
-- ============================================================
DROP TABLE IF EXISTS `park_info`;
CREATE TABLE `park_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '园区ID',
  `park_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '园区名称',
  `park_type` tinyint NULL DEFAULT NULL COMMENT '园区类型：1=制造类, 2=服务类',
  `district_id` bigint NULL DEFAULT NULL COMMENT '所属区县ID',
  `district_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区县名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '园区地址',
  `build_area` decimal(12, 2) NULL DEFAULT NULL COMMENT '已建建筑面积（亩）',
  `land_area` decimal(12, 2) NULL DEFAULT NULL COMMENT '实际用地数（亩）',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '园区简介',
  `star_level` tinyint NULL DEFAULT NULL COMMENT '星级：null=未评定, 3=三星, 4=四星, 5=五星',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '园区信息表' ROW_FORMAT = Dynamic;

-- Records of park_info
INSERT INTO `park_info` VALUES (1, '西湖科技园', 1, 1, '西湖区', '杭州市西湖区文一西路998号', 350.00, 500.00, '陈主任', '0571-88000001', '西湖科技园是杭州市重点扶持的科技产业园区', 4, '2026-06-11 19:00:25', '2026-06-11 21:04:46');
INSERT INTO `park_info` VALUES (2, '滨江高新技术园', 1, 2, '滨江区', '杭州市滨江区江南大道100号', 600.00, 800.00, '赵主任', '0571-88000002', '滨江高新技术园聚焦集成电路和生物医药产业', 5, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `park_info` VALUES (3, '余杭未来产业园', 2, 3, '余杭区', '杭州市余杭区良渚街道200号', 900.00, 1200.00, '刘主任', '0571-88000003', '余杭未来产业园以数字经济和新材料为主导', NULL, '2026-06-11 19:00:25', '2026-06-11 19:00:25');

-- ============================================================
-- Table structure for park_operation
-- ============================================================
DROP TABLE IF EXISTS `park_operation`;
CREATE TABLE `park_operation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '运营数据ID',
  `park_id` bigint NOT NULL COMMENT '园区ID',
  `year` int NOT NULL COMMENT '年度',
  `quarter` tinyint NOT NULL COMMENT '季度：1/2/3/4',
  `enterprise_count` int NULL DEFAULT NULL COMMENT '入驻企业数',
  `employee_count` int NULL DEFAULT NULL COMMENT '员工总数',
  `build_area` decimal(12, 2) NULL DEFAULT NULL COMMENT '已建面积',
  `land_area` decimal(12, 2) NULL DEFAULT NULL COMMENT '用地面积',
  `patent_count` int NULL DEFAULT NULL COMMENT '专利总数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_park_year_quarter`(`park_id` ASC, `year` ASC, `quarter` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '运营数据表' ROW_FORMAT = Dynamic;

-- Records of park_operation
INSERT INTO `park_operation` VALUES (1, 1, 2026, 1, 45, 3200, 350.00, 500.00, 120, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `park_operation` VALUES (2, 1, 2026, 2, 46, 3350, 355.00, 500.00, 125, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `park_operation` VALUES (3, 2, 2026, 1, 78, 5600, 600.00, 800.00, 280, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `park_operation` VALUES (4, 2, 2026, 2, 80, 5800, 610.00, 800.00, 295, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `park_operation` VALUES (5, 3, 2026, 1, 25, 1800, 900.00, 1200.00, 45, '2026-06-11 19:00:25', '2026-06-11 19:00:25');

-- ============================================================
-- Table structure for sys_user
-- ============================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `role_type` tinyint NOT NULL COMMENT '角色类型：1=市级管理员, 2=区县管理员, 3=园区管理员',
  `district_id` bigint NULL DEFAULT NULL COMMENT '所属区县ID',
  `park_id` bigint NULL DEFAULT NULL COMMENT '所属园区ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=禁用, 1=启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- Records of sys_user
-- 密码均为 123456，BCrypt 加密值：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张管理', '13800000001', 1, NULL, NULL, 1, '2026-06-11 19:00:25', '2026-06-11 19:00:25');
INSERT INTO `sys_user` VALUES (2, 'district', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李区管', '13800000002', 2, 3, NULL, 1, '2026-06-11 19:00:25', '2026-06-11 21:04:15');
INSERT INTO `sys_user` VALUES (3, 'park', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王园管', '13800000003', 3, NULL, 1, 1, '2026-06-11 19:00:25', '2026-06-11 19:00:25');

SET FOREIGN_KEY_CHECKS = 1;
