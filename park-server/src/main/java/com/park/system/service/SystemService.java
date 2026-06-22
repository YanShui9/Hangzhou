package com.park.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.system.dto.request.DistrictAccountDTO;
import com.park.system.dto.request.EnterpriseInfoDTO;
import com.park.system.dto.request.ParkAccountDTO;
import com.park.system.dto.request.SystemDataDTO;
import com.park.system.entity.DistrictAccount;
import com.park.system.entity.EnterpriseInfo;
import com.park.system.entity.ParkAccount;
import com.park.system.entity.SystemData;

/**
 * 系统服务接口
 *
 * @author park-team
 */
public interface SystemService {

    /**
     * 分页查询区县账号列表
     */
    IPage<DistrictAccount> getDistrictAccountPage(Integer pageNum, Integer pageSize, String name, String phone, String district);

    /**
     * 保存区县账号
     */
    void saveDistrictAccount(DistrictAccountDTO dto);

    /**
     * 删除区县账号
     */
    void deleteDistrictAccount(Long id);

    /**
     * 重置区县账号密码
     */
    void resetDistrictAccountPwd(Long id);

    /**
     * 分页查询园区账号列表
     */
    IPage<ParkAccount> getParkAccountPage(Integer pageNum, Integer pageSize, String companyName, String unifiedCode, String parkName, String district);

    /**
     * 保存园区账号
     */
    void saveParkAccount(ParkAccountDTO dto);

    /**
     * 删除园区账号
     */
    void deleteParkAccount(Long id);

    /**
     * 分页查询数据仓库列表
     */
    IPage<SystemData> getDataWarehousePage(Integer pageNum, Integer pageSize, String dataName, String year);

    /**
     * 保存数据仓库
     */
    void saveDataWarehouse(SystemDataDTO dto);

    /**
     * 删除数据仓库
     */
    void deleteDataWarehouse(Long id);

    /**
     * 分页查询企业信息列表
     */
    IPage<EnterpriseInfo> getEnterpriseInfoPage(Integer pageNum, Integer pageSize, String keyword, String district, String parkId, String status);

    /**
     * 根据ID查询企业信息
     */
    EnterpriseInfo getEnterpriseInfoById(Long id);

    /**
     * 保存企业信息
     */
    void saveEnterpriseInfo(EnterpriseInfoDTO dto);
}