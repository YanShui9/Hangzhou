package com.park.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.R;
import com.park.common.result.ResultCode;
import com.park.system.entity.ParkDocument;
import com.park.system.mapper.ParkDocumentMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * 园区行文文件管理控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
@Api(tags = "园区行文文件管理")
public class DocumentController {

    @Autowired
    private ParkDocumentMapper parkDocumentMapper;

    /**
     * 查询园区文件列表
     */
    @GetMapping("/park/{parkId}")
    @ApiOperation(value = "查询园区文件列表", notes = "根据园区ID查询行文文件列表")
    public R<List<ParkDocument>> getParkFiles(@PathVariable Long parkId, HttpServletRequest request) {
        checkLogin(request); // 登录用户均可访问
        LambdaQueryWrapper<ParkDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkDocument::getParkId, parkId);
        wrapper.orderByDesc(ParkDocument::getCreateTime);
        List<ParkDocument> list = parkDocumentMapper.selectList(wrapper);
        return R.ok(list);
    }

    /**
     * 上传园区文件
     */
    @PostMapping("/park/{parkId}")
    @ApiOperation(value = "上传园区文件", notes = "上传园区行文文件")
    public R<ParkDocument> uploadParkFile(@PathVariable Long parkId,
                                          @RequestParam("file") MultipartFile file,
                                          HttpServletRequest request) {
        checkParkOperator(request); // 仅园区端可上传文件，市级管理员无上传权限
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;
        String dirPath = "uploads/document/" + parkId;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dirPath + "/" + newFilename);
        try {
            java.nio.file.Files.copy(file.getInputStream(), dest.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.FAILURE, "文件上传失败：" + e.getMessage());
        }
        String fileUrl = "/uploads/document/" + parkId + "/" + newFilename;

        ParkDocument doc = new ParkDocument();
        doc.setParkId(parkId);
        doc.setFileName(originalFilename);
        doc.setFileUrl(fileUrl);
        doc.setFileSize(file.getSize());
        doc.setFileType(suffix);
        parkDocumentMapper.insert(doc);
        log.info("园区文件上传成功：parkId={}, fileName={}", parkId, originalFilename);
        return R.ok(doc);
    }

    /**
     * 删除园区文件
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除园区文件", notes = "根据文件ID删除行文文件")
    public R<Void> deleteParkFile(@PathVariable Long id, HttpServletRequest request) {
        checkParkOperatorOrCityAdmin(request); // 仅园区端或市级管理员可删除
        ParkDocument doc = parkDocumentMapper.selectById(id);
        if (doc == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "文件不存在");
        }
        // 删除物理文件
        String filePath = doc.getFileUrl().startsWith("/uploads/") ? doc.getFileUrl().substring(1) : doc.getFileUrl();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        parkDocumentMapper.deleteById(id);
        return R.ok();
    }

    /**
     * 预览/下载园区文件
     */
    @GetMapping("/preview/{id}")
    @ApiOperation(value = "预览/下载园区文件", notes = "根据文件ID预览或下载行文文件")
    public void previewFile(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        checkLogin(request); // 登录用户均可访问
        ParkDocument doc = parkDocumentMapper.selectById(id);
        if (doc == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String filePath = doc.getFileUrl().startsWith("/uploads/") ? doc.getFileUrl().substring(1) : doc.getFileUrl();
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String filename = doc.getFileName();
        String contentType = getContentType(filename);
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "inline; filename=" + filename);
        try (InputStream is = new FileInputStream(file);
             java.io.OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 登录校验：任意已登录用户均可通过
     */
    private void checkLogin(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        if (!(roleTypeObj instanceof Integer)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限");
        }
    }

    /**
     * 校验是否为园区端用户（roleType=3）
     * 项目规范：上传文件是园区端的工作，市级管理员只能预览文件并打分，无文件上传权限
     */
    private void checkParkOperator(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        Integer roleType = (roleTypeObj instanceof Integer) ? (Integer) roleTypeObj : null;
        if (roleType == null || roleType != 3) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限");
        }
    }

    /**
     * 校验是否为园区端（roleType=3）或市级管理员（roleType=1）
     */
    private void checkParkOperatorOrCityAdmin(HttpServletRequest request) {
        Object roleTypeObj = request.getAttribute("roleType");
        Integer roleType = (roleTypeObj instanceof Integer) ? (Integer) roleTypeObj : null;
        if (roleType == null || (roleType != 1 && roleType != 3)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限");
        }
    }

    private String getContentType(String filename) {
        if (filename == null) return "application/octet-stream";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (lower.endsWith(".xls")) return "application/vnd.ms-excel";
        if (lower.endsWith(".doc")) return "application/msword";
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
}
