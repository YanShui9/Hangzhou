package com.park.system.controller;

import com.park.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Api(tags = "通用工具")
@RestController
@RequestMapping("/api/common")
public class CommonController {

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("recordId") Long recordId,
            @RequestParam("sectionKey") String sectionKey) {
        if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;
        String dirPath = "uploads/audit/" + recordId + "/" + sectionKey;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dirPath + "/" + newFilename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return R.fail("文件上传失败：" + e.getMessage());
        }
        String url = "/uploads/audit/" + recordId + "/" + sectionKey + "/" + newFilename;
        Map<String, Object> data = new HashMap<>();
        data.put("name", originalFilename);
        data.put("url", url);
        data.put("size", file.getSize());
        return R.ok("上传成功", data);
    }

    @ApiOperation("文件预览/下载")
    @GetMapping("/download")
    public void download(@RequestParam String url, HttpServletResponse response) {
        if (url == null || url.isEmpty()) {
            return;
        }
        // 去掉前缀 /uploads/，得到相对路径
        String filePath = url.startsWith("/uploads/") ? url.substring(1) : url;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String filename = file.getName();
        String contentType = getContentType(filename);
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
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
        if (lower.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
