package com.park.evaluation.service;

import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.evaluation.dto.FileUploadVO;
import com.park.evaluation.entity.EvaluationFile;
import com.park.evaluation.mapper.EvaluationFileMapper;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 评价文件 Service
 *
 * @author park-team
 */
@Slf4j
@Service
public class EvaluationFileService {

    @Autowired
    private EvaluationFileMapper fileMapper;

    @Value("${park.file.upload-path:./uploads/}")
    private String uploadPath;

    @Value("${park.file.access-prefix:/api/files/preview/}")
    private String accessPrefix;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024L; // 50MB

    /**
     * 上传文件
     *
     * @param file       上传的文件
     * @param bizType    业务类型
     * @param userId     上传用户ID
     * @return 文件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public FileUploadVO upload(MultipartFile file, String bizType, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.FAILURE, "文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ResultCode.FAILURE, "文件大小不能超过50MB");
        }

        String originalName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalName)) {
            originalName = "unknown";
        }

        // 生成存储路径：uploadPath/yyyyMM/dd/uuid.ext
        SimpleDateFormat sdfDir = new SimpleDateFormat("yyyyMM/dd/");
        String dateDir = sdfDir.format(new Date());
        String storedName = UUID.randomUUID().toString().replace("-", "")
                + getExtension(originalName);

        String fullDir = uploadPath + dateDir;
        File dir = new File(fullDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException(ResultCode.FAILURE, "创建目录失败");
        }

        String fullPath = fullDir + storedName;
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(fullPath);
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error("文件写入失败", e);
            throw new BusinessException(ResultCode.FAILURE, "文件保存失败");
        }

        // 持久化到数据库
        EvaluationFile entity = new EvaluationFile();
        entity.setName(originalName);
        entity.setStoredName(storedName);
        entity.setPath(fullPath);
        entity.setUrl(accessPrefix + storedName);
        entity.setSize(file.getSize());
        entity.setFileType(detectFileType(originalName));
        entity.setBizType(bizType);
        entity.setUploadUserId(userId);
        fileMapper.insert(entity);

        // 返回VO
        FileUploadVO vo = new FileUploadVO();
        vo.setId(entity.getId());
        vo.setName(originalName);
        vo.setUrl(entity.getUrl());
        vo.setSize(file.getSize());
        vo.setFileType(entity.getFileType());
        return vo;
    }

    /**
     * 删除文件
     *
     * @param id 文件ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        EvaluationFile file = fileMapper.selectById(id);
        if (file == null) {
            return;
        }
        // 删除物理文件
        try {
            File f = new File(file.getPath());
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            log.warn("删除物理文件失败：{}", file.getPath(), e);
        }
        // 逻辑删除记录
        fileMapper.deleteById(id);
    }

    /**
     * 根据ID获取文件信息
     */
    public EvaluationFile getById(Long id) {
        return fileMapper.selectById(id);
    }

    /**
     * 获取文件扩展名
     */
    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0) {
            return "";
        }
        return filename.substring(idx).toLowerCase();
    }

    /**
     * 检测文件类型
     */
    private String detectFileType(String filename) {
        String ext = getExtension(filename);
        if (ext.matches("\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
            return "image";
        }
        if (ext.equals(".pdf")) {
            return "pdf";
        }
        if (ext.matches("\\.(doc|docx)$")) {
            return "word";
        }
        if (ext.matches("\\.(xls|xlsx)$")) {
            return "excel";
        }
        return "other";
    }
}
