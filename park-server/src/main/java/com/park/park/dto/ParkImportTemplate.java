package com.park.park.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 园区导入模板DTO
 * 对应 Excel 文件中的列，与前端表单字段保持一致
 *
 * @author park-team
 */
@Data
public class ParkImportTemplate {

    @ExcelProperty(value = "园区名称", index = 0)
    private String parkName;

    @ExcelProperty(value = "园区代码", index = 1)
    private String parkCode;

    @ExcelProperty(value = "园区类型", index = 2)
    private String parkType;

    @ExcelProperty(value = "所属区域", index = 3)
    private String districtName;

    @ExcelProperty(value = "园区状态", index = 4)
    private String parkStatus;

    @ExcelProperty(value = "园区认定", index = 5)
    private String recognition;

    @ExcelProperty(value = "星级评定", index = 6)
    private Integer starLevel;

    @ExcelProperty(value = "开发模式", index = 7)
    private String devMode;

    @ExcelProperty(value = "土地来源", index = 8)
    private String landSource;

    @ExcelProperty(value = "土地性质", index = 9)
    private String landNature;

    @ExcelProperty(value = "主导产业", index = 10)
    private String leadingIndustry;

    @ExcelProperty(value = "是否升级改造", index = 11)
    private String isUpgradable;

    @ExcelProperty(value = "改造提升内容", index = 12)
    private String upgradeContent;

    @ExcelProperty(value = "园区地址", index = 13)
    private String address;

    @ExcelProperty(value = "年度", index = 14)
    private Integer year;

    @ExcelProperty(value = "运营单位", index = 15)
    private String operatorUnit;

    @ExcelProperty(value = "运营性质", index = 16)
    private String operatorNature;

    @ExcelProperty(value = "负责人", index = 17)
    private String personInCharge;

    @ExcelProperty(value = "负责人电话", index = 18)
    private String inChargePhone;

    @ExcelProperty(value = "联系人", index = 19)
    private String contactPerson;

    @ExcelProperty(value = "联系人电话", index = 20)
    private String contactPhone;

    @ExcelProperty(value = "实际用地面积（亩）", index = 21)
    private java.math.BigDecimal landArea;

    @ExcelProperty(value = "已建建筑面积（平方米）", index = 22)
    private java.math.BigDecimal buildArea;

    @ExcelProperty(value = "园区已租面积（平方米）", index = 23)
    private java.math.BigDecimal leasedArea;

    @ExcelProperty(value = "园区剩余可租面积（平方米）", index = 24)
    private java.math.BigDecimal remainingLeasableArea;

    @ExcelProperty(value = "园区剩余可售面积（平方米）", index = 25)
    private java.math.BigDecimal remainingSellableArea;

    @ExcelProperty(value = "入驻企业总数（家）", index = 26)
    private Integer enterpriseCount;

    @ExcelProperty(value = "规模以上企业（家）", index = 27)
    private Integer aboveScaleCount;

    @ExcelProperty(value = "高新技术企业（家）", index = 28)
    private Integer highTechCount;

    @ExcelProperty(value = "科技型中小企业（家）", index = 29)
    private Integer techSmeCount;

    @ExcelProperty(value = "隐形冠军及培育企业（家）", index = 30)
    private Integer hiddenChampionCount;

    @ExcelProperty(value = "专精特新\"小巨人\"企业（家）", index = 31)
    private Integer nationalSpecializedCount;

    @ExcelProperty(value = "创新型中小企业（家）", index = 32)
    private Integer innovativeSmeCount;

    @ExcelProperty(value = "省专精特新中小企业（家）", index = 33)
    private Integer provincialSpecializedCount;

    @ExcelProperty(value = "入驻企业员工总数（人）", index = 34)
    private Integer employeeCount;

    @ExcelProperty(value = "\"国千\"人才人数（人）", index = 35)
    private Integer national1000TalentCount;

    @ExcelProperty(value = "\"省千\"人才人数（人）", index = 36)
    private Integer provincial1000TalentCount;

    @ExcelProperty(value = "正高级工程师人数（人）", index = 37)
    private Integer seniorEngineerCount;

    @ExcelProperty(value = "高级工程师人数（人）", index = 38)
    private Integer senior2EngineerCount;

    @ExcelProperty(value = "高级技师人数（人）", index = 39)
    private Integer seniorTechnicianCount;

    @ExcelProperty(value = "硕士及副高以上人数（人）", index = 40)
    private Integer masterAndAboveCount;

    @ExcelProperty(value = "硕士以上人数（人）", index = 41)
    private Integer masterCount;

    @ExcelProperty(value = "专利拥有量（件）", index = 42)
    private Integer patentTotalCount;

    @ExcelProperty(value = "发明专利（件）", index = 43)
    private Integer inventionCount;

    @ExcelProperty(value = "实用新型专利（件）", index = 44)
    private Integer utilityModelCount;

    @ExcelProperty(value = "外观设计专利（件）", index = 45)
    private Integer appearanceCount;

    @ExcelProperty(value = "园区介绍", index = 46)
    private String introduction;

    @ExcelProperty(value = "公共配套设施", index = 47)
    private String publicFacilities;

    @ExcelProperty(value = "公共配套服务", index = 48)
    private String publicServices;
}