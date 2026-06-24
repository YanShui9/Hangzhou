package com.park.evaluation.controller;

import com.park.common.result.R;
import com.park.evaluation.dto.CultivationRecordSaveDTO;
import com.park.evaluation.entity.CultivationRecord;
import com.park.evaluation.service.CultivationRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 企业培育 Controller
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/cultivation-records")
@Api(tags = "企业培育管理")
public class CultivationRecordController {

    @Autowired
    private CultivationRecordService cultivationRecordService;

    @GetMapping("/list/{evaluationId}")
    @ApiOperation(value = "查询企业培育列表", notes = "根据评价记录ID查询企业培育列表")
    public R<List<CultivationRecord>> list(
            @ApiParam(value = "评价记录ID", required = true) @PathVariable Long evaluationId) {
        return R.ok(cultivationRecordService.listByEvaluationId(evaluationId));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询企业培育详情", notes = "根据ID查询企业培育详情")
    public R<CultivationRecord> getById(
            @ApiParam(value = "主键ID", required = true) @PathVariable Long id) {
        return R.ok(cultivationRecordService.getById(id));
    }

    @PostMapping
    @ApiOperation(value = "新增企业培育", notes = "新增一条企业培育记录")
    public R<Long> add(@Valid @RequestBody CultivationRecordSaveDTO dto) {
        dto.setId(null);
        return R.ok(cultivationRecordService.save(dto));
    }

    @PutMapping
    @ApiOperation(value = "修改企业培育", notes = "修改企业培育记录")
    public R<Void> update(@Valid @RequestBody CultivationRecordSaveDTO dto) {
        cultivationRecordService.save(dto);
        return R.ok();
    }

    @PostMapping("/batch-save/{evaluationId}")
    @ApiOperation(value = "批量保存企业培育", notes = "批量保存企业培育（先删后插）")
    public R<Void> batchSave(
            @ApiParam(value = "评价记录ID", required = true) @PathVariable Long evaluationId,
            @RequestBody List<CultivationRecordSaveDTO> dtos) {
        cultivationRecordService.batchSave(evaluationId, dtos);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除企业培育", notes = "根据ID删除企业培育记录")
    public R<Void> delete(
            @ApiParam(value = "主键ID", required = true) @PathVariable Long id) {
        cultivationRecordService.deleteById(id);
        return R.ok();
    }
}
