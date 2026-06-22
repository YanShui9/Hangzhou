package com.park.park.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.park.dto.ParkQueryDTO;
import com.park.park.dto.ParkSaveDTO;
import com.park.park.dto.ParkStatsDTO;
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

    /**
     * 分页查询园区列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<ParkInfo> getParkPage(ParkQueryDTO queryDTO) {
        // 构建分页对象
        Page<ParkInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getId() != null, ParkInfo::getId, queryDTO.getId());
        queryWrapper.like(StringUtils.hasText(queryDTO.getParkName()), ParkInfo::getParkName, queryDTO.getParkName());
        queryWrapper.eq(StringUtils.hasText(queryDTO.getDistrictName()), ParkInfo::getDistrictName, queryDTO.getDistrictName());
        queryWrapper.eq(queryDTO.getParkType() != null, ParkInfo::getParkType, queryDTO.getParkType());
        queryWrapper.eq(queryDTO.getStarLevel() != null, ParkInfo::getStarLevel, queryDTO.getStarLevel());
        queryWrapper.orderByDesc(ParkInfo::getCreateTime);

        IPage<ParkInfo> result = parkMapper.selectPage(page, queryWrapper);
        
        // 如果数据库没有数据，返回模拟数据
        if (result.getTotal() == 0) {
            return getMockParkPage(queryDTO);
        }
        
        return result;
    }

    /**
     * 获取模拟园区数据（用于演示）
     *
     * @param queryDTO 查询条件
     * @return 模拟分页结果
     */
    private IPage<ParkInfo> getMockParkPage(ParkQueryDTO queryDTO) {
        Page<ParkInfo> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        java.util.List<ParkInfo> mockData = new java.util.ArrayList<>();
        
        mockData.add(createMockPark(1L, "紫金港生命健康产业园", "西湖区", 
            "西湖区三墩镇金蓬街368号", "生产性制造类", 1, "已投运",
            "政府主导开发", "租用", "工业用地", "省级小微企业园", "是", 
            "外立面整治、园区指挥系统接入", "生物医药", 
            "紫金港生命健康产业园位于杭州市西湖区，总占地面积40亩，建筑面积35043平方米。园区以生命健康产业为主导，集聚了一批生物医药、医疗器械、健康服务等领域的优质企业，致力于打造成为杭州乃至长三角地区重要的生命健康产业创新基地。",
            "杭州紫金港科技城建设投资有限公司", "国有", "郑建", "15988115758", "沈烨琳", "15858172079",
            40, 35043, 25395.37, 3250, "-",
            15, 0, 1, 0, 0, 0, 0, 0,
            350, 1, 1, 44, 1, 7, 5, 0,
            48, 21, 8, 9,
            "宿舍公寓,食堂餐饮,展览展示场馆,便利超市,会议场馆,物流,光纤宽带",
            "物业管理服务,政策与信息服务,投融资服务,信息化服务",
            3));
        
        mockData.add(createMockPark(2L, "杭州智慧信息产业园", "滨江区", 
            "滨江区江南大道100号", "生产性服务类", 2, "已投运",
            "企业自主开发", "出让", "商业用地", "市级小微企业园", "否", 
            "-", "信息技术", 
            "杭州智慧信息产业园坐落于滨江区核心区域，总建筑面积5万平方米。园区聚焦数字经济、人工智能、云计算等前沿领域，构建了完善的产业生态体系，为入驻企业提供全方位的科技创新服务支持。",
            "杭州智慧信息产业发展有限公司", "民营", "王建国", "13800138000", "李娜", "13900139000",
            30, 50000, 42000, 8000, 0,
            32, 5, 8, 15, 2, 1, 3, 4,
            850, 3, 5, 120, 8, 35, 20, 45,
            280, 95, 120, 65,
            "办公场地,会议室,停车场,餐饮配套,健身房,咖啡厅",
            "创业孵化,技术咨询,人才招聘,市场推广,法务咨询,知识产权服务",
            4));
        
        mockData.add(createMockPark(3L, "萧山智能制造产业园", "萧山区", 
            "萧山区经济技术开发区", "生产性制造类", 1, "已投运",
            "政企合作", "出让", "工业用地", "国家级小微企业园", "是", 
            "智能化改造、生产线升级", "智能制造", 
            "萧山智能制造产业园是萧山区重点打造的高端制造产业平台，规划面积100亩，已建成标准化厂房8万平方米。园区以智能制造、精密机械、新能源装备为核心产业方向，配备完善的生产配套设施。",
            "萧山智能制造产业发展有限公司", "国有", "张伟", "13700137000", "陈静", "13600136000",
            100, 80000, 72000, 8000, "-",
            45, 8, 12, 20, 3, 2, 5, 6,
            1200, 5, 8, 200, 15, 60, 35, 80,
            560, 180, 260, 120,
            "标准厂房,仓库,物流中心,检测中心,员工宿舍,食堂",
            "技术研发,质量检测,供应链管理,设备维护,培训服务,金融服务",
            5));
        
        mockData.add(createMockPark(4L, "余杭数字创意产业园", "余杭区", 
            "余杭区梦想小镇", "生产性服务类", 2, "已投运",
            "政府主导开发", "划拨", "商业用地", "省级小微企业园", "否", 
            "-", "数字创意", 
            "余杭数字创意产业园位于梦想小镇核心区，总建筑面积3.5万平方米。园区专注于数字内容创作、游戏开发、影视制作等数字创意产业，汇聚了一批年轻有活力的创业团队和创新企业。",
            "杭州梦想小镇投资发展有限公司", "国有", "刘洋", "13500135000", "赵雪", "13400134000",
            25, 35000, 30000, 5000, 0,
            28, 3, 6, 12, 1, 0, 4, 2,
            680, 2, 4, 85, 6, 25, 15, 30,
            180, 45, 80, 55,
            "创意办公,演播厅,录音室,摄影棚,会议室,休闲区",
            "创意孵化,项目路演,投融资对接,版权服务,营销推广,人才培训",
            4));
        
        mockData.add(createMockPark(5L, "富阳新材料产业园", "富阳区", 
            "富阳区银湖街道", "生产性制造类", 1, "在建",
            "企业自主开发", "出让", "工业用地", "-", "否", 
            "-", "新材料", 
            "富阳新材料产业园是富阳区重点培育的新兴产业园区，规划面积120亩，计划建设高标准厂房10万平方米。园区将聚焦先进复合材料、新能源材料、生物基材料等领域，打造绿色环保的新材料产业集聚平台。",
            "富阳新材料产业投资有限公司", "民营", "陈明", "13300133000", "周芳", "13200132000",
            120, 0, 0, 100000, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0,
            "-",
            "-",
            null));
        
        mockData.add(createMockPark(6L, "临安科创孵化园", "临安区", 
            "临安区锦南新城", "生产性服务类", 2, "已投运",
            "政府主导开发", "划拨", "科研用地", "市级小微企业园", "是", 
            "孵化器升级改造", "科技孵化", 
            "临安科创孵化园是临安区科技创新的重要载体，建筑面积2万平方米。园区致力于为初创科技企业提供全方位的孵化服务，包括办公场地、技术支持、投融资对接等，已成功孵化多家高新技术企业。",
            "临安科技创新服务中心", "国有", "吴强", "13100131000", "郑梅", "13000130000",
            15, 20000, 18000, 2000, 0,
            22, 2, 5, 8, 1, 0, 3, 1,
            450, 1, 3, 55, 4, 18, 12, 20,
            120, 30, 55, 35,
            "孵化空间,众创空间,实验室,会议室,培训室,展示厅",
            "创业孵化,技术转移,知识产权,科技咨询,政策申报,创业导师",
            3));

        // 过滤数据
        java.util.List<ParkInfo> filteredData = mockData.stream()
            .filter(park -> {
                if (StringUtils.hasText(queryDTO.getParkName()) && 
                    !park.getParkName().contains(queryDTO.getParkName())) {
                    return false;
                }
                if (StringUtils.hasText(queryDTO.getDistrictName()) && 
                    !park.getDistrictName().equals(queryDTO.getDistrictName())) {
                    return false;
                }
                if (queryDTO.getParkType() != null && 
                    !queryDTO.getParkType().equals(park.getParkType())) {
                    return false;
                }
                if (queryDTO.getStarLevel() != null && 
                    !queryDTO.getStarLevel().equals(park.getStarLevel())) {
                    return false;
                }
                return true;
            })
            .collect(java.util.stream.Collectors.toList());

        // 分页处理
        int start = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        int end = Math.min(start + queryDTO.getPageSize(), filteredData.size());
        java.util.List<ParkInfo> pageData = start < filteredData.size() 
            ? filteredData.subList(start, end) 
            : new java.util.ArrayList<>();

        page.setRecords(pageData);
        page.setTotal(filteredData.size());
        page.setCurrent(queryDTO.getPageNum());
        page.setSize(queryDTO.getPageSize());
        
        return page;
    }

    /**
     * 创建模拟园区数据
     */
    private ParkInfo createMockPark(Long id, String parkName, String districtName, String address,
        String mainIndustry, Integer parkType, String parkStatus, String devMode, String landSource,
        String landNature, String recognition, String isUpgrade, String upgradeContent,
        String industry, String introduction, String operationOrgName, String operationOrgNature,
        String orgLeader, String orgLeaderPhone, String orgContact, String orgContactPhone,
        Integer landArea, Integer buildArea, Double rentedArea, Integer rentRemainArea, String saleRemainArea,
        Integer enterpriseCount, Integer aboveScaleCount, Integer highTechCount, Integer techSmeCount,
        Integer hiddenChampionCount, Integer nationalSrtiCount, Integer innovativeSmeCount, Integer provincialSrtiCount,
        Integer employeeCount, Integer nationalTalent, Integer provincialTalent, Integer masterAbove,
        Integer seniorEngineer, Integer engineer, Integer seniorTechnician, Integer masterDegree,
        Integer patentTotal, Integer patentInvention, Integer patentUtility, Integer patentDesign,
        String publicFacilities, String publicServices, Integer starLevel) {
        
        ParkInfo park = new ParkInfo();
        park.setId(id);
        park.setParkName(parkName);
        park.setDistrictName(districtName);
        park.setAddress(address);
        park.setMainIndustry(mainIndustry);
        park.setParkType(parkType);
        park.setParkStatus(parkStatus);
        park.setDevMode(devMode);
        park.setLandSource(landSource);
        park.setLandNature(landNature);
        park.setRecognition(recognition);
        park.setIsUpgrade(isUpgrade);
        park.setUpgradeContent(upgradeContent);
        park.setIntroduction(introduction);
        park.setOperationOrgName(operationOrgName);
        park.setOperationOrgNature(operationOrgNature);
        park.setOrgLeader(orgLeader);
        park.setOrgLeaderPhone(orgLeaderPhone);
        park.setOrgContact(orgContact);
        park.setOrgContactPhone(orgContactPhone);
        park.setLandArea(java.math.BigDecimal.valueOf(landArea));
        park.setBuildArea(java.math.BigDecimal.valueOf(buildArea));
        park.setRentedArea(java.math.BigDecimal.valueOf(rentedArea));
        park.setRentRemainArea(java.math.BigDecimal.valueOf(rentRemainArea));
        park.setSaleRemainArea(saleRemainArea.equals("-") ? null : java.math.BigDecimal.valueOf(Integer.parseInt(saleRemainArea)));
        park.setEnterpriseCount(enterpriseCount);
        park.setAboveScaleCount(aboveScaleCount);
        park.setHighTechCount(highTechCount);
        park.setTechSmeCount(techSmeCount);
        park.setHiddenChampionCount(hiddenChampionCount);
        park.setNationalSrtiCount(nationalSrtiCount);
        park.setInnovativeSmeCount(innovativeSmeCount);
        park.setProvincialSrtiCount(provincialSrtiCount);
        park.setEmployeeCount(employeeCount);
        park.setNationalTalent(nationalTalent);
        park.setProvincialTalent(provincialTalent);
        park.setMasterAbove(masterAbove);
        park.setSeniorEngineer(seniorEngineer);
        park.setEngineer(engineer);
        park.setSeniorTechnician(seniorTechnician);
        park.setMasterDegree(masterDegree);
        park.setPatentTotal(patentTotal);
        park.setPatentInvention(patentInvention);
        park.setPatentUtility(patentUtility);
        park.setPatentDesign(patentDesign);
        park.setPublicFacilities(publicFacilities);
        park.setPublicServices(publicServices);
        park.setStarLevel(starLevel);
        
        return park;
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
     * 新增园区
     *
     * @param saveDTO 园区信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void savePark(ParkSaveDTO saveDTO) {
        // 检查园区名称是否重复
        checkParkNameDuplicate(saveDTO.getParkName(), null);

        ParkInfo parkInfo = new ParkInfo();
        BeanUtils.copyProperties(saveDTO, parkInfo);

        log.info("新增园区：{}", parkInfo.getParkName());
        parkMapper.insert(parkInfo);
    }

    /**
     * 修改园区
     *
     * @param saveDTO 园区信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePark(ParkSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区ID不能为空");
        }

        // 检查园区是否存在
        getParkById(saveDTO.getId());

        // 检查园区名称是否重复（排除自身）
        checkParkNameDuplicate(saveDTO.getParkName(), saveDTO.getId());

        ParkInfo parkInfo = new ParkInfo();
        BeanUtils.copyProperties(saveDTO, parkInfo);

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
     * @param excludeId 排除的园区ID（修改时使用）
     */
    private void checkParkNameDuplicate(String parkName, Long excludeId) {
        LambdaQueryWrapper<ParkInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParkInfo::getParkName, parkName);
        if (excludeId != null) {
            queryWrapper.ne(ParkInfo::getId, excludeId);
        }
        Long count = parkMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区名称已存在");
        }
    }

    /**
     * 获取园区统计数据
     *
     * @param parkId 园区ID
     * @return 园区统计数据
     */
    public ParkStatsDTO getParkStats(Long parkId) {
        // 检查园区是否存在
        getParkById(parkId);

        // 返回模拟统计数据
        ParkStatsDTO statsDTO = new ParkStatsDTO();
        statsDTO.setEnterpriseCount(120);
        statsDTO.setLargeEnterpriseCount(15);
        statsDTO.setHighTechEnterpriseCount(28);
        statsDTO.setSmeCount(45);
        statsDTO.setInnovativeSmeCount(30);
        statsDTO.setSpecializedSmeCount(8);
        statsDTO.setEmployeeCount(3500);
        statsDTO.setNationalTalentCount(5);
        statsDTO.setProvincialTalentCount(12);
        statsDTO.setSeniorEngineerCount(85);
        statsDTO.setEngineerCount(240);
        statsDTO.setSeniorTechnicianCount(60);
        statsDTO.setMasterCount(150);
        statsDTO.setDoctorCount(35);
        statsDTO.setPatentCount(520);
        statsDTO.setInventionPatentCount(180);
        statsDTO.setUtilityModelCount(220);
        statsDTO.setDesignPatentCount(120);

        return statsDTO;
    }
}
