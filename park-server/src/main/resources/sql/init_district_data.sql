-- =============================================
-- 区县端数据库初始化脚本
-- 杭州市小微园区评价数据分析平台
-- =============================================

USE park_evaluation;

-- =============================================
-- 1. 区县信息表（district_info）
-- =============================================
INSERT IGNORE INTO `district_info` (`district_code`, `district_name`, `city`, `province`, `sort_order`) VALUES
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

-- =============================================
-- 2. 入驻企业表（enterprise_info）
-- =============================================
INSERT INTO `enterprise_info` (`enterprise_name`, `credit_code`, `district_id`, `park_id`, `enterprise_status`, `register_status`, `legal_person`, `contact_person`, `contact_phone`, `remark`) VALUES
-- 西湖科技园（park_id=1, district_id=3 西湖区）的企业
('杭州智云科技有限公司', '91330106MA12345A01', 3, 1, '正常', '在营', '张三', '张三', '13900001001', NULL),
('杭州锐创电子有限公司', '91330106MA12345A02', 3, 1, '正常', '在营', '李四', '李四', '13900001002', NULL),
('杭州云启数据服务有限公司', '91330106MA12345A03', 3, 1, '正常', '在营', '王五', '王五', '13900001003', NULL),
('杭州鑫源新材料科技有限公司', '91330106MA12345A04', 3, 1, '正常', '在营', '赵六', '赵六', '13900001004', NULL),
('杭州博远信息技术有限公司', '91330106MA12345A05', 3, 1, '正常', '在营', '孙七', '孙七', '13900001005', '规模较小暂未参评'),

-- 滨江创新园（park_id=2, district_id=4 滨江区）的企业
('杭州星辰生物科技有限公司', '91330108MA23456B01', 4, 2, '正常', '在营', '周八', '周八', '13900002001', NULL),
('杭州光合新能源科技有限公司', '91330108MA23456B02', 4, 2, '正常', '在营', '吴九', '吴九', '13900002002', NULL),
('杭州数智科技有限公司', '91330108MA23456B03', 4, 2, '正常', '在营', '郑十', '郑十', '13900002003', NULL),
('杭州微创精密仪器有限公司', '91330108MA23456B04', 4, 2, '正常', '在营', '冯十一', '冯十一', '13900002004', NULL),

-- 上城产业基地（park_id=3, district_id=1 上城区）的企业
('杭州锦绣纺织有限公司', '91330102MA34567C01', 1, 3, '正常', '在营', '陈十二', '陈十二', '13900003001', NULL),
('杭州绿源食品有限公司', '91330102MA34567C02', 1, 3, '正常', '在营', '褚十三', '褚十三', '13900003002', NULL),
('杭州恒达机械制造有限公司', '91330102MA34567C03', 1, 3, '正常', '在营', '卫十四', '卫十四', '13900003003', NULL);

-- =============================================
-- 3. 审核记录表（audit_record）
-- =============================================
INSERT INTO `audit_record` (`evaluation_id`, `auditor_id`, `auditor_name`, `auditor_role`, `action`, `opinion`, `create_time`) VALUES
-- 评价记录1（西湖科技园2024年，已通过）
(1, 2, '区县管理员', 2, 1, '材料齐全，数据准确，同意通过。', '2024-12-10 10:00:00'),
(1, 1, '系统管理员', 1, 1, '复核通过，评价结果有效。', '2024-12-15 14:30:00'),
-- 评价记录2（滨江创新园2024年，已通过）
(2, 2, '区县管理员', 2, 1, '数据完整，同意通过。', '2024-12-11 09:00:00'),
(2, 1, '系统管理员', 1, 1, '复核通过。', '2024-12-16 11:00:00'),
-- 评价记录3（上城产业基地2024年，待市局审）
(3, 2, '区县管理员', 2, 1, '材料基本齐全，提交市局审核。', '2024-12-12 16:00:00');

