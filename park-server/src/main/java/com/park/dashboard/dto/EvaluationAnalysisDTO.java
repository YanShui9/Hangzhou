package com.park.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "园区评价分析统计")
public class EvaluationAnalysisDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区数量类型分析数据（按年份）")
    private List<ParkTypeYearData> parkTypeYearList;

    @ApiModelProperty(value = "科技型企业统计")
    private TechEnterpriseStats techEnterpriseStats;

    @ApiModelProperty(value = "企业培育统计")
    private EnterpriseCultivationStats cultivationStats;

    @ApiModelProperty(value = "亩均分析数据（按年份）")
    private List<MuJunYearData> muJunYearList;

    @ApiModelProperty(value = "各区县园区绩效分档统计")
    private List<DistrictGradeStats> districtGradeList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "园区数量类型年度数据")
    public static class ParkTypeYearData {
        @ApiModelProperty(value = "年份")
        private Integer year;
        @ApiModelProperty(value = "服务类园区数")
        private Long serviceCount;
        @ApiModelProperty(value = "制造类园区数")
        private Long manufacturingCount;
        @ApiModelProperty(value = "园区总数")
        private Long totalCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "科技型企业统计")
    public static class TechEnterpriseStats {
        @ApiModelProperty(value = "科技型企业总数")
        private Long techEnterpriseCount;
        @ApiModelProperty(value = "高企数")
        private Long highTechCount;
        @ApiModelProperty(value = "浙江省科技型企业数")
        private Long zhejiangTechCount;
        @ApiModelProperty(value = "省级优秀工业新产品数")
        private Long excellentProductCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "企业培育统计")
    public static class EnterpriseCultivationStats {
        @ApiModelProperty(value = "统计项名称")
        private String name;
        @ApiModelProperty(value = "新增数量")
        private Long newCount;
        @ApiModelProperty(value = "总数")
        private Long totalCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "亩均分析年度数据")
    public static class MuJunYearData {
        @ApiModelProperty(value = "年份")
        private Integer year;
        @ApiModelProperty(value = "生产性服务类亩均")
        private BigDecimal serviceMuJun;
        @ApiModelProperty(value = "平均值")
        private BigDecimal averageMuJun;
        @ApiModelProperty(value = "生产制造类亩均")
        private BigDecimal manufacturingMuJun;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "区县绩效分档统计")
    public static class DistrictGradeStats {
        @ApiModelProperty(value = "区县名称")
        private String districtName;
        @ApiModelProperty(value = "服务类园区数")
        private Long serviceCount;
        @ApiModelProperty(value = "制造类园区数")
        private Long manufacturingCount;
        @ApiModelProperty(value = "A档园区数")
        private Long gradeACount;
        @ApiModelProperty(value = "B档园区数")
        private Long gradeBCount;
        @ApiModelProperty(value = "C档园区数")
        private Long gradeCCount;
        @ApiModelProperty(value = "D档园区数")
        private Long gradeDCount;
        @ApiModelProperty(value = "亩均税收（万元/亩）")
        private BigDecimal taxPerMu;
        @ApiModelProperty(value = "亩均产出（万元/亩）")
        private BigDecimal revenuePerMu;
    }
}