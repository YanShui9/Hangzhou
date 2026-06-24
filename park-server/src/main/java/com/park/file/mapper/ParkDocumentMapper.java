package com.park.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.file.entity.ParkDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区文件Mapper
 */
@Mapper
public interface ParkDocumentMapper extends BaseMapper<ParkDocument> {

    /**
     * 根据园区ID查询文件列表
     */
    List<ParkDocument> selectByParkId(@Param("parkId") Long parkId);

    /**
     * 根据园区ID删除文件
     */
    int deleteByParkId(@Param("parkId") Long parkId);
}
