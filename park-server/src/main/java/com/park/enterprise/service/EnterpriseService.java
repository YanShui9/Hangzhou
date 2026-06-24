package com.park.enterprise.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.enterprise.dto.EnterpriseHonorSummaryQueryDTO;
import com.park.enterprise.dto.EnterpriseQueryDTO;
import com.park.enterprise.dto.EnterpriseSaveDTO;
import com.park.enterprise.entity.EnterpriseHonorRecord;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.mapper.EnterpriseHonorRecordMapper;
import com.park.enterprise.mapper.EnterpriseMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 入驻企业服务
 *
 * @author park-team
 */
@Slf4j
@Service
public class EnterpriseService {

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private EnterpriseHonorRecordMapper enterpriseHonorRecordMapper;

    @Autowired
    private ParkMapper parkMapper;

    /**
     * 企业荣誉汇总表表头（与前端列对应）
     */
    private static final String[] HONOR_SUMMARY_HEADERS = {
            "序号", "园区名称", "所属区域", "园区类型", "参评企业总数",
            "存量规上", "新增规上", "退规", "新增专精特新小巨人", "新增省级隐形冠军",
            "新增专精中小企业", "新增单项冠军", "新增上市", "新增国高", "创新型中小企业",
            "新增省科小", "投早投小创新", "新增首台（套）装备", "首次次", "首批次",
            "省级优秀工业新品", "浙江制造精品", "新增国家级研发机构", "新增省级研发机构", "新增市级研发机构",
            "务平台（科研创新/检验检测等公共服）", "企业孵化检验检测等公共服",
            "A类人才", "B类人才", "C类人才"
    };

    /**
     * honor_type → 前端字段名 映射（顺序与表头一致）
     */
    private static final LinkedHashMap<String, String> HONOR_TYPE_FIELD_MAP = new LinkedHashMap<>();
    static {
        HONOR_TYPE_FIELD_MAP.put("existing_above_scale", "existingAboveScale");
        HONOR_TYPE_FIELD_MAP.put("new_above_scale", "newAboveScale");
        HONOR_TYPE_FIELD_MAP.put("retired_above_scale", "retiredAboveScale");
        HONOR_TYPE_FIELD_MAP.put("new_specialty_giant", "newSpecialtyGiant");
        HONOR_TYPE_FIELD_MAP.put("new_provincial_hidden_champion", "newProvincialHiddenChampion");
        HONOR_TYPE_FIELD_MAP.put("new_specialty_sme", "newSpecialtySME");
        HONOR_TYPE_FIELD_MAP.put("new_single_champion", "newSingleChampion");
        HONOR_TYPE_FIELD_MAP.put("new_ipo", "newIPO");
        HONOR_TYPE_FIELD_MAP.put("new_national_high_tech", "newNationalHighTech");
        HONOR_TYPE_FIELD_MAP.put("innovative_sme", "innovativeSME");
        HONOR_TYPE_FIELD_MAP.put("new_provincial_tech_small", "newProvincialTechSmall");
        HONOR_TYPE_FIELD_MAP.put("early_invest_innovation", "earlyInvestInnovation");
        HONOR_TYPE_FIELD_MAP.put("new_first_equipment", "newFirstEquipment");
        HONOR_TYPE_FIELD_MAP.put("first_version", "firstVersion");
        HONOR_TYPE_FIELD_MAP.put("first_batch", "firstBatch");
        HONOR_TYPE_FIELD_MAP.put("provincial_excellent_industrial", "provincialExcellentIndustrial");
        HONOR_TYPE_FIELD_MAP.put("zhejiang_made_quality", "zhejiangMadeQuality");
        HONOR_TYPE_FIELD_MAP.put("new_national_rd_agency", "newNationalRDAgency");
        HONOR_TYPE_FIELD_MAP.put("new_provincial_rd_agency", "newProvincialRDAgency");
        HONOR_TYPE_FIELD_MAP.put("new_municipal_rd_agency", "newMunicipalRDAgency");
        HONOR_TYPE_FIELD_MAP.put("public_service_platform", "publicServicePlatform");
        HONOR_TYPE_FIELD_MAP.put("enterprise_incubator", "enterpriseIncubator");
        HONOR_TYPE_FIELD_MAP.put("talent_a_class", "talentAClass");
        HONOR_TYPE_FIELD_MAP.put("talent_b_class", "talentBClass");
        HONOR_TYPE_FIELD_MAP.put("talent_c_class", "talentCClass");
    }

