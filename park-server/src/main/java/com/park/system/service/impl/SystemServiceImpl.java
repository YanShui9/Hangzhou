package com.park.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.park.system.dto.request.DistrictAccountDTO;
import com.park.system.dto.request.EnterpriseInfoDTO;
import com.park.system.dto.request.ParkAccountDTO;
import com.park.system.dto.request.SystemDataDTO;
import com.park.system.entity.DistrictAccount;
import com.park.system.entity.EnterpriseInfo;
import com.park.system.entity.ParkAccount;
import com.park.system.entity.SystemData;
import com.park.system.mapper.DistrictAccountMapper;
import com.park.system.mapper.EnterpriseInfoMapper;
import com.park.system.mapper.ParkAccountMapper;
import com.park.system.mapper.SystemDataMapper;
import com.park.system.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 系统服务实现类
 *
 * @author park-team
 */
@Slf4j
@Service
public class SystemServiceImpl extends ServiceImpl<DistrictAccountMapper, DistrictAccount> implements SystemService {

    private final DistrictAccountMapper districtAccountMapper;
    private final ParkAccountMapper parkAccountMapper;
    private final SystemDataMapper systemDataMapper;
    private final EnterpriseInfoMapper enterpriseInfoMapper;

    public SystemServiceImpl(DistrictAccountMapper districtAccountMapper,
                           ParkAccountMapper parkAccountMapper,
                           SystemDataMapper systemDataMapper,
                           EnterpriseInfoMapper enterpriseInfoMapper) {
        this.districtAccountMapper = districtAccountMapper;
        this.parkAccountMapper = parkAccountMapper;
        this.systemDataMapper = systemDataMapper;
        this.enterpriseInfoMapper = enterpriseInfoMapper;
    }

    @Override
    public IPage<DistrictAccount> getDistrictAccountPage(Integer pageNum, Integer pageSize, String name, String phone, String district) {
        Page<DistrictAccount> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DistrictAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), DistrictAccount::getName, name)
                    .like(StringUtils.isNotBlank(phone), DistrictAccount::getPhone, phone)
                    .eq(StringUtils.isNotBlank(district), DistrictAccount::getDistrict, district)
                    .orderByDesc(DistrictAccount::getCreateTime);
        return districtAccountMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void saveDistrictAccount(DistrictAccountDTO dto) {
        DistrictAccount account = new DistrictAccount();
        BeanUtils.copyProperties(dto, account);
        if (account.getId() == null) {
            account.setCreateTime(LocalDateTime.now());
            districtAccountMapper.insert(account);
        } else {
            districtAccountMapper.updateById(account);
        }
    }

    @Override
    public void deleteDistrictAccount(Long id) {
        districtAccountMapper.deleteById(id);
    }

    @Override
    public void resetDistrictAccountPwd(Long id) {
        // 密码重置逻辑
    }

    @Override
    public IPage<ParkAccount> getParkAccountPage(Integer pageNum, Integer pageSize, String companyName, String unifiedCode, String parkName, String district) {
        Page<ParkAccount> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ParkAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(companyName), ParkAccount::getCompanyName, companyName)
                    .like(StringUtils.isNotBlank(unifiedCode), ParkAccount::getUnifiedCode, unifiedCode)
                    .like(StringUtils.isNotBlank(parkName), ParkAccount::getParkName, parkName)
                    .eq(StringUtils.isNotBlank(district), ParkAccount::getDistrict, district)
                    .orderByDesc(ParkAccount::getCreateTime);
        return parkAccountMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void saveParkAccount(ParkAccountDTO dto) {
        ParkAccount account = new ParkAccount();
        BeanUtils.copyProperties(dto, account);
        if (account.getId() == null) {
            account.setCreateTime(LocalDateTime.now());
            parkAccountMapper.insert(account);
        } else {
            parkAccountMapper.updateById(account);
        }
    }

    @Override
    public void deleteParkAccount(Long id) {
        parkAccountMapper.deleteById(id);
    }

    @Override
    public IPage<SystemData> getDataWarehousePage(Integer pageNum, Integer pageSize, String dataName, String year) {
        Page<SystemData> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SystemData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(dataName), SystemData::getDataName, dataName)
                    .eq(StringUtils.isNotBlank(year), SystemData::getYear, year)
                    .orderByDesc(SystemData::getCreateTime);
        return systemDataMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void saveDataWarehouse(SystemDataDTO dto) {
        SystemData data = new SystemData();
        BeanUtils.copyProperties(dto, data);
        if (data.getId() == null) {
            data.setCreateTime(LocalDateTime.now());
            systemDataMapper.insert(data);
        } else {
            systemDataMapper.updateById(data);
        }
    }

    @Override
    public void deleteDataWarehouse(Long id) {
        systemDataMapper.deleteById(id);
    }

    @Override
    public IPage<EnterpriseInfo> getEnterpriseInfoPage(Integer pageNum, Integer pageSize, String keyword, String district, String parkId, String status) {
        Page<EnterpriseInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EnterpriseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(StringUtils.isNotBlank(keyword), wrapper -> wrapper
                    .like(EnterpriseInfo::getEnterpriseName, keyword)
                    .or()
                    .like(EnterpriseInfo::getUnifiedCode, keyword))
                    .eq(StringUtils.isNotBlank(district), EnterpriseInfo::getDistrict, district)
                    .eq(StringUtils.isNotBlank(parkId), EnterpriseInfo::getParkName, parkId)
                    .eq(StringUtils.isNotBlank(status), EnterpriseInfo::getStatus, status)
                    .orderByDesc(EnterpriseInfo::getCreateTime);
        return enterpriseInfoMapper.selectPage(page, queryWrapper);
    }

    @Override
    public EnterpriseInfo getEnterpriseInfoById(Long id) {
        return enterpriseInfoMapper.selectById(id);
    }

    @Override
    public void saveEnterpriseInfo(EnterpriseInfoDTO dto) {
        EnterpriseInfo info = new EnterpriseInfo();
        BeanUtils.copyProperties(dto, info);
        if (info.getId() == null) {
            info.setCreateTime(LocalDateTime.now());
            enterpriseInfoMapper.insert(info);
        } else {
            enterpriseInfoMapper.updateById(info);
        }
    }
}