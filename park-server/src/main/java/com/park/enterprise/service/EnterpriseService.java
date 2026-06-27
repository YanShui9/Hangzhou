package com.park.enterprise.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.enterprise.dto.EnterpriseQueryDTO;
import com.park.enterprise.dto.EnterpriseSaveDTO;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.mapper.EnterpriseMapper;
import com.park.enterprise.entity.EnterpriseHonorRecord;
import com.park.enterprise.mapper.EnterpriseHonorRecordMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 入驻企业服务
 *
 * @author park-team
 */
@Slf4j
@Service
public class EnterpriseService {

    private static final java.util.List<String> DISPLAY_HONOR_TYPES = java.util.Arrays.asList(
            "new_national_high_tech",
            "new_specialty_giant",
            "new_specialty_sme",
            "new_provincial_hidden_champion",
            "new_single_champion",
            "new_ipo",
            "innovative_sme",
            "new_provincial_tech_small"
    );

    /** 荣誉类型编码 → 中文名称 映射 */
    private static final java.util.Map<String, String> HONOR_TYPE_NAME_MAP = new java.util.HashMap<>();

    static {
        HONOR_TYPE_NAME_MAP.put("existing_above_scale", "规上工业企业(存量)");
        HONOR_TYPE_NAME_MAP.put("new_above_scale", "新增规上工业企业");
        HONOR_TYPE_NAME_MAP.put("retired_above_scale", "退规下工业企业");
        HONOR_TYPE_NAME_MAP.put("new_single_champion", "单项冠军");
        HONOR_TYPE_NAME_MAP.put("new_ipo", "上市企业");
        HONOR_TYPE_NAME_MAP.put("new_specialty_giant", "专精特新小巨人");
        HONOR_TYPE_NAME_MAP.put("new_provincial_hidden_champion", "省级隐形冠军");
        HONOR_TYPE_NAME_MAP.put("new_specialty_sme", "省专精特新中小企业");
        HONOR_TYPE_NAME_MAP.put("new_national_high_tech", "国家高新技术企业");
        HONOR_TYPE_NAME_MAP.put("innovative_sme", "创新型中小企业");
        HONOR_TYPE_NAME_MAP.put("new_provincial_tech_small", "省科技型中小企业");
        HONOR_TYPE_NAME_MAP.put("new_first_equipment", "首台(套)装备");
        HONOR_TYPE_NAME_MAP.put("first_version", "首批次");
        HONOR_TYPE_NAME_MAP.put("first_batch", "首台批次");
        HONOR_TYPE_NAME_MAP.put("provincial_excellent_industrial", "省级优秀工业新产品");
        HONOR_TYPE_NAME_MAP.put("zhejiang_made_quality", "浙江制造精品");
        HONOR_TYPE_NAME_MAP.put("new_national_rd_agency", "国家级研发机构");
        HONOR_TYPE_NAME_MAP.put("new_provincial_rd_agency", "省级研发机构");
        HONOR_TYPE_NAME_MAP.put("new_municipal_rd_agency", "市级研发机构");
        HONOR_TYPE_NAME_MAP.put("public_service_platform", "公共服务平台");
    }

    /** 中文名称 → 荣誉类型编码 映射（反向查找，用于筛选） */
    private static final java.util.Map<String, String> HONOR_NAME_TYPE_MAP = new java.util.HashMap<>();