    /**
     * 分页查询企业列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<EnterpriseInfo> getEnterprisePage(EnterpriseQueryDTO queryDTO) {
        // 构建分页对象
        Page<EnterpriseInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<EnterpriseInfo> queryWrapper = new LambdaQueryWrapper<>();

        // 企业名称模糊查询
        if (StringUtils.hasText(queryDTO.getEnterpriseName())) {
            queryWrapper.like(EnterpriseInfo::getEnterpriseName, queryDTO.getEnterpriseName());
        }

        // 园区筛选
        if (queryDTO.getParkId() != null) {
            queryWrapper.eq(EnterpriseInfo::getParkId, queryDTO.getParkId());
        }
        // 支持多园区查询（区县管理员使用）
        if (queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty()) {
            queryWrapper.in(EnterpriseInfo::getParkId, queryDTO.getParkIds());
        }

        // 行业筛选
        if (StringUtils.hasText(queryDTO.getIndustryName())) {
            queryWrapper.eq(EnterpriseInfo::getIndustryName, queryDTO.getIndustryName());
        }

        // 状态筛选
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(EnterpriseInfo::getStatus, queryDTO.getStatus());
        }

        // 参评筛选
        if (queryDTO.getIsParticipate() != null) {
            queryWrapper.eq(EnterpriseInfo::getIsParticipate, queryDTO.getIsParticipate());
        }

        // 按创建时间降序排序
        queryWrapper.orderByDesc(EnterpriseInfo::getCreateTime);

        return enterpriseMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID查询企业详情
     *
     * @param id 企业ID
     * @return 企业信息
     */
    public EnterpriseInfo getEnterpriseById(Long id) {
        EnterpriseInfo enterprise = enterpriseMapper.selectById(id);
        if (enterprise == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "企业不存在");
        }
        return enterprise;
    }

    /**
     * 新增企业
     *
     * @param saveDTO 企业信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEnterprise(EnterpriseSaveDTO saveDTO) {
        // 检查企业名称是否已存在
        checkEnterpriseNameExists(saveDTO.getEnterpriseName(), saveDTO.getParkId(), null);

        // 转换并保存
        EnterpriseInfo enterprise = new EnterpriseInfo();
        BeanUtils.copyProperties(saveDTO, enterprise);
        enterprise.setIsParticipate(enterprise.getIsParticipate() != null ? enterprise.getIsParticipate() : 1);
        enterprise.setStatus(enterprise.getStatus() != null ? enterprise.getStatus() : "在营");

        int rows = enterpriseMapper.insert(enterprise);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_SAVE_ERROR);
        }
        log.info("新增企业成功：{}", enterprise.getEnterpriseName());
    }

    /**
     * 修改企业
     *
     * @param saveDTO 企业信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEnterprise(EnterpriseSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "企业ID不能为空");
        }

        // 检查企业是否存在
        EnterpriseInfo existing = getEnterpriseById(saveDTO.getId());

        // 检查企业名称是否已存在（排除自身）
        checkEnterpriseNameExists(saveDTO.getEnterpriseName(), saveDTO.getParkId(), saveDTO.getId());

        // 更新属性
        BeanUtils.copyProperties(saveDTO, existing);

        int rows = enterpriseMapper.updateById(existing);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_UPDATE_ERROR);
        }
        log.info("修改企业成功：ID={}, 名称={}", existing.getId(), existing.getEnterpriseName());
    }

    /**
     * 删除企业
     *
     * @param id 企业ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnterprise(Long id) {
        // 检查企业是否存在
        getEnterpriseById(id);

        int rows = enterpriseMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_DELETE_ERROR);
        }
        log.info("删除企业成功：ID={}", id);
    }

    /**
     * 检查企业名称是否已存在
     *
     * @param enterpriseName 企业名称
     * @param parkId         园区ID
     * @param excludeId      排除的企业ID（修改时使用）
     */
    private void checkEnterpriseNameExists(String enterpriseName, Long parkId, Long excludeId) {
        LambdaQueryWrapper<EnterpriseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnterpriseInfo::getEnterpriseName, enterpriseName)
                   .eq(EnterpriseInfo::getParkId, parkId);

        if (excludeId != null) {
            queryWrapper.ne(EnterpriseInfo::getId, excludeId);
        }

        Long count = enterpriseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_EXISTS, "该园区下已存在同名企业");
        }
    }

    // ==================== 企业荣誉汇总 ====================

    /**
     * 查询企业荣誉数量统计汇总表（分页）
     * 按 park_id + year 聚合各类荣誉数量，关联 park_info 获取园区基础信息，
     * 关联 enterprise_info 获取参评企业总数。
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<Map<String, Object>> getEnterpriseHonorSummary(EnterpriseHonorSummaryQueryDTO queryDTO) {
        // 1. 解析园区过滤条件
        List<Long> parkIds = resolveParkIds(queryDTO);
        if (parkIds != null && parkIds.isEmpty()) {
            return buildEmptyPage(queryDTO);
        }

        // 2. 查询荣誉记录并按园区聚合
        List<Map<String, Object>> records = buildHonorSummaryRows(parkIds, queryDTO.getYear());

        // 3. 内存分页
        return buildPage(records, queryDTO);
    }

    /**
     * 导出企业荣誉数量统计汇总表（按 parkType 分两个 Sheet）
     *
     * @param queryDTO 查询条件
     * @return Excel 字节数组
     */
    public byte[] exportEnterpriseHonorSummary(EnterpriseHonorSummaryQueryDTO queryDTO) {
        List<Long> parkIds = resolveParkIds(queryDTO);
        List<Map<String, Object>> records = parkIds.isEmpty()
                ? Collections.emptyList()
                : buildHonorSummaryRows(parkIds, queryDTO.getYear());

        try (Workbook wb = new XSSFWorkbook()) {
            // 按 parkType 分组
            Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
            grouped.put("生产性制造类", new ArrayList<>());
            grouped.put("生产性服务类", new ArrayList<>());
            for (Map<String, Object> row : records) {
                String parkType = row.get("parkType") != null ? row.get("parkType").toString() : "其他";
                grouped.computeIfAbsent(parkType, k -> new ArrayList<>()).add(row);
            }
            grouped.values().removeIf(List::isEmpty);

            for (Map.Entry<String, List<Map<String, Object>>> entry : grouped.entrySet()) {
                Sheet sheet = wb.createSheet(entry.getKey());
                createHonorHeaderRow(wb, sheet);
                List<Map<String, Object>> groupRows = entry.getValue();
                int rowIdx = 1;
                for (int i = 0; i < groupRows.size(); i++) {
                    Row row = sheet.createRow(rowIdx++);
                    writeHonorRow(row, i + 1, groupRows.get(i));
                }
                autoSizeColumns(sheet, HONOR_SUMMARY_HEADERS.length);
            }
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                wb.write(out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            log.error("导出企业荣誉汇总失败", e);
            throw new BusinessException(ResultCode.FAILURE, "导出失败：" + e.getMessage());
        }
    }

    /**
     * 解析园区ID列表（根据数据权限和筛选条件）
     *
     * @param queryDTO 查询条件
     * @return 园区ID列表，null 表示不限制，空列表表示无数据
     */
    private List<Long> resolveParkIds(EnterpriseHonorSummaryQueryDTO queryDTO) {
        // 数据权限优先：园区管理员指定单园区 / 区县管理员指定多园区
        if (queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty()) {
            return new ArrayList<>(queryDTO.getParkIds());
        }
        if (queryDTO.getParkId() != null) {
            return Collections.singletonList(queryDTO.getParkId());
        }

        // 按园区名称/区域/类型过滤
        LambdaQueryWrapper<ParkInfo> parkWrapper = new LambdaQueryWrapper<>();
        parkWrapper.select(ParkInfo::getId, ParkInfo::getParkName, ParkInfo::getDistrictName,
                ParkInfo::getParkType, ParkInfo::getEnterpriseCount);
        if (StringUtils.hasText(queryDTO.getParkName())) {
            parkWrapper.like(ParkInfo::getParkName, queryDTO.getParkName().trim());
        }
        if (StringUtils.hasText(queryDTO.getRegion())) {
            parkWrapper.eq(ParkInfo::getDistrictName, queryDTO.getRegion().trim());
        }
        if (StringUtils.hasText(queryDTO.getType())) {
            parkWrapper.eq(ParkInfo::getParkType, queryDTO.getType().trim());
        }
        return parkMapper.selectList(parkWrapper).stream()
                .map(ParkInfo::getId)
                .collect(Collectors.toList());
    }

    /**
     * 构建荣誉汇总行（每行一个园区）
     */
    private List<Map<String, Object>> buildHonorSummaryRows(List<Long> parkIds, Integer year) {
        if (parkIds == null || parkIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 查询园区信息
        Map<Long, ParkInfo> parkMap = new LinkedHashMap<>();
        for (ParkInfo p : parkMapper.selectBatchIds(parkIds)) {
            parkMap.put(p.getId(), p);
        }

        // 2. 查询荣誉记录
        LambdaQueryWrapper<EnterpriseHonorRecord> honorWrapper = new LambdaQueryWrapper<>();
        honorWrapper.in(EnterpriseHonorRecord::getParkId, parkIds);
        if (year != null) {
            honorWrapper.eq(EnterpriseHonorRecord::getYear, year);
        }
        List<EnterpriseHonorRecord> honorRecords = enterpriseHonorRecordMapper.selectList(honorWrapper);

        // 3. 按 parkId 聚合：parkId -> (honorType -> totalCount)
        Map<Long, Map<String, Integer>> parkHonorMap = new LinkedHashMap<>();
        for (EnterpriseHonorRecord r : honorRecords) {
            if (r.getParkId() == null) continue;
            Map<String, Integer> typeCount = parkHonorMap.computeIfAbsent(r.getParkId(), k -> new HashMap<>());
            int count = r.getHonorCount() != null ? r.getHonorCount() : 0;
            typeCount.merge(r.getHonorType(), count, Integer::sum);
        }

        // 4. 查询参评企业数（enterprise_info.is_participate=1）
        LambdaQueryWrapper<EnterpriseInfo> entWrapper = new LambdaQueryWrapper<>();
        entWrapper.in(EnterpriseInfo::getParkId, parkIds);
        entWrapper.eq(EnterpriseInfo::getIsParticipate, 1);
        entWrapper.select(EnterpriseInfo::getId, EnterpriseInfo::getParkId);
        Map<Long, Long> participateCountMap = enterpriseMapper.selectList(entWrapper).stream()
                .collect(Collectors.groupingBy(EnterpriseInfo::getParkId, Collectors.counting()));

        // 5. 组装结果行
        List<Map<String, Object>> records = new ArrayList<>();
        for (Long parkId : parkIds) {
            ParkInfo park = parkMap.get(parkId);
            if (park == null) continue;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("parkName", park.getParkName());
            row.put("region", park.getDistrictName());
            row.put("parkType", park.getParkType());
            // 参评企业总数：取 enterprise_info 中 is_participate=1 的企业数量，无则显示0
            Long participateCount = participateCountMap.get(parkId);
            row.put("totalEnterprises", participateCount != null ? participateCount.intValue() : 0);

            // 各荣誉类型数量
            Map<String, Integer> typeCount = parkHonorMap.getOrDefault(parkId, Collections.emptyMap());
            for (Map.Entry<String, String> entry : HONOR_TYPE_FIELD_MAP.entrySet()) {
                row.put(entry.getValue(), typeCount.getOrDefault(entry.getKey(), 0));
            }
            records.add(row);
        }
        return records;
    }

    /**
     * 内存分页构建
     */
    private IPage<Map<String, Object>> buildPage(List<Map<String, Object>> records, EnterpriseHonorSummaryQueryDTO queryDTO) {
        int pageNum = queryDTO.getPageNum() != null && queryDTO.getPageNum() > 0 ? queryDTO.getPageNum() : 1;
        int pageSize = queryDTO.getPageSize() != null && queryDTO.getPageSize() > 0 ? queryDTO.getPageSize() : 20;
        int total = records.size();
        int fromIndex = Math.min((pageNum - 1) * pageSize, total);
        int toIndex = Math.min(pageNum * pageSize, total);
        List<Map<String, Object>> pageRecords = records.subList(fromIndex, toIndex);

        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);
        page.setTotal(total);
        page.setRecords(pageRecords);
        return page;
    }

    private IPage<Map<String, Object>> buildEmptyPage(EnterpriseHonorSummaryQueryDTO queryDTO) {
        Page<Map<String, Object>> page = new Page<>(
                queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1,
                queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 20);
        page.setTotal(0);
        page.setRecords(Collections.emptyList());
        return page;
    }

    private void createHonorHeaderRow(Workbook wb, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < HONOR_SUMMARY_HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HONOR_SUMMARY_HEADERS[i]);
            cell.setCellStyle(style);
        }
    }

    private void writeHonorRow(Row row, int index, Map<String, Object> data) {
        int col = 0;
        row.createCell(col++).setCellValue(index);
        row.createCell(col++).setCellValue(str(data.get("parkName")));
        row.createCell(col++).setCellValue(str(data.get("region")));
        row.createCell(col++).setCellValue(str(data.get("parkType")));
        setCellInt(row.createCell(col++), toInt(data.get("totalEnterprises")));
        for (String field : HONOR_TYPE_FIELD_MAP.values()) {
            setCellInt(row.createCell(col++), toInt(data.get(field)));
        }
    }

    private void setCellInt(Cell cell, int val) {
        cell.setCellValue(val);
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(val.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private String str(Object val) {
        return val != null ? val.toString() : "";
    }

    private void autoSizeColumns(Sheet sheet, int count) {
        for (int i = 0; i < count; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.min(width + 512, 8192));
        }
    }
}
