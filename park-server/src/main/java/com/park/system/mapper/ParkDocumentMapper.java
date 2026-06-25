package com.park.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.system.entity.ParkDocument;
import org.apache.ibatis.annotations.Mapper;

/**
 * 园区行文文件 Mapper
 *
 * @author park-team
 */
@Mapper
public interface ParkDocumentMapper extends BaseMapper<ParkDocument> {
}
