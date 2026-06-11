package com.park.park.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 园区信息实体
 * 对应 park_info 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_info")
@ApiModel(description = "园区信息")
public class ParkInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区名称")
    private String parkName;

    @ApiModelProperty(value = "园区类型：1=制造类, 2=服务类")
    private Integer parkType;

    @ApiModelProperty(value = "所属区县ID")
    private Long districtId;

    @ApiModelProperty(value = "区县名称")
    private String districtName;

    @ApiModelProperty(value = "园区地址")
    private String address;

    @ApiModelProperty(value = "已建建筑面积（亩）")
    private BigDecimal buildArea;

    @ApiModelProperty(value = "实际用地数（亩）")
    private BigDecimal landArea;

    @ApiModelProperty(value = "联系人")
    private String contactName;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "园区简介")
    private String introduction;

    @ApiModelProperty(value = "星级：null=未评定, 3=三星, 4=四星, 5=五星")
    private Integer starLevel;
}
