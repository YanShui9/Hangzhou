package com.park.evaluation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.auth.entity.SysUser;
import com.park.auth.service.AuthService;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.evaluation.dto.EvaluationQueryDTO;
import com.park.evaluation.dto.EvaluationSaveDTO;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.service.EvaluationService;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评价控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/evaluations")
@Api(tags = "评价管理")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ParkMapper parkMapper;

    /**
     * 分页查询评价记录列表
     *
     * @param queryDTO 查询条件
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping
    @ApiOperation(value = "分页查询评价记录", notes = "根据条件分页查询评价记录列表")
    public R<PageResult<EvaluationRecord>> getEvaluationPage(
            EvaluationQueryDTO queryDTO,
            HttpServletRequest request) {

        // 应用数据权限
        applyDataPermission(queryDTO, request);

        IPage<EvaluationRecord> page = evaluationService.getEvaluationPage(queryDTO);
        List<EvaluationRecord> records = page.getRecords();
        PageResult<EvaluationRecord> pageResult = PageResult.of(
                records,
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 根据ID查询评价记录详情
     *
     * @param id 评价记录ID
     * @return 评价记录详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询评价记录详情", notes = "根据ID查询评价记录详细信息")
    public R<EvaluationRecord> getEvaluationById(@PathVariable Long id) {
        EvaluationRecord record = evaluationService.getEvaluationById(id);
        return R.ok(record);
    }

    /**
     * 新增评价记录
     *
     * @param saveDTO 保存请求参数
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation(value = "新增评价记录", notes = "创建新的评价记录（草稿状态）")
    public R<Void> addEvaluation(@Valid @RequestBody EvaluationSaveDTO saveDTO) {
        saveDTO.setId(null); // 确保是新增操作
        evaluationService.saveEvaluation(saveDTO);
        return R.ok();
    }

    /**
     * 修改评价记录
     *
     * @param saveDTO 保存请求参数（必须包含id）
     * @return 操作结果
     */
    @PutMapping
    @ApiOperation(value = "修改评价记录", notes = "修改草稿状态的评价记录")
    public R<Void> updateEvaluation(@Valid @RequestBody EvaluationSaveDTO saveDTO) {
        evaluationService.saveEvaluation(saveDTO);
        return R.ok();
    }

    /**
     * 提交评价（状态从 draft 改为 submitted）
     *
     * @param id 评价记录ID
     * @return 操作结果
     */
    @PostMapping("/{id}/submit")
    @ApiOperation(value = "提交评价", notes = "将草稿状态的评价记录提交审核")
    public R<Void> submitEvaluation(@PathVariable Long id) {
        evaluationService.submitEvaluation(id);
        return R.ok();
    }

    /**
     * 区县审核通过（状态从 1=待区县审 改为 2=待市局审）
     *
     * @param id      评价记录ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/district-pass")
    @ApiOperation(value = "区县审核通过", notes = "区县管理员审核通过评价记录")
    public R<Void> districtPass(@PathVariable Long id, HttpServletRequest request) {
        checkAuditPermission(request, 2); // 校验区县管理员权限
        evaluationService.districtPass(id);
        return R.ok();
    }

    /**
     * 区县审核驳回（状态从 1=待区县审 改为 4=驳回）
     *
     * @param id      评价记录ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/district-reject")
    @ApiOperation(value = "区县审核驳回", notes = "区县管理员审核驳回评价记录")
    public R<Void> districtReject(@PathVariable Long id, HttpServletRequest request) {
        checkAuditPermission(request, 2); // 校验区县管理员权限
        evaluationService.districtReject(id);
        return R.ok();
    }

    /**
     * 市级审核通过（状态从 2=待市局审 改为 3=通过）
     *
     * @param id      评价记录ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/city-pass")
    @ApiOperation(value = "市级审核通过", notes = "市级管理员审核通过评价记录")
    public R<Void> cityPass(@PathVariable Long id, HttpServletRequest request) {
        checkAuditPermission(request, 1); // 校验市级管理员权限
        evaluationService.cityPass(id);
        return R.ok();
    }

    /**
     * 市级审核驳回（状态从 2=待市局审 改为 4=驳回）
     *
     * @param id      评价记录ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/city-reject")
    @ApiOperation(value = "市级审核驳回", notes = "市级管理员审核驳回评价记录")
    public R<Void> cityReject(@PathVariable Long id, HttpServletRequest request) {
        checkAuditPermission(request, 1); // 校验市级管理员权限
        evaluationService.cityReject(id);
        return R.ok();
    }

    /**
     * 检查园区当年是否已提交评价
     *
     * @param parkId 园区ID
     * @param year   评价年份
     * @param request HTTP请求
     * @return 是否已提交
     */
    @GetMapping("/check-submitted")
    @ApiOperation(value = "检查评价是否已提交", notes = "检查园区当年是否已提交评价材料")
    public R<Boolean> checkSubmitted(
            @RequestParam Long parkId,
            @RequestParam Integer year,
            HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType != null && roleType == 3) {
            Object userIdObj = request.getAttribute("userId");
            Long userId = null;
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            }
            SysUser user = authService.getUserById(userId);
            if (user != null && user.getParkId() != null && !user.getParkId().equals(parkId)) {
                throw new BusinessException(ResultCode.FORBIDDEN, "只能查询本园区的评价状态");
            }
        }
        boolean hasSubmitted = evaluationService.hasSubmittedEvaluation(parkId, year);
        return R.ok(hasSubmitted);
    }

    /**
     * 应用数据权限控制
     * 市级管理员：查看所有
     * 区县管理员：查看本区县的评价记录
     * 园区管理员：查看本园区的评价记录
     */
    private void applyDataPermission(EvaluationQueryDTO queryDTO, HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        Object userIdObj = request.getAttribute("userId");
        Long userId = null;
        if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        }

        if (roleType == null || roleType == 1) {
            // 市级管理员，查看所有
            return;
        }

        // 获取用户信息
        SysUser user = authService.getUserById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (roleType == 2) {
            // 区县管理员：查看本区县的评价记录
            if (user.getDistrictId() == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "区县管理员未分配区县");
            }
            // 查询该区县下的所有园区ID
            List<Long> parkIds = parkMapper.selectList(
                    new LambdaQueryWrapper<ParkInfo>()
                            .eq(ParkInfo::getDistrictId, user.getDistrictId())
                            .select(ParkInfo::getId)
            ).stream().map(ParkInfo::getId).collect(Collectors.toList());

            if (parkIds.isEmpty()) {
                queryDTO.setParkId(-1L); // 无数据
            } else {
                queryDTO.setParkIds(parkIds);
            }
        } else if (roleType == 3) {
            // 园区管理员：查看本园区的评价记录
            if (user.getParkId() == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "园区管理员未分配园区");
            }
            queryDTO.setParkId(user.getParkId());
        }
    }

    /**
     * 校验审核权限
     *
     * @param request         HTTP请求
     * @param requiredRoleType 期望的角色类型
     */
    private void checkAuditPermission(HttpServletRequest request, Integer requiredRoleType) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || !roleType.equals(requiredRoleType)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无审核权限");
        }
    }
}
