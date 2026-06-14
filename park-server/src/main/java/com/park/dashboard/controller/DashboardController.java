package com.park.dashboard.controller;

import com.park.auth.entity.SysUser;
import com.park.auth.service.AuthService;
import com.park.common.result.R;
import com.park.dashboard.dto.DashboardStatsDTO;
import com.park.dashboard.dto.MonthlyStatsDTO;
import com.park.dashboard.dto.ParkRankDTO;
import com.park.dashboard.service.DashboardService;
import com.park.system.entity.DistrictInfo;
import com.park.system.mapper.DistrictMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 数据驾驶舱控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@Api(tags = "数据驾驶舱")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthService authService;

    @Autowired
    private DistrictMapper districtMapper;

    /**
     * 获取统计数据
     * 根据当前登录用户的角色类型，返回不同范围的统计数据：
     * - 1（市级）：全市数据
     * - 2（区县）：当前区县数据
     * - 3（园区）：当前园区数据
     *
     * @param request HTTP 请求
     * @return 统计数据
     */
    @GetMapping("/stats")
    @ApiOperation(value = "获取统计数据", notes = "根据用户角色返回不同范围的统计数据")
    public R<DashboardStatsDTO> getStats(HttpServletRequest request) {
        UserScope scope = resolveUserScope(request);
        DashboardStatsDTO stats = dashboardService.getStats(scope.districtName, scope.parkId);
        return R.ok(stats);
    }

    /**
     * 获取园区排名（TOP N）
     *
     * @param limit   返回数量，默认 10
     * @param request HTTP 请求
     * @return 园区排名列表
     */
    @GetMapping("/top-parks")
    @ApiOperation(value = "获取园区排名", notes = "按评价得分降序排列的园区排名")
    public R<List<ParkRankDTO>> getTopParks(
            @ApiParam(value = "返回数量", example = "10") @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {
        UserScope scope = resolveUserScope(request);
        List<ParkRankDTO> topParks = dashboardService.getTopParks(limit, scope.districtName, scope.parkId);
        return R.ok(topParks);
    }

    /**
     * 获取季度统计数据
     *
     * @param year    年份，默认当前年
     * @param request HTTP 请求
     * @return 季度统计列表（4个季度）
     */
    @GetMapping("/monthly-stats")
    @ApiOperation(value = "获取季度统计", notes = "指定年份每季度的运营数据汇总")
    public R<List<MonthlyStatsDTO>> getMonthlyStats(
            @ApiParam(value = "年份", example = "2026") @RequestParam(defaultValue = "2026") int year,
            HttpServletRequest request) {
        UserScope scope = resolveUserScope(request);
        List<MonthlyStatsDTO> quarterlyStats = dashboardService.getQuarterlyStats(year, scope.districtName, scope.parkId);
        return R.ok(quarterlyStats);
    }

    // ==================== 私有方法 ====================

    /**
     * 解析当前用户的作用范围
     * 根据用户角色类型确定数据查询的范围（区县、园区）
     *
     * @param request HTTP 请求
     * @return 用户作用范围
     */
    private UserScope resolveUserScope(HttpServletRequest request) {
        // 从 JWT 过滤器设置的请求属性中获取用户信息
        Object userIdObj = request.getAttribute("userId");
        Long userId = null;
        if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        }

        if (userId == null) {
            log.warn("无法从请求中获取用户ID");
            return new UserScope(null, null);
        }

        SysUser user = authService.getUserById(userId);
        if (user == null) {
            log.warn("用户不存在：userId={}", userId);
            return new UserScope(null, null);
        }

        Integer roleType = user.getRoleType();
        String districtName = null;
        Long parkId = null;

        if (roleType == 2) {
            // 区县管理员：只看本区县数据
            // 需要通过 districtId 查询区县名称
            districtName = getDistrictNameById(user.getDistrictId());
        } else if (roleType == 3) {
            // 园区管理员：只看本园区数据
            parkId = user.getParkId();
        }
        // roleType == 1（市级管理员）：districtName 和 parkId 都为 null，查看全市数据

        return new UserScope(districtName, parkId);
    }

    /**
     * 根据区县ID获取区县名称
     */
    private String getDistrictNameById(Long districtId) {
        if (districtId == null) return null;
        DistrictInfo district = districtMapper.selectById(districtId);
        return district != null ? district.getName() : null;
    }

    /**
     * 用户数据作用范围（内部类）
     */
    private static class UserScope {
        final String districtName;
        final Long parkId;

        UserScope(String districtName, Long parkId) {
            this.districtName = districtName;
            this.parkId = parkId;
        }
    }
}
