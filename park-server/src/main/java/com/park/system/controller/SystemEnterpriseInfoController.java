package com.park.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.mapper.EnterpriseMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import com.park.system.dto.SystemEnterpriseInfoQueryDTO;
import com.park.system.entity.DistrictInfo;
import com.park.system.mapper.DistrictMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统设置 - 企业信息控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/system/enterprise-info")
@Api(tags = "系统设置-企业信息")
public class SystemEnterpriseInfoController {

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private DistrictMapper districtMapper;

    /**
     * 分页查询企业信息列表
     */
    @GetMapping
    @ApiOperation(value = "分页查询企业信息", notes = "支持关键字、区域、园区、状态筛选")
    public R<PageResult<EnterpriseInfo>> getEnterpriseInfoPage(SystemEnterpriseInfoQueryDTO queryDTO) {
        Page<EnterpriseInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<EnterpriseInfo> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(EnterpriseInfo::getEnterpriseName, queryDTO.getKeyword())
                    .or()
                    .like(EnterpriseInfo::getCreditCode, queryDTO.getKeyword()));
        }
        if (queryDTO.getParkId() != null) {
            wrapper.eq(EnterpriseInfo::getParkId, queryDTO.getParkId());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(EnterpriseInfo::getStatus, queryDTO.getStatus());
        }
        // districtId 通过园区反查
        if (queryDTO.getDistrictId() != null) {
            List<Long> parkIds = parkMapper.selectList(
                    new LambdaQueryWrapper<ParkInfo>().eq(ParkInfo::getDistrictId, queryDTO.getDistrictId())
            ).stream().map(ParkInfo::getId).collect(Collectors.toList());
            if (parkIds.isEmpty()) {
                parkIds.add(-1L);
            }
            wrapper.in(EnterpriseInfo::getParkId, parkIds);
        }

        wrapper.orderByDesc(EnterpriseInfo::getCreateTime);
        IPage<EnterpriseInfo> result = enterpriseMapper.selectPage(page, wrapper);

        // 填充园区名称、区县名称
        fillParkInfo(result.getRecords());

        PageResult<EnterpriseInfo> pageResult = PageResult.of(
                result.getRecords(),
                result.getTotal(),
                (int) result.getCurrent(),
                (int) result.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 根据ID查询企业详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询企业详情", notes = "根据ID查询企业详细信息")
    public R<EnterpriseInfo> getEnterpriseInfoById(@PathVariable Long id) {
        EnterpriseInfo enterprise = enterpriseMapper.selectById(id);
        if (enterprise == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "企业不存在");
        }
        fillParkInfo(java.util.Collections.singletonList(enterprise));
        return R.ok(enterprise);
    }

    /**
     * 新增企业信息
     */
    @PostMapping
    @ApiOperation(value = "新增企业信息", notes = "新增企业信息")
    public R<Void> saveEnterpriseInfo(@Valid @RequestBody EnterpriseInfo enterpriseInfo) {
        enterpriseMapper.insert(enterpriseInfo);
        return R.ok("新增成功", null);
    }

    /**
     * 修改企业信息
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改企业信息", notes = "根据ID修改企业信息")
    public R<Void> updateEnterpriseInfo(@PathVariable Long id, @Valid @RequestBody EnterpriseInfo enterpriseInfo) {
        enterpriseInfo.setId(id);
        enterpriseMapper.updateById(enterpriseInfo);
        return R.ok("修改成功", null);
    }

    /**
     * 删除企业信息
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除企业信息", notes = "根据ID删除企业信息")
    public R<Void> deleteEnterpriseInfo(@PathVariable Long id) {
        enterpriseMapper.deleteById(id);
        return R.ok("删除成功", null);
    }

    private void fillParkInfo(List<EnterpriseInfo> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<Long> parkIds = records.stream()
                .map(EnterpriseInfo::getParkId)
                .distinct()
                .collect(Collectors.toList());
        if (parkIds.isEmpty()) {
            return;
        }
        Map<Long, ParkInfo> parkMap = parkMapper.selectBatchIds(parkIds).stream()
                .collect(Collectors.toMap(ParkInfo::getId, p -> p, (a, b) -> a));
        for (EnterpriseInfo e : records) {
            ParkInfo p = parkMap.get(e.getParkId());
            if (p != null) {
                e.setParkName(p.getParkName());
                e.setDistrictName(p.getDistrictName());
            }
        }
    }

    /**
     * 下载企业信息导入模板
     */
    @GetMapping("/template")
    @ApiOperation(value = "下载导入模板", notes = "下载企业信息导入Excel模板")
    public void downloadTemplate(HttpServletResponse response) {
        String fileName = "企业信息导入模板 (1).xlsx";
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
            log.error("下载企业信息导入模板失败", e);
            throw new BusinessException(ResultCode.FAILURE, "下载模板失败");
        }
    }

    /**
     * 批量导入企业信息
     */
    @PostMapping("/import")
    @ApiOperation(value = "批量导入企业信息", notes = "通过Excel批量导入企业信息")
    public R<Map<String, Object>> importEnterpriseInfo(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "上传文件不能为空");
        }
        int successCount = 0;
        int failCount = 0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 预加载园区映射：parkName -> parkId, districtId -> parkId（取每个区县首个园区）
        List<ParkInfo> allParks = parkMapper.selectList(null);
        Map<String, Long> parkNameToId = new HashMap<>();
        Map<Long, Long> districtIdToParkId = new HashMap<>();
        for (ParkInfo p : allParks) {
            if (p.getParkName() != null && !parkNameToId.containsKey(p.getParkName())) {
                parkNameToId.put(p.getParkName(), p.getId());
            }
            if (p.getDistrictId() != null && !districtIdToParkId.containsKey(p.getDistrictId())) {
                districtIdToParkId.put(p.getDistrictId(), p.getId());
            }
        }
        // 预加载区县映射：districtName -> districtId
        List<DistrictInfo> allDistricts = districtMapper.selectList(null);
        Map<String, Long> districtNameToId = new HashMap<>();
        for (DistrictInfo d : allDistricts) {
            if (d.getDistrictName() != null && !districtNameToId.containsKey(d.getDistrictName())) {
                districtNameToId.put(d.getDistrictName(), d.getId());
            }
        }

        try (InputStream is = file.getInputStream();
             Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                try {
                    String enterpriseName = getCellString(row, 0);
                    // 空行跳过（企业名称为空）
                    if (!StringUtils.hasText(enterpriseName)) {
                        continue;
                    }
                    EnterpriseInfo info = new EnterpriseInfo();
                    info.setEnterpriseName(enterpriseName);
                    info.setCreditCode(getCellString(row, 1));

                    // 所属园区 -> parkId（先按园区名查，找不到再按区县名查）
                    String parkName = getCellString(row, 2);
                    String districtName = getCellString(row, 3);
                    Long parkId = null;
                    if (StringUtils.hasText(parkName)) {
                        parkId = parkNameToId.get(parkName.trim());
                    }
                    if (parkId == null && StringUtils.hasText(districtName)) {
                        Long districtId = districtNameToId.get(districtName.trim());
                        if (districtId != null) {
                            parkId = districtIdToParkId.get(districtId);
                        }
                    }
                    info.setParkId(parkId);

                    info.setIndustryName(getCellString(row, 5));
                    info.setStatus(getCellString(row, 6));
                    info.setLegalPerson(getCellString(row, 9));
                    info.setContactName(getCellString(row, 10));
                    info.setContactPhone(getCellString(row, 11));

                    // 注册资本（万元）
                    String capitalStr = getCellString(row, 12);
                    if (StringUtils.hasText(capitalStr)) {
                        try {
                            info.setRegisteredCapital(new BigDecimal(capitalStr.trim()));
                        } catch (Exception ex) {
                            // 解析失败忽略
                        }
                    }
                    // 注册日期 yyyy-MM-dd
                    String dateStr = getCellString(row, 13);
                    if (StringUtils.hasText(dateStr)) {
                        try {
                            info.setRegisterDate(LocalDate.parse(dateStr.trim(), dateFormatter));
                        } catch (Exception ex) {
                            // 解析失败忽略
                        }
                    }

                    enterpriseMapper.insert(info);
                    successCount++;
                } catch (Exception e) {
                    log.warn("导入企业信息第{}行失败: {}", i + 1, e.getMessage());
                    failCount++;
                }
            }
        } catch (Exception e) {
            log.error("解析Excel文件失败", e);
            throw new BusinessException(ResultCode.FAILURE, "解析Excel文件失败");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return R.ok("导入完成", result);
    }

    /**
     * 导出企业信息列表
     */
    @GetMapping("/export")
    @ApiOperation(value = "导出企业信息", notes = "导出企业信息列表Excel")
    public void exportEnterpriseInfo(SystemEnterpriseInfoQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        // 构建查询条件（与列表查询一致）
        LambdaQueryWrapper<EnterpriseInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(EnterpriseInfo::getEnterpriseName, queryDTO.getKeyword())
                    .or()
                    .like(EnterpriseInfo::getCreditCode, queryDTO.getKeyword()));
        }
        if (queryDTO.getParkId() != null) {
            wrapper.eq(EnterpriseInfo::getParkId, queryDTO.getParkId());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(EnterpriseInfo::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getDistrictId() != null) {
            List<Long> parkIds = parkMapper.selectList(
                    new LambdaQueryWrapper<ParkInfo>().eq(ParkInfo::getDistrictId, queryDTO.getDistrictId())
            ).stream().map(ParkInfo::getId).collect(Collectors.toList());
            if (parkIds.isEmpty()) {
                parkIds.add(-1L);
            }
            wrapper.in(EnterpriseInfo::getParkId, parkIds);
        }
        wrapper.orderByDesc(EnterpriseInfo::getCreateTime);

        List<EnterpriseInfo> list = enterpriseMapper.selectList(wrapper);
        fillParkInfo(list);

        String[] headers = {"企业名称", "统一社会信用代码", "所属园区", "所属区域", "所属行业门类",
                "企业状态", "法定代表人", "联系人", "联系人手机", "注册资本（万元）", "注册日期"};
        byte[] data;
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("企业信息");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            for (int i = 0; i < list.size(); i++) {
                EnterpriseInfo e = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(nullToEmpty(e.getEnterpriseName()));
                row.createCell(1).setCellValue(nullToEmpty(e.getCreditCode()));
                row.createCell(2).setCellValue(nullToEmpty(e.getParkName()));
                row.createCell(3).setCellValue(nullToEmpty(e.getDistrictName()));
                row.createCell(4).setCellValue(nullToEmpty(e.getIndustryName()));
                row.createCell(5).setCellValue(nullToEmpty(e.getStatus()));
                row.createCell(6).setCellValue(nullToEmpty(e.getLegalPerson()));
                row.createCell(7).setCellValue(nullToEmpty(e.getContactName()));
                row.createCell(8).setCellValue(nullToEmpty(e.getContactPhone()));
                if (e.getRegisteredCapital() != null) {
                    row.createCell(9).setCellValue(e.getRegisteredCapital().doubleValue());
                } else {
                    row.createCell(9).setCellValue("");
                }
                row.createCell(10).setCellValue(e.getRegisterDate() != null ? e.getRegisterDate().toString() : "");
            }
            wb.write(baos);
            data = baos.toByteArray();
        } catch (Exception e) {
            log.error("导出企业信息失败", e);
            throw new BusinessException(ResultCode.FAILURE, "导出企业信息失败");
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("企业信息列表.xlsx", "UTF-8"));
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    /**
     * 读取单元格字符串值
     */
    private String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                }
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d) && !Double.isInfinite(d)) {
                    return String.valueOf((long) d);
                }
                return String.valueOf(d);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    /**
     * null 转空字符串
     */
    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
