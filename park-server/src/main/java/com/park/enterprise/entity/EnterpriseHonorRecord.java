package com.park.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业荣誉记录实体
 * 对应 enterprise_honor_record 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("enterprise_honor_record")
@ApiModel(description = "企业荣誉记录")
public class EnterpriseHonorRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    @ApiModelProperty(value = "所属园区ID")
    private Long parkId;

    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "荣誉大类")
    private String honorCategory;

    @ApiModelProperty(value = "荣誉类型")
    private String honorType;

    @ApiModelProperty(value = "数量")
    private Integer honorCount;

    @ApiModelProperty(value = "来源文件")
    private String sourceFile;
}
