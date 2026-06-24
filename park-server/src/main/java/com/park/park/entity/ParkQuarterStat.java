package com.park.park.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 园区季度填报状态实体
 * 对应 park_quarter_stat 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_quarter_stat")
@ApiModel(description = "园区季度填报状态")
public class ParkQuarterStat extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区ID")
    private Long parkId;

    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "季度：1/2/3/4")
    private Integer quarter;

    @ApiModelProperty(value = "填报状态：0=未填报, 1=已填报")
    private Integer status;

    @ApiModelProperty(value = "入驻企业数")
    private Integer enterpriseCount;

    @ApiModelProperty(value = "员工总数")
    private Integer employeeCount;

    @ApiModelProperty(value = "专利总数")
    private Integer patentCount;

    @ApiModelProperty(value = "填报人")
    private String reportBy;

    @ApiModelProperty(value = "填报时间")
    private java.util.Date reportTime;
}