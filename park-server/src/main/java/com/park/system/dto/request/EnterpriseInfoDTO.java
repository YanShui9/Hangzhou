package com.park.system.dto.request;

import lombok.Data;

/**
 * 企业信息DTO
 *
 * @author park-team
 */
@Data
public class EnterpriseInfoDTO {

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