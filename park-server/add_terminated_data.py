import pymysql
import random

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def add_terminated_data():
    try:
        # 连接数据库
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 查询西湖区的园区ID列表
        cursor.execute("SELECT id FROM park_info WHERE district_id = (SELECT id FROM district_info WHERE district_name = '西湖区')")
        park_ids = [row[0] for row in cursor.fetchall()]
        
        if not park_ids:
            print("未找到西湖区的园区")
            return
        
        # 查询已有的评价记录数量，避免重复
        cursor.execute("SELECT COUNT(*) FROM evaluation_record WHERE status = 6")
        existing_count = cursor.fetchone()[0]
        print(f"已存在 {existing_count} 条已终止状态的记录")
        
        # 为部分园区添加已终止状态的评价记录
        terminated_count = 0
        for park_id in park_ids[:5]:  # 选择前5个园区
            # 检查是否已有该园区的已终止记录
            cursor.execute("SELECT COUNT(*) FROM evaluation_record WHERE park_id = %s AND status = 6", (park_id,))
            if cursor.fetchone()[0] > 0:
                continue
            
            # 插入已终止状态的评价记录
            sql = """
                INSERT INTO evaluation_record (park_id, year, status, eval_status)
                VALUES (%s, 2023, 6, 1)
            """
            cursor.execute(sql, (park_id,))
            terminated_count += 1
        
        conn.commit()
        print(f"成功添加 {terminated_count} 条已终止状态的评价记录")
        
    except Exception as e:
        print(f"发生错误: {e}")
    finally:
        if conn:
            conn.close()

if __name__ == "__main__":
    add_terminated_data()
