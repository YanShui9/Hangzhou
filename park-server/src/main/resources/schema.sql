-- ============================================
-- 杭州市小微园区评价数据分析平台 - H2数据库初始化脚本
-- 与实体类字段严格对应
-- ============================================

-- 1. 用户表 (对应 SysUser extends BaseEntity)
DROP TABLE IF EXISTS SYS_USER;
CREATE TABLE SYS_USER (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USERNAME VARCHAR(50) NOT NULL UNIQUE,
    PASSWORD VARCHAR(200) NOT NULL,
    REAL_NAME VARCHAR(50),
    PHONE VARCHAR(20),
    ROLE_TYPE TINYINT,
    DISTRICT_ID BIGINT,
    PARK_ID BIGINT,
    STATUS TINYINT DEFAULT 1,
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试用户数据（密码明文：123456）
INSERT INTO SYS_USER (USERNAME, PASSWORD, REAL_NAME, PHONE, ROLE_TYPE, STATUS) VALUES
('admin', '123456', '管理员', '13800138000', 1, 1),
('district01', '123456', '西湖区管理员', '13800138001', 2, 1),
('park01', '123456', '紫金港园区管理员', '13800138002', 3, 1);

-- 2. 区县信息表 (对应 DistrictInfo extends BaseEntity)
DROP TABLE IF EXISTS DISTRICT_INFO;
CREATE TABLE DISTRICT_INFO (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    DISTRICT_CODE VARCHAR(20) NOT NULL UNIQUE,
    DISTRICT_NAME VARCHAR(100) NOT NULL UNIQUE,
    CITY VARCHAR(50) DEFAULT '杭州市',
    PROVINCE VARCHAR(50) DEFAULT '浙江省',
    SORT_ORDER INT DEFAULT 0,
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO DISTRICT_INFO (DISTRICT_CODE, DISTRICT_NAME, CITY, PROVINCE, SORT_ORDER) VALUES
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

-- 更新用户表的district_id
UPDATE SYS_USER SET DISTRICT_ID = 3 WHERE USERNAME = 'district01';
UPDATE SYS_USER SET PARK_ID = 1 WHERE USERNAME = 'park01';

-- 3. 园区基础信息表 (对应 ParkInfo extends BaseEntity)
-- parkType: 1=生产性制造类, 2=生产性服务类
DROP TABLE IF EXISTS PARK_INFO;
CREATE TABLE PARK_INFO (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    PARK_CODE VARCHAR(50),
    PARK_NAME VARCHAR(200) NOT NULL,
    PARK_TYPE INT,
    DISTRICT_ID BIGINT,
    DISTRICT_NAME VARCHAR(50),
    ADDRESS VARCHAR(500),
    STAR_LEVEL INT DEFAULT 0,
    PERFORMANCE VARCHAR(20),
    PARK_STATUS VARCHAR(20),
    DEV_MODE VARCHAR(50),
    LAND_SOURCE VARCHAR(50),
    LAND_NATURE VARCHAR(50),
    RECOGNITION VARCHAR(100),
    IS_UPGRADE VARCHAR(10),
    UPGRADE_CONTENT TEXT,
    MAIN_INDUSTRY VARCHAR(500),
    INTRODUCTION TEXT,
    LAND_AREA DECIMAL(12,2),
    BUILD_AREA DECIMAL(12,2),
    RENT_REMAIN_AREA DECIMAL(12,2),
    SALE_REMAIN_AREA DECIMAL(12,2),
    RENTED_AREA DECIMAL(12,2),
    PUBLIC_FACILITIES TEXT,
    PUBLIC_SERVICES TEXT,
    OPERATION_ORG_NAME VARCHAR(200),
    OPERATION_ORG_CODE VARCHAR(50),
    OPERATION_ORG_NATURE VARCHAR(50),
    ORG_LEADER VARCHAR(50),
    ORG_LEADER_PHONE VARCHAR(20),
    ORG_CONTACT VARCHAR(50),
    ORG_CONTACT_PHONE VARCHAR(20),
    ENTERPRISE_COUNT INT DEFAULT 0,
    ABOVE_SCALE_COUNT INT DEFAULT 0,
    HIGH_TECH_COUNT INT DEFAULT 0,
    TECH_SME_COUNT INT DEFAULT 0,
    LISTED_COUNT INT DEFAULT 0,
    HIDDEN_CHAMPION_COUNT INT DEFAULT 0,
    NATIONAL_SRTI_COUNT INT DEFAULT 0,
    PROVINCIAL_SRTI_COUNT INT DEFAULT 0,
    INNOVATIVE_SME_COUNT INT DEFAULT 0,
    EMPLOYEE_COUNT INT DEFAULT 0,
    NATIONAL_TALENT INT DEFAULT 0,
    PROVINCIAL_TALENT INT DEFAULT 0,
    MASTER_ABOVE INT DEFAULT 0,
    SENIOR_ENGINEER INT DEFAULT 0,
    ENGINEER INT DEFAULT 0,
    SENIOR_TECHNICIAN INT DEFAULT 0,
    MASTER_DEGREE INT DEFAULT 0,
    PATENT_TOTAL INT DEFAULT 0,
    PATENT_INVENTION INT DEFAULT 0,
    PATENT_UTILITY INT DEFAULT 0,
    PATENT_DESIGN INT DEFAULT 0,
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO PARK_INFO (PARK_CODE, PARK_NAME, PARK_TYPE, DISTRICT_ID, DISTRICT_NAME, ADDRESS, STAR_LEVEL, PARK_STATUS, DEV_MODE, LAND_SOURCE, LAND_NATURE, RECOGNITION, IS_UPGRADE, MAIN_INDUSTRY, INTRODUCTION, LAND_AREA, BUILD_AREA, RENTED_AREA, RENT_REMAIN_AREA, SALE_REMAIN_AREA, OPERATION_ORG_NAME, OPERATION_ORG_NATURE, ORG_LEADER, ORG_LEADER_PHONE, ORG_CONTACT, ORG_CONTACT_PHONE, ENTERPRISE_COUNT, EMPLOYEE_COUNT) VALUES
('PK001', '紫金港生命健康产业园', 1, 3, '西湖区', '西湖区三墩镇金蓬街368号', 4, '已投运', '政府主导开发', '租用', '工业用地', '省级小微企业园', '是', '生物医药', '紫金港生命健康产业园位于杭州市西湖区，总占地面积40亩，建筑面积35043平方米。', 40, 35043, 25395.37, 3250, 0, '杭州紫金港科技城建设投资有限公司', '国有', '郑建', '15988115758', '沈烨琳', '15858172079', 15, 350),
('PK002', '杭州智慧信息产业园', 2, 4, '滨江区', '滨江区江南大道100号', 3, '已投运', '工业地产开发', '出让', '商业用地', '市级小微企业园', '否', '信息技术', '杭州智慧信息产业园坐落于滨江区核心区域。', 30, 50000, 42000.00, 8000, 0, '杭州智慧信息产业发展有限公司', '民营', '王建国', '13800138000', '李娜', '13900139000', 20, 500),
('PK003', '萧山智能制造产业园', 1, 5, '萧山区', '萧山区经济技术开发区', 5, '已投运', '龙头企业开发', '出让', '工业用地', '国家级小微企业园', '是', '智能制造', '萧山智能制造产业园是萧山区重点打造的高端制造产业平台。', 100, 80000, 72000.00, 8000, 0, '萧山智能制造产业发展有限公司', '国有', '张伟', '13700137000', '陈静', '13600136000', 25, 800),
('PK004', '余杭数字创意产业园', 2, 6, '余杭区', '余杭区梦想小镇', 3, '已投运', '企业联合开发', '划拨', '商业用地', '省级小微企业园', '否', '数字创意', '余杭数字创意产业园位于梦想小镇核心区。', 25, 35000, 30000.00, 5000, 0, '杭州梦想小镇投资发展有限公司', '国有', '刘洋', '13500135000', '赵雪', '13400134000', 30, 450),
('PK005', '富阳新材料产业园', 1, 7, '富阳区', '富阳区银湖街道', 2, '在建', '工业地产开发', '出让', '工业用地', '-', '否', '新材料', '富阳新材料产业园是富阳区重点培育的新兴产业园区。', 120, 0, 0.00, 100000, 0, '富阳新材料产业投资有限公司', '民营', '陈明', '13300133000', '周芳', '13200132000', 0, 0),
('PK006', '临安科创孵化园', 2, 8, '临安区', '临安区锦南新城', 3, '已投运', '专业机构开发', '划拨', '科研用地', '市级小微企业园', '是', '科技孵化', '临安科创孵化园是临安区科技创新的重要载体。', 15, 20000, 18000.00, 2000, 0, '临安科技创新服务中心', '国有', '吴强', '13100131000', '郑梅', '13000130000', 18, 280),
('PK007', '中电万谷产业园', 1, 3, '西湖区', '西湖区转塘街道万谷路88号', 3, '已投运', '企业联合开发', '出让', '工业用地', '省级小微企业园', '是', '电子信息', '中电万谷产业园是西湖区重点打造的电子信息产业平台。', 60, 45000, 38000.00, 7000, 0, '杭州中电万谷投资管理有限公司', '国有', '林峰', '13711112222', '张莉', '13622223333', 22, 520),
('PK008', '云创谷研发中心', 2, 3, '西湖区', '西湖区文一西路998号', 4, '已投运', '龙头企业开发', '出让', '商业用地', '省级小微企业园', '否', '云计算', '云创谷研发中心聚焦云计算、大数据等新一代信息技术产业。', 35, 28000, 25000.00, 3000, 0, '杭州云创谷科技有限公司', '民营', '孙伟', '13533334444', '王芳', '13644445555', 35, 680),
('PK009', '绿方科创大厦', 2, 3, '西湖区', '西湖区古翠路80号', 2, '已投运', '专业机构开发', '租赁', '商务用地', '市级小微企业园', '否', '科技服务', '绿方科创大厦是西湖区核心商圈内的科技创新服务载体。', 8, 12000, 10000.00, 2000, 0, '杭州绿方科创服务有限公司', '民营', '赵军', '13755556666', '陈雪', '13666667777', 28, 420),
('PK010', '尚坤云谷中心', 2, 3, '西湖区', '西湖区三墩镇灯彩街567号', 5, '已投运', '政府主导开发', '划拨', '商业用地', '省级小微企业园', '是', '人工智能', '尚坤云谷中心是西湖区人工智能产业创新基地。', 45, 40000, 35000.00, 5000, 0, '杭州尚坤投资管理有限公司', '民营', '马骏', '13977778888', '林小芳', '13888889999', 40, 750),
('PK011', '尚坤生态创孵中心', 2, 3, '西湖区', '西湖区双浦镇科海路288号', 3, '已投运', '村集体联合开发', '出让', '科研用地', '市级小微企业园', '否', '生态环保', '尚坤生态创孵中心聚焦生态环保产业孵化。', 50, 30000, 26000.00, 4000, 0, '杭州尚坤生态投资有限公司', '民营', '杨帆', '13699990000', '吴丽', '13700001111', 18, 320),
('PK012', '浙大科技园西湖分园', 1, 3, '西湖区', '西湖区浙大路38号', 5, '已投运', '政府主导开发', '划拨', '科研用地', '国家级小微企业园', '是', '产学研', '浙大科技园西湖分园依托浙江大学科研资源。', 80, 60000, 55000.00, 5000, 0, '浙江大学科技园发展有限公司', '国有', '陈志强', '13811112222', '李明辉', '13722223333', 55, 1200),
('PK013', '浙商大创业园', 2, 3, '西湖区', '西湖区教工路149号', 3, '已投运', '企业联合开发', '出让', '商业用地', '省级小微企业园', '否', '商贸服务', '浙商大创业园依托浙江工商大学创业资源。', 20, 18000, 15000.00, 3000, 0, '浙商大创业投资管理有限公司', '国有', '黄志伟', '13533335555', '周小红', '13644446666', 32, 480),
('PK014', '西湖科创智谷', 1, 3, '西湖区', '西湖区双浦镇袁浦街136号', 4, '已投运', '村集体联合开发', '出让', '工业用地', '省级小微企业园', '是', '智能装备', '西湖科创智谷是西湖区智能装备产业集聚区。', 70, 52000, 48000.00, 4000, 0, '杭州西湖科创智谷发展有限公司', '国有', '陈国平', '13755557777', '张秀英', '13666668888', 28, 650),
('PK015', '西溪创意产业园', 2, 3, '西湖区', '西湖区西溪湿地周边', 2, '已投运', '专业机构开发', '租赁', '商务用地', '-', '否', '文化创意', '西溪创意产业园依托西溪湿地生态资源发展文创产业。', 15, 10000, 8500.00, 1500, 0, '杭州西溪创意产业投资有限公司', '民营', '沈涛', '13977779999', '朱丽', '13888880000', 12, 180),
('PK016', '西湖数字经济小镇', 2, 3, '西湖区', '西湖区之江板块', 4, '已投运', '政企合作', '出让', '商业用地', '省级小微企业园', '是', '数字经济', '西湖数字经济小镇是西湖区数字经济核心平台。', 55, 48000, 42000.00, 6000, 0, '杭州之江数字经济发展有限公司', '国有', '吴志强', '13699991111', '王丽', '13700002222', 45, 880);

-- 4. 入驻企业表 (对应 EnterpriseInfo extends BaseEntity)
DROP TABLE IF EXISTS ENTERPRISE_INFO;
CREATE TABLE ENTERPRISE_INFO (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    ENTERPRISE_NAME VARCHAR(200) NOT NULL,
    CREDIT_CODE VARCHAR(50),
    DISTRICT_ID BIGINT,
    PARK_ID BIGINT,
    INDUSTRY_CODE VARCHAR(50),
    INDUSTRY_NAME VARCHAR(100),
    HONOR VARCHAR(200),
    ENTERPRISE_STATUS VARCHAR(20) DEFAULT 'normal',
    REGISTER_STATUS VARCHAR(20),
    LEGAL_PERSON VARCHAR(50),
    CONTACT_PERSON VARCHAR(50),
    CONTACT_PHONE VARCHAR(20),
    REMARK TEXT,
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO ENTERPRISE_INFO (PARK_ID, ENTERPRISE_NAME, CREDIT_CODE, LEGAL_PERSON, INDUSTRY_NAME) VALUES
(1, '杭州生物科技有限公司', '91330106MA27W7YH9L', '张三', '生物医药'),
(1, '浙江医疗器械有限公司', '91330106MA27W7YK2R', '李四', '医疗器械'),
(3, '杭州软件技术有限公司', '91330108MA27W7YL5T', '王五', '信息技术');

-- 5. 评价记录表 (对应 EvaluationRecord extends BaseEntity)
-- status: 0=草稿/未提交, 1=待区县审, 2=待市局审, 3=通过, 4=驳回, 5=已上报, 6=已终止
-- eval_status: 0=不参评, 1=参评
DROP TABLE IF EXISTS EVALUATION_RECORD;
CREATE TABLE EVALUATION_RECORD (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    PARK_ID BIGINT,
    EVAL_YEAR INT,
    STATUS TINYINT DEFAULT 0,
    EVAL_STATUS TINYINT DEFAULT 1,
    TOTAL_SCORE DECIMAL(10,2),
    GRADE VARCHAR(10),
    REJECT_CATEGORY VARCHAR(50),
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO EVALUATION_RECORD (PARK_ID, EVAL_YEAR, STATUS, EVAL_STATUS, TOTAL_SCORE, GRADE) VALUES
(1, 2024, 1, 1, NULL, NULL),
(2, 2024, 2, 1, NULL, NULL),
(3, 2024, 3, 1, 85.50, 'A'),
(4, 2024, 1, 1, NULL, NULL),
(5, 2024, 4, 1, 62.00, 'C'),
(7, 2024, 1, 1, NULL, NULL),
(8, 2024, 1, 1, NULL, NULL),
(9, 2024, 0, 0, NULL, NULL),
(10, 2024, 1, 1, NULL, NULL),
(11, 2024, 1, 1, NULL, NULL),
(12, 2024, 3, 1, 92.00, 'A'),
(13, 2024, 1, 1, NULL, NULL),
(14, 2024, 4, 1, 58.00, 'D'),
(15, 2024, 0, 0, NULL, NULL),
(16, 2024, 1, 1, NULL, NULL);

-- 6. 园区运营数据表 (对应 ParkOperation extends BaseEntity)
DROP TABLE IF EXISTS PARK_OPERATION;
CREATE TABLE PARK_OPERATION (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    PARK_ID BIGINT,
    `YEAR` INT,
    `QUARTER` INT,
    ENTERPRISE_COUNT INT,
    EMPLOYEE_COUNT INT,
    BUILD_AREA DECIMAL(12,2),
    LAND_AREA DECIMAL(12,2),
    PATENT_COUNT INT,
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO PARK_OPERATION (PARK_ID, `YEAR`, `QUARTER`, ENTERPRISE_COUNT, EMPLOYEE_COUNT, BUILD_AREA, LAND_AREA, PATENT_COUNT) VALUES
(1, 2024, 1, 15, 350, 35043, 40, 12),
(1, 2024, 2, 16, 360, 35043, 40, 15),
(2, 2024, 1, 20, 500, 50000, 30, 8),
(3, 2024, 1, 25, 800, 80000, 100, 22),
(4, 2024, 1, 30, 450, 35000, 25, 5);

-- 7. 审核记录表 (对应 AuditRecord)
-- action: 1=通过, 2=驳回
DROP TABLE IF EXISTS AUDIT_RECORD;
CREATE TABLE AUDIT_RECORD (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    EVALUATION_ID BIGINT,
    AUDITOR_ID BIGINT,
    AUDITOR_NAME VARCHAR(50),
    AUDITOR_ROLE INT,
    ACTION INT,
    OPINION TEXT,
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO AUDIT_RECORD (EVALUATION_ID, AUDITOR_ID, AUDITOR_NAME, AUDITOR_ROLE, ACTION, OPINION) VALUES
(3, 1, '管理员', 1, 1, '审核通过，数据完整'),
(5, 2, '西湖区管理员', 2, 2, '数据不完整，需补充');
