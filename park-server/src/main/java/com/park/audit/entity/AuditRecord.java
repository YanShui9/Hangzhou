package com.park.audit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审核记录实体
 * 对应 audit_record 表
 *
 * @author park-team
 */
@Data
@TableName("audit_record")
@ApiModel(description = "审核记录")
public class AuditRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "评价记录ID")
    private Long evaluationId;

    @ApiModelProperty(value = "审核人ID")
    private Long auditorId;

    @ApiModelProperty(value = "审核人姓名")
    private String auditorName;

    @ApiModelProperty(value = "审核人角色")
    private Integer auditorRole;

    /**
     * 操作：
     * 1 - 通过
     * 2 - 驳回
     */
    @ApiModelProperty(value = "操作：1=通过, 2=驳回")
    private Integer action;

    @ApiModelProperty(value = "审核意见")
    private String opinion;

    @ApiModelProperty(value = "变更前状态")
    private String fromStatus;

    @ApiModelProperty(value = "变更后状态")
    private String toStatus;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
