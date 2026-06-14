package com.park.system.dto;

import com.park.common.result.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询条件 DTO
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "用户查询条件")
public class UserQueryDTO extends PageQuery {

    @ApiModelProperty(value = "用户名（模糊查询）")
    private String username;

    @ApiModelProperty(value = "角色类型：1=市级管理员, 2=区县管理员, 3=园区管理员")
    private Integer roleType;

    @ApiModelProperty(value = "所属区县ID")
    private Long districtId;

    @ApiModelProperty(value = "账号状态：0=禁用, 1=启用")
    private Integer status;
}
