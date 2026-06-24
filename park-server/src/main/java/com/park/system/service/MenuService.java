package com.park.system.service;

import com.park.system.entity.MenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 菜单服务
 * 根据设计文档，菜单按角色类型返回：
 * - 1=市级管理员: 数据驾驶舱、园区列表、入驻企业、评价审核（终审）、评价结果、系统设置
 * - 2=区县管理员: 数据看板、园区列表、入驻企业、评价审核（初审）、评价结果
 * - 3=园区管理员: 数据看板、我的园区、入驻企业、评价列表、评价结果
 *
 * @author park-team
 */
@Slf4j
@Service
public class MenuService {

    /**
     * 根据角色类型获取菜单列表
     *
     * @param roleType 角色类型：1=市级, 2=区县, 3=园区
     * @return 菜单列表
     */
    public List<MenuVO> getMenusByRoleType(Integer roleType) {
        if (roleType == null) {
            log.warn("角色类型为空，返回空菜单列表");
            return new ArrayList<>();
        }

        switch (roleType) {
            case 1:
                return getCityAdminMenus();
            case 2:
                return getDistrictAdminMenus();
            case 3:
                return getParkAdminMenus();
            default:
                log.warn("未知角色类型: {}，返回空菜单列表", roleType);
                return new ArrayList<>();
        }
    }

    /**
     * 市级管理员菜单
     * 数据驾驶舱、园区列表、入驻企业、评价审核（终审）、评价结果、系统设置
     */
    private List<MenuVO> getCityAdminMenus() {
        return Arrays.asList(
                new MenuVO(1L, "数据驾驶舱", "/dashboard", "el-icon-data-line", null),
                new MenuVO(2L, "园区列表", "/park/list", "el-icon-office-building", null),
                new MenuVO(3L, "入驻企业", "/enterprise/list", "el-icon-s-shop", null),
                new MenuVO(4L, "评价审核", "/audit/list", "el-icon-s-check", null),
                new MenuVO(5L, "评价结果", "/result/list", "el-icon-s-data", null),
                new MenuVO(6L, "系统设置", "/system/settings", "el-icon-setting", null)
        );
    }

    /**
     * 区县管理员菜单
     * 数据看板、园区列表、入驻企业、评价审核（初审）、评价结果
     */
    private List<MenuVO> getDistrictAdminMenus() {
        return Arrays.asList(
                new MenuVO(1L, "数据看板", "/dashboard", "el-icon-data-line", null),
                new MenuVO(2L, "园区列表", "/park/list", "el-icon-office-building", null),
                new MenuVO(3L, "入驻企业", "/enterprise/list", "el-icon-s-shop", null),
                new MenuVO(4L, "评价审核", "/audit/list", "el-icon-s-check", null),
                new MenuVO(5L, "评价结果", "/result/list", "el-icon-s-data", null)
        );
    }

    /**
     * 园区管理员菜单
     * 数据看板、我的园区、入驻企业、评价列表、评价结果
     */
    private List<MenuVO> getParkAdminMenus() {
        return Arrays.asList(
                new MenuVO(1L, "数据看板", "/dashboard", "el-icon-data-line", null),
                new MenuVO(2L, "我的园区", "/park/mine", "el-icon-office-building", null),
                new MenuVO(3L, "入驻企业", "/enterprise/list", "el-icon-s-shop", null),
                new MenuVO(4L, "评价列表", "/evaluation/list", "el-icon-edit-outline", null),
                new MenuVO(5L, "评价结果", "/result/list", "el-icon-s-data", null)
        );
    }
}
