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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 区县账号管理控制器（仅市级管理员可用）
 * 自动强制 roleType=2
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/district-users")
@Api(tags = "区县账号管理")
public class DistrictUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    /** 区县账号分页列表 */
    @GetMapping
    @ApiOperation(value = "区县账号分页", notes = "市级管理员查询区县账号列表，自动筛选 roleType=2")
    public R<PageResult<SysUser>> getDistrictUserPage(UserQueryDTO queryDTO, HttpServletRequest request) {
        checkCityAdmin(request);
        queryDTO.setRoleType(2); // 强制区县角色
        IPage<SysUser> page = userService.getUserPage(queryDTO);
        page.getRecords().forEach(u -> u.setPassword(null));
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(),
                (int) page.getCurrent(), (int) page.getSize()));
    }

    /** 区县账号详情 */
    @GetMapping("/{id}")
    @ApiOperation(value = "区县账号详情", notes = "根据ID查询区县账号")
    public R<SysUser> getDistrictUserById(@PathVariable Long id, HttpServletRequest request) {
        checkCityAdmin(request);
        SysUser user = userService.getUserById(id);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 2) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "区县账号不存在");
        }
        user.setPassword(null);
        return R.ok(user);
    }

    /** 新增区县账号 */
    @PostMapping
    @ApiOperation(value = "新增区县账号", notes = "新增区县账号，roleType 强制为 2")
    public R<Void> saveDistrictUser(@RequestBody UserSaveDTO dto, HttpServletRequest request) {
        checkCityAdmin(request);
        dto.setRoleType(2);
        if (dto.getDistrictId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "区县账号必须指定 districtId");
        }
        if (dto.getStatus() == null) {
            dto.setStatus(1);
        }
        if (!org.springframework.util.StringUtils.hasText(dto.getPassword())) {
            dto.setPassword("123456");
        }
        userService.saveUser(dto);
        return R.ok("新增区县账号成功", null);
    }

    /** 修改区县账号 */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改区县账号", notes = "修改区县账号信息")
    public R<Void> updateDistrictUser(@PathVariable Long id,
                                      @RequestBody UserSaveDTO dto,
                                      HttpServletRequest request) {
        checkCityAdmin(request);
        SysUser exist = userService.getUserById(id);
        if (exist == null || exist.getRoleType() == null || exist.getRoleType() != 2) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "区县账号不存在");
        }
        // 部分更新：缺失字段用现有值填充
        if (!org.springframework.util.StringUtils.hasText(dto.getUsername())) {
            dto.setUsername(exist.getUsername());
        }
        if (dto.getRoleType() == null) {
            dto.setRoleType(2);
        }
        if (dto.getDistrictId() == null) {
            dto.setDistrictId(exist.getDistrictId());
        }
        if (dto.getStatus() == null) {
            dto.setStatus(exist.getStatus());
        }
        dto.setId(id);
        dto.setRoleType(2);
        userService.updateUser(dto);
        return R.ok("修改区县账号成功", null);
    }

    /** 删除区县账号 */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除区县账号", notes = "根据ID删除区县账号")
    public R<Void> deleteDistrictUser(@PathVariable Long id, HttpServletRequest request) {
        checkCityAdmin(request);
        SysUser exist = userService.getUserById(id);
        if (exist == null || exist.getRoleType() == null || exist.getRoleType() != 2) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "区县账号不存在");
        }
        userService.deleteUser(id);
        return R.ok("删除区县账号成功", null);
    }

    /** 批量删除 */
    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除区县账号", notes = "根据ID数组批量删除")
    public R<Void> batchDeleteDistrictUser(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        checkCityAdmin(request);
        @SuppressWarnings("unchecked")
        List<Object> ids = (List<Object>) body.get("ids");
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "ids 不能为空");
        }
        for (Object o : ids) {
            Long id = Long.valueOf(o.toString());
            SysUser exist = userService.getUserById(id);
            if (exist == null || exist.getRoleType() == null || exist.getRoleType() != 2) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "账号不存在或非区县角色: id=" + id);
            }
            userService.deleteUser(id);
        }
        return R.ok("批量删除成功", null);
    }

    /** 重置密码 */
    @PostMapping("/{id}/reset-password")
    @ApiOperation(value = "重置区县账号密码", notes = "重置为默认密码 123456")
    public R<Void> resetDistrictUserPassword(@PathVariable Long id, HttpServletRequest request) {
        checkCityAdmin(request);
        SysUser exist = userService.getUserById(id);
        if (exist == null || exist.getRoleType() == null || exist.getRoleType() != 2) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "区县账号不存在");
        }
        userService.resetPassword(id);
        return R.ok("密码已重置为 123456", null);
    }

    /** 启用/禁用 */
    @PutMapping("/{id}/status")
    @ApiOperation(value = "切换区县账号状态", notes = "1=启用 / 0=禁用")
    public R<Void> toggleDistrictUserStatus(@PathVariable Long id,
                                            @RequestBody Map<String, Object> body,
                                            HttpServletRequest request) {
        checkCityAdmin(request);
        SysUser exist = userService.getUserById(id);
        if (exist == null || exist.getRoleType() == null || exist.getRoleType() != 2) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "区县账号不存在");
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
    @ApiOperation(value = "下载区县账号导入模板", notes = "返回 Excel 模板文件")
    public void downloadTemplate(HttpServletRequest request,
                                 javax.servlet.http.HttpServletResponse response) {
        checkCityAdmin(request);
        try {
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=district_user_template.xls");
            String csv = "username,realName,phone,districtId\n"
                    + "district_demo,示例账号,13800000000,3\n"
                    + "district_test,测试账号,13800000001,5\n";
            response.getOutputStream().write(csv.getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "下载模板失败：" + ex.getMessage());
        }
    }

    /** 批量导入 */
    @PostMapping("/import")
    @ApiOperation(value = "批量导入区县账号", notes = "上传 CSV/Excel 文件批量创建区县账号")
    public R<Map<String, Object>> importDistrictUser(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                                     HttpServletRequest request) {
        checkCityAdmin(request);
        try {
            String content = new String(file.getBytes(), "UTF-8");
            String[] lines = content.split("\\r?\\n");
            int created = 0, skipped = 0;
            for (int i = 1; i < lines.length; i++) { // 跳过表头
                String line = lines[i];
                if (line == null || line.trim().isEmpty()) continue;
                String[] cols = line.split(",");
                if (cols.length < 3) { skipped++; continue; }
                try {
                    UserSaveDTO dto = new UserSaveDTO();
                    dto.setUsername(cols[0].trim());
                    dto.setRealName(cols[1].trim());
                    dto.setPhone(cols.length > 2 ? cols[2].trim() : null);
                    dto.setDistrictId(cols.length > 3 && !cols[3].trim().isEmpty()
                            ? Long.valueOf(cols[3].trim()) : null);
                    dto.setRoleType(2);
                    userService.saveUser(dto);
                    created++;
                } catch (Exception ex) {
                    log.warn("导入区县账号失败：{}", line, ex);
                    skipped++;
                }
            }
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("created", created);
            data.put("skipped", skipped);
            return R.ok("导入完成", data);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "导入失败：" + ex.getMessage());
        }
    }

    /** 导出 */
    @GetMapping("/export")
    @ApiOperation(value = "导出区县账号", notes = "按查询条件导出区县账号为 Excel")
    public void exportDistrictUser(UserQueryDTO queryDTO,
                                   HttpServletRequest request,
                                   javax.servlet.http.HttpServletResponse response) {
        checkCityAdmin(request);
        queryDTO.setRoleType(2);
        queryDTO.setPageSize(10000);
        try {
            IPage<SysUser> page = userService.getUserPage(queryDTO);
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=district_users.xls");
            StringBuilder sb = new StringBuilder("username,realName,phone,districtId,status\n");
            for (SysUser u : page.getRecords()) {
                sb.append(u.getUsername()).append(",")
                  .append(u.getRealName() == null ? "" : u.getRealName()).append(",")
                  .append(u.getPhone() == null ? "" : u.getPhone()).append(",")
                  .append(u.getDistrictId() == null ? "" : u.getDistrictId()).append(",")
                  .append(u.getStatus() == null ? "" : u.getStatus()).append("\n");
            }
            response.getOutputStream().write(sb.toString().getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "导出失败：" + ex.getMessage());
        }
    }

    private void checkCityAdmin(HttpServletRequest request) {
        Integer roleType = (Integer) request.getAttribute("roleType");
        if (roleType == null || roleType != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅市级管理员可操作");
        }
    }
}
