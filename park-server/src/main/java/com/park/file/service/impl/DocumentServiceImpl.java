package com.park.file.service.impl;

import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.file.entity.ParkDocument;
import com.park.file.mapper.ParkDocumentMapper;
import com.park.file.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private ParkDocumentMapper documentMapper;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.access-url:/api/files}")
    private String accessUrl;

    /**
     * 获取上传目录的绝对路径
     */
    private Path getUploadPath() {
        // 获取项目根目录（相对于运行目录）
        Path currentPath = Paths.get(".").toAbsolutePath().normalize();
        Path uploadPath = currentPath.resolve(uploadDir).normalize();
        
        // 确保上传目录存在
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
                log.info("创建上传目录: {}", uploadPath);
            } catch (IOException e) {
                log.error("创建上传目录失败", e);
                throw new BusinessException(ResultCode.SERVER_ERROR, "无法创建上传目录");
            }
        }
        
        return uploadPath;
    }

    @Override
    public List<ParkDocument> getDocumentsByParkId(Long parkId) {
        return documentMapper.selectByParkId(parkId);
    }

    @Override
    @Transactional
    public ParkDocument uploadFile(Long parkId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "请选择要上传的文件");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件名不能为空");
        }

        // 获取文件扩展名
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + extension;

        try {
            // 获取上传目录（确保存在）
            Path uploadPath = getUploadPath();

            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 构建文件URL
            String fileUrl = accessUrl + "/" + newFilename;

            // 保存文件记录
            ParkDocument document = new ParkDocument();
            document.setParkId(parkId);
            document.setFileName(originalFilename);
            document.setFileSize(file.getSize());
            document.setFilePath(filePath.toString());
            document.setFileType(file.getContentType());
            document.setFileUrl(fileUrl);

            documentMapper.insert(document);
            return document;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.SERVER_ERROR, "文件上传失败");
        }
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        ParkDocument document = documentMapper.selectById(id);
        if (document == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文件不存在");
        }

        // 删除物理文件
        try {
            File file = new File(document.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            log.warn("删除物理文件失败", e);
        }

        // 删除数据库记录
        documentMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByParkId(Long parkId) {
        List<ParkDocument> documents = documentMapper.selectByParkId(parkId);
        for (ParkDocument document : documents) {
            deleteFile(document.getId());
        }
    }

    @Override
    public ParkDocument getDocumentById(Long id) {
        return documentMapper.selectById(id);
    }
}
