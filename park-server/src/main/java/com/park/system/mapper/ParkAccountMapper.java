package com.park.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.system.entity.ParkAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 园区账号 Mapper
 *
 * @author park-team
 */
@Mapper
public interface ParkAccountMapper extends BaseMapper<ParkAccount> {
}