package com.park.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.audit.entity.AuditRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审核记录 Mapper 接口
 *
 * @author park-team
 */
@Mapper
public interface AuditMapper extends BaseMapper<AuditRecord> {
}
