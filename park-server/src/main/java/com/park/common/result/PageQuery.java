package com.park.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页查询参数基类
 *
 * @author park-team
 */
@Data
@ApiModel(description = "分页查询参数")
public class PageQuery {

    @ApiModelProperty(value = "当前页码，默认为1", example = "1")
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页数量，默认为10", example = "10")
    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 500, message = "每页数量最大为500")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序字段")
    private String orderBy;

    @ApiModelProperty(value = "排序方式（asc/desc）")
    private String orderDirection = "desc";
}
