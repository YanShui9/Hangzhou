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

    @ApiModelProperty(value = "园区名称（模糊搜索）")
    private String parkName;

    @ApiModelProperty(value = "所属区域")
    private String region;

    @ApiModelProperty(value = "园区类型")
    private String type;

    @ApiModelProperty(value = "所属区域（前端参数，等同于region）")
    private String districtName;

    @ApiModelProperty(value = "园区类型（前端参数，等同于type）")
    private String parkType;

    @ApiModelProperty(value = "审核状态中文（前端参数，如 市级待审核）")
    private String auditStatus;

    @ApiModelProperty(value = "参评状态（前端参数，参评/不参评）")
    private String parkStatus;
}
