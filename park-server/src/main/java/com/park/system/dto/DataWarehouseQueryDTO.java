package com.park.system.dto;

import com.park.common.result.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据仓库查询条件 DTO
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "数据仓库查询条件")
public class DataWarehouseQueryDTO extends PageQuery {

    @ApiModelProperty(value = "数据名称（模糊查询）")
    private String name;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "年度")
    private Integer year;
}
