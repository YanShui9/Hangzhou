package com.park.evaluation.controller;

import com.park.common.exception.BusinessException;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.evaluation.dto.FileUploadVO;
import com.park.evaluation.dto.IndustryDevelopmentParseResult;
import com.park.evaluation.entity.EvaluationFile;
import com.park.evaluation.service.EvaluationFileService;
import com.park.evaluation.service.IndustryDevelopmentParseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评价文件 Controller
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@Api(tags = "评价文件管理")
public class EvaluationFileController {

    @Autowired
    private EvaluationFileService fileService;

    @Autowired
    private IndustryDevelopmentParseService industryDevelopmentParseService;

    @Value("${park.file.upload-path:./uploads/}")
    private String uploadPath;

    @Value("${park.file.template-path:template/}")
    private String templatePath;

    private static final Map<String, String> TEMPLATE_FILE_MAP = new HashMap<>();
    static {
        TEMPLATE_FILE_MAP.put("industry_development", "产业发展数据模板.xlsx");
    }

    private static final List<String> INDUSTRY_TEMPLATE_HEADERS = Arrays.asList(
            "园区名称",
            "入园企业名称",
            "统一社会信用代码",
            "入驻开始时间",
            "入驻截止时间",
            "企业注册地址"
    );

    @PostMapping("/upload")
    @ApiOperation(value = "上传文件", notes = "上传评价相关文件，支持图片、PDF、Word、Excel等")
    public R<FileUploadVO> upload(
            @ApiParam(value = "文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "业务类型：tech_innovation / tech_project / enterprise / cultivation / industry", required = true)
            @RequestParam("bizType") String bizType,
            HttpServletRequest request) {

        Object userIdObj = request.getAttribute("userId");
        Long userId = null;
        if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        }

        return R.ok(fileService.upload(file, bizType, userId));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除文件", notes = "根据ID删除文件（包括物理文件）")
    public R<Void> delete(
            @ApiParam(value = "文件ID", required = true) @PathVariable Long id) {
        fileService.delete(id);
        return R.ok();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询文件信息", notes = "根据ID查询文件元信息")
    public R<EvaluationFile> getById(
            @ApiParam(value = "文件ID", required = true) @PathVariable Long id) {
        return R.ok(fileService.getById(id));
    }

    @GetMapping("/preview/{storedName:.+}")
    @ApiOperation(value = "预览/下载文件", notes = "根据存储文件名预览或下载文件")
    public ResponseEntity<Resource> preview(
            @ApiParam(value = "存储文件名", required = true) @PathVariable String storedName) throws IOException {

        // 兼容 /preview/template/xxx 的子路径，如果路径以 template/ 开头则按模板处理
        if (storedName.startsWith("template/")) {
            return previewTemplate(storedName.substring("template/".length()));
        }

        // 安全检查：防止路径穿越
        if (storedName.contains("..") || storedName.contains("/") || storedName.contains("\\")) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "非法文件名");
        }

        // 查找文件
        File file = findFile(storedName);
        if (file == null || !file.exists()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文件不存在");
        }

        return buildFileResponse(file, false);
    }

