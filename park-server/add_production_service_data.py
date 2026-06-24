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

def add_production_service_data():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 查询生产性服务类园区（类型1）
        cursor.execute("SELECT id FROM park_info WHERE park_type = 1")
        park_ids = [row[0] for row in cursor.fetchall()]
        print(f"生产性服务类园区数量: {len(park_ids)}")
        
        # 查询已有评价记录的园区
        cursor.execute("SELECT DISTINCT park_id FROM evaluation_record")
        existing_park_ids = set([row[0] for row in cursor.fetchall()])
        print(f"已有评价记录的园区数量: {len(existing_park_ids)}")
        
        # 找出没有评价记录的生产性服务类园区
        new_park_ids = [pid for pid in park_ids if pid not in existing_park_ids]
        print(f"需要添加评价记录的园区数量: {len(new_park_ids)}")
        
        # 为这些园区添加评价记录
        added_count = 0
        for park_id in new_park_ids:
            # 随机选择状态
            status = random.choice([2, 3, 4])  # 待审核、审核通过、审核不通过
            
            # 插入评价记录
            sql = """
                INSERT INTO evaluation_record (park_id, year, status, eval_status)
                VALUES (%s, 2023, %s, 1)
            """
            cursor.execute(sql, (park_id, status))
            added_count += 1
        
        conn.commit()
        print(f"成功添加 {added_count} 条生产性服务类园区的评价记录")
        
        # 更新统计
        cursor.execute("SELECT COUNT(*) FROM evaluation_record WHERE status IN (1,2,3,4,5,6)")
        total = cursor.fetchone()[0]
        print(f"当前评价记录总数: {total}")
        
    except Exception as e:
        print(f"添加数据失败: {e}")
        if 'conn' in locals():
            conn.rollback()
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    add_production_service_data()
