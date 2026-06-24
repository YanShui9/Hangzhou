package com.park.audit.dto;

import lombok.Data;

/**
 * 审核列表项DTO
 * 包含评价记录和园区信息
 */
@Data
public class AuditListItemDTO {

    /**
     * 评价记录ID
     */
    private Long id;

    /**
     * 园区ID
     */
    private Long parkId;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 所属区域
     */
    private String districtName;

    /**
     * 园区类型：1=生产性制造类, 2=生产性服务类
     */
    private Integer parkType;

    /**
     * 参评状态：0=不参评, 1=参评
     */
    private Integer evaluationStatus;

    /**
     * 审核状态：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回, 5=已上报, 6=已终止
     */
    private Integer auditStatus;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 评价年份
     */
    private Integer evaluationYear;
}
