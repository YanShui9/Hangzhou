import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def check_park_types():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 查询园区类型分布
        cursor.execute("SELECT park_type, COUNT(*) as count FROM park_info GROUP BY park_type")
        print("园区类型分布：")
        for row in cursor.fetchall():
            print(f"  {row[0]}: {row[1]} 个园区")
        
        # 查询评价记录的状态分布
        cursor.execute("SELECT status, COUNT(*) as count FROM evaluation_record GROUP BY status")
        print("\n评价记录状态分布：")
        status_map = {1: '待提交', 2: '待审核', 3: '审核通过', 4: '审核不通过', 5: '已撤回', 6: '已终止'}
        for row in cursor.fetchall():
            status_name = status_map.get(row[0], f'未知({row[0]})')
            print(f"  {status_name}({row[0]}): {row[1]} 条")
        
    except Exception as e:
        print(f"查询失败: {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    check_park_types()
