-- ============================================================
-- 修复园区类型数据
-- 将数字类型（1/2）转换为字符串类型（生产性制造类/生产性服务类）
-- 执行日期：2026-06-23
-- ============================================================

USE park_evaluation;

-- 更新园区类型为字符串值
UPDATE park_info SET park_type = '生产性制造类' WHERE park_type = '1';
UPDATE park_info SET park_type = '生产性服务类' WHERE park_type = '2';

-- 验证更新结果
SELECT id, park_name, park_type FROM park_info;
