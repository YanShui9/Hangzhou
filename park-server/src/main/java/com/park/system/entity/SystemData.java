package com.park.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据仓库实体类
 *
 * @author park-team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemData {

    private Long id;
    private String dataName;
    private String year;
    private String attachment;
}