-- =============================================
-- 4. 评价结果表（evaluation_result）
-- =============================================
INSERT INTO `evaluation_result` (`park_id`, `evaluation_year`, `business_revenue_per_mu`, `tax_per_mu`, `land_area`, `participant_enterprise_count`, `enterprise_development_score`, `enterprise_cultivate_score`, `tech_innovation_score`, `service_level_score`, `benefit_output_score`, `safety_production_score`, `other_score`, `total_score`, `performance_grade`, `final_approve_time`, `create_time`) VALUES
(1, 2024, 120.50, 15.80, 500.00, 45, 18.50, 15.20, 17.80, 12.00, 10.00, 8.00, 4.00, 85.50, 'A', '2024-12-15 14:30:00', NOW()),
(2, 2024, 95.30, 12.60, 400.00, 38, 16.20, 13.80, 15.50, 11.20, 9.50, 7.50, 4.50, 78.20, 'B', '2024-12-16 11:00:00', NOW());

-- =============================================
-- 5. 评价材料表（evaluation_material）
-- =============================================
INSERT INTO `evaluation_material` (`park_id`, `evaluation_year`, `submit_user_id`, `material_status`, `business_revenue_per_mu`, `tax_per_mu`, `land_area`, `participant_enterprise_count`, `enterprise_development_score`, `enterprise_cultivate_score`, `tech_innovation_score`, `service_level_score`, `benefit_output_score`, `safety_production_score`, `other_score`, `total_score`, `performance_grade`, `submit_time`, `create_time`) VALUES
(1, 2024, 3, 'SUBMITTED', 120.50, 15.80, 500.00, 45, 18.50, 15.20, 17.80, 12.00, 10.00, 8.00, 4.00, 85.50, 'A', '2024-12-01 09:00:00', NOW()),
(2, 2024, 3, 'SUBMITTED', 95.30, 12.60, 400.00, 38, 16.20, 13.80, 15.50, 11.20, 9.50, 7.50, 4.50, 78.20, 'B', '2024-12-02 10:00:00', NOW()),
(3, 2024, 3, 'PENDING', 70.00, 8.50, 350.00, 30, 14.00, 12.00, 13.50, 10.00, 8.50, 7.00, 7.00, 72.00, NULL, NULL, NOW());

-- =============================================
-- 6. 评价复核表（evaluation_review）
-- =============================================
INSERT INTO `evaluation_review` (`material_id`, `park_id`, `review_level`, `reviewer_id`, `review_status`, `review_comment`, `review_time`, `create_time`) VALUES
(1, 1, 1, 2, 'APPROVED', '区县审核通过，材料完整，数据准确。', '2024-12-10 10:00:00', NOW()),
(1, 1, 2, 1, 'APPROVED', '市局复核通过，评价结果有效。', '2024-12-15 14:30:00', NOW()),
(2, 2, 1, 2, 'APPROVED', '区县审核通过，数据完整。', '2024-12-11 09:00:00', NOW()),
(2, 2, 2, 1, 'APPROVED', '市局复核通过。', '2024-12-16 11:00:00', NOW()),
(3, 3, 1, 2, 'APPROVED', '区县审核通过，提交市局。', '2024-12-12 16:00:00', NOW());

-- =============================================
-- 验证数据
-- =============================================
SELECT '区县信息' AS table_name, COUNT(*) AS record_count FROM district_info
UNION ALL
SELECT '入驻企业' AS table_name, COUNT(*) AS record_count FROM enterprise_info
UNION ALL
SELECT '审核记录' AS table_name, COUNT(*) AS record_count FROM audit_record
UNION ALL
SELECT '评价结果' AS table_name, COUNT(*) AS record_count FROM evaluation_result
UNION ALL
SELECT '评价材料' AS table_name, COUNT(*) AS record_count FROM evaluation_material
UNION ALL
SELECT '评价复核' AS table_name, COUNT(*) AS record_count FROM evaluation_review;
