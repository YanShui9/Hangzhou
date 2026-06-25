package com.park.evaluation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科技创新 - 院所合作项目实体
 * 对应 tech_project 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tech_project")
@ApiModel(description = "院所合作项目")
public class TechProject extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评价记录ID")
    private Long evaluationId;

    @ApiModelProperty(value = "所属项目名称")
    private String name;

    @ApiModelProperty(value = "附件文件ID")
    private Long fileId;

    @ApiModelProperty(value = "附件文件名")
    private String fileName;

    @ApiModelProperty(value = "附件文件URL")
    private String fileUrl;
}
