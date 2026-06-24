package com.park.operation.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.operation.dto.EnterpriseListParseResult;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.mapper.EnterpriseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class EnterpriseListParseService {

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    public EnterpriseListParseResult parseExcel(InputStream inputStream) {
        EnterpriseListParseResult result = new EnterpriseListParseResult();
        List<Map<Integer, Object>> dataList = new ArrayList<>();

        EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, Object>>() {
            @Override
            public void invoke(Map<Integer, Object> rowData, AnalysisContext context) {
                if (context.readRowHolder().getRowIndex() == 0) {
                    return;
                }
                if (!rowData.isEmpty() && hasValidData(rowData)) {
                    dataList.add(rowData);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("Excel解析完成，共{}条数据", dataList.size());
            }
        }).sheet().doRead();

        result.setTotalCount(dataList.size());
        result.setEnterpriseNames(extractEnterpriseNames(dataList));
        result.setCreditCodes(extractCreditCodes(dataList));

        return result;
    }

    private boolean hasValidData(Map<Integer, Object> rowData) {
        for (Object value : rowData.values()) {
            if (value != null && !value.toString().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<String> extractEnterpriseNames(List<Map<Integer, Object>> dataList) {
        List<String> names = new ArrayList<>();
        for (Map<Integer, Object> row : dataList) {
            Object name = row.get(0);
            if (name != null && !name.toString().trim().isEmpty()) {
                names.add(name.toString().trim());
            }
        }
        return names;
    }

    private List<String> extractCreditCodes(List<Map<Integer, Object>> dataList) {
        List<String> codes = new ArrayList<>();
        for (Map<Integer, Object> row : dataList) {
            Object code = row.get(1);
            if (code != null && !code.toString().trim().isEmpty()) {
                codes.add(code.toString().trim());
            }
        }
        return codes;
    }

    public EnterpriseListParseResult calculateHonorStats(Long parkId, List<String> enterpriseNames) {
        EnterpriseListParseResult result = new EnterpriseListParseResult();
        result.setTotalCount(enterpriseNames.size());

        if (enterpriseNames.isEmpty()) {
            return result;
        }

        List<EnterpriseInfo> allEnterprises = enterpriseMapper.selectList(null);
        Map<String, String> honorMap = new HashMap<>();
        for (EnterpriseInfo enterprise : allEnterprises) {
            honorMap.put(enterprise.getEnterpriseName(), enterprise.getHonor());
        }

        int highTechCount = 0;
        int hiddenChampionCount = 0;
        int nationalSrtiCount = 0;
        int innovativeSmeCount = 0;
        int provincialSrtiCount = 0;

        for (String enterpriseName : enterpriseNames) {
            String honor = honorMap.get(enterpriseName);
            if (honor != null && !honor.isEmpty()) {
                String[] honors = honor.split(",");
                for (String h : honors) {
                    String trimmed = h.trim();
                    switch (trimmed) {
                        case "national_high":
                        case "国高":
                            highTechCount++;
                            break;
                        case "hidden_champion":
                        case "隐形冠军":
                            hiddenChampionCount++;
                            break;
                        case "national_small_giant":
                        case "小巨人":
                        case "国家级专精特新小巨人":
                            nationalSrtiCount++;
                            break;
                        case "innovation":
                        case "创新型":
                            innovativeSmeCount++;
                            break;
                        case "provincial_high":
                        case "省专":
                        case "provincial_small_giant":
                        case "省专小巨人":
                            provincialSrtiCount++;
                            break;
                    }
                }
            }
        }

        result.setHighTechCount(highTechCount);
        result.setHiddenChampionCount(hiddenChampionCount);
        result.setNationalSrtiCount(nationalSrtiCount);
        result.setInnovativeSmeCount(innovativeSmeCount);
        result.setProvincialSrtiCount(provincialSrtiCount);
        result.setEnterpriseNames(enterpriseNames);

        return result;
    }

    public EnterpriseListParseResult parseAndCalculate(Long parkId, InputStream inputStream) {
        EnterpriseListParseResult parseResult = parseExcel(inputStream);
        EnterpriseListParseResult honorResult = calculateHonorStats(parkId, parseResult.getEnterpriseNames());
        honorResult.setTotalCount(parseResult.getTotalCount());
        honorResult.setCreditCodes(parseResult.getCreditCodes());
        return honorResult;
    }
}
