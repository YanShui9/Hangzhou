package com.park.park.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 园区新增/修改请求 DTO
 * 字段名与前端表单保持一致，便于直接接收前端数据
 *
 * @author park-team
 */
@Data
@ApiModel(description = "园区新增/修改请求")
public class ParkSaveDTO {

    @ApiModelProperty(value = "园区ID（修改时必填）")
    private Long id;

    @ApiModelProperty(value = "年度（默认当前年）")
    private Integer year;

    @NotBlank(message = "园区名称不能为空")
    @ApiModelProperty(value = "园区名称", required = true)
    private String parkName;

    @ApiModelProperty(value = "园区代码（如 DS2026001）")
    private String parkCode;

    @ApiModelProperty(value = "园区类型：制造类 / 服务类 / 科技类 / 数字经济")
    private String parkType;

    @ApiModelProperty(value = "所属区县ID")
    private Long districtId;

    @NotBlank(message = "所属区域不能为空")
    @ApiModelProperty(value = "区县名称", required = true)
    private String districtName;

    @ApiModelProperty(value = "园区状态：规划中 / 建设中 / 已投运")
    private String parkStatus;

    @ApiModelProperty(value = "园区认定：已认定 / 未认定")
    private String recognition;

    @ApiModelProperty(value = "星级评定：3/4/5")
    private Integer starLevel;

    @ApiModelProperty(value = "开发模式：政府主导 / 企业自建 / 政企合作 / 市场运营")
    private String devMode;

    @ApiModelProperty(value = "土地来源：划拨 / 出让 / 租赁 / 国有建设用地出让 / 集体建设用地")
    private String landSource;

    @ApiModelProperty(value = "土地性质：工业用地(M1/M2/M3) / 商业用地(B1) / 商务用地(B2) / 其他")
    private String landNature;

    @ApiModelProperty(value = "主导产业：数字经济 / 智能制造 / 生物医药 / 新材料 / 新能源 / 集成电路 / 科技服务 / 其他")
    private String leadingIndustry;

    @ApiModelProperty(value = "是否升级改造：是 / 否")
    private String isUpgradable;

    @ApiModelProperty(value = "改造提升内容")
    private String upgradeContent;

    @ApiModelProperty(value = "园区地址")
    private String address;

    // ========== 联系方式 ==========

    @ApiModelProperty(value = "运营单位")
    private String operatorUnit;

    @ApiModelProperty(value = "运营性质：国有企业 / 民营企业 / 事业单位 / 其他")
    private String operatorNature;

    @ApiModelProperty(value = "负责人")
    private String personInCharge;

    @ApiModelProperty(value = "负责人电话")
    private String inChargePhone;

    @ApiModelProperty(value = "联系人")
    private String contactPerson;

    @ApiModelProperty(value = "联系人电话")
    private String contactPhone;

    // ========== 园区面积 ==========

    @ApiModelProperty(value = "实际用地面积（亩）")
    private BigDecimal landArea;

    @ApiModelProperty(value = "已建建筑面积（平方米）")
    private BigDecimal buildArea;

    @ApiModelProperty(value = "园区已租面积（平方米）")
    private BigDecimal leasedArea;

    @ApiModelProperty(value = "园区剩余可租面积（平方米）")
    private BigDecimal remainingLeasableArea;

    @ApiModelProperty(value = "园区剩余可售面积（平方米）")
    private BigDecimal remainingSellableArea;

    // ========== 入驻企业统计 ==========

    @ApiModelProperty(value = "入驻企业总数（家）")
    private Integer enterpriseCount;

    @ApiModelProperty(value = "规模以上企业（家）")
    private Integer aboveScaleCount;

    @ApiModelProperty(value = "高新技术企业（家）")
    private Integer highTechCount;

    @ApiModelProperty(value = "科技型中小企业（家）")
    private Integer techSmeCount;

    @ApiModelProperty(value = "隐形冠军及培育企业（家）")
    private Integer hiddenChampionCount;

    @ApiModelProperty(value = "专精特新\"小巨人\"企业（家）")
    private Integer nationalSpecializedCount;

    @ApiModelProperty(value = "创新型中小企业（家）")
    private Integer innovativeSmeCount;

    @ApiModelProperty(value = "省专精特新中小企业（家）")
    private Integer provincialSpecializedCount;

    // ========== 入驻员工统计 ==========

    @ApiModelProperty(value = "入驻企业员工总数（人）")
    private Integer employeeCount;

    @ApiModelProperty(value = "\"国千\"人才人数（人）")
    private Integer national1000TalentCount;

    @ApiModelProperty(value = "\"省千\"人才人数（人）")
    private Integer provincial1000TalentCount;

    @ApiModelProperty(value = "正高级工程师人数（人）")
    private Integer seniorEngineerCount;

    @ApiModelProperty(value = "高级工程师人数（人）")
    private Integer senior2EngineerCount;

    @ApiModelProperty(value = "高级技师人数（人）")
    private Integer seniorTechnicianCount;

    @ApiModelProperty(value = "硕士及副高以上人数（人）")
    private Integer masterAndAboveCount;

    @ApiModelProperty(value = "硕士以上人数（人）")
    private Integer masterCount;

    // ========== 创新专利统计 ==========

    @ApiModelProperty(value = "专利拥有量（件）")
    private Integer patentTotalCount;

    @ApiModelProperty(value = "发明专利（件）")
    private Integer inventionCount;

    @ApiModelProperty(value = "实用新型专利（件）")
    private Integer utilityModelCount;

    @ApiModelProperty(value = "外观设计专利（件）")
    private Integer appearanceCount;

    // ========== 园区简介 ==========

    @ApiModelProperty(value = "园区介绍")
    private String introduction;

    @ApiModelProperty(value = "园区图片（JSON数组）")
    private String parkImages;

    @ApiModelProperty(value = "公共配套设施")
    private String publicFacilities;

    @ApiModelProperty(value = "公共配套服务")
    private String publicServices;
}