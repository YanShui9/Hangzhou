package com.park.evaluation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.evaluation.dto.EvaluationQueryDTO;
import com.park.evaluation.dto.EvaluationSaveDTO;
import com.park.evaluation.entity.EvaluationRecord;
import com.park.evaluation.mapper.EvaluationMapper;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评价服务
 * 状态定义：0=草稿, 1=待区县审, 2=待市局审, 3=通过, 4=驳回
 *
 * @author park-team
 */
@Slf4j
@Service
public class EvaluationService {

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private ParkMapper parkMapper;

    /**
     * 分页查询评价记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<EvaluationRecord> getEvaluationPage(EvaluationQueryDTO queryDTO) {
        Page<EvaluationRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (queryDTO.getParkId() != null) {
            wrapper.eq(EvaluationRecord::getParkId, queryDTO.getParkId());
        }
        // 支持多园区查询（区县管理员使用）
        if (queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty()) {
            wrapper.in(EvaluationRecord::getParkId, queryDTO.getParkIds());
        }
        if (queryDTO.getEvalYear() != null) {
            wrapper.eq(EvaluationRecord::getEvalYear, queryDTO.getEvalYear());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(EvaluationRecord::getStatus, queryDTO.getStatus());
        }

        // 默认按创建时间降序排序
        wrapper.orderByDesc(EvaluationRecord::getCreateTime);

        return evaluationMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询评价记录详情
     *
     * @param id 评价记录ID
     * @return 评价记录实体
     */
    public EvaluationRecord getEvaluationById(Long id) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        return record;
    }

    /**
     * 保存评价记录（新增或修改）
     *
     * @param saveDTO 保存请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEvaluation(EvaluationSaveDTO saveDTO) {
        // 检查同一园区、年份是否已存在评价记录（排除自身）
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EvaluationRecord::getParkId, saveDTO.getParkId())
                .eq(EvaluationRecord::getEvalYear, saveDTO.getEvalYear());
        if (saveDTO.getId() != null) {
            wrapper.ne(EvaluationRecord::getId, saveDTO.getId());
        }
        Long count = evaluationMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_EXISTS, "该园区该年度的评价记录已存在");
        }

        EvaluationRecord record = new EvaluationRecord();
        BeanUtils.copyProperties(saveDTO, record);

        if (saveDTO.getId() != null) {
            // 修改
            EvaluationRecord existing = evaluationMapper.selectById(saveDTO.getId());
            if (existing == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
            }
            // 只有草稿状态才能修改
            if (existing.getStatus() != 0) {
                throw new BusinessException(ResultCode.FAILURE, "只有草稿状态的评价记录才能修改");
            }
            evaluationMapper.updateById(record);
            log.info("评价记录修改成功：id={}", saveDTO.getId());
        } else {
            // 新增
            record.setStatus(0); // 0=草稿
            evaluationMapper.insert(record);
            log.info("评价记录新增成功：parkId={}, year={}",
                    saveDTO.getParkId(), saveDTO.getEvalYear());
        }
    }

    /**
     * 提交评价（状态从 0=草稿 改为 1=待区县审）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitEvaluation(Long id) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        if (record.getStatus() != 0) {
            throw new BusinessException(ResultCode.FAILURE, "只有草稿状态的评价记录才能提交");
        }

        record.setStatus(1); // 1=待区县审
        evaluationMapper.updateById(record);
        log.info("评价记录已提交：id={}", id);
    }

    /**
     * 区县审核通过（状态从 1=待区县审 改为 2=待市局审）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void districtPass(Long id) {
        updateStatus(id, 1, 2, "区县审核通过");
    }

    /**
     * 区县审核驳回（状态从 1=待区县审 改为 4=驳回）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void districtReject(Long id) {
        updateStatus(id, 1, 4, "区县审核驳回");
    }

    /**
     * 市级审核通过（状态从 2=待市局审 改为 3=通过）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cityPass(Long id) {
        updateStatus(id, 2, 3, "市级审核通过");
    }

    /**
     * 市级审核驳回（状态从 2=待市局审 改为 4=驳回）
     *
     * @param id 评价记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cityReject(Long id) {
        updateStatus(id, 2, 4, "市级审核驳回");
    }

    /**
     * 更新参评状态
     * 0=不参评, 1=参评
     *
     * @param id     评价记录ID
     * @param status 目标状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateParticipateStatus(Long id, Integer status) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        if (status != 0 && status != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "参评状态必须为 0(不参评) 或 1(参评)");
        }
        record.setEvalStatus(status);
        // 如果从参评变为不参评，同时将审核状态重置为草稿
        if (status == 0) {
            record.setStatus(0);
        }
        // 如果从不参评变为参评，同时将审核状态设为待区县审
        if (status == 1 && record.getStatus() == 0) {
            record.setStatus(1);
        }
        evaluationMapper.updateById(record);
        log.info("评价记录参评状态更新：id={}, evalStatus={}, auditStatus={}", id, status, record.getStatus());
    }

    /**
     * 更新评价记录状态
     *
     * @param id             评价记录ID
     * @param expectedStatus 期望的当前状态
     * @param newStatus      新状态
     * @param actionDesc     操作描述（用于日志）
     */
    private void updateStatus(Long id, Integer expectedStatus, Integer newStatus, String actionDesc) {
        EvaluationRecord record = evaluationMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评价记录不存在");
        }
        if (!expectedStatus.equals(record.getStatus())) {
            throw new BusinessException(ResultCode.FAILURE,
                    "当前状态不允许执行此操作，期望状态：" + expectedStatus + "，实际状态：" + record.getStatus());
        }

        record.setStatus(newStatus);
        evaluationMapper.updateById(record);
        log.info("评价记录{}：id={}", actionDesc, id);
    }

    /**
     * 查询评价记录列表（不分页，用于导出）
     *
     * @param queryDTO 查询条件
     * @return 评价记录列表
     */
    public List<EvaluationRecord> getEvaluationList(EvaluationQueryDTO queryDTO) {
        LambdaQueryWrapper<EvaluationRecord> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getParkId() != null) {
            wrapper.eq(EvaluationRecord::getParkId, queryDTO.getParkId());
        }
        if (queryDTO.getParkIds() != null && !queryDTO.getParkIds().isEmpty()) {
            wrapper.in(EvaluationRecord::getParkId, queryDTO.getParkIds());
        }
        if (queryDTO.getEvalYear() != null) {
            wrapper.eq(EvaluationRecord::getEvalYear, queryDTO.getEvalYear());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(EvaluationRecord::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByDesc(EvaluationRecord::getCreateTime);

        return evaluationMapper.selectList(wrapper);
    }

    /**
     * 导出评价记录为Excel
     *
     * @param queryDTO 查询条件
     * @return Excel文件字节数组
     */
    public byte[] exportEvaluations(EvaluationQueryDTO queryDTO) throws IOException {
        List<EvaluationRecord> records = getEvaluationList(queryDTO);

        // 获取园区信息映射
        List<Long> parkIds = records.stream()
                .map(EvaluationRecord::getParkId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ParkInfo> parkMap = parkMapper.selectBatchIds(parkIds).stream()
                .collect(Collectors.toMap(ParkInfo::getId, p -> p));

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("园区评价记录");

            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 创建数据样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            // 创建表头
            String[] headers = {"序号", "园区名称", "区县", "评价年份", "评价总分", "绩效分档", "状态", "驳回类别", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            int rowNum = 1;
            for (EvaluationRecord record : records) {
                Row row = sheet.createRow(rowNum);

                ParkInfo park = parkMap.get(record.getParkId());
                String parkName = park != null ? park.getParkName() : "-";
                String districtName = park != null ? park.getDistrictName() : "-";

                // 序号
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(rowNum);
                cell0.setCellStyle(dataStyle);

                // 园区名称
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(parkName);
                cell1.setCellStyle(dataStyle);

                // 区县
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(districtName);
                cell2.setCellStyle(dataStyle);

                // 评价年份
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(record.getEvalYear() != null ? record.getEvalYear() + "年" : "-");
                cell3.setCellStyle(dataStyle);

                // 评价总分
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(record.getTotalScore() != null ? record.getTotalScore().toString() : "-");
                cell4.setCellStyle(dataStyle);

                // 绩效分档
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(record.getGrade() != null ? record.getGrade() : "-");
                cell5.setCellStyle(dataStyle);

                // 状态
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(getStatusLabel(record.getStatus()));
                cell6.setCellStyle(dataStyle);

                // 驳回类别
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(record.getRejectCategory() != null ? record.getRejectCategory() : "-");
                cell7.setCellStyle(dataStyle);

                // 创建时间
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(record.getCreateTime() != null ? record.getCreateTime().toString() : "-");
                cell8.setCellStyle(dataStyle);

                rowNum++;
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 获取状态标签
     */
    private String getStatusLabel(Integer status) {
        if (status == null) {
            return "-";
        }
        switch (status) {
            case 0:
                return "草稿";
            case 1:
                return "待区县审";
            case 2:
                return "待市局审";
            case 3:
                return "通过";
            case 4:
                return "驳回";
            default:
                return "-";
        }
    }
}
