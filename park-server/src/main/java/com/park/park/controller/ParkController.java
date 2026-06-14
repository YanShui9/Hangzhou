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
