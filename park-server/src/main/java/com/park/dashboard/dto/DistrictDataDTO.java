package com.park.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据大屏区县分布数据 DTO
 *
 * @author park-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "区县分布数据")
public class DistrictDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "区县名称")
    private String name;

    @ApiModelProperty(value = "园区总数")
    private Long parkCount;

    @ApiModelProperty(value = "生产制造类园区数")
    private Long manufacturingCount;

    @ApiModelProperty(value = "生产服务类园区数")
    private Long serviceCount;

    @ApiModelProperty(value = "入驻企业员工总数")
    private Long employeeCount;

    @ApiModelProperty(value = "入驻企业总数")
    private Long enterpriseCount;

    @ApiModelProperty(value = "参评企业总数")
    private Long participateEnterpriseCount;

    @ApiModelProperty(value = "国家专精特新小巨人企业数")
    private Long nationalSpecializedCount;

    @ApiModelProperty(value = "省专精特新中小企业数")
    private Long provincialSpecializedCount;

    @ApiModelProperty(value = "创新型中小企业数")
    private Long innovativeSmeCount;
}
