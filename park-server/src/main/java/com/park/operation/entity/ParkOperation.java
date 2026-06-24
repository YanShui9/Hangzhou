package com.park.operation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 园区运营数据实体
 * 对应 park_operation 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_operation")
@ApiModel(description = "园区运营数据")
public class ParkOperation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区ID")
    private Long parkId;

    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "季度：1/2/3/4")
    private Integer quarter;

    @ApiModelProperty(value = "入驻企业数")
    private Integer enterpriseCount;

    @ApiModelProperty(value = "员工总数")
    private Integer employeeCount;

    @ApiModelProperty(value = "已建面积")
    private BigDecimal buildArea;

    @ApiModelProperty(value = "用地面积")
    private BigDecimal landArea;

    @ApiModelProperty(value = "专利总数")
    private Integer patentCount;
}
