package com.park.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.audit.entity.AuditRecord;
import com.park.audit.service.AuditService;
import com.park.evaluation.dto.EvaluationQueryDTO;
import com.park.evaluation.dto.EvaluationSaveDTO;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.entity.ParkEvaluationScore;
import com.park.evaluation.mapper.EvaluationMapper;
import com.park.evaluation.mapper.ParkEvaluationScoreMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评价服务
 * 状态定义：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回
 *
 * @author park-team
 */
@Slf4j
@Service
public class EvaluationService {

    private final EvaluationMapper evaluationMapper;

    private final ParkEvaluationScoreMapper scoreMapper;

    private final ParkMapper parkMapper;

    private final AutoCalculationService autoCalculationService;

    private final AuditService auditService;

    public EvaluationService(EvaluationMapper evaluationMapper,
                             ParkEvaluationScoreMapper scoreMapper,
                             ParkMapper parkMapper,
                             AutoCalculationService autoCalculationService,
                             AuditService auditService) {
        this.evaluationMapper = evaluationMapper;
        this.scoreMapper = scoreMapper;
        this.parkMapper = parkMapper;
        this.autoCalculationService = autoCalculationService;
        this.auditService = auditService;
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 统计指定园区在指定年份已提交（非草稿）的评价记录数
     *
     * @param parkId 园区ID
     * @param year   评价年份
     * @return 已提交记录数
     */
    public long countSubmitted(Long parkId, Integer year) {
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EvaluationRecord::getParkId, parkId)
               .eq(EvaluationRecord::getYear, year)
               .ne(EvaluationRecord::getStatus, 0);
        return evaluationMapper.selectCount(wrapper);
    }

