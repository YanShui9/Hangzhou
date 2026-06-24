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
 * 企业信息实体类
 *
 * @author park-team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("enterprise_info")
public class EnterpriseInfo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String enterpriseName;
    private String unifiedCode;
    private String district;
    private String parkName;
    private String address;
    private String industry;
    private String status;
    private String enterTime;
    private String legalPerson;
    private String contactName;
    private String contactPhone;
    private String registeredCapital;
    private String registerDate;
    private String parkEvaluation;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}