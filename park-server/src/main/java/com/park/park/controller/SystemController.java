package com.park.park.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.system.dto.request.DistrictAccountDTO;
import com.park.system.dto.request.EnterpriseInfoDTO;
import com.park.system.dto.request.ParkAccountDTO;
import com.park.system.dto.request.SystemDataDTO;
import com.park.system.entity.DistrictAccount;
import com.park.system.entity.EnterpriseInfo;
import com.park.system.entity.ParkAccount;
import com.park.system.entity.SystemData;
import com.park.system.service.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统设置控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/system")
@Api(tags = "系统设置")
public class SystemController {

    @Autowired
    private SystemService systemService;

    /**
     * 分页查询区县账号列表
     */
    @GetMapping("/district-accounts")
    @ApiOperation(value = "分页查询区县账号列表", notes = "支持按姓名、手机号、区域筛选")
    public R<PageResult<DistrictAccount>> getDistrictAccountPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String district) {

        IPage<DistrictAccount> page = systemService.getDistrictAccountPage(pageNum, pageSize, name, phone, district);
        PageResult<DistrictAccount> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 新增或更新区县账号
     */
    @PostMapping("/district-accounts")
    @ApiOperation(value = "新增区县账号", notes = "创建新的区县账号")
    public R<Void> saveDistrictAccount(@RequestBody DistrictAccountDTO dto) {
        systemService.saveDistrictAccount(dto);
        return R.ok();
    }

    /**
     * 更新区县账号
     */
    @PutMapping("/district-accounts")
    @ApiOperation(value = "更新区县账号", notes = "更新区县账号信息")
    public R<Void> updateDistrictAccount(@RequestBody DistrictAccountDTO dto) {
        systemService.saveDistrictAccount(dto);
        return R.ok();
    }

    /**
     * 删除区县账号
     */
    @DeleteMapping("/district-accounts/{id}")
    @ApiOperation(value = "删除区县账号", notes = "根据ID删除区县账号")
    public R<Void> deleteDistrictAccount(@PathVariable Long id) {
        systemService.deleteDistrictAccount(id);
        return R.ok();
    }

    /**
     * 重置区县账号密码
     */
    @PostMapping("/district-accounts/{id}/reset-pwd")
    @ApiOperation(value = "重置区县账号密码", notes = "将密码重置为123456")
    public R<Void> resetDistrictAccountPwd(@PathVariable Long id) {
        systemService.resetDistrictAccountPwd(id);
        return R.ok();
    }

    /**
     * 分页查询园区账号列表
     */
    @GetMapping("/park-accounts")
    @ApiOperation(value = "分页查询园区账号列表", notes = "支持按企业名称、统一信用代码、园区名称、区域筛选")
    public R<PageResult<ParkAccount>> getParkAccountPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String unifiedCode,
            @RequestParam(required = false) String parkName,
            @RequestParam(required = false) String district) {

        IPage<ParkAccount> page = systemService.getParkAccountPage(pageNum, pageSize, companyName, unifiedCode, parkName, district);
        PageResult<ParkAccount> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 新增或更新园区账号
     */
    @PostMapping("/park-accounts")
    @ApiOperation(value = "新增园区账号", notes = "创建新的园区账号")
    public R<Void> saveParkAccount(@RequestBody ParkAccountDTO dto) {
        systemService.saveParkAccount(dto);
        return R.ok();
    }

    /**
     * 更新园区账号
     */
    @PutMapping("/park-accounts")
    @ApiOperation(value = "更新园区账号", notes = "更新园区账号信息")
    public R<Void> updateParkAccount(@RequestBody ParkAccountDTO dto) {
        systemService.saveParkAccount(dto);
        return R.ok();
    }

    /**
     * 删除园区账号
     */
    @DeleteMapping("/park-accounts/{id}")
    @ApiOperation(value = "删除园区账号", notes = "根据ID删除园区账号")
    public R<Void> deleteParkAccount(@PathVariable Long id) {
        systemService.deleteParkAccount(id);
        return R.ok();
    }

    /**
     * 分页查询数据仓库列表
     */
    @GetMapping("/data-warehouse")
    @ApiOperation(value = "分页查询数据仓库列表", notes = "支持按数据名称、年度筛选")
    public R<PageResult<SystemData>> getDataWarehousePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dataName,
            @RequestParam(required = false) String year) {

        IPage<SystemData> page = systemService.getDataWarehousePage(pageNum, pageSize, dataName, year);
        PageResult<SystemData> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 新增或更新数据仓库
     */
    @PostMapping("/data-warehouse")
    @ApiOperation(value = "新增数据仓库", notes = "创建新的数据仓库记录")
    public R<Void> saveDataWarehouse(@RequestBody SystemDataDTO dto) {
        systemService.saveDataWarehouse(dto);
        return R.ok();
    }

    /**
     * 更新数据仓库
     */
    @PutMapping("/data-warehouse")
    @ApiOperation(value = "更新数据仓库", notes = "更新数据仓库信息")
    public R<Void> updateDataWarehouse(@RequestBody SystemDataDTO dto) {
        systemService.saveDataWarehouse(dto);
        return R.ok();
    }

    /**
     * 删除数据仓库
     */
    @DeleteMapping("/data-warehouse/{id}")
    @ApiOperation(value = "删除数据仓库", notes = "根据ID删除数据仓库记录")
    public R<Void> deleteDataWarehouse(@PathVariable Long id) {
        systemService.deleteDataWarehouse(id);
        return R.ok();
    }

    /**
     * 分页查询企业信息列表
     */
    @GetMapping("/enterprise-info")
    @ApiOperation(value = "分页查询企业信息列表", notes = "支持按关键字、区域、园区、状态筛选")
    public R<PageResult<EnterpriseInfo>> getEnterpriseInfoPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String parkId,
            @RequestParam(required = false) String status) {

        IPage<EnterpriseInfo> page = systemService.getEnterpriseInfoPage(pageNum, pageSize, keyword, district, parkId, status);
        PageResult<EnterpriseInfo> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 根据ID查询企业信息
     */
    @GetMapping("/enterprise-info/{id}")
    @ApiOperation(value = "查询企业信息详情", notes = "根据ID查询企业详细信息")
    public R<EnterpriseInfo> getEnterpriseInfoById(@PathVariable Long id) {
        EnterpriseInfo enterpriseInfo = systemService.getEnterpriseInfoById(id);
        return R.ok(enterpriseInfo);
    }

    /**
     * 新增或更新企业信息
     */
    @PostMapping("/enterprise-info")
    @ApiOperation(value = "新增企业信息", notes = "创建新的企业信息")
    public R<Void> saveEnterpriseInfo(@RequestBody EnterpriseInfoDTO dto) {
        systemService.saveEnterpriseInfo(dto);
        return R.ok();
    }

    /**
     * 更新企业信息
     */
    @PutMapping("/enterprise-info")
    @ApiOperation(value = "更新企业信息", notes = "更新企业信息")
    public R<Void> updateEnterpriseInfo(@RequestBody EnterpriseInfoDTO dto) {
        systemService.saveEnterpriseInfo(dto);
        return R.ok();
    }
}