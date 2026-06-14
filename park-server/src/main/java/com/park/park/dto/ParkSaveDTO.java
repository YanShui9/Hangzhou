package com.park.park.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 园区新增/修改请求 DTO
 * 字段与 ParkInfo 实体一一对应，类型保持一致，
 * 以保证 BeanUtils.copyProperties 能正确赋值。
 *
 * @author park-team
 */
@Data
@ApiModel(description = "园区新增/修改请求")
public class ParkSaveDTO {

    @ApiModelProperty(value = "园区ID（修改时必填）")
    private Long id;

    @NotBlank(message = "园区名称不能为空")
    @ApiModelProperty(value = "园区名称", required = true)
    private String parkName;

    @ApiModelProperty(value = "园区代码（如 DS2026001）")
    private String parkCode;

    @ApiModelProperty(value = "园区类型：制造类 / 服务类")
    private String parkType;

    @ApiModelProperty(value = "所属区县ID")
    private Long districtId;

    @ApiModelProperty(value = "区县名称")
    private String districtName;

    @ApiModelProperty(value = "园区地址")
    private String address;

    @ApiModelProperty(value = "园区认定：已认定 / 未认定")
    private String recognition;

    @ApiModelProperty(value = "星级：null=未评定, 3=三星级, 4=四星级, 5=五星级")
    private Integer starLevel;

    @ApiModelProperty(value = "园区状态：规划中 / 建设中 / 已投运")
    private String parkStatus;

    @ApiModelProperty(value = "开发模式：政府主导 / 企业自建 / 政企合作 / 市场运营")
    private String devMode;

    @ApiModelProperty(value = "土地来源：划拨 / 出让 / 租赁")
    private String landSource;

    @ApiModelProperty(value = "土地性质：工业用地 / 商业用地 / 商务用地")
    private String landNature;

    @ApiModelProperty(value = "已建建筑面积（亩）")
    private BigDecimal buildArea;

    @ApiModelProperty(value = "实际用地面积（亩）")
    private BigDecimal landArea;

    @ApiModelProperty(value = "园区简介")
    private String introduction;

    @ApiModelProperty(value = "园区图片（Base64或URL）")
    private String parkImage;
}
