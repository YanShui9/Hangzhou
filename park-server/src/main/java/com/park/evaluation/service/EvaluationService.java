package com.park.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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

    public EvaluationService(EvaluationMapper evaluationMapper,
                             ParkEvaluationScoreMapper scoreMapper,
                             ParkMapper parkMapper,
                             AutoCalculationService autoCalculationService) {
        this.evaluationMapper = evaluationMapper;
        this.scoreMapper = scoreMapper;
        this.parkMapper = parkMapper;
        this.autoCalculationService = autoCalculationService;
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
        Page<EvaluationRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (queryDTO.getParkId() != null) {
            wrapper.eq(EvaluationRecord::getParkId, queryDTO.getParkId());
        }
        // 支持多园区查询（区县管理员使用）
        if (queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty()) {
            wrapper.in(EvaluationRecord::getParkId, queryDTO.getParkIds());
        }
        if (queryDTO.getYear() != null) {
            wrapper.eq(EvaluationRecord::getYear, queryDTO.getYear());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(EvaluationRecord::getStatus, queryDTO.getStatus());
        }

        // 默认按创建时间降序排序
        wrapper.orderByDesc(EvaluationRecord::getCreateTime);

        return evaluationMapper.selectPage(page, wrapper);
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
            score.setLeadingIndustry(park.getMainIndustry());
            score.setEnterpriseTotal(park.getEnterpriseCount());
        }

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

            // 安全生产①~④
            Map<String, Object> safety = (Map<String, Object>) detail.get("safety");
            if (safety != null) {
                List<Object> sScores = (List<Object>) safety.get("scores");
                if (sScores != null && !sScores.isEmpty()) {
                    Map<String, BigDecimal> s = new LinkedHashMap<>();
                    for (int i = 0; i < Math.min(sScores.size(), 4); i++) {
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
}
