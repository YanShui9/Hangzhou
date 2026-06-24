package com.park.auth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应结果
 *
 * @author park-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "登录响应结果")
public class LoginVO {

    @ApiModelProperty(value = "JWT Token")
    private String token;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "角色类型", notes = "1=市级管理员, 2=区县管理员, 3=园区管理员")
    private Integer roleType;

    @ApiModelProperty(value = "所属区县ID", notes = "区县管理员时有值")
    private Long districtId;

    @ApiModelProperty(value = "所属园区ID", notes = "园区管理员时有值")
    private Long parkId;
}
