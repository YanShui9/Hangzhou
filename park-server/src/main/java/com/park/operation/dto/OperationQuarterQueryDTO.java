package com.park.operation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "园区运营数据季度填报查询请求")
public class OperationQuarterQueryDTO {

    @ApiModelProperty(value = "园区ID")
    private Long parkId;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "季度（1-4）")
    private Integer quarter;
}
