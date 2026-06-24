package com.park.operation.controller;

import com.park.common.exception.BusinessException;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.operation.dto.EnterpriseListParseResult;
import com.park.operation.dto.OperationQuarterQueryDTO;
import com.park.operation.dto.OperationQuarterSaveDTO;
import com.park.operation.entity.ParkOperationQuarter;
import com.park.operation.service.EnterpriseListParseService;
import com.park.operation.service.OperationQuarterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/operation-quarter")
@Api(tags = "园区运营数据季度填报")
public class OperationQuarterController {

    @Autowired
    private OperationQuarterService operationQuarterService;

    @Autowired
    private EnterpriseListParseService enterpriseListParseService;

    @Value("${park.file.template-path:template/}")
    private String templatePath;

    @GetMapping("/list")
    @ApiOperation(value = "查询季度填报列表", notes = "根据条件查询园区运营数据季度填报列表")
    public R<List<ParkOperationQuarter>> list(
            @ApiParam(value = "园区ID") @RequestParam(required = false) Long parkId,
            @ApiParam(value = "年份") @RequestParam(required = false) Integer year,
            @ApiParam(value = "季度（1-4）") @RequestParam(required = false) Integer quarter) {
        return R.ok(operationQuarterService.listByQuery(parkId, year, quarter));
    }

    @GetMapping("/list/{parkId}/{year}")
    @ApiOperation(value = "查询指定园区指定年份的季度填报", notes = "根据园区ID和年份查询季度填报列表")
    public R<List<ParkOperationQuarter>> listByParkIdAndYear(
            @ApiParam(value = "园区ID", required = true) @PathVariable Long parkId,
            @ApiParam(value = "年份", required = true) @PathVariable Integer year) {
        return R.ok(operationQuarterService.listByParkIdAndYear(parkId, year));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询季度填报详情", notes = "根据ID查询季度填报详情")
    public R<ParkOperationQuarter> getById(
            @ApiParam(value = "主键ID", required = true) @PathVariable Long id) {
        return R.ok(operationQuarterService.getById(id));
    }

    @GetMapping("/query")
    @ApiOperation(value = "查询季度填报", notes = "根据园区ID、年份、季度查询")
    public R<ParkOperationQuarter> getByParkIdYearQuarter(
            @ApiParam(value = "园区ID", required = true) @RequestParam Long parkId,
            @ApiParam(value = "年份", required = true) @RequestParam Integer year,
            @ApiParam(value = "季度（1-4）", required = true) @RequestParam Integer quarter) {
        ParkOperationQuarter entity = operationQuarterService.getByParkIdYearQuarter(parkId, year, quarter);
        return R.ok(entity);
    }

    @PostMapping
    @ApiOperation(value = "新增季度填报", notes = "新增一条园区运营数据季度填报记录")
    public R<Long> add(@Valid @RequestBody OperationQuarterSaveDTO dto) {
        dto.setId(null);
        return R.ok(operationQuarterService.save(dto));
    }

    @PutMapping
    @ApiOperation(value = "修改季度填报", notes = "修改园区运营数据季度填报记录")
    public R<Void> update(@Valid @RequestBody OperationQuarterSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "ID不能为空");
        }
        operationQuarterService.save(dto);
        return R.ok();
    }

    @PostMapping("/save-or-update")
    @ApiOperation(value = "保存或更新季度填报", notes = "根据园区ID、年份、季度保存或更新季度填报数据")
    public R<Long> saveOrUpdate(@Valid @RequestBody OperationQuarterSaveDTO dto) {
        ParkOperationQuarter existing = operationQuarterService.getByParkIdYearQuarter(
                dto.getParkId(), dto.getYear(), dto.getQuarter());
        
        if (existing != null) {
            dto.setId(existing.getId());
        } else {
            dto.setId(null);
        }
        
        return R.ok(operationQuarterService.save(dto));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除季度填报", notes = "根据ID删除季度填报记录")
    public R<Void> delete(
            @ApiParam(value = "主键ID", required = true) @PathVariable Long id) {
        operationQuarterService.deleteById(id);
        return R.ok();
    }

    @GetMapping("/download/template/enterprise-list")
    @ApiOperation(value = "下载入驻企业名单模板", notes = "下载入驻企业名单Excel模板文件")
    public ResponseEntity<Resource> downloadEnterpriseListTemplate() throws IOException {
        String templateName = "产业发展数据模板.xlsx";
        
        if (templateName.contains("..") || templateName.contains("/") || templateName.contains("\\")) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "非法文件名");
        }

        File templateFile = new File(templatePath + templateName);
        if (!templateFile.exists()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "模板文件不存在");
        }

        Resource resource = new FileSystemResource(templateFile);
        String contentType = Files.probeContentType(templateFile.toPath());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String encodedFileName = java.net.URLEncoder.encode(templateName, "UTF-8").replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }

    @PostMapping("/upload/enterprise-list")
    @ApiOperation(value = "上传并解析企业名单", notes = "上传企业名单Excel文件，自动解析统计企业数量和荣誉类型")
    public R<EnterpriseListParseResult> uploadAndParseEnterpriseList(
            @ApiParam(value = "园区ID", required = true) @RequestParam Long parkId,
            @ApiParam(value = "企业名单Excel文件", required = true) @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择要上传的文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只支持Excel文件（.xlsx或.xls格式）");
        }

        try (InputStream inputStream = file.getInputStream()) {
            EnterpriseListParseResult result = enterpriseListParseService.parseAndCalculate(parkId, inputStream);
            return R.ok(result);
        } catch (IOException e) {
            log.error("解析Excel文件失败", e);
            throw new BusinessException(ResultCode.SERVER_ERROR, "解析文件失败");
        }
    }
}
