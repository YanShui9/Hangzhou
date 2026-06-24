-- 测试数据：企业荣誉记录 + 园区评价评分
-- 生成范围：park_info 中前 60 个未删除园区，2024-2026 年

USE park_evaluation;

-- 清理旧测试数据（可选，谨慎使用）
-- DELETE FROM enterprise_honor_record WHERE source_file LIKE 'TEST_%';
-- DELETE FROM park_evaluation_score WHERE data_source = 'TEST_DATA';

SET @row_num := 0;

-- ============================================
-- 1. 企业荣誉记录 enterprise_honor_record
-- ============================================

-- 荣誉类型定义
DROP TEMPORARY TABLE IF EXISTS tmp_honor_types;
CREATE TEMPORARY TABLE tmp_honor_types (
    honor_type VARCHAR(80),
    honor_category VARCHAR(30)
);

INSERT INTO tmp_honor_types VALUES
('existing_above_scale', 'enterprise_cultivate'),
('new_above_scale', 'enterprise_cultivate'),
('retired_above_scale', 'enterprise_cultivate'),
('new_specialty_giant', 'enterprise_cultivate'),
('new_provincial_hidden_champion', 'enterprise_cultivate'),
('new_specialty_sme', 'enterprise_cultivate'),
('new_single_champion', 'enterprise_cultivate'),
('new_ipo', 'enterprise_cultivate'),
('new_national_high_tech', 'enterprise_cultivate'),
('innovative_sme', 'enterprise_cultivate'),
('new_provincial_tech_small', 'enterprise_cultivate'),
('early_invest_innovation', 'enterprise_cultivate'),
('new_first_equipment', 'tech_innovation'),
('first_version', 'tech_innovation'),
('first_batch', 'tech_innovation'),
('provincial_excellent_industrial', 'tech_innovation'),
('zhejiang_made_quality', 'tech_innovation'),
('new_national_rd_agency', 'tech_innovation'),
('new_provincial_rd_agency', 'tech_innovation'),
('new_municipal_rd_agency', 'tech_innovation'),
('public_service_platform', 'tech_innovation'),
('enterprise_incubator', 'tech_innovation'),
('talent_a_class', 'tech_innovation'),
('talent_b_class', 'tech_innovation'),
('talent_c_class', 'tech_innovation');

-- 为每个园区每年每种荣誉类型生成 1 条记录
INSERT INTO enterprise_honor_record (
    enterprise_name, credit_code, park_id, year,
    honor_category, honor_type, honor_count, source_file
)
SELECT
    CONCAT('测试企业_', p.id, '_', h.honor_type),
    CONCAT('91330100', LPAD(p.id, 10, '0'), 'M'),
    p.id,
    y.year,
    h.honor_category,
    h.honor_type,
    FLOOR(1 + RAND() * 5),
    'TEST_DATA'
FROM (
    SELECT id FROM park_info WHERE deleted = 0 ORDER BY id LIMIT 60
) p
CROSS JOIN (SELECT 2024 AS year UNION SELECT 2025 UNION SELECT 2026) y
CROSS JOIN tmp_honor_types h
WHERE RAND() > 0.2;  -- 80% 概率生成，制造一些 0 值

-- ============================================
-- 2. 园区评价评分 park_evaluation_score
-- ============================================

-- 先删除可能存在的旧测试数据，避免唯一键冲突
DELETE FROM park_evaluation_score WHERE data_source = 'TEST_DATA';

