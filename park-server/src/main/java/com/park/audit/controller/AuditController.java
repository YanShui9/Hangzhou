package com.park.audit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.audit.dto.AuditDTO;
import com.park.audit.entity.AuditRecord;
import com.park.audit.service.AuditService;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.evaluation.entity.EvaluationRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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

    /**
     * 获取审核列表（待审核 + 已审核）
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param status   状态筛选：pending/audited（可选）
     * @param request  HTTP请求（用于获取当前用户角色类型）
     * @return 分页结果
     */
    @GetMapping
    @ApiOperation(value = "查询审核列表", notes = "查询待审核和已审核的评价记录列表")
    public R<PageResult<EvaluationRecord>> getAuditList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Integer roleType = getRoleType(request);
        Integer auditLevel = getAuditLevel(roleType);

        IPage<EvaluationRecord> page = auditService.getAuditPage(pageNum, pageSize, status, auditLevel);
        PageResult<EvaluationRecord> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 获取待审核列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param request  HTTP请求
     * @return 分页结果
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
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param request  HTTP请求
     * @return 分页结果
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
     *
     * @param auditDTO 审核请求参数
     * @param request  HTTP请求
     * @return 操作结果
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
     *
     * @param evaluationId 评价记录ID
     * @return 审核记录列表
     */
    @GetMapping("/history/{evaluationId}")
    @ApiOperation(value = "查询审核历史", notes = "查询某条评价记录的所有审核记录")
    public R<List<AuditRecord>> getAuditHistory(@PathVariable Long evaluationId) {
        List<AuditRecord> history = auditService.getAuditHistory(evaluationId);
        return R.ok(history);
    }

    /**
     * 从请求中获取用户角色类型
     *
     * @param request HTTP请求
     * @return 角色类型
     */
    private Integer getRoleType(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        if (roleTypeObj instanceof Integer) {
            return (Integer) roleTypeObj;
        }
        return 2; // 默认区县级别
    }

    /**
     * 根据用户角色类型确定审核级别
     *
     * @param roleType 用户角色类型
     * @return 审核级别：2=区县初审, 1=市级终审
     */
    private Integer getAuditLevel(Integer roleType) {
        if (roleType == 1) {
            return 1; // 市级终审
        } else if (roleType == 2) {
            return 2; // 区县初审
        }
        return 2; // 默认区县级别
    }
}
