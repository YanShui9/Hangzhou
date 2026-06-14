package com.park.park.entity;

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
 *
 * @author park-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_info")
@ApiModel(description = "园区基础信息")
public class ParkInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "是否改造提升")
    private String isUpgrade;

    @ApiModelProperty(value = "改造提升内容")
    private String upgradeContent;

    @ApiModelProperty(value = "主导产业")
    private String mainIndustry;

    @ApiModelProperty(value = "园区介绍")
    private String introduction;

    @ApiModelProperty(value = "实际用地面积（亩）")
    private BigDecimal landArea;

    @ApiModelProperty(value = "已建建筑面积（平方米）")
    private BigDecimal buildArea;

    @ApiModelProperty(value = "园区剩余可租面积（平方米）")
    private BigDecimal rentRemainArea;

    @ApiModelProperty(value = "园区剩余可售面积（平方米）")
    private BigDecimal saleRemainArea;

    @ApiModelProperty(value = "已出租面积（平方米）")
    private BigDecimal rentedArea;

    @ApiModelProperty(value = "公共配套设施")
    private String publicFacilities;

    @ApiModelProperty(value = "公共配套服务")
    private String publicServices;

    // ========== 运营机构信息 ==========

    @ApiModelProperty(value = "运营机构名称")
    private String operationOrgName;

    @ApiModelProperty(value = "运营机构统一社会信用代码")
    private String operationOrgCode;

    @ApiModelProperty(value = "运营机构性质（民营/国有/集体/其他）")
    private String operationOrgNature;

    @ApiModelProperty(value = "机构负责人")
    private String orgLeader;

    @ApiModelProperty(value = "机构负责人手机")
    private String orgLeaderPhone;

    @ApiModelProperty(value = "机构联系人")
    private String orgContact;

    @ApiModelProperty(value = "机构联系人手机")
    private String orgContactPhone;

    // ========== 入驻企业统计 ==========

    @ApiModelProperty(value = "入驻企业总数（家）")
    private Integer enterpriseCount;

    @ApiModelProperty(value = "规模以上企业（家）")
    private Integer aboveScaleCount;

    @ApiModelProperty(value = "高新技术企业（家）")
    private Integer highTechCount;

    @ApiModelProperty(value = "科技中小企业（家）")
    private Integer techSmeCount;

    @ApiModelProperty(value = "上市企业（家）")
    private Integer listedCount;

    @ApiModelProperty(value = "隐形冠军企业（家）")
    private Integer hiddenChampionCount;

    @ApiModelProperty(value = "国家级专精特新小巨人企业（家）")
    private Integer nationalSrtiCount;

    @ApiModelProperty(value = "省专精特新中小企业（家）")
    private Integer provincialSrtiCount;

    @ApiModelProperty(value = "创新型中小企业（家）")
    private Integer innovativeSmeCount;

    // ========== 人才统计 ==========

    @ApiModelProperty(value = "入驻企业员工人数（人）")
    private Integer employeeCount;

    @ApiModelProperty(value = "国千人才（人）")
    private Integer nationalTalent;

    @ApiModelProperty(value = "省千人才（人）")
    private Integer provincialTalent;

    @ApiModelProperty(value = "硕士/副高以上（人）")
    private Integer masterAbove;

    @ApiModelProperty(value = "正高级工程师人数（人）")
    private Integer seniorEngineer;

    @ApiModelProperty(value = "高级工程师人数（人）")
    private Integer engineer;

    @ApiModelProperty(value = "高级技师人数（人）")
    private Integer seniorTechnician;

    @ApiModelProperty(value = "硕士以上人数（人）")
    private Integer masterDegree;

    // ========== 专利统计 ==========

    @ApiModelProperty(value = "专利拥有量（件）")
    private Integer patentTotal;

    @ApiModelProperty(value = "发明专利（件）")
    private Integer patentInvention;

    @ApiModelProperty(value = "实用新型专利（件）")
    private Integer patentUtility;

    @ApiModelProperty(value = "外观设计专利（件）")
    private Integer patentDesign;
}
