package com.park.evaluation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 园区评价评分实体
 * 对应 park_evaluation_score 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_evaluation_score")
@ApiModel(description = "园区评价评分")
public class ParkEvaluationScore extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区ID")
    private Long parkId;

    @ApiModelProperty(value = "评价年度")
    private Integer year;

    @ApiModelProperty(value = "亩均营收（万元/亩）")
    private BigDecimal revenuePerMu;

    @ApiModelProperty(value = "亩均税收（万元/亩）")
    private BigDecimal taxPerMu;

    @ApiModelProperty(value = "主导产业")
    private String leadingIndustry;

    @ApiModelProperty(value = "参评企业总数")
    private Integer enterpriseTotal;

    @ApiModelProperty(value = "产业发展得分")
    private BigDecimal industryDevScore;

    @ApiModelProperty(value = "企业培育得分")
    private BigDecimal enterpriseCultivateScore;

    @ApiModelProperty(value = "科技创新得分")
    private BigDecimal techInnovationScore;

    @ApiModelProperty(value = "服务能力得分")
    private BigDecimal serviceCapabilityScore;

    @ApiModelProperty(value = "效益产出得分")
    private BigDecimal benefitOutputScore;

    @ApiModelProperty(value = "安全生产得分")
    private BigDecimal safetyProductionScore;

    @ApiModelProperty(value = "其他得分")
    private BigDecimal otherScore;

    @ApiModelProperty(value = "各维度分数JSON")
    private String scoresJson;

    @ApiModelProperty(value = "总得分")
    private BigDecimal totalScore;

    @ApiModelProperty(value = "绩效分档：A/B/C/D")
    private String grade;

    @ApiModelProperty(value = "数据来源")
    private String dataSource;
}
