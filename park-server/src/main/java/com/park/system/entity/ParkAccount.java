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
 * 园区账号实体类
 *
 * @author park-team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("park_account")
public class ParkAccount {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String companyName;
    private String unifiedCode;
    private String district;
    private String parkName;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}