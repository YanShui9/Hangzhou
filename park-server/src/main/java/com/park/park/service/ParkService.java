package com.park.park.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.park.dto.ParkQueryDTO;
import com.park.park.dto.ParkSaveDTO;
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
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<ParkInfo> getParkPage(ParkQueryDTO queryDTO) {
        // 构建分页对象
        Page<ParkInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getId() != null, ParkInfo::getId, queryDTO.getId());
        queryWrapper.like(StringUtils.hasText(queryDTO.getParkName()), ParkInfo::getParkName, queryDTO.getParkName());
        queryWrapper.eq(StringUtils.hasText(queryDTO.getDistrictName()), ParkInfo::getDistrictName, queryDTO.getDistrictName());
        queryWrapper.eq(queryDTO.getParkType() != null, ParkInfo::getParkType, queryDTO.getParkType());
        queryWrapper.eq(queryDTO.getStarLevel() != null, ParkInfo::getStarLevel, queryDTO.getStarLevel());
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
        log.info("开始查询园区，ID: {}", id);
        try {
            ParkInfo parkInfo = parkMapper.selectById(id);
            log.info("查询完成，结果: {}", parkInfo);
            if (parkInfo == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "园区不存在");
            }
            return parkInfo;
        } catch (Exception e) {
            log.error("查询园区失败，ID: {}，异常: {}", id, e.getMessage(), e);
            throw e;
        }
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
     * @param saveDTO 园区信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePark(ParkSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区ID不能为空");
        }

        getParkById(saveDTO.getId());

        checkParkNameDuplicate(saveDTO.getParkName(), saveDTO.getId());

        ParkInfo parkInfo = new ParkInfo();
        BeanUtils.copyProperties(saveDTO, parkInfo);

        log.info("修改园区：ID={}, 名称={}, parkImages={}", parkInfo.getId(), parkInfo.getParkName(), parkInfo.getParkImages());
        parkMapper.updateById(parkInfo);

        if (saveDTO.getParkImages() != null) {
            LambdaUpdateWrapper<ParkInfo> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(ParkInfo::getId, saveDTO.getId())
                   .set(ParkInfo::getParkImages, saveDTO.getParkImages());
            parkMapper.update(null, wrapper);
            log.info("强制更新 parkImages：ID={}, parkImages={}", saveDTO.getId(), saveDTO.getParkImages());
        }
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
     * @param excludeId 排除的园区ID（修改时使用）
     */
    private void checkParkNameDuplicate(String parkName, Long excludeId) {
        LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParkInfo::getParkName, parkName);
        if (excludeId != null) {
            queryWrapper.ne(ParkInfo::getId, excludeId);
        }
        Long count = parkMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区名称已存在");
        }
    }
}
