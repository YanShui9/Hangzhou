package com.park.enterprise.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.common.exception.BusinessException;
import com.park.common.result.PageResult;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.enterprise.entity.EnterpriseInfo;
import com.park.enterprise.service.EnterpriseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业信息管理（系统管理模块）
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/system/enterprise-info")
@Api(tags = "企业信息管理（系统）")
public class EnterpriseInfoController {

    @Autowired
    private EnterpriseService enterpriseService;

    /** 分页列表 */
    @GetMapping
    @ApiOperation(value = "企业信息分页", notes = "分页查询企业信息列表")
    public R<PageResult<EnterpriseInfo>> getEnterpriseInfoPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long parkId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<EnterpriseInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EnterpriseInfo> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            w.and(qw -> qw.like(EnterpriseInfo::getEnterpriseName, keyword)
                    .or().like(EnterpriseInfo::getCreditCode, keyword)
                    .or().like(EnterpriseInfo::getContactName, keyword));
        }
        w.eq(parkId != null, EnterpriseInfo::getParkId, parkId)
                .eq(StringUtils.hasText(status), EnterpriseInfo::getStatus, status)
                .orderByDesc(EnterpriseInfo::getParkId);
        IPage<EnterpriseInfo> result = enterpriseService.getEnterpriseMapper().selectPage(page, w);
        return R.ok(PageResult.of(result.getRecords(), result.getTotal(),
                (int) result.getCurrent(), (int) result.getSize()));
    }

    /** 详情 */
    @GetMapping("/{id}")
    @ApiOperation(value = "企业信息详情", notes = "根据ID查询企业详情")
    public R<EnterpriseInfo> getById(@PathVariable Long id) {
        EnterpriseInfo e = enterpriseService.getEnterpriseMapper().selectById(id);
        if (e == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "企业信息不存在");
        }
        return R.ok(e);
    }

    /** 新增 */
    @PostMapping
    @ApiOperation(value = "新增企业信息", notes = "新增一条企业信息")
    public R<Void> save(@RequestBody EnterpriseInfo dto, HttpServletRequest request) {
        checkAdmin(request);
        enterpriseService.getEnterpriseMapper().insert(dto);
        return R.ok("新增成功", null);
    }

    /** 修改 */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改企业信息", notes = "根据ID修改企业信息")
    public R<Void> update(@PathVariable Long id, @RequestBody EnterpriseInfo dto, HttpServletRequest request) {
        checkAdmin(request);
        dto.setId(id);
        enterpriseService.getEnterpriseMapper().updateById(dto);
        return R.ok("修改成功", null);
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除企业信息", notes = "根据ID删除企业信息")
    public R<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        checkAdmin(request);
        enterpriseService.getEnterpriseMapper().deleteById(id);
        return R.ok("删除成功", null);
    }

    /** 批量删除 */
    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除企业信息", notes = "根据ID数组批量删除")
    public R<Void> batchDelete(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        checkAdmin(request);
        @SuppressWarnings("unchecked")
        List<Object> ids = (List<Object>) body.get("ids");
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "ids 不能为空");
        }
        for (Object o : ids) {
            enterpriseService.getEnterpriseMapper().deleteById(Long.valueOf(o.toString()));
        }
        return R.ok("批量删除成功", null);
    }

    /** 下载模板 */
    @GetMapping("/template")
    @ApiOperation(value = "下载企业信息导入模板", notes = "返回 CSV 模板")
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        checkAdmin(request);
        try {
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=enterprise_info_template.xls");
            String csv = "enterpriseName,creditCode,parkId,legalPerson,contactName,contactPhone,status\n"
                    + "示例企业,91330100MA000000XX,1,张三,李四,13800000000,在业\n";
            response.getOutputStream().write(csv.getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "下载模板失败：" + ex.getMessage());
        }
    }

    /** 批量导入 */
    @PostMapping("/import")
    @ApiOperation(value = "批量导入企业信息", notes = "上传 CSV 批量创建")
    public R<Map<String, Object>> importInfo(@RequestParam("file") MultipartFile file,
                                             HttpServletRequest request) {
        checkAdmin(request);
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
                    EnterpriseInfo e = new EnterpriseInfo();
                    e.setEnterpriseName(cols[0].trim());
                    e.setCreditCode(cols[1].trim());
                    if (cols.length > 2 && !cols[2].trim().isEmpty()) {
                        e.setParkId(Long.valueOf(cols[2].trim()));
                    }
                    if (cols.length > 3) e.setLegalPerson(cols[3].trim());
                    if (cols.length > 4) e.setContactName(cols[4].trim());
                    if (cols.length > 5) e.setContactPhone(cols[5].trim());
                    if (cols.length > 6) e.setStatus(cols[6].trim());
                    enterpriseService.getEnterpriseMapper().insert(e);
                    created++;
                } catch (Exception ex) {
                    log.warn("导入企业失败：{}", line, ex);
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
    @ApiOperation(value = "导出企业信息", notes = "导出 Excel")
    public void exportInfo(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Long districtId,
                           @RequestParam(required = false) Long parkId,
                           @RequestParam(required = false) String status,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        try {
            Page<EnterpriseInfo> page = new Page<>(1, 10000);
            LambdaQueryWrapper<EnterpriseInfo> w = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(keyword)) {
                w.and(qw -> qw.like(EnterpriseInfo::getEnterpriseName, keyword)
                        .or().like(EnterpriseInfo::getCreditCode, keyword));
            }
            w.eq(parkId != null, EnterpriseInfo::getParkId, parkId)
                    .eq(StringUtils.hasText(status), EnterpriseInfo::getStatus, status);
            IPage<EnterpriseInfo> result = enterpriseService.getEnterpriseMapper().selectPage(page, w);
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=enterprise_info.xls");
            StringBuilder sb = new StringBuilder("id,enterpriseName,creditCode,parkId,legalPerson,contactName,contactPhone,status\n");
            for (EnterpriseInfo e : result.getRecords()) {
                sb.append(e.getId()).append(",")
                  .append(e.getEnterpriseName() == null ? "" : e.getEnterpriseName()).append(",")
                  .append(e.getCreditCode() == null ? "" : e.getCreditCode()).append(",")
                  .append(e.getParkId() == null ? "" : e.getParkId()).append(",")
                  .append(e.getLegalPerson() == null ? "" : e.getLegalPerson()).append(",")
                  .append(e.getContactName() == null ? "" : e.getContactName()).append(",")
                  .append(e.getContactPhone() == null ? "" : e.getContactPhone()).append(",")
                  .append(e.getStatus() == null ? "" : e.getStatus()).append("\n");
            }
            response.getOutputStream().write(sb.toString().getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.FAILURE, "导出失败：" + ex.getMessage());
        }
    }

    private void checkAdmin(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        Integer roleType = (roleTypeObj instanceof Integer) ? (Integer) roleTypeObj : null;
        if (roleType == null || roleType != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅市级管理员可操作");
        }
    }
}
