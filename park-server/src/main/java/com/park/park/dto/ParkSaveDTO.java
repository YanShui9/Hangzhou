package com.park.park.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 园区新增/修改请求 DTO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "园区新增/修改请求")
public class ParkSaveDTO {

    @ApiModelProperty(value = "园区ID（修改时必填）")
    private Long id;

    @NotBlank(message = "园区名称不能为空")
    @ApiModelProperty(value = "园区名称", required = true)
    private String parkName;

    @ApiModelProperty(value = "园区类型：1=制造类, 2=服务类")
    private Integer parkType;

    @ApiModelProperty(value = "所属区县ID")
    private Long districtId;

    @ApiModelProperty(value = "区县名称")
    private String districtName;

    @ApiModelProperty(value = "园区地址")
    private String address;

    @ApiModelProperty(value = "已建建筑面积（亩）")
    private BigDecimal buildArea;

    @ApiModelProperty(value = "实际用地数（亩）")
    private BigDecimal landArea;

    @ApiModelProperty(value = "联系人")
    private String contactName;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "园区简介")
    private String introduction;

    @ApiModelProperty(value = "星级：null=未评定, 3=三星, 4=四星, 5=五星")
    private Integer starLevel;
}
