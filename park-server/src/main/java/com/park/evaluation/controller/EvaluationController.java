package com.park.evaluation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.audit.entity.AuditRecord;
import com.park.audit.mapper.AuditMapper;
import com.park.auth.entity.SysUser;
import com.park.auth.service.AuthService;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.evaluation.dto.EvaluationQueryDTO;
import com.park.evaluation.dto.EvaluationSaveDTO;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.mapper.EvaluationMapper;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
    private EvaluationMapper evaluationMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private AuditMapper auditMapper;

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
    public R<Void> districtPass(@PathVariable Long id,
                                @RequestBody(required = false) Map<String, Object> body,
                                HttpServletRequest request) {
        checkAuditPermission(request, 2);
        String opinion = body == null ? null : (String) body.get("opinion");
        evaluationService.districtPass(id, request, opinion);
        return R.ok();
    }

    @PostMapping("/{id}/district-reject")
    @ApiOperation(value = "区县审核驳回", notes = "区县管理员审核驳回评价记录")
    public R<Void> districtReject(@PathVariable Long id,
                                  @RequestBody(required = false) Map<String, Object> body,
                                  HttpServletRequest request) {
        checkAuditPermission(request, 2);
        String opinion = body == null ? null : (String) body.get("opinion");
        evaluationService.districtReject(id, request, opinion);
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
    public R<Void> cityPass(@PathVariable Long id,
                            @RequestBody(required = false) Map<String, Object> body,
                            HttpServletRequest request) {
        checkAuditPermission(request, 1);
        String opinion = body == null ? null : (String) body.get("opinion");
        evaluationService.cityPass(id, request, opinion);
        return R.ok();
    }

    @PostMapping("/{id}/city-reject")
    @ApiOperation(value = "市级审核驳回", notes = "市级管理员审核驳回评价记录")
    public R<Void> cityReject(@PathVariable Long id,
                              @RequestBody(required = false) Map<String, Object> body,
                              HttpServletRequest request) {
        checkAuditPermission(request, 1);
        String opinion = body == null ? null : (String) body.get("opinion");
        evaluationService.cityReject(id, request, opinion);
        return R.ok();
    }

    /**
     * 获取年度下拉选项（用于发起年度填报）
     */
    @GetMapping("/year-options")
    @ApiOperation(value = "获取评价年度选项", notes = "返回近 5 年的年度列表，用于「发起年度填报」下拉框")
    public R<List<Map<String, Object>>> getYearOptions() {
        int currentYear = LocalDate.now().getYear();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int y = currentYear; y >= currentYear - 4; y--) {
            Map<String, Object> m = new HashMap<>();
            m.put("value", y);
            m.put("label", y + "年度");
            list.add(m);
        }
        return R.ok(list);
    }

    /**
     * 评价审核概览统计（顶部 4 个卡片）
     */
    @GetMapping("/summary")
    @ApiOperation(value = "评价审核概览", notes = "返回 total/pending/passed/returned 四个统计指标")
    public R<Map<String, Object>> getSummary(HttpServletRequest request) {
        // 应用数据权限：只统计当前用户可见的评价记录
        EvaluationQueryDTO scope = new EvaluationQueryDTO();
        applyDataPermission(scope, request);

        LambdaQueryWrapper<EvaluationRecord> base = new LambdaQueryWrapper<>();
        if (scope.getParkId() != null) {
            base.eq(EvaluationRecord::getParkId, scope.getParkId());
        }
        if (scope.getParkIds() != null && !scope.getParkIds().isEmpty()) {
            base.in(EvaluationRecord::getParkId, scope.getParkIds());
        }

        Long total = evaluationMapper.selectCount(base.clone());
        Long pending = evaluationMapper.selectCount(base.clone()
                .in(EvaluationRecord::getStatus, 1, 2));
        Long passed = evaluationMapper.selectCount(base.clone()
                .eq(EvaluationRecord::getStatus, 3));
        Long returned = evaluationMapper.selectCount(base.clone()
                .eq(EvaluationRecord::getStatus, 4));

        Map<String, Object> data = new HashMap<>();
        data.put("total", total == null ? 0 : total.intValue());
        data.put("pending", pending == null ? 0 : pending.intValue());
        data.put("passed", passed == null ? 0 : passed.intValue());
        data.put("returned", returned == null ? 0 : returned.intValue());
        return R.ok(data);
    }

    /**
     * 发起年度填报（按 year 为所有园区创建草稿评价记录）
     */
    @PostMapping("/init")
    @ApiOperation(value = "发起年度填报", notes = "为指定年度的所有园区生成草稿状态评价记录；每个年度只能发起一次")
    public R<Map<String, Object>> initByYear(@RequestBody Map<String, Object> body,
                                              HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || roleType != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅市级管理员可发起年度填报");
        }
        if (body == null || body.get("year") == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "year 不能为空");
        }
        Integer year;
        try {
            year = Integer.parseInt(body.get("year").toString());
        } catch (NumberFormatException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "year 必须为整数");
        }

        // 检查同年度是否已存在评价记录
        Long exists = evaluationMapper.selectCount(
                new LambdaQueryWrapper<EvaluationRecord>().eq(EvaluationRecord::getYear, year));
        if (exists != null && exists > 0) {
            throw new BusinessException(ResultCode.EVALUATION_EXISTS);
        }

        // 获取所有园区
        List<ParkInfo> parks = parkMapper.selectList(null);
        int created = 0;
        for (ParkInfo p : parks) {
            EvaluationRecord r = new EvaluationRecord();
            r.setParkId(p.getId());
            r.setYear(year);
            r.setStatus(0); // 草稿
            evaluationMapper.insert(r);
            created++;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("year", year);
        data.put("created", created);
        return R.ok(data);
    }

    /**
     * 获取评价记录的审核历史
     */
    @GetMapping("/{id}/history")
    @ApiOperation(value = "获取审核历史", notes = "按时间正序返回该评价记录的所有审核流水")
    public R<List<Map<String, Object>>> getHistory(@PathVariable Long id) {
        List<AuditRecord> records = auditMapper.selectList(
                new LambdaQueryWrapper<AuditRecord>()
                        .eq(AuditRecord::getEvaluationId, id)
                        .orderByAsc(AuditRecord::getCreateTime));
        List<Map<String, Object>> result = new ArrayList<>();
        int lastIdx = records.size() - 1;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < records.size(); i++) {
            AuditRecord r = records.get(i);
            String role = r.getAuditorRole() == null ? "" :
                    (r.getAuditorRole() == 1 ? "市" : (r.getAuditorRole() == 2 ? "区县" : "园区"));
            String action = r.getAction() != null && r.getAction() == 1 ? "审批通过" : "审批驳回";
            Map<String, Object> m = new HashMap<>();
            m.put("content", String.format("%s（%s端）%s了评价材料",
                    r.getAuditorName() == null ? "系统" : r.getAuditorName(), role, action));
            m.put("time", r.getCreateTime() == null ? null : r.getCreateTime().format(fmt));
            m.put("active", i == lastIdx);
            result.add(m);
        }
        return R.ok(result);
    }

    // =======================================================================
    // 园区端评价汇总（简版 / 详版 / 导出 / 一键评定 / 模板）
    // =======================================================================

    /**
     * 园区评价汇总表（简版）
     * 支持按 year / parkName / region / type 分页查询
     */
    @GetMapping("/park")
    @ApiOperation(value = "园区评价汇总（简版）", notes = "按年度/区县/类型汇总园区评价，返回每个园区的总得分与分档")
    public R<PageResult<Map<String, Object>>> getParkEvaluationList(EvaluationQueryDTO queryDTO,
                                                                    HttpServletRequest request) {
        applyDataPermission(queryDTO, request);
        if (queryDTO.getYear() == null) {
            queryDTO.setYear(LocalDate.now().getYear());
        }
        return R.ok(queryParkEvaluations(queryDTO, false));
    }

    /**
     * 导出园区评价汇总（简版）
     */
    @GetMapping("/park/export")
    @ApiOperation(value = "导出园区评价汇总（简版）", notes = "导出 Excel 文件")
    public void exportParkEvaluationList(EvaluationQueryDTO queryDTO,
                                         HttpServletRequest request,
                                         javax.servlet.http.HttpServletResponse response) {
        applyDataPermission(queryDTO, request);
        if (queryDTO.getYear() == null) {
            queryDTO.setYear(LocalDate.now().getYear());
        }
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10000);
        try {
            PageResult<Map<String, Object>> pr = queryParkEvaluations(queryDTO, false);
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=park_evaluation_" + queryDTO.getYear() + ".xls");
            StringBuilder sb = new StringBuilder("id,parkName,districtName,totalScore,grade\n");
            for (Map<String, Object> r : pr.getRecords()) {
                sb.append(r.get("id")).append(",")
                  .append(r.get("parkName")).append(",")
                  .append(r.get("districtName")).append(",")
                  .append(r.get("totalScore")).append(",")
                  .append(r.get("grade")).append("\n");
            }
            response.getOutputStream().write(sb.toString().getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "导出失败：" + ex.getMessage());
        }
    }

    /**
     * 园区评价汇总表（详版）
     */
    @GetMapping("/park/detail")
    @ApiOperation(value = "园区评价汇总（详版）", notes = "返回每个园区的总得分、分档、以及各分项得分")
    public R<PageResult<Map<String, Object>>> getParkEvaluationDetail(EvaluationQueryDTO queryDTO,
                                                                      HttpServletRequest request) {
        applyDataPermission(queryDTO, request);
        if (queryDTO.getYear() == null) {
            queryDTO.setYear(LocalDate.now().getYear());
        }
        return R.ok(queryParkEvaluations(queryDTO, true));
    }

    /**
     * 导出园区评价汇总（详版）
     */
    @GetMapping("/park/detail/export")
    @ApiOperation(value = "导出园区评价汇总（详版）", notes = "导出 Excel 文件")
    public void exportParkEvaluationDetail(EvaluationQueryDTO queryDTO,
                                           HttpServletRequest request,
                                           javax.servlet.http.HttpServletResponse response) {
        applyDataPermission(queryDTO, request);
        if (queryDTO.getYear() == null) {
            queryDTO.setYear(LocalDate.now().getYear());
        }
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10000);
        try {
            PageResult<Map<String, Object>> pr = queryParkEvaluations(queryDTO, true);
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=park_evaluation_detail_" + queryDTO.getYear() + ".xls");
            StringBuilder sb = new StringBuilder("id,parkName,districtName,totalScore,grade,industryDevScore,enterpriseCultivateScore,techInnovationScore,serviceCapabilityScore,benefitOutputScore,safetyProductionScore,otherScore\n");
            for (Map<String, Object> r : pr.getRecords()) {
                sb.append(r.get("id")).append(",")
                  .append(r.get("parkName")).append(",")
                  .append(r.get("districtName")).append(",")
                  .append(r.get("totalScore")).append(",")
                  .append(r.get("grade")).append(",")
                  .append(r.get("industryDevScore")).append(",")
                  .append(r.get("enterpriseCultivateScore")).append(",")
                  .append(r.get("techInnovationScore")).append(",")
                  .append(r.get("serviceCapabilityScore")).append(",")
                  .append(r.get("benefitOutputScore")).append(",")
                  .append(r.get("safetyProductionScore")).append(",")
                  .append(r.get("otherScore")).append("\n");
            }
            response.getOutputStream().write(sb.toString().getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "导出失败：" + ex.getMessage());
        }
    }

    /**
     * 一键绩效评定
     * 对指定年度的所有评价记录计算总分与分档（A/B/C/D）
     */
    @PostMapping("/park/grade")
    @ApiOperation(value = "一键绩效评定", notes = "对指定年度的评价记录计算 totalScore 和 grade")
    public R<Map<String, Object>> performParkEvaluation(@RequestBody Map<String, Object> body,
                                                        HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || roleType != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅市级管理员可一键评定");
        }
        Object yObj = body == null ? null : body.get("year");
        if (yObj == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "year 不能为空");
        }
        Integer year = Integer.parseInt(yObj.toString());
        List<EvaluationRecord> records = evaluationMapper.selectList(
                new LambdaQueryWrapper<EvaluationRecord>().eq(EvaluationRecord::getYear, year));
        int updated = 0;
        for (EvaluationRecord r : records) {
            // 简单评分逻辑：基于企业数/园区规模计算总分（占位实现）
            ParkInfo p = parkMapper.selectById(r.getParkId());
            double score = 60.0;
            if (p != null) {
                if (p.getEnterpriseCount() != null) score += Math.min(p.getEnterpriseCount() * 0.5, 20);
                if (p.getLandArea() != null) score += Math.min(p.getLandArea().doubleValue() * 0.05, 10);
                if (p.getAboveScaleCount() != null) score += Math.min(p.getAboveScaleCount() * 0.3, 10);
            }
            score = Math.min(score, 100);
            r.setTotalScore(BigDecimal.valueOf(score));
            r.setGrade(score >= 90 ? "A" : score >= 80 ? "B" : score >= 70 ? "C" : "D");
            evaluationMapper.updateById(r);
            updated++;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("year", year);
        data.put("updated", updated);
        return R.ok("评定完成", data);
    }

    /**
     * 下载评价模板
     */
    @GetMapping("/park/template")
    @ApiOperation(value = "下载评价导入模板", notes = "返回 Excel 模板文件")
    public void downloadEvaluationTemplate(HttpServletRequest request,
                                           javax.servlet.http.HttpServletResponse response) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || roleType != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅市级管理员可下载");
        }
        try {
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=evaluation_template.xls");
            String csv = "parkId,year,totalScore,grade\n1,2026,85.5,B\n2,2026,90.0,A\n";
            response.getOutputStream().write(csv.getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "下载模板失败：" + ex.getMessage());
        }
    }

    /**
     * 内部方法：构建园区评价数据（含简版/详版差异）
     */
    private PageResult<Map<String, Object>> queryParkEvaluations(EvaluationQueryDTO queryDTO, boolean detail) {
        Page<EvaluationRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<EvaluationRecord> w = new LambdaQueryWrapper<>();
        w.eq(EvaluationRecord::getYear, queryDTO.getYear())
                .in(queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty(),
                        EvaluationRecord::getParkId, queryDTO.getParkIds())
                .eq(queryDTO.getParkId() != null, EvaluationRecord::getParkId, queryDTO.getParkId())
                .orderByDesc(EvaluationRecord::getTotalScore);
        IPage<EvaluationRecord> result = evaluationMapper.selectPage(page, w);
        List<Map<String, Object>> list = new ArrayList<>();
        for (EvaluationRecord er : result.getRecords()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", er.getId());
            m.put("parkId", er.getParkId());
            m.put("year", er.getYear());
            BigDecimal total = er.getTotalScore() == null ? BigDecimal.ZERO : er.getTotalScore();
            m.put("totalScore", total);
            m.put("grade", er.getGrade());

            ParkInfo p = parkMapper.selectById(er.getParkId());
            if (p != null) {
                m.put("parkName", p.getParkName());
                m.put("districtName", p.getDistrictName());
                m.put("districtId", p.getDistrictId());
                m.put("parkType", p.getParkType());
                m.put("enterpriseTotal", p.getEnterpriseCount() == null ? 0 : p.getEnterpriseCount());
                m.put("leadingIndustry", p.getLeadingIndustry());
                // 亩均营收 / 亩均税收（占位：基于 land_area 与 enterpriseCount 估算）
                m.put("revenuePerMu", 0);
                m.put("taxPerMu", 0);
            } else {
                m.put("parkName", "");
                m.put("districtName", "");
            }

            if (detail) {
                // 分项得分（按权重拆解 totalScore 作为占位）
                double d = total.doubleValue();
                m.put("industryDevScore", round(d * 0.20));
                m.put("enterpriseCultivateScore", round(d * 0.20));
                m.put("techInnovationScore", round(d * 0.20));
                m.put("serviceCapabilityScore", round(d * 0.15));
                m.put("benefitOutputScore", round(d * 0.15));
                m.put("safetyProductionScore", round(d * 0.05));
                m.put("otherScore", round(d * 0.05));
            }
            list.add(m);
        }
        return PageResult.of(list, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
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
