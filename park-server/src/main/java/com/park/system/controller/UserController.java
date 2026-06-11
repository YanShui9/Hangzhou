package com.park.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.auth.entity.SysUser;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.system.dto.UserQueryDTO;
import com.park.system.dto.UserSaveDTO;
import com.park.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户管理控制器（仅市级管理员可用）
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户列表（分页）
     */
    @GetMapping
    @ApiOperation(value = "用户列表", notes = "分页查询用户列表，支持按用户名、角色、区县、状态筛选")
    public R<PageResult<SysUser>> getUserPage(UserQueryDTO queryDTO, HttpServletRequest request) {
        checkCityAdmin(request);
        IPage<SysUser> page = userService.getUserPage(queryDTO);
        PageResult<SysUser> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 用户详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "用户详情", notes = "根据ID查询用户详情")
    public R<SysUser> getUserById(@PathVariable Long id, HttpServletRequest request) {
        checkCityAdmin(request);
        SysUser user = userService.getUserById(id);
        // 返回时清除密码
        user.setPassword(null);
        return R.ok(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @ApiOperation(value = "新增用户", notes = "新增系统用户，密码自动BCrypt加密")
    public R<Void> saveUser(@Valid @RequestBody UserSaveDTO dto, HttpServletRequest request) {
        checkCityAdmin(request);
        userService.saveUser(dto);
        return R.ok("新增用户成功", null);
    }

    /**
     * 修改用户
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改用户", notes = "修改用户信息，密码可选（不传则不更新密码）")
    public R<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserSaveDTO dto, HttpServletRequest request) {
        checkCityAdmin(request);
        dto.setId(id);
        userService.updateUser(dto);
        return R.ok("修改用户成功", null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", notes = "根据ID删除用户")
    public R<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        checkCityAdmin(request);
        userService.deleteUser(id);
        return R.ok("删除用户成功", null);
    }

    /**
     * 重置密码
     */
    @PostMapping("/{id}/reset-password")
    @ApiOperation(value = "重置密码", notes = "将用户密码重置为默认密码 123456")
    public R<Void> resetPassword(@PathVariable Long id, HttpServletRequest request) {
        checkCityAdmin(request);
        userService.resetPassword(id);
        return R.ok("密码重置成功", null);
    }

    /**
     * 校验当前用户是否为市级管理员
     */
    private void checkCityAdmin(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        Integer roleType = (roleTypeObj instanceof Integer) ? (Integer) roleTypeObj : null;
        if (roleType == null || roleType != 1) {
            log.warn("非市级管理员尝试访问用户管理接口，当前角色类型: {}", roleType);
            throw new BusinessException(ResultCode.FORBIDDEN, "仅市级管理员可操作");
        }
    }
}
