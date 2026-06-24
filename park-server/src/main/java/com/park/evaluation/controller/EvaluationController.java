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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
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
            // 参评状态：有评价记录即表示参评
            vo.put("parkStatus", "参评");
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
     * 查询园区评价汇总表（简版）分页列表
     */
    @GetMapping("/park")
    @ApiOperation(value = "查询园区评价汇总表（简版）", notes = "分页查询园区评价汇总简版列表")
    public R<PageResult<Map<String, Object>>> getParkEvaluationList(EvaluationQueryDTO queryDTO,
                                                                    HttpServletRequest request) {
        applyDataPermission(queryDTO, request);
        IPage<Map<String, Object>> page = evaluationService.getParkEvaluationList(queryDTO);
        return R.ok(PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        ));
    }

    /**
     * 查询园区评价汇总表（详版）分页列表
     */
    @GetMapping("/park/detail")
    @ApiOperation(value = "查询园区评价汇总表（详版）", notes = "分页查询园区评价汇总详细版列表")
    public R<PageResult<Map<String, Object>>> getParkEvaluationDetail(EvaluationQueryDTO queryDTO,
                                                                      HttpServletRequest request) {
        applyDataPermission(queryDTO, request);
        IPage<Map<String, Object>> page = evaluationService.getParkEvaluationDetail(queryDTO);
        return R.ok(PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        ));
    }

    /**
     * 导出园区评价汇总表（简版）
     *
     * @param queryDTO 查询条件
     * @param request  HTTP请求
     * @param response HTTP响应
     */
    @GetMapping("/park/export")
    @ApiOperation(value = "导出园区评价汇总表（简版）", notes = "导出园区评价汇总简版Excel")
    public void exportParkEvaluationList(EvaluationQueryDTO queryDTO,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
        applyDataPermission(queryDTO, request);
        byte[] data = evaluationService.exportParkEvaluationList(queryDTO);
        writeExcelResponse(response, data, "园区评价统计简化版.xlsx");
    }

    /**
     * 导出园区评价汇总表（详版）
     *
     * @param queryDTO 查询条件
     * @param request  HTTP请求
     * @param response HTTP响应
     */
    @GetMapping("/park/detail/export")
    @ApiOperation(value = "导出园区评价汇总表（详版）", notes = "导出园区评价汇总详细版Excel")
    public void exportParkEvaluationDetail(EvaluationQueryDTO queryDTO,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws IOException {
        applyDataPermission(queryDTO, request);
        byte[] data = evaluationService.exportParkEvaluationDetail(queryDTO);
        writeExcelResponse(response, data, "园区评价统计详细版.xlsx");
    }

    /**
     * 绩效评定：生成本年度各园区绩效分档结果（仅市级管理员）
     * 规则：所有园区本年度考核完成后，按分值高低排序分档（A≤20%、B≤30%、C≈45%、D≥5%），
     * 四星/五星园区继续评A档不占A档比例，支持多次评定。
     *
     * @param body    请求体 { year: 2026 }
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/park/grade")
    @ApiOperation(value = "绩效评定", notes = "生成本年度各园区绩效分档结果（A/B/C/D），仅市级管理员")
    public R<Void> performParkGradeEvaluation(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        checkCityAdmin(request);
        Integer year = body.get("year") != null ? Integer.valueOf(body.get("year").toString()) : null;
        if (year == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "年度参数不能为空");
        }
        evaluationService.performParkGradeEvaluation(year);
        return R.ok("绩效评定完成", null);
    }

    private void writeExcelResponse(HttpServletResponse response, byte[] data, String filename) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
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
    @ApiOperation(value = "查询评价记录详情", notes = "根据ID查询评价记录详细信息（含scoreDetail解析和区县意见）")
    public R<Map<String, Object>> getEvaluationById(@PathVariable Long id) {
        Map<String, Object> detail = evaluationService.getEvaluationDetailMap(id);
        return R.ok(detail);
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
     * 获取待审核列表（根据当前用户角色自动区分区县初审/市级终审）
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping("/pending")
    @ApiOperation(value = "查询待审核列表", notes = "根据当前用户角色查询待审核的评价记录")
    public R<PageResult<EvaluationRecord>> getPendingAuditList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        Integer auditLevel = (roleType != null && roleType == 1) ? 1 : 2;
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
     * 获取已审核列表（根据当前用户角色自动区分区县初审/市级终审）
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping("/audited")
    @ApiOperation(value = "查询已审核列表", notes = "根据当前用户角色查询已审核的评价记录")
    public R<PageResult<EvaluationRecord>> getAuditedList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        Integer auditLevel = (roleType != null && roleType == 1) ? 1 : 2;
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
     * 提交审核结果（通过/驳回）统一入口
     *
     * @param body    请求体 { evaluationId, action, opinion }
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/audit")
    @ApiOperation(value = "提交审核结果", notes = "对评价记录进行审核（action: 1=通过, 2=驳回）")
    public R<Void> submitAudit(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        Long auditorId = null;
        if (userIdObj instanceof Integer) {
            auditorId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            auditorId = (Long) userIdObj;
        }
        if (auditorId == null) {
            return R.fail("无法获取审核人信息");
        }

        Long evaluationId = body.get("evaluationId") != null ? Long.valueOf(body.get("evaluationId").toString()) : null;
        Integer action = body.get("action") != null ? Integer.valueOf(body.get("action").toString()) : null;
        String opinion = body.get("opinion") != null ? body.get("opinion").toString() : null;

        if (evaluationId == null || action == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "评价记录ID和审核动作不能为空");
        }

        com.park.audit.dto.AuditDTO auditDTO = new com.park.audit.dto.AuditDTO();
        auditDTO.setEvaluationId(evaluationId);
        auditDTO.setAction(action);
        auditDTO.setOpinion(opinion);

        Integer roleType = (Integer) request.getAttribute("roleType");
        auditService.audit(auditDTO, auditorId, roleType);
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
