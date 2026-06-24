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

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    /**
     * 分页查询企业列表（JOIN多表查询）
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<EnterpriseInfo> getEnterprisePage(EnterpriseQueryDTO queryDTO) {
        Page<EnterpriseInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        return enterpriseMapper.selectEnterprisePageWithJoin(
                page,
                queryDTO.getKeyword(),
                queryDTO.getEnterpriseName(),
                queryDTO.getParkId(),
                queryDTO.getParkIds(),
                queryDTO.getDistrictId(),
                queryDTO.getIndustryName(),
                queryDTO.getHonor(),
                queryDTO.getStatus(),
                queryDTO.getRegisterStatus(),
                queryDTO.getIsParticipate()
        );
    }

    /**
     * 根据ID查询企业详情（JOIN多表查询）
     *
     * @param id 企业ID
     * @return 企业信息
     */
    public EnterpriseInfo getEnterpriseById(Long id) {
        EnterpriseInfo enterprise = enterpriseMapper.selectEnterpriseByIdWithJoin(id);
        if (enterprise == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "企业不存在");
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
