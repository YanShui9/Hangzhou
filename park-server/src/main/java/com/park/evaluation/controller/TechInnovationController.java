package com.park.evaluation.controller;

import com.park.common.result.R;
import com.park.evaluation.dto.TechInnovationSaveDTO;
import com.park.evaluation.entity.TechInnovation;
import com.park.evaluation.service.TechInnovationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 科技创新 Controller
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/tech-innovations")
@Api(tags = "科技创新管理")
public class TechInnovationController {

    @Autowired
    private TechInnovationService techInnovationService;

    @GetMapping("/list/{evaluationId}")
    @ApiOperation(value = "查询科技创新列表", notes = "根据评价记录ID查询科技创新列表")
    public R<List<TechInnovation>> list(
            @ApiParam(value = "评价记录ID", required = true) @PathVariable Long evaluationId) {
        return R.ok(techInnovationService.listByEvaluationId(evaluationId));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询科技创新详情", notes = "根据ID查询科技创新详情")
    public R<TechInnovation> getById(
            @ApiParam(value = "主键ID", required = true) @PathVariable Long id) {
        return R.ok(techInnovationService.getById(id));
    }

    @PostMapping
    @ApiOperation(value = "新增科技创新", notes = "新增一条科技创新记录")
    public R<Long> add(@Valid @RequestBody TechInnovationSaveDTO dto) {
        dto.setId(null);
        return R.ok(techInnovationService.save(dto));
    }

    @PutMapping
    @ApiOperation(value = "修改科技创新", notes = "修改科技创新记录")
    public R<Void> update(@Valid @RequestBody TechInnovationSaveDTO dto) {
        techInnovationService.save(dto);
        return R.ok();
    }

    @PostMapping("/batch-save/{evaluationId}")
    @ApiOperation(value = "批量保存科技创新", notes = "批量保存科技创新（先删后插）")
    public R<Void> batchSave(
            @ApiParam(value = "评价记录ID", required = true) @PathVariable Long evaluationId,
            @RequestBody List<TechInnovationSaveDTO> dtos) {
        techInnovationService.batchSave(evaluationId, dtos);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除科技创新", notes = "根据ID删除科技创新记录")
    public R<Void> delete(
            @ApiParam(value = "主键ID", required = true) @PathVariable Long id) {
        techInnovationService.deleteById(id);
        return R.ok();
    }
}
