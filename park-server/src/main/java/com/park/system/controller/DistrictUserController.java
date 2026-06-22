package com.park.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.auth.entity.SysUser;
import com.park.auth.service.AuthService;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.system.dto.UserQueryDTO;
import com.park.system.dto.UserSaveDTO;
import com.park.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 区县端用户管理控制器
 * <p>
 * 区县管理员可管理本区县下的园区管理员账号
 * </p>
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/district/users")
@Api(tags = "区县端-园区管理员管理")
public class DistrictUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    /**
     * 获取园区管理员列表（分页）
     *
     * @param queryDTO 查询参数
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping
    @ApiOperation(value = "园区管理员列表", notes = "分页查询本区县下的园区管理员列表")
    public R<PageResult<SysUser>> getParkAdminPage(
            @ApiParam(value = "查询参数", required = false) UserQueryDTO queryDTO,
            HttpServletRequest request) {
        // 获取当前区县管理员信息
        SysUser currentUser = getCurrentUser(request);

        // 设置查询条件：只查询园区管理员（roleType=3）且属于本区县
        queryDTO.setRoleType(3);
        queryDTO.setDistrictId(currentUser.getDistrictId());

        IPage<SysUser> page = userService.getUserPage(queryDTO);

        // 清除密码
        page.getRecords().forEach(user -> user.setPassword(null));

        PageResult<SysUser> pageResult = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 获取园区管理员详情
     *
     * @param id      园区管理员ID
     * @param request HTTP请求
     * @return 园区管理员信息
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "园区管理员详情", notes = "根据ID查询园区管理员详情")
    public R<SysUser> getUserById(
            @ApiParam(value = "园区管理员ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        // 校验权限
        checkPermission(id, request);

        SysUser user = userService.getUserById(id);
        user.setPassword(null);
        return R.ok(user);
    }

    /**
     * 新增园区管理员
     *
     * @param dto     园区管理员信息
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation(value = "新增园区管理员", notes = "创建园区管理员账号，密码自动BCrypt加密")
    public R<Void> saveParkAdmin(
            @ApiParam(value = "园区管理员信息", required = true) @Valid @RequestBody UserSaveDTO dto,
            HttpServletRequest request) {
        // 获取当前区县管理员信息
        SysUser currentUser = getCurrentUser(request);

        // 强制设置为园区管理员
        dto.setRoleType(3);
        // 设置所属区县
        dto.setDistrictId(currentUser.getDistrictId());

        userService.saveUser(dto);
        return R.ok("新增园区管理员成功", null);
    }

    /**
     * 修改园区管理员
     *
     * @param id      园区管理员ID
     * @param dto     园区管理员信息
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改园区管理员", notes = "修改园区管理员信息，密码可选")
    public R<Void> updateParkAdmin(
            @ApiParam(value = "园区管理员ID", required = true) @PathVariable Long id,
            @ApiParam(value = "园区管理员信息", required = true) @Valid @RequestBody UserSaveDTO dto,
            HttpServletRequest request) {
        // 校验权限
        checkPermission(id, request);

        dto.setId(id);
        // 强制保持为园区管理员
        dto.setRoleType(3);

        userService.updateUser(dto);
        return R.ok("修改园区管理员成功", null);
    }

    /**
     * 删除园区管理员
     *
     * @param id      园区管理员ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除园区管理员", notes = "根据ID删除园区管理员")
    public R<Void> deleteParkAdmin(
            @ApiParam(value = "园区管理员ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        // 校验权限
        checkPermission(id, request);

        userService.deleteUser(id);
        return R.ok("删除园区管理员成功", null);
    }

    /**
     * 重置园区管理员密码
     *
     * @param id      园区管理员ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/reset-password")
    @ApiOperation(value = "重置密码", notes = "将园区管理员密码重置为默认密码 123456")
    public R<Void> resetPassword(
            @ApiParam(value = "园区管理员ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        // 校验权限
        checkPermission(id, request);

        userService.resetPassword(id);
        return R.ok("密码重置成功", null);
    }

    /**
     * 获取当前登录用户信息
     */
    private SysUser getCurrentUser(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        Long userId = null;
        if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        }

        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        SysUser user = authService.getUserById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 校验是否为区县管理员
        if (user.getRoleType() != 2) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅区县管理员可操作");
        }

        return user;
    }

    /**
     * 校验操作权限（确保操作的是本区县的园区管理员）
     */
    private void checkPermission(Long userId, HttpServletRequest request) {
        SysUser currentUser = getCurrentUser(request);
        SysUser targetUser = userService.getUserById(userId);
        
        if (targetUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        
        // 只能操作园区管理员
        if (targetUser.getRoleType() != 3) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能操作园区管理员账号");
        }
        
        // 只能操作本区县的园区管理员
        if (!currentUser.getDistrictId().equals(targetUser.getDistrictId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能管理本区县的园区管理员");
        }
    }
}
