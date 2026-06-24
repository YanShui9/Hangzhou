import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def check_manufacture_data():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 查询制造类园区（类型2）
        cursor.execute("SELECT id, park_name FROM park_info WHERE park_type = 2")
        manufacture_parks = cursor.fetchall()
        print(f"制造类园区总数: {len(manufacture_parks)}")
        
        # 查询制造类园区的评价记录
        sql = """
            SELECT pi.id, pi.park_name, er.status, er.year
            FROM park_info pi
            LEFT JOIN evaluation_record er ON pi.id = er.park_id
            WHERE pi.park_type = 2
            ORDER BY pi.id
        """
        cursor.execute(sql)
        
        status_map = {0: '未知', 1: '待提交', 2: '待审核', 3: '审核通过', 4: '审核不通过', 5: '已撤回', 6: '已终止'}
        
        print("\n制造类园区评价记录：")
        print("-" * 70)
        print(f"{'园区ID':<8} {'园区名称':<20} {'年度':<6} {'状态'}")
        print("-" * 70)
        
        parks_without_record = []
        
        for row in cursor.fetchall():
            park_id, park_name, status, year = row
            if status is None:
                parks_without_record.append((park_id, park_name))
                print(f"{park_id:<8} {park_name[:20]:<20} {'-':<6} 无评价记录")
            else:
                status_name = status_map.get(status, f'未知({status})')
                year_str = str(year) if year else '-'
                print(f"{park_id:<8} {park_name[:20]:<20} {year_str:<6} {status_name}")
        
        print(f"\n无评价记录的制造类园区数量: {len(parks_without_record)}")
        
        # 统计各状态数量
        cursor.execute("SELECT status, COUNT(*) FROM evaluation_record er JOIN park_info pi ON er.park_id = pi.id WHERE pi.park_type = 2 GROUP BY status")
        print("\n制造类园区评价记录状态分布：")
        for row in cursor.fetchall():
            status, count = row
            status_name = status_map.get(status, f'未知({status})')
            print(f"  {status_name}({status}): {count} 条")
        
    except Exception as e:
        print(f"查询失败: {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    check_manufacture_data()
