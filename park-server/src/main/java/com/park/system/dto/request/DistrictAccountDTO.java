package com.park.system.dto.request;

import lombok.Data;

/**
 * 区县账号DTO
 *
 * @author park-team
 */
@Data
public class DistrictAccountDTO {

    private Long id;
    private String name;
    private String phone;
    private String district;
}