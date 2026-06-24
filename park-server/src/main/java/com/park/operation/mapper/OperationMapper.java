package com.park.operation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.operation.entity.ParkOperation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 园区运营数据 Mapper 接口
 *
 * @author park-team
 */
@Mapper
public interface OperationMapper extends BaseMapper<ParkOperation> {
}
