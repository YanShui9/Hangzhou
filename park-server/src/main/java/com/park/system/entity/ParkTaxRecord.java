package com.park.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 园区税收记录实体
 * 对应 park_tax_record 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_tax_record")
@ApiModel(description = "园区税收记录")
public class ParkTaxRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区名称")
    private String parkName;

    @ApiModelProperty(value = "园区代码")
    private String parkCode;

    @ApiModelProperty(value = "营业收入")
    private BigDecimal revenue;

    @ApiModelProperty(value = "净入库税款")
    private BigDecimal tax;

    @ApiModelProperty(value = "类型：park_total/leading_industry/enterprise_type")
    private String taxType;

    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "来源文件")
    private String sourceFile;
}
