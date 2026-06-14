package com.park.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 区县信息实体
 * 对应 district_info 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("district_info")
@ApiModel(description = "区县信息")
public class DistrictInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "区县编码（如 330102）")
    private String districtCode;

    @ApiModelProperty(value = "区县名称")
    private String districtName;

    @ApiModelProperty(value = "所属地市")
    private String city;

    @ApiModelProperty(value = "所属省份")
    private String province;

    @ApiModelProperty(value = "排序")
    private Integer sortOrder;
}
