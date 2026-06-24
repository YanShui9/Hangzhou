package com.park.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 数据仓库实体类
 *
 * @author park-team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("system_data")
public class SystemData {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String dataName;
    private String year;
    private String attachment;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}