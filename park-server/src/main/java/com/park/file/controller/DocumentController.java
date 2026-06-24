package com.park.file.controller;

import com.park.common.result.R;
import com.park.file.entity.ParkDocument;
import com.park.file.service.DocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
@Api(tags = "文件管理")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * 根据园区ID查询文件列表
     */
    @GetMapping("/park/{parkId}")
    @ApiOperation(value = "查询园区文件列表", notes = "根据园区ID查询已上传的行文文件列表")
    public R<List<ParkDocument>> getDocumentsByParkId(@PathVariable Long parkId) {
        List<ParkDocument> documents = documentService.getDocumentsByParkId(parkId);
        return R.ok(documents);
    }

    /**
     * 上传文件
     */
    @PostMapping("/park/{parkId}")
    @ApiOperation(value = "上传文件", notes = "为指定园区上传行文文件")
    public R<ParkDocument> uploadFile(
            @PathVariable Long parkId,
            @RequestParam("file") MultipartFile file) {
        ParkDocument document = documentService.uploadFile(parkId, file);
        return R.ok(document);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除文件", notes = "根据文件ID删除文件")
    public R<Void> deleteFile(@PathVariable Long id) {
        documentService.deleteFile(id);
        return R.ok();
    }

    /**
     * 预览/下载文件
     */
    @GetMapping("/preview/{id}")
    @ApiOperation(value = "预览文件", notes = "根据文件ID预览或下载文件")
    public ResponseEntity<Resource> previewFile(@PathVariable Long id) {
        ParkDocument document = documentService.getDocumentById(id);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(document.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        String contentType = document.getFileType();
        if (!MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(contentType)) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String encodedFileName = URLEncoder.encode(document.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }
}
