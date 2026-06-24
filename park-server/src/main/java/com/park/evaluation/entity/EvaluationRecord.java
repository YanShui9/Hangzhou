package com.park.evaluation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 评价记录实体
 * 对应 evaluation_record 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("evaluation_record")
@ApiModel(description = "评价记录")
public class EvaluationRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "园区ID")
    private Long parkId;

    @ApiModelProperty(value = "评价年度")
    @TableField("year")
    private Integer evalYear;

    /**
     * 状态值：
     * 0 - 草稿
     * 1 - 待区县审
     * 2 - 待市局审
     * 3 - 通过
     * 4 - 驳回
     */
    @ApiModelProperty(value = "状态：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回")
    private Integer status;

    @ApiModelProperty(value = "总分")
    private BigDecimal totalScore;

    @ApiModelProperty(value = "绩效分档：A/B/C/D")
    private String grade;

    @ApiModelProperty(value = "驳回类别")
    private String rejectCategory;

    /**
     * 参评状态：
     * 0 - 不参评
     * 1 - 参评
     */
    @ApiModelProperty(value = "参评状态：0=不参评, 1=参评")
    @TableField("eval_status")
    private Integer evalStatus;
}
