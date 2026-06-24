import pymysql

# 数据库连接配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'park_evaluation',
    'charset': 'utf8mb4'
}

def add_manufacture_park():
    try:
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()
        
        # 插入西湖区制造类园区
        park_data = {
            'park_name': '西湖智能制造产业园',
            'park_type': 2,  # 制造类
            'district_id': 3,  # 西湖区ID
            'district_name': '西湖区',
            'address': '西湖区文三路xxx号',
            'star_level': 3,
            'performance': 85,
            'park_status': 1,
            'dev_mode': '政府主导',
            'land_source': '出让',
            'land_nature': '工业用地',
            'recognition': '市级园区',
            'is_upgrade': 0,
            'main_industry': '智能制造、高端装备',
            'introduction': '西湖智能制造产业园是西湖区重点打造的制造业集聚园区',
            'land_area': 50000.0,
            'build_area': 80000.0,
            'rent_remain_area': 20000.0,
            'sale_remain_area': 0.0,
            'rented_area': 60000.0,
            'public_facilities': '会议室、展厅、停车场',
            'public_services': '政策咨询、融资服务、人才招聘',
            'operation_org_name': '西湖区产业发展有限公司',
            'operation_org_code': '91330106xxx',
            'operation_org_nature': '国有企业',
            'org_leader': '张经理',
            'org_leader_phone': '138xxxx1234',
            'org_contact': '李主任',
            'org_contact_phone': '139xxxx5678',
            'enterprise_count': 50,
            'above_scale_count': 15,
            'high_tech_count': 20,
            'tech_sme_count': 30,
            'listed_count': 2,
            'hidden_champion_count': 3,
            'national_srti_count': 5,
            'provincial_srti_count': 8,
            'innovative_sme_count': 25,
            'employee_count': 2000,
            'national_talent': 10,
            'provincial_talent': 20,
            'master_above': 50,
            'senior_engineer': 30,
            'engineer': 100,
            'senior_technician': 20,
            'master_degree': 80,
            'patent_total': 200,
            'patent_invention': 50,
            'patent_utility': 120,
            'patent_design': 30
        }
        
        # 构建插入SQL
        columns = ', '.join(park_data.keys())
        placeholders = ', '.join(['%s'] * len(park_data))
        sql = f"INSERT INTO park_info ({columns}) VALUES ({placeholders})"
        
        cursor.execute(sql, tuple(park_data.values()))
        park_id = cursor.lastrowid
        conn.commit()
        print(f"成功添加西湖区制造类园区: {park_data['park_name']} (ID: {park_id})")
        
        # 添加评价记录
        eval_sql = """
            INSERT INTO evaluation_record (park_id, year, status, eval_status)
            VALUES (%s, 2023, 3, 1),  -- 审核通过
                   (%s, 2024, 3, 1),  -- 审核通过
                   (%s, 2025, 1, 1),  -- 待审核
                   (%s, 2026, 2, 1)   -- 待市局审核
        """
        cursor.execute(eval_sql, (park_id, park_id, park_id, park_id))
        conn.commit()
        print(f"成功添加4条评价记录")
        
    except Exception as e:
        print(f"添加园区失败: {e}")
        if 'conn' in locals():
            conn.rollback()
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    add_manufacture_park()
