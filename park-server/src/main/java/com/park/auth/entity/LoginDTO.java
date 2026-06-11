package com.park.auth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 登录请求参数
 *
 * @author park-team
 */
@Data
@ApiModel(description = "登录请求参数")
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true, example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;

    @NotNull(message = "角色类型不能为空")
    @ApiModelProperty(value = "角色类型", required = true, example = "1", notes = "1=市级管理员, 2=区县管理员, 3=园区管理员")
    private Integer roleType;
}
