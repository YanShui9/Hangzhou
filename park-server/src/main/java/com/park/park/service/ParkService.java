package com.park.park.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.park.dto.ParkExportDTO;
import com.park.park.dto.ParkImportTemplate;
import com.park.park.dto.ParkQueryDTO;
import com.park.park.dto.ParkSaveDTO;
import com.park.park.dto.ParkStatsDTO;
import com.park.park.dto.TotalStatsDTO;
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 园区服务
 *
 * @author park-team
 */
@Slf4j
@Service
public class ParkService {

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private com.park.enterprise.mapper.EnterpriseMapper enterpriseMapper;

    /**
     * 分页查询园区列表
     * 所有查询条件均为可选项：空字符串或 null 表示不过滤该字段。
     * 类型严格与 ParkInfo 对齐，避免 MyBatis-Plus 类型不匹配导致的异常。
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<ParkInfo> getParkPage(ParkQueryDTO queryDTO) {
        // 构建分页对象
        Page<ParkInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();

        // ID 精确匹配
        queryWrapper.eq(queryDTO.getId() != null, ParkInfo::getId, queryDTO.getId());

        // 园区名称 模糊匹配
        queryWrapper.like(StringUtils.hasText(queryDTO.getParkName()),
                ParkInfo::getParkName, queryDTO.getParkName());

        // 区县名称 精确匹配
        queryWrapper.eq(StringUtils.hasText(queryDTO.getDistrictName()),
                ParkInfo::getDistrictName, queryDTO.getDistrictName());

        // 园区类型 精确匹配（String 类型，与 ParkInfo.parkType 保持一致）
        // 支持空字符串（前端默认值）：当为 null 或空串时不加入条件
        queryWrapper.eq(StringUtils.hasText(queryDTO.getParkType()),
                ParkInfo::getParkType, queryDTO.getParkType());

        // 星级 精确匹配
        queryWrapper.eq(queryDTO.getStarLevel() != null,
                ParkInfo::getStarLevel, queryDTO.getStarLevel());

        // 园区认定 精确匹配
        queryWrapper.eq(StringUtils.hasText(queryDTO.getRecognition()),
                ParkInfo::getRecognition, queryDTO.getRecognition());

        // 园区状态 精确匹配
        queryWrapper.eq(StringUtils.hasText(queryDTO.getParkStatus()),
                ParkInfo::getParkStatus, queryDTO.getParkStatus());

        // 年度 精确匹配
        queryWrapper.eq(queryDTO.getYear() != null,
                ParkInfo::getYear, queryDTO.getYear());

        // 按创建时间倒序，最新在前
        queryWrapper.orderByDesc(ParkInfo::getCreateTime);

        return parkMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID查询园区
     *
     * @param id 园区ID
     * @return 园区信息
     */
    public ParkInfo getParkById(Long id) {
        ParkInfo parkInfo = parkMapper.selectById(id);
        if (parkInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "园区不存在");
        }
        return parkInfo;
    }

    /**
     * 获取园区统计数据
     * 将 ParkInfo 中的统计字段映射为前端期望的字段名
     *
     * @param id 园区ID
     * @return 园区统计数据
     */
    public ParkStatsDTO getParkStats(Long id) {
        ParkInfo parkInfo = getParkById(id);

        return ParkStatsDTO.builder()
                // 入驻企业统计
                .enterpriseCount(parkInfo.getEnterpriseCount())
                .largeEnterpriseCount(parkInfo.getAboveScaleCount())
                .highTechEnterpriseCount(parkInfo.getHighTechCount())
                .smeCount(parkInfo.getTechSmeCount())
                .innovativeSmeCount(parkInfo.getInnovativeSmeCount())
                .specializedSmeCount(parkInfo.getNationalSpecializedCount())
                // 人才统计
                .employeeCount(parkInfo.getEmployeeCount())
                .nationalTalentCount(parkInfo.getNational1000TalentCount())
                .provincialTalentCount(parkInfo.getProvincial1000TalentCount())
                .seniorEngineerCount(parkInfo.getSeniorEngineerCount())
                .engineerCount(parkInfo.getSenior2EngineerCount())
                .seniorTechnicianCount(parkInfo.getSeniorTechnicianCount())
                .masterCount(parkInfo.getMasterAndAboveCount())
                .doctorCount(null)
                // 专利统计
                .patentCount(parkInfo.getPatentTotalCount())
                .inventionPatentCount(parkInfo.getInventionCount())
                .utilityModelCount(parkInfo.getUtilityModelCount())
                .designPatentCount(parkInfo.getAppearanceCount())
                .build();
    }

    /**
     * 获取全市园区统计数据
     * 用于前端"园区列表"页面顶部统计卡片
     *
     * @return 全市统计数据
     */
    public TotalStatsDTO getTotalStats() {
        // 统计园区总数
        Long parkTotal = parkMapper.selectCount(null);

        // 统计星级园区数
        Long starParkCount = parkMapper.selectCount(
                new LambdaQueryWrapper<ParkInfo>()
                        .isNotNull(ParkInfo::getStarLevel)
                        .gt(ParkInfo::getStarLevel, 0)
        );

        // 计算星级园区占比
        String starRate = "0%";
        if (parkTotal != null && parkTotal > 0 && starParkCount != null) {
            double rate = (starParkCount * 100.0) / parkTotal;
            starRate = String.format("%.1f%%", rate);
        }

        return TotalStatsDTO.builder()
                .parkTotal(parkTotal != null ? parkTotal.intValue() : 0)
                .enterpriseTotal(0)
                .employeeTotal(0)
                .starRate(starRate)
                .build();
    }

    /**
     * 新增园区
     * 字段名与 ParkInfo 保持一致，直接复制属性
     *
     * @param saveDTO 园区信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void savePark(ParkSaveDTO saveDTO) {
        checkParkNameDuplicate(saveDTO.getParkName(), null);

        ParkInfo parkInfo = new ParkInfo();
        BeanUtils.copyProperties(saveDTO, parkInfo);
        
        // 设置默认年度为当前年
        if (parkInfo.getYear() == null) {
            parkInfo.setYear(java.time.LocalDate.now().getYear());
        }

        log.info("新增园区：{}", parkInfo.getParkName());
        parkMapper.insert(parkInfo);
    }

    /**
     * 修改园区
     * 注意：运营数据由季度填报生成，不可手动修改
     *
     * @param saveDTO 园区信息（必须包含 id）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePark(ParkSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区ID不能为空");
        }

        // 检查园区是否存在
        ParkInfo existPark = getParkById(saveDTO.getId());

        // 检查园区名称是否重复（排除自身）
        checkParkNameDuplicate(saveDTO.getParkName(), saveDTO.getId());

        ParkInfo parkInfo = new ParkInfo();
        BeanUtils.copyProperties(saveDTO, parkInfo);
        parkInfo.setId(saveDTO.getId());
        
        // 保留原有的运营统计数据（入驻企业、员工、专利等），这些由季度数据汇总生成
        parkInfo.setEnterpriseCount(existPark.getEnterpriseCount());
        parkInfo.setAboveScaleCount(existPark.getAboveScaleCount());
        parkInfo.setHighTechCount(existPark.getHighTechCount());
        parkInfo.setTechSmeCount(existPark.getTechSmeCount());
        parkInfo.setListedCount(existPark.getListedCount());
        parkInfo.setHiddenChampionCount(existPark.getHiddenChampionCount());
        parkInfo.setNationalSpecializedCount(existPark.getNationalSpecializedCount());
        parkInfo.setProvincialSpecializedCount(existPark.getProvincialSpecializedCount());
        parkInfo.setInnovativeSmeCount(existPark.getInnovativeSmeCount());
        parkInfo.setEmployeeCount(existPark.getEmployeeCount());
        parkInfo.setNational1000TalentCount(existPark.getNational1000TalentCount());
        parkInfo.setProvincial1000TalentCount(existPark.getProvincial1000TalentCount());
        parkInfo.setMasterAndAboveCount(existPark.getMasterAndAboveCount());
        parkInfo.setSeniorEngineerCount(existPark.getSeniorEngineerCount());
        parkInfo.setSenior2EngineerCount(existPark.getSenior2EngineerCount());
        parkInfo.setSeniorTechnicianCount(existPark.getSeniorTechnicianCount());
        parkInfo.setMasterCount(existPark.getMasterCount());
        parkInfo.setPatentTotalCount(existPark.getPatentTotalCount());
        parkInfo.setInventionCount(existPark.getInventionCount());
        parkInfo.setUtilityModelCount(existPark.getUtilityModelCount());
        parkInfo.setAppearanceCount(existPark.getAppearanceCount());

        log.info("修改园区：ID={}, 名称={}", parkInfo.getId(), parkInfo.getParkName());
        parkMapper.updateById(parkInfo);
    }

    /**
     * 删除园区
     *
     * @param id 园区ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePark(Long id) {
        // 检查园区是否存在
        getParkById(id);

        log.info("删除园区：ID={}", id);
        parkMapper.deleteById(id);
    }

    /**
     * 检查园区名称是否重复
     *
     * @param parkName  园区名称
     * @param excludeId 排除的园区ID（修改时使用，新增时为 null）
     */
    private void checkParkNameDuplicate(String parkName, Long excludeId) {
        if (!StringUtils.hasText(parkName)) {
            return;
        }
        LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParkInfo::getParkName, parkName);
        if (excludeId != null) {
            queryWrapper.ne(ParkInfo::getId, excludeId);
        }
        Long count = parkMapper.selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区名称已存在");
        }
    }

    /**
     * 批量删除园区
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeletePark(java.util.List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "请选择要删除的园区");
        }
        parkMapper.deleteBatchIds(ids);
        log.info("批量删除园区：数量={}", ids.size());
    }

    /**
     * 批量导入园区（Excel）
     */
    @Transactional(rollbackFor = Exception.class)
    public java.util.Map<String, Object> importParks(org.springframework.web.multipart.MultipartFile file) {
        try {
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            int success = 0;
            int fail = 0;
            java.util.List<String> errors = new java.util.ArrayList<>();

            // 使用 EasyExcel 解析 Excel 文件
            java.util.List<ParkImportTemplate> rows = com.alibaba.excel.EasyExcel.read(file.getInputStream())
                    .head(ParkImportTemplate.class)
                    .sheet()
                    .doReadSync();

            if (rows == null || rows.isEmpty()) {
                throw new BusinessException(ResultCode.SERVER_ERROR, "Excel 文件中没有数据");
            }

            for (int i = 0; i < rows.size(); i++) {
                ParkImportTemplate template = rows.get(i);
                int rowNum = i + 2; // Excel行号从2开始（第1行是表头）

                try {
                    // 必填字段校验
                    if (!StringUtils.hasText(template.getParkName())) {
                        errors.add("第" + rowNum + "行：园区名称不能为空");
                        fail++;
                        continue;
                    }

                    // 转换为 ParkInfo 实体
                    ParkInfo park = convertTemplateToPark(template);
                    
                    // 设置默认年度为当前年
                    if (park.getYear() == null) {
                        park.setYear(java.time.LocalDate.now().getYear());
                    }

                    // 保存到数据库
                    parkMapper.insert(park);
                    success++;
                } catch (Exception e) {
                    fail++;
                    errors.add("第" + rowNum + "行：" + e.getMessage());
                    log.error("导入第{}行失败", rowNum, e);
                }
            }

            result.put("success", success);
            result.put("fail", fail);
            result.put("errors", errors);
            log.info("批量导入园区：成功={}, 失败={}", success, fail);
            return result;
        } catch (Exception e) {
            log.error("批量导入园区失败", e);
            throw new BusinessException(ResultCode.SERVER_ERROR, "导入失败：" + e.getMessage());
        }
    }

    /**
     * 将模板 DTO 转换为 ParkInfo 实体
     */
    private ParkInfo convertTemplateToPark(ParkImportTemplate template) {
        ParkInfo park = new ParkInfo();
        
        // ========== 基本信息 ==========
        park.setParkName(template.getParkName());
        park.setParkCode(template.getParkCode());
        park.setParkType(template.getParkType());
        park.setDistrictName(template.getDistrictName());
        park.setParkStatus(template.getParkStatus());
        park.setRecognition(template.getRecognition());
        park.setStarLevel(template.getStarLevel());
        park.setDevMode(template.getDevMode());
        park.setLandSource(template.getLandSource());
        park.setLandNature(template.getLandNature());
        park.setLeadingIndustry(template.getLeadingIndustry());
        park.setIsUpgradable(template.getIsUpgradable());
        park.setUpgradeContent(template.getUpgradeContent());
        park.setAddress(template.getAddress());
        park.setYear(template.getYear());
        
        // ========== 联系方式 ==========
        park.setOperatorUnit(template.getOperatorUnit());
        park.setOperatorNature(template.getOperatorNature());
        park.setPersonInCharge(template.getPersonInCharge());
        park.setInChargePhone(template.getInChargePhone());
        park.setContactPerson(template.getContactPerson());
        park.setContactPhone(template.getContactPhone());
        
        // ========== 园区面积 ==========
        park.setLandArea(template.getLandArea());
        park.setBuildArea(template.getBuildArea());
        park.setLeasedArea(template.getLeasedArea());
        park.setRemainingLeasableArea(template.getRemainingLeasableArea());
        park.setRemainingSellableArea(template.getRemainingSellableArea());
        
        // ========== 入驻企业统计 ==========
        park.setEnterpriseCount(template.getEnterpriseCount());
        park.setAboveScaleCount(template.getAboveScaleCount());
        park.setHighTechCount(template.getHighTechCount());
        park.setTechSmeCount(template.getTechSmeCount());
        park.setHiddenChampionCount(template.getHiddenChampionCount());
        park.setNationalSpecializedCount(template.getNationalSpecializedCount());
        park.setInnovativeSmeCount(template.getInnovativeSmeCount());
        park.setProvincialSpecializedCount(template.getProvincialSpecializedCount());
        
        // ========== 人才统计 ==========
        park.setEmployeeCount(template.getEmployeeCount());
        park.setNational1000TalentCount(template.getNational1000TalentCount());
        park.setProvincial1000TalentCount(template.getProvincial1000TalentCount());
        park.setSeniorEngineerCount(template.getSeniorEngineerCount());
        park.setSenior2EngineerCount(template.getSenior2EngineerCount());
        park.setSeniorTechnicianCount(template.getSeniorTechnicianCount());
        park.setMasterAndAboveCount(template.getMasterAndAboveCount());
        park.setMasterCount(template.getMasterCount());
        
        // ========== 专利统计 ==========
        park.setPatentTotalCount(template.getPatentTotalCount());
        park.setInventionCount(template.getInventionCount());
        park.setUtilityModelCount(template.getUtilityModelCount());
        park.setAppearanceCount(template.getAppearanceCount());
        
        // ========== 园区简介 ==========
        park.setIntroduction(template.getIntroduction());
        park.setPublicFacilities(template.getPublicFacilities());
        park.setPublicServices(template.getPublicServices());
        
        return park;
    }

    /**
     * 下载园区导入模板
     */
    public void downloadTemplate(javax.servlet.http.HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode("园区导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 使用 EasyExcel 生成模板
            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), ParkImportTemplate.class)
                    .sheet("园区导入")
                    .doWrite(new java.util.ArrayList<>());
        } catch (Exception e) {
            log.error("下载模板失败", e);
            throw new BusinessException(ResultCode.SERVER_ERROR, "下载模板失败");
        }
    }

    /**
     * 导出园区列表
     */
    public void exportParks(ParkQueryDTO queryDTO, javax.servlet.http.HttpServletResponse response) {
        try {
            // 查询所有数据（不分页）
            LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();
            
            // 园区名称 模糊查询
            if (StringUtils.hasText(queryDTO.getParkName())) {
                queryWrapper.like(ParkInfo::getParkName, queryDTO.getParkName());
            }
            
            // 所属区域 模糊查询
            if (StringUtils.hasText(queryDTO.getDistrictName())) {
                queryWrapper.like(ParkInfo::getDistrictName, queryDTO.getDistrictName());
            }
            
            // 园区类型 精确匹配
            if (StringUtils.hasText(queryDTO.getParkType())) {
                queryWrapper.eq(ParkInfo::getParkType, queryDTO.getParkType());
            }
            
            // 星级 精确匹配
            queryWrapper.eq(queryDTO.getStarLevel() != null,
                    ParkInfo::getStarLevel, queryDTO.getStarLevel());
            
            // 年度 精确匹配
            queryWrapper.eq(queryDTO.getYear() != null,
                    ParkInfo::getYear, queryDTO.getYear());
            
            // 按创建时间降序
            queryWrapper.orderByDesc(ParkInfo::getCreateTime);
            
            java.util.List<ParkInfo> list = parkMapper.selectList(queryWrapper);
            
            // 转换为导出 DTO
            java.util.List<ParkExportDTO> exportList = new java.util.ArrayList<>();
            int rowNum = 1;
            for (ParkInfo park : list) {
                ParkExportDTO dto = new ParkExportDTO();
                dto.setRowNum(rowNum++);
                dto.setParkName(park.getParkName());
                dto.setYear(park.getYear());
                dto.setParkCode(park.getParkCode());
                dto.setDistrictName(park.getDistrictName());
                dto.setLeadingIndustry(park.getLeadingIndustry());
                dto.setRecognition(park.getRecognition());
                // 星级评定：将数字转换为中文
                if (park.getStarLevel() != null) {
                    dto.setStarRating(park.getStarLevel() + "星级");
                }
                dto.setParkStatus(park.getParkStatus());
                dto.setParkType(park.getParkType());
                dto.setDevMode(park.getDevMode());
                dto.setLandSource(park.getLandSource());
                dto.setLandNature(park.getLandNature());
                dto.setAddress(park.getAddress());
                exportList.add(dto);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode("园区列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 使用 EasyExcel 生成真正的 Excel 文件
            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), ParkExportDTO.class)
                    .autoCloseStream(true)
                    .sheet("园区列表")
                    .doWrite(exportList);
                    
            log.info("导出园区列表成功，共 {} 条记录", exportList.size());
        } catch (Exception e) {
            log.error("导出园区列表失败", e);
            throw new BusinessException(ResultCode.SERVER_ERROR, "导出失败");
        }
    }

    /**
     * 获取园区主要产业（企业数量前三的产业）
     * 用于园区详情页展示
     *
     * @param parkId 园区ID
     * @return 主要产业列表（最多3个）
     */
    public java.util.List<com.park.park.dto.ParkIndustryStatDTO> getTopIndustries(Long parkId) {
        // 检查园区是否存在
        getParkById(parkId);

        // 查询该园区下所有企业的产业分布
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.park.enterprise.entity.EnterpriseInfo> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(com.park.enterprise.entity.EnterpriseInfo::getParkId, parkId)
               .isNotNull(com.park.enterprise.entity.EnterpriseInfo::getIndustryName)
               .ne(com.park.enterprise.entity.EnterpriseInfo::getIndustryName, "");

        java.util.List<com.park.enterprise.entity.EnterpriseInfo> enterprises = enterpriseMapper.selectList(wrapper);

        // 统计每个产业的企业数量
        java.util.Map<String, Long> industryCountMap = enterprises.stream()
                .filter(e -> e.getIndustryName() != null && !e.getIndustryName().trim().isEmpty())
                .collect(java.util.stream.Collectors.groupingBy(
                        com.park.enterprise.entity.EnterpriseInfo::getIndustryName,
                        java.util.stream.Collectors.counting()
                ));

        // 按企业数量降序排序，取前三
        java.util.List<com.park.park.dto.ParkIndustryStatDTO> topIndustries = industryCountMap.entrySet().stream()
                .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(entry -> com.park.park.dto.ParkIndustryStatDTO.builder()
                        .industryName(entry.getKey())
                        .enterpriseCount(entry.getValue().intValue())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        // 设置排名
        for (int i = 0; i < topIndustries.size(); i++) {
            topIndustries.get(i).setRank(i + 1);
        }

        log.info("获取园区主要产业：园区ID={}, 产业数量={}", parkId, topIndustries.size());
        return topIndustries;
    }
}
