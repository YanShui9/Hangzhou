package com.park.evaluation.dto;

import com.park.evaluation.entity.CultivationRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 企业培育保存 DTO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "企业培育保存请求")
public class CultivationRecordSaveDTO {

    @ApiModelProperty(value = "主键ID（修改时必填）")
    private Long id;

    @ApiModelProperty(value = "评价记录ID")
    private Long evaluationId;

    @ApiModelProperty(value = "所属项目名称")
    private String projectName;

    @ApiModelProperty(value = "附件文件ID")
    private Long fileId;

    @ApiModelProperty(value = "附件文件名")
    private String fileName;

    @ApiModelProperty(value = "附件文件URL")
    private String fileUrl;

    /**
     * 转换为 Entity
     */
    public CultivationRecord toEntity() {
        CultivationRecord entity = new CultivationRecord();
        entity.setId(this.id);
        entity.setEvaluationId(this.evaluationId);
        entity.setProjectName(this.projectName);
        entity.setFileId(this.fileId);
        entity.setFileName(this.fileName);
        entity.setFileUrl(this.fileUrl);
        return entity;
    }
}
