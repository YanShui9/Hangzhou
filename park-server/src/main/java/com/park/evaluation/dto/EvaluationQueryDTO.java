package com.park.evaluation.dto;

import com.park.common.result.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 评价记录查询条件 DTO
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "评价记录查询条件")
public class EvaluationQueryDTO extends PageQuery {

    @ApiModelProperty(value = "园区ID")
    private Long parkId;

    @ApiModelProperty(value = "园区ID列表（区县管理员使用）")
    private List<Long> parkIds;

    @ApiModelProperty(value = "评价年份")
    private Integer year;

    @ApiModelProperty(value = "状态：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回")
    private Integer status;
}
