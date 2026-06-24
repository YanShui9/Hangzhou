import pymysql
import random
from datetime import datetime

conn = pymysql.connect(host='localhost', port=3306, user='root', password='123456', database='park_evaluation', charset='utf8mb4')
cursor = conn.cursor()

# 获取园区列表
cursor.execute('SELECT id, park_name FROM park_info')
parks = cursor.fetchall()

# 状态映射: 0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回
status_options = [0, 1, 2, 3, 4]
performance_grades = ['A', 'B', 'C', 'D']

# 生成评价记录数据
for park_id, park_name in parks:
    for year in [2024, 2025]:
        # 检查是否已存在该园区该年份的评价记录
        cursor.execute('SELECT COUNT(*) FROM evaluation_record WHERE park_id = %s AND year = %s', (park_id, year))
        if cursor.fetchone()[0] > 0:
            continue
        
        status = random.choice(status_options)
        total_score = round(random.uniform(60, 98), 2) if status != 0 else None
        grade = performance_grades[min(int(total_score // 25), 3)] if total_score else None
        
        cursor.execute('''
            INSERT INTO evaluation_record 
            (park_id, year, status, total_score, grade, eval_status)
            VALUES (%s, %s, %s, %s, %s, %s)
        ''', (
            park_id, year, status, total_score, grade, 1
        ))
        
        print(f'已为 [{park_name}] {year}年 创建评价记录，状态: {status}')

conn.commit()

# 验证数据
cursor.execute('SELECT COUNT(*) FROM evaluation_record')
print(f'\\nevaluation_record表总记录数: {cursor.fetchone()[0]}')

conn.close()