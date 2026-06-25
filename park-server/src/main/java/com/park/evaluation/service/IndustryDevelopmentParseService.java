package com.park.evaluation.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.mapper.EnterpriseMapper;
import com.park.evaluation.dto.IndustryDevelopmentExcelData;
import com.park.evaluation.dto.IndustryDevelopmentParseResult;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IndustryDevelopmentParseService {

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private ParkMapper parkMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public IndustryDevelopmentParseResult parseAndSave(MultipartFile file) throws IOException {
        IndustryDevelopmentParseResult result = new IndustryDevelopmentParseResult();
        
        List<IndustryDevelopmentExcelData> dataList = new ArrayList<>();
        List<IndustryDevelopmentParseResult.ParseError> errorList = new ArrayList<>();
        
        EasyExcel.read(file.getInputStream(), new IndustryDevelopmentDataListener(dataList, errorList))
                .headRowNumber(0)
                .sheet()
                .doRead();
        
        calculateSettledDate(dataList);
        
        result.setDataList(dataList);
        result.setErrorList(errorList);
        result.setTotalCount(dataList.size() + errorList.size());
        result.setSuccessCount(dataList.size());
        result.setErrorCount(errorList.size());
        
        if (!errorList.isEmpty()) {
            result.setSuccess(false);
            return result;
        }
        
        saveToDatabase(dataList);
        
        return result;
    }

    private void calculateSettledDate(List<IndustryDevelopmentExcelData> dataList) {
        for (IndustryDevelopmentExcelData data : dataList) {
            String startTime = data.getSettledStartTime();
            String endTime = data.getSettledEndTime();
            
            if (startTime != null && !startTime.trim().isEmpty() && endTime != null && !endTime.trim().isEmpty()) {
                data.setSettledDate(startTime.trim() + " - " + endTime.trim());
            } else if (startTime != null && !startTime.trim().isEmpty()) {
                data.setSettledDate(startTime.trim());
            } else if (endTime != null && !endTime.trim().isEmpty()) {
                data.setSettledDate(endTime.trim());
            } else {
                data.setSettledDate("-");
            }
        }
    }

    private void saveToDatabase(List<IndustryDevelopmentExcelData> dataList) {
        Map<String, Long> parkNameToIdMap = buildParkNameToIdMap();
        
        for (IndustryDevelopmentExcelData data : dataList) {
            EnterpriseInfo enterprise = convertToEntity(data, parkNameToIdMap);
            
            EnterpriseInfo existing = enterpriseMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EnterpriseInfo>()
                            .eq(EnterpriseInfo::getCreditCode, data.getUnifiedCreditCode())
            );
            
            if (existing != null) {
                enterprise.setId(existing.getId());
                enterprise.setCreateTime(existing.getCreateTime());
                enterpriseMapper.updateById(enterprise);
            } else {
                enterpriseMapper.insert(enterprise);
            }
        }
    }

    private Map<String, Long> buildParkNameToIdMap() {
        Map<String, Long> map = new HashMap<>();
        List<ParkInfo> parks = parkMapper.selectList(null);
        for (ParkInfo park : parks) {
            map.put(park.getParkName(), park.getId());
        }
        return map;
    }

    private EnterpriseInfo convertToEntity(IndustryDevelopmentExcelData data, Map<String, Long> parkNameToIdMap) {
        EnterpriseInfo enterprise = new EnterpriseInfo();

        String parkName = data.getBelongParkName() != null ? data.getBelongParkName() : data.getParkName();
        enterprise.setParkId(parkNameToIdMap.getOrDefault(parkName, null));
        enterprise.setParkName(parkName);
        enterprise.setEnterpriseName(data.getEnterpriseName());
        enterprise.setCreditCode(data.getUnifiedCreditCode());
        // 适配本地实体：registeredAddress -> enterpriseAddress
        enterprise.setEnterpriseAddress(data.getRegisteredAddress());
        // 适配本地实体：honor -> enterpriseHonor (transient)
        enterprise.setEnterpriseHonor(data.getHonor());
        enterprise.setDistrictName(data.getDistrictName());
        enterprise.setLegalPerson(data.getLegalPerson());
        enterprise.setContactName(data.getContactName());
        enterprise.setContactPhone(data.getContactPhone());
        enterprise.setBusinessScope(data.getBusinessScope());
        // 适配本地实体：registerStatus 合并到 status
        if (data.getRegisterStatus() != null && !data.getRegisterStatus().trim().isEmpty()) {
            enterprise.setStatus(data.getRegisterStatus());
        } else {
            enterprise.setStatus(data.getStatus());
        }
        enterprise.setRemark(data.getRemark());
        enterprise.setIsParticipate(1);

        if (data.getRegisteredCapital() != null && !data.getRegisteredCapital().trim().isEmpty()) {
            try {
                enterprise.setRegisteredCapital(new BigDecimal(data.getRegisteredCapital().trim()));
            } catch (NumberFormatException e) {
                enterprise.setRegisteredCapital(BigDecimal.ZERO);
            }
        }

        // 本地 EnterpriseInfo 实体无 employeeCount 字段，跳过

        // 本地实体无 annualOutput / annualTax 字段，跳过

        if (data.getRegisterDate() != null && !data.getRegisterDate().trim().isEmpty()) {
            try {
                enterprise.setRegisterDate(LocalDate.parse(data.getRegisterDate().trim(), DATE_FORMATTER));
            } catch (DateTimeParseException e) {
                enterprise.setRegisterDate(null);
            }
        }

        // 适配本地实体：entryStartTime -> settledTime（取入驻开始时间作为入驻时间）
        if (data.getSettledStartTime() != null && !data.getSettledStartTime().trim().isEmpty()) {
            try {
                enterprise.setSettledTime(LocalDate.parse(data.getSettledStartTime().trim(), DATE_FORMATTER));
            } catch (DateTimeParseException e) {
                enterprise.setSettledTime(null);
            }
        }
        // 本地实体无 entryEndTime 字段，跳过

        return enterprise;
    }

    private static class IndustryDevelopmentDataListener extends AnalysisEventListener<Map<Integer, Object>> {
        
        private final List<IndustryDevelopmentExcelData> dataList;
        private final List<IndustryDevelopmentParseResult.ParseError> errorList;
        private int rowNum = 0;
        private Map<String, Integer> headerIndexMap;
        
        public IndustryDevelopmentDataListener(List<IndustryDevelopmentExcelData> dataList, 
                                               List<IndustryDevelopmentParseResult.ParseError> errorList) {
            this.dataList = dataList;
            this.errorList = errorList;
        }

        @Override
        public void invoke(Map<Integer, Object> rowData, AnalysisContext context) {
            rowNum++;
            
            if (rowNum == 1) {
                parseHeader(rowData);
                return;
            }
            
            IndustryDevelopmentExcelData data = new IndustryDevelopmentExcelData();
            
            data.setParkId(getValueByHeader(rowData, "park_id", "园区编号"));
            data.setParkName(getValueByHeader(rowData, "园区名称"));
            data.setEnterpriseName(getValueByHeader(rowData, "入园企业名称", "入驻企业名称"));
            data.setUnifiedCreditCode(getValueByHeader(rowData, "统一社会信用代码"));
            data.setSettledStartTime(getValueByHeader(rowData, "入驻开始时间"));
            data.setSettledEndTime(getValueByHeader(rowData, "入驻截止时间"));
            data.setRegisteredAddress(getValueByHeader(rowData, "企业注册地址"));
            data.setHonor(getValueByHeader(rowData, "企业荣誉"));
            data.setDistrictName(getValueByHeader(rowData, "所属区域"));
            data.setBelongParkName(getValueByHeader(rowData, "所属园区"));
            data.setLegalPerson(getValueByHeader(rowData, "法定代表人"));
            data.setContactName(getValueByHeader(rowData, "联系人"));
            data.setContactPhone(getValueByHeader(rowData, "联系人电话"));
            data.setRegisteredCapital(getValueByHeader(rowData, "注册资本"));
            data.setRegisterDate(getValueByHeader(rowData, "注册日期"));
            data.setEmployeeCount(getValueByHeader(rowData, "员工人数"));
            data.setAnnualOutput(getValueByHeader(rowData, "年产值"));
            data.setAnnualTax(getValueByHeader(rowData, "年纳税额"));
            data.setLeaseArea(getValueByHeader(rowData, "租凭面积", "租赁面积"));
            data.setLeaseTime(getValueByHeader(rowData, "租赁时间"));
            data.setBusinessScope(getValueByHeader(rowData, "经营范围"));
            data.setRegisterStatus(getValueByHeader(rowData, "登记状态"));
            data.setStatus(getValueByHeader(rowData, "企业状态"));
            data.setRemark(getValueByHeader(rowData, "备注"));
            
            List<String> errors = validateData(data, rowNum);
            if (!errors.isEmpty()) {
                errorList.add(new IndustryDevelopmentParseResult.ParseError(
                        rowNum, 
                        data.getEnterpriseName(), 
                        String.join("；", errors)
                ));
            } else {
                dataList.add(data);
            }
        }

        private void parseHeader(Map<Integer, Object> rowData) {
            headerIndexMap = new HashMap<>();
            for (Map.Entry<Integer, Object> entry : rowData.entrySet()) {
                String header = getStringValue(entry.getValue());
                if (header != null && !header.isEmpty()) {
                    headerIndexMap.put(header, entry.getKey());
                }
            }
        }

        private String getValueByHeader(Map<Integer, Object> rowData, String... possibleHeaders) {
            for (String header : possibleHeaders) {
                Integer index = headerIndexMap.get(header);
                if (index != null) {
                    return getStringValue(rowData.get(index));
                }
                for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {
                    if (entry.getKey() != null && entry.getKey().contains(header)) {
                        return getStringValue(rowData.get(entry.getValue()));
                    }
                }
            }
            return null;
        }

        private String getStringValue(Object value) {
            if (value == null) {
                return null;
            }
            return value.toString().trim();
        }

        private List<String> validateData(IndustryDevelopmentExcelData data, int rowNum) {
            List<String> errors = new ArrayList<>();
            
            if (data.getParkName() == null || data.getParkName().isEmpty()) {
                errors.add("园区名称为空");
            }
            
            if (data.getEnterpriseName() == null || data.getEnterpriseName().isEmpty()) {
                errors.add("入园企业名称为空");
            }
            
            if (data.getUnifiedCreditCode() == null || data.getUnifiedCreditCode().isEmpty()) {
                errors.add("统一社会信用代码为空");
            } else {
                String creditCode = data.getUnifiedCreditCode().replace("-", "").trim();
                if (!isValidCreditCode(creditCode)) {
                    errors.add("统一社会信用代码格式不正确");
                }
            }
            
            return errors;
        }

        private boolean isValidCreditCode(String creditCode) {
            if (creditCode == null || creditCode.length() != 18) {
                return false;
            }
            
            String baseCode = "0123456789ABCDEFGHJKLMNPQRTUWXY";
            
            for (int i = 0; i < 18; i++) {
                char c = creditCode.charAt(i);
                if (baseCode.indexOf(c) == -1) {
                    return false;
                }
            }
            
            return true;
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("产业发展数据解析完成，总行数: {}, 成功: {}, 失败: {}", 
                    rowNum - 1, dataList.size(), errorList.size());
        }
    }
}
