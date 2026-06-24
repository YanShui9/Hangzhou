package com.park.park.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.park.entity.ParkInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 园区信息 Mapper 接口
 *
 * @author park-team
 */
@Mapper
public interface ParkMapper extends BaseMapper<ParkInfo> {
}
