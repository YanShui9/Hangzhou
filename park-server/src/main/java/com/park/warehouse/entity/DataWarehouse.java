package com.park.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据仓库实体
 * 对应 data_warehouse 表
 * 用于园区认定、星级评定、产业方向导入
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_warehouse")
@ApiModel(description = "数据仓库")
public class DataWarehouse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区名称（匹配 park_info）")
    private String parkName;

    @ApiModelProperty(value = "园区认定：已认定/未认定")
    private String recognition;

    @ApiModelProperty(value = "星级：3/4/5")
    private Integer starLevel;

    @ApiModelProperty(value = "主导产业（逗号分隔）")
    private String leadingIndustry;

    @ApiModelProperty(value = "导入年度")
    private Integer importYear;

    @ApiModelProperty(value = "导入人")
    private String importBy;

    @ApiModelProperty(value = "导入时间")
    private java.util.Date importTime;

    @ApiModelProperty(value = "备注")
    private String remark;
}