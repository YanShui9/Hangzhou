package com.park.dashboard.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.dashboard.dto.DashboardStatsDTO;
import com.park.dashboard.dto.MonthlyStatsDTO;
import com.park.dashboard.dto.ParkRankDTO;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.mapper.EnterpriseMapper;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.mapper.EvaluationMapper;
import com.park.operation.entity.ParkOperation;
import com.park.operation.mapper.OperationMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
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

        // 3. 查询最新季度的员工和企业数汇总
        ParkOperation latestOperation = getLatestOperation(districtName, parkId);
        Long totalEmployment = latestOperation != null && latestOperation.getEmployeeCount() != null
                ? latestOperation.getEmployeeCount().longValue() : 0L;
        // 使用企业数作为替代指标
        Long totalEnterpriseCount = latestOperation != null && latestOperation.getEnterpriseCount() != null
                ? latestOperation.getEnterpriseCount().longValue() : 0L;

        // 4. 查询待审核数量（状态为 1=待区县审 的评价记录）
        Long pendingAudits = countPendingAudits(districtName, parkId);

        return DashboardStatsDTO.builder()
                .totalParks(totalParks)
                .totalEnterprises(totalEnterprises)
                .totalEmployment(totalEmployment)
                .totalRevenue(BigDecimal.ZERO) // 设计文档中无此字段，设为0
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
            evalQuery.orderByDesc(EvaluationRecord::getEvalYear);
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
                    .revenue(BigDecimal.ZERO)
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
     */
    private Long countEnterprises(String districtName, Long parkId) {
        LambdaQueryWrapper<EnterpriseInfo> queryWrapper = new LambdaQueryWrapper<>();

        if (parkId != null) {
            queryWrapper.eq(EnterpriseInfo::getParkId, parkId);
        } else if (districtName != null && !districtName.isEmpty()) {
            // 先查询该区县的园区ID列表
            List<Long> parkIds = getParkIdsByDistrict(districtName);
            if (parkIds.isEmpty()) {
                return 0L;
            }
            queryWrapper.in(EnterpriseInfo::getParkId, parkIds);
        }

        return enterpriseMapper.selectCount(queryWrapper);
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
     * 构建空的季度统计数据（当没有数据时返回4个季度的空记录）
     */
    private List<MonthlyStatsDTO> buildEmptyQuarterlyStats(int year) {
        List<MonthlyStatsDTO> list = new ArrayList<>();
        for (int q = 1; q <= 4; q++) {
            String quarterStr = year + "-Q" + q;
            list.add(MonthlyStatsDTO.builder()
                    .month(quarterStr)
                    .revenue(BigDecimal.ZERO)
                    .employment(0)
                    .enterpriseCount(0)
                    .build());
        }
        return list;
    }
}
