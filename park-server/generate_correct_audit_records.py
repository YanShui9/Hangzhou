import pymysql
from datetime import datetime, timedelta

conn = pymysql.connect(host='localhost', port=3306, user='root', password='123456', database='park_evaluation', charset='utf8mb4')
cursor = conn.cursor()

# 查询西湖区的评价记录 (district_id=6对应西湖区)
print("=== 查询西湖区评价记录 ===")
cursor.execute("""
    SELECT er.id, pi.park_name 
    FROM evaluation_record er 
    JOIN park_info pi ON er.park_id = pi.id 
    WHERE pi.district_id = 6 
    ORDER BY er.id
""")
xihu_evaluations = cursor.fetchall()
print(f"西湖区评价记录数: {len(xihu_evaluations)}")
for row in xihu_evaluations:
    print(f"ID: {row[0]}, 园区: {row[1]}")

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

# 删除旧的关联错误的审核记录
print("\n=== 清理旧的审核记录 ===")
cursor.execute("DELETE FROM audit_record")
conn.commit()
print("已清空旧的审核记录")

# 为每条西湖区评价记录生成审核历史
print("\n=== 生成新的审核记录 ===")
base_time = datetime(2026, 6, 15, 9, 0, 0)
record_count = 0

for eval_idx, (eval_id, park_name) in enumerate(xihu_evaluations):
    # 交替使用不同的流程
    if eval_idx % 3 == 0:
        # 使用驳回流程
        flow = reject_flow
    else:
        # 使用正常流程
        flow = status_flow
    
    for step_idx, step in enumerate(flow):
        create_time = base_time + timedelta(days=eval_idx*2, hours=step_idx*3)
        
        # 更新evaluation_record的状态
        cursor.execute("UPDATE evaluation_record SET status = %s WHERE id = %s", (step['to_status'], eval_id))
        
        # 插入审核记录
        sql = """
            INSERT INTO audit_record (evaluation_id, auditor_id, auditor_name, auditor_role, action, opinion, create_time)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        auditor_id = eval_idx + 100  # 生成唯一的审核员ID
        cursor.execute(sql, (
            eval_id,
            auditor_id,
            step['auditor_name'],
            step['auditor_role'],
            step['action'],
            step['opinion'],
            create_time
        ))
        record_count += 1

conn.commit()
print(f"成功生成 {record_count} 条审核记录")

# 验证结果
print("\n=== 验证结果 ===")
cursor.execute("SELECT COUNT(*) FROM audit_record")
print(f"audit_record表记录数: {cursor.fetchone()[0]}")

cursor.execute("SELECT er.id, er.status, pi.park_name FROM evaluation_record er JOIN park_info pi ON er.park_id = pi.id WHERE pi.district_id = 6 LIMIT 5")
print("\n西湖区评价记录状态:")
for row in cursor.fetchall():
    print(f"ID: {row[0]}, 状态: {row[1]}, 园区: {row[2]}")

conn.close()
print("\n数据生成完成！")