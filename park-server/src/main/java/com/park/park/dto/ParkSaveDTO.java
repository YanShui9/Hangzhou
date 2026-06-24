package com.park.park.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 园区新增/修改请求 DTO
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

    @ApiModelProperty(value = "园区类型（生产性制造类/生产性服务类）")
    private String parkType;

    @ApiModelProperty(value = "所属区县ID")
    private Long districtId;

    @ApiModelProperty(value = "区县名称")
    private String districtName;

    @ApiModelProperty(value = "园区地址")
    private String address;

    @ApiModelProperty(value = "已建建筑面积（平方米）")
    private BigDecimal buildArea;

    @ApiModelProperty(value = "实际用地面积（亩）")
    private BigDecimal landArea;

    @ApiModelProperty(value = "园区简介")
    private String introduction;

    @ApiModelProperty(value = "园区图片（JSON数组格式，最多6张）")
    private String parkImages;

    @ApiModelProperty(value = "星级：null=未评定, 3=三星, 4=四星, 5=五星")
    private Integer starLevel;

    @ApiModelProperty(value = "园区状态（已投运/在建/规划）")
    private String parkStatus;

    @ApiModelProperty(value = "开发模式")
    private String devMode;

    @ApiModelProperty(value = "土地来源")
    private String landSource;

    @ApiModelProperty(value = "土地性质")
    private String landNature;

    @ApiModelProperty(value = "是否改造提升")
    private String isUpgrade;

    @ApiModelProperty(value = "改造提升内容")
    private String upgradeContent;

    @ApiModelProperty(value = "主导产业")
    private String mainIndustry;

    @ApiModelProperty(value = "园区剩余可租面积（平方米）")
    private BigDecimal rentRemainArea;

    @ApiModelProperty(value = "园区剩余可售面积（平方米）")
    private BigDecimal saleRemainArea;

    @ApiModelProperty(value = "已出租面积（平方米）")
    private BigDecimal rentedArea;

    @ApiModelProperty(value = "公共配套设施")
    private String publicFacilities;

    @ApiModelProperty(value = "公共配套服务")
    private String publicServices;

    @ApiModelProperty(value = "运营机构名称")
    private String operationOrgName;

    @ApiModelProperty(value = "运营机构性质（民营/国有/集体/其他）")
    private String operationOrgNature;

    @ApiModelProperty(value = "机构负责人")
    private String orgLeader;

    @ApiModelProperty(value = "机构负责人手机")
    private String orgLeaderPhone;

    @ApiModelProperty(value = "机构联系人")
    private String orgContact;

    @ApiModelProperty(value = "机构联系人手机")
    private String orgContactPhone;
}
