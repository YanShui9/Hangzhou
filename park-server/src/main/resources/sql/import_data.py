# -*- coding: utf-8 -*-
"""
杭州市小微园区评价数据分析平台 - Excel数据导入脚本
功能：读取Excel文件，导入园区基础信息和用户数据到MySQL
"""

import pandas as pd
import pymysql
import hashlib
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

# Excel文件路径
PARK_INFO_FILE = r'D:\qq聊天记录\园区基本信息.xlsx'
PARK_USER_FILE = r'D:\qq聊天记录\园区与运营机构映射关系数据.xlsx'

# 区县编码映射
DISTRICT_CODE_MAP = {
    '上城区': '330102',
    '拱墅区': '330105',
    '西湖区': '330106',
    '滨江区': '330108',
    '萧山区': '330109',
    '余杭区': '330110',
    '富阳区': '330111',
    '临安区': '330112',
    '临平区': '330113',
    '钱塘区': '330114',
    '桐庐县': '330122',
    '淳安县': '330127',
    '建德市': '330182',
}


def get_district_id(cursor, district_name):
    """根据区县名称获取区县ID"""
    cursor.execute("SELECT id FROM district_info WHERE district_name = %s", (district_name,))
    result = cursor.fetchone()
    return result[0] if result else None


def safe_int(value, default=0):
    """安全转换为整数"""
    try:
        if pd.isna(value):
            return default
        return int(float(value))
    except:
        return default


def safe_decimal(value, default=None):
    """安全转换为小数"""
    try:
        if pd.isna(value):
            return default
        return float(value)
    except:
        return default


def safe_str(value, default=None):
    """安全转换为字符串"""
    try:
        if pd.isna(value):
            return default
        return str(value).strip()
    except:
        return default


def generate_password_hash(password):
    """生成BCrypt密码哈希（简化版，实际项目应使用bcrypt库）"""
    # 这里使用与现有数据一致的BCrypt哈希
    return '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'


def import_parks(cursor, conn):
    """导入园区基础信息"""
    print("正在读取园区基础信息Excel...")
    df = pd.read_excel(PARK_INFO_FILE, engine='openpyxl')
    print(f"读取到 {len(df)} 条园区记录")

    # 清空现有数据（可选）
    # cursor.execute("DELETE FROM park_info WHERE create_by = 'import'")

    success_count = 0
    error_count = 0

    for index, row in df.iterrows():
        try:
            district_name = safe_str(row.get('区县'))
            district_id = get_district_id(cursor, district_name) if district_name else None

            sql = """
            INSERT INTO park_info (
                park_code, park_name, park_type, district_id, district_name,
                address, star_level, performance, park_status,
                dev_mode, land_source, land_nature, recognition,
                is_upgrade, upgrade_content, main_industry, introduction,
                land_area, build_area, rent_remain_area, sale_remain_area, rented_area,
                public_facilities, public_services,
                operation_org_name, operation_org_code, operation_org_nature,
                org_leader, org_leader_phone, org_contact, org_contact_phone,
                enterprise_count, above_scale_count, high_tech_count, tech_sme_count,
                listed_count, hidden_champion_count, national_srti_count,
                provincial_srti_count, innovative_sme_count,
                employee_count, national_talent, provincial_talent, master_above,
                senior_engineer, engineer, senior_technician, master_degree,
                patent_total, patent_invention, patent_utility, patent_design,
                create_by, create_time
            ) VALUES (
                %s, %s, %s, %s, %s,
                %s, %s, %s, %s,
                %s, %s, %s, %s,
                %s, %s, %s, %s,
                %s, %s, %s, %s, %s,
                %s, %s,
                %s, %s, %s,
                %s, %s, %s, %s,
                %s, %s, %s, %s,
                %s, %s, %s,
                %s, %s,
                %s, %s, %s, %s,
                %s, %s, %s, %s,
                %s, %s, %s, %s,
                'import', NOW()
            )
            """

            values = (
                safe_str(row.get('代码')),
                safe_str(row.get('园区名称')),
                safe_str(row.get('园区类型')),
                district_id,
                district_name,
                safe_str(row.get('园区地址')),
                safe_int(row.get('园区星级')),
                safe_str(row.get('绩效评价')),
                safe_str(row.get('园区状态')),
                safe_str(row.get('开发模式')),
                safe_str(row.get('土地来源')),
                safe_str(row.get('土地性质')),
                safe_str(row.get('园区认定')),
                safe_str(row.get('是否改造提升')),
                safe_str(row.get('改造提升内容')),
                safe_str(row.get('主导产业')),
                safe_str(row.get('园区介绍')),
                safe_decimal(row.get('实际用地面积（亩）')),
                safe_decimal(row.get('已建建筑面积（平方米）')),
                safe_decimal(row.get('园区剩余可租面积（平方米）')),
                safe_decimal(row.get('园区剩余可售面积（平方米）')),
                safe_decimal(row.get('已出租面积（平方米）')),
                safe_str(row.get('公共配套设施')),
                safe_str(row.get('公共配套服务')),
                safe_str(row.get('运营机构名称')),
                safe_str(row.get('运营机构统一社会信用代码')),
                safe_str(row.get('运营机构性质')),
                safe_str(row.get('机构负责人')),
                safe_str(row.get('机构负责人手机')),
                safe_str(row.get('机构联系人')),
                safe_str(row.get('机构联系人手机')),
                safe_int(row.get('入驻企业总数（家）')),
                safe_int(row.get('规模以上企业（家）')),
                safe_int(row.get('高新技术企业（家）')),
                safe_int(row.get('科技中小企业（家）')),
                safe_int(row.get('上市企业（家）')),
                safe_int(row.get('隐形冠军企业（家）')),
                safe_int(row.get('国家级专精特新小巨人企业（家）')),
                safe_int(row.get('省专精特新中小企业（家）')),
                safe_int(row.get('创新型中小企业（家）')),
                safe_int(row.get('入驻企业员工人数(人)')),
                safe_int(row.get('国千人才(人)')),
                safe_int(row.get('"省千"人才(人)')),
                safe_int(row.get('硕士/副高以上(人)')),
                safe_int(row.get('正高级工程师人数(人)')),
                safe_int(row.get('高级工程师人数(人)')),
                safe_int(row.get('高级技师人数(人)')),
                safe_int(row.get('硕士以上人数(人)')),
                safe_int(row.get('专利拥有量(件)')),
                safe_int(row.get('发明专利(件)')),
                safe_int(row.get('实用新型专利(件)')),
                safe_int(row.get('外观设计专利(件)')),
            )

            cursor.execute(sql, values)
            success_count += 1

        except Exception as e:
            error_count += 1
            print(f"  导入园区失败 [{index+1}]: {safe_str(row.get('园区名称'))} - 错误: {e}")

    conn.commit()
    print(f"园区导入完成: 成功 {success_count} 条, 失败 {error_count} 条")
    return success_count


