-- ============================================================
-- 区县信息表
-- ============================================================

DROP TABLE IF EXISTS district_info;
CREATE TABLE district_info (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '区县ID',
    name        VARCHAR(50)  NOT NULL                COMMENT '区县名称',
    code        VARCHAR(20)  DEFAULT NULL            COMMENT '区县编码',
    parent_id   BIGINT       DEFAULT 0               COMMENT '父级ID（预留）',
    sort_order  INT          DEFAULT 0               COMMENT '排序',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0=禁用, 1=启用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区县信息表';

-- 插入杭州市区县数据
INSERT INTO district_info (name, code, sort_order) VALUES
('上城区', '330102', 1),
('拱墅区', '330105', 2),
('西湖区', '330106', 3),
('滨江区', '330108', 4),
('萧山区', '330109', 5),
('余杭区', '330110', 6),
('富阳区', '330111', 7),
('临安区', '330112', 8),
('临平区', '330113', 9),
('钱塘区', '330114', 10),
('桐庐县', '330122', 11),
('淳安县', '330127', 12),
('建德市', '330182', 13);
