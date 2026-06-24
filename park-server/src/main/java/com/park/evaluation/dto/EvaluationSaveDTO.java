package com.park.evaluation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 评价记录保存 DTO
 *
 * @author park-team
 */
@Data
@ApiModel(description = "评价记录保存请求")
public class EvaluationSaveDTO {

    @ApiModelProperty(value = "评价记录ID（修改时必填）")
    private Long id;

    @NotNull(message = "园区ID不能为空")
    @ApiModelProperty(value = "园区ID", required = true)
    private Long parkId;

    @NotNull(message = "评价年份不能为空")
    @ApiModelProperty(value = "评价年份", required = true)
    private Integer evalYear;

    @ApiModelProperty(value = "评价总分")
    private BigDecimal totalScore;

    @ApiModelProperty(value = "绩效分档：A/B/C/D")
    private String grade;

    @ApiModelProperty(value = "驳回类别")
    private String rejectCategory;
}
