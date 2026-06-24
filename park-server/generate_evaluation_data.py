import pymysql
import random
from datetime import datetime

conn = pymysql.connect(host='localhost', port=3306, user='root', password='123456', database='park_evaluation', charset='utf8mb4')
cursor = conn.cursor()

# 获取园区列表
cursor.execute('SELECT id, park_name FROM park_info')
parks = cursor.fetchall()

# 获取用户列表（用于提交人和审核人）
cursor.execute('SELECT id FROM sys_user')
user_ids = [row[0] for row in cursor.fetchall()]

# 评价状态
status_options = ['DRAFT', 'SUBMITTED', 'REVIEWING', 'APPROVED', 'REJECTED']
review_status_options = ['PENDING', 'APPROVED', 'REJECTED']
performance_grades = ['A', 'B', 'C', 'D']

# 生成评价数据
for park_id, park_name in parks:
    for year in [2024, 2025]:
        # 1. 生成评价材料
        total_score = round(random.uniform(60, 98), 2)
        material_status = random.choice(status_options)
        
        cursor.execute('''
            INSERT INTO evaluation_material 
            (park_id, evaluation_year, submit_user_id, material_status,
             business_revenue_per_mu, tax_per_mu, land_area, participant_enterprise_count,
             enterprise_development_score, enterprise_cultivate_score, tech_innovation_score,
             service_level_score, benefit_output_score, safety_production_score, other_score,
             total_score, performance_grade, submit_time)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        ''', (
            park_id, year, random.choice(user_ids), material_status,
            round(random.uniform(100, 5000), 2),
            round(random.uniform(10, 500), 2),
            round(random.uniform(10, 500), 2),
            random.randint(10, 200),
            round(random.uniform(0, 100), 2),
            round(random.uniform(0, 100), 2),
            round(random.uniform(0, 100), 2),
            round(random.uniform(0, 100), 2),
            round(random.uniform(0, 100), 2),
            round(random.uniform(0, 100), 2),
            round(random.uniform(0, 100), 2),
            total_score,
            performance_grades[min(int(total_score // 25), 3)],
            datetime.now() if material_status != 'DRAFT' else None
        ))
        material_id = cursor.lastrowid
        
        # 2. 生成评价审核记录（如果材料已提交）
        if material_status != 'DRAFT':
            for level in [1, 2, 3]:
                cursor.execute('''
                    INSERT INTO evaluation_review 
                    (material_id, park_id, review_level, reviewer_id, review_status, review_comment, review_time)
                    VALUES (%s, %s, %s, %s, %s, %s, %s)
                ''', (
                    material_id, park_id, level, random.choice(user_ids),
                    random.choice(review_status_options),
                    '审核通过' if level == 3 else '审核中',
                    datetime.now()
                ))
        
        # 3. 生成评价结果（如果审核已通过）
        if material_status == 'APPROVED':
            cursor.execute('''
                INSERT INTO evaluation_result 
                (park_id, evaluation_year, business_revenue_per_mu, tax_per_mu, land_area,
                 participant_enterprise_count, enterprise_development_score, enterprise_cultivate_score,
                 tech_innovation_score, service_level_score, benefit_output_score, safety_production_score,
                 other_score, total_score, performance_grade, final_approve_time)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ''', (
                park_id, year,
                round(random.uniform(100, 5000), 2),
                round(random.uniform(10, 500), 2),
                round(random.uniform(10, 500), 2),
                random.randint(10, 200),
                round(random.uniform(0, 100), 2),
                round(random.uniform(0, 100), 2),
                round(random.uniform(0, 100), 2),
                round(random.uniform(0, 100), 2),
                round(random.uniform(0, 100), 2),
                round(random.uniform(0, 100), 2),
                round(random.uniform(0, 100), 2),
                total_score,
                performance_grades[min(int(total_score // 25), 3)],
                datetime.now()
            ))
        
        print(f'已为 [{park_name}] {year}年 创建评价数据')

conn.commit()

# 验证数据
cursor.execute('SELECT COUNT(*) FROM evaluation_material')
print(f'\\n评价材料总数: {cursor.fetchone()[0]}')
cursor.execute('SELECT COUNT(*) FROM evaluation_review')
print(f'审核记录总数: {cursor.fetchone()[0]}')
cursor.execute('SELECT COUNT(*) FROM evaluation_result')
print(f'评价结果总数: {cursor.fetchone()[0]}')

conn.close()