import pymysql

conn = pymysql.connect(host='localhost', port=3306, user='root', password='123456', database='park_evaluation', charset='utf8mb4')
cursor = conn.cursor()

# 检查audit_record表
print("=== audit_record表结构 ===")
cursor.execute("DESCRIBE audit_record")
for row in cursor.fetchall():
    print(row)

print("\n=== audit_record表数据 ===")
cursor.execute("SELECT COUNT(*) FROM audit_record")
print(f"记录数: {cursor.fetchone()[0]}")
cursor.execute("SELECT * FROM audit_record LIMIT 3")
for row in cursor.fetchall():
    print(row)

# 检查evaluation_record表
print("\n=== evaluation_record表数据 ===")
cursor.execute("SELECT COUNT(*) FROM evaluation_record")
print(f"记录数: {cursor.fetchone()[0]}")
cursor.execute("SELECT id, park_id, year, status FROM evaluation_record LIMIT 5")
for row in cursor.fetchall():
    print(row)

conn.close()