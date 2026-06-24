package com.park.file.service;

import com.park.file.entity.ParkDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 */
public interface DocumentService {

    /**
     * 根据园区ID查询文件列表
     */
    List<ParkDocument> getDocumentsByParkId(Long parkId);

    /**
     * 上传文件
     */
    ParkDocument uploadFile(Long parkId, MultipartFile file);

    /**
     * 删除文件
     */
    void deleteFile(Long id);

    /**
     * 根据园区ID删除所有文件
     */
    void deleteByParkId(Long parkId);

    /**
     * 根据ID查询文件
     */
    ParkDocument getDocumentById(Long id);
}
