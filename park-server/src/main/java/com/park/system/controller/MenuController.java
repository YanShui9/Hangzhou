package com.park.system.controller;

import com.park.common.result.R;
import com.park.common.util.JwtUtil;
import com.park.system.entity.MenuVO;
import com.park.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 菜单控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Api(tags = "菜单管理")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    /**
     * 获取当前用户菜单
     *
     * @param request HTTP请求
     * @return 菜单列表
     */
    @GetMapping("/menus")
    @ApiOperation(value = "获取当前用户菜单", notes = "根据当前登录用户的角色返回对应的菜单列表")
    public R<List<MenuVO>> getMenus(HttpServletRequest request) {
        // 从请求头中获取 Token
        String authHeader = request.getHeader(tokenHeader);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(tokenPrefix)) {
            log.warn("获取菜单失败：未提供有效的Token");
            return R.fail("未认证，请先登录");
        }

        // 解析 Token 获取角色类型
        String token = authHeader.substring(tokenPrefix.length());
        String roleStr = jwtUtil.getRoleFromToken(token);

        if (!StringUtils.hasText(roleStr)) {
            log.warn("获取菜单失败：Token中未包含角色信息");
            return R.fail("用户角色信息缺失");
        }

        Integer roleType;
        try {
            roleType = Integer.valueOf(roleStr);
        } catch (NumberFormatException e) {
            log.warn("获取菜单失败：角色类型格式错误 - {}", roleStr);
            return R.fail("用户角色信息格式错误");
        }

        log.info("获取菜单，用户角色类型: {}", roleType);

        // 根据角色类型获取菜单
        List<MenuVO> menus = menuService.getMenusByRoleType(roleType);

        return R.ok(menus);
    }
}
