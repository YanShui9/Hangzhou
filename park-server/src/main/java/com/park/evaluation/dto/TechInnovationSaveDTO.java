package com.park.evaluation.dto;

import com.park.evaluation.entity.TechInnovation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 科技创新保存 DTO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "科技创新保存请求")
public class TechInnovationSaveDTO {

    @ApiModelProperty(value = "主键ID（修改时必填）")
    private Long id;

    @ApiModelProperty(value = "评价记录ID")
    private Long evaluationId;

    @ApiModelProperty(value = "所属项目名称")
    private String projectName;

    @ApiModelProperty(value = "附件文件ID（已上传返回的）")
    private Long fileId;

    @ApiModelProperty(value = "附件文件名")
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

    /**
     * 转换为 Entity
     */
    public TechInnovation toEntity() {
        TechInnovation entity = new TechInnovation();
        entity.setId(this.id);
        entity.setEvaluationId(this.evaluationId);
        entity.setProjectName(this.projectName);
        entity.setFileId(this.fileId);
        entity.setFileName(this.fileName);
        entity.setFileUrl(this.fileUrl);
        entity.setCategory(this.category);
        entity.setName(this.name);
        entity.setDate(this.date);
        entity.setCompany(this.company);
        return entity;
    }
}
