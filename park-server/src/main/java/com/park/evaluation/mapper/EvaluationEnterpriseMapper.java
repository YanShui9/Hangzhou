package com.park.evaluation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.evaluation.entity.EvaluationEnterprise;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评价关联企业 Mapper
 *
 * @author park-team
 */
@Mapper
public interface EvaluationEnterpriseMapper extends BaseMapper<EvaluationEnterprise> {
}
