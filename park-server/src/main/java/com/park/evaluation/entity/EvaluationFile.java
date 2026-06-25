package com.park.evaluation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 评价附件实体
 * 对应 evaluation_file 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("evaluation_file")
@ApiModel(description = "评价附件")
public class EvaluationFile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "原始文件名")
    private String name;

    @ApiModelProperty(value = "存储文件名（UUID）")
    private String storedName;

    @ApiModelProperty(value = "文件存储路径")
    private String path;

    @ApiModelProperty(value = "文件访问URL")
    private String url;

    @ApiModelProperty(value = "文件大小（字节）")
    private Long size;

    @ApiModelProperty(value = "文件类型：image/pdf/word/excel/other")
    private String fileType;

    @ApiModelProperty(value = "业务模块：tech_innovation / tech_project / enterprise / cultivation / industry")
    private String bizType;

    @ApiModelProperty(value = "关联业务ID")
    private Long bizId;

    @ApiModelProperty(value = "上传用户ID")
    private Long uploadUserId;
}
