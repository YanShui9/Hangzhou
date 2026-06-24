package com.park.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.common.result.R;
import com.park.system.entity.DistrictInfo;
import com.park.system.mapper.DistrictMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 区县信息控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/districts")
@Api(tags = "区县信息")
public class DistrictController {

    @Autowired
    private DistrictMapper districtMapper;

    /**
     * 查询全部区县列表（按 sort_order 排序）
     */
    @GetMapping
    @ApiOperation(value = "区县列表", notes = "查询全部区县，按 sort_order 排序")
    public R<List<DistrictInfo>> listDistricts() {
        LambdaQueryWrapper<DistrictInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DistrictInfo::getSortOrder);
        List<DistrictInfo> list = districtMapper.selectList(wrapper);
        return R.ok(list);
    }
}
