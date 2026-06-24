import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def create_document_table():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 创建表的SQL
        create_table_sql = """
        CREATE TABLE IF NOT EXISTS park_document (
            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
            park_id BIGINT NOT NULL COMMENT '园区ID',
            file_name VARCHAR(255) NOT NULL COMMENT '文件名',
            file_size BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
            file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
            file_type VARCHAR(50) DEFAULT NULL COMMENT '文件类型(MIME类型)',
            file_url VARCHAR(500) DEFAULT NULL COMMENT '文件访问URL',
            create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
            update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
            INDEX idx_park_id (park_id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='园区文件表';
        """
        
        cursor.execute(create_table_sql)
        conn.commit()
        print("表 park_document 创建成功")
        
    except Exception as e:
        print(f"创建表失败: {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    create_document_table()
