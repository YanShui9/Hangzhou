package com.park.evaluation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评价关联企业实体
 * 对应 evaluation_enterprise 表
 * 存储产业发展数据模板导入的企业，按评价记录隔离
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("evaluation_enterprise")
@ApiModel(description = "评价关联企业")
public class EvaluationEnterprise extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评价记录ID")
    private Long evaluationId;

    @ApiModelProperty(value = "园区ID")
    private Long parkId;

    @ApiModelProperty(value = "园区名称")
    private String parkName;

    @ApiModelProperty(value = "入驻企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    @ApiModelProperty(value = "入驻开始时间")
    private String settledStartTime;

    @ApiModelProperty(value = "入驻截止时间")
    private String settledEndTime;

    @ApiModelProperty(value = "入驻起止时间（合并展示）")
    private String settledDate;

    @ApiModelProperty(value = "企业注册地址")
    private String registeredAddress;
}
