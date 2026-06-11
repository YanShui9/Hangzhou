package com.park.enterprise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.enterprise.entity.EnterpriseInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入驻企业 Mapper
 *
 * @author park-team
 */
@Mapper
public interface EnterpriseMapper extends BaseMapper<EnterpriseInfo> {
}
