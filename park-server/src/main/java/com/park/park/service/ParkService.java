package com.park.park.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.park.dto.ParkQueryDTO;
import com.park.park.dto.ParkSaveDTO;
import com.park.park.dto.ParkStatsDTO;
import com.park.park.dto.TotalStatsDTO;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 园区服务
 *
 * @author park-team
 */
@Slf4j
@Service
public class ParkService {

    @Autowired
    private ParkMapper parkMapper;

    /**
     * 分页查询园区列表
     * 所有查询条件均为可选项：空字符串或 null 表示不过滤该字段。
     * 类型严格与 ParkInfo 对齐，避免 MyBatis-Plus 类型不匹配导致的异常。
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<ParkInfo> getParkPage(ParkQueryDTO queryDTO) {
        // 构建分页对象
        Page<ParkInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();

        // ID 精确匹配
        queryWrapper.eq(queryDTO.getId() != null, ParkInfo::getId, queryDTO.getId());

        // 园区名称 模糊匹配
        queryWrapper.like(StringUtils.hasText(queryDTO.getParkName()),
                ParkInfo::getParkName, queryDTO.getParkName());

        // 区县名称 精确匹配
        queryWrapper.eq(StringUtils.hasText(queryDTO.getDistrictName()),
                ParkInfo::getDistrictName, queryDTO.getDistrictName());

        // 园区类型 精确匹配（String 类型，与 ParkInfo.parkType 保持一致）
        // 支持空字符串（前端默认值）：当为 null 或空串时不加入条件
        queryWrapper.eq(StringUtils.hasText(queryDTO.getParkType()),
                ParkInfo::getParkType, queryDTO.getParkType());

        // 星级 精确匹配
        queryWrapper.eq(queryDTO.getStarLevel() != null,
                ParkInfo::getStarLevel, queryDTO.getStarLevel());

        // 园区认定 精确匹配
        queryWrapper.eq(StringUtils.hasText(queryDTO.getRecognition()),
                ParkInfo::getRecognition, queryDTO.getRecognition());

        // 园区状态 精确匹配
        queryWrapper.eq(StringUtils.hasText(queryDTO.getParkStatus()),
                ParkInfo::getParkStatus, queryDTO.getParkStatus());

        // 按创建时间倒序，最新在前
        queryWrapper.orderByDesc(ParkInfo::getCreateTime);

        return parkMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID查询园区
     *
     * @param id 园区ID
     * @return 园区信息
     */
    public ParkInfo getParkById(Long id) {
        ParkInfo parkInfo = parkMapper.selectById(id);
        if (parkInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "园区不存在");
        }
        return parkInfo;
    }

    /**
     * 获取园区统计数据
     * 将 ParkInfo 中的统计字段映射为前端期望的字段名
     *
     * @param id 园区ID
     * @return 园区统计数据
     */
    public ParkStatsDTO getParkStats(Long id) {
        ParkInfo parkInfo = getParkById(id);

        return ParkStatsDTO.builder()
                // 入驻企业统计
                .enterpriseCount(parkInfo.getEnterpriseCount())
                .largeEnterpriseCount(parkInfo.getAboveScaleCount())
                .highTechEnterpriseCount(parkInfo.getHighTechCount())
                .smeCount(parkInfo.getTechSmeCount())
                .innovativeSmeCount(parkInfo.getInnovativeSmeCount())
                .specializedSmeCount(parkInfo.getNationalSrtiCount())
                // 人才统计
                .employeeCount(parkInfo.getEmployeeCount())
                .nationalTalentCount(parkInfo.getNationalTalent())
                .provincialTalentCount(parkInfo.getProvincialTalent())
                .seniorEngineerCount(parkInfo.getSeniorEngineer())
                .engineerCount(parkInfo.getEngineer())
                .seniorTechnicianCount(parkInfo.getSeniorTechnician())
                .masterCount(parkInfo.getMasterAbove())
                .doctorCount(null)
                // 专利统计
                .patentCount(parkInfo.getPatentTotal())
                .inventionPatentCount(parkInfo.getPatentInvention())
                .utilityModelCount(parkInfo.getPatentUtility())
                .designPatentCount(parkInfo.getPatentDesign())
                .build();
    }

    /**
     * 获取全市园区统计数据
     * 用于前端"园区列表"页面顶部统计卡片
     *
     * @return 全市统计数据
     */
    public TotalStatsDTO getTotalStats() {
        // 统计园区总数
        Long parkTotal = parkMapper.selectCount(null);

        // 统计星级园区数
        Long starParkCount = parkMapper.selectCount(
                new LambdaQueryWrapper<ParkInfo>()
                        .isNotNull(ParkInfo::getStarLevel)
                        .gt(ParkInfo::getStarLevel, 0)
        );

        // 计算星级园区占比
        String starRate = "0%";
        if (parkTotal != null && parkTotal > 0 && starParkCount != null) {
            double rate = (starParkCount * 100.0) / parkTotal;
            starRate = String.format("%.1f%%", rate);
        }

        return TotalStatsDTO.builder()
                .parkTotal(parkTotal != null ? parkTotal.intValue() : 0)
                .enterpriseTotal(0)
                .employeeTotal(0)
                .starRate(starRate)
                .build();
    }

    /**
     * 新增园区
     *
     * @param saveDTO 园区信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void savePark(ParkSaveDTO saveDTO) {
        // 检查园区名称是否重复
        checkParkNameDuplicate(saveDTO.getParkName(), null);

        ParkInfo parkInfo = new ParkInfo();
        BeanUtils.copyProperties(saveDTO, parkInfo);

        log.info("新增园区：{}", parkInfo.getParkName());
        parkMapper.insert(parkInfo);
    }

    /**
     * 修改园区
     *
     * @param saveDTO 园区信息（必须包含 id）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePark(ParkSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区ID不能为空");
        }

        // 检查园区是否存在
        getParkById(saveDTO.getId());

        // 检查园区名称是否重复（排除自身）
        checkParkNameDuplicate(saveDTO.getParkName(), saveDTO.getId());

        ParkInfo parkInfo = new ParkInfo();
        BeanUtils.copyProperties(saveDTO, parkInfo);

        log.info("修改园区：ID={}, 名称={}", parkInfo.getId(), parkInfo.getParkName());
        parkMapper.updateById(parkInfo);
    }

    /**
     * 删除园区
     *
     * @param id 园区ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePark(Long id) {
        // 检查园区是否存在
        getParkById(id);

        log.info("删除园区：ID={}", id);
        parkMapper.deleteById(id);
    }

    /**
     * 检查园区名称是否重复
     *
     * @param parkName  园区名称
     * @param excludeId 排除的园区ID（修改时使用，新增时为 null）
     */
    private void checkParkNameDuplicate(String parkName, Long excludeId) {
        if (!StringUtils.hasText(parkName)) {
            return;
        }
        LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParkInfo::getParkName, parkName);
        if (excludeId != null) {
            queryWrapper.ne(ParkInfo::getId, excludeId);
        }
        Long count = parkMapper.selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区名称已存在");
        }
    }
}
