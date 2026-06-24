package com.park.enterprise.dto;

import com.park.common.result.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 企业荣誉汇总查询条件 DTO
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "企业荣誉汇总查询条件")
public class EnterpriseHonorSummaryQueryDTO extends PageQuery {

    @ApiModelProperty(value = "评价年份")
    private Integer year;

    @ApiModelProperty(value = "园区名称（模糊搜索）")
    private String parkName;

    @ApiModelProperty(value = "所属区域")
    private String region;

    @ApiModelProperty(value = "园区类型")
    private String type;

    @ApiModelProperty(value = "园区ID（数据权限-园区管理员）")
    private Long parkId;

    @ApiModelProperty(value = "园区ID列表（数据权限-区县管理员）")
    private List<Long> parkIds;
}
