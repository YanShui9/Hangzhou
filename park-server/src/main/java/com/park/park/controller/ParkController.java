package com.park.park.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.auth.entity.SysUser;
import com.park.auth.service.AuthService;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.park.dto.ParkQueryDTO;
import com.park.park.dto.ParkSaveDTO;
import com.park.park.dto.ParkStatsDTO;
import com.park.park.dto.TotalStatsDTO;
import com.park.park.entity.ParkInfo;
import com.park.park.service.ParkService;
import com.park.system.entity.DistrictInfo;
import com.park.system.mapper.DistrictMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 园区控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/parks")
@Api(tags = "园区管理")
public class ParkController {

    @Autowired
    private ParkService parkService;

    @Autowired
    private AuthService authService;

    @Autowired
    private DistrictMapper districtMapper;

    /**
     * 分页查询园区列表
     *
     * @param queryDTO 查询条件
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping
    @ApiOperation(value = "分页查询园区列表", notes = "支持按名称、区县、状态筛选")
    public R<PageResult<ParkInfo>> getParkPage(ParkQueryDTO queryDTO, HttpServletRequest request) {
        // 数据权限控制
        applyDataPermission(queryDTO, request);

        IPage<ParkInfo> page = parkService.getParkPage(queryDTO);
        PageResult<ParkInfo> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 根据ID查询园区详情
     *
     * @param id 园区ID
     * @return 园区信息
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询园区详情", notes = "根据园区ID查询详细信息")
    public R<ParkInfo> getParkById(@PathVariable Long id) {
        ParkInfo parkInfo = parkService.getParkById(id);
        return R.ok(parkInfo);
    }

    /**
     * 获取园区统计数据
     *
     * @param id 园区ID
     * @return 园区统计数据
     */
    @GetMapping("/{id}/stats")
    @ApiOperation(value = "获取园区统计数据", notes = "根据园区ID查询统计数据（企业、人才、专利等）")
    public R<ParkStatsDTO> getParkStats(@PathVariable Long id) {
        ParkStatsDTO stats = parkService.getParkStats(id);
        return R.ok(stats);
    }

    /**
     * 获取全市园区统计数据
     * 用于前端"园区列表"页面顶部统计卡片
     *
     * @return 全市统计数据
     */
    @GetMapping("/stats")
    @ApiOperation(value = "获取全市园区统计数据", notes = "用于园区列表页面顶部统计卡片")
    public R<TotalStatsDTO> getTotalStats() {
        TotalStatsDTO stats = parkService.getTotalStats();
        return R.ok(stats);
    }

    /**
     * 新增园区
     *
     * @param saveDTO 园区信息
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation(value = "新增园区", notes = "创建新的园区信息")
    public R<Void> savePark(@Valid @RequestBody ParkSaveDTO saveDTO) {
        parkService.savePark(saveDTO);
        return R.ok();
    }

    /**
     * 修改园区
     *
     * @param id      园区ID
     * @param saveDTO 园区信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改园区", notes = "更新园区信息")
    public R<Void> updatePark(@PathVariable Long id, @Valid @RequestBody ParkSaveDTO saveDTO) {
        saveDTO.setId(id);
        parkService.updatePark(saveDTO);
        return R.ok();
    }

    /**
     * 删除园区
     *
     * @param id 园区ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除园区", notes = "根据园区ID删除园区")
    public R<Void> deletePark(@PathVariable Long id) {
        parkService.deletePark(id);
        return R.ok();
    }

    /**
     * 批量删除园区
     */
    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除园区", notes = "根据ID数组批量删除")
    public R<Void> batchDeletePark(@RequestBody java.util.List<Long> ids) {
        parkService.batchDeletePark(ids);
        return R.ok();
    }

    /**
     * 批量导入园区
     */
    @PostMapping("/import")
    @ApiOperation(value = "批量导入园区", notes = "上传 Excel 批量创建")
    public R<java.util.Map<String, Object>> importParks(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                                         HttpServletRequest request) {
        checkAdmin(request);
        java.util.Map<String, Object> result = parkService.importParks(file);
        return R.ok(result);
    }

