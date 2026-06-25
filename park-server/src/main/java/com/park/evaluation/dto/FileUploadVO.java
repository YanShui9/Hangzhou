package com.park.evaluation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 评价文件上传返回 DTO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "文件上传响应")
public class FileUploadVO {

    @ApiModelProperty(value = "文件ID")
    private Long id;

    @ApiModelProperty(value = "原始文件名")
    private String name;

    @ApiModelProperty(value = "文件访问URL")
    private String url;

    @ApiModelProperty(value = "文件大小（字节）")
    private Long size;

    @ApiModelProperty(value = "文件类型：image/pdf/word/excel/other")
    private String fileType;
}
