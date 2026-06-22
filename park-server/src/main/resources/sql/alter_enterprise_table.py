import pymysql

# 连接数据库
conn = pymysql.connect(
    host='localhost',
    user='root',
    password='123456',
    database='park_evaluation',
    charset='utf8mb4'
)

cursor = conn.cursor()

try:
    # 添加 industry_code 列
    cursor.execute('ALTER TABLE enterprise_info ADD COLUMN industry_code VARCHAR(50) COMMENT "行业门类代码" AFTER park_id')
    print("industry_code 列添加成功")

    # 添加 industry_name 列
    cursor.execute('ALTER TABLE enterprise_info ADD COLUMN industry_name VARCHAR(100) COMMENT "行业名称" AFTER industry_code')
    print("industry_name 列添加成功")

    # 提交更改
    conn.commit()
    print("数据库表结构更新成功！")

except Exception as e:
    print(f"错误: {e}")
    conn.rollback()
finally:
    cursor.close()
    conn.close()