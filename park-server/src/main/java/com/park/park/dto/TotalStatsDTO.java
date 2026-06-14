package com.park.park.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全市园区统计数据传输对象
 * 用于前端"园区列表"页面顶部统计卡片
 *
 * @author park-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "全市园区统计数据")
public class TotalStatsDTO {

    @ApiModelProperty(value = "园区总数")
    private Integer parkTotal;

    @ApiModelProperty(value = "入驻企业总数")
    private Integer enterpriseTotal;

    @ApiModelProperty(value = "从业人员总数")
    private Integer employeeTotal;

    @ApiModelProperty(value = "星级园区占比")
    private String starRate;

    @ApiModelProperty(value = "制造类园区数")
    private Integer manufacturingParkCount;

    @ApiModelProperty(value = "服务类园区数")
    private Integer serviceParkCount;

    @ApiModelProperty(value = "已建建筑面积（亩）")
    private Double buildAreaTotal;

    @ApiModelProperty(value = "实际用地数（亩）")
    private Double landAreaTotal;
}
