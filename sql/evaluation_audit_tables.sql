-- ============================================================
-- 评价模块 + 审核模块 数据库建表脚本
-- 适用数据库：MySQL 5.7+ / MySQL 8.0+
-- ============================================================

-- ----------------------------
-- 评价记录表
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_record`;
CREATE TABLE `evaluation_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `park_id` bigint(20) NOT NULL COMMENT '园区ID',
  `year` int(11) NOT NULL COMMENT '评价年份',
  `quarter` int(11) NOT NULL COMMENT '评价季度：1/2/3/4',
  `total_score` decimal(10,2) DEFAULT NULL COMMENT '评价总分',
  `status` varchar(30) NOT NULL DEFAULT 'draft' COMMENT '状态：draft/submitted/district_passed/district_rejected/city_passed/city_rejected',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int(11) DEFAULT 0 COMMENT '删除标志（0=未删除，1=已删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_park_year_quarter` (`park_id`, `year`, `quarter`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价记录表';

-- ----------------------------
-- 审核记录表
-- ----------------------------
DROP TABLE IF EXISTS `audit_record`;
CREATE TABLE `audit_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `evaluation_id` bigint(20) NOT NULL COMMENT '评价记录ID',
  `auditor_id` bigint(20) NOT NULL COMMENT '审核人ID',
  `action` varchar(20) NOT NULL COMMENT '审核动作：pass/reject',
  `opinion` varchar(1000) DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime NOT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `audit_level` varchar(20) NOT NULL COMMENT '审核级别：district=区县初审, city=市级终审',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_id` (`evaluation_id`),
  KEY `idx_auditor_id` (`auditor_id`),
  KEY `idx_audit_time` (`audit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';
