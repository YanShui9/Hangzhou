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

/**
 * 数据大屏市级汇总统计 DTO
 * 所有字段均来自数据库真实数据，无 mock 值
 *
 * @author park-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "数据大屏市级汇总统计")
public class BigScreenStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区总数")
    private Long parkTotal;

    @ApiModelProperty(value = "生产制造类园区数")
    private Long manufacturingCount;

    @ApiModelProperty(value = "生产服务类园区数")
    private Long serviceCount;

    @ApiModelProperty(value = "实际用地总面积（亩）")
    private BigDecimal landArea;

    @ApiModelProperty(value = "已建建筑面积总面积（平方米）")
    private BigDecimal buildArea;

    @ApiModelProperty(value = "四星级园区数")
    private Long fourStarCount;

    @ApiModelProperty(value = "五星级园区数")
    private Long fiveStarCount;

    @ApiModelProperty(value = "入驻企业员工总数")
    private Long employeeTotal;

    @ApiModelProperty(value = "入驻企业总数")
    private Long enterpriseTotal;

    @ApiModelProperty(value = "参评企业总数")
    private Long participateEnterpriseCount;

    @ApiModelProperty(value = "国家专精特新小巨人企业数")
    private Long nationalSpecializedCount;

    @ApiModelProperty(value = "省专精特新中小企业数")
    private Long provincialSpecializedCount;

    @ApiModelProperty(value = "创新型中小企业数")
    private Long innovativeSmeCount;

    @ApiModelProperty(value = "全市平均亩均产值（万元/亩）")
    private BigDecimal revenuePerMu;

    @ApiModelProperty(value = "全市平均亩均税收（万元/亩）")
    private BigDecimal taxPerMu;
}
