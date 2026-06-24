import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def check_district_data():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 查询区县信息
        cursor.execute("SELECT id, district_name FROM district_info")
        print("区县信息：")
        for row in cursor.fetchall():
            district_id, district_name = row
            print(f"  ID: {district_id}, 名称: {district_name}")
        
        # 查询用户所属区县
        cursor.execute("SELECT id, username, district_id FROM sys_user WHERE role_type = 2")
        print("\n区县管理员：")
        for row in cursor.fetchall():
            user_id, username, district_id = row
            print(f"  用户: {username}(ID:{user_id}), 区县ID: {district_id}")
        
        # 查询各区县的园区数量
        cursor.execute("SELECT district_name, COUNT(*) FROM park_info GROUP BY district_name")
        print("\n各区县园区数量：")
        for row in cursor.fetchall():
            district_name, count = row
            print(f"  {district_name}: {count} 个园区")
        
    except Exception as e:
        print(f"查询失败: {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    check_district_data()
