package com.park.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 区县账号 VO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "区县账号")
public class DistrictUserVO {

    @ApiModelProperty(value = "用户ID")
    private Long id;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "所属区域ID")
    private Long districtId;

    @ApiModelProperty(value = "所属区域名称")
    private String districtName;

    @ApiModelProperty(value = "账号状态：0=禁用, 1=启用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