    @GetMapping("/download/template/{templateKey}")
    @ApiOperation(value = "下载模板文件", notes = "下载评价相关的模板文件，如产业发展数据模板")
    public ResponseEntity<Resource> downloadTemplate(
            @ApiParam(value = "模板key，如：industry_development", required = true) @PathVariable String templateKey) throws IOException {

        if (templateKey.contains("..") || templateKey.contains("/") || templateKey.contains("\\")) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "非法参数");
        }

        if ("industry_development".equals(templateKey)) {
            byte[] excelBytes = generateIndustryDevelopmentTemplate();
            ByteArrayResource resource = new ByteArrayResource(excelBytes);
            String fileName = "产业发展数据模板.xlsx";
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Expose-Headers", "Content-Disposition")
                    .body(resource);
        }

        String fileName = TEMPLATE_FILE_MAP.get(templateKey);
        if (fileName != null) {
            ClassPathResource resource = new ClassPathResource("templates/" + fileName);
            if (resource.exists()) {
                return buildClasspathFileResponse(resource, fileName, true);
            }
            File classpathFallback = new File(templatePath + fileName);
            if (classpathFallback.exists()) {
                return buildFileResponse(classpathFallback, true);
            }
        }

        File templateFile = new File(templatePath + templateKey);
        if (!templateFile.exists()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "模板文件不存在");
        }
        return buildFileResponse(templateFile, true);
    }

    private byte[] generateIndustryDevelopmentTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("产业发展数据");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < INDUSTRY_TEMPLATE_HEADERS.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(INDUSTRY_TEMPLATE_HEADERS.get(i));
            }
            for (int i = 0; i < INDUSTRY_TEMPLATE_HEADERS.size(); i++) {
                sheet.setColumnWidth(i, 20 * 256);
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    @GetMapping("/preview/template/{templateName:.+}")
    @ApiOperation(value = "预览模板文件", notes = "在浏览器内联预览模板文件，如评价年度承诺函模板")
    public ResponseEntity<Resource> previewTemplate(
            @ApiParam(value = "模板文件名", required = true) @PathVariable String templateName) throws IOException {

        if (templateName.contains("..") || templateName.contains("/") || templateName.contains("\\")) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "非法文件名");
        }

        File templateFile = new File(templatePath + templateName);
        if (!templateFile.exists()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "模板文件不存在");
        }

        return buildFileResponse(templateFile, false);
    }

    private ResponseEntity<Resource> buildFileResponse(File file, boolean download) throws IOException {
        Resource resource = new FileSystemResource(file);
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String encodedFileName = java.net.URLEncoder.encode(file.getName(), "UTF-8").replace("+", "%20");
        String disposition = download
                ? "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName
                : "inline; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .body(resource);
    }

    private ResponseEntity<Resource> buildClasspathFileResponse(ClassPathResource resource, String fileName, boolean download) throws IOException {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        try {
            contentType = Files.probeContentType(Paths.get(fileName));
        } catch (Exception ignored) {}
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        String disposition = download
                ? "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName
                : "inline; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .body(resource);
    }

    @PostMapping("/upload/industry-development")
    @ApiOperation(value = "上传并解析产业发展数据", notes = "上传产业发展数据Excel文件，解析数据并保存到enterprise_info表和evaluation_enterprise关联表")
    public R<IndustryDevelopmentParseResult> uploadAndParseIndustryDevelopment(
            @ApiParam(value = "产业发展数据Excel文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "评价记录ID", required = true) @RequestParam("evaluationId") Long evaluationId) {

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "只支持Excel文件格式（.xlsx或.xls）");
        }

        try {
            validateIndustryTemplateHeader(file);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("校验产业发展模板失败", e);
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件格式校验失败，请使用下载的模板");
        }

        try {
            IndustryDevelopmentParseResult result = industryDevelopmentParseService.parseAndSave(file, evaluationId);
            return R.ok(result);
        } catch (IOException e) {
            log.error("解析产业发展数据失败", e);
            throw new BusinessException(ResultCode.SERVER_ERROR, "文件解析失败，请检查文件格式");
        }
    }

    private void validateIndustryTemplateHeader(MultipartFile uploadFile) throws Exception {
        List<String> templateHeaders = INDUSTRY_TEMPLATE_HEADERS;
        List<String> uploadHeaders = readFirstRow(uploadFile.getInputStream());

        if (templateHeaders.isEmpty()) {
            return;
        }
        if (uploadHeaders.size() != templateHeaders.size()) {
            throw new BusinessException(ResultCode.PARAM_ERROR,
                    "表头不匹配，请使用下载的模板。模板共" + templateHeaders.size() + "列，上传文件共" + uploadHeaders.size() + "列");
        }
        for (int i = 0; i < templateHeaders.size(); i++) {
            String t = templateHeaders.get(i);
            String u = uploadHeaders.get(i);
            if (t == null ? u != null : !t.equals(u)) {
                throw new BusinessException(ResultCode.PARAM_ERROR,
                        "表头不匹配（第" + (i + 1) + "列），请使用下载的模板");
            }
        }
    }

    private List<String> readFirstRow(InputStream is) throws Exception {
        List<String> headers = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return headers;
            }
            Row row = sheet.getRow(0);
            if (row == null) {
                return headers;
            }
            int lastCol = row.getLastCellNum();
            for (int i = 0; i < lastCol; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    headers.add(null);
                    continue;
                }
                switch (cell.getCellType()) {
                    case STRING:
                        headers.add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                            headers.add(cell.getDateCellValue().toString());
                        } else {
                            double num = cell.getNumericCellValue();
                            if (num == Math.floor(num)) {
                                headers.add(String.valueOf((long) num));
                            } else {
                                headers.add(String.valueOf(num));
                            }
                        }
                        break;
                    case BOOLEAN:
                        headers.add(String.valueOf(cell.getBooleanCellValue()));
                        break;
                    case FORMULA:
                        headers.add(cell.getCellFormula());
                        break;
                    default:
                        headers.add(null);
                }
            }
        }
        return headers;
    }

    /**
     * 在上传目录和模板目录中查找文件
     */
    private File findFile(String storedName) {
        File uploadDir = new File(uploadPath);
        if (uploadDir.exists()) {
            File result = searchFile(uploadDir, storedName);
            if (result != null) {
                return result;
            }
        }
        
        File templateDir = new File(templatePath);
        if (templateDir.exists()) {
            return searchFile(templateDir, storedName);
        }
        
        return null;
    }

    private File searchFile(File dir, String storedName) {
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                File result = searchFile(f, storedName);
                if (result != null) {
                    return result;
                }
            } else if (f.getName().equals(storedName)) {
                return f;
            }
        }
        return null;
    }
}
