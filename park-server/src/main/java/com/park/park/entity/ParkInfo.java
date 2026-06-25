package com.park.park.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 园区基础信息实体
 * 对应 park_info 表
 * 字段名与前端表单保持一致，通过 @TableField 映射到数据库列名
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_info")
@ApiModel(description = "园区基础信息")
public class ParkInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "年度（默认当前年）")
    private Integer year;

    @ApiModelProperty(value = "园区代码")
    private String parkCode;

    @ApiModelProperty(value = "园区名称")
    private String parkName;

    @ApiModelProperty(value = "园区类型（生产性制造类/生产性服务类）")
    private String parkType;

    @ApiModelProperty(value = "所属区县ID")
    private Long districtId;

    @ApiModelProperty(value = "区县名称")
    private String districtName;

    @ApiModelProperty(value = "园区地址")
    private String address;

    @ApiModelProperty(value = "园区星级")
    private Integer starLevel;

    @ApiModelProperty(value = "绩效评价（A/B/C/D/未参评）")
    private String performance;

    @ApiModelProperty(value = "园区状态（已投运/在建/规划）")
    private String parkStatus;

    @ApiModelProperty(value = "开发模式")
    private String devMode;

    @ApiModelProperty(value = "土地来源")
    private String landSource;

    @ApiModelProperty(value = "土地性质")
    private String landNature;

    @ApiModelProperty(value = "园区认定")
    private String recognition;

    @ApiModelProperty(value = "是否升级改造")
    @TableField("is_upgrade")
    private String isUpgradable;

    @ApiModelProperty(value = "改造提升内容")
    private String upgradeContent;

    @ApiModelProperty(value = "主导产业")
    @TableField("main_industry")
    private String leadingIndustry;

    @ApiModelProperty(value = "园区介绍")
    private String introduction;

    @ApiModelProperty(value = "实际用地面积（亩）")
    private BigDecimal landArea;

    @ApiModelProperty(value = "已建建筑面积（平方米）")
    private BigDecimal buildArea;

    @ApiModelProperty(value = "园区已租面积（平方米）")
    @TableField("rented_area")
    private BigDecimal leasedArea;

    @ApiModelProperty(value = "园区剩余可租面积（平方米）")
    @TableField("rent_remain_area")
    private BigDecimal remainingLeasableArea;

    @ApiModelProperty(value = "园区剩余可售面积（平方米）")
    @TableField("sale_remain_area")
    private BigDecimal remainingSellableArea;

    @ApiModelProperty(value = "公共配套设施")
    private String publicFacilities;

    @ApiModelProperty(value = "公共配套服务")
    private String publicServices;

    // ========== 运营机构信息 ==========

    @ApiModelProperty(value = "运营单位")
    @TableField("operation_org_name")
    private String operatorUnit;

    @ApiModelProperty(value = "运营机构统一社会信用代码")
    private String operationOrgCode;

    @ApiModelProperty(value = "运营性质")
    @TableField("operation_org_nature")
    private String operatorNature;

    @ApiModelProperty(value = "负责人")
    @TableField("org_leader")
    private String personInCharge;

    @ApiModelProperty(value = "负责人电话")
    @TableField("org_leader_phone")
    private String inChargePhone;

    @ApiModelProperty(value = "联系人")
    @TableField("org_contact")
    private String contactPerson;

    @ApiModelProperty(value = "联系人电话")
    @TableField("org_contact_phone")
    private String contactPhone;

    // ========== 入驻企业统计 ==========

    @ApiModelProperty(value = "入驻企业总数（家）")
    private Integer enterpriseCount;

    @ApiModelProperty(value = "规模以上企业（家）")
    private Integer aboveScaleCount;

    @ApiModelProperty(value = "高新技术企业（家）")
    private Integer highTechCount;

    @ApiModelProperty(value = "科技型中小企业（家）")
    private Integer techSmeCount;

    @ApiModelProperty(value = "上市企业（家）")
    private Integer listedCount;

    @ApiModelProperty(value = "隐形冠军及培育企业（家）")
    private Integer hiddenChampionCount;

    @ApiModelProperty(value = "国家级专精特新\"小巨人\"企业（家）")
    @TableField("national_srti_count")
    private Integer nationalSpecializedCount;

    @ApiModelProperty(value = "省专精特新中小企业（家）")
    @TableField("provincial_srti_count")
    private Integer provincialSpecializedCount;

    @ApiModelProperty(value = "创新型中小企业（家）")
    private Integer innovativeSmeCount;

    // ========== 人才统计 ==========

    @ApiModelProperty(value = "入驻企业员工总数（人）")
    private Integer employeeCount;

    @ApiModelProperty(value = "\"国千\"人才人数（人）")
    @TableField("national_talent")
    private Integer national1000TalentCount;

    @ApiModelProperty(value = "\"省千\"人才人数（人）")
    @TableField("provincial_talent")
    private Integer provincial1000TalentCount;

    @ApiModelProperty(value = "正高级工程师人数（人）")
    @TableField("senior_engineer")
    private Integer seniorEngineerCount;

    @ApiModelProperty(value = "高级工程师人数（人）")
    @TableField("engineer")
    private Integer senior2EngineerCount;

    @ApiModelProperty(value = "高级技师人数（人）")
    @TableField("senior_technician")
    private Integer seniorTechnicianCount;

    @ApiModelProperty(value = "硕士及副高以上人数（人）")
    @TableField("master_above")
    private Integer masterAndAboveCount;

    @ApiModelProperty(value = "硕士以上人数（人）")
    @TableField("master_degree")
    private Integer masterCount;

    // ========== 专利统计 ==========

    @ApiModelProperty(value = "专利拥有量（件）")
    @TableField("patent_total")
    private Integer patentTotalCount;

    @ApiModelProperty(value = "发明专利（件）")
    @TableField("patent_invention")
    private Integer inventionCount;

    @ApiModelProperty(value = "实用新型专利（件）")
    @TableField("patent_utility")
    private Integer utilityModelCount;

    @ApiModelProperty(value = "外观设计专利（件）")
    @TableField("patent_design")
    private Integer appearanceCount;

    @ApiModelProperty(value = "园区图片（JSON数组）")
    @TableField("park_image")
    private String parkImages;
}