import pymysql

conn = pymysql.connect(host='localhost', port=3306, user='root', password='123456', database='park_evaluation', charset='utf8mb4')
cursor = conn.cursor()

# 查询所有评价记录和对应的审核记录
cursor.execute("SELECT id, status FROM evaluation_record ORDER BY id")
eval_records = {row[0]: row[1] for row in cursor.fetchall()}

# 审核记录的状态变更流程（按action和最终状态推断）
# action: 0=系统操作, 1=通过, 2=驳回

# 为每条评价记录的审核记录填充状态变更
for eval_id, final_status in eval_records.items():
    # 查询该评价记录的所有审核记录（按时间排序）
    cursor.execute("SELECT id, action FROM audit_record WHERE evaluation_id = %s ORDER BY create_time", (eval_id,))
    audit_records = cursor.fetchall()
    
    if not audit_records:
        continue
    
    # 根据action推断状态变更
    # 状态流转: 0(未提交) -> 1(区县待审核) -> 3(区县审核通过)/4(区县审核驳回) -> 5(已上报) -> 6(市级审核通过)/7(市级审核驳回) -> 8(已发布)
    current_status = '0'  # 初始状态为未提交
    status_changes = []
    
    for audit_id, action in audit_records:
        if action == 0:  # 系统操作：提交/上报/发布
            if current_status == '0':
                next_status = '1'  # 提交申请
            elif current_status == '3':
                next_status = '5'  # 上报市局
            elif current_status == '6':
                next_status = '8'  # 发布结果
            elif current_status == '4':
                next_status = '1'  # 驳回后重新提交
            else:
                next_status = str(int(current_status) + 1)
        elif action == 1:  # 通过
            if current_status == '1':
                next_status = '3'  # 区县审核通过
            elif current_status == '5':
                next_status = '6'  # 市级审核通过
            else:
                next_status = str(int(current_status) + 2)
        elif action == 2:  # 驳回
            if current_status == '1':
                next_status = '4'  # 区县审核驳回
            elif current_status == '5':
                next_status = '7'  # 市级审核驳回
            else:
                next_status = str(int(current_status) + 3)
        else:
            next_status = str(int(current_status) + 1)
        
        status_changes.append((audit_id, current_status, next_status))
        current_status = next_status
    
    # 更新数据库中的状态变更字段
    for audit_id, from_status, to_status in status_changes:
        cursor.execute(
            "UPDATE audit_record SET from_status = %s, to_status = %s WHERE id = %s",
            (from_status, to_status, audit_id)
        )

conn.commit()
print("审核记录状态变更字段更新完成！")

# 验证结果
cursor.execute("SELECT id, evaluation_id, from_status, to_status FROM audit_record LIMIT 5")
print("\n验证结果（前5条）:")
for row in cursor.fetchall():
    print(f"ID: {row[0]}, evaluation_id: {row[1]}, from_status: {row[2]}, to_status: {row[3]}")

conn.close()