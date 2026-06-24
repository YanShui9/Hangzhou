package com.park.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据仓库实体
 * 对应 data_warehouse 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_warehouse")
@ApiModel(description = "数据仓库")
public class DataWarehouse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据名称")
    private String name;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "是否删除：0=未删除, 1=已删除")
    private Integer deleted;
}
