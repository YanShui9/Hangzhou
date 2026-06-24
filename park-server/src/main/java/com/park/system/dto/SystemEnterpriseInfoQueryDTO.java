package com.park.system.dto;

import com.park.common.result.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统设置 - 企业信息查询条件 DTO
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "系统设置企业信息查询条件")
public class SystemEnterpriseInfoQueryDTO extends PageQuery {

    @ApiModelProperty(value = "关键字（企业名称/信用代码模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "所属区域ID")
    private Long districtId;

    @ApiModelProperty(value = "所属园区ID")
    private Long parkId;

    @ApiModelProperty(value = "状态")
    private String status;
}
