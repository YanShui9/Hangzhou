package com.park.system.dto.request;

import lombok.Data;

/**
 * 数据仓库DTO
 *
 * @author park-team
 */
@Data
public class SystemDataDTO {

    private Long id;
    private String dataName;
    private String year;
    private String attachment;
}