import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def check_production_service_records():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 查询生产性服务类园区及其评价记录
        sql = """
            SELECT pi.id, pi.park_name, pi.park_type, er.status, er.year
            FROM park_info pi
            LEFT JOIN evaluation_record er ON pi.id = er.park_id
            WHERE pi.park_type = 1
            ORDER BY pi.id, er.year DESC
        """
        cursor.execute(sql)
        
        status_map = {0: '未知', 1: '待提交', 2: '待审核', 3: '审核通过', 4: '审核不通过', 5: '已撤回', 6: '已终止', 8: '未知'}
        type_map = {1: '生产性服务类', 2: '制造类'}
        
        print("生产性服务类园区评价记录：")
        print("-" * 80)
        print(f"{'园区ID':<8} {'园区名称':<20} {'类型':<12} {'年度':<6} {'状态'}")
        print("-" * 80)
        
        for row in cursor.fetchall():
            park_id, park_name, park_type, status, year = row
            status_name = status_map.get(status, f'未知({status})') if status else '无记录'
            type_name = type_map.get(park_type, f'未知({park_type})')
            year_str = str(year) if year else '-'
            print(f"{park_id:<8} {park_name[:20]:<20} {type_name:<12} {year_str:<6} {status_name}")
        
    except Exception as e:
        print(f"查询失败: {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    check_production_service_records()
