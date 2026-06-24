-- ============================================================
-- 为 enterprise_info 表添加入驻开始时间和入驻截止时间字段
-- ============================================================
USE park_evaluation;

-- 添加入驻开始时间字段
ALTER TABLE enterprise_info ADD COLUMN entry_start_time DATE DEFAULT NULL COMMENT '入驻开始时间';

-- 添加入驻截止时间字段
ALTER TABLE enterprise_info ADD COLUMN entry_end_time DATE DEFAULT NULL COMMENT '入驻截止时间';

-- 更新现有企业的入驻时间数据
-- 根据注册日期推算入驻时间，入驻开始时间通常在注册日期之后或相近，入驻期限为3-5年
UPDATE enterprise_info SET entry_start_time = '2020-01-15', entry_end_time = '2025-01-14' WHERE id = 1;
UPDATE enterprise_info SET entry_start_time = '2019-06-01', entry_end_time = '2024-05-31' WHERE id = 2;
UPDATE enterprise_info SET entry_start_time = '2021-03-10', entry_end_time = '2026-03-09' WHERE id = 3;
UPDATE enterprise_info SET entry_start_time = '2020-09-20', entry_end_time = '2025-09-19' WHERE id = 4;
UPDATE enterprise_info SET entry_start_time = '2018-11-01', entry_end_time = '2023-10-31' WHERE id = 5;
UPDATE enterprise_info SET entry_start_time = '2020-07-15', entry_end_time = '2025-07-14' WHERE id = 6;
UPDATE enterprise_info SET entry_start_time = '2019-09-20', entry_end_time = '2024-09-19' WHERE id = 7;
UPDATE enterprise_info SET entry_start_time = '2021-11-10', entry_end_time = '2026-11-09' WHERE id = 8;
UPDATE enterprise_info SET entry_start_time = '2022-03-25', entry_end_time = '2027-03-24' WHERE id = 9;
UPDATE enterprise_info SET entry_start_time = '2017-04-18', entry_end_time = '2022-04-17' WHERE id = 10;
UPDATE enterprise_info SET entry_start_time = '2020-01-10', entry_end_time = '2025-01-09' WHERE id = 11;
UPDATE enterprise_info SET entry_start_time = '2019-05-20', entry_end_time = '2024-05-19' WHERE id = 12;
UPDATE enterprise_info SET entry_start_time = '2021-02-15', entry_end_time = '2026-02-14' WHERE id = 13;
UPDATE enterprise_info SET entry_start_time = '2020-08-01', entry_end_time = '2025-07-31' WHERE id = 14;
UPDATE enterprise_info SET entry_start_time = '2019-12-10', entry_end_time = '2024-12-09' WHERE id = 15;
UPDATE enterprise_info SET entry_start_time = '2021-06-15', entry_end_time = '2026-06-14' WHERE id = 16;
UPDATE enterprise_info SET entry_start_time = '2020-04-20', entry_end_time = '2025-04-19' WHERE id = 17;
UPDATE enterprise_info SET entry_start_time = '2019-08-01', entry_end_time = '2024-07-31' WHERE id = 18;
UPDATE enterprise_info SET entry_start_time = '2022-01-15', entry_end_time = '2027-01-14' WHERE id = 19;
UPDATE enterprise_info SET entry_start_time = '2021-05-10', entry_end_time = '2026-05-09' WHERE id = 20;
UPDATE enterprise_info SET entry_start_time = '2020-10-01', entry_end_time = '2025-09-30' WHERE id = 21;
UPDATE enterprise_info SET entry_start_time = '2019-03-15', entry_end_time = '2024-03-14' WHERE id = 22;
UPDATE enterprise_info SET entry_start_time = '2021-07-20', entry_end_time = '2026-07-19' WHERE id = 23;
UPDATE enterprise_info SET entry_start_time = '2020-02-01', entry_end_time = '2025-01-31' WHERE id = 24;
UPDATE enterprise_info SET entry_start_time = '2019-01-10', entry_end_time = '2024-01-09' WHERE id = 25;
UPDATE enterprise_info SET entry_start_time = '2022-06-15', entry_end_time = '2027-06-14' WHERE id = 26;
UPDATE enterprise_info SET entry_start_time = '2021-09-01', entry_end_time = '2026-08-31' WHERE id = 27;
UPDATE enterprise_info SET entry_start_time = '2020-05-15', entry_end_time = '2025-05-14' WHERE id = 28;
UPDATE enterprise_info SET entry_start_time = '2019-10-20', entry_end_time = '2024-10-19' WHERE id = 29;
UPDATE enterprise_info SET entry_start_time = '2022-04-01', entry_end_time = '2027-03-31' WHERE id = 30;
UPDATE enterprise_info SET entry_start_time = '2021-01-10', entry_end_time = '2026-01-09' WHERE id = 31;
UPDATE enterprise_info SET entry_start_time = '2020-03-20', entry_end_time = '2025-03-19' WHERE id = 32;
UPDATE enterprise_info SET entry_start_time = '2019-07-01', entry_end_time = '2024-06-30' WHERE id = 33;

SELECT id, enterprise_name, entry_start_time, entry_end_time FROM enterprise_info ORDER BY id;
