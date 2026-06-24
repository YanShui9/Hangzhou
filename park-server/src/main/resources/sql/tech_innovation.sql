-- ============================================
-- 评价模块 - 科技创新 / 企业培育 相关表
-- 数据库：park_evaluation
-- ============================================

-- 1. 科技创新记录表
DROP TABLE IF EXISTS `tech_innovation`;
CREATE TABLE `tech_innovation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `evaluation_id` BIGINT(20) DEFAULT NULL COMMENT '评价记录ID',
  `project_name` VARCHAR(255) DEFAULT NULL COMMENT '所属项目名称',
  `file_id` BIGINT(20) DEFAULT NULL COMMENT '附件文件ID',
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT '附件文件名',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '附件文件URL',
  `category` VARCHAR(20) DEFAULT NULL COMMENT '人才类别：A/B/C/D类',
  `name` VARCHAR(100) DEFAULT NULL COMMENT '评定人才姓名',
  `date` DATE DEFAULT NULL COMMENT '评定日期',
  `company` VARCHAR(255) DEFAULT NULL COMMENT '所属企业',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科技创新记录';

-- 2. 院所合作项目表
DROP TABLE IF EXISTS `tech_project`;
CREATE TABLE `tech_project` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `evaluation_id` BIGINT(20) DEFAULT NULL COMMENT '评价记录ID',
  `name` VARCHAR(255) DEFAULT NULL COMMENT '项目名称',
  `file_id` BIGINT(20) DEFAULT NULL COMMENT '附件文件ID',
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT '附件文件名',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '附件文件URL',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='院所合作项目';

-- 3. 企业培育记录表
DROP TABLE IF EXISTS `cultivation_record`;
CREATE TABLE `cultivation_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `evaluation_id` BIGINT(20) DEFAULT NULL COMMENT '评价记录ID',
  `project_name` VARCHAR(255) DEFAULT NULL COMMENT '所属项目名称',
  `file_id` BIGINT(20) DEFAULT NULL COMMENT '附件文件ID',
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT '附件文件名',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '附件文件URL',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业培育记录';

-- 4. 评价附件表
DROP TABLE IF EXISTS `evaluation_file`;
CREATE TABLE `evaluation_file` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
  `stored_name` VARCHAR(255) DEFAULT NULL COMMENT '存储文件名（UUID）',
  `path` VARCHAR(500) DEFAULT NULL COMMENT '文件存储路径',
  `url` VARCHAR(500) DEFAULT NULL COMMENT '文件访问URL',
  `size` BIGINT(20) DEFAULT NULL COMMENT '文件大小（字节）',
  `file_type` VARCHAR(20) DEFAULT NULL COMMENT '文件类型：image/pdf/word/excel/other',
  `biz_type` VARCHAR(50) DEFAULT NULL COMMENT '业务模块',
  `biz_id` BIGINT(20) DEFAULT NULL COMMENT '关联业务ID',
  `upload_user_id` BIGINT(20) DEFAULT NULL COMMENT '上传用户ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_biz` (`biz_type`, `biz_id`),
  KEY `idx_stored_name` (`stored_name`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价附件';
