package com.park.system.controller;

import com.park.auth.entity.SysUser;
import com.park.auth.mapper.UserMapper;
import com.park.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化控制器
 * 用于初始化测试数据
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/init")
@Api(tags = "数据初始化")
public class InitController {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 初始化测试用户数据
     */
    @GetMapping("/users")
    @ApiOperation(value = "初始化测试用户", notes = "创建测试用户数据（密码均为123456）")
    public R<String> initUsers() {
        // 检查是否已存在用户
        long count = userMapper.selectCount(null);
        if (count > 0) {
            return R.ok("用户数据已存在，跳过初始化");
        }

        // 创建测试用户
        List<SysUser> users = Arrays.asList(
            createUser("admin", "市级管理员", "13800000001", 1, null, null),
            createUser("district01", "区县管理员", "13800000002", 2, 1L, null),
            createUser("park01", "园区管理员", "13800000003", 3, null, 1L)
        );

        users.forEach(userMapper::insert);
        log.info("成功初始化 {} 个测试用户", users.size());
        
        return R.ok("成功初始化测试用户数据");
    }

    private SysUser createUser(String username, String realName, String phone, int roleType, Long districtId, Long parkId) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRoleType(roleType);
        user.setDistrictId(districtId);
        user.setParkId(parkId);
        user.setStatus(1);
        return user;
    }
}