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
import com.park.system.entity.DistrictInfo;
import com.park.system.mapper.DistrictMapper;
import com.park.system.service.UserService;
import com.park.system.vo.DistrictUserVO;
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
 * 区县账号控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/district-users")
@Api(tags = "区县账号管理")
public class DistrictUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private DistrictMapper districtMapper;

    /**
     * 分页查询区县账号列表
     */
    @GetMapping
    @ApiOperation(value = "分页查询区县账号", notes = "支持按姓名、手机号、区域筛选")
    public R<PageResult<DistrictUserVO>> getDistrictUserPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long districtId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRoleType, 2);
        if (StringUtils.hasText(name)) {
            wrapper.like(SysUser::getRealName, name);
        }
        if (StringUtils.hasText(phone)) {
            wrapper.like(SysUser::getPhone, phone);
        }
        if (districtId != null) {
            wrapper.eq(SysUser::getDistrictId, districtId);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> result = userMapper.selectPage(page, wrapper);
        List<DistrictUserVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return R.ok(PageResult.of(voList, result.getTotal(), (int) result.getCurrent(), (int) result.getSize()));
    }

    /**
     * 根据ID查询区县账号详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询区县账号详情", notes = "根据ID查询区县账号详情")
    public R<DistrictUserVO> getDistrictUserById(@PathVariable Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 2) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "账号不存在");
        }
        return R.ok(convertToVO(user));
    }

    /**
     * 新增区县账号
     */
    @PostMapping
    @ApiOperation(value = "新增区县账号", notes = "新增区县管理员账号")
    public R<Void> saveDistrictUser(@Valid @RequestBody UserSaveDTO dto) {
        dto.setRoleType(2);
        userService.saveUser(dto);
        return R.ok("新增成功", null);
    }

    /**
     * 修改区县账号
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改区县账号", notes = "根据ID修改区县账号")
    public R<Void> updateDistrictUser(@PathVariable Long id, @Valid @RequestBody UserSaveDTO dto) {
        dto.setId(id);
        dto.setRoleType(2);
        userService.updateUser(dto);
        return R.ok("修改成功", null);
    }

    /**
     * 删除区县账号
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除区县账号", notes = "根据ID删除区县账号")
    public R<Void> deleteDistrictUser(@PathVariable Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 2) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "账号不存在");
        }
        userMapper.deleteById(id);
        return R.ok("删除成功", null);
    }

    /**
     * 下载区县账号导入模板
     */
    @GetMapping("/template")
    @ApiOperation(value = "下载导入模板", notes = "下载区县账号导入模板Excel文件")
    public void downloadTemplate(HttpServletResponse response) {
        String fileName = "区县账号导入模板 (1).xlsx";
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
            log.error("下载区县账号模板失败", e);
            throw new BusinessException(ResultCode.FAILURE, "下载模板失败");
        }
    }

    /**
     * 批量导入区县账号
     * 模板列：姓名、手机号、所属区域、账号
     */
    @PostMapping("/import")
    @ApiOperation(value = "批量导入区县账号", notes = "通过Excel批量导入区县账号")
    public R<Map<String, Object>> importDistrictUsers(@RequestParam("file") MultipartFile file) {
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
                    // 列：姓名、手机号、所属区域、账号
                    String realName = getCellString(row.getCell(0));
                    String phone = getCellString(row.getCell(1));
                    String districtName = getCellString(row.getCell(2));
                    String username = getCellString(row.getCell(3));
                    // 空行跳过
                    if (!StringUtils.hasText(username) && !StringUtils.hasText(realName)
                            && !StringUtils.hasText(phone) && !StringUtils.hasText(districtName)) {
                        continue;
                    }
                    // 通过区县名称查询 district_id
                    LambdaQueryWrapper<DistrictInfo> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(DistrictInfo::getDistrictName, districtName);
                    DistrictInfo district = districtMapper.selectOne(wrapper);
                    if (district == null) {
                        failCount++;
                        continue;
                    }
                    UserSaveDTO dto = new UserSaveDTO();
                    dto.setUsername(username);
                    dto.setPassword("123456");
                    dto.setRoleType(2);
                    dto.setDistrictId(district.getId());
                    dto.setRealName(realName);
                    dto.setPhone(phone);
                    dto.setStatus(1);
                    userService.saveUser(dto);
                    successCount++;
                } catch (Exception e) {
                    log.warn("导入区县账号第{}行失败: {}", i + 1, e.getMessage());
                    failCount++;
                }
            }
        } catch (Exception e) {
            log.error("解析区县账号Excel失败", e);
            throw new BusinessException(ResultCode.FAILURE, "Excel解析失败");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return R.ok(result);
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

    private DistrictUserVO convertToVO(SysUser user) {
        DistrictUserVO vo = new DistrictUserVO();
        BeanUtils.copyProperties(user, vo);
        // 兼容历史数据：real_name为空时回退到username
        vo.setName(user.getRealName() != null && !user.getRealName().isEmpty() ? user.getRealName() : user.getUsername());
        if (user.getDistrictId() != null) {
            DistrictInfo district = districtMapper.selectById(user.getDistrictId());
            if (district != null) {
                vo.setDistrictName(district.getDistrictName());
            }
        }
        return vo;
    }
}
