package com.park.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.warehouse.entity.DataWarehouse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据仓库 Mapper
 *
 * @author park-team
 */
@Mapper
public interface DataWarehouseMapper extends BaseMapper<DataWarehouse> {
}