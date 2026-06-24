-- ============================================================
-- 更新 park_info 表结构
-- 添加所有缺失的字段
-- ============================================================

USE park_evaluation;

-- 修改 park_type 字段类型（从 TINYINT 改为 VARCHAR，支持中文）
ALTER TABLE park_info MODIFY COLUMN park_type VARCHAR(50) DEFAULT NULL COMMENT '园区类型（生产性制造类/生产性服务类）';

-- 添加缺失的基本信息字段
ALTER TABLE park_info ADD COLUMN year INT DEFAULT NULL COMMENT '年度（默认当前年）' AFTER id;
ALTER TABLE park_info ADD COLUMN park_code VARCHAR(50) DEFAULT NULL COMMENT '园区代码' AFTER year;
ALTER TABLE park_info ADD COLUMN performance VARCHAR(10) DEFAULT NULL COMMENT '绩效评价（A/B/C/D/未参评）' AFTER star_level;
ALTER TABLE park_info ADD COLUMN park_status VARCHAR(20) DEFAULT NULL COMMENT '园区状态（已投运/在建/规划）' AFTER performance;
ALTER TABLE park_info ADD COLUMN dev_mode VARCHAR(50) DEFAULT NULL COMMENT '开发模式' AFTER park_status;
ALTER TABLE park_info ADD COLUMN land_source VARCHAR(50) DEFAULT NULL COMMENT '土地来源' AFTER dev_mode;
ALTER TABLE park_info ADD COLUMN land_nature VARCHAR(50) DEFAULT NULL COMMENT '土地性质' AFTER land_source;
ALTER TABLE park_info ADD COLUMN recognition VARCHAR(20) DEFAULT NULL COMMENT '园区认定' AFTER land_nature;
ALTER TABLE park_info ADD COLUMN is_upgrade VARCHAR(10) DEFAULT NULL COMMENT '是否升级改造' AFTER recognition;
ALTER TABLE park_info ADD COLUMN upgrade_content TEXT DEFAULT NULL COMMENT '改造提升内容' AFTER is_upgrade;
ALTER TABLE park_info ADD COLUMN main_industry VARCHAR(255) DEFAULT NULL COMMENT '主导产业' AFTER upgrade_content;
ALTER TABLE park_info ADD COLUMN public_facilities TEXT DEFAULT NULL COMMENT '公共配套设施' AFTER introduction;
ALTER TABLE park_info ADD COLUMN public_services TEXT DEFAULT NULL COMMENT '公共配套服务' AFTER public_facilities;

-- 修改 build_area 字段注释
ALTER TABLE park_info MODIFY COLUMN build_area DECIMAL(12,2) DEFAULT NULL COMMENT '已建建筑面积（平方米）';

-- 添加园区面积相关字段
ALTER TABLE park_info ADD COLUMN rented_area DECIMAL(12,2) DEFAULT NULL COMMENT '园区已租面积（平方米）' AFTER build_area;
ALTER TABLE park_info ADD COLUMN rent_remain_area DECIMAL(12,2) DEFAULT NULL COMMENT '园区剩余可租面积（平方米）' AFTER rented_area;
ALTER TABLE park_info ADD COLUMN sale_remain_area DECIMAL(12,2) DEFAULT NULL COMMENT '园区剩余可售面积（平方米）' AFTER rent_remain_area;

-- 添加运营机构信息字段
ALTER TABLE park_info ADD COLUMN operation_org_name VARCHAR(100) DEFAULT NULL COMMENT '运营单位' AFTER sale_remain_area;
ALTER TABLE park_info ADD COLUMN operation_org_code VARCHAR(50) DEFAULT NULL COMMENT '运营机构统一社会信用代码' AFTER operation_org_name;
ALTER TABLE park_info ADD COLUMN operation_org_nature VARCHAR(50) DEFAULT NULL COMMENT '运营性质' AFTER operation_org_code;
ALTER TABLE park_info ADD COLUMN org_leader VARCHAR(50) DEFAULT NULL COMMENT '负责人' AFTER operation_org_nature;
ALTER TABLE park_info ADD COLUMN org_leader_phone VARCHAR(20) DEFAULT NULL COMMENT '负责人电话' AFTER org_leader;
ALTER TABLE park_info ADD COLUMN org_contact VARCHAR(50) DEFAULT NULL COMMENT '联系人' AFTER org_leader_phone;
ALTER TABLE park_info ADD COLUMN org_contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系人电话' AFTER org_contact;

