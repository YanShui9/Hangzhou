package com.park.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 园区账号实体类
 *
 * @author park-team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkAccount {

    private Long id;
    private String companyName;
    private String unifiedCode;
    private String district;
    private String parkName;
    private LocalDateTime createTime;
}