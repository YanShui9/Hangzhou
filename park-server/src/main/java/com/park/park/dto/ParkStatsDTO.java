package com.park.park.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 园区统计数据 DTO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "园区统计数据")
public class ParkStatsDTO {

    @ApiModelProperty(value = "入驻企业总数（家）")
    private Integer enterpriseCount;

    @ApiModelProperty(value = "规模以上企业（家）")
    private Integer largeEnterpriseCount;

    @ApiModelProperty(value = "高新技术企业（家）")
    private Integer highTechEnterpriseCount;

    @ApiModelProperty(value = "科技型中小企业（家）")
    private Integer smeCount;

    @ApiModelProperty(value = "创新型中小企业（家）")
    private Integer innovativeSmeCount;

    @ApiModelProperty(value = "国家专精特新中小企业（家）")
    private Integer specializedSmeCount;

    @ApiModelProperty(value = "入驻企业员工总数（人）")
    private Integer employeeCount;

    @ApiModelProperty(value = "\"国千\"人才人数（人）")
    private Integer nationalTalentCount;

    @ApiModelProperty(value = "\"省千\"人才人数（人）")
    private Integer provincialTalentCount;

    @ApiModelProperty(value = "正高级工程师人数（人）")
    private Integer seniorEngineerCount;

    @ApiModelProperty(value = "高级工程师人数（人）")
    private Integer engineerCount;

    @ApiModelProperty(value = "高级技师人数（人）")
    private Integer seniorTechnicianCount;

    @ApiModelProperty(value = "硕士及副高以上人数（人）")
    private Integer masterCount;

    @ApiModelProperty(value = "博士以上人数（人）")
    private Integer doctorCount;

    @ApiModelProperty(value = "专利拥有量（件）")
    private Integer patentCount;

    @ApiModelProperty(value = "发明专利（件）")
    private Integer inventionPatentCount;

    @ApiModelProperty(value = "实用新型专利（件）")
    private Integer utilityModelCount;

    @ApiModelProperty(value = "外观设计专利（件）")
    private Integer designPatentCount;
}