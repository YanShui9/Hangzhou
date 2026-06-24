# -*- coding: utf-8 -*-
"""
执行初始化SQL脚本
"""
import pymysql
import sys

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

SQL_FILE = r'd:\zzjj12\Hangzhou\park-server\src\main\resources\sql\init.sql'

def execute_sql_file(cursor, sql_file):
    """执行SQL文件"""
    with open(sql_file, 'r', encoding='utf-8') as f:
        sql_content = f.read()
    
    # 按分号分割SQL语句（简单处理）
    sql_statements = sql_content.split(';')
    
    for i, stmt in enumerate(sql_statements):
        stmt = stmt.strip()
        if stmt and not stmt.startswith('--'):
            try:
                cursor.execute(stmt)
                print(f"执行语句 {i+1}: OK")
            except Exception as e:
                print(f"执行语句 {i+1} 失败: {e}")
                print(f"SQL: {stmt[:100]}...")

def main():
    print("=" * 50)
    print("执行初始化SQL脚本")
    print("=" * 50)
    
    try:
        print(f"\n连接数据库: {DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}")
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("数据库连接成功！\n")
        
        print(f"执行SQL文件: {SQL_FILE}")
        execute_sql_file(cursor, SQL_FILE)
        
        conn.commit()
        print("\n" + "=" * 50)
        print("初始化完成！")
        print("=" * 50)
        
        cursor.close()
        conn.close()
        
    except pymysql.Error as e:
        print(f"\n数据库错误: {e}")
        print("\n请确保：")
        print("1. MySQL 服务已启动")
        print("2. 数据库 park_evaluation 已创建")
        print("3. 数据库密码正确（当前配置: root/123456）")
        sys.exit(1)
    except Exception as e:
        print(f"\n错误: {e}")
        sys.exit(1)

if __name__ == '__main__':
    main()
