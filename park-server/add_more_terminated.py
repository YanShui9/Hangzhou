import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def add_more_terminated():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 查询所有园区ID
        cursor.execute("SELECT id FROM park_info")
        park_ids = [row[0] for row in cursor.fetchall()]
        
        # 查询已有的已终止记录
        cursor.execute("SELECT COUNT(*) FROM evaluation_record WHERE status = 6")
        existing_count = cursor.fetchone()[0]
        print(f"已存在 {existing_count} 条已终止状态的记录")
        
        # 添加更多已终止状态的记录
        terminated_count = 0
        for park_id in park_ids[:8]:  # 选择前8个园区
            # 检查是否已有该园区的已终止记录
            cursor.execute("SELECT COUNT(*) FROM evaluation_record WHERE park_id = %s AND status = 6", (park_id,))
            if cursor.fetchone()[0] > 0:
                continue
            
            # 插入已终止状态的评价记录（2023年度）
            sql = """
                INSERT INTO evaluation_record (park_id, year, status, eval_status)
                VALUES (%s, 2023, 6, 1)
            """
            cursor.execute(sql, (park_id,))
            terminated_count += 1
        
        conn.commit()
        print(f"成功添加 {terminated_count} 条已终止状态的评价记录")
        
        # 验证结果
        cursor.execute("SELECT COUNT(*) FROM evaluation_record WHERE status = 6")
        total = cursor.fetchone()[0]
        print(f"当前已终止状态的记录总数: {total}")
        
    except Exception as e:
        print(f"发生错误: {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    add_more_terminated()
