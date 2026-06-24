package com.park.evaluation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.auth.entity.SysUser;
import com.park.auth.service.AuthService;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.audit.entity.AuditRecord;
import com.park.audit.service.AuditService;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private AuditService auditService;

    /**
     * 状态码 → 中文审核状态
     */
    private static final Map<Integer, String> AUDIT_STATUS_MAP = new LinkedHashMap<>();
    static {
        AUDIT_STATUS_MAP.put(0, "未提交");
        AUDIT_STATUS_MAP.put(1, "区县待审核");
        AUDIT_STATUS_MAP.put(2, "市级待审核");
        AUDIT_STATUS_MAP.put(3, "市级审核通过");
        AUDIT_STATUS_MAP.put(4, "市级审核驳回");
    }

    /**
     * 分页查询评价记录列表
     *
     * @param queryDTO 查询条件
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping
    @ApiOperation(value = "分页查询评价记录", notes = "根据条件分页查询评价记录列表")
    public R<PageResult<Map<String, Object>>> getEvaluationPage(
            EvaluationQueryDTO queryDTO,
            HttpServletRequest request) {

        // 应用数据权限
        applyDataPermission(queryDTO, request);

        IPage<EvaluationRecord> page = evaluationService.getEvaluationPage(queryDTO);
        List<EvaluationRecord> records = page.getRecords();

        // 关联 park_info 补充 parkName/districtName/parkType，状态码转中文
        List<Long> parkIds = records.stream()
                .map(EvaluationRecord::getParkId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ParkInfo> parkMap = new java.util.HashMap<>();
        if (!parkIds.isEmpty()) {
            List<ParkInfo> parks = parkMapper.selectBatchIds(parkIds);
            for (ParkInfo p : parks) parkMap.put(p.getId(), p);
        }

        List<Map<String, Object>> voList = new ArrayList<>();
        for (EvaluationRecord r : records) {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", r.getId());
            vo.put("parkId", r.getParkId());
            vo.put("year", r.getYear());
            vo.put("status", r.getStatus());
            vo.put("totalScore", r.getTotalScore());
            vo.put("grade", r.getGrade());
            vo.put("createTime", r.getCreateTime());
            vo.put("updateTime", r.getUpdateTime());
            vo.put("scoreDetail", r.getScoreDetail());
            vo.put("rejectCategories", r.getRejectCategories());
            vo.put("auditStatus", AUDIT_STATUS_MAP.getOrDefault(r.getStatus(), "未提交"));
            ParkInfo park = parkMap.get(r.getParkId());
            if (park != null) {
                vo.put("parkName", park.getParkName());
                vo.put("districtName", park.getDistrictName());
                vo.put("parkType", park.getParkType());
            }
            voList.add(vo);
        }

        PageResult<Map<String, Object>> pageResult = PageResult.of(
                voList,
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 发起年度填报（仅市级管理员）
     *
     * @param body    请求体 { year: 2025 }
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/init")
    @ApiOperation(value = "发起年度填报", notes = "为所有园区创建该年度的评价记录（仅市级管理员）")
    public R<Void> initEvaluationByYear(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        checkCityAdmin(request);
        Integer year = body.get("year") != null ? Integer.valueOf(body.get("year").toString()) : null;
        if (year == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "年度参数不能为空");
        }
        evaluationService.initEvaluationByYear(year);
        return R.ok("发起成功", null);
    }

    /**
     * 获取年度可选项
     *
     * @return 年度选项列表
     */
    @GetMapping("/year-options")
    @ApiOperation(value = "获取年度选项", notes = "获取可发起年度填报的年度列表")
    public R<List<Map<String, Object>>> getYearOptions() {
        List<Map<String, Object>> options = new ArrayList<>();
        int currentYear = java.time.Year.now().getValue();
        for (int i = 0; i < 3; i++) {
            int year = currentYear - i;
            Map<String, Object> option = new LinkedHashMap<>();
            option.put("value", year);
            option.put("label", year + "年度");
            options.add(option);
        }
        return R.ok(options);
    }

    /**
     * 评价审核概览统计
     *
     * @param request HTTP请求
     * @return 统计数据
     */
    @GetMapping("/summary")
    @ApiOperation(value = "评价审核概览统计", notes = "获取全部/市级待审核/市级通过/市级驳回数量")
    public R<Map<String, Integer>> getEvaluationSummary(HttpServletRequest request) {
        EvaluationQueryDTO queryDTO = new EvaluationQueryDTO();
        applyDataPermission(queryDTO, request);
        Map<String, Integer> summary = evaluationService.getEvaluationSummary(
                queryDTO.getParkId(), queryDTO.getParkIds()
        );
        return R.ok(summary);
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

    @GetMapping("/{id}/history")
    @ApiOperation(value = "查询审核历史", notes = "获取某条评价记录的审核历史列表")
    public R<List<Map<String, Object>>> getAuditHistory(@PathVariable Long id) {
        List<AuditRecord> records = auditService.getAuditHistory(id);
        List<Map<String, Object>> history = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            AuditRecord record = records.get(i);
            Map<String, Object> item = new LinkedHashMap<>();

            String role = record.getAuditorRole() == 1 ? "市级管理员" : "区县管理员";
            String action = record.getAction() == 1 ? "审核通过" : "审核驳回";
            String content = record.getAuditorName() + "(" + role + ")" + action + "了评价材料";

            item.put("content", content);
            item.put("time", record.getCreateTime());
            item.put("active", i == 0);
            history.add(item);
        }
        return R.ok(history);
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
     * 保存审核打分详情（市级/区县审核时保存打分）
     *
     * @param id   评价记录ID
     * @param body 请求体 { "scoreDetail": "{...}" }
     * @return 操作结果
     */
    @PutMapping("/{id}/score")
    @ApiOperation(value = "保存审核打分", notes = "保存市级/区县审核时的打分详情JSON")
    public R<Void> saveAuditDetail(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String scoreDetail = body.get("scoreDetail") != null ? body.get("scoreDetail").toString() : null;
        evaluationService.saveAuditDetail(id, scoreDetail);
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

    private void checkCityAdmin(HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || roleType != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅市级管理员可操作");
        }
    }
}
