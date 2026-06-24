package com.park.enterprise.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 企业导出 DTO
 *
 * @author park-team
 */
@Data
public class EnterpriseExportDTO {

    @ExcelProperty("序号")
    private Integer index;

    @ExcelProperty("企业名称")
    private String enterpriseName;

    @ExcelProperty("统一社会信用代码")
    private String creditCode;

    @ExcelProperty("所属区域")
    private String districtName;

    @ExcelProperty("所属园区")
    private String parkName;

    @ExcelProperty("企业荣誉")
    private String enterpriseHonor;

    @ExcelProperty("参评状态")
    private String isParticipateText;

    @ExcelProperty("法定代表人")
    private String legalPerson;

    @ExcelProperty("联系人")
    private String contactName;

    @ExcelProperty("联系电话")
    private String contactPhone;

    @ExcelProperty("经营状态")
    private String status;
}
