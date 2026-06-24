package com.park.warehouse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.park.common.result.R;
import com.park.warehouse.entity.DataWarehouse;
import com.park.warehouse.service.DataWarehouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 数据仓库控制器
 * 用于园区认定、星级评定、产业方向导入
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/data-warehouse")
@Api(tags = "数据仓库管理")
public class DataWarehouseController {

    @Autowired
    private DataWarehouseService dataWarehouseService;

    /**
     * 分页查询数据仓库
     */
    @GetMapping
    @ApiOperation(value = "分页查询数据仓库", notes = "支持按园区名称、年度筛选")
    public R<IPage<DataWarehouse>> getDataWarehousePage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String parkName,
            @RequestParam(required = false) Integer year) {
        IPage<DataWarehouse> page = dataWarehouseService.getDataWarehousePage(pageNum, pageSize, parkName, year);
        return R.ok(page);
    }

    /**
     * 根据ID获取数据仓库记录
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取数据仓库记录")
    public R<DataWarehouse> getById(@PathVariable Long id) {
        DataWarehouse dataWarehouse = dataWarehouseService.getById(id);
        return R.ok(dataWarehouse);
    }

    /**
     * 导入数据仓库（CSV/Excel）
     */
    @PostMapping
    @ApiOperation(value = "导入数据仓库", notes = "上传文件导入园区认定/星级/产业信息")
    public R<Map<String, Object>> importData(@RequestParam("file") MultipartFile file,
                                             HttpServletRequest request) {
        String importBy = (String) request.getAttribute("username");
        Map<String, Object> result = dataWarehouseService.importData(file, importBy);
        return R.ok(result);
    }

    /**
     * 删除数据仓库记录
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除数据仓库记录")
    public R<Void> deleteDataWarehouse(@PathVariable Long id) {
        dataWarehouseService.deleteDataWarehouse(id);
        return R.ok();
    }

    /**
     * 同步数据仓库到 park_info
     */
    @PostMapping("/{id}/sync")
    @ApiOperation(value = "同步数据仓库到 park_info", notes = "手动触发同步")
    public R<Void> syncToParkInfo(@PathVariable Long id) {
        dataWarehouseService.syncToParkInfo(id);
        return R.ok();
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/template")
    @ApiOperation(value = "下载导入模板")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode("数据仓库导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 简化实现：实际应使用 EasyExcel
            response.getOutputStream().write("园区名称,园区认定,星级,主导产业,导入年度,备注\n".getBytes());
        } catch (Exception e) {
            log.error("下载模板失败", e);
            throw new RuntimeException("下载模板失败");
        }
    }
}