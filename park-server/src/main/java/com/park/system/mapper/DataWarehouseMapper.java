package com.park.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.system.entity.DataWarehouse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据仓库 Mapper 接口
 *
 * @author park-team
 */
@Mapper
public interface DataWarehouseMapper extends BaseMapper<DataWarehouse> {
}
