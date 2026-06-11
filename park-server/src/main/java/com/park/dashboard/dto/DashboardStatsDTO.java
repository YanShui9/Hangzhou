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
 * 数据驾驶舱统计响应 DTO
 *
 * @author park-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "统计数据响应")
public class DashboardStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区总数")
    private Long totalParks;

    @ApiModelProperty(value = "企业总数")
    private Long totalEnterprises;

    @ApiModelProperty(value = "就业总人数")
    private Long totalEmployment;

    @ApiModelProperty(value = "总营收（万元）")
    private BigDecimal totalRevenue;

    @ApiModelProperty(value = "待审核数量")
    private Long pendingAudits;
}
