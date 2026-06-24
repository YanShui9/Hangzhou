package com.park.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.enterprise.entity.EnterpriseHonorRecord;
import com.park.enterprise.mapper.EnterpriseHonorRecordMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import com.park.system.entity.ParkTaxRecord;
import com.park.system.mapper.ParkTaxRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评分自动计算服务
 * 根据企业荣誉、税收等数据自动计算各维度得分
 *
 * 数据来源：
 * - 企业培育①②、科技创新①②③：enterprise_honor_record 表
 * - 产业发展①②③、效益产出④⑤：park_tax_record 表 + park_info.land_area
 *
 * 计算规则（来自需求文档）：
 * 产业发展：
 *   ①主导产业产值占比 = leading_industry营收 / park_total营收
 *   ②入驻企业数占比 = leading_industry企业数 / park_total企业数（简化：用营收占比近似）
 *   ③企业类型产值占比 = enterprise_type营收 / park_total营收
 *   规则：①②取最小值-50%，差值*10的整数部分为得分；③≥60%时(③-60%)*10的整数部分为得分
 * 效益产出：
 *   ④亩均税收系数 = 园区亩均税收 / 全市同类型园区亩均税收平均值
 *   ⑤亩均产出系数 = 园区亩均产出 / 全市同类型园区亩均产出平均值
 *   规则：系数1.5-2倍得2分，2-2.5倍得4分，2.5-3倍得6分，3-3.5倍得8分，3.5倍及以上得10分
 *
 * @author park-team
 */
@Slf4j
@Service
public class AutoCalculationService {

    @Autowired
    private EnterpriseHonorRecordMapper enterpriseHonorRecordMapper;

    @Autowired
    private ParkTaxRecordMapper parkTaxRecordMapper;

    @Autowired
    private ParkMapper parkMapper;

    /**
     * 自动计算指定园区、年度的各维度得分
     *
     * @param parkId 园区ID
     * @param year   年度
     * @return 维度 -> (子指标 -> 分值)，子指标key为"1","2"..."total"
     */
    public Map<String, Map<String, BigDecimal>> calculateAutoScores(Long parkId, Integer year) {
        Map<String, Map<String, BigDecimal>> result = new LinkedHashMap<>();
        result.put("industryDev", new LinkedHashMap<>());
        result.put("entCultivate", new LinkedHashMap<>());
        result.put("techInnovation", new LinkedHashMap<>());
        result.put("serviceCap", new LinkedHashMap<>());
        result.put("benefitOutput", new LinkedHashMap<>());
        result.put("safetyProd", new LinkedHashMap<>());
        result.put("other", new LinkedHashMap<>());

        ParkInfo park = parkMapper.selectById(parkId);
        if (park == null) {
            log.warn("自动评分：园区不存在 parkId={}", parkId);
            fillZeroTotals(result);
            return result;
        }

        try {
            // 1. 企业培育①②、科技创新①②③：基于荣誉数据
            calcHonorScores(parkId, year, result);

            // 2. 产业发展①②③：基于税收数据
            calcIndustryDevScores(park, year, result);

            // 3. 效益产出④⑤：基于税收数据+用地面积
            calcBenefitOutputScores(park, year, result);

            // 4. 计算各维度 total
            fillTotals(result);
        } catch (Exception e) {
            log.error("自动评分计算失败 parkId={} year={}", parkId, year, e);
            fillZeroTotals(result);
        }
        return result;
    }

    // ==================== 荣誉评分（企业培育①②、科技创新①②③） ====================

    /**
     * 基于荣誉数据计算企业培育和科技创新得分
     * 企业培育①②：规上企业相关荣誉（existing_above_scale, new_above_scale）
     * 科技创新①②③：研发机构相关荣誉（new_national_rd_agency, new_provincial_rd_agency, new_municipal_rd_agency）
     *
     * 简化规则：每项荣誉按数量给分，单项上限10分
     */
    private void calcHonorScores(Long parkId, Integer year, Map<String, Map<String, BigDecimal>> result) {
        LambdaQueryWrapper<EnterpriseHonorRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnterpriseHonorRecord::getParkId, parkId)
                .eq(EnterpriseHonorRecord::getYear, year);
        List<EnterpriseHonorRecord> honors = enterpriseHonorRecordMapper.selectList(wrapper);

        if (honors.isEmpty()) {
            return;
        }

