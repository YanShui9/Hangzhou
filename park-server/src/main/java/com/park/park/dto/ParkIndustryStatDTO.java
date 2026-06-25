package com.park.park.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 园区主要产业统计 DTO
 * 用于返回园区企业数量前三的产业信息
 *
 * @author park-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "园区主要产业统计")
public class ParkIndustryStatDTO {

    @ApiModelProperty(value = "产业名称")
    private String industryName;

    @ApiModelProperty(value = "企业数量")
    private Integer enterpriseCount;

    @ApiModelProperty(value = "排名")
    private Integer rank;
}