def import_users(cursor, conn):
    """导入园区运营机构联系人作为用户"""
    print("\n正在读取运营机构映射关系Excel...")
    df = pd.read_excel(PARK_USER_FILE, engine='openpyxl')
    print(f"读取到 {len(df)} 条映射记录")

    # 清空园区管理员用户（保留admin等系统用户）
    cursor.execute("DELETE FROM sys_user WHERE role_type = 3")

    # 获取园区ID映射
    cursor.execute("SELECT id, park_code FROM park_info")
    park_map = {}
    for row in cursor.fetchall():
        park_map[row[1]] = row[0]

    success_count = 0
    error_count = 0
    seen_contacts = set()  # 去重：同一联系人可能管理多个园区

    for index, row in df.iterrows():
        try:
            contact_name = safe_str(row.get('机构联系人'))
            contact_phone = safe_str(row.get('机构联系人手机'))
            park_code = safe_str(row.get('园区代码'))
            district_name = safe_str(row.get('区县'))

            if not contact_name:
                continue

            # 去重检查
            unique_key = f"{contact_name}_{contact_phone}"
            if unique_key in seen_contacts:
                continue
            seen_contacts.add(unique_key)

            # 获取区县ID
            district_id = get_district_id(cursor, district_name) if district_name else None

            # 获取园区ID
            park_id = park_map.get(park_code)

            # 生成用户名（手机号或姓名拼音）
            if contact_phone and len(contact_phone) >= 11:
                username = contact_phone
            else:
                username = f"park_{index+1:03d}"

            # 检查用户名是否已存在
            cursor.execute("SELECT COUNT(*) FROM sys_user WHERE username = %s", (username,))
            if cursor.fetchone()[0] > 0:
                username = f"park_{contact_name}_{index+1}"

            sql = """
            INSERT INTO sys_user (
                username, password, real_name, phone, role_type,
                district_id, park_id, status
            ) VALUES (
                %s, %s, %s, %s, %s,
                %s, %s, 1
            )
            """

            values = (
                username,
                generate_password_hash('123456'),  # 默认密码 123456
                contact_name,
                contact_phone,
                3,  # 园区管理员
                district_id,
                park_id,
            )

            cursor.execute(sql, values)
            success_count += 1

        except Exception as e:
            error_count += 1
            print(f"  导入用户失败 [{index+1}]: {safe_str(row.get('机构联系人'))} - 错误: {e}")

    conn.commit()
    print(f"用户导入完成: 成功 {success_count} 条, 失败 {error_count} 条")
    return success_count


def main():
    print("=" * 50)
    print("杭州市小微园区评价数据分析平台 - 数据导入工具")
    print("=" * 50)

    try:
        # 连接数据库
        print(f"\n连接数据库: {DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}")
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("数据库连接成功！\n")

        # 1. 导入园区数据
        park_count = import_parks(cursor, conn)

        # 2. 导入用户数据
        user_count = import_users(cursor, conn)

        # 3. 统计结果
        print("\n" + "=" * 50)
        print("导入完成！统计信息：")
        print(f"  - 园区数据: {park_count} 条")
        print(f"  - 用户数据: {user_count} 条")

        cursor.execute("SELECT COUNT(*) FROM district_info")
        print(f"  - 区县数据: {cursor.fetchone()[0]} 条")

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