        // 按荣誉类型分组统计数量
        Map<String, Integer> typeCount = new HashMap<>();
        for (EnterpriseHonorRecord h : honors) {
            String type = h.getHonorType();
            int cnt = h.getHonorCount() != null ? h.getHonorCount() : 1;
            typeCount.merge(type, cnt, Integer::sum);
        }

        // 企业培育①②：规上企业培育
        Map<String, BigDecimal> entCultivate = result.get("entCultivate");
        entCultivate.put("1", scoreByCount(typeCount.get("existing_above_scale"), 10));
        entCultivate.put("2", scoreByCount(typeCount.get("new_above_scale"), 10));

        // 科技创新①②③：研发机构
        Map<String, BigDecimal> techInnovation = result.get("techInnovation");
        techInnovation.put("1", scoreByCount(typeCount.get("new_national_rd_agency"), 10));
        techInnovation.put("2", scoreByCount(typeCount.get("new_provincial_rd_agency"), 10));
        techInnovation.put("3", scoreByCount(typeCount.get("new_municipal_rd_agency"), 10));
    }

    /**
     * 按数量计算得分：每个单位得2分，上限maxScore
     */
    private BigDecimal scoreByCount(Integer count, int maxScore) {
        if (count == null || count <= 0) return BigDecimal.ZERO;
        int score = Math.min(count * 2, maxScore);
        return BigDecimal.valueOf(score);
    }

    // ==================== 产业发展评分（①②③） ====================

    /**
     * 产业发展得分计算
     * ①主导产业产值占比 = leading_industry营收 / park_total营收
     * ②入驻企业数占比（简化为营收占比近似）
     * ③企业类型产值占比 = enterprise_type营收 / park_total营收
     *
     * 规则：①②取最小值-50%，差值≥10%时差值*10的整数部分为得分
     *      ③≥60%时(③-60%)*10的整数部分为得分，③<60%或制造类企业<10家得0分
     */
    private void calcIndustryDevScores(ParkInfo park, Integer year, Map<String, Map<String, BigDecimal>> result) {
        Map<String, BigDecimal> industryDev = result.get("industryDev");

        // 查询该园区3种税收类型数据
        List<ParkTaxRecord> taxRecords = getParkTaxRecords(park.getParkName(), year);
        if (taxRecords.isEmpty()) {
            industryDev.put("1", BigDecimal.ZERO);
            industryDev.put("2", BigDecimal.ZERO);
            industryDev.put("3", BigDecimal.ZERO);
            return;
        }

        Map<String, ParkTaxRecord> taxMap = taxRecords.stream()
                .collect(Collectors.toMap(ParkTaxRecord::getTaxType, t -> t, (a, b) -> a));

        ParkTaxRecord totalTax = taxMap.get("park_total");
        ParkTaxRecord leadingTax = taxMap.get("leading_industry");
        ParkTaxRecord entTypeTax = taxMap.get("enterprise_type");

        if (totalTax == null || totalTax.getRevenue() == null
                || totalTax.getRevenue().compareTo(BigDecimal.ZERO) <= 0) {
            industryDev.put("1", BigDecimal.ZERO);
            industryDev.put("2", BigDecimal.ZERO);
            industryDev.put("3", BigDecimal.ZERO);
            return;
        }

        // ①主导产业产值占比
        BigDecimal ratio1 = leadingTax != null && leadingTax.getRevenue() != null
                ? leadingTax.getRevenue().divide(totalTax.getRevenue(), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // ②入驻企业数占比（简化：用主导产业营收占比近似）
        BigDecimal ratio2 = ratio1;

        // ①②取最小值-50%，差值*10的整数部分
        BigDecimal minRatio = ratio1.min(ratio2);
        BigDecimal diff12 = minRatio.subtract(new BigDecimal("0.5"));
        BigDecimal score12 = diff12.compareTo(new BigDecimal("0.1")) >= 0
                ? new BigDecimal(diff12.multiply(BigDecimal.TEN).intValue())
                : BigDecimal.ZERO;
        industryDev.put("1", score12);
        industryDev.put("2", score12);

        // ③企业类型产值占比
        BigDecimal ratio3 = entTypeTax != null && entTypeTax.getRevenue() != null
                ? entTypeTax.getRevenue().divide(totalTax.getRevenue(), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal diff3 = ratio3.subtract(new BigDecimal("0.6"));
        BigDecimal score3 = diff3.compareTo(BigDecimal.ZERO) >= 0
                ? new BigDecimal(diff3.multiply(BigDecimal.TEN).intValue())
                : BigDecimal.ZERO;
        industryDev.put("3", score3);
    }

    // ==================== 效益产出评分（④⑤） ====================

    /**
     * 效益产出得分计算
     * ④亩均税收系数 = 园区亩均税收 / 全市同类型园区亩均税收平均值
     * ⑤亩均产出系数 = 园区亩均产出 / 全市同类型园区亩均产出平均值
     *
     * 规则：系数1.5-2倍得2分，2-2.5倍得4分，2.5-3倍得6分，3-3.5倍得8分，3.5倍及以上得10分
     */
    private void calcBenefitOutputScores(ParkInfo park, Integer year, Map<String, Map<String, BigDecimal>> result) {
        Map<String, BigDecimal> benefitOutput = result.get("benefitOutput");

        // 园区用地面积
        BigDecimal landArea = park.getLandArea();
        if (landArea == null || landArea.compareTo(BigDecimal.ZERO) <= 0) {
            // land_area为0时该园区得0分
            benefitOutput.put("4", BigDecimal.ZERO);
            benefitOutput.put("5", BigDecimal.ZERO);
            return;
        }

        // 查询该园区park_total税收
        List<ParkTaxRecord> taxRecords = getParkTaxRecords(park.getParkName(), year);
        ParkTaxRecord totalTax = taxRecords.stream()
                .filter(t -> "park_total".equals(t.getTaxType()))
                .findFirst().orElse(null);

        if (totalTax == null || totalTax.getRevenue() == null || totalTax.getTax() == null) {
            benefitOutput.put("4", BigDecimal.ZERO);
            benefitOutput.put("5", BigDecimal.ZERO);
            return;
        }

        // 园区亩均税收 = 净入库总税款 / 用地面积
        BigDecimal parkTaxPerMu = totalTax.getTax().divide(landArea, 4, RoundingMode.HALF_UP);
        // 园区亩均产出 = 营业总收入 / 用地面积
        BigDecimal parkRevenuePerMu = totalTax.getRevenue().divide(landArea, 4, RoundingMode.HALF_UP);

        // 计算全市同类型园区平均值
        BigDecimal[] avg = calcCityAvgPerMu(park.getParkType(), year);
        BigDecimal avgTaxPerMu = avg[0];
        BigDecimal avgRevenuePerMu = avg[1];

        // ④亩均税收系数
        BigDecimal taxCoeff = avgTaxPerMu.compareTo(BigDecimal.ZERO) > 0
                ? parkTaxPerMu.divide(avgTaxPerMu, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        benefitOutput.put("4", scoreByCoefficient(taxCoeff));

        // ⑤亩均产出系数
        BigDecimal revenueCoeff = avgRevenuePerMu.compareTo(BigDecimal.ZERO) > 0
                ? parkRevenuePerMu.divide(avgRevenuePerMu, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        benefitOutput.put("5", scoreByCoefficient(revenueCoeff));
    }

    /**
     * 计算全市同类型园区的亩均税收和亩均产出平均值
     * @return [avgTaxPerMu, avgRevenuePerMu]
     */
    private BigDecimal[] calcCityAvgPerMu(String parkType, Integer year) {
        // 查询同类型园区（park_info 无软删除字段，全部视为有效）
        LambdaQueryWrapper<ParkInfo> parkWrapper = new LambdaQueryWrapper<>();
        parkWrapper.eq(ParkInfo::getParkType, parkType)
                .gt(ParkInfo::getLandArea, BigDecimal.ZERO);
        List<ParkInfo> sameTypeParks = parkMapper.selectList(parkWrapper);

        if (sameTypeParks.isEmpty()) {
            return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
        }

        // 查询这些园区的park_total税收
        Set<String> parkNames = sameTypeParks.stream()
                .map(ParkInfo::getParkName).collect(Collectors.toSet());
        LambdaQueryWrapper<ParkTaxRecord> taxWrapper = new LambdaQueryWrapper<>();
        taxWrapper.eq(ParkTaxRecord::getTaxType, "park_total")
                .eq(ParkTaxRecord::getYear, year)
                .in(ParkTaxRecord::getParkName, parkNames);
        List<ParkTaxRecord> taxRecords = parkTaxRecordMapper.selectList(taxWrapper);

        // 总税款 / 总用地面积
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalLand = BigDecimal.ZERO;
        Map<String, ParkInfo> nameToPark = sameTypeParks.stream()
                .collect(Collectors.toMap(ParkInfo::getParkName, p -> p, (a, b) -> a));

        for (ParkTaxRecord tax : taxRecords) {
            ParkInfo p = nameToPark.get(tax.getParkName());
            if (p == null || p.getLandArea() == null
                    || p.getLandArea().compareTo(BigDecimal.ZERO) <= 0) continue;
            if (tax.getTax() != null) totalTax = totalTax.add(tax.getTax());
            if (tax.getRevenue() != null) totalRevenue = totalRevenue.add(tax.getRevenue());
            totalLand = totalLand.add(p.getLandArea());
        }

        if (totalLand.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
        }

        BigDecimal avgTax = totalTax.divide(totalLand, 4, RoundingMode.HALF_UP);
        BigDecimal avgRevenue = totalRevenue.divide(totalLand, 4, RoundingMode.HALF_UP);
        return new BigDecimal[]{avgTax, avgRevenue};
    }

    /**
     * 按系数计算得分
     * 1.5-2倍得2分；2-2.5倍得4分；2.5-3倍得6分；3-3.5倍得8分；3.5倍及以上得10分
     */
    private BigDecimal scoreByCoefficient(BigDecimal coeff) {
        if (coeff == null || coeff.compareTo(new BigDecimal("1.5")) < 0) {
            return BigDecimal.ZERO;
        }
        if (coeff.compareTo(new BigDecimal("3.5")) >= 0) {
            return BigDecimal.TEN;
        }
        if (coeff.compareTo(new BigDecimal("3")) >= 0) {
            return new BigDecimal("8");
        }
        if (coeff.compareTo(new BigDecimal("2.5")) >= 0) {
            return new BigDecimal("6");
        }
        if (coeff.compareTo(new BigDecimal("2")) >= 0) {
            return new BigDecimal("4");
        }
        // 1.5-2倍
        return new BigDecimal("2");
    }

    // ==================== 工具方法 ====================

    /**
     * 查询指定园区某年度的所有税收记录
     */
    private List<ParkTaxRecord> getParkTaxRecords(String parkName, Integer year) {
        LambdaQueryWrapper<ParkTaxRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkTaxRecord::getParkName, parkName)
                .eq(ParkTaxRecord::getYear, year);
        return parkTaxRecordMapper.selectList(wrapper);
    }

    /**
     * 计算各维度的total
     */
    private void fillTotals(Map<String, Map<String, BigDecimal>> result) {
        for (Map.Entry<String, Map<String, BigDecimal>> entry : result.entrySet()) {
            Map<String, BigDecimal> group = entry.getValue();
            BigDecimal total = group.entrySet().stream()
                    .filter(e -> !"total".equals(e.getKey()))
                    .map(Map.Entry::getValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            group.put("total", total);
        }
    }

    /**
     * 所有维度total置0
     */
    private void fillZeroTotals(Map<String, Map<String, BigDecimal>> result) {
        for (Map<String, BigDecimal> group : result.values()) {
            group.put("total", BigDecimal.ZERO);
        }
    }

    /**
     * 计算园区亩均税收和亩均产出（用于同步到 park_evaluation_score）
     * @return [taxPerMu, revenuePerMu]，数据缺失或land_area为0时返回[0, 0]
     */
    public BigDecimal[] calcPerMu(Long parkId, Integer year) {
        ParkInfo park = parkMapper.selectById(parkId);
        if (park == null || park.getLandArea() == null
                || park.getLandArea().compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
        }
        List<ParkTaxRecord> taxRecords = getParkTaxRecords(park.getParkName(), year);
        ParkTaxRecord totalTax = taxRecords.stream()
                .filter(t -> "park_total".equals(t.getTaxType()))
                .findFirst().orElse(null);
        if (totalTax == null || totalTax.getTax() == null || totalTax.getRevenue() == null) {
            return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
        }
        BigDecimal taxPerMu = totalTax.getTax().divide(park.getLandArea(), 2, RoundingMode.HALF_UP);
        BigDecimal revenuePerMu = totalTax.getRevenue().divide(park.getLandArea(), 2, RoundingMode.HALF_UP);
        return new BigDecimal[]{taxPerMu, revenuePerMu};
    }
}
