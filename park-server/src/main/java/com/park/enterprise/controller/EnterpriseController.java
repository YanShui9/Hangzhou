package com.park.enterprise.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.auth.entity.SysUser;
import com.park.auth.service.AuthService;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.enterprise.dto.EnterpriseHonorSummaryQueryDTO;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
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
     * 新增企业
     *
     * @param saveDTO 企业信息
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation(value = "新增企业", notes = "新增入驻企业信息")
    public R<Void> saveEnterprise(@Valid @RequestBody EnterpriseSaveDTO saveDTO) {
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
    public R<Void> updateEnterprise(@PathVariable Long id, @Valid @RequestBody EnterpriseSaveDTO saveDTO) {
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
            @ApiParam(value = "企业ID", required = true) @PathVariable Long id) {
        log.info("删除企业：id={}", id);
        enterpriseService.deleteEnterprise(id);
        return R.ok("删除成功", null);
    }

    /**
     * 查询企业荣誉数量统计汇总表（分页）
     * 数据来源：enterprise_honor_record 表按园区聚合，关联 park_info 和 enterprise_info。
     * 数据权限：市级看全部，区县看本区县，园区看本园区。
     *
     * @param queryDTO 查询条件
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping("/honor/summary")
    @ApiOperation(value = "查询企业荣誉数量统计汇总表", notes = "按园区聚合企业荣誉数据，分页返回")
    public R<PageResult<Map<String, Object>>> getEnterpriseHonorSummary(EnterpriseHonorSummaryQueryDTO queryDTO,
                                                                        HttpServletRequest request) {
        applyHonorSummaryDataPermission(queryDTO, request);
        IPage<Map<String, Object>> page = enterpriseService.getEnterpriseHonorSummary(queryDTO);
        return R.ok(PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        ));
    }

    /**
     * 导出企业荣誉数量统计汇总表（按 parkType 分两个 Sheet）
     *
     * @param queryDTO 查询条件
     * @param request  HTTP请求
     * @param response HTTP响应
     */
    @GetMapping("/honor/summary/export")
    @ApiOperation(value = "导出企业荣誉数量统计汇总表", notes = "导出企业荣誉汇总Excel，按园区类型分Sheet")
    public void exportEnterpriseHonorSummary(EnterpriseHonorSummaryQueryDTO queryDTO,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws IOException {
        applyHonorSummaryDataPermission(queryDTO, request);
        byte[] data = enterpriseService.exportEnterpriseHonorSummary(queryDTO);
        String filename = (queryDTO.getYear() != null ? queryDTO.getYear() + "年度" : "")
                + "企业荣誉数量统计汇总表.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
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
                DistrictInfo district = districtMapper.selectById(user.getDistrictId());
                if (district != null) {
                    // 查询该区县下的所有园区ID
                    LambdaQueryWrapper<ParkInfo> parkQuery = new LambdaQueryWrapper<>();
                    parkQuery.eq(ParkInfo::getDistrictName, district.getDistrictName());
                    parkQuery.select(ParkInfo::getId);
                    List<ParkInfo> parks = parkMapper.selectList(parkQuery);
                    List<Long> parkIds = parks.stream().map(ParkInfo::getId).collect(Collectors.toList());

                    if (parkIds.isEmpty()) {
                        // 该区县没有园区，设置一个不可能的ID
                        queryDTO.setParkId(-1L);
                    } else {
                        // 使用parkIds进行IN查询，支持多园区
                        queryDTO.setParkIds(parkIds);
                    }
                }
            }
        } else if (roleType == 3) {
            // 园区管理员：只查看本园区的企业
            if (user.getParkId() != null) {
                queryDTO.setParkId(user.getParkId());
            }
        }
    }

    /**
     * 企业荣誉汇总数据权限控制
     * - 市级管理员（roleType=1）：查看所有
     * - 区县管理员（roleType=2）：只查看本区县的园区
     * - 园区管理员（roleType=3）：只查看本园区
     */
    private void applyHonorSummaryDataPermission(EnterpriseHonorSummaryQueryDTO queryDTO, HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || roleType == 1) {
            // 市级管理员：查看所有
            return;
        }

        Object userIdObj = request.getAttribute("userId");
        Long userId = null;
        if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        }
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        SysUser user = authService.getUserById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (roleType == 2) {
            // 区县管理员：查看本区县的园区
            if (user.getDistrictId() == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "区县管理员未分配区县");
            }
            DistrictInfo district = districtMapper.selectById(user.getDistrictId());
            if (district == null) {
                queryDTO.setParkId(-1L);
                return;
            }
            List<Long> parkIds = parkMapper.selectList(
                    new LambdaQueryWrapper<ParkInfo>()
                            .eq(ParkInfo::getDistrictName, district.getDistrictName())
                            .select(ParkInfo::getId)
            ).stream().map(ParkInfo::getId).collect(Collectors.toList());

            if (parkIds.isEmpty()) {
                queryDTO.setParkId(-1L);
            } else {
                queryDTO.setParkIds(parkIds);
            }
        } else if (roleType == 3) {
            // 园区管理员：查看本园区
            if (user.getParkId() == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "园区管理员未分配园区");
            }
            queryDTO.setParkId(user.getParkId());
        }
    }
}
