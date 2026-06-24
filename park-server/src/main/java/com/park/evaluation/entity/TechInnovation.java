package com.park.evaluation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 科技创新记录实体
 * 对应 tech_innovation 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tech_innovation")
@ApiModel(description = "科技创新记录")
public class TechInnovation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评价记录ID")
    private Long evaluationId;

    @ApiModelProperty(value = "所属项目名称")
    private String projectName;

    @ApiModelProperty(value = "附件文件ID（关联tech_file）")
    private Long fileId;

    @ApiModelProperty(value = "附件文件名（冗余字段，方便列表显示）")
    private String fileName;

    @ApiModelProperty(value = "附件文件URL")
    private String fileUrl;

    @ApiModelProperty(value = "人才类别：A/B/C/D类")
    private String category;

    @ApiModelProperty(value = "评定人才姓名")
    private String name;

    @ApiModelProperty(value = "评定日期")
    private Date date;

    @ApiModelProperty(value = "所属企业")
    private String company;
}
