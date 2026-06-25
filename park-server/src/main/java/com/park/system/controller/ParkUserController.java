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
import com.park.park.entity.ParkInfo;
import com.park.park.mapper.ParkMapper;
import com.park.system.dto.UserSaveDTO;
import com.park.system.service.UserService;
import com.park.system.vo.ParkUserVO;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 园区账号控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/park-users")
@Api(tags = "园区账号管理")
public class ParkUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ParkMapper parkMapper;

    /**
     * 分页查询园区账号列表
     */
    @GetMapping
    @ApiOperation(value = "分页查询园区账号", notes = "支持按企业名称、信用代码、园区名称、区域筛选")
    public R<PageResult<ParkUserVO>> getParkUserPage(
            @RequestParam(required = false) String enterpriseName,
            @RequestParam(required = false) String creditCode,
            @RequestParam(required = false) String parkName,
            @RequestParam(required = false) Long districtId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleType, 3);

        boolean needParkFilter = StringUtils.hasText(enterpriseName)
                || StringUtils.hasText(creditCode)
                || StringUtils.hasText(parkName)
                || districtId != null;

        if (needParkFilter) {
            LambdaQueryWrapper<ParkInfo> parkWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(enterpriseName)) {
                parkWrapper.like(ParkInfo::getOperatorUnit, enterpriseName);
            }
            if (StringUtils.hasText(creditCode)) {
                parkWrapper.like(ParkInfo::getOperationOrgCode, creditCode);
            }
            if (StringUtils.hasText(parkName)) {
                parkWrapper.like(ParkInfo::getParkName, parkName);
            }
            if (districtId != null) {
                parkWrapper.eq(ParkInfo::getDistrictId, districtId);
            }
            List<Long> parkIds = parkMapper.selectList(parkWrapper).stream()
                    .map(ParkInfo::getId)
                    .collect(Collectors.toList());
            if (parkIds.isEmpty()) {
                return R.ok(PageResult.of(java.util.Collections.emptyList(), 0L, pageNum, pageSize));
            }
            wrapper.in(SysUser::getParkId, parkIds);
        }

        wrapper.orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> result = userMapper.selectPage(page, wrapper);
        List<ParkUserVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return R.ok(PageResult.of(voList, result.getTotal(), (int) result.getCurrent(), (int) result.getSize()));
    }

    /**
     * 根据ID查询园区账号详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询园区账号详情", notes = "根据ID查询园区账号详情")
    public R<ParkUserVO> getParkUserById(@PathVariable Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 3) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "账号不存在");
        }
        return R.ok(convertToVO(user));
    }

    /**
     * 新增园区账号
     */
    @PostMapping
    @ApiOperation(value = "新增园区账号", notes = "新增园区管理员账号")
    public R<Void> saveParkUser(@Valid @RequestBody UserSaveDTO dto) {
        dto.setRoleType(3);
        userService.saveUser(dto);
        return R.ok("新增成功", null);
    }

    /**
     * 修改园区账号
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改园区账号", notes = "根据ID修改园区账号")
    public R<Void> updateParkUser(@PathVariable Long id, @Valid @RequestBody UserSaveDTO dto) {
        dto.setId(id);
        dto.setRoleType(3);
        userService.updateUser(dto);
        return R.ok("修改成功", null);
    }

    /**
     * 删除园区账号
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除园区账号", notes = "根据ID删除园区账号")
    public R<Void> deleteParkUser(@PathVariable Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 3) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "账号不存在");
        }
        userMapper.deleteById(id);
        return R.ok("删除成功", null);
    }

    /**
     * 下载园区账号导入模板
     */
    @GetMapping("/template")
    @ApiOperation(value = "下载导入模板", notes = "下载园区账号导入模板Excel文件")
    public void downloadTemplate(HttpServletResponse response) {
        String fileName = "园区账号导入模板 (1).xlsx";
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
            log.error("下载园区账号模板失败", e);
            throw new BusinessException(ResultCode.FAILURE, "下载模板失败");
        }
    }

    /**
     * 批量导入园区账号
     * 模板列：联系人、手机号、所属园区、运营单位、机构性质、统一社会信用代码
     */
    @PostMapping("/import")
    @ApiOperation(value = "批量导入园区账号", notes = "通过Excel批量导入园区账号")
    public R<Map<String, Object>> importParkUsers(@RequestParam("file") MultipartFile file) {
        int successCount = 0;
        int failCount = 0;
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            // 第0行为表头，从第1行开始读取
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                try {
                    // 列：联系人、手机号、所属园区、运营单位、机构性质、统一社会信用代码
                    String realName = getCellString(row.getCell(0));
                    String phone = getCellString(row.getCell(1));
                    String parkName = getCellString(row.getCell(2));
                    // 第3列运营单位、第4列机构性质 不用于创建账号
                    String creditCode = getCellString(row.getCell(5));
                    // 空行跳过
                    if (!StringUtils.hasText(realName) && !StringUtils.hasText(phone)
                            && !StringUtils.hasText(parkName) && !StringUtils.hasText(creditCode)) {
                        continue;
                    }
                    // 通过园区名称查询 park_id
                    LambdaQueryWrapper<ParkInfo> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(ParkInfo::getParkName, parkName);
                    ParkInfo park = parkMapper.selectOne(wrapper);
                    if (park == null) {
                        failCount++;
                        continue;
                    }
                    UserSaveDTO dto = new UserSaveDTO();
                    dto.setUsername(creditCode);
                    dto.setPassword("123456");
                    dto.setRoleType(3);
                    dto.setParkId(park.getId());
                    dto.setRealName(realName);
                    dto.setPhone(phone);
                    dto.setStatus(1);
                    userService.saveUser(dto);
                    successCount++;
                } catch (Exception e) {
                    log.warn("导入园区账号第{}行失败: {}", i + 1, e.getMessage());
                    failCount++;
                }
            }
        } catch (Exception e) {
            log.error("解析园区账号Excel失败", e);
            throw new BusinessException(ResultCode.FAILURE, "Excel解析失败");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return R.ok(result);
    }

    /**
     * 重置园区账号密码
     */
    @PostMapping("/{id}/reset-password")
    @ApiOperation(value = "重置密码", notes = "将园区账号密码重置为默认密码 123456")
    public R<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return R.ok("密码重置成功", null);
    }

    /**
     * 获取单元格字符串值
     */
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

    private ParkUserVO convertToVO(SysUser user) {
        ParkUserVO vo = new ParkUserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setName(user.getRealName());
        if (user.getParkId() != null) {
            ParkInfo park = parkMapper.selectById(user.getParkId());
            if (park != null) {
                vo.setParkName(park.getParkName());
                vo.setDistrictName(park.getDistrictName());
                // 企业名称和信用代码从园区运营机构信息中获取
                vo.setEnterpriseName(park.getOperatorUnit());
                vo.setCreditCode(park.getOperationOrgCode());
            }
        }
        return vo;
    }
}
