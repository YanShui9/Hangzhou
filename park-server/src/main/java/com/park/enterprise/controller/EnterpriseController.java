package com.park.enterprise.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.auth.entity.SysUser;
import com.park.auth.service.AuthService;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.alibaba.excel.EasyExcel;
import com.park.enterprise.dto.EnterpriseExportDTO;
import com.park.enterprise.dto.EnterpriseQueryDTO;
import com.park.enterprise.dto.EnterpriseSaveDTO;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.service.EnterpriseService;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import com.park.system.entity.DistrictInfo;
import com.park.system.mapper.DistrictMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 入驻企业控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/enterprises")
@Api(tags = "入驻企业管理")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private DistrictMapper districtMapper;

    /**
     * 分页查询企业列表
     *
     * @param queryDTO 查询条件
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping
    @ApiOperation(value = "分页查询企业列表", notes = "根据条件分页查询入驻企业列表")
    public R<PageResult<EnterpriseInfo>> getEnterprisePage(EnterpriseQueryDTO queryDTO, HttpServletRequest request) {
        // 数据权限控制
        applyDataPermission(queryDTO, request);

        log.info("查询企业列表：pageNum={}, pageSize={}, enterpriseName={}, parkId={}, industryName={}, status={}",
                queryDTO.getPageNum(), queryDTO.getPageSize(), queryDTO.getEnterpriseName(),
                queryDTO.getParkId(), queryDTO.getIndustryName(), queryDTO.getStatus());
        IPage<EnterpriseInfo> page = enterpriseService.getEnterprisePage(queryDTO);
        PageResult<EnterpriseInfo> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 根据ID查询企业详情
     *
     * @param id 企业ID
     * @return 企业详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询企业详情", notes = "根据企业ID查询企业详细信息")
    public R<EnterpriseInfo> getEnterpriseById(
            @ApiParam(value = "企业ID", required = true) @PathVariable Long id) {
        log.info("查询企业详情：id={}", id);
        EnterpriseInfo enterprise = enterpriseService.getEnterpriseById(id);
        return R.ok(enterprise);
    }

    /**
     * 查询企业详情（园区端视图，字段名适配前端）
     */
    @GetMapping("/park/{id}")
    @ApiOperation(value = "查询企业详情(园区端)", notes = "返回适配园区端前端字段名的企业详情")
    public R<Map<String, Object>> getEnterpriseForPark(@PathVariable Long id) {
        EnterpriseInfo e = enterpriseService.getEnterpriseById(id);
        if (e == null) {
            return R.ok(java.util.Collections.emptyMap());
        }
        Map<String, Object> vo = new java.util.LinkedHashMap<>();
        vo.put("enterpriseName", e.getEnterpriseName());
        vo.put("creditCode", e.getCreditCode());
        // 适配本地实体：address -> enterpriseAddress
        vo.put("address", e.getEnterpriseAddress());
        vo.put("registeredAddress", e.getEnterpriseAddress());
        vo.put("industry", e.getIndustryName());
        vo.put("registerStatus", e.getStatus());
        // 适配本地实体：entryDate -> settledTime
        vo.put("entryStartTime", e.getSettledTime());
        vo.put("legalPerson", e.getLegalPerson());
        vo.put("contactName", e.getContactName());
        vo.put("contactPhone", e.getContactPhone());
        vo.put("registeredCapital", e.getRegisteredCapital());
        vo.put("registerDate", e.getRegisterDate());
        vo.put("businessScope", e.getBusinessScope());
        if (e.getParkId() != null) {
            ParkInfo park = parkMapper.selectById(e.getParkId());
            if (park != null) {
                vo.put("parkName", park.getParkName());
                vo.put("districtName", park.getDistrictName());
            }
        }
        return R.ok(vo);
    }

    /**
     * 新增企业
     *
     * @param saveDTO 企业信息
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation(value = "新增企业", notes = "新增入驻企业信息")
    public R<Void> saveEnterprise(@Valid @RequestBody EnterpriseSaveDTO saveDTO, HttpServletRequest request) {
        checkLogin(request); // 登录校验
        log.info("新增企业：enterpriseName={}, parkId={}", saveDTO.getEnterpriseName(), saveDTO.getParkId());
        enterpriseService.saveEnterprise(saveDTO);
        return R.ok("新增成功", null);
    }

    /**
     * 修改企业
     *
     * @param id      企业ID
     * @param saveDTO 企业信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改企业", notes = "修改入驻企业信息")
    public R<Void> updateEnterprise(@PathVariable Long id, @Valid @RequestBody EnterpriseSaveDTO saveDTO,
                                     HttpServletRequest request) {
        checkLogin(request); // 登录校验
        saveDTO.setId(id);
        log.info("修改企业：id={}, enterpriseName={}", id, saveDTO.getEnterpriseName());
        enterpriseService.updateEnterprise(saveDTO);
        return R.ok("修改成功", null);
    }

    /**
     * 删除企业
     *
     * @param id 企业ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除企业", notes = "根据企业ID删除企业")
    public R<Void> deleteEnterprise(
            @ApiParam(value = "企业ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        checkLogin(request);

        // 数据权限校验：园区管理员只能删除本园区的企业
        Object roleTypeObj = request.getAttribute("roleType");
        Integer roleType = (roleTypeObj instanceof Integer) ? (Integer) roleTypeObj : null;
        if (roleType != null && roleType == 3) {
            Object userIdObj = request.getAttribute("userId");
            Long userId = null;
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            }
            if (userId != null) {
                SysUser user = authService.getUserById(userId);
                if (user != null && user.getParkId() != null) {
                    EnterpriseInfo enterprise = enterpriseService.getEnterpriseById(id);
                    if (enterprise == null) {
                        throw new BusinessException(ResultCode.DATA_NOT_FOUND, "企业不存在");
                    }
                    if (!user.getParkId().equals(enterprise.getParkId())) {
                        throw new BusinessException(ResultCode.FORBIDDEN, "无权操作其他园区的企业数据");
                    }
                }
            }
        }

        log.info("删除企业：id={}", id);
        enterpriseService.deleteEnterprise(id);
        return R.ok("删除成功", null);
    }

    // ===================================================================
    // 指标 / 荣誉 / 导出
    // ===================================================================

    /**
     * 导出企业列表（Excel）
     */
    @GetMapping("/export")
    @ApiOperation(value = "导出企业列表", notes = "按查询条件导出 Excel")
    public void exportEnterprises(EnterpriseQueryDTO queryDTO,
                                  HttpServletRequest request,
                                  javax.servlet.http.HttpServletResponse response) {
        applyDataPermission(queryDTO, request);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10000);
        try {
            IPage<EnterpriseInfo> page = enterpriseService.getEnterprisePage(queryDTO);
            List<EnterpriseExportDTO> exportList = new java.util.ArrayList<>();
            int index = 1;
            for (EnterpriseInfo e : page.getRecords()) {
                EnterpriseExportDTO dto = new EnterpriseExportDTO();
                dto.setIndex(index++);
                dto.setEnterpriseName(e.getEnterpriseName());
                dto.setCreditCode(e.getCreditCode());
                dto.setDistrictName(e.getDistrictName());
                // 查询园区名称
                if (e.getParkId() != null) {
                    ParkInfo p = parkMapper.selectById(e.getParkId());
                    if (p != null) {
                        dto.setParkName(p.getParkName());
                        if (dto.getDistrictName() == null) {
                            dto.setDistrictName(p.getDistrictName());
                        }
                    }
                }
                dto.setEnterpriseHonor(e.getEnterpriseHonor());
                dto.setIsParticipateText(e.getIsParticipate() != null ? (e.getIsParticipate() == 1 ? "参评" : "不参评") : "");
                dto.setLegalPerson(e.getLegalPerson());
                dto.setContactName(e.getContactName());
                dto.setContactPhone(e.getContactPhone());
                dto.setStatus(e.getStatus());
                exportList.add(dto);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode("入驻企业列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), EnterpriseExportDTO.class).sheet("企业列表").doWrite(exportList);
        } catch (Exception ex) {
            throw new com.park.common.exception.BusinessException(
                    com.park.common.result.ResultCode.FAILURE, "导出失败：" + ex.getMessage());
        }
    }

    /**
     * 企业荣誉数量统计汇总表
     */
    @GetMapping("/honor/summary")
    @ApiOperation(value = "企业荣誉数量统计汇总表", notes = "按园区维度统计企业荣誉数量")
    public R<PageResult<java.util.Map<String, Object>>> getEnterpriseHonorSummary(EnterpriseQueryDTO queryDTO,
                                                                                   @RequestParam(required = false) String region,
                                                                                   @RequestParam(required = false) String type,
                                                                                   HttpServletRequest request) {
        applyDataPermission(queryDTO, request);
        // 简化实现：按 park 维度聚合
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ParkInfo> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ParkInfo> w =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        // 园区名称模糊搜索
        if (org.springframework.util.StringUtils.hasText(queryDTO.getParkName())) {
            w.like(ParkInfo::getParkName, queryDTO.getParkName());
        }
        // 区域筛选
        if (org.springframework.util.StringUtils.hasText(region)) {
            w.eq(ParkInfo::getDistrictName, region);
        }
        // 园区类型筛选
        if (org.springframework.util.StringUtils.hasText(type)) {
            w.like(ParkInfo::getParkType, type);
        }
        // 兼容旧参数：enterpriseName / districtName
        if (org.springframework.util.StringUtils.hasText(queryDTO.getEnterpriseName())) {
            w.like(ParkInfo::getParkName, queryDTO.getEnterpriseName());
        }
        if (org.springframework.util.StringUtils.hasText(queryDTO.getDistrictName())) {
            w.like(ParkInfo::getDistrictName, queryDTO.getDistrictName());
        }
        IPage<ParkInfo> parkPage = parkMapper.selectPage(page, w);
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        for (ParkInfo p : parkPage.getRecords()) {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", p.getId());
            m.put("parkName", p.getParkName());
            m.put("region", p.getDistrictName());
            m.put("parkType", p.getParkType());
            m.put("totalEnterprises", p.getEnterpriseCount() == null ? 0 : p.getEnterpriseCount());
            // 占位数据：与 enterprise_info 字段对齐
            m.put("existingAboveScale", p.getAboveScaleCount() == null ? 0 : p.getAboveScaleCount());
            m.put("newAboveScale", 0);
            m.put("retiredAboveScale", 0);
            m.put("newSpecialtyGiant", 0);
            m.put("newProvincialHiddenChampion", p.getHiddenChampionCount() == null ? 0 : p.getHiddenChampionCount());
            m.put("newSpecialtySME", p.getTechSmeCount() == null ? 0 : p.getTechSmeCount());
            m.put("newSingleChampion", 0);
            m.put("newIPO", p.getListedCount() == null ? 0 : p.getListedCount());
            m.put("newNationalHighTech", p.getHighTechCount() == null ? 0 : p.getHighTechCount());
            m.put("innovativeSME", p.getInnovativeSmeCount() == null ? 0 : p.getInnovativeSmeCount());
            m.put("newProvincialTechSmall", p.getProvincialSpecializedCount() == null ? 0 : p.getProvincialSpecializedCount());
            m.put("earlyInvestInnovation", 0);
            m.put("newFirstEquipment", 0);
            m.put("firstVersion", 0);
            m.put("firstBatch", 0);
            m.put("provincialExcellentIndustrial", 0);
            m.put("zhejiangMadeQuality", 0);
            m.put("newNationalRDAgency", p.getNationalSpecializedCount() == null ? 0 : p.getNationalSpecializedCount());
            m.put("newProvincialRDAgency", 0);
            m.put("newMunicipalRDAgency", 0);
            m.put("publicServicePlatform", 0);
            m.put("enterpriseIncubator", 0);
            m.put("talentAClass", 0);
            m.put("talentBClass", 0);
            m.put("talentCClass", 0);
            list.add(m);
        }
        return R.ok(PageResult.of(list, parkPage.getTotal(),
                (int) parkPage.getCurrent(), (int) parkPage.getSize()));
    }

    /**
     * 企业指标列表
     */
    @GetMapping("/indicators")
    @ApiOperation(value = "企业指标列表", notes = "按园区维度查询企业指标")
    public R<PageResult<java.util.Map<String, Object>>> getEnterpriseIndicatorList(EnterpriseQueryDTO queryDTO,
                                                                                    HttpServletRequest request) {
        return getEnterpriseHonorSummary(queryDTO, null, null, request);
    }

    /**
     * 导出企业指标列表
     */
    @GetMapping("/indicators/export")
    @ApiOperation(value = "导出企业指标列表", notes = "按查询条件导出 Excel")
    public void exportEnterpriseIndicators(EnterpriseQueryDTO queryDTO,
                                           HttpServletRequest request,
                                           javax.servlet.http.HttpServletResponse response) {
        applyDataPermission(queryDTO, request);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10000);
        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<ParkInfo> page =
                    new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10000);
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ParkInfo> w =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            if (org.springframework.util.StringUtils.hasText(queryDTO.getEnterpriseName())) {
                w.like(ParkInfo::getParkName, queryDTO.getEnterpriseName());
            } else if (org.springframework.util.StringUtils.hasText(queryDTO.getDistrictName())) {
                w.like(ParkInfo::getDistrictName, queryDTO.getDistrictName());
            }
            IPage<ParkInfo> parkPage = parkMapper.selectPage(page, w);
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=enterprise_indicators.xls");
            StringBuilder sb = new StringBuilder("id,parkName,region,totalEnterprises,aboveScaleCount,highTechCount,listedCount,hiddenChampionCount\n");
            for (ParkInfo p : parkPage.getRecords()) {
                sb.append(p.getId()).append(",")
                  .append(p.getParkName() == null ? "" : p.getParkName()).append(",")
                  .append(p.getDistrictName() == null ? "" : p.getDistrictName()).append(",")
                  .append(p.getEnterpriseCount() == null ? 0 : p.getEnterpriseCount()).append(",")
                  .append(p.getAboveScaleCount() == null ? 0 : p.getAboveScaleCount()).append(",")
                  .append(p.getHighTechCount() == null ? 0 : p.getHighTechCount()).append(",")
                  .append(p.getListedCount() == null ? 0 : p.getListedCount()).append(",")
                  .append(p.getHiddenChampionCount() == null ? 0 : p.getHiddenChampionCount()).append("\n");
            }
            response.getOutputStream().write(sb.toString().getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new com.park.common.exception.BusinessException(
                    com.park.common.result.ResultCode.FAILURE, "导出失败：" + ex.getMessage());
        }
    }

    /**
     * 登录校验：任意已登录用户均可通过
     */
    private void checkLogin(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        if (!(roleTypeObj instanceof Integer)) {
            throw new com.park.common.exception.BusinessException(
                    com.park.common.result.ResultCode.FORBIDDEN, "无权限");
        }
    }

    /**
     * 应用数据权限
     * - 市级管理员（roleType=1）：查看所有数据
     * - 区县管理员（roleType=2）：只查看本区县的企业
     * - 园区管理员（roleType=3）：只查看本园区的企业
     */
    private void applyDataPermission(EnterpriseQueryDTO queryDTO, HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        Integer roleType = (roleTypeObj instanceof Integer) ? (Integer) roleTypeObj : null;

        if (roleType == null || roleType == 1) {
            // 市级管理员：查看所有数据，不做限制
            return;
        }

        // 获取当前用户信息
        Object userIdObj = request.getAttribute("userId");
        Long userId = null;
        if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        }

        if (userId == null) {
            return;
        }

        SysUser user = authService.getUserById(userId);
        if (user == null) {
            return;
        }

        if (roleType == 2) {
            // 区县管理员：只查看本区县的企业
            if (user.getDistrictId() != null) {
                LambdaQueryWrapper<ParkInfo> parkQuery = new LambdaQueryWrapper<>();
                parkQuery.eq(ParkInfo::getDistrictId, user.getDistrictId());
                parkQuery.select(ParkInfo::getId);
                List<ParkInfo> parks = parkMapper.selectList(parkQuery);
                List<Long> parkIds = parks.stream().map(ParkInfo::getId).collect(Collectors.toList());

                if (parkIds.isEmpty()) {
                    DistrictInfo district = districtMapper.selectById(user.getDistrictId());
                    if (district != null && district.getDistrictName() != null) {
                        LambdaQueryWrapper<ParkInfo> nameQuery = new LambdaQueryWrapper<>();
                        nameQuery.eq(ParkInfo::getDistrictName, district.getDistrictName());
                        nameQuery.select(ParkInfo::getId);
                        parks = parkMapper.selectList(nameQuery);
                        parkIds = parks.stream().map(ParkInfo::getId).collect(Collectors.toList());
                        log.info("区县管理员查询企业：按 district_id 未找到园区，尝试按 district_name={} 查询，找到园区数={}", 
                                district.getDistrictName(), parks.size());
                    }
                }

                log.info("区县管理员查询企业：userId={}, districtId={}, 查询到园区数={}, parkIds={}", 
                        userId, user.getDistrictId(), parks.size(), parkIds);
                queryDTO.setParkIds(parkIds);
            } else {
                log.warn("区县管理员查询企业：userId={}, districtId为空", userId);
            }
        } else if (roleType == 3) {
            // 园区管理员：只查看本园区的企业
            if (user.getParkId() != null) {
                queryDTO.setParkId(user.getParkId());
            }
        }
    }
}
