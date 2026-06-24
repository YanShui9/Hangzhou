import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def alter_document_table():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 修改 file_type 字段长度
        alter_sql = """
        ALTER TABLE park_document MODIFY COLUMN file_type VARCHAR(100) DEFAULT NULL COMMENT '文件类型(MIME类型)';
        """
        
        cursor.execute(alter_sql)
        conn.commit()
        print("表 park_document 修改成功：file_type 字段长度改为 100")
        
    except Exception as e:
        print(f"修改表失败: {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    alter_document_table()