    /**
     * 下载园区导入模板
     */
    @GetMapping("/template")
    @ApiOperation(value = "下载园区导入模板", notes = "返回 Excel 模板")
    public void downloadTemplate(javax.servlet.http.HttpServletResponse response) {
        parkService.downloadTemplate(response);
    }

    /**
     * 导出园区列表
     */
    @GetMapping("/export")
    @ApiOperation(value = "导出园区列表", notes = "按查询条件导出 Excel")
    public void exportParks(ParkQueryDTO queryDTO,
                            HttpServletRequest request,
                            javax.servlet.http.HttpServletResponse response) {
        applyDataPermission(queryDTO, request);
        parkService.exportParks(queryDTO, response);
    }

    /**
     * 园区端编辑季度运营数据
     */
    @PutMapping("/{id}/operation")
    @ApiOperation(value = "园区端编辑季度运营数据", notes = "园区管理员编辑季度运营数据，回写到 park_info")
    public R<Void> updateOperation(@PathVariable Long id,
                                    @RequestBody java.util.Map<String, Object> data,
                                    HttpServletRequest request) {
        checkParkAdmin(request, id);
        parkService.updateOperation(id, data);
        return R.ok();
    }

    /**
     * 获取园区季度填报状态
     */
    @GetMapping("/{id}/quarter-status")
    @ApiOperation(value = "获取园区季度填报状态", notes = "返回年度各季度填报状态")
    public R<java.util.List<java.util.Map<String, Object>>> getQuarterStatus(@PathVariable Long id,
                                                                               @RequestParam(required = false) Integer year) {
        return R.ok(parkService.getQuarterStatus(id, year));
    }

    /**
     * 获取园区主要产业（企业数量前三）
     */
    @GetMapping("/{id}/top-industries")
    @ApiOperation(value = "获取园区主要产业", notes = "返回园区企业数量前三的产业，用于详情页展示")
    public R<java.util.List<com.park.park.dto.ParkIndustryStatDTO>> getTopIndustries(@PathVariable Long id) {
        return R.ok(parkService.getTopIndustries(id));
    }

    private void checkAdmin(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        Integer roleType = (roleTypeObj instanceof Integer) ? (Integer) roleTypeObj : null;
        if (roleType == null || roleType != 1) {
            throw new com.park.common.exception.BusinessException(
                    com.park.common.result.ResultCode.FORBIDDEN, "仅市级管理员可操作");
        }
    }

    private void checkParkAdmin(HttpServletRequest request, Long parkId) {
        Object roleTypeObj = request.getAttribute("roleType");
        Integer roleType = (roleTypeObj instanceof Integer) ? (Integer) roleTypeObj : null;
        if (roleType == null || roleType != 3) {
            throw new com.park.common.exception.BusinessException(
                    com.park.common.result.ResultCode.FORBIDDEN, "仅园区管理员可操作");
        }
        Object userIdObj = request.getAttribute("userId");
        Long userId = userIdObj instanceof Long ? (Long) userIdObj : (userIdObj instanceof Integer ? ((Integer) userIdObj).longValue() : null);
        com.park.auth.entity.SysUser user = authService.getUserById(userId);
        if (user == null || user.getParkId() == null || !user.getParkId().equals(parkId)) {
            throw new com.park.common.exception.BusinessException(
                    com.park.common.result.ResultCode.FORBIDDEN, "无权操作该园区");
        }
    }

    /**
     * 应用数据权限
     * - 市级管理员（roleType=1）：查看所有数据
     * - 区县管理员（roleType=2）：只查看本区县数据
     * - 园区管理员（roleType=3）：只查看本园区数据
     */
    private void applyDataPermission(ParkQueryDTO queryDTO, HttpServletRequest request) {
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
            // 区县管理员：只查看本区县数据
            if (user.getDistrictId() != null) {
                DistrictInfo district = districtMapper.selectById(user.getDistrictId());
                if (district != null) {
                    queryDTO.setDistrictName(district.getDistrictName());
                }
            }
        } else if (roleType == 3) {
            // 园区管理员：只查看本园区数据
            if (user.getParkId() != null) {
                queryDTO.setId(user.getParkId());
            }
        }
    }
}
