package com.park.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.system.entity.SystemData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据仓库 Mapper
 *
 * @author park-team
 */
@Mapper
public interface SystemDataMapper extends BaseMapper<SystemData> {
}