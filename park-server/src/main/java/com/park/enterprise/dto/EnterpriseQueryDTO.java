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

    @ApiModelProperty(value = "企业名称（模糊查询）")
    private String enterpriseName;

    @ApiModelProperty(value = "统一社会信用代码（模糊查询）")
    private String creditCode;

    @ApiModelProperty(value = "所属园区ID")
    private Long parkId;

    @ApiModelProperty(value = "园区ID列表（区县管理员使用）")
    private List<Long> parkIds;

    @ApiModelProperty(value = "所属区县名称")
    private String districtName;

    @ApiModelProperty(value = "行业名称")
    private String industryName;

    @ApiModelProperty(value = "经营状态")
    private String status;

    @ApiModelProperty(value = "是否参评：0=不参评, 1=参评")
    private Integer isParticipate;

    @ApiModelProperty(value = "企业荣誉")
    private String enterpriseHonor;
}
