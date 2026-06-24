package com.park.warehouse.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import com.park.warehouse.entity.DataWarehouse;
import com.park.warehouse.mapper.DataWarehouseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据仓库服务
 * 用于园区认定、星级评定、产业方向导入，并同步到 park_info
 *
 * @author park-team
 */
@Slf4j
@Service
public class DataWarehouseService {

    @Autowired
    private DataWarehouseMapper dataWarehouseMapper;

    @Autowired
    private ParkMapper parkMapper;

    /**
     * 分页查询数据仓库
     */
    public IPage<DataWarehouse> getDataWarehousePage(int pageNum, int pageSize, String parkName, Integer year) {
        Page<DataWarehouse> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DataWarehouse> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(parkName)) {
            wrapper.like(DataWarehouse::getParkName, parkName);
        }
        if (year != null) {
            wrapper.eq(DataWarehouse::getImportYear, year);
        }
        wrapper.orderByDesc(DataWarehouse::getImportTime);
        return dataWarehouseMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID获取数据仓库记录
     */
    public DataWarehouse getById(Long id) {
        return dataWarehouseMapper.selectById(id);
    }

    /**
     * 导入数据仓库（CSV/Excel）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importData(MultipartFile file, String importBy) {
        try {
            Map<String, Object> result = new HashMap<>();
            int success = 0;
            int fail = 0;
            List<String> errors = new ArrayList<>();

            // 简化实现：假设文件已解析为 List<Map>
            List<Map<String, Object>> rows = parseFile(file);

            for (Map<String, Object> row : rows) {
                try {
                    String parkName = (String) row.get("园区名称");
                    if (parkName == null || parkName.trim().isEmpty()) {
                        errors.add("第" + (rows.indexOf(row) + 1) + "行：园区名称为空");
                        fail++;
                        continue;
                    }

                    // 查找园区
                    LambdaQueryWrapper<ParkInfo> parkWrapper = new LambdaQueryWrapper<>();
                    parkWrapper.eq(ParkInfo::getParkName, parkName);
                    ParkInfo parkInfo = parkMapper.selectOne(parkWrapper);

                    if (parkInfo == null) {
                        errors.add("第" + (rows.indexOf(row) + 1) + "行：园区不存在");
                        fail++;
                        continue;
                    }

                    // 创建数据仓库记录
                    DataWarehouse dataWarehouse = new DataWarehouse();
                    dataWarehouse.setParkName(parkName);
                    dataWarehouse.setRecognition((String) row.get("园区认定"));
                    dataWarehouse.setStarLevel(row.get("星级") != null ? Integer.parseInt(row.get("星级").toString()) : null);
                    dataWarehouse.setLeadingIndustry((String) row.get("主导产业"));
                    dataWarehouse.setImportYear(row.get("导入年度") != null ? Integer.parseInt(row.get("导入年度").toString()) : java.time.LocalDate.now().getYear());
                    dataWarehouse.setImportBy(importBy);
                    dataWarehouse.setImportTime(new java.util.Date());
                    dataWarehouse.setRemark((String) row.get("备注"));

                    dataWarehouseMapper.insert(dataWarehouse);

                    // 同步到 park_info
                    if (StringUtils.hasText(dataWarehouse.getRecognition())) {
                        parkInfo.setRecognition(dataWarehouse.getRecognition());
                    }
                    if (dataWarehouse.getStarLevel() != null) {
                        parkInfo.setStarLevel(dataWarehouse.getStarLevel());
                    }
                    if (StringUtils.hasText(dataWarehouse.getLeadingIndustry())) {
                        parkInfo.setLeadingIndustry(dataWarehouse.getLeadingIndustry());
                    }
                    parkMapper.updateById(parkInfo);

                    success++;
                } catch (Exception e) {
                    fail++;
                    errors.add("第" + (rows.indexOf(row) + 1) + "行：" + e.getMessage());
                }
            }

            result.put("success", success);
            result.put("fail", fail);
            result.put("errors", errors);
            log.info("数据仓库导入：成功={}, 失败={}", success, fail);
            return result;
        } catch (Exception e) {
            log.error("数据仓库导入失败", e);
            throw new BusinessException(ResultCode.SERVER_ERROR, "导入失败：" + e.getMessage());
        }
    }

    private List<Map<String, Object>> parseFile(MultipartFile file) {
        // 简化实现，实际应使用 EasyExcel 或 Apache POI
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("园区名称", "测试园区");
        row.put("园区认定", "已认定");
        row.put("星级", 4);
        row.put("主导产业", "数字经济,高端装备");
        rows.add(row);
        return rows;
    }

    /**
     * 删除数据仓库记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataWarehouse(Long id) {
        dataWarehouseMapper.deleteById(id);
        log.info("删除数据仓库记录：ID={}", id);
    }

    /**
     * 同步数据仓库到 park_info
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncToParkInfo(Long id) {
        DataWarehouse dataWarehouse = dataWarehouseMapper.selectById(id);
        if (dataWarehouse == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "记录不存在");
        }

        LambdaQueryWrapper<ParkInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkInfo::getParkName, dataWarehouse.getParkName());
        ParkInfo parkInfo = parkMapper.selectOne(wrapper);

        if (parkInfo == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区不存在");
        }

        if (StringUtils.hasText(dataWarehouse.getRecognition())) {
            parkInfo.setRecognition(dataWarehouse.getRecognition());
        }
        if (dataWarehouse.getStarLevel() != null) {
            parkInfo.setStarLevel(dataWarehouse.getStarLevel());
        }
        if (StringUtils.hasText(dataWarehouse.getLeadingIndustry())) {
            parkInfo.setLeadingIndustry(dataWarehouse.getLeadingIndustry());
        }

        parkMapper.updateById(parkInfo);
        log.info("同步数据仓库到 park_info：园区={}", dataWarehouse.getParkName());
    }
}