import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def check_westlake_park():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 查询西湖科技园的ID
        cursor.execute("SELECT id FROM park_info WHERE park_name LIKE '%西湖科技园%'")
        result = cursor.fetchone()
        if not result:
            print("未找到西湖科技园")
            return
        
        park_id = result[0]
        print(f"西湖科技园 ID: {park_id}")
        
        # 查询该园区的评价记录
        sql = """
            SELECT id, year, status, eval_status 
            FROM evaluation_record 
            WHERE park_id = %s 
            ORDER BY year DESC
        """
        cursor.execute(sql, (park_id,))
        
        status_map = {0: '未知', 1: '待提交', 2: '待审核', 3: '审核通过', 4: '审核不通过', 5: '已撤回', 6: '已终止'}
        eval_status_map = {1: '参评', 2: '不参评'}
        
        print("\n西湖科技园评价记录：")
        print("-" * 70)
        print(f"{'ID':<6} {'年度':<6} {'审核状态':<12} {'参评状态'}")
        print("-" * 70)
        
        for row in cursor.fetchall():
            record_id, year, status, eval_status = row
            status_name = status_map.get(status, f'未知({status})')
            eval_status_name = eval_status_map.get(eval_status, f'未知({eval_status})')
            print(f"{record_id:<6} {year:<6} {status_name:<12} {eval_status_name}")
        
    except Exception as e:
        print(f"查询失败: {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    check_westlake_park()
