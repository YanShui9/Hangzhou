package com.park.evaluation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.evaluation.entity.EvaluationRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评价记录 Mapper 接口
 *
 * @author park-team
 */
@Mapper
public interface EvaluationMapper extends BaseMapper<EvaluationRecord> {
}
