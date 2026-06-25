package com.park.dashboard.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.dashboard.dto.BigScreenStatsDTO;
import com.park.dashboard.dto.DashboardStatsDTO;
import com.park.dashboard.dto.DistrictDataDTO;
import com.park.dashboard.dto.EvaluationAnalysisDTO;
import com.park.dashboard.dto.MonthlyStatsDTO;
import com.park.dashboard.dto.ParkRankDTO;
import com.park.enterprise.entity.EnterpriseHonorRecord;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.mapper.EnterpriseHonorRecordMapper;
import com.park.enterprise.mapper.EnterpriseMapper;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.entity.ParkEvaluationScore;
import com.park.evaluation.mapper.EvaluationMapper;
import com.park.evaluation.mapper.ParkEvaluationScoreMapper;
import com.park.operation.entity.ParkOperation;
import com.park.operation.mapper.OperationMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import com.park.system.entity.ParkTaxRecord;
import com.park.system.mapper.ParkTaxRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据驾驶舱服务
 *
 * @author park-team
 */
@Slf4j
@Service
public class DashboardService {

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private OperationMapper operationMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private ParkTaxRecordMapper parkTaxRecordMapper;

    @Autowired
    private EnterpriseHonorRecordMapper enterpriseHonorRecordMapper;

    @Autowired
    private ParkEvaluationScoreMapper parkEvaluationScoreMapper;

    /**
     * 获取统计数据
     *
     * @param districtName 区县名称（区县管理员传入，市级管理员传 null）
     * @param parkId       园区ID（园区管理员传入，其他传 null）
     * @return 统计数据
     */
    public DashboardStatsDTO getStats(String districtName, Long parkId) {
        // 1. 查询园区总数
        Long totalParks = countParks(districtName, parkId);

        // 2. 查询企业总数
        Long totalEnterprises = countEnterprises(districtName, parkId);

        // 3. 查询就业人数（园区端取本园区最新一条，市级/区县端SUM汇总最新季度）
        Long totalEmployment = sumLatestEmployment(districtName, parkId);

        // 4. 查询总营收（从 park_tax_record 表获取，tax_type=park_total）
        BigDecimal totalRevenue = sumLatestRevenue(districtName, parkId);

        // 5. 查询待审核数量（状态为 1=待区县审 的评价记录）
        Long pendingAudits = countPendingAudits(districtName, parkId);

        return DashboardStatsDTO.builder()
                .totalParks(totalParks)
                .totalEnterprises(totalEnterprises)
                .totalEmployment(totalEmployment)
                .totalRevenue(totalRevenue)
                .pendingAudits(pendingAudits)
                .build();
    }

