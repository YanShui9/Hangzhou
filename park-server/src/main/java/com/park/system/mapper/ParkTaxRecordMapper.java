package com.park.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.system.entity.ParkTaxRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 园区税收记录 Mapper 接口
 *
 * @author park-team
 */
@Mapper
public interface ParkTaxRecordMapper extends BaseMapper<ParkTaxRecord> {
}
