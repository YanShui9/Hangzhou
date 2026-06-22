package com.park.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.system.dto.request.DistrictAccountDTO;
import com.park.system.dto.request.EnterpriseInfoDTO;
import com.park.system.dto.request.ParkAccountDTO;
import com.park.system.dto.request.SystemDataDTO;
import com.park.system.entity.DistrictAccount;
import com.park.system.entity.EnterpriseInfo;
import com.park.system.entity.ParkAccount;
import com.park.system.entity.SystemData;
import com.park.system.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统服务实现类
 *
 * @author park-team
 */
@Slf4j
@Service
public class SystemServiceImpl implements SystemService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 模拟区县账号数据
    private List<DistrictAccount> mockDistrictAccounts = new ArrayList<>();
    // 模拟园区账号数据
    private List<ParkAccount> mockParkAccounts = new ArrayList<>();
    // 模拟数据仓库数据
    private List<SystemData> mockSystemData = new ArrayList<>();
    // 模拟企业信息数据
    private List<EnterpriseInfo> mockEnterpriseInfo = new ArrayList<>();

    private Long districtAccountIdCounter = 1L;
    private Long parkAccountIdCounter = 1L;
    private Long systemDataIdCounter = 1L;
    private Long enterpriseInfoIdCounter = 1L;

    public SystemServiceImpl() {
        initMockData();
    }

    private void initMockData() {
        // 初始化区县账号模拟数据
        mockDistrictAccounts.add(new DistrictAccount(1L, "张三", "13800138001", "滨江区", LocalDateTime.parse("2024-01-15 10:30:00", FORMATTER)));
        mockDistrictAccounts.add(new DistrictAccount(2L, "李四", "13800138002", "萧山区", LocalDateTime.parse("2024-01-16 14:20:00", FORMATTER)));
        mockDistrictAccounts.add(new DistrictAccount(3L, "王五", "13800138003", "西湖区", LocalDateTime.parse("2024-01-17 09:15:00", FORMATTER)));
        mockDistrictAccounts.add(new DistrictAccount(4L, "赵六", "13800138004", "余杭区", LocalDateTime.parse("2024-01-18 11:45:00", FORMATTER)));
        mockDistrictAccounts.add(new DistrictAccount(5L, "孙七", "13800138005", "富阳区", LocalDateTime.parse("2024-01-19 15:30:00", FORMATTER)));
        districtAccountIdCounter = 6L;

        // 初始化园区账号模拟数据
        mockParkAccounts.add(new ParkAccount(1L, "杭州怡明医疗器械股份有限公司", "913301001695775M", "滨江区", "万轮科技园", LocalDateTime.parse("2024-01-10 08:00:00", FORMATTER)));
        mockParkAccounts.add(new ParkAccount(2L, "杭州艾名医学科技有限公司", "91330108MA2JG5G55D", "滨江区", "传化科创园", LocalDateTime.parse("2024-01-11 09:30:00", FORMATTER)));
        mockParkAccounts.add(new ParkAccount(3L, "杭州环特生物科技股份有限公司", "9133010556612982F", "钱塘区", "和达药谷中心", LocalDateTime.parse("2024-01-12 10:00:00", FORMATTER)));
        mockParkAccounts.add(new ParkAccount(4L, "杭州禾睿康宇医药科技有限公司", "91330108MA2JG5GL9G", "萧山区", "颐高创业园", LocalDateTime.parse("2024-01-13 14:00:00", FORMATTER)));
        mockParkAccounts.add(new ParkAccount(5L, "杭州启明医疗器械股份有限公司", "9133010856673312C", "余杭区", "天和国际产业园", LocalDateTime.parse("2024-01-14 16:00:00", FORMATTER)));
        parkAccountIdCounter = 6L;

        // 初始化数据仓库模拟数据
        mockSystemData.add(new SystemData(1L, "2025年园区统计数据", "2025", "园区统计数据.xlsx"));
        mockSystemData.add(new SystemData(2L, "2025年企业年报数据", "2025", "企业年报数据.xlsx"));
        mockSystemData.add(new SystemData(3L, "2024年园区统计数据", "2024", "园区统计数据2024.xlsx"));
        mockSystemData.add(new SystemData(4L, "2024年企业年报数据", "2024", "企业年报数据2024.xlsx"));
        systemDataIdCounter = 5L;

        // 初始化企业信息模拟数据
        mockEnterpriseInfo.add(new EnterpriseInfo(1L, "杭州怡明医疗器械股份有限公司", "913301001695775M", "滨江区", "万轮科技园", "浙江省杭州市滨江区江南大道699号", "生物医药", "在园", "2022-06-30", "张明", "李华", "13800138010", "5000万", "2018-01-15", "园区环境良好，服务周到"));
        mockEnterpriseInfo.add(new EnterpriseInfo(2L, "杭州艾名医学科技有限公司", "91330108MA2JG5G55D", "滨江区", "传化科创园", "浙江省杭州市滨江区西兴街道江陵路88号", "生物医药", "在园", "2022-07-15", "王芳", "赵强", "13800138011", "3000万", "2019-03-20", "入驻以来发展良好"));
        mockEnterpriseInfo.add(new EnterpriseInfo(3L, "杭州环特生物科技股份有限公司", "9133010556612982F", "钱塘区", "和达药谷中心", "浙江省杭州市钱塘区江潮路88号", "生物医药", "在园", "2022-08-01", "刘洋", "陈静", "13800138012", "8000万", "2017-06-10", "创新能力强"));
        mockEnterpriseInfo.add(new EnterpriseInfo(4L, "杭州禾睿康宇医药科技有限公司", "91330108MA2JG5GL9G", "萧山区", "颐高创业园", "浙江省杭州市萧山区金城路185号", "生物医药", "在园", "2022-09-10", "周杰", "吴丽", "13800138013", "2000万", "2020-04-15", "发展潜力大"));
        mockEnterpriseInfo.add(new EnterpriseInfo(5L, "杭州启明医疗器械股份有限公司", "9133010856673312C", "余杭区", "天和国际产业园", "浙江省杭州市余杭区文一西路128号", "医疗器械", "在园", "2022-10-01", "郑凯", "孙燕", "13800138014", "1亿", "2016-08-20", "行业领军企业"));
        mockEnterpriseInfo.add(new EnterpriseInfo(6L, "杭州腾品科技有限公司", "9133010856673315E", "富阳区", "银海科创中心", "浙江省杭州市富阳区富春街道恩波大道88号", "新材料", "在园", "2022-11-15", "黄涛", "林静", "13800138015", "1500万", "2021-02-28", "技术创新型企业"));
        enterpriseInfoIdCounter = 7L;
    }

    @Override
    public IPage<DistrictAccount> getDistrictAccountPage(Integer pageNum, Integer pageSize, String name, String phone, String district) {
        List<DistrictAccount> filtered = mockDistrictAccounts.stream()
                .filter(account -> (name == null || name.isEmpty() || account.getName().contains(name)))
                .filter(account -> (phone == null || phone.isEmpty() || account.getPhone().contains(phone)))
                .filter(account -> (district == null || district.isEmpty() || account.getDistrict().equals(district)))
                .collect(Collectors.toList());

        return createPage(filtered, pageNum, pageSize);
    }

    @Override
    public void saveDistrictAccount(DistrictAccountDTO dto) {
        if (dto.getId() != null) {
            mockDistrictAccounts.removeIf(a -> a.getId().equals(dto.getId()));
        } else {
            dto.setId(districtAccountIdCounter++);
        }
        mockDistrictAccounts.add(new DistrictAccount(
                dto.getId(),
                dto.getName(),
                dto.getPhone(),
                dto.getDistrict(),
                LocalDateTime.now()
        ));
    }

    @Override
    public void deleteDistrictAccount(Long id) {
        mockDistrictAccounts.removeIf(a -> a.getId().equals(id));
    }

    @Override
    public void resetDistrictAccountPwd(Long id) {
        // 密码重置逻辑
    }

    @Override
    public IPage<ParkAccount> getParkAccountPage(Integer pageNum, Integer pageSize, String companyName, String unifiedCode, String parkName, String district) {
        List<ParkAccount> filtered = mockParkAccounts.stream()
                .filter(account -> (companyName == null || companyName.isEmpty() || account.getCompanyName().contains(companyName)))
                .filter(account -> (unifiedCode == null || unifiedCode.isEmpty() || account.getUnifiedCode().contains(unifiedCode)))
                .filter(account -> (parkName == null || parkName.isEmpty() || account.getParkName().contains(parkName)))
                .filter(account -> (district == null || district.isEmpty() || account.getDistrict().equals(district)))
                .collect(Collectors.toList());

        return createPage(filtered, pageNum, pageSize);
    }

    @Override
    public void saveParkAccount(ParkAccountDTO dto) {
        if (dto.getId() != null) {
            mockParkAccounts.removeIf(a -> a.getId().equals(dto.getId()));
        } else {
            dto.setId(parkAccountIdCounter++);
        }
        mockParkAccounts.add(new ParkAccount(
                dto.getId(),
                dto.getCompanyName(),
                dto.getUnifiedCode(),
                dto.getDistrict(),
                dto.getParkName(),
                LocalDateTime.now()
        ));
    }

    @Override
    public void deleteParkAccount(Long id) {
        mockParkAccounts.removeIf(a -> a.getId().equals(id));
    }

    @Override
    public IPage<SystemData> getDataWarehousePage(Integer pageNum, Integer pageSize, String dataName, String year) {
        List<SystemData> filtered = mockSystemData.stream()
                .filter(data -> (dataName == null || dataName.isEmpty() || data.getDataName().contains(dataName)))
                .filter(data -> (year == null || year.isEmpty() || data.getYear().equals(year)))
                .collect(Collectors.toList());

        return createPage(filtered, pageNum, pageSize);
    }

    @Override
    public void saveDataWarehouse(SystemDataDTO dto) {
        if (dto.getId() != null) {
            mockSystemData.removeIf(d -> d.getId().equals(dto.getId()));
        } else {
            dto.setId(systemDataIdCounter++);
        }
        mockSystemData.add(new SystemData(
                dto.getId(),
                dto.getDataName(),
                dto.getYear(),
                dto.getAttachment()
        ));
    }

    @Override
    public void deleteDataWarehouse(Long id) {
        mockSystemData.removeIf(d -> d.getId().equals(id));
    }

    @Override
    public IPage<EnterpriseInfo> getEnterpriseInfoPage(Integer pageNum, Integer pageSize, String keyword, String district, String parkId, String status) {
        List<EnterpriseInfo> filtered = mockEnterpriseInfo.stream()
                .filter(e -> (keyword == null || keyword.isEmpty() || e.getEnterpriseName().contains(keyword) || e.getUnifiedCode().contains(keyword)))
                .filter(e -> (district == null || district.isEmpty() || e.getDistrict().equals(district)))
                .filter(e -> (parkId == null || parkId.isEmpty() || true))
                .filter(e -> (status == null || status.isEmpty() || e.getStatus().equals(status)))
                .collect(Collectors.toList());

        return createPage(filtered, pageNum, pageSize);
    }

    @Override
    public EnterpriseInfo getEnterpriseInfoById(Long id) {
        return mockEnterpriseInfo.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void saveEnterpriseInfo(EnterpriseInfoDTO dto) {
        if (dto.getId() != null) {
            mockEnterpriseInfo.removeIf(e -> e.getId().equals(dto.getId()));
        } else {
            dto.setId(enterpriseInfoIdCounter++);
        }
        mockEnterpriseInfo.add(new EnterpriseInfo(
                dto.getId(),
                dto.getEnterpriseName(),
                dto.getUnifiedCode(),
                dto.getDistrict(),
                dto.getParkName(),
                dto.getAddress(),
                dto.getIndustry(),
                dto.getStatus(),
                dto.getEnterTime(),
                dto.getLegalPerson(),
                dto.getContactName(),
                dto.getContactPhone(),
                dto.getRegisteredCapital(),
                dto.getRegisterDate(),
                dto.getParkEvaluation()
        ));
    }

    private <T> IPage<T> createPage(List<T> list, Integer pageNum, Integer pageSize) {
        Page<T> page = new Page<>(pageNum, pageSize);
        int total = list.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<T> pageContent = start < total ? list.subList(start, end) : new ArrayList<>();
        
        page.setRecords(pageContent);
        page.setTotal(total);
        return page;
    }
}