    static {
        for (java.util.Map.Entry<String, String> entry : HONOR_TYPE_NAME_MAP.entrySet()) {
            HONOR_NAME_TYPE_MAP.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 荣誉类型编码转中文名称
     */
    private String honorTypeToName(String honorType) {
        if (!StringUtils.hasText(honorType)) return honorType;
        String name = HONOR_TYPE_NAME_MAP.get(honorType);
        return name != null ? name : honorType;
    }

    /**
     * 中文名称转荣誉类型编码（用于筛选）
     */
    private String honorNameToType(String honorName) {
        if (!StringUtils.hasText(honorName)) return honorName;
        String type = HONOR_NAME_TYPE_MAP.get(honorName);
        return type != null ? type : honorName;
    }

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private EnterpriseHonorRecordMapper enterpriseHonorRecordMapper;

    public EnterpriseMapper getEnterpriseMapper() {
        return enterpriseMapper;
    }

    /**
     * 分页查询企业列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<EnterpriseInfo> getEnterprisePage(EnterpriseQueryDTO queryDTO) {
        // 构建分页对象
        Page<EnterpriseInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<EnterpriseInfo> queryWrapper = new LambdaQueryWrapper<>();

        // 企业名称模糊查询
        if (StringUtils.hasText(queryDTO.getEnterpriseName())) {
            queryWrapper.like(EnterpriseInfo::getEnterpriseName, queryDTO.getEnterpriseName());
        }

        // 园区筛选
        if (queryDTO.getParkId() != null) {
            queryWrapper.eq(EnterpriseInfo::getParkId, queryDTO.getParkId());
        }
        // 支持多园区查询（区县管理员使用）
        if (queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty()) {
            queryWrapper.in(EnterpriseInfo::getParkId, queryDTO.getParkIds());
        }

        // 行业筛选
        if (StringUtils.hasText(queryDTO.getIndustryName())) {
            queryWrapper.eq(EnterpriseInfo::getIndustryName, queryDTO.getIndustryName());
        }

        // 状态筛选
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(EnterpriseInfo::getStatus, queryDTO.getStatus());
        }

        // 参评筛选
        if (queryDTO.getIsParticipate() != null) {
            queryWrapper.eq(EnterpriseInfo::getIsParticipate, queryDTO.getIsParticipate());
        }

        // 企业荣誉筛选：通过 enterprise_honor_record 表反查匹配的企业
        if (StringUtils.hasText(queryDTO.getEnterpriseHonor())) {
            // 前端传中文名称，转为英文编码后查询
            String honorType = honorNameToType(queryDTO.getEnterpriseHonor());
            LambdaQueryWrapper<EnterpriseHonorRecord> honorWrapper = new LambdaQueryWrapper<>();
            honorWrapper.eq(EnterpriseHonorRecord::getHonorType, honorType);
            java.util.List<EnterpriseHonorRecord> honorRecords = enterpriseHonorRecordMapper.selectList(honorWrapper);
            if (honorRecords.isEmpty()) {
                // 没有匹配的企业荣誉，返回空结果
                queryWrapper.eq(EnterpriseInfo::getId, -1L);
            } else {
                // 收集匹配企业的 credit_code 和 enterprise_name
                java.util.Set<String> creditCodes = new java.util.HashSet<>();
                java.util.Set<String> enterpriseNames = new java.util.HashSet<>();
                for (EnterpriseHonorRecord hr : honorRecords) {
                    if (StringUtils.hasText(hr.getCreditCode())) {
                        creditCodes.add(hr.getCreditCode());
                    }
                    if (StringUtils.hasText(hr.getEnterpriseName())) {
                        enterpriseNames.add(hr.getEnterpriseName());
                    }
                }
                // 用 credit_code in 或 enterprise_name in 过滤企业
                if (!creditCodes.isEmpty()) {
                    queryWrapper.in(EnterpriseInfo::getCreditCode, creditCodes);
                } else if (!enterpriseNames.isEmpty()) {
                    queryWrapper.in(EnterpriseInfo::getEnterpriseName, enterpriseNames);
                } else {
                    queryWrapper.eq(EnterpriseInfo::getId, -1L);
                }
            }
        }

        // 所属区县筛选（districtName 是 transient 字段，不能在DB层筛选，由内存过滤）

        // 按创建时间降序排序
        queryWrapper.orderByDesc(EnterpriseInfo::getCreateTime);

        IPage<EnterpriseInfo> result = enterpriseMapper.selectPage(page, queryWrapper);

        // 填充非表字段：园区名称、区县名称、企业荣誉
        for (EnterpriseInfo enterprise : result.getRecords()) {
            // 填充园区名称、区县名称
            if (enterprise.getParkId() != null) {
                ParkInfo park = parkMapper.selectById(enterprise.getParkId());
                if (park != null) {
                    enterprise.setParkName(park.getParkName());
                    if (!StringUtils.hasText(enterprise.getDistrictName())) {
                        enterprise.setDistrictName(park.getDistrictName());
                    }
                }
            }
            // 填充企业荣誉（查询所有荣誉类型）
            if (StringUtils.hasText(enterprise.getCreditCode()) || StringUtils.hasText(enterprise.getEnterpriseName())) {
                LambdaQueryWrapper<EnterpriseHonorRecord> honorWrapper = new LambdaQueryWrapper<>();
                if (StringUtils.hasText(enterprise.getCreditCode())) {
                    honorWrapper.eq(EnterpriseHonorRecord::getCreditCode, enterprise.getCreditCode());
                } else {
                    honorWrapper.eq(EnterpriseHonorRecord::getEnterpriseName, enterprise.getEnterpriseName());
                }
                java.util.List<EnterpriseHonorRecord> honorRecords = enterpriseHonorRecordMapper.selectList(honorWrapper);
                if (!honorRecords.isEmpty()) {
                    String honorText = honorRecords.stream()
                            .map(EnterpriseHonorRecord::getHonorType)
                            .filter(StringUtils::hasText)
                            .distinct()
                            .map(this::honorTypeToName)
                            .collect(java.util.stream.Collectors.joining("/"));
                    enterprise.setEnterpriseHonor(honorText);
                }
            }
        }

        return result;
    }

    /**
     * 根据ID查询企业详情
     *
     * @param id 企业ID
     * @return 企业信息
     */
    public EnterpriseInfo getEnterpriseById(Long id) {
        EnterpriseInfo enterprise = enterpriseMapper.selectById(id);
        if (enterprise == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "企业不存在");
        }
        // 填充园区名称、区县名称
        if (enterprise.getParkId() != null) {
            ParkInfo park = parkMapper.selectById(enterprise.getParkId());
            if (park != null) {
                enterprise.setParkName(park.getParkName());
                if (!StringUtils.hasText(enterprise.getDistrictName())) {
                    enterprise.setDistrictName(park.getDistrictName());
                }
            }
        }
        // 填充企业荣誉（按企业查询，仅显示核心荣誉类型）
        if (StringUtils.hasText(enterprise.getCreditCode()) || StringUtils.hasText(enterprise.getEnterpriseName())) {
            LambdaQueryWrapper<EnterpriseHonorRecord> honorWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(enterprise.getCreditCode())) {
                honorWrapper.eq(EnterpriseHonorRecord::getCreditCode, enterprise.getCreditCode());
            } else {
                honorWrapper.eq(EnterpriseHonorRecord::getEnterpriseName, enterprise.getEnterpriseName());
            }
            honorWrapper.in(EnterpriseHonorRecord::getHonorType, DISPLAY_HONOR_TYPES);
            java.util.List<EnterpriseHonorRecord> honorRecords = enterpriseHonorRecordMapper.selectList(honorWrapper);
            if (!honorRecords.isEmpty()) {
                String honorText = honorRecords.stream()
                        .map(EnterpriseHonorRecord::getHonorType)
                        .filter(StringUtils::hasText)
                        .distinct()
                        .map(this::honorTypeToName)
                        .collect(java.util.stream.Collectors.joining("/"));
                enterprise.setEnterpriseHonor(honorText);
            }
        }
        return enterprise;
    }

