package com.park.operation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_operation_quarter")
@ApiModel(description = "园区运营数据季度填报")
public class ParkOperationQuarter extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区ID")
    private Long parkId;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "季度（1-4）")
    private Integer quarter;

    @ApiModelProperty(value = "当前入驻企业（家）")
    private Integer currentEnterprises;

    @ApiModelProperty(value = "入驻企业总人数（人）")
    private Integer totalEmployees;

    @ApiModelProperty(value = "入驻企业名单附件ID")
    private Long enterpriseListFileId;

    @ApiModelProperty(value = "入驻企业名单附件名")
    private String enterpriseListFileName;

    @ApiModelProperty(value = "入驻企业名单附件URL")
    private String enterpriseListFileUrl;

    @ApiModelProperty(value = "园区已租面积（平方米）")
    private BigDecimal rentedArea;

    @ApiModelProperty(value = "园区剩余可租面积（平方米）")
    private BigDecimal availableRentArea;

    @ApiModelProperty(value = "园区剩余可售面积（平方米）")
    private BigDecimal availableSaleArea;

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
    private Integer technicianCount;

    @ApiModelProperty(value = "硕士及副高以上人数（人）")
    private Integer masterAndSeniorCount;

    @ApiModelProperty(value = "硕士以上人数（人）")
    private Integer masterCount;

    @ApiModelProperty(value = "专利拥有量（件）")
    private Integer patentCount;

    @ApiModelProperty(value = "发明专利（件）")
    private Integer inventionPatentCount;

    @ApiModelProperty(value = "实用新型专利（件）")
    private Integer utilityPatentCount;

    @ApiModelProperty(value = "外观设计专利（件）")
    private Integer designPatentCount;

    @ApiModelProperty(value = "规模以上企业（家）")
    private Integer aboveScaleCount;

    @ApiModelProperty(value = "高新技术企业（家）")
    private Integer highTechCount;

    @ApiModelProperty(value = "科技型中小企业（家）")
    private Integer techSmeCount;

    @ApiModelProperty(value = "隐形冠军及培育企业（家）")
    private Integer hiddenChampionCount;

    @ApiModelProperty(value = "国家级专精特新小巨人企业（家）")
    private Integer nationalSrtiCount;

    @ApiModelProperty(value = "创新型中小企业（家）")
    private Integer innovativeSmeCount;

    @ApiModelProperty(value = "省专精特新中小企业（家）")
    private Integer provincialSrtiCount;

    @ApiModelProperty(value = "备注")
    private String remark;
}
