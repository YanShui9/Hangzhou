import pymysql
from datetime import datetime, timedelta

conn = pymysql.connect(host='localhost', port=3306, user='root', password='123456', database='park_evaluation', charset='utf8mb4')
cursor = conn.cursor()

# 西湖区园区名称列表
xihu_parks = [
    {'name': '西湖科创园', 'address': '杭州市西湖区文三路478号'},
    {'name': '西溪智慧谷', 'address': '杭州市西湖区文一西路857号'},
    {'name': '之江文化中心', 'address': '杭州市西湖区之江路158号'},
    {'name': '紫金港科技城', 'address': '杭州市西湖区古墩路888号'},
    {'name': '云栖小镇', 'address': '杭州市西湖区科海路188号'},
    {'name': '梦想小镇', 'address': '杭州市西湖区文二西路1号'},
    {'name': '西湖数字产业园', 'address': '杭州市西湖区文三路121号'},
    {'name': '转塘科技园区', 'address': '杭州市西湖区转塘街道'},
]

# 更新现有的西湖区园区名称
print("=== 更新西湖区园区名称 ===")
cursor.execute("UPDATE park_info SET park_name = '西湖科创园', address = '杭州市西湖区文三路478号' WHERE id = 13 AND district_id = 6")
conn.commit()
print("已更新园区13的名称")

# 为西湖区添加更多园区
print("\n=== 添加西湖区园区 ===")
for park in xihu_parks[1:]:  # 跳过第一个，已经存在
    sql = """
        INSERT INTO park_info (park_name, district_id, district_name, address, park_type, park_status, create_time)
        VALUES (%s, 6, '西湖区', %s, 1, '正常', NOW())
    """
    cursor.execute(sql, (park['name'], park['address']))

conn.commit()
print(f"成功添加 {len(xihu_parks) - 1} 个新园区")

# 查询西湖区园区
print("\n=== 西湖区园区列表 ===")
cursor.execute("SELECT id, park_name FROM park_info WHERE district_id = 6 ORDER BY id")
parks = cursor.fetchall()
for park in parks:
    print(f"ID: {park[0]}, 名称: {park[1]}")

# 为每个西湖区园区生成评价记录
print("\n=== 生成西湖区评价记录 ===")
base_time = datetime(2026, 6, 1, 9, 0, 0)
eval_count = 0

for park_idx, (park_id, park_name) in enumerate(parks):
    for year in [2026]:
        sql = """
            INSERT INTO evaluation_record (park_id, year, status, create_time)
            VALUES (%s, %s, 3, %s)
        """
        create_time = base_time + timedelta(days=park_idx*5)
        cursor.execute(sql, (park_id, year, create_time))
        eval_count += 1

conn.commit()
print(f"成功生成 {eval_count} 条评价记录")

# 审核状态变更流程
status_flow = [
    {'from_status': '0', 'to_status': '1', 'action': 0, 'opinion': '园区提交参评申请', 'auditor_name': '系统', 'auditor_role': 0},
    {'from_status': '1', 'to_status': '3', 'action': 1, 'opinion': '材料齐全，数据真实，予以通过', 'auditor_name': '李区管', 'auditor_role': 2},
    {'from_status': '3', 'to_status': '5', 'action': 0, 'opinion': '已上报至市局', 'auditor_name': '系统', 'auditor_role': 0},
    {'from_status': '5', 'to_status': '6', 'action': 1, 'opinion': '市级审核通过，同意发布', 'auditor_name': '市局审核', 'auditor_role': 1},
    {'from_status': '6', 'to_status': '8', 'action': 0, 'opinion': '评价结果已发布', 'auditor_name': '系统', 'auditor_role': 0},
]

# 驳回流程
reject_flow = [
    {'from_status': '0', 'to_status': '1', 'action': 0, 'opinion': '园区提交参评申请', 'auditor_name': '系统', 'auditor_role': 0},
    {'from_status': '1', 'to_status': '4', 'action': 2, 'opinion': '部分材料缺失，请补充后重新提交', 'auditor_name': '王区管', 'auditor_role': 2},
    {'from_status': '4', 'to_status': '1', 'action': 0, 'opinion': '园区补充材料后重新提交', 'auditor_name': '系统', 'auditor_role': 0},
    {'from_status': '1', 'to_status': '3', 'action': 1, 'opinion': '材料已齐全，同意通过', 'auditor_name': '王区管', 'auditor_role': 2},
]

# 删除旧的审核记录并重新生成
print("\n=== 生成审核记录 ===")
cursor.execute("DELETE FROM audit_record")

# 查询西湖区评价记录
cursor.execute("""
    SELECT er.id, pi.park_name 
    FROM evaluation_record er 
    JOIN park_info pi ON er.park_id = pi.id 
    WHERE pi.district_id = 6 
    ORDER BY er.id
""")
evaluations = cursor.fetchall()

record_count = 0
base_time = datetime(2026, 6, 10, 9, 0, 0)

for eval_idx, (eval_id, park_name) in enumerate(evaluations):
    if eval_idx % 3 == 0:
        flow = reject_flow
    else:
        flow = status_flow
    
    for step_idx, step in enumerate(flow):
        create_time = base_time + timedelta(days=eval_idx*2, hours=step_idx*3)
        cursor.execute("UPDATE evaluation_record SET status = %s WHERE id = %s", (step['to_status'], eval_id))
        
        sql = """
            INSERT INTO audit_record (evaluation_id, auditor_id, auditor_name, auditor_role, action, opinion, create_time)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        auditor_id = eval_idx + 100
        cursor.execute(sql, (
            eval_id, auditor_id, step['auditor_name'], step['auditor_role'],
            step['action'], step['opinion'], create_time
        ))
        record_count += 1

conn.commit()
print(f"成功生成 {record_count} 条审核记录")

# 验证结果
print("\n=== 验证结果 ===")
cursor.execute("SELECT COUNT(*) FROM park_info WHERE district_id = 6")
print(f"西湖区园区数: {cursor.fetchone()[0]}")

cursor.execute("SELECT COUNT(*) FROM evaluation_record er JOIN park_info pi ON er.park_id = pi.id WHERE pi.district_id = 6")
print(f"西湖区评价记录数: {cursor.fetchone()[0]}")

cursor.execute("SELECT COUNT(*) FROM audit_record")
print(f"审核记录总数: {cursor.fetchone()[0]}")

conn.close()
print("\n数据修复完成！")