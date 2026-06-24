package com.park.evaluation.controller;

import com.park.common.result.R;
import com.park.evaluation.dto.TechProjectSaveDTO;
import com.park.evaluation.entity.TechProject;
import com.park.evaluation.service.TechProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 院所合作项目 Controller
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/tech-projects")
@Api(tags = "院所合作项目管理")
public class TechProjectController {

    @Autowired
    private TechProjectService techProjectService;

    @GetMapping("/list/{evaluationId}")
    @ApiOperation(value = "查询院所合作项目列表", notes = "根据评价记录ID查询院所合作项目列表")
    public R<List<TechProject>> list(
            @ApiParam(value = "评价记录ID", required = true) @PathVariable Long evaluationId) {
        return R.ok(techProjectService.listByEvaluationId(evaluationId));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询院所合作项目详情", notes = "根据ID查询")
    public R<TechProject> getById(
            @ApiParam(value = "主键ID", required = true) @PathVariable Long id) {
        return R.ok(techProjectService.getById(id));
    }

    @PostMapping
    @ApiOperation(value = "新增院所合作项目", notes = "新增一个院所合作项目")
    public R<Long> add(@Valid @RequestBody TechProjectSaveDTO dto) {
        dto.setId(null);
        return R.ok(techProjectService.save(dto));
    }

    @PutMapping
    @ApiOperation(value = "修改院所合作项目", notes = "修改院所合作项目")
    public R<Void> update(@Valid @RequestBody TechProjectSaveDTO dto) {
        techProjectService.save(dto);
        return R.ok();
    }

    @PostMapping("/batch-save/{evaluationId}")
    @ApiOperation(value = "批量保存院所合作项目", notes = "批量保存院所合作项目（先删后插）")
    public R<Void> batchSave(
            @ApiParam(value = "评价记录ID", required = true) @PathVariable Long evaluationId,
            @RequestBody List<TechProjectSaveDTO> dtos) {
        techProjectService.batchSave(evaluationId, dtos);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除院所合作项目", notes = "根据ID删除")
    public R<Void> delete(
            @ApiParam(value = "主键ID", required = true) @PathVariable Long id) {
        techProjectService.deleteById(id);
        return R.ok();
    }
}
