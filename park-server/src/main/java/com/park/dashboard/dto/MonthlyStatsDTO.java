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
 * 月度统计 DTO
 *
 * @author park-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "月度统计")
public class MonthlyStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "月份（yyyy-MM）")
    private String month;

    @ApiModelProperty(value = "营收金额（万元）")
    private BigDecimal revenue;

    @ApiModelProperty(value = "就业人数")
    private Integer employment;

    @ApiModelProperty(value = "企业数量")
    private Integer enterpriseCount;
}
