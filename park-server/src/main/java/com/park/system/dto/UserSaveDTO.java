package com.park.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户新增/修改请求 DTO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "用户新增/修改请求")
public class UserSaveDTO {

    @ApiModelProperty(value = "用户ID（修改时必填）")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @ApiModelProperty(value = "密码（新增时必填，修改时可为空）")
    private String password;

    @NotNull(message = "角色类型不能为空")
    @ApiModelProperty(value = "角色类型：1=市级管理员, 2=区县管理员, 3=园区管理员", required = true)
    private Integer roleType;

    @ApiModelProperty(value = "所属区县ID（区县管理员必填）")
    private Long districtId;

    @ApiModelProperty(value = "所属园区ID（园区管理员必填）")
    private Long parkId;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty(value = "账号状态：0=禁用, 1=启用", required = true)
    private Integer status;

    @ApiModelProperty(value = "所属部门")
    private String department;
}