INSERT INTO park_evaluation_score (
    park_id, year,
    revenue_per_mu, tax_per_mu,
    leading_industry, enterprise_total,
    industry_dev_score, enterprise_cultivate_score, tech_innovation_score,
    service_capability_score, benefit_output_score, safety_production_score,
    other_score, scores_json, total_score, grade, data_source
)
SELECT
    p.id,
    y.year,
    ROUND(50 + RAND() * 450, 2),
    ROUND(5 + RAND() * 45, 2),
    '电子信息,高端装备制造,生物医药',
    p.enterprise_count,
    ROUND(RAND() * 20 + 5, 2),
    ROUND(RAND() * 40 + 10, 2),
    ROUND(RAND() * 30 + 5, 2),
    ROUND(RAND() * 60 + 20, 2),
    ROUND(RAND() * 40 + 10, 2),
    ROUND(RAND() * 20 + 80, 2),
    ROUND(RAND() * 10 + 5, 2),
    JSON_OBJECT(
        'industryDev', JSON_OBJECT('1', 5, '2', ROUND(RAND()*5,2), '3', ROUND(RAND()*5,2), '4', ROUND(RAND()*5,2), '5', ROUND(RAND()*5,2), '6', 0, '7', 0, '8', 0, '9', 0, '10', 0, '11', 0, '12', 0, '13', 0, '14', 0, 'total', ROUND(RAND()*20+5,2)),
        'entCultivate', JSON_OBJECT('1', ROUND(RAND()*15+5,2), '2', ROUND(RAND()*20+5,2), '3', 0, '4', 0, '5', 0, '6', 0, '7', 0, '8', 0, '9', 0, '10', 0, '11', 0, '12', 0, '13', 0, '14', 0, '15', 0, '16', 0, '17', 0, '18', 0, '19', 0, 'total', ROUND(RAND()*40+10,2)),
        'techInnovation', JSON_OBJECT('1', ROUND(RAND()*10+2,2), '2', ROUND(RAND()*10+2,2), '3', ROUND(RAND()*10+2,2), '4', 0, '5', 0, '6', 0, '7', 0, '8', 0, '9', 0, '10', 0, '11', 0, '12', 0, '13', 0, '14', 0, '15', 0, '16', 0, '17', 0, '18', 0, '19', 0, 'total', ROUND(RAND()*30+5,2)),
        'serviceCap', JSON_OBJECT('1', ROUND(RAND()*10+5,2), '2', ROUND(RAND()*10+5,2), '3', ROUND(RAND()*10+5,2), '4', ROUND(RAND()*10+5,2), '5', ROUND(RAND()*10+5,2), '6', ROUND(RAND()*10+5,2), '7', ROUND(RAND()*10+5,2), 'total', ROUND(RAND()*60+20,2)),
        'benefitOutput', JSON_OBJECT('1', ROUND(RAND()*5+2,2), '2', ROUND(RAND()*5+2,2), '3', ROUND(RAND()*5+2,2), '4', ROUND(RAND()*5+2,2), '5', ROUND(RAND()*5+2,2), '6', ROUND(RAND()*5+2,2), '7', ROUND(RAND()*5+2,2), '8', ROUND(RAND()*5+2,2), '9', ROUND(RAND()*5+2,2), 'total', ROUND(RAND()*40+10,2)),
        'safetyProd', JSON_OBJECT('1', ROUND(RAND()*5+2,2), '2', ROUND(RAND()*5+2,2), '3', ROUND(RAND()*5+2,2), '4', ROUND(RAND()*5+2,2), '5', 0, '6', 0, '7', 0, '8', 0, '9', 0, 'total', ROUND(RAND()*20+80,2)),
        'other', JSON_OBJECT('1', 1, '2', 0, '3', 0, 'total', ROUND(RAND()*10+5,2))
    ),
    ROUND(RAND() * 40 + 60, 2),
    CASE
        WHEN RAND() > 0.75 THEN 'A'
        WHEN RAND() > 0.5 THEN 'B'
        WHEN RAND() > 0.25 THEN 'C'
        ELSE 'D'
    END,
    'TEST_DATA'
FROM (
    SELECT id, enterprise_count FROM park_info WHERE deleted = 0 ORDER BY id LIMIT 60
) p
CROSS JOIN (SELECT 2024 AS year UNION SELECT 2025 UNION SELECT 2026) y;

-- ============================================
-- 3. 评价记录 evaluation_record（供评价审核页使用）
-- ============================================
DELETE FROM evaluation_record;
INSERT INTO evaluation_record (park_id, year, status, total_score, grade, reject_categories)
SELECT
    id,
    y.year,
    FLOOR(RAND() * 5),
    ROUND(RAND() * 40 + 60, 2),
    CASE
        WHEN RAND() > 0.75 THEN 'A'
        WHEN RAND() > 0.5 THEN 'B'
        WHEN RAND() > 0.25 THEN 'C'
        ELSE 'D'
    END,
    NULL
FROM park_info
CROSS JOIN (SELECT 2024 AS year UNION SELECT 2025 UNION SELECT 2026) y
WHERE deleted = 0;

SELECT 'enterprise_honor_record count' AS info, COUNT(*) FROM enterprise_honor_record;
SELECT 'park_evaluation_score count' AS info, COUNT(*) FROM park_evaluation_score;
SELECT 'evaluation_record count' AS info, COUNT(*) FROM evaluation_record;
