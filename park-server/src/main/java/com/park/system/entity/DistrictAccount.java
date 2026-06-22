package com.park.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 区县账号实体类
 *
 * @author park-team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictAccount {

    private Long id;
    private String name;
    private String phone;
    private String district;
    private LocalDateTime createTime;
}