package com.park.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.park.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 园区行文文件实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_document")
public class ParkDocument extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 园区ID
     */
    private Long parkId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件URL
     */
    private String fileUrl;
}
