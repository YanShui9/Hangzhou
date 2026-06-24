ALTER TABLE `park_info`
ADD COLUMN `park_images` TEXT DEFAULT NULL COMMENT '园区图片（JSON数组格式，最多6张）';
