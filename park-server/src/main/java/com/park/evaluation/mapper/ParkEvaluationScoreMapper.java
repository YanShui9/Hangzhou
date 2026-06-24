package com.park.evaluation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.evaluation.entity.ParkEvaluationScore;
import org.apache.ibatis.annotations.Mapper;

/**
 * 园区评价评分 Mapper 接口
 *
 * @author park-team
 */
@Mapper
public interface ParkEvaluationScoreMapper extends BaseMapper<ParkEvaluationScore> {
}
