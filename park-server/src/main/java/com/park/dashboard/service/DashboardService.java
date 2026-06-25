package com.park.dashboard.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.dashboard.dto.DashboardStatsDTO;
import com.park.dashboard.dto.QuarterlyStatsDTO;
import com.park.dashboard.dto.ParkRankDTO;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.mapper.EnterpriseMapper;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.mapper.EvaluationMapper;
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
    public List<QuarterlyStatsDTO> getQuarterlyStats(int year, String districtName, Long parkId) {
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
        Map<Integer, QuarterlyStatsDTO> quarterMap = new LinkedHashMap<>();
        // 初始化4个季度
        for (int q = 1; q <= 4; q++) {
            String quarterStr = year + "-Q" + q;
            quarterMap.put(q, QuarterlyStatsDTO.builder()
                    .quarter(quarterStr)
                    .employment(0)
                    .enterpriseCount(0)
                    .build());
        }

        // 汇总数据
        for (ParkOperation op : operations) {
            QuarterlyStatsDTO dto = quarterMap.get(op.getQuarter());
            if (dto != null) {
                dto.setEmployment(dto.getEmployment() +
                        (op.getEmployeeCount() != null ? op.getEmployeeCount() : 0));
                dto.setEnterpriseCount(dto.getEnterpriseCount() +
                        (op.getEnterpriseCount() != null ? op.getEnterpriseCount() : 0));
            }
        }

        // 无运营数据时，用 park_info 当前统计值填充最新季度（Q4），避免趋势图全0
        if (operations.isEmpty()) {
            QuarterlyStatsDTO q4 = quarterMap.get(4);
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
    private List<QuarterlyStatsDTO> buildEmptyQuarterlyStats(int year) {
        List<QuarterlyStatsDTO> list = new ArrayList<>();
        for (int q = 1; q <= 4; q++) {
            String quarterStr = year + "-Q" + q;
            list.add(QuarterlyStatsDTO.builder()
                    .quarter(quarterStr)
                    .employment(0)
                    .enterpriseCount(0)
                    .build());
        }
        return list;
    }
}