-- 添加入驻企业统计字段
ALTER TABLE park_info ADD COLUMN enterprise_count INT DEFAULT NULL COMMENT '入驻企业总数（家）' AFTER org_contact_phone;
ALTER TABLE park_info ADD COLUMN above_scale_count INT DEFAULT NULL COMMENT '规模以上企业（家）' AFTER enterprise_count;
ALTER TABLE park_info ADD COLUMN high_tech_count INT DEFAULT NULL COMMENT '高新技术企业（家）' AFTER above_scale_count;
ALTER TABLE park_info ADD COLUMN tech_sme_count INT DEFAULT NULL COMMENT '科技型中小企业（家）' AFTER high_tech_count;
ALTER TABLE park_info ADD COLUMN listed_count INT DEFAULT NULL COMMENT '上市企业（家）' AFTER tech_sme_count;
ALTER TABLE park_info ADD COLUMN hidden_champion_count INT DEFAULT NULL COMMENT '隐形冠军及培育企业（家）' AFTER listed_count;
ALTER TABLE park_info ADD COLUMN national_srti_count INT DEFAULT NULL COMMENT '国家级专精特新"小巨人"企业（家）' AFTER hidden_champion_count;
ALTER TABLE park_info ADD COLUMN provincial_srti_count INT DEFAULT NULL COMMENT '省专精特新中小企业（家）' AFTER national_srti_count;
ALTER TABLE park_info ADD COLUMN innovative_sme_count INT DEFAULT NULL COMMENT '创新型中小企业（家）' AFTER provincial_srti_count;

-- 添加人才统计字段
ALTER TABLE park_info ADD COLUMN employee_count INT DEFAULT NULL COMMENT '入驻企业员工总数（人）' AFTER innovative_sme_count;
ALTER TABLE park_info ADD COLUMN national_talent INT DEFAULT NULL COMMENT '"国千"人才人数（人）' AFTER employee_count;
ALTER TABLE park_info ADD COLUMN provincial_talent INT DEFAULT NULL COMMENT '"省千"人才人数（人）' AFTER national_talent;
ALTER TABLE park_info ADD COLUMN senior_engineer INT DEFAULT NULL COMMENT '正高级工程师人数（人）' AFTER provincial_talent;
ALTER TABLE park_info ADD COLUMN engineer INT DEFAULT NULL COMMENT '高级工程师人数（人）' AFTER senior_engineer;
ALTER TABLE park_info ADD COLUMN master_above INT DEFAULT NULL COMMENT '硕士及副高以上人数（人）' AFTER engineer;
ALTER TABLE park_info ADD COLUMN master_degree INT DEFAULT NULL COMMENT '硕士以上人数（人）' AFTER master_above;

-- 添加专利统计字段
ALTER TABLE park_info ADD COLUMN patent_total INT DEFAULT NULL COMMENT '专利拥有量（件）' AFTER master_degree;
ALTER TABLE park_info ADD COLUMN patent_invention INT DEFAULT NULL COMMENT '发明专利（件）' AFTER patent_total;
ALTER TABLE park_info ADD COLUMN patent_utility INT DEFAULT NULL COMMENT '实用新型专利（件）' AFTER patent_invention;
ALTER TABLE park_info ADD COLUMN patent_design INT DEFAULT NULL COMMENT '外观设计专利（件）' AFTER patent_utility;

-- 添加园区图片字段
ALTER TABLE park_info ADD COLUMN park_image TEXT DEFAULT NULL COMMENT '园区图片（JSON数组）' AFTER patent_design;

-- 更新现有数据的园区类型
UPDATE park_info SET park_type = '生产性制造类' WHERE park_type = '1';
UPDATE park_info SET park_type = '生产性服务类' WHERE park_type = '2';

-- 更新现有数据的年度
UPDATE park_info SET year = 2026 WHERE year IS NULL;