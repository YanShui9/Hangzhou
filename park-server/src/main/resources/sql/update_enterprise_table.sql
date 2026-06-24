-- ============================================================
-- 更新企业信息表数据 - 适配前端格式
-- ============================================================
USE park_evaluation;

-- 更新测试数据的状态为前端期望的格式
UPDATE enterprise_info SET status = '存续/在业', register_status = '存续/在业', honor = 'national_high', district_name = '西湖区', park_name = '西湖科技园' WHERE id = 1;
UPDATE enterprise_info SET status = '存续/在业', register_status = '存续/在业', honor = 'provincial_high', district_name = '西湖区', park_name = '西湖科技园' WHERE id = 2;
UPDATE enterprise_info SET status = '存续/在业', register_status = '存续/在业', honor = 'national_high,national_small_giant', district_name = '滨江区', park_name = '滨江高新技术园' WHERE id = 3;
UPDATE enterprise_info SET status = '存续/在业', register_status = '存续/在业', honor = 'innovation', district_name = '滨江区', park_name = '滨江高新技术园' WHERE id = 4;
UPDATE enterprise_info SET status = '存续/在业', register_status = '存续/在业', honor = 'hidden_champion', district_name = '余杭区', park_name = '余杭未来产业园' WHERE id = 5;

-- 添加更多测试数据
INSERT INTO enterprise_info (enterprise_name, credit_code, park_id, industry_code, industry_name, status, register_status, register_date, legal_person, registered_capital, contact_name, contact_phone, is_participate, district_name, park_name, honor, remark) VALUES
('杭州数字科技有限公司', '91330100MA67890123', 1, 'I65', '软件和信息技术服务业', '存续/在业', '存续/在业', '2019-07-15', '陈八', 300.00, '杨经理', '13900000006', 1, '西湖区', '西湖科技园', 'innovation', '专注于数字营销'),
('杭州智能设备有限公司', '91330100MA78901234', 2, 'C39', '计算机、通信和其他电子设备制造业', '停业', '停业', '2018-09-20', '周九', 1000.00, '吴经理', '13900000007', 1, '滨江区', '滨江高新技术园', 'provincial_small_giant', '智能硬件研发'),
('杭州新材料科技有限公司', '91330100MA89012345', 3, 'C30', '非金属矿物制品业', '存续/在业', '存续/在业', '2020-11-10', '郑十', 600.00, '王经理', '13900000008', 1, '余杭区', '余杭未来产业园', 'single_champion', '新材料研发'),
('杭州生物医药科技有限公司', '91330100MA90123456', 2, 'C27', '医药制造业', '存续/在业', '存续/在业', '2021-03-25', '冯十一', 1500.00, '赵经理', '13900000009', 1, '滨江区', '滨江高新技术园', 'national_high', '生物医药研发'),
('杭州环保科技有限公司', '91330100MA01234567', 1, 'N77', '生态保护和环境治理业', '注销', '注销', '2017-04-18', '钱十二', 200.00, '孙经理', '13900000010', 0, '西湖区', '西湖科技园', '', '已注销');
