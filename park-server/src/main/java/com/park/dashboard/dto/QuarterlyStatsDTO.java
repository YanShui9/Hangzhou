package com.park.dashboard.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 季度统计 DTO
 *
 * @author park-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "季度统计")
public class QuarterlyStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "季度标识（yyyy-Qn）")
    private String quarter;

    @ApiModelProperty(value = "就业人数")
    private Integer employment;

    @ApiModelProperty(value = "企业数量")
    private Integer enterpriseCount;
}
