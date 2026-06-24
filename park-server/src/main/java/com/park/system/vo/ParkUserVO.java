package com.park.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 园区账号 VO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "园区账号")
public class ParkUserVO {

    @ApiModelProperty(value = "用户ID")
    private Long id;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "所属园区ID")
    private Long parkId;

    @ApiModelProperty(value = "所属园区名称")
    private String parkName;

    @ApiModelProperty(value = "所属区域名称")
    private String districtName;

    @ApiModelProperty(value = "账号状态：0=禁用, 1=启用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