    /**
     * 新增企业
     *
     * @param saveDTO 企业信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEnterprise(EnterpriseSaveDTO saveDTO) {
        // 检查企业名称是否已存在
        checkEnterpriseNameExists(saveDTO.getEnterpriseName(), saveDTO.getParkId(), null);

        // 转换并保存
        EnterpriseInfo enterprise = new EnterpriseInfo();
        BeanUtils.copyProperties(saveDTO, enterprise);
        enterprise.setIsParticipate(enterprise.getIsParticipate() != null ? enterprise.getIsParticipate() : 1);
        enterprise.setStatus(enterprise.getStatus() != null ? enterprise.getStatus() : "在营");

        // 同步园区区县名称
        syncDistrictName(enterprise);

        int rows = enterpriseMapper.insert(enterprise);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_SAVE_ERROR);
        }
        log.info("新增企业成功：{}", enterprise.getEnterpriseName());
    }

    /**
     * 修改企业
     *
     * @param saveDTO 企业信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEnterprise(EnterpriseSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "企业ID不能为空");
        }

        // 检查企业是否存在
        EnterpriseInfo existing = getEnterpriseById(saveDTO.getId());

        // 检查企业名称是否已存在（排除自身）
        checkEnterpriseNameExists(saveDTO.getEnterpriseName(), saveDTO.getParkId(), saveDTO.getId());

        // 更新属性
        BeanUtils.copyProperties(saveDTO, existing);

        // 同步园区区县名称
        syncDistrictName(existing);

        int rows = enterpriseMapper.updateById(existing);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_UPDATE_ERROR);
        }
        log.info("修改企业成功：ID={}, 名称={}", existing.getId(), existing.getEnterpriseName());
    }

    /**
     * 删除企业
     *
     * @param id 企业ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnterprise(Long id) {
        // 检查企业是否存在
        getEnterpriseById(id);

        int rows = enterpriseMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_DELETE_ERROR);
        }
        log.info("删除企业成功：ID={}", id);
    }

    /**
     * 检查企业名称是否已存在
     *
     * @param enterpriseName 企业名称
     * @param parkId         园区ID
     * @param excludeId      排除的企业ID（修改时使用）
     */
    /**
     * 同步园区区县名称到企业实体
     */
    private void syncDistrictName(EnterpriseInfo enterprise) {
        if (enterprise.getParkId() != null) {
            ParkInfo park = parkMapper.selectById(enterprise.getParkId());
            if (park != null && StringUtils.hasText(park.getDistrictName())) {
                enterprise.setDistrictName(park.getDistrictName());
            }
        }
    }

    private void checkEnterpriseNameExists(String enterpriseName, Long parkId, Long excludeId) {
        LambdaQueryWrapper<EnterpriseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnterpriseInfo::getEnterpriseName, enterpriseName)
                   .eq(EnterpriseInfo::getParkId, parkId);

        if (excludeId != null) {
            queryWrapper.ne(EnterpriseInfo::getId, excludeId);
        }

        Long count = enterpriseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_EXISTS, "该园区下已存在同名企业");
        }
    }
}
