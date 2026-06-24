package com.park.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.system.entity.UnreportedParkRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 未上报运营园区记录 Mapper 接口
 *
 * @author park-team
 */
@Mapper
public interface UnreportedParkRecordMapper extends BaseMapper<UnreportedParkRecord> {
}
