package com.park.audit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.audit.dto.AuditDTO;
import com.park.audit.entity.AuditRecord;
import com.park.audit.service.AuditService;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 审核控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/audits")
@Api(tags = "审核管理")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @Autowired
    private ParkMapper parkMapper;

    /**
     * 获取审核列表（待审核 + 已审核）
     * 支持多条件筛选：园区名称、园区类型、审核状态、参评状态、评价年份
     *
     * @param pageNum          页码
     * @param pageSize         每页数量
     * @param status           审核状态筛选（可选）
     * @param name             园区名称（模糊匹配）
     * @param parkType         园区类型（数字：1=制造类, 2=服务类）
     * @param evaluationStatus 参评状态（1=参评, 0=不参评）
     * @param evaluationYear   评价年份
     * @param request          HTTP请求
     * @return 分页结果（含关联字段 parkName/districtName/parkType/evaluationStatus/evaluationYear）
     */
    @GetMapping
    @ApiOperation(value = "查询审核列表", notes = "查询待审核和已审核的评价记录列表，支持多条件筛选")
    public R<PageResult<Map<String, Object>>> getAuditList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer parkType,
            @RequestParam(required = false) Integer evaluationStatus,
            @RequestParam(required = false) Integer evaluationYear,
            HttpServletRequest request) {
        Integer roleType = getRoleType(request);
        Integer auditLevel = getAuditLevel(roleType);

        // 1. 如果有园区名称或园区类型筛选，先查park_info获取parkIds
        List<Long> filteredParkIds = null;
        if (StringUtils.hasText(name) || parkType != null) {
            LambdaQueryWrapper<ParkInfo> parkWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(name)) {
                parkWrapper.like(ParkInfo::getParkName, name);
            }
            if (parkType != null) {
                // 前端传数字：1=制造类, 2=服务类；后端存字符串
                String parkTypeStr = (parkType == 1) ? "制造类" : "服务类";
                parkWrapper.eq(ParkInfo::getParkType, parkTypeStr);
            }
            parkWrapper.select(ParkInfo::getId);
            List<ParkInfo> parks = parkMapper.selectList(parkWrapper);
            filteredParkIds = parks.stream().map(ParkInfo::getId).collect(Collectors.toList());
            if (filteredParkIds.isEmpty()) {
                // 没有匹配的园区，直接返回空
                return R.ok(PageResult.of(new ArrayList<>(), 0, pageNum, pageSize));
            }
        }

        // 2. 查询评价记录（带筛选条件）
        Page<EvaluationRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();

        // 审核状态筛选
        if (StringUtils.hasText(status)) {
            try {
                int statusVal = Integer.parseInt(status);
                wrapper.eq(EvaluationRecord::getStatus, statusVal);
            } catch (NumberFormatException e) {
                // status可能是 pending/audited 字符串
                applyStatusFilter(wrapper, status, auditLevel);
            }
        } else {
            // 默认按角色显示相关状态
            applyDefaultStatusFilter(wrapper, auditLevel);
        }

        // 参评状态筛选
        if (evaluationStatus != null) {
            wrapper.eq(EvaluationRecord::getEvaluationStatus, evaluationStatus);
        }

        // 评价年份筛选
        if (evaluationYear != null) {
            wrapper.eq(EvaluationRecord::getYear, evaluationYear);
        }

        // 园区ID列表筛选
        if (filteredParkIds != null) {
            wrapper.in(EvaluationRecord::getParkId, filteredParkIds);
        }

        wrapper.orderByDesc(EvaluationRecord::getCreateTime);
        IPage<EvaluationRecord> recordPage = auditService.getAuditPageWithWrapper(page, wrapper);

        // 3. 关联park_info补充字段
        List<EvaluationRecord> records = recordPage.getRecords();
        List<Long> parkIds = records.stream()
                .map(EvaluationRecord::getParkId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ParkInfo> parkMap = new HashMap<>();
        if (!parkIds.isEmpty()) {
            List<ParkInfo> parks = parkMapper.selectBatchIds(parkIds);
            for (ParkInfo p : parks) parkMap.put(p.getId(), p);
        }

        // 4. 构建返回VO
        List<Map<String, Object>> voList = new ArrayList<>();
        for (EvaluationRecord r : records) {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", r.getId());
            vo.put("parkId", r.getParkId());
            vo.put("evaluationYear", r.getYear());
            vo.put("auditStatus", r.getStatus());
            vo.put("evaluationStatus", r.getEvaluationStatus() != null ? r.getEvaluationStatus() : 1);
            vo.put("totalScore", r.getTotalScore());
            vo.put("grade", r.getGrade());
            vo.put("createTime", r.getCreateTime());
            ParkInfo park = parkMap.get(r.getParkId());
            if (park != null) {
                vo.put("parkName", park.getParkName());
                vo.put("districtName", park.getDistrictName());
                // parkType字符串转数字：制造类→1, 服务类→2
                String pt = park.getParkType();
                Integer ptNum = null;
                if ("制造类".equals(pt)) ptNum = 1;
                else if ("服务类".equals(pt)) ptNum = 2;
                vo.put("parkType", ptNum);
            }
            voList.add(vo);
        }

        PageResult<Map<String, Object>> pageResult = PageResult.of(
                voList,
                recordPage.getTotal(),
                (int) recordPage.getCurrent(),
                (int) recordPage.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 应用状态筛选（pending/audited字符串）
     */
    private void applyStatusFilter(LambdaQueryWrapper<EvaluationRecord> wrapper, String status, Integer auditLevel) {
        if (auditLevel == 2) {
            // 区县审核：1=待区县审, 2=待市局审, 3=通过, 4=驳回
            if ("pending".equals(status)) {
                wrapper.eq(EvaluationRecord::getStatus, 1);
            } else if ("audited".equals(status)) {
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(2, 3, 4));
            } else {
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(1, 2, 3, 4));
            }
        } else {
            // 市级审核
            if ("pending".equals(status)) {
                wrapper.eq(EvaluationRecord::getStatus, 2);
            } else if ("audited".equals(status)) {
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(3, 4));
            } else {
                wrapper.in(EvaluationRecord::getStatus, Arrays.asList(2, 3, 4));
            }
        }
    }

    /**
     * 默认状态筛选（按角色显示相关状态）
     */
    private void applyDefaultStatusFilter(LambdaQueryWrapper<EvaluationRecord> wrapper, Integer auditLevel) {
        if (auditLevel == 2) {
            // 区县端显示：1=待区县审, 2=待市局审(区县已通过), 3=通过, 4=驳回
            wrapper.in(EvaluationRecord::getStatus, Arrays.asList(1, 2, 3, 4));
        } else {
            wrapper.in(EvaluationRecord::getStatus, Arrays.asList(2, 3, 4));
        }
    }

    /**
     * 获取待审核列表
     */
    @GetMapping("/pending")
    @ApiOperation(value = "查询待审核列表", notes = "查询待当前用户审核的评价记录列表")
    public R<PageResult<EvaluationRecord>> getPendingAuditList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Integer roleType = getRoleType(request);
        Integer auditLevel = getAuditLevel(roleType);

        IPage<EvaluationRecord> page = auditService.getPendingAuditPage(pageNum, pageSize, auditLevel);
        PageResult<EvaluationRecord> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 获取已审核列表
     */
    @GetMapping("/audited")
    @ApiOperation(value = "查询已审核列表", notes = "查询当前用户已审核的评价记录列表")
    public R<PageResult<EvaluationRecord>> getAuditedList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Integer roleType = getRoleType(request);
        Integer auditLevel = getAuditLevel(roleType);

        IPage<EvaluationRecord> page = auditService.getAuditedPage(pageNum, pageSize, auditLevel);
        PageResult<EvaluationRecord> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 提交审核（通过/驳回）
     */
    @PostMapping
    @ApiOperation(value = "提交审核", notes = "对评价记录进行审核（通过/驳回）")
    public R<Void> audit(@Valid @RequestBody AuditDTO auditDTO, HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        Long auditorId = null;
        if (userIdObj instanceof Integer) {
            auditorId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            auditorId = (Long) userIdObj;
        }

        Integer roleType = getRoleType(request);
        if (auditorId == null) {
            return R.fail("无法获取审核人信息");
        }

        auditService.audit(auditDTO, auditorId, roleType);
        return R.ok();
    }

    /**
     * 查询某条评价记录的审核历史
     */
    @GetMapping("/history/{evaluationId}")
    @ApiOperation(value = "查询审核历史", notes = "查询某条评价记录的所有审核记录")
    public R<List<AuditRecord>> getAuditHistory(@PathVariable Long evaluationId, HttpServletRequest request) {
        checkLogin(request); // 登录校验
        List<AuditRecord> history = auditService.getAuditHistory(evaluationId);
        return R.ok(history);
    }

    /**
     * 登录校验：任意已登录用户均可通过
     */
    private void checkLogin(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        if (!(roleTypeObj instanceof Integer)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限");
        }
    }

    private Integer getRoleType(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        if (roleTypeObj instanceof Integer) {
            return (Integer) roleTypeObj;
        }
        return 2;
    }

    private Integer getAuditLevel(Integer roleType) {
        if (roleType == 1) {
            return 1;
        } else if (roleType == 2) {
            return 2;
        }
        return 2;
    }
}
