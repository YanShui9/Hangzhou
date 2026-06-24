package com.park.enterprise.dto;

import com.park.common.result.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 企业查询条件 DTO
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "企业查询条件")
public class EnterpriseQueryDTO extends PageQuery {

    @ApiModelProperty(value = "关键字搜索（企业名称、信用代码等）")
    private String keyword;

    @ApiModelProperty(value = "企业名称（模糊查询）")
    private String enterpriseName;

    @ApiModelProperty(value = "所属园区ID")
    private Long parkId;

    @ApiModelProperty(value = "园区ID列表（区县管理员使用）")
    private List<Long> parkIds;

    @ApiModelProperty(value = "所属区域ID")
    private Long districtId;

    @ApiModelProperty(value = "行业名称")
    private String industryName;

    @ApiModelProperty(value = "企业荣誉")
    private String honor;

    @ApiModelProperty(value = "经营状态", notes = "可选值：存续/在业（组合状态，包含存续和在业）、停业、注销、吊销等")
    private String status;

    @ApiModelProperty(value = "登记状态", notes = "可选值：存续/在业（组合状态，包含存续和在业）、开业、注销、吊销等")
    private String registerStatus;

    @ApiModelProperty(value = "是否参评：0=不参评, 1=参评")
    private Integer isParticipate;
}