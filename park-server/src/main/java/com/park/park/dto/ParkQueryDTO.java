package com.park.park.dto;

import com.park.common.result.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 园区查询条件 DTO
 * 注意：所有字段均为可选项（null 表示不做筛选）。
 * 字段类型与 ParkInfo 实体保持一致，避免 MyBatis-Plus 查询时的类型不匹配。
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

    @ApiModelProperty(value = "园区类型：制造类 / 服务类")
    private String parkType;

    @ApiModelProperty(value = "星级：1(一星级) / 2(二星级) / 3(三星级) / 4(四星级) / 5(五星级)")
    private Integer starLevel;

    @ApiModelProperty(value = "园区认定：已认定 / 未认定")
    private String recognition;

    @ApiModelProperty(value = "园区状态：规划中 / 建设中 / 已投运")
    private String parkStatus;

    @ApiModelProperty(value = "年度（示例：2025）")
    private Integer year;
}
