package com.park.operation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(description = "企业名单解析结果")
public class EnterpriseListParseResult {

    @ApiModelProperty(value = "企业总数")
    private Integer totalCount;

    @ApiModelProperty(value = "规模以上企业数")
    private Integer aboveScaleCount;

    @ApiModelProperty(value = "高新技术企业数")
    private Integer highTechCount;

    @ApiModelProperty(value = "科技型中小企业数")
    private Integer techSmeCount;

    @ApiModelProperty(value = "隐形冠军企业数")
    private Integer hiddenChampionCount;

    @ApiModelProperty(value = "国家级专精特新小巨人企业数")
    private Integer nationalSrtiCount;

    @ApiModelProperty(value = "创新型中小企业数")
    private Integer innovativeSmeCount;

    @ApiModelProperty(value = "省专精特新中小企业数")
    private Integer provincialSrtiCount;

    @ApiModelProperty(value = "企业名称列表")
    private List<String> enterpriseNames = new ArrayList<>();

    @ApiModelProperty(value = "企业信用代码列表")
    private List<String> creditCodes = new ArrayList<>();
}
