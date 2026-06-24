package com.park.operation.dto;

import com.park.operation.entity.ParkOperationQuarter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(description = "园区运营数据季度填报保存请求")
public class OperationQuarterSaveDTO {

    @ApiModelProperty(value = "主键ID（修改时必填）")
    private Long id;

    @ApiModelProperty(value = "园区ID", required = true)
    @NotNull(message = "园区ID不能为空")
    private Long parkId;

    @ApiModelProperty(value = "年份", required = true)
    @NotNull(message = "年份不能为空")
    private Integer year;

    @ApiModelProperty(value = "季度（1-4）", required = true)
    @NotNull(message = "季度不能为空")
    @Min(value = 1, message = "季度必须为1-4")
    @Max(value = 4, message = "季度必须为1-4")
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

    public ParkOperationQuarter toEntity() {
        ParkOperationQuarter entity = new ParkOperationQuarter();
        entity.setId(this.id);
        entity.setParkId(this.parkId);
        entity.setYear(this.year);
        entity.setQuarter(this.quarter);
        entity.setCurrentEnterprises(this.currentEnterprises);
        entity.setTotalEmployees(this.totalEmployees);
        entity.setEnterpriseListFileId(this.enterpriseListFileId);
        entity.setEnterpriseListFileName(this.enterpriseListFileName);
        entity.setEnterpriseListFileUrl(this.enterpriseListFileUrl);
        entity.setRentedArea(this.rentedArea);
        entity.setAvailableRentArea(this.availableRentArea);
        entity.setAvailableSaleArea(this.availableSaleArea);
        entity.setEmployeeCount(this.employeeCount);
        entity.setNationalTalentCount(this.nationalTalentCount);
        entity.setProvincialTalentCount(this.provincialTalentCount);
        entity.setSeniorEngineerCount(this.seniorEngineerCount);
        entity.setEngineerCount(this.engineerCount);
        entity.setTechnicianCount(this.technicianCount);
        entity.setMasterAndSeniorCount(this.masterAndSeniorCount);
        entity.setMasterCount(this.masterCount);
        entity.setPatentCount(this.patentCount);
        entity.setInventionPatentCount(this.inventionPatentCount);
        entity.setUtilityPatentCount(this.utilityPatentCount);
        entity.setDesignPatentCount(this.designPatentCount);
        entity.setAboveScaleCount(this.aboveScaleCount);
        entity.setHighTechCount(this.highTechCount);
        entity.setTechSmeCount(this.techSmeCount);
        entity.setHiddenChampionCount(this.hiddenChampionCount);
        entity.setNationalSrtiCount(this.nationalSrtiCount);
        entity.setInnovativeSmeCount(this.innovativeSmeCount);
        entity.setProvincialSrtiCount(this.provincialSrtiCount);
        entity.setRemark(this.remark);
        return entity;
    }
}
