package com.park.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入驻企业实体
 * 对应 enterprise_info 表
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("enterprise_info")
@ApiModel(description = "入驻企业")
public class EnterpriseInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    @ApiModelProperty(value = "所属园区ID")
    private Long parkId;

    @ApiModelProperty(value = "行业门类代码")
    private String industryCode;

    @ApiModelProperty(value = "行业名称")
    private String industryName;

    @ApiModelProperty(value = "经营状态")
    private String status;

    @ApiModelProperty(value = "注册日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registerDate;

    @ApiModelProperty(value = "法定代表人")
    private String legalPerson;

    @ApiModelProperty(value = "注册资本（万元）")
    private BigDecimal registeredCapital;

    @ApiModelProperty(value = "联系人")
    private String contactName;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "是否参评：0=不参评, 1=参评")
    private Integer isParticipate;

    @ApiModelProperty(value = "不参评原因")
    private String participateReason;

    /* ========== 非表字段 - 列表页展示使用（由关联查询填充） ========== */

    @ApiModelProperty(value = "所属园区名称")
    private transient String parkName;

    @ApiModelProperty(value = "所属区县名称")
    private transient String districtName;

    @ApiModelProperty(value = "企业荣誉（国高/小巨人/省专/单项冠军...）")
    private transient String enterpriseHonor;

    @ApiModelProperty(value = "备注")
    private transient String remark;
}
