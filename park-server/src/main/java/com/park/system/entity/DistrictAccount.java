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
 * 区县账号实体类
 *
 * @author park-team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("district_account")
public class DistrictAccount {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String phone;
    private String district;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}