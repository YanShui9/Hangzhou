import pymysql
import random
from datetime import datetime

# 连接数据库
conn = pymysql.connect(host='localhost', port=3306, user='root', password='123456', database='park_evaluation', charset='utf8mb4')
cursor = conn.cursor()

# ==================== 1. 获取西湖区园区 ====================
print("获取西湖区园区列表...")
cursor.execute("SELECT id, park_name FROM park_info WHERE district_id = (SELECT id FROM district_info WHERE district_name = '西湖区')")
xihu_parks = cursor.fetchall()
print(f"西湖区园区: {xihu_parks}")

# ==================== 2. 获取用户列表 ====================
cursor.execute("SELECT id FROM sys_user")
user_ids = [row[0] for row in cursor.fetchall()]

# ==================== 3. 生成西湖区园区评价记录 ====================
print("\n生成西湖区园区评价记录...")
status_options = [0, 1, 2, 3, 4]  # 0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回
performance_grades = ['A', 'B', 'C', 'D']

for park_id, park_name in xihu_parks:
    # 为每个西湖区园区生成2023-2025年的评价记录
    for year in [2023, 2024, 2025]:
        # 检查是否已存在
        cursor.execute("SELECT COUNT(*) FROM evaluation_record WHERE park_id = %s AND year = %s", (park_id, year))
        if cursor.fetchone()[0] > 0:
            continue
        
        status = random.choice(status_options)
        total_score = round(random.uniform(75, 98), 2) if status != 0 else None
        grade = performance_grades[min(int(total_score // 25), 3)] if total_score else None
        
        cursor.execute("""
            INSERT INTO evaluation_record (park_id, year, status, total_score, grade, eval_status)
            VALUES (%s, %s, %s, %s, %s, %s)
        """, (park_id, year, status, total_score, grade, 1))
        
        print(f"  [{park_name}] {year}年 - 状态:{status} 总分:{total_score} 等级:{grade}")

# ==================== 4. 生成西湖区入驻企业数据 ====================
print("\n生成西湖区入驻企业数据...")

# 真实企业名称
enterprise_names = [
    "杭州阿里云智能科技有限公司",
    "杭州蚂蚁科技集团股份有限公司",
    "杭州网易雷火科技有限公司",
    "杭州华为技术有限公司",
    "杭州海康威视数字技术股份有限公司",
    "杭州大华技术股份有限公司",
    "杭州恒生电子股份有限公司",
    "杭州信雅达系统工程股份有限公司",
    "杭州同花顺数据开发有限公司",
    "杭州东方通信股份有限公司",
    "杭州华三通信技术有限公司",
    "杭州东信网络技术有限公司",
    "杭州中恒电气股份有限公司",
    "杭州炬华科技股份有限公司",
    "杭州银江技术股份有限公司",
    "杭州创业软件股份有限公司",
    "杭州和仁科技股份有限公司",
    "杭州泰格医药科技股份有限公司",
    "杭州迪安诊断技术股份有限公司",
    "杭州贝瑞基因科技有限公司"
]

# 真实信用代码格式
def generate_credit_code():
    return "9133010" + random.choice(["5", "6", "7", "8"]) + "MA" + ''.join(random.choices('0123456789ABCDEFGHJKLMNPQRTUWXY', k=8))

legal_persons = ["马云", "马化腾", "丁磊", "任正非", "陈宗年", "傅利泉", "彭政纲", "郭华强", "易峥", "赵波"]
contact_persons = ["张经理", "李主管", "王主任", "刘总", "陈经理", "周总监", "吴部长", "郑助理"]
honor_options = ["高新技术企业", "省级研发中心", "科技型中小企业", "瞪羚企业", "独角兽企业", ""]

for park_id, park_name in xihu_parks:
    # 获取西湖区ID
    cursor.execute("SELECT district_id FROM park_info WHERE id = %s", (park_id,))
    district_id = cursor.fetchone()[0]
    
    # 每个园区增加15家企业（最多20家）
    cursor.execute("SELECT COUNT(*) FROM enterprise_info WHERE park_id = %s", (park_id,))
    existing_count = cursor.fetchone()[0]
    need_add = min(15, 20 - existing_count)
    
    for i in range(need_add):
        idx = (existing_count + i) % len(enterprise_names)
        enterprise_name = enterprise_names[idx]
        if need_add > 1 and i > 0:
            enterprise_name = enterprise_name.replace("有限公司", f"杭州分公司")
        
        credit_code = generate_credit_code()
        
        cursor.execute("""
            INSERT INTO enterprise_info 
            (enterprise_name, credit_code, district_id, park_id, legal_person, contact_person, contact_phone, honor)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        """, (
            enterprise_name,
            credit_code,
            district_id,
            park_id,
            legal_persons[idx % len(legal_persons)],
            random.choice(contact_persons),
            "139" + str(random.randint(10000000, 99999999)),
            random.choice(honor_options)
        ))
        
        print(f"  [{park_name}] - {enterprise_name}")

# ==================== 5. 生成西湖区评价审核记录 ====================
print("\n生成西湖区评价审核记录...")

# 获取需要审核的评价记录
cursor.execute("SELECT id, park_id FROM evaluation_record WHERE status IN (1, 2) AND park_id IN (SELECT id FROM park_info WHERE district_id = (SELECT id FROM district_info WHERE district_name = '西湖区'))")
pending_records = cursor.fetchall()

for record_id, park_id in pending_records:
    # 生成三级审核记录
    for level in [1, 2, 3]:
        cursor.execute("SELECT COUNT(*) FROM evaluation_review WHERE material_id = %s AND review_level = %s", (record_id, level))
        if cursor.fetchone()[0] > 0:
            continue
        
        # 审核状态
        if level == 1:
            review_status = random.choice(["PENDING", "APPROVED"])
        elif level == 2:
            review_status = random.choice(["PENDING", "APPROVED"]) if random.random() > 0.3 else "REJECTED"
        else:
            review_status = random.choice(["APPROVED", "REJECTED"])
        
        cursor.execute("""
            INSERT INTO evaluation_review 
            (material_id, park_id, review_level, reviewer_id, review_status, review_comment, review_time)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """, (
            record_id,
            park_id,
            level,
            random.choice(user_ids),
            review_status,
            "审核通过" if review_status == "APPROVED" else "审核驳回" if review_status == "REJECTED" else "待审核",
            datetime.now()
        ))
        
        print(f"  评价记录{record_id} - 第{level}级审核: {review_status}")

# ==================== 6. 提交事务 ====================
conn.commit()

# ==================== 7. 统计数据 ====================
print("\n=== 西湖区数据统计 ===")

cursor.execute("SELECT COUNT(*) FROM evaluation_record WHERE park_id IN (SELECT id FROM park_info WHERE district_id = (SELECT id FROM district_info WHERE district_name = '西湖区'))")
print(f"评价记录总数: {cursor.fetchone()[0]}")

cursor.execute("SELECT COUNT(*) FROM enterprise_info WHERE park_id IN (SELECT id FROM park_info WHERE district_id = (SELECT id FROM district_info WHERE district_name = '西湖区'))")
print(f"入驻企业总数: {cursor.fetchone()[0]}")

cursor.execute("SELECT COUNT(*) FROM evaluation_review WHERE park_id IN (SELECT id FROM park_info WHERE district_id = (SELECT id FROM district_info WHERE district_name = '西湖区'))")
print(f"审核记录总数: {cursor.fetchone()[0]}")

cursor.execute("SELECT park_name, COUNT(*) as cnt FROM enterprise_info e JOIN park_info p ON e.park_id = p.id WHERE p.district_id = (SELECT id FROM district_info WHERE district_name = '西湖区') GROUP BY park_name")
print("\n各园区企业分布:")
for row in cursor.fetchall():
    print(f"  {row[0]}: {row[1]}家企业")

conn.close()
print("\n西湖区数据生成完成！")