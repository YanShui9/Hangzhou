package com.park.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.auth.entity.SysUser;
import com.park.auth.mapper.UserMapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.system.dto.UserSaveDTO;
import com.park.system.service.UserService;
import com.park.system.vo.AdminUserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin-users")
@Api(tags = "市级管理员账号管理")
public class AdminUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping
    @ApiOperation(value = "分页查询市级管理员账号", notes = "支持按姓名、手机号、所属部门筛选")
    public R<PageResult<AdminUserVO>> getAdminUserPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            HttpServletRequest request) {
        checkCityAdmin(request);

        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleType, 1);
        if (StringUtils.hasText(name)) {
            wrapper.like(SysUser::getRealName, name);
        }
        if (StringUtils.hasText(phone)) {
            wrapper.like(SysUser::getPhone, phone);
        }
        if (StringUtils.hasText(department)) {
            wrapper.like(SysUser::getDepartment, department);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> result = userMapper.selectPage(page, wrapper);
        List<AdminUserVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return R.ok(PageResult.of(voList, result.getTotal(), (int) result.getCurrent(), (int) result.getSize()));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询市级管理员账号详情")
    public R<AdminUserVO> getAdminUserById(@PathVariable Long id, HttpServletRequest request) {
        checkCityAdmin(request);
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 1) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "账号不存在");
        }
        return R.ok(convertToVO(user));
    }

    @PostMapping
    @ApiOperation(value = "新增市级管理员账号")
    public R<Void> saveAdminUser(@Valid @RequestBody UserSaveDTO dto, HttpServletRequest request) {
        checkCityAdmin(request);
        dto.setRoleType(1);
        dto.setStatus(1);
        userService.saveUser(dto);
        return R.ok("新增成功", null);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除市级管理员账号", notes = "至少保留一个市级管理员，且不能删除当前登录账号")
    public R<Void> deleteAdminUser(@PathVariable Long id, HttpServletRequest request) {
        checkCityAdmin(request);

        SysUser user = userMapper.selectById(id);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 1) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "账号不存在");
        }

        Long currentUserId = getCurrentUserId(request);
        if (currentUserId != null && currentUserId.equals(id)) {
            throw new BusinessException(ResultCode.FAILURE, "不能删除当前登录账号");
        }

        LambdaQueryWrapper<SysUser> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(SysUser::getRoleType, 1);
        Long count = userMapper.selectCount(countWrapper);
        if (count != null && count <= 1) {
            throw new BusinessException(ResultCode.FAILURE, "至少保留一个市级管理员账号");
        }

        userMapper.deleteById(id);
        return R.ok("删除成功", null);
    }

    @PostMapping("/{id}/reset-password")
    @ApiOperation(value = "重置密码", notes = "重置为默认密码 123456")
    public R<Void> resetPassword(@PathVariable Long id, HttpServletRequest request) {
        checkCityAdmin(request);
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 1) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "账号不存在");
        }
        userService.resetPassword(id);
        return R.ok("密码重置成功", null);
    }

    @GetMapping("/template")
    @ApiOperation(value = "下载导入模板", notes = "下载市级管理员账号导入模板Excel文件")
    public void downloadTemplate(HttpServletResponse response, HttpServletRequest request) {
        checkCityAdmin(request);
        String fileName = "管理员账号导入模板.xlsx";
        ClassPathResource resource = new ClassPathResource("templates/" + fileName);
        if (!resource.exists()) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "模板文件不存在: " + fileName);
        }
        try (InputStream is = resource.getInputStream();
             OutputStream os = response.getOutputStream()) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            log.error("下载管理员账号模板失败", e);
            throw new BusinessException(ResultCode.FAILURE, "下载模板失败");
        }
    }

    @PostMapping("/import")
    @ApiOperation(value = "批量导入市级管理员账号", notes = "通过Excel批量导入，模板列：姓名、手机号、所属部门、账号")
    public R<Map<String, Object>> importAdminUsers(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        checkCityAdmin(request);
        int successCount = 0;
        int failCount = 0;
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                try {
                    String realName = getCellString(row.getCell(0));
                    String phone = getCellString(row.getCell(1));
                    String department = getCellString(row.getCell(2));
                    String username = getCellString(row.getCell(3));
                    if (!StringUtils.hasText(username) && !StringUtils.hasText(realName)
                            && !StringUtils.hasText(phone) && !StringUtils.hasText(department)) {
                        continue;
                    }
                    UserSaveDTO dto = new UserSaveDTO();
                    dto.setUsername(username);
                    dto.setPassword("123456");
                    dto.setRoleType(1);
                    dto.setRealName(realName);
                    dto.setPhone(phone);
                    dto.setDepartment(department);
                    dto.setStatus(1);
                    userService.saveUser(dto);
                    successCount++;
                } catch (Exception e) {
                    log.warn("导入管理员账号第{}行失败: {}", i + 1, e.getMessage());
                    failCount++;
                }
            }
        } catch (Exception e) {
            log.error("解析管理员账号Excel失败", e);
            throw new BusinessException(ResultCode.FAILURE, "Excel解析失败");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return R.ok(result);
    }

    private String getCellString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private AdminUserVO convertToVO(SysUser user) {
        AdminUserVO vo = new AdminUserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setName(user.getRealName() != null && !user.getRealName().isEmpty() ? user.getRealName() : user.getUsername());
        return vo;
    }

    private void checkCityAdmin(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        Integer roleType = (roleTypeObj instanceof Integer) ? (Integer) roleTypeObj : null;
        if (roleType == null || roleType != 1) {
            log.warn("非市级管理员尝试访问管理员账号接口，当前角色类型: {}", roleType);
            throw new BusinessException(ResultCode.FORBIDDEN, "仅市级管理员可操作");
        }
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) return null;
        if (userIdObj instanceof Long) return (Long) userIdObj;
        if (userIdObj instanceof Integer) return ((Integer) userIdObj).longValue();
        try {
            return Long.valueOf(userIdObj.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
