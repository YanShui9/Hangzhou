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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 园区账号管理控制器（市级/区县管理员可用）
 * 自动强制 roleType=3
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/park-users")
@Api(tags = "园区账号管理")
public class ParkUserController {

    @Autowired
    private UserService userService;

    /** 园区账号分页列表 */
    @GetMapping
    @ApiOperation(value = "园区账号分页", notes = "分页查询园区账号列表；市级看全部，区县仅本区")
    public R<PageResult<SysUser>> getParkUserPage(UserQueryDTO queryDTO, HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        Object userIdObj = request.getAttribute("userId");
        Long userId = userIdObj instanceof Long ? (Long) userIdObj
                : (userIdObj instanceof Integer ? ((Integer) userIdObj).longValue() : null);
        if (roleType == null || (roleType != 1 && roleType != 2)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限访问");
        }
        queryDTO.setRoleType(3);
        if (roleType == 2 && userId != null) {
            // 区县管理员：限制到本区
            SysUser me = userService.getUserById(userId);
            if (me != null && me.getDistrictId() != null) {
                queryDTO.setDistrictId(me.getDistrictId());
            }
        }
        IPage<SysUser> page = userService.getUserPage(queryDTO);
        page.getRecords().forEach(u -> u.setPassword(null));
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(),
                (int) page.getCurrent(), (int) page.getSize()));
    }

    /** 园区账号详情 */
    @GetMapping("/{id}")
    @ApiOperation(value = "园区账号详情", notes = "根据ID查询园区账号")
    public R<SysUser> getParkUserById(@PathVariable Long id, HttpServletRequest request) {
        checkCanAccess(id, request);
        SysUser user = userService.getUserById(id);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 3) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "园区账号不存在");
        }
        user.setPassword(null);
        return R.ok(user);
    }

    /** 新增园区账号 */
    @PostMapping
    @ApiOperation(value = "新增园区账号", notes = "新增园区账号，roleType 强制为 3")
    public R<Void> saveParkUser(@RequestBody UserSaveDTO dto, HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || (roleType != 1 && roleType != 2)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限操作");
        }
        dto.setRoleType(3);
        if (dto.getParkId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区账号必须指定 parkId");
        }
        if (dto.getStatus() == null) {
            dto.setStatus(1);
        }
        if (!org.springframework.util.StringUtils.hasText(dto.getPassword())) {
            dto.setPassword("123456");
        }
        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户名不能为空");
        }
        if (roleType == 2) {
            Object userIdObj = request.getAttribute("userId");
            Long userId = userIdObj instanceof Long ? (Long) userIdObj
                    : (userIdObj instanceof Integer ? ((Integer) userIdObj).longValue() : null);
            SysUser me = userService.getUserById(userId);
            if (me == null || me.getDistrictId() == null) {
                throw new BusinessException(ResultCode.FORBIDDEN, "区县管理员未分配区县");
            }
            dto.setDistrictId(me.getDistrictId());
        }
        userService.saveUser(dto);
        return R.ok("新增园区账号成功", null);
    }

    /** 修改园区账号 */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改园区账号", notes = "修改园区账号信息")
    public R<Void> updateParkUser(@PathVariable Long id,
                                  @RequestBody UserSaveDTO dto,
                                  HttpServletRequest request) {
        checkCanAccess(id, request);
        SysUser exist = userService.getUserById(id);
        if (!org.springframework.util.StringUtils.hasText(dto.getUsername())) {
            dto.setUsername(exist.getUsername());
        }
        if (dto.getRoleType() == null) {
            dto.setRoleType(3);
        }
        if (dto.getParkId() == null) {
            dto.setParkId(exist.getParkId());
        }
        if (dto.getDistrictId() == null) {
            dto.setDistrictId(exist.getDistrictId());
        }
        if (dto.getStatus() == null) {
            dto.setStatus(exist.getStatus());
        }
        dto.setId(id);
        dto.setRoleType(3);
        userService.updateUser(dto);
        return R.ok("修改园区账号成功", null);
    }

    /** 删除园区账号 */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除园区账号", notes = "根据ID删除园区账号")
    public R<Void> deleteParkUser(@PathVariable Long id, HttpServletRequest request) {
        checkCanAccess(id, request);
        SysUser exist = userService.getUserById(id);
        if (exist == null || exist.getRoleType() == null || exist.getRoleType() != 3) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "园区账号不存在");
        }
        userService.deleteUser(id);
        return R.ok("删除园区账号成功", null);
    }

    /** 批量删除 */
    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除园区账号", notes = "根据ID数组批量删除")
    public R<Void> batchDeleteParkUser(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || (roleType != 1 && roleType != 2)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限操作");
        }
        @SuppressWarnings("unchecked")
        List<Object> ids = (List<Object>) body.get("ids");
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "ids 不能为空");
        }
        for (Object o : ids) {
            Long id = Long.valueOf(o.toString());
            SysUser exist = userService.getUserById(id);
            if (exist == null || exist.getRoleType() == null || exist.getRoleType() != 3) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "账号不存在或非园区角色: id=" + id);
            }
            userService.deleteUser(id);
        }
        return R.ok("批量删除成功", null);
    }

    /** 重置密码 */
    @PostMapping("/{id}/reset-password")
    @ApiOperation(value = "重置园区账号密码", notes = "重置为默认密码 123456")
    public R<Void> resetParkUserPassword(@PathVariable Long id, HttpServletRequest request) {
        checkCanAccess(id, request);
        SysUser exist = userService.getUserById(id);
        if (exist == null || exist.getRoleType() == null || exist.getRoleType() != 3) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "园区账号不存在");
        }
        userService.resetPassword(id);
        return R.ok("密码已重置为 123456", null);
    }

    /** 启用/禁用 */
    @PutMapping("/{id}/status")
    @ApiOperation(value = "切换园区账号状态", notes = "1=启用 / 0=禁用")
    public R<Void> toggleParkUserStatus(@PathVariable Long id,
                                        @RequestBody Map<String, Object> body,
                                        HttpServletRequest request) {
        checkCanAccess(id, request);
        SysUser exist = userService.getUserById(id);
        if (exist == null || exist.getRoleType() == null || exist.getRoleType() != 3) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "园区账号不存在");
        }
        Object s = body == null ? null : body.get("status");
        if (s == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "status 不能为空");
        }
        Integer status = Integer.parseInt(s.toString());
        userService.updateStatus(id, status);
        return R.ok("操作成功", null);
    }

    /** 下载导入模板 */
    @GetMapping("/template")
    @ApiOperation(value = "下载园区账号导入模板", notes = "返回 Excel 模板文件")
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || (roleType != 1 && roleType != 2)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限操作");
        }
        try {
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=park_user_template.xls");
            String csv = "username,realName,phone,parkId\n"
                    + "park_demo,示例账号,13800000000,1\n"
                    + "park_test,测试账号,13800000001,2\n";
            response.getOutputStream().write(csv.getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "下载模板失败：" + ex.getMessage());
        }
    }

    /** 批量导入 */
    @PostMapping("/import")
    @ApiOperation(value = "批量导入园区账号", notes = "上传 CSV 文件批量创建园区账号")
    public R<Map<String, Object>> importParkUser(@RequestParam("file") MultipartFile file,
                                                 HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || (roleType != 1 && roleType != 2)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限操作");
        }
        try {
            String content = new String(file.getBytes(), "UTF-8");
            String[] lines = content.split("\\r?\\n");
            int created = 0, skipped = 0;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line == null || line.trim().isEmpty()) continue;
                String[] cols = line.split(",");
                if (cols.length < 3) { skipped++; continue; }
                try {
                    UserSaveDTO dto = new UserSaveDTO();
                    dto.setUsername(cols[0].trim());
                    dto.setRealName(cols[1].trim());
                    dto.setPhone(cols.length > 2 ? cols[2].trim() : null);
                    dto.setParkId(cols.length > 3 && !cols[3].trim().isEmpty()
                            ? Long.valueOf(cols[3].trim()) : null);
                    dto.setRoleType(3);
                    userService.saveUser(dto);
                    created++;
                } catch (Exception ex) {
                    log.warn("导入园区账号失败：{}", line, ex);
                    skipped++;
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("created", created);
            data.put("skipped", skipped);
            return R.ok("导入完成", data);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "导入失败：" + ex.getMessage());
        }
    }

    /** 导出 */
    @GetMapping("/export")
    @ApiOperation(value = "导出园区账号", notes = "按查询条件导出园区账号为 Excel")
    public void exportParkUser(UserQueryDTO queryDTO,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || (roleType != 1 && roleType != 2)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限操作");
        }
        queryDTO.setRoleType(3);
        if (roleType == 2) {
            Object userIdObj = request.getAttribute("userId");
            Long userId = userIdObj instanceof Long ? (Long) userIdObj
                    : (userIdObj instanceof Integer ? ((Integer) userIdObj).longValue() : null);
            SysUser me = userService.getUserById(userId);
            if (me != null && me.getDistrictId() != null) {
                queryDTO.setDistrictId(me.getDistrictId());
            }
        }
        queryDTO.setPageSize(10000);
        try {
            IPage<SysUser> page = userService.getUserPage(queryDTO);
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=park_users.xls");
            StringBuilder sb = new StringBuilder("username,realName,phone,parkId,status\n");
            for (SysUser u : page.getRecords()) {
                sb.append(u.getUsername()).append(",")
                  .append(u.getRealName() == null ? "" : u.getRealName()).append(",")
                  .append(u.getPhone() == null ? "" : u.getPhone()).append(",")
                  .append(u.getParkId() == null ? "" : u.getParkId()).append(",")
                  .append(u.getStatus() == null ? "" : u.getStatus()).append("\n");
            }
            response.getOutputStream().write(sb.toString().getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "导出失败：" + ex.getMessage());
        }
    }

    /**
     * 校验当前用户可访问指定园区账号
     * 市级：可访问所有
     * 区县：仅可访问本区
     */
    private void checkCanAccess(Long userId, HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || (roleType != 1 && roleType != 2)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限操作");
        }
        if (roleType == 2) {
            Object userIdObj = request.getAttribute("userId");
            Long me = userIdObj instanceof Long ? (Long) userIdObj
                    : (userIdObj instanceof Integer ? ((Integer) userIdObj).longValue() : null);
            SysUser mySelf = userService.getUserById(me);
            SysUser target = userService.getUserById(userId);
            if (mySelf == null || mySelf.getDistrictId() == null
                    || target == null || !mySelf.getDistrictId().equals(target.getDistrictId())) {
                throw new BusinessException(ResultCode.FORBIDDEN, "无权访问其他区县的账号");
            }
        }
    }
}
