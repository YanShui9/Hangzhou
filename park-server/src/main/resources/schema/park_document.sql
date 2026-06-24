CREATE TABLE IF NOT EXISTS park_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    park_id BIGINT NOT NULL COMMENT '园区ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(100) COMMENT '文件类型',
    file_url VARCHAR(500) COMMENT '文件访问URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_park_id (park_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区行文文件表';
