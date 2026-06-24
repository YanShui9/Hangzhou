package com.park.audit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 审核请求 DTO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "审核请求")
public class AuditDTO {

    @NotNull(message = "评价记录ID不能为空")
    @ApiModelProperty(value = "评价记录ID", required = true)
    private Long evaluationId;

    @NotNull(message = "审核动作不能为空")
    @ApiModelProperty(value = "审核动作：1=通过, 2=驳回", required = true)
    private Integer action;

    @ApiModelProperty(value = "审核意见")
    private String opinion;
}
