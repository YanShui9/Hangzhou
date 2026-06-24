package com.park.evaluation.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 产业发展数据解析结果DTO
 */
@Data
public class IndustryDevelopmentParseResult {

    private boolean success;
    
    private List<IndustryDevelopmentExcelData> dataList;
    
    private List<ParseError> errorList;
    
    private int totalCount;
    
    private int successCount;
    
    private int errorCount;

    public IndustryDevelopmentParseResult() {
        this.dataList = new ArrayList<>();
        this.errorList = new ArrayList<>();
        this.success = true;
        this.totalCount = 0;
        this.successCount = 0;
        this.errorCount = 0;
    }

    @Data
    public static class ParseError {
        private Integer rowNum;
        private String enterpriseName;
        private String errorMessage;

        public ParseError(Integer rowNum, String enterpriseName, String errorMessage) {
            this.rowNum = rowNum;
            this.enterpriseName = enterpriseName;
            this.errorMessage = errorMessage;
        }
    }
}