    /**
     * 发起年度填报
     * 为所有园区创建该年度的评价记录（草稿状态）
     *
     * @param year 年度
     */
    @Transactional(rollbackFor = Exception.class)
    public void initEvaluationByYear(Integer year) {
        if (year == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "年度参数不能为空");
        }
        // 检查是否已经发起过
        LambdaQueryWrapper<EvaluationRecord> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(EvaluationRecord::getYear, year);
        checkWrapper.last("LIMIT 1");
        if (evaluationMapper.selectCount(checkWrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_EXISTS, year + "年度已发起，不能重复发起");
        }
        // 获取所有园区
        List<ParkInfo> parks = parkMapper.selectList(new LambdaQueryWrapper<ParkInfo>()
                .select(ParkInfo::getId, ParkInfo::getParkName));
        if (parks == null || parks.isEmpty()) {
            throw new BusinessException(ResultCode.FAILURE, "暂无园区数据");
        }
        // 批量创建评价记录（草稿状态=0）
        int count = 0;
        for (ParkInfo park : parks) {
            EvaluationRecord record = new EvaluationRecord();
            record.setParkId(park.getId());
            record.setYear(year);
            record.setStatus(0); // 0=草稿
            evaluationMapper.insert(record);
            count++;
        }
        log.info("发起年度填报完成：year={}, 园区数={}", year, count);
    }

    /**
     * 获取评价审核概览统计
     *
     * @param parkId  园区ID（单园区）
     * @param parkIds 园区ID列表（多园区，区县管理员）
     * @return 统计数据
     */
    public Map<String, Integer> getEvaluationSummary(Long parkId, List<Long> parkIds) {
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();
        if (parkId != null) {
            wrapper.eq(EvaluationRecord::getParkId, parkId);
        }
        if (parkIds != null && !parkIds.isEmpty()) {
            wrapper.in(EvaluationRecord::getParkId, parkIds);
        }

        List<EvaluationRecord> all = evaluationMapper.selectList(wrapper);

        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("total", all.size());
        summary.put("cityPending", (int) all.stream().filter(r -> r.getStatus() != null && r.getStatus() == 2).count());
        summary.put("cityPassed", (int) all.stream().filter(r -> r.getStatus() != null && r.getStatus() == 3).count());
        summary.put("cityReturned", (int) all.stream().filter(r -> r.getStatus() != null && r.getStatus() == 4).count());
        return summary;
    }

    /**
     * 分页查询评价记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getEvaluationPage(EvaluationQueryDTO queryDTO) {
        // 1. 根据园区查询条件（parkName/districtName/parkType）过滤 parkId
        List<Long> parkIds;
        if (queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty()) {
            parkIds = new ArrayList<>(queryDTO.getParkIds());
        } else if (queryDTO.getParkId() != null) {
            parkIds = Collections.singletonList(queryDTO.getParkId());
        } else {
            LambdaQueryWrapper<ParkInfo> parkWrapper = new LambdaQueryWrapper<>();
            if (queryDTO.getParkName() != null && !queryDTO.getParkName().trim().isEmpty()) {
                parkWrapper.like(ParkInfo::getParkName, queryDTO.getParkName().trim());
            }
            // 兼容前端 districtName 和 region
            String region = (queryDTO.getDistrictName() != null && !queryDTO.getDistrictName().trim().isEmpty())
                    ? queryDTO.getDistrictName().trim()
                    : (queryDTO.getRegion() != null ? queryDTO.getRegion().trim() : null);
            if (region != null && !region.isEmpty()) {
                parkWrapper.eq(ParkInfo::getDistrictName, region);
            }
            // 兼容前端 parkType 和 type
            String parkType = (queryDTO.getParkType() != null && !queryDTO.getParkType().trim().isEmpty())
                    ? queryDTO.getParkType().trim()
                    : (queryDTO.getType() != null ? queryDTO.getType().trim() : null);
            if (parkType != null && !parkType.isEmpty()) {
                parkWrapper.eq(ParkInfo::getParkType, parkType);
            }
            parkIds = parkMapper.selectList(parkWrapper).stream()
                    .map(ParkInfo::getId)
                    .collect(Collectors.toList());
        }

        Page<EvaluationRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        if (parkIds == null || parkIds.isEmpty()) {
            return page;
        }

        // 2. 查询评价记录
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EvaluationRecord::getParkId, parkIds);
        if (queryDTO.getYear() != null) {
            wrapper.eq(EvaluationRecord::getYear, queryDTO.getYear());
        }
        // 兼容前端 auditStatus 中文转 status 数字
        if (queryDTO.getAuditStatus() != null && !queryDTO.getAuditStatus().trim().isEmpty()) {
            Integer status = auditStatusToCode(queryDTO.getAuditStatus().trim());
            if (status != null) {
                wrapper.eq(EvaluationRecord::getStatus, status);
            }
        } else if (queryDTO.getStatus() != null) {
            wrapper.eq(EvaluationRecord::getStatus, queryDTO.getStatus());
        }

        // 默认按创建时间降序排序
        wrapper.orderByDesc(EvaluationRecord::getCreateTime);

        return evaluationMapper.selectPage(page, wrapper);
    }

    /**
     * 审核状态中文转状态码
     */
    private Integer auditStatusToCode(String auditStatus) {
        switch (auditStatus) {
            case "未提交": return 0;
            case "区县待审核": return 1;
            case "市级待审核": return 2;
            case "市级审核通过": return 3;
            case "市级审核驳回": return 4;
            default: return null;
        }
    }

    /**
     * 根据ID查询评价记录详情
     *
     * @param id 评价记录ID
     * @return 评价记录实体
     */
    public EvaluationRecord getEvaluationById(Long id) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        return record;
    }

    public Map<String, Object> getEvaluationDetailMap(Long id) {
        EvaluationRecord record = getEvaluationById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", record.getId());
        result.put("parkId", record.getParkId());
        ParkInfo parkInfo = record.getParkId() != null ? parkMapper.selectById(record.getParkId()) : null;
        result.put("parkName", parkInfo != null ? parkInfo.getParkName() : null);
        result.put("year", record.getYear());
        result.put("status", record.getStatus());
        result.put("totalScore", record.getTotalScore());
        result.put("grade", record.getGrade());
        result.put("rejectCategories", record.getRejectCategories());
        result.put("createTime", record.getCreateTime());
        result.put("updateTime", record.getUpdateTime());

        Map<String, Object> scoreDetailMap = new LinkedHashMap<>();
        if (record.getScoreDetail() != null && !record.getScoreDetail().isEmpty()) {
            try {
                scoreDetailMap = OBJECT_MAPPER.readValue(record.getScoreDetail(),
                        new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.warn("解析scoreDetail失败", e);
            }
        }
        result.put("scoreDetailMap", scoreDetailMap);

        String districtOpinion = "";
        Integer districtResult = null;
        List<AuditRecord> history = auditService.getAuditHistory(id);
        for (AuditRecord ar : history) {
            if (ar.getAuditorRole() != null && ar.getAuditorRole() == 2) {
                districtOpinion = ar.getOpinion() != null ? ar.getOpinion() : "";
                districtResult = ar.getAction();
                break;
            }
        }
        result.put("districtOpinion", districtOpinion);
        result.put("districtResult", districtResult);

        return result;
    }

    /**
     * 保存评价记录（新增或修改）
     *
     * @param saveDTO 保存请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEvaluation(EvaluationSaveDTO saveDTO) {
        // 检查同一园区、年份是否已存在评价记录（排除自身）
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EvaluationRecord::getParkId, saveDTO.getParkId())
                .eq(EvaluationRecord::getYear, saveDTO.getYear());
        if (saveDTO.getId() != null) {
            wrapper.ne(EvaluationRecord::getId, saveDTO.getId());
        }
        Long count = evaluationMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_EXISTS, "该园区该年度的评价记录已存在");
        }

        EvaluationRecord record = new EvaluationRecord();
        BeanUtils.copyProperties(saveDTO, record);

        if (saveDTO.getId() != null) {
            // 修改
            EvaluationRecord existing = evaluationMapper.selectById(saveDTO.getId());
            if (existing == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
            }
            // 只有草稿状态才能修改
            if (existing.getStatus() != 0) {
                throw new BusinessException(ResultCode.FAILURE, "只有草稿状态的评价记录才能修改");
            }
            evaluationMapper.updateById(record);
            log.info("评价记录修改成功：id={}", saveDTO.getId());
        } else {
            // 新增
            record.setStatus(0); // 0=草稿
            evaluationMapper.insert(record);
            log.info("评价记录新增成功：parkId={}, year={}",
                    saveDTO.getParkId(), saveDTO.getYear());
        }
    }

    /**
     * 保存审核打分详情（市级审核时保存打分，status=2）
     *
     * @param id          评价记录ID
     * @param scoreDetail 打分详情JSON
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAuditDetail(Long id, String scoreDetail) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        // 允许 status=1(待区县审)或2(待市局审)时保存
        if (record.getStatus() != 1 && record.getStatus() != 2) {
            throw new BusinessException(ResultCode.FAILURE, "当前状态不允许保存打分");
        }
        record.setScoreDetail(scoreDetail);
        evaluationMapper.updateById(record);
        log.info("审核打分已保存：id={}", id);
    }

    /**
     * 提交评价（状态从 0=草稿 改为 1=待区县审）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitEvaluation(Long id) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        if (record.getStatus() != 0) {
            throw new BusinessException(ResultCode.FAILURE, "只有草稿状态的评价记录才能提交");
        }

        record.setStatus(1); // 1=待区县审
        evaluationMapper.updateById(record);
        log.info("评价记录已提交：id={}", id);
    }

    /**
     * 区县审核通过（状态从 1=待区县审 改为 2=待市局审）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void districtPass(Long id) {
        updateStatus(id, 1, 2, "区县审核通过");
    }

    /**
     * 区县审核驳回（状态从 1=待区县审 改为 4=驳回）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void districtReject(Long id) {
        updateStatus(id, 1, 4, "区县审核驳回");
    }

    /**
     * 市级审核通过（状态从 2=待市局审 改为 3=通过）
     * 同步打分数据到 park_evaluation_score
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cityPass(Long id) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        if (!Integer.valueOf(2).equals(record.getStatus())) {
            throw new BusinessException(ResultCode.FAILURE,
                    "当前状态不允许执行此操作，期望状态：2，实际状态：" + record.getStatus());
        }

        // 1. 更新审核状态
        record.setStatus(3); // 3=通过
        evaluationMapper.updateById(record);
        log.info("评价记录市级审核通过：id={}", id);

        // 2. 同步打分到 park_evaluation_score
        syncToParkEvaluationScore(record);
    }

    /**
     * 市级审核驳回（状态从 2=待市局审 改为 4=驳回）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cityReject(Long id) {
        updateStatus(id, 2, 4, "市级审核驳回");
    }

    /**
     * 更新评价记录状态
     *
     * @param id             评价记录ID
     * @param expectedStatus 期望的当前状态
     * @param newStatus      新状态
     * @param actionDesc     操作描述（用于日志）
     */
    private void updateStatus(Long id, Integer expectedStatus, Integer newStatus, String actionDesc) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        if (!expectedStatus.equals(record.getStatus())) {
            throw new BusinessException(ResultCode.FAILURE,
                    "当前状态不允许执行此操作，期望状态：" + expectedStatus + "，实际状态：" + record.getStatus());
        }

        record.setStatus(newStatus);
        evaluationMapper.updateById(record);
        log.info("评价记录{}：id={}", actionDesc, id);
    }

    /**
     * 审核通过时同步打分到 park_evaluation_score 表
     * 合并自动计算分数 + 手动打分，写入业务评分表
     */
    private void syncToParkEvaluationScore(EvaluationRecord record) {
        Long parkId = record.getParkId();
        Integer year = record.getYear();

        // 1. 获取自动计算分数
        Map<String, Map<String, BigDecimal>> autoScores = autoCalculationService.calculateAutoScores(parkId, year);

        // 2. 解析手动打分（scoreDetail JSON）
        Map<String, Map<String, BigDecimal>> manualScores = parseManualScores(record.getScoreDetail());

        // 3. 合并：手动覆盖自动
        mergeManualScores(autoScores, manualScores);

        // 4. 获取 ParkInfo 以填充基础字段
        ParkInfo park = parkMapper.selectById(parkId);

        // 5. 查找或创建 ParkEvaluationScore 记录
        LambdaQueryWrapper<ParkEvaluationScore> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(ParkEvaluationScore::getParkId, parkId)
                    .eq(ParkEvaluationScore::getYear, year);
        ParkEvaluationScore score = scoreMapper.selectOne(existWrapper);
        if (score == null) {
            score = new ParkEvaluationScore();
            score.setParkId(parkId);
            score.setYear(year);
        }

        // 基础信息
        if (park != null) {
            score.setLeadingIndustry(park.getLeadingIndustry());
            score.setEnterpriseTotal(park.getEnterpriseCount());
        }

        // 同步亩均税收、亩均产出（从数据仓库导入的税收数据计算）
        BigDecimal[] perMu = autoCalculationService.calcPerMu(parkId, year);
        score.setTaxPerMu(perMu[0]);
        score.setRevenuePerMu(perMu[1]);

        // 6个维度总分
        BigDecimal industryDev = sumGroup(autoScores, "industryDev");
        BigDecimal entCultivate = sumGroup(autoScores, "entCultivate");
        BigDecimal techInnovation = sumGroup(autoScores, "techInnovation");
        BigDecimal serviceCap = sumGroup(autoScores, "serviceCap");
        BigDecimal benefitOutput = sumGroup(autoScores, "benefitOutput");
        BigDecimal safetyProd = sumGroup(autoScores, "safetyProd");
        BigDecimal other = sumGroup(autoScores, "other");

        score.setIndustryDevScore(industryDev);
        score.setEnterpriseCultivateScore(entCultivate);
        score.setTechInnovationScore(techInnovation);
        score.setServiceCapabilityScore(serviceCap);
        score.setBenefitOutputScore(benefitOutput);
        score.setSafetyProductionScore(safetyProd);
        score.setOtherScore(other);

        // 总得分
        BigDecimal total = industryDev.add(entCultivate).add(techInnovation)
                .add(serviceCap).add(benefitOutput).add(safetyProd).add(other);
        score.setTotalScore(total);
        score.setDataSource("audit");

        // 序列化 scoresJson
        try {
            score.setScoresJson(OBJECT_MAPPER.writeValueAsString(autoScores));
        } catch (Exception e) {
            log.warn("序列化 scoresJson 失败: parkId={}, year={}", parkId, year, e);
        }

        // 同步 grade
        score.setGrade(record.getGrade());

        // upsert
        if (score.getId() != null) {
            scoreMapper.updateById(score);
            log.info("更新 park_evaluation_score: parkId={}, year={}, totalScore={}, grade={}",
                    parkId, year, score.getTotalScore(), score.getGrade());
        } else {
            scoreMapper.insert(score);
            log.info("新增 park_evaluation_score: parkId={}, year={}, totalScore={}, grade={}",
                    parkId, year, score.getTotalScore(), score.getGrade());
        }

        // 同步回 evaluation_record
        record.setTotalScore(score.getTotalScore());
        record.setGrade(score.getGrade());
        evaluationMapper.updateById(record);
    }

    /**
     * 解析手动打分 JSON → 维度->子指标 Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Map<String, BigDecimal>> parseManualScores(String scoreDetailJson) {
        Map<String, Map<String, BigDecimal>> result = new LinkedHashMap<>();
        if (scoreDetailJson == null || scoreDetailJson.isEmpty()) {
            return result;
        }
        try {
            Map<String, Object> detail = OBJECT_MAPPER.readValue(scoreDetailJson,
                    new TypeReference<Map<String, Object>>() {});

            // 企业培育③
            Map<String, Object> enterprise = (Map<String, Object>) detail.get("enterprise");
            if (enterprise != null && enterprise.get("score") != null) {
                Map<String, BigDecimal> ent = new LinkedHashMap<>();
                ent.put("3", toBigDecimal(enterprise.get("score")));
                result.put("entCultivate", ent);
            }

            // 科技创新④
            Map<String, Object> tech = (Map<String, Object>) detail.get("tech");
            if (tech != null && tech.get("score") != null) {
                Map<String, BigDecimal> t = new LinkedHashMap<>();
                t.put("4", toBigDecimal(tech.get("score")));
                result.put("techInnovation", t);
            }

            // 服务能力①~⑦
            Map<String, Object> service = (Map<String, Object>) detail.get("service");
            if (service != null) {
                List<Object> scores = (List<Object>) service.get("scores");
                if (scores != null && !scores.isEmpty()) {
                    Map<String, BigDecimal> svc = new LinkedHashMap<>();
                    for (int i = 0; i < Math.min(scores.size(), 7); i++) {
                        svc.put(String.valueOf(i + 1), toBigDecimal(scores.get(i)));
                    }
                    result.put("serviceCap", svc);
                }
            }

            // 效益产出①~⑥
            Map<String, BigDecimal> benefitMap = new LinkedHashMap<>();
            for (int i = 1; i <= 6; i++) {
                String key = "benefitScore" + i;
                if (detail.get(key) != null) {
                    benefitMap.put(String.valueOf(i), toBigDecimal(detail.get(key)));
                }
            }
            if (!benefitMap.isEmpty()) {
                result.put("benefitOutput", benefitMap);
            }

            // 安全生产①~⑤
            Map<String, Object> safety = (Map<String, Object>) detail.get("safety");
            if (safety != null) {
                List<Object> sScores = (List<Object>) safety.get("scores");
                if (sScores != null && !sScores.isEmpty()) {
                    Map<String, BigDecimal> s = new LinkedHashMap<>();
                    for (int i = 0; i < Math.min(sScores.size(), 5); i++) {
                        s.put(String.valueOf(i + 1), toBigDecimal(sScores.get(i)));
                    }
                    result.put("safetyProd", s);
                }
            }

            // 其他②③
            Map<String, Object> other = (Map<String, Object>) detail.get("other");
            if (other != null) {
                Map<String, BigDecimal> o = new LinkedHashMap<>();
                if (other.get("dGrade") != null) {
                    // D档 → score记为0
                    o.put("2", BigDecimal.ZERO);
                }
                if (other.get("score") != null) {
                    o.put("3", toBigDecimal(other.get("score")));
                }
                if (!o.isEmpty()) {
                    result.put("other", o);
                }
            }
        } catch (Exception e) {
            log.warn("解析 scoreDetail JSON 失败", e);
        }
        return result;
    }

    /**
     * 将手动打分合并到自动打分中（手动覆盖自动）
     */
    private void mergeManualScores(Map<String, Map<String, BigDecimal>> autoScores,
                                    Map<String, Map<String, BigDecimal>> manualScores) {
        if (manualScores.isEmpty()) return;

        for (Map.Entry<String, Map<String, BigDecimal>> entry : manualScores.entrySet()) {
            String group = entry.getKey();
            Map<String, BigDecimal> defaultGroup = autoScores.computeIfAbsent(group, k -> new LinkedHashMap<>());
            defaultGroup.putAll(entry.getValue());
        }

        // 重新计算被修改的维度的 total
        for (String group : manualScores.keySet()) {
            Map<String, BigDecimal> g = autoScores.get(group);
            if (g != null) {
                BigDecimal newTotal = g.entrySet().stream()
                        .filter(e -> !"total".equals(e.getKey()))
                        .map(Map.Entry::getValue)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                g.put("total", newTotal);
            }
        }
    }

    private BigDecimal sumGroup(Map<String, Map<String, BigDecimal>> scores, String groupName) {
        Map<String, BigDecimal> group = scores.get(groupName);
        if (group == null) return BigDecimal.ZERO;
        return group.getOrDefault("total", BigDecimal.ZERO);
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).doubleValue());
        try {
            return new BigDecimal(val.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 绩效评定：生成本年度各园区绩效分档结果（A/B/C/D）
     * 规则：
     * 1. 该年度所有评价记录必须全部市级审核通过（status=3）
     * 2. 已获评四星级、五星级园区直接定为A档，不占A档比例
     * 3. 剩余园区按总得分降序排序，按 A≤20%、B≤30%、C≈45%、D≥5% 分档
     * 4. 支持多次评定，直接覆盖旧 grade
     *
     * @param year 评价年度
     */
    @Transactional(rollbackFor = Exception.class)
    public void performParkGradeEvaluation(Integer year) {
        if (year == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "年度参数不能为空");
        }

        // 1. 校验该年度所有评价记录均已市级审核通过（status=3）
        LambdaQueryWrapper<EvaluationRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(EvaluationRecord::getYear, year);
        List<EvaluationRecord> records = evaluationMapper.selectList(recordWrapper);
        if (records.isEmpty()) {
            throw new BusinessException(ResultCode.FAILURE, year + "年度无评价记录，请先发起年度填报");
        }
        long notPassed = records.stream()
                .filter(r -> r.getStatus() == null || r.getStatus() != 3)
                .count();
        if (notPassed > 0) {
            throw new BusinessException(ResultCode.FAILURE,
                    year + "年度仍有 " + notPassed + " 条评价记录未完成市级审核，无法进行绩效评定");
        }

        // 2. 查询该年度所有园区评分
        LambdaQueryWrapper<ParkEvaluationScore> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(ParkEvaluationScore::getYear, year);
        List<ParkEvaluationScore> scores = scoreMapper.selectList(scoreWrapper);
        if (scores.isEmpty()) {
            throw new BusinessException(ResultCode.FAILURE, year + "年度无评分数据");
        }

        // 3. 查询园区星级信息
        List<Long> parkIds = scores.stream().map(ParkEvaluationScore::getParkId).distinct().collect(Collectors.toList());
        Map<Long, ParkInfo> parkMap = new LinkedHashMap<>();
        if (!parkIds.isEmpty()) {
            for (ParkInfo p : parkMapper.selectBatchIds(parkIds)) {
                parkMap.put(p.getId(), p);
            }
        }

        // 4. 分离四星/五星园区（直接A档）和剩余园区
        List<ParkEvaluationScore> starScores = new ArrayList<>();
        List<ParkEvaluationScore> normalScores = new ArrayList<>();
        for (ParkEvaluationScore score : scores) {
            ParkInfo park = parkMap.get(score.getParkId());
            Integer starLevel = park != null ? park.getStarLevel() : null;
            if (starLevel != null && (starLevel == 4 || starLevel == 5)) {
                starScores.add(score);
            } else {
                normalScores.add(score);
            }
        }

        // 5. 剩余园区按总得分降序排序
        normalScores.sort((a, b) -> {
            BigDecimal ta = a.getTotalScore() != null ? a.getTotalScore() : BigDecimal.ZERO;
            BigDecimal tb = b.getTotalScore() != null ? b.getTotalScore() : BigDecimal.ZERO;
            return tb.compareTo(ta);
        });

        // 6. 按比例分档：A≤20%、B≤30%、D≥5%、C≈45%
        int n = normalScores.size();
        int aCount = (int) Math.floor(n * 0.20);
        int bCount = (int) Math.floor(n * 0.30);
        int dCount = (int) Math.ceil(n * 0.05);
        // 兜底：确保 aCount + bCount + dCount 不超过 n
        if (aCount + bCount + dCount > n) {
            dCount = Math.max(0, n - aCount - bCount);
        }
        int cCount = n - aCount - bCount - dCount;

        // 7. 分配档位
        Map<Long, String> gradeMap = new LinkedHashMap<>();
        for (ParkEvaluationScore s : starScores) {
            gradeMap.put(s.getParkId(), "A");
        }
        for (int i = 0; i < n; i++) {
            String grade;
            if (i < aCount) {
                grade = "A";
            } else if (i < aCount + bCount) {
                grade = "B";
            } else if (i < aCount + bCount + cCount) {
                grade = "C";
            } else {
                grade = "D";
            }
            gradeMap.put(normalScores.get(i).getParkId(), grade);
        }

        // 8. 更新 park_evaluation_score.grade 和 evaluation_record.grade
        for (ParkEvaluationScore score : scores) {
            String grade = gradeMap.get(score.getParkId());
            if (grade != null) {
                score.setGrade(grade);
                scoreMapper.updateById(score);
            }
        }
        for (EvaluationRecord record : records) {
            String grade = gradeMap.get(record.getParkId());
            if (grade != null) {
                record.setGrade(grade);
                evaluationMapper.updateById(record);
            }
        }

        log.info("绩效评定完成：year={}, 参评园区={}, 四五星A档={}, 常规分档(A={},B={},C={},D={})",
                year, scores.size(), starScores.size(), aCount, bCount, cCount, dCount);
    }

    /**
     * 查询园区评价导出数据
     */
    public List<ParkEvaluationScore> queryParkEvaluationExportData(EvaluationQueryDTO queryDTO) {
        List<Long> parkIds;

        // 1. 如果已指定园区ID（数据权限），直接使用
        if (queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty()) {
            parkIds = new ArrayList<>(queryDTO.getParkIds());
        } else if (queryDTO.getParkId() != null) {
            parkIds = Collections.singletonList(queryDTO.getParkId());
        } else {
            // 根据园区查询条件过滤 parkId
            LambdaQueryWrapper<ParkInfo> parkWrapper = new LambdaQueryWrapper<>();
            if (queryDTO.getParkName() != null && !queryDTO.getParkName().trim().isEmpty()) {
                parkWrapper.like(ParkInfo::getParkName, queryDTO.getParkName().trim());
            }
            if (queryDTO.getRegion() != null && !queryDTO.getRegion().trim().isEmpty()) {
                parkWrapper.eq(ParkInfo::getDistrictName, queryDTO.getRegion().trim());
            }
            if (queryDTO.getType() != null && !queryDTO.getType().trim().isEmpty()) {
                parkWrapper.eq(ParkInfo::getParkType, queryDTO.getType().trim());
            }
            parkIds = parkMapper.selectList(parkWrapper).stream()
                    .map(ParkInfo::getId)
                    .collect(Collectors.toList());
        }

        if (parkIds == null || parkIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 查询评分记录
        LambdaQueryWrapper<ParkEvaluationScore> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.in(ParkEvaluationScore::getParkId, parkIds);
        if (queryDTO.getYear() != null) {
            scoreWrapper.eq(ParkEvaluationScore::getYear, queryDTO.getYear());
        }
        scoreWrapper.orderByAsc(ParkEvaluationScore::getParkId);
        return scoreMapper.selectList(scoreWrapper);
    }

    /**
     * 导出园区评价汇总表（简版）
     * 按 parkType 分两个 Sheet（生产性制造类/生产性服务类）
     */
    public byte[] exportParkEvaluationList(EvaluationQueryDTO queryDTO) {
        List<ParkEvaluationScore> scores = queryParkEvaluationExportData(queryDTO);
        Map<Long, ParkInfo> parkMap = getParkMap(scores);

        String[] headers = {"序号", "园区名称", "亩均营收（万元/亩）", "亩均税收（万元/亩）",
                "产业发展", "企业培育", "科技创新", "服务能力", "效益产出", "安全生产", "其他", "总得分", "绩效分档"};

        // 按 parkType 分组
        Map<String, List<ParkEvaluationScore>> grouped = groupScoresByParkType(scores, parkMap);

        try (Workbook wb = new XSSFWorkbook()) {
            for (Map.Entry<String, List<ParkEvaluationScore>> entry : grouped.entrySet()) {
                Sheet sheet = wb.createSheet(entry.getKey());
                createHeaderRow(wb, sheet, headers);
                int rowIdx = 1;
                List<ParkEvaluationScore> groupScores = entry.getValue();
                for (int i = 0; i < groupScores.size(); i++) {
                    ParkEvaluationScore score = groupScores.get(i);
                    ParkInfo park = parkMap.get(score.getParkId());
                    Row row = sheet.createRow(rowIdx++);
                    int col = 0;
                    row.createCell(col++).setCellValue(i + 1);
                    row.createCell(col++).setCellValue(park != null ? park.getParkName() : "");
                    setCellBigDecimal(row.createCell(col++), score.getRevenuePerMu());
                    setCellBigDecimal(row.createCell(col++), score.getTaxPerMu());
                    setCellBigDecimal(row.createCell(col++), score.getIndustryDevScore());
                    setCellBigDecimal(row.createCell(col++), score.getEnterpriseCultivateScore());
                    setCellBigDecimal(row.createCell(col++), score.getTechInnovationScore());
                    setCellBigDecimal(row.createCell(col++), score.getServiceCapabilityScore());
                    setCellBigDecimal(row.createCell(col++), score.getBenefitOutputScore());
                    setCellBigDecimal(row.createCell(col++), score.getSafetyProductionScore());
                    setCellBigDecimal(row.createCell(col++), score.getOtherScore());
                    setCellBigDecimal(row.createCell(col++), score.getTotalScore());
                    row.createCell(col++).setCellValue(score.getGrade() != null ? score.getGrade() : "");
                }
                autoSizeColumns(sheet, headers.length);
            }
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                wb.write(out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            log.error("导出园区评价简版失败", e);
            throw new BusinessException(ResultCode.FAILURE, "导出失败：" + e.getMessage());
        }
    }

    /**
     * 按 parkType 分组评分数据（生产性制造类/生产性服务类/其他）
     */
    private Map<String, List<ParkEvaluationScore>> groupScoresByParkType(List<ParkEvaluationScore> scores,
                                                                          Map<Long, ParkInfo> parkMap) {
        Map<String, List<ParkEvaluationScore>> grouped = new LinkedHashMap<>();
        // 保证制造类、服务类优先排序
        grouped.put("生产性制造类", new ArrayList<>());
        grouped.put("生产性服务类", new ArrayList<>());
        for (ParkEvaluationScore score : scores) {
            ParkInfo park = parkMap.get(score.getParkId());
            String parkType = park != null && park.getParkType() != null ? park.getParkType() : "其他";
            grouped.computeIfAbsent(parkType, k -> new ArrayList<>()).add(score);
        }
        // 移除空分组
        grouped.values().removeIf(List::isEmpty);
        return grouped;
    }

    /**
     * 查询园区评价汇总表（简版）分页列表
     */
    public IPage<Map<String, Object>> getParkEvaluationList(EvaluationQueryDTO queryDTO) {
        List<ParkEvaluationScore> scores = queryParkEvaluationExportData(queryDTO);
        Map<Long, ParkInfo> parkMap = getParkMap(scores);

        List<Map<String, Object>> records = new ArrayList<>();
        for (ParkEvaluationScore score : scores) {
            ParkInfo park = parkMap.get(score.getParkId());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("parkName", park != null ? park.getParkName() : "");
            row.put("revenuePerMu", score.getRevenuePerMu());
            row.put("taxPerMu", score.getTaxPerMu());
            row.put("industryDevScore", score.getIndustryDevScore());
            row.put("enterpriseCultivateScore", score.getEnterpriseCultivateScore());
            row.put("techInnovationScore", score.getTechInnovationScore());
            row.put("serviceCapabilityScore", score.getServiceCapabilityScore());
            row.put("benefitOutputScore", score.getBenefitOutputScore());
            row.put("safetyProductionScore", score.getSafetyProductionScore());
            row.put("otherScore", score.getOtherScore());
            row.put("totalScore", score.getTotalScore());
            row.put("grade", score.getGrade());
            records.add(row);
        }

        return buildPage(records, queryDTO);
    }

    /**
     * 查询园区评价汇总表（详版）分页列表
     */
    public IPage<Map<String, Object>> getParkEvaluationDetail(EvaluationQueryDTO queryDTO) {
        List<ParkEvaluationScore> scores = queryParkEvaluationExportData(queryDTO);
        Map<Long, ParkInfo> parkMap = getParkMap(scores);

        List<Map<String, Object>> records = new ArrayList<>();
        for (ParkEvaluationScore score : scores) {
            ParkInfo park = parkMap.get(score.getParkId());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("parkName", park != null ? park.getParkName() : "");
            row.put("districtName", park != null ? park.getDistrictName() : "");
            row.put("parkType", park != null ? park.getParkType() : "");
            row.put("enterpriseTotal", score.getEnterpriseTotal());
            row.put("leadingIndustry", score.getLeadingIndustry());
            row.put("revenuePerMu", score.getRevenuePerMu());
            row.put("taxPerMu", score.getTaxPerMu());

            Map<String, Map<String, BigDecimal>> detail = parseScoresJson(score.getScoresJson());
            fillDimension(row, detail, "industryDev", 14);
            fillDimension(row, detail, "entCultivate", 19);
            fillDimension(row, detail, "techInnovation", 19);
            fillDimension(row, detail, "serviceCap", 7);
            fillDimension(row, detail, "benefitOutput", 9);
            fillDimension(row, detail, "safetyProd", 9);
            fillDimension(row, detail, "other", 2);

            row.put("totalScore", score.getTotalScore());
            row.put("grade", score.getGrade());
            records.add(row);
        }

        return buildPage(records, queryDTO);
    }

    private void fillDimension(Map<String, Object> row,
                               Map<String, Map<String, BigDecimal>> detail,
                               String prefix,
                               int count) {
        Map<String, BigDecimal> group = detail.get(prefix);
        if (group == null) {
            group = new LinkedHashMap<>();
        }
        for (int i = 1; i <= count; i++) {
            row.put(prefix + "_" + i, group.getOrDefault(String.valueOf(i), BigDecimal.ZERO));
        }
        row.put(prefix + "_total", group.getOrDefault("total", BigDecimal.ZERO));
    }

    private IPage<Map<String, Object>> buildPage(List<Map<String, Object>> records, EvaluationQueryDTO queryDTO) {
        int pageNum = queryDTO.getPageNum() != null && queryDTO.getPageNum() > 0 ? queryDTO.getPageNum() : 1;
        int pageSize = queryDTO.getPageSize() != null && queryDTO.getPageSize() > 0 ? queryDTO.getPageSize() : 20;
        int total = records.size();
        int fromIndex = Math.min((pageNum - 1) * pageSize, total);
        int toIndex = Math.min(pageNum * pageSize, total);
        List<Map<String, Object>> pageRecords = records.subList(fromIndex, toIndex);

        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);
        page.setTotal(total);
        page.setRecords(pageRecords);
        return page;
    }

    /**
     * 导出园区评价汇总表（详版）
     * 按 parkType 分两个 Sheet（生产性制造类/生产性服务类）
     */
    public byte[] exportParkEvaluationDetail(EvaluationQueryDTO queryDTO) {
        List<ParkEvaluationScore> scores = queryParkEvaluationExportData(queryDTO);
        Map<Long, ParkInfo> parkMap = getParkMap(scores);

        String[] headers = {
                "序号", "园区名称", "所属区域", "园区类型", "参评企业总数", "主导产业",
                "亩均营收（万元/亩）", "亩均税收（万元/亩）",
                "五大产业业态聚集度", "主导产业企业及营收占比提升", "生产性服务企业数量与产值", "产业发展合计",
                "新增规上企业1", "新增规上企业2", "制造业单项冠军", "新增上市企业", "小巨人/隐形冠军",
                "省专精特新/国高企", "创新型中小企业", "投早投小案例", "企业培育合计",
                "首台(套)", "首版次软件", "首批新材料", "工业新产品", "浙江制造精品",
                "国家级研发机构", "省级研发机构", "市级研发机构", "公共服务平台", "高层次人才", "科研成果转化",
                "科技创新机制", "企业服务机制", "园区大脑建设", "信息发布平台", "数字化物业",
                "数字化管理机构", "安全生产/数字化监管", "普惠性服务活动", "与其他小微园合作", "服务能力合计",
                "亩均税收对比", "工业上楼效益提升", "效益产出合计",
                "未落实安全管理通则", "未签消防责任书", "未落实培训演练", "消防设施不完整", "被省市通报", "是否D档", "安全生产合计",
                "季度数据少报", "年度数据未报", "是否D档（其他）", "媒体负面报道", "其他合计",
                "总分", "绩效分档"
        };

        Map<String, List<ParkEvaluationScore>> grouped = groupScoresByParkType(scores, parkMap);

        try (Workbook wb = new XSSFWorkbook()) {
            for (Map.Entry<String, List<ParkEvaluationScore>> entry : grouped.entrySet()) {
                Sheet sheet = wb.createSheet(entry.getKey());
                createHeaderRow(wb, sheet, headers);
                int rowIdx = 1;
                List<ParkEvaluationScore> groupScores = entry.getValue();
                for (int i = 0; i < groupScores.size(); i++) {
                    Row row = sheet.createRow(rowIdx++);
                    writeDetailRow(row, i + 1, groupScores.get(i), parkMap);
                }
                autoSizeColumns(sheet, headers.length);
            }
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                wb.write(out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            log.error("导出园区评价详版失败", e);
            throw new BusinessException(ResultCode.FAILURE, "导出失败：" + e.getMessage());
        }
    }

    /**
     * 写入详版单行数据
     */
    private void writeDetailRow(Row row, int index, ParkEvaluationScore score, Map<Long, ParkInfo> parkMap) {
        ParkInfo park = parkMap.get(score.getParkId());
        Map<String, Map<String, BigDecimal>> detail = parseScoresJson(score.getScoresJson());

        int col = 0;
        row.createCell(col++).setCellValue(index);
        row.createCell(col++).setCellValue(park != null ? park.getParkName() : "");
        row.createCell(col++).setCellValue(park != null ? park.getDistrictName() : "");
        row.createCell(col++).setCellValue(park != null ? park.getParkType() : "");
        setCellInt(row.createCell(col++), score.getEnterpriseTotal());
        row.createCell(col++).setCellValue(score.getLeadingIndustry() != null ? score.getLeadingIndustry() : "");
        setCellBigDecimal(row.createCell(col++), score.getRevenuePerMu());
        setCellBigDecimal(row.createCell(col++), score.getTaxPerMu());

        // 产业发展 12 列
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "1"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "2"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "3"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "total"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "4"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "5"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "6"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "7"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "8"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "9"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "10"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "industryDev", "11"));

        // 企业培育 12 列
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "total"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "1"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "2"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "3"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "4"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "5"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "6"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "7"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "8"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "9"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "10"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "entCultivate", "11"));

        // 科技创新 + 服务能力 10 列
        setCellBigDecimal(row.createCell(col++), getScore(detail, "techInnovation", "1"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "techInnovation", "2"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "techInnovation", "3"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "techInnovation", "4"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "techInnovation", "5"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "techInnovation", "6"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "techInnovation", "7"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "techInnovation", "8"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "techInnovation", "9"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "serviceCap", "total"));

        // 效益产出 3 列
        setCellBigDecimal(row.createCell(col++), getScore(detail, "benefitOutput", "1"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "benefitOutput", "2"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "benefitOutput", "total"));

        // 安全生产 7 列
        setCellBigDecimal(row.createCell(col++), getScore(detail, "safetyProd", "1"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "safetyProd", "2"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "safetyProd", "3"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "safetyProd", "4"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "safetyProd", "5"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "safetyProd", "6"));
        row.createCell(col++).setCellValue("D".equals(score.getGrade()) ? "是" : "否");
        setCellBigDecimal(row.createCell(col++), getScore(detail, "safetyProd", "total"));

        // 其他 5 列
        setCellBigDecimal(row.createCell(col++), getScore(detail, "other", "1"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "other", "2"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "other", "3"));
        row.createCell(col++).setCellValue("D".equals(score.getGrade()) ? "是" : "否");
        setCellBigDecimal(row.createCell(col++), getScore(detail, "other", "4"));
        setCellBigDecimal(row.createCell(col++), getScore(detail, "other", "total"));

        setCellBigDecimal(row.createCell(col++), score.getTotalScore());
        row.createCell(col++).setCellValue(score.getGrade() != null ? score.getGrade() : "");
    }

    private Map<Long, ParkInfo> getParkMap(List<ParkEvaluationScore> scores) {
        List<Long> parkIds = scores.stream().map(ParkEvaluationScore::getParkId).distinct().collect(Collectors.toList());
        Map<Long, ParkInfo> parkMap = new LinkedHashMap<>();
        if (!parkIds.isEmpty()) {
            for (ParkInfo p : parkMapper.selectBatchIds(parkIds)) {
                parkMap.put(p.getId(), p);
            }
        }
        return parkMap;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, BigDecimal>> parseScoresJson(String scoresJson) {
        Map<String, Map<String, BigDecimal>> result = new LinkedHashMap<>();
        if (scoresJson == null || scoresJson.trim().isEmpty()) {
            return result;
        }
        try {
            Map<String, Object> root = OBJECT_MAPPER.readValue(scoresJson, new TypeReference<Map<String, Object>>() {});
            for (Map.Entry<String, Object> entry : root.entrySet()) {
                Map<String, BigDecimal> group = new LinkedHashMap<>();
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> e : map.entrySet()) {
                        group.put(e.getKey(), toBigDecimal(e.getValue()));
                    }
                }
                result.put(entry.getKey(), group);
            }
        } catch (Exception e) {
            log.warn("解析 scoresJson 失败", e);
        }
        return result;
    }

    private BigDecimal getScore(Map<String, Map<String, BigDecimal>> detail, String group, String key) {
        Map<String, BigDecimal> g = detail.get(group);
        if (g == null) return BigDecimal.ZERO;
        return g.getOrDefault(key, BigDecimal.ZERO);
    }

    private void createHeaderRow(Workbook wb, Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void setCellBigDecimal(Cell cell, BigDecimal val) {
        if (val == null) {
            cell.setCellValue("");
        } else {
            cell.setCellValue(val.doubleValue());
        }
    }

    private void setCellInt(Cell cell, Integer val) {
        if (val == null) {
            cell.setCellValue("");
        } else {
            cell.setCellValue(val);
        }
    }

    private void autoSizeColumns(Sheet sheet, int count) {
        for (int i = 0; i < count; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.min(width + 512, 8192));
        }
    }
}
