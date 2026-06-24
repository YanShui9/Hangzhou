package com.park.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.system.dto.DataWarehouseQueryDTO;
import com.park.system.entity.DataWarehouse;
import com.park.system.service.DataWarehouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 数据仓库控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/data-warehouse")
@Api(tags = "数据仓库")
public class DataWarehouseController {

    @Autowired
    private DataWarehouseService dataWarehouseService;

    /**
     * 分页查询数据仓库列表
     */
    @GetMapping
    @ApiOperation(value = "分页查询数据仓库列表", notes = "支持按名称、文件类型、年度筛选")
    public R<PageResult<DataWarehouse>> getDataWarehousePage(DataWarehouseQueryDTO queryDTO) {
        IPage<DataWarehouse> page = dataWarehouseService.getDataWarehousePage(queryDTO);
        PageResult<DataWarehouse> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 根据ID查询数据仓库详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询数据仓库详情", notes = "根据ID查询数据仓库详细信息")
    public R<DataWarehouse> getDataWarehouseById(@PathVariable Long id) {
        return R.ok(dataWarehouseService.getDataWarehouseById(id));
    }

    /**
     * 文件类型 -> 中文名映射
     */
    private static final Map<String, String> FILE_TYPE_NAME_MAP = new HashMap<>();
    static {
        FILE_TYPE_NAME_MAP.put("honor_new", "全市企业荣誉新增汇总");
        FILE_TYPE_NAME_MAP.put("honor_total", "全市企业荣誉累计汇总");
        FILE_TYPE_NAME_MAP.put("unreported_park", "未上报运营园区名单");
        FILE_TYPE_NAME_MAP.put("park_total_tax", "园区总营税收");
        FILE_TYPE_NAME_MAP.put("leading_industry_tax", "主导产业企业的园区营税收");
        FILE_TYPE_NAME_MAP.put("enterprise_type_tax", "企业类型的园区营税收");
        FILE_TYPE_NAME_MAP.put("park_star_summary", "园区星级汇总");
    }

    /**
     * 上传数据仓库文件（multipart/form-data）
     * 保存文件 -> 记录data_warehouse -> 解析写入业务表
     */
    @PostMapping
    @ApiOperation(value = "上传数据仓库文件", notes = "上传Excel文件并解析导入对应业务表")
    public R<Map<String, Object>> uploadDataWarehouse(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType,
            @RequestParam("year") Integer year) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件不能为空");
        }
        if (!StringUtils.hasText(fileType)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件类型不能为空");
        }
        if (year == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "年度不能为空");
        }

        // 1. 保存文件到本地目录
        String originalFilename = file.getOriginalFilename();
        String savedFileName = fileType + "_" + year + "_" + System.currentTimeMillis() + "_" + originalFilename;
        String dirPath = "uploads/data-warehouse/";
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建上传目录失败: {}", dir.getAbsolutePath());
            throw new BusinessException(ResultCode.UPLOAD_ERROR, "创建上传目录失败");
        }
        File destFile = new File(dirPath + savedFileName);
        try {
            Files.copy(file.getInputStream(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("保存文件失败", e);
            throw new BusinessException(ResultCode.UPLOAD_ERROR, "保存文件失败");
        }

        // 校验Excel表头是否与模板一致
        try {
            validateTemplateHeader(fileType, destFile);
        } catch (BusinessException e) {
            destFile.delete();
            throw e;
        } catch (Exception e) {
            destFile.delete();
            log.error("校验模板失败", e);
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件格式校验失败，请使用下载的模板");
        }

        // 2. 记录到 data_warehouse 表
        String fileTypeName = FILE_TYPE_NAME_MAP.getOrDefault(fileType, fileType);
        DataWarehouse dataWarehouse = new DataWarehouse();
        dataWarehouse.setName(fileTypeName + year + "年度");
        dataWarehouse.setFileType(fileType);
        dataWarehouse.setYear(year);
        dataWarehouse.setFileName(savedFileName);
        dataWarehouse.setFilePath("uploads/data-warehouse/" + savedFileName);
        dataWarehouse.setFileSize(String.valueOf(file.getSize()));
        dataWarehouseService.saveDataWarehouse(dataWarehouse);

        // 3. 解析Excel并写入业务表
        Map<String, Object> parseResult = dataWarehouseService.parseAndImportData(file, fileType, year, savedFileName);
        return R.ok(parseResult);
    }

    /**
     * 文件预览（返回文件流）
     */
    @GetMapping("/{id}/preview")
    @ApiOperation(value = "预览文件", notes = "根据ID预览数据仓库Excel文件")
    public void previewFile(@PathVariable Long id, HttpServletResponse response) {
        DataWarehouse dataWarehouse = dataWarehouseService.getDataWarehouseById(id);
        String filePath = dataWarehouse.getFilePath();
        if (!StringUtils.hasText(filePath)) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND, "文件路径不存在");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND, "文件不存在");
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            log.error("预览文件失败: id={}", id, e);
            throw new BusinessException(ResultCode.FAILURE, "预览文件失败");
        }
    }

    /**
     * 修改数据仓库记录
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改数据仓库记录", notes = "根据ID修改数据仓库记录")
    public R<Void> updateDataWarehouse(@PathVariable Long id, @Valid @RequestBody DataWarehouse dataWarehouse) {
        dataWarehouse.setId(id);
        dataWarehouseService.updateDataWarehouse(dataWarehouse);
        return R.ok("修改成功", null);
    }

    /**
     * 删除数据仓库记录
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除数据仓库记录", notes = "根据ID删除数据仓库记录")
    public R<Void> deleteDataWarehouse(@PathVariable Long id) {
        dataWarehouseService.deleteDataWarehouse(id);
        return R.ok("删除成功", null);
    }

    /**
     * 模板key -> 物理文件名映射（共7个模板）
     */
    private static final Map<String, String> TEMPLATE_FILE_MAP = new HashMap<>();
    static {
        TEMPLATE_FILE_MAP.put("honor_new", "全市企业荣誉新增汇总模版.xlsx");
        TEMPLATE_FILE_MAP.put("honor_total", "全市企业荣誉累计汇总模版.xlsx");
        TEMPLATE_FILE_MAP.put("unreported_park", "未上报运营园区名单模版.xlsx");
        TEMPLATE_FILE_MAP.put("park_total_tax", "园区总营税收模版.xlsx");
        TEMPLATE_FILE_MAP.put("leading_industry_tax", "主导产业企业的园区营税收模版.xlsx");
        TEMPLATE_FILE_MAP.put("enterprise_type_tax", "企业类型的园区营税收模版.xlsx");
        TEMPLATE_FILE_MAP.put("park_star_summary", "园区星级汇总模版.xlsx");
    }

    /**
     * 下载指定模板（返回物理Excel文件）
     *
     * 模板与评价结果、park_evaluation 数据库的关系：
     * - honor_new/honor_total: 企业荣誉数据，导入后写入 enterprise_honor_record 表，
     *   自动同步 enterprise_info 并更新 park_info 统计字段，作为"评价结果-企业指标"的数据来源，
     *   并参与 park_evaluation_score 企业培育/科技创新维度自动计分。
     * - park_total_tax/leading_industry_tax/enterprise_type_tax: 园区税收数据，导入后写入 park_tax_record 表，
     *   作为 park_evaluation_score 中 revenue_per_mu(亩均营收)/tax_per_mu(亩均税收) 的数据来源（效益产出维度）。
     *   注意：审核通过后这两个字段不被新导入数据覆盖。
     * - unreported_park: 未上报园区名单，导入后写入 unreported_park_record 表。
     * - park_star_summary: 园区星级汇总，导入后同步到 park_info.star_level 字段。
     */
    @GetMapping("/template/{templateKey}")
    @ApiOperation(value = "下载模板", notes = "根据模板key下载对应的Excel模板文件")
    public void downloadTemplate(@PathVariable String templateKey, HttpServletResponse response) {
        String fileName = TEMPLATE_FILE_MAP.get(templateKey);
        if (fileName == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "模板类型不存在: " + templateKey);
        }
        ClassPathResource resource = new ClassPathResource("templates/" + fileName);
        if (!resource.exists()) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "模板文件不存在: " + fileName);
        }
        try (InputStream is = resource.getInputStream();
             OutputStream os = response.getOutputStream()) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            log.error("下载模板失败: templateKey={}, fileName={}", templateKey, fileName, e);
            throw new BusinessException(ResultCode.FAILURE, "下载模板失败");
        }
    }

    /**
     * 校验上传的Excel表头是否与模板一致
     * 对比第一行所有单元格的值，顺序和内容必须完全匹配
     */
    private void validateTemplateHeader(String fileType, File uploadFile) throws Exception {
        String templateName = TEMPLATE_FILE_MAP.get(fileType);
        if (templateName == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件类型不存在: " + fileType);
        }
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        if (!resource.exists()) {
            return;
        }

        List<String> templateHeaders = readFirstRow(resource.getInputStream());
        List<String> uploadHeaders = readFirstRow(new FileInputStream(uploadFile));

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

    /**
     * 读取Excel第一个sheet的第一行，返回单元格值列表（按顺序）
     */
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
                        if (DateUtil.isCellDateFormatted(cell)) {
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
}
