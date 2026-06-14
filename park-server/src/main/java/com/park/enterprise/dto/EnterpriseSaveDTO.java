package com.park.enterprise.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 企业新增/修改请求 DTO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "企业新增/修改请求")
public class EnterpriseSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "企业ID（修改时必填）")
    private Long id;

    @ApiModelProperty(value = "企业名称", required = true)
    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    @ApiModelProperty(value = "所属园区ID", required = true)
    @NotNull(message = "所属园区不能为空")
    private Long parkId;

    @ApiModelProperty(value = "行业门类代码")
    private String industryCode;

    @ApiModelProperty(value = "行业名称")
    private String industryName;

    @ApiModelProperty(value = "经营状态")
    private String status;

    @ApiModelProperty(value = "注册日期")
    private LocalDate registerDate;

    @ApiModelProperty(value = "法定代表人")
    private String legalPerson;

    @ApiModelProperty(value = "注册资本（万元）")
    private BigDecimal registeredCapital;

    @ApiModelProperty(value = "联系人")
    private String contactName;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "是否参评：0=不参评, 1=参评")
    private Integer isParticipate;

    @ApiModelProperty(value = "不参评原因")
    private String participateReason;
}
