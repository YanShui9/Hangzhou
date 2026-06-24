package com.park.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 未上报运营园区记录实体
 * 对应 unreported_park_record 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("unreported_park_record")
@ApiModel(description = "未上报运营园区记录")
public class UnreportedParkRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区名称")
    private String parkName;

    @ApiModelProperty(value = "园区代码")
    private String parkCode;

    @ApiModelProperty(value = "园区类型")
    private String parkType;

    @ApiModelProperty(value = "所属区域")
    private String districtName;

    @ApiModelProperty(value = "未上报季度")
    private String unreportedQuarter;

    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "来源文件")
    private String sourceFile;
}
