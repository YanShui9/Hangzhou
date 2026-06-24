ALTER TABLE `park_operation_quarter`
ADD COLUMN `above_scale_count` INT DEFAULT 0 COMMENT '规模以上企业（家）',
ADD COLUMN `high_tech_count` INT DEFAULT 0 COMMENT '高新技术企业（家）',
ADD COLUMN `tech_sme_count` INT DEFAULT 0 COMMENT '科技型中小企业（家）',
ADD COLUMN `hidden_champion_count` INT DEFAULT 0 COMMENT '隐形冠军及培育企业（家）',
ADD COLUMN `national_srti_count` INT DEFAULT 0 COMMENT '国家级专精特新小巨人企业（家）',
ADD COLUMN `innovative_sme_count` INT DEFAULT 0 COMMENT '创新型中小企业（家）',
ADD COLUMN `provincial_srti_count` INT DEFAULT 0 COMMENT '省专精特新中小企业（家）';
