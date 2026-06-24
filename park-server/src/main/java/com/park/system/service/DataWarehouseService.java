package com.park.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.enterprise.entity.EnterpriseHonorRecord;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.mapper.EnterpriseHonorRecordMapper;
import com.park.enterprise.mapper.EnterpriseMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import com.park.system.dto.DataWarehouseQueryDTO;
import com.park.system.entity.DataWarehouse;
import com.park.system.entity.ParkTaxRecord;
import com.park.system.entity.UnreportedParkRecord;
import com.park.system.mapper.DataWarehouseMapper;
import com.park.system.mapper.ParkTaxRecordMapper;
import com.park.system.mapper.UnreportedParkRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据仓库服务
 *
 * @author park-team
 */
@Slf4j
@Service
public class DataWarehouseService {

    @Autowired
    private DataWarehouseMapper dataWarehouseMapper;

    @Autowired
    private EnterpriseHonorRecordMapper enterpriseHonorRecordMapper;

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private UnreportedParkRecordMapper unreportedParkRecordMapper;

    @Autowired
    private ParkTaxRecordMapper parkTaxRecordMapper;

    /**
     * 分页查询数据仓库列表
     */
    public IPage<DataWarehouse> getDataWarehousePage(DataWarehouseQueryDTO queryDTO) {
        Page<DataWarehouse> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<DataWarehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataWarehouse::getDeleted, 0);
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(DataWarehouse::getName, queryDTO.getName());
        }
        if (StringUtils.hasText(queryDTO.getFileType())) {
            wrapper.eq(DataWarehouse::getFileType, queryDTO.getFileType());
        }
        if (queryDTO.getYear() != null) {
            wrapper.eq(DataWarehouse::getYear, queryDTO.getYear());
        }
        wrapper.orderByDesc(DataWarehouse::getCreateTime);
        return dataWarehouseMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询详情
     */
    public DataWarehouse getDataWarehouseById(Long id) {
        DataWarehouse record = dataWarehouseMapper.selectById(id);
        if (record == null || record.getDeleted() != null && record.getDeleted() == 1) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "数据不存在");
        }
        return record;
    }

    /**
     * 新增数据仓库记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveDataWarehouse(DataWarehouse dataWarehouse) {
        dataWarehouse.setDeleted(0);
        dataWarehouseMapper.insert(dataWarehouse);
    }

    /**
     * 修改数据仓库记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDataWarehouse(DataWarehouse dataWarehouse) {
        if (dataWarehouse.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "ID不能为空");
        }
        DataWarehouse existing = getDataWarehouseById(dataWarehouse.getId());
        BeanUtils.copyProperties(dataWarehouse, existing, "id", "createTime", "createBy", "deleted");
        dataWarehouseMapper.updateById(existing);
    }

    /**
     * 删除数据仓库记录（同时删除物理文件）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataWarehouse(Long id) {
        DataWarehouse record = getDataWarehouseById(id);
        // 删除物理文件
        if (StringUtils.hasText(record.getFilePath())) {
            try {
                java.io.File file = new java.io.File(record.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                log.warn("删除物理文件失败: {}", record.getFilePath(), e);
            }
        }
        record.setDeleted(1);
        dataWarehouseMapper.updateById(record);
    }

    // ==================== 文件解析与导入 ====================

    /** honor_new 列定义：{列索引, 荣誉类型, 荣誉大类} */
    private static final String[][] HONOR_NEW_DEFS = {
            {"2", "existing_above_scale", "enterprise_cultivate"},
            {"3", "new_above_scale", "enterprise_cultivate"},
            {"4", "retired_above_scale", "enterprise_cultivate"},
            {"5", "new_single_champion", "enterprise_cultivate"},
            {"6", "new_ipo", "enterprise_cultivate"},
            {"7", "new_specialty_giant", "enterprise_cultivate"},
            {"8", "new_provincial_hidden_champion", "enterprise_cultivate"},
            {"9", "new_specialty_sme", "enterprise_cultivate"},
            {"10", "new_national_high_tech", "enterprise_cultivate"},
            {"11", "innovative_sme", "enterprise_cultivate"},
            {"12", "new_provincial_tech_small", "enterprise_cultivate"},
            {"13", "new_first_equipment", "tech_innovation"},
            {"14", "first_version", "tech_innovation"},
            {"15", "first_batch", "tech_innovation"},
            {"16", "provincial_excellent_industrial", "tech_innovation"},
            {"17", "zhejiang_made_quality", "tech_innovation"},
            {"18", "new_national_rd_agency", "tech_innovation"},
            {"19", "new_provincial_rd_agency", "tech_innovation"},
            {"20", "new_municipal_rd_agency", "tech_innovation"},
            {"21", "public_service_platform", "tech_innovation"}
    };

    /** honor_total 列定义：{列索引, 荣誉类型, 荣誉大类} */
    private static final String[][] HONOR_TOTAL_DEFS = {
            {"2", "existing_above_scale", "enterprise_cultivate"},
            {"3", "retired_above_scale", "enterprise_cultivate"},
            {"4", "new_single_champion", "enterprise_cultivate"},
            {"5", "new_ipo", "enterprise_cultivate"},
            {"6", "new_specialty_giant", "enterprise_cultivate"},
            {"7", "new_provincial_hidden_champion", "enterprise_cultivate"},
            {"8", "new_specialty_sme", "enterprise_cultivate"},
            {"9", "new_national_high_tech", "enterprise_cultivate"},
            {"10", "innovative_sme", "enterprise_cultivate"},
            {"11", "new_provincial_tech_small", "enterprise_cultivate"}
    };

    /**
     * 解析Excel并导入业务表
     *
     * @param file          上传的文件（transferTo后输入流可能不可用，实际从已保存文件读取）
     * @param fileType      文件类型
     * @param year          年度
     * @param savedFileName 已保存的文件名
     * @return 含 successCount 和 message
     */
    public Map<String, Object> parseAndImportData(MultipartFile file, String fileType, Integer year, String savedFileName) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        // 从已保存的文件读取Excel（transferTo后MultipartFile输入流可能不可用）
        String filePath = "uploads/data-warehouse/" + savedFileName;
        try (InputStream is = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            switch (fileType) {
                case "honor_new":
                    deleteOldHonorRecords("honor_new", year);
                    successCount = parseHonor(sheet, year, savedFileName, HONOR_NEW_DEFS);
                    break;
                case "honor_total":
                    deleteOldHonorRecords("honor_total", year);
                    successCount = parseHonor(sheet, year, savedFileName, HONOR_TOTAL_DEFS);
                    break;
                case "unreported_park":
                    deleteOldUnreportedParkRecords(year);
                    successCount = parseUnreportedPark(sheet, year, savedFileName);
                    break;
                case "park_total_tax":
                    deleteOldParkTaxRecords("park_total", year);
                    successCount = parseParkTax(sheet, year, savedFileName, "park_total");
                    break;
                case "leading_industry_tax":
                    deleteOldParkTaxRecords("leading_industry", year);
                    successCount = parseParkTax(sheet, year, savedFileName, "leading_industry");
                    break;
                case "enterprise_type_tax":
                    deleteOldParkTaxRecords("enterprise_type", year);
                    successCount = parseParkTax(sheet, year, savedFileName, "enterprise_type");
                    break;
                case "park_star_summary":
                    successCount = parseParkStar(sheet);
                    break;
                default:
                    throw new BusinessException(ResultCode.PARAM_ERROR, "不支持的文件类型: " + fileType);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析Excel失败: fileType={}", fileType, e);
            throw new BusinessException(ResultCode.FAILURE, "解析Excel失败: " + e.getMessage());
        }
        result.put("successCount", successCount);
        result.put("message", "导入成功，共" + successCount + "条");
        return result;
    }

    /**
     * 解析企业荣誉（honor_new / honor_total）
     * 列0=企业名称, 列1=企业代码, 其余列为荣誉列
     * 值为"是"则插入1条记录，"否"则不插入
     */
    private int parseHonor(Sheet sheet, Integer year, String sourceFile, String[][] defs) {
        int count = 0;
        // 企业名称 -> parkId 缓存，避免重复查询
        Map<String, Long> parkIdCache = new HashMap<>();
        // 跳过表头行，从第1行开始
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            try {
                String enterpriseName = getCellString(row, 0);
                if (enterpriseName == null || enterpriseName.trim().isEmpty()) {
                    continue; // 空行跳过
                }
                String creditCode = getCellString(row, 1);
                // 通过企业名称关联enterprise_info查park_id
                Long parkId = parkIdCache.computeIfAbsent(enterpriseName.trim(), this::lookupParkIdByName);
                // 遍历荣誉列
                for (String[] def : defs) {
                    int colIdx = Integer.parseInt(def[0]);
                    String val = getCellString(row, colIdx);
                    if (val != null && val.trim().equals("是")) {
                        EnterpriseHonorRecord record = new EnterpriseHonorRecord();
                        record.setEnterpriseName(enterpriseName.trim());
                        record.setCreditCode(creditCode != null ? creditCode.trim() : null);
                        record.setParkId(parkId);
                        record.setYear(year);
                        record.setHonorCategory(def[2]);
                        record.setHonorType(def[1]);
                        record.setHonorCount(1);
                        record.setSourceFile(sourceFile);
                        enterpriseHonorRecordMapper.insert(record);
                        count++;
                    }
                }
            } catch (Exception e) {
                log.warn("解析荣誉行失败: row={}", i, e);
            }
        }
        return count;
    }

    /**
     * 通过企业名称查询所属园区ID
     */
    private Long lookupParkIdByName(String enterpriseName) {
        LambdaQueryWrapper<EnterpriseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnterpriseInfo::getEnterpriseName, enterpriseName)
                .select(EnterpriseInfo::getParkId)
                .last("LIMIT 1");
        EnterpriseInfo info = enterpriseMapper.selectOne(wrapper);
        return info != null ? info.getParkId() : null;
    }

    /**
     * 解析未上报运营园区名单
     * 列：园区名称、园区代码、园区类型、所属区域、未上报季度
     */
    private int parseUnreportedPark(Sheet sheet, Integer year, String sourceFile) {
        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            try {
                String parkName = getCellString(row, 0);
                if (parkName == null || parkName.trim().isEmpty()) {
                    continue;
                }
                UnreportedParkRecord record = new UnreportedParkRecord();
                record.setParkName(parkName.trim());
                record.setParkCode(getCellString(row, 1));
                record.setParkType(getCellString(row, 2));
                record.setDistrictName(getCellString(row, 3));
                record.setUnreportedQuarter(getCellString(row, 4));
                record.setYear(year);
                record.setSourceFile(sourceFile);
                unreportedParkRecordMapper.insert(record);
                count++;
            } catch (Exception e) {
                log.warn("解析未上报园区行失败: row={}", i, e);
            }
        }
        return count;
    }

    /**
     * 解析园区税收（park_total_tax / leading_industry_tax / enterprise_type_tax）
     * 列：园区名称、园区代码、营业收入、净入库税款
     */
    private int parseParkTax(Sheet sheet, Integer year, String sourceFile, String taxType) {
        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            try {
                String parkName = getCellString(row, 0);
                if (parkName == null || parkName.trim().isEmpty()) {
                    continue;
                }
                ParkTaxRecord record = new ParkTaxRecord();
                record.setParkName(parkName.trim());
                record.setParkCode(getCellString(row, 1));
                record.setRevenue(getCellDecimal(row, 2));
                record.setTax(getCellDecimal(row, 3));
                record.setTaxType(taxType);
                record.setYear(year);
                record.setSourceFile(sourceFile);
                parkTaxRecordMapper.insert(record);
                count++;
            } catch (Exception e) {
                log.warn("解析园区税收行失败: row={}", i, e);
            }
        }
        return count;
    }

    /**
     * 解析园区星级汇总，更新 park_info.star_level
     * 列：园区名称、园区代码、园区星级
     * 先按park_code匹配，再按park_name匹配
     */
    private int parseParkStar(Sheet sheet) {
        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            try {
                String parkName = getCellString(row, 0);
                if (parkName == null || parkName.trim().isEmpty()) {
                    continue;
                }
                String parkCode = getCellString(row, 1);
                Integer starLevel = getCellInteger(row, 2);
                if (starLevel == null) {
                    continue;
                }
                // 先按park_code匹配
                ParkInfo park = null;
                if (parkCode != null && !parkCode.trim().isEmpty()) {
                    LambdaQueryWrapper<ParkInfo> codeWrapper = new LambdaQueryWrapper<>();
                    codeWrapper.eq(ParkInfo::getParkCode, parkCode.trim())
                            .last("LIMIT 1");
                    park = parkMapper.selectOne(codeWrapper);
                }
                // 再按park_name匹配
                if (park == null) {
                    LambdaQueryWrapper<ParkInfo> nameWrapper = new LambdaQueryWrapper<>();
                    nameWrapper.eq(ParkInfo::getParkName, parkName.trim())
                            .last("LIMIT 1");
                    park = parkMapper.selectOne(nameWrapper);
                }
                if (park != null) {
                    park.setStarLevel(starLevel);
                    parkMapper.updateById(park);
                    count++;
                } else {
                    log.warn("园区星级更新：未找到匹配园区: parkName={}, parkCode={}", parkName, parkCode);
                }
            } catch (Exception e) {
                log.warn("解析园区星级行失败: row={}", i, e);
            }
        }
        return count;
    }

    /**
     * 获取单元格字符串值
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
                    return cell.getDateCellValue().toString();
                }
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 获取单元格BigDecimal值
     */
    private BigDecimal getCellDecimal(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        String val = getCellString(row, col);
        if (val == null || val.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取单元格Integer值
     */
    private Integer getCellInteger(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        String val = getCellString(row, col);
        if (val == null || val.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ==================== 先删后插：防重复导入 ====================

    /**
     * 删除企业荣誉旧数据（按 fileType 前缀 + year 精确匹配）
     * honor_new 和 honor_total 的 honorCategory 相同，用 sourceFile 前缀区分
     */
    private void deleteOldHonorRecords(String fileTypePrefix, Integer year) {
        LambdaQueryWrapper<EnterpriseHonorRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnterpriseHonorRecord::getYear, year)
               .likeRight(EnterpriseHonorRecord::getSourceFile, fileTypePrefix + "_");
        enterpriseHonorRecordMapper.delete(wrapper);
    }

    /**
     * 删除未上报园区旧数据（按 year）
     */
    private void deleteOldUnreportedParkRecords(Integer year) {
        LambdaQueryWrapper<UnreportedParkRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UnreportedParkRecord::getYear, year);
        unreportedParkRecordMapper.delete(wrapper);
    }

    /**
     * 删除园区税收旧数据（按 taxType + year）
     */
    private void deleteOldParkTaxRecords(String taxType, Integer year) {
        LambdaQueryWrapper<ParkTaxRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkTaxRecord::getTaxType, taxType)
               .eq(ParkTaxRecord::getYear, year);
        parkTaxRecordMapper.delete(wrapper);
    }
}
