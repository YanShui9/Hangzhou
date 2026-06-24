package com.park.park.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 园区导出DTO
 * 对应导出Excel的列
 *
 * @author park-team
 */
@Data
public class ParkExportDTO {

    @ExcelProperty(value = "序号", index = 0)
    private Integer rowNum;

    @ExcelProperty(value = "园区名称", index = 1)
    private String parkName;

    @ExcelProperty(value = "年度", index = 2)
    private Integer year;

    @ExcelProperty(value = "园区代码", index = 3)
    private String parkCode;

    @ExcelProperty(value = "所属区域", index = 4)
    private String districtName;

    @ExcelProperty(value = "主导产业", index = 5)
    private String leadingIndustry;

    @ExcelProperty(value = "园区认定", index = 6)
    private String recognition;

    @ExcelProperty(value = "星级评定", index = 7)
    private String starRating;

    @ExcelProperty(value = "园区状态", index = 8)
    private String parkStatus;

    @ExcelProperty(value = "园区类型", index = 9)
    private String parkType;

    @ExcelProperty(value = "开发模式", index = 10)
    private String devMode;

    @ExcelProperty(value = "土地来源", index = 11)
    private String landSource;

    @ExcelProperty(value = "土地性质", index = 12)
    private String landNature;

    @ExcelProperty(value = "园区地址", index = 13)
    private String address;

    @ExcelProperty(value = "运营单位", index = 14)
    private String operationOrgName;

    @ExcelProperty(value = "负责人", index = 15)
    private String orgLeader;

    @ExcelProperty(value = "负责人电话", index = 16)
    private String orgLeaderPhone;

    @ExcelProperty(value = "联系人", index = 17)
    private String orgContact;

    @ExcelProperty(value = "联系人电话", index = 18)
    private String orgContactPhone;

    @ExcelProperty(value = "实际用地面积（亩）", index = 19)
    private java.math.BigDecimal landArea;

    @ExcelProperty(value = "已建建筑面积（亩）", index = 20)
    private java.math.BigDecimal buildArea;

    @ExcelProperty(value = "入驻企业总数（家）", index = 21)
    private Integer enterpriseCount;

    @ExcelProperty(value = "入驻企业员工总数（人）", index = 22)
    private Integer employeeCount;

    @ExcelProperty(value = "专利总数（件）", index = 23)
    private Integer patentTotal;

    @ExcelProperty(value = "园区简介", index = 24)
    private String introduction;
}