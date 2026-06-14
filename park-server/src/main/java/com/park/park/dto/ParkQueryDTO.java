package com.park.park.dto;

import com.park.common.result.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 园区查询条件 DTO
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "园区查询条件")
public class ParkQueryDTO extends PageQuery {

    @ApiModelProperty(value = "园区ID")
    private Long id;

    @ApiModelProperty(value = "园区名称（模糊查询）")
    private String parkName;

    @ApiModelProperty(value = "区县名称")
    private String districtName;

    @ApiModelProperty(value = "园区类型：1=制造类, 2=服务类")
    private Integer parkType;

    @ApiModelProperty(value = "星级")
    private Integer starLevel;
}
