package com.park.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业信息实体类
 *
 * @author park-team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseInfo {

    private Long id;
    private String enterpriseName;
    private String unifiedCode;
    private String district;
    private String parkName;
    private String address;
    private String industry;
    private String status;
    private String enterTime;
    private String legalPerson;
    private String contactName;
    private String contactPhone;
    private String registeredCapital;
    private String registerDate;
    private String parkEvaluation;
}