package com.park.system.dto.request;

import lombok.Data;

/**
 * 园区账号DTO
 *
 * @author park-team
 */
@Data
public class ParkAccountDTO {

    private Long id;
    private String companyName;
    private String unifiedCode;
    private String district;
    private String parkName;
}