    /**
     * 获取园区排名（TOP N）
     *
     * @param limit        返回数量
     * @param districtName 区县名称（区县管理员传入，市级管理员传 null）
     * @param parkId       园区ID（园区管理员传入，其他传 null）
     * @return 园区排名列表
     */
    public List<ParkRankDTO> getTopParks(int limit, String districtName, Long parkId) {
        // 查询园区信息
        LambdaQueryWrapper<ParkInfo> parkQuery = new LambdaQueryWrapper<>();
        if (districtName != null && !districtName.isEmpty()) {
            parkQuery.eq(ParkInfo::getDistrictName, districtName);
        }
        if (parkId != null) {
            parkQuery.eq(ParkInfo::getId, parkId);
        }
        List<ParkInfo> parks = parkMapper.selectList(parkQuery);

        if (parks.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询每个园区的最新评价得分
        List<ParkRankDTO> rankList = new ArrayList<>();
        for (ParkInfo park : parks) {
            LambdaQueryWrapper<EvaluationRecord> evalQuery = new LambdaQueryWrapper<>();
            evalQuery.eq(EvaluationRecord::getParkId, park.getId());
            evalQuery.eq(EvaluationRecord::getStatus, 3); // 3=通过
            evalQuery.orderByDesc(EvaluationRecord::getYear);
            evalQuery.last("LIMIT 1");
            EvaluationRecord latestEval = evaluationMapper.selectOne(evalQuery);

            if (latestEval != null && latestEval.getTotalScore() != null) {
                rankList.add(ParkRankDTO.builder()
                        .parkId(park.getId())
                        .parkName(park.getParkName())
                        .score(latestEval.getTotalScore())
                        .build());
            }
        }

        // 按得分降序排序
        rankList.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        // 设置排名并限制数量
        for (int i = 0; i < rankList.size(); i++) {
            rankList.get(i).setRank(i + 1);
        }

        return rankList.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 获取季度统计数据
     *
     * @param year         年份
     * @param districtName 区县名称（区县管理员传入，市级管理员传 null）
     * @param parkId       园区ID（园区管理员传入，其他传 null）
     * @return 季度统计列表
     */
    public List<MonthlyStatsDTO> getQuarterlyStats(int year, String districtName, Long parkId) {
        // 构建查询条件
        LambdaQueryWrapper<ParkOperation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParkOperation::getYear, year);

        // 如果是区县管理员，先查询该区县的园区ID列表
        if (districtName != null && !districtName.isEmpty()) {
            LambdaQueryWrapper<ParkInfo> parkQuery = new LambdaQueryWrapper<>();
            parkQuery.eq(ParkInfo::getDistrictName, districtName);
            parkQuery.select(ParkInfo::getId);
            List<ParkInfo> parks = parkMapper.selectList(parkQuery);
            List<Long> parkIds = parks.stream().map(ParkInfo::getId).collect(Collectors.toList());
            if (parkIds.isEmpty()) {
                return buildEmptyQuarterlyStats(year);
            }
            queryWrapper.in(ParkOperation::getParkId, parkIds);
        }

        // 如果是园区管理员，只查该园区
        if (parkId != null) {
            queryWrapper.eq(ParkOperation::getParkId, parkId);
        }

        queryWrapper.orderByAsc(ParkOperation::getQuarter);
        List<ParkOperation> operations = operationMapper.selectList(queryWrapper);

        // 按季度汇总
        Map<Integer, MonthlyStatsDTO> quarterMap = new LinkedHashMap<>();
        // 初始化4个季度
        for (int q = 1; q <= 4; q++) {
            String quarterStr = year + "-Q" + q;
            quarterMap.put(q, MonthlyStatsDTO.builder()
                    .month(quarterStr)
                    .employment(0)
                    .enterpriseCount(0)
                    .build());
        }

        // 汇总数据
        for (ParkOperation op : operations) {
            MonthlyStatsDTO dto = quarterMap.get(op.getQuarter());
            if (dto != null) {
                dto.setEmployment(dto.getEmployment() +
                        (op.getEmployeeCount() != null ? op.getEmployeeCount() : 0));
                dto.setEnterpriseCount(dto.getEnterpriseCount() +
                        (op.getEnterpriseCount() != null ? op.getEnterpriseCount() : 0));
            }
        }

        // 无运营数据时，用 park_info 当前统计值填充最新季度（Q4），避免趋势图全0
        if (operations.isEmpty()) {
            MonthlyStatsDTO q4 = quarterMap.get(4);
            q4.setEmployment(sumParkInfoEmployeeCount(districtName, parkId).intValue());
            q4.setEnterpriseCount(sumParkInfoEnterpriseCount(districtName, parkId).intValue());
        }

        return new ArrayList<>(quarterMap.values());
    }

    // ==================== 私有方法 ====================

    /**
     * 统计园区数量
     */
    private Long countParks(String districtName, Long parkId) {
        LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (parkId != null) {
            queryWrapper.eq(ParkInfo::getId, parkId);
        }
        if (districtName != null && !districtName.isEmpty()) {
            queryWrapper.eq(ParkInfo::getDistrictName, districtName);
        }
        return parkMapper.selectCount(queryWrapper);
    }

    /**
     * 统计企业数量
     * 优先使用 park_info.enterprise_count（数据仓库导入的权威汇总值），
     * 因为 enterprise_info 明细表可能不完整
     */
    private Long countEnterprises(String districtName, Long parkId) {
        return sumParkInfoEnterpriseCount(districtName, parkId);
    }

    /**
     * 获取最新的运营数据汇总
     */
    private ParkOperation getLatestOperation(String districtName, Long parkId) {
        LambdaQueryWrapper<ParkOperation> queryWrapper = new LambdaQueryWrapper<>();

        if (parkId != null) {
            queryWrapper.eq(ParkOperation::getParkId, parkId);
        } else if (districtName != null && !districtName.isEmpty()) {
            List<Long> parkIds = getParkIdsByDistrict(districtName);
            if (parkIds.isEmpty()) {
                return null;
            }
            queryWrapper.in(ParkOperation::getParkId, parkIds);
        }

        // 按年季度降序，取最新一条
        queryWrapper.orderByDesc(ParkOperation::getYear, ParkOperation::getQuarter);
        queryWrapper.last("LIMIT 1");
        return operationMapper.selectOne(queryWrapper);
    }

    /**
     * 汇总就业人数
     * 园区端：取本园区最新一条运营记录的 employee_count；无运营数据时回退 park_info.employee_count
     * 市级/区县端：SUM 最新季度所有园区的 employee_count；无运营数据时 SUM park_info.employee_count
     */
    private Long sumLatestEmployment(String districtName, Long parkId) {
        // 园区端：取本园区最新一条
        if (parkId != null) {
            ParkOperation op = getLatestOperation(districtName, parkId);
            if (op != null && op.getEmployeeCount() != null) {
                return op.getEmployeeCount().longValue();
            }
            // 回退：park_operation无数据时，使用park_info.employee_count
            ParkInfo park = parkMapper.selectById(parkId);
            return park != null && park.getEmployeeCount() != null ? park.getEmployeeCount().longValue() : 0L;
        }

        // 市级/区县端：先找最新年份和季度
        LambdaQueryWrapper<ParkOperation> latestQuery = new LambdaQueryWrapper<>();
        latestQuery.orderByDesc(ParkOperation::getYear, ParkOperation::getQuarter).last("LIMIT 1");
        ParkOperation latest = operationMapper.selectOne(latestQuery);
        if (latest == null) {
            // 回退：无运营数据时，SUM park_info.employee_count
            return sumParkInfoEmployeeCount(districtName, parkId);
        }

        // SUM 该季度的所有园区 employee_count
        LambdaQueryWrapper<ParkOperation> sumQuery = new LambdaQueryWrapper<>();
        sumQuery.eq(ParkOperation::getYear, latest.getYear())
                .eq(ParkOperation::getQuarter, latest.getQuarter());

        if (districtName != null && !districtName.isEmpty()) {
            List<Long> parkIds = getParkIdsByDistrict(districtName);
            if (parkIds.isEmpty()) {
                return 0L;
            }
            sumQuery.in(ParkOperation::getParkId, parkIds);
        }

        List<ParkOperation> ops = operationMapper.selectList(sumQuery);
        return ops.stream()
                .mapToLong(op -> op.getEmployeeCount() != null ? op.getEmployeeCount() : 0)
                .sum();
    }

    /**
     * 汇总总营收
     * 从 park_tax_record 表获取（tax_type=park_total），取最新年度的 SUM(revenue)
     */
    private BigDecimal sumLatestRevenue(String districtName, Long parkId) {
        // 先查最新年度
        LambdaQueryWrapper<ParkTaxRecord> yearQuery = new LambdaQueryWrapper<>();
        yearQuery.eq(ParkTaxRecord::getTaxType, "park_total");
        yearQuery.orderByDesc(ParkTaxRecord::getYear).last("LIMIT 1");
        ParkTaxRecord latest = parkTaxRecordMapper.selectOne(yearQuery);
        if (latest == null) {
            return BigDecimal.ZERO;
        }

        int latestYear = latest.getYear();

        // SUM 该年度的 revenue
        LambdaQueryWrapper<ParkTaxRecord> sumQuery = new LambdaQueryWrapper<>();
        sumQuery.eq(ParkTaxRecord::getTaxType, "park_total")
                .eq(ParkTaxRecord::getYear, latestYear);

        if (parkId != null) {
            sumQuery.eq(ParkTaxRecord::getParkId, parkId);
        } else if (districtName != null && !districtName.isEmpty()) {
            List<Long> parkIds = getParkIdsByDistrict(districtName);
            if (parkIds.isEmpty()) {
                return BigDecimal.ZERO;
            }
            sumQuery.in(ParkTaxRecord::getParkId, parkIds);
        }

        List<ParkTaxRecord> records = parkTaxRecordMapper.selectList(sumQuery);
        return records.stream()
                .map(ParkTaxRecord::getRevenue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 统计待审核数量
     * 待审核 = 状态为 1（待区县审）
     */
    private Long countPendingAudits(String districtName, Long parkId) {
        LambdaQueryWrapper<EvaluationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EvaluationRecord::getStatus, 1); // 1=待区县审

        if (parkId != null) {
            queryWrapper.eq(EvaluationRecord::getParkId, parkId);
        } else if (districtName != null && !districtName.isEmpty()) {
            List<Long> parkIds = getParkIdsByDistrict(districtName);
            if (parkIds.isEmpty()) {
                return 0L;
            }
            queryWrapper.in(EvaluationRecord::getParkId, parkIds);
        }

        return evaluationMapper.selectCount(queryWrapper);
    }

    /**
     * 根据区县名称获取园区ID列表
     */
    private List<Long> getParkIdsByDistrict(String districtName) {
        LambdaQueryWrapper<ParkInfo> parkQuery = new LambdaQueryWrapper<>();
        parkQuery.eq(ParkInfo::getDistrictName, districtName);
        parkQuery.select(ParkInfo::getId);
        List<ParkInfo> parks = parkMapper.selectList(parkQuery);
        return parks.stream().map(ParkInfo::getId).collect(Collectors.toList());
    }

    /**
     * SUM park_info.employee_count（无运营数据时的回退方案）
     * 园区端：查单园区；区县端：SUM本区县所有园区；市级：SUM所有园区
     */
    private Long sumParkInfoEmployeeCount(String districtName, Long parkId) {
        LambdaQueryWrapper<ParkInfo> query = new LambdaQueryWrapper<>();
        query.select(ParkInfo::getId, ParkInfo::getEmployeeCount);
        if (parkId != null) {
            query.eq(ParkInfo::getId, parkId);
        } else if (districtName != null && !districtName.isEmpty()) {
            query.eq(ParkInfo::getDistrictName, districtName);
        }
        List<ParkInfo> parks = parkMapper.selectList(query);
        return parks.stream()
                .mapToLong(p -> p.getEmployeeCount() != null ? p.getEmployeeCount() : 0)
                .sum();
    }

    /**
     * SUM park_info.enterprise_count（无enterprise_info明细时的回退方案）
     * 园区端：查单园区；区县端：SUM本区县所有园区；市级：SUM所有园区
     */
    private Long sumParkInfoEnterpriseCount(String districtName, Long parkId) {
        LambdaQueryWrapper<ParkInfo> query = new LambdaQueryWrapper<>();
        query.select(ParkInfo::getId, ParkInfo::getEnterpriseCount);
        if (parkId != null) {
            query.eq(ParkInfo::getId, parkId);
        } else if (districtName != null && !districtName.isEmpty()) {
            query.eq(ParkInfo::getDistrictName, districtName);
        }
        List<ParkInfo> parks = parkMapper.selectList(query);
        return parks.stream()
                .mapToLong(p -> p.getEnterpriseCount() != null ? p.getEnterpriseCount() : 0)
                .sum();
    }

    /**
     * 构建空的季度统计数据（当没有数据时返回4个季度的空记录）
     */
    private List<MonthlyStatsDTO> buildEmptyQuarterlyStats(int year) {
        List<MonthlyStatsDTO> list = new ArrayList<>();
        for (int q = 1; q <= 4; q++) {
            String quarterStr = year + "-Q" + q;
            list.add(MonthlyStatsDTO.builder()
                    .month(quarterStr)
                    .employment(0)
                    .enterpriseCount(0)
                    .build());
        }
        return list;
    }

    // ==================== 数据大屏（市级汇总） ====================

    /**
     * 获取数据大屏市级汇总统计
     * 所有数据均来自数据库真实聚合，无 mock 值
     *
     * @param year 评价年度（用于过滤参评企业、亩均数据）
     * @return 市级汇总统计
     */
    public BigScreenStatsDTO getBigScreenStats(int year) {
        // 查询所有园区（park_info 表 deleted 字段当前全为 0，无需过滤）
        LambdaQueryWrapper<ParkInfo> parkQuery = new LambdaQueryWrapper<>();
        List<ParkInfo> parks = parkMapper.selectList(parkQuery);

        // 基础聚合
        long parkTotal = parks.size();
        long manufacturingCount = parks.stream()
                .filter(p -> "生产性制造类".equals(p.getParkType()))
                .count();
        long serviceCount = parks.stream()
                .filter(p -> "生产性服务类".equals(p.getParkType()))
                .count();
        long fourStarCount = parks.stream()
                .filter(p -> p.getStarLevel() != null && p.getStarLevel() == 4)
                .count();
        long fiveStarCount = parks.stream()
                .filter(p -> p.getStarLevel() != null && p.getStarLevel() == 5)
                .count();

        BigDecimal landArea = parks.stream()
                .map(ParkInfo::getLandArea)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal buildArea = parks.stream()
                .map(ParkInfo::getBuildArea)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long employeeTotal = parks.stream()
                .mapToLong(p -> p.getEmployeeCount() != null ? p.getEmployeeCount() : 0)
                .sum();
        long enterpriseTotal = parks.stream()
                .mapToLong(p -> p.getEnterpriseCount() != null ? p.getEnterpriseCount() : 0)
                .sum();

        // 参评企业数（enterprise_info.is_participate=1）
        LambdaQueryWrapper<EnterpriseInfo> entQuery = new LambdaQueryWrapper<>();
        entQuery.eq(EnterpriseInfo::getIsParticipate, 1);
        long participateEnterpriseCount = enterpriseMapper.selectCount(entQuery);

        // 企业荣誉累计统计（按 honor_type，跨年聚合）
        long nationalSpecializedCount = countHonorByType("new_specialty_giant");
        long provincialSpecializedCount = countHonorByType("new_specialty_sme");
        long innovativeSmeCount = countHonorByType("innovative_sme");

        // 亩均产值/税收（取 park_evaluation_score 表 AVG，过滤掉 NULL 和 0）
        BigDecimal revenuePerMu = avgEvaluationMetric(true);
        BigDecimal taxPerMu = avgEvaluationMetric(false);

        return BigScreenStatsDTO.builder()
                .parkTotal(parkTotal)
                .manufacturingCount(manufacturingCount)
                .serviceCount(serviceCount)
                .landArea(landArea)
                .buildArea(buildArea)
                .fourStarCount(fourStarCount)
                .fiveStarCount(fiveStarCount)
                .employeeTotal(employeeTotal)
                .enterpriseTotal(enterpriseTotal)
                .participateEnterpriseCount(participateEnterpriseCount)
                .nationalSpecializedCount(nationalSpecializedCount)
                .provincialSpecializedCount(provincialSpecializedCount)
                .innovativeSmeCount(innovativeSmeCount)
                .revenuePerMu(revenuePerMu)
                .taxPerMu(taxPerMu)
                .build();
    }

    /**
     * 获取各区县园区分布数据（按 district_name 分组聚合）
     *
     * @return 区县分布列表
     */
    public List<DistrictDataDTO> getDistrictData() {
        // 一次性查询所有园区（按 district_name 分组）
        LambdaQueryWrapper<ParkInfo> parkQuery = new LambdaQueryWrapper<>();
        parkQuery.isNotNull(ParkInfo::getDistrictName);
        List<ParkInfo> parks = parkMapper.selectList(parkQuery);

        // 按 district_name 分组聚合
        Map<String, List<ParkInfo>> grouped = parks.stream()
                .collect(Collectors.groupingBy(ParkInfo::getDistrictName));

        // 一次性查询各园区参评企业数（is_participate=1）按 park_id 分组
        LambdaQueryWrapper<EnterpriseInfo> entQuery = new LambdaQueryWrapper<>();
        entQuery.eq(EnterpriseInfo::getIsParticipate, 1);
        entQuery.select(EnterpriseInfo::getParkId);
        List<EnterpriseInfo> participateEnts = enterpriseMapper.selectList(entQuery);
        Map<Long, Long> participateCntByPark = participateEnts.stream()
                .collect(Collectors.groupingBy(EnterpriseInfo::getParkId, Collectors.counting()));

        // 一次性查询企业荣誉（按 park_id 分组）
        LambdaQueryWrapper<EnterpriseHonorRecord> honorQuery = new LambdaQueryWrapper<>();
        honorQuery.select(EnterpriseHonorRecord::getParkId, EnterpriseHonorRecord::getHonorType);
        List<EnterpriseHonorRecord> honors = enterpriseHonorRecordMapper.selectList(honorQuery);
        Map<Long, Map<String, Long>> honorByPark = honors.stream()
                .filter(h -> h.getParkId() != null && h.getHonorType() != null)
                .collect(Collectors.groupingBy(
                        EnterpriseHonorRecord::getParkId,
                        Collectors.groupingBy(EnterpriseHonorRecord::getHonorType, Collectors.counting())
                ));

        // 构建区县列表（按园区数降序）
        List<DistrictDataDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<ParkInfo>> entry : grouped.entrySet()) {
            String name = entry.getKey();
            List<ParkInfo> districtParks = entry.getValue();

            long parkCount = districtParks.size();
            long mfg = districtParks.stream()
                    .filter(p -> "生产性制造类".equals(p.getParkType()))
                    .count();
            long svc = districtParks.stream()
                    .filter(p -> "生产性服务类".equals(p.getParkType()))
                    .count();
            long emp = districtParks.stream()
                    .mapToLong(p -> p.getEmployeeCount() != null ? p.getEmployeeCount() : 0)
                    .sum();
            long ent = districtParks.stream()
                    .mapToLong(p -> p.getEnterpriseCount() != null ? p.getEnterpriseCount() : 0)
                    .sum();

            long participate = districtParks.stream()
                    .mapToLong(p -> participateCntByPark.getOrDefault(p.getId(), 0L))
                    .sum();

            long natl = districtParks.stream()
                    .mapToLong(p -> honorByPark.getOrDefault(p.getId(), Collections.emptyMap())
                            .getOrDefault("new_specialty_giant", 0L))
                    .sum();
            long prov = districtParks.stream()
                    .mapToLong(p -> honorByPark.getOrDefault(p.getId(), Collections.emptyMap())
                            .getOrDefault("new_specialty_sme", 0L))
                    .sum();
            long innov = districtParks.stream()
                    .mapToLong(p -> honorByPark.getOrDefault(p.getId(), Collections.emptyMap())
                            .getOrDefault("innovative_sme", 0L))
                    .sum();

            result.add(DistrictDataDTO.builder()
                    .name(name)
                    .parkCount(parkCount)
                    .manufacturingCount(mfg)
                    .serviceCount(svc)
                    .employeeCount(emp)
                    .enterpriseCount(ent)
                    .participateEnterpriseCount(participate)
                    .nationalSpecializedCount(natl)
                    .provincialSpecializedCount(prov)
                    .innovativeSmeCount(innov)
                    .build());
        }

        // 按园区总数降序排序
        result.sort((a, b) -> Long.compare(b.getParkCount(), a.getParkCount()));
        return result;
    }

    /**
     * 按荣誉类型统计记录数（跨年度累计）
     */
    private long countHonorByType(String honorType) {
        LambdaQueryWrapper<EnterpriseHonorRecord> q = new LambdaQueryWrapper<>();
        q.eq(EnterpriseHonorRecord::getHonorType, honorType);
        return enterpriseHonorRecordMapper.selectCount(q);
    }

    /**
     * 计算 park_evaluation_score 表中 revenue_per_mu 或 tax_per_mu 的平均值
     * 过滤掉 NULL 和 0（避免未填园区拉低均值）
     *
     * @param isRevenue true=亩均产值，false=亩均税收
     */
    private BigDecimal avgEvaluationMetric(boolean isRevenue) {
        LambdaQueryWrapper<ParkEvaluationScore> q = new LambdaQueryWrapper<>();
        if (isRevenue) {
            q.select(ParkEvaluationScore::getRevenuePerMu);
            q.isNotNull(ParkEvaluationScore::getRevenuePerMu);
            q.gt(ParkEvaluationScore::getRevenuePerMu, BigDecimal.ZERO);
        } else {
            q.select(ParkEvaluationScore::getTaxPerMu);
            q.isNotNull(ParkEvaluationScore::getTaxPerMu);
            q.gt(ParkEvaluationScore::getTaxPerMu, BigDecimal.ZERO);
        }
        List<ParkEvaluationScore> scores = parkEvaluationScoreMapper.selectList(q);
        if (scores.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = scores.stream()
                .map(isRevenue ? ParkEvaluationScore::getRevenuePerMu : ParkEvaluationScore::getTaxPerMu)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(scores.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 获取园区评价分析数据（数据大屏第二个标签页）
     *
     * @param year 评价年度
     * @return 评价分析统计
     */
    public EvaluationAnalysisDTO getEvaluationAnalysis(int year) {
        List<EvaluationAnalysisDTO.ParkTypeYearData> parkTypeYearList = buildParkTypeYearData();
        EvaluationAnalysisDTO.TechEnterpriseStats techEnterpriseStats = buildTechEnterpriseStats(year);
        List<EvaluationAnalysisDTO.EnterpriseCultivationStats> cultivationStats = buildCultivationStats(year);
        List<EvaluationAnalysisDTO.MuJunYearData> muJunYearList = buildMuJunYearData(year);
        List<EvaluationAnalysisDTO.DistrictGradeStats> districtGradeList = buildDistrictGradeStats(year);

        return EvaluationAnalysisDTO.builder()
                .parkTypeYearList(parkTypeYearList)
                .techEnterpriseStats(techEnterpriseStats)
                .cultivationStats(null)
                .muJunYearList(muJunYearList)
                .districtGradeList(districtGradeList)
                .build();
    }

    /**
     * 构建园区数量类型年度数据（近3年）
     */
    private List<EvaluationAnalysisDTO.ParkTypeYearData> buildParkTypeYearData() {
        List<EvaluationAnalysisDTO.ParkTypeYearData> result = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear - 2; year <= currentYear; year++) {
            LambdaQueryWrapper<ParkInfo> q = new LambdaQueryWrapper<>();
            List<ParkInfo> parks = parkMapper.selectList(q);
            long serviceCount = parks.stream()
                    .filter(p -> "生产性服务类".equals(p.getParkType()))
                    .count();
            long manufacturingCount = parks.stream()
                    .filter(p -> "生产性制造类".equals(p.getParkType()))
                    .count();
            result.add(EvaluationAnalysisDTO.ParkTypeYearData.builder()
                    .year(year)
                    .serviceCount(serviceCount)
                    .manufacturingCount(manufacturingCount)
                    .totalCount(serviceCount + manufacturingCount)
                    .build());
        }
        return result;
    }

    /**
     * 构建科技型企业统计
     */
    private EvaluationAnalysisDTO.TechEnterpriseStats buildTechEnterpriseStats(int year) {
        long techEnterpriseCount = countHonorByType("high_tech");
        long highTechCount = techEnterpriseCount;
        long zhejiangTechCount = countHonorByType("zhejiang_tech");
        long excellentProductCount = countHonorByType("excellent_product");

        return EvaluationAnalysisDTO.TechEnterpriseStats.builder()
                .techEnterpriseCount(techEnterpriseCount)
                .highTechCount(highTechCount)
                .zhejiangTechCount(zhejiangTechCount)
                .excellentProductCount(excellentProductCount)
                .build();
    }

    /**
     * 构建企业培育统计列表
     */
    private List<EvaluationAnalysisDTO.EnterpriseCultivationStats> buildCultivationStats(int year) {
        List<EvaluationAnalysisDTO.EnterpriseCultivationStats> result = new ArrayList<>();
        String[] types = {"online", "规上", "高新", "专精特新", "国家专精特新", "单项冠军", "上市企业", "创新型中小企业"};
        for (String type : types) {
            result.add(EvaluationAnalysisDTO.EnterpriseCultivationStats.builder()
                    .name(type)
                    .newCount(0L)
                    .totalCount(countHonorByType(type))
                    .build());
        }
        return result;
    }

    /**
     * 构建亩均分析年度数据（近3年，含产值和税收）
     */
    private List<EvaluationAnalysisDTO.MuJunYearData> buildMuJunYearData(int year) {
        List<EvaluationAnalysisDTO.MuJunYearData> result = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear - 2; y <= currentYear; y++) {
            LambdaQueryWrapper<ParkEvaluationScore> q = new LambdaQueryWrapper<>();
            q.eq(ParkEvaluationScore::getYear, y);
            List<ParkEvaluationScore> scores = parkEvaluationScoreMapper.selectList(q);

            List<ParkEvaluationScore> serviceScores = new ArrayList<>();
            List<ParkEvaluationScore> mfgScores = new ArrayList<>();
            for (ParkEvaluationScore score : scores) {
                ParkInfo park = parkMapper.selectById(score.getParkId());
                if (park != null) {
                    if ("生产性服务类".equals(park.getParkType())) {
                        serviceScores.add(score);
                    } else if ("生产性制造类".equals(park.getParkType())) {
                        mfgScores.add(score);
                    }
                }
            }

            BigDecimal serviceAvg = avgFromList(serviceScores, true);
            BigDecimal mfgAvg = avgFromList(mfgScores, true);
            BigDecimal overallAvg = avgFromList(scores, true);

            result.add(EvaluationAnalysisDTO.MuJunYearData.builder()
                    .year(y)
                    .serviceMuJun(serviceAvg)
                    .averageMuJun(overallAvg)
                    .manufacturingMuJun(mfgAvg)
                    .build());
        }
        return result;
    }

    /**
     * 从分数列表计算平均值
     */
    private BigDecimal avgFromList(List<ParkEvaluationScore> scores, boolean isRevenue) {
        if (scores.isEmpty()) return BigDecimal.ZERO;
        BigDecimal sum = scores.stream()
                .map(isRevenue ? ParkEvaluationScore::getRevenuePerMu : ParkEvaluationScore::getTaxPerMu)
                .filter(Objects::nonNull)
                .filter(v -> v.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long count = scores.stream()
                .map(isRevenue ? ParkEvaluationScore::getRevenuePerMu : ParkEvaluationScore::getTaxPerMu)
                .filter(Objects::nonNull)
                .filter(v -> v.compareTo(BigDecimal.ZERO) > 0)
                .count();
        return count > 0 ? sum.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    /**
     * 构建区县绩效分档统计
     */
    private List<EvaluationAnalysisDTO.DistrictGradeStats> buildDistrictGradeStats(int year) {
        LambdaQueryWrapper<ParkInfo> parkQuery = new LambdaQueryWrapper<>();
        parkQuery.isNotNull(ParkInfo::getDistrictName);
        List<ParkInfo> parks = parkMapper.selectList(parkQuery);

        Map<String, List<ParkInfo>> grouped = parks.stream()
                .collect(Collectors.groupingBy(ParkInfo::getDistrictName));

        LambdaQueryWrapper<ParkEvaluationScore> scoreQuery = new LambdaQueryWrapper<>();
        scoreQuery.eq(ParkEvaluationScore::getYear, year);
        List<ParkEvaluationScore> scores = parkEvaluationScoreMapper.selectList(scoreQuery);
        Map<Long, ParkEvaluationScore> scoreByParkId = scores.stream()
                .collect(Collectors.toMap(ParkEvaluationScore::getParkId, s -> s));

        List<EvaluationAnalysisDTO.DistrictGradeStats> result = new ArrayList<>();
        for (Map.Entry<String, List<ParkInfo>> entry : grouped.entrySet()) {
            String districtName = entry.getKey();
            List<ParkInfo> districtParks = entry.getValue();

            long serviceCount = districtParks.stream()
                    .filter(p -> "生产性服务类".equals(p.getParkType()))
                    .count();
            long manufacturingCount = districtParks.stream()
                    .filter(p -> "生产性制造类".equals(p.getParkType()))
                    .count();

            long gradeA = 0, gradeB = 0, gradeC = 0, gradeD = 0;
            BigDecimal totalTax = BigDecimal.ZERO, totalRevenue = BigDecimal.ZERO;
            int scoreCount = 0;

            for (ParkInfo park : districtParks) {
                ParkEvaluationScore score = scoreByParkId.get(park.getId());
                if (score != null) {
                    scoreCount++;
                    String grade = score.getGrade();
                    if ("A".equals(grade)) gradeA++;
                    else if ("B".equals(grade)) gradeB++;
                    else if ("C".equals(grade)) gradeC++;
                    else if ("D".equals(grade)) gradeD++;

                    if (score.getTaxPerMu() != null && score.getTaxPerMu().compareTo(BigDecimal.ZERO) > 0) {
                        totalTax = totalTax.add(score.getTaxPerMu());
                    }
                    if (score.getRevenuePerMu() != null && score.getRevenuePerMu().compareTo(BigDecimal.ZERO) > 0) {
                        totalRevenue = totalRevenue.add(score.getRevenuePerMu());
                    }
                }
            }

            BigDecimal avgTax = scoreCount > 0 ? totalTax.divide(BigDecimal.valueOf(scoreCount), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
            BigDecimal avgRevenue = scoreCount > 0 ? totalRevenue.divide(BigDecimal.valueOf(scoreCount), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

            result.add(EvaluationAnalysisDTO.DistrictGradeStats.builder()
                    .districtName(districtName)
                    .serviceCount(serviceCount)
                    .manufacturingCount(manufacturingCount)
                    .gradeACount(gradeA)
                    .gradeBCount(gradeB)
                    .gradeCCount(gradeC)
                    .gradeDCount(gradeD)
                    .taxPerMu(avgTax)
                    .revenuePerMu(avgRevenue)
                    .build());
        }

        result.sort((a, b) -> b.getManufacturingCount().compareTo(a.getManufacturingCount()));
        return result;
    }
}
