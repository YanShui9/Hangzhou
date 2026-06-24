-- 杭州市小微园区评价数据分析平台 - 数据库初始化脚本
-- 生成日期：2026-06-14

USE park_evaluation;

-- 1. 区县信息表
DROP TABLE IF EXISTS district_info;
CREATE TABLE district_info (
    id BIGINT NOT NULL AUTO_INCREMENT,
    district_code VARCHAR(20) NOT NULL,
    district_name VARCHAR(100) NOT NULL,
    city VARCHAR(50) DEFAULT ('杭州市'),
    province VARCHAR(50) DEFAULT ('浙江省'),
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_district_code (district_code),
    UNIQUE KEY uk_district_name (district_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区县信息表';

INSERT INTO district_info (district_code, district_name, city, province, sort_order) VALUES
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


-- 2. 园区基础信息表
DROP TABLE IF EXISTS park_info;
CREATE TABLE park_info (
    id BIGINT NOT NULL AUTO_INCREMENT,
    park_code VARCHAR(50) DEFAULT NULL,
    park_name VARCHAR(200) NOT NULL,
    park_type VARCHAR(50) DEFAULT NULL,
    district_id BIGINT DEFAULT NULL,
    district_name VARCHAR(50) DEFAULT NULL,
    address VARCHAR(500) DEFAULT NULL,
    star_level INT DEFAULT 0,
    performance VARCHAR(20) DEFAULT NULL,
    park_status VARCHAR(20) DEFAULT NULL,
    dev_mode VARCHAR(50) DEFAULT NULL,
    land_source VARCHAR(50) DEFAULT NULL,
    land_nature VARCHAR(50) DEFAULT NULL,
    recognition VARCHAR(100) DEFAULT NULL,
    is_upgrade VARCHAR(10) DEFAULT NULL,
    upgrade_content TEXT DEFAULT NULL,
    main_industry VARCHAR(500) DEFAULT NULL,
    introduction TEXT DEFAULT NULL,
    land_area DECIMAL(12,2) DEFAULT NULL,
    build_area DECIMAL(12,2) DEFAULT NULL,
    rent_remain_area DECIMAL(12,2) DEFAULT NULL,
    sale_remain_area DECIMAL(12,2) DEFAULT NULL,
    rented_area DECIMAL(12,2) DEFAULT NULL,
    public_facilities TEXT DEFAULT NULL,
    public_services TEXT DEFAULT NULL,
    operation_org_name VARCHAR(200) DEFAULT NULL,
    operation_org_code VARCHAR(50) DEFAULT NULL,
    operation_org_nature VARCHAR(50) DEFAULT NULL,
    org_leader VARCHAR(50) DEFAULT NULL,
    org_leader_phone VARCHAR(20) DEFAULT NULL,
    org_contact VARCHAR(50) DEFAULT NULL,
    org_contact_phone VARCHAR(20) DEFAULT NULL,
    enterprise_count INT DEFAULT 0,
    above_scale_count INT DEFAULT 0,
    high_tech_count INT DEFAULT 0,
    tech_sme_count INT DEFAULT 0,
    listed_count INT DEFAULT 0,
    hidden_champion_count INT DEFAULT 0,
    national_srti_count INT DEFAULT 0,
    provincial_srti_count INT DEFAULT 0,
    innovative_sme_count INT DEFAULT 0,
    employee_count INT DEFAULT 0,
    national_talent INT DEFAULT 0,
    provincial_talent INT DEFAULT 0,
    master_above INT DEFAULT 0,
    senior_engineer INT DEFAULT 0,
    engineer INT DEFAULT 0,
    senior_technician INT DEFAULT 0,
    master_degree INT DEFAULT 0,
    patent_total INT DEFAULT 0,
    patent_invention INT DEFAULT 0,
    patent_utility INT DEFAULT 0,
    patent_design INT DEFAULT 0,
    park_image TEXT DEFAULT NULL COMMENT '园区图片（Base64或URL）',
    create_by VARCHAR(50) DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50) DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_district_id (district_id),
    KEY idx_park_code (park_code),
    KEY idx_park_name (park_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区基础信息表';


-- 3. 修改用户表
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS district_id BIGINT DEFAULT NULL COMMENT '所属区县ID' AFTER district;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS role_type TINYINT DEFAULT NULL COMMENT '角色类型：1=市级, 2=区县, 3=园区' AFTER role;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS department VARCHAR(100) DEFAULT NULL COMMENT '所属部门' AFTER status;

UPDATE sys_user SET role_type = 1 WHERE role = 'admin';
UPDATE sys_user SET role_type = 2 WHERE role = 'district';
UPDATE sys_user SET role_type = 3 WHERE role = 'park';

UPDATE sys_user u
INNER JOIN district_info d ON u.district = d.district_name
SET u.district_id = d.id
WHERE u.district IS NOT NULL;
