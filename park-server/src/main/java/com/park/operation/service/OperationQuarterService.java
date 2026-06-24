package com.park.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.operation.dto.OperationQuarterSaveDTO;
import com.park.operation.entity.ParkOperationQuarter;
import com.park.operation.mapper.OperationQuarterMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class OperationQuarterService extends ServiceImpl<OperationQuarterMapper, ParkOperationQuarter> {

    @Autowired
    private ParkMapper parkMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long save(OperationQuarterSaveDTO dto) {
        ParkOperationQuarter entity = dto.toEntity();
        
        if (entity.getId() != null) {
            ParkOperationQuarter existing = getById(entity.getId());
            if (existing == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "记录不存在");
            }
            updateById(entity);
            updateParkInfo(entity);
            return entity.getId();
        } else {
            LambdaQueryWrapper<ParkOperationQuarter> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ParkOperationQuarter::getParkId, entity.getParkId())
                    .eq(ParkOperationQuarter::getYear, entity.getYear())
                    .eq(ParkOperationQuarter::getQuarter, entity.getQuarter());
            
            ParkOperationQuarter existing = getOne(queryWrapper);
            if (existing != null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "该季度数据已存在");
            }
            
            save(entity);
            updateParkInfo(entity);
            return entity.getId();
        }
    }

    private void updateParkInfo(ParkOperationQuarter entity) {
        ParkInfo parkInfo = parkMapper.selectById(entity.getParkId());
        if (parkInfo != null) {
            if (entity.getCurrentEnterprises() != null) {
                parkInfo.setEnterpriseCount(entity.getCurrentEnterprises());
            }
            if (entity.getAboveScaleCount() != null) {
                parkInfo.setAboveScaleCount(entity.getAboveScaleCount());
            }
            if (entity.getHighTechCount() != null) {
                parkInfo.setHighTechCount(entity.getHighTechCount());
            }
            if (entity.getTechSmeCount() != null) {
                parkInfo.setTechSmeCount(entity.getTechSmeCount());
            }
            if (entity.getHiddenChampionCount() != null) {
                parkInfo.setHiddenChampionCount(entity.getHiddenChampionCount());
            }
            if (entity.getNationalSrtiCount() != null) {
                parkInfo.setNationalSrtiCount(entity.getNationalSrtiCount());
            }
            if (entity.getInnovativeSmeCount() != null) {
                parkInfo.setInnovativeSmeCount(entity.getInnovativeSmeCount());
            }
            if (entity.getProvincialSrtiCount() != null) {
                parkInfo.setProvincialSrtiCount(entity.getProvincialSrtiCount());
            }
            if (entity.getRentedArea() != null) {
                parkInfo.setRentedArea(entity.getRentedArea());
            }
            if (entity.getAvailableRentArea() != null) {
                parkInfo.setRentRemainArea(entity.getAvailableRentArea());
            }
            if (entity.getAvailableSaleArea() != null) {
                parkInfo.setSaleRemainArea(entity.getAvailableSaleArea());
            }
            if (entity.getEmployeeCount() != null) {
                parkInfo.setEmployeeCount(entity.getEmployeeCount());
            }
            if (entity.getNationalTalentCount() != null) {
                parkInfo.setNationalTalent(entity.getNationalTalentCount());
            }
            if (entity.getProvincialTalentCount() != null) {
                parkInfo.setProvincialTalent(entity.getProvincialTalentCount());
            }
            if (entity.getMasterAndSeniorCount() != null) {
                parkInfo.setMasterAbove(entity.getMasterAndSeniorCount());
            }
            if (entity.getSeniorEngineerCount() != null) {
                parkInfo.setSeniorEngineer(entity.getSeniorEngineerCount());
            }
            if (entity.getEngineerCount() != null) {
                parkInfo.setEngineer(entity.getEngineerCount());
            }
            if (entity.getTechnicianCount() != null) {
                parkInfo.setSeniorTechnician(entity.getTechnicianCount());
            }
            if (entity.getMasterCount() != null) {
                parkInfo.setMasterDegree(entity.getMasterCount());
            }
            if (entity.getPatentCount() != null) {
                parkInfo.setPatentTotal(entity.getPatentCount());
            }
            if (entity.getInventionPatentCount() != null) {
                parkInfo.setPatentInvention(entity.getInventionPatentCount());
            }
            if (entity.getUtilityPatentCount() != null) {
                parkInfo.setPatentUtility(entity.getUtilityPatentCount());
            }
            if (entity.getDesignPatentCount() != null) {
                parkInfo.setPatentDesign(entity.getDesignPatentCount());
            }
            
            parkMapper.updateById(parkInfo);
            log.info("同步更新园区基础信息成功，园区ID: {}", entity.getParkId());
        }
    }

    public ParkOperationQuarter getById(Long id) {
        ParkOperationQuarter entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "记录不存在");
        }
        return entity;
    }

    public List<ParkOperationQuarter> listByParkId(Long parkId) {
        LambdaQueryWrapper<ParkOperationQuarter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParkOperationQuarter::getParkId, parkId)
                .orderByDesc(ParkOperationQuarter::getYear)
                .orderByDesc(ParkOperationQuarter::getQuarter);
        return list(queryWrapper);
    }

    public List<ParkOperationQuarter> listByParkIdAndYear(Long parkId, Integer year) {
        LambdaQueryWrapper<ParkOperationQuarter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParkOperationQuarter::getParkId, parkId)
                .eq(ParkOperationQuarter::getYear, year)
                .orderByAsc(ParkOperationQuarter::getQuarter);
        return list(queryWrapper);
    }

    public ParkOperationQuarter getByParkIdYearQuarter(Long parkId, Integer year, Integer quarter) {
        LambdaQueryWrapper<ParkOperationQuarter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParkOperationQuarter::getParkId, parkId)
                .eq(ParkOperationQuarter::getYear, year)
                .eq(ParkOperationQuarter::getQuarter, quarter);
        return getOne(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        ParkOperationQuarter entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "记录不存在");
        }
        removeById(id);
    }

    public List<ParkOperationQuarter> listByQuery(Long parkId, Integer year, Integer quarter) {
        LambdaQueryWrapper<ParkOperationQuarter> queryWrapper = new LambdaQueryWrapper<>();
        
        if (parkId != null) {
            queryWrapper.eq(ParkOperationQuarter::getParkId, parkId);
        }
        if (year != null) {
            queryWrapper.eq(ParkOperationQuarter::getYear, year);
        }
        if (quarter != null) {
            queryWrapper.eq(ParkOperationQuarter::getQuarter, quarter);
        }
        
        queryWrapper.orderByDesc(ParkOperationQuarter::getYear)
                .orderByDesc(ParkOperationQuarter::getQuarter);
        
        return list(queryWrapper);
    }
}
