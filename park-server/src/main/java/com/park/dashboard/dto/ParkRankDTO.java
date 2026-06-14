package com.park.dashboard.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 园区排名 DTO
 *
 * @author park-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "园区排名")
public class ParkRankDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区ID")
    private Long parkId;

    @ApiModelProperty(value = "园区名称")
    private String parkName;

    @ApiModelProperty(value = "评价得分")
    private BigDecimal score;

    @ApiModelProperty(value = "排名")
    private Integer rank;